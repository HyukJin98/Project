package edu.du.samplep.service;

import edu.du.samplep.entity.Message;
import edu.du.samplep.entity.User;
import edu.du.samplep.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    public Page<Message> getReceivedMessages(User receiver, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        return messageRepository.findByReceiver(receiver, pageable);
    }

    public Page<Message> getSentMessages(User sender, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        return messageRepository.findBySender(sender, pageable);
    }

    // 메시지 전송
    public Message sendMessage(User sender, User receiver, String content) {
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .isRead(false)
                .build();
        return messageRepository.save(message);
    }

    // 메시지 삭제
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        messageRepository.delete(message);
    }

    // 고객센터로 메시지 전송
    public Message sendMessageToAdmin(User sender, String content) {
        // 관리자 계정을 찾아서 수신자로 지정
        User admin = userService.findByUsername("관리자");

        if (admin == null) {
            throw new IllegalArgumentException("관리자 계정을 찾을 수 없습니다.");
        }

        // 메시지 생성 및 저장
        Message message = Message.builder()
                .sender(sender)
                .receiver(admin)
                .content(content)
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

}
