package com.metacoding.springv2.reply;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyReactionRepository extends JpaRepository<ReplyReaction, Integer> {
    boolean existsByReplyIdAndUserIdAndReactionType(Integer replyId, Integer userId, String reactionType);
}
