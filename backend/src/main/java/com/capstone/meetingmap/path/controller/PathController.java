package com.capstone.meetingmap.path.controller;

import com.capstone.meetingmap.path.service.PathService;
import com.capstone.meetingmap.schedule.dto.ScheduleDetailCreateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/path")
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    // 보행자 길찾기
    @PostMapping("/pedestrian")
    public ResponseEntity<?> pedestrianPath(@RequestBody List<ScheduleDetailCreateDto> dtoList) {
        return ResponseEntity.ok(pathService.getPedestrianPath(dtoList));
    }

    // 자동차 길찾기
    @PostMapping("/car")
    public ResponseEntity<?> carPath(@RequestBody List<ScheduleDetailCreateDto> dtoList) {
        return ResponseEntity.ok(pathService.getCarPath(dtoList));
    }

    // 대중교통 길찾기
    @PostMapping("/transit")
    public ResponseEntity<?> transitPath(@RequestBody List<ScheduleDetailCreateDto> dtoList) {
        return ResponseEntity.ok(pathService.getTransitPath(dtoList));
    }

    // 장소 이름으로 보행자 길찾기
    @GetMapping("/pedestrian")
    public ResponseEntity<?> getPedestrianPath(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "name", required = false) List<String> name
    ) {
        return ResponseEntity.ok(pathService.getPedestrianPathByName(start, end, name));
    }

    // 장소 이름으로 자동차 길찾기
    @GetMapping("/car")
    public ResponseEntity<?> getCarPath(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "name", required = false) List<String> name
    ) {
        return ResponseEntity.ok(pathService.getCarPathByName(start, end, name));
    }

    // 장소 이름으로 대중교통 길찾기
    @GetMapping("/transit")
    public ResponseEntity<?> getTransitPath(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "name", required = false) List<String> name
    ) {
        return ResponseEntity.ok(pathService.getTransitPathByName(start, end, name));
    }
}
