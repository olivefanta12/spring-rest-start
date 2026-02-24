package com.metacoding.springv2._core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import com.metacoding.springv2._core.handler.ex.*;
import com.metacoding.springv2._core.util.Resp;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> exApi400(Exception400 e, HttpServletRequest request) {
        log.warn("[WARN] 사용자 입력 유효성 실패: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse(e.getMessage());
        }
        return Resp.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> exApi401(Exception401 e, HttpServletRequest request) {
        log.warn("[WARN] 사용자 인증 실패: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse(e.getMessage());
        }
        return Resp.fail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> exApi403(Exception403 e, HttpServletRequest request) {
        log.warn("[WARN] 사용자 권한 실패: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse(e.getMessage());
        }
        return Resp.fail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> exApi404(Exception404 e, HttpServletRequest request) {
        log.warn("[WARN] 사용자 자원 찾기 실패: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse(e.getMessage());
        }
        return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> exApi500(Exception500 e, HttpServletRequest request) {
        log.warn("[ERROR] 예상 가능한 서버 오류: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse(e.getMessage());
        }
        return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> exUploadSize(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("[WARN] 업로드 용량 초과: " + e.getMessage());
        if (isWebRequest(request)) {
            return webErrorResponse("파일 업로드 용량을 초과했습니다. 20MB 이하로 업로드해주세요");
        }
        return Resp.fail(HttpStatus.BAD_REQUEST, "파일 업로드 용량을 초과했습니다. 20MB 이하로 업로드해주세요");
    }

    // 해당 오류가 발생하면 직접 처리 혹은 Exception500으로 관리하는 것이 좋다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exUnKnown(Exception e, HttpServletRequest request) {
        log.error("[SYSTEM] 예상 불가능한 서버 오류: " + e.getMessage());
        e.printStackTrace();
        if (isWebRequest(request)) {
            return webErrorResponse("관리자에게 문의하세요");
        }
        return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의하세요");
    }

    private boolean isWebRequest(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    private ResponseEntity<String> webErrorResponse(String message) {
        String escaped = message.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", " ");
        String body = "<script>alert('" + escaped + "'); location.href='/boards';</script>";
        MediaType htmlUtf8 = new MediaType("text", "html", StandardCharsets.UTF_8);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(htmlUtf8)
                .body(body);
    }
}
