package com.metacoding.springv2.reply;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.metacoding.springv2._core.util.Resp;
import com.metacoding.springv2.user.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 인증된 사용자가 게시글에 댓글을 작성한다.
    @PostMapping("/api/boards/{boardId}/replies")
    public ResponseEntity<?> 댓글쓰기(@PathVariable("boardId") Integer boardId,
            @RequestBody @Valid ReplyRequest.SaveDTO reqDTO,
            Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        ReplyResponse.DetailDTO respDTO = replyService.댓글쓰기(boardId, sessionUser.getId(), reqDTO.getComment(), null);
        return Resp.ok(respDTO);
    }

    // 인증된 사용자가 본인 댓글만 삭제한다.
    @DeleteMapping("/api/replies/{id}")
    public ResponseEntity<?> 댓글삭제하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        replyService.댓글삭제하기(id, sessionUser.getId());
        return Resp.ok("댓글 삭제 완료");
    }

    @PostMapping("/api/boards/{boardId}/replies/{id}/like")
    public ResponseEntity<?> 댓글좋아요하기(@PathVariable("boardId") Integer boardId, @PathVariable("id") Integer id,
            Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        replyService.댓글좋아요하기(boardId, id, sessionUser.getId());
        return Resp.ok("댓글 좋아요 완료");
    }

    @PostMapping("/api/boards/{boardId}/replies/{id}/dislike")
    public ResponseEntity<?> 댓글싫어요하기(@PathVariable("boardId") Integer boardId, @PathVariable("id") Integer id,
            Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        replyService.댓글싫어요하기(boardId, id, sessionUser.getId());
        return Resp.ok("댓글 싫어요 완료");
    }
}
