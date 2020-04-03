package io.coderose.ccc2020.challenges;

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import io.coderose.ccc2020.utilities.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class Challenge2 extends AbstractChallenge {

    private FileReader reader;

    static class FlightData {
        String source;
        String target;
        int takeoff;
    }

    @Override
    public void run() {
        for (int f = 1; f <= 5; f++) {
            reader = newReader("level2_" + f + ".in");
            File file = new File("level2_" + f + ".out");
            System.out.println(file.getAbsoluteFile());

            try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
                final Integer numLines = reader.parseInts().get(0);

                // table of source/target/set<timestamp>
                Table<String, String, Set<Integer>> flightCounts = TreeBasedTable.create();

                for (int l = 0; l < numLines; l++) {
                    final List<String> lineValues = reader.parseStrings();
                    String source = lineValues.get(4);
                    String target = lineValues.get(5);
                    int timestamp = Integer.parseInt(lineValues.get(6));
                    Set<Integer> count = flightCounts.get(source, target);
                    if (count == null) {
                        count = Sets.newHashSet();
                        flightCounts.put(source, target, count);
                    }
                    count.add(timestamp);
                }

                for (Table.Cell<String, String, Set<Integer>> cell : flightCounts.cellSet()) {
                    w.println(cell.getRowKey() + " " + cell.getColumnKey() + " " + cell.getValue().size());
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void main(String[] args) {
        new Challenge2().run();
    }
}
