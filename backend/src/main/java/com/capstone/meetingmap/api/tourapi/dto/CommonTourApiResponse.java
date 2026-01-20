package com.capstone.meetingmap.api.tourapi.dto;

import lombok.Data;

import java.util.List;

//api로부터 받아올 기본 dto ,<T> 부분에 AreaCodeItem 등을 넣어서 사용
@Data
public class CommonTourApiResponse<T> {
    private Response<T> response;

    @Data
    public static class Response<T> {
        private Header header;
        private Body<T> body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body<T> {
        private Items<T> items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items<T> {
        private List<T> item;
    }
}


