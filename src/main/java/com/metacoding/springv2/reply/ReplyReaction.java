package com.metacoding.springv2.reply;

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
@Table(name = "reply_reaction_tb", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "reply_id", "user_id", "reaction_type" })
})
public class ReplyReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reaction_type", length = 10, nullable = false)
    private String reactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public ReplyReaction(Integer id, String reactionType, Reply reply, User user, Timestamp createdAt) {
        this.id = id;
        this.reactionType = reactionType;
        this.reply = reply;
        this.user = user;
        this.createdAt = createdAt;
    }
}
