package com.capstone.meetingmap.api.google.dto;

import lombok.Data;

import java.util.List;

@Data
public class GooglePlaceResult {
        private String business_status;
        private String formatted_address;
        private Geometry geometry;
        private String icon;
        private String icon_background_color;
        private String icon_mask_base_uri;
        private String name;
        private OpeningHours opening_hours;
        private List<Photo> photos;
        private String place_id;
        private PlusCode plus_code;
        private Integer price_level;
        private Double rating;
        private String reference;
        private List<String> types;
        private Integer user_ratings_total;
        private String vicinity;

    @Data
    public static class Geometry {
        private Location location;
        private Viewport viewport;
    }

    @Data
    public static class Location {
        private Double lat;
        private Double lng;
    }

    @Data
    public static class Viewport {
        private Location northeast;
        private Location southwest;
    }

    @Data
    public static class OpeningHours {
        private Boolean open_now;
    }

    @Data
    public static class Photo {
        private Integer height;
        private Integer width;
        private String photo_reference;
        private List<String> html_attributions;
    }

    @Data
    public static class PlusCode {
        private String compound_code;
        private String global_code;
    }
}
