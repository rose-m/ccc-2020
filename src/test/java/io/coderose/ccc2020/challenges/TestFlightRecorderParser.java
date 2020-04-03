package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import io.coderose.ccc2020.utilities.CsvFlightDataReader.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFlightRecorderParser {

    @Test
    public void testInterpolation() {
        final FlightRecorderParser parser = new FlightRecorderParser(null);
        final List<Point> points = parser.computeInterpolatedPoints(Lists.newArrayList(
                new Point(0, 0, 0, 50, 150),
                new Point(5, 5, 50, 100, 200)
        ), 0);

        assertEquals(6, points.size());
        assertEquals(Lists.newArrayList(
                new Point(0, 0, 0, 50, 150),
                new Point(1, 1, 10, 60, 160),
                new Point(2, 2, 20, 70, 170),
                new Point(3, 3, 30, 80, 180),
                new Point(4, 4, 40, 90, 190),
                new Point(5, 5, 50, 100, 200)
        ), points);
    }
}
