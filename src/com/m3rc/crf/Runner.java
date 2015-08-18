package com.m3rc.crf;

import java.io.File;
import java.io.IOException;

/**
 * Created by Antonello on 17/08/15.
 */
public class Runner {

    public static void main(String[] args) throws IOException {

        Process tokenization = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                "Dataset/tokenizer.praat").inheritIO().start();

        Process featuresGeneration = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                "Dataset/features_generator.praat").inheritIO().start();

        Process posTagging = new ProcessBuilder("python", "EnRDRPOSTagger.py",
                "tag", "../Models/English.RDR",
                "../Dicts/English.DICT", "../../tokens.txt")
                .directory(new File("Dataset/postagger/pSCRDRtagger")).inheritIO().start();

    }


}
