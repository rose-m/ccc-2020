package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import io.coderose.ccc2020.utilities.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Challenge2 extends AbstractChallenge {

    private FileReader reader;

    @Override
    public void run() {
        reader = newReader("level2_1.in");

            PrintWriter w;
        try {
            File file = new File("level2_1.out");
            System.out.println(file.getAbsoluteFile());
            w = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        final Integer numLines = reader.parseInts().get(0);

        Table<String, String, Integer> flightCounts = TreeBasedTable.create();

        for (int l = 0; l < numLines; l++) {
            final List<String> lineValues = reader.parseStrings();
            String source = lineValues.get(4);
            String target = lineValues.get(5);
            Integer count = flightCounts.get(source, target);
            count = (count == null ? 1 : count + 1);
            flightCounts.put(source, target, count);
        }

        for (Table.Cell<String, String, Integer> cell : flightCounts.cellSet()) {
            w.println(cell.getRowKey() + " " + cell.getColumnKey() + " " + cell.getValue());
        }
        w.close();
    }

    public static void main(String[] args) {
        new Challenge2().run();
    }
}
