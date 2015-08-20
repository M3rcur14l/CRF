package com.m3rc.crf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Antonello on 20/08/15.
 */
public class PosMerger {

    final static String POS_TAGGED_PATH = "Dataset/features.txt";
    final static String FEATURES_PATH = "Dataset/tokens-tagged.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;


    List<String> readTaggedTextFile() throws IOException {
        Path path = Paths.get(POS_TAGGED_PATH);
        return Files.readAllLines(path, ENCODING);
    }

    void writeFeaturesTextFile(List<String> aLines) throws IOException {
        Path path = Paths.get(FEATURES_PATH);
        Files.write(path, aLines, ENCODING);
    }

    void mergePostag() {

    }

    void readLargerTextFile(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        try (Scanner scanner = new Scanner(path, ENCODING.name())) {
            while (scanner.hasNextLine()) {
                //process each line in some way
                log(scanner.nextLine());
            }
        }
    }

    void readLargerTextFileAlternate(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //process each line in some way
                log(line);
            }
        }
    }

    void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
        Path path = Paths.get(aFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
            for (String line : aLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static void log(Object aMsg) {
        System.out.println(String.valueOf(aMsg));
    }

}
