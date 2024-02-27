package com.example.personmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig {

    private static final String PERSON_URL_PATTERN = "/api/people/**";
    private static final String EMPLOYEE_URL_PATTERN = "/api/employees/**";
    private static final String UPLOAD_URL_PATTERN = "/api/file-imports/**";

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http,
                                                      HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http.csrf(csrfConfigurer ->
                csrfConfigurer.ignoringRequestMatchers(mvcMatcherBuilder.pattern(PERSON_URL_PATTERN),
                        mvcMatcherBuilder.pattern(EMPLOYEE_URL_PATTERN),
                        mvcMatcherBuilder.pattern(UPLOAD_URL_PATTERN)
                ));

        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers(mvcMatcherBuilder.pattern(PERSON_URL_PATTERN)).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(EMPLOYEE_URL_PATTERN)).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(UPLOAD_URL_PATTERN)).permitAll()
                        .anyRequest().authenticated()

        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails employee = User.withDefaultPasswordEncoder()
                .username("employee")
                .password("employee")
                .roles("EMPLOYEE")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN", "USER")
                .build();
        UserDetails importer = User.withDefaultPasswordEncoder()
                .username("import")
                .password("import")
                .roles("IMPORTER")
                .build();

        return new InMemoryUserDetailsManager(List.of(employee, admin, importer));
    }


}