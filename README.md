# hotels
This Spring Boot project is an example of API Server deployed with openapi-generator-maven-plugin. 
## Configuration
In the pom.xml is added the following configurations:
### openapi-generator
The plugin, version 7.14.0
````xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.14.0</version>
    <executions>
        <execution>
            <id>spring</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/api.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <output>${project.build.directory}</output>
                <configurationFile>${project.basedir}/src/main/resources/config.yaml</configurationFile>
                <templateDirectory>${project.basedir}/src/main/resources/templates</templateDirectory>
            </configuration>
        </execution>
        <!-- Generate postman collection JSON -->
        <execution>
            <id>postman</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/api.yaml</inputSpec>
                <generatorName>postman-collection</generatorName>
                <output>${project.build.directory}</output>
            </configuration>
        </execution>
    </executions>
</plugin>
````
There are two executors. Are two generators.
1. Spring
2. postman-collection
#### Spring
This generator generate the code Spring Boot based in the API Specification.
The configuration is defined in the element `<configuation>`.
* **inputSpec**: Specific the path where is the API Specification. Stored in the `resources` folder 
* **generatorName**: the generator name to use.
* **output**: The path of destination where the code is generated. Is generated inside the `target` folder
* **configurationFile**: The path of configuration file in YAML. Stored in the `resources` folder.
* **templateDirectory**: The path of mustache templates folder.

