package com.SentimentAnalysis;

/**
 * The class represents the result of a sentence
 */
public class SentenceOutput {

    private String idSentence;
    private String text;
    private String label;
    private double confidence;
    
    public void setIdSentence(String idSentence) {
        this.idSentence = idSentence;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void setLabel(String sentiment) {
        this.label = sentiment;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    

    public String getIdSentence() {
        return idSentence;
    }

    public String getText() {
        return text;
    }
    
    public String getLabel() {
        return label;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "Result{" + "idDocument=" + idSentence + ", sentiment=" + label + ", confidence=" + confidence + '}';
    }
    
     
  

}
