package edu.du.samplep.controller;

import edu.du.samplep.entity.Friendship;
import edu.du.samplep.entity.User;
import edu.du.samplep.repository.FriendshipRepository;
import edu.du.samplep.service.FriendshipService;
import edu.du.samplep.service.UserDetailService;
import edu.du.samplep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;

    // 친구 목록 조회
    @GetMapping("/friendship/list")
    public String getFriendsList(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model,RedirectAttributes redirectAttributes) {
        if(isAuthenticated()) {
            User user = userService.findByUsername(userDetails.getUsername());

            // 친구 목록 조회
            List<Friendship> friends = friendshipService.getFriends(user);

            // 대기 중인 친구 요청 조회
            List<Friendship> pendingRequests = friendshipService.getPendingRequests(user);

            model.addAttribute("friends", friends);
            model.addAttribute("pendingRequests", pendingRequests);

            return "friendship/list";
        } else{
            redirectAttributes.addFlashAttribute("warningMessage","로그인이 필요합니다");
            return "redirect:/";
        }
    }

    // 친구 검색 및 친구 요청 보내기
    @GetMapping("/friendship/search")
    public String searchFriends(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                @RequestParam(required = false) String searchTerm, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<User> users = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            // 이름에 검색어가 포함된 사용자만 찾기
            users = userService.findUsersByNameContaining(searchTerm, user);
        }

        model.addAttribute("users", users);
        model.addAttribute("searchTerm", searchTerm); // 검색어를 뷰에 전달

        return "friendship/search";
    }

    // 친구 요청 보내기
    @PostMapping("/friendship/send/{receiverId}")
    public String sendFriendRequest(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                    @PathVariable Long receiverId, RedirectAttributes redirectAttributes) {
        User sender = userService.findByUsername(userDetails.getUsername());
        User receiver = userService.findById(receiverId);

        // 친구 요청 보내기
        try {
            Friendship friendship = friendshipService.sendFriendRequest(sender, receiver);
        } catch (IllegalArgumentException e) {
            // 친구 요청 불가능한 경우 에러 메시지를 리다이렉트 속성에 추가
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/friendship/search";  // 친구 검색 페이지로 리다이렉트
        }

        return "redirect:/friendship/list";  // 친구 목록 페이지로 리다이렉트
    }

    // 친구 요청 수락
    @PostMapping("/friendship/accept/{friendshipId}")
    public String acceptFriendRequest(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                      @PathVariable Long friendshipId) {
        Friendship friendship = friendshipService.findById(friendshipId);

        if (friendship == null) {
            return "redirect:/friendship/list?error=Friendship not found";
        }

        // 현재 로그인한 사용자가 친구 요청을 받은 사람인지 확인
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (!friendship.getReceiver().equals(currentUser)) {
            return "redirect:/friendship/list?error=You are not the receiver of this request";
        }

        friendshipService.acceptFriendRequest(friendship);

        return "redirect:/friendship/list";  // 친구 목록 페이지로 리다이렉트
    }

    // 친구 요청 거절
    @PostMapping("/friendship/reject/{friendshipId}")
    public String rejectFriendRequest(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                                      @PathVariable Long friendshipId) {
        Friendship friendship = friendshipService.findById(friendshipId);

        if (friendship == null) {
            return "redirect:/friendship/list?error=Friendship not found";
        }

        // 현재 로그인한 사용자가 친구 요청을 받은 사람인지 확인
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (!friendship.getReceiver().equals(currentUser)) {
            return "redirect:/friendship/list?error=You are not the receiver of this request";
        }

        friendshipService.rejectFriendRequest(friendship);

        return "redirect:/friendship/list";  // 친구 목록 페이지로 리다이렉트
    }

    @PostMapping("/friendship/delete/{friendshipId}")
    public String deleteFriendRequest(@PathVariable Long friendshipId) {
        // 삭제할 친구 요청을 찾아서 삭제
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid friendship Id: " + friendshipId));

        friendshipRepository.delete(friendship);

        // 삭제 후 친구 목록 페이지로 리다이렉트
        return "redirect:/friendship/list"; // 친구 목록 페이지로 리다이렉트
    }

    // 로그인 여부 확인
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getName().equals("anonymousUser"));
    }

}
