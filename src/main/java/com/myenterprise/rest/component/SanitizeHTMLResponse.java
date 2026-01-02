/*
 *   MIT License
 *
 *  Copyright (c) 2025 cleanet
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
// 20ms~40ms
package com.myenterprise.rest.component;

import com.myenterprise.rest.v1.configuration.ConfigurationPropertiesReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.myenterprise.rest.annotation.SanitizeHTML;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.myenterprise.rest.component.CKEditorSanitizerPolicy.CKEDITOR_POLICY;

/**
 * Advice that sanitizes HTML content in response bodies before they are written
 * to the HTTP response.
 * <p>
 * This component intercepts controller responses and applies HTML sanitization
 * to String properties annotated with {@link SanitizeHTML}.
 */
@Component
@ControllerAdvice
public class SanitizeHTMLResponse implements ResponseBodyAdvice<Object> {

    /**
     * Reader used to access application configuration properties.
     */
    private final ConfigurationPropertiesReader propertiesConfiguration;

    private final SanitizerHTMLLoggerConfiguration logger;

    private final Map<Class<?>, List<PropertyDescriptor>> cachedPropertyDescriptors = new ConcurrentHashMap<>();

    private final Map<Class<?>, List<String>> cachedPropertyNamesToSanitize = new ConcurrentHashMap<>();

    private static final Pattern API_PACKAGE = Pattern.compile("com\\.myenterprise\\.rest\\..*\\.api.*");

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");

    /**
     * Creates a new {@code SanitizeHTMLResponse} using the provided
     * configuration properties reader.
     *
     * @param propertiesConfiguration configuration properties reader
     */
    @Autowired
    public SanitizeHTMLResponse(ConfigurationPropertiesReader propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
        this.logger = new SanitizerHTMLLoggerConfiguration(this.propertiesConfiguration);
    }

    private void validateObject(
            @NotNull Object object,
            @NotNull MethodParameter returnType
    ) throws InvocationTargetException, IllegalAccessException, IntrospectionException, NoSuchFieldException {

        if (object instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                validateObject(item, returnType);
            }
            return;
        }

