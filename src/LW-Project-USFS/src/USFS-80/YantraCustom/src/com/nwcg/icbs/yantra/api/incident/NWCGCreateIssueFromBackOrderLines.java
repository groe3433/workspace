package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author gacharya, drodriguez
 * @since Business Release 1 (BR2) Increment 1
 */
public class NWCGCreateIssueFromBackOrderLines implements YIFCustomApi {
	/**
	 * Sample Input XML
	 * 	<OrderLineList IgnoreOrdering="Y">
	 * 		<OrderLine IncidentNo="WY-SHF-000500" IncidentYear="2010" NewIssueQty="2" OrderLineKey="201005211137354312946" YFC_NODE_NUMBER="1"/>
	 * 		<OrderLine IncidentNo="WY-SHF-000500" IncidentYear="2010" NewIssueQty="3" OrderLineKey="201005221640094314606" YFC_NODE_NUMBER="3"/>
	 * 	</OrderLineList>   
	 * 
	 * @param env 
	 * @param inDoc The OrderLineList input org.w3c.dom.Document with the backorder qty for which the issue needs to be created
	 * @return Document The createOrder output after the issue has been created 
	 * @throws Exception
	 */
	public Document createIssuesFromBackOrderLines(YFSEnvironment env, Document inDoc) throws Exception
	{
		if(logger.isVerboseEnabled())
		 	logger.verbose("Input XML createIssuesFromBackOrderLines()-> " + 
		 						XMLUtil.extractStringFromDocument(inDoc));
		 
		// The changeOrder API Input XML document
		 Document changeOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		 
		 // The createOrder API Input XML document
		 Document createOrderInput = null;
		 
		 // Document returned by the API
		 Document returnDoc = null;
		 
		 Element inputDocElm = inDoc.getDocumentElement();
		 if (inputDocElm == null) {
				NWCGException e = new NWCGException("NWCG_ISSUE_BACKORDER_000");
				throw e;
		 }
		 
		 /* Validate the inDoc's Order/OrderLines/OrderLine elements before continuing
		  * with creationg of the back order, in case we find that the input is not for
		  * any reason. Then the API will throw an exception.
		  */ 
		 validateLines(env, inDoc);
		 
		 /* 
		  * Loop over the Order/OrderLines/OrderLine Elements in the NodeList from the inDoc
		  * containing a Node for all the order line(s) in the input that need to be 
		  * added to a BO order.
		  */		 
		 NodeList inputDocOrderLineList = inputDocElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		 for (int i = 0; i < inputDocOrderLineList.getLength(); i++) {
			 Element currOrderLineFromUI = (Element) inputDocOrderLineList.item(i);
			 
			 //Get the current input order line's OLK
			 String currOLKey = currOrderLineFromUI.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			 
			 //Get the output of getOrderLineDetails API
			 Document getOrderLineDtlsOpDoc  = getOrderLineDetails(env, currOLKey);
			 
			 if(logger.isVerboseEnabled()) {
				 logger.verbose("getOrderLineDetails OUTPUT XML: " + 
						 XMLUtil.extractStringFromDocument(getOrderLineDtlsOpDoc));
			 }
			 
			 //Set an Element reference to getOrderLineDetails output document element
			 Element getOrderLineDtlsDocElm = getOrderLineDtlsOpDoc.getDocumentElement();
			 
			 //Get the current order line's OHK
			 String currInputOrderHeaderKey = getOrderLineDtlsDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			 
			 //Create the order header using the details from the first line as the template
			 if(i == 0){
				 TempOrderHeaderKey = currInputOrderHeaderKey;
				 
				 Element changeOrderDocElm = changeOrderInput.getDocumentElement();
				 changeOrderDocElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
				 changeOrderDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, currInputOrderHeaderKey);
				 
				 Element orderLines = changeOrderInput.createElement(NWCGConstants.ORDER_LINES);
				 changeOrderDocElm.appendChild(orderLines);
				 createOrderInput = createOrderHeader(env, getOrderLineDtlsDocElm);
			}
			 
			 //Add the line to the createOrder/createOrderHeader Doc API Input XML
			 addLineToIssue(createOrderInput, currOrderLineFromUI, getOrderLineDtlsDocElm, i+1);
			 changeOrderInput = changeBackOrderStatusInOriginalIssue(env, currOrderLineFromUI, getOrderLineDtlsDocElm, changeOrderInput);
		 }
		 
