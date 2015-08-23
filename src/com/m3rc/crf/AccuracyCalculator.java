package com.m3rc.crf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Antonello on 22/08/15.
 */
public class AccuracyCalculator {

    final static String CRF_OUTPUT = "crf-out.txt";
    final static String ACCURACY_OUTPUT = "accuracy.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void writeAccuracyToFile(int numOfCross, int numOfIteration) throws IOException {
        Path crfOutput = Paths.get(CRF_OUTPUT);
        Path accuracyOutput = Paths.get(ACCURACY_OUTPUT);

        try (Scanner crfOutputScanner = new Scanner(crfOutput, ENCODING.name());
             BufferedWriter accuracyOutputWriter = Files.newBufferedWriter(accuracyOutput, ENCODING)) {
            int cross = 1;
            int iter = 1;
            float accuracyMean = 0f;
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            accuracyOutputWriter.write("===== Cross validation (" + cross + "/" + numOfCross + ") =====");
            accuracyOutputWriter.newLine();
            while (crfOutputScanner.hasNextLine() && cross != numOfCross) {
                String crfOutputLine = crfOutputScanner.nextLine();
                if (iter == numOfIteration) {
                    cross++;
                    accuracyOutputWriter.write("Accuracy = " + accuracyMean);
                    accuracyOutputWriter.newLine();
                    accuracyOutputWriter.write("===== Cross validation (" + cross + "/" + numOfCross + ") =====");
                    accuracyOutputWriter.newLine();
                    accuracyMean = 0f;
                    iter = 1;
                }
                if (crfOutputLine.contains("Item accuracy")) {
                    Matcher matcher = pattern.matcher(crfOutputLine);
                    if (matcher.find()) {
                        float accuracy = Float.parseFloat(matcher.group(1));
                        accuracyMean = accuracyMean + ((accuracy - accuracyMean) / iter);
                        iter++;
                    }
                }
            }
        }
    }
}
