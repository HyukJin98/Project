package edu.du.samplep.config;

import edu.du.samplep.entity.User;
import edu.du.samplep.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private UserRepository userRepository;


    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider) // CustomAuthenticationProvider 등록
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/friends/**").authenticated()
                .antMatchers("/user").hasRole("USER")
                .antMatchers("/manager").hasRole("MANAGER")
                .antMatchers("/notice/create").hasAuthority("ROLE_MANAGER")
                .antMatchers("/css/**", "/js/**", "/images/**", "/register/**", "/login", "/basic","/","/posts/**","/user/**","update-success","/comments/**","/assets/**","/upload","/uploads/**","/messages/**","/friendship/**","/login-fail","/login-suspend").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/")
                .defaultSuccessUrl("/login-success",true)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout-success")
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf().disable();

        http.authenticationManager(authManager);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner dataLoader(PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("manager@example.com").isEmpty()) {
                edu.du.samplep.entity.User manager = User.builder()
                        .email("manager@example.com")
                        .username("관리자")
                        .password(passwordEncoder.encode("112233"))
                        .role("ROLE_MANAGER")
                        .build();
                userRepository.save(manager);
            }
        };
    }

}
