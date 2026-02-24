package com.metacoding.springv2.imageboard;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

public class ImageBoardResponse {

    @Data
    public static class ListDTO {
        private Integer id;
        private String title;
        private String content;
        private String imageUrl;
        private Integer likeCount;
        private Integer dislikeCount;

        public ListDTO(ImageBoard imageBoard) {
            this.id = imageBoard.getId();
            this.title = imageBoard.getTitle();
            this.content = imageBoard.getContent();
            this.imageUrl = imageBoard.getImageUrl();
            this.likeCount = imageBoard.getLikeCount();
            this.dislikeCount = imageBoard.getDislikeCount();
        }
    }

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String content;
        private String imageUrl;
        private Integer likeCount;
        private Integer dislikeCount;
        private List<ReplyDTO> replies;

        public DetailDTO(ImageBoard imageBoard) {
            this.id = imageBoard.getId();
            this.title = imageBoard.getTitle();
            this.content = imageBoard.getContent();
            this.imageUrl = imageBoard.getImageUrl();
            this.likeCount = imageBoard.getLikeCount();
            this.dislikeCount = imageBoard.getDislikeCount();
            this.replies = imageBoard.getReplies().stream().map(reply -> new ReplyDTO(reply)).collect(Collectors.toList());
        }
    }

    @Data
    public static class ReplyDTO {
        private Integer id;
        private Integer imageBoardId;
        private String comment;
        private Integer likeCount;
        private Integer dislikeCount;
        private Integer userId;
        private String username;

        public ReplyDTO(ImageBoardReply reply) {
            this.id = reply.getId();
            this.imageBoardId = reply.getImageBoard().getId();
            this.comment = reply.getComment();
            this.likeCount = reply.getLikeCount();
            this.dislikeCount = reply.getDislikeCount();
            this.userId = reply.getUser().getId();
            this.username = reply.getUser().getUsername();
        }
    }
}
