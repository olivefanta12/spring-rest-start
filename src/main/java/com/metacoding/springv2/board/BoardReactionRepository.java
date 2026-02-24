package com.metacoding.springv2.board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReactionRepository extends JpaRepository<BoardReaction, Integer> {
    boolean existsByBoardIdAndUserIdAndReactionType(Integer boardId, Integer userId, String reactionType);
    void deleteByBoardId(Integer boardId);
}
