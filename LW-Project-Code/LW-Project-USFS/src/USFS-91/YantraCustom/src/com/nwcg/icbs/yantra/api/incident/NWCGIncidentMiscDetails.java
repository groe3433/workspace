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

package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * This class gets the contact list and Ross account codes
 */
public class NWCGIncidentMiscDetails implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGIncidentMiscDetails.class);
	
	private Properties myProperties = null;

	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}

	public Document getMiscDetails(YFSEnvironment env, Document ipDoc) throws Exception {
		if (ipDoc == null){
			logger.debug("NWCGIncidentMiscDetails::getMiscDetails, Input Doc is NULL");
			return ipDoc;
		}
		logger.verbose("input doc to NWCGIncidentContacts:"+XMLUtil.extractStringFromDocument(ipDoc));
		
		Document opDoc = XMLUtil.getDocument();
		Document incidentOrderDoc = XMLUtil.getDocument();
		Document contactInfoDoc = XMLUtil.getDocument();
		Document rossAcctCodesDoc = XMLUtil.getDocument();
		
		String incidentKey = NWCGConstants.EMPTY_STRING;
		
		// Fetch tag name
		String tagName = ipDoc.getDocumentElement().getTagName();
		
		try{
			NWCGIncidentROSSAccountCodes rossAcctCodes = new NWCGIncidentROSSAccountCodes();
			NWCGIncidentContacts incidentContacts = new NWCGIncidentContacts();
			if(tagName.equals(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG)){
				NodeList nwcgIncidentOrderNl = ipDoc.getDocumentElement().getElementsByTagName(NWCGAAConstants.INCIDENT_ORDER_TAG);
				if(nwcgIncidentOrderNl != null){
		            // Creating the final return document
					opDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG);
					
					for(int c=0;c<nwcgIncidentOrderNl.getLength();c++){
					    Element nwcgIncidentOrderElement = (Element)nwcgIncidentOrderNl.item(c);
						Document elemDoc = XMLUtil.getDocumentForElement(nwcgIncidentOrderElement); 
					    // Retrieving the incident key
						incidentKey = nwcgIncidentOrderElement.getAttribute(NWCGConstants.INCIDENT_KEY);
						logger.debug("Incident key:"+incidentKey);
						contactInfoDoc = incidentContacts.getIncidentOrderContactInfo(env, incidentKey);
						
						// Appending the output of NWCGGetIncidentContactsListService to the incident Doc
						if(contactInfoDoc.getChildNodes() != null){
						    incidentOrderDoc = elemDoc.getDocumentElement().appendChild(elemDoc.importNode(contactInfoDoc.getDocumentElement(),true)).getOwnerDocument();
						}
					    
					    // Get ROSS Account Codes and append to incident order
					    rossAcctCodesDoc = rossAcctCodes.getROSSAccountCodesDoc(env, incidentKey);
					    if (rossAcctCodesDoc != null){
					    	incidentOrderDoc = elemDoc.getDocumentElement().appendChild(elemDoc.importNode(rossAcctCodesDoc.getDocumentElement(), true)).getOwnerDocument();
					    }
					    
						// appending the incidentOrder to incidentOrderList
					    opDoc.getDocumentElement().appendChild(opDoc.importNode(incidentOrderDoc.getDocumentElement(),true));
					}
					logger.verbose("final Doc for NWCGIncidentOrderList:"+XMLUtil.extractStringFromDocument(opDoc));
				}
			}else{
				incidentKey = ipDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_KEY);
				logger.debug("inc key:"+incidentKey);

				Element incidentOrderElement = ipDoc.getDocumentElement();
				contactInfoDoc = incidentContacts.getIncidentOrderContactInfo(env, incidentKey);
		
				// Appending the output of NWCGGetIncidentContactsListService to the input Doc
				if(contactInfoDoc.getChildNodes() != null){
					opDoc = incidentOrderElement.appendChild(ipDoc.importNode(contactInfoDoc.getDocumentElement(),true)).getOwnerDocument();
				}
		
			    // Get ROSS Account Codes and append to incident order
			    rossAcctCodesDoc = rossAcctCodes.getROSSAccountCodesDoc(env, incidentKey);
			    if (rossAcctCodesDoc != null){
			    	opDoc = incidentOrderElement.appendChild(ipDoc.importNode(rossAcctCodesDoc.getDocumentElement(), true)).getOwnerDocument();
			    }
				logger.verbose("Incident Doc for NWCGIncidentOrder:"+XMLUtil.extractStringFromDocument(opDoc));
		    }
		}catch(Exception e){
			logger.error("!!!!! Caught General Exception :: " + e);
		    
		}

		// Sunjay
		// This will be used as part of incident issue order entry. If the incident is not present, then the codes
		// makes a call to ROSS to get the incident details. After that, code will insert it into DB and then makes
		// another call to get it back from DB as part of this service. Incident issue entry jsp code assumes that
		// ROSS Account code is present at root level of the incident, so we are updating the account codes at the
		// root level.
		if (rossAcctCodesDoc != null){
			updtROSSAcctCodeAtRootLevel(opDoc, rossAcctCodesDoc);
		}
		return opDoc;		
	}
	
	
	/*
	 * This method deletes all the information regarding ROSS Account Codes and Contact Information
	 * based on incident key
	 */
	public Document deleteMiscDetails(YFSEnvironment env, Document ipDoc) throws Exception{
		logger.verbose("Input Document : " + XMLUtil.extractStringFromDocument(ipDoc));
		String incidentKey = ipDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_KEY);
		logger.verbose("Incident Key: " + incidentKey);
		logger.verbose("Deleting ROSS Account Codes... ");
		NWCGIncidentROSSAccountCodes rossAcctCodes = new NWCGIncidentROSSAccountCodes();
		rossAcctCodes.deleteROSSAccountCodes(env, incidentKey);
		logger.verbose("Deleting Incident Contact Info... ");
		NWCGIncidentContacts incidentContacts = new NWCGIncidentContacts();
		incidentContacts.deleteIncidentOrderContactInfo(env, incidentKey);
		return ipDoc;
	}	

	/**
	 * This method will call individual update methods of ROSSAccountCodes and
	 * IncidentContacts. As part of update, these classes will internally call delete and create
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document updateMiscDetails(YFSEnvironment env, Document ipDoc) throws Exception {
		NWCGIncidentContacts incidentContacts = new NWCGIncidentContacts();
		incidentContacts.updateIncidentContacts(env, ipDoc);
		
		NWCGIncidentROSSAccountCodes rossAcctCodes = new NWCGIncidentROSSAccountCodes();
		rossAcctCodes.updateROSSAccountCodes(env, ipDoc);
		return ipDoc;
	}

	
	/**
	 * This method will get the primary financial code of the incident and will set it at the
	 * root level of incident details. If there is no primary incident details with indicator
	 * of true, then it will not set anything at all
	 * @param opDoc
	 * @param rossAcctCodesDoc
	 */
	private void updtROSSAcctCodeAtRootLevel(Document opDoc, Document rossAcctCodesDoc){
		Element elmROSSAcctCodesRoot = rossAcctCodesDoc.getDocumentElement();
		if (elmROSSAcctCodesRoot == null){
			return;
		}
		
		NodeList nlROSSAcctCodes = elmROSSAcctCodesRoot.getElementsByTagName("NWCGRossAccountCodes");
		String financialCode = "";
		String owningAgency = "";
		String fiscalYear = "";
		boolean obtPrimInd = false;
		
		if (nlROSSAcctCodes != null){
			for (int i=0; i < nlROSSAcctCodes.getLength() && !obtPrimInd; i++){
				Element elmROSSAcctCodes = (Element) nlROSSAcctCodes.item(i);
				if (elmROSSAcctCodes != null){
					financialCode = elmROSSAcctCodes.getAttribute("FinancialCode");
					owningAgency = elmROSSAcctCodes.getAttribute("OwningAgencyName");
					fiscalYear = elmROSSAcctCodes.getAttribute("FiscalYear");
					String primaryInd = elmROSSAcctCodes.getAttribute("PrimaryIndicator");
					if (primaryInd.equalsIgnoreCase("TRUE")){
						obtPrimInd = true;
					}
				}
				else {
					logger.debug("NWCGROSSAccountCodes::getROSSAcctCodeInfoForUpdate, ROSS Acct Codes element is null");
				}
			}
		}
		else {
			logger.debug("NWCGROSSAccountCodes::getROSSAcctCodeInfoForUpdate, NWCGIncidentROSSAccountCodesList is emtpy");
		}
		
		if (obtPrimInd){
			Element elmIncRoot = opDoc.getDocumentElement();
			elmIncRoot.setAttribute("FinancialCode", financialCode);
			elmIncRoot.setAttribute("OwningAgency", owningAgency);
			elmIncRoot.setAttribute("FiscalYear", fiscalYear);
		}
		return;
	}
	
}