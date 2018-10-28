/**
 * @author sgunda
 */

package com.nwcg.icbs.yantra.api.refurb;

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateRefurbIssueFromRefurbWO implements YIFCustomApi{

	private Properties _properties;
	private static Logger log = Logger.getLogger(NWCGRefurbDisposition.class.getName()); 
	
  public void setProperties(Properties prop) throws Exception
  {
      _properties = prop;
  }
	

  /**
   * - Create the refurb issue only if quantity completed and quantity requested are same.
   * - Update the cost of refurb issue on refurb work order
   * - Change the issue status
   * @param env
   * @param inputDoc
   * @return
   * @throws NWCGException
   */
  public Document checkAndCreateRefurbIssue(YFSEnvironment env, Document inputDoc)
  throws NWCGException,Exception {
  	try {
  	
  		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Entered");
  		if (log.isVerboseEnabled()){
  			if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, inputDoc : " + XMLUtil.getXMLString(inputDoc));
  		}
    	Element rootNode = inputDoc.getDocumentElement();
    	if(log.isVerboseEnabled()) log.verbose("The input doc is: \n" + XMLUtil.getXMLString(inputDoc));

    		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Creating Input Refurb Issue Doc");
    		if(log.isVerboseEnabled()) log.verbose("Creating refurb doc...");
    		Document inputRfbIssueDoc = createRefurbIssueDocument(env, inputDoc);
    		Document rfbIssueOutputDoc = null ;// Jay: we shouldnt assing it a blank document XMLUtil.newDocument();
    		
    		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Invoking Create Order : " + NWCGConstants.API_CREATE_ORDER);
    		
    		if(inputRfbIssueDoc != null )
    		{
    			if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, inputRfbIssueDoc is : \n" + XMLUtil.getXMLString(inputRfbIssueDoc));
   		 		if(log.isVerboseEnabled()) log.verbose("inputRfbIssueDoc != null");
   		 		NodeList lsOrderLineList = inputRfbIssueDoc.getElementsByTagName("OrderLine");
   		 		if(lsOrderLineList != null && lsOrderLineList.getLength()>0)
   		 		{
   		 			rfbIssueOutputDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_CREATE_ORDER, inputRfbIssueDoc);

   		 			if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, rfbIssueOutputDoc is : \n" + XMLUtil.getXMLString(rfbIssueOutputDoc));
   		 		}
    		}
     		
    		if (rfbIssueOutputDoc != null){
      		// Update the workorder on creation of workorder.
      		if(log.isVerboseEnabled()) log.verbose("(rfbIssueOutputDoc != null)");
      		
    			if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Updating Refurb WO Cost");
 
    			Document chgStatusDoc = createChgIssueStatusDoc(rfbIssueOutputDoc);
    			if(log.isVerboseEnabled()) log.verbose("chgStatusDoc is: \n " + XMLUtil.getXMLString(chgStatusDoc));
          		// Changing the status to an extended status
          		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Changing the status : " + XMLUtil.getXMLString(chgStatusDoc));
          		Document chgStatusOutput = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER_STATUS, chgStatusDoc);      		
          	if(log.isVerboseEnabled()) log.verbose("chgStatusOutput is: \n" + XMLUtil.getXMLString(chgStatusOutput));
    			
    		}
    		else {
    		
    			if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Unable to create refurb issue as order document is NULL ");
    		}
   
  	}
  	catch (ParserConfigurationException pce){
  		log.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Parser Conf Exc : " + pce.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Parser StackTrace : " + pce.getStackTrace());
  		throw new NWCGException(pce);
  	}
  	catch (Exception e){
  		log.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Exception Message : " + e.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, StackTrace : " + e.getStackTrace());
  		// Jay: dont mask the exception !!!!!!!!!
  		throw e;
  	}
  	if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Exiting");
  	if(log.isVerboseEnabled()) log.verbose("Returning the input document");
  	return inputDoc;
  }
  
  /**
   * This method builds the input xml for createOrder API.
   * @param env
   * @param skuOPDoc
   * @return
   * @throws NWCGException
   */
  private Document createRefurbIssueDocument(YFSEnvironment env, Document skuOPDoc)
  throws NWCGException{
  	try {
  		if(log.isVerboseEnabled()) log.verbose("Entering createRefurbIssueDocument...");
  		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Entered");
    	Document orderIssueDoc = XMLUtil.newDocument();
    	Element rootNode = skuOPDoc.getDocumentElement();
    	String nodeKey = XMLUtil.getAttribute(rootNode, "Node");
    	String enterpriseCode = XMLUtil.getAttribute(rootNode, "EnterpriseCode");
	
    	String workOrderNo = XMLUtil.getAttribute(rootNode, "WorkOrderNo");
    	
    	Element orderElm = orderIssueDoc.createElement("Order");
    	orderIssueDoc.appendChild(orderElm);
    	orderElm.setAttribute("DocumentType", NWCGConstants.ORDER_DOCUMENT_TYPE);
    	orderElm.setAttribute("ApplyDefaultTemplate", NWCGConstants.YES);
    	orderElm.setAttribute("CreatedAtNode", NWCGConstants.YES);
    	orderElm.setAttribute("CreatedByNode", nodeKey);
    	orderElm.setAttribute("EnterpriseCode", enterpriseCode);
    	orderElm.setAttribute("IgnoreOrdering", NWCGConstants.YES);
    	orderElm.setAttribute("DraftOrderFlag", NWCGConstants.NO);
    	orderElm.setAttribute("OrderType", NWCGConstants.REFURBISHMENT_ORDER_TYPE);
    	orderElm.setAttribute("SellerOrganizationCode", enterpriseCode);
    	orderElm.setAttribute("BuyerOrganizationCode", nodeKey);
    	orderElm.setAttribute("ShipNode", nodeKey);

    	Element orderLinesElm = orderIssueDoc.createElement("OrderLines");
    	NodeList orderLinesList = rootNode.getElementsByTagName("WorkOrderComponent");
    	
    	if(orderLinesList == null || orderLinesList.getLength() <= 0)
    	{
    		if(log.isVerboseEnabled()) log.verbose("orderLinesList == null || orderLinesList.getLength() <= 0");
    		// Returning null as we dont want to create an issue for work order without any components
    		return null ;
    	}
    	
    	for (int i=0; i < orderLinesList.getLength(); i++){
    		
    		if(log.isVerboseEnabled()) log.verbose("Entering for loop... iteration " + i);
    		Element tempOrderLine = (Element) orderLinesList.item(i);
    		String cmpQty = tempOrderLine.getAttribute("ComponentQuantity");
    		if(cmpQty != null && !cmpQty.equals(""))
    		{
	    		double dblCmpQty = Double.parseDouble(cmpQty);
	    		if(dblCmpQty > 0)
	    		{
		    		String uom = tempOrderLine.getAttribute("Uom");
		    		String itemID = tempOrderLine.getAttribute("ItemID");
		    		String prodClass = tempOrderLine.getAttribute("ProductClass");
		    		
		    		Element orderLineElm = orderIssueDoc.createElement("OrderLine");
		    		orderLineElm.setAttribute("ShipNode", nodeKey);
		    		
		    		Element orderLineTranQtyElm = orderIssueDoc.createElement("OrderLineTranQuantity");
		     		orderLineTranQtyElm.setAttribute("OrderedQty", cmpQty);
		    		orderLineTranQtyElm.setAttribute("TransactionalUOM", uom);
		    		orderLineElm.appendChild(orderLineTranQtyElm);
		    		
		    		Element itemElm = orderIssueDoc.createElement("Item");
		    		itemElm.setAttribute("ItemID", itemID);
		    		itemElm.setAttribute("ProductClass", prodClass);
		    		orderLineElm.appendChild(itemElm);
		    		
		    		orderLinesElm.appendChild(orderLineElm);
	    		}
    		}
    	}
    	orderElm.appendChild(orderLinesElm);
 	
    	// Get Incident Number, FS Acct Code, BLM Acct Code, Other Acct Code
    	NodeList nlNWCGMasterWorkOrderLine = rootNode.getElementsByTagName("NWCGMasterWorkOrderLine");
    	Element eleNWCGMasterWorkOrderLine = (Element) nlNWCGMasterWorkOrderLine.item(0);
    	
    	
    	String workOrderKey = XMLUtil.getAttribute(eleNWCGMasterWorkOrderLine, "MasterWorkOrderKey");
    	if(log.isVerboseEnabled()) log.verbose("************************** MasterWO Key **************");
    	if(log.isVerboseEnabled()) log.verbose(workOrderKey);
    	if(log.isVerboseEnabled()) log.verbose("************************** MasterWO Key **************");
    	
    	//get root node
    	if(log.isVerboseEnabled()) log.verbose(XMLUtil.getElementXMLString(rootNode));
    	
    	Hashtable extnDtls = getIncidentDetails(env, workOrderKey);
    	Element extnElm = orderIssueDoc.createElement("Extn");
    	extnElm.setAttribute("ExtnRefurbWO", workOrderNo);
    	if (extnDtls != null && extnDtls.size() > 0){
    		
    		String extnIncidentNo = (String) extnDtls.get("ExtnIncidentNo");
    		String extnMasterWorkOrderNo = (String) extnDtls.get("ExtnMasterWorkOrderNo");
    		String extnIncidentName = (String) extnDtls.get("ExtnIncidentName");
    		String extnIncidentType = (String) extnDtls.get("ExtnIncidentType");
    		String extnOverrideCode = (String) extnDtls.get("ExtnOverrideCode");
    		String extnIncidentYear = (String) extnDtls.get("ExtnIncidentYear");
    		

        extnElm.setAttribute("ExtnIncidentYear", extnIncidentYear);
        extnElm.setAttribute("ExtnMasterWorkOrderNo", extnMasterWorkOrderNo);
        extnElm.setAttribute("ExtnOverrideCode", extnOverrideCode);
      	extnElm.setAttribute("ExtnIncidentNo", extnIncidentNo);
      	extnElm.setAttribute("ExtnIncidentName", extnIncidentName);
      	extnElm.setAttribute("ExtnIncidentType", extnIncidentType);
      	
      	extnElm.setAttribute("ExtnFsAcctCode", (String)extnDtls.get("ExtnFsAcctCode"));
      	extnElm.setAttribute("ExtnBlmAcctCode", (String)extnDtls.get("ExtnBlmAcctCode"));
      	extnElm.setAttribute("ExtnOtherAcctCode", (String)extnDtls.get("ExtnOtherAcctCode"));
      	orderElm.setAttribute("BillToID", (String) extnDtls.get("IncidentCustomerId"));
    	}
    	orderElm.appendChild(extnElm);
    	
    	if (log.isVerboseEnabled()){
      	if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Order XML : " + XMLUtil.getXMLString(orderIssueDoc));
    	}
    	if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Returning");
    	return orderIssueDoc;
  	}
  	catch (ParserConfigurationException pce){
  		log.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Parser Conf Exc : " + pce.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Parser StackTrace : " + pce.getStackTrace());
  		throw new NWCGException(pce);
  	}
  	catch (Exception e){
  		log.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Exception Message : " + e.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, StackTrace : " + e.getStackTrace());
  		throw new NWCGException(e);
  	}
  }
  
	/**
	 * This method gets the incident related information (Incident No, FS, BLM and Other Account Code
	 * from getWorkOrderDetails API call.
	 * @param env
	 * @param workOrderKey
	 * @return
	 */
	private Hashtable getIncidentDetails(YFSEnvironment env, String workOrderKey){
		Hashtable result = new Hashtable();
		
		try {
			if(log.isVerboseEnabled()) log.verbose("Entering  getIncidentDetails...");
			Document inputDoc = XMLUtil.getDocument();
			Element workOrderElem = inputDoc.createElement("NWCGMasterWorkOrder");
			inputDoc.appendChild(workOrderElem);
			workOrderElem.setAttribute("MasterWorkOrderKey", workOrderKey);	
			if (log.isVerboseEnabled()){
				if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Input XML : " + XMLUtil.getXMLString(inputDoc));
			}
		
			
			Document outputDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_MASTER_WORK_ORDER_DTLS_SERVICE, inputDoc);
			
			if (outputDoc != null){
				if(log.isVerboseEnabled()) log.verbose("outputDoc != null");
				if(log.isVerboseEnabled()) log.verbose("outputDoc is : \n" + XMLUtil.getXMLString(outputDoc));
				if (log.isVerboseEnabled()){
					if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, getWorkOrderDetails : " + XMLUtil.getXMLString(outputDoc));
				}
				if(log.isVerboseEnabled()) log.verbose("Element rootNode = outputDoc.getDocumentElement()");
				Element rootNode = outputDoc.getDocumentElement();
				
				if(log.isVerboseEnabled()) log.verbose("String incidentNo = extnElm.getAttribute(extnincidentno)");
				String incidentNo = rootNode.getAttribute("IncidentNo");
				String masterWONo = rootNode.getAttribute("MasterWorkOrderNo");
				String incidentName = rootNode.getAttribute("IncidentName");
				String incidentYear = rootNode.getAttribute("IncidentYear");
				String incidentType = rootNode.getAttribute("IncidentType");
				String overrideCode = rootNode.getAttribute("OverrideCode");
				if(log.isVerboseEnabled()) log.verbose("incidentNo getting sent to getcustomerdi is: " + incidentNo);

			result.put("ExtnIncidentYear", incidentYear);
			result.put("ExtnMasterWorkOrderNo", masterWONo);	
			result.put("ExtnOverrideCode", overrideCode);
			result.put("ExtnIncidentName", incidentName);
			result.put("ExtnIncidentType", incidentType);				
	    	result.put("ExtnIncidentNo", incidentNo);
	    	if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Incident No : " + incidentNo);
	    	String fsAcctCode = rootNode.getAttribute("FSAccountCode");
	    	result.put("ExtnFsAcctCode", fsAcctCode);
	    	String blmAcctCode = rootNode.getAttribute("BLMAccountCode");
	    	result.put("ExtnBlmAcctCode", blmAcctCode);
	    	String otherAcctCode = rootNode.getAttribute("OtherAccountCode");
	    	result.put("ExtnOtherAcctCode", otherAcctCode);
	    	
      	String incidentCustomerId = getIncidentCustomerId(env, incidentNo);
      	
      	if(log.isVerboseEnabled()) log.verbose("incidentCustomerId is: " + incidentCustomerId);
      	result.put("IncidentCustomerId", incidentCustomerId);
			}
			else {
				if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, NULL output from getWorkOrderDetails");
			}
		}
		catch(ParserConfigurationException pce){
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, ParserConfigurationException Msg : " + pce.getMessage());
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, StackTrace : " + pce.getStackTrace());
		}
		catch(Exception e){
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Exception Msg : " + e.getMessage());
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, StackTrace : " + e.getStackTrace());
		}
		return result;
	}

	/**
	 * This methods build the input xml string for changing the refurb issue
	 * status from CREATED to REFURB_COMPLETED.
	 * @param rfbIssueOutputDoc
	 * @return
	 * @throws NWCGException
	 */
  private Document createChgIssueStatusDoc(Document rfbIssueOutputDoc)
  throws NWCGException{
  	try {
  		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Entered");
    	Document chgStatusDoc = XMLUtil.newDocument();
    	Element orderStatusChgElm = chgStatusDoc.createElement("OrderStatusChange");
    	chgStatusDoc.appendChild(orderStatusChgElm);
    	orderStatusChgElm.setAttribute("BaseDropStatus", NWCGConstants.RFB_CMPL_DROP_STATUS);
    	orderStatusChgElm.setAttribute("DocumentType", NWCGConstants.ORDER_DOCUMENT_TYPE);
    	
    	Element rootNode = rfbIssueOutputDoc.getDocumentElement();
    	String enterpriseCode = XMLUtil.getAttribute(rootNode, "EnterpriseCode");
    	orderStatusChgElm.setAttribute("EnterpriseCode", enterpriseCode);
    	
    	String orderNo = XMLUtil.getAttribute(rootNode, "OrderNo");
    	orderStatusChgElm.setAttribute("OrderNo", orderNo);
    	
    	orderStatusChgElm.setAttribute("ChangeForAllAvailableQty", NWCGConstants.YES);
    	orderStatusChgElm.setAttribute("TransactionId", NWCGConstants.TRANSACTION_REFURB_STATUS_CHANGE);
    	if (log.isVerboseEnabled()){
    		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Input : " + XMLUtil.getXMLString(chgStatusDoc));
    	}
  		if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Exiting");
    	return chgStatusDoc;
  	}
  	catch (ParserConfigurationException pce){
  		log.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Parser Conf Exc : " + pce.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Parser StackTrace : " + pce.getStackTrace());
  		throw new NWCGException(pce);
  	}
  	catch (Exception e){
  		log.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Exception Message : " + e.getMessage());
  		log.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, StackTrace : " + e.getStackTrace());
  		throw new NWCGException(e);
  	}
  }
  
  
  private String getIncidentCustomerId(YFSEnvironment env, String incidentNo){
  	String customerId = "";
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentOrderElm = inputDoc.createElement("NWCGIncidentOrder");
			inputDoc.appendChild(incidentOrderElm);
			incidentOrderElm.setAttribute("IncidentNo", incidentNo);
			if (log.isVerboseEnabled()){
				if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, Input XML : " + XMLUtil.getXMLString(inputDoc));
			}
			Document outputDoc = CommonUtilities.invokeService(env, "NWCGGetIncidentOrderListService", inputDoc);

			if(log.isVerboseEnabled()) log.verbose("getIncidentCustomerID output is: \n" + XMLUtil.getXMLString(outputDoc));
			
			if (outputDoc != null){
				if(log.isVerboseEnabled()) log.verbose("Customer output doc is != null");
				if (log.isVerboseEnabled()){
					if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, NWCGGetIncidentOrderService : " + XMLUtil.getXMLString(outputDoc));
				}
				Element rootNode = outputDoc.getDocumentElement();
				NodeList nlNWCGIncidentOrder = rootNode.getElementsByTagName("NWCGIncidentOrder");
				Element eleNWCGIncidentOrder = (Element) nlNWCGIncidentOrder.item(0);
				if(log.isVerboseEnabled()) log.verbose("eleNWCGIncidentOrder is: " + XMLUtil.getElementXMLString(eleNWCGIncidentOrder));
				
				customerId = XMLUtil.getAttribute(eleNWCGIncidentOrder, "CustomerId");
				if(log.isVerboseEnabled()) log.verbose("Customer id from eleNWCGIncidentOrder is: " + customerId);
			}
			else {
				if(log.isVerboseEnabled()) log.verbose("output doc from getcustomerid is null");
				if(log.isVerboseEnabled()) log.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, NULL output from NWCGGetIncidentOrderService");
			}
		}
		catch(ParserConfigurationException pce){
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, ParserConfigurationException Msg : " + pce.getMessage());
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, StackTrace : " + pce.getStackTrace());
		}
		catch(Exception e){
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, Exception Msg : " + e.getMessage());
    	log.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, StackTrace : " + e.getStackTrace());
		}
  	return customerId;
  }
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
