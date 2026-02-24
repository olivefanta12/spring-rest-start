package com.metacoding.springv2.imageboard;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageBoardReplyRepository extends JpaRepository<ImageBoardReply, Integer> {
    @Query("select r from ImageBoardReply r join fetch r.imageBoard where r.id = :id")
    Optional<ImageBoardReply> findByIdJoinImageBoard(@Param("id") Integer id);

    @Query("select r from ImageBoardReply r join fetch r.user join fetch r.imageBoard where r.id = :id")
    Optional<ImageBoardReply> findByIdJoinUserAndImageBoard(@Param("id") Integer id);
}
