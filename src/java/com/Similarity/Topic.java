
package com.Similarity;
/**
 * The class represents the Topic 
 */
public class Topic {
    private String id;
    private double percentage;

    public void setId(String id) {
        this.id = id;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getId() {
        return id;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "Topic{" + "id=" + id + ", percentage=" + percentage + '}';
    }
    
    
    
}