        if (!isPojo(object)) return;
        BeanWrapper wrapper = new BeanWrapperImpl(object);
        this.iteratePropertiesOfAObject(wrapper, returnType);
    }

    /**
     * Validates whether the current field is eligible for HTML sanitization.
     * <p>
     * A field is eligible if:
     * <ul>
     *   <li>It exists in the target class</li>
     *   <li>It is of type {@link String}</li>
     *   <li>It is annotated with {@link SanitizeHTML}</li>
     *   <li>It has both getter and setter methods</li>
     *   <li>Its current value is not {@code null}</li>
     * </ul>
     *
     * @throws IllegalAccessException    if the field is not accessible
     * @throws InvocationTargetException if an error occurs while invoking the getter
     */
    @NotNull
    private Boolean isValidField(
            @NotNull Class<?> clazz,
            @NotNull PropertyDescriptor propertyDescriptor,
            @NotNull MethodParameter returnType,
            @NotNull String propertyName,
            @NotNull Object propertyValue
    ) throws IllegalAccessException, InvocationTargetException, IntrospectionException, NoSuchFieldException {
        if ("class".equals(propertyName)) {
            return false;
        }

        if (this.isCachedPropertyNameToSanitize( clazz, propertyName )) return true;

        Field field = clazz.getDeclaredField(propertyDescriptor.getName());

        boolean isNotPropertyString = propertyDescriptor.getPropertyType() != String.class;
        boolean isIterable = Iterable.class.isAssignableFrom(propertyDescriptor.getPropertyType());
        boolean hasNotSanitizeHTMLAnnotation = !field.isAnnotationPresent(SanitizeHTML.class);
        boolean isEmptyPropertyValue = propertyValue.toString().isEmpty();

        if (isIterable){
            this.validateObject( propertyValue, returnType );
            return false;
        }
        if (isNotPropertyString || hasNotSanitizeHTMLAnnotation || isEmptyPropertyValue ){
            return false;
        }
        return this.hasHTML(propertyValue.toString());
    }

    private boolean hasHTML(@NotNull String value ){
        return HTML_PATTERN.matcher(value).find();
    }

    private List<PropertyDescriptor> getPropertyDescriptors(@NotNull Class<?> clazz) {
        return this.cachedPropertyDescriptors.computeIfAbsent(clazz, item -> {
            BeanWrapper wrapper = new BeanWrapperImpl(item);
            return Arrays.asList(wrapper.getPropertyDescriptors());
        });
    }

    /**
     * Sanitizes the value of the current String field using the CKEditor
     * sanitization policy and updates the field with the sanitized value.
     */
    private void sanitizeValueField(
            @NotNull Class<?> clazz,
            @NotNull BeanWrapper wrapper,
            @NotNull PropertyDescriptor propertyDescriptor,
            @NotNull MethodParameter returnType,
            @NotNull String propertyName,
            @NotNull Object propertyValue
    ) {

        this.logger.setFieldName(propertyName)
                   .setModelClassName(clazz.getName())
                   .setControllerClassName(returnType.getDeclaringClass().getName())
                   .setControllerMethodName(Objects.requireNonNull(returnType.getMethod()).getName());

        SanitizerHTMLListener sanitizerHTMLListener =
                new SanitizerHTMLListener(this.logger, this.propertiesConfiguration);

        String sanitizedValue = CKEDITOR_POLICY.sanitize(
                propertyValue.toString(),
                sanitizerHTMLListener,
                SanitizeHTMLResponse.class
        );

        sanitizerHTMLListener.register();
        wrapper.setPropertyValue(propertyName, sanitizedValue);
    }

    private boolean isCachedPropertyNameToSanitize( @NotNull Class<?> clazz, @NotNull String propertyName ){
        if ( this.cachedPropertyNamesToSanitize.get(clazz) != null && !this.cachedPropertyNamesToSanitize.get(clazz).isEmpty() ){
            return this.cachedPropertyNamesToSanitize.get(clazz).contains(propertyName);
        }
        return false;
    }

    private void cachePropertyNameToSanitize(@NotNull Class<?> clazz, @NotNull String propertyName ){
        List<String> list = new ArrayList<>();
        if ( this.cachedPropertyNamesToSanitize.get(clazz) == null ){
            list.add(propertyName);
            this.cachedPropertyNamesToSanitize.put(clazz, list);
        } else {
            list = this.cachedPropertyNamesToSanitize.get(clazz);
            if (!list.contains(propertyName))  list.add(propertyName);
        }
        this.cachedPropertyNamesToSanitize.put(clazz, list);
    }

    /**
     * Iterates over the properties of the current response object and
     * sanitizes all eligible fields.
     *
     * @throws IntrospectionException    if an error occurs during JavaBeans introspection
     * @throws IllegalAccessException    if a field is not accessible
     * @throws InvocationTargetException if an error occurs during method invocation
     */
    private void iteratePropertiesOfAObject(
            @NotNull BeanWrapper wrapper,
            @NotNull MethodParameter returnType
    ) throws IntrospectionException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        for (PropertyDescriptor propertyDescriptor : this.getPropertyDescriptors(wrapper.getWrappedClass())) {

            String propertyName = propertyDescriptor.getName();
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            Class<?> clazz = wrapper.getWrappedClass();

            if (propertyValue == null) continue;

            if (!this.isValidField( clazz, propertyDescriptor, returnType, propertyName, propertyValue )) continue;

            this.sanitizeValueField( clazz, wrapper, propertyDescriptor, returnType, propertyName, propertyValue );

            this.cachePropertyNameToSanitize( clazz, propertyName );
        }
    }

    /**
     * Determines whether this advice applies to the given controller method.
     * <p>
     * The advice is applied only to controller classes whose package name
     * matches {@code com.myenterprise.rest.(...).api(...)}.
     *
     * @param returnType    controller method return type
     * @param converterType selected HTTP message converter
     * @return {@code true} if the response body should be intercepted
     */
    @Override
    public boolean supports(
            @NotNull MethodParameter returnType,
            @NotNull Class converterType
    ) {
        return API_PACKAGE.matcher(
                returnType
                        .getContainingClass()
                        .getPackage()
                        .getName()
        ).matches();
    }

    /**
     * Determines whether the given object should be treated as a POJO
     * eligible for property introspection.
     * <p>
     * Simple types such as primitives, wrappers, strings, byte arrays,
     * and iterable types are excluded.
     *
     * @param object object to evaluate
     * @return {@code true} if the object is a POJO; {@code false} otherwise
     */
    private boolean isPojo(Object object) {
        return !(object instanceof String ||
                object instanceof Number ||
                object instanceof Boolean ||
                object instanceof byte[] ||
                object instanceof Iterable ||
                object.getClass().isPrimitive());
    }

    /**
     * Intercepts the response body before it is written to the HTTP response.
     * <p>
     * If the body is a POJO, its properties are inspected and sanitized.
     * If the body is an {@link Iterable}, each element is processed individually.
     *
     * @param body                  response body
     * @param returnType            controller method return type
     * @param selectedContentType   selected response content type
     * @param selectedConverterType selected HTTP message converter
     * @param request               current HTTP request
     * @param response              current HTTP response
     * @return sanitized response body
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class selectedConverterType,
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response
    ) {
        if (body == null) return null;

        BeanWrapper wrapper = new BeanWrapperImpl(body);

        if (isPojo(body)) {
            try {
                this.iteratePropertiesOfAObject(wrapper, returnType);
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException |
                     NoSuchFieldException error) {
                throw new RuntimeException(error);
            }
        } else if (body instanceof Iterable<?> collection) {
            ArrayList<Object> result = new ArrayList<>();
            collection.forEach(item -> {
                if (item == null) return;
                BeanWrapper itemWrapper = new BeanWrapperImpl(item);
                try {
                    this.iteratePropertiesOfAObject(itemWrapper, returnType);
                } catch (IntrospectionException | IllegalAccessException | InvocationTargetException |
                         NoSuchFieldException error) {
                    throw new RuntimeException(error);
                }
                result.add(itemWrapper.getWrappedInstance());
            });
            body = result;
        }
        return body;
    }
}
