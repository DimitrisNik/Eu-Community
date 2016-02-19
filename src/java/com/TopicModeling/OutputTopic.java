package com.TopicModeling;
/**
 * The class represents the topic for the output xml file. 
 */
public class OutputTopic {

    String id_topic;
    double percentage;

    public void setId_topic(String id_topic) {
        this.id_topic = id_topic;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getId_topic() {
        return id_topic;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "OutputTopic{" + "id_topic=" + id_topic + ", percentage=" + percentage + '}';
    }

}
