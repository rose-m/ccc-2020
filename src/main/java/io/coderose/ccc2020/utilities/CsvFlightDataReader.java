package io.coderose.ccc2020.utilities;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.coderose.ccc2020.challenges.FlightKey;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CsvFlightDataReader extends FileReader {

    public final FlightKey route;
    public final int takeOffTimestamp;
    private final int dataLines;

    public static class Point {
        public int offset;
        public int timestamp; // takeOff + offset
        public double lat;
        public double lon;
        public double alt;

        public Point(int offset, int timestamp, double lat, double lon, double alt) {
            this.offset = offset;
            this.timestamp = timestamp;
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
        }
    }

    CsvFlightDataReader(List<String> content) {
        super(content);
        route = new FlightKey(parseStrings().get(0), parseStrings().get(0));
        takeOffTimestamp = parseInts().get(0);
        dataLines = parseInts().get(0);
    }

    public Point parsePoint() {
        final List<Double> values = parseDoubles();
        final int offset = values.get(0).intValue();
        return new Point(
                offset,
                takeOffTimestamp + offset,
                values.get(1),
                values.get(2),
                values.get(3)
        );
    }

    public List<Point> parseAllPoints() {
        gotoLine(4);
        final List<Point> result = new ArrayList<>(dataLines);
        for (int i = 0; i < dataLines; i++) {
            result.add(parsePoint());
        }
        return result;
    }

    public static CsvFlightDataReader forResource(String challengeName, int flightId) {
        final String resourcePath = "/" + challengeName + "/usedFlights/" + flightId + ".csv";
        final URL resource = FileReader.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource " + resourcePath + " doesn't exist");
        }

        final File file = new File(resource.getFile());
        if (!file.exists()) {
            throw new IllegalArgumentException("Resource " + resourcePath + " doesn't exist");
        }

        final List<String> content;
        try {
            content = Files.readLines(file, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("failed to read resource content", e);
        }

        return new CsvFlightDataReader(content);
    }

}
