package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.coderose.ccc2020.challenges.Challenge3.GeoData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        if (!timeIntersect(flightA, flightB)) {
//            System.out.println("No time intersection between " + flightA.flightId + " and " + flightB.flightId);
            return;
        }
        if (!pathsIntersect(flightA, flightB, minDist, maxDist)) {
//            System.out.println("No space intersection between " + flightA.flightId + " and " + flightB.flightId);
            return;
        }
//        System.out.println("Check space-time intersection between " + flightA.flightId + " and " + flightB.flightId);

        for (int offset = 0; offset <= 3600; offset++) {
            intersectWithOffset(flightA, flightB, minDist, maxDist, resultAtoB, offset);
        }
        for (int offset = 0; offset <= 3600; offset++) {
            intersectWithOffset(flightB, flightA, minDist, maxDist, resultBtoA, offset);
        }
    }

    private static boolean timeIntersect(FlightRecorder flightA, FlightRecorder flightB) {
        return flightA.maxTimestamp + 3600 >= flightB.minTimestamp && flightB.maxTimestamp + 3600 >= flightA.minTimestamp;
    }

    private static boolean pathsIntersect(FlightRecorder flightA, FlightRecorder flightB, double minDist, double maxDist) {

        // cover the space with slices of width 10_000 + maxDist starting every 10_000
        // collect dataA and dataB into distinct set of slices, and compute dist only within each slice

        Map<Integer, List<GeoData>> partitionAX = getPartitionX(flightA, maxDist);
        Map<Integer, List<GeoData>> partitionBX = getPartitionX(flightB, maxDist);
        for (int sliceX : Sets.union(partitionAX.keySet(), partitionBX.keySet())) {
            List<GeoData> xSliceA = partitionAX.get(sliceX);
            List<GeoData> xSliceB = partitionBX.get(sliceX);
            if (xSliceA == null || xSliceB == null) {
                continue;
            }
            Map<Integer, List<GeoData>> partitionAXY = getPartitionY(maxDist, xSliceA, flightA, sliceX);
            Map<Integer, List<GeoData>> partitionBXY = getPartitionY(maxDist, xSliceB, flightB, sliceX);
            for (int sliceY : Sets.union(partitionAXY.keySet(), partitionBXY.keySet())) {
                List<GeoData> ySliceA = partitionAXY.get(sliceY);
                List<GeoData> ySliceB = partitionBXY.get(sliceY);
                if (ySliceA == null || ySliceB == null) {
                    continue;
                }
                for (GeoData pA : ySliceA) {
                    for (GeoData pB : ySliceB) {
                        if (isInDistBounds(pA, pB, minDist, maxDist)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static Map<Integer, List<GeoData>> getPartitionX(FlightRecorder flight, double maxDist) {
        return flight.partitionX == null ? (flight.partitionX = partitionX(maxDist, flight.flightPath.values())) : flight.partitionX;
    }

    private static Map<Integer, List<GeoData>> getPartitionY(double maxDist, List<GeoData> xSlice, FlightRecorder flight, int sliceX) {
        return flight.partitionY.computeIfAbsent(sliceX, k -> partitionY(maxDist, xSlice));
    }

    private static Map<Integer, List<GeoData>> partitionX(double maxDist, Collection<GeoData> flightPath) {
        return partition(maxDist, flightPath, p -> p.x);
    }

    private static Map<Integer, List<GeoData>> partitionY(double maxDist, Collection<GeoData> flightPath) {
        return partition(maxDist, flightPath, p -> p.y);
    }

    private static Map<Integer, List<GeoData>> partition(double maxDist, Collection<GeoData> flightPath, Function<GeoData, Double> getCoordinate) {
        Map<Integer, List<GeoData>> partition = Maps.newHashMap();
        flightPath.forEach(p -> {
            partition.computeIfAbsent((int) Math.floor(getCoordinate.apply(p) / 10_000), k -> Lists.newArrayList()).add(p);
            partition.computeIfAbsent((int) Math.floor((getCoordinate.apply(p) + maxDist) / 10_000), k -> Lists.newArrayList()).add(p);
        });
        return Collections.unmodifiableMap(partition);
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
                if (isInDistBounds(pA, pB, minDist, maxDist)) {
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

    private static boolean isInDistBounds(GeoData pA, GeoData pB, double minDist, double maxDist) {
        double dx = Math.abs(pA.x - pB.x);
        double dy = Math.abs(pA.y - pB.y);
        double dz = Math.abs(pA.z - pB.z);
        if (dx > maxDist || dy > maxDist || dz > maxDist) {
            return false;
        }
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return dist >= minDist && dist <= maxDist;
    }

}
