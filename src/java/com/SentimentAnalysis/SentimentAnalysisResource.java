package com.SentimentAnalysis;

import com.Logs.OtherException;
import com.Logs.LogFile;
import com.Preprocessing.Postag;
import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("StanfordSentimentAnalysis")
public class SentimentAnalysisResource {

    @Context
    private UriInfo context;

    public SentimentAnalysisResource() {
    }

    private String run(String xmlinput) throws Exception {
        String xml = "";
        ArrayList<Output> resultList = new ArrayList<>();
        XmlFileOperations xmloperation = new XmlFileOperations();
        Input input = xmloperation.getInput(xmlinput);   //input with preprocessing             
        Input originalinput = xmloperation.getInput(xmlinput);//unmodified input
        
        ArrayList<InputDocument> documentList = input.getDocumentList();
        if (documentList.isEmpty()) {
            throw new OtherException("Does not exist documents");
        }


        //Subjectivity Classifier
        Subjectivity subjectivity = new Subjectivity();
        ArrayList<InputDocument> tempDocumentlist = new ArrayList<>();

        for (int i = 0; i < documentList.size(); i++) {
            ArrayList<Sentence> tempSentencseslist = new ArrayList<>();
            InputDocument document = documentList.get(i);
            ArrayList<Sentence> sentences = document.getSentences();
            for(Sentence sentence:sentences){
                if(subjectivity.getSubjectiveSentences(sentence.getText())){
                    tempSentencseslist.add(sentence);
                }
                else{
                    sentence.setText("");
                    tempSentencseslist.add(sentence);
                    
                }
            }
            sentences.clear();
            sentences.addAll(tempSentencseslist);
            tempDocumentlist.add(document);
        }        
        documentList.clear();
        documentList.addAll(tempDocumentlist);
        input.setDocumentList(documentList);
        //if does not exist subjective document return null
        if (documentList.size() == 0) {
            xml = xmloperation.getOutputXml(resultList);
            return xml;
        }      
        
        // find postags
        Postag tagger = new Postag();
        for (int j=0; j<documentList.size(); j++) {
            ArrayList<Sentence> sentences = documentList.get(j).getSentences();
            for (int i = 0; i < sentences.size(); i++) {
                String text = sentences.get(i).getText();
                if (!input.getPosTags().isEmpty() && !text.isEmpty()) {
                    text = tagger.getPosTagging(text, input.getPosTags());
                }
                sentences.get(i).setText(text);
            }
            documentList.get(j).setSentences(sentences);
        }


        switch (input.getMethod()) {
            case "PL": //run PL method                   
                PolarityLexicon pl = new PolarityLexicon();
               ArrayList<Output> r = pl.getSentiment(documentList, originalinput);
                xml = xmloperation.getOutputXml(r);
                
                break;

            case "ML": //run ML method                
                MachineLearning ml = new MachineLearning();
                resultList.addAll(ml.getSentiment(documentList,originalinput));

                xml = xmloperation.getOutputXml(resultList);

                break;
            case "HML"://run HML method                
                HybridicML hml = new HybridicML();
                resultList.addAll(hml.getSentiment(documentList, originalinput));

                xml = xmloperation.getOutputXml(resultList);
                break;
            default:
                //write to log file and return null
                throw new OtherException("No suitable method for Sentiment Analysis");

        }

        //dealocate
        xmloperation = null;
        input = null;
        documentList = null;

        return xml;
    }

    @POST
    @Produces("application/xml")
    public String getXml(@FormParam("xmlinput") String xmlinput) throws IOException {
        try {
            String xml = run(xmlinput);
            System.gc();
            return xml;
        } catch (Exception e) {
            LogFile logs = new LogFile();
            logs.writelogs(e, xmlinput, "SentimentAnalysis");
            System.gc();
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<sentimentOutput>null</sentimentOutput>";
        }
    }

}
