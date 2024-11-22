package edu.du.samplep.service;

import edu.du.samplep.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // 사용자가 정지된 상태인지 확인
        if (user.getSuspensionEndDate() != null && user.getSuspensionEndDate().isAfter(LocalDate.now())) {
            throw new UsernameNotFoundException("User is suspended until " + user.getSuspensionEndDate());
        }

        if (user.getRole().contains("SUSPENDED")) {
            throw new LockedException("Your account is suspended. Please contact support.");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));


        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
