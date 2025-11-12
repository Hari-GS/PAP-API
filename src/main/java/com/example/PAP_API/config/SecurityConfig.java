package com.example.PAP_API.config;

//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
//    private final UserAuthenticationProvider userAuthenticationProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Return 401 for unauthorized access
//                .exceptionHandling(exception ->
//                        exception.authenticationEntryPoint(userAuthenticationEntryPoint)
//                )
//                // Add custom JWT filter before Spring’s BasicAuthenticationFilter
//                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
//                .csrf(csrf -> csrf.disable()) // No CSRF for stateless APIs
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(auth ->
//                        auth
//                                // Allow login/register endpoints (usually under /auth)
//                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
//
//                                // TEST ENDPOINTS BASED ON ROLE
//                                .requestMatchers("/api/test/hr").hasRole("HR") // Only HR can access
//                                .requestMatchers("/api/test/employee").hasRole("EMPLOYEE") // Only Employee can access
//
//                                // All other endpoints require authentication
//                                .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//}
//Commented out for deployment testing

import lombok.RequiredArgsConstructor;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.http.HttpMethod;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
        import org.springframework.security.config.http.SessionCreationPolicy;
        import org.springframework.security.web.SecurityFilterChain;
        import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
        import org.springframework.web.cors.CorsConfiguration;
        import org.springframework.web.cors.CorsConfigurationSource;
        import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
        import java.util.Arrays;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ENABLE CORS - THIS IS WHAT'S MISSING
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Return 401 for unauthorized access
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(userAuthenticationEntryPoint)
                )
                // Add custom JWT filter before Spring's BasicAuthenticationFilter
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // No CSRF for stateless APIs
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                // Allow OPTIONS requests for CORS preflight - CRITICAL!
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // Allow login/register endpoints (usually under /auth)
                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/auth/verify-invite").permitAll()
                                .requestMatchers(HttpMethod.GET, "/auth/organization/verify").permitAll()
                                // TEST ENDPOINTS BASED ON ROLE
                                .requestMatchers("/api/test/hr").hasRole("HR") // Only HR can access
                                .requestMatchers("/api/test/employee").hasRole("EMPLOYEE") // Only Employee can access
                                // All other endpoints require authentication
                                .anyRequest().authenticated()
                );
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://18.141.93.210:91",  // Your React app
                "http://localhost:3000"     // Local development
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
