/*
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */

package com.nwcg.icbs.yantra.util.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGMDTOHelperDocument;

/**
 * A helper class providing methods for XML document processing.
 * All methods are static, object of this class cannot be created.
 *
 */
public class XMLUtil {
    /**
     *	Avoid instantiating an object
     */
    private XMLUtil() {}
    
    /**
     *	Create a new blank XML Document
     *	@throws ParserConfigurationException
     */
    public static Document newDocument()
    throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder docBuilder = fac.newDocumentBuilder();
		
		return docBuilder.newDocument();
    }
    
    /**
     * Parse an XML string or a file, to return the Document.
     *
     * @param inXML if starts with '&lt;', it is an XML string; otherwise it should be an XML file name.
     *
     * @return the Document object generated
     * @throws ParserConfigurationException when XML parser is not properly configured.
     * @throws SAXException when failed parsing XML string.
     * @throws IOException
     */
    public static Document getDocument( String inXML )
    throws ParserConfigurationException, SAXException, IOException {
        if( (inXML != null) ) {
            inXML = inXML.trim();
            if ( inXML.length() > 0 ) {
                if ( inXML.startsWith("<")) {
                    StringReader strReader = new StringReader(inXML);
                    InputSource iSource = new InputSource(strReader);
                    return getDocument( iSource );
                }
                
                // It's a file
                FileReader inFileReader = new FileReader( inXML );
                Document retVal = null;
                try {
                    InputSource iSource = new InputSource( inFileReader );
                    retVal = getDocument( iSource );
                } finally {
                    inFileReader.close();
                }
                return retVal;
            }
        }
        return null;
    }
    
    /**
     *	Generate a Document object according to InputSource object.
     * @throws ParserConfigurationException when XML parser is not properly configured.
     * @throws SAXException when failed parsing XML string.
     * @throws IOException
     */
    public static Document getDocument( InputSource inSource )
    throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

        return dbdr.parse( inSource );
    }
    
    /**
     *	Generate a Document object according to InputStream object.
     * @throws ParserConfigurationException when XML parser is not properly configured.
     * @throws SAXException when failed parsing XML string.
     * @throws IOException
     */
    public static Document getDocument( InputStream inStream )
    throws ParserConfigurationException, SAXException, IOException {
        Document retDoc = getDocument( new InputSource(new InputStreamReader(inStream)));
        inStream.close();
        return retDoc;
    }
    
    /**
     * Parse an XML file, to return the Document.
     *
     * @deprecated use getDocument(String) instead.
     *
     * @return the Document object generated
     * @throws ParserConfigurationException when XML parser is not properly configured.
     * @throws SAXException when failed parsing XML string.
     * @throws IOException
     */
    public static Document getDocument( String inXMLFileName, boolean isFile )
    throws ParserConfigurationException, SAXException, IOException {
        if( (inXMLFileName != null) && (!inXMLFileName.equals("")) ) {
            FileReader inFileReader = new FileReader( inXMLFileName );
            InputSource iSource = new InputSource( inFileReader );
            Document doc = getDocument( iSource );
            inFileReader.close();
            return doc;
        }
        return null;
    }
    
    /**
     *	Create a Document object with input as the name of document element.
     *
     *	@param docElementTag: the document element name.
     *	@throws ParserConfigurationException
     */
    public static Document createDocument(String docElementTag)
    throws ParserConfigurationException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

        Document doc = dbdr.newDocument();
        Element ele = doc.createElement(docElementTag);
        doc.appendChild(ele);
        return doc;
    }
    
    /**
     *	Create a Document object with input node 
     *	@param docNode: the Node to create a document from.
     *	@throws ParserConfigurationException
     */
    public static Document createDocument(Node docNode)
    throws ParserConfigurationException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();
        Document doc = dbdr.newDocument();
        Element inElement = (Element)doc.importNode(docNode,true);
        doc.appendChild(inElement);
        return doc;
    }
    
    /**
     *  Merges document doc2 in to doc1.
     * For e.g., <p>
     * if doc1 =   &lt;Root1>&lt;A1/>&lt;/Root1> <p>
     * & doc2 =  &lt;Root2>&lt;B1/>&lt;Root2> <p>
     *  then the merged Doc will be
     *  doc1 =  &lt;Root1>&lt;A1/>&lt;B1/>&lt;/Root1>
     * @param doc1
     * @param doc2
     * @return
     * @deprecated use  addDocument(Document doc1,Document doc2, boolean ignoreRoot)
     */
    public static Document addDocument(Document doc1,Document doc2) {
        Element  rt1 = doc1.getDocumentElement();
        Element  rt2 = doc2.getDocumentElement();
        
        NodeList nlst2 = rt2.getChildNodes();
        int len = nlst2.getLength();
        Node nd = null;
        for (int i=0; i < len; i++) {
            nd = doc1.importNode(nlst2.item(i), true);
            rt1.appendChild(nd);
        }
        return doc1;
    }
    
    
    /**
     * Merges document doc2 in to doc1. Root node of doc2 is included only if ignoreRoot flag is set to false.
     * <p/>
     * For e.g., <p>
     * if doc1 =   &lt;Root1>&lt;A1/>&lt;/Root1> <p>
     * & doc2 =  &lt;Root2>&lt;B1/>&lt;Root2> <p>
     * then the merged Doc will be
     * doc1 =  &lt;Root1>&lt;A1/><B>&lt;Root2>&lt;B1/>&lt;Root2></B>&lt;/Root1>  <B>if ignoreRoot = false</B> <p>
     * <B>if ignoreRoot = true</B> then the merged Doc will be <p>
     * doc1 =  &lt;Root1>&lt;A1/><B>&lt;B1/></B>&lt;/Root1>
     *
     * @param doc1
     * @param doc2
     * @param ignoreRoot ignores root element of doc2 in the merged doc.
     * @return
     */
    public static Document addDocument(Document doc1, Document doc2, boolean ignoreRoot) {
        Element rt1 = doc1.getDocumentElement();
        Element rt2 = doc2.getDocumentElement();
        if (!ignoreRoot) {
            Node nd = doc1.importNode(rt2, true);
            rt1.appendChild(nd);
            return doc1;
        }
        NodeList nlst2 = rt2.getChildNodes();
        int len = nlst2.getLength();
        Node nd = null;
        for (int i = 0; i < len; i++) {
            nd = doc1.importNode(nlst2.item(i), true);
            rt1.appendChild(nd);
        }
        return doc1;
    }
    
    
    /**
     * Create a new Document with the given Element as the root node.
     * @param inElement
     * @return
     * @throws Exception
     */
    public static Document getDocumentForElement(Element inElement) throws
        Exception {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

        Document doc = dbdr.newDocument();
        Element docElement = doc.createElement(inElement.getNodeName());
        doc.appendChild(docElement);
        copyElement(doc, inElement, docElement);
        return doc;
    }
    
    
    
    
    
    /**
     * Returns a formatted XML string for the Node, using encoding 'iso-8859-1'.
     *
     * @param node   a valid document object for which XML output in String form is required.
     *
     * @return the formatted XML string.
     */
    
    public static String serialize( Node node ) {
        return serialize(node, "iso-8859-1", true);
    }
    
    /**
     *	Return a XML string for a Node, with specified encoding and indenting flag.
     *	<p>
     *	<b>Note:</b> only serialize DOCUMENT_NODE, ELEMENT_NODE, and DOCUMENT_FRAGMENT_NODE
     *
     *	@param node the input node.
     *	@param encoding such as "UTF-8", "iso-8859-1"
     *	@param indenting indenting output or not.
     *
     *	@return the XML string
     */
    public static String serialize(Node node, String encoding, boolean indenting) {
        OutputFormat outFmt = null;
        StringWriter strWriter = null;
        XMLSerializer xmlSerializer = null;
        String retVal = null;
        
        try{
            outFmt = new OutputFormat("xml", encoding, indenting);
            outFmt.setOmitXMLDeclaration(true);
            
            strWriter = new StringWriter();
            
            xmlSerializer = new XMLSerializer(strWriter, outFmt);
            if(node == null)
            {
            	return "null";
            }
            short ntype = node.getNodeType();
            
            switch(ntype) {
                case Node.DOCUMENT_FRAGMENT_NODE: xmlSerializer.serialize((DocumentFragment)node); break;
                case Node.DOCUMENT_NODE: xmlSerializer.serialize((Document)node); break;
                case Node.ELEMENT_NODE: xmlSerializer.serialize((Element)node); break;
                default: throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
            }
            
            retVal = strWriter.toString();
        } catch (IOException e) {
            retVal = e.getMessage();
        } finally{
            try {
                strWriter.close();
            } catch (IOException ie) {}
        }
        
        return retVal;
    }
    
    /**
     *	Return a decendent of first parameter, that is the first one to match the XPath specified in
     *	the second parameter.
     *
     *	@param ele The element to work on.
     *	@param tagName format like "CHILD/GRANDCHILD/GRANDGRANDCHILD"
     *
     *	@return	the first element that matched, null if nothing matches.
     */
    public static Element getFirstElementByName(Element ele, String tagName) {
        StringTokenizer st = new StringTokenizer(tagName, "/");
        Element curr = ele;
        Node node;
        String tag;
        while (st.hasMoreTokens()) {
            tag = st.nextToken();
            node = curr.getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE && 
                		(tag.equals(node.getNodeName()) || tag.equals(node.getLocalName()))) {
                    break;
                }
                node = node.getNextSibling();
            }
            
            if (node != null)
                curr = (Element)node;
            else
                return null;
        }
        
        return curr;
    }
    
    /**
     * csc stands for Convert Special Character. Change &, <, ", ' into XML acceptable.
     * Because it could be used frequently, it is short-named to 'csc'.
     * Usually when a string is used for XML values, the string should be parsed first.
     *
     * @param str the String to convert.
     * @return converted String with & to &amp;amp;, < to &amp;lt;, " to &amp;quot;, ' to &amp;apos;
     */
    public static String csc(String str) {
        if (str == null || str.length() == 0)
            return str;
        
        StringBuffer buf = new StringBuffer(str);
        int i = 0;
        char c;
        
        while (i < buf.length()) {
            c = buf.charAt(i);
            if (c == '&') {
                buf.replace(i, i+1, "&amp;");
                i += 5;
            } else if (c == '<') {
                buf.replace(i, i+1, "&lt;");
                i += 4;
            } else if (c == '"') {
                buf.replace(i, i+1, "&quot;");
                i += 6;
            } else if (c == '\'') {
                buf.replace(i, i+1, "&apos;");
                i += 6;
            } else if (c == '>') {
                buf.replace(i, i+1, "&gt;");
                i += 4;
            } else
                i++;
        }
        
        return buf.toString();
    }
    
    
    /**
     *  For an Element node, return its Text node's value; otherwise return the node's value.
     * @param node
     * @return
     */
    public static String getNodeValue(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE)
                    return child.getNodeValue();
                child = child.getNextSibling();
            }
            return null;
        } else
            return node.getNodeValue();
    }
    
    /**
     *	For an Element node, set its Text node's value (create one if it does not have);
     *	otherwise set the node's value.
     * @param node
     * @param val
     */
    public static void setNodeValue(Node node, String val) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE)
                    break;
                child = child.getNextSibling();
            }
            if (child == null) {
                child = node.getOwnerDocument().createTextNode(val);
                node.appendChild(child);
            } else
                child.setNodeValue(val);
        } else
            node.setNodeValue(val);
    }
    
    /**
     *	@deprecated Recommended to use Document.getDocumentElement() directly.
     */
    public static Element getRootElement( Document doc ) {
        return doc.getDocumentElement();
    }
    
    /**
     * Creates an element with the supplied name and attributevalues.
     * @param doc XML Document on which to create the element
     * @param elementName the name of the node element
     * @param hashAttributes usually a Hashtable containing name/value pairs for the attributes of the element.
     */
    public static Element createElement( Document doc, String elementName, Object hashAttributes ) {
        return createElement( doc, elementName, hashAttributes, false );
    }
    
    /**
     * Creates an node text node element with the text node value supplied
     * @param doc the XML document on which this text node element has to be created.
     * @param elementName the name of the element to be created
     * @param textStr should be a String for the value of the text node
     */
    public static Element createTextElement( Document doc, String elementName, Object textStr ) {
        return createElement( doc, elementName, textStr, true );
    }
    
    /**
     * Creates an element with the text node value supplied
     * @param doc the XML document on which this text node element has to be created.
     * @param elementName the name of the element to be created
     * @param attributes usually a Hashtable containing name/value pairs for the attributes of the element.
     * @param textValue the value for the text node of the element.
     */
    public static Element createTextElement( Document doc, String elementName, String textValue, Hashtable attributes ) {
        Element elem = doc.createElement(elementName);
        elem.appendChild( doc.createTextNode(textValue) );
        if( attributes != null ) {
            Enumeration e = attributes.keys();
            while ( e.hasMoreElements() ) {
                String attributeName =  (String)e.nextElement();
                String attributeValue = (String)( attributes.get(attributeName) );
                elem.setAttribute( attributeName, attributeValue );
            }
        }
        return elem;
    }
    
    /**
     * Creates an element with the text node value supplied
     * @param doc the XML document on which this text node element has to be created.
     * @param parentElement the parent element on which this text node element has to be appended
     * @param elementName the name of the element to be created
     * @param attributes usually a Hashtable containing name/value pairs for the attributes of the element.
     * @param textValue the value for the text node of the element.
     */
    public static Element appendTextChild( Document doc, Element parentElement, String elementName, String textValue, Hashtable attributes ) {
        Element elem = doc.createElement(elementName);
        elem.appendChild( doc.createTextNode(textValue) );
        if( attributes != null ) {
            Enumeration e = attributes.keys();
            while ( e.hasMoreElements() ) {
                String attributeName =  (String)e.nextElement();
                String attributeValue = (String)( attributes.get(attributeName) );
                elem.setAttribute( attributeName, attributeValue );
            }
        }
        parentElement.appendChild(elem);
        return elem;
    }
    
    /**
     * Create an element with either attributes or text node.
     * @param doc the XML document on which the node has to be created
     * @param elementName the name of the element to be created
     * @param hashAttributes the value for the text node or the attributes for the node element
     * @param textNodeFlag a flag signifying whether te node to be created is the text node
     */
    public static Element createElement( Document doc, String elementName, Object hashAttributes, boolean textNodeFlag ) {
        Element elem = doc.createElement(elementName);
        if (hashAttributes != null) {
            if (hashAttributes instanceof String) {
                if (textNodeFlag ) {
                    elem.appendChild( doc.createTextNode( (String)hashAttributes ) );
                }
            } else if(hashAttributes instanceof Hashtable) {
                Enumeration e = ((Hashtable)hashAttributes).keys();
                while ( e.hasMoreElements() ) {
                    String attributeName =  (String)e.nextElement();
                    String attributeValue = (String)((Hashtable)hashAttributes).get( attributeName );
                    elem.setAttribute( attributeName, attributeValue );
                }
            }
        }
        return elem;
    }
    
    /**
     * This method is for adding child Nodes to parent node element, the child element has to be created first.
     * @param doc
     * @param      parentElement Parent Element under which the new Element should be present
     * @param      elementName   Name of the element to be created
     * @param      value         Can be either a String ,just the element value if it is a single attribute
     * @return
     */
    public static Element appendChild( Document doc, Element parentElement, String elementName, Object value ) {
        Element childElement = createElement( doc, elementName, value);
        parentElement.appendChild(childElement);
        return childElement;
    }
    
    /**
     *	@deprecated Use appendChild(Element, Element) instead.
     */
    public static void appendChild( Document doc, Element parentElement, Element childElement ) {
        parentElement.appendChild(childElement);
        return;
    }
    
    /**
     * This method is for adding child Nodes to parent node element.
     * @param      parentElement Parent Element under which the new Element should be present
     * @param      childElement  Child Element which should be added.
     */
    public static void appendChild( Element parentElement, Element childElement ) {
        parentElement.appendChild(childElement);
    }
    
    /**
     * This method is for setting the attribute of an element
     * @param      objElement     Element where this attribute should be set
     * @param      attributeName  Name of the attribute
     * @param      attributeValue Value of the attribute
     */
    public static void setAttribute(Element objElement, String attributeName, String attributeValue) {
        objElement.setAttribute(attributeName,attributeValue);
    }
    
    /**
     * This method is for removing an attribute from an Element.
     * @param      objElement     Element from where the attribute should be removed.
     * @param      attributeName  Name of the attribute
     */
    public static void removeAttribute( Element objElement, String attributeName ) {
        objElement.removeAttribute(attributeName);
    }
    
    /**
     * This method is for removing the child element of an element
     * @param      parentElement     Element from where the child element should be removed.
     * @param      childElement  Child Element which needs to be removed from the parent
     */
    public static void removeChild( Element parentElement, Element childElement ) {
        parentElement.removeChild(childElement);
    }
    
    /**
     * Method to create a text mode for an element
     * @param doc the XML document on which the node has to be created
     * @param parentElement the element for which the text node has to be created.
     * @param elementValue the value for the text node.
     */
    public static void createTextNode( Document doc, Element parentElement, String elementValue ) {
        parentElement.appendChild( doc.createTextNode( elementValue ) );
    }
    
    /**
     * If this class was used for building XML from scratch , this method
     * would give constructed XML as String.
     *	@deprecated use serialize(Node) instead.
     */
    public static String constructXML( Document doc ) {
        return serialize( doc );
    }
    
    /**
     * This method takes Document as input and returns the XML String.
     * @param document   a valid document object for which XML output in String form is required.
     */
    public static String getXMLString( Document document ) {
    	if (document == null){
    		return "null";
    	}
    	else {
            return serialize( document );
    	}
    }
    
    /**
     *
     * This method takes a document Element as input and returns the XML String.
     * @param element   a valid element object for which XML output in String form is required.
     * @return XML String of the given element
     */
    
    public static String getElementXMLString( Element element ) {
    	if (element == null){
    		return "null";
    	}
    	else {
            return serialize( element );
    	}
    }
    
    /**
     *	Convert the Document to String and write to a file.
     * @param document
     * @param fileName
     * @throws IOException
     */
    public static void flushToAFile( Document document, String fileName )
    throws IOException {
        if( document != null ) {
            OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
            oFmt.setPreserveSpace(true);
            XMLSerializer xmlOP = new XMLSerializer(oFmt);
            FileWriter out = new FileWriter(new File(fileName));
            xmlOP.setOutputCharStream(out);
            xmlOP.serialize(document);
            out.close();
        }
    }
    
    
    /**
     *	Serialize a Document to String and output to a java.io.Writer.
     * @param document
     * @param writer
     * @throws IOException
     */
    public static void flushToAFile( Document document, Writer writer ) throws IOException {
        if( document != null ) {
            OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
            oFmt.setPreserveSpace(true);
            XMLSerializer xmlOP = new XMLSerializer(oFmt);
            xmlOP.setOutputCharStream(writer);
            xmlOP.serialize(document);
            writer.close();
        }
    }
    
    
    /**
     * This method  constructs and inserts a process Instruction in the given document
     * @param doc
     * @param rootElement
     * @param strTarget
     * @param strData
     */
    public static void createProcessingInstruction( Document doc, Element rootElement, String strTarget, String strData ) {
        ProcessingInstruction p = doc.createProcessingInstruction( strTarget, strData );
        doc.insertBefore( p, rootElement );
    }
    
    
    
    /**
     *
     * @param element
     * @param attributeName
     * @return the value of the attribute in the element.
     */
    public static String getAttribute(Element element, String attributeName) {
        if (element != null)
            return element.getAttribute(attributeName);
        else
            return null;
    }
    
    /**
     *	Get the first direct child Element with the name.
     *	@deprecated use getFirstElementByName() instead.
     */
    public static Element getUniqueSubNode( Element element, String nodeName ) {
        Element uniqueElem = null;
        NodeList nodeList = element.getElementsByTagName( nodeName );
        if( nodeList != null && nodeList.getLength()>0 ) {
            int size = nodeList.getLength();
            for( int count=0; count<size; count++ ) {
                uniqueElem = (Element)(nodeList.item(count));
                if( uniqueElem != null ) {
                    if( uniqueElem.getParentNode() == element )
                        break;
                }
            }
        }
        return uniqueElem;
    }
    
    /**
     *	Gets the node value for a sub element under a Element with unique name.
     *	@deprecated the logic is not clear as the implementation gets the value of grand-child instead of direct child.
     *	should use getFirstElementByName() and getNodeValue() combination for application logic.
     */
    public static String getUniqueSubNodeValue( Element element, String nodeName ) {
        NodeList nodeList = element.getElementsByTagName( nodeName );
        if( nodeList != null ) {
            Element uniqueElem = (Element)(nodeList.item(0));
            if( uniqueElem != null ) {
                if( uniqueElem.getFirstChild() != null )
                    return uniqueElem.getFirstChild().getNodeValue();
                else
                    return null;
            } else
                return null;
        } else
            return null;
    }
    
    /**
     * Return the sub elements with given name, as a List.
     * @param element
     * @param nodeName
     * @return
     */
    public static List getSubNodeList( Element element, String nodeName ) {
        NodeList nodeList = element.getElementsByTagName( nodeName );
        List elemList = new ArrayList();
        for( int count=0; count<nodeList.getLength(); count++ )
            elemList.add( nodeList.item( count ) );
        return elemList;
    }
    
    /**
     *	Same as getSubNodeList().
     *	@see #getSubNodeList(Element, String).
     */
    public static List getElementsByTagName( Element startElement, String elemName ) {
        NodeList nodeList = startElement.getElementsByTagName( elemName );
        List elemList = new ArrayList();
        for( int count=0; count<nodeList.getLength(); count++ )
            elemList.add(nodeList.item(count));
        return elemList;
    }
    
    /**
     *	Gets the count of sub nodes under one node matching the sub node name
     *	@param parentElement Element under which sub nodes reside
     *   @param subElementName Name of the sub node to look for in the parent node
     */
    public static int getElementsCountByTagName( Element parentElement, String subElementName ) {
        NodeList nodeList = parentElement.getElementsByTagName( subElementName );
        if( nodeList != null )
            return nodeList.getLength();
        else
            return 0;
    }
    
    /**
     *	Augment a destination Element with a source Element. Including the source Element's Attributes and child nodes.
     *	<p>
     *	The behavior is a little inconsistant: attributes in destElem are replaced, but child nodes are added, i.e. no
     *	equality check of child nodes. So the meaningful way to use it is to start with an empty destination Element.
     *	<br>
     *	It's better be replaced by a method with signature: <i>Element copyElement(Document destDoc, Element srcElem)</i>
     *
     *	@param destDoc the Document for destination Element, must be the same as destElem.getDocument().
     *	@param srcElem the source Element.
     *	@param destElem the destination Element.
     */
    
    
    public static void copyElement( Document destDoc, Element srcElem, Element destElem ) {
        NamedNodeMap attrMap = srcElem.getAttributes();
        int attrLength = attrMap.getLength();
        for( int count=0; count<attrLength; count++ ) {
            Node attr = attrMap.item(count);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            destElem.setAttribute( attrName, attrValue );
        }
        
        if( srcElem.hasChildNodes() ) {
            NodeList childList = srcElem.getChildNodes();
            int numOfChildren = childList.getLength();
            for( int cnt=0; cnt<numOfChildren; cnt++ ) {
                Object childSrcNode = childList.item(cnt);
                if( childSrcNode instanceof CharacterData ) {
                    if( childSrcNode instanceof Text ) {
                        String data = ((CharacterData)(childSrcNode)).getData();
                        Node childDestNode = destDoc.createTextNode( data );
                        destElem.appendChild( childDestNode );
                    } else if( childSrcNode instanceof Comment ) {
                        String data = ((CharacterData)(childSrcNode)).getData();
                        Node childDestNode = destDoc.createComment( data );
                        destElem.appendChild( childDestNode );
                    }
                } else {
                    Element childSrcElem = (Element)(childSrcNode);
                    Element childDestElem = appendChild( destDoc, destElem, childSrcElem.getNodeName(), null );
                    copyElement( destDoc, childSrcElem, childDestElem );
                }
            }
        }
    }
    /**
     *	Finds the first child node with the given name and returns it.
     *  This method can be useful for getting first level children and significantly faster than using XPATH
     *	@param parentNode The parent Node 
     *	@param childnodeName The child node name to be searched
     */
	public static Node getChildNodeByName(Node parentNode, String childnodeName) {
		NodeList childrenNodes = parentNode.getChildNodes();
		int length = childrenNodes.getLength();
		for(int index = 0; index < length; index++){
			Node childNode = childrenNodes.item(index);
			String name = childNode.getNodeName();
			if (childnodeName.equalsIgnoreCase(name)){
				return childNode;
			}
		}
		return null;
	}
	/**
     * Creates a Document object
     * 
     * @return empty Document object
     * @throws ParserConfigurationException
     */
    public static Document getDocument() throws ParserConfigurationException {
        //Create a new Document Bilder Factory instance
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        documentBuilderFactory.setNamespaceAware(true);
        
        //Create new document builder
        DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();

        //Create and return document object
        return documentBuilder.newDocument();
    }

    /*
     * This method takes the notification XML as input and returns the actual notification message. we have to do this way becase root element 
     * for body is always DeliverNotificationReq, and even the header attribute messageName is set to DeliverNotificationReq and we 
     * do not have any way to see what is the actual notification comming in. all notifications will be delivered by DeliverNotificationReq interface
     * Update: this methods will not used until we get the LocalName, currently it is comming as null
     */
    
	public static String getNotificationMessageName(Document notificationObjDoc) {
		String strMsgName = "";
		Element elem = notificationObjDoc.getDocumentElement();
		NodeList nl = elem.getChildNodes();
		for(int index = 0 ; index < nl.getLength() ; index++)
		{
			Object obj = nl.item(index);
			if(obj instanceof org.w3c.dom.Element)
			{
				Element elemChild = (Element) obj;
				//String strLocalName =  elemChild.getLocalName() ;
				String strTagName =  StringUtil.nonNull(elemChild.getTagName()) ;
				/* Hardcoding this to NotifiedSystem because as per agreement with LM every inbound notification will have two
				* elements first being NotifiedSystem and second will be the actual notification message. if we have more than two elements a seperate
				* OR statement needs to be added here
				*/				
				String strLocalName = NWCGAAUtil.getNodeNameWOURI(strTagName);
				if(!strLocalName.equals("NotifiedSystem"))
				{
					strMsgName = strLocalName ;
				}
				
			}
		}
		return strMsgName;
	}
