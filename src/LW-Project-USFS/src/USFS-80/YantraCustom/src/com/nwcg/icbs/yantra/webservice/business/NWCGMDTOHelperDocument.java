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

package com.nwcg.icbs.yantra.webservice.business;

import java.util.HashMap;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.StringUtil;

/**
 * @author jvishwakarma This is helper class for Medatadata object, currently we
 *         do not have any way to get a MDTO object from Sterling or ROSS
 *         document. Everytime we need to have method to setMap this helper
 *         class is to avoid repetative code
 * 
 */
public class NWCGMDTOHelperDocument {
	// field to capture the document in either ROSS or ICBS format
	private Document docInExternalFormat = null;

	private String UserName = null;

	private String MesssageName = null;

	private String DistID = null;

	private String Namespace = null;

	private String EntityName = null;

	private String EntityKey = null;

	private String EntityValue = null;

	public NWCGMDTOHelperDocument(Document doc) {
		System.out.println("@@@@@ In NWCGMDTOHelperDocument::NWCGMDTOHelperDocument 0 @@@@@");
		docInExternalFormat = doc;
	}

	/*
	 * This constructor takes the document and map containing other helper
	 * objects. map can be null
	 */
	public NWCGMDTOHelperDocument(Document doc, HashMap<String, String> map) {
		System.out.println("@@@@@ Entering NWCGMDTOHelperDocument::NWCGMDTOHelperDocument 1 @@@@@");
		
		docInExternalFormat = doc;
		if (map != null) {
			setUserName(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_USERNAME)));
			setMesssageName(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_MSGNAME)));
			setDistID(StringUtil.nonNull(map.get(NWCGAAConstants.MDTO_DISTID)));
			setNamespace(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_NAMESPACE)));
			setEntityName(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_ENTNAME)));
			setEntityKey(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_ENTKEY)));
			setEntityValue(StringUtil.nonNull(map
					.get(NWCGAAConstants.MDTO_ENTVALUE)));
		}
		
		System.out.println("@@@@@ Exiting NWCGMDTOHelperDocument::NWCGMDTOHelperDocument 1 @@@@@");
	}

	public NWCGMDTOHelperDocument(Document doc, String userName,
			String msgName, String distID, String namespace,
			String entityName, String entityKey, String entityValue) {
		System.out.println("@@@@@ Entering NWCGMDTOHelperDocument::NWCGMDTOHelperDocument 2 @@@@@");		
		
		docInExternalFormat = doc;
		setUserName(StringUtil.nonNull(userName));
		setMesssageName(StringUtil.nonNull(msgName));
		setDistID(StringUtil.nonNull(distID));
		setNamespace(StringUtil.nonNull(namespace));
		setEntityName(StringUtil.nonNull(entityName));
		setEntityKey(StringUtil.nonNull(entityKey));
		setEntityValue(StringUtil.nonNull(entityValue));
		
		System.out.println("@@@@@ Exiting NWCGMDTOHelperDocument::NWCGMDTOHelperDocument 2 @@@@@");
	}

	public Document getDocInExternalFormat() {
		return docInExternalFormat;
	}

	public void setDocInExternalFormat(Document docInExternalFormat) {
		this.docInExternalFormat = docInExternalFormat;
	}

	/**
	 * @return the distID
	 */
	public String getDistID() {
		return DistID;
	}

	/**
	 * @param distID
	 *            the distID to set
	 */
	public void setDistID(String distID) {
		DistID = distID;
	}

	/**
	 * @return the entityKey
	 */
	public String getEntityKey() {
		return EntityKey;
	}

	/**
	 * @param entityKey
	 *            the entityKey to set
	 */
	public void setEntityKey(String entityKey) {
		EntityKey = entityKey;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return EntityName;
	}

	/**
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		EntityName = entityName;
	}

	/**
	 * @return the entityValue
	 */
	public String getEntityValue() {
		return EntityValue;
	}

	/**
	 * @param entityValue
	 *            the entityValue to set
	 */
	public void setEntityValue(String entityValue) {
		EntityValue = entityValue;
	}

	/**
	 * @return the msgName
	 */
	public String getMesssageName() {
		return MesssageName;
	}

	/**
	 * @param msgName
	 *            the msgName to set
	 */
	public void setMesssageName(String msgName) {
		MesssageName = msgName;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return Namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		Namespace = namespace;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
}