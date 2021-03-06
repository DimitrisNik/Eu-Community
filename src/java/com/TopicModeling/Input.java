package com.TopicModeling;

import java.util.ArrayList;
/**
 * The class represents the input xml
 */
public class Input {
    private int numTopics;
    private int numIterations;
    private int optimization;
    private int burninInterval;
    private String posTags;
    private String stemmer;
    private int ngrams;
    /*documents*/
    private ArrayList<InputDocument> documentList;

    public void setNumTopics(int numTopics) {
        this.numTopics = numTopics;
    }

    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

    public void setOptimization(int optimization) {
        this.optimization = optimization;
    }

    public void setBurninInterval(int burninInterval) {
        this.burninInterval = burninInterval;
    }

    public void setPosTags(String posTags) {
        this.posTags = posTags;
    }

    public void setStemmer(String stemmer) {
        this.stemmer = stemmer;
    }

    public void setNgrams(int ngrams) {
        this.ngrams = ngrams;
    }

    public void setDocumentList(ArrayList<InputDocument> documentList) {
        this.documentList = documentList;
    }

    public int getNumTopics() {
        return numTopics;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public int getOptimization() {
        return optimization;
    }

    public int getBurninInterval() {
        return burninInterval;
    }

    public String getPosTags() {
        return posTags;
    }

    public String getStemmer() {
        return stemmer;
    }

    public int getNgrams() {
        return ngrams;
    }

    public ArrayList<InputDocument> getDocumentList() {
        return documentList;
    }

    @Override
    public String toString() {
        return "Input{" + "numTopics=" + numTopics + ", numIterations=" + numIterations + ", optimization=" + optimization + ", burninInterval=" + burninInterval + ", posTags=" + posTags + ", stemmer=" + stemmer + ", ngrams=" + ngrams + ", documentList=" + documentList + '}';
    }

}
