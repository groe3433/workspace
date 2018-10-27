package com.nwcg.icbs.yantra.api.issue.util;

import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGNFESResourceRequest {

	private Document docNFESResourceRequest;
	private StringBuffer specialNeedVal;
	// This variable is set when a ROSS initiated line has been cancelled by ICBSR user
	// Since we are sending cancelled line in consolidated element, we are going to append this notes
	// mentioning that this request is not consolidated, rather it is cancelled
	private String cancelNote;
	
	/**
	 * Instantiates the class with null document
	 *
	 */
	public NWCGNFESResourceRequest(){
		docNFESResourceRequest = null;
		cancelNote = null;
	}

	/**
	 * This constructor will create a document with the passed
	 * input as root name
	 * @param rootName
	 */
	public NWCGNFESResourceRequest(String rootName){
		try {
			docNFESResourceRequest = XMLUtil.getDocument();
			Element elmNFESResReqRoot = docNFESResourceRequest.createElementNS(
											NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, rootName);
			docNFESResourceRequest.appendChild(elmNFESResReqRoot);
			// need to append the ResponseStatus element as first element if it is StatusNFESResourceRequestResp element
			// later this if can be replaced by rootName.endsWith("Resp") if other interfaces plan to use this class
			if(rootName.endsWith("StatusNFESResourceRequestResp"))
			{
				// creating an element with positive response, incase of negative scenario the caller have to change it to negative  
				Element elemResponseStatus = docNFESResourceRequest.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ResponseStatus");
				elmNFESResReqRoot.appendChild(elemResponseStatus);

				Element elemReturnCode = docNFESResourceRequest.createElement("ReturnCode");
				elemReturnCode.setTextContent(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE);
				elemResponseStatus.appendChild(elemReturnCode);
				
				Element elemResponseMessage = docNFESResourceRequest.createElement("ResponseMessage");
				elemResponseStatus.appendChild(elemResponseMessage);
				
				Element elemCode = docNFESResourceRequest.createElement("Code");
				elemCode.setTextContent(NWCGConstants.NWCG_MSG_CODE_STATUS_I_1);
				elemResponseMessage.appendChild(elemCode);
				
				Element elemSeverity = docNFESResourceRequest.createElement("Severity");
				elemSeverity.setTextContent(NWCGAAConstants.SEVERITY_SUCCESS);
				elemResponseMessage.appendChild(elemSeverity);
				
				Element elemDescription = docNFESResourceRequest.createElement("Description");
				elemDescription.setTextContent("SUCCESS");
				elemResponseMessage.appendChild(elemDescription);
				//elmNFESResReqRoot.appendChild(arg0);
			}
			
			//elmNFESResReqRoot.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, 
			//							   NWCGAAConstants.RESOURCE_ORDER_NS_WITH_SCHEMA);
			//elmNFESResReqRoot.setAttribute("xsi:schemaLocation", 
			//								"http://nwcg.gov/services/ross/resource_order/1.1 ResourceOrder.xsd");
			elmNFESResReqRoot.setAttribute("xmlns:xsi", 
											"http://www.w3.org/2001/XMLSchema-instance");
			specialNeedVal = new StringBuffer();
		}
		catch(Exception e){
			System.out.println("NWCGNFESResourceRequest, Exception Message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will return the base document
	 * @return
	 */
	public Document getNFESResourceRequestDocument()
	{
		return docNFESResourceRequest;
	}
	
	/**
	 * This method will set the base document
	 * @param doc
	 */
	public void setNFESResourceRequestDocument(Document doc){
		docNFESResourceRequest = doc;
	}
	
	/**
	 * This method will set the attributes at the root
	 * @param userId
	 * @param entityKey
	 * @param entityName
	 * @param entityVal
	 */
	public boolean setDocAttributes(String userId, String entityKey, 
								 String entityName, String entityVal, String distId){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::setDocAttributes, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}
		
		Element elmNFESResReqRoot = docNFESResourceRequest.getDocumentElement();
		if (userId != null){
			elmNFESResReqRoot.setAttribute("NWCGUSERID", userId);
		}
		
		if (entityKey != null){
			elmNFESResReqRoot.setAttribute(NWCGAAConstants.ENTITY_KEY, entityKey);
		}
		
		if (entityName != null){
			elmNFESResReqRoot.setAttribute(NWCGAAConstants.ENTITY_NAME, entityName);
		}
		
		if (entityVal != null){
			elmNFESResReqRoot.setAttribute(NWCGAAConstants.ENTITY_VALUE, entityVal);
		}
		
		if (distId != null){
			elmNFESResReqRoot.setAttribute(NWCGAAConstants.MDTO_DISTID, distId);
		}
		return true;
	}
	
	/**
	 * This method will set the MessageOriginator element
	 * @return
	 */
	public boolean setMessageOriginator(String shipNode){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::setMessageOriginator, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}

		Element elmMsgOriginator = docNFESResourceRequest.createElementNS(
									NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:MessageOriginator");
		docNFESResourceRequest.getDocumentElement().appendChild(elmMsgOriginator);
		Element elmSysOfOrig = docNFESResourceRequest.createElement("SystemOfOrigin");
		elmMsgOriginator.appendChild(elmSysOfOrig);
		Element elmSysType = docNFESResourceRequest.createElement("SystemType");
		elmSysType.setTextContent(NWCGConstants.ICBS_SYSTEM);
		elmSysOfOrig.appendChild(elmSysType);

		if (shipNode != null && shipNode.length() > 2){
			Element elmDispUnitID = docNFESResourceRequest.createElement("DispatchUnitID");
			elmMsgOriginator.appendChild(elmDispUnitID);
			Element elmUnitIDPrefix = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_PREFIX);
			elmUnitIDPrefix.setTextContent(shipNode.substring(0, 2));
			elmDispUnitID.appendChild(elmUnitIDPrefix);
			Element elmUnitIDSuffix = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_SUFFIX);
			elmUnitIDSuffix.setTextContent(shipNode.substring(2, shipNode.length()));
			elmDispUnitID.appendChild(elmUnitIDSuffix);
		}
		
		return true;
	}
	
	/**
	 * This method will set the RequestKey element
	 * @param incNo
	 * @param incYr
	 * @param reqNo
	 * @return
	 */
	public boolean setRequestKey(String incNo, String incYr, String reqNo){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::setRequestKey, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}
				
		Element elmReqKey = docNFESResourceRequest.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:RequestKey");
		docNFESResourceRequest.getDocumentElement().appendChild(elmReqKey);
		Element elmNaturalResReqKey = docNFESResourceRequest.createElement("NaturalResourceRequestKey");
		elmReqKey.appendChild(elmNaturalResReqKey);
		Element elmIncKey = docNFESResourceRequest.createElement("IncidentKey");
		elmNaturalResReqKey.appendChild(elmIncKey);
		Element elmNaturalIncKey = docNFESResourceRequest.createElement("NaturalIncidentKey");
		elmIncKey.appendChild(elmNaturalIncKey);

		String indexVal = "-";
		int hashIndex = incNo.indexOf(indexVal);
		StringBuffer aftPrefix = new StringBuffer(
				incNo.substring(hashIndex + indexVal.length(), incNo.length()));
		
		Element elmHostID = docNFESResourceRequest.createElement("HostID");
		elmNaturalIncKey.appendChild(elmHostID);
		Element elmUnitIDPref = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_PREFIX);
		elmUnitIDPref.setTextContent(incNo.substring(0, hashIndex));
		elmHostID.appendChild(elmUnitIDPref);
		Element elmUnitIDSuf = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_SUFFIX);
		elmUnitIDSuf.setTextContent(aftPrefix.substring(0, aftPrefix.indexOf(indexVal)));
		elmHostID.appendChild(elmUnitIDSuf);
		
		// Remove the leading zeroes only if its completely numeric (parsable to a
		// primitive int.)
		String seqNo = aftPrefix.substring(
						aftPrefix.indexOf(indexVal) + indexVal.length(), aftPrefix.length());		
		String elmSeqNoContent = NWCGConstants.EMPTY_STRING;
		int intSeqNo = 0;
		try {
			intSeqNo = (new Integer(seqNo)).intValue();
			elmSeqNoContent = (new Integer(intSeqNo)).toString();
		}
		catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Element elmSeqNo = docNFESResourceRequest.createElement("SequenceNumber");
		if (!StringUtil.isEmpty(elmSeqNoContent)) {
			elmSeqNo.setTextContent(elmSeqNoContent);
		}
		else {
			elmSeqNo.setTextContent(seqNo);
		}		
		elmNaturalIncKey.appendChild(elmSeqNo);
		Element elmYrCreated = docNFESResourceRequest.createElement("YearCreated");
		elmYrCreated.setTextContent(incYr);
		elmNaturalIncKey.appendChild(elmYrCreated);
		
		// Populate Request Code
		Element elmReqCode = docNFESResourceRequest.createElement("RequestCode");
		elmNaturalResReqKey.appendChild(elmReqCode);
		Element catalogID = docNFESResourceRequest.createElement("CatalogID");
		elmReqCode.appendChild(catalogID);
		catalogID.setTextContent("S");
		Element elmBaseReqNo = docNFESResourceRequest.createElement("SequenceNumber");
		elmReqCode.appendChild(elmBaseReqNo);
		if (reqNo != null){
			// Pass the request number as it is for StatusNFESResourceRequest where as
			// pass the base request number for UpdateNFESResourceRequest
			String nodeName = getNFESResourceRequestDocument().getDocumentElement().getNodeName();
			if (nodeName.contains("StatusNFESResourceRequestResp")){
				if (reqNo.indexOf("-") != -1){
					reqNo = reqNo.substring(reqNo.indexOf("-") + 1);
				}
				elmBaseReqNo.setTextContent(reqNo);
			}
			else {
				elmBaseReqNo.setTextContent(getBaseReqNo(reqNo));
			}
		}
		return true;
	}
	
	/**
	 * This method will get the base request no
	 * @param reqNo
	 * @return
	 */
	private String getBaseReqNo(String reqNo){
		if (reqNo != null){
			if (reqNo.indexOf("-") != -1){
				reqNo = reqNo.substring(reqNo.indexOf("-") + 1);
			}
			
			if (reqNo.indexOf(".") != -1){
				reqNo = reqNo.substring(0, reqNo.indexOf("."));
			}
		}
		return reqNo;
	}
	
	/**
	 * This method should be called if request number is not passed while creating
	 * RequestKey element
	 * @param reqNo
	 * @return
	 */
	public boolean updateRequestNo(String reqNo){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::updateRequestNo, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}
		
		NodeList nlReqCode = docNFESResourceRequest.getDocumentElement().getElementsByTagName("RequestCode");
		if (nlReqCode != null && nlReqCode.getLength() > 0){
			// There will be only one element with RequestCode
			Element elmReqCode = (Element) nlReqCode.item(0);
			NodeList nlReqCodeChilds = elmReqCode.getChildNodes();
			for (int i=0; i < nlReqCodeChilds.getLength(); i++){
				Node tmpNode = nlReqCodeChilds.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE){
					Element elmIsSeqNo = (Element) tmpNode;
					if (elmIsSeqNo.getNodeName().equals("SequenceNumber")){
						elmIsSeqNo.setTextContent(reqNo.substring(reqNo.indexOf("-") + 1));
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * This method will create ConsolidationDetail or FillDetail element in the document.
	 * ConsolidationDetail element: This element will be created if the status of the
	 * line is 'Cancelled Due To Consolidation'. Caller of this method should check
	 * if the line is non-surviving consolidated line.
	 * FillDetail element: This element will be created if the status of the line is not
	 * 'Cancelled Due To Consolidation'. Caller of this method should have the business logic
	 * of creating a FillDetail element
	 * 		UpdateRequest: Check it is not full UTF line
	 * 		StatusRequest: No check needed as of now
	 * Calling class should make sure that the item id for base request no is part of hashtable.
	 * This is used as part of FillDetail
	 * @param elmOL
	 * @param shipmentOL
	 * @return
	 */
	public boolean populateFillDtlOrConsolidationDtl(Element elmOL, Element elmShipmentOL, Hashtable<String, String> htReqNo2ItemID){
		boolean retnVal = true;
		
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::populateFillDtlOrConsolidationDtl, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}
		
		String olStatus = elmOL.getAttribute(NWCGConstants.MAX_LINE_STATUS);
		if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS)){
			retnVal = createConsolidationElement(elmOL);
		}
		else {
			retnVal = createFillDetail(elmOL, elmShipmentOL, htReqNo2ItemID);
		}
		return retnVal;
	}
	
	/**
	 * Expected Orderline XML
	 * 		<OrderLine OrderLineKey="" MaxLineStatus="" OrderNo="" OrderCreatets="">
				<Extn />
				<Notes NumberOfNotes="">
					<Note />
				</Notes>
				<Item ItemID="" ItemShortDesc=""/>
			</OrderLine>		
	 * Expected Shipment OL
	 * 		<ShipmentLine  ActualQuantity="" ExtnEstimatedDepartDate="" ExtnEstimatedArrivalDate="">
				<ShipmentTagSerials>
					<ShipmentTagSerial Quantity=" " SerialNo=" "/>
				</ShipmentTagSerials>
			</ShipmentLine>
	 * Calling class should make sure that the item id for base request no is part of hashtable.
	 * Note: Other elements/attribtues can be present, but this class will not be using it.
	 * @param elmOL
	 * @param elmShipmentOL
	 * @return
	 */
	private boolean createFillDetail(Element elmOL, Element elmShipmentOL, Hashtable<String, String> htReqNo2ItemID){
		NodeList nlOL = elmOL.getChildNodes();
		Element elmOLExtn = null;
		Element elmOLNotes = null;
		Element elmOLItem = null;
		Element elmOLInstr = null;
		
		for (int i=0; i < nlOL.getLength(); i++){
			Node elmOLChild = nlOL.item(i);
			if (elmOLChild.getNodeType() == Node.ELEMENT_NODE){
				if (elmOLChild.getNodeName().equalsIgnoreCase("Extn")){
					elmOLExtn = (Element) elmOLChild;
				}
				else if (elmOLChild.getNodeName().equalsIgnoreCase("Notes")){
					elmOLNotes = (Element) elmOLChild;
				}
				else if (elmOLChild.getNodeName().equalsIgnoreCase("Item")){
					elmOLItem = (Element) elmOLChild;
				}
				else if (elmOLChild.getNodeName().equalsIgnoreCase("Instructions")){
					elmOLInstr = (Element) elmOLChild;
				}
			}
		}
		
		Element elmRoot = docNFESResourceRequest.getDocumentElement();
		Element elmFillDtl = docNFESResourceRequest.createElementNS(
								NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:FillDetail");
		elmRoot.appendChild(elmFillDtl);
		
		
		Element elmCacheIssue = docNFESResourceRequest.createElement("CacheIssue");
		elmFillDtl.appendChild(elmCacheIssue);
		
		Element elmCacheIssueNo = docNFESResourceRequest.createElement("CacheIssueNumber");
		elmCacheIssue.appendChild(elmCacheIssueNo);
		elmCacheIssueNo.setTextContent(elmOL.getAttribute("OrderNo"));
		Element elmCacheIssueCreateDateTime = docNFESResourceRequest.createElement("CacheIssueCreateDateTime");
		elmCacheIssue.appendChild(elmCacheIssueCreateDateTime);
		elmCacheIssueCreateDateTime.setTextContent(elmOL.getAttribute("OrderCreatets"));
		
		String sysNo = null;
		if (elmOLExtn != null){
			sysNo = elmOLExtn.getAttribute("ExtnSystemNo");
		}
		
		// Changing the logic
		// 08/03 Logic: If System Number is present, then send CacheRequestCode element
		// New Logic: We need to send CacheRequestCode if 'group of items' flag is enabled. This flag is
		// present in ROSS only and not in ICBSR. This flag indicates that it will be filled by
		// its subordinate items, e.g radio kit. Also the subordinate components cannot be backordered
		// or forwarded, but it can be substituted or consolidated. So, the logic will be
		// if ExtnRequestNo has a dot in it, then send CacheRequestCode. Also, ROSS checks this element
		// only if the 'group of items' flags is enabled and in all other scenarios it ignores it. So, we
		// will be sending CacheRequestCode element in all the cases except the base request no (with
		// out a dot)
		String reqNo = elmOLExtn.getAttribute("ExtnRequestNo");
		String baseReqNo = elmOLExtn.getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
		boolean addCacheReqCode = false;
		if (baseReqNo.equalsIgnoreCase(reqNo)){
			addCacheReqCode = false;
		}
		else if (reqNo.indexOf(".") == -1){
			addCacheReqCode = false;
		}
		else if ((reqNo.indexOf(".") != -1) && (!baseReqNo.equalsIgnoreCase(reqNo))){
			// Get base req no item id. If the base req no item id is 004390, then send CacheRequestCode
			if (htReqNo2ItemID != null && (htReqNo2ItemID.size() > 0)){
				String itemID = htReqNo2ItemID.get(baseReqNo);
				if (itemID != null && itemID.trim().length() > 0){
					if (itemID.equalsIgnoreCase("004390")){
						addCacheReqCode = true;
					}
					else {
						addCacheReqCode = false;
					}
				}
			}
		}
		
		if (addCacheReqCode){
			Element elmCacheReqCode = docNFESResourceRequest.createElement("CacheRequestCode");
			elmFillDtl.appendChild(elmCacheReqCode);
			Element elmCatalogID = docNFESResourceRequest.createElement("CatalogID");
			elmCacheReqCode.appendChild(elmCatalogID);
			elmCatalogID.setTextContent(reqNo.substring(0, reqNo.indexOf("-")));
			
			Element elmSeqNo = docNFESResourceRequest.createElement("SequenceNumber");
			elmCacheReqCode.appendChild(elmSeqNo);
			elmSeqNo.setTextContent(reqNo.substring(reqNo.indexOf("-")+1, reqNo.length()));
		}

		Element elmCatalogItem = docNFESResourceRequest.createElement("CatalogItem");
		elmFillDtl.appendChild(elmCatalogItem);
		Element elmCatalogType = docNFESResourceRequest.createElement("CatalogType");
		elmCatalogItem.appendChild(elmCatalogType);
		elmCatalogType.setTextContent("NWCG");

		String itemShortDesc = elmOLItem.getAttribute("ItemShortDesc");
		String itemID = elmOLItem.getAttribute("ItemID");
		if (itemShortDesc != null && itemShortDesc.length() > 0){
			Element elmCatalogItemName = docNFESResourceRequest.createElement("CatalogItemName");
			elmCatalogItem.appendChild(elmCatalogItemName);
			elmCatalogItemName.setTextContent(itemShortDesc);
		}
		else {
			Element elmCatalogItemCode = docNFESResourceRequest.createElement("CatalogItemCode");
			elmCatalogItem.appendChild(elmCatalogItemCode);
			elmCatalogItemCode.setTextContent(itemID);
		}		
		
		String shippedQty = "0";
		String edd = "";
		String ead = "";
		Vector<String> vecSerialNos = new Vector<String>();
		
		if (elmShipmentOL != null){
			shippedQty = elmShipmentOL.getAttribute("ActualQuantity");
			if (shippedQty.indexOf(".") != -1){
				shippedQty = shippedQty.substring(0, shippedQty.indexOf("."));
			}
			
			edd = elmShipmentOL.getAttribute(NWCGConstants.EXTN_ESTIMATED_DEPART_DATE);
			ead = elmShipmentOL.getAttribute(NWCGConstants.EXTN_ESTIMATED_ARRIVAL_DATE);
			
			NodeList nlShipmentTags = elmShipmentOL.getElementsByTagName(NWCGConstants.SHIPMENT_TAG_SERIAL);
			if (nlShipmentTags != null && nlShipmentTags.getLength() > 0){
				for (int j=0; j < nlShipmentTags.getLength(); j++){
					vecSerialNos.add(((Element) nlShipmentTags.item(j)).getAttribute(NWCGConstants.SERIAL_NO));
				}
			}
			
		}
		
		// Populate Fill Quantity and Trackable information if line status
		// is greater than shipped status
		String olStatus = elmOL.getAttribute(NWCGConstants.MAX_LINE_STATUS);
		if (olStatus.indexOf(".") != -1){
			olStatus = olStatus.substring(0, olStatus.indexOf("."));
		}

		if (olStatus.compareTo("3700") < 0){
			shippedQty = "0";
		}
		Element elmFillQty = docNFESResourceRequest.createElement("FillQuantity");
		elmFillDtl.appendChild(elmFillQty);
		elmFillQty.setTextContent(shippedQty);

		Element elmUtfQty = docNFESResourceRequest.createElement("UtfQuantity");
		elmFillDtl.appendChild(elmUtfQty);
		String utfQty = elmOLExtn.getAttribute(NWCGConstants.EXTN_UTF_QTY);
		if (utfQty != null && utfQty.trim().length() > 0){
			if (utfQty.indexOf(".") != -1){
				utfQty = utfQty.substring(0, utfQty.indexOf("."));
			}
		}
		else {
			utfQty = "0";
		}
		elmUtfQty.setTextContent(utfQty);
		
		Element elmBackOrderQty = docNFESResourceRequest.createElement("BackorderQuantity");
		elmFillDtl.appendChild(elmBackOrderQty);
		String boQty = elmOLExtn.getAttribute("ExtnBackorderedQty");
		if (boQty != null && boQty.trim().length() > 0){
			if (boQty.indexOf(".") != -1){
				boQty = boQty.substring(0, boQty.indexOf("."));
			}
		}
		else {
			boQty = "0";
		}
		elmBackOrderQty.setTextContent(boQty);

		Element elmFwdQty = docNFESResourceRequest.createElement("ForwardQuantity");
		elmFillDtl.appendChild(elmFwdQty);
		String fwdQty = elmOLExtn.getAttribute("ExtnFwdQty");
		if (fwdQty != null && fwdQty.trim().length() > 0){
			if (fwdQty.indexOf(".") != -1){
				fwdQty = fwdQty.substring(0, fwdQty.indexOf("."));
			}
		}
		else {
			fwdQty = "0";
		}
		elmFwdQty.setTextContent(fwdQty);

		String olNotes = getLatestNote(elmOLNotes);
		if (olNotes != null && olNotes.trim().length() > 0){
			Element elmNote = docNFESResourceRequest.createElement("UserDocumentation");
			elmFillDtl.appendChild(elmNote);
			elmNote.setTextContent(olNotes);
		}
		
		if (olStatus.compareTo("3700") >= 0){
			if (vecSerialNos != null && vecSerialNos.size() > 0){
				for (int i=0; i < vecSerialNos.size(); i++){
					Element elmSerialNo = docNFESResourceRequest.createElement("TrackableId");
					elmFillDtl.appendChild(elmSerialNo);
					elmSerialNo.setTextContent(vecSerialNos.get(i));
				}
			}
		}
		
		if (sysNo != null && sysNo.trim().length() > 0){
			Element elmSysNo = docNFESResourceRequest.createElement("SystemNumber");
			elmFillDtl.appendChild(elmSysNo);
			elmSysNo.setTextContent(sysNo);
		}

		if (edd.trim().length() > 0){
			Element elmMobilizationETD = docNFESResourceRequest.createElement("MobilizationETD");
			elmFillDtl.appendChild(elmMobilizationETD);
			elmMobilizationETD.setTextContent(edd);
		}

		if (ead.trim().length() > 0){
			Element elmMobilizationETA = docNFESResourceRequest.createElement("MobilizationETA");
			elmFillDtl.appendChild(elmMobilizationETA);
			elmMobilizationETA.setTextContent(ead);
		}
		
		if (elmOLInstr != null){
			setSpecialNeedVal(elmOLInstr, itemID);
		}
		return true;
	}
	
	/**
	 * Expected OrderLine OL
	 * 		<OrderLine OrderLineKey="" MaxLineStatus="" OrderNo="">
				<Notes NumberOfNotes="">
					<Note />
				</Notes>
				<Instructions>
					<Instruction InstructionType="" InstructionText=""/>
				</Instructions>
			</OrderLine>		
	 * Note: Other elements/attribtues can be present, but this class will not be using it.
	 * @param elmOL
	 * @return
	 */
	public boolean createConsolidationElement(Element elmOL){
		Element elmDocUpdtNFES = docNFESResourceRequest.getDocumentElement();
		Element elmConsDtl = docNFESResourceRequest.createElementNS(
									NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ConsolidationDetail");
		elmDocUpdtNFES.appendChild(elmConsDtl);

		NodeList nlOL = elmOL.getChildNodes();
		Element elmOLNotes = null;
		Element elmOLInstrs = null;
		
		boolean obtNotesChild = false;
		for (int i=0; i < nlOL.getLength() && !obtNotesChild; i++){
			Node elmOLChild = nlOL.item(i);
			if (elmOLChild.getNodeType() == Node.ELEMENT_NODE){
				String nodeName = elmOLChild.getNodeName();
				if (nodeName.equalsIgnoreCase("Notes")){
					elmOLNotes = (Element) elmOLChild;
					obtNotesChild = true;
				}
				else if (nodeName.equalsIgnoreCase("Instructions")){
					elmOLInstrs = (Element) elmOLChild;
				}
			}
		}
		
		Element elmNote = docNFESResourceRequest.createElement("UserDocumentation");
		elmConsDtl.appendChild(elmNote);
		elmNote.setTextContent(getLatestNote(elmOLNotes));
		if (elmOLInstrs != null){
			setSpecialNeedVal(elmOLInstrs, "");
		}
		return true;
	}
	
	/**
	 * This method will populate the address element based on EXTN_NAV_FLAG at
	 * Order/Extn element
	 * @param elmOrderExtn
	 * @param personInfoShipTo
	 * @return
	 */
	public boolean populateAddressDtls(Element elmOrderExtn, Element elmPersonInfoShipTo){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::populateAddressDtls, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}
		
		Element elmRoot = docNFESResourceRequest.getDocumentElement();
		String addrType = elmOrderExtn.getAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR);
		if (addrType == null || addrType.trim().length() < 1){
			System.out.println("NWCGNFESResourceRequest::populateAddressDtls, " +
			   					"Address Type is invalid - Either it is NULL or it is empty.");
			return false;
		}
		
		if (addrType.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)){
			Element elmWillPickUp = docNFESResourceRequest.createElementNS(
										NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:WillPickUpInfo");
			elmRoot.appendChild(elmWillPickUp);
			
			Element elmPickUpContactName = docNFESResourceRequest.createElement("PickUpContactName");
			elmPickUpContactName.setTextContent(elmOrderExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME));
			elmWillPickUp.appendChild(elmPickUpContactName);
			Element elmPickUpContactInfo = docNFESResourceRequest.createElement("PickUpContactInfo");
			elmPickUpContactInfo.setTextContent(elmOrderExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO));
			elmWillPickUp.appendChild(elmPickUpContactInfo);
			Element elmPickUpDateTime = docNFESResourceRequest.createElement("PickUpDateTime");
			elmPickUpDateTime.setTextContent(elmOrderExtn.getAttribute(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR));
			elmWillPickUp.appendChild(elmPickUpDateTime);			
		}
		else if (addrType.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)){
			Element elmShippingInstr = docNFESResourceRequest.createElementNS(
										NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ShippingInstructions");
			elmRoot.appendChild(elmShippingInstr);
			
			Element elmShippingInstrValue = docNFESResourceRequest.createElement("ShippingInstructions");
			elmShippingInstrValue.setTextContent(elmOrderExtn.getAttribute("ExtnShippingInstructions"));
			elmShippingInstr.appendChild(elmShippingInstrValue);
			Element elmShippingInstrCity = docNFESResourceRequest.createElement("City");
			elmShippingInstrCity.setTextContent(elmOrderExtn.getAttribute("ExtnShipInstrCity"));
			elmShippingInstr.appendChild(elmShippingInstrCity);
			Element elmShippingInstrState = docNFESResourceRequest.createElement("State");
			elmShippingInstrState.setTextContent(elmOrderExtn.getAttribute("ExtnShipInstrState"));
			elmShippingInstr.appendChild(elmShippingInstrState);			
		}
		else if (addrType.equalsIgnoreCase(NWCGConstants.SHIPPING_ADDRESS)){
			Element elmShippingAddr = docNFESResourceRequest.createElementNS(
										NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ShippingAddress");
			elmRoot.appendChild(elmShippingAddr);
			
			Element elmName = docNFESResourceRequest.createElement("Name");
			elmName.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.FIRST_NAME)
					 				+ " " + elmPersonInfoShipTo.getAttribute(NWCGConstants.LAST_NAME));
			elmShippingAddr.appendChild(elmName);

			Element elmType = docNFESResourceRequest.createElement("Type");
			elmType.setTextContent("Shipping");
			elmShippingAddr.appendChild(elmType);

			Element elmLine1 = docNFESResourceRequest.createElement("Line1");
			elmLine1.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.ADDRESS_LINE_1));
			elmShippingAddr.appendChild(elmLine1);

			Element elmLine2 = docNFESResourceRequest.createElement("Line2");
			elmLine2.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.ADDRESS_LINE_2));
			elmShippingAddr.appendChild(elmLine2);

			Element elmCity = docNFESResourceRequest.createElement("City");
			elmCity.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.CITY));
			elmShippingAddr.appendChild(elmCity);

			Element elmState = docNFESResourceRequest.createElement("State");
			elmState.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.STATE));
			elmShippingAddr.appendChild(elmState);

			Element elmZipCode = docNFESResourceRequest.createElement("ZipCode");
			elmZipCode.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.ZIP_CODE));
			elmShippingAddr.appendChild(elmZipCode);

			Element elmCountryCode = docNFESResourceRequest.createElement("CountryCode");
			elmCountryCode.setTextContent(elmPersonInfoShipTo.getAttribute(NWCGConstants.COUNTRY));
			elmShippingAddr.appendChild(elmCountryCode);
			
			String unitID = elmPersonInfoShipTo.getAttribute(NWCGConstants.ALTERNATE_EMAIL_ID);
			if (unitID != null && (unitID.indexOf("-")  != -1)){
				Element elmUnitID = docNFESResourceRequest.createElement("UnitID");
				elmShippingAddr.appendChild(elmUnitID);
				Element elmUnitIDPrefix = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_PREFIX);
				elmUnitIDPrefix.setTextContent(unitID.substring(0, unitID.indexOf("-")));
				elmUnitID.appendChild(elmUnitIDPrefix);
				Element elmUnitIDSuffix = docNFESResourceRequest.createElement(NWCGAAConstants.UNIT_ID_SUFFIX);
				elmUnitIDSuffix.setTextContent(unitID.substring(unitID.indexOf("-")+1));
				elmUnitID.appendChild(elmUnitIDSuffix);
			}			
		}
		else {
			System.out.println("NWCGNFESResourceRequest::populateAddressDtls, " +
								"Invalid Order/ExtnNavInfo : " + addrType);
		}
		
		return true;
	}
	
	
	/**
	 * This method will populate shipping contact name and shipping contact phone
	 * @param shippingContactName
	 * @param shippingContactPhone
	 * @return
	 */
	public boolean populateShippingContactDtls(String shippingContactName, String shippingContactPhone){
		if (docNFESResourceRequest == null){
			System.out.println("NWCGNFESResourceRequest::populateShippingContactDtls, " +
							   "Instantiate NWCGNFESResourceRequest with root node");
			return false;
		}

		Element elmNFESResReqDocRoot = docNFESResourceRequest.getDocumentElement();
		Element elmShippingContactName = docNFESResourceRequest.createElementNS(
				NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ShippingContactName");
		elmShippingContactName.setTextContent(shippingContactName);
		elmNFESResReqDocRoot.appendChild(elmShippingContactName);
		Element elmShippingContactPhone = docNFESResourceRequest.createElementNS(
				NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:ShippingContactPhone");
		elmShippingContactPhone.setTextContent(shippingContactPhone);
		elmNFESResReqDocRoot.appendChild(elmShippingContactPhone);
		return true;
	}
	
	/**
	 * This method will get the latest note (NoteText) from the notes element
	 * @param elmNotes
	 * @return
	 */
	public String getLatestNote(Element elmNotes){
		String latestNote = "";
		String noOfNotes = elmNotes.getAttribute("NumberOfNotes");
		NodeList nlNoteList = elmNotes.getElementsByTagName("Note");
		if (nlNoteList == null || nlNoteList.getLength() < 1){
			if (cancelNote != null){
				return cancelNote;
			}
			else {
				return latestNote;
			}
		}
		
		boolean obtLatestNote = false;
		for (int i=0; i < nlNoteList.getLength() && !obtLatestNote; i++){
			Element elmNote = (Element) nlNoteList.item(i);
			if (elmNote.getAttribute("SequenceNo").equalsIgnoreCase(noOfNotes)){
				obtLatestNote = true;
				latestNote = elmNote.getAttribute("NoteText");
				if (latestNote == null){
					latestNote = "";
				}
			}
		}
		if (cancelNote != null){
			latestNote.concat(";" + cancelNote);
		}
		
		return latestNote;
	}
	
	/**
	 * This method will get the Special Instructions due to ROSS notes
	 * and will add it to private value of SpecialNeedVal
	 * Since we are appending ';' after each instruction, we are checking if
	 * the existing specialNeedVal already has instructions, if so add a semi-colon
	 * and then add the new instruction
	 * In the case of consolidation, UpdateRequest message will have only one
	 * element, so SpecialNeeds will be for that item. In that scenario, this method
	 * does not expect non-null item id (whose size is greater than 1). So, we are
	 * adding just the InstructionText.
	 * @param elmOLInstr
	 * @param itemID
	 */
	private void setSpecialNeedVal(Element elmOLInstrs, String itemID){
		NodeList nlInstr = elmOLInstrs.getElementsByTagName("Instruction");
		if (nlInstr != null && nlInstr.getLength() > 0){
			for (int i=0; i < nlInstr.getLength(); i++){
				Element elmInstr = (Element) nlInstr.item(i);
				String instrType = elmInstr.getAttribute("InstructionType");
				if (instrType.equalsIgnoreCase("ROSS Notes")){
					if (specialNeedVal.length() > 0){
						specialNeedVal.append(";");
					}
					if (itemID != null && itemID.length() > 1){
						specialNeedVal.append(itemID + " - " + elmInstr.getAttribute("InstructionText"));
					}
					else {
						specialNeedVal.append(elmInstr.getAttribute("InstructionText"));
					}
				}
			}
		}
	}
	
	/**
	 * This method will set SpecialNeed element from the privately
	 * defined specialNeedVal
	 */
	public void populateSpecialNeeds(){
		// append child only when special need exists
		if(specialNeedVal.length() > 0)
		{
			Element elmSpecialNeed = docNFESResourceRequest.createElementNS(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:SpecialNeeds");
			docNFESResourceRequest.getDocumentElement().appendChild(elmSpecialNeed);
			if (specialNeedVal.length() > 1500){
				elmSpecialNeed.setTextContent(specialNeedVal.substring(0, 1499));
			}
			else {
				elmSpecialNeed.setTextContent(specialNeedVal.toString());
			}
		}
	}
	public void setFailureResponseStatus(String strCode, String strDesc)
	{
		NodeList nlResponseStatus = docNFESResourceRequest.getElementsByTagNameNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE,"ResponseStatus");
		if(nlResponseStatus.getLength() > 0)
		{
			NodeList nlReturnCode = ((Element)nlResponseStatus.item(0)).getElementsByTagName("ReturnCode");
			if(nlReturnCode.getLength() > 0)
			{
				nlReturnCode.item(0).setTextContent(NWCGAAConstants.ROSS_RET_FAILURE_VALUE);
			}
			NodeList nlCode = ((Element)nlResponseStatus.item(0)).getElementsByTagName("Code");
			if(nlCode.getLength() > 0)
			{
				nlCode.item(0).setTextContent(strCode);
			}
			NodeList nlSeverity = ((Element)nlResponseStatus.item(0)).getElementsByTagName("Severity");
			if(nlSeverity.getLength() > 0)
			{
				nlSeverity.item(0).setTextContent(NWCGAAConstants.SEVERITY_ERROR);
			}
			NodeList nlDescription = ((Element)nlResponseStatus.item(0)).getElementsByTagName("Description");
			if(nlDescription.getLength() > 0)
			{
				nlDescription.item(0).setTextContent(strDesc);
			}
		}
	}
	
	public void setCancelledNote(String note){
		cancelNote = note;
	}
	
	public static Document getLatestIncidentInfo(YFSEnvironment env, String incidentNo, String incidentYear)
	{
		Document latestIncidentInfoDoc = null;
		try
		{
			Document incidentIdDoc = XMLUtil.getDocument("<NWCGIncidentOrder IncidentNo=\"" + incidentNo + "\" Year=\"" + incidentYear + "\" />");
			Document idOutDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, incidentIdDoc);
			String incidentID = idOutDoc.getDocumentElement().getAttribute("IncidentId");
			
			if(incidentID != null && incidentID.trim().length() > 0)
			{
				String orderByElement = "<OrderBy><Attribute Name=\"Createts\" Desc=\"Y\" /></OrderBy>";			
				Document incidentNoDoc = XMLUtil.getDocument("<NWCGIncidentOrder IgnoreOrdering=\"N\" IncidentId=\"" + incidentID + "\">" + orderByElement + "</NWCGIncidentOrder>");
				latestIncidentInfoDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_LIST_SVC, incidentNoDoc);
			}
			else
			{
				String errorDescription = "Could not find IncidentID corresponding to [IncidentNo,Year]=[" + incidentNo + "," + incidentYear + "] therefore using existing IncidentNo.";
				CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, errorDescription, null, null, null);
			}
		}
		catch(ParserConfigurationException pce){
			System.out.println("NWCGNFESResourceRequest:getLatestIncidentNo, " + "Parser Configuration Exception : " + pce.getMessage());
		}
		catch(Exception e){
			System.out.println("NWCGNFESResourceRequest:getLatestIncidentNo, " + "Exception : " + e.getMessage());
		}
		return latestIncidentInfoDoc;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
