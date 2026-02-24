package com.metacoding.springv2.reply;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import com.metacoding.springv2.board.Board;
import com.metacoding.springv2.user.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "reply_tb")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100, nullable = false)
    private String comment;
    @Column(length = 300)
    private String imageUrl;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @CreationTimestamp
    private Timestamp createdAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    @Builder
    public Reply(Integer id, String comment, String imageUrl, int likeCount, int dislikeCount, User user, Board board,
            Timestamp createdAt) {
        this.id = id;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.user = user;
        this.board = board;
        this.createdAt = createdAt;
    }

}
