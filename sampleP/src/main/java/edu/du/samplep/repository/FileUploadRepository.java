package edu.du.samplep.repository;

import edu.du.samplep.entity.FileUpload;
import edu.du.samplep.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    // 특정 게시글에 첨부된 파일 목록을 조회
    List<FileUpload> findByPostId(Long postId);

    // 파일 이름을 기준으로 첨부 파일 조회
    FileUpload findByFileName(String fileName);

    List<FileUpload> findByPost(Post post);
}
