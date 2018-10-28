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

package com.nwcg.icbs.yantra.webservice.business.inf;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGMDTOHelperDocument;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * @author sp-tek-oginzburg
 * 
 */
public class NWCGMetaDataTransferObject implements
		NWCGMetaDataTransferObjectInterface {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGMetaDataTransferObject.class);

	public static NWCGMetaDataTransferObjectInterface getInstance() {
		return new NWCGMetaDataTransferObject();
	}

	public Document getDocFromMap(HashMap inMap) {
		logger.verbose("@@@@@ In NWCGMetaDataTransferObject::getDocFromMap");
		setDtoMap(inMap);
		return getDocFromMap();
	}

	public Document getDocFromMap() {
		logger.verbose("@@@@@ Entering NWCGMetaDataTransferObject::getDocFromMap");
		Document aDoc = null;
		if ((dtoMap != null) && (!dtoMap.isEmpty())) {
			try {
				aDoc = XMLUtil
						.createDocument(NWCGAAConstants.MDTO_DOCUMENT_ROOT);
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_DISTID,
						(String) dtoMap.get(NWCGAAConstants.MDTO_DISTID));
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_USERNAME,
						(String) dtoMap.get(NWCGAAConstants.MDTO_USERNAME)); //$NON-NLS-1$ //$NON-NLS-2$
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_NAMESPACE,
						(String) dtoMap.get(NWCGAAConstants.MDTO_NAMESPACE)); //$NON-NLS-1$ //$NON-NLS-2$
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_MSGNAME,
						(String) dtoMap.get(NWCGAAConstants.MDTO_MSGNAME)); //$NON-NLS-1$ //$NON-NLS-2$
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_ENTNAME,
						(String) dtoMap.get(NWCGAAConstants.MDTO_ENTNAME)); //$NON-NLS-1$ //$NON-NLS-2$
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_ENTKEY,
						(String) dtoMap.get(NWCGAAConstants.MDTO_ENTKEY)); //$NON-NLS-1$ //$NON-NLS-2$
				aDoc = setAttribute(aDoc, NWCGAAConstants.MDTO_ENTVALUE,
						(String) dtoMap.get(NWCGAAConstants.MDTO_ENTVALUE)); //$NON-NLS-1$ //$NON-NLS-2$
				String bodyStr = (String) dtoMap
						.get(NWCGAAConstants.MDTO_MSGBODY);
				if (!isEmpty(bodyStr)) {
					Document bodyDoc = XMLUtil.getDocument(bodyStr);
					XMLUtil.addDocument(aDoc, bodyDoc, false);
				}
			} catch (ParserConfigurationException e) {
				aDoc = null;
				logger.error("!!!!! getDocFromMap>>ParserConfigurationException" + e.getMessage()); //$NON-NLS-1$
				logger.error(e.getLocalizedMessage(), e);
			} catch (SAXException e) {
				logger.error("!!!!! getDocFromMap>>SAXException:"
						+ e.getMessage());
				logger.error(e.getLocalizedMessage(), e);
			} catch (IOException e) {
				logger.error("!!!!! getDocFromMap>>IOException:"
						+ e.getMessage());
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		// if(aDoc != null)
		setDtoDoc(aDoc);
		logger.verbose("@@@@@ Exiting NWCGMetaDataTransferObject::getDocFromMap");
		return this.dtoDoc;
	}

	public boolean isMapValid() {
		logger.verbose("@@@@@ Entering NWCGMetaDataTransferObject::isMapValid");
		boolean valid = false;

		// checking presence of required data
		String uid = (String) dtoMap.get(NWCGAAConstants.MDTO_USERNAME);
		String nms = (String) dtoMap.get(NWCGAAConstants.MDTO_NAMESPACE);
		String msn = (String) dtoMap.get(NWCGAAConstants.MDTO_MSGNAME);
		String msb = (String) dtoMap.get(NWCGAAConstants.MDTO_MSGBODY);
		if ((!isEmpty(uid)) && (!isEmpty(nms)) && (!isEmpty(msn))
				&& (!isEmpty(msb)))
			valid = true;

		if (!valid) {
			logger.verbose("NWCGMetaDataTransferObject::isMapValid returing false uid="
							+ uid + " nms=" + nms + " msb=" + msb);
		}
		logger.verbose("@@@@@ Exiting NWCGMetaDataTransferObject::isMapValid");
		return valid;
	}

	public String toString() {
		logger.verbose("@@@@@ Entering NWCGMetaDataTransferObject::toString");
		StringBuffer objBuf = new StringBuffer(NWCGAAConstants.MDTO_DOCUMENT_ROOT + ">>");
		objBuf.append("; " + NWCGAAConstants.MDTO_USERNAME + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_USERNAME));
		objBuf.append("; " + NWCGAAConstants.MDTO_MSGNAME + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_MSGNAME));
		objBuf.append("; " + NWCGAAConstants.MDTO_DISTID + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_DISTID));
		objBuf.append("; " + NWCGAAConstants.MDTO_NAMESPACE + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_NAMESPACE));
		objBuf.append("; " + NWCGAAConstants.MDTO_ENTNAME + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_ENTNAME));
		objBuf.append("; " + NWCGAAConstants.MDTO_ENTKEY + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_ENTKEY));
		objBuf.append("; " + NWCGAAConstants.MDTO_ENTVALUE + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_ENTVALUE));
		objBuf.append("; " + NWCGAAConstants.MDTO_MSGBODY + "=");
		objBuf.append((String) dtoMap.get(NWCGAAConstants.MDTO_MSGBODY));
		logger.verbose("@@@@@ Exiting NWCGMetaDataTransferObject::toString");
		return objBuf.toString();
	}

	// This method always expects the root element as
	// MetadataTransferObject, no need to get the elements by tag name
	public HashMap<Object, Object> getMapFromDoc(Document mdtoDoc) {
		logger.verbose("@@@@@ Entering NWCGMetaDataTransferObject::getMapFromDoc");
		HashMap<Object, Object> imap = new HashMap<Object, Object>();
		Element elemRoot = mdtoDoc.getDocumentElement();
		{
			imap.put(NWCGAAConstants.MDTO_USERNAME, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_USERNAME));
			imap.put(NWCGAAConstants.MDTO_MSGNAME, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_MSGNAME));
			imap.put(NWCGAAConstants.MDTO_DISTID, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_DISTID));
			imap.put(NWCGAAConstants.MDTO_NAMESPACE, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_NAMESPACE));
			imap.put(NWCGAAConstants.MDTO_ENTNAME, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_ENTNAME));
			imap.put(NWCGAAConstants.MDTO_ENTKEY, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_ENTKEY));
			imap.put(NWCGAAConstants.MDTO_ENTVALUE, elemRoot
					.getAttribute(NWCGAAConstants.MDTO_ENTVALUE));

			// now get msg body since we know message name
			/*
			 * nl =
			 * mdtoDoc.getChildNodes();//.getElementsByTagName("ro:PlaceResourceRequestResp");//(String)elem.getAttribute(MDTO_MSGNAME));
			 * if(nl.getLength()> 0) { elem = (Element)nl.item(0);
			 */
			// this code assumes the MDTO object will have one and only one
			// child and that clind will be the actual xml message to be send
			Node ce = elemRoot.getFirstChild();
			Element firstChildElem = (Element) ce;

			imap.put(NWCGAAConstants.MDTO_ENTNAME, firstChildElem
					.getAttribute(NWCGAAConstants.MDTO_ENTNAME));
			imap.put(NWCGAAConstants.MDTO_ENTKEY, firstChildElem
					.getAttribute(NWCGAAConstants.MDTO_ENTKEY));
			imap.put(NWCGAAConstants.MDTO_ENTVALUE, firstChildElem
					.getAttribute(NWCGAAConstants.MDTO_ENTVALUE));

			String body = XMLUtil.serialize(ce);// .getElementXMLString(ce);
			imap.put(NWCGAAConstants.MDTO_MSGBODY, body);
			// }

		}
		setDtoMap(imap);
		logger.verbose("@@@@@ Exiting NWCGMetaDataTransferObject::getMapFromDoc");
		return imap;
	}

	public Document getDtoDoc() {
		return dtoDoc;
	}

	public void setDtoDoc(Document dtoDoc) {
		this.dtoDoc = dtoDoc;
	}

	// returns itself as a map
	public HashMap getDtoMap() {
		return dtoMap;
	}

	// set itself as a map
	public void setDtoMap(HashMap dtoMap) {
		this.dtoMap = dtoMap;
	}

	// methods to be moved out to util class
	private Document setAttribute(Document doc, String attribute, String value) {
		if (doc != null) {
			if (!isEmpty(value)) {
				doc.getDocumentElement().setAttribute(attribute, value);
			}
		}
		
		return doc;
	}

	private boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	//
	private HashMap dtoMap = null;

	private Document dtoDoc = null;

	private NWCGMetaDataTransferObject() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nwcg.icbs.yantra.webservice.business.inf.NWCGMetaDataTransferObjectInterface#getMDTODocumentFromExternalDoc(com.nwcg.icbs.yantra.webservice.business.NWCGROSSDocument)
	 *      added this method to remove overhead of string to document
	 *      conversion.
	 */
	public Document getMDTODocumentFromHelperDoc(NWCGMDTOHelperDocument doc) {
		logger.verbose("@@@@@ Entering NWCGMetaDataTransferObject::getMapFromDoc");
		Document aDoc = null;
		if (doc != null) {
			try {
				aDoc = XMLUtil
						.createDocument(NWCGAAConstants.MDTO_DOCUMENT_ROOT);
				Element elemRoot = aDoc.getDocumentElement();

				elemRoot.setAttribute(NWCGAAConstants.MDTO_DISTID, doc
						.getDistID());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_USERNAME, doc
						.getUserName());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, doc
						.getNamespace());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_MSGNAME, doc
						.getMesssageName());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_ENTNAME, doc
						.getEntityName());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_ENTKEY, doc
						.getEntityKey());
				elemRoot.setAttribute(NWCGAAConstants.MDTO_ENTVALUE, doc
						.getEntityValue());
				XMLUtil.addDocument(aDoc, doc.getDocInExternalFormat(), false);

			} catch (ParserConfigurationException e) {
				aDoc = null;
				logger.error("!!!!! getDocFromMap>>ParserConfigurationException" + e.getMessage()); //$NON-NLS-1$
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		//if(aDoc != null)
		setDtoDoc(aDoc);
		logger.verbose("@@@@@ Exiting NWCGMetaDataTransferObject::getMapFromDoc");
		return aDoc;
	}
}