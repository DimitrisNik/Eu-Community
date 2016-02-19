package com.TopicModeling;
/**
 * The class represents a token
 */
public class Token {

    private String id;
    private String text;

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Token{" + "id=" + id + ", text=" + text + '}';
    }

}
