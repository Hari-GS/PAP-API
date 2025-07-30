package com.example.PAP_API.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Return 401 for unauthorized access
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(userAuthenticationEntryPoint)
                )
                // Add custom JWT filter before Spring’s BasicAuthenticationFilter
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // No CSRF for stateless APIs
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                // Allow login/register endpoints (usually under /auth)
                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                                // TEST ENDPOINTS BASED ON ROLE
                                .requestMatchers("/api/test/hr").hasRole("HR") // Only HR can access
                                .requestMatchers("/api/test/employee").hasRole("EMPLOYEE") // Only Employee can access

                                // All other endpoints require authentication
                                .anyRequest().authenticated()
                );

        return http.build();
    }
}
