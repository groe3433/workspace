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

import java.util.Hashtable;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sdas
 *
 */
public class NWCGIncidentROSSAccountCodes implements YIFCustomApi {

	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger();
	private String primIndFinCodeDB = "";
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
    
    public Document getROSSAccountCodesList(YFSEnvironment environment,Document document) throws Exception{
        String incidentKey = NWCGConstants.EMPTY_STRING;
        logger.verbose("inside getROSSAccountCodesList method!!");
        getROSSAccountCodesDoc(environment,incidentKey);
        return document;
    }
    
    public Document createROSSAccountCodes(YFSEnvironment environment, Document document) throws Exception{
        logger.beginTimer("createROSSAccountCodes");
        
        Document opDoc = null;
        Document inDoc = null;
        
        try{
        	logger.verbose("Inside createROSSAccountCodes method. Input: ");
        	logger.verbose(XMLUtil.extractStringFromDocument(document));
            
	        XPathWrapper xpWrap = new XPathWrapper(document);
	        NodeList nodeList = xpWrap.getNodeList("/MergedDocument/EnvironmentDocument/NWCGIncidentOrder/NWCGRossAccountCodesList/NWCGRossAccountCodes");
	        for(int c=0;c<nodeList.getLength();c++){
	            Element element = (Element)nodeList.item(c);
	            String incidentKey = xpWrap.getAttribute("/MergedDocument/InputDocument/NWCGIncidentOrder/@IncidentKey");
	            
	            inDoc = XMLUtil.createDocument("NWCGRossAccountCodes");
	            Element inDocRootElm = inDoc.getDocumentElement();
	            inDocRootElm.setAttribute("FinancialCode",element.getAttribute("FinancialCode"));
	            inDocRootElm.setAttribute("FiscalYear",element.getAttribute("FiscalYear"));
	            inDocRootElm.setAttribute("OwningAgencyName",element.getAttribute("OwningAgencyName"));
	            inDocRootElm.setAttribute(NWCGConstants.PRIMARY_INDICATOR,element.getAttribute(NWCGConstants.PRIMARY_INDICATOR));
	            inDocRootElm.setAttribute("IncidentOrderKey",incidentKey);
	            
	            logger.verbose("NWCGRossAccountCodes ipDoc:"+XMLUtil.extractStringFromDocument(inDoc));	            
	            opDoc = CommonUtilities.invokeService(environment,NWCGConstants.NWCG_CREATE_ROSS_ACCOUNT_CODES_SERVICE,inDoc);	            
	            logger.verbose("NWCGRossAccountCodes opDoc:"+XMLUtil.extractStringFromDocument(opDoc));
	        }
	      
        }catch(Exception e){
            logger.error("Exception raised in createROSSAccountCodes.Raising alert ");
            CommonUtilities.raiseAlert(environment,NWCGAAConstants.QUEUEID_INCIDENT_FAILURE,e.toString(),document,e,null);
        }
        
        XPathWrapper wrapper = new XPathWrapper(document);
        Element element = (Element)wrapper.getNode("/MergedDocument/InputDocument/NWCGIncidentOrder");
        Document elemDoc = XMLUtil.getDocumentForElement(element);
        
        logger.verbose("elemDoc :"+XMLUtil.extractStringFromDocument(elemDoc)); 
        logger.endTimer("createROSSAccountCodes");
        return elemDoc;
    }
    
    /**
     * @param environment
     * @param incidentKey
     */
    public Document getROSSAccountCodesDoc(YFSEnvironment environment, String incidentKey) throws Exception {
        Document ipDoc = XMLUtil.getDocument("<NWCGRossAccountCodes IncidentOrderKey=\""+incidentKey+"\" />"); 
        Document opDoc = CommonUtilities.invokeService(environment,NWCGConstants.NWCG_GET_ROSS_ACCOUNT_CODES_LIST_SERVICE,ipDoc);
        return opDoc;
    }

    /**
     * This method deletes the ROSS Account Codes for a given incident key
     */
    public void deleteROSSAccountCodes(YFSEnvironment env, String incidentKey) throws Exception {
    	Document rossAcctCodesListDoc = getROSSAccountCodesDoc(env, incidentKey);
    	if (rossAcctCodesListDoc != null){
    		Element rossAcctCodesListElm = rossAcctCodesListDoc.getDocumentElement();
    		NodeList rossAcctCodesListNL = rossAcctCodesListElm.getElementsByTagName("NWCGRossAccountCodes");
    		if (rossAcctCodesListNL != null){
    			for (int i=0; i < rossAcctCodesListNL.getLength(); i++){
    				Element rossAcctCodesElm = (Element) rossAcctCodesListNL.item(i);
    				if (rossAcctCodesElm != null){
    					String rossAcctCodeKey = rossAcctCodesElm.getAttribute("ROSSAccountCodeKey");
    					if (rossAcctCodeKey != null && rossAcctCodeKey.length() > 0){
    				    	Document deleteRossAcctCodesDoc = XMLUtil.getDocument(
    				    							"<NWCGRossAccountCodes ROSSAccountCodeKey=\"" + rossAcctCodeKey + "\" />");
    				    	CommonUtilities.invokeService(env, NWCGConstants.NWCG_DELETE_ROSS_ACCOUNT_CODES_SERVICE, deleteRossAcctCodesDoc);
    					}
    					else {
    						logger.debug("ROSS Account Code Key is null");
    					}
    				}
    				else {
    					logger.debug("ROSS Acct Codes element is null");
    				}
    			}
    		}
    		else {
    			logger.debug("NWCGIncidentROSSAccountCodesList is emtpy");
    		}
    	}
    	else {
    		logger.debug("There are no NWCGIncidentROSSAccountCode elements for incident key : " + incidentKey);
    	}
    }
 
