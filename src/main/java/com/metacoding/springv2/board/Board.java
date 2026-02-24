package com.metacoding.springv2.board;

import java.sql.Timestamp;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import com.metacoding.springv2.reply.Reply;
import com.metacoding.springv2.user.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "board_tb")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 30, nullable = false)
    private String title;
    @Column(length = 300, nullable = false)
    private String content;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replies = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    @Builder
    public Board(Integer id, String title, String content, int likeCount, int dislikeCount, User user, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.user = user;
        this.createdAt = createdAt;
    }

}
