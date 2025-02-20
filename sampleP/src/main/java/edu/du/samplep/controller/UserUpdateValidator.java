package edu.du.samplep.controller;

import edu.du.samplep.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // User 클래스에 대해 유효성 검사를 지원하는지 확인
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // 이메일 형식 검사
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.rejectValue("email", "user.email.invalid", "올바른 이메일을 입력해주세요.");
        }

        // 비밀번호 길이 검사 (예: 최소 6자 이상)
        if (user.getPassword() != null && user.getPassword().length() < 6) {
            errors.rejectValue("password", "user.password.length", "비밀번호는 6자 이상이어야 합니다.");
        }

        // 비밀번호 확인 필드가 있는 경우 (예: 비밀번호와 확인 비밀번호 일치 여부 검사)
        // if (!user.getPassword().equals(user.getConfirmPassword())) {
        //     errors.rejectValue("confirmPassword", "user.password.mismatch", "비밀번호가 일치하지 않습니다.");
        // }
    }
}
