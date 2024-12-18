package com.example.PersonalAccounting.config;

import com.example.PersonalAccounting.config.securityFilters.CsrfLoggerFilter;
import com.example.PersonalAccounting.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CsrfLoggerFilter csrfLoggerFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, CsrfLoggerFilter csrfLoggerFilter) {
        this.userDetailsService = userDetailsService;
        this.csrfLoggerFilter = csrfLoggerFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                //TODO: Remove in future (add for easier testing)

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("users", "transactions/all", "accumulations/all",
                                "/financial-arrangements/all").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/process_login")
                        .defaultSuccessUrl("/auth", true)
                        .failureUrl("/login?error")
                )
                .logout(out -> out
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                );

        //httpSecurity.addFilterAfter(csrfLoggerFilter, CsrfFilter.class);

        return httpSecurity.build();
    }


}
