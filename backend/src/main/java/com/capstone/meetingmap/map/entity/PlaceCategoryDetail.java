package com.capstone.meetingmap.map.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PlaceCategoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer placeCategoryDetailNo;

    @Column(length = 30)
    private String placeCategoryDetailCode;

    @Column(length = 20)
    private String placeCategoryDetailName;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "parent_no", nullable = false)
    private PlaceCategory placeCategory;

    @Column(length = 2)
    private String contentTypeId;

    @Column(length = 3)
    private String cat1;

    @Column(length = 5)
    private String cat2;

    @Column(length = 9)
    private String cat3;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean additionalSearch;

    @Column(length = 30)
    private String searchType;

    @Builder
    public PlaceCategoryDetail(String placeCategoryDetailCode, String placeCategoryDetailName, PlaceCategory placeCategory, String contentTypeId, String cat1, String cat2, String cat3, Boolean additionalSearch, String searchType) {
        this.placeCategoryDetailCode = placeCategoryDetailCode;
        this.placeCategoryDetailName = placeCategoryDetailName;
        this.placeCategory = placeCategory;
        this.contentTypeId = contentTypeId;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.additionalSearch = additionalSearch;
        this.searchType = searchType;
    }
}
