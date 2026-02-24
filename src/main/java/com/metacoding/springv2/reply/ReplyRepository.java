package com.metacoding.springv2.reply;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    // 댓글 삭제 시 댓글 작성자와 게시글 정보를 함께 조회해서 권한 체크와 리다이렉트에 사용한다.
    @Query("select r from Reply r join fetch r.user join fetch r.board where r.id = :id")
    Optional<Reply> findByIdJoinUserAndBoard(@Param("id") Integer id);

    @Query("select r from Reply r join fetch r.board where r.id = :id")
    Optional<Reply> findByIdJoinBoard(@Param("id") Integer id);
}
