package com.capstone.meetingmap.api.google.dto;

import lombok.Data;

import java.util.List;

@Data
public class GooglePlaceDetailResult {
    private List<AddressComponent> address_components;
    private String adr_address;
    private String business_status;
    private OpeningHours current_opening_hours;
    private Boolean dine_in;
    private EditorialSummary editorial_summary;
    private String formatted_address;
    private String formatted_phone_number;
    private Geometry geometry;
    private String icon;
    private String icon_background_color;
    private String icon_mask_base_uri;
    private String international_phone_number;
    private String name;
    private OpeningHours opening_hours;
    private List<Photo> photos;
    private String place_id;
    private PlusCode plus_code;
    private String price_level;
    private String rating;
    private String reference;
    private Boolean reservable;
    private List<Review> reviews;
    private Boolean serves_beer;
    private Boolean serves_breakfast;
    private Boolean serves_brunch;
    private Boolean serves_dinner;
    private Boolean serves_lunch;
    private Boolean serves_wine;
    private Boolean takeout;
    private List<String> types;
    private String url;
    private String user_ratings_total;
    private String utc_offset;
    private String vicinity;
    private String website;
    private Boolean wheelchair_accessible_entrance;

    @Data
    public static class AddressComponent {
        private String long_name;
        private String short_name;
        private List<String> types;
    }

    @Data
    public static class OpeningHours {
        private Boolean open_now;
        private List<Period> periods;
        private List<String> weekday_text;
    }

    @Data
    public static class Period {
        private OpenClose close;
        private OpenClose open;
    }

    @Data
    public static class OpenClose {
        private String date;  // yyyy-MM-dd (current_opening_hours에는 존재)
        private Integer day;
        private String time;  // HHmm
    }

    @Data
    public static class EditorialSummary {
        private String language;
        private String overview;
    }

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
        private LatLng northeast;
        private LatLng southwest;
    }

    @Data
    public static class LatLng {
        private Double lat;
        private Double lng;
    }

    @Data
    public static class Photo {
        private Integer height;
        private List<String> html_attributions;
        private String photo_reference;
        private Integer width;
    }

    @Data
    public static class PlusCode {
        private String compound_code;
        private String global_code;
    }

    @Data
    public static class Review {
        private String author_name;
        private String author_url;
        private String language;
        private String original_language;
        private String profile_photo_url;
        private Integer rating;
        private String relative_time_description;
        private String text;
        private Long time;
        private Boolean translated;
    }
}
