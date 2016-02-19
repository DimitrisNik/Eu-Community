package com.Similarity;

/**
 * The class represents the  "document" from input XML
 */
public class InputDocument {

    private String id;
    private String type;
    private String url;
    private String mediasource;
    private String text;

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

    public void setText(String text) {
        this.text = text;
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

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", type=" + type + ", url=" + url + ", mediasource=" + mediasource + ", text=" + text + '}';
    }
}