    /**
     * This method is used to update the ROSS Account Codes as part of getIncidentDetails +
     * update incident details request from ROSS.
     * This method is going to delete the existing ROSS Account Codes and will
     * create new ROSS Account Codes based on the input xml
     * 
     * @param env
     * @param ipDoc
     * @return
     * @throws Exception
     */
    public Document updateROSSAccountCodes(YFSEnvironment env, Document ipDoc) throws Exception {
    	logger.debug("Input Doc : " + XMLUtil.extractStringFromDocument(ipDoc));
    	Element nwcgIncidentOrderElm = ipDoc.getDocumentElement();
    	String incidentKey = nwcgIncidentOrderElm.getAttribute(NWCGConstants.INCIDENT_KEY);
    	//deleteROSSAccountCodes(env, incidentKey);
    	System.out.println("updateROSSAccountCodes, Primary Financial Code before setting : " + primIndFinCodeDB);
    	Hashtable <String, Hashtable <String, String>> htFinCode2ROSSAcctCodeKey = 
    		getROSSAcctCodeInfoForUpdate(env, incidentKey);
    	System.out.println("updateROSSAccountCodes, Primary Financial Code after setting : " + primIndFinCodeDB);
    	
    	boolean processedOldFinCode = false;
    	// Insert new ROSS Account Codes from the input document
    	NodeList rossAcctCodesNL = nwcgIncidentOrderElm.getElementsByTagName("NWCGRossAccountCodes");
    	if (rossAcctCodesNL.getLength() > 0){
    		for (int i=0; i < rossAcctCodesNL.getLength(); i++){
    			Node rossAcctCode = rossAcctCodesNL.item(i);
    			NamedNodeMap rossAcctCodeAttrs = rossAcctCode.getAttributes();
	            Document rossAcctCodeDoc = XMLUtil.createDocument("NWCGRossAccountCodes");
	            Element rossAcctCodeElm = rossAcctCodeDoc.getDocumentElement();
    			for (int attr=0; attr < rossAcctCodeAttrs.getLength(); attr++){
    				Node attrNameVal = rossAcctCodeAttrs.item(attr);
    				rossAcctCodeElm.setAttribute(attrNameVal.getNodeName(), attrNameVal.getNodeValue());
    			}
    			// We don't know whether incidentOrderKey is part of the xml and if it is part of it
    			// we are not getting the incidentkey value at ROSSAccountCodes. So, we are resetting it to IncidentOrderKey 
    			// from input xml
    			rossAcctCodeElm.setAttribute("IncidentOrderKey", incidentKey);
    			
    			// 1. Get the financial code and primary indicator from input xml
    			String currFinCode = rossAcctCodeElm.getAttribute("FinancialCode");
    			String currPrimInd = rossAcctCodeElm.getAttribute("PrimaryIndicator");
    			// 2. Check if this financial code is present in DB
    			//		- If present, delete that entry from DB
    			//			- Is the financial code's primary indicator true in input XML 
    			//				- Is primIndFinCodeDB same as that of current financial code
    			//					- TRUE: Set LastFinancialCode from DB
    			//					- FALSE: Set LastFinancialCode to primIndFinCodeDB
    			//			- Primary Ind is false in input xml - Set LastFinancialCode from DB
    			if (htFinCode2ROSSAcctCodeKey.containsKey(currFinCode)){
    				Hashtable<String, String> htKeyAndLastFinCode = htFinCode2ROSSAcctCodeKey.get(currFinCode);
    				String rossAcctCodeKey = htKeyAndLastFinCode.get("ROSSAccountCodeKey");
    				String lastFinCode = htKeyAndLastFinCode.get("LastFinancialCode");
    				if (lastFinCode == null){
    					lastFinCode = "";
    				}
    				
			    	Document deleteRossAcctCodesDoc = XMLUtil.getDocument(
							"<NWCGRossAccountCodes ROSSAccountCodeKey=\"" + rossAcctCodeKey + "\" />");
			    	CommonUtilities.invokeService(env, NWCGConstants.NWCG_DELETE_ROSS_ACCOUNT_CODES_SERVICE, deleteRossAcctCodesDoc);
    				
			    	if (currPrimInd.equalsIgnoreCase("TRUE")){
			    		if(currFinCode.equals(primIndFinCodeDB)){
			    			rossAcctCodeElm.setAttribute("LastFinancialCode", lastFinCode);
			    		}
			    		else {
			    			rossAcctCodeElm.setAttribute("LastFinancialCode", primIndFinCodeDB);
			    		}
			    	}
			    	else {
		    			rossAcctCodeElm.setAttribute("LastFinancialCode", lastFinCode);
			    	}
			    	
			    	// We will use this flag to reset the old fin code to false
			    	if (currFinCode.equalsIgnoreCase(primIndFinCodeDB) && !processedOldFinCode){
			    		processedOldFinCode = true;
			    	}
    			}
    			else {
        			//		- If not present
        			//			- Is the financial code's primary indicator true in input XML 
        			//				- Set LastFinancialCode to primIndFinCodeDB
			    	if (currPrimInd.equalsIgnoreCase("TRUE")){
		    			rossAcctCodeElm.setAttribute("LastFinancialCode", primIndFinCodeDB);
			    	}
			    	else {
		    			rossAcctCodeElm.setAttribute("LastFinancialCode", "");
			    	}
    			}
    						
    			
                logger.debug("NWCGRossAccountCodes ipDoc:"+XMLUtil.extractStringFromDocument(rossAcctCodeDoc));
                CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_ROSS_ACCOUNT_CODES_SERVICE, rossAcctCodeDoc);
    		}
    	}
    	else {
    		logger.debug("There are no ROSS Account Codes (Element: NWCGRossAccountCodes) for this incident");
    	}

    	// Old primary financial code is not present in input XML and we are not deleting the old acct codes
    	// and adding new ones. For this reason, we need to change the old primary financial codes indicator
    	// to false if it was not processed earlier
    	if (!processedOldFinCode){
			Hashtable<String, String> htKeyAndLastFinCode = htFinCode2ROSSAcctCodeKey.get(primIndFinCodeDB);
			if (htKeyAndLastFinCode != null){
				String rossAcctCodeKey = htKeyAndLastFinCode.get("ROSSAccountCodeKey");
		    	Document updtRossAcctCodesDoc = XMLUtil.getDocument(
						"<NWCGRossAccountCodes ROSSAccountCodeKey=\"" + rossAcctCodeKey + "\" PrimaryIndicator=\"false\" />");
		    	CommonUtilities.invokeService(env, NWCGConstants.NWCG_MODIFY_ROSS_ACCOUNT_CODES_SERVICE, updtRossAcctCodesDoc);
			}
    	}
    	return ipDoc;
    }
    
    private Hashtable <String, Hashtable<String, String>> 
    getROSSAcctCodeInfoForUpdate(YFSEnvironment env, String incidentKey) throws Exception{
    	Hashtable <String, Hashtable<String, String>> htFinCode2PrimaryInd = new Hashtable<String, Hashtable<String, String>>();
    	Document docROSSAcctCodesList = getROSSAccountCodesDoc(env, incidentKey);
    	if (docROSSAcctCodesList != null){
    		Element elmROSSAcctCodesList = docROSSAcctCodesList.getDocumentElement();
    		NodeList nlROSSAcctCodes = elmROSSAcctCodesList.getElementsByTagName("NWCGRossAccountCodes");
    		if (nlROSSAcctCodes != null){
    			for (int i=0; i < nlROSSAcctCodes.getLength(); i++){
    				Element elmROSSAcctCodes = (Element) nlROSSAcctCodes.item(i);
    				if (elmROSSAcctCodes != null){
    					String rossAcctCodeKey = elmROSSAcctCodes.getAttribute("ROSSAccountCodeKey");
    					String financialCode = elmROSSAcctCodes.getAttribute("FinancialCode");
    					String primaryInd = elmROSSAcctCodes.getAttribute("PrimaryIndicator");
    					String lastFinCode = elmROSSAcctCodes.getAttribute("LastFinancialCode");
    					Hashtable<String, String> htTmp = new Hashtable<String, String>();
    					htTmp.put("ROSSAccountCodeKey", rossAcctCodeKey);
    					htTmp.put("LastFinancialCode", lastFinCode);
    					htFinCode2PrimaryInd.put(financialCode, htTmp);
    					if (primaryInd.equalsIgnoreCase("TRUE")){
    						primIndFinCodeDB = financialCode;
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
    	}
    	else {
    		logger.debug("NWCGROSSAccountCodes::getROSSAcctCodeInfoForUpdate, " +
    				"There are no NWCGIncidentROSSAccountCode elements for incident key : " + incidentKey);
    	}
    	System.out.println("getROSSAcctCodeInfoForUpdate, Primary Indicator Financial Code : " + primIndFinCodeDB);
    	return htFinCode2PrimaryInd;
    }
    
}