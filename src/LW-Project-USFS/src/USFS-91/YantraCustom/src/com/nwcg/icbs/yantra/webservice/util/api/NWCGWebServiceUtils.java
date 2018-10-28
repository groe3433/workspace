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

package com.nwcg.icbs.yantra.webservice.util.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.interop.japi.YIFApi;
import com.yantra.integration.adapter.client.ClientFactory;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGWebServiceUtils {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGWebServiceUtils.class);

	//private static ClientFactory api = null;
	private static YIFApi api = null;

	private static YFSEnvironment env = null;

	private static Properties serviceProp = null;

	static {
		try {
			//api = YIFClientFactory.getInstance().getLocalApi();
			api = YIFClientFactory.getInstance().getApi("LOCAL");
			Document document = XMLUtil.createDocument("YFSEnvironment");
			Element elem = document.getDocumentElement();
			elem.setAttribute("userId", NWCGAAConstants.ENV_USER_ID);
			elem.setAttribute("progId", NWCGAAConstants.ENV_PROG_ID);
			//env = api.createEnvironment(document);
			env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
		} catch (YIFClientCreationException cce) {
			logger.error("!!!!! YIFClientCreationException ==> " , cce);
			cce.printStackTrace();
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! ParserConfigurationException ==> " , pce);
			pce.printStackTrace();
		} catch (YFSException ye) {
			logger.error("!!!!! YFSException ==> " , ye);
			ye.printStackTrace();
		} catch (RemoteException re) {
			logger.error("!!!!! RemoteException ==> " , re);
			re.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
	}

	public static YFSEnvironment getEnvironment() {
		logger.verbose("@@@@@ Returning the static YFSEnvironment variable...");
		if(env == null) {
			logger.verbose("!!!!! env is null...");
		}
		return env;
	}

	/**
	 * Use xslt transformation to get a compatiable xml document
	 * Should be useful in Yantra to ROSS and ROSS to Yantra transformation.
	 * @param doc
	 * @return
	 */
	public static Document transformDocument(Document doc, String xsltSheet) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::transformDocument");
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();

			StreamSource stylesource = new StreamSource(xsltSheet);
			Transformer transformer = tFactory.newTransformer(stylesource);

			DOMSource source = new DOMSource(doc);
			java.io.CharArrayWriter wr = new java.io.CharArrayWriter();
			StreamResult result = new StreamResult(wr);
			transformer.transform(source, result);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document retDoc = builder.parse(new InputSource(
					new java.io.StringReader(wr.toString())));
			
			logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::transformDocument");
			return retDoc;

		} catch (Exception e) {
			logger.error("!!!!! Exiting NWCGWebServiceUtils::transformDocument (exception)");
			return null;
		}
	}

	/**
	 * Use XML document to get corresponding Class. 
	 * @return
	 */
	public static Object getUnMarshallClass(Document doc, String packageName) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::getUnMarshallClass");
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);

			Unmarshaller u = jc.createUnmarshaller();

			// unmarshal a po instance document into a tree of Java content
			JAXBElement poe = (JAXBElement) u.unmarshal(doc);
			return poe.getValue();

		} catch (JAXBException je) {
			//logger.printStackTrace(je);
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::getUnMarshallClass");
		return null;
	}

	/**
	 * @return boolean
	 */
	public static Unmarshaller validate(String packageName, String XSDPath) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::validate");
		// create a JAXBContext capable of handling classes generated into the com.xml.test.mapping.object package
		JAXBContext jc;
		Unmarshaller u = null;
		try {
			jc = JAXBContext.newInstance(packageName);
			u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				Schema schema = sf.newSchema(new java.io.File(XSDPath));
				u.setSchema(schema);
				u.setEventHandler(new ValidationEventHandler() {
					public boolean handleEvent(ValidationEvent ve) {
						if (ve.getSeverity() == ValidationEvent.WARNING || ve.getSeverity() != ValidationEvent.WARNING) {
							ValidationEventLocator vel = ve.getLocator();
							logger.verbose("@@@@@ Line:Col[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
							logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::validate (false)");
							return false;
						}
						logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::validate (true)");
						return true;
					}
				});
			} catch (org.xml.sax.SAXException se) {
				logger.error("Unable to validate due to following error.");
				logger.error(se.getLocalizedMessage(), se);
			}
		} catch (JAXBException e) {
			logger.error("@@@@@ Exiting NWCGWebServiceUtils::validate (JAXBException)");
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::validate (end)");
		return u;
	}

	/**
	 * 
	 * Method to get the String from a java object 
	 * 
	 * @param req
	 * @param className
	 * @param packageName
	 * @return
	 */
	public static Document getMarshallDocument(Object req, String qName,
			String packageName) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::getMarshallDocument");
		Document doc = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(packageName);
			Marshaller marshal = ctx.createMarshaller();
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement element = new JAXBElement(new QName("", qName), req
					.getClass(), null, req);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException pce) {
				logger.error("@@@@@ Exiting NWCGWebServiceUtils::getMarshallDocument (ParserConfigurationException)");
				//logger.printStackTrace(pce);
			}
			if (db != null)
				doc = db.newDocument();

			marshal.marshal(element, doc);
		} catch (JAXBException e) {
			logger.error("@@@@@ Exiting NWCGWebServiceUtils::getMarshallDocument (JAXBException)");
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::getMarshallDocument");
		return doc;
	}

	/**
	 *
	 * 
	 * Method to get the String from a java object 
	 * 
	 * @param req
	 * @param className
	 * @param packageName
	 * @return
	 */
	public static String getMarshallString(Object req, String qName,
			String packageName, String encoding) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::getMarshallString");
		Document doc = getMarshallDocument(req, qName, packageName);
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::getMarshallString");
		return serialize(doc, encoding, true);
	}

	/**
	 * 
	 * 
	 * Method to serialize a XML node to a String
	 * 
	 *  @param node
	 *  @param encoding
	 *  @param indenting
	 *  
	 *  @return String
	 */
	public static String serialize(Node node, String encoding, boolean indenting) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::serialize");
		OutputFormat outFmt = null;
		StringWriter strWriter = null;
		XMLSerializer xmlSerializer = null;
		String retVal = null;
		try {
			outFmt = new OutputFormat("xml", encoding, indenting);
			outFmt.setOmitXMLDeclaration(true);
			strWriter = new StringWriter();
			xmlSerializer = new XMLSerializer(strWriter, outFmt);
			if (node == null) {
				logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::serialize (null)");
				return "null";
			}
			short ntype = node.getNodeType();
			switch (ntype) {
			case Node.DOCUMENT_FRAGMENT_NODE:
				xmlSerializer.serialize((DocumentFragment) node);
				break;
			case Node.DOCUMENT_NODE:
				xmlSerializer.serialize((Document) node);
				break;
			case Node.ELEMENT_NODE:
				xmlSerializer.serialize((Element) node);
				break;
			default:
				throw new IOException(
						"Can serialize only Document, DocumentFragment and Element type nodes");
			}
			retVal = strWriter.toString();
		} catch (IOException e) {
			retVal = e.getMessage();
		} finally {
			try {
				strWriter.close();
			} catch (IOException ie) {
			}
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::serialize");
		return retVal;
	}

	/**
	 * @author sdas
	 * 
	 * Generic method to invoke standard Yantra API
	 * 
	 * @param apiName
	 * @param ipXML
	 * @param templateXML
	 * 
	 * @return Document
	 *
	 */
	public static Document invokeAPI(String apiName, Document ipXML,
			Document templateXML) throws RemoteException, YFSException {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::invokeAPI");
		// can not use commonutils.invokeapi as it requires one env object
		if (templateXML != null) {
			env.setApiTemplate(apiName, templateXML);
		}
		Document returnDoc = api.invoke(env, apiName, ipXML);
		env.clearApiTemplate(apiName);
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::invokeAPI");
		return returnDoc;
	}

	/**
	 * @author sdas
	 * 
	 * Generic method for invoking Yantra SDF service
	 * 
	 * @param serviceName
	 * @param ipXML
	 * @return Document
	 * 
	 */
	public static Document invokeServiceMethod(String serviceName,
			Document ipXML) throws Exception {
		logger.verbose("@@@@@ In NWCGWebServiceUtils::invokeServiceMethod");
		return CommonUtilities.invokeService(env, serviceName, ipXML);
	}

	/**
	 *	@author sdas
	 *
	 *	Generic raise alert method
	 *
	 *	@param queueID - ALERT QUEUE ID
	 *	@param inDoc - Document which can be put as a reference
	 *	@param errDesc - ERROR DESCRIPTION
	 *	@param e - Throwable object
	 *	@param map - Map object of reference name value pairs.
	 *
	 *	@return void
	 */
	public static void raiseAlert(String queueID, String errDesc, Document inDoc, Throwable e, Map<Object, Object> map) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::raiseAlert");

		String itemID = "";
		Document rt_Inbox = null;
		try {
			rt_Inbox = XMLUtil.newDocument();
			Element el_Inbox = rt_Inbox.createElement("Inbox");
			rt_Inbox.appendChild(el_Inbox);
			el_Inbox.setAttribute("EnterpriseKey", NWCGAAConstants.ENTERPRISE_KEY);
			el_Inbox.setAttribute("InboxType", NWCGAAConstants.INBOX_TYPE);
			el_Inbox.setAttribute("Priority", NWCGAAConstants.PRIORITY);
			el_Inbox.setAttribute("QueueId", queueID);
			el_Inbox.setAttribute("AssignedToUserId", NWCGAAConstants.ASSIGNED_TO_USER_ID);
			Element el_InboxReferencesList = rt_Inbox.createElement("InboxReferencesList");
			el_Inbox.appendChild(el_InboxReferencesList);
			if (null != map) {
				el_Inbox.setAttribute("ExceptionType", (String) map.get("ExceptionType"));
				Iterator<Object> k = map.keySet().iterator();
				while (k.hasNext()) {
					String nextKey = (String) k.next();
					String nextValueStr;
					Object nextValue = map.get(nextKey);
					if (!(nextValue instanceof String)) {
						continue;
					} else {
						nextValueStr = (String) map.get(nextKey);
					}
					if (nextKey.equals("messageBody") || nextKey.equals("message") || nextKey.equals("SOAP-MESSAGE") || nextKey.equals("password") || nextKey.equals("namespace") || nextKey.equals("customProperty") || nextKey.equals("serviceName") || nextKey.equals("message_key") || nextKey.equals("EntityName")) {
						continue;
					}
					Element el_InboxRef = rt_Inbox.createElement("InboxReferences");
					el_InboxReferencesList.appendChild(el_InboxRef);
					String entityName = (String) map.get("EntityName");
					String inboxNameRef = nextKey;
					if (nextKey.equals("latest_message_key")) {
						inboxNameRef = "Messsage Reference Number";
					} else if (nextKey.equals("EntityValue")) {
						if (entityName.equalsIgnoreCase("CATALOG")) {
							inboxNameRef = "Item ID - UOM";
						} else if (entityName.equalsIgnoreCase("ISSUE")) {
							inboxNameRef = "Issue Number";
						} else if (entityName.equalsIgnoreCase("INCIDENT")) {
							inboxNameRef = "Incident # / Year";
						}
					} else if (nextKey.equals("EntityKey")) {
						if (entityName.equalsIgnoreCase("CATALOG")) {
							inboxNameRef = "Item Key";
						} else if (entityName.equalsIgnoreCase("ISSUE")) {
							inboxNameRef = "Issue Key";
						} else if (entityName.equalsIgnoreCase("INCIDENT")) {
							inboxNameRef = "Incident Key";
						}
					} else {
						el_InboxRef.setAttribute("Name", inboxNameRef);
					}
					el_InboxRef.setAttribute("Name", inboxNameRef);
					el_InboxRef.setAttribute("ReferenceType", "TEXT");
					el_InboxRef.setAttribute("Value", nextValueStr);
					if (nextKey.equals("CatalogItemName") || nextKey.equals("DistributionId")) {
						itemID = (String) map.get(nextKey);
					}
				}
			}
			el_Inbox.setAttribute("Description", errDesc.concat(" " + itemID));
			if (null != e) {
				StringWriter writer = new StringWriter();
				e.printStackTrace(new PrintWriter(writer));
				String expTrace = writer.getBuffer().substring(0, 1000);
				el_Inbox.setAttribute("DetailDescription", expTrace);
			} else {
				el_Inbox.setAttribute("DetailDescription", errDesc.concat(" " + itemID));
			}
			CommonUtilities.invokeAPI(env, NWCGAAConstants.API_CREATE_EXCEPTION, rt_Inbox);
		} catch (Exception e1) {
			logger.error("!!!!! NWCGWebServiceUtils.raiseAlert>>Exception while creating alert xml= " + XMLUtil.getXMLString(rt_Inbox));
			e1.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::raiseAlert");
	}

	public static String getStringToken(String userName, String loginID,
			String pw_key, String message_name) {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::getStringToken");
		if (pw_key == "" || pw_key == null)
			pw_key = " ";
		if (loginID == "" || loginID == null)
			loginID = " ";
		if (userName == "" || userName == null)
			userName = " ";
		if (message_name == "" || message_name == null)
			message_name = " ";
		String sysID = NWCGAAConstants.SYSTEM_ID;
		if (sysID == "" || sysID == null)
			sysID = " ";

		String string_token = "#".concat(pw_key).concat("#").concat(loginID)
				.concat("#").concat(userName).concat("#").concat(message_name)
				.concat("#").concat(sysID).concat("#");
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::getStringToken");
		return string_token;
	}

	public void setProperties(Properties properties) throws Exception {
		logger.verbose("@@@@@ In NWCGWebServiceUtils::setProperties");
		serviceProp = properties;
	}

	public static Properties getServiceProp() {
		logger.verbose("@@@@@ Entering NWCGWebServiceUtils::getServiceProp");
		if (null == serviceProp) {
			serviceProp = new Properties();
		}
		logger.verbose("@@@@@ Exiting NWCGWebServiceUtils::getServiceProp");
		return serviceProp;
	}

	public static boolean isEmpty(String str) {
		logger.verbose("@@@@@ In NWCGWebServiceUtils::isEmpty");
		return (str == null || str.trim().length() == 0);
	}
}
