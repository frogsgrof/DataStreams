import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class SearchStream {

    File file;
    String search;

    public SearchStream(SearchManager searchManager) {
        file = searchManager.getFile();
        search = searchManager.getSearch();
    }

    /**
     * Searches this instance's {@link #file} for any lines that contain this instance's {@link #search}. Appends
     * "On line n:" to the beginning and a line break at the end of each String in the list, for displaying search
     * results in a JTextArea.
     * @return list of every line of a file that matches query
     */
    public List<String> search() {
        if (file == null || !file.exists() || search == null || search.isEmpty()) return null;
        List<String> list;
        try (Stream<String> stream = Files.lines(file.toPath(), Charset.defaultCharset())
                .filter(s -> !s.isEmpty())) {
            List<String> lines = stream.toList();
            list = lines.stream().filter(s -> s.contains(search)).map(s -> "On line " +
                    (lines.indexOf(s) + 1) + ":\n\t" + s + "\n").toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Uses a {@link Stream} object to read this instance's {@link #file} field into a List of Strings for use in
     * JTextAreas. Ignores blank lines and appends "n:" to the beginning of each line and a line break between
     * each line.
     * @return list of each line of a text file
     */
    public List<String> readFile() {
        if (file == null || !file.exists()) return null;
        List<String> list;
        try (Stream<String> stream = Files.lines(file.toPath(), Charset.defaultCharset())
                .filter(s -> !s.isEmpty())) {
            List<String> lines = stream.toList();
            list = lines.stream().map(s -> (lines.indexOf(s) + 1) + ":\t" + s + '\n').toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
