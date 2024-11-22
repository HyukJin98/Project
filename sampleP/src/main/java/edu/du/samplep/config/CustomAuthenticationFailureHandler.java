package edu.du.samplep.config;

import edu.du.samplep.entity.User;
import edu.du.samplep.repository.UserRepository;
import edu.du.samplep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
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
import java.util.Optional;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "이메일이나 비번이 틀렸습니다.";


        if (exception instanceof UsernameNotFoundException) {
            // 계정 비활성화된 경우 메시지 설정
            errorMessage = "정지를 당하였습니다!! 관리자에게 문의하세요";
        }

        System.out.println("Exception: " + exception.getClass().getName());

        // 실패 메시지를 파라미터로 전달하며 메인 페이지로 리다이렉트
        response.sendRedirect("/?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}