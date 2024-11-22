package edu.du.samplep.repository;

import edu.du.samplep.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String query);

    // ID가 다른 모든 사용자 조회 (현재 사용자 제외)
    List<User> findAllByIdNot(Long id);

    List<User> findByUsernameContainingAndUsernameNot(String searchTerm, String username);

    Optional<User> findByEmail(String email);

    User findByRole(String role);
}