The configuration options inside the `config.yaml` are following:
```shell
$ openapi-generator-cli config-help -g spring
CONFIG OPTIONS

	additionalEnumTypeAnnotations
	    Additional annotations for enum type(class level annotations)

	additionalModelTypeAnnotations
	    Additional annotations for model type(class level annotations). List separated by semicolon(;) or new line (Linux or Windows)

	additionalOneOfTypeAnnotations
	    Additional annotations for oneOf interfaces(class level annotations). List separated by semicolon(;) or new line (Linux or Windows)

	allowUnicodeIdentifiers
	    boolean, toggles whether unicode identifiers are allowed in names or not, default is false (Default: false)

	annotationLibrary
	    Select the complementary documentation annotation library. (Default: swagger2)
	        none - Do not annotate Model and Api with complementary annotations.
	        swagger1 - Annotate Model and Api using the Swagger Annotations 1.x library.
	        swagger2 - Annotate Model and Api using the Swagger Annotations 2.x library.

	apiFirst
	    Generate the API from the OAI spec at server compile time (API first approach) (Default: false)

	apiPackage
	    package for generated api classes (Default: org.openapitools.api)

	artifactDescription
	    artifact description in generated pom.xml (Default: OpenAPI Java)

	artifactId
	    artifactId in generated pom.xml. This also becomes part of the generated library's filename (Default: openapi-spring)

	artifactUrl
	    artifact URL in generated pom.xml (Default: https://github.com/openapitools/openapi-generator)

	artifactVersion
	    artifact version in generated pom.xml. This also becomes part of the generated library's filename. If not provided, uses the version from the OpenAPI specification file. If that's also not present, uses the default value of the artifactVersion option. (Default: 1.0.0)

	async
	    use async Callable controllers (Default: false)

	basePackage
	    base package (invokerPackage) for generated code (Default: org.openapitools)

	bigDecimalAsString
	    Treat BigDecimal values as Strings to avoid precision loss. (Default: false)

	booleanGetterPrefix
	    Set booleanGetterPrefix (Default: get)

	camelCaseDollarSign
	    Fix camelCase when starting with $ sign. when true : $Value when false : $value (Default: false)

	configPackage
	    configuration package for generated code (Default: org.openapitools.configuration)

	containerDefaultToNull
	    Set containers (array, set, map) default to null (Default: false)

	dateLibrary
	    Option. Date library to use (Default: java8)
	        joda - Joda (for legacy app only)
	        legacy - Legacy java.util.Date
	        java8-localdatetime - Java 8 using LocalDateTime (for legacy app only)
	        java8 - Java 8 native JSR310 (preferred for jdk 1.8+)

	delegatePattern
	    Whether to generate the server files using the delegate pattern (Default: false)

	developerEmail
	    developer email in generated pom.xml (Default: team@openapitools.org)

	developerName
	    developer name in generated pom.xml (Default: OpenAPI-Generator Contributors)

	developerOrganization
	    developer organization in generated pom.xml (Default: OpenAPITools.org)

	developerOrganizationUrl
	    developer organization URL in generated pom.xml (Default: http://openapitools.org)

	disableHtmlEscaping
	    Disable HTML escaping of JSON strings when using gson (needed to avoid problems with byte[] fields) (Default: false)

	disallowAdditionalPropertiesIfNotPresent
	    If false, the 'additionalProperties' implementation (set to true by default) is compliant with the OAS and JSON schema specifications. If true (default), keep the old (incorrect) behaviour that 'additionalProperties' is set to false by default. (Default: true)
	        false - The 'additionalProperties' implementation is compliant with the OAS and JSON schema specifications.
	        true - Keep the old (incorrect) behaviour that 'additionalProperties' is set to false by default.

	discriminatorCaseSensitive
	    Whether the discriminator value lookup should be case-sensitive or not. This option only works for Java API client (Default: true)

	documentationProvider
	    Select the OpenAPI documentation provider. (Default: springdoc)
	        none - Do not publish an OpenAPI specification.
	        source - Publish the original input OpenAPI specification.
	        springfox - Generate an OpenAPI 2 (fka Swagger RESTful API Documentation Specification) specification using SpringFox 2.x. Deprecated (for removal); use springdoc instead.
	        springdoc - Generate an OpenAPI 3 specification using SpringDoc.

	ensureUniqueParams
	    Whether to ensure parameter names are unique in an operation (rename parameters that are not). (Default: true)

	enumPropertyNaming
	    Naming convention for enum properties: 'MACRO_CASE', 'legacy' and 'original' (Default: MACRO_CASE)

	enumUnknownDefaultCase
	    If the server adds new enum cases, that are unknown by an old spec/client, the client will fail to parse the network response.With this option enabled, each enum will have a new case, 'unknown_default_open_api', so that when the server sends an enum case that is not known by the client/spec, they can safely fallback to this case. (Default: false)
	        false - No changes to the enum's are made, this is the default option.
	        true - With this option enabled, each enum will have a new case, 'unknown_default_open_api', so that when the enum case sent by the server is not known by the client/spec, can safely be decoded to this case.

	generateBuilders
	    Whether to generate builders for models (Default: false)

	generateConstructorWithAllArgs
	    whether to generate a constructor for all arguments (Default: false)

	generatedConstructorWithRequiredArgs
	    Whether to generate constructors with required args for models (Default: true)

	groupId
	    groupId in generated pom.xml (Default: org.openapitools)

	hateoas
	    Use Spring HATEOAS library to allow adding HATEOAS links (Default: false)

	hideGenerationTimestamp
	    Hides the generation timestamp when files are generated. (Default: false)

	ignoreAnyOfInEnum
	    Ignore anyOf keyword in enum (Default: false)

	implicitHeaders
	    Skip header parameters in the generated API methods using @ApiImplicitParams annotation. (Default: false)

	implicitHeadersRegex
	    Skip header parameters that matches given regex in the generated API methods using @ApiImplicitParams annotation. Note: this parameter is ignored when implicitHeaders=true

	interfaceOnly
	    Whether to generate only API interface stubs without the server files. (Default: false)

	invokerPackage
	    root package for generated code (Default: org.openapitools.api)

	legacyDiscriminatorBehavior
	    Set to false for generators with better support for discriminators. (Python, Java, Go, PowerShell, C# have this enabled by default). (Default: true)
	        true - The mapping in the discriminator includes descendent schemas that allOf inherit from self and the discriminator mapping schemas in the OAS document.
	        false - The mapping in the discriminator includes any descendent schemas that allOf inherit from self, any oneOf schemas, any anyOf schemas, any x-discriminator-values, and the discriminator mapping schemas in the OAS document AND Codegen validates that oneOf and anyOf schemas contain the required discriminator and throws an error if the discriminator is missing.

	library
	    library template (sub-template) (Default: spring-boot)
	        spring-boot - Spring-boot Server application.
	        spring-cloud - Spring-Cloud-Feign client with Spring-Boot auto-configured settings.
	        spring-http-interface - Spring 6 HTTP interfaces (testing)

	licenseName
	    The name of the license (Default: Unlicense)

Spring Boot Server

## Overview
This server was generated by the [OpenAPI Generator](https://openapi-generator.tech) project.
By using the [OpenAPI-Spec](https://openapis.org), you can easily generate a server stub.
This is an example of building a OpenAPI-enabled server in Java using the SpringBoot framework.


The underlying library integrating OpenAPI to Spring Boot is [springdoc](https://springdoc.org).
Springdoc will generate an OpenAPI v3 specification based on the generated Controller and Model classes.
The specification is available to download using the following url:
http://localhost:8000/v3/api-docs/

Start your server as a simple java application

You can view the api documentation in swagger-ui by pointing to
http://localhost:8000/swagger-ui.html

Change default port value in application.properties
```
For more information of generator spring: https://openapi-generator.tech/docs/generators/spring
##### requeriments
###### Spring Doc
openapi-generator by default use Spring Docs (unless otherwise specified). Is need install the dependency
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```
This generates the Swagger UI in the path `/swagger-ui/index.html`. Is configurable.
And the path `/v3/api-docs`, shows the api specification in JSON. Based in the code generated.

###### Spring Security
openapi-generator by default use Spring Security. Is need install the dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
By default, all the path are protected with user/password (the password is generated in compilation).
By which, the API paths (GET,POST,PUT,DELETE) will return a `401 Unauthorized`.

For access to Swagger UI without authentication, define the class `com.myenterprise.rest.v1.configuration.SecurityConfiguration`.
This class define the authorization. There two ways.
1. Use the `securityMatcher()` specifying the basePath. Doing only this path (recursively) will is protected.
2. Use the `requestMatchers()` allowing the paths: `/swagger-ui/**, /v3/api-docs*/**`. We use this way.

Also add the authentication, adding a new filter:
```
.addFilterBefore(bearerTokenAuthFilter, LogoutFilter.class);
```
The class `com.myenterprise.rest.v1.configuration.filters.bearerTokenAuthFilter` read the bearer token if is valid.
###### H2 database (embedded database)
Is necessary the dependency:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```
#### postman-collection
This generator generates a postman collection in json format.
In the root path of `target` folder. This is very useful for do testing.
This isn't need configuration

For more information of generator: https://openapi-generator.tech/docs/generators/postman-collection/
### Maven Site
Is installed the plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <version>4.0.0-M16</version>
</plugin>
```
So at execute: `mvn site`. This generates the folder `site` in `target`.
### JavaDoc in Maven Site
Also, add the JavaDoc:
```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.11.2</version>
        </plugin>
    </plugins>
</reporting>
```
Inside the `<project>`
This generates the folder `/site/apidocs`.

Thanks acampos.
### Preventing XSS Attack
Now, we add protection for prevent XSS Attack. In two ways.
1. Using the Spring Security.
2. Creating the annotation `@SanitizeHTML`

#### 1. Using the Spring Security
In Spring Security add the header `X-XSS-Protection`. This header tells the browser to block what looks like XSS.
Enabling the XSS filter in most recent web browsers.

For enable it, add the following:
```
http.headers( header ->
    header.xssProtection( xss ->
            xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
    )
)
```
With the method `headers()`, we add security headers in spring boot. The method `header.xssProtection()` adds the header
`X-XSS-Protection`, with the method `xss.headerValue()`, adding the value `1; mode=block` with the enum `XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK`.
Enabling it in block mode.

Also, in Spring Security add the Content Security Policy (CSP) feature.
The CSP is an added layer of security that helps mitigate XSS and data injection attacks.
```
http.headers( header ->
    header.xssProtection( xss ->
            xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
    ).contentSecurityPolicy(
        contentSecurityPolicy ->
            contentSecurityPolicy.policyDirectives("script-src 'self'")
    )
)
```
In the `contentSecurityPolicy()` method we add the policy directive `script-src 'self'`.
This policy checks the valid sources of Javascript. More information in:
https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/script-src

With content Security Policy we can add all these policies:
https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy

More information of XSS Security in https://www.baeldung.com/spring-prevent-xss

#### 2. Creating the annotation `@SanitizeHTML`
This project is a Spring Boot API Server, so optionally we created the annotation `@SanitizeHTML`.
This annotation is used in the fields of type String of the API paths.
The annotation cleans the content of field. Deleting HTML tags and attributes that can be dangerous, preventing XSS attacks.
Also, these stores it in the log file as warning.

The packages with the classes are following:
* **com.myenterprise.rest.annotation**
  * **SanitizeHTML**: Annotation definition
* **com.myenterprise.rest.components**
  * **CHEditorSanitizerPolicy**: A policy component for sanitizing HTML content generated by CKEditor.
  * **SanitizerHTMLRequestBody**: Advice that sanitizes HTML content in request bodies after they are read and before they are passed to controller methods.
  * **SanitizerHTMLResponse**: Advice that sanitizes HTML content in response bodies before they are written to the HTTP response.
  * **SanitizerHTMLListener**: A component that acts as a listener for discarded HTML tags and attributes during sanitization. And is stored in logfile
  * **SanitizerHTMLLoggerConfiguration**: A configuration component for logging sanitization events.
* **com.myenterprise.rest.v1.configuration**:
  * **ConfigurationPropertiesReader**: Reads configuration properties for the application.

The annotation uses the OWASP Java HTML sanitizer library for sanitizes the HTML content. Installed with the dependency:
```xml
<dependency>
    <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
    <artifactId>owasp-java-html-sanitizer</artifactId>
    <version>20240325.1</version>
