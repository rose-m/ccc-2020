package io.coderose.ccc2020.utilities;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.coderose.ccc2020.challenges.FlightKey;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CsvFlightDataReader extends FileReader {

    public final FlightKey route;

    CsvFlightDataReader(List<String> content) {
        super(content);
        route = new FlightKey(parseStrings().get(0), parseStrings().get(0));
    }

    public static CsvFlightDataReader forResource(String challengeName, int flightId) {
        final String resourcePath = "/" + challengeName + "/usedFlights/" + flightId;
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
