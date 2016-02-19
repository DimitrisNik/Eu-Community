package com.TopicModeling;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.*;
import org.w3c.dom.*;

public class XmlFileOperations {

    private String numTopics = "";
    private String numIterations = "";
    private String optimization = "";
    private String burninInterval = "";
    private String posTags;
    private String stemmer;
    private String ngrams;
    private ArrayList<InputDocument> documentList;
    private Input in;

      /**
     * this method returns the Input of the xml file
     */
    public Input getInput(String xml) throws Exception {
        parseXml(xml);
        in = new Input();
        in.setNumTopics(Integer.parseInt(numTopics));
        in.setNumIterations(Integer.parseInt(numIterations));
        in.setOptimization(Integer.parseInt(optimization));
        in.setBurninInterval(Integer.parseInt(burninInterval));
        in.setPosTags(posTags);
        in.setStemmer(stemmer);
        in.setNgrams(Integer.valueOf(ngrams));
        in.setDocumentList(documentList);

        return in;
    }

    /**
     * This method parses the xml file  
     */
    private void parseXml(String xml) throws Exception {
        //replace <?xml version="1.0"?> if exist
        if (xml.contains("<?xml")) {
            xml = xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
        }
        documentList = new ArrayList<>();

        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));

        dom = db.parse(is);

        Element doc = dom.getDocumentElement();

        this.numTopics = getTextValue(doc, "numTopics", 1);
        this.numIterations = getTextValue(doc, "numIterations", 1);
        this.optimization = getTextValue(doc, "optimization", 1);
        this.burninInterval = getTextValue(doc, "burninInterval", 1);

        //preprocessing
        this.posTags = getTextValue(doc, "POStags", 1);
        this.stemmer = getTextValue(doc, "stemmer", 1);
        this.ngrams = getTextValue(doc, "n-grams", 1);

        //documents
        Preprocessing preproc = new Preprocessing();
        com.Preprocessing.EscapeSpecialCharacters preproc2 = new com.Preprocessing.EscapeSpecialCharacters();
        InputDocument inDoc;
        NodeList nl = doc.getElementsByTagName("document");
        for (int i = 1; i <= nl.getLength(); i++) {
            inDoc = new InputDocument();
            inDoc.setId(getAttributeValue(doc, "document", "id", i));
            inDoc.setType(getAttributeValue(doc, "document", "type", i));
            inDoc.setUrl(getAttributeValue(doc, "document", "url", i));
            inDoc.setMediasource(getAttributeValue(doc, "mediasource", "", i));
            String temp = getTextValue(doc, "document", i);
            temp = preproc2.escapeHtmlSpecialCharacters(temp);
            inDoc.setText(temp);
            documentList.add(inDoc);
            inDoc = null;
        }

        //dealoate
        dom = null;
        dbf = null;
        db = null;
        is = null;
        nl = null;
    }

    /**
     * This method returns the value of an element
     */
    private String getTextValue(Element doc, String tag, int numOfElements) {
        String value = "";
        NodeList nl;
        nl = doc.getElementsByTagName(tag);

        if (nl.getLength() > 0 && nl.item(numOfElements - 1).hasChildNodes()) {
            value = nl.item(numOfElements - 1).getFirstChild().getNodeValue();

        }

        //dealocate
        nl = null;

        return value;
    }

    /**
     * This method returns the value of an attribute
     */
    private String getAttributeValue(Element doc, String ElementTag, String AttributeTag, int numOfElements) {
        String value = "";
        NodeList nl;
        nl = doc.getElementsByTagName(ElementTag);
        if (nl.getLength() > 0 && nl.item(numOfElements - 1).hasChildNodes()) {
            value = nl.item(numOfElements - 1).getAttributes().getNamedItem(AttributeTag).getNodeValue();
        }

        //dealocate
        nl = null;

        return value;
    }

    /**
     * This method creates an xml file for topic modeling web service
     */
    public String getOutputXml(Output output) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element modelingOutputElement = doc.createElement("modelingOutput");
        doc.appendChild(modelingOutputElement);

        //listOfTopics
        Element listOfTopicsElement = doc.createElement("listOfTopics");
        modelingOutputElement.appendChild(listOfTopicsElement);
        ArrayList<Topic> topicList = output.getTopicList();
        for (Topic topic : topicList) {
            //topic
            Element topicElement = doc.createElement("topic");
            topicElement.setAttribute("id", topic.getId_topic());
            listOfTopicsElement.appendChild(topicElement);
            //token
            ArrayList<Token> tokenList = topic.getTokenList();
            for (Token token : tokenList) {
                Element tokenElement = doc.createElement("token");
                tokenElement.setAttribute("id", token.getId());
                tokenElement.appendChild(doc.createTextNode(token.getText()));
                topicElement.appendChild(tokenElement);

                //dealocate
                token = null;
                tokenElement = null;
            }

            //dealocate
            topic = null;
            topicElement = null;
            tokenList = null;
        }

        //rankedDocs
        ArrayList<RankedDoc> rankedDocsList = output.getRankedDocsList();
        Element rankedDocsElement = doc.createElement("rankedDocs");
        modelingOutputElement.appendChild(rankedDocsElement);

        for (RankedDoc rankedDoc : rankedDocsList) {
            //topic
            Element rtopicElement = doc.createElement("topic");
            rtopicElement.setAttribute("id", rankedDoc.getOutTopic().id_topic);
            rankedDocsElement.appendChild(rtopicElement);
            //documents
            Element rdocumentsElement = doc.createElement("documents");
            rtopicElement.appendChild(rdocumentsElement);
            //document
            ArrayList<OutputDocument> documentslist = rankedDoc.getDocumentslist();
            for (OutputDocument outdoc : documentslist) {
                String numWord = String.valueOf(outdoc.getNumWords());
                Element rdocumentElement = doc.createElement("document");
                rdocumentElement.setAttribute("id", outdoc.getId_document());
                rdocumentElement.setAttribute("numWords", numWord);
                rdocumentsElement.appendChild(rdocumentElement);
            }
        }

        //listOfDocs
        ArrayList<TopicSimilarity> listOfdocs = output.getTopicSimilarityList();
        Element listOfDocsElement = doc.createElement("listOfDocs");
        modelingOutputElement.appendChild(listOfDocsElement);
        //documents
        Element sdocumentsElement = doc.createElement("documents");
        listOfDocsElement.appendChild(sdocumentsElement);

        for (TopicSimilarity similarityDoc : listOfdocs) {
            //document
            Element sdocument = doc.createElement("document");
            sdocument.setAttribute("id", similarityDoc.getOutdoc().getId_document());
            sdocumentsElement.appendChild(sdocument);
            //topic
            ArrayList<OutputTopic> topiclist = similarityDoc.getTopiclist();
            for (OutputTopic topic : topiclist) {
                double temp = topic.getPercentage();
                temp = temp * 100;
                 BigDecimal bd = new BigDecimal(temp);
                 bd = bd.setScale(2, RoundingMode.HALF_UP);
                 temp = bd.doubleValue();
                String percentage = String.valueOf(temp) + "%";
                Element stopic = doc.createElement("topic");
                stopic.setAttribute("id", topic.getId_topic());
                stopic.setAttribute("percentage", percentage);
                sdocument.appendChild(stopic);
            }
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        doc.setXmlStandalone(true);
        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(source, sr);
        String xml = sw.toString();

        //dealocate
        docFactory = null;
        docBuilder = null;
        doc = null;
        modelingOutputElement = null;
        listOfTopicsElement = null;
        topicList = null;
        rankedDocsList = null;
        rankedDocsElement = null;
        listOfdocs = null;
        transformerFactory = null;
        transformer = null;
        source = null;
        sw = null;
        sr = null;

        return xml;

    }

}
