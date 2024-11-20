package edu.du.samplep.repository;

import edu.du.samplep.entity.Friendship;
import edu.du.samplep.entity.User;
import edu.du.samplep.service.FriendshipService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByReceiverAndStatus(User receiver, FriendshipService.FriendshipStatus status);

    List<Friendship> findBySenderOrReceiverAndIsFriend(User sender, User receiver, Boolean isFriend);

    Optional<Friendship> findById(Long id);

    boolean existsBySenderAndReceiverAndIsFriend(User sender, User receiver, boolean b);

    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendshipService.FriendshipStatus friendshipStatus);

    // 친구 목록 조회 (사용자가 보낸 친구 요청 또는 받은 친구 요청)
    List<Friendship> findBySenderOrReceiverAndIsFriend(User sender, User receiver, boolean isFriend);

    @Query("SELECT f FROM Friendship f WHERE f.user = :user")
    List<Friendship> findByUser(@Param("user") User user);

    Friendship findBySenderAndReceiver(User sender, User receiver);

}
