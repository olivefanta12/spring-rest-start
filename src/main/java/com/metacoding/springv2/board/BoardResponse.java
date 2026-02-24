package com.metacoding.springv2.board;

import lombok.Data;

public class BoardResponse {

    @Data
    public static class ListDTO {
        private Integer id;
        private String title;
        private String content;
        private Integer likeCount;
        private Integer dislikeCount;
        private Integer score;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.likeCount = board.getLikeCount();
            this.dislikeCount = board.getDislikeCount();
            this.score = board.getLikeCount() - board.getDislikeCount();
        }
    }
}
