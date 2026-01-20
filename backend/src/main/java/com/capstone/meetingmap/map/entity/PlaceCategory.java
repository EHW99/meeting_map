package com.capstone.meetingmap.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PlaceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer placeCategoryNo;

    @Column(length = 20)
    private String placeCategoryCode;

    @Column(length = 20)
    private String placeCategoryName;
}
