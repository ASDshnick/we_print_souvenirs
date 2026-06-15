package com.weprintsouvenirs.we_print_souvenirs.config;

import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole() != null ? user.getRole().name() : "USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // активация cors
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        .requestMatchers("/user/orders").permitAll()
                        .requestMatchers("/user/orders/{orderId}").permitAll()
                        .requestMatchers("/user/profile").permitAll()
                        .requestMatchers("/user/change-data").permitAll()
                        .requestMatchers("/chat/{orderId}").permitAll()

                        .requestMatchers(HttpMethod.GET, "/admin").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/users/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/orders/{orderId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/orders/{orderId}/chat").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/admin/users/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/admin/users/{userId}/note").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/users/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/admin/orders/{orderId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/admin/orders/{orderId}/note").hasRole("ADMIN")

                        .requestMatchers("/ws/**").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/user/change-data").authenticated()
                        .requestMatchers(HttpMethod.POST, "/order/checkout").authenticated()
                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/chat/{orderId}/history").authenticated()

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/profile.html",
                                "/orders.html",
                                "/order-detail.html",
                                "/admin.html",
                                "/admin-user.html",
                                "/admin-orders.html",
                                "/admin-chat.html",
                                "/edit-profile.html",
                                "/assets/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:63342",
                "http://127.0.0.1:63342",
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}