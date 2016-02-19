package com.TopicModeling;

import java.util.ArrayList;

/**
 * The class represents the "RankedDoc" parameter for the output xml file
 */
public class RankedDoc {

    OutputTopic outTopic;
    ArrayList<OutputDocument> documentslist;

    public void setOutTopic(OutputTopic outTopic) {
        this.outTopic = outTopic;
    }

    public void setDocumentslist(ArrayList<OutputDocument> documentslist) {
        this.documentslist = documentslist;
    }

    public OutputTopic getOutTopic() {
        return outTopic;
    }

    public ArrayList<OutputDocument> getDocumentslist() {
        return documentslist;
    }

}
