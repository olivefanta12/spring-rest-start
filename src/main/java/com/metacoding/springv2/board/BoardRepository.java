package com.metacoding.springv2.board;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 상세조회 시 User를 함께 조회해서 Lazy Loading으로 인한 추가 쿼리를 방지한다.
    @Query("select b from Board b join fetch b.user where b.id = :id")
    Optional<Board> findByIdJoinUser(@Param("id") Integer id);

    // 상세조회 시 게시글, 작성자, 댓글, 댓글 작성자를 한 번에 조회한다.
    @Query("select distinct b from Board b join fetch b.user left join fetch b.replies r left join fetch r.user where b.id = :id")
    Optional<Board> findByIdJoinUserAndReplies(@Param("id") Integer id);
}
