package com.metacoding.springv2.imageboard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "image_board_tb")
public class ImageBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 300, nullable = false)
    private String content;

    @Column(length = 300, nullable = false)
    private String imageUrl;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikeCount;

    @OneToMany(mappedBy = "imageBoard", cascade = CascadeType.REMOVE)
    private List<ImageBoardReply> replies = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    @Builder
    public ImageBoard(Integer id, String title, String content, String imageUrl, int likeCount, int dislikeCount,
            Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.createdAt = createdAt;
    }
}
