package io.coderose.ccc2020.challenges;

import com.google.common.collect.Lists;
import io.coderose.ccc2020.utilities.FileReader;

import java.util.List;

public class Challenge1 extends AbstractChallenge {

    private FileReader reader;

    @Override
    public void run() {
        reader = newReader("level1_5.in");

        final List<Integer> count = reader.parseInts();
        final Integer numLines = count.get(0);

        Integer minTimestamp = null;
        Integer maxTimestamp = null;
        Double minLat = null;
        Double maxLat = null;
        Double minLon = null;
        Double maxLon = null;
        Double maxAlt = null;

        final List<Integer> timestamps = Lists.newArrayList();
        final List<Double> lats = Lists.newArrayList();
        final List<Double> lons = Lists.newArrayList();
        final List<Double> alts = Lists.newArrayList();


        for (int l = 0; l < numLines; l++) {
            final List<Double> lineValues = reader.parseDoubles();
            timestamps.add(lineValues.get(0).intValue());

            lats.add(lineValues.get(1));
            lons.add(lineValues.get(2));
            alts.add(lineValues.get(3));
        }

        minTimestamp = timestamps.stream().mapToInt(Integer::intValue).min().getAsInt();
        maxTimestamp = timestamps.stream().mapToInt(Integer::intValue).max().getAsInt();

        minLat = lats.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        maxLat = lats.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

        minLon = lons.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        maxLon = lons.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

        maxAlt = alts.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

        System.out.println(minTimestamp + " " + maxTimestamp);
        System.out.println(minLat + " " + maxLat);
        System.out.println(minLon + " " + maxLon);
        System.out.println(maxAlt);
    }

    public static void main(String[] args) {
        new Challenge1().run();
    }
}
