package io.coderose.ccc2020.challenges;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlightIntersectorTest {

    @Test
    public void test1() {
        FlightRecorder flightA = new FlightRecorder();
        flightA.flightId = 42;
        flightA.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(1, new Challenge3.GeoData(10, 1, 1))
                .put(2, new Challenge3.GeoData(20, 1, 1))
                .put(4, new Challenge3.GeoData(30, 1, 1))
                .build();

        FlightRecorder flightB = new FlightRecorder();
        flightB.flightId = 43;
        flightB.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(1, new Challenge3.GeoData(20, 10, 1))
                .put(2, new Challenge3.GeoData(20, 1, 1))
                .put(3, new Challenge3.GeoData(20, 10, 1))
                .build();

        flightA.minTimestamp = 1;
        flightA.maxTimestamp = 3;

        flightB.minTimestamp = 1;
        flightB.maxTimestamp = 3;

        final ArrayList<String> aToB = Lists.newArrayList();
        final ArrayList<String> bToA = Lists.newArrayList();
        FlightIntersector.intersect(
                flightA, flightB,
                aToB, bToA,
                0, 1
        );

        assertEquals(aToB, ImmutableList.of("42 43 0 2"));
    }

    @Test
    public void test2() {
        FlightRecorder flightA = new FlightRecorder();
        flightA.flightId = 42;
        flightA.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(1, new Challenge3.GeoData(10, 1, 1))
                .put(2, new Challenge3.GeoData(20, 1, 1))
                .put(4, new Challenge3.GeoData(30, 1, 1))
                .put(5, new Challenge3.GeoData(30, 2, 1))
                .build();

        FlightRecorder flightB = new FlightRecorder();
        flightB.flightId = 43;
        flightB.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(1, new Challenge3.GeoData(20, 10, 1))
                .put(2, new Challenge3.GeoData(20, 1, 1))
                .put(3, new Challenge3.GeoData(20, 10, 1))
                .put(4, new Challenge3.GeoData(30, 1, 1))
                .put(5, new Challenge3.GeoData(30, 2, 1))
                .build();

        flightA.minTimestamp = 1;
        flightA.maxTimestamp = 5;

        flightB.minTimestamp = 1;
        flightB.maxTimestamp = 5;

        final ArrayList<String> aToB = Lists.newArrayList();
        final ArrayList<String> bToA = Lists.newArrayList();
        FlightIntersector.intersect(
                flightA, flightB,
                aToB, bToA,
                0, 1
        );

        assertEquals(ImmutableList.of("42 43 0 2 4-5", "42 43 1 5"), aToB);
        assertEquals(ImmutableList.of("43 42 1 5"), bToA);
    }

    @Test
    public void test3() {
        FlightRecorder flightA = new FlightRecorder();
        flightA.flightId = 42;
        flightA.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(1, new Challenge3.GeoData(10, 10, 1))
                .put(2, new Challenge3.GeoData(11, 10, 1))
                .put(4, new Challenge3.GeoData(12, 10, 1))
                .put(5, new Challenge3.GeoData(13, 10, 1))
                .build();

        FlightRecorder flightB = new FlightRecorder();
        flightB.flightId = 43;
        flightB.flightPath = ImmutableMap.<Integer, Challenge3.GeoData>builder()
                .put(11, new Challenge3.GeoData(10, 10, 1))
                .put(12, new Challenge3.GeoData(10, 11, 1))
                .put(13, new Challenge3.GeoData(10, 12, 1))
                .put(14, new Challenge3.GeoData(10, 13, 1))
                .put(15, new Challenge3.GeoData(10, 14, 1))
                .build();

        flightA.minTimestamp = 1;
        flightA.maxTimestamp = 5;

        flightB.minTimestamp = 11;
        flightB.maxTimestamp = 15;

        final ArrayList<String> aToB = Lists.newArrayList();
        final ArrayList<String> bToA = Lists.newArrayList();
        FlightIntersector.intersect(
                flightA, flightB,
                aToB, bToA,
                0, 10
        );

        assertEquals(0, aToB.size());
        assertEquals(Arrays.asList("43 42 6 11, 43 42 7 11-12, 43 42 8 12-13, 43 42 9 11 13-14, 43 42 10 11-12 14-15, 43 42 11 12-13 15, 43 42 12 13-14, 43 42 13 14-15, 43 42 14 15".split(", ")), bToA);
    }
}
