package com.m3rc.crf;

import java.io.IOException;

/**
 * Created by Antonello on 17/08/15.
 */
public class Runner {

    public static void main(String[] args) throws IOException {

        new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                "Dataset/tokenizer.praat").inheritIO().start();
        new ProcessBuilder("python", "Dataset/postagger/pSCRDRtagger/EnRDRPOSTagger.py",
                "tag", "../Models/English.RDR",
                "../Dicts/English.DICT", "../../tokens.txt").inheritIO().start();

    }


}
