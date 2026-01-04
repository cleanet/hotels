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

import com.myenterprise.rest.annotation.sanitizeHTML.SanitizeHTMLListener;
import com.myenterprise.rest.annotation.sanitizeHTML.SanitizeHTMLLoggerConfiguration;
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

import com.myenterprise.rest.annotation.sanitizeHTML.SanitizeHTML;

import static com.myenterprise.rest.annotation.sanitizeHTML.SanitizePolicy.CKEDITOR_POLICY;
// Usar JsonSerializer y JsonDeserializer ya que es más optimo que Advice. Para sanear los campos.
// Para validar los queryParams de los POST si se debe usar RequestBodyAdvice.
// https://docs.spring.io/spring-boot/3.5/reference/features/json.html
/*
Anotación @SanitizeHTML (limpia y declarativa)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SanitizeHtmlSerializer.class)
public @interface SanitizeHTML {
}


📌 @JacksonAnnotationsInside permite que Jackson la trate como propia.

Perfecto, vamos a hacer un **análisis profundo del runtime** y por qué **JSON Serializer/Deserializer es mejor que ResponseBodyAdvice/RequestBodyAdvice** para tu caso de sanitización y transformación de datos en Spring Boot 3 (y WebFlux).

Voy a desglosarlo paso a paso, incluyendo **qué pasa en memoria, CPU y GC**, y luego argumento claramente la ventaja.

---

# 1️⃣ Pipeline runtime con `ResponseBodyAdvice` / `RequestBodyAdvice`

Cuando usas **Advice**, el flujo es así:

```
Request (HTTP)
    ↓
MessageConverter (Jackson / Gson)
    ↓
RequestBodyAdvice.beforeBodyRead
    ↓
Deserialización a DTO (Jackson)
    ↓
Controller
    ↓
ResponseBodyAdvice.beforeBodyWrite
    ↓
Reflection + BeanWrapper + PropertyDescriptors
    ↓
Sanitización manual (field a field)
    ↓
MessageConverter (Jackson)
    ↓
JSON final
```

### Detalles del runtime

1. **Reflection en cada request/response**

   * `BeanWrapperImpl` + `PropertyDescriptor` + `Field.get/set`
   * Para objetos complejos (20–50 propiedades) → **mucho overhead**
2. **Iteración de listas / colecciones**

   * Cada elemento → `BeanWrapper` → reflection
3. **Regex por cada String con HTML**

   * Coste CPU por patrón `"<[^>]+>"`
4. **Caching limitado**

   * Aunque caches `PropertyDescriptor` → aún hay lookup + validación
5. **GC y allocations**

   * Muchos objetos temporales: listas, wrappers, arrays
   * En un API con 500–1000 req/s → presión de memoria

**Resultado típico:**

* Latencia: 20–40 ms extra por response
* GC pressure: alta
* CPU: mucho overhead en reflection

---

# 2️⃣ Pipeline runtime con **Jackson Serializer / Deserializer**

```
Request (HTTP)
    ↓
MessageConverter (Jackson)
    ↓
JsonDeserializer (field a field) → DTO
    ↓
Controller
    ↓
JsonSerializer (field a field) → JSON
    ↓
Response (HTTP)
```

### Detalles del runtime

1. **Cero reflection en caliente**

   * Jackson genera **bytecode optimizado** para setters/getters de DTO
   * Con módulos `ParameterNamesModule` o `KotlinModule` → incluso sin reflection
2. **Field-level control**

   * Solo los campos anotados con `@SanitizeHTML` usan el serializer
   * Otros String pasan directo → sin coste
3. **Streaming**

   * Jackson escribe directamente a `JsonGenerator` → evita copies
   * Compatible con WebFlux y Netty non-blocking
4. **Menos allocations**

   * Solo objetos de sanitización temporal (String)
   * No crea wrappers por cada propiedad
5. **Optimización de regex / sanitización**

   * Solo se aplica al campo que realmente tiene HTML → fast-path

**Resultado típico:**

* Latencia: 2–5 ms
* GC pressure: mínima
* CPU: muy bajo
* Escala lineal incluso a 10k req/s

---

# 3️⃣ Comparación de costos

| Concepto         | ResponseBodyAdvice             | Jackson Serializer              |
| ---------------- | ------------------------------ | ------------------------------- |
| Reflection       | ✅ (cada propiedad)             | ❌ (bytecode optimizado)         |
| Field-level      | ❌ inspecciona todo             | ✅ solo campos anotados          |
| Colecciones      | ❌ crea wrappers por elemento   | ✅ streaming / sin wrapper extra |
| GC / Memoria     | Alta                           | Muy baja                        |
| WebFlux-friendly | ⚠️ (difícil de hacer reactive) | ✅ streaming, reactive           |
| Mantenibilidad   | Baja (código complejo)         | Alta (limpio, declarativo)      |
| Latencia típica  | 20–40 ms                       | 2–5 ms                          |

---

# 4️⃣ Por qué **JSON Serializer / Deserializer** es mejor

1. **Performance**

   * No reflection repetida
   * Streaming directo → mínimo overhead
   * Fast-path si no hay HTML

2. **Escalabilidad**

   * Funciona en WebFlux (non-blocking)
   * Funciona en high-load APIs
   * Latencia y CPU controlables

3. **Mantenibilidad**

   * Declarativo: solo `@JsonSerialize(using = ...)`
   * Código limpio, sin recorrer objetos manualmente
   * Fácil de testear campo a campo

4. **Seguridad**

   * Sanitización controlada por campo → cero riesgo de afectar campos no deseados

5. **Compatibilidad futura**

   * Jackson Serializer/Deserializer es parte de Jackson core
   * Spring Boot 3.x lo usa nativamente
   * Advice depende de reflection y API interna → más propenso a romper en upgrades

---

# 5️⃣ Consideraciones adicionales

* **Request sanitization:**

  * Mismo enfoque: `JsonDeserializer` para limpiar input
  * No necesitas `RequestBodyAdvice` salvo que quieras contexto de headers o usuario

* **Hybrid:**

  * Si quieres logging o contexto de controller → Advice solo para eso
  * Sanitización real → Jackson Serializer/Deserializer

* **WebFlux:**

  * Advice es **difícil y bloqueante**
  * Serializer/Deserializer → full reactive / streaming → mucho mejor

---

# 🔑 Conclusión profesional

> Para sanitización de HTML y transformación de datos en Spring Boot 3 (MVC o WebFlux), usar **Jackson JsonSerializer / JsonDeserializer** es **claramente superior** a ResponseBodyAdvice/RequestBodyAdvice, porque:
>
> 1. Es mucho más rápido (≈10×)
> 2. Consume menos memoria y CPU
> 3. Escala mejor en WebFlux/reactive
> 4. Código más limpio y mantenible
> 5. Futuro-proof con Spring Boot y Jackson

---

Si quieres, puedo hacer un **diagrama visual del runtime** comparando **Advice vs Serializer/Deserializer**, que te deja ver **cada paso, allocations y reflection** para convencer a cualquier equipo técnico.

¿Quieres que haga ese diagrama?
 */
