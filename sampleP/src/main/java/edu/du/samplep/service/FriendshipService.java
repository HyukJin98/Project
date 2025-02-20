package edu.du.samplep.service;

import edu.du.samplep.entity.Friendship;
import edu.du.samplep.entity.User;
import edu.du.samplep.repository.FriendshipRepository;
import edu.du.samplep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public enum FriendshipStatus {
        PENDING, ACCEPTED, REJECTED
    }

    // 친구 요청 보내기
    public Friendship sendFriendRequest(User sender, User receiver) {
        Friendship existingRequest = friendshipRepository.findBySenderAndReceiver(sender, receiver);
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        // 이미 친구 관계인 경우 요청 불가
        if (isAlreadyFriend(sender, receiver)) {
            throw new IllegalArgumentException("이미 친구로 등록된 사용자입니다.");
        }

        // 이미 요청한 경우 요청 불가
        if (isRequestAlreadySent(sender, receiver)) {
            throw new IllegalArgumentException("이미 요청한 사용자입니다..");
        }

        if (existingRequest != null) {
            // 기존 요청이 있으면 삭제
            friendshipRepository.delete(existingRequest);
        }

        // 친구 요청 생성 및 저장
        Friendship friendship = new Friendship(sender, receiver, FriendshipStatus.PENDING, false);
        return friendshipRepository.save(friendship);
    }

    // 친구 요청 수락
    public Friendship acceptFriendRequest(Friendship friendship) {
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be accepted.");
        }
        friendship.acceptFriend();
        return friendshipRepository.save(friendship);
    }

    // 친구 요청 거절
    public void rejectFriendRequest(Friendship friendship) {
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be rejected.");
        }
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    // 수락된 친구 목록 조회
    public List<Friendship> getFriends(User user) {
        return friendshipRepository.findBySenderOrReceiverAndIsFriend(user, user, true);
    }

    // 대기 중인 친구 요청 조회
    public List<Friendship> getPendingRequests(User user) {
        return friendshipRepository.findByReceiverAndStatus(user, FriendshipStatus.PENDING);
    }

    // 친구 관계 확인
    private boolean isAlreadyFriend(User sender, User receiver) {
        return friendshipRepository.existsBySenderAndReceiverAndIsFriend(sender, receiver, true) ||
                friendshipRepository.existsBySenderAndReceiverAndIsFriend(receiver, sender, true);
    }

    // 이미 친구 요청을 보냈는지 확인
    private boolean isRequestAlreadySent(User sender, User receiver) {
        return friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING) ||
                friendshipRepository.existsBySenderAndReceiverAndStatus(receiver, sender, FriendshipStatus.PENDING);
    }

    // Friendship ID로 찾기
    public Friendship findById(Long id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found for id: " + id));
    }
}
