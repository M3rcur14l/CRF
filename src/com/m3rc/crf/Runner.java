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
                .directory(new File("Dataset/stanfordpostagger")).inheritIO().redirectOutput(new File("Dataset/tokens-tagged.txt")).start();
        System.out.println("Generating postag...");

        System.out.println("Merging postag...");
        PosMerger.mergePosTag();

    }

}
