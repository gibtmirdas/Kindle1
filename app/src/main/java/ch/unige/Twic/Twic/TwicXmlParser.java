package ch.unige.Twic.Twic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parser for TWiC server responses.
 */
public class TwicXmlParser implements TwicFields{

    /**
     * Parse a TWiC xml response.
     * @param response TWiC xml response.
     * @return Map that contains the xml fields of the response.
     */
    public static Map<String, String[]> parseTwicResponse(String response){
        Map<String, String[]> responseMap = new HashMap<>();
        if(response == null || response.length() == 0)
            return responseMap;
        Document doc = convertXml(response);
        String rootName = doc.getDocumentElement().getTagName();
        Node nNode = doc.getElementsByTagName(rootName).item(0);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            for(String nodeName: FIELDS){
                NodeList element = eElement.getElementsByTagName(nodeName);
                responseMap.put(nodeName, getTextListFromNodeList(element));
            }
        }
        return responseMap;
    }

    /**
     * Parse an Its xml response.
     * @param response Its xml response.
     * @return Map that contains the xml fields of the response.
     */
    public static Map<String, String[]> parseItsResponse(String response){
        Map<String, String[]> responseMap = new HashMap<>();
        if(response == null || response.length() == 0)
            return responseMap;
        Document doc = convertXml(response);
        String rootName = doc.getDocumentElement().getTagName();
        Node nNode = doc.getElementsByTagName(rootName).item(0);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            for(String nodeName: FIELDSITS){
                NodeList element = eElement.getElementsByTagName(nodeName);
                responseMap.put(nodeName, getTextListFromNodeList(element));
            }
        }
        return responseMap;
    }

    /**
     * Parse a Microsoft translate response.
     * @param response Microsoft translate HTTP response.
     * @return The translated text.
     */
    public static String parseMsResponse(String response) {
        response = response.
                replace("/&#39;/g", "'").
                replace("/&quot;/g", "\"").
                replace("/&gt;/g", ">").
                replace("/&lt;/g", "<").
                replace("/&amp;/g", "&");
        response = response.substring(1, response.length());
        if (response.charAt(0) == '"')
            response = response.substring(1, response.length());
        if (response.charAt(response.length() - 1) == '"')
            response = response.substring(0, response.length() - 1);
        return response;
    }

    /**
     * Parse a TWiC language list xml response and record them in the CodeNameMap.
     * @param response xlm language list to parse.
     */
    public static void parseLanguageList(String response){
        Document doc = convertXml(response);
        // set pairs
        NodeList pairs = doc.getElementsByTagName("pair");

        PairsList.add(new LanguagePair(AUTO, AUTO, true));
        for(int i = 0; i < pairs.getLength(); i++){
            Node nNode = pairs.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                String src = ((Element) nNode).getAttribute("src");
                String tgt = ((Element) nNode).getAttribute("tgt");
                boolean trad = ((Element) nNode).getAttribute("trad").equals("true");
                if (!PairsList.containsKey(src))
                    PairsList.add(new LanguagePair(src, AUTO, true));
                LanguagePair srcAuto = new LanguagePair(AUTO, tgt, true);
                if(!PairsList.containsCouple(srcAuto))
                    PairsList.add(srcAuto);
                PairsList.add(new LanguagePair(src, tgt, trad));
            }
        }

        // set codeNames
        NodeList codeNames = doc.getElementsByTagName("language");
        for(int i = 0; i < codeNames.getLength(); i++){
            Node nNode = codeNames.item(i);
            String code = ((Element) nNode).getAttribute("code");
            String name = nNode.getTextContent();
            boolean synt = ((Element) nNode).getAttribute("synt").equals("true");
            CodeNamesMap.put(code, new CodeName(code, name, synt));
        }
        CodeNamesMap.put(AUTO, new CodeName(AUTO, AUTO, true));
    }

    /**
     * Convert an xml response into a Document object.
     * @param response xml response to parse.
     * @return Resulting Document object.
     */
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

    /**
     * Extract the text of each nodes of the NodeList.
     * @param element Node list to process
     * @return Array of all text of the list.
     */
    private static String[] getTextListFromNodeList(NodeList element){
        if (element.getLength() == 0)
            return new String[0];
        String[] toPut = new String[element.getLength()];
        for(int i = 0; i < element.getLength(); i++)
            toPut[i] = element.item(i).getTextContent();
        return toPut;
    }
}
