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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "이메일이나 비번이 틀렸습니다.";


        if (exception instanceof LockedException) {
            // 계정 비활성화된 경우 메시지 설정
            String username = request.getParameter("username");
            edu.du.samplep.entity.User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            // 사용자 정지 상태 확인
            if (user.getSuspensionEndDate() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime suspensionEndDate = user.getSuspensionEndDate().atStartOfDay();

                // 남은 시간 계산
                if (suspensionEndDate.isAfter(now)) {
                    long daysRemaining = ChronoUnit.DAYS.between(now, suspensionEndDate);
                    long hoursRemaining = ChronoUnit.HOURS.between(now, suspensionEndDate) % 24;
                    long minutesRemaining = ChronoUnit.MINUTES.between(now, suspensionEndDate) % 60;
                    long secondsRemaining = ChronoUnit.SECONDS.between(now, suspensionEndDate) % 60;

                    errorMessage = String.format("계정이 정지되었습니다. 정지 해제까지 %d일 %d시간 %d분 %d초 남았습니다.",
                            daysRemaining, hoursRemaining, minutesRemaining, secondsRemaining);
                } else {
                    errorMessage = "계정이 정지되었습니다. 정지 기간이 만료되었습니다.";
                }
            }
        }

        System.out.println("Exception: " + exception.getClass().getName());

        // 실패 메시지를 파라미터로 전달하며 메인 페이지로 리다이렉트
        response.sendRedirect("/?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}