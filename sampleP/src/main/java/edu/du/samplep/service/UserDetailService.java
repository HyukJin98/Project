package edu.du.samplep.service;

import edu.du.samplep.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("==========> 사용자: " + username);

        edu.du.samplep.entity.User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // 사용자 권한을 생성 (예: ROLE_USER)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(); // 역할 추가 (예: ROLE_USER)
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        // UserDetails 객체 생성 시 권한을 포함시킴
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
