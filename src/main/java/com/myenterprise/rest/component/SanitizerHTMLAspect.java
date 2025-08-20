package com.myenterprise.rest.component;

import com.myenterprise.rest.v1.configuration.ConfigurationPropertiesReader;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import com.myenterprise.rest.annotation.SanitizeHTML;

import static com.myenterprise.rest.component.CKEditorSanitizerPolicy.CKEDITOR_POLICY;

/**
 * Aspect that sanitizes HTML content before a controller method is executed.
 * This aspect intercepts methods in the specified packages and applies HTML sanitization
 * to any String fields that are annotated with {@link SanitizeHTML}.
 */
@Aspect
@Component
public class SanitizerHTMLAspect {

    /**
     * Reader for configuration properties.
     */
    private final ConfigurationPropertiesReader propertiesConfiguration;
    /**
     * The join point of the intercepted method.
     */
    private JoinPoint joinPoint;
    /**
     * The getter method of a property.
     */
    private Method getter;
    /**
     * The setter method of a property.
     */
    private Method setter;
    /**
     * The current argument being processed.
     */
    private Object argument;
    /**
     * The descriptor for a property of the argument.
     */
    private PropertyDescriptor propertyDescriptor;
    /**
     * The class of the current argument.
     */
    private Class<?> argumentClass;
    /**
     * The field being processed.
     */
    private Field field;
    /**
     * The signature of the intercepted method.
     */
    private Signature signature;

    /**
     * Constructs a new SanitizerHTMLAspect with the given configuration properties reader.
     *
     * @param propertiesConfiguration The configuration properties reader.
     */
    @Autowired
    public SanitizerHTMLAspect(ConfigurationPropertiesReader propertiesConfiguration){
        this.propertiesConfiguration = propertiesConfiguration;
    }

    /**
     * Validates if a field meets the criteria for sanitization.
     * This includes checking for the {@link SanitizeHTML} annotation,
     * the presence of getter/setter methods, and a non-null String value.
     *
     * @throws NoSuchFieldException if the field is not valid for sanitization.
     * @throws IllegalAccessException if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void validateField() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException{
        try{
            this.field = this.argumentClass.getDeclaredField(propertyDescriptor.getName());
            boolean isGetterNull = Objects.isNull(this.getter);
            boolean isSetterNull = Objects.isNull(this.setter);
            boolean isNotPropertyString = this.propertyDescriptor.getPropertyType() != String.class;
            boolean hasNotSanitizeHTMLAnnotation = !field.isAnnotationPresent(SanitizeHTML.class);

            if ( isGetterNull || isSetterNull || isNotPropertyString || hasNotSanitizeHTMLAnnotation )
                throw new NoSuchFieldException();

            boolean isArgumentInvokeNull = this.getter.invoke(this.argument) == null;
            if (isArgumentInvokeNull) throw new NoSuchFieldException();

        } catch (NoSuchFieldException error){
            throw new NoSuchFieldException();
        }

    }

    /**
     * Sanitizes the value of a String field using the CKEditor policy.
     *
     * @throws IllegalAccessException if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void sanitizeValueField()
            throws IllegalAccessException, InvocationTargetException{
        String originalValue = (String) this.getter.invoke(this.argument);

        SanitizerHTMLLoggerConfiguration loggerConfiguration = new SanitizerHTMLLoggerConfiguration(this.propertiesConfiguration)
                .setFieldName(this.field.getName())
                .setModelClassName(this.argumentClass.getName())
                .setControllerClassName(this.signature.getDeclaringTypeName())
                .setControllerMethodName(this.signature.getName());

        SanitizerHTMLListener sanitizerHTMLListener = new SanitizerHTMLListener(loggerConfiguration, this.propertiesConfiguration);
        String sanitizedValue = CKEDITOR_POLICY.sanitize(originalValue, sanitizerHTMLListener, SanitizerHTMLAspect.class);
        sanitizerHTMLListener.register();

        this.setter.invoke(this.argument, sanitizedValue);
    }

    /**
     * Iterates through the properties of a method argument and attempts to sanitize them.
     *
     * @throws IntrospectionException if an exception occurs during introspection.
     * @throws IllegalAccessException if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void iteratePropertiesOfAArgument()
            throws IntrospectionException, IllegalAccessException, InvocationTargetException{
        this.argumentClass = this.argument.getClass();
        for (PropertyDescriptor pd : Introspector.getBeanInfo(this.argumentClass).getPropertyDescriptors()){
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

    /**
     * Iterates through the arguments of the intercepted method.
     *
     * @throws IntrospectionException if an exception occurs during introspection.
     * @throws IllegalAccessException if the field is not accessible.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    private void iterateArguments()
            throws IntrospectionException, IllegalAccessException, InvocationTargetException{
        for( Object arg : this.joinPoint.getArgs()){
            this.argument = arg;
            if (this.argument == null) continue;
            this.iteratePropertiesOfAArgument();
        }
    }

    /**
     * Aspect method that intercepts and sanitizes the HTML content of DTOs before a method execution.
     * This method is triggered for any public method within the specified package and its sub-packages.
     *
     * @param joinPoint The join point of the intercepted method.
     * @throws IllegalAccessException if a field is not accessible.
     * @throws IntrospectionException if an exception occurs during introspection.
     * @throws InvocationTargetException if an exception occurs during method invocation.
     */
    @Before("execution(* com.myenterprise.rest..api..*Api.*(..))")
    public void sanitizerHTMLMethod(@NotNull JoinPoint joinPoint)
            throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        this.joinPoint = joinPoint;
        this.signature = this.joinPoint.getSignature();
        this.iterateArguments();
    }
}