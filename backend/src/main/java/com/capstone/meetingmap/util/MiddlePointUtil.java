package com.capstone.meetingmap.util;

import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.map.dto.XYCoordinate;
import com.capstone.meetingmap.map.dto.XYDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

public class MiddlePointUtil {
    public static XYDto getMiddlePoint(List<PointCoord> pointCoordList) {
        // 각 주소에 대한 x, y 좌표를 저장할 리스트
        List<Double> xCoordinates = new ArrayList<>();
        List<Double> yCoordinates = new ArrayList<>();
        List<XYCoordinate> coordinates = new ArrayList<>();

        // 각 주소에 대해 좌표 검색 후 리스트에 저장
        for (PointCoord pointCoord : pointCoordList) {
            Double x = Double.parseDouble(pointCoord.getLon());
            Double y = Double.parseDouble(pointCoord.getLat());

            xCoordinates.add(x);
            yCoordinates.add(y);
            coordinates.add(new XYCoordinate(String.valueOf(x), String.valueOf(y)));
        }

        // x와 y의 평균을 계산하여 중간점 구하기
        double middleX = xCoordinates.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double middleY = yCoordinates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        // 좌표 클램핑(경계 조정)
        Point clampedMiddlePoint = ClampUtil.clampPoint(new GeometryFactory().createPoint(new Coordinate(middleX, middleY)));

        // XYDto 반환
        return XYDto.builder()
                .coordinates(coordinates)
                .middleX(clampedMiddlePoint.getX())
                .middleY(clampedMiddlePoint.getY())
                .build();
    }
}
