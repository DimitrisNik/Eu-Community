
package com.SentimentAnalysis;

import java.util.ArrayList;
/**
 * The class represents the output of this process
 */
public class Output {
    
    private String idDocument;
    private ArrayList<SentenceOutput> sentencesResult;

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    public void setSentencesResult(ArrayList<SentenceOutput> sentencesResult) {
        this.sentencesResult = sentencesResult;
    }

    public String getIdDocument() {
        return idDocument;
    }

    public ArrayList<SentenceOutput> getSentencesResult() {
        return sentencesResult;
    }

    
    
    
    
}