</dependency>
```
More information in: https://www.baeldung.com/java-sanitize-html-prevent-xss-attacks

The objective of we create this annotation, is use it in API's paths fields that may contain HTML comming from rich text.
As for example fields: name, description, short_description,...

we would use it in all the properties of String type.

As the API Server is created with API First. With openapi-generator-maven-plugin. In the properties definitions of all schemas.
We add the keyword `x-field-extra-annotation`. For example:
```yaml
components:
  schemas:
    HotelInput:
      type: object
      properties:
        name:
          x-field-extra-annotation: '@SanitizeHTML'
          type: string
          example: Hotel Continental
        description:
          x-field-extra-annotation: '@SanitizeHTML'
          type: string
          example: This is hotel is the best hotel of the world
        address:
          x-field-extra-annotation: '@SanitizeHTML'
          type: string
          example: Street Falsa 123
        city:
          x-field-extra-annotation: '@SanitizeHTML'
          type: string
          example: Madrid
        rating:
          type: number
          format: float
          example: 4.5
        has_wifi:
          type: boolean
          example: true
      required: [ name, address, city, rating, has_wifi ]
```
Is very important add the `model.mustache` in `/src/main/resources/templates/`
Add the importation of the custom annotation:
```
import {{basePackage}}.annotation.SanitizeHTML;
```
The `basePackage` is configured in `config.yaml` in resources folder:
```
basePackage: com.myenterprise.rest
```
The output is:
```
import com.myenterprise.rest.annotation.sanitizeHTML.SanitizeHTML;
```
otherwise, the compilation will fail.

If we don't want to use the mustaches templates. We add in the `x-field-extra-annotation` property with the value:
`@com.myenterprise.rest.annotation.sanitizeHTML.SanitizeHTML`. So is not necessary to import the class.

### CORS
Now, we configure the CORS. For only allows the origins `http://localhost:8080`.

First in the `SecurityConfiguration` class implements `WebMvcConfigurer` class. And we add the annotation `@EnableWebSecurity`

After overrides the method `addCorsMappings`.
```java
@Override
public void addCorsMappings(@NotNull CorsRegistry registry) {
    registry
            .addMapping("/**")
            .allowedOrigins(configurationPropertiesReader.origins);
}
```
The `configurationPropertiesReader` is the class `ConfigurationPropertiesReader` declared with Autowired.

The variable `origins` in the class `ConfigurationPropertiesReader` gets the value of property `management.endpoints.web.cors.allowed-origins`
in the application.yaml

For more information: https://www.baeldung.com/spring-cors#1-javaconfig

