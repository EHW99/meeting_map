package com.capstone.meetingmap.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category {
    @Id
    private Integer categoryNo;

    private String categoryName;

    @Builder
    public Category(Integer categoryNo, String categoryName) {
        this.categoryNo = categoryNo;
        this.categoryName = categoryName;
    }
}
