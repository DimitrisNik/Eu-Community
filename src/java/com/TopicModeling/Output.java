package com.TopicModeling;

import java.util.ArrayList;

/**
 * The class represents the output of this process
 */
public class Output {

    ArrayList<Topic> topicList;
    ArrayList<RankedDoc> rankedDocsList;
    ArrayList<TopicSimilarity> topicSimilarityList;

    public Output() {
    }

    public Output(ArrayList<Topic> topicList, ArrayList<RankedDoc> rankedDocsList, ArrayList<TopicSimilarity> topicSimilarityList) {
        this.topicList = topicList;
        this.rankedDocsList = rankedDocsList;
        this.topicSimilarityList = topicSimilarityList;
    }

    public void setTopicList(ArrayList<Topic> topicList) {
        this.topicList = topicList;
    }

    public void setRankedDocsList(ArrayList<RankedDoc> rankedDocsList) {
        this.rankedDocsList = rankedDocsList;
    }

    public void setTopicSimilarityList(ArrayList<TopicSimilarity> topicSimilarityList) {
        this.topicSimilarityList = topicSimilarityList;
    }

    public ArrayList<Topic> getTopicList() {
        return topicList;
    }

    public ArrayList<RankedDoc> getRankedDocsList() {
        return rankedDocsList;
    }

    public ArrayList<TopicSimilarity> getTopicSimilarityList() {
        return topicSimilarityList;
    }

}
