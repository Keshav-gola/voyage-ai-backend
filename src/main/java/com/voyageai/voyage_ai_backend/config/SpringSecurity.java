package com.voyageai.voyage_ai_backend.config;

import com.voyageai.voyage_ai_backend.filter.JwtFilter;
import com.voyageai.voyage_ai_backend.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SpringSecurity(JwtFilter jwtFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ No sessions (JWT based)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/travel/plan").permitAll()
                        .requestMatchers("/travel/chat").permitAll()
                        .requestMatchers("/travel/save").authenticated()
                        .requestMatchers("/travel/my-trips").authenticated()
                        .anyRequest().authenticated()
                )

                // ✅ Add JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}