/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SentimentAnalysis;

import java.util.ArrayList;


public class HybridicML {

    private final String POSITIVE = "pos";
    private final String NEUTRAL = "neu";
    private final String NEGATIVE = "neg";

     /**
     * This method finds the sentiment for hybridic approach
     */
    public ArrayList<Output> getSentiment(ArrayList<InputDocument> documents, Input input) throws Exception {
        int numberOfExample = documents.size();
        ArrayList<Output> resultList = new ArrayList<>();
        WordnetAnalyzer wordnet = new WordnetAnalyzer();
        ArrayList<Output> wordnetresults = wordnet.getSentiment(documents, input);
        MachineLearning ml = new MachineLearning();
        ArrayList<Output> mlresults = ml.getSentiment(documents, input);

        for (int i = 0; i < numberOfExample; i++) {  
            Output result = new Output();
            result.setIdDocument(documents.get(i).getId());
            
            Output mlExample = mlresults.get(i);
            Output wordnExample = wordnetresults.get(i);
            ArrayList<SentenceOutput> mlsentencesResult = mlExample.getSentencesResult();
            ArrayList<SentenceOutput> wordnetsentencesResult = wordnExample.getSentencesResult();
            int numberOfSentenses = mlExample.getSentencesResult().size();
            ArrayList<SentenceOutput> sentencesResultList = new ArrayList<>();
            for(int j=0; j<numberOfSentenses; j++){                     
                SentenceOutput sentenceResult = new SentenceOutput();
                sentenceResult.setIdSentence(mlsentencesResult.get(j).getIdSentence());
                sentenceResult.setText(mlsentencesResult.get(j).getText());
                SentenceOutput mlsentence = mlsentencesResult.get(j);
                SentenceOutput wordnetsentence = wordnetsentencesResult.get(j);
                double score = getScore(mlsentence, wordnetsentence);
                if (score < 0) {
                    sentenceResult.setLabel(NEGATIVE);
                } else if (score > 0) {
                    sentenceResult.setLabel(POSITIVE);
                } else {
                    sentenceResult.setLabel(NEUTRAL);
                }
            

                sentenceResult.setConfidence(score);
                sentencesResultList.add(sentenceResult);                     
            }
            result.setSentencesResult(sentencesResultList);
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * This method finds the sentiment score
     */
    private double getScore(SentenceOutput mlExample, SentenceOutput wordnExample) {
        double score = 0;
        double mlscore = mlExample.getConfidence();
        
        mlscore = convertRange(mlscore);
        double wordnetscore = wordnExample.getConfidence();

        if (mlExample.getLabel().toLowerCase().equals("pos")) {
            score = (0.7 * wordnetscore) + (0.3 * mlscore);

        } else {
            score = (0.7 * wordnetscore) - (0.3 * mlscore);
        }

        return score;
    }

    /**
     * This method converts a number to another range
     */
    private double convertRange(double oldValue) {
        double OldMin = 0.5;
        double oldMax = 1;
        double newMin = -1;
        double newMax = 1;

        double oldRange = (oldMax - OldMin);
        double newRange = (newMax - newMin);
        double newValue = (((oldValue - OldMin) * newRange) / oldRange) + newMin;

        return newValue;
    }

}
