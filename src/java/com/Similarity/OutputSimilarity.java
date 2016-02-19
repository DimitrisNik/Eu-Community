
package com.Similarity;

/**
 * The class represents the output of this process
 */
public class OutputSimilarity {
    private String firstId;
    private String SecondId;
    private String Score;

    public OutputSimilarity() {
    }

    
    public OutputSimilarity(String firstId, String SecondId, String Score) {
        this.firstId = firstId;
        this.SecondId = SecondId;
        this.Score = Score;
    }
    
    

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public void setSecondId(String SecondId) {
        this.SecondId = SecondId;
    }

    public void setScore(String Score) {
        this.Score = Score;
    }

    public String getFirstId() {
        return firstId;
    }

    public String getSecondId() {
        return SecondId;
    }

    public String getScore() {
        return Score;
    }

    @Override
    public String toString() {
        return "DocumentSimilarity{" + "firstId=" + firstId + ", SecondId=" + SecondId + ", Score=" + Score + '}';
    }
    
    
    
}
