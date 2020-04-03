package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.utilities.CsvFlightDataReader;
import io.coderose.ccc2020.utilities.CsvFlightDataReader.Point;
import io.coderose.ccc2020.utilities.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Challenge4 extends AbstractChallenge {

    private FileReader reader;

    @Override
    public void run() {
        for (int f = 1; f <= 5; f++) {
            reader = newReader("level4_" + f + ".in");
//            reader = newReader("level4_example.in");
            File file = new File("level4_" + f + ".out");
            System.out.println(file.getAbsoluteFile());

            reader.setSeparator(" ");

            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                final int numLines = reader.parseInts().get(0);
                for (int l = 0; l < numLines; l++) {
                    final List<Integer> lineData = reader.parseInts();
                    final int flightId = lineData.get(0);
                    final int timestamp = lineData.get(1);

                    final CsvFlightDataReader reader = CsvFlightDataReader.forResource("challenge4", flightId);
                    final List<Point> points = reader.parseAllPoints();

                    final Point result = Interpolate4.interpolate(points, timestamp);
                    w.println(result.lat + " " + result.lon + " " + result.alt);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void main(String[] args) {
        new Challenge4().run();
    }
}
