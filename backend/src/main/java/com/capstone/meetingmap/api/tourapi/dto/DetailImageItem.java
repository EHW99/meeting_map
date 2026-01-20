package com.capstone.meetingmap.api.tourapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DetailImageItem {
    private String contentid;
    private String imgname;
    private String originimgurl;
    private String serialnum;
    private String cpyrhtDivCd;
    private String smallimageurl;
}
