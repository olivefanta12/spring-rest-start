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
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "image_reply_reaction_tb", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "image_board_reply_id", "user_id", "reaction_type" })
})
public class ImageBoardReplyReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reaction_type", length = 10, nullable = false)
    private String reactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    private ImageBoardReply imageBoardReply;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public ImageBoardReplyReaction(Integer id, String reactionType, ImageBoardReply imageBoardReply, User user,
            Timestamp createdAt) {
        this.id = id;
        this.reactionType = reactionType;
        this.imageBoardReply = imageBoardReply;
        this.user = user;
        this.createdAt = createdAt;
    }
}
