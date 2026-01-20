package com.capstone.meetingmap.api.kakao.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class SearchKeywordResponse {
    private List<Documents> documents;
    private Meta meta;

    @Data
    @Builder
    public static class Documents {
        private String address_name;
        private String category_group_code;
        private String category_group_name;
        private String category_name;
        private String distance;
        private String id;
        private String phone;
        private String place_name;
        private String place_url;
        private String road_address_name;
        private String x;
        private String y;
    }

    @Data
    @Builder
    public static class Meta {
        private Boolean is_end;
        private Integer pageable_count;
        private Integer total_count;
        private SameName same_name;
    }

    @Data
    @Builder
    public static class SameName {
        private String keyword;
        private List<String> region;
        private String selected_region;
    }
}
