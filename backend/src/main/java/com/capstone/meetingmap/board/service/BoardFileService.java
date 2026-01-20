package com.capstone.meetingmap.board.service;

import com.capstone.meetingmap.api.amazon.service.S3Service;
import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.board.entity.BoardFile;
import com.capstone.meetingmap.board.repository.BoardFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardFileService {
    private final BoardFileRepository boardFileRepository;
    private final S3Service s3Service;

    public BoardFileService(BoardFileRepository boardFileRepository, S3Service s3Service) {
        this.boardFileRepository = boardFileRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public List<BoardFile> saveFiles(Board board, List<MultipartFile> files) throws IOException {
        List<BoardFile> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            // 파일 저장 로직
            String fileUrl = s3Service.upload(file);

            BoardFile boardFile = BoardFile.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .board(board)
                    .build();
            boardFileRepository.save(boardFile);
            savedFiles.add(boardFile);
        }
        return savedFiles;
    }

    @Transactional
    public void deleteFiles(Integer boardNo) {
        List<BoardFile> files = boardFileRepository.findAllByBoard_BoardNo(boardNo);
        // S3에서 실제 삭제
        for (BoardFile file : files) {
            String fileName = s3Service.extractKeyFromUrl(file.getFileUrl());
            s3Service.delete(fileName);
        }
        boardFileRepository.deleteAll(files);
    }
}
