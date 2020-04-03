package io.coderose.ccc2020.utilities;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileReader {

    private final List<String> content;
    private int currentLine = 0;
    private String separator = ",";

    private FileReader(List<String> content) {
        this.content = content;
    }

    public static FileReader forResource(@Nonnull String challengeName, @Nonnull String filename) {
        final String resourcePath = "/" + challengeName + "/" + filename;
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

        return new FileReader(content);
    }

    @Nonnull
    public <T> T extract(LineExtractor<T> extractor) {
        return extractor.extract(this);
    }

    /**
     * Sets current line index to the given line (0-based)
     */
    public FileReader gotoLine(int line) {
        if (line < 0 || line > content.size() - 1) {
            throw new IndexOutOfBoundsException("line is out of bounds");
        }
        currentLine = line;
        return this;
    }

    /**
     * Sets the current separator to use for parsing between entries
     */
    public FileReader setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public String getAndAdvanceLine() {
        return content.get(currentLine++);
    }

    /**
     * Parses the current line into strings
     */
    public List<String> parseStrings() {
        return Arrays.stream(getAndAdvanceLine().split(this.separator))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Parses the current line into integers
     */
    public List<Integer> parseInts() {
        return parseStrings().stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public List<Double> parseDoubles() {
        return parseStrings().stream()
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

}
