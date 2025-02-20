package edu.du.samplep.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;  // 댓글과의 관계

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 답글 작성자

    private String author;

    @Column(nullable = false)
    private String content; // 답글 내용

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 답글 작성일

    // Getters and Setters
}