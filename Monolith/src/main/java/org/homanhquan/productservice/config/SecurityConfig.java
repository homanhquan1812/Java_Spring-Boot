package org.homanhquan.productservice.config;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.exception.CustomAuthenticationEntryPoint;
import org.homanhquan.productservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("<http://localhost:3000>", "<https://your-frontend.com>"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}