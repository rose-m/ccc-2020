package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import io.coderose.ccc2020.challenges.Challenge3.GeoData;

import java.util.Collection;
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

    static class IntervalSet {
        int flightAId;
        int flightBId;
        int flightBDelay;
        List<Interval> intervals;

        @Override
        public String toString() {
            return flightAId + " " + flightBId + " " + flightBDelay + " " + intervals.stream().map(Object::toString).collect(Collectors.joining(" "));
        }
    }

    public static void intersect(
            FlightRecorder flightA, FlightRecorder flightB,
            List<IntervalSet> resultAtoB, List<IntervalSet> resultBtoA,
            double minDist, double maxDist
    ) {
//        if (!pathsIntersect(flightA, flightB, minDist, maxDist)) {
//            return;
//        }

        for (int offset = 0; offset <= 3600; offset++) {
            intersectWithOffset(flightA, flightB, minDist, maxDist, resultAtoB, offset);
        }
        for (int offset = 0; offset <= 3600; offset++) {
            intersectWithOffset(flightB, flightA, minDist, maxDist, resultBtoA, offset);
        }
    }

    private static boolean pathsIntersect(FlightRecorder flightA, FlightRecorder flightB, double minDist, double maxDist) {
        Collection<GeoData> dataA = flightA.flightPath.values();
        Collection<GeoData> dataB = flightB.flightPath.values();

        double minX = dataA.stream().mapToDouble(p -> p.x).min().getAsDouble();
        double maxX = dataA.stream().mapToDouble(p -> p.x).max().getAsDouble();
        // TODO partition the space into 10_000 slices of width 10_000 + maxDist
        // collect dataA and dataB into distinct set of slices, and compute dist only within each slice

        boolean intersects = false;
        for (GeoData pA : dataA) {
            for (GeoData pB : dataB) {
                double dist = dist(pA, pB);
                if (dist >= minDist && dist <= maxDist) {
                    intersects = true;
                    break;
                }
            }
        }
        return intersects;
    }

    private static void intersectWithOffset(FlightRecorder flightA, FlightRecorder flightB, double minDist, double maxDist, List<IntervalSet> results, int offset) {
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
            IntervalSet intervals = new IntervalSet();
            intervals.flightAId = flightA.flightId;
            intervals.flightBId = flightB.flightId;
            intervals.flightBDelay = offset;
            intervals.intervals = timestamps;
            results.add(intervals);
        }
    }

    private static double dist(GeoData pA, GeoData pB) {
        double dx = pA.x - pB.x;
        double dy = pA.y - pB.y;
        double dz = pA.z - pB.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
