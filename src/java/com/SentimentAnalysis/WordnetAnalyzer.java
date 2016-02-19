
package com.SentimentAnalysis;

import com.Logs.OtherException;
import com.Rapidminer.RapidminerProccess;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class WordnetAnalyzer {
    private final String POSITIVE = "pos";
    private final String NEUTRAL = "neu";
    private final String NEGATIVE = "neg";

    
     private  final String path = "C:\\Test\\temp\\";
     private static final String path_dictionary = "C:\\Test\\dictionary.txt";
     private final String path_to_wordnet_dictionary ="C:\\Users\\Dimitris\\Desktop\\WordNet-3.0\\dict";
     
    
     /**
     * This method returns the sentiment
     */
      public ArrayList<Output> getSentiment(ArrayList<InputDocument> documents, Input input) throws  Exception {
          ArrayList<SentenceOutput> sentensesresults = new ArrayList<>();
          ArrayList<Output> results = new ArrayList<>();
          for(InputDocument document:documents){
              ArrayList<Sentence> sentences = document.getSentences();
            createProcess(document, input.getStemmer(), input.getN_grams());
            sentensesresults = runModel(sentences);
            Output result = new Output();
            result.setIdDocument(document.getId());
            result.setSentencesResult(sentensesresults);
            results.add(result);
          }
          return results;
      }
    
     /**
     * This method creates the rapidminer process
     */
    private void createProcess(InputDocument document, String stemmerMethod, int n_grams) throws Exception {
        ArrayList<Sentence> sentences = document.getSentences();
        String xml
                = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<process version=\"5.3.015\">\n"
                + "  <context>\n"
                + "    <input/>\n"
                + "    <output/>\n"
                + "    <macros/>\n"
                + "  </context>\n"
                + "  <operator activated=\"true\" class=\"process\" compatibility=\"5.3.015\" expanded=\"true\" name=\"Process\">\n"
                + "    <process expanded=\"true\">\n";
        for (int i = 0; i < sentences.size(); i++) {
            xml += " <operator activated=\"true\" class=\"text:create_document\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Create Document (" + (i + 1) + ")\" width=\"90\" x=\"45\" y=\"165\">\n"
                    + "        <parameter key=\"text\" value=\" " + sentences.get(i).getText() + " \"/>\n"
                    + "      </operator>";
        }

        
        //process documents
        xml += "<operator activated=\"true\" class=\"text:process_documents\" compatibility=\"5.3.002\" expanded=\"true\" height=\"94\" name=\"Process Documents\" width=\"90\" x=\"313\" y=\"165\">\n"
                + "        <process expanded=\"true\">";
        //inside process_documents
        //wordnet dictionary
        xml += "<operator activated=\"true\" class=\"wordnet:open_wordnet_dictionary\" compatibility=\"5.3.000\" expanded=\"true\" height=\"60\" name=\"Open WordNet Dictionary (2)\" width=\"90\" x=\"112\" y=\"345\">\n" +
"            <parameter key=\"directory\" value=\"" +path_to_wordnet_dictionary+ "\"/>\n" +
"          </operator>";
        //tokenize
        xml
                += "<operator activated=\"true\" class=\"text:tokenize\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Tokenize\" width=\"90\" x=\"45\" y=\"120\">\n"
                + "              <parameter key=\"mode\" value=\"regular expression\"/>\n"
                + "              <parameter key=\"expression\" value=\"(http|https)://\\S*\"/>\n"
                + "          </operator>"
                + "<operator activated=\"true\" class=\"text:tokenize\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Tokenize (2)\" width=\"90\" x=\"179\" y=\"210\"/>\n";

        //transform_cases
        xml
                += "<operator activated=\"true\" class=\"text:transform_cases\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Transform Cases\" width=\"90\" x=\"313\" y=\"210\"/> \n";
        //filter by length
        xml
                += "<operator activated=\"true\" class=\"text:filter_by_length\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Filter Tokens (by Length)\" width=\"90\" x=\"447\" y=\"210\">\n"
                + "            <parameter key=\"min_chars\" value=\"2\"/>\n"
                + "</operator> \n";
        //filter stopwords dictionary
        xml
                += "<operator activated=\"true\" class=\"text:filter_stopwords_dictionary\" compatibility=\"5.3.002\" expanded=\"true\" height=\"76\" name=\"Filter Stopwords (Dictionary)\" width=\"90\" x=\"581\" y=\"210\">\n"
                + "            <parameter key=\"file\" value=\"" + path_dictionary + "\"/>\n"
                + "</operator> \n";
        //filter_stopwords_english
        xml
                += "<operator activated=\"true\" class=\"text:filter_stopwords_english\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Filter Stopwords (English)\" width=\"90\" x=\"179\" y=\"30\"/>\n";
        //stemm
        if (stemmerMethod.equals("Porter")) {
            xml
                    += "<operator activated=\"true\" class=\"text:stem_porter\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Porter)\" width=\"90\" x=\"447\" y=\"30\"/>\n";
        } else if (stemmerMethod.equals("Lovins")) {
            xml
                    += "<operator activated=\"true\" class=\"text:stem_lovins\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Lovins)\" width=\"90\" x=\"447\" y=\"30\"/>\n";
        } else {
            throw new OtherException("No suitable method for stem");
        }
        //n-grams
        xml
                += "<operator activated=\"true\" class=\"text:generate_n_grams_terms\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Generate n-Grams (Terms)\" width=\"90\" x=\"648\" y=\"30\">\n";
        xml
                += "            <parameter key=\"max_length\" value=\"" + String.valueOf(n_grams) + "\"/>\n";
        xml += "</operator>\n";
        
        //wordnet operator
        xml += "<operator activated=\"true\" class=\"wordnet:find_sentiment_wordnet\" compatibility=\"5.3.000\" expanded=\"true\" height=\"76\" name=\"Extract Sentiment (2)\" width=\"90\" x=\"715\" y=\"210\"/>";

        //connect operators
        //incide process_documents
        xml
                += "<connect from_port=\"document\" to_op=\"Tokenize\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize\" from_port=\"document\" to_op=\"Tokenize (2)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize (2)\" from_port=\"document\" to_op=\"Transform Cases\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Transform Cases\" from_port=\"document\" to_op=\"Filter Tokens (by Length)\" to_port=\"document\"/> \n"
                + " <connect from_op=\"Filter Tokens (by Length)\" from_port=\"document\" to_op=\"Filter Stopwords (Dictionary)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Filter Stopwords (Dictionary)\" from_port=\"document\" to_op=\"Filter Stopwords (English)\" to_port=\"document\"/> \n";

        if (stemmerMethod.equals("Porter")) {
            xml += "<connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Porter)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Porter)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        } else if (stemmerMethod.equals("Lovins")) {
            xml
                    += " <connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Lovins)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Lovins)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        }
        xml
                +=" <connect from_op=\"Open WordNet Dictionary (2)\" from_port=\"dictionary\" to_op=\"Extract Sentiment (2)\" to_port=\"dictionary\"/>\n"
                +"<connect from_op=\"Generate n-Grams (Terms)\" from_port=\"document\" to_op=\"Extract Sentiment (2)\" to_port=\"document\"/>\n" +
"          <connect from_op=\"Extract Sentiment (2)\" from_port=\"document\" to_port=\"document 1\"/>\n" +
"          <portSpacing port=\"source_document\" spacing=\"0\"/>\n" +
"          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n" +
"          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>";
        xml
                += "</process>\n"
                + "</operator>\n";

       

        for (int i = 0; i < sentences.size(); i++) {
            xml
                    += "<connect from_op=\"Create Document (" + (i + 1) + ")\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents " + (i + 1) + "\"/>";
        }
        
        xml += " <connect from_op=\"Process Documents\" from_port=\"example set\" to_port=\"result 1\"/>\n" +
"      <portSpacing port=\"source_input 1\" spacing=\"0\"/>\n" +
"      <portSpacing port=\"sink_result 1\" spacing=\"0\"/>\n" +
"      <portSpacing port=\"sink_result 2\" spacing=\"0\"/>\n" +
"    </process>\n" +
"  </operator>\n" +
"</process>";

        

        safeProcess("wordnetProcess.rmp", xml);
    }


    /**
     * This method executes the rapidminer process
     */
    private ArrayList<SentenceOutput> runModel(ArrayList<Sentence> sentences) throws Exception {
        ArrayList<SentenceOutput> resultList = new ArrayList<>();
        SentenceOutput result;
        com.Rapidminer.RapidminerProccess rm = new RapidminerProccess(path + "wordnetProcess.rmp");
        IOContainer ioResult = rm.runproccess();

        ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);

        for (int i = 0; i < resultSet.size(); i++) {
            Example example = resultSet.getExample(i);
            Attribute sentiment = example.getAttributes().get("sentiment");
            String scorestr = example.getValueAsString(sentiment);
            

            
            result = new SentenceOutput();
            result.setIdSentence(sentences.get(i).getIdSentnece());
            double score = Double.valueOf(scorestr);
            if(score<0){
                result.setLabel(NEGATIVE);
            }
            else if(score>0){
                result.setLabel(POSITIVE);
            }
            else{
                result.setLabel(NEUTRAL);
            }            

            result.setConfidence(score);
            resultList.add(result);
        }

        return resultList;
    }

    /**
     * This method saves the rapidminer process
     */
    protected void safeProcess(String name, String xml) throws Exception {
        File file = new File(path + name);
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(xml);
        bw.close();

        //dealocate
        file = null;
        fw = null;
        bw = null;

    }
    

    
}
