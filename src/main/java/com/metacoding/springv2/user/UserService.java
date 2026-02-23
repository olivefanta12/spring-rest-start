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

    // 비지니스 로직: 여기서는 간단하지만, 복잡한 계산, 외부 API 호출, 여러 Repository를 사용하는 등의 로직이 포함될 수 있다.
    public UserResponse.DetailDTO 회원정보보기(Integer id) {
        // 1. 유저 아이디로 사용자를 찾는다.
        User userPS = userRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        
        // 2. 찾은 사용자 정보를 DTO로 변환하여 반환한다.
        return new UserResponse.DetailDTO(userPS);
    }
}