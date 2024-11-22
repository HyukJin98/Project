package edu.du.samplep.controller;

import edu.du.samplep.entity.User;
import edu.du.samplep.repository.UserRepository;
import edu.du.samplep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String login() {
        return "/login/login";
    }


    @GetMapping("/login-success")
    public String loginSuccess() {
        return "login-success";  // login-success.html 템플릿을 반환
    }



    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout-success";  // login-success.html 템플릿을 반환
    }





    @GetMapping("/basic") // 인덱스 페이지를 위한 메서드
    public String index() {
        return "basic"; // basic.html 뷰 반환
    }



}
