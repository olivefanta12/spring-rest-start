package com.metacoding.springv2.board;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metacoding.springv2._core.handler.ex.Exception403;
import com.metacoding.springv2._core.handler.ex.Exception400;
import com.metacoding.springv2._core.handler.ex.Exception404;
import com.metacoding.springv2.user.User;
import com.metacoding.springv2.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardReactionRepository boardReactionRepository;
    private final UserRepository userRepository;

    public Page<BoardResponse.ListDTO> 게시글목록보기(Integer page) {
        // 게시글 목록은 한 페이지에 5개씩, 오래된 글부터(id 오름차순) 순서로 조회한다.
        PageRequest pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<Board> boardPage = boardRepository.findAll(pageable);
        return boardPage.map(board -> new BoardResponse.ListDTO(board));
    }

    public BoardDetailDTO 게시글상세보기(Integer id) {
        // 게시글 상세조회 시 작성자와 댓글, 댓글 작성자까지 fetch join으로 한 번에 조회한다.
        Board boardPS = boardRepository.findByIdJoinUserAndReplies(id)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));

        // 조회한 게시글 엔티티를 상세조회 DTO로 변환해 반환한다.
        return new BoardDetailDTO(boardPS);
    }

    public Optional<BoardResponse.ListDTO> 베스트게시글보기() {
        // 점수(좋아요-싫어요)가 가장 높은 게시글 1개를 반환한다. 동점이면 id가 큰 글을 우선한다.
        return boardRepository.findAll().stream()
                .max(Comparator.comparingInt((Board b) -> b.getLikeCount() - b.getDislikeCount())
                        .thenComparingInt(Board::getId))
                .map(BoardResponse.ListDTO::new);
    }

    @Transactional
    public void 게시글쓰기(String title, String content, Integer userId) {
        // 글 작성자를 조회해서 게시글 엔티티에 연결한다.
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));

        // 제목/내용/작성자를 포함한 게시글을 저장한다.
        Board board = Board.builder()
                .title(title)
                .content(content)
                .user(userPS)
                .build();
        boardRepository.save(board);
    }

    @Transactional
    public void 게시글수정하기(Integer id, String title, String content, Integer userId) {
        // 수정할 게시글을 작성자와 함께 조회해서 권한 체크에 사용한다.
        Board boardPS = boardRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));

        // 게시글 작성자 본인만 수정할 수 있도록 권한을 검사한다.
        if (!boardPS.getUser().getId().equals(userId)) {
            throw new Exception403("게시글 수정 권한이 없습니다");
        }

        // 제목/내용을 수정한다.
        boardPS.update(title, content);
    }

    @Transactional
    public void 게시글삭제하기(Integer id, Integer userId) {
        // 삭제할 게시글을 작성자와 함께 조회해서 권한 체크에 사용한다.
        Board boardPS = boardRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        boolean isAdmin = userPS.getRoles() != null && userPS.getRoles().contains("ADMIN");

        // 게시글 작성자 본인 또는 ADMIN만 삭제할 수 있도록 권한을 검사한다.
        if (!boardPS.getUser().getId().equals(userId) && !isAdmin) {
            throw new Exception403("게시글 삭제 권한이 없습니다");
        }

        // 게시글 반응 이력을 먼저 삭제해서 FK 제약 위반을 방지한다.
        boardReactionRepository.deleteByBoardId(id);

        // 조회된 게시글을 삭제한다. (연관된 댓글은 Board 엔티티의 cascade 설정으로 함께 제거된다)
        boardRepository.delete(boardPS);
    }

    @Transactional
    public void 게시글좋아요하기(Integer id, Integer userId) {
        Board boardPS = boardRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));

        boolean exists = boardReactionRepository.existsByBoardIdAndUserIdAndReactionType(id, userId, "LIKE");
        if (exists) {
            throw new Exception400("이미 좋아요를 누른 게시글입니다");
        }

        BoardReaction reaction = BoardReaction.builder()
                .reactionType("LIKE")
                .board(boardPS)
                .user(userPS)
                .build();
        boardReactionRepository.save(reaction);
        boardPS.increaseLikeCount();
    }

    @Transactional
    public void 게시글싫어요하기(Integer id, Integer userId) {
        Board boardPS = boardRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));

        boolean exists = boardReactionRepository.existsByBoardIdAndUserIdAndReactionType(id, userId, "DISLIKE");
        if (exists) {
            throw new Exception400("이미 싫어요를 누른 게시글입니다");
        }

        BoardReaction reaction = BoardReaction.builder()
                .reactionType("DISLIKE")
                .board(boardPS)
                .user(userPS)
                .build();
        boardReactionRepository.save(reaction);
        boardPS.increaseDislikeCount();
    }
}
