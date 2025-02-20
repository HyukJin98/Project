package edu.du.samplep.repository;

import edu.du.samplep.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 조회수를 기준으로 오름차순 정렬된 모든 게시글 조회
    List<Post> findTop5ByIsNoticeFalseOrderByViewsDesc();

    Page<Post> findAll(Pageable pageable);

    List<Post> findByTitleContaining(String keyword);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Post> findByContentContainingIgnoreCase(String content, Pageable pageable);

    Page<Post> findByUser_UsernameContainingIgnoreCase(String username, Pageable pageable);

    // 제목에 "(공지)"가 포함된 게시글 가져오기, createdAt으로 정렬
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    // 제목에 "(공지)"와 특정 keyword가 포함된 게시글 가져오기, createdAt으로 정렬
    Page<Post> findByTitleContainingAndTitleContaining(String firstKeyword, String secondKeyword, Pageable pageable);

    Page<Post> findByIsNoticeFalse(Pageable pageable);
    List<Post> findByIsNoticeTrue();
}
