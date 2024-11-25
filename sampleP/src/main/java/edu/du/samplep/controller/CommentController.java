package edu.du.samplep.controller;

import edu.du.samplep.entity.Comment;
import edu.du.samplep.entity.Post;
import edu.du.samplep.entity.Reply;
import edu.du.samplep.entity.User;
import edu.du.samplep.repository.ReplyRepository;
import edu.du.samplep.repository.UserRepository;
import edu.du.samplep.service.CommentService;
import edu.du.samplep.service.PostService;
import edu.du.samplep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final PostService postService;

    private final UserService userService;

    private final UserRepository userRepository;


    private final ReplyRepository replyRepository;


    @PostMapping("/comments/add")
    public String addComment(@RequestParam Long id, @RequestParam String content, RedirectAttributes redirectAttributes) {
        System.out.println("게시글 id :" + id);
        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);
            Post post = postService.getPostById(id).orElse(null);

            if (post != null) {
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setPost(post);
                comment.setUser(user);  // 로그인된 사용자 정보 설정

                commentService.saveComment(comment);  // 댓글 저장
                redirectAttributes.addFlashAttribute("successMessage", "댓글이 작성되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "게시글을 찾을 수 없습니다.");
            }

            return "redirect:/posts/"+id;  // 작성한 게시글의 상세 페이지로 리다이렉트
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "로그인이 필요합니다.");
            return "redirect:/";  // 로그인 페이지로 리다이렉트
        }
    }

    @PostMapping("/comments/{commentId}/delete")
    @ResponseBody  // JSON 응답을 반환하기 위해 @ResponseBody 추가
    public Map<String, Object> deleteComment(@PathVariable Long commentId,
                                             @RequestParam("_method") String method,
                                             @RequestParam("postId") Long postId) {
        Map<String, Object> response = new HashMap<>();

        // 댓글 조회
        Optional<Comment> comment = commentService.getCommentById(commentId);
        if (!comment.isPresent()) {
            response.put("success", false);
            response.put("message", "댓글을 찾을 수 없습니다.");
            return response;  // 댓글이 없으면 에러 메시지 반환
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String commentAuthor = comment.get().getUser().getUsername();

        // 현재 사용자와 댓글 작성자가 일치하는지 확인
        if (!commentAuthor.equals(currentUser)) {
            response.put("success", false);
            response.put("message", "작성자만 댓글을 삭제할 수 있습니다.");
            return response;  // 작성자가 다르면 에러 메시지 반환
        }

        // 댓글 삭제
        try {
            int rowsAffected = commentService.deleteComment(commentId);  // 삭제 후 영향을 받은 행 수 확인
            if (rowsAffected == 0) {
                response.put("success", false);
                response.put("message", "댓글을 삭제할 수 없습니다. 댓글이 이미 삭제되었을 수 있습니다.");
            } else {
                response.put("success", true);
                response.put("message", "댓글이 삭제되었습니다.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "댓글 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return response;  // 결과를 JSON 형태로 반환
    }

    @GetMapping("/comments/{commentId}/update")
    public String editCommentForm(@PathVariable Long commentId, Model model,@RequestParam("postId") Long postId) {
        Optional<Comment> comment = commentService.getCommentById(commentId);
        if (comment.isPresent()) {
            model.addAttribute("comment", comment);
            return "/redirect:/posts/"+postId; // 댓글 수정 뷰로 이동
        }
        return "redirect:/"; // 댓글을 찾을 수 없는 경우 홈으로 리디렉션
    }

    @PostMapping("/comments/{commentId}/update")
    @ResponseBody
    public Map<String, Object> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> request,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 댓글 수정 로직
            String content = request.get("content");
            Long postId = Long.valueOf(request.get("postId"));

            // 현재 로그인된 사용자와 댓글 작성자가 같은지 확인
            String loggedInUsername = principal.getName();
            Comment comment = commentService.findById(commentId);

            if (!comment.getUser().getUsername().equals(loggedInUsername)) {
                response.put("success", false);
                response.put("message", "작성자만 댓글을 수정할 수 있습니다.");
                return response;
            }

            // 댓글 수정
            commentService.updateComment(commentId, content);
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "댓글 수정 중 오류 발생");
        }
        return response;
    }


    @PostMapping("/comments/reply")
    public String addReply(@RequestParam Long postId, @RequestParam Long parentCommentId, @RequestParam String content, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        commentService.addReply(parentCommentId, user.getId(), content);
        return "redirect:/posts/" + postId; // 답글 작성 후 게시글로 리다이렉트
    }

    @GetMapping("/comments/replies/{commentId}")
    public String getReplies(@PathVariable Long commentId,@RequestParam Long postId, Model model) {
        List<Reply> replies = commentService.getRepliesForComment(commentId);
        model.addAttribute("replies", replies);
        return "redirect:/posts/" + postId; // 답글 목록을 보여주는 게시글 상세보기 페이지
    }


    @PostMapping("/comments/reply/{replyId}/delete")
    public ResponseEntity<Map<String, Object>> deleteReply(@PathVariable Long replyId, @RequestParam Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 댓글 삭제
            replyRepository.deleteById(replyId);

            // 성공 메시지
            response.put("success", true);
            response.put("message", "답글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            // 오류 메시지
            response.put("success", false);
            response.put("message", "답글 삭제 중 오류가 발생했습니다.");
        }

        // JSON 응답 반환
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments/reply/{replyId}")
    public String editReply(@PathVariable Long replyId,
                            @RequestParam String content,
                            @RequestParam Long postId) {
        try {
            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));
            reply.setContent(content);
            replyRepository.save(reply);

            // 리다이렉트 URL에 메시지를 쿼리 파라미터로 추가
            return "redirect:/posts/" + postId + "?message=" + URLEncoder.encode("답글이 수정되었습니다.", StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 실패한 경우도 쿼리 파라미터로 메시지 전달
            return "redirect:/posts/" + postId + "?message=" + URLEncoder.encode("답글 수정에 실패했습니다.", StandardCharsets.UTF_8);
        }
    }


    // 로그인 여부 확인
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getName().equals("anonymousUser"));
    }
}