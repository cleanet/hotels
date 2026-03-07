/*
 *   MIT License
 *
 *  Copyright (c) 2026 cleanet
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
package com.myenterprise.rest.v1.configuration;

import com.myenterprise.rest.annotation.validatersql.ValidateRsqlHandlerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import com.myenterprise.rest.v1.configuration.filters.BearerTokenAuthFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Configuration for allow to public access to Swagger UI and the API Documentation by OpenAPI.
 * <p>
 * This class defines a {@link SecurityFilterChain} for configure the HTTP authorization rules.
 * </p>
 * reference: <a href="https://www.baeldung.com/java-spring-security-permit-swagger-ui">Spring Boot 3 – Configure Spring Security to Allow Swagger UI</a>
 * Main security configuration class for the application.
 * This class is responsible for setting up the Spring Security filter chain,
 * defining access control rules, and configuring Cross-Origin Resource Sharing (CORS).
 */
@Configuration
@EnableWebSecurity
@EnableCaching
public class SecurityConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ValidateRsqlHandlerInterceptor());
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method sets up HTTP authorization rules, allowing public access to Swagger UI and OpenAPI documentation,
     * and restricting access to the API endpoints based on user authorities.
     * It adds a custom {@link BearerTokenAuthFilter} before the {@link LogoutFilter}.
     * It also disables CSRF and configures various security headers.
     * </p>
     *
     * @param http The {@link HttpSecurity} object to configure HTTP security.
     * @return A configured {@link SecurityFilterChain} instance.
     * @throws Exception if an error occurs during the security configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {

        BearerTokenAuthFilter bearerTokenAuthFilter = new BearerTokenAuthFilter();
        http
                //.securityMatcher("/api/**") // This line is commented out and is not currently in use.
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests.requestMatchers(
                                                "/swagger-ui/**",
                                                "/v3/api-docs*/**")
                                        .permitAll()
                                        .requestMatchers("/api/**")
                                        .hasAuthority("WRITE_HOTELS")
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF is disabled because the authentication is by JWT.
                .headers( header ->
                        header.xssProtection( xss ->
                                        xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                                ).contentSecurityPolicy(
                                        contentSecurityPolicy ->
                                                contentSecurityPolicy.policyDirectives("script-src 'self'")
                                )
                                .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                )
                .addFilterBefore(bearerTokenAuthFilter, LogoutFilter.class);
        return http.build();
    }
}