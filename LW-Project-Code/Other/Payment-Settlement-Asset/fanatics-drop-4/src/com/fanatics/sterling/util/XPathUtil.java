package com.fanatics.sterling.util;


import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xpath.CachedXPathAPI;

import com.yantra.yfs.japi.YFSException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

/**
*
* This is an utility class which uses
* 					Xpath to access the nodes in a xml document.
*

*/
public class XPathUtil {
    /**
     * @return
     * @throws Exception
     * @deprecated use XMLUtil.newDocument() instead.
     */
    public static Document getDocument() throws Exception {
        //Create a new Document Bilder Factory instance
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        //Create new document builder
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        //Create and return document object
        return documentBuilder.newDocument();
    }

    /**
     * Evaluates the given Xpath and returns the corresponding node.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return node if found
     * @throws Exception exception
     */
    public static Node getNode(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        Node ret = null;
        try {
            ret = XPathAPI.selectSingleNode(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }

    /**
     * Evaluates the given Xpath and returns the corresponding node using CachedXPathAPI.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return node if found
     * @throws Exception exception
     */
    public static Node getNodeUsingCachedXPath(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        Node ret = null;
        try {
        	CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
            ret = cachedXPathAPI.selectSingleNode(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    /**
     * Evaluates the given Xpath and returns the corresponding value as a String.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return String Value of the XPath Execution.
     * @throws Exception exception
     */
    public static String  getString(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        String value = null;
        try {
            XObject xobj = XPathAPI.eval(node, xpath);
            value = xobj.toString();
        } catch (TransformerException e) {
            throw e;
        }
        return value;
    }


    /**
     * Evaluates the given Xpath and returns the corresponding node list.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeList getNodeList(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeList ret = null;
        try {
            ret = XPathAPI.selectNodeList(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }

    /**
     * Evaluates the given Xpath and returns the corresponding node list.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeList getNodeListUsingCachedXPath(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeList ret = null;
        try {
        	CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
            ret = cachedXPathAPI.selectNodeList(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }


    /**
     * Evaluates the given Xpath and returns the corresponding node iterator.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeIterator getNodeIterator(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeIterator ret = null;
        try {
            ret = XPathAPI.selectNodeIterator(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }

    /**
     * @param elName
     * @return
     * @throws Exception
     * @deprecated use XMLUtil.createDocument(String docElementTag)
     */
    public static Document getEmptyDoc(String elName) throws Exception {
        Document ret = getDocument();
        Element el = ret.createElement(elName);
        ret.appendChild(el);
        return ret;
    }

    /**
	 * Gets value of an attribute from node
	 * @param node
	 *            Node Object
	 * @param attributeName
	 *            Attribute Name
	 * @return Attribute Value
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static String getAttribute(Node node, String attributeName)
		throws IllegalArgumentException, Exception {
		//Validate attribute name
		if (attributeName == null) {
			throw new IllegalArgumentException(
				"Attribute Name "
					+ " cannot be null in XmlUtils.getAttribute method");
		}
		//Validate node
		if (node == null) {
			throw new IllegalArgumentException(
				"Node cannot "
					+ " be null in XmlUtils.getAttribute method for"
					+ " attribute name:"
					+ attributeName);
		}
		NamedNodeMap attributeList = node.getAttributes();
		Node attribute = attributeList.getNamedItem(attributeName);

		//Validate attribute name
		if (attribute == null) {
			return "";		}

		return ((Attr) attribute).getValue();
	}

	/**
	 * This method finds and returns the first node for xpath specified.
	 * It internally uses 'getNodeList()' method from which it takes the
	 * first item and returns it.
	 *
	 * @param doc
	 *            Document Object
	 * @param xpath
	 *            XPath URL
	 * @return nodeList
	 *        List of Nodes
	 * @throws Exception
	 *             For general exception
	 * @throws TransformerException
	 *             Exception arised from transformation
	 */
	public static Node getXpathNode(Document doc, String xpath)
		throws Exception {
		//Validate doc name
		if (doc == null) {
			throw new IllegalArgumentException(
				"Document Object passed " + " cannot be null");
		}
		//Validate xPath name
		if (xpath == null) {
			throw new IllegalArgumentException(
				"XPath String passed " + " cannot be null");
		}
		NodeList nodeList = getXpathNodeList(doc, xpath);
		if (nodeList.getLength() > 0)
			return nodeList.item(0);
		else
			return null;
	}
	/**
	 * Gets the Node List from document object and Xpath expression.
	 * It will get all the nodes for which the xpath is set.
	 *
	 * @param doc
	 *            Document Object
	 * @param xpath
	 *            XPath URL
	 * @return nodeList
	 *        List of Nodes
	 * @throws Exception
	 *             For general exception
	 * @throws TransformerException
	 *             Exception arised from transformation
	 */

	public static NodeList getXpathNodeList(Document doc, String xpath)
		throws TransformerException, Exception {
		//Validate doc name
		if (doc == null) {
			throw new IllegalArgumentException(
				"Document Object passed " + " cannot be null");
		}
		//Validate xPath name
		if (xpath == null) {
			throw new IllegalArgumentException(
				"XPath String passed " + " cannot be null");
		}
		XPathContext ctx = null;
		try {
			ctx = new XPathContext();
			XPath path = new XPath(xpath, null, null, XPath.SELECT);
			XObject obj = path.execute(ctx, doc, null);
			NodeList nodeList = obj.nodelist();
			return nodeList;
		} catch (TransformerException ex) {
			throw new YFSException(
				ex.toString());
		}
	}
	/**
	 * This method returns node value for given child. If there is no text
	 * available for given node, then this method returns null
	 * @return Node value of input node
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 * @throws Exception
	 *             incase of any other exceptions
	 */
	public static String getNodeValue(Node inputNode, String defaultStr)
		throws IllegalArgumentException, Exception {
		//Child count
		int childCount = 0;

		//Validate input stream
		if (inputNode == null) {
			throw new IllegalArgumentException(
				"Input Node cannot be null in " + "XmlUtils.getNodeValue");
		}

		//Return null if child not found
		NodeList childList = inputNode.getChildNodes();
		if ((childList == null) || (childList.getLength() < 1)) {
			return null;
		}

		//Get child count
		childCount = childList.getLength();

		//For each child
		Node childNode = null;
		for (int childIndex = 0; childIndex < childCount; childIndex++) {
			//Get each child
			childNode = childList.item(childIndex);
			//Check if text node
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				//Return node value
				return childNode.getNodeValue();
			}
		}
		//If no text node found return null
		return defaultStr;
	}
	
	/**
	 * This method gets the attribute of the attribute for which the
	 * xpath has been specified. It takes the attribute value from the
	 * the xpath.
	 *
	 * @param doc
	 *            Document Object
	 * @param xpath
	 *            XPath URL
	 * @return attribute The attribute value to the
	 *           xpath.
	 *
	 * @throws Exception
	 *             For general exception
	 *
	 */
	public static String getXpathAttribute(Node node, String xpath)
			throws Exception {
			//Create a new XML document from the node to start
			//the traversal from the node.

			Document document =  XMLUtil.newDocument();
			document.appendChild(document.importNode(node, true));
			//Call the getAttribute which takes the Dcoument input
			//to get the actual value.

			String attributeValue = getXpathAttribute(document, xpath);
			return attributeValue;
		}
	
	 /**
		 * This method gets the attribute of the element for which the
		 * xpath has been specified. It returns an attribute for a node.
		 * If the node is repeated in the xml doc then the attribute value
		 * for the last encountered node will be returned.
		 *
		 * @param doc
		 *            Document Object
		 * @param xpath
		 *            XPath URL
		 * @return nodeList
		 *        List of Nodes
		 * @throws Exception
		 *             For general exception
		 * @throws TransformerException
		 *             Exception arised from transformation
		 */
		public static String getXpathAttribute(Document doc, String xpath)
			throws Exception {
			//Validate doc name
			if (doc == null) {
				throw new IllegalArgumentException(
					"Document Object passed " + " cannot be null");
			}
			//Validate xPath name
			if (xpath == null) {
				throw new IllegalArgumentException(
					"XPath String passed " + " cannot be null");
			}
			Node node = getXpathNode(doc, xpath);
			if (node == null) {

				return "";

			} else
				return node.getNodeValue();
		}
}

