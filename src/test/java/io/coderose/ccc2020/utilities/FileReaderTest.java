package io.coderose.ccc2020.utilities;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {

    @Test
    public void loadingAResourceWorks() {
        final FileReader reader = FileReader.forResource("testChallenge1", "file.txt");
        assertNotNull(reader);
    }

    @Test
    public void LoadingNonExistentResourceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                FileReader.forResource("IdontExist", "file.txt")
        );
    }

    @Test
    public void parsingLinesWorksCorrectly() {
        final FileReader reader = FileReader.forResource("testChallenge1", "file.txt");

        final List<String> strings = reader.parseStrings();
        assertEquals(strings, Lists.newArrayList("these", "are", "strings"));

        final List<Integer> integers = reader.parseInts();
        assertEquals(integers, Lists.newArrayList(1, 2, 3, 4));

        reader.getAndAdvanceLine();

        final List<Double> doubles = reader.parseDoubles();
        assertEquals(doubles, Lists.newArrayList(1.0, 2.34, 223.0));

        final List<String> strings2 = reader.setSeparator(";").parseStrings();
        assertEquals(strings2, Lists.newArrayList("other", "strings"));
    }
}
