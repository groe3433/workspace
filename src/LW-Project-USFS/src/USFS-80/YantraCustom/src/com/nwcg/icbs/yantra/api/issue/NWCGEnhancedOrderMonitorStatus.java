package com.nwcg.icbs.yantra.api.issue;

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGEnhancedOrderMonitorStatus implements YIFCustomApi {

	String orderNo;
	String orderHdrKey;
	String incNo;
	String incYr;
	String shippingContactName;
	String shippingContactPhone;
	Hashtable<String, String> htAddrInfo;
	Element elmOrderExtn;
	Element elmPersonInfoShipTo;
	String shipNode;
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public NWCGEnhancedOrderMonitorStatus(){
		orderNo = "";
		orderHdrKey = "";
		incNo = "";
		incYr = "";
		shippingContactName = "";
		shippingContactPhone = "";
		shipNode = "";
		htAddrInfo = new Hashtable<String, String>();
	}

	/**
	 * This method will get the UTF lines and will send UpdateNFESResourceRequestReq message
	 * to ROSS
	 * - Get the line from MonitorConsolidation/Order/OrderStatuses/OrderStatus/OrderLine
	 * - Get the OL details and send update message to ROSS if the line is in UTF due to 
	 * 		- User entered UTF quantity equal to Requested Quantity
	 * 			If this line is in UTF status due to partial substitution, then do not send 
	 * 			this to ROSS as this will be sent as part of 'Confirm Shipment'
	 * 		- Line is in UTF status due to Consolidation (non-surviving line)
	 * 
	 * Input XML: Check the file ORDER_MONITOR_EX.xml under template/monitor/extn
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document processUTFAndNonSurvivingLines(YFSEnvironment env, Document docIP) throws Exception {
		System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, Entered");
		System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, Input Doc : " 
											+ XMLUtil.extractStringFromDocument(docIP));
		Element elmDocIP = docIP.getDocumentElement();
		// There is only one child to the root element, so going with getFirstChild
		Element elmOrder = (Element) elmDocIP.getElementsByTagName("Order").item(0);
		setOrderAttributes(elmOrder);
		System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, Retrieved Order Attributes");
		
		// Get OL elements
		NodeList nlOLs = elmOrder.getElementsByTagName("OrderLine");
		String orderCreateTS = elmOrder.getAttribute("Createts");
		if (nlOLs != null && nlOLs.getLength() > 0){
			System.out.println("processUTFAndNonSurvivingLines, No. of Order Lines : " + nlOLs.getLength()); 
			for (int i=0; i < nlOLs.getLength(); i++){
				Element elmOL = (Element) nlOLs.item(i);
				NodeList nlOrderLineChilds = elmOL.getChildNodes();
				//Get the Request No and Item ID
				String reqNo = "";
				//String itemID = "";
				for (int childs=0; childs < nlOrderLineChilds.getLength(); childs++){
					Node tmpNode = nlOrderLineChilds.item(childs);
					if ((tmpNode.getNodeType() == Node.ELEMENT_NODE)) {
						if(tmpNode.getNodeName().equals("Extn")) {
							reqNo = ((Element)tmpNode).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
						}
/*						else if (tmpNode.getNodeName().equals("Item")){
							itemID = ((Element) tmpNode).getAttribute("ItemID");
						}*/
					} // end of if (node is of ELEMENT_NODE type)
				} // end of for loop of getChildNodes
				
				String olStatus = elmOL.getAttribute(NWCGConstants.MAX_LINE_STATUS);
				System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, " +
									"Order Line Status : " + olStatus);
				//boolean partiallySubstituted = true;
				boolean nonSurvivingLine = false;

