package edu.du.samplep.controller;

import edu.du.samplep.entity.*;
import edu.du.samplep.repository.CommentRepository;
import edu.du.samplep.service.CommentService;
import edu.du.samplep.service.FileUploadService;
import edu.du.samplep.service.PostService;
import edu.du.samplep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CommentRepository commentRepository;

    // 모든 게시글 목록 조회
    @GetMapping("/")
    public String getAllPostsWithPaging(Model model,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "type", required = false) String type) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        System.out.println("현재 로그인된 이메일: " + currentUserEmail);

        // 기본 pageSize 설정
        int pageSize = 6;
        Page<Post> postPage;

        List<Post> posts2 = postService.getTopViewedPosts();
        // 검색 조건이 있을 경우 검색된 게시글을 가져오고, 없으면 전체 게시글을 가져옴
        if (keyword != null && type != null && !keyword.isEmpty()) {
            postPage = postService.searchPosts(keyword, type, page, pageSize);
        } else {
            postPage = postService.getAllPostsWithPagingAndSorting(page, pageSize);
        }

        // 게시글 리스트 가져오기
        List<Post> posts = postPage.getContent();

        // 공지사항은 페이징 없이 가져오기
        List<Post> notices = postService.getAllNotices(); // 공지사항 페이징 제거, 전체 공지사항 목록

        // 공지사항이 있으면 페이지 사이즈를 1 늘려줌
        int adjustedPageSize = notices.isEmpty() ? pageSize : pageSize + 1;

        // (isNotice == false)인 게시글에만 페이지네이션 적용
        List<Post> regularPosts = posts.stream()
                .filter(post -> !post.isNotice())  // isNotice가 false인 게시글만 필터링
                .collect(Collectors.toList()); // 일반 게시글 리스트

        // 모델에 전체 게시글 정보와 구분된 게시글 추가
        model.addAttribute("postsPage", postPage);
        model.addAttribute("posts", regularPosts);  // 일반 게시글만 페이지네이션 적용
        model.addAttribute("notices", notices); // 공지사항 리스트
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("commentService", commentService);
        model.addAttribute("topPosts", posts2);
        model.addAttribute("adjustedPageSize", adjustedPageSize); // 페이지 사이즈 조정값

        return "basic";
    }







    // 게시글 작성 폼 (로그인한 사용자만 접근 가능)
    @GetMapping("/posts/new")
    public String createPostForm(Model model, RedirectAttributes redirectAttributes) {
        // 로그인 여부 체크
        if (isAuthenticated()) {
            model.addAttribute("post", new Post());
            return "basic";  // 게시글 작성 폼을 포함한 메인 페이지 (모달로 작성 가능)
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "로그인이 필요합니다.");
            return "redirect:/";  // 로그인 페이지로 리다이렉트
        }
    }

    // 게시글 저장
    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute Post post,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) throws IOException {
        if (isAuthenticated()) {
            // 현재 로그인한 사용자 정보 가져오기
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);

            // 제목에 (공지)가 포함되면 일반 사용자는 작성 불가
            if (post.getTitle().contains("(공지)") && !user.getRole().equals("ROLE_MANAGER")) {
                redirectAttributes.addFlashAttribute("warningMessage", "일반 사용자는 '(공지)'로 시작하는 제목을 사용할 수 없습니다.");
                return "redirect:/"; // 게시글 작성 폼으로 다시 리다이렉트
            }

            // 게시글의 작성자 설정
            post.setUser(user);

            // 게시글 저장
            Post savedPost = postService.savePost(post);

            // 파일 업로드 처리
            if (!file.isEmpty()) {
                fileUploadService.uploadFile(file, savedPost.getId()); // 파일 업로드
            }

            // 게시글 작성 완료 후 게시글 페이지로 리디렉션
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "로그인이 필요합니다.");
            return "redirect:/";  // 로그인되지 않은 경우 로그인 페이지로 이동
        }
    }
    @PostMapping("/posts/notice")
    @PreAuthorize("hasRole('MANAGER')")
    public String createNotice(@ModelAttribute Post post,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);

            // 공지사항의 제목과 내용을 설정
            post.setUser(user);
            post.setNotice(true);
            post.setTitle("(공지)" + post.getTitle());  // 공지사항 제목 앞에 '공지:' 추가

            // 게시글 저장
            Post savedPost = postService.savePost(post);

            // 파일 업로드 처리
            if (!file.isEmpty()) {
                fileUploadService.uploadFile(file, savedPost.getId());
            }

            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 작성되었습니다.");
            return "redirect:/";  // 공지사항 작성 후 다시 공지사항 페이지로 리다이렉트
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "로그인이 필요합니다.");
            return "redirect:/";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
    }


    // 게시글 상세보기
    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Post> post = postService.getPostById(id);

        if (post.isPresent()) {
            // 줄바꿈을 <br>로 변환 (게시글 내용)
            String contentWithBr = post.get().getContent().replace("\n", "<br/>");
            model.addAttribute("post", post.get());
            model.addAttribute("postContent", contentWithBr); // 변환된 content 전달

            // 댓글 및 답글 내용 줄바꿈 처리
            List<Comment> comments = commentService.getCommentsByPostId(id);
            for (Comment comment : comments) {
                String commentContentWithBr = comment.getContent().replace("\n", "<br/>");
                comment.setContent(commentContentWithBr); // 댓글 내용 변환

                // 답글 내용 변환
                List<Reply> replies = comment.getReplies();
                for (Reply reply : replies) {
                    String replyContentWithBr = reply.getContent().replace("\n", "<br/>");
                    reply.setContent(replyContentWithBr); // 답글 내용 변환
                }
            }
            model.addAttribute("comments", comments); // 변환된 댓글들 및 답글들 전달

            model.addAttribute("postId", id);
            return "posts/detail";
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "게시글을 찾을 수 없습니다.");
            return "redirect:/"; // 게시글을 찾을 수 없을 때 리다이렉트
        }
    }



    // 게시글 삭제 (작성자만 삭제 가능)
    @PostMapping("/posts/{id}")
    @ResponseBody
    public Map<String, Object> deletePost(@PathVariable Long id, @RequestParam("_method") String method) {
        Map<String, Object> response = new HashMap<>();

        if ("delete".equals(method)) {
            Optional<Post> post = postService.getPostById(id);
            if (post.isPresent()) {
                // 현재 로그인한 사용자 이름 가져오기
                String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

                // 게시글의 작성자 확인
                String postAuthor = post.get().getUser().getUsername();

                // 현재 사용자와 작성자 비교
                if (postAuthor.equals(currentUser)) {
                    postService.deletePost(id);
                    response.put("success", true);
                    response.put("message", "성공적으로 삭제되었습니다.");
                } else {
                    response.put("success", false);
                    response.put("message", "작성자만 삭제할 수 있습니다.");
                }
            } else {
                response.put("success", false);
                response.put("message", "게시글을 찾을 수 없습니다.");
            }
        } else {
            response.put("success", false);
            response.put("message", "잘못된 요청입니다.");
        }

        return response;
    }




    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            // 게시글 작성자의 username 가져오기
            String postAuthor = post.get().getUser().getUsername();

            // 게시글 작성자만 수정 가능
            if (postAuthor.equals(currentUser)) {
                model.addAttribute("post", post.get());
                return "posts/edit";  // 수정 폼을 제공하는 뷰로 리턴
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "작성자만 수정할 수 있습니다.");
                return "redirect:/";  // 작성자가 아니면 리다이렉트
            }
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "게시글을 찾을 수 없습니다.");
            return "redirect:/";  // 게시글을 찾을 수 없으면 리다이렉트
        }
    }

    // 게시글 업데이트 (폼 제출 후)
    // 게시글 업데이트 (폼 제출 후)
    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, RedirectAttributes redirectAttributes) {
        Optional<Post> existingPost = postService.getPostById(id);
        if (existingPost.isPresent()) {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            // 게시글 작성자의 username 가져오기
            String postAuthor = existingPost.get().getUser().getUsername();

            // 게시글 작성자만 수정 가능
            if (postAuthor.equals(currentUser)) {
                // 기존 게시글의 user 설정 유지
                post.setUser(existingPost.get().getUser());

                postService.updatePost(id, post);  // 기존 게시글을 업데이트
                redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 업데이트되었습니다.");
                return "redirect:/posts/" + id;  // 업데이트 후 게시글 상세 페이지로 리다이렉트
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "작성자만 수정할 수 있습니다.");
                return "redirect:/";  // 작성자가 아니면 리다이렉트
            }
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "게시글을 찾을 수 없습니다.");
            return "redirect:/";  // 게시글을 찾을 수 없으면 리다이렉트
        }
    }


    // 로그인 여부 확인
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getName().equals("anonymousUser"));
    }

}
