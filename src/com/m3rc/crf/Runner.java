package com.m3rc.crf;

import java.io.File;
import java.io.IOException;

/**
 * Created by Antonello on 17/08/15.
 */
public class Runner {

    public static void main(String[] args) throws IOException, InterruptedException {

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

        System.out.println("Training crf and performing cross-validation...");
        Process crfTraining = new ProcessBuilder("crfsuite", "learn", "-g10",
                "-x", "-p", "max_iterations=10", "features-pos.txt")
                .directory(new File("Dataset"))
                .inheritIO().redirectOutput(new File("crf-out.txt")).start();
        crfTraining.waitFor();
        System.out.println("Done! results in crf-out.txt");

    }

}
