package com.metacoding.springv2.reply;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metacoding.springv2._core.handler.ex.Exception400;
import com.metacoding.springv2._core.handler.ex.Exception403;
import com.metacoding.springv2._core.handler.ex.Exception404;
import com.metacoding.springv2.board.Board;
import com.metacoding.springv2.board.BoardRepository;
import com.metacoding.springv2.user.User;
import com.metacoding.springv2.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyReactionRepository replyReactionRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReplyResponse.DetailDTO 댓글쓰기(Integer boardId, Integer userId, String comment, MultipartFile imageFile) {
        // 댓글이 달릴 게시글을 조회하고 없으면 404 예외를 발생시킨다.
        Board boardPS = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다"));

        // 댓글 작성자를 조회하고 없으면 404 예외를 발생시킨다.
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Path uploadDir = Paths.get("upload");
            try {
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
            } catch (IOException e) {
                throw new RuntimeException("업로드 폴더 생성에 실패했습니다");
            }

            String originName = imageFile.getOriginalFilename();
            String saveName = UUID.randomUUID() + "_" + originName;
            Path savePath = uploadDir.resolve(saveName);

            try {
                Files.copy(imageFile.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("댓글 이미지 업로드에 실패했습니다");
            }
            imageUrl = "/upload/" + saveName;
        }

        // 댓글 엔티티를 생성해 저장한다.
        Reply reply = Reply.builder()
                .comment(comment)
                .imageUrl(imageUrl)
                .board(boardPS)
                .user(userPS)
                .build();
        Reply replyPS = replyRepository.save(reply);

        // 저장된 댓글을 응답 DTO로 변환해서 반환한다.
        return new ReplyResponse.DetailDTO(replyPS);
    }

    @Transactional
    public Integer 댓글삭제하기(Integer replyId, Integer userId) {
        // 삭제할 댓글을 작성자/게시글과 함께 조회해서 권한 체크와 리다이렉트에 활용한다.
        Reply replyPS = replyRepository.findByIdJoinUserAndBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        boolean isAdmin = userPS.getRoles() != null && userPS.getRoles().contains("ADMIN");

        // 댓글 작성자 본인 또는 ADMIN만 삭제할 수 있도록 권한을 검사한다.
        if (!replyPS.getUser().getId().equals(userId) && !isAdmin) {
            throw new Exception403("댓글 삭제 권한이 없습니다");
        }

        // 리다이렉트할 게시글 ID를 기억한 뒤 댓글을 삭제한다.
        Integer boardId = replyPS.getBoard().getId();
        replyRepository.delete(replyPS);
        return boardId;
    }

    @Transactional
    public void 댓글좋아요하기(Integer boardId, Integer replyId, Integer userId) {
        Reply replyPS = replyRepository.findByIdJoinBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        if (!replyPS.getBoard().getId().equals(boardId)) {
            throw new Exception404("해당 게시글의 댓글이 아닙니다");
        }
        boolean exists = replyReactionRepository.existsByReplyIdAndUserIdAndReactionType(replyId, userId, "LIKE");
        if (exists) {
            throw new Exception400("이미 좋아요를 누른 댓글입니다");
        }
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        ReplyReaction reaction = ReplyReaction.builder()
                .reactionType("LIKE")
                .reply(replyPS)
                .user(userPS)
                .build();
        replyReactionRepository.save(reaction);
        replyPS.increaseLikeCount();
    }

    @Transactional
    public void 댓글싫어요하기(Integer boardId, Integer replyId, Integer userId) {
        Reply replyPS = replyRepository.findByIdJoinBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        if (!replyPS.getBoard().getId().equals(boardId)) {
            throw new Exception404("해당 게시글의 댓글이 아닙니다");
        }
        boolean exists = replyReactionRepository.existsByReplyIdAndUserIdAndReactionType(replyId, userId, "DISLIKE");
        if (exists) {
            throw new Exception400("이미 싫어요를 누른 댓글입니다");
        }
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        ReplyReaction reaction = ReplyReaction.builder()
                .reactionType("DISLIKE")
                .reply(replyPS)
                .user(userPS)
                .build();
        replyReactionRepository.save(reaction);
        replyPS.increaseDislikeCount();
    }
}
