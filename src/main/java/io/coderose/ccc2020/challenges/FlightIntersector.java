package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import io.coderose.ccc2020.challenges.Challenge3.GeoData;

import java.util.List;
import java.util.stream.Collectors;

public class FlightIntersector {

    public static final int NOT_STARTED = Integer.MIN_VALUE;

    static class Interval {
        int start;
        int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return start == end ? "" + start : start + "-" + end;
        }
    }

    public static List<String> intersect(FlightRecorder flightA, FlightRecorder flightB, double minDist, double maxDist) {
        List<String> results = Lists.newArrayList();

        for (int offset = 0; offset <= 3600; offset++) {
            intersectWithOffset(flightA, flightB, minDist, maxDist, results, offset);
        }
        for (int offset = 1; offset <= 3600; offset++) {
            intersectWithOffset(flightB, flightA, minDist, maxDist, results, offset);
        }

        return results;
    }

    private static void intersectWithOffset(FlightRecorder flightA, FlightRecorder flightB, double minDist, double maxDist, List<String> results, int offset) {
        int firstTimestamp = Math.max(flightA.minTimestamp, flightB.minTimestamp + offset);
        int lastTimestamp = Math.min(flightA.maxTimestamp, flightB.maxTimestamp + offset);
        if (firstTimestamp > lastTimestamp) {
            // no time intersection
            return;
        }

        List<Interval> timestamps = Lists.newArrayList();
        int lastStart = NOT_STARTED;

        for (int timestamp = firstTimestamp; timestamp <= lastTimestamp; timestamp++) {
            GeoData pA = flightA.flightPath.get(timestamp);
            GeoData pB = flightB.flightPath.get(timestamp - offset);
            boolean intersects = false;
            if (pA != null && pB != null) {
                double dist = dist(pA, pB);
                if (dist >= minDist && dist <= maxDist) {
                    intersects = true;
                }
            }
            if (intersects) {
                if (lastStart == NOT_STARTED) {
                    lastStart = timestamp;
                }
            } else {
                if (lastStart != NOT_STARTED) {
                    timestamps.add(new Interval(lastStart, timestamp - 1));
                    lastStart = NOT_STARTED;
                }
            }
        }
        if (lastStart != NOT_STARTED) {
            timestamps.add(new Interval(lastStart, lastTimestamp));
        }

        if (!timestamps.isEmpty()) {
            results.add(flightA.flightId + " " + flightB.flightId + " " + offset + " " + timestamps.stream().map(Object::toString).collect(Collectors.joining(" ")));
        }
    }

    private static double dist(GeoData pA, GeoData pB) {
        double dx = pA.x - pB.x;
        double dy = pA.y - pB.y;
        double dz = pA.z - pB.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
