/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Similarity;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.*;
import org.w3c.dom.*;
import com.Preprocessing.EscapeSpecialCharacters;

public class XmlFileOperations {

    private String similarity = "";
    private String metric = "";
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
        parseInputXml(xml);
        in = new Input();
        in.setSimilarity(similarity);
        in.setMetric(metric);
        if (similarity.equals("advancedsimilarity")) {
            in.setNumTopics(Integer.parseInt(numTopics));
            in.setNumIterations(Integer.parseInt(numIterations));
            in.setOptimization(Integer.parseInt(optimization));
            in.setBurninInterval(Integer.parseInt(burninInterval));
        }
        in.setPosTags(posTags);
        in.setStemmer(stemmer);
        in.setNgrams(Integer.valueOf(ngrams));
        in.setDocumentList(documentList);

        return in;
    }

    
    /**
     * This method parses the xml file 
     */
    private void parseInputXml(String xml) throws Exception {
        EscapeSpecialCharacters preproc = new EscapeSpecialCharacters();
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
        this.similarity = doc.getTagName();
        this.metric = getTextValue(doc, "metric", 1);
        if (similarity.equals("advancedsimilarity")) {
            this.numTopics = getTextValue(doc, "numTopics", 1);
            this.numIterations = getTextValue(doc, "numIterations", 1);
            this.optimization = getTextValue(doc, "optimization", 1);
            this.burninInterval = getTextValue(doc, "burninInterval", 1);
        }
        //preprocessing
        this.posTags = getTextValue(doc, "POStags", 1);
        this.stemmer = getTextValue(doc, "stemmer", 1);
        this.ngrams = getTextValue(doc, "n-grams", 1);

        //documents
        InputDocument inDoc;
        NodeList nl = doc.getElementsByTagName("document");
        for (int i = 1; i <= nl.getLength(); i++) {
            inDoc = new InputDocument();
            inDoc.setId(getAttributeValue(doc, "document", "id", i));
            inDoc.setType(getAttributeValue(doc, "document", "type", i));
            inDoc.setUrl(getAttributeValue(doc, "document", "url", i));
            inDoc.setMediasource(getAttributeValue(doc, "mediasource", "", i));
            String temp = getTextValue(doc, "document", i);
            temp = preproc.escapeHtmlSpecialCharacters(temp);

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
     * this method parse the output of (topic modeling)  xml file  
     */
    public ArrayList<TopicDocument> parseTopicXml(String xml) throws Exception {

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
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xPath.compile("//listOfDocs//document");
        NodeList documentnodelist = (NodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);

        TopicDocument topicdoc;
        Topic topic;
        ArrayList<TopicDocument> topicdocList = new ArrayList<>();
        for (int i = 0; i < documentnodelist.getLength(); i++) {
            Node documentnode = documentnodelist.item(i);
            Element documentElement = (Element) documentnode;
            ArrayList<Topic> topiclist = new ArrayList<>();
            topicdoc = new TopicDocument();
            topicdoc.setId(documentElement.getAttribute("id"));
            NodeList topicnodelist = documentnode.getChildNodes();
            for (int j = 0; j < topicnodelist.getLength(); j++) {
                Node topicNode = topicnodelist.item(j);
                Element topicElement = (Element) topicNode;
                topic = new Topic();
                Attr topicAtrr = topicElement.getAttributeNode("id");
                topic.setId(topicAtrr.getValue());
                Attr percentageAtrr = topicElement.getAttributeNode("percentage");
                String temp = percentageAtrr.getValue();
                temp = temp.replaceAll("%", "");
                double percentage = Double.valueOf(temp);
                percentage = percentage / 100;
                topic.setPercentage(percentage);
                topiclist.add(topic);
            }
            topicdoc.setList(topiclist);
            topicdocList.add(topicdoc);
        }

        return topicdocList;
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
    public String getTopicXml(Input in) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        //modeling job
        Element modelingjobElement = doc.createElement("modelingjob");
        doc.appendChild(modelingjobElement);
        //parameters
        Element parametersElement = doc.createElement("parameters");
        modelingjobElement.appendChild(parametersElement);
        //num of topics
        Element numTopicsElement = doc.createElement("numTopics");
        numTopicsElement.appendChild(doc.createTextNode(String.valueOf(in.getNumTopics())));
        parametersElement.appendChild(numTopicsElement);
        //NumIterations
        Element numIterationsElement = doc.createElement("numIterations");
        numIterationsElement.appendChild(doc.createTextNode(String.valueOf(in.getNumIterations())));
        parametersElement.appendChild(numIterationsElement);
        //optimization
        Element optimizationElement = doc.createElement("optimization");
        optimizationElement.appendChild(doc.createTextNode(String.valueOf(in.getOptimization())));
        parametersElement.appendChild(optimizationElement);
        //burnInterval
        Element burnIntervalElement = doc.createElement("burninInterval");
        burnIntervalElement.appendChild(doc.createTextNode(String.valueOf(in.getBurninInterval())));
        parametersElement.appendChild(burnIntervalElement);

        //preprocessing
        Element preprocessingElement = doc.createElement("preprocessing");
        modelingjobElement.appendChild(preprocessingElement);
        //POSTags
        Element postagElement = doc.createElement("POStags");
        postagElement.appendChild(doc.createTextNode(in.getPosTags()));
        preprocessingElement.appendChild(postagElement);
        //stemmer
        Element stemmerElement = doc.createElement("stemmer");
        stemmerElement.appendChild(doc.createTextNode(in.getStemmer()));
        preprocessingElement.appendChild(stemmerElement);
        //ngrams
        Element ngramsElement = doc.createElement("n-grams");
        ngramsElement.appendChild(doc.createTextNode(String.valueOf(in.getNgrams())));
        preprocessingElement.appendChild(ngramsElement);

        //documents
        Element documentsElement = doc.createElement("documents");
        modelingjobElement.appendChild(documentsElement);
        ArrayList<InputDocument> documentList1 = in.getDocumentList();
        for (InputDocument indoc : documentList1) {
            Element documentElement = doc.createElement("document");
            documentElement.setAttribute("id", indoc.getId());
            documentElement.setAttribute("type", indoc.getType());
            documentElement.setAttribute("url", indoc.getUrl());
            documentElement.setAttribute("mediasource", indoc.getMediasource());
            documentElement.appendChild(doc.createTextNode(indoc.getText()));
            documentsElement.appendChild(documentElement);
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
        transformerFactory = null;
        transformer = null;
        source = null;
        sw = null;
        sr = null;

        return xml;
    }
    
    /**
     * This method creates the output xml file
     */
    public String getOutputXml(ArrayList<OutputSimilarity> list, String metric) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element similarityOutput = doc.createElement("similarityOutput");
        doc.appendChild(similarityOutput);

        //metric element
        Element metricElement = doc.createElement("metric");
        metricElement.appendChild(doc.createTextNode(metric));
        similarityOutput.appendChild(metricElement);

        //result
        Element resultElement = doc.createElement("result");
        similarityOutput.appendChild(resultElement);

        for (OutputSimilarity similarity : list) {
            //pair
            Element pairElement = doc.createElement("pair");
            
            pairElement.setAttribute("firstID", similarity.getFirstId());
            pairElement.setAttribute("secondID", similarity.getSecondId());
            double temp = Double.parseDouble(similarity.getScore());
            BigDecimal bd = new BigDecimal(temp);
            bd = bd.setScale(3, RoundingMode.HALF_UP);
            temp = bd.doubleValue();
            pairElement.setAttribute("score", String.valueOf(temp) );

            resultElement.appendChild(pairElement);

            //dealovate
            pairElement = null;
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
        similarityOutput = null;
        transformerFactory = null;
        transformer = null;
        source = null;
        sw = null;
        sr = null;

        return xml;

    }

}
