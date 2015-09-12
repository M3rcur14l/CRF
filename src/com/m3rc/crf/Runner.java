package com.m3rc.crf;

import java.io.File;
import java.io.IOException;

/**
 * Created by Antonello on 17/08/15.
 */
public class Runner {

    final static int NUMBER_OF_ITERATIONS = 200;
    static int NUMBER_OF_CROSS_VALIDATIONS = 5;

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length > 0) {
            if (args.length == 1) {
                if (args[0].matches("[1-9]|10")) {
                    NUMBER_OF_CROSS_VALIDATIONS = Integer.parseInt(args[0]);
                } else if (args[0].equals("-h")) {
                    System.out.println("Dialogue Tagger 2015 by Antonello Fodde\n\n" +
                            "USAGE:[k]\n" +
                            "k\tnumber k in the range [1-10] for the k-fold cross-validation\n");
                    return;
                } else {
                    System.out.println("Bad input. See help (-h) for the usage.");
                    return;
                }
            } else {
                System.out.println("Bad input. See help (-h) for the usage.");
                return;
            }
        }

        Process tokenization = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                "Dataset/tokenizer.praat").inheritIO().start();
        System.out.println("Tokenizing...");

        Process featuresGeneration = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                "Dataset/features_generator.praat").inheritIO().start();
        System.out.println("Generating features...");
        featuresGeneration.waitFor();

        Process posTagging = new ProcessBuilder("java", "-mx300m", "-classpath",
                "stanford-postagger.jar", "edu.stanford.nlp.tagger.maxent.MaxentTagger",
                "-model", "models/english-left3words-distsim.tagger", "-textFile", "../tokens.txt",
                "-sentenceDelimiter", "newline")
                .directory(new File("Dataset/stanfordpostagger"))
                .inheritIO().redirectOutput(new File("Dataset/tokens-tagged.txt")).start();
        System.out.println("Generating postag...");
        posTagging.waitFor();

        PosMerger.mergePosTag();
        System.out.println("Postag merged");

        System.out.println("Training crf and performing " + NUMBER_OF_CROSS_VALIDATIONS + "-fold cross-validation...");
        Process crfTraining = new ProcessBuilder("crfsuite", "learn", "-g" + NUMBER_OF_CROSS_VALIDATIONS,
                "-x", "-p", "max_iterations=" + NUMBER_OF_ITERATIONS, "features-pos.txt")
                .directory(new File("Dataset"))
                .inheritIO().redirectOutput(new File("Dataset/crf-out.txt")).start();
        crfTraining.waitFor();

        System.out.println("Computing accuracy...");
        AccuracyCalculator.writeAccuracyToFile(NUMBER_OF_CROSS_VALIDATIONS, NUMBER_OF_ITERATIONS);
        System.out.println("Done! results in accuracy.txt");
    }

}
