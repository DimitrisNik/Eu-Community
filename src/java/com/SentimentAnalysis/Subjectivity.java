package com.SentimentAnalysis;

import com.Rapidminer.RapidminerProccess;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Subjectivity {

    private static final String path = "C:\\Test\\temp\\";
    private static final String path_dictionary = "C:\\Test\\dictionary.txt";

    public boolean getSubjectiveSentences(String text) throws Exception {

//         List<String> sentences = splitText(text);
        createProcess(text);
        boolean result = runModel(text);

        return result;
    }

    /**
     * This method creates the rapidminer process
     */
    private void createProcess(String text) throws Exception {
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
                + "    <process expanded=\"true\">\n"
                + "      <operator activated=\"true\" class=\"text:create_document\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Create Document (1)\" width=\"90\" x=\"112\" y=\"390\">\n"
                + "        <parameter key=\"text\" value=\" " + text + "  \"/>\n"
                + "      </operator>\n"
                + "      <operator activated=\"true\" class=\"text:read_document\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Read Document\" width=\"90\" x=\"45\" y=\"165\">\n"
                + "        <parameter key=\"file\" value=\"C:\\Test\\temp\\\\Models\\wordlist\\wordlist Subjectivity.txt\"/>\n"
                + "      </operator>\n"
                + "      <operator activated=\"true\" class=\"read_model\" compatibility=\"6.3.000\" expanded=\"true\" height=\"60\" name=\"Read Model\" width=\"90\" x=\"112\" y=\"30\">\n"
                + "        <parameter key=\"model_file\" value=\"C:\\Test\\temp\\\\Models\\objectivity.mod\"/>\n"
                + "      </operator>\n"
                + "      <operator activated=\"true\" class=\"text:process_documents\" compatibility=\"6.1.000\" expanded=\"true\" height=\"94\" name=\"Process Documents\" width=\"90\" x=\"179\" y=\"210\">\n"
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
                + "      </operator>\n"
                + "      <operator activated=\"true\" class=\"text:process_documents\" compatibility=\"6.1.000\" expanded=\"true\" height=\"94\" name=\"Process Documents (2)\" width=\"90\" x=\"447\" y=\"255\">\n"
                + "        <parameter key=\"vector_creation\" value=\"Binary Term Occurrences\"/>\n"
                + "        <process expanded=\"true\">\n"
                + "          <operator activated=\"true\" class=\"text:tokenize\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Tokenize (3)\" width=\"90\" x=\"45\" y=\"165\">\n"
                + "            <parameter key=\"mode\" value=\"regular expression\"/>\n"
                + "            <parameter key=\"expression\" value=\"(http|https)://\\S*\"/>\n"
                + "          </operator>\n"
                + "          <operator activated=\"true\" class=\"text:tokenize\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Tokenize (2)\" width=\"90\" x=\"179\" y=\"165\"/>\n"
                + "          <operator activated=\"true\" class=\"text:transform_cases\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Transform Cases\" width=\"90\" x=\"313\" y=\"165\"/>\n"
                + "          <operator activated=\"true\" class=\"text:filter_by_length\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Filter Tokens (by Length)\" width=\"90\" x=\"447\" y=\"165\">\n"
                + "            <parameter key=\"min_chars\" value=\"2\"/>\n"
                + "          </operator>\n"
                + "          <operator activated=\"true\" class=\"text:filter_stopwords_dictionary\" compatibility=\"6.1.000\" expanded=\"true\" height=\"76\" name=\"Filter Stopwords (Dictionary)\" width=\"90\" x=\"581\" y=\"210\">\n"
                + "            <parameter key=\"file\" value=\"C:\\Test\\dictionary.txt\"/>\n"
                + "          </operator>\n"
                + "          <operator activated=\"true\" class=\"text:filter_stopwords_english\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Filter Stopwords (English)\" width=\"90\" x=\"179\" y=\"30\"/>\n"
                + "          <operator activated=\"true\" class=\"text:stem_porter\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Stem (Porter)\" width=\"90\" x=\"447\" y=\"30\"/>\n"
                + "          <operator activated=\"true\" class=\"text:generate_n_grams_terms\" compatibility=\"6.1.000\" expanded=\"true\" height=\"60\" name=\"Generate n-Grams (Terms)\" width=\"90\" x=\"648\" y=\"30\">\n"
                + "            <parameter key=\"max_length\" value=\"1\"/>\n"
                + "          </operator>\n"
                + "          <connect from_port=\"document\" to_op=\"Tokenize (3)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Tokenize (3)\" from_port=\"document\" to_op=\"Tokenize (2)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Tokenize (2)\" from_port=\"document\" to_op=\"Transform Cases\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Transform Cases\" from_port=\"document\" to_op=\"Filter Tokens (by Length)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Filter Tokens (by Length)\" from_port=\"document\" to_op=\"Filter Stopwords (Dictionary)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Filter Stopwords (Dictionary)\" from_port=\"document\" to_op=\"Filter Stopwords (English)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Porter)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Stem (Porter)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n"
                + "          <connect from_op=\"Generate n-Grams (Terms)\" from_port=\"document\" to_port=\"document 1\"/>\n"
                + "          <portSpacing port=\"source_document\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>\n"
                + "        </process>\n"
                + "      </operator>\n"
                + "      <operator activated=\"true\" class=\"apply_model\" compatibility=\"6.3.000\" expanded=\"true\" height=\"76\" name=\"Apply Model\" width=\"90\" x=\"581\" y=\"120\">\n"
                + "        <list key=\"application_parameters\"/>\n"
                + "      </operator>\n"
                + "      <connect from_op=\"Create Document (1)\" from_port=\"output\" to_op=\"Process Documents (2)\" to_port=\"documents 1\"/>\n"
                + "      <connect from_op=\"Read Document\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents 1\"/>\n"
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

        safeProcess("subjectivity.rmp", xml);

    }

    /**
     * This method executes the rapidminer process
     */
    private boolean runModel(String text) throws Exception {

        boolean result = false;

        com.Rapidminer.RapidminerProccess rm = new RapidminerProccess(path + "subjectivity.rmp");
        IOContainer ioResult = rm.runproccess();

        ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);

        for (int i = 0; i < resultSet.size(); i++) {
            Example example = resultSet.getExample(i);
            Attribute label = example.getAttributes().get("prediction");
            String labelvalue = example.getValueAsString(label);
            if (labelvalue.equals("subjective")) {
                result = true;
            }

        }

        return result;
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
