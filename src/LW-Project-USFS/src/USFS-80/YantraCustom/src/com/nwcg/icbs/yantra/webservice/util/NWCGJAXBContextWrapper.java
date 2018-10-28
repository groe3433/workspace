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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * This is wrapper around cached JAXBContext to each type of namespace
 */
public class NWCGJAXBContextWrapper {

	// private static Logger logger =
	// Logger.getLogger(NWCGJAXBContextWrapper.class.getName());
	private static JAXBContext contextCatalog = null;

	private static JAXBContext contextResourceOrder = null;

	private static JAXBContext contextResourceOrderNotification = null;

	private static JAXBContext contextCommonTypes = null;

	static {
		try {
			// initializing the static variables
			NWCGLoggerUtil.Log.finer("**** Creating CATALOG Context ***");
			NWCGLoggerUtil.Log
					.finer("BEGIN TIMER: JAXBContext.newInstance(CATALOG) ");
			contextCatalog = JAXBContext
					.newInstance(NWCGAAConstants.CATALOG_PACKAGE_NAME);
			NWCGLoggerUtil.Log
					.finer("END TIMER: JAXBContext.newInstance(CATALOG) ");
			NWCGLoggerUtil.Log.finer("**** Created CATALOG Context ***");

			NWCGLoggerUtil.Log
					.finer("**** Creating RESOURCE ORDER Context ***");
			NWCGLoggerUtil.Log
					.finer("BEGIN TIMER: JAXBContext.newInstance(RESOURCE ORDER) ");
			contextResourceOrder = JAXBContext
					.newInstance(NWCGAAConstants.RESOURCEORDER_PACKAGE_NAME);
			NWCGLoggerUtil.Log
					.finer("END TIMER: JAXBContext.newInstance(RESOURCE ORDER) ");
			NWCGLoggerUtil.Log.finer("**** Created RESOURCE ORDER Context ***");

			NWCGLoggerUtil.Log
					.finer("**** Creating RESOURCE ORDER Notification Context ***");
			NWCGLoggerUtil.Log
					.finer("BEGIN TIMER: JAXBContext.newInstance(RESOURCE ORDER NOTIFICATION) ");
			contextResourceOrderNotification = JAXBContext
					.newInstance(NWCGAAConstants.RESOURCEORDER_NOTIFICATION_PACKAGE_NAME);
			NWCGLoggerUtil.Log
					.finer("END TIMER: JAXBContext.newInstance(RESOURCE ORDER NOTIFICATION) ");
			NWCGLoggerUtil.Log
					.finer("**** Created RESOURCE ORDER Notification Context ***");

			NWCGLoggerUtil.Log
					.finer("**** Creating Common Package Context ***");
			NWCGLoggerUtil.Log
					.finer("BEGIN TIMER: JAXBContext.newInstance(COMMON) ");
			contextCommonTypes = JAXBContext
					.newInstance(NWCGAAConstants.COMMON_PACKAGE_NAME);
			NWCGLoggerUtil.Log
					.finer("END TIMER: JAXBContext.newInstance(COMMON) ");
			NWCGLoggerUtil.Log.finer("**** Created Common Package Context ***");

		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Exception caught " + e);
			e.printStackTrace();
		}
	}