### Rate Limiting
Now, we will configure rate limiting in our endpoints. This is useful to prevent DDOS/DOS attacks.
We are guided by https://www.baeldung.com/spring-bucket4j#bd-boot-starter.

This way, implement rate limiting with bucket4j using the [Token-bucket Algorithm](https://www.baeldung.com/spring-bucket4j#token-bucket)

![token-bucket-algorithm](/src/main/resources/docs/krakend-token-bucket.webp)

This allows to configure it globally in `application.yaml`.

First we install the follow dependencies:
```xml
<dependencies>
  ...
  <dependency>
      <groupId>com.bucket4j</groupId>
      <artifactId>bucket4j_jdk17-core</artifactId>
      <version>8.14.0</version>
  </dependency>
  <dependency>
      <groupId>com.giffing.bucket4j.spring.boot.starter</groupId>
      <artifactId>bucket4j-spring-boot-starter</artifactId>
      <version>0.13.0</version>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>
  <dependency>
      <groupId>javax.cache</groupId>
      <artifactId>cache-api</artifactId>
  </dependency>
  <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>caffeine</artifactId>
      <version>3.2.2</version>
  </dependency>
  <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>jcache</artifactId>
      <version>3.2.2</version>
  </dependency>
  ...
</dependencies>
```
For implement the rate limiting we will use:
* bucket4j
* cache jcache

After we add the `@EnableCaching` annotation in `SecurityConfiguration` for enable the cache management.

Finally, in `application.yaml` follow configuration:
1. We configure the cache
```yaml
spring:
  cache:
    cache-names:
      - rate-limit-buckets
    caffeine:
      spec: maximumSize=100000,expireAfterAccess=3600s 
```
Creating the `rate-limit-buckets` cache name
2. We apply the bucket4j configuration:
```yaml
bucket4j:
  enabled: true
  filters:
    - cache-name: rate-limit-buckets
      url: /api/.*
      strategy: first
      http-response-body: "{ \"error\": \"HOTELS-ERROR-00429\", \"message\": \"Too many Requests. You have exhausted your API Request Quota\" }"
      rate-limits:
        - cache-key: "getRemoteAddr()"
          bandwidths:
            - capacity: 5
              time: 1
              unit: minutes 
```
This configuration enables the bucket4j, and add a filter.
This filter adds in the caches `rate-limit-buckets` that in the paths of base path `/api/` is added a rate-limit.
This rate-limit is by IP Address of client. And this adds a bandwidth of maximum 5 request per 1 minute.

If a client overs the limit, the API will return the response body specified in the property `http-response-body`

This rate-limit is an example.

You can tell him for example:
* The rate-limit is by API-key and the API key starts with BX001- has a bandwidth of 100 requests per 2 hours
```yaml
        - cache-key: "getHeader('X-api-key')"
          execute-condition: "getHeader('X-api-key').startsWith('BX001-')"
          bandwidths:
            - capacity: 100
              time: 2
              unit: hours 
```

The properties `execute-condition` and `cache-key` uses [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html)
For more information of properties: https://github.com/MarcGiffing/bucket4j-spring-boot-starter#bucket4j_complete_properties

Also, the configuration allows specific the method type. For example
* The rate-limit is by API-key and the API key starts with BX001- and the method is GET, has a bandwidth of 100 requests per 2 hours
```yaml
        - cache-key: "getHeader('X-api-key')"
          execute-condition: "getHeader('X-api-key').startsWith('BX001-')"
          execute-predicates:
            - METHOD: GET
          bandwidths:
            - capacity: 100
              time: 2
              unit: hours 
```
(not tested), based in the documentation

### UUIDs as primary keys
This is recomendable, the registers in the DB your primary key is a UUID.

If the key is sequential, there is a risk that an ill-intentioned try get all the IDs by brute force.
If the key is UUD, this is not possible, so is random (the possibility is minimal).

For example, for this POC, in the `HotelsRepository` class is defined the field `id`:
```java
    @Id
    @GeneratedValue
    private UUID id;
```

In this case, the UUID is managed by the application, because is a embedded database (H2)

For production, the database (mysql, oracle...) must manage the ID of type UUID.