		 if(logger.isVerboseEnabled() || icbsDebug.booleanValue()) {
		 	 logger.verbose("+++++++++++++ changeOrder API Input XML ->+++++++++++++");
		 	 logger.verbose("Change Order API Input XML:" + XMLUtil.extractStringFromDocument(changeOrderInput));
		 }
			 //System.out.println("+++++++++++++ changeOrder API Input XML ->+++++++++++++");
		 	 //System.out.println("Change Order API Input XML:" + XMLUtil.extractStringFromDocument(changeOrderInput));			 
		 
		 
		 // Call changeOrder
		 returnDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInput);
		 
		 
			 String rDStr = XMLUtil.extractStringFromDocument(returnDoc);
			 //System.out.println("+++++++++++++ changeOrder API Output XML ->+++++++++++++ " + rDStr);
			 
		 
		 //Create the Issue
		 Document createOrderTemplate = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		 createOrderTemplate.getDocumentElement().setAttribute(NWCGConstants.ORDER_HEADER_KEY, "");
		 
		 if(logger.isVerboseEnabled() || icbsDebug.booleanValue()) {
			 logger.verbose("createIssuesFromBackOrderLines()->Create Order API Input XML -> " + XMLUtil.extractStringFromDocument(createOrderInput));
		 }
			 //System.out.println("createIssuesFromBackOrderLines()->Create Order API Input XML -> " + XMLUtil.extractStringFromDocument(createOrderInput));
		 
		 
		 returnDoc = CommonUtilities.invokeAPI(env, createOrderTemplate, NWCGConstants.API_CREATE_ORDER, createOrderInput);
		 String returnDocStr = XMLUtil.extractStringFromDocument(returnDoc);
		 
		 if(logger.isVerboseEnabled()) {
			 logger.verbose("Returned from createOrder: ");
			 logger.verbose(returnDocStr);
		 }
		 return returnDoc;
	}
	
	/**
	 * This method validates the input from the JSP if the selected lines belong
	 * to different order/incidents then we throw and exception if the issue
	 * quantity for the new lines is not entered or not a number we throw and
	 * exception
	 */
	private void validateLines(YFSEnvironment env, Document inDoc) throws Exception {
        if(logger.isVerboseEnabled())
		 	logger.verbose("In validateLines() -> " + XMLUtil.extractStringFromDocument(inDoc));
        
		String primaryOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
		NodeList inputDocOrderLineList = inDoc.getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		for (int i = 0; i < inputDocOrderLineList.getLength(); i++) {
			Element ol = (Element) inputDocOrderLineList.item(i);
			// ---[Check the primary order identifier]-----//
			String tempOrderIdentifier = ol.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			if (StringUtil.isEmpty(tempOrderIdentifier))
				tempOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
			
			if (i == 0) {
				// for the first line assign the identifier
				primaryOrderIdentifier = tempOrderIdentifier;
			} else {
				// Check if the identifiers are not the same and throw an
				// exception
				if (!primaryOrderIdentifier.equals(tempOrderIdentifier)) {
					throw new NWCGException("NWCG_ISSUE_BACKORDER_002");
				}
			}
			 //---[End check primary identifier]------//
			 
			 //---[Check for issue quantity]-----//
			 String newIssueQty = ol.getAttribute("NewIssueQty");
			if (StringUtil.isEmpty(newIssueQty)) {
				throw new NWCGException("NWCG_ISSUE_BACKORDER_003");
			} else {
				// Check if we can parse it to a Double, else throw an exception
				try {
					Double.parseDouble(newIssueQty);
				} catch (NumberFormatException ne) {
					throw new NWCGException("NWCG_ISSUE_BACKORDER_004",
							new String[] { newIssueQty });
				}
			}
			 //---[End check for issue quantity]-----//
		 }
	}
	
	/**
	 * Create the issue header using the original order details also
	 * incorporates the incident details into the header from the original order
	 * 
	 * @param env
	 * @param orderLine
	 * @return
	 * @throws Exception
	 */
	private Document createOrderHeader (YFSEnvironment env, Element orderLine ) throws Exception 
	{
		if (logger.isVerboseEnabled()) 
			logger.verbose("Entering createOrderHeader(env, orderLine)");
		
		Document createOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element createOrderInputDocElm = createOrderInput.getDocumentElement();
		//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ORDERLINE ---- "+XMLUtil.getElementXMLString(orderLine));
		//----[Set the order headers]----//
		Element olOrdElem = (Element) XMLUtil.getChildNodeByName(orderLine, NWCGConstants.ORDER_ELM);
		createOrderInputDocElm.setAttribute(NWCGConstants.DRAFT_ORDER_FLAG, NWCGConstants.YES);//set the draft order flag 
		createOrderInputDocElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, olOrdElem.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR));
		createOrderInputDocElm.setAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE, olOrdElem.getAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE));
		createOrderInputDocElm.setAttribute("BillToID", olOrdElem.getAttribute("BillToID"));
		createOrderInputDocElm.setAttribute(NWCGConstants.DOCUMENT_TYPE, olOrdElem.getAttribute(NWCGConstants.DOCUMENT_TYPE));
		createOrderInputDocElm.setAttribute(NWCGConstants.ORDER_TYPE, NWCGConstants.OB_ORDER_TYPE_BACKORDERED);
		createOrderInputDocElm.setAttribute(NWCGConstants.SHIP_NODE, orderLine.getAttribute(NWCGConstants.SHIP_NODE));//not found in getOrderLineDetails op xml
		createOrderInputDocElm.setAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR));
		createOrderInputDocElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR));
		createOrderInputDocElm.setAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR));
		createOrderInputDocElm.setAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY, olOrdElem.getAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY));
		//---[End set headers]--//
		
		//--Set OH/Extn by importing it from the getOrderLineDetails:/OrderLine/Order element
		Node olOrderExtnNode = XMLUtil.getChildNodeByName(olOrdElem, NWCGConstants.EXTN_ELEMENT);
				
		// Import the getOrderLineDetails:/OrdeLine/Order/Extn node into our createOrder input doc
		olOrderExtnNode = createOrderInput.importNode(olOrderExtnNode, true);
		Element olOrderExtnElem = (Element) olOrderExtnNode;
		createOrderInputDocElm.appendChild(olOrderExtnNode);
		
		String incidentNo = olOrderExtnElem.getAttribute(NWCGConstants.INCIDENT_NO);
		String incidentYear = olOrderExtnElem.getAttribute(NWCGConstants.INCIDENT_YEAR);
		
		if (logger.isVerboseEnabled()) 			
			logger.verbose("Incident No/Year:"+incidentNo+"/"+incidentYear);
		
		//Call NWCGGetOrderService and copy data from its output to our
		//createOrderInput doc
			// Top of - CR 524 - populate extn attributes from OrderHeader records
		String reqDeliveryDate = olOrderExtnElem.getAttribute("ExtnReqDeliveryDate");
		String shipAcctCode = olOrderExtnElem.getAttribute("ExtnShipAcctCode");
		String sAOverrideCode = olOrderExtnElem.getAttribute("ExtnSAOverrideCode");
		// Bottom of - CR 524 - populate extn attributes from OrderHeader records
		if(!StringUtil.isEmpty(incidentNo)){
			//-----------[Add the incident details]---------------//
			addIncidentDetails(env, createOrderInput, incidentNo, incidentYear,reqDeliveryDate,shipAcctCode,sAOverrideCode);
		}
		
		////-----------[Add the orderLines element]---------------//
		Element orderLines = createOrderInput.createElement(NWCGConstants.ORDER_LINES);
		createOrderInputDocElm.appendChild(orderLines);
		
		if(logger.isVerboseEnabled() || icbsDebug.booleanValue()) {
			logger.verbose("Exiting createOrderHeader()-> "+XMLUtil.extractStringFromDocument(createOrderInput));
			
		}
		//System.out.println("Exiting createOrderHeader()-> "+XMLUtil.extractStringFromDocument(createOrderInput));
		return createOrderInput;
	}
	
	/**
	 * This method adds the incident details to the order header if Incident # &
	 * Year is found in ICBS
	 * 
	 * @param env
	 *            com.yantra.yfs.japi.YFSEnvironment object
	 * @param createOrderInput
	 *            org.w3c.dom.Document to be passed into createOrder to create
	 *            the BackOrder
	 * @param incidentNo
	 *            Incident Number e.g. CO-RMK-000310
	 * @param incidentYear
	 *            Incident Year
	 * @throws Exception
	 */
	private void addIncidentDetails(YFSEnvironment env, Document createOrderInput, String incidentNo, String incidentYear,String reqDeliveryDate,String shipAcctCode, String sAOverrideCode) 
	throws Exception
	{
		Element createOrderInputDocElm = createOrderInput.getDocumentElement();
		
		if(logger.isVerboseEnabled()) {
			String coi = XMLUtil.extractStringFromDocument(createOrderInput);
			logger.verbose("org.w3c.dom.Document createOrderInput: " + coi);
		}				
		
		Document getIncidentOrderInput = XMLUtil.createDocument("NWCGIncidentOrder");
		getIncidentOrderInput.getDocumentElement().setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
		if (incidentYear.length() == 0)
			incidentYear = " ";
        getIncidentOrderInput.getDocumentElement().setAttribute(NWCGConstants.YEAR_ATTR, incidentYear);
        
        if(logger.isVerboseEnabled()) {
	        String inputRightBeforeInvokeService = XMLUtil.extractStringFromDocument(getIncidentOrderInput);
	        logger.verbose("Input to NWCGGetIncidentOrderService: " + inputRightBeforeInvokeService);
        }
        
		Document getIncidentOrderServiceOpDoc = CommonUtilities.invokeService(
				env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentOrderInput);
		
        if(logger.isVerboseEnabled()) {
	        String inputRightAfterInvokeService = XMLUtil.extractStringFromDocument(getIncidentOrderServiceOpDoc);
	        logger.verbose("Output of NWCGGetIncidentOrderService: " + inputRightAfterInvokeService);
        }		
		
		//If we do not get any incident details, throw an exception 
		if(null == getIncidentOrderServiceOpDoc){
			NWCGException e = new NWCGException("NWCG_ISSUE_BACKORDER_001", new String[]{incidentNo});
			throw e;
		}
			
		Element incidentDocElm = getIncidentOrderServiceOpDoc.getDocumentElement();
				
		// Copy the following attributes from the NWCGIncidentOrder node into
		// Order node
		//createOrderInputDocElm.setAttribute(NWCGConstants.SHIP_NODE, incidentDocElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID));
		
		if (logger.isVerboseEnabled()) {
			logger.verbose("-----------------------------------------------------");
			logger.verbose("createOrder IP XML: "+ XMLUtil.extractStringFromDocument(createOrderInput));
			logger.verbose("-----------------------------------------------------");
		}
		
		Node olOrderExtnNode = XMLUtil.getChildNodeByName(createOrderInputDocElm, NWCGConstants.EXTN_ELEMENT);
		Element orderExtn = (olOrderExtnNode instanceof Element) ? (Element) olOrderExtnNode : null;
		
		// Top of CR 524 - populate Requested Delivery Date from the original order
	    if (orderExtn != null) {
			if(!StringUtil.isEmpty(reqDeliveryDate)){
				orderExtn.setAttribute("ExtnReqDeliveryDate",reqDeliveryDate);
		    }
		    if(!StringUtil.isEmpty(shipAcctCode)){
				orderExtn.setAttribute("ExtnShipAcctCode",shipAcctCode);  //user manually entered ShipAcctCode
		    }
		    if(!StringUtil.isEmpty(sAOverrideCode)){
				orderExtn.setAttribute("ExtnSAOverrideCode",sAOverrideCode);  //user manually entered ShipAcctOverrideCode
		    }
	    }
		// Bottom of CR 524 - populate Requested Delivery Date from the original order
		//--------[Copy Incident addresses]--------------//
		
		//ShipTo		
		Element incidentShipToAdd = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoShipTo");
		if(null != incidentShipToAdd) {
			Element newOrderPersonInfoShipTo = createOrderInput.createElement("PersonInfoShipTo");
			createOrderInputDocElm.appendChild(newOrderPersonInfoShipTo);
			
			copyAddress(incidentShipToAdd, newOrderPersonInfoShipTo);
			newOrderPersonInfoShipTo = removeUnneededAddressFieldsFromPersonInfoElement(newOrderPersonInfoShipTo);			
		}
		
		//BillTo
		Element incidentBillToAdd = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoBillTo");
		if (null != incidentBillToAdd){
			Element newOrderPersonInfoBillTo = createOrderInput.createElement("PersonInfoBillTo");			
			
			copyAddress(incidentBillToAdd, newOrderPersonInfoBillTo);
			newOrderPersonInfoBillTo = removeUnneededAddressFieldsFromPersonInfoElement(newOrderPersonInfoBillTo);
			createOrderInputDocElm.appendChild(newOrderPersonInfoBillTo);			
		}
		//DeliverTo
		Element incidentDeliverTo = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoDeliverTo");
		if(null != incidentDeliverTo){
			Element additionalAddresses = createOrderInput.createElement("AdditionalAddresses");
			createOrderInputDocElm.appendChild(additionalAddresses);
			Element additionalAddress = createOrderInput.createElement("AdditionalAddress");
			additionalAddresses.appendChild(additionalAddress);
			additionalAddress.setAttribute("AddressType", NWCGConstants.ADDRESS_TYPE_DELIVER);
			Element personInfo = createOrderInput.createElement("PersonInfo");
			
			copyAddress(incidentDeliverTo, personInfo);
			personInfo = removeUnneededAddressFieldsFromPersonInfoElement(personInfo);
			additionalAddress.appendChild(personInfo);
		}
		
		if(logger.isVerboseEnabled()) {
			logger.verbose("END addIncidentDetails() createOrder Input XML:");
			logger.verbose(XMLUtil.extractStringFromDocument(createOrderInput));
		}
	}
	
	/**
	 * This method adds an OrderLine element to the createOrder input document.
	 * It gets the issue qty from the input orderline and adjusts it with the
	 * order qty while creating the new issue. It also sets the backorder flag
	 * on the line if the issue qty cannot does not fully satisfy the backorder
	 * qty The S-Number or the Request Number is also updated with increments
	 * and extensions.
	 * 
	 * eg. S-1 would become S-1.1 after first extension and S-1.2 after second
	 * extension and so on.
	 * 
	 * @param orderDoc
	 * @param inOrderLine
	 * @param origOrderLine
	 *            aka output of getOrderLineDetails API: getOrderLineDtlsDocElm
	 *            from the calling method
	 * @return Document
	 * @throws Exception
	 */
	private Document addLineToIssue(Document createOrderInputDoc, Element inOrderLine, Element origOrderLine, int lineCounter) 
	throws Exception {
        if(logger.isVerboseEnabled()) {
		 	logger.verbose("Entering addLineToIssue() createOrderInputDoc XML -> "
							+ XMLUtil.extractStringFromDocument(createOrderInputDoc));
        }
        
		Element orderLines = (Element) XMLUtil.getChildNodeByName(
				createOrderInputDoc.getDocumentElement(),
				NWCGConstants.ORDER_LINES);
		
		Element orderLine = (Element) createOrderInputDoc.importNode(
				origOrderLine, true);
		
		orderLines.appendChild(orderLine);

		// Remove the OrderHeaderKey from the orderline element
		orderLine.removeAttribute(NWCGConstants.ORDER_HEADER_KEY);

		// Remove the order and extn elements from the orderline (on the getOrderLineList OP Doc)
		orderLine.removeChild(XMLUtil.getChildNodeByName(orderLine, NWCGConstants.ORDER_ELM));
		
		// Get the input issue quantity
		String newIssueQty = inOrderLine.getAttribute("NewIssueQty");
		
		// if the issue quantity is not entered throw back a NWCGException
		if(StringUtil.isEmpty(newIssueQty)){
			throw new NWCGException("NWCG_ISSUE_BACKORDER_003"); 
		}
		// Else we'll process the order line
		// Set the OL/OrderLineTranQuantity/@OrderedQty on the new line to the
		// newIssueQty variable
		Element olTranQtyElm = createOrderInputDoc
				.createElement(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		
		orderLine.appendChild(olTranQtyElm);
		olTranQtyElm.setAttribute(NWCGConstants.ORDERED_QTY, newIssueQty);
		
		//-----[The back order and quantity]--------//
		//As we have already imported the orig orderline we can get the request number from there
		Element orderLineExtn = (Element) XMLUtil.getChildNodeByName(orderLine, NWCGConstants.EXTN_ELEMENT);
		Element origOrderLineExtn = (Element) XMLUtil.getChildNodeByName(origOrderLine, NWCGConstants.EXTN_ELEMENT);
		
		orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, "0");
		orderLineExtn.setAttribute("ExtnOrigReqQty", newIssueQty);// set the RequestedQty
		orderLineExtn.setAttribute("ExtnBackOrderFlag", NWCGConstants.NO);// set the back order flag to Y
						
		// Set the Order/Extn/@ExtnShippingContactName&Phone from the original OrderLine/Order/Extn
		// element
		orderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR , origOrderLineExtn
				.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR));
		orderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR, origOrderLineExtn
				.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR));
	
		// -----[ The Request # "S-" number assignment]----//
		String requestNo = orderLineExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		
		if (!StringUtil.isEmpty(requestNo)) {
			StringBuffer sb = new StringBuffer(requestNo);
			sb.append(NWCGConstants.REQUEST_NO_EXTN_DELIM);
			sb.append(new String(""+lineCounter));
			orderLineExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, sb.toString());
		}
		
        if(logger.isVerboseEnabled())
		 	logger.verbose("Exiting addLineToIssue() orderDoc XML ->" + 
		 						XMLUtil.extractStringFromDocument(createOrderInputDoc));
        
		return createOrderInputDoc;
	}
	
	/**
	 * This method removes the backorder status from the original line as all
	 * the backorder qty is already transfered to the new issue
	 * 
	 * @param env
	 * @param inpOrderLine
	 * @param origOrderLine
	 * @return
	 * @throws Exception
	 */
	private Document changeBackOrderStatusInOriginalIssue(YFSEnvironment env,
			Element inpOrderLine, Element origOrderLine, Document changeOrderInput) 
	throws Exception {
        if(logger.isVerboseEnabled())
		 	logger.verbose("Entering changeBackOrderStatusInOriginalIssue() -> " + 
		 						XMLUtil.extractStringFromDocument(changeOrderInput));
        
		String origOLorderHeaderKey = origOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Element origOrderLineExtn = (Element) XMLUtil.getChildNodeByName(origOrderLine, NWCGConstants.EXTN_ELEMENT);
		String origBackOrderQty = origOrderLineExtn.getAttribute(NWCGConstants.EXTN_BACKORDER_QTY);
		Double dblOrigBackOrderQty = Double.parseDouble(origBackOrderQty);
		
		String newIssueQty = inpOrderLine.getAttribute("NewIssueQty");
		Double dblNewIssueQty = Double.parseDouble(newIssueQty);
		
		if (origOLorderHeaderKey.equals(TempOrderHeaderKey))
		{
			Element order = changeOrderInput.getDocumentElement();
			Element orderLines = (Element) order.getElementsByTagName(NWCGConstants.ORDER_LINES).item(0);
			Element orderLine = changeOrderInput.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);
			
			// Reset the backorder flag to N
			if ((dblOrigBackOrderQty - dblNewIssueQty) <= 0){
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, NWCGConstants.NO);
            //  Commenting out the following line for CR-800 - SGN
			//	orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, "0");
			}
			else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, new Double(dblOrigBackOrderQty - dblNewIssueQty).toString());
			}
			//orderLineExtn.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.SHIPPING_ADDRESS);
		}
		else
		{
			logger.verbose("changeOrder API XML: (before): "+ XMLUtil.extractStringFromDocument(changeOrderInput));    		
    		// API call to changeOrder
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInput);
			
    		changeOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
    		Element order = changeOrderInput.getDocumentElement();
			order.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
			order.setAttribute(NWCGConstants.ORDER_HEADER_KEY, origOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY));
			Element orderLines = changeOrderInput.createElement(NWCGConstants.ORDER_LINES);
			order.appendChild(orderLines);
			Element orderLine = changeOrderInput.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);
			
			// Reset the backorder flag to N
			if ((dblOrigBackOrderQty - dblNewIssueQty) <= 0){
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, NWCGConstants.NO);
            //  Commenting out the following line for CR-800 - SGN
			//	orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, "0");
			}
			else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, new Double(dblOrigBackOrderQty - dblNewIssueQty).toString());
			}
			orderLineExtn.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.SHIPPING_ADDRESS);
			logger.verbose("changeOrder API XML: (after): "+ XMLUtil.extractStringFromDocument(changeOrderInput));
		}
		
		if(logger.isVerboseEnabled())
			logger.verbose("Exiting changeBackOrderQtyInOriginalIssue()-> ChangeOrderInput XML -> " + 
								XMLUtil.extractStringFromDocument(changeOrderInput));
		
		// Document returnDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER,
		// changeOrderInput);
		TempOrderHeaderKey = origOLorderHeaderKey; 
		return changeOrderInput;
	}
	
	/**
	 * Calls and returns the output of the getOrderLineDetals API for the
	 * given orderLineKey
	 * 
	 * @param env
	 * @param orderLineKey
	 * @return Document Output of getOrderLineDetails API
	 * @throws Exception
	 */
	private Document getOrderLineDetails(YFSEnvironment env, String orderLineKey) throws Exception{
		
		Document getOrderLineDetailsInput = XMLUtil.createDocument("OrderLineDetail");
		getOrderLineDetailsInput.getDocumentElement().setAttribute(NWCGConstants.ORDER_LINE_KEY, orderLineKey);
		env.setApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS, "NWCGCreateIssueFromBackOrderLines_getOrderLineDetails");
		Document returnDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ORDER_LINE_DETAILS, getOrderLineDetailsInput);
		env.clearApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS);
		
		if(logger.isVerboseEnabled())
			logger.verbose("Exiting getOrderLineDetails()->" + XMLUtil.extractStringFromDocument(returnDoc));
		
		return returnDoc;
	}	

	/**
	 * Copies all attribute/value pairs from the 'from' Element to the 'to' Element
	 * 
	 * @param from
	 *            Element PersonInfo Address element
	 * @param to
	 *            Element PersonInfo Address element
	 */
	private void copyAddress(Element from, Element to) {
		NamedNodeMap attrMap = from.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) {
			Attr attr = (Attr) attrMap.item(i);
			// Get the binding key from the template
			String name = attr.getName();
			String value = attr.getValue();
			to.setAttribute(name, value);
		}
	}
	
	/**
	 * Removes attributes from a PersonInfo{Ship,Bill,Deliver}To element that
	 * should not be present in the input to createOrder
	 * 
	 * @param aPersonInfoElm
	 * @return Element Containing the Element passed in with misc attributes
	 *         removed
	 */
	private Element removeUnneededAddressFieldsFromPersonInfoElement(Element aPersonInfoElm) {
		aPersonInfoElm.removeAttribute("Createprogid");		
		aPersonInfoElm.removeAttribute("Createts");
		aPersonInfoElm.removeAttribute("Createuserid");		
		aPersonInfoElm.removeAttribute("Modifyprogid");
		aPersonInfoElm.removeAttribute("Modifyts");
		aPersonInfoElm.removeAttribute("Modifyuserid");		
		aPersonInfoElm.removeAttribute("PersonInfoKey");
		aPersonInfoElm.removeAttribute("UseCount");
		aPersonInfoElm.removeAttribute("VerificationStatus");
		aPersonInfoElm.removeAttribute("PersonID");
		aPersonInfoElm.removeAttribute("isHistory");
		aPersonInfoElm.removeAttribute("PreferredShipAddress");
		aPersonInfoElm.removeAttribute("Lockid");		
		return aPersonInfoElm;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
		myProperties = props;
	}
	
	private String TempOrderHeaderKey = "";
	private Boolean icbsDebug = new Boolean(ResourceUtil.get("icbs.devmode.debug", "false"));
	
	/**
	 * Logger Instance.
	 */	 
	private static Logger logger = Logger
			.getLogger(NWCGCreateIssueFromBackOrderLines.class.getName());
	
	/**
	 * Properties instance holding MCF SDF API component name/value argument
	 * pairs defined in the configurator
	 */
	private Properties myProperties = null;	
}
