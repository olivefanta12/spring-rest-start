package com.metacoding.springv2._core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 파일을 /upload/** 경로로 브라우저에서 접근할 수 있도록 정적 리소스로 연결한다.
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:./upload/");
    }
}
