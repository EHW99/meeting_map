package com.capstone.meetingmap.util;

public class AddressUtil {

    private static final String ADDRESS_REGEX =
            "^.*[가-힣]+\\s+[가-힣]+(시|군|구)\\s+.*(동|읍|면|길|로|산).*\\d+(-\\d+)?\\s*$";

    public static boolean isAddressQuery(String input) {
        if (input == null) return false;
        if (!input.matches(".*[가-힣]+.*")) return false; // 한글 포함 필수

        return input.matches(ADDRESS_REGEX);
    }
}