/**
 * Advice that sanitizes HTML content in response bodies before they are written
 * to the HTTP response.
 * <p>
 * This component intercepts controller responses and applies HTML sanitization
 * to String properties annotated with {@link SanitizeHTML}.
 */
@Component
//@ControllerAdvice
public class SanitizeHTMLResponse { //implements ResponseBodyAdvice<Object> {

    private final SanitizeHTMLLoggerConfiguration sanitizeHTMLLoggerConfiguration;

    private final Map<Class<?>, List<PropertyDescriptor>> cachedPropertyDescriptors = new ConcurrentHashMap<>();

    private final Map<Class<?>, List<String>> cachedPropertyNamesToSanitize = new ConcurrentHashMap<>();

    private static final Pattern API_PACKAGE = Pattern.compile("com\\.myenterprise\\.rest\\..*\\.api.*");

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");

    private final SanitizeHTMLListener sanitizeHTMLListener = new SanitizeHTMLListener();

    /**
     * Creates a new {@code SanitizeHTMLResponse} using the provided
     * configuration properties reader.
     *
     * @param propertiesConfiguration configuration properties reader
     */
    @Autowired
    public SanitizeHTMLResponse(ConfigurationPropertiesReader propertiesConfiguration) {
        this.sanitizeHTMLLoggerConfiguration = new SanitizeHTMLLoggerConfiguration(propertiesConfiguration);
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
            @NotNull MethodParameter returnType,
            @NotNull String propertyName,
            @NotNull Object propertyValue
    ) {

        this.sanitizeHTMLLoggerConfiguration.setFieldName(propertyName)
                   .setModelClassName(clazz.getName())
                   .setControllerClassName(returnType.getDeclaringClass().getName())
                   .setControllerMethodName(Objects.requireNonNull(returnType.getMethod()).getName());

        this.sanitizeHTMLListener.setSanitizerHTMLLoggerConfiguration(this.sanitizeHTMLLoggerConfiguration);

        String sanitizedValue = CKEDITOR_POLICY.sanitize(
                propertyValue.toString(),
                this.sanitizeHTMLListener,
                SanitizeHTMLResponse.class
        );

        this.sanitizeHTMLListener.register();
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

            this.sanitizeValueField( clazz, wrapper, returnType, propertyName, propertyValue );

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
    //@Override
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
    //@Override
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
