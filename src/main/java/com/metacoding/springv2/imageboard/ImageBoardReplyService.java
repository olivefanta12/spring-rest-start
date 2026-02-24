package com.metacoding.springv2.imageboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metacoding.springv2._core.handler.ex.Exception400;
import com.metacoding.springv2._core.handler.ex.Exception403;
import com.metacoding.springv2._core.handler.ex.Exception404;
import com.metacoding.springv2.user.User;
import com.metacoding.springv2.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageBoardReplyService {

    private final ImageBoardReplyRepository imageBoardReplyRepository;
    private final ImageBoardReplyReactionRepository imageBoardReplyReactionRepository;
    private final ImageBoardRepository imageBoardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void 댓글쓰기(Integer imageBoardId, Integer userId, String comment) {
        ImageBoard imageBoardPS = imageBoardRepository.findById(imageBoardId)
                .orElseThrow(() -> new Exception404("해당 이미지 게시글을 찾을 수 없습니다"));

        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));

        ImageBoardReply reply = ImageBoardReply.builder()
                .comment(comment)
                .imageBoard(imageBoardPS)
                .user(userPS)
                .build();
        imageBoardReplyRepository.save(reply);
    }

    @Transactional
    public void 댓글좋아요하기(Integer imageBoardId, Integer replyId, Integer userId) {
        ImageBoardReply replyPS = imageBoardReplyRepository.findByIdJoinImageBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        if (!replyPS.getImageBoard().getId().equals(imageBoardId)) {
            throw new Exception404("해당 이미지 게시글의 댓글이 아닙니다");
        }
        boolean exists = imageBoardReplyReactionRepository
                .existsByImageBoardReplyIdAndUserIdAndReactionType(replyId, userId, "LIKE");
        if (exists) {
            throw new Exception400("이미 좋아요를 누른 댓글입니다");
        }
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        ImageBoardReplyReaction reaction = ImageBoardReplyReaction.builder()
                .reactionType("LIKE")
                .imageBoardReply(replyPS)
                .user(userPS)
                .build();
        imageBoardReplyReactionRepository.save(reaction);
        replyPS.increaseLikeCount();
    }

    @Transactional
    public void 댓글싫어요하기(Integer imageBoardId, Integer replyId, Integer userId) {
        ImageBoardReply replyPS = imageBoardReplyRepository.findByIdJoinImageBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        if (!replyPS.getImageBoard().getId().equals(imageBoardId)) {
            throw new Exception404("해당 이미지 게시글의 댓글이 아닙니다");
        }
        boolean exists = imageBoardReplyReactionRepository
                .existsByImageBoardReplyIdAndUserIdAndReactionType(replyId, userId, "DISLIKE");
        if (exists) {
            throw new Exception400("이미 싫어요를 누른 댓글입니다");
        }
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        ImageBoardReplyReaction reaction = ImageBoardReplyReaction.builder()
                .reactionType("DISLIKE")
                .imageBoardReply(replyPS)
                .user(userPS)
                .build();
        imageBoardReplyReactionRepository.save(reaction);
        replyPS.increaseDislikeCount();
    }

    @Transactional
    public void 댓글삭제하기(Integer imageBoardId, Integer replyId, Integer userId) {
        ImageBoardReply replyPS = imageBoardReplyRepository.findByIdJoinUserAndImageBoard(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다"));
        if (!replyPS.getImageBoard().getId().equals(imageBoardId)) {
            throw new Exception404("해당 이미지 게시글의 댓글이 아닙니다");
        }

        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
        boolean isAdmin = userPS.getRoles() != null && userPS.getRoles().contains("ADMIN");
        if (!replyPS.getUser().getId().equals(userId) && !isAdmin) {
            throw new Exception403("댓글 삭제 권한이 없습니다");
        }

        imageBoardReplyReactionRepository.deleteByImageBoardReplyId(replyId);
        imageBoardReplyRepository.delete(replyPS);
    }
}
