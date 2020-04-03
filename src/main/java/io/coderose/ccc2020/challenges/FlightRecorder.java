package io.coderose.ccc2020.challenges;

import com.google.common.collect.Maps;
import io.coderose.ccc2020.challenges.Challenge3.GeoData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightRecorder {

    FlightKey route;

    int flightId;
    int takeoffTime;

    // first one over 6000;
    int minTimestamp;
    // last one over 6000;
    int maxTimestamp;

    // key is the GLOBAL timestamp
    Map<Integer, GeoData> flightPath = new HashMap<>();

    // key is derived from x coordinates
    Map<Integer, List<GeoData>> partitionX;

    Map<Integer, Map<Integer, List<GeoData>>> partitionY = Maps.newConcurrentMap();
}
