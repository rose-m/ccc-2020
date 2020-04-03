package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.utilities.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Challenge3 extends AbstractChallenge {

    private FileReader reader;

    static class PointData {
        public PointData(double lat, double lon, double alt) {
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
        }

        double lat;
        double lon;
        double alt;

        @Override
        public String toString() {
            return "PointData{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    ", alt=" + alt +
                    '}';
        }
    }

    static class GeoData {
        public GeoData(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        double x;
        double y;
        double z;

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    @Override
    public void run() {
        for (int f = 1; f <= 5; f++) {
            reader = newReader("level3_" + f + ".in");
//            reader = newReader("level3_example.in");
            File file = new File("level3_" + f + ".out");
            System.out.println(file.getAbsoluteFile());

            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                final int numLines = reader.parseInts().get(0);
                for (int l = 0; l < numLines; l++) {
                    final List<Double> values = reader.parseDoubles();
                    final PointData data = new PointData(values.get(0) / 180 * Math.PI, values.get(1) / 180 * Math.PI, values.get(2));

                    final GeoData geoData = convertCoordinates(data);
                    w.println(geoData);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static GeoData convertCoordinates(PointData data) {
        double a = 6371 * 1000, b = a;
        double e = 1 - (b * b) / (a * a);
        double N_lat = a / Math.sqrt(1 - e * e * Math.sin(data.lat) * Math.sin(data.lat));
        double x = (N_lat + data.alt) * Math.cos(data.lat) * Math.cos(data.lon);
        double y = (N_lat + data.alt) * Math.cos(data.lat) * Math.sin(data.lon);
        double z = (b * b / (a * a) * N_lat + data.alt) * Math.sin(data.lat);

        return new GeoData(x, y, z);
    }

    public static void main(String[] args) {
        new Challenge3().run();
    }
}
