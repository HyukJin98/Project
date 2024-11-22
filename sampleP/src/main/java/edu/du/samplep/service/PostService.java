package edu.du.samplep.service;

import edu.du.samplep.entity.FileUpload;
import edu.du.samplep.entity.Post;
import edu.du.samplep.repository.FileUploadRepository;
import edu.du.samplep.repository.PostRepository;
import edu.du.samplep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileUploadRepository fileUploadRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
    }

    public Page<Post> getAllPostsWithPagingAndSorting(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("createdAt")));

        // 일반 게시글만 페이징 적용
        Page<Post> posts = postRepository.findByIsNoticeFalse(pageable);
        return posts;
    }

    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            p.increaseViews();  // 조회수 증가
            postRepository.save(p);
        });
        return post;

    }

    public Post savePost(Post post) {
        if (post.getTitle().contains("(공지)")) {
            post.setNotice(true);
        } else {
            post.setNotice(false);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 파일 삭제
        List<FileUpload> files = fileUploadRepository.findByPost(post);
        for (FileUpload file : files) {
            fileUploadRepository.delete(file); // 파일 삭제
        }

        // 게시글 삭제
        postRepository.delete(post);
    }

    public Post updatePost(Long id, Post postDetails) {
        // 먼저 id로 기존 게시글을 찾아옵니다.
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        // 찾은 게시글의 속성들을 업데이트합니다.
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        // 다른 필드들에 대해서도 동일하게 업데이트할 수 있습니다.

        // 업데이트된 게시글을 저장하여 반환합니다.
        return postRepository.save(post);
    }

    public List<Post> getTopViewedPosts() {
        return postRepository.findTop5ByIsNoticeFalseOrderByViewsDesc();
    }

    public List<Post> getAllNotices() {
        // '공지'라는 단어가 포함된 게시글을 모두 가져옴
        return postRepository.findByIsNoticeTrue();
    }





    public Page<Post> searchPosts(String keyword, String type, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        switch (type) {
            case "title":
                return postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
            case "content":
                return postRepository.findByContentContainingIgnoreCase(keyword, pageable);
            case "user":
                return postRepository.findByUser_UsernameContainingIgnoreCase(keyword, pageable);
            default:
                return postRepository.findAll(pageable);
        }
    }

}
