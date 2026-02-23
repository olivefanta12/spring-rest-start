package com.metacoding.springv2.user;

import lombok.Data;

public class UserResponse {

    // 응답 DTO는 서비스(비지니스) 로직을 전혀 가지지 않고, 데이터 전송만을 목적으로 사용된다.
    @Data
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
