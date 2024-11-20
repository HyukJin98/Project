package edu.du.samplep.repository;

import edu.du.samplep.entity.Message;
import edu.du.samplep.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 사용자가 보낸 메시지 목록
    List<Message> findBySender(User sender);

    // 특정 사용자가 받은 메시지 목록
    List<Message> findByReceiver(User receiver);

    // 읽지 않은 메시지 목록
    List<Message> findByReceiverAndIsReadFalse(User receiver);

    Optional<Message> findByIdAndReceiver(Long id, User receiver);
    Optional<Message> findByIdAndSender(Long id, User sender);

    Page<Message> findByReceiver(User receiver, Pageable pageable);
    Page<Message> findBySender(User sender, Pageable pageable);
}