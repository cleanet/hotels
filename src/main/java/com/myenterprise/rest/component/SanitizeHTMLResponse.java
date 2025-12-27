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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

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

    /**
     * Creates a new {@code SanitizeHTMLResponse} using the provided
     * configuration properties reader.
     *
     * @param propertiesConfiguration configuration properties reader
     */
    @Autowired
    public SanitizeHTMLResponse(ConfigurationPropertiesReader propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    private void validateObject(BeanWrapper wrapper, MethodParameter returnType )
            throws InvocationTargetException, IllegalAccessException, IntrospectionException, NoSuchFieldException {
        Object object = wrapper.getWrappedInstance();

        if (object instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                validateObject(new BeanWrapperImpl(item), returnType);
            }
            return;
        }

        if (!isPojo(object)) return;
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
     * @throws NoSuchFieldException      if the field does not meet the criteria
     * @throws IllegalAccessException    if the field is not accessible
     * @throws InvocationTargetException if an error occurs while invoking the getter
     */
    private void validateField( BeanWrapper wrapper, PropertyDescriptor propertyDescriptor, MethodParameter returnType )
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, IntrospectionException {

        boolean isNotPropertyString = propertyDescriptor.getPropertyType() != String.class;
        boolean isIterable = Iterable.class.isAssignableFrom(propertyDescriptor.getPropertyType());;
        boolean hasNotSanitizeHTMLAnnotation = !propertyDescriptor.getReadMethod().isAnnotationPresent(SanitizeHTML.class);
        Object propertyValue = wrapper.getPropertyValue(propertyDescriptor.getName());
        boolean isNullPropertyValue = propertyValue == null;

        if (isNullPropertyValue) throw new NoSuchFieldException();

        if (isIterable){
            this.validateObject( new BeanWrapperImpl(propertyValue), returnType );
            throw new NoSuchFieldException();
        }
        if (isNotPropertyString || hasNotSanitizeHTMLAnnotation){
            throw new NoSuchFieldException();
        }

        boolean isNotHTML = !propertyValue.toString().contains(">");
        if (isNotHTML) throw new NoSuchFieldException();
    }

    /**
     * Sanitizes the value of the current String field using the CKEditor
     * sanitization policy and updates the field with the sanitized value.
     */
    private void sanitizeValueField( BeanWrapper wrapper, PropertyDescriptor propertyDescriptor, MethodParameter returnType ) {

        SanitizerHTMLLoggerConfiguration loggerConfiguration =
                new SanitizerHTMLLoggerConfiguration(this.propertiesConfiguration)
                        .setFieldName(propertyDescriptor.getReadMethod().getName())
                        .setModelClassName(wrapper.getWrappedClass().getName())
                        .setControllerClassName(returnType.getDeclaringClass().getName())
                        .setControllerMethodName(Objects.requireNonNull(returnType.getMethod()).getName());

        SanitizerHTMLListener sanitizerHTMLListener =
                new SanitizerHTMLListener(loggerConfiguration, this.propertiesConfiguration);


        String propertyValue = (String) wrapper.getPropertyValue(propertyDescriptor.getName());
        String sanitizedValue = CKEDITOR_POLICY.sanitize(
                propertyValue,
                sanitizerHTMLListener,
                SanitizeHTMLResponse.class
        );

        sanitizerHTMLListener.register();
        wrapper.setPropertyValue(propertyDescriptor.getName(), sanitizedValue);
    }

    /**
     * Iterates over the properties of the current response object and
     * sanitizes all eligible fields.
     *
     * @throws IntrospectionException    if an error occurs during JavaBeans introspection
     * @throws IllegalAccessException    if a field is not accessible
     * @throws InvocationTargetException if an error occurs during method invocation
     */
    private void iteratePropertiesOfAObject( BeanWrapper wrapper, MethodParameter returnType )
            throws IntrospectionException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors()) {

            try {
                this.validateField( wrapper, propertyDescriptor, returnType );
            } catch (NoSuchFieldException ignored) {
                continue;
            }

            this.sanitizeValueField( wrapper, propertyDescriptor, returnType );
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
            MethodParameter returnType,
            @NotNull Class converterType
    ) {
        return returnType
                .getContainingClass()
                .getPackage()
                .getName()
                .matches("com.myenterprise.rest.(.*).api(.*)");
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
            return result;
        }

        return body;
    }
}
