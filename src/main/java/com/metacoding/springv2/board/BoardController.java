package com.metacoding.springv2.board;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metacoding.springv2._core.util.Resp;
import com.metacoding.springv2.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/boards")
    public ResponseEntity<?> 목록보기(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<BoardResponse.ListDTO> respDTO = boardService.게시글목록보기(page);
        return Resp.ok(respDTO.getContent());
    }

    // 게시글 ID로 게시글 상세 정보를 조회한다. (ex. /api/boards/1)
    @GetMapping("/api/boards/{id}")
    public ResponseEntity<?> 상세보기(@PathVariable("id") Integer id) {
        // 서비스에서 게시글 상세 조회 비즈니스 로직을 실행한다.
        BoardDetailDTO respDTO = boardService.게시글상세보기(id);
        // 프로젝트 공통 응답 포맷으로 감싸서 반환한다.
        return Resp.ok(respDTO);
    }

    // 게시글 ID로 게시글을 삭제한다. (ex. /api/boards/1)
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<?> 삭제하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        // 서비스에서 게시글 삭제 비즈니스 로직을 실행한다.
        boardService.게시글삭제하기(id, sessionUser.getId());
        // 프로젝트 공통 응답 포맷으로 삭제 결과를 반환한다.
        return Resp.ok("게시글 삭제 완료");
    }

    @PostMapping("/api/boards/{id}/like")
    public ResponseEntity<?> 좋아요하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        boardService.게시글좋아요하기(id, sessionUser.getId());
        return Resp.ok("좋아요 완료");
    }

    @PostMapping("/api/boards/{id}/dislike")
    public ResponseEntity<?> 싫어요하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        boardService.게시글싫어요하기(id, sessionUser.getId());
        return Resp.ok("싫어요 완료");
    }
}
