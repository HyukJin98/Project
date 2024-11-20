package edu.du.samplep.config;

import edu.du.samplep.entity.User;
import edu.du.samplep.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/friends/**").authenticated()
                .antMatchers("/user").hasRole("USER")
                .antMatchers("/manager").hasRole("MANAGER")
                .antMatchers("/notice/create").hasAuthority("ROLE_MANAGER")
                .antMatchers("/css/**", "/js/**", "/images/**", "/register/**", "/login", "/basic","/","/posts/**","/user/**","update-success","/comments/**","/assets/**","/upload","/uploads/**","/messages/**","/friendship/**").permitAll() // /basic을 permitAll()로 설정
                .anyRequest().authenticated() // 그 외의 경로는 인증이 필요하도록 설정
                .and()
                .formLogin()
                .loginPage("/")
                .defaultSuccessUrl("/login-success",true)
                .failureUrl("/login-fail")
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
                .csrf().disable();  // CSRF 비활성화

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
