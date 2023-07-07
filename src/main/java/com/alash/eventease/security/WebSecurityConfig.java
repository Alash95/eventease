package com.alash.eventease.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private UserDetailsService userDetailsService;

    private static final String[] ADMIN_SECURED_URL = {
            "/api/v1/admin/create-role",
            "/api/v1/admin/**"
    };

    private static final String[] UN_SECURED_URL = {
            "/api/v1/user/**",
            "/api/v1/admin/adminsignup/**",
            "/api/v1/admin/user-role/**",

//            "/v2/api-docs",
//            "/v3/api-docs/**",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/configuration/ui",
//            "/configuration/security",
//            "/swagger-ui/**",
//            "/swagger-ui.html"
    };

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //    public UserDetailsService userDetailsService() {
//        UserDetails firstUser = User.withUsername("musa")
//                .password(passwordEncoder().encode("1234"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails secondUser = User.withUsername("ade")
//                .password(passwordEncoder().encode("12345"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(firstUser, secondUser);
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                                authorize.requestMatchers(HttpMethod.POST,"/api/v1/user/signup").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/api/v1/admin/adminsignup").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/api/v1/user/resetpassword").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/v1/user/signin").permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/resetpassword").hasAuthority("USER")
                                        .requestMatchers(HttpMethod.GET, "/api/v1/user/fetchAll").hasRole("ADMIN")
//                                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/create-role").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.GET, "/api/v1/user/getUser").hasRole("USER")
                                        .requestMatchers(HttpMethod.POST, "/api/v1/user/event").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.POST, "/api/v1/user/event").hasRole("USER")
                                        .requestMatchers(ADMIN_SECURED_URL).hasRole("ADMIN")
                                        .requestMatchers(UN_SECURED_URL).permitAll()
                                        .anyRequest().authenticated()
//                        hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                )
                .httpBasic(Customizer.withDefaults());

        http.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider());
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
