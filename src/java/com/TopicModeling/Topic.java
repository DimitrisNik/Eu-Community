package com.TopicModeling;

import java.util.ArrayList;
/**
 * The class represents a topic
 */
public class Topic {

    private String id_topic;
    private ArrayList<Token> tokenList;

    public void setId_topic(String id_topic) {
        this.id_topic = id_topic;
    }

    public void setTokenList(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public String getId_topic() {
        return id_topic;
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }

    @Override
    public String toString() {
        String r = "Topic{" + "id_topic=" + id_topic + "\n";
        for (Token t : tokenList) {
            r = r + t + " ";
        }
        return r;
    }

}
