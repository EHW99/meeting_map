package com.capstone.meetingmap.board.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//생성, 수정 시간 관리를 위한 클래스
public class BoardTime {
    @CreatedDate
    private LocalDateTime boardWriteDate; //게시글 작성일

    @LastModifiedDate
    private LocalDateTime boardUpdateDate; //게시글 수정일
}
