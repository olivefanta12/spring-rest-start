package com.metacoding.springv2.imageboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metacoding.springv2._core.handler.ex.Exception400;
import com.metacoding.springv2._core.handler.ex.Exception404;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageBoardService {

    private final ImageBoardRepository imageBoardRepository;

    public List<ImageBoardResponse.ListDTO> 목록보기() {
        List<ImageBoard> imageBoards = imageBoardRepository.findAll();
        return imageBoards.stream().map(imageBoard -> new ImageBoardResponse.ListDTO(imageBoard)).collect(Collectors.toList());
    }

    public ImageBoardResponse.DetailDTO 상세보기(Integer id) {
        // 이미지 게시글 상세 조회 시 id로 단건을 조회하고 없으면 404 예외를 발생시킨다.
        ImageBoard imageBoardPS = imageBoardRepository.findByIdJoinRepliesAndUser(id)
                .orElseThrow(() -> new Exception404("해당 이미지 게시글을 찾을 수 없습니다"));
        return new ImageBoardResponse.DetailDTO(imageBoardPS);
    }

    @Transactional
    public void 글쓰기(String title, String content, MultipartFile imageFile) {
        // 이미지 파일이 없으면 업로드 실패로 처리한다.
        if (imageFile == null || imageFile.isEmpty()) {
            throw new Exception400("이미지 파일을 선택해주세요");
        }

        // 업로드 폴더가 없으면 생성한다.
        Path uploadDir = Paths.get("upload");
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("업로드 폴더 생성에 실패했습니다");
        }

        // 파일명 충돌을 방지하기 위해 UUID를 붙여 저장한다.
        String originName = imageFile.getOriginalFilename();
        String saveName = UUID.randomUUID() + "_" + originName;
        Path savePath = uploadDir.resolve(saveName);

        try {
            Files.copy(imageFile.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다");
        }

        // 브라우저에서 접근 가능한 URL(/upload/**)을 DB에 저장한다.
        String imageUrl = "/upload/" + saveName;

        ImageBoard imageBoard = ImageBoard.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .build();
        imageBoardRepository.save(imageBoard);
    }

    @Transactional
    public void 좋아요하기(Integer id) {
        ImageBoard imageBoardPS = imageBoardRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 이미지 게시글을 찾을 수 없습니다"));
        imageBoardPS.increaseLikeCount();
    }

    @Transactional
    public void 싫어요하기(Integer id) {
        ImageBoard imageBoardPS = imageBoardRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 이미지 게시글을 찾을 수 없습니다"));
        imageBoardPS.increaseDislikeCount();
    }

    @Transactional
    public void 삭제하기(Integer id) {
        ImageBoard imageBoardPS = imageBoardRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 이미지 게시글을 찾을 수 없습니다"));
        imageBoardRepository.delete(imageBoardPS);
    }
}
