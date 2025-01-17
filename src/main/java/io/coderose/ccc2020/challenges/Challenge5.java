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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Challenge5 extends AbstractChallenge {


    private FileReader reader;

    @Override
    public void run() {
        for (int f = 1; f <= 5; f++) {
            long startTime = System.currentTimeMillis();
            reader = newReader("level5_" + f + ".in");
//            reader = newReader("level5_example.in");
            File file = new File("level5_" + f + ".out");
            System.out.println(file.getAbsoluteFile());

            reader.setSeparator(" ");

            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                final double transferRange = reader.parseDoubles().get(0);
                final int numLines = reader.parseInts().get(0);

                final List<FlightRecorder> flightData = new ArrayList<>(numLines);

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
                AtomicInteger progress = new AtomicInteger(0);
                IntStream.range(0, flightData.size()).parallel().forEach(i -> {
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
                            synchronized (sortedResult) {
                                final Map<Integer, List<IntervalSet>> data = sortedResult.computeIfAbsent(flightA.flightId, k -> new TreeMap<>());
                                data.put(flightB.flightId, resultAtoB);
                            }
                        }
                        if (!resultBtoA.isEmpty()) {
                            synchronized (sortedResult) {
                                final Map<Integer, List<IntervalSet>> data = sortedResult.computeIfAbsent(flightB.flightId, k -> new TreeMap<>());
                                data.put(flightA.flightId, resultBtoA);
                            }
                        }
                    }
                    System.out.println("Did " + (progress.incrementAndGet()) + " of " + flightData.size() + " after " + (System.currentTimeMillis() - startTime) / 1000. + " sec");
                });

                sortedResult.values().forEach(val -> {
                    val.values().forEach(output -> {
                        output.forEach(w::println);
                    });
                });
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            long stopTime = System.currentTimeMillis();
            System.out.println((stopTime - startTime) / 1000. + " sec");
        }
    }

    public static void main(String[] args) {
        new Challenge5().run();
    }
}
