package com.metacoding.springv2.reply;

import lombok.Data;

public class ReplyResponse {

    @Data
    public static class DetailDTO {
        private Integer id;
        private String comment;
        private String imageUrl;
        private Integer likeCount;
        private Integer dislikeCount;
        private Integer boardId;
        private Integer userId;
        private String username;

        public DetailDTO(Reply reply) {
            this.id = reply.getId();
            this.comment = reply.getComment();
            this.imageUrl = reply.getImageUrl();
            this.likeCount = reply.getLikeCount();
            this.dislikeCount = reply.getDislikeCount();
            this.boardId = reply.getBoard().getId();
            this.userId = reply.getUser().getId();
            this.username = reply.getUser().getUsername();
        }
    }
}
