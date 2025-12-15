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
import java.util.Objects;

import com.myenterprise.rest.annotation.SanitizeHTML;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.myenterprise.rest.component.CKEditorSanitizerPolicy.CKEDITOR_POLICY;

/**
 * Aspect that sanitizes HTML content before a controller method is executed.
 * This aspect intercepts methods in the specified packages and applies HTML sanitization
 * to any String fields that are annotated with {@link SanitizeHTML}.
 */
@Component
@ControllerAdvice
public class SanitizeHTMLResponse implements ResponseBodyAdvice<Object> {

    /**
     * Reader for configuration properties.
     */
    private final ConfigurationPropertiesReader propertiesConfiguration;

    /**
     * The getter method of a property.
     */
    private Method getter;
    /**
     * The setter method of a property.
     */
    private Method setter;
    /**
     * The current body being processed.
     */
    private Object body;
    /**
     * The descriptor for a property of the argument.
     */
    private PropertyDescriptor propertyDescriptor;
    /**
     * The class of the current argument.
     */
    private Class<?> argumentSuperClass;
    /**
     * The field being processed.
     */
    private Field field;
    /**
     * The signature of the intercepted method.
     */
    private Method signature;

    /**
     * Constructs a new SanitizerHTMLAspect with the given configuration properties reader.
     *
     * @param propertiesConfiguration The configuration properties reader.
     */
    @Autowired
    public SanitizeHTMLResponse(ConfigurationPropertiesReader propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    /**
     * Validates if a field meets the criteria for sanitization.
     * This includes checking for the {@link SanitizeHTML} annotation,
     * the presence of getter/setter methods, and a non-null String value.
     *
     * @throws NoSuchFieldException      if the field is not valid for sanitization.
     * @throws IllegalAccessException    if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void validateField() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        this.field = this.argumentSuperClass.getDeclaredField(propertyDescriptor.getName());
        boolean isGetterNull = Objects.isNull(this.getter);
        boolean isSetterNull = Objects.isNull(this.setter);
        boolean isNotPropertyString = this.propertyDescriptor.getPropertyType() != String.class;
        boolean hasNotSanitizeHTMLAnnotation = !this.field.isAnnotationPresent(SanitizeHTML.class);

        if ( isGetterNull || isSetterNull || isNotPropertyString || hasNotSanitizeHTMLAnnotation)
            throw new NoSuchFieldException();

        boolean isArgumentInvokeNull = this.getter.invoke(this.body) == null;
        if (isArgumentInvokeNull) throw new NoSuchFieldException();

    }

    /**
     * Sanitizes the value of a String field using the CKEditor policy.
     *
     * @throws IllegalAccessException    if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void sanitizeValueField()
            throws IllegalAccessException, InvocationTargetException {
        String originalValue = (String) this.getter.invoke(this.body);

        SanitizerHTMLLoggerConfiguration loggerConfiguration = new SanitizerHTMLLoggerConfiguration(
                this.propertiesConfiguration
        )
        .setFieldName(this.field.getName())
        .setModelClassName(this.argumentSuperClass.getName())
        .setControllerClassName(this.signature.getDeclaringClass().getName())
        .setControllerMethodName(this.signature.getName());

        SanitizerHTMLListener sanitizerHTMLListener = new SanitizerHTMLListener(
                loggerConfiguration,
                this.propertiesConfiguration
        );
        String sanitizedValue = CKEDITOR_POLICY.sanitize(
                originalValue,
                sanitizerHTMLListener,
                SanitizerHTMLAspect.class
        );
        sanitizerHTMLListener.register();

        this.setter.invoke(this.body, sanitizedValue);
    }

    /**
     * Iterates through the properties of a method argument and attempts to sanitize them.
     *
     * @throws IntrospectionException    if an exception occurs during introspection.
     * @throws IllegalAccessException    if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void iteratePropertiesOfAArgument()
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        for (PropertyDescriptor pd : Introspector.getBeanInfo(this.argumentSuperClass).getPropertyDescriptors()) {
            this.propertyDescriptor = pd;
            this.getter = propertyDescriptor.getReadMethod();
            this.setter = propertyDescriptor.getWriteMethod();

            try {
                this.validateField();
            } catch (NoSuchFieldException error) {
                continue;
            }

            this.sanitizeValueField();
        }
    }

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

    private boolean isPojo(Object object) {
        return !(object instanceof String ||
                object instanceof Number ||
                object instanceof Boolean ||
                object instanceof byte[] ||
                object instanceof Iterable ||
                object.getClass().isPrimitive());
    }

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
            this.argumentSuperClass = body.getClass().getSuperclass();
            this.signature = returnType.getMethod();
            try {
                this.iteratePropertiesOfAArgument();
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException error) {
                throw new RuntimeException(error);
            }
        }
        else if (body instanceof Iterable<?> collection) {
            ArrayList<Object> result = new ArrayList<>();
            collection.forEach((item) -> {
                if (item == null) return;
                this.body = item;
                this.argumentSuperClass = item.getClass().getSuperclass();
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