	// call this method to to convert document to object, later this method will
	// removed and getObjectFromNode will be used
	public Object getObjectFromDocument(Document document, String strMessageName)
			throws JAXBException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromDocument @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(strMessageName,
				null);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromDocument @@@@@");
		return processUnmarshalling(document, context);
	}

	public Object getObjectFromUnknownDocument(Document document) {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromUnknownDocument @@@@@");
		
		Object returnObj = null;

		try {
			returnObj = getObjectFromDocument(document, new URL(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		if (returnObj == null) {
			NWCGLoggerUtil.Log
					.warning("Resource order marshalling failed, trying catalog now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromDocument(
						document, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		if (returnObj == null) {
			NWCGLoggerUtil.Log
					.warning("catalog marshalling failed, trying resource now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromDocument(
						document,
						new URL(NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromUnknownDocument @@@@@");
		return returnObj;
	}

	// similar to aobve method but it is used to convert Node instead of
	// Document.
	public Object getObjectFromNode(Node node, String strMessageName)
			throws JAXBException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromNode 1 @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(strMessageName,
				null);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromNode 1 @@@@@");
		return processUnmarshalling(node, context);
	}

	public Object getObjectFromNode(Node node, URL url) throws JAXBException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromNode 2 @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(null, url);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromNode 2 @@@@@");
		return processUnmarshalling(node, context);
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public Object getObjectFromUnknownNode(Node document) {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromUnknownNode @@@@@");
		
		Object returnObj = null;

		try {
			returnObj = getObjectFromNode(document, new URL(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		if (returnObj == null) {
			NWCGLoggerUtil.Log
					.warning("Resource order marshalling failed, trying catalog now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromNode(
						document, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		if (returnObj == null) {
			NWCGLoggerUtil.Log
					.warning("catalog marshalling failed, trying resource now");
			try {
				returnObj = new NWCGJAXBContextWrapper().getObjectFromNode(
						document,
						new URL(NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromUnknownNode @@@@@");
		return returnObj;
	}

	public Object getObjectFromDocument(Document document, URL urlNamespace)
			throws JAXBException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getObjectFromDocument @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(null, urlNamespace);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getObjectFromDocument @@@@@");
		return processUnmarshalling(document, context);
	}

	public Document getDocumentFromObject(Object obj, Document document,
			String strMessageName) throws JAXBException,
			ParserConfigurationException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getDocumentFromObject 1 @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(strMessageName,
				null);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getDocumentFromObject 1 @@@@@");
		return processMarshalling(obj, document, context);
	}

	public Document getDocumentFromObject(Object obj, Document document,
			URL urlNamespace) throws JAXBException,
			ParserConfigurationException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getDocumentFromObject 2 @@@@@");
		
		JAXBContext context = getJAXBContextFromMessageName(null, urlNamespace);
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getDocumentFromObject 2 @@@@@");
		return processMarshalling(obj, document, context);
	}

	/*
	 * This method gets the document object for unknown objects. Typically this
	 * mehtod will be used with methods like get operation results and deliver
	 * operations results where we have the response object but we don't know
	 * what that object is. Developer shuld avoid using this method for other
	 * operatiions where we know what object we are parsing.
	 */

	public Document getDocumentFromUnknownObject(Object obj, Document document) {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getDocumentFromUnknownObject @@@@@");
		
		try {
			document = getDocumentFromObject(obj, document, new URL(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		if (document == null) {
			NWCGLoggerUtil.Log
					.warning("Resource order marshalling failed, trying Catalog now");
			try {
				document = new NWCGJAXBContextWrapper().getDocumentFromObject(
						obj, document, new URL(
								NWCGAAConstants.CATALOG_NAMESPACE));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		if (document == null) {
			NWCGLoggerUtil.Log
					.warning("Catalog marshalling failed, trying Resource now");
			try {
				document = new NWCGJAXBContextWrapper().getDocumentFromObject(
						obj, document, new URL(
								NWCGAAConstants.RESOURCE_NAMESPACE_OB));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getDocumentFromUnknownObject @@@@@");
		return document;
	}

	private Object processUnmarshalling(Node node, JAXBContext context)
			throws JAXBException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::processUnmarshalling @@@@@");
		
		Unmarshaller unmar = context.createUnmarshaller();
		unmar
				.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		NWCGLoggerUtil.Log.finer("*** Unmarshaller Created***");
		NWCGLoggerUtil.Log.finer("*** Unmarshalling Object ***");
		Object returnObj = unmar.unmarshal(node);
		NWCGLoggerUtil.Log.finer("*** Object Unmarshalled***");

		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::processUnmarshalling @@@@@");
		return returnObj;
	}

	private Document processMarshalling(Object obj, Document document,
			JAXBContext context) throws JAXBException,
			ParserConfigurationException {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::processMarshalling @@@@@");
		
		NWCGLoggerUtil.Log.finer("*** Creating Marshaller ***");
		Marshaller mar = context.createMarshaller();
		NWCGLoggerUtil.Log.finer("*** Marshaller Created***");

		if (document == null)
			document = XMLUtil.getDocument();

		NWCGLoggerUtil.Log.finer("*** Marshalling Object ***");
		mar.marshal(obj, document);
		NWCGLoggerUtil.Log.finer("*** Object Marshalled***");

		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::processMarshalling @@@@@");
		return document;
	}

	/**
	 * Client should pass either message name or namespace, if both passed
	 * preference is given to the namespace.
	 */
	private JAXBContext getJAXBContextFromMessageName(String strMessageName,
			URL urlNamespace) {
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::getJAXBContextFromMessageName @@@@@");
		
		String strNamespace = null;

		if (urlNamespace == null)
			strNamespace = ResourceUtil.get(strMessageName + ".namespace");
		else
			strNamespace = urlNamespace.toExternalForm(); // can be replaced
															// by toString()

		if (strNamespace.equals(NWCGAAConstants.CATALOG_NAMESPACE)) {
			System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextCatalog) @@@@@");
			return contextCatalog;
		} else if (strNamespace
				.equals(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE)) {
			System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextResourceOrder) @@@@@");
			return contextResourceOrder;
		} else if (strNamespace
				.equals(NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE)) {
			System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextResourceOrderNotification) @@@@@");
			return contextResourceOrderNotification;
		} else if (strNamespace.equals(NWCGAAConstants.COMMON_TYPES_NAMESPACE)) {
			System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName (contextCommonTypes) @@@@@");
			return contextCommonTypes;
		}
		// Program should never reach this statement... if reached then its a time to add new else if statement
		
		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::getJAXBContextFromMessageName @@@@@");
		return null;
	}
}