//this methods will not used until we get the namespaceURI, currently it is comming as null
	public static String getNotificationMessageNamespace(Document notificationObjDoc) {
		String strMsgURI = "";
		Element elem = notificationObjDoc.getDocumentElement();
		NodeList nl = elem.getChildNodes();
		for(int index = 0 ; index < nl.getLength() ; index++)
		{
			Object obj = nl.item(index);
			if(obj instanceof org.w3c.dom.Element)
			{
				Element elemChild = (Element) obj;
				//String strLocalName =  elemChild.getLocalName() ;
				String strTagName =  StringUtil.nonNull(elemChild.getTagName()) ;
				/* Hardcoding this to NotifiedSystem because as per agreement with LM every inbound notification will have two
				* elements first being NotifiedSystem and second will be the actual notification message. if we have more than two elements a seperate
				* OR statement needs to be added here
				*/		
				String strLocalName = NWCGAAUtil.getNodeNameWOURI(strTagName);
				if(!strLocalName.equals("NotifiedSystem"))
				{
					//strMsgURI = elemChild.getNamespaceURI() ;
					strMsgURI = ResourceUtil.get(strLocalName+"."+NWCGAAConstants.MDTO_NAMESPACE);
					NWCGLoggerUtil.Log.info("setNotificationMessageNameAndNamespace :: ResourceUtil.get(strLocalName)" + ResourceUtil.get(strLocalName+".namespace"));
				}
				
			}
		}
		return strMsgURI;
	}

	public static void setNotificationMessageNameAndNamespace(Document notificationObjDoc, NWCGMDTOHelperDocument docHelper) {
		
		Element elem = notificationObjDoc.getDocumentElement();
		NodeList nl = elem.getChildNodes();
		for(int index = 0 ; index < nl.getLength() ; index++)
		{
			Object obj = nl.item(index);
			if(obj instanceof org.w3c.dom.Element)
			{
				Element elemChild = (Element) obj;
				
				
				
				//String strLocalName =  StringUtil.nonNull(elemChild.getLocalName()) ; // Jay currently we are not getting the localname and namespaceURI
				String strTagName =  StringUtil.nonNull(elemChild.getTagName()) ;
				/* Hardcoding this to NotifiedSystem because as per agreement with LM every inbound notification will have two
				* elements first being NotifiedSystem and second will be the actual notification message. if we have more than two elements a seperate
				* OR statement needs to be added here
				*/		
				NWCGLoggerUtil.Log.info("setNotificationMessageNameAndNamespace :: strTagName " + strTagName);
				String strLocalName = NWCGAAUtil.getNodeNameWOURI(strTagName);
				NWCGLoggerUtil.Log.info("setNotificationMessageNameAndNamespace :: strLocalName " + strLocalName);
				if(!strLocalName.equals("NotifiedSystem"))
				{
					docHelper.setMesssageName(strLocalName);
					docHelper.setNamespace(ResourceUtil.get(strLocalName+"."+NWCGAAConstants.MDTO_NAMESPACE));
					NWCGLoggerUtil.Log.info("setNotificationMessageNameAndNamespace :: strLocalName " + strLocalName);
					NWCGLoggerUtil.Log.info("setNotificationMessageNameAndNamespace :: ResourceUtil.get(strLocalName)" + ResourceUtil.get(strLocalName+".namespace"));
				}
				
			}
		}
		
	}
    
    public static String extractStringFromDocument(Document doc) throws TransformerException 
	{
	    DOMSource domSource = new DOMSource(doc);
       StringWriter writer = new StringWriter();
       StreamResult result = new StreamResult(writer);
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       transformer.transform(domSource, result);
       return writer.toString();
	    
	}
    public static String extractStringFromNode(Node node) throws TransformerException 
	{
	    DOMSource domSource = new DOMSource(node);
       StringWriter writer = new StringWriter();
       StreamResult result = new StreamResult(writer);
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       transformer.transform(domSource, result);
       return writer.toString();
	    
	}
}
