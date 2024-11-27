package edu.du.samplep.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "sender")
    private List<Friendship> friendsRequested; // 내가 요청한 친구 관계 목록

    @OneToMany(mappedBy = "receiver")
    private List<Friendship> friendsReceived;  // 내가 받은 친구 요청 목록

    @Column(name = "suspension_end_date")
    private LocalDate suspensionEndDate;



    // 정지 상태 여부 확인 메서드
    public boolean isSuspended() {
        return this.suspensionEndDate != null && LocalDate.now().isBefore(this.suspensionEndDate);
    }

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(Long userId) {
        this.id = userId;
    }

    public String getName() {
        return username;
    }


}