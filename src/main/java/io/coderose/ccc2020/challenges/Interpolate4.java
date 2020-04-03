package io.coderose.ccc2020.challenges;

import java.util.List;

import static io.coderose.ccc2020.utilities.CsvFlightDataReader.Point;

public class Interpolate4 {

    static Point interpolate(List<Point> points, int timestamp) {
        Point prev = null, next = null;
        for (Point point : points) {
            if (point.timestamp > timestamp) {
                next = point;
                break;
            }
            prev = point;
        }
        if (prev == null || next == null) {
            throw new IllegalArgumentException("out of bounds");
        }

        double lat = interpolate(prev.timestamp, prev.lat, next.timestamp, next.lat, timestamp);
        double lon = interpolate(prev.timestamp, prev.lon, next.timestamp, next.lon, timestamp);
        double alt = interpolate(prev.timestamp, prev.alt, next.timestamp, next.alt, timestamp);

        return new Point(0, 0, lat, lon, alt);
    }

    private static double interpolate(int x0, double y0, int x1, double y1, int x) {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
    }

}
