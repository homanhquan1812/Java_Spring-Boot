package org.homanhquan.productservice.config;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.exception.CustomAuthenticationEntryPoint;
import org.homanhquan.productservice.security.JwtAuthenticationFilter;
import org.homanhquan.productservice.security.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security definitions:
 * - Spring Security is a security framework for Spring applications that provides authentication (verifying user identity) and authorization (access control) through a chain of security filters.
 * - Security headers: Special HTTP response headers that tell the browser to apply extra security rules to help protect your app against
 *   common attacks such as Clickjacking, MIME sniffing, etc. (Added by default since v3.2).
 * - Authentication: The process of verifying the identity of a user or system before granting access to an application.
 *   Some authentication models:
 *   + Session-Based Authentication (Stateful): An authentication method where the server stores the user’s login session on the server side,
 *   and the client identifies itself by sending a session ID (usually via cookie) on each request.
 *   Pros: High security (session data is not exposed to clients) & Easy invalidation (server can delete or expire sessions anytime).
 *   Cons: Consumes memory (each user session occupies server RAM) & Limited cross-domain use (cookies don’t work easily across different domains or APIs).
 *   Use-cases: Banking & Financial Apps, E-learning Platforms, Live Streaming/Gaming, etc.
 * ==================================================
 *   HTTP by nature is stateless -> Every request from a client is independent; the server doesn’t remember anything about previous ones.
 *   Without sessions, you’d have to log in again on every page & the server wouldn’t know which user sent which request.
 * ==================================================
 *   + Token-Based Authentication (Stateless): An authentication method where the server does not store any session data;
 *   instead, the client includes a self-contained token (e.g., JWT) with every request, and the server validates the token to authenticate the user.
 *   The most common token format is JWT (JSON Web Token), which contains:
 *     * Header: token type and algorithm.
 *     * Payload: user data (username, role, expiry).
 *     * Signature: ensures token hasn't been tampered with.
 *   Pros: Easy to scale (No shared session storage needed) & Cross-domain friendly & Mobile-friendly & Stateless (Server doesn't consume memory for sessions).
 *   Cons: Hard to invalidate (Once issued, tokens remain valid until expiry) & Security risks & Token size (JWTs are larger than session IDs, increasing bandwidth for every request).
 *   Use-cases: SPA (Facebook, X, etc), microservices, mobile apps, etc.
 * - Authorization: The process of determining what an authenticated user is allowed to do.
 *   Some models to implement authorization:
 *   + RBAC (Role-Based Access Control): A model where access permissions are assigned based on a user’s role (e.g., hasRole("ADMIN"), hasAnyRole("USER", "ADMIN")).
 *   + PBAC (Permission-Based Access Control): A model where access is granted based on specific permissions (e.g., hasAuthority("USER_DELETE")).
 *   + ABAC (Attribute-Based Access Control): A model where access decisions are made based on attributes of the user, resource, or environment
 *     (e.g., @PreAuthorize("#doc.ownerId == principal.id")).
 *   -> To use those models, enable @EnableMethodSecurity for @PreAuthorize, @PostAuthorize, @Secured, or @RolesAllowed to control access directly on methods.
 *   -> @PreAuthorize is common among 4 mentioned annotations because it checks permissions before the method executes & uses SpEL (Spring Expression Language) to define conditions.
 * ==================================================
 * Annotation explanation:
 * - @Configuration: Marks a class as a source of bean definitions.
 * - @Bean: Marks a method inside @Configuration class to define and return a Spring bean.
 * - @Primary: Marks a bean as the default choice when multiple beans of the same type exist.
 *   If no @Qualifier is specified during injection, the @Primary bean will be used.
 * - @Qualifier: Is used along with @Autowired to inject a specific bean by name when multiple beans of the same type exist.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Bcrypt: A password-hashing function that securely stores passwords by adding salt and performing multiple hash rounds (mostly 10 rounds).
     * NEVER store plain passwords in database.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Spring Security uses a chain of servlet filters (SecurityFilterChain) that intercept every HTTP request and apply security logic.
     * Each filter handles a specific task (like authentication, authorization, CSRF, CORS, etc).
     * If access is allowed, the request continues to the controller; otherwise, a 401/403 is returned. Here are some main components:
     * - AuthenticationManager : Main entry point for authentication.
     * - AuthenticationProvider : Handles actual verification (e.g., password check).
     *   + Spring automatically provides DaoAuthenticationProvider -> No need to manually create AuthenticationProvider.
     *   + DaoAuthenticationProvider uses UserDetailsService + PasswordEncoder for authentication
     * - UserDetailsService : Loads user data (username, password, roles) from database or other source.
     * - SecurityContext : Stores the current authenticated user (Authentication object).
     * ==================================================
     * Method explanation:
     * - CORS enabled.
     * - CSRF Protection disabled (Recommended for JWT). If Session-based, enable it.
     * - Form login disabled.
     * - Default page login disabled.
     * - Authorization rules for Auth Service endpoints:
     *   + Public -> permitAll(): No authentication required.
     *   + Protected -> authenticated(): Requires valid JWT.
     * - Exception Handling: Custom 401 response format.
     * - JWT is being used (Stateless session management). If Session-based:
     *   + Session mode.
     *   + Limit to 3 sessions per user.
     *   + false: old session invalidated, new login allowed (Kick old session).
     * - Custom JWT Filter: Adds jwtAuthenticationFilter BEFORE UsernamePasswordAuthenticationFilter.
     *   + This ensures JWT validation happens first in the filter chain.
     *   + If JWT is valid → Authentication object is set in SecurityContext.
     *   + If JWT is invalid/missing → Request continues to next filter (will be blocked by authorization rules).
     *   + UsernamePasswordAuthenticationFilter (default for form login) is not used since form login is disabled.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register/**").permitAll()
                        .requestMatchers("/auth/logout").authenticated()

                        // PRODUCT
                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/product/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.PUT,"/api/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/api/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/product/**").hasRole("ADMIN")

                        // ORDER
                        .requestMatchers(HttpMethod.GET, "/api/order/my-list/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/order/all").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.POST,"/api/order/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/order/{orderId}/status").hasAnyRole("ADMIN", "CHEF")

                        // CART
                        .requestMatchers(HttpMethod.GET, "/api/cart").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/cart/add-product").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/cart/{cartItemId}").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * - CORS (Cross-Origin Resource Sharing): Allows a browser (Frontend) to make requests to your server (Backend) from a different domain.
     * - There are 3 approaches:
     *   + Spring Security + CORS: Handles CORS at Security Filter Chain with CorsConfigurationSource.
     *     Recommended to let Spring Security handle CORS at SecurityFilterChain because the browser sends a preflight OPTIONS request (an HTTP method to check permissions)
     *     that Spring Security handles before it reaches your controller. If not allowed, Spring Security blocks it before Spring MVC runs.
     *   + Global MVC CORS: Handles CORS at DispatcherServlet (MVC layer) with WebConfig.
     *     Runs after Spring Security -> Preflight OPTIONS methods are blocked by Security Filter before reaching MVC -> Often causes 401/403 errors.
     *   + Controller Level: Handles CORS at each controller/method with @CrossOrigin.
     *     Repetitive, hard to maintain, and can't prevent Security Filter from blocking preflight requests, leading to inconsistent behavior.
     * - In most projects:
     *   + Microservices: CORS is handled in application.xml by Spring Cloud Gateway.
     *   + Monolith: CORS is handled in SecurityConfig (Add .cors(Customizer.withDefaults()) to http in filterChain).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://your-frontend.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}