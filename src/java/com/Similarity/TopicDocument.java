
package com.Similarity;

import java.util.ArrayList;

/**
 * The class represents the topics of a document 
 */
public class TopicDocument {
    
    private String id;
    private ArrayList<Topic> list;

    public void setId(String id) {
        this.id = id;
    }

    public void setList(ArrayList<Topic> list) {
        this.list = list;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Topic> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "TopidDocument{" + "id=" + id + ", list=" + list + '}';
    }
    
    
    
}
