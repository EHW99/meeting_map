package com.capstone.meetingmap.user.controller;

import com.capstone.meetingmap.board.dto.BoardScrapView;
import com.capstone.meetingmap.board.entity.BoardView;
import com.capstone.meetingmap.board.service.BoardService;
import com.capstone.meetingmap.group.dto.GroupResponseDto;
import com.capstone.meetingmap.group.service.GroupService;
import com.capstone.meetingmap.user.dto.*;
import com.capstone.meetingmap.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final BoardService boardService;
    private final GroupService groupService;

    public UserController(UserService userService, BoardService boardService, GroupService groupService) {
        this.userService = userService;
        this.boardService = boardService;
        this.groupService = groupService;
    }

    //아이디 중복 검사
    @PostMapping("/check-id")
    public ResponseEntity<?> checkUserId(@RequestBody UserCheckIdRequestDto userCheckIdRequestDto) {
        UserCheckIdResponseDto userCheckIdResponseDto = userService.existsByUserId(userCheckIdRequestDto);
        return ResponseEntity.ok(userCheckIdResponseDto);
    }

    //회원가입 처리
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        UserResponseDto userResponseDto = userService.join(userRegisterRequestDto);

        log.info("New User Registered!: {}", userRegisterRequestDto.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    //회원 본인 조회
    @GetMapping
    public ResponseEntity<?> getUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDto userResponseDto = userService.findOne(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    //전체 회원 조회
    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDto>> list() {
        List<UserResponseDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    //회원 정보 수정
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "user", required = false) UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponseDto userResponseDto = userService.updateUser(userUpdateRequestDto, profileImage, userId);

        log.info("User Updated: {}", userId);

        return ResponseEntity.ok(userResponseDto);
    }

    // 작성한 글 조회
    @GetMapping("/boards")
    public ResponseEntity<?> getMyBoards() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<BoardView> boardView = boardService.searchArticlesByUser(userId);
        return ResponseEntity.ok(boardView);
    }

    // 좋아요한 글 조회
    @GetMapping("/boards/liked")
    public ResponseEntity<?> getLikedBoards() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<BoardView> boardView = boardService.searchArticlesByLiked(userId);
        return ResponseEntity.ok(boardView);
    }

    // 저장한 글 조회
    @GetMapping("/boards/scraped")
    public ResponseEntity<?> getScrapedBoards() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<BoardScrapView> scrapBoardView = boardService.searchArticlesByScraped(userId);

        return ResponseEntity.ok(scrapBoardView);
    }

    // 속한 그룹 조회
    @GetMapping("/groups")
    public ResponseEntity<?> getMyGroups() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GroupResponseDto> groupResponseDtoList = groupService.getGroupsByUserId(userId);

        return ResponseEntity.ok(groupResponseDtoList);
    }

    // 비밀번호 변경
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody UserUpdatePasswdRequestDto userUpdatePasswdRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.updatePassword(userUpdatePasswdRequestDto, userId);

        return ResponseEntity.noContent().build();
    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody UserDeleteRequestDto userDeleteRequestDto, HttpServletResponse response) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.delete(userDeleteRequestDto, userId);

        // "accessToken" 쿠키 삭제
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setMaxAge(0); // 쿠키 만료
        cookie.setPath("/"); // 쿠키 경로 설정 (서버에서 설정한 경로와 동일해야 함)
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie); // 응답에 쿠키 추가

        return ResponseEntity.noContent().build();
    }
}
