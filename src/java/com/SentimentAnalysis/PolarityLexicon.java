package com.SentimentAnalysis;

import java.util.ArrayList;

public class PolarityLexicon {

    /**
     * This method finds the sentiment for Polarity Lexion approach
     */
    public ArrayList<Output> getSentiment(ArrayList<InputDocument> documents, Input input) throws Exception {                    
        ArrayList<Output> resultList = new ArrayList<>();
        //preprocesing rapidminer
        Preprocessing preprocrm = new Preprocessing();
        for (int i = 0; i < documents.size(); i++) {
            ArrayList<Sentence> sentences = documents.get(i).getSentences();
            ArrayList<String> temp = preprocrm.run(sentences, input);
            int index = 0;
            for (int j = 0; j < sentences.size(); j++) {
                if (!sentences.get(j).getText().isEmpty()) {
                    String text = temp.get(index);
                    sentences.get(j).setText(text);
                    index++;

                } else {
                    sentences.get(j).setText("");
                }
            }
            documents.get(i).setSentences(sentences);
        }      
        

        for (int i = 0; i < documents.size(); i++) {
            Output result = new Output();    
            ArrayList<SentenceOutput> SentenceresultList = new ArrayList<>();
            InputDocument indoc = documents.get(i);
            ArrayList<Sentence> sentences = indoc.getSentences();
            InputDocument originaldocuments = input.getDocumentList().get(i); //documents without preprocessing
            ArrayList<Sentence> originalsentences = originaldocuments.getSentences(); //sentences without preprocessing
            for (int j = 0; j < sentences.size(); j++) {
                SentenceOutput sentenceresult =  new SentenceOutput();
                Sentence sent = sentences.get(j);
                String text = sent.getText();
                String label;
                if (!text.isEmpty()) {
                    label = getLabel(text);                    
                    sentenceresult.setIdSentence(sent.getIdSentnece());
                    sentenceresult.setText(originalsentences.get(j).getText());
                    sentenceresult.setLabel(label);
                    SentenceresultList.add(sentenceresult);
                } 
                else {                    
                    sentenceresult.setIdSentence(sent.getIdSentnece());
                    sentenceresult.setText(originalsentences.get(j).getText());
                    sentenceresult.setLabel("null");
                    SentenceresultList.add(sentenceresult);
                }
            }
            result.setIdDocument(indoc.getId());
            result.setSentencesResult(SentenceresultList);
            resultList.add(result);
        }

        return resultList;
    }

  
    public String getLabel(String text) {
        StanfordSentimentAnalyzer analyzer = new StanfordSentimentAnalyzer();
        String label = analyzer.getSentiment(text);

        return label;
    }
}
