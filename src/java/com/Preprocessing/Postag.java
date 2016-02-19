
package com.Preprocessing;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Postag {
    private final String taggerPath = "C:\\Test\\english-left3words-distsim.tagger";
    MaxentTagger tagger = null;

    public Postag() {
        tagger = new MaxentTagger(taggerPath);
    }
    
    /**
     * this method finds the pos tags
     */
    private List<TaggedWord> findPosTags(String sample) {      
        InputStream is = new ByteArrayInputStream(sample.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        List<List<HasWord>> tokenizeText = MaxentTagger.tokenizeText(br);
        List<HasWord> hasWordList = tokenizeText.get(0);

        List<TaggedWord> list = tagger.apply(hasWordList);

        return list;
    }
    
    /**
     * this method filters the documents based on the pos tags 
     */
     public String getPosTagging(String Sample, String pos) {
        String result = "";
        List<TaggedWord> findPosTags = findPosTags(Sample);
        pos = pos.replace(" ", "");
        String[] split = pos.split("|");
        for (TaggedWord tagword : findPosTags) {
            for (String s : split) {
                if (s.startsWith("C") && tagword.tag().startsWith("C")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("C") && tagword.tag().startsWith("C")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("E") && tagword.tag().startsWith("E")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("F") && tagword.tag().startsWith("F")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("I") && tagword.tag().startsWith("I")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("J") && tagword.tag().startsWith("J")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("L") && tagword.tag().startsWith("L")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("M") && tagword.tag().startsWith("M")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("N") && tagword.tag().startsWith("N")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("P") && tagword.tag().startsWith("P")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("R") && tagword.tag().startsWith("R")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("S") && tagword.tag().startsWith("S")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("T") && tagword.tag().startsWith("T")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("U") && tagword.tag().startsWith("U")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("V") && tagword.tag().startsWith("V")) {
                    result += " " + tagword.word();
                } else if (s.startsWith("W") && tagword.tag().startsWith("W")) {
                    result += " " + tagword.word();
                }
            }
        }

        return result;
    }
    
    
    
}
