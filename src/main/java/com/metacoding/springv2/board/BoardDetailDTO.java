package com.metacoding.springv2.board;

import java.util.List;
import java.util.stream.Collectors;

import com.metacoding.springv2.reply.ReplyResponse;

import lombok.Data;

@Data
public class BoardDetailDTO {
    private Integer id;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer userId;
    private String username;
    private List<ReplyResponse.DetailDTO> replies;

    // 게시글 엔티티를 상세조회 전용 DTO로 변환한다.
    public BoardDetailDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.likeCount = board.getLikeCount();
        this.dislikeCount = board.getDislikeCount();
        this.userId = board.getUser().getId();
        this.username = board.getUser().getUsername();
        this.replies = board.getReplies().stream().map(reply -> new ReplyResponse.DetailDTO(reply)).collect(Collectors.toList());
    }
}
