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

package com.nwcg.icbs.yantra.webservice.util.api;//import gov.nwcg.services.icbs.auth.wsdl._1.AuthPortImpl;
//import gov.nwcg.services.ross.common_types._1.AuthUserReq;

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
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGWebServiceUtils {

	private static YIFApi api = null;

	private static YFSEnvironment env = null;

	private static Properties serviceProp = null;

	static {
		try {
			NWCGLoggerUtil.Log.info("Creating the API in local mode");

			api = YIFClientFactory.getInstance().getApi("LOCAL");
			Document document = XMLUtil.createDocument("YFSEnvironment");
			Element elem = document.getDocumentElement();
			elem.setAttribute("userId", NWCGAAConstants.ENV_USER_ID);
			elem.setAttribute("progId", NWCGAAConstants.ENV_PROG_ID);
			env = api.createEnvironment(document);

			NWCGLoggerUtil.Log.info("Got the static API as :: " + api);

		} catch (YIFClientCreationException e) {
			NWCGLoggerUtil.Log.warning("YIFClientCreationException ==> " + e);
			e.printStackTrace();
		} catch (ParserConfigurationException p) {
			NWCGLoggerUtil.Log.warning("YIFClientCreationException ==> " + p);
			p.printStackTrace();
		} catch (YFSException ye) {
			NWCGLoggerUtil.Log.warning("YIFClientCreationException ==> " + ye);
			ye.printStackTrace();
		} catch (RemoteException re) {
			NWCGLoggerUtil.Log.warning("YIFClientCreationException ==> " + re);
			re.printStackTrace();
		}
	}

	public static YFSEnvironment getEnvironment() {
		return env;
	}

	/**
	 * Use xslt transformation to get a compatiable xml document
	 * Should be useful in Yantra to ROSS and ROSS to Yantra transformation.
	 * @param doc
	 * @return
	 */
	public static Document transformDocument(Document doc, String xsltSheet) {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::transformDocument @@@@@");

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
			
			System.out.println("@@@@@ Exiting NWCGWebServiceUtils::transformDocument @@@@@");
			return retDoc;

		} catch (Exception e) {
			System.out.println("@@@@@ Exiting NWCGWebServiceUtils::transformDocument @@@@@");
			return null;
		}
	}

	/**
	 * Use XML document to get corresponding Class. 
	 * @return
	 */
	public static Object getUnMarshallClass(Document doc, String packageName) {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::getUnMarshallClass @@@@@");
		
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);

			Unmarshaller u = jc.createUnmarshaller();

			// unmarshal a po instance document into a tree of Java content
			JAXBElement poe = (JAXBElement) u.unmarshal(doc);
			return poe.getValue();

		} catch (JAXBException je) {
			je.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::getUnMarshallClass @@@@@");
		return null;
	}

	/**
	 * @return boolean
	 */
	public static Unmarshaller validate(String packageName, String XSDPath) {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::validate @@@@@");
		
		// create a JAXBContext capable of handling classes generated into
		// the com.xml.test.mapping.object package
		JAXBContext jc;
		Unmarshaller u = null;
		try {
			jc = JAXBContext.newInstance(packageName);

			u = jc.createUnmarshaller();

			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				Schema schema = sf.newSchema(new java.io.File(XSDPath));
				u.setSchema(schema);
				u.setEventHandler(new ValidationEventHandler() {
					public boolean handleEvent(ValidationEvent ve) {
						if (ve.getSeverity() == ValidationEvent.WARNING
								|| ve.getSeverity() != ValidationEvent.WARNING) {
							ValidationEventLocator vel = ve.getLocator();
							NWCGLoggerUtil.Log.info("Line:Col["
									+ vel.getLineNumber() + ":"
									+ vel.getColumnNumber() + "]:"
									+ ve.getMessage());
							return false;
						}
						return true;
					}
				});
			} catch (org.xml.sax.SAXException se) {
				NWCGLoggerUtil.Log
						.warning("Unable to validate due to following error.");
				NWCGLoggerUtil.printStackTraceToLog(se);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::validate @@@@@");
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
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::getMarshallDocument @@@@@");
		
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
				pce.printStackTrace();
			}
			if (db != null)
				doc = db.newDocument();

			marshal.marshal(element, doc);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::getMarshallDocument @@@@@");
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
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::getMarshallString @@@@@");
		
		Document doc = getMarshallDocument(req, qName, packageName);
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::getMarshallString @@@@@");
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
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::serialize @@@@@");
		
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
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::serialize @@@@@");
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
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::invokeAPI @@@@@");
		
		// can not use commonutils.invokeapi as it requires one env object
		if (templateXML != null) {
			env.setApiTemplate(apiName, templateXML);
		}
		Document returnDoc = api.invoke(env, apiName, ipXML);
		env.clearApiTemplate(apiName);
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::invokeAPI @@@@@");
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
		System.out.println("@@@@@ In NWCGWebServiceUtils::invokeServiceMethod @@@@@");
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
	public static void raiseAlert(String queueID, String errDesc,
			Document inDoc, Throwable e, Map<Object, Object> map) {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::raiseAlert @@@@@");
		
		//boolean addAllAttributesAsRef = false;
		String itemID = "";
		Document rt_Inbox = null;
		try {
			rt_Inbox = XMLUtil.newDocument();
			Element el_Inbox = rt_Inbox.createElement("Inbox");
			rt_Inbox.appendChild(el_Inbox);

			//el_Inbox.setAttribute("Description", errDesc); // moved below
			el_Inbox.setAttribute("EnterpriseKey",
					NWCGAAConstants.ENTERPRISE_KEY);
			el_Inbox.setAttribute("InboxType", NWCGAAConstants.INBOX_TYPE);
			el_Inbox.setAttribute("Priority", NWCGAAConstants.PRIORITY);
			el_Inbox.setAttribute("QueueId", queueID);
			el_Inbox.setAttribute("AssignedToUserId",
					NWCGAAConstants.ASSIGNED_TO_USER_ID);

			Element el_InboxReferencesList = rt_Inbox
					.createElement("InboxReferencesList");
			el_Inbox.appendChild(el_InboxReferencesList);

			if (null != map) {
				el_Inbox.setAttribute("ExceptionType", (String) map
						.get("ExceptionType"));
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

					//Do not create message, messageBody, SOAP-MESSAGE inbox references.
					if (nextKey.equals("messageBody")
							|| nextKey.equals("message")
							|| nextKey.equals("SOAP-MESSAGE")
							|| nextKey.equals("password")
							|| nextKey.equals("namespace")
							|| nextKey.equals("customProperty")
							|| nextKey.equals("serviceName")
							|| nextKey.equals("message_key")
							|| nextKey.equals("EntityName")) {
						continue;
					}

					Element el_InboxRef = rt_Inbox
							.createElement("InboxReferences");
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

					// 	code added for changing the details and description to 
					// 	reflect item id or distribution id
					if (nextKey.equals("CatalogItemName")
							|| nextKey.equals("DistributionId")) {
						itemID = (String) map.get(nextKey);
					}
				}
			}

			// moved from top to here.
			/*****/
			el_Inbox.setAttribute("Description", errDesc.concat(" " + itemID));
			if (null != e) {
				StringWriter writer = new StringWriter();
				e.printStackTrace(new PrintWriter(writer));
				String expTrace = writer.getBuffer().substring(0, 1000);
				el_Inbox.setAttribute("DetailDescription", expTrace);
			} else {
				el_Inbox.setAttribute("DetailDescription", errDesc.concat(" "
						+ itemID));
			}
			/*****/
			/*String soapFault = (String) map.get(NWCGAAConstants.NAME);
			 if (soapFault == null){
			 Element el_InboxReferences = rt_Inbox.createElement("InboxReferences");
			 el_InboxReferencesList.appendChild(el_InboxReferences);
			 el_InboxReferences.setAttribute("Name",NWCGAAConstants.NAME);
			 el_InboxReferences.setAttribute("ReferenceType",NWCGAAConstants.REFERENCE_TYPE);
			 el_InboxReferences.setAttribute("Value", XMLUtil.getXMLString(inDoc));
			 //System.out.println(NWCGAAConstants.NAME+"; Value " + (XMLUtil.getXMLString(inDoc)));

			 }*/
			//System.out.println("NWCGWebServiceUtils.raiseAlert>>XML before alert ==> " + XMLUtil.getXMLString(rt_Inbox));     
			CommonUtilities.invokeAPI(env,
					NWCGAAConstants.API_CREATE_EXCEPTION, rt_Inbox);

		} catch (Exception e1) {
			NWCGLoggerUtil.Log
					.warning("NWCGWebServiceUtils.raiseAlert>>Exception while creating alert xml= "
							+ XMLUtil.getXMLString(rt_Inbox));
			e1.printStackTrace();
		}

		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::raiseAlert @@@@@");
	}

	public static String getStringToken(String userName, String loginID,
			String pw_key, String message_name) {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::getStringToken @@@@@");

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
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::getStringToken @@@@@");
		return string_token;
	}

	public void setProperties(Properties properties) throws Exception {
		System.out.println("@@@@@ In NWCGWebServiceUtils::setProperties @@@@@");
		serviceProp = properties;
	}

	public static Properties getServiceProp() {
		System.out.println("@@@@@ Entering NWCGWebServiceUtils::getServiceProp @@@@@");
		
		if (null == serviceProp) {
			serviceProp = new Properties();
		}
		
		System.out.println("@@@@@ Exiting NWCGWebServiceUtils::getServiceProp @@@@@");
		return serviceProp;
	}

	public static boolean isEmpty(String str) {
		System.out.println("@@@@@ In NWCGWebServiceUtils::isEmpty @@@@@");
		return (str == null || str.trim().length() == 0);
	}
}
