package com.metacoding.springv2.imageboard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageBoardReplyReactionRepository extends JpaRepository<ImageBoardReplyReaction, Integer> {
    boolean existsByImageBoardReplyIdAndUserIdAndReactionType(Integer imageBoardReplyId, Integer userId, String reactionType);
    void deleteByImageBoardReplyId(Integer imageBoardReplyId);
}
