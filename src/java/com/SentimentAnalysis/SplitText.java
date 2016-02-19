package com.SentimentAnalysis;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SplitText {

    /**
     * This method divide the document into sentences
     */
    public List<String> getSplitText(String document) {
        Reader reader = new StringReader(document);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);

        List<String> sentenceList = new LinkedList<String>();
        Iterator<List<HasWord>> it = dp.iterator();
        while (it.hasNext()) {
            StringBuilder sentenceSb = new StringBuilder();
            List<HasWord> sentence = it.next();
            for (HasWord token : sentence) {
                if (sentenceSb.length() > 1) {
                    sentenceSb.append(" ");
                }
                sentenceSb.append(token);
            }
            sentenceList.add(sentenceSb.toString());
        }
        return sentenceList;
    }

}
