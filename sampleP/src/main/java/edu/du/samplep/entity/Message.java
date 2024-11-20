package edu.du.samplep.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 발신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // 수신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 메시지 내용
    @Column(nullable = false, length = 1000)
    private String content;

    // 메시지 전송 시간
    @Column(nullable = false)
    private LocalDateTime sentAt;

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false") // 기본값을 false로 설정
    private Boolean isRead;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }

    // 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}
