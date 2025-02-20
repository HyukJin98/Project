package edu.du.samplep.config;

import edu.du.samplep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 기본 오류 메시지 설정
        String errorMessage = "이메일이나 비번이 틀렸습니다.";

        // 예외가 LockedException인 경우(계정이 잠긴 상태)
        if (exception instanceof LockedException) {
            // 계정 비활성화된 경우 처리
            String username = request.getParameter("username");  // 로그인 시 입력된 이메일 가져오기
            edu.du.samplep.entity.User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));  // 이메일로 사용자 조회

            // 사용자 정지 상태 확인
            if (user.getSuspensionEndDate() != null) {  // 정지된 계정이라면
                LocalDateTime now = LocalDateTime.now();  // 현재 시간
                LocalDateTime suspensionEndDate = user.getSuspensionEndDate().atStartOfDay();  // 계정 정지 종료 시간 (하루의 시작 시간으로 설정)

                // 남은 시간 계산
                if (suspensionEndDate.isAfter(now)) {  // 정지 시간이 아직 끝나지 않았다면
                    // 남은 일, 시간, 분, 초 계산
                    long daysRemaining = ChronoUnit.DAYS.between(now, suspensionEndDate);  // 남은 일수
                    long hoursRemaining = ChronoUnit.HOURS.between(now, suspensionEndDate) % 24;  // 남은 시간
                    long minutesRemaining = ChronoUnit.MINUTES.between(now, suspensionEndDate) % 60;  // 남은 분
                    long secondsRemaining = ChronoUnit.SECONDS.between(now, suspensionEndDate) % 60;  // 남은 초

                    // 남은 시간 메시지 설정
                    errorMessage = String.format("계정이 정지되었습니다.\n" +
                                    "%d일 %d시간 %d분 %d초 \n" +
                                    "남았습니다.",
                            daysRemaining, hoursRemaining, minutesRemaining, secondsRemaining);

                } else {
                    // 계정이 영구정지인 경우
                    errorMessage = "계정이 영구정지되었습니다.\n" +
                            "관리자에게 문의하세요.";
                }
            }
        }

        // 예외 로그 출력
        System.out.println("Exception: " + exception.getClass().getName());

        // 오류 메시지를 URL 파라미터로 전달하며 메인 페이지로 리다이렉트
        response.sendRedirect("/?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}