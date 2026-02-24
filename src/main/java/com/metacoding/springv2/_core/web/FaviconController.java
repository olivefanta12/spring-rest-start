package com.metacoding.springv2._core.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    // 브라우저의 favicon.ico 자동 요청을 정상 처리해 불필요한 404 경고 로그를 줄인다.
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
