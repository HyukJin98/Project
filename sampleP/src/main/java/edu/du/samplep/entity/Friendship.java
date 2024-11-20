package edu.du.samplep.entity;

import edu.du.samplep.service.FriendshipService;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 친구 요청을 보낸 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // 친구 요청을 받은 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 친구 요청 상태 (Pending, Accepted, Rejected)
    @Enumerated(EnumType.STRING)
    private FriendshipService.FriendshipStatus status;

    // 친구 관계 여부 (수락되면 true)
    private Boolean isFriend = false;

    private LocalDateTime createdAt;

    public Friendship(User sender, User receiver, FriendshipService.FriendshipStatus status, Boolean isFriend) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.isFriend = isFriend;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 수락 후 친구로 등록되도록 처리
    public void acceptFriend() {
        this.status = FriendshipService.FriendshipStatus.ACCEPTED;
        this.isFriend = true;
    }
}
