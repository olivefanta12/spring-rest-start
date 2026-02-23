# 작업 변경사항 요약 (2026-02-23)

## 1. CORS 필터 적용

### `src/main/java/com/metacoding/springv2/_core/config/SecurityConfig.java`

- 기존에 비활성화(`disable()`) 되어 있던 CORS 정책 대신, 직접 구현한 `CorsFilter`를 시큐리티 필터 체인에 추가하여 CORS 요청을 처리하도록 변경했습니다.
- `JwtAuthorizationFilter` 앞에 `CorsFilter`를 추가하여 인증 이전에 CORS 정책이 적용되도록 설정했습니다.

```java
// 변경 전
http.cors(c -> c.disable()); // 시큐리티가 들고있는 cors 동작중지
// ...
// 인증 필터를 변경
http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);


// 변경 후
http.cors(c -> c.disable()); // 시큐리티가 들고있는 cors 동작중지
// ...
// 인증 필터를 변경
http.addFilterBefore(new CorsFilter(), UsernamePasswordAuthenticationFilter.class);
http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
```

## 2. 회원 정보 보기 기능 구현

### `src/main/java/com/metacoding/springv2/user/UserResponse.java` (신규 파일)

- API 응답으로 사용될 `UserResponse.DetailDTO`를 새로 생성했습니다.
- DTO는 사용자의 `id`, `username`, `email`, `roles` 정보를 포함하며, 보안상 민감한 정보(비밀번호 등)는 제외합니다.

```java
package com.metacoding.springv2.user;

import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter
    @Setter
    public static class DetailDTO {
        private Integer id;
        private String username;
        private String email;
        private String roles;

        public DetailDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.roles = user.getRoles();
        }
    }
}
```

### `src/main/java/com/metacoding/springv2/user/UserService.java`

- `회원정보보기(Integer id)` 메서드를 구현했습니다.
- `UserRepository`를 사용하여 ID로 사용자를 조회하고, 없을 경우 `Exception404` (Not Found) 예외를 발생시킵니다.
- 조회된 `User` 엔티티를 `UserResponse.DetailDTO`로 변환하여 반환합니다.

```java
package com.metacoding.springv2.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metacoding.springv2._core.handler.ex.Exception404;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse.DetailDTO 회원정보보기(Integer id) {
        User userPS = userRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        return new UserResponse.DetailDTO(userPS);
    }
}
```

### `src/main/java/com/metacoding/springv2/user/UserController.java`

- `GET /api/users/{id}` 요청을 처리하는 `유저정보보기` 엔드포인트를 추가했습니다.
- `UserService`를 호출하여 사용자 정보를 조회하고, `Resp.ok()`를 사용하여 표준화된 `ResponseEntity` 형태로 클라이언트에 응답합니다.

```java
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

    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> 유저정보보기(@PathVariable Integer id) {
        UserResponse.DetailDTO respDTO = userService.회원정보보기(id);
        return Resp.ok(respDTO);
    }
}
```
