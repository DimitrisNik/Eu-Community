package com.Similarity;

import com.rapidminer.Process;
import com.Logs.OtherException;
import com.Rapidminer.*;
import com.rapidminer.example.*;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.similarity.ExampleSet2SimilarityExampleSet;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.OperatorService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SimilarityProcess {

    private static final String path = "C:\\Test\\temp";
    private static final String path_dictionary = "C:\\Test\\dictionary.txt";

    public ArrayList<OutputSimilarity> run(Input in) throws Exception {
        if (in.getSimilarity().equals("simplesimilarity")) {
            return getSimpleSimilarity(in);
        } else if (in.getSimilarity().equals("advancedsimilarity")) {
            return getAdvanceSimilarity(in);
        } else {
            throw new com.Logs.OtherException("Similarity method not exist");
        }
    }

     /**
     * this method return the results for  simple similarity process
     */
    private ArrayList<OutputSimilarity> getSimpleSimilarity(Input in) throws Exception {

        String rmprocess = createSimpleSimilarityProcess(in);
        safeProcess(rmprocess);

        RapidminerProccess rm = new RapidminerProccess(path + "\\preproccesing_SipleSimilarity.rmp");
        IOContainer ioResult = rm.runproccess();
        ArrayList<OutputSimilarity> similarityList = getSimilarity(ioResult, in.getMetric());

        //dealocate
        rmprocess = null;
        rm = null;
        ioResult = null;

        return similarityList;
    }

    /**
     * this method return the results for  advance similarity process
     */
    private ArrayList<OutputSimilarity> getAdvanceSimilarity(Input in) throws Exception {
        //call topic modeling web service
        XmlFileOperations xo = new XmlFileOperations();
        String topicXmlrequest = xo.getTopicXml(in); //create topic modeling input
        TopicModeling tm = new TopicModeling();
        String topicXmlresponse = tm.sendPost(topicXmlrequest);//response of wb topocmodeling
        ArrayList<TopicDocument> doclist = xo.parseTopicXml(topicXmlresponse);

        //create example set and run process
        ExampleSet exampleset = CreateExampleset(doclist, in.getNumTopics());
        Process process = createAdvanceSimilarityProcess(in);
        IOContainer ioResult = process.run(new IOContainer(exampleset));

        ArrayList<OutputSimilarity> similaritylist = getSimilarity(ioResult, in.getMetric());

        return similaritylist;
    }

    /**
     * this method return the results of rapiminer process
     */
    private ArrayList<OutputSimilarity> getSimilarity(IOContainer ioResult, String metric) throws Exception {
        String first_id;
        String second_id;
        String distance;
        OutputSimilarity sim;
        ArrayList<OutputSimilarity> list = new ArrayList<>();

        ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);

        for (int i = 0; i < resultSet.size(); i++) {
            Example SimilarityExample = resultSet.getExample(i);
            Attribute firstIdAttribute = SimilarityExample.getAttributes().get("FIRST_ID");
            Attribute secondIdAttribute = SimilarityExample.getAttributes().get("SECOND_ID");
            Attribute similarityAttribute;
            if (metric.equals("Euclidean")) {
                similarityAttribute = SimilarityExample.getAttributes().get("DISTANCE");
            } else {
                similarityAttribute = SimilarityExample.getAttributes().get("SIMILARITY");
            }
            first_id = SimilarityExample.getValueAsString(firstIdAttribute);
            second_id = SimilarityExample.getValueAsString(secondIdAttribute);
            distance = SimilarityExample.getValueAsString(similarityAttribute);
            double fistid = Double.valueOf(first_id);
            double secid = Double.valueOf(second_id);
            if (fistid < secid) {
                sim = new OutputSimilarity(first_id, second_id, distance);
                list.add(sim);
            }

            //dealocate
            sim = null;
            SimilarityExample = null;
            firstIdAttribute = null;
            secondIdAttribute = null;
            similarityAttribute = null;

        }

        return list;
    }

    //simple similarity
    /**
     * this method create a rapidminer process for simple similarity 
     */
    protected String createSimpleSimilarityProcess(Input in) throws OtherException {
        ArrayList<InputDocument> documentList = in.getDocumentList();

        int counter = 1;
        String xml
                = //start
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<process version=\"5.3.013\">\n"
                + "  <context>\n"
                + "    <input/>\n"
                + "    <output/>\n"
                + "    <macros/>\n"
                + "  </context>\n"
                + "  <operator activated=\"true\" class=\"process\" compatibility=\"5.3.013\" expanded=\"true\" name=\"Process\">\n"
                + "    <process expanded=\"true\">\n";
        //create document
        xml = xml
                + " <operator activated=\"true\" class=\"text:create_document\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Create Document\" width=\"90\" x=\"45\" y=\"30\">\n";
        xml = xml
                + " <parameter key=\"text\" value=\"" + documentList.get(0).getText() + "\"/>";
        xml = xml + "</operator> \n";
        if (documentList.size() > 1) {

            for (int i = 1; i < documentList.size(); i++) {
                counter++;
                xml = xml
                        + "<operator activated=\"true\" class=\"text:create_document\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Create Document (" + counter + ")\" width=\"90\" x=\"45\" y=\"120\">\n";
                xml = xml
                        + " <parameter key=\"text\" value=\"" + documentList.get(i).getText() + "\"/>";
                xml = xml + "</operator> \n";
            }
        }

        //process_documents
        xml = xml
                + "<operator activated=\"true\" class=\"text:process_documents\" compatibility=\"5.3.002\" expanded=\"true\" height=\"112\" name=\"Process Documents\" width=\"90\" x=\"313\" y=\"30\">\n"
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
                + "<operator activated=\"true\" class=\"text:filter_stopwords_english\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Filter Stopwords (English)\" width=\"90\" x=\"179\" y=\"30\"/>\n";
        //stemm
        if (in.getStemmer().equals("Porter")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"text:stem_porter\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Porter)\" width=\"90\" x=\"447\" y=\"30\"/>\n";
        } else if (in.getStemmer().equals("Lovins")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"text:stem_lovins\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Stem (Lovins)\" width=\"90\" x=\"447\" y=\"30\"/>\n";
        } else {
            throw new OtherException("No suitable method for stem");
        }
        //n-grams
        xml = xml
                + "<operator activated=\"true\" class=\"text:generate_n_grams_terms\" compatibility=\"5.3.002\" expanded=\"true\" height=\"60\" name=\"Generate n-Grams (Terms)\" width=\"90\" x=\"648\" y=\"30\">\n";
        xml = xml
                + "            <parameter key=\"max_length\" value=\"" + in.getNgrams() + "\"/>\n";
        xml = xml + "</operator>\n";

        //connect operators
        //incide process_documents
        xml = xml
                + "<connect from_port=\"document\" to_op=\"Tokenize\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize\" from_port=\"document\" to_op=\"Tokenize (2)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Tokenize (2)\" from_port=\"document\" to_op=\"Transform Cases\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Transform Cases\" from_port=\"document\" to_op=\"Filter Tokens (by Length)\" to_port=\"document\"/> \n"
                + " <connect from_op=\"Filter Tokens (by Length)\" from_port=\"document\" to_op=\"Filter Stopwords (Dictionary)\" to_port=\"document\"/> \n"
                + "<connect from_op=\"Filter Stopwords (Dictionary)\" from_port=\"document\" to_op=\"Filter Stopwords (English)\" to_port=\"document\"/> \n";

        if (in.getStemmer().equals("Porter")) {
            xml = xml + "<connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Porter)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Porter)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        } else if (in.getStemmer().equals("Lovins")) {
            xml = xml
                    + " <connect from_op=\"Filter Stopwords (English)\" from_port=\"document\" to_op=\"Stem (Lovins)\" to_port=\"document\"/>\n"
                    + "<connect from_op=\"Stem (Lovins)\" from_port=\"document\" to_op=\"Generate n-Grams (Terms)\" to_port=\"document\"/>\n";
        }
        xml = xml
                + "          <connect from_op=\"Generate n-Grams (Terms)\" from_port=\"document\" to_port=\"document 1\"/>\n"
                + "          <portSpacing port=\"source_document\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 1\" spacing=\"0\"/>\n"
                + "          <portSpacing port=\"sink_document 2\" spacing=\"0\"/>";
        xml = xml
                + "</process>\n"
                + "</operator>\n";

//main process
        if (in.getMetric().equals("Euclidean")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"data_to_similarity_data\" compatibility=\"5.3.013\" expanded=\"true\" height=\"60\" name=\"Data to Similarity Data\" width=\"90\" x=\"447\" y=\"30\">\n"
                    + "<parameter key=\"measure_types\" value=\"NumericalMeasures\"/> \n"
                    + "</operator> \n";
        } else if (in.getMetric().equals("Cosine")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"data_to_similarity_data\" compatibility=\"5.3.013\" expanded=\"true\" height=\"60\" name=\"Data to Similarity Data\" width=\"90\" x=\"447\" y=\"30\">\n"
                    + "        <parameter key=\"measure_types\" value=\"NumericalMeasures\"/>\n"
                    + "        <parameter key=\"numerical_measure\" value=\"CosineSimilarity\"/>\n"
                    + "</operator>\n";
        } else if (in.getMetric().equals("Jaccard")) {
            xml = xml
                    + "<operator activated=\"true\" class=\"data_to_similarity_data\" compatibility=\"5.3.013\" expanded=\"true\" height=\"60\" name=\"Data to Similarity Data\" width=\"90\" x=\"447\" y=\"30\">\n"
                    + "         <parameter key=\"measure_types\" value=\"NumericalMeasures\"/>\n"
                    + "         <parameter key=\"numerical_measure\" value=\"JaccardSimilarity\"/>\n"
                    + " </operator>\n";
        } else {
            new com.Logs.OtherException("No accepting Similarity method");
        }

        //conections
        xml = xml
                + "<connect from_op=\"Create Document\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents 1\"/>\n";
//if exist more than one documents
        counter = 1;
        if (documentList.size() > 1) {
            for (int i = 1; i < documentList.size(); i++) {
                counter++;
                xml = xml
                        + " <connect from_op=\"Create Document (" + counter + ")\" from_port=\"output\" to_op=\"Process Documents\" to_port=\"documents " + counter + "\"/>\n";
            }
        }

        xml = xml
                + "<connect from_op=\"Process Documents\" from_port=\"example set\" to_op=\"Data to Similarity Data\" to_port=\"example set\"/>\n"
                + "<connect from_op=\"Data to Similarity Data\" from_port=\"similarity example set\" to_port=\"result 1\"/>\n"
                + "<portSpacing port=\"source_input 1\" spacing=\"0\"/>\n"
                + "<portSpacing port=\"sink_result 1\" spacing=\"0\"/>\n"
                + "<portSpacing port=\"sink_result 2\" spacing=\"0\"/>";

//end process
        xml = xml
                + "</process>\n"
                + "</operator>\n"
                + "</process>";

        //dealocate 
        documentList = null;

        return xml;
    }

    protected void safeProcess(String xml) throws Exception {

        File file = new File(path + "\\preproccesing_SipleSimilarity.rmp");

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

    //advance similarity
    /**
     * this method create rapidminer process for advance similarity 
     */
    public Process createAdvanceSimilarityProcess(Input in) throws OperatorCreationException, OperatorException, IOException {
//        RapidMiner.init();

        Operator silimarityOperator = OperatorService.createOperator(ExampleSet2SimilarityExampleSet.class);
        if (in.getMetric().equals("Euclidean")) {
            silimarityOperator.setParameter("measure_types", "NumericalMeasures");
        } else if (in.getMetric().equals("Cosine")) {
            silimarityOperator.setParameter("measure_types", "NumericalMeasures");
            silimarityOperator.setParameter("numerical_measure", "CosineSimilarity");
        } else if (in.getMetric().equals("Jaccard")) {
            silimarityOperator.setParameter("measure_types", "NumericalMeasures");
            silimarityOperator.setParameter("numerical_measure", "JaccardSimilarity");
        } else {
            new com.Logs.OtherException("No accepting Similarity method");
        }

        Process process = new Process();
        process.getRootOperator().getSubprocess(0).addOperator(silimarityOperator);

        process.getRootOperator().getSubprocess(0).getInnerSources().getPortByIndex(0).connectTo(silimarityOperator.getInputPorts().getPortByIndex(0));
        silimarityOperator.getOutputPorts().getPortByIndex(0).connectTo(process.getRootOperator().getSubprocess(0).getInnerSinks().getPortByIndex(0));
        File f = new File(path + "\\preproccesing_AdvanceSimilarity.rmp");
        if (!f.exists()) {
            f.createNewFile();
        }
        process.save(f);

        return process;

    }

    /**
     * this method create an exampleset for advance similarity process
     */
    public ExampleSet CreateExampleset(ArrayList<TopicDocument> doclist, int numofTopics) {
        // construct attribute set
        Attribute[] attributes = new Attribute[numofTopics];

        int counter = 0;
        for (int i = 0; i < numofTopics; i++) {
            counter = i + 1;
            attributes[i] = AttributeFactory.createAttribute("Topic" + counter, Ontology.NUMERICAL);
        }

        //create table
        MemoryExampleTable table = new MemoryExampleTable(attributes);
        Double[] percentage;
        for (TopicDocument doc : doclist) {
            ArrayList<Topic> topiclist = doc.getList();
            DataRowFactory ROW_FACTORY = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_SPARSE_ARRAY);

            percentage = new Double[topiclist.size()];

            for (int i = 0; i < topiclist.size(); i++) {
                Topic topic = topiclist.get(i);
                percentage[i] = topic.getPercentage();
            }
            DataRow row = ROW_FACTORY.create(percentage, attributes);
            table.addDataRow(row);
        }
        ExampleSet exampleSet = table.createExampleSet();

        return exampleSet;
    }

}
