package com.SentimentAnalysis;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;
import org.ejml.simple.SimpleMatrix;

public class StanfordSentimentAnalyzer {

    private final String POSITIVE = "Pos";
    private final String NEUTRAL = "Neu";
    private final String NEGATIVE = "Neg";
    private final String UNCLASSIFIED = "Unclassified";
    
    private static final Properties props = getProperties();
    private static StanfordCoreNLP   pipeline = new StanfordCoreNLP(props);;


    
    
    /**
     * This method returns the properties
     */
    private static Properties getProperties() {
        Properties props = new Properties();
         props.setProperty("annotators", "tokenize, ssplit, pos,lemma,parse, sentiment");

        return props;
    }

    /**
     * This method returns the sentiment score
     */
    private double getScore(String line) {
        Annotation  annotation;
        int sentiment = -1;
        double score = 0;
        
        if (line != null && line.length() > 0) {
                annotation = pipeline.process(line);
                
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

                    Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                    SimpleMatrix nodeVector = RNNCoreAnnotations.getNodeVector(tree);
                    sentiment = RNNCoreAnnotations.getPredictedClass(tree);
 
                    if (sentiment == 0 || sentiment == 1) {
                        score = score - Math.abs(nodeVector.elementSum());
                    } else if (sentiment == 3 || sentiment == 4) {

                        score = score + Math.abs(nodeVector.elementSum());
                    }
                    
                }
        }


        return score;
    }
    
    

      /**
     * This method returns the sentiment label
     */
    public String getSentiment(String line) {
        double score = getScore(line);
        score = converRange(score);
        
        if (score <0 ) {
            return NEGATIVE;
        } else if (score >0) {
            return POSITIVE;
        } else if( score == 0) {
            return NEUTRAL;
        } else { //default
            return UNCLASSIFIED;
        }

    }
    
    /**
     * This method converts a number to another range
     */
    public double converRange(double oldValue) {
        double oldMax = 2;
        double OldMin = -2;
        double newMin = -1;
        double newMax = 1;

        double oldRange = (oldMax - OldMin);
        double newRange = (newMax - newMin);
        double newValue = (((oldValue - OldMin) * newRange) / oldRange) + newMin;
        
        return newValue;
    }

}
