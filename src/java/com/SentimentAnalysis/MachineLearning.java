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

public class MachineLearning {

    private static final String path = "C:\\Test\\temp\\";
    private static final String path_dictionary = "C:\\Test\\dictionary.txt";
    private static final String path_toNaiveBayesModel = path + "\\Models\\naivebayes";
    private static final String path_to_SvmModel = path + "\\Models\\svm";
    private static final String path_to_NeuralNetModel = path + "\\Models\\neuralNet";
    private static final String path_to_naivebayes_wordlist = "C:\\Test\\temp\\Models\\wordlist\\wordlist Sentiment 50k features.txt";
    private static final String path_to_SVM_wordlist = "C:\\Test\\temp\\Models\\wordlist\\wordlist Sentiment 50k features.txt";

    /**
     * This method finds the sentiment for Machine Learning approach
     */
    public ArrayList<Output> getSentiment(ArrayList<InputDocument> documents, Input input) throws Exception {
        Output result;
        ArrayList<SentenceOutput> sentenceresultList;
        ArrayList<Output> resultlist = new ArrayList<>();
        switch (input.getClassifier()) {
            case "NB":
                for (int i = 0; i < documents.size(); i++) {
                    result = new Output();
                    InputDocument document = documents.get(i);
                    createProcess(document, path_toNaiveBayesModel, path_to_naivebayes_wordlist, input.getStemmer(), input.getN_grams());
                    ArrayList<Sentence> originalsentences = input.getDocumentList().get(i).getSentences();
                    sentenceresultList = runModel(originalsentences);
                    result.setIdDocument(document.getId());
                    result.setSentencesResult(sentenceresultList);
                    resultlist.add(result);
                }
                break;
            case "SVM":
                for (int i = 0; i < documents.size(); i++) {
                    result = new Output();
                    InputDocument document = documents.get(i);
                    createProcess(document, path_to_SvmModel, path_to_SVM_wordlist, input.getStemmer(), input.getN_grams());
                    ArrayList<Sentence> originalsentences = input.getDocumentList().get(i).getSentences();
                    sentenceresultList = runModel(originalsentences);
                    result.setIdDocument(document.getId());
                    result.setSentencesResult(sentenceresultList);
                    resultlist.add(result);
                }
                break;
//            case "MLP":
//                for(InputDocument document:documents){
//                createProcess(document, path_to_NeuralNetModel, input.getStemmer(), input.getN_grams());
//                resultList = runModel(documents);
//        }
//                break;
            default:
                //write to log file and return null
                throw new OtherException("No suitable Classiffier");
        }

        return resultlist;
    }

