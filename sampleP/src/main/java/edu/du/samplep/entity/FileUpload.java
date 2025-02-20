package edu.du.samplep.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 ID

    private String fileName; // 원본 파일명

    private String filePath; // 저장된 파일 경로

    private Long fileSize; // 파일 크기

    private LocalDateTime uploadedAt = LocalDateTime.now(); // 파일 업로드 시간

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post; // 게시글과의 연관 관계

    // 파일을 업로드할 때 사용되는 생성자
    public FileUpload(String fileName, String filePath, Long fileSize, Post post) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.post = post;
    }

    public FileUpload(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}