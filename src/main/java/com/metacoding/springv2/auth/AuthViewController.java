package com.metacoding.springv2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    // 로그인 화면을 렌더링한다.
    @GetMapping("/login")
    public String 로그인페이지() {
        return "auth/login";
    }

    // 회원가입 화면을 렌더링한다.
    @GetMapping("/join")
    public String 회원가입페이지() {
        return "auth/join";
    }
}
