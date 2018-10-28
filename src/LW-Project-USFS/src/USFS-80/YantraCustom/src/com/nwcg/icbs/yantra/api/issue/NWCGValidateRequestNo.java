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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateRequestNo implements YIFCustomApi {

	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger(NWCGValidateRequestNo.class.getName());
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	
	/*
	 * Input: 
	 * <ValidationRequest RequestNo="" IncidentNo="" IncidentYear="" SystemOfOrigin=""/>
	 * Response:
	 * <ValidationResponse Result="Y/N" Message="Faliure message"/>
	 *  
	 */
	public Document validateRequestNo(YFSEnvironment env,Document inDoc) throws Exception {
		logger.verbose("NWCGValidateRequestNo.validateRequestNo START");
		logger.verbose("inDoc: " + XMLUtil.extractStringFromDocument(inDoc));
		
		Document returnDoc = XMLUtil.createDocument("ValidationResponse");
		String requestNo = inDoc.getDocumentElement().getAttribute("RequestNo");
		
		String incidentNo = inDoc.getDocumentElement().getAttribute("IncidentNo");
		String incidentYear = inDoc.getDocumentElement().getAttribute("IncidentYear");
		String sysOfOrigin = inDoc.getDocumentElement().getAttribute("SystemOfOrigin");
		
		// if there is a prefix of "S" or "E" on the request number
		//cr 409 kjs
		if((requestNo.indexOf(NWCGConstants.NFES_SUPPLY_PREFIX_LTR) < 0 ) || (requestNo.indexOf(NWCGConstants.NFES_EQUIP_PREFIX_LTR) < 0 )){
			
			if(requestNo.indexOf(NWCGConstants.DASH) < 0 || (requestNo.length() < 2)) {
				returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);				
				returnDoc.getDocumentElement().setAttribute("Message",ResourceUtil.resolveMsgCode("NWCG_VALIDATE_REQUEST_NO_003"));
				return returnDoc;
			}
		}	
		
		//Get a string that is the Seq # with dots, e.g.g 100600.4.2.6.8
		String numericRequestNo = requestNo.substring(2);
		
		//Get the 2 char length prefix "S-" or "E-"
		String sNo = requestNo.substring(0,2);
		
		//Split the numeric part of the request into an array of its components.
		String [] sNostr = numericRequestNo.split("\\.");
		
		// Base Request Number without any of the dots
		StringBuffer sb = new StringBuffer(sNo);
		int idxFirstDot = numericRequestNo.indexOf('.',0);
		if (idxFirstDot == -1)
			sb.append(numericRequestNo);
		else {
			String baseNumber =numericRequestNo.substring(0, idxFirstDot);  
			sb.append(baseNumber);
		}
	
		if(!sNo.equals(NWCGConstants.NFES_SUPPLY_FULL_PFX) || !sNo.equals(NWCGConstants.NFES_EQUIP_FULL_PFX)) {
			if(requestNo.indexOf(NWCGConstants.DASH) < 0) {
				returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);				
				returnDoc.getDocumentElement().setAttribute("Message",ResourceUtil.resolveMsgCode("NWCG_VALIDATE_REQUEST_NO_003"));
				return returnDoc;
			}
		}
		 
		//Check to see that every integer in between the dots of
		//the sequence portion of the request number is numeric
		for(int j=0; j< sNostr.length; j++)
		{
			try {
			    Integer.parseInt(sNostr[j]);
			}
			catch(NumberFormatException nFE) {
			    logger.verbose("Not an Integer");
			    returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);			
				returnDoc.getDocumentElement().setAttribute("Message",ResourceUtil.resolveMsgCode("NWCG_VALIDATE_REQUEST_NO_003"));
				return returnDoc;
			}
		}	
		
		// Check to see if the request number already exists on the same Incident/Year
		// if there is an orderline existing with the given request no send a validation failure		
		boolean olExists = CommonUtilities.checkOrderLineExistsForRequestNo(env,requestNo,incidentNo,incidentYear);
		if(olExists){
			logger.verbose(ResourceUtil.resolveMsgCode("NWCG_VALIDATE_REQUEST_NO_001"));
			returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);			
			returnDoc.getDocumentElement().setAttribute("Message",ResourceUtil.resolveMsgCode("NWCG_VALIDATE_REQUEST_NO_001"));
			return returnDoc;
		}	

		// Lastly, check to see if the  
		// OH.EXTN_SYSTEM_Of_ORIGIN = ROSS  or R if so, then we need to see if the 
		// request number is subordinate of one of the existing lines on the issue.
		// e.g.: Input S-10004.2 is a subordinate of S-100004, yes, 
		// but not S-10005.3 of S-10006.1
		boolean isIssueFromROSS = false;
		if (!StringUtil.isEmpty(sysOfOrigin) && 
				(sysOfOrigin.equalsIgnoreCase(NWCGConstants.ROSS_SYSTEM) || sysOfOrigin.equalsIgnoreCase("R"))) {
			isIssueFromROSS = true;
		}
		logger.debug("NWCGValidateRequestNo, Is This Issue From ROSS? "+isIssueFromROSS);
		
		if (!isIssueFromROSS) 
			return returnDoc;
		
		logger.debug("This is for an Issue created by ROSS.");
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.getOLsForABaseReqInAnInc(
					env, incidentNo, incidentYear, sb.toString(),
					NWCGConstants.GET_ORDER_LINE_LIST_OP_TEMPLATE);
		} catch (Exception e) {
			logger.error("ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);
			returnDoc.getDocumentElement().setAttribute("Message", "ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			return returnDoc;
		}
		
		// Check if the API returned null
		if (apiOutputDoc == null) {
			logger.error("ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);
			returnDoc.getDocumentElement().setAttribute("Message", "ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			return returnDoc;
		}

		Element docElm = apiOutputDoc.getDocumentElement();
		NodeList orderLineNodeList = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		// If no lines are returned for the given Incident No/Year by
		// getOrderLineList, MCF will not return TotalLineList attribute
		if (orderLineNodeList == null || orderLineNodeList.getLength() < 1){
			logger.error("ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);
			returnDoc.getDocumentElement().setAttribute("Message", "ICBS was unable to validate the given request number " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear+". Only subordinate request issue lines may" +
					" be added to ROSS-initiated Issues in Draft or Created status.");
			return returnDoc;
		}		
		
		returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);
		returnDoc.getDocumentElement().setAttribute("Message", "This request number's parent not found on this ROSS initiated Issue!");
		
		// This integer represent the total # of OrderLine elements found in the
		// output XML of the getOrderLineList API call made by the CommonUtilities
		// method getOLsForABaseReqInAnInc()
		int numberOfOrderLineNodes = orderLineNodeList.getLength();	
		// Iterate over all of the "OrderLine" Nodes 
		for (int i = 0; i < numberOfOrderLineNodes; i++) 
		{
			Object o = orderLineNodeList.item(i);
			Element currentOrderLineElement = (o instanceof Element) ? (Element) o : null;
			if (currentOrderLineElement == null) continue;
			
			if (currentOrderLineElement.getNodeName().equals(NWCGConstants.ORDER_LINE) ||
			    currentOrderLineElement.getLocalName().equals(NWCGConstants.ORDER_LINE)) 
			{							
				 // Get OrderLine/Extn element, which is the firstChild of
				 // currentOrderLineElement
				Node firstChildOfOrderLine = currentOrderLineElement.getFirstChild();
				Element firstChildElement = (firstChildOfOrderLine instanceof Element) ? (Element) firstChildOfOrderLine : null;
				if (firstChildElement == null) continue;
				
				if (firstChildElement.getNodeName().equals(NWCGConstants.EXTN_ELEMENT) ||
					firstChildElement.getLocalName().equals(NWCGConstants.EXTN_ELEMENT)) 
				{
					Element orderLineExtnElm = firstChildElement;
					String currOLExtnRequestNo = orderLineExtnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
					
					if (StringUtil.isEmpty(currOLExtnRequestNo)) {
						logger.error("OrderLine/Extn/@ExtnRequestNo attribute not found on getOrderLineList OrderLine");
						returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.NO);
						returnDoc.getDocumentElement().setAttribute("Message", "OrderLine/Extn/@ExtnRequestNo attribute not found on getOrderLineList OrderLine");
						return returnDoc;
					}
				
					// if a derived line is found, allow the UI input
					if (requestNo.startsWith(currOLExtnRequestNo) && !requestNo.equalsIgnoreCase(currOLExtnRequestNo)) {
						returnDoc.getDocumentElement().setAttribute("Result", NWCGConstants.YES);
						returnDoc.getDocumentElement().setAttribute("Message", "This request number is a child of an existing request number for this Issue");
						break;
					}// if derived line found
				}// if Order/Extn element found
			}// if Order element found
		}// for loop over OrderLine elements
		
		logger.verbose("NWCGValidateRequestNo.validateRequestNo END");		
		return returnDoc;
	}		
}