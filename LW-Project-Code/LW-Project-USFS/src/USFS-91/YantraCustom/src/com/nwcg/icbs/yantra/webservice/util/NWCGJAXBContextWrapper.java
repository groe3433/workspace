/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.webservice.util;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.NWCGVerifyHandler;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * This is wrapper around cached JAXBContext to each type of namespace
 * 
 * Modifications for 9.1:
 * 	- Line 318-342 - add another method to specifically take in a DOCUMENT, and added a validation for: 
 *    (msgName.equals("PlaceResourceRequestExternalReq") || msgName.equals("StatusNFESResourceRequestReq") || msgName.equals("RetrieveResourceRequestReq")
 *    This is because of an ummarshalling exception. 
 *  
 * @revisions lightwell
 * @version 1.2
 */
public class NWCGJAXBContextWrapper {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGJAXBContextWrapper.class);

	private static JAXBContext contextCatalog = null;

	private static JAXBContext contextResourceOrder = null;

	private static JAXBContext contextResourceOrderNotification = null;

	private static JAXBContext contextCommonTypes = null;

	static {
		try {
			// initializing the static variables
			logger.verbose("@@@@@ Creating CATALOG Context ***");
			logger.verbose("@@@@@ BEGIN TIMER: JAXBContext.newInstance(CATALOG) ");
			contextCatalog = JAXBContext.newInstance(NWCGAAConstants.CATALOG_PACKAGE_NAME);
			logger.verbose("@@@@@ END TIMER: JAXBContext.newInstance(CATALOG) ");
			logger.verbose("@@@@@ Created CATALOG Context ***");

			logger.verbose("@@@@@ Creating RESOURCE ORDER Context ***");
			logger.verbose("@@@@@ BEGIN TIMER: JAXBContext.newInstance(RESOURCE ORDER) ");
			contextResourceOrder = JAXBContext.newInstance(NWCGAAConstants.RESOURCEORDER_PACKAGE_NAME);
			logger.verbose("@@@@@ END TIMER: JAXBContext.newInstance(RESOURCE ORDER) ");
			logger.verbose("@@@@@ Created RESOURCE ORDER Context ***");

			logger.verbose("@@@@@ Creating RESOURCE ORDER Notification Context ***");
			logger.verbose("@@@@@ BEGIN TIMER: JAXBContext.newInstance(RESOURCE ORDER NOTIFICATION) ");
			contextResourceOrderNotification = JAXBContext.newInstance(NWCGAAConstants.RESOURCEORDER_NOTIFICATION_PACKAGE_NAME);
			logger.verbose("@@@@@ END TIMER: JAXBContext.newInstance(RESOURCE ORDER NOTIFICATION) ");
			logger.verbose("@@@@@ Created RESOURCE ORDER Notification Context ***");

			logger.verbose("@@@@@ Creating Common Package Context ***");
			logger.verbose("@@@@@ BEGIN TIMER: JAXBContext.newInstance(COMMON) ");
			// gov.nwcg.services.ross.common_types._1
			contextCommonTypes = JAXBContext.newInstance(NWCGAAConstants.COMMON_PACKAGE_NAME);
			logger.verbose("@@@@@ END TIMER: JAXBContext.newInstance(COMMON) ");
			logger.verbose("@@@@@ Created Common Package Context ***");
		} catch (Exception e) {
			logger.error("!!!!! Exception caught " + e);
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * call this method to to convert document to object, later this method will removed and getObjectFromNode will be used. 
	 * 
	 * @param document
	 * @param strMessageName
	 * @return
	 * @throws JAXBException
	 */
	public Object getObjectFromDocument(Document document, String strMessageName) throws JAXBException {
		JAXBContext context = getJAXBContextFromMessageName(strMessageName, null);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getObjectFromDocument 1");		
		return processUnmarshalling(document, context);
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public Object getObjectFromUnknownDocument(Document document) {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromUnknownDocument");	
		Object returnObj = null;
		try {
			returnObj = getObjectFromDocument(document, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException mue) {
			logger.error("!!!!! Caught MalformedURLException : " + mue);
		} catch (JAXBException je) {
			logger.error("!!!!! Caught JAXBException : " + je);
		}
		if (returnObj == null) {
			logger.verbose("@@@@@ Resource order marshalling failed, trying catalog now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromDocument(document, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			}
		}
		if (returnObj == null) {
			logger.verbose("@@@@@ catalog marshalling failed, trying resource now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromDocument(document, new URL(NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromUnknownDocument");	
		return returnObj;
	}

	/**
	 * similar to aobve method but it is used to convert Node instead of Document.
	 * 
	 * @param node
	 * @param strMessageName
	 * @return
	 * @throws JAXBException
	 */
	public Object getObjectFromNode(Node node, String strMessageName) throws JAXBException {
		JAXBContext context = getJAXBContextFromMessageName(strMessageName, null);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getObjectFromNode (strMessageName)");	
		return processUnmarshalling(node, context);
	}

	/**
	 * 
	 * @param node
	 * @param url
	 * @return
	 * @throws JAXBException
	 */
	public Object getObjectFromNode(Node node, URL url) throws JAXBException {
		JAXBContext context = getJAXBContextFromMessageName(null, url);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getObjectFromNode (url)");
		return processUnmarshalling(node, context);
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public Object getObjectFromUnknownNode(Node document) {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromUnknownNode");	
		Object returnObj = null;
		try {
			returnObj = getObjectFromNode(document, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException mue) {
			logger.error("!!!!! Caught MalformedURLException : " + mue);
		} catch (JAXBException je) {
			logger.error("!!!!! Caught JAXBException : " + je);
		}
		if (returnObj == null) {
			logger.warn("Resource order marshalling failed, trying catalog now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromNode(document, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			}
		}
		if (returnObj == null) {
			logger.warn("catalog marshalling failed, trying resource now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromNode(document, new URL(NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromUnknownNode");	
		return returnObj;
	}

	/**
	 * 
	 * @param document
	 * @param urlNamespace
	 * @return
	 * @throws JAXBException
	 */
	public Object getObjectFromDocument(Document document, URL urlNamespace) throws JAXBException {
		JAXBContext context = getJAXBContextFromMessageName(null, urlNamespace);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getObjectFromDocument 2");	
		return processUnmarshalling(document, context);
	}

	/**
	 * 
	 * @param obj
	 * @param document
	 * @param strMessageName
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	public Document getDocumentFromObject(Object obj, Document document, String strMessageName) throws JAXBException, ParserConfigurationException {
		JAXBContext context = getJAXBContextFromMessageName(strMessageName, null);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getDocumentFromObject 1");
		return processMarshalling(obj, document, context);
	}

	/**
	 * 
	 * @param obj
	 * @param document
	 * @param urlNamespace
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	public Document getDocumentFromObject(Object obj, Document document, URL urlNamespace) throws JAXBException, ParserConfigurationException {
		JAXBContext context = getJAXBContextFromMessageName(null, urlNamespace);
		logger.verbose("@@@@@ In NWCGJAXBContextWrapper::getDocumentFromObject 2");
		return processMarshalling(obj, document, context);
	}

	/**
	 * This method gets the document object for unknown objects. Typically this
	 * mehtod will be used with methods like get operation results and deliver
	 * operations results where we have the response object but we don't know
	 * what that object is. Developer shuld avoid using this method for other
	 * operatiions where we know what object we are parsing.
	 */
	public Document getDocumentFromUnknownObject(Object obj, Document document) {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::getDocumentFromUnknownObject");
		try {
			document = getDocumentFromObject(obj, document, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException mue) {
			logger.error("!!!!! Caught MalformedURLException : " + mue);
		} catch (JAXBException je) {
			logger.error("!!!!! Caught JAXBException : " + je);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		}
		if (document == null) {
			logger.warn("Resource order marshalling failed, trying Catalog now");
			try {
				document = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, document, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			}
		}
		if (document == null) {
			logger.warn("Catalog marshalling failed, trying Resource now");
			try {
				document = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, document, new URL(NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException : " + mue);
			} catch (JAXBException je) {
				logger.error("!!!!! Caught JAXBException : " + je);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getDocumentFromUnknownObject");
		return document;
	}

	/**
	 * 
	 * @param doc
	 * @param context
	 * @return
	 * @throws JAXBException
	 */
	private Object processUnmarshalling(Document doc, JAXBContext context) throws JAXBException {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::processUnmarshalling (Document)");
		Unmarshaller unmar = context.createUnmarshaller();
		unmar.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		logger.verbose("*** Unmarshaller Created***");
		logger.verbose("*** Unmarshalling Object ***");
		Object returnObj = null;
		
		int index = doc.getDocumentElement().getNodeName().indexOf(":") + 1;
		String msgName = doc.getDocumentElement().getNodeName().substring(index);
		String str = XMLUtil.getXMLString(doc);
		logger.verbose("@@@@@ msgName :: " + msgName);
		if(msgName.equals("PlaceResourceRequestExternalReq") || msgName.equals("StatusNFESResourceRequestReq") || msgName.equals("RetrieveResourceRequestReq")) {
			returnObj = unmar.unmarshal(new StreamSource(new StringReader(str)));
		} else {
			returnObj = unmar.unmarshal(doc);
		}
		
		if(returnObj == null) {
			logger.verbose("@@@@@@@@@@ returnObj :: " + returnObj);
		}
		logger.verbose("*** Object Unmarshalled***");
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::processUnmarshalling (Document)");
		return returnObj;
	}
	
	/**
	 * 
	 * @param node
	 * @param context
	 * @return
	 * @throws JAXBException
	 */
	private Object processUnmarshalling(Node node, JAXBContext context) throws JAXBException {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::processUnmarshalling (Node)");
		Unmarshaller unmar = context.createUnmarshaller();
		unmar.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		logger.verbose("*** Unmarshaller Created***");
		logger.verbose("*** Unmarshalling Object ***");
		Object returnObj = unmar.unmarshal(node);
		if(returnObj == null) {
			logger.verbose("@@@@@@@@@@ returnObj :: " + returnObj);
		}
		logger.verbose("*** Object Unmarshalled***");
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::processUnmarshalling (Node)");
		return returnObj;
	}

	/**
	 * 
	 * @param obj
	 * @param document
	 * @param context
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	private Document processMarshalling(Object obj, Document document, JAXBContext context) throws JAXBException, ParserConfigurationException {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::processMarshalling");
		logger.verbose("*** Creating Marshaller ***");
		Marshaller mar = context.createMarshaller();
		logger.verbose("*** Marshaller Created***");
		if (document == null)
			document = XMLUtil.getDocument();
		logger.verbose("*** Marshalling Object ***");
		mar.marshal(obj, document);
		logger.verbose("*** Object Marshalled***");
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::processMarshalling");
		return document;
	}

	/**
	 * Client should pass either message name or namespace, if both passed
	 * preference is given to the namespace.
	 */
	private JAXBContext getJAXBContextFromMessageName(String strMessageName, URL urlNamespace) {
		logger.verbose("@@@@@ Entering NWCGJAXBContextWrapper::getJAXBContextFromMessageName");
		String strNamespace = null;
		if (urlNamespace == null) {
			strNamespace = ResourceUtil.get(strMessageName + ".namespace");
			logger.verbose("@@@@@@@@@@ if strNamespace :: " + strNamespace);
		} else {
			strNamespace = urlNamespace.toExternalForm(); 
			logger.verbose("@@@@@@@@@@ else strNamespace :: " + strNamespace);
		}
		if (strNamespace.equals(NWCGAAConstants.CATALOG_NAMESPACE)) {
			logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextCatalog)");
			return contextCatalog;
		} else if (strNamespace.equals(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE)) {
			logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextResourceOrder)");
			return contextResourceOrder;
		} else if (strNamespace.equals(NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE)) {
			logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextResourceOrderNotification)");
			return contextResourceOrderNotification;
		} else if (strNamespace.equals(NWCGAAConstants.COMMON_TYPES_NAMESPACE)) {
			// http://nwcg.gov/services/ross/common_types/1.1
			if(contextCommonTypes == null) {
				logger.verbose("@@@@@@@@@@ contextCommonTypes is null..." + contextCommonTypes);
			}
			logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextCommonTypes)");
			return contextCommonTypes;
		}
		logger.verbose("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (null)");
		// Program should never reach this statement... if reached then its a time to add new else if statement		
		return null;
	}
}