/*				if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_UTF)){
					String sysOfOrigin = elmOrderExtn.getAttribute("ExtnSystemOfOrigin");
					if (sysOfOrigin == null || 
						sysOfOrigin.equalsIgnoreCase("ICBSR") || 
						sysOfOrigin.equalsIgnoreCase("ICBS")){
						System.out.println("processUTFAndNonSurvivingLines, " +
								"This is a ICBSR initiated issue UTF, so not doing anything"); 
						return docIP;
					}
					else {
						partiallySubstituted = isOLPartiallySubstituted(env, reqNo, itemID);
					}
				}
				else if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS)){*/
				if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS)){
					nonSurvivingLine = isNonSurvivingConsolidatedLine(env, reqNo);
				}
				else {
					String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					System.out.println("processUTFAndNonSurvivingLines, Order Line : " + 
							olKey + " has unknown status type " + olStatus + 
							", for Enhanced Order Status monitor");
				}
				
				if (nonSurvivingLine){
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
						System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, Input XML: " +
									XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
						CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, 
								XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
					}
					catch (Exception ex){
						System.out.println("NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines, " +
										"Exception for Req No " + reqNo + " : " + ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		}
		return docIP;
	}
	
	/**
	 * This method sets order related attributes to class variables
	 * @param elmOrder
	 */
	private void setOrderAttributes(Element elmOrder){
		orderNo = elmOrder.getAttribute(NWCGConstants.ORDER_NO);
		orderHdrKey = elmOrder.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		shipNode = elmOrder.getAttribute(NWCGConstants.SHIP_NODE);
		System.out.println("NWCGEnhancedOrderMonitorStatus::setOrderAttributes, Order No : " + orderNo);
		NodeList nlOrderChildNodes = elmOrder.getChildNodes();
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
	/*
	private boolean isOLPartiallySubstituted(YFSEnvironment env, String reqNo, String itemID){
		boolean isPartiallySubstituted = false;
		if (reqNo.indexOf(".") == -1){
			System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"Request no doesn't have index of dot, so it is not a partially substituted line");
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
			System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"Input XML : " + XMLUtil.extractStringFromDocument(docGetOLListIP));
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, 
											"NWCGEnhancedOrderMonitor_getOrderLineList", 
											"getOrderLineList", docGetOLListIP);
			
			System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"output XML : " + XMLUtil.extractStringFromDocument(docGetOLListOP));
			String matchingLines = docGetOLListOP.getDocumentElement().getAttribute("TotalLineList");
			if (matchingLines == null || matchingLines.trim().length() < 1){
				System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
						"No matching lines with the order line input. Not partially substituted");
				isPartiallySubstituted = false;
			}
			else {
				int noOfLines = new Integer(matchingLines).intValue();
				System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " + 
									"No. of Lines : " + noOfLines);
				if (noOfLines > 0){
					System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
										"Partially substituted line");
					isPartiallySubstituted = true;
				}
				else {
					System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"NOT Partially substituted line");
					isPartiallySubstituted = false;
				}
			}
		}
		catch(ParserConfigurationException pce){
			System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"Parser Configuration Exception : " + pce.getMessage());
			pce.printStackTrace();
		}
		catch(Exception e){
			System.out.println("NWCGEnhancedOrderMonitorStatus::isOLPartiallySubstituted, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
		
		return isPartiallySubstituted;
	}
	*/
	
	
	/**
	 * Make getOrderLineList with the below input template
	 *  <OrderLine OrderHeaderKey="201004141410074018626">
  			  <Extn ExtnRequestNoQryType="FLIKE" ExtnRequestNo="S-100511"/>
		</OrderLine>
			
		Output template
		<OrderLineList TotalLineList="">
			<OrderLine MaxLineStatus="" OrderLineKey="">
			</OrderLine>
		</OrderLineList>
		If there is more than one entry, then it means that the original line on
		which getOrderLineList is called is a surviving line. Do not send
		UpdateRequest for this line
			
		If there is only one entry, then send Update Request as this is a non surviving line
	 * @param reqNo
	 * @return
	 */
	private boolean isNonSurvivingConsolidatedLine(YFSEnvironment env, String reqNo){
		boolean nonSurvivingLine = false;
		try {
			Document docGetOLListIP = XMLUtil.createDocument("OrderLine");
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			elmGetOL.setAttribute("OrderHeaderKey", orderHdrKey);
			Element elmOLExtn = docGetOLListIP.createElement("Extn");
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNoQryType", "FLIKE");
			elmOLExtn.setAttribute("ExtnRequestNo", reqNo);
			
			System.out.println("NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine, " +
					"Input XML : " + XMLUtil.extractStringFromDocument(docGetOLListIP));
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, 
											"NWCGEnhancedOrderMonitor_getOrderLineList", 
											"getOrderLineList", docGetOLListIP);
			System.out.println("NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine, " +
					"Output XML : " + XMLUtil.extractStringFromDocument(docGetOLListOP));
			
			String matchingLines = docGetOLListOP.getDocumentElement().getAttribute("TotalLineList");
			System.out.println("NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine, " +
								"TotalLineList : " + matchingLines);
			int noOfLines = new Integer(matchingLines).intValue();
			if (noOfLines > 1){
				nonSurvivingLine = false;
			}
			else {
				nonSurvivingLine = true;
			}
		}
		catch(ParserConfigurationException pce){
			System.out.println("NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine, " +
					"Parser Configuration Exception : " + pce.getMessage());
			pce.printStackTrace();
		}
		catch(Exception e){
			System.out.println("NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return nonSurvivingLine;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
