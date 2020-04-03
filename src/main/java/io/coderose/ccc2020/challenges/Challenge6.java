package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.challenges.FlightIntersector.IntervalSet;
import io.coderose.ccc2020.utilities.CsvFlightDataReader;
import io.coderose.ccc2020.utilities.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Challenge6 extends AbstractChallenge {

    private FileReader reader;

    @Override
    public void run() {
        for (int f = 1; f <= 3; f++) {
//            reader = newReader("level6_" + f + ".in");
            reader = newReader("level6_example.in");
            File file = new File("level6_" + f + ".out");
            System.out.println(file.getAbsoluteFile());

            reader.setSeparator(" ");

            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                final List<String> dests = reader.parseStrings();
                final String source = dests.get(0);
                final String dest = dests.get(1);
                final double transferRange = reader.parseDoubles().get(0);
                final int numLines = reader.parseInts().get(0);

                final List<FlightRecorder> flightData = new ArrayList<>(numLines);
//                final Set<Integer> starting

                for (int l = 0; l < numLines; l++) {
                    final List<Integer> lineData = reader.parseInts();
                    final int flightId = lineData.get(0);

                    final CsvFlightDataReader reader = CsvFlightDataReader.forResource(flightId);
                    final FlightRecorder data = new FlightRecorderParser(reader).extract();
                    if (data == null) {
                        continue;
                    }
                    flightData.add(data);
                }

                Map<Integer, Map<Integer, List<IntervalSet>>> sortedResult = new TreeMap<>();
                for (int i = 0; i < flightData.size(); i++) {
                    final FlightRecorder flightA = flightData.get(i);
                    assert flightA != null;
                    assert flightA.route != null;
                    assert flightA.route.destination != null;
                    for (int j = i + 1; j < flightData.size(); j++) {
                        final FlightRecorder flightB = flightData.get(j);
                        assert flightB != null;
                        assert flightB.route != null;
                        assert flightB.route.destination != null;
                        if (flightA.route.destination.equals(flightB.route.destination)) {
                            continue;
                        }

                        List<IntervalSet> resultAtoB = new ArrayList<>();
                        List<IntervalSet> resultBtoA = new ArrayList<>();

                        FlightIntersector.intersect(
                                flightA, flightB,
                                resultAtoB, resultBtoA,
                                1000, transferRange
                        );

                        if (!resultAtoB.isEmpty()) {
                            final Map<Integer, List<IntervalSet>> data = sortedResult.computeIfAbsent(flightA.flightId, k -> new TreeMap<>());
                            data.put(flightB.flightId, resultAtoB);
                        }
                        if (!resultBtoA.isEmpty()) {
                            final Map<Integer, List<IntervalSet>> data = sortedResult.computeIfAbsent(flightB.flightId, k -> new TreeMap<>());
                            data.put(flightA.flightId, resultBtoA);
                        }
                    }
                    System.out.println("Did i: " + i + " of " + flightData.size());
                }

                sortedResult.values().forEach(val -> {
                    val.values().forEach(output -> {
                        output.forEach(w::println);
                    });
                });
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void main(String[] args) {
        new Challenge6().run();
    }
}
