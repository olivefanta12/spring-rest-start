package com.metacoding.springv2.imageboard;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageBoardRepository extends JpaRepository<ImageBoard, Integer> {
    @Query("select distinct ib from ImageBoard ib left join fetch ib.replies r left join fetch r.user where ib.id = :id")
    Optional<ImageBoard> findByIdJoinRepliesAndUser(@Param("id") Integer id);
}
