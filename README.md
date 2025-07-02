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