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
package com.myenterprise.rest.component;

import com.myenterprise.rest.v1.configuration.ConfigurationPropertiesReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
     * Getter method of the current property being inspected.
     */
    private Method getter;

    /**
     * Setter method of the current property being inspected.
     */
    private Method setter;

    /**
     * Current response body being processed.
     */
    private Object body;

    /**
     * Property descriptor of the current property being inspected.
     */
    private PropertyDescriptor propertyDescriptor;

    /**
     * Class of the response body object being inspected.
     */
    // Get the class because, this gets JPA object, not Entity.
    // This is so, because is executed the HotelMapper.toModel(HotelsEntity)
    private Class<?> argumentClass;

    /**
     * Field corresponding to the current property being inspected.
     */
    private Field field;

    /**
     * Controller method that produced the response.
     */
    private Method signature;

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

    private void sanitizeObject(Object value)
            throws InvocationTargetException, IllegalAccessException, IntrospectionException {

        if (value == null) return;

        if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                sanitizeObject(item);
            }
            return;
        }

        if (!isPojo(value)) return;

        Object previousBody = this.body;
        Class<?> previousClass = this.argumentClass;

        this.body = value;
        this.argumentClass = value.getClass();

        this.iteratePropertiesOfAArgument();

        this.body = previousBody;
        this.argumentClass = previousClass;
    }


    private void validateIterable()
            throws InvocationTargetException, IllegalAccessException {

        Object value = this.getter.invoke(this.body);
        try {
            this.sanitizeObject(value);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
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
    private void validateField()
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {

        this.field = this.argumentClass.getDeclaredField(propertyDescriptor.getName());

        boolean isGetterNull = Objects.isNull(this.getter);
        boolean isSetterNull = Objects.isNull(this.setter);
        boolean isNotPropertyString = this.propertyDescriptor.getPropertyType() != String.class;
        boolean isIterable = this.propertyDescriptor.getPropertyType() == List.class;
        boolean hasNotSanitizeHTMLAnnotation = !this.field.isAnnotationPresent(SanitizeHTML.class);
        boolean isArgumentInvokeNull = this.getter.invoke(this.body) == null;

        if (isArgumentInvokeNull) throw new NoSuchFieldException();

        if (isIterable){
            this.validateIterable();
            throw new NoSuchFieldException();
        }
        if (isGetterNull || isSetterNull || isNotPropertyString || hasNotSanitizeHTMLAnnotation){
            throw new NoSuchFieldException();
        }

        String originalValue = (String) this.getter.invoke(this.body);
        boolean isNotHTML = !originalValue.contains("<");
        if (isNotHTML) throw new NoSuchFieldException();
    }

    /**
     * Sanitizes the value of the current String field using the CKEditor
     * sanitization policy and updates the field with the sanitized value.
     *
     * @throws IllegalAccessException    if the setter is not accessible
     * @throws InvocationTargetException if an error occurs during method invocation
     */
    private void sanitizeValueField()
            throws IllegalAccessException, InvocationTargetException {

        String originalValue = (String) this.getter.invoke(this.body);

        SanitizerHTMLLoggerConfiguration loggerConfiguration =
                new SanitizerHTMLLoggerConfiguration(this.propertiesConfiguration)
                        .setFieldName(this.field.getName())
                        .setModelClassName(this.argumentClass.getName())
                        .setControllerClassName(this.signature.getDeclaringClass().getName())
                        .setControllerMethodName(this.signature.getName());

        SanitizerHTMLListener sanitizerHTMLListener =
                new SanitizerHTMLListener(loggerConfiguration, this.propertiesConfiguration);

        String sanitizedValue = CKEDITOR_POLICY.sanitize(
                originalValue,
                sanitizerHTMLListener,
                SanitizeHTMLResponse.class
        );

        sanitizerHTMLListener.register();
        this.setter.invoke(this.body, sanitizedValue);
    }

    /**
     * Iterates over the properties of the current response object and
     * sanitizes all eligible fields.
     *
     * @throws IntrospectionException    if an error occurs during JavaBeans introspection
     * @throws IllegalAccessException    if a field is not accessible
     * @throws InvocationTargetException if an error occurs during method invocation
     */
    private void iteratePropertiesOfAArgument()
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {

        for (PropertyDescriptor pd :
                Introspector.getBeanInfo(this.argumentClass).getPropertyDescriptors()) {

            this.propertyDescriptor = pd;
            this.getter = pd.getReadMethod();
            this.setter = pd.getWriteMethod();

            try {
                this.validateField();
            } catch (NoSuchFieldException ignored) {
                continue;
            }

            this.sanitizeValueField();
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

        this.body = body;

        if (isPojo(body)) {
            this.argumentClass = body.getClass();
            this.signature = returnType.getMethod();
            try {
                this.iteratePropertiesOfAArgument();
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException error) {
                throw new RuntimeException(error);
            }
        } else if (body instanceof Iterable<?> collection) {
            ArrayList<Object> result = new ArrayList<>();
            collection.forEach(item -> {
                if (item == null) return;

                this.body = item;
                this.argumentClass = item.getClass();
                this.signature = returnType.getMethod();

                try {
                    this.iteratePropertiesOfAArgument();
                } catch (IntrospectionException | IllegalAccessException | InvocationTargetException error) {
                    throw new RuntimeException(error);
                }

                result.add(this.body);
            });
            this.body = result;
        }

        return this.body;
    }
}
