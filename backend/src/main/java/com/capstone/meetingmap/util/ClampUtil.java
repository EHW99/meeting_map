package com.capstone.meetingmap.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ClampUtil {
    public static Point clampPoint(Point point) {
        // 서울 위도/경도 경계값
        double minLat = 37.413294;
        double maxLat = 37.715133;
        double minLon = 126.734086;
        double maxLon = 127.269311;

        // 중간 위치를 가장 가까운 서울 내부 위치로 클램핑(경계 조정)
        double clampedX = Math.min(Math.max(point.getX(), minLon), maxLon);
        double clampedY = Math.min(Math.max(point.getY(), minLat), maxLat);

        return new GeometryFactory().createPoint(new Coordinate(clampedX, clampedY));
    }
}
