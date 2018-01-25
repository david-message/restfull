package com.xxx.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            StanfordCoreNLP.main(new String[]{"-outputFormat","json", "-file","input.txt"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
