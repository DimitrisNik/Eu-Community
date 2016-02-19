package com.SentimentAnalysis;

import java.util.ArrayList;
/**
 * The class represents the input xml
 */
public class Input {

    /*parameters*/
    private String method;
    private String classifier;
    /*preprocessing*/
    private String posTags;
    private String stemmer;
    private int n_grams;
    /*documents*/
    private ArrayList<com.SentimentAnalysis.InputDocument> documentList;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setPosTags(String posTags) {
        this.posTags = posTags;
    }

    public void setStemmer(String stemmer) {
        this.stemmer = stemmer;
    }

    public void setN_grams(int n_grams) {
        this.n_grams = n_grams;
    }

    public void setDocumentList(ArrayList<com.SentimentAnalysis.InputDocument> documentList) {
        this.documentList = documentList;
    }

    public String getMethod() {
        return method;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getPosTags() {
        return posTags;
    }

    public String getStemmer() {
        return stemmer;
    }

    public int getN_grams() {
        return n_grams;
    }

    public ArrayList<com.SentimentAnalysis.InputDocument> getDocumentList() {
        return documentList;
    }

    @Override
    public String toString() {
        return "Intput{" + "method=" + method + ", classifier=" + classifier + ", posTags=" + posTags + ", stemmer=" + stemmer + ", n_grams=" + n_grams + ", documentList=" + documentList + '}';
    }

}
