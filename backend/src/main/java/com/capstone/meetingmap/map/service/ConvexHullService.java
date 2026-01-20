package com.capstone.meetingmap.map.service;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvexHullService {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    // search=middle-point2에 대한 알고리즘(그라함 스캔(Graham Scan)과 무게 중심)
    public Point calculateConvexHullCentroid(List<Coordinate> coordList) {
        Coordinate[] coords = coordList.toArray(new Coordinate[0]);
        MultiPoint points = geometryFactory.createMultiPointFromCoords(coords);
        ConvexHull convexHull = new ConvexHull(points);
        Geometry hullGeometry = convexHull.getConvexHull();

        if (hullGeometry instanceof Polygon) {
            Polygon hullPolygon = (Polygon) hullGeometry;
            return hullPolygon.getCentroid();
        } else {
            throw new IllegalArgumentException("볼록 껍질이 다각형이 아닙니다.");
        }
    }
}
