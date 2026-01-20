package com.capstone.meetingmap.schedule.controller;

import com.capstone.meetingmap.schedule.dto.*;
import com.capstone.meetingmap.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 자신이 만든 스케줄 조회
    @GetMapping
    public List<ScheduleResponseDto> getMySchedules() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return scheduleService.getSchedulesByUserId(userId);
    }

    // 스케줄 저장
    @PostMapping
    public ResponseEntity<?> saveSchedule(@RequestBody ScheduleSaveRequestDto request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer scheduleNo = scheduleService.saveScheduleWithDetails(userId, request);

        URI location = URI.create("/api/schedules/" + scheduleNo);

        return ResponseEntity.created(location).build();
    }

    // 스케줄 삭제
    @DeleteMapping("/{scheduleNo}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer scheduleNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        scheduleService.deleteSchedule(userId, scheduleNo);
        return ResponseEntity.noContent().build();
    }

    // 스케줄 수정
    @PutMapping("/{scheduleNo}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer scheduleNo, @RequestBody ScheduleSaveRequestDto request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        scheduleService.updateSchedule(userId, scheduleNo, request);
        return ResponseEntity.noContent().build();
    }

    // 사용자로부터 입력받은 데이터를 통해 스케줄 생성
    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@Valid @RequestBody ScheduleCreateRequestDto scheduleCreateRequestDto) {
        ScheduleCreateResponseDto scheduleCreateResponseDto = scheduleService.createSchedule(scheduleCreateRequestDto);
        return ResponseEntity.ok(scheduleCreateResponseDto);
    }
}
