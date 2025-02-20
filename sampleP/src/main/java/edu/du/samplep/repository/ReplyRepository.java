package edu.du.samplep.repository;

import edu.du.samplep.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 댓글에 대한 모든 답글 조회
    List<Reply> findByParentCommentId(Long commentId);


    long countByPost_Id(Long postId);
}