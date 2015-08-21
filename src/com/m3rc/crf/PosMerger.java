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

    final static String POS_TAGGED_PATH = "Dataset/tokens-tagged.txt";
    final static String FEATURES_PATH = "Dataset/features.txt";
    final static String FEATURES_WITH_POS_PATH = "Dataset/features-pos.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;


    private List<String> readTaggedTextFile() throws IOException {
        Path path = Paths.get(POS_TAGGED_PATH);
        return Files.readAllLines(path, ENCODING);
    }

    private void writeFeaturesTextFile(List<String> aLines) throws IOException {
        Path path = Paths.get(FEATURES_PATH);
        Files.write(path, aLines, ENCODING);
    }


    public static void mergePosTag() throws IOException {
        Path posPath = Paths.get(POS_TAGGED_PATH);
        Path featuresPath = Paths.get(FEATURES_PATH);
        Path featuresPosPath = Paths.get(FEATURES_WITH_POS_PATH);

        try (Scanner posScanner = new Scanner(posPath, ENCODING.name());
             Scanner featuresScanner = new Scanner(featuresPath, ENCODING.name());
             BufferedWriter featuresPosWriter = Files.newBufferedWriter(featuresPosPath, ENCODING)) {
            featuresScanner.nextLine();
            while (posScanner.hasNextLine()) {
                String posLine = posScanner.nextLine();
                String featuresLine = featuresScanner.nextLine();
                String[] splittedPosLine = posLine.split(" ");

                for (String taggedWord : splittedPosLine) {
                    String pos = taggedWord.split("_")[1];
                    featuresLine += "\t" + "pos=" + pos;
                    featuresPosWriter.write(featuresLine);
                    featuresPosWriter.newLine();
                    featuresLine = featuresScanner.nextLine();
                }
                featuresPosWriter.newLine();
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
