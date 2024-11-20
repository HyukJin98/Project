package edu.du.samplep.service;

import edu.du.samplep.entity.Comment;
import edu.du.samplep.entity.Post;
import edu.du.samplep.entity.Reply;
import edu.du.samplep.entity.User;
import edu.du.samplep.repository.CommentRepository;
import edu.du.samplep.repository.ReplyRepository;
import edu.du.samplep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final UserRepository userRepository;


    public long getCountComments(Long postId) {
        return commentRepository.countByPost_Id(postId);
    }

    public long getCountReplies(Long postId) {
        return commentRepository.findByPost_Id(postId).stream()
                .mapToLong(comment -> comment.getReplies().size()) // 댓글에 대한 대댓글 수 합산
                .sum();
    }
    public long getTotalCommentCount(Long postId) {
        long commentCount = getCountComments(postId);  // 댓글 수
        long replyCount = getCountReplies(postId);     // 대댓글 수
        return commentCount + replyCount;              // 댓글 수와 대댓글 수 합산
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }


    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    public int deleteComment(Long commentId) {
        return commentRepository.deleteCommentById(commentId);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    // 댓글 수정
    @Transactional
    public Optional<Comment> updateComment(Long commentId, String newContent) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment existingComment = comment.get();
            existingComment.setContent(newContent);  // 새로운 내용으로 수정
            commentRepository.save(existingComment);  // 수정된 댓글 저장
            return Optional.of(existingComment);  // 수정된 댓글 반환
        } else {
            return Optional.empty();  // 댓글이 존재하지 않으면 빈 Optional 반환
        }
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    // 댓글에 답글 추가
    public void addReply(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Reply reply = new Reply();
        reply.setParentComment(comment);
        reply.setUser(user);
        reply.setContent(content);

        replyRepository.save(reply);
    }

    // 특정 댓글에 대한 답글 목록 조회
    public List<Reply> getRepliesForComment(Long commentId) {
        return replyRepository.findByParentCommentId(commentId);
    }



}