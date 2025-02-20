package edu.du.samplep.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Reply> replies; // replies 필드를 추가

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt;

    /*
 엔티티가 데이터베이스에 저장되기 전에 호출되는 메서드
 createdAt 필드에 현재 시간을 자동으로 설정
*/
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}