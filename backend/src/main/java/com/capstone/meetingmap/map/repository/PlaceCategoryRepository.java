package com.capstone.meetingmap.map.repository;

import com.capstone.meetingmap.map.entity.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Integer> {
    List<PlaceCategory> findByPlaceCategoryCodeNot(String placeCategoryCode);
}
