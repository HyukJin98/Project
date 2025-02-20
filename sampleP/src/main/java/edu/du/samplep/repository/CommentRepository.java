package edu.du.samplep.repository;

import edu.du.samplep.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPost_Id(Long postId);

    List<Comment> findByPostId(Long postId);

    // 특정 게시글의 부모 댓글 조

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
    int deleteCommentById(@Param("commentId") Long commentId);


     List<Comment> findByPost_Id(Long postId);
}
