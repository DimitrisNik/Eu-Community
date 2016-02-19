package com.SentimentAnalysis;

import java.util.ArrayList;

/**
 * The class represents the  "document" from input XML
 */
public class InputDocument {

    private String id;
    private String type;
    private String url;
    private String mediasource;
    private ArrayList<Sentence> sentences;

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMediasource(String mediasource) {
        this.mediasource = mediasource;
    }

    public void setSentences(ArrayList<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getMediasource() {
        return mediasource;
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
    }

    @Override
    public String toString() {
        return "InputDocument{" + "id=" + id + ", type=" + type + ", url=" + url + ", mediasource=" + mediasource + ", sentences=" + sentences + '}';
    }

}
