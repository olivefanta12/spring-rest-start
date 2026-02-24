package com.metacoding.springv2.board;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.metacoding.springv2._core.handler.ex.Exception401;
import com.metacoding.springv2._core.handler.ex.Exception403;
import com.metacoding.springv2.imageboard.ImageBoardResponse;
import com.metacoding.springv2.imageboard.ImageBoardReplyService;
import com.metacoding.springv2.imageboard.ImageBoardService;
import com.metacoding.springv2.reply.ReplyService;
import com.metacoding.springv2.user.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final ImageBoardService imageBoardService;
    private final ImageBoardReplyService imageBoardReplyService;

    // 루트 경로 접근 시 게시글 목록 화면을 바로 렌더링한다.
    @GetMapping("/")
    public String 홈(@RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
        Page<BoardResponse.ListDTO> boardPage = boardService.게시글목록보기(page);
        model.addAttribute("boards", boardPage.getContent());
        베스트게시글보기().ifPresent(bestPost -> model.addAttribute("bestPost", bestPost));
        model.addAttribute("page", boardPage.getNumber());
        model.addAttribute("displayPage", boardPage.getNumber() + 1);
        model.addAttribute("hasPrev", boardPage.hasPrevious());
        model.addAttribute("hasNext", boardPage.hasNext());
        model.addAttribute("prevPage", boardPage.getNumber() - 1);
        model.addAttribute("nextPage", boardPage.getNumber() + 1);
        return "board/list";
    }

    // 기존 주소(/board/list)로 접근해도 새 주소(/boards)로 보낸다.
    @GetMapping({ "/board/list", "/board/list." })
    public String 목록페이지레거시리다이렉트() {
        return "redirect:/boards";
    }

    // 게시글 목록 화면을 렌더링한다.
    @GetMapping("/boards")
    public String 목록페이지(@RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
        Page<BoardResponse.ListDTO> boardPage = boardService.게시글목록보기(page);
        model.addAttribute("boards", boardPage.getContent());
        베스트게시글보기().ifPresent(bestPost -> model.addAttribute("bestPost", bestPost));
        model.addAttribute("page", boardPage.getNumber());
        model.addAttribute("displayPage", boardPage.getNumber() + 1);
        model.addAttribute("hasPrev", boardPage.hasPrevious());
        model.addAttribute("hasNext", boardPage.hasNext());
        model.addAttribute("prevPage", boardPage.getNumber() - 1);
        model.addAttribute("nextPage", boardPage.getNumber() + 1);
        return "board/list";
    }

    // 글쓰기 게시판 전용 글쓰기 페이지를 렌더링한다.
    @GetMapping("/boards/write-form")
    public String 글쓰기페이지() {
        return "board/write-form";
    }

    // 게시글 수정 페이지를 렌더링한다. (작성자만 접근 가능)
    @GetMapping("/boards/{id}/edit-form")
    public String 수정페이지(@PathVariable("id") Integer id, Authentication authentication, Model model) {
        User sessionUser = 로그인유저(authentication);
        BoardDetailDTO board = boardService.게시글상세보기(id);
        if (!board.getUserId().equals(sessionUser.getId())) {
            throw new Exception403("게시글 수정 권한이 없습니다");
        }
        model.addAttribute("board", board);
        return "board/edit-form";
    }

    // 글쓰기 게시판에서 새 글을 등록한 뒤 목록으로 이동한다.
    @PostMapping("/boards/write")
    public String 글쓰기(@RequestParam("title") String title, @RequestParam("content") String content,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        boardService.게시글쓰기(title, content, sessionUser.getId());
        return "redirect:/boards";
    }

    // 글쓰기 게시판에서 게시글을 수정한 뒤 상세로 이동한다.
    @PostMapping("/boards/{id}/update")
    public String 수정하기(@PathVariable("id") Integer id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        boardService.게시글수정하기(id, title, content, sessionUser.getId());
        return "redirect:/boards/" + id;
    }

    // 이미지 게시판 화면을 렌더링한다.
    @GetMapping("/image-boards")
    public String 이미지게시판페이지(Model model) {
        List<ImageBoardResponse.ListDTO> imageBoards = imageBoardService.목록보기();
        model.addAttribute("imageBoards", imageBoards);
        return "board/image-list";
    }

    // 이미지 게시글 상세 화면을 렌더링한다.
    @GetMapping("/image-boards/{id}")
    public String 이미지게시글상세페이지(@PathVariable("id") Integer id, Model model) {
        ImageBoardResponse.DetailDTO imageBoard = imageBoardService.상세보기(id);
        model.addAttribute("imageBoard", imageBoard);
        return "board/image-detail";
    }

    // 이미지 게시글 상세 화면에서 좋아요를 누르면 수를 증가시키고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/like")
    public String 이미지게시글좋아요(@PathVariable("id") Integer id, Authentication authentication) {
        로그인유저(authentication);
        imageBoardService.좋아요하기(id);
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시글 상세 화면에서 싫어요를 누르면 수를 증가시키고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/dislike")
    public String 이미지게시글싫어요(@PathVariable("id") Integer id, Authentication authentication) {
        로그인유저(authentication);
        imageBoardService.싫어요하기(id);
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시글 상세 화면에서 댓글을 등록하고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/replies")
    public String 이미지댓글쓰기(@PathVariable("id") Integer id, @RequestParam("comment") String comment,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        imageBoardReplyService.댓글쓰기(id, sessionUser.getId(), comment);
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시글 댓글 좋아요를 반영하고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/replies/{replyId}/like")
    public String 이미지댓글좋아요(@PathVariable("id") Integer id, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        imageBoardReplyService.댓글좋아요하기(id, replyId, sessionUser.getId());
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시글 댓글 싫어요를 반영하고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/replies/{replyId}/dislike")
    public String 이미지댓글싫어요(@PathVariable("id") Integer id, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        imageBoardReplyService.댓글싫어요하기(id, replyId, sessionUser.getId());
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시글 댓글 삭제를 반영하고 다시 상세 페이지로 이동한다.
    @PostMapping("/image-boards/{id}/replies/{replyId}/delete")
    public String 이미지댓글삭제(@PathVariable("id") Integer id, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        imageBoardReplyService.댓글삭제하기(id, replyId, sessionUser.getId());
        return "redirect:/image-boards/" + id;
    }

    // 이미지 게시판 전용 글쓰기 페이지를 렌더링한다.
    @GetMapping("/image-boards/write-form")
    public String 이미지게시글쓰기페이지() {
        return "board/image-write-form";
    }

    // 이미지 게시판에서 글쓰기와 이미지 업로드를 처리한다.
    @PostMapping("/image-boards/write")
    public String 이미지게시글쓰기(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("imageFile") MultipartFile imageFile,
            Authentication authentication) {
        로그인유저(authentication);
        imageBoardService.글쓰기(title, content, imageFile);
        return "redirect:/image-boards";
    }

    // 이미지 게시글 삭제 후 목록으로 이동한다.
    @PostMapping("/image-boards/{id}/delete")
    public String 이미지게시글삭제(@PathVariable("id") Integer id, Authentication authentication) {
        로그인유저(authentication);
        imageBoardService.삭제하기(id);
        return "redirect:/image-boards";
    }

    // 기존 주소(/board/detail/{id})로 접근해도 새 주소(/boards/{id})로 보낸다.
    @GetMapping("/board/detail/{id}")
    public String 상세페이지레거시리다이렉트(@PathVariable("id") Integer id) {
        return "redirect:/boards/" + id;
    }

    // 게시글 상세 화면을 렌더링한다.
    @GetMapping("/boards/{id}")
    public String 상세페이지(@PathVariable("id") Integer id, Model model) {
        BoardDetailDTO board = boardService.게시글상세보기(id);
        model.addAttribute("board", board);
        return "board/detail";
    }

    // 상세 화면에서 댓글을 등록하고 다시 상세 페이지로 이동한다.
    @PostMapping("/boards/{id}/replies")
    public String 댓글쓰기(@PathVariable("id") Integer id,
            @RequestParam("comment") String comment,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        replyService.댓글쓰기(id, sessionUser.getId(), comment, imageFile);
        return "redirect:/boards/" + id;
    }

    // 화면에서 댓글 삭제 버튼을 누르면 댓글을 삭제하고 상세 페이지로 다시 이동한다.
    @PostMapping("/boards/{boardId}/replies/{replyId}/delete")
    public String 댓글삭제하기(@PathVariable("boardId") Integer boardId, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        replyService.댓글삭제하기(replyId, sessionUser.getId());
        return "redirect:/boards/" + boardId;
    }

    // 댓글 좋아요를 반영하고 다시 상세 페이지로 이동한다.
    @PostMapping("/boards/{boardId}/replies/{replyId}/like")
    public String 댓글좋아요(@PathVariable("boardId") Integer boardId, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        replyService.댓글좋아요하기(boardId, replyId, sessionUser.getId());
        return "redirect:/boards/" + boardId;
    }

    // 댓글 싫어요를 반영하고 다시 상세 페이지로 이동한다.
    @PostMapping("/boards/{boardId}/replies/{replyId}/dislike")
    public String 댓글싫어요(@PathVariable("boardId") Integer boardId, @PathVariable("replyId") Integer replyId,
            Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        replyService.댓글싫어요하기(boardId, replyId, sessionUser.getId());
        return "redirect:/boards/" + boardId;
    }

    // 화면에서 좋아요 버튼을 누르면 좋아요 수를 증가시키고 상세 페이지로 다시 이동한다.
    @PostMapping("/boards/{id}/like")
    public String 좋아요하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        boardService.게시글좋아요하기(id, sessionUser.getId());
        return "redirect:/boards/" + id;
    }

    // 화면에서 싫어요 버튼을 누르면 싫어요 수를 증가시키고 상세 페이지로 다시 이동한다.
    @PostMapping("/boards/{id}/dislike")
    public String 싫어요하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        boardService.게시글싫어요하기(id, sessionUser.getId());
        return "redirect:/boards/" + id;
    }

    // 화면에서 삭제 버튼을 누르면 게시글을 삭제하고 목록 페이지로 이동한다.
    @PostMapping("/boards/{id}/delete")
    public String 삭제하기(@PathVariable("id") Integer id, Authentication authentication) {
        User sessionUser = 로그인유저(authentication);
        boardService.게시글삭제하기(id, sessionUser.getId());
        return "redirect:/boards";
    }

    private Optional<BestPostDTO> 베스트게시글보기() {
        Optional<BestPostDTO> bestBoard = boardService.베스트게시글보기()
                .map(board -> new BestPostDTO(board.getTitle(), "/boards/" + board.getId(),
                        board.getLikeCount(), board.getDislikeCount()));

        Optional<BestPostDTO> bestImageBoard = imageBoardService.목록보기().stream()
                .max(Comparator.comparingInt((ImageBoardResponse.ListDTO b) -> b.getLikeCount() - b.getDislikeCount())
                        .thenComparingInt(ImageBoardResponse.ListDTO::getId))
                .map(board -> new BestPostDTO(board.getTitle(), "/image-boards/" + board.getId(),
                        board.getLikeCount(), board.getDislikeCount()));

        if (bestBoard.isEmpty()) {
            return bestImageBoard;
        }
        if (bestImageBoard.isEmpty()) {
            return bestBoard;
        }

        int boardScore = bestBoard.get().getLikeCount() - bestBoard.get().getDislikeCount();
        int imageScore = bestImageBoard.get().getLikeCount() - bestImageBoard.get().getDislikeCount();
        return boardScore >= imageScore ? bestBoard : bestImageBoard;
    }

    @lombok.Getter
    private static class BestPostDTO {
        private final String title;
        private final String url;
        private final Integer likeCount;
        private final Integer dislikeCount;

        private BestPostDTO(String title, String url, Integer likeCount, Integer dislikeCount) {
            this.title = title;
            this.url = url;
            this.likeCount = likeCount;
            this.dislikeCount = dislikeCount;
        }
    }

    private User 로그인유저(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User sessionUser)) {
            throw new Exception401("로그인 후 이용해주세요");
        }
        return sessionUser;
    }
}
