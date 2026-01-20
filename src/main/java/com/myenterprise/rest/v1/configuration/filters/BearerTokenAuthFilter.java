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
package com.myenterprise.rest.v1.configuration.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authentication filter for Bearer tokens.
 * This filter executes once per request and verifies the presence and validity of an authentication token
 * in the "Authorization" header for paths starting with "/api".
 * If the path starts with "/api" and the token is not present or is not "1234567890",
 * the request is rejected with a 401 (Unauthorized) status.
 * If the token is valid (or the path is not "/api"), a test authentication is set
 * in Spring's security context.
 * <p>
 * This filter is a simplified example and should not be used as-is in a production environment.
 * It uses a fixed token ("1234567890") and a test authentication (`TestingAuthenticationToken`).
 * </p>
 *
 * @see <a href="https://medium.com/@sallu-salman/implementing-token-based-authentication-in-a-spring-boot-project-dba7811ffcee">Original reference</a>
 */
@Component
public class BearerTokenAuthFilter extends OncePerRequestFilter {

    /**
     * Performs the filtering logic for each request.
     * <p>
     * This method intercepts requests to check if they are directed to paths
     * starting with "/api". If so, it extracts the "Authorization" header
     * and verifies if it contains the token "1234567890". If the token is invalid or missing,
     * the response is set to 401 (Unauthorized).
     * Otherwise, or if the path does not start with "/api", a test authentication
     * (`TestingAuthenticationToken`) is set in the security context with the role "WRITE_HOTELS".
     * </p>
     *
     * @param request The {@link HttpServletRequest} object representing the HTTP request.
     * @param response The {@link HttpServletResponse} object representing the HTTP response.
     * @param filterChain The {@link FilterChain} object to invoke the next filter in the chain.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Checks if the request URI starts with "/api"
        // If the authorization header is null or does not contain the expected token
        // (assuming a Bearer token, hence comparing from index 7)
        if (
                request.getRequestURI().startsWith("/api") &&
                        (authHeader == null || ! "1234567890".equals(authHeader.substring(7)))
        ) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Sets the 401 status
            return;
        }

        // If the path is not "/api" or the token is valid for "/api" paths,
        // a test authentication is created and set.
        // In a real application, actual token validation would occur here
        // (JWT, OAuth2, etc.) and an Authentication object would be built based on the token information.
        Authentication authentication =
                new TestingAuthenticationToken("user", "12345", "WRITE_HOTELS");
        SecurityContextHolder.getContext().setAuthentication(authentication);


        // Continues with the filter chain
        filterChain.doFilter(request, response);
    }
}