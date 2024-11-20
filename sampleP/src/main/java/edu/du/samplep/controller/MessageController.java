package edu.du.samplep.controller;

import edu.du.samplep.entity.Message;
import edu.du.samplep.entity.User;
import edu.du.samplep.service.MessageService;
import edu.du.samplep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    // 메시지 전송
    @PostMapping("/send")
    public String sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long receiverId,
            @RequestParam String content
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인된 사용자가 없습니다.");
        }

        // 로그인한 사용자를 User 엔티티로 변환
        User sender = userService.findByUsername(userDetails.getUsername());
        User receiver = userService.findById(receiverId);

        if (receiver == null) {
            throw new IllegalArgumentException("수신자를 찾을 수 없습니다.");
        }

        messageService.sendMessage(sender, receiver, content);
        return "redirect:/messages/box"; // 쪽지함으로 리다이렉트
    }

    // 받은 쪽지와 보낸 쪽지를 모두 조회
    @GetMapping("/box")
    public String messageBox(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "received") String tab,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
       if(isAuthenticated()) {
           if (userDetails == null) {
               throw new IllegalArgumentException("로그인된 사용자가 없습니다.");
           }

           User loggedInUser = userService.findByUsername(userDetails.getUsername());

           if ("received".equals(tab)) {
               Page<Message> receivedMessages = messageService.getReceivedMessages(loggedInUser, page - 1, size);
               model.addAttribute("messages", receivedMessages);
           } else if ("sent".equals(tab)) {
               Page<Message> sentMessages = messageService.getSentMessages(loggedInUser, page - 1, size);
               model.addAttribute("messages", sentMessages);
           }

           model.addAttribute("currentTab", tab);
           return "messages/box";
       } else{
           redirectAttributes.addFlashAttribute("warningMessage","로그인이 필요합니다");
           return "redirect:/";
       }
    }
    // 새 쪽지 작성 폼
    @GetMapping("/new")
    public String showMessageForm(@RequestParam Long receiverId, Model model) {
        User receiver = userService.findById(receiverId); // 수신자 정보 가져오기
        model.addAttribute("receiver", receiver);
        model.addAttribute("message", new Message());
        return "messages/write";
    }

    @PostMapping("/inbox/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteReceivedMessage(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            messageService.deleteMessage(id); // 사용자 관계없이 메시지 삭제
            response.put("success", true);
            response.put("message", "쪽지가 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "삭제에 실패했습니다.");
        }
        return response;
    }

    // 보낸 쪽지 삭제
    @PostMapping("/outbox/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteSentMessage(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            messageService.deleteMessage(id); // 사용자 관계없이 메시지 삭제
            response.put("success", true);
            response.put("message", "쪽지가 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "삭제에 실패했습니다.");
        }
        return response;
    }

    // 로그인 여부 확인
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getName().equals("anonymousUser"));
    }
}