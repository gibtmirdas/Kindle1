package ch.unige.kindle1.Twic;

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

    public static Map<String, String[]> parseData(String response) {
        Map<String, String[]> responseMap = new HashMap<String,String[]>();
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));

            String rootName = doc.getDocumentElement().getTagName();
            Node nNode = doc.getElementsByTagName(rootName).item(0);
            switch (rootName){
                case "twicResponse":
                    parseTwicResponse(nNode, responseMap);
                    break;
                case "languagelist":
                    parseLanguagelist(nNode, responseMap);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseMap;
    }

    private static void parseTwicResponse(Node nNode, Map<String, String[]> responseMap){
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            for(String nodeName: FIELDS){
                NodeList element = eElement.getElementsByTagName(nodeName);
                responseMap.put(nodeName, getToPut(element));
            }
        }
    }

    private static void parseLanguagelist(Node nNode, Map<String, String[]> responseMap){

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
