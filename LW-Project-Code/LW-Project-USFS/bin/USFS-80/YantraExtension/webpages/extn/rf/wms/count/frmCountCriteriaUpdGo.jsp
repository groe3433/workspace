<%@ include file="/yfc/rfutil.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>

<% 
	String formName = "/frmCountEntryForCaseOrSKU"; 
	String sFocusField = "txtBarCodeData"; 
	String entitybase= getParameter("entitybase") ;
	String forwardPage= "";	
	String errorDesc = null;
	String sLocationId = null;
	String sparentLPN = null;
	String sCaseId = null;
	String sItemId = "";
	String sUom = "";
	String sDisplayItemId = "";
	String sDisplayItemDesc = "";
	String sEnterpriseCode = "";
	//String sInvOrg = getParameter("xml:/Enterprise/@EnterpriseCode");
	String sRecentPalletId="";
	String sRecentCaseId="";
	String sSuggestedCase="";
	String sSuggestedPallet="";

	String sTaskId = getParameter("xml:/CountTask/@TaskId");
	if(!isVoid(sTaskId)){
		YFCElement taskElem = getTempQ().getDocumentElement().getChildElement("Task");
		if(!isVoid(taskElem)){
			request.setAttribute("xml:/CountTask/@TaskId",taskElem.getAttribute("TaskId"));
		}
	} else {
		request.setAttribute("xml:/CountTask/@TaskId", sTaskId);
	}
 
	String sLastScannedLPN=getParameter("xml:/RecentLPN/@LastScannedLPN");
	YFCDocument yinTempdoc = getTempQ() ;
	YFCElement parentLPNElem = getStoredElement(yinTempdoc,"ParentLPN",sLastScannedLPN);  
	if(parentLPNElem != null) {
		sRecentPalletId = parentLPNElem.getAttribute("PalletId");	
		sRecentCaseId = parentLPNElem.getAttribute("CaseId");	
	}

	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);

	YFCElement taskElem= (YFCElement)((getTempQ()).getElementsByTagName("Task")).item(0);	
	if(taskElem != null) {
		sEnterpriseCode = taskElem.getAttribute("EnterpriseCode");
		sItemId = taskElem.getAttribute("ItemId");
		sUom = taskElem.getAttribute("UnitOfMeasure");
		sSuggestedPallet = taskElem.getAttribute("SourcePalletId");
		sSuggestedCase = taskElem.getAttribute("SourceCaseId");
	}
	if(isVoid(sEnterpriseCode)){
		sEnterpriseCode = getParameter("xml:/Enterprise/@EnterpriseCode");
	}

	request.setAttribute("RecordCountResult", countResult);
	YFCElement countLocation= (YFCElement)((getTempQ()).getElementsByTagName("Location")).item(0);
	if(countLocation != null){
		sLocationId = countLocation.getAttribute("LocationId");
	}
	
	if( sLocationId==null || YFCObject.isVoid(sLocationId)) {
		errorDesc="Mobile_Location_Scanning_Is_Mandatory";
	}

	if(errorDesc == null) {
		
		if(!YFCObject.isVoid(sSuggestedPallet) || !YFCObject.isVoid(sSuggestedCase)) {
			if(YFCObject.isVoid(sLastScannedLPN)) {
				errorDesc="Mobile_LPN_Scanning_Is_Mandatory";
			}
		}

		if(errorDesc == null) {
			request.setAttribute("xml:/Location/@LocationId",sLocationId);
			request.setAttribute("xml:/ParentLPN/@PalletId",sRecentPalletId);
			request.setAttribute("xml:/ParentLPN/@CaseId",sRecentCaseId);
			request.setAttribute("xml:/RecentLPN/@LastScannedLPN",sLastScannedLPN);
			request.setAttribute("xml:/RecordCountResult/@TaskType",countResult.getAttribute("TaskType")); 

			YFCDocument ydoc = getForm(formName);

			/*CR 63503: To remove "Pallet" literal on Item/Case screen when Pallet ID has not been scanned on previous screen*/
			if (isVoid(resolveValue("xml:/ParentLPN/@PalletId"))) {
				YFCElement	lblPallet	= getField(ydoc,"lblPalletId");
				if(!isVoid(lblPallet)) {
					lblPallet.setAttribute("type","hidden");
					lblPallet.setAttribute("subtype","Hidden");
				}
				YFCElement	txtPallet	= getField(ydoc,"txtPalletId");
				if(!isVoid(txtPallet)) {						
					txtPallet.setAttribute("type","hidden");
					txtPallet.setAttribute("subtype","Hidden");
				}
			}

			/*CR 63503: To remove Item ID label and value from the first screen in case of Location Count*/
			if (isVoid(resolveValue("xml:/TaskList/Task/Inventory/@ItemId"))) {
				boolean hasItemClassification = ((!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification1"))) || (!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification2"))) || (!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification3"))));
				if(!hasItemClassification){
					YFCElement	lblItemId = getField(ydoc,"lblItemId");
					if(!isVoid(lblItemId)) {
						lblItemId.setAttribute("type","hidden");
						lblItemId.setAttribute("subtype","Hidden");
					}
					YFCElement txtItemId = getField(ydoc,"txtItemId");
					if(!isVoid(txtItemId)) {						
						txtItemId.setAttribute("type","hidden");
						txtItemId.setAttribute("subtype","Hidden");
					}
				}
			}
			
			if(!isVoid(sItemId) && !isVoid(sUom) && !isVoid(sEnterpriseCode)){
				//TODO call getNodeItemDetails here
				YFCDocument tempDocForItem = YFCDocument.parse("<Item ItemID=\"\" OrganizationCode=\"\" DisplayItemId=\"\" ><PrimaryInformation DisplayItemDescription=\"\"/></Item>");			
				YFCDocument itemDoc = YFCDocument.createDocument("Item");
				YFCElement itemInXML = itemDoc.getDocumentElement();
				itemInXML.setAttribute("ItemID",sItemId);
				itemInXML.setAttribute("UnitOfMeasure",sUom);
				itemInXML.setAttribute("OrganizationCode",sEnterpriseCode);
				itemInXML.setAttribute("Node", getValue("CurrentUser","xml:CurrentUser:/User/@Node"));				
				%>
					<yfc:callAPI apiName="getNodeItemDetails" inputElement='<%=itemInXML%>' 
					templateElement='<%=tempDocForItem.getDocumentElement()%>'/>
				<%
				errorDesc=checkForError() ;	
				if(errorDesc==null){
					sDisplayItemId = getValue("Item","xml:/Item/@DisplayItemId");
					sDisplayItemDesc = getValue("Item","xml:/Item/PrimaryInformation/@DisplayItemDescription");
				}
				if(!isVoid(sDisplayItemId)){
					// on 2nd, 3rd counts, display the item id on the next screen
					request.setAttribute("xml:/RecordCountResult/@BarCodeData",sDisplayItemId);
					getTempQ().getDocumentElement().getChildElement("RecordCountResult",true).setAttribute("NoOfItemsScanned","9999");
				}
			}
			if(!isVoid(sRecentCaseId)){
				YFCElement lblPallet=getField(ydoc,"lblPalletId");
				if(lblPallet != null){
					lblPallet.setAttribute("value","Case");	
				}

				YFCElement txtPallet=getField(ydoc,"txtPalletId");
				if(txtPallet != null){
					txtPallet.setAttribute("inputbinding","xml:/ParentLPN/@CaseId");
					txtPallet.setAttribute("outputbinding","xml:/ParentLPN/@CaseId");
				}
			}
			request.setAttribute("xml:/Inventory/@DisplayItemId",sDisplayItemId);
			request.setAttribute("xml:/Inventory/@DisplayItemDescription",sDisplayItemDesc);
			request.setAttribute("xml:/RecordCountResult/@TaskType",countResult.getAttribute("TaskType"));
			request.setAttribute("xml:/Enterprise/@EnterpriseCode", sEnterpriseCode); 
			out.println(sendForm(ydoc, sFocusField,true));
		}
	}

	if (errorDesc != null) {
		String errorXML = getErrorXML(errorDesc, "");
		%><%=errorXML%><%
	}
%>			
