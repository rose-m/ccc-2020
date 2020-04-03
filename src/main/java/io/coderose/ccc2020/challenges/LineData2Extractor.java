package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.utilities.FileReader;
import io.coderose.ccc2020.utilities.LineExtractor;

import javax.annotation.Nonnull;
import java.util.List;

public class LineData2Extractor implements LineExtractor<LineData2> {
    @Nonnull
    @Override
    public LineData2 extract(@Nonnull FileReader reader) {
        final LineData2 result = new LineData2();
        final List<String> strings = reader.parseStrings();
        result.timestamp = Integer.parseInt(strings.get(0));
        result.latitude = Double.parseDouble(strings.get(1));
        result.longtitude = Double.parseDouble(strings.get(2));
        result.altitude = Double.parseDouble(strings.get(3));
        result.source = strings.get(4);
        result.destination = strings.get(5);
        result.takeoff = Integer.parseInt(strings.get(6));
        return result;
    }
}
