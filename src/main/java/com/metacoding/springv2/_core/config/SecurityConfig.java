package com.metacoding.springv2._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.metacoding.springv2._core.filter.JwtAuthorizationFilter;
import com.metacoding.springv2._core.util.RespFilter;

@Configuration
public class SecurityConfig {

        @Bean
        public BCryptPasswordEncoder encode() {
                return new BCryptPasswordEncoder();
        }

        // 시큐리티 필터 등록
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.headers(headers -> headers
                                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

                // http.cors(c -> c.disable()); // 시큐리티가 들고있는 cors 동작중지

                http.exceptionHandling(ex -> ex
                                .authenticationEntryPoint( // 인증필터
                                                (request, response, authException) -> RespFilter.fail(response, 401,
                                                                "로그인 후 이용해주세요"))
                                .accessDeniedHandler(
                                                (request, response, accessDeniedException) -> RespFilter.fail(response,
                                                                403, "권한이 없습니다")));

                http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                // 인증/권한 주소 커스터마이징
                http.authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/**").authenticated()
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .anyRequest().permitAll()); // permitall = 허용

                // 폼 로그인의 비활성화 (POST : x-www.form-urlencoded : username, password)
                http.formLogin(f -> f.disable());

                // 기본(basic) 인증 활성화 시킴 (request 할때마다 username, password를 요구)
                http.httpBasic(b -> b.disable());

                http.csrf(c -> c.disable());

                // 인증 필터를 변경
                http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}