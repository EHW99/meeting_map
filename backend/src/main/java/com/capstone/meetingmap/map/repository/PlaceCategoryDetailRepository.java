package com.capstone.meetingmap.map.repository;

import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaceCategoryDetailRepository extends JpaRepository<PlaceCategoryDetail, Integer> {
    Optional<PlaceCategoryDetail> findByPlaceCategoryDetailCode(String placeCategoryDetailCode);
    List<PlaceCategoryDetail> findByPlaceCategoryDetailCodeStartingWith(String prefix);
    Optional<PlaceCategoryDetail> findBySearchType(String searchType);
    Optional<PlaceCategoryDetail> findByContentTypeIdAndCat1(String contentTypeId, String cat1);
    Optional<PlaceCategoryDetail> findByContentTypeIdAndCat1AndCat2(String contentTypeId, String cat1, String cat2);
    Optional<PlaceCategoryDetail> findByContentTypeIdAndCat1AndCat2AndCat3(String contentTypeId, String cat1, String cat2, String cat3);

    @Query("SELECT DISTINCT p.searchType FROM PlaceCategoryDetail p WHERE p.searchType IS NOT NULL")
    List<String> findDistinctSearchTypes();
}
