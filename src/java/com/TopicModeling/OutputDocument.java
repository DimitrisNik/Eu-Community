package com.TopicModeling;
//represent the document in output. We use it for (listOfTopics and  listOfDocs)
/**
 * The class represents the document for the output xml file. 
 */
public class OutputDocument {

    String id_document;
    int numWords;

    public void setId_document(String id_document) {
        this.id_document = id_document;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }

    public String getId_document() {
        return id_document;
    }

    public int getNumWords() {
        return numWords;
    }

    @Override
    public String toString() {
        return "OutputDocument{" + "id_document=" + id_document + ", numWords=" + numWords + '}';
    }

}