    /**
     * This method creates the rapidminer process
     */
    private void createProcess(InputDocument document, String path_to_Classifier, String path_to_wordlist, String stemmerMethod, int n_grams) throws Exception {
        String xml
                = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<process version=\"6.3.000\">\n"
                + "  <context>\n"
                + "    <input/>\n"
                + "    <output/>\n"
                + "    <macros>\n"
                + "      <macro>\n"
                + "        <key>label</key>\n"
                + "        <value>Sentiment</value>\n"
                + "      </macro>\n"
                + "      <macro>\n"
                + "        <key>label_positive_class</key>\n"
                + "        <value>positive</value>\n"
                + "      </macro>\n"
                + "      <macro>\n"
                + "        <key>label_negative_class</key>\n"
                + "        <value>negative</value>\n"
                + "      </macro>\n"
                + "    </macros>\n"
                + "  </context>\n"
                + "  <operator activated=\"true\" class=\"process\" compatibility=\"6.3.000\" expanded=\"true\" name=\"Process\">\n"
                + "    <process expanded=\"true\">";
        //documents
        ArrayList<Sentence> sentences = document.getSentences();
        for (int i = 0; i < sentences.size(); i++) {
            xml += " <operator activated=\"true\" class=\"text:create_document\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Create Document (" + (i + 1) + ")\" width=\"90\" x=\"45\" y=\"165\">\n"
                    + "        <parameter key=\"text\" value=\"" + sentences.get(i).getText() + " \"/>\n"
                    + "      </operator>\n";
        }
        //read wordlist
        xml += "  <operator activated=\"true\" class=\"text:read_document\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Read Document\" width=\"90\" x=\"45\" y=\"165\">\n"
                + "        <parameter key=\"file\" value=\"" + path_to_wordlist + "\"/>\n"
                + "      </operator>\n";

        //read model
        xml += "<operator activated=\"true\" class=\"read_model\" compatibility=\"6.3.000\" expanded=\"true\" height=\"60\" name=\"Read Model\" width=\"90\" x=\"112\" y=\"30\">\n"
                + "        <parameter key=\"model_file\" value=\"" + path_to_Classifier + "\"/>\n"
                + "      </operator>";
        //process wordlist
        xml += " <operator activated=\"true\" class=\"text:process_documents\" compatibility=\"6.1.000\" expanded=\"true\" height=\"94\" name=\"Process Documents\" width=\"90\" x=\"246\" y=\"120\">\n"
                + "        <process expanded=\"true\">\n"
                + "          <operator activated=\"true\" class=\"text:tokenize\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Tokenize\" width=\"90\" x=\"45\" y=\"30\">\n"
                + "            <parameter key=\"mode\" value=\"regular expression\"/>\n"
                + "            <parameter key=\"expression\" value=\"\\W\"/>\n"
                + "          </operator>\n"
                + "          <connect from_port=\"document\" to_op=\"Tokenize\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Tokenize\" from_port=\"document\" to_port=\"document 1\"/>\n"
                + "          <portSpacing port=\"source_document\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>\n"
                + "        </process>\n"
                + "      </operator>\n";

        //process documents
        xml += " <operator activated=\"true\" class=\"text:process_documents\" compatibility=\"6.1.000\" expanded=\"true\" height=\"112\" name=\"Process Documents (2)\" width=\"90\" x=\"313\" y=\"300\">\n"
                + "<parameter key=\"vector_creation\" value=\"Binary Term Occurrences\"/> \n"
                + "        <process expanded=\"true\">";
        //inside process_documents
        //tokenize
        xml
                += "<operator activated=\"true\" class=\"text:tokenize\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Tokenize (3)\" width=\"90\" x=\"45\" y=\"165\">\n"
                + "            <parameter key=\"mode\" value=\"regular expression\"/>\n"
                + "            <parameter key=\"expression\" value=\"(http|https)://\\S*\"/>\n"
                + "          </operator>\n"
                + "          <operator activated=\"true\" class=\"text:tokenize\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Tokenize (2)\" width=\"90\" x=\"179\" y=\"165\"/>\n";

        //transform_cases
        xml
                += " <operator activated=\"true\" class=\"text:transform_cases\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Transform Cases\" width=\"90\" x=\"313\" y=\"165\"/>\n";
        //filter by length
        xml
                += " <operator activated=\"true\" class=\"text:filter_by_length\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Filter Tokens (by Length)\" width=\"90\" x=\"447\" y=\"165\">\n"
                + "            <parameter key=\"min_chars\" value=\"2\"/>\n"
                + "          </operator>\n";
        //filter stopwords dictionary
        xml
                += "<operator activated=\"true\" class=\"text:filter_stopwords_dictionary\" compatibility=\"6.1.000\" expanded=\"true\" height=\"76\" name=\"Filter Stopwords (Dictionary)\" width=\"90\" x=\"581\" y=\"210\">\n"
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

        //connect operators
        //incide process_documents
        xml += "<connect from_port=\"document\" to_op=\"Tokenize (3)\" to_port=\"document\"/>\n";
        xml += "<connect from_op=\"Tokenize (3)\" from_port=\"document\" to_op=\"Tokenize (2)\" to_port=\"document\"/>\n";
        xml += "<connect from_op=\"Tokenize (2)\" from_port=\"document\" to_op=\"Transform Cases\" to_port=\"document\"/>\n";
        xml += "<connect from_op=\"Transform Cases\" from_port=\"document\" to_op=\"Filter Tokens (by Length)\" to_port=\"document\"/>\n";
        xml += "<connect from_op=\"Filter Tokens (by Length)\" from_port=\"document\" to_op=\"Filter Stopwords (Dictionary)\" to_port=\"document\"/>\n";
        xml += "<connect from_op=\"Filter Stopwords (Dictionary)\" from_port=\"document\" to_op=\"Filter Stopwords (English)\" to_port=\"document\"/>\n";

        if (stemmerMethod.equals("Porter")) {
            xml += "<connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Porter)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Porter)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        } else if (stemmerMethod.equals("Lovins")) {
            xml
                    += " <connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Lovins)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Lovins)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        }
        xml
                += " <connect from_op=\"Generate n-Grams (Terms)\" from_port=\"document\" to_port=\"document 1\"/>\n"
                + "          <portSpacing port=\"source_document\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>";
        xml
                += "</process>\n"
                + "</operator>\n";

        //apply model
        xml += " <operator activated=\"true\" class=\"apply_model\" compatibility=\"5.3.015\" expanded=\"true\" height=\"76\" name=\"Apply Model\" width=\"90\" x=\"581\" y=\"120\">\n"
                + "        <list key=\"application_parameters\"/>\n"
                + "      </operator>";

        for (int i = 0; i < sentences.size(); i++) {
            xml
                    += "<connect from_op=\"Create Document (" + (i + 1) + ")\" from_port=\"output\" to_op=\"Process Documents (2)\" to_port=\"documents " + (i + 1) + "\"/>";
        }

        xml
                += "<connect from_op=\"Read Document\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents 1\"/>\n"
                + "      <connect from_op=\"Read Model\" from_port=\"output\" to_op=\"Apply Model\" to_port=\"model\"/>\n"
                + "      <connect from_op=\"Process Documents\" from_port=\"word list\" to_op=\"Process Documents (2)\" to_port=\"word list\"/>\n"
                + "      <connect from_op=\"Process Documents (2)\" from_port=\"example set\" to_op=\"Apply Model\" to_port=\"unlabelled data\"/>\n"
                + "      <connect from_op=\"Apply Model\" from_port=\"labelled data\" to_port=\"result 1\"/>\n"
                + "      <portSpacing port=\"source_input 1\" spacing=\"0\"/>\n"
                + "      <portSpacing port=\"sink_result 1\" spacing=\"0\"/>\n"
                + "      <portSpacing port=\"sink_result 2\" spacing=\"0\"/>\n"
                + "    </process>\n"
                + "  </operator>\n"
                + "</process>";

        safeProcess("Classification.rmp", xml);
    }

    /**
     * This method executes the rapidminer process
     */
    private ArrayList<SentenceOutput> runModel(ArrayList<Sentence> sentences) throws Exception {
        ArrayList<SentenceOutput> resultList = new ArrayList<>();
        SentenceOutput result;
        com.Rapidminer.RapidminerProccess rm = new RapidminerProccess(path + "Classification.rmp");
        IOContainer ioResult = rm.runproccess();

        ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);

        for (int i = 0; i < resultSet.size(); i++) {
            Example example = resultSet.getExample(i);
            Attribute label = example.getAttributes().get("prediction");
            String labelvalue = example.getValueAsString(label);
            Attribute confidence = null;
            String confidencevalue;
            String temp = sentences.get(i).getText().replaceAll(" ", "");
            if (temp.isEmpty()) {
                confidencevalue = "1";
                labelvalue = "null";
            } else {
                if (labelvalue.equals("neg")) {
                    confidence = example.getAttributes().get("confidence(neg)");
                } else if (labelvalue.equals("pos")) {
                    confidence = example.getAttributes().get("confidence(pos)");
                }
                confidencevalue = example.getValueAsString(confidence);
            }
            result = new SentenceOutput();
            result.setIdSentence(sentences.get(i).getIdSentnece());
            result.setText(sentences.get(i).getText());
            result.setLabel(labelvalue);
            result.setConfidence(Double.valueOf(confidencevalue));
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
