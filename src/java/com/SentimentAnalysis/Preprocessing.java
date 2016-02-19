/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SentimentAnalysis;

import com.Logs.OtherException;
import com.Preprocessing.Postag;
import com.Rapidminer.RapidminerProccess;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.operator.IOContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author DimitrisTesting
 */
public class Preprocessing {
    private static final String path = "C:\\Test\\temp";
    private static final String path_dictionary = "C:\\Test\\dictionary.txt";


     /**
     * This method creates rapidminer process
     */
    private String createProcess(ArrayList<Sentence> sentences, Input in) throws OtherException {
       
        int counter = 1;
        String xml
                = //start
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<process version=\"5.3.015\">\n"
                + "  <context>\n"
                + "    <input/>\n"
                + "    <output/>\n"
                + "    <macros/>\n"
                + "  </context>\n"
                + "  <operator activated=\"true\" class=\"process\" compatibility=\"5.3.015\" expanded=\"true\" name=\"Process\">\n"
                + "    <process expanded=\"true\">\n";
        //create document
        xml = xml
                + " <operator activated=\"true\" class=\"text:create_document\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Create Document\" width=\"90\" x=\"45\" y=\"30\">\n";
        xml = xml
                + " <parameter key=\"text\" value=\"" + sentences.get(0).getText() + "\"/>";
        xml = xml + "</operator> \n";
        if (sentences.size() > 1) {

            for (int i = 1; i < sentences.size(); i++) {
                counter++;
                xml = xml
                        + "<operator activated=\"true\" class=\"text:create_document\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Create Document (" + counter + ")\" width=\"90\" x=\"45\" y=\"120\">\n";
                xml = xml
                        + " <parameter key=\"text\" value=\"" + sentences.get(i).getText() + "\"/>";
                xml = xml + "</operator> \n";
            }
        }

        //process_documents
        xml = xml
                + "<operator activated=\"true\" class=\"text:process_documents\" compatibility=\"5.3.002\" expanded=\"true\" height=\"112\" name=\"Process Documents\" width=\"90\" x=\"313\" y=\"30\">\n"
                + "        <parameter key=\"vector_creation\" value=\"Binary Term Occurrences\"/>\n"
                + "        <process expanded=\"true\">";
        //inside process_documents
        //tokenize
        xml = xml
                + "<operator activated=\"true\" class=\"text:tokenize\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Tokenize\" width=\"90\" x=\"45\" y=\"120\">\n"
                + "              <parameter key=\"mode\" value=\"regular expression\"/>\n"
                + "              <parameter key=\"expression\" value=\"(http|https)://\\S*\"/>\n"
                + "          </operator>"
                + "<operator activated=\"true\" class=\"text:tokenize\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Tokenize (2)\" width=\"90\" x=\"179\" y=\"210\"/>\n";

        //transform_cases
        xml = xml
                + "<operator activated=\"true\" class=\"text:transform_cases\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Transform Cases\" width=\"90\" x=\"313\" y=\"210\"/> \n";
        //filter by length
        xml = xml
                + "<operator activated=\"true\" class=\"text:filter_by_length\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Filter Tokens (by Length)\" width=\"90\" x=\"447\" y=\"210\">\n"
                + "            <parameter key=\"min_chars\" value=\"2\"/>\n"
                + "</operator> \n";
        //filter stopwords dictionary
        xml = xml
                + "<operator activated=\"true\" class=\"text:filter_stopwords_dictionary\" compatibility=\"5.3.002\" expanded=\"true\" height=\"76\" name=\"Filter Stopwords (Dictionary)\" width=\"90\" x=\"581\" y=\"210\">\n"
                + "            <parameter key=\"file\" value=\"" + path_dictionary + "\"/>\n"
                + "</operator> \n";
        //filter_stopwords_english
        xml = xml
                + "<operator activated=\"true\" class=\"text:filter_stopwords_english\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Filter Stopwords (English)\" width=\"90\" x=\"179\" y=\"30\"/>";
//        //stemm
        if (in.getStemmer().equals("Porter")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"text:stem_porter\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Porter)\" width=\"90\" x=\"447\" y=\"30\"/>";
        } else if (in.getStemmer().equals("Lovins")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"text:stem_lovins\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Lovins)\" width=\"90\" x=\"447\" y=\"30\"/>";
        } else {
            throw new OtherException("No suitable method for stem");
        }
        //n-grams
        xml = xml
                + "<operator activated=\"true\" class=\"text:generate_n_grams_terms\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Generate n-Grams (Terms)\" width=\"90\" x=\"648\" y=\"30\">";
        xml = xml
                + "            <parameter key=\"max_length\" value=\"" + in.getN_grams()+ "\"/>";
        xml = xml + "</operator>";

        //connect operators
        //indide process_documents
        xml = xml
                + "<connect from_port=\"document\" to_op=\"Tokenize\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize\" from_port=\"document\" to_op=\"Tokenize (2)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize (2)\" from_port=\"document\" to_op=\"Transform Cases\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Transform Cases\" from_port=\"document\" to_op=\"Filter Tokens (by Length)\" to_port=\"document\"/> \n"
                + " <connect from_op=\"Filter Tokens (by Length)\" from_port=\"document\" to_op=\"Filter Stopwords (Dictionary)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Filter Stopwords (Dictionary)\" from_port=\"document\" to_op=\"Filter Stopwords (English)\" to_port=\"document\"/> \n";
        //me stem
        if (in.getStemmer().equals("Porter")) {
            xml = xml + "<connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Porter)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Porter)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        } else if (in.getStemmer().equals("Lovins")) {
            xml = xml
                    + " <connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Lovins)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Lovins)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        }
        //mestem telos
        //xoris to stem 
//         xml = xml + "<connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
       //----xoris to stem telos----
         xml = xml
                + "          <connect from_op=\"Generate n-Grams (Terms)\" from_port=\"document\" to_port=\"document 1\"/>\n"
                + "          <portSpacing port=\"source_document\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>";
        xml = xml
                + "</process>\n"
                + "</operator>";

//main process
        xml = xml
                + "<connect from_op=\"Create Document\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents 1\"/>\n";
//if exist more than one documents
        counter = 1;
        if (sentences.size() > 1) {
            for (int i = 1; i < sentences.size(); i++) {
                counter++;
                xml = xml
                        + " <connect from_op=\"Create Document (" + counter + ")\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents " + counter + "\"/>\n";
            }
        }
        xml = xml
                + " <connect from_op=\"Process Documents\" from_port=\"example set\" to_port=\"result 1\"/>\n"
                + " <portSpacing port=\"source_input 1\" spacing=\"0\"/>\n"
                + " <portSpacing port=\"sink_result 1\" spacing=\"0\"/>\n"
                + " <portSpacing port=\"sink_result 2\" spacing=\"0\"/>\n";

//end process
        xml = xml
                + "</process>\n"
                + "</operator>\n"
                + "</process>";

        //dealocate 
        sentences = null;

        return xml;
    }

    /**
     * This method saves the rapidminer process
     */
    private void safeProcess(String xml) throws Exception {

        File file = new File(path + "\\preproccesing_Sentiment.rmp");

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

    /**
     * This method executes the rapidminer process
     */
    public ArrayList<String> run(ArrayList<Sentence> sentences, Input in) throws Exception {
        ArrayList<String> nlpPost = new ArrayList<>(); //list of post after preprocessing
        
        
        String rmprocess = createProcess(sentences, in);
        safeProcess(rmprocess);

        RapidminerProccess rm = new RapidminerProccess(path + "\\preproccesing_Sentiment.rmp");
        IOContainer ioResult = rm.runproccess();
        ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);
        ExampleTable exampleTable = resultSet.getExampleTable();

        for (int i = 0; i < exampleTable.size(); i++) {
            Example example = resultSet.getExample(i);
            String post = "";
            for (int j = 0; j < exampleTable.getAttributeCount(); j++) {
                if (example.getValue(exampleTable.getAttribute(j)) == 1.0) {
                    post = post + exampleTable.getAttribute(j).getName() + " ";
                }

            }
            if (post != "") {
                nlpPost.add(post);
            }

            post = null;
        }

        //dealocate
        rmprocess = null;
        rm = null;
        ioResult = null;
        resultSet = null;
        exampleTable = null;

        return nlpPost;
    }
    
}
