package com.capstone.meetingmap.schedule.repository;

import com.capstone.meetingmap.schedule.entity.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleDetailRepository extends JpaRepository<ScheduleDetail, Integer> {
}
