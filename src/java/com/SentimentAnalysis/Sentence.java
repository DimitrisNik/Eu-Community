
package com.SentimentAnalysis;
/**
 * The class represents a sentence of a document
 */
public class Sentence {
    private String idSentnece;
    private String text;

    public void setIdSentnece(String idSentnece) {
        this.idSentnece = idSentnece;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdSentnece() {
        return idSentnece;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Sentence{" + "idSentnece=" + idSentnece + ", text=" + text + '}';
    }
    
    
}
