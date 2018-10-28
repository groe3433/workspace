package com.nwcg.icbs.yantra.api.issue;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.issue.util.NWCGCloseAlert;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be called on click of 'Save' in 'Issue Details' screen. This class will get
 * the UTF lines from input and if it is ROSS originated issue, then this class will send a
 * full UTF message to ROSS for all the lines.
 * @author sgunda
 *
 */
public class NWCGCheckUTFLines implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGCheckUTFLines.class.getName());
	private String orderNo;
	private String orderHdrKey;
	private String incNo;
	private String incYr;
	private String shippingContactName;
	private String shippingContactPhone;
	private Element elmOrderExtn;
	private Element elmPersonInfoShipTo;
	private String shipNode;
	private String orderCreateTS;
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	
	public Document checkUTFAndSendMsgToROSS(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("NWCGCheckUTFLines::checkUTFAndSendMsgToROSS, Entered");
		logger.verbose("NWCGCheckUTFLines::checkUTFAndSendMsgToROSS, Input XML : " + XMLUtil.extractStringFromDocument(docIP));
		String orderHdrKey = "";
		int noOfUTFLines = 0;
		//Document docOrderDtls = null;
		// 1. Check if issue's System of origin is ROSS
		// 2. If it is not ROSS, return
		// 3. If all the lines Extn element contain OrigReqIssueQty equals UTFQty and ConditionVariable2 does not 
		//	  contain XLD
		// 4a.1. Send UTF fill message for each line
		// 4a.2. Close the PlaceNFESResourceRequest alert (if all the lines in the issue are in UTF status)
		// 4b. Return, don't do anything
		Element elmRootUTF = docIP.getDocumentElement();
		Element elmExtnOrder = (Element) elmRootUTF.getElementsByTagName("Extn").item(0);
		Element elmOLs = null;
		String sysOfOrigin = elmExtnOrder.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
		if (sysOfOrigin != null && sysOfOrigin.equals(NWCGConstants.ROSS_SYSTEM)){
			orderHdrKey = elmRootUTF.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

			NodeList nlOrderChilds = elmRootUTF.getChildNodes();
			boolean obtOLs = false;
			for (int childs=0; childs < nlOrderChilds.getLength() && !obtOLs; childs++){
				Node tmpNode = nlOrderChilds.item(childs);
				if ((tmpNode.getNodeType() == Node.ELEMENT_NODE)) {
					if(tmpNode.getNodeName().equals(NWCGConstants.ORDER_LINES)) {
						elmOLs = (Element) tmpNode;
						
						NodeList nlOrderLine = elmOLs.getElementsByTagName(NWCGConstants.ORDER_LINE);
						if (nlOrderLine != null && nlOrderLine.getLength() > 0){
							setOrderAttributes(elmRootUTF);
							noOfUTFLines = nlOrderLine.getLength();
							for (int i=0; i < noOfUTFLines; i++){
								Element elmOrderLine = (Element) nlOrderLine.item(i);
								if (isFullUTFLine(env, elmOrderLine)){
									sendMsgToROSS(env, elmOrderLine);
								}
							}
						}
						else {
							logger.verbose("NWCGCheckUTFLines::checkUTFAndSendMsgToROSS, This " +
									"should not happen as we should have some lines on the issue");
						}
						obtOLs = true;
					}
				} // end of if (node is of ELEMENT_NODE type)
			} // end of for loop of getChildNodes


		}
		else {
			logger.verbose("NWCGCheckUTFLines::checkUTFAndSendMsgToROSS, This is an ICBSR initiated issue, " +
								"so we are not sending any message to ROSS");
		}
		
		if (noOfUTFLines > 0){
			String noOfOLs = elmOLs.getAttribute(NWCGConstants.TOTAL_NUMBER_OF_RECORDS);
			if (noOfOLs.indexOf(".") != -1){
				noOfOLs = noOfOLs.substring(0, noOfOLs.indexOf("."));
			}
			int totalOLs = new Integer(noOfOLs).intValue();
			if (totalOLs == noOfUTFLines){
				// All the lines in this issue has been UTFed. ICBSR System is closing the alert automatically
				String closeReason = "Automatically resolved by ICBSR. All the lines on this issue are in UTF status";
				NWCGCloseAlert.closeAlert(env, orderHdrKey, "PlaceResourceRequestExternalReq", closeReason);
			}
		}
		return docIP;
	}
	

	/**
	 * This method will formulate the XML needed as per fill message and will
	 * send it to ROSS
	 * @param env
	 * @param elmOL
	 */
	private void sendMsgToROSS(YFSEnvironment env, Element elmOL){
		logger.verbose("NWCGCheckUTFLines::sendMsgToROSS, Entered");

		String reqNo = ((Element)elmOL.getElementsByTagName(
				NWCGConstants.EXTN).item(0)).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		
		NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
		nfesResReq.setDocAttributes(NWCGAAConstants.ENV_USER_ID, orderHdrKey, "Order", orderNo, null);
		nfesResReq.setMessageOriginator(shipNode);
		nfesResReq.setRequestKey(incNo, incYr, reqNo);
		elmOL.setAttribute("OrderNo", orderNo);
		if (orderCreateTS == null){
			orderCreateTS = "";
		}
		elmOL.setAttribute("OrderCreatets", orderCreateTS);
		nfesResReq.populateFillDtlOrConsolidationDtl(elmOL, null, null);
		nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
		nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
		nfesResReq.populateSpecialNeeds();
		
		try {
			logger.error("NWCGCheckUTFLines::sendMsgToROSS, Input XML: " +
						XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, 
					XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
		}
		catch (Exception ex){
			logger.error("NWCGCheckUTFLines::sendMsgToROSS, " +
							"Exception for Req No " + reqNo + " : " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method sets order related attributes to class variables
	 * @param elmOrder
	 */
	private void setOrderAttributes(Element elmOrderDtls){
		orderNo = elmOrderDtls.getAttribute(NWCGConstants.ORDER_NO);
		orderHdrKey = elmOrderDtls.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		shipNode = elmOrderDtls.getAttribute(NWCGConstants.SHIP_NODE);
		orderCreateTS = elmOrderDtls.getAttribute("Createts");
		NodeList nlOrderChildNodes = elmOrderDtls.getChildNodes();
		if (nlOrderChildNodes != null && nlOrderChildNodes.getLength() > 0){
			for (int i=0; i < nlOrderChildNodes.getLength(); i++){
				Node tmpNode = nlOrderChildNodes.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elmIsExtn = (Element) tmpNode;
					if (elmIsExtn.getNodeName().equals("Extn")){
						incNo = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_NO);
						incYr = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_YEAR);
						shippingContactName = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR);
						shippingContactPhone = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR);
						elmOrderExtn = elmIsExtn;
					}
					else if (elmIsExtn.getNodeName().equals("PersonInfoShipTo")){
						elmPersonInfoShipTo = elmIsExtn;
					}
				}
			} // end of for(nlOrderChildNodes)
		} // end of if(nlOrderChildNodes != null)
	}
	
	/**
	 * DETERMING WHETHER THE LINE IS CREATED DUE TO PARTIAL SUBSTITUTION
	 * - Check if the request no has a dot in it (S-1.2). If not, it is
	 * a UTF by a manual user action
	 * - If there is a dot, then make getOrderLineList as below
	 * Input Request No = S-1 (Remove the last dot. If it is S-1.2.1, then
	 * the input request will be S-1.2)
	 * Input XML
	 *  <OrderLine OrderHeaderKey="201004141410074018626">
			<Extn ExtnRequestNo="S-1"/> // Request No = Remove the last dot and number
			<Item ItemID="Item ID of the current line"/>
		</OrderLine>
		If it returns any entry, then it means that the current line is
		created as part of partial substitution. Do not send this request to ROSS
	 * @param env
	 * @param reqNo
	 * @return
	 */
	private boolean isOLPartiallySubstituted(YFSEnvironment env, String reqNo, String itemID){
		boolean isPartiallySubstituted = false;
		if (reqNo.indexOf(".") == -1){
			isPartiallySubstituted = false;
			return isPartiallySubstituted;
		}
		
		try {
			Document docGetOLListIP = XMLUtil.createDocument("OrderLine");
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			elmGetOL.setAttribute("OrderHeaderKey", orderHdrKey);
			Element elmOLExtn = docGetOLListIP.createElement("Extn");
			String targetReqNo = reqNo.substring(0, reqNo.lastIndexOf("."));
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNo", targetReqNo);

			Element elmItem = docGetOLListIP.createElement("Item");
			elmGetOL.appendChild(elmItem);
			elmItem.setAttribute("ItemID", itemID);
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, 
											"NWCGEnhancedOrderMonitor_getOrderLineList", 
											"getOrderLineList", docGetOLListIP);
			
			String matchingLines = docGetOLListOP.getDocumentElement().getAttribute("TotalLineList");
			if (matchingLines == null || matchingLines.trim().length() < 1){
				isPartiallySubstituted = false;
			}
			else {
				int noOfLines = new Integer(matchingLines).intValue();
				if (noOfLines > 0){
					isPartiallySubstituted = true;
				}
				else {
					isPartiallySubstituted = false;
				}
			}
		}
		catch(ParserConfigurationException pce){
			logger.error("NWCGNWCGCheckUTFLines::isOLPartiallySubstituted, " +
					"Parser Configuration Exception : " + pce.getMessage());
			pce.printStackTrace();
		}
		catch(Exception e){
			logger.error("NWCGNWCGCheckUTFLines::isOLPartiallySubstituted, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
		return isPartiallySubstituted;
	}
	
	/**
	 * This method will check if it is a Full UTF line or not
	 * - Requested Quantity = UTF Quantity
	 * - ConditionVariable2 is NULL or not equal to subs or cons or retrived
	 * - Not partially substituted line
	 * @param env
	 * @param elmOrderLine
	 * @return
	 */
	private boolean isFullUTFLine(YFSEnvironment env, Element elmOrderLine){
		boolean isFullUTF = false;
		Element elmExtnOL = (Element) elmOrderLine.getElementsByTagName(NWCGConstants.EXTN).item(0);
		
		String origReqQty = elmExtnOL.getAttribute("ExtnOrigReqQty");
		String utfQty = elmExtnOL.getAttribute("ExtnUTFQty");
		if (origReqQty == null || origReqQty.trim().length() < 1){
			origReqQty = "0";
		}
		if (utfQty == null || utfQty.trim().length() < 1){
			utfQty = "0";
		}
		float fltOrigReqQty = new Float(origReqQty).floatValue();
		float fltUTFQty = new Float(utfQty).floatValue();
		if (fltOrigReqQty == fltUTFQty){
			String condVar2 = elmOrderLine.getAttribute("ConditionVariable2");
			if (!(condVar2 != null && condVar2.contains("XLD"))){
				isFullUTF = true;
				String reqNo = elmExtnOL.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				Element elmOLItem = (Element) elmOrderLine.getElementsByTagName(NWCGConstants.ITEM).item(0); 
				String itemID = elmOLItem.getAttribute("ItemID");
				isFullUTF = !isOLPartiallySubstituted(env, reqNo, itemID);
			}
		}
		logger.info("NWCGCheckUTFLines::isFullUTFLine, Is line Full UTF? " + isFullUTF);
		return isFullUTF;
	}
/*	private void changeIssueStatusToUTF(YFSEnvironment env, String orderHdrKey){
		Document docChgOrder
	}
*/	
}
