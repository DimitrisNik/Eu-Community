
package com.TopicModeling;

import com.Logs.OtherException;
import com.Preprocessing.Postag;
import com.Preprocessing.EscapeSpecialCharacters;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Task  implements Callable<String> {
    
    private String xmlinput;

    public Task(String xmlinput)  {
        this.xmlinput = xmlinput;
    }
    
    /**
     * This method execute the topic modeling web service
     */
    private String run(String xmlinput)  throws Exception {
        String xml = "";
        XmlFileOperations xmloperation = new XmlFileOperations();
        Input input = xmloperation.getInput(xmlinput);
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
         Output output = null ;
         Model model = null;

         model = new Model();
        output = model.runModel(input);
        
        
        xml = xmloperation.getOutputXml(output);

        //dealocate
        xmloperation = null;
        input = null;
        model = null;
        output = null;

        return xml;
    }

    @Override
    public String call() throws Exception {
        String out = run(this.xmlinput);
        return out;
    }
    
}
