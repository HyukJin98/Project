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
}
