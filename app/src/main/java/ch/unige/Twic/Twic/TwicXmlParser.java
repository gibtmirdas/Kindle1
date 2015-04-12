package ch.unige.Twic.Twic;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 2/27/15.
 */
public class TwicXmlParser implements TwicFields{

    public static Map<String, String[]> parseTwicResponse(String response){
        Document doc = convertXml(response);
        String rootName = doc.getDocumentElement().getTagName();
        Node nNode = doc.getElementsByTagName(rootName).item(0);

        Map<String, String[]> responseMap = new HashMap<>();
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            for(String nodeName: FIELDS){
                NodeList element = eElement.getElementsByTagName(nodeName);
                responseMap.put(nodeName, getToPut(element));
            }
        }
        return responseMap;
    }

    public static void parseLanguagelist(String response){
        Document doc = convertXml(response);
        // set pairs
        NodeList pairs = doc.getElementsByTagName("pair");
        String src, tgt;
        boolean trad;
        for(int i=0; i<pairs.getLength(); i++){
            Node nNode = pairs.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                src = ((Element) nNode).getAttribute("src");
                tgt = ((Element) nNode).getAttribute("tgt");
                trad = ((Element) nNode).getAttribute("trad").equals("true");
                PairsList.add(new LanguagePair(src, tgt, trad));
            }
        }

        // set codeNames
        NodeList codeNames = doc.getElementsByTagName("language");
        String code, name;
        boolean synt;
        for(int i=0; i<codeNames.getLength(); i++){
            Node nNode = codeNames.item(i);
            code = ((Element) nNode).getAttribute("code");
            name = nNode.getTextContent();
            synt = ((Element) nNode).getAttribute("synt").equals("true");
            CodeNamesMap.put(code,new CodeName(code, name, synt));
        }

    }

    private static Document convertXml(String response){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    private static String[] getToPut(NodeList element){
        if (element.getLength() == 0)
            return new String[0];
        String[] toPut;
        toPut = new String[element.getLength()];
        for(int i=0; i<element.getLength(); i++)
            toPut[i] = element.item(i).getTextContent();
        return toPut;
    }
}
