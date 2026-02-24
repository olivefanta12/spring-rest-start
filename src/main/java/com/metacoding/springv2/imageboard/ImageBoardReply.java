package com.metacoding.springv2.imageboard;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.metacoding.springv2.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "image_board_reply_tb")
public class ImageBoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String comment;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ImageBoard imageBoard;

    @CreationTimestamp
    private Timestamp createdAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    @Builder
    public ImageBoardReply(Integer id, String comment, int likeCount, int dislikeCount, User user, ImageBoard imageBoard,
            Timestamp createdAt) {
        this.id = id;
        this.comment = comment;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.user = user;
        this.imageBoard = imageBoard;
        this.createdAt = createdAt;
    }
}
