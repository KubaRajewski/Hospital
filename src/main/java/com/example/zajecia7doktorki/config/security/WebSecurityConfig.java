package com.example.zajecia7doktorki.config.security;

import com.example.zajecia7doktorki.config.security.jwt.CustomAccessDeniedHandler;
import com.example.zajecia7doktorki.config.security.jwt.CustomAuthenticationEntryPoint;
import com.example.zajecia7doktorki.config.security.jwt.JwtAuthenticationFilter;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import com.example.zajecia7doktorki.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final CustomAuthenticationEntryPoint entryPoint;

    private final CustomerRepository customerRepository;

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "v2/api-docs",
            "/swagger-resources",
            "swagger-resources",
            "/swagger-resources/**",
            "swagger-resources/**",
            "/configuration/ui",
            "configuration/ui",
            "/configuration/security",
            "configuration/security",
            "/swagger-ui.html",
            "swagger-ui.html",
            "webjars/**",
            // -- Swagger UI v3
            "/v3/api-docs/**",
            "v3/api-docs/**",
            "/swagger-ui/**",
            "swagger-ui/**",
            // CSA Controllers
            "/csa/api/token",
            // Actuators
            "/actuator/**",
            "/health/**"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService(customerRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        try {
            httpSecurity
                    .csrf()
                    .disable()
                    .authorizeRequests(authz -> {
                                try {
                                    authz
                                            .antMatchers(AUTH_WHITELIST).permitAll()
                                            .antMatchers(HttpMethod.POST, "/api/v1/register/**").permitAll()
                                            .antMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                                            .antMatchers("/api/v1/admins/***").hasAuthority("ADMIN")
                                            .antMatchers("/api/v1/doctors/***").hasAnyAuthority("ADMIN", "USER")
                                            .antMatchers("/api/v1/patients/***").hasAnyAuthority("ADMIN", "USER")
                                            .anyRequest()
                                            .authenticated()
                                            .and()
                                            .sessionManagement()
                                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                            .and()
                                            .authenticationProvider(authenticationProvider())
                                            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                            .exceptionHandling()
                                            .authenticationEntryPoint(entryPoint)
                                            .accessDeniedHandler(accessDeniedHandler());
                                } catch (Exception e) {
                                    log.error("Error occurred with filterChain {}", e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            }
                    ).httpBasic(Customizer.withDefaults());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return httpSecurity.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
