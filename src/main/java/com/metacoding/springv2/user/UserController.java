package com.metacoding.springv2.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.metacoding.springv2._core.util.Resp;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // REST API 규칙상 GET 요청은 리소스 조회를 나타내며, URL에 자원의 식별자를 포함해야 한다. (ex. /api/users/{id})
    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> 유저정보보기(@PathVariable("id") Integer id) {
        // 1. UserService에서 비지니스 로직을 호출한다.
        UserResponse.DetailDTO respDTO = userService.회원정보보기(id);
        
        // 2. 통일성 있는 응답을 위해 Resp 유틸리티를 사용하여 ResponseEntity를 생성한다.
        return Resp.ok(respDTO);
    }
}
