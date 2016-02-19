package com.TopicModeling;

//represent Listof docs
import java.util.ArrayList;
/**
 * The class represents the topic similarity
 */
public class TopicSimilarity {

    OutputDocument outdoc;
    ArrayList<OutputTopic> topiclist;

    public void setOutdoc(OutputDocument outdoc) {
        this.outdoc = outdoc;
    }

    public void setTopiclist(ArrayList<OutputTopic> topiclist) {
        this.topiclist = topiclist;
    }

    public OutputDocument getOutdoc() {
        return outdoc;
    }

    public ArrayList<OutputTopic> getTopiclist() {
        return topiclist;
    }

}
