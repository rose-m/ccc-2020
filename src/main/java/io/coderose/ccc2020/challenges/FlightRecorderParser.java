package io.coderose.ccc2020.challenges;

import com.google.common.base.Preconditions;
import io.coderose.ccc2020.challenges.Challenge3.GeoData;
import io.coderose.ccc2020.utilities.CsvFlightDataReader;
import io.coderose.ccc2020.utilities.CsvFlightDataReader.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.coderose.ccc2020.challenges.Interpolate4.interpolate;

public class FlightRecorderParser {

    private final CsvFlightDataReader csvReader;

    public FlightRecorderParser(CsvFlightDataReader csvReader) {
        this.csvReader = csvReader;
    }

    public FlightRecorder extract() {
        final List<Point> above6000 = computeInterpolatedPoints(csvReader.parseAllPoints(), csvReader.takeOffTimestamp)
                .stream()
                .filter(p -> p.alt > 6000)
                .collect(Collectors.toList());
        if (above6000.isEmpty()) {
            return null;
        }

        final int firstAbove = above6000.get(0).timestamp;
        final int lastAbove = above6000.get(above6000.size() - 1).timestamp;

        final FlightRecorder result = new FlightRecorder();
        result.route = Preconditions.checkNotNull(csvReader.route);
        result.flightId = csvReader.flightId;
        result.takeoffTime = csvReader.takeOffTimestamp;
        result.minTimestamp = firstAbove;
        result.maxTimestamp = lastAbove;
        result.flightPath = above6000.stream().collect(Collectors.toMap(
                p -> p.timestamp,
                FlightRecorderParser::convertCoordinates
        ));
        return result;
    }

    List<Point> computeInterpolatedPoints(List<Point> points, int takeOffTimestamp) {
        final int maxTimestamp = points.get(points.size() - 1).timestamp;

        final List<Point> interpolated = new ArrayList<>(maxTimestamp - takeOffTimestamp + 1);
        Point last = points.get(0);
        assert last.timestamp == takeOffTimestamp;
        Point next = points.get(1);
        int p = 2;

        interpolated.add(last);
        for (int t = takeOffTimestamp + 1; t <= maxTimestamp; t++) {
            if (next.timestamp < t) {
                // last ---- next -!
                last = next;
                next = points.get(p++);
            }

            if (next.timestamp == t) {
                // last ---- !next
                interpolated.add(next);
            } else {
                // last -!---  next
                interpolated.add(interpolate(last, next, t));
            }
        }

        return interpolated;
    }

    public static GeoData convertCoordinates(Point data) {
        final double lat = data.lat / 180 * Math.PI;
        final double lon = data.lon / 180 * Math.PI;

        double a = 6371 * 1000, b = a;
        double e = 1 - (b * b) / (a * a);
        double N_lat = a / Math.sqrt(1 - e * e * Math.sin(lat) * Math.sin(lat));
        double x = (N_lat + data.alt) * Math.cos(lat) * Math.cos(lon);
        double y = (N_lat + data.alt) * Math.cos(lat) * Math.sin(lon);
        double z = (b * b / (a * a) * N_lat + data.alt) * Math.sin(lat);
        return new GeoData(x, y, z);
    }
}
