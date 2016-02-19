
package com.Similarity;

import com.Logs.OtherException;
import com.Preprocessing.Postag;
import java.util.ArrayList;
import java.util.concurrent.Callable;
public class Task implements Callable<String> {
    private String xmlinput;

    public Task(String xmlinput)  {
        this.xmlinput = xmlinput;
    }
    
    /**
     * this method execute  similarity process 
     */
    private String run(String xmlinput)  throws Exception {
        XmlFileOperations xo= new XmlFileOperations();
        Input input = xo.getInput(xmlinput);
        if (input.getDocumentList().isEmpty()) {
            throw new OtherException("Does not exist documents");
        }   
        
        //preprocessing      
        if (!input.getPosTags().isEmpty()) {
            Postag tagger = new Postag();
            ArrayList<InputDocument> documentList = input.getDocumentList();
            for (InputDocument indoc : documentList) {
                String posTagging = tagger.getPosTagging(indoc.getText(), input.getPosTags());
                indoc.setText(posTagging);
            }

        }


            SimilarityProcess ssp = new SimilarityProcess();
            ArrayList<OutputSimilarity> list = ssp.run(input);
            String xml = xo.getOutputXml(list, input.getMetric());
            return xml;
    }
    
    @Override
    public String call() throws Exception {
        String out = run(this.xmlinput);
        return out;
    }
    
}
