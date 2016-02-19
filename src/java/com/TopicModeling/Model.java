package com.TopicModeling;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class implements lda algorithm.
 */
public class Model {

    private ParallelTopicModel model;

//    private final double ALPHA_T = 1.0;
//    private final double BETA_W = 0.01;
    private final int THREADS = Runtime.getRuntime().availableProcessors();
    private final int numberOfToken = 5;
    int numTopics;
    int numIterations;
    int optimizeation;
    int burninInterval;

    /**
     * This method sets parameters for lda algorithm
     */
    public void setParameters(Input in) {
        this.numTopics = in.getNumTopics();
        this.numIterations = in.getNumIterations();
        this.optimizeation = in.getOptimization();
        this.burninInterval = in.getBurninInterval();
    }

    /**
     * This method creates the pipeline
     */
    private InstanceList getPipeline(String[] str) throws Exception {

        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        instances.addThruPipe(new StringArrayIterator(str));

        return instances;
    }

    /**
     * This method sets the parameters in model
     */
    private void setmodel(InstanceList instances) throws Exception {

        model = new ParallelTopicModel(numTopics);

//        model = new ParallelTopicModel(numTopics, ALPHA_T, BETA_W);
        model.addInstances(instances);
        model.setNumThreads(THREADS);
        model.setOptimizeInterval(optimizeation);
        model.setBurninPeriod(burninInterval);
        model.setNumIterations(numIterations);
        //model.estimate();

    }

    /**
     * This method returns the list of topics
     */
    public ArrayList<Topic> getTopic(InstanceList instances) throws Exception {
        ArrayList<Topic> topicsList = new ArrayList<>();
        setmodel(instances);
        Alphabet dataAlphabet = instances.getDataAlphabet();
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        Topic topic;
        int counter = 0;
        for (int i = 0; i < numTopics; i++) {
            counter++;
            ArrayList<Token> tokenlist = new ArrayList<>();
            topic = new Topic();
            topic.setId_topic(String.valueOf(counter));

            //set token of topic
            Token token;
            Iterator<IDSorter> iterator = topicSortedWords.get(i).iterator();
            int rank = 0;
            while (iterator.hasNext() && rank < numberOfToken) {
                token = new Token();
                IDSorter idCountPair = iterator.next();
                token.setId(String.valueOf(idCountPair.getID()));
                token.setText(dataAlphabet.lookupObject(idCountPair.getID()).toString());
                tokenlist.add(token);
                token = null;
                rank++;
            }
            topic.setTokenList(tokenlist);
            topicsList.add(topic);

            //dealocate
            tokenlist = null;
            topic = null;
        }

        //dealocate
        instances = null;
        dataAlphabet = null;
        topicSortedWords = null;

        return topicsList;

    }

    /**
     * This method executes the lda algorithm
     */
    public Output runModel(Input input) throws Exception {
        setParameters(input);
        ArrayList<InputDocument> documentList = input.getDocumentList();

        //preprocessing
        ArrayList<String> preprocessingdocumentsList;

        Preprocessing preprocessing = new Preprocessing();
        preprocessingdocumentsList = preprocessing.run(input);
        //set an array with documents and get Topcs  
        String text[] = new String[documentList.size()];
        for (int i = 0; i < preprocessingdocumentsList.size(); i++) {
            text[i] = preprocessingdocumentsList.get(i);
        }

        InstanceList instances = getPipeline(text);

        ArrayList<Topic> topicList = getTopic(instances);

        //ranked Docs      
        ArrayList<RankedDoc> rankedDocsList = new ArrayList<>();
        for (Topic topic : topicList) {
            OutputTopic outTopic = new OutputTopic();
            outTopic.setId_topic(topic.getId_topic());
            ArrayList<OutputDocument> outdocList = new ArrayList<>();
            for (InputDocument indoc : documentList) {
                OutputDocument outdoc = new OutputDocument();
                outdoc.setId_document(indoc.getId());
                int numOfWords = getnumOfWords(topic, indoc.getText());
                outdoc.setNumWords(numOfWords);
                outdocList.add(outdoc);

                //dealocate
                indoc = null;
                outdoc = null;
            }

            RankedDoc rank = new RankedDoc();
            rank.setDocumentslist(outdocList);
            rank.setOutTopic(outTopic);
            rankedDocsList.add(rank);

            //dealocate
            topic = null;
            outTopic = null;
            outdocList = null;
            rank = null;
        }

        ArrayList<TopicSimilarity> topicSimilarityList = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            ArrayList<OutputTopic> outtopicList = new ArrayList<>();
            InputDocument indoc = documentList.get(i);
            OutputDocument outdoc = new OutputDocument();
            outdoc.setId_document(indoc.getId());

            TopicInferencer inferencer = model.getInferencer();
            double[] topicProbabilities = inferencer.getSampledDistribution(instances.get(i), numIterations, 1, burninInterval);

            for (int j = 0; j < topicProbabilities.length; j++) {
                Topic topic = topicList.get(j);
                OutputTopic outTopic = new OutputTopic();
                outTopic.setId_topic(topic.getId_topic());
                outTopic.setPercentage(topicProbabilities[j]);
                outtopicList.add(outTopic);
                //dealocate
                topic = null;
                outTopic = null;
            }
            TopicSimilarity topicSimilarity = new TopicSimilarity();
            topicSimilarity.setTopiclist(outtopicList);
            topicSimilarity.setOutdoc(outdoc);
            topicSimilarityList.add(topicSimilarity);

            //dealocate
            outtopicList = null;
            indoc = null;
            outdoc = null;
        }

        Output output = new Output(topicList, rankedDocsList, topicSimilarityList);

        //dealocate 
        documentList = null;
        preprocessing = null;
        preprocessingdocumentsList = null;
        topicList = null;
        rankedDocsList = null;
        topicSimilarityList = null;

        return output;
    }

    /**
     * This method finds the numOfWords parameter
     */
    private int getnumOfWords(Topic topic, String document) throws Exception {
        int numOfWords = 0;

        ArrayList<Token> tokenList = topic.getTokenList();
        for (Token token : tokenList) {
            String tokenstr = token.getText();
            //isws na min xriazetai
            tokenstr = tokenstr.replace("_", " ");

            Pattern pattern = Pattern.compile(tokenstr);
            Matcher matcher = pattern.matcher(document);
            while (matcher.find()) {
                numOfWords++;
            }

            //dealocate
            token = null;
            pattern = null;
            matcher = null;
            tokenstr = null;
        }

        tokenList = null;

        return numOfWords;

    }

}
