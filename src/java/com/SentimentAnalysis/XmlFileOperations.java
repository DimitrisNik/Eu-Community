package com.SentimentAnalysis;

import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.*;
import org.w3c.dom.*;
import com.Preprocessing.EscapeSpecialCharacters;
import java.util.List;

public class XmlFileOperations {
    /*parameters*/

    private String method;
    private String classifier;
    /*preprocessing*/
    private String posTags;
    private String stemmer;
    String ngrams;
    /*documents*/
    private ArrayList<com.SentimentAnalysis.InputDocument> documentList;
    private com.SentimentAnalysis.Input in;

     /**
     * this method returns the Input of the xml file
     */
    public Input getInput(String xml) throws Exception {
        parseXml(xml);
        this.in = new com.SentimentAnalysis.Input();
        this.in.setMethod(method);
        this.in.setClassifier(classifier);
        this.in.setPosTags(posTags);
        this.in.setStemmer(stemmer);
        this.in.setN_grams(Integer.valueOf(ngrams));
        this.in.setDocumentList(documentList);

        return in;

    }

    /**
     * This method parses the xml file 
     */
    private void parseXml(String xml) throws Exception {
        documentList = new ArrayList<>();
        //replace <?xml version="1.0"?> if exist
        if (xml.contains("<?xml")) {
            xml = xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
        }

        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        dom = db.parse(is);

        Element doc = dom.getDocumentElement();

        method = getTextValue(doc, "method", 1);
        classifier = getTextValue(doc, "classifier", 1);

        //preprocessing
        posTags = getTextValue(doc, "POStags", 1);
        stemmer = getTextValue(doc, "stemmer", 1);
        ngrams = getTextValue(doc, "n-grams", 1);

        //documents
        EscapeSpecialCharacters preproc = new EscapeSpecialCharacters();
        com.SentimentAnalysis.InputDocument inDoc;
        SplitText splitter = new SplitText();
       
        NodeList nl = doc.getElementsByTagName("document");
        for (int i = 1; i <= nl.getLength(); i++) {
            inDoc = new com.SentimentAnalysis.InputDocument();
            inDoc.setId(getAttributeValue(doc, "document", "id", i));
            inDoc.setType(getAttributeValue(doc, "document", "type", i));
            inDoc.setUrl(getAttributeValue(doc, "document", "url", i));
            inDoc.setMediasource(getAttributeValue(doc, "mediasource", "", i));
            String temp = getTextValue(doc, "document", i);
            temp = preproc.escapeHtmlSpecialCharacters(temp);
            List<String> splitText = splitter.getSplitText(temp);
           
            ArrayList<Sentence> sentences = new ArrayList<>();
            int idSentence  = 0;            
            for(String s:splitText){   
                idSentence++;             
                Sentence sentence = new Sentence();
                sentence.setIdSentnece(String.valueOf(idSentence));
                sentence.setText(s);
                sentences.add(sentence);
            }
            inDoc.setSentences(sentences);
            documentList.add(inDoc);           
            
        }

        //dealocate
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
        nl = null;

        return value;
    }

    /**
     * This method creates the output xml file
     */
    public String getOutputXml(ArrayList<Output> resultList) throws Exception {        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element sentimentOutputElement = doc.createElement("sentimentOutput");
        doc.appendChild(sentimentOutputElement);

        //documentsElement
        Element documentsElement = doc.createElement("documents");
        sentimentOutputElement.appendChild(documentsElement);

        // document elements
        for (Output r : resultList) {
            Element document = doc.createElement("document");
            document.setAttribute("id", r.getIdDocument());
            Element sentencesElement = doc.createElement("sentences");
            ArrayList<SentenceOutput> sentencesResult = r.getSentencesResult();
            for(SentenceOutput sentences:sentencesResult){                
                Element sentenceElement = doc.createElement("sentence");
                sentenceElement.setAttribute("id", sentences.getIdSentence());
                Element sentenceTextElement = doc.createElement("text");
                sentenceTextElement.appendChild(doc.createTextNode(sentences.getText()));
                Element label = doc.createElement("label");
                label.appendChild(doc.createTextNode(sentences.getLabel()));
                sentenceElement.appendChild(sentenceTextElement);
                sentenceElement.appendChild(label);
                sentencesElement.appendChild(sentenceElement);
            }   
            document.appendChild(sentencesElement);
            documentsElement.appendChild(document);

            //dealocate
            document = null;
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
        sentimentOutputElement = null;
        documentsElement = null;
        transformerFactory = null;
        transformer = null;
        source = null;
        sw = null;
        sr = null;

        return xml;

    }

}
