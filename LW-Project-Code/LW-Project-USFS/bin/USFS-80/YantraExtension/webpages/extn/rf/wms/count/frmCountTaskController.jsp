<%@ include file="/yfc/rfutil.jspf" %>
<%@ include file="/rf/wms/count/count_include.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>

<% 
	String formName = "/frmCountCriteria" ;
	String errorDesc=null;
	String errorField="";
	String entitybase= getParameter("entitybase") ;
	String innerPanel= getParameter("CurrentInnerPanelID") ;
	String forwardPage="";
	String focusField="txtLocationId";
	String sPalletComplete = "Y";
	String sForceLocation = "Y";
	String sTrackCaseLPN = "Y";
	String sTrackPalletLPN = "Y";
	HashMap rootMap= new HashMap();
	HashMap taskMap= new HashMap();
	HashMap locMap= new HashMap();
	String sSuggestedCase="";
	String sSuggestedPallet="";
	String sSuggestedItem="";
	String sDisplayItemDesc="";
	String sEnterpriseCode="";
	String sLocationId="";
	String sTaskType="";

	boolean hasItemClassification = false;
	int NoOfItemsScanned=0;
	int NoOfCasesScanned=0;
	String sNode = getValue("CurrentUser","xml:CurrentUser:/User/@Node");
	String sUserId = resolveValue("xml:CurrentUser:/User/@Loginid");
	String sTaskId=getParameter("xml:/CountTask/@TaskId");
	String sPalletId=getParameter("xml:/CountTask/@PalletId");
	String sCaseId=getParameter("xml:/CountTask/@CaseId");
	String sLPNOrgCode=getParameter("xml:/CountTask/@OrganizationCode");
	String sOrgCode= resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode");
	String sTaskLocationId=getValue("TaskList","xml:/TaskList/Task/@SourceLocationId");
	String sScannedLocationId=getParameter("xml:/Location/@BarCodeData");
	if(sScannedLocationId == null) sScannedLocationId ="";
	if(sTaskId == null) sTaskId ="";
	if(sPalletId == null) sPalletId ="";
	if(sCaseId == null) sCaseId ="";
	if(sLPNOrgCode == null) sLPNOrgCode ="";

	if(equals("01",getValue("TaskList","xml:/TaskList/Task/TaskType/@SourceTaskSuggestion"))) {
		forwardPage= entitybase + "/" + "frmInitiateUserDrivenCount.jsp";
		forwardPage=checkExtension(forwardPage);
		%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
	}

	hasItemClassification = ((!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification1"))) || (!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification2"))) || (!isVoid(getValue("TaskList","xml:/TaskList/Task/@ItemClassification3"))));

	clearTempQ();
	YFCDocument recordDoc= YFCDocument.createDocument("RecordCountResult");
	YFCElement rootCountResult=recordDoc.getDocumentElement();
	YFCDocument taskDoc= YFCDocument.createDocument("Task");
	YFCElement rootTask=taskDoc.getDocumentElement();
	//accept task if in open status

	if(equals("1100",getValue("TaskList", "xml:/TaskList/Task/@TaskStatus"))){
		%><yfc:callAPI apiID='AP3' /> <%							
		errorDesc=checkForError() ;
	}	
	
	if (errorDesc == null) {
        rootMap.put("Node",sNode);
		rootMap.put("TaskId", getValue("TaskList","xml:/TaskList/Task/@TaskId"));
		rootMap.put("TaskKey", getValue("TaskList","xml:/TaskList/Task/@TaskKey"));
		rootMap.put("SummaryTaskId",getValue("TaskList","xml:/TaskList/Task/@ParentTaskId"));
		rootMap.put("CountRequestKey", getValue("TaskList","xml:/TaskList/Task/TaskReferences/@CountRequestKey"));
		rootMap.put("EnterpriseCode",getValue("TaskList","xml:/TaskList/Task/@EnterpriseKey"));
		rootMap.put("TaskType",getValue("TaskList","xml:/TaskList/Task/@TaskType"));
		rootMap.put("NoOfItemsScanned",String.valueOf(NoOfItemsScanned));
		rootMap.put("NoOfCasesScanned",String.valueOf(NoOfCasesScanned));
		rootMap.put("PalletComplete",sPalletComplete);
		
		taskMap.put("TaskId", getValue("TaskList","xml:/TaskList/Task/@TaskId"));	
	    taskMap.put("TaskKey",getValue("TaskList","xml:/TaskList/Task/@TaskKey"));	
		taskMap.put("TaskType",getValue("TaskList","xml:/TaskList/Task/@TaskType"));
		taskMap.put("OrganizationCode",getValue("TaskList","xml:/TaskList/Task/@OrganizationCode"));
		taskMap.put("EnterpriseCode",getValue("TaskList","xml:/TaskList/Task/@EnterpriseKey"));
		taskMap.put("SourceLocationId", getValue("TaskList","xml:/TaskList/Task/@SourceLocationId"));	
		taskMap.put("SourceZoneId", getValue("TaskList","xml:/TaskList/Task/@SourceZoneId"));	
		taskMap.put("SourceCaseId", getValue("TaskList","xml:/TaskList/Task/Inventory/@SourceCaseId"));	
		taskMap.put("SourcePalletId", getValue("TaskList","xml:/TaskList/Task/Inventory/@SourcePalletId"));	
		taskMap.put("ItemId",getValue("TaskList","xml:/TaskList/Task/Inventory/@ItemId"));		
		taskMap.put("ItemClassification1",getValue("TaskList","xml:/TaskList/Task/@ItemClassification1"));
		taskMap.put("ItemClassification2",getValue("TaskList","xml:/TaskList/Task/@ItemClassification2"));
		taskMap.put("ItemClassification3",getValue("TaskList","xml:/TaskList/Task/@ItemClassification3"));
		taskMap.put("UnitOfMeasure",getValue("TaskList","xml:/TaskList/Task/Inventory/@UnitOfMeasure"));
		taskMap.put("InventoryStatus",getValue("TaskList","xml:/TaskList/Task/Inventory/@InventoryStatus"));
		taskMap.put("ProductClass",getValue("TaskList","xml:/TaskList/Task/Inventory/@ProductClass"));
		taskMap.put("SerialNo",getValue("TaskList","xml:/TaskList/Task/Inventory/@SerialNo"));
		taskMap.put("TagNumber",getValue("TaskList","xml:/TaskList/Task/Inventory/@TagNumber"));
		taskMap.put("TaskStatus",getValue("TaskList","xml:/TaskList/Task/@TaskStatus"));
		taskMap.put("SourceTaskSuggestion",getValue("TaskList","xml:/TaskList/Task/TaskType/@SourceTaskSuggestion"));	
		sLocationId = getValue("TaskList","xml:/TaskList/Task/@SourceLocationId");
		sSuggestedCase= getValue("TaskList","xml:/TaskList/Task/Inventory/@SourceCaseId");
		sSuggestedPallet= getValue("TaskList","xml:/TaskList/Task/Inventory/@SourcePalletId");
		sSuggestedItem= getValue("TaskList","xml:/TaskList/Task/Inventory/@ItemId");		
		sTaskType = getValue("TaskList","xml:/TaskList/Task/@TaskType");
		
		try {
			addToTempQ("Task", getValue("TaskList","xml:/TaskList/Task/@TaskKey") ,taskMap,false);
		}catch (Exception e) {
			try {
				replaceInTempQ("Task", getValue("TaskList","xml:/TaskList/Task/@TaskKey") ,taskMap);
			}catch(Exception ee){
				errorDesc="Mobile_Duplicate_Values";
			}			   
		}

		request.setAttribute("xml:/ParentLPN1/@CaseId", sCaseId);
		request.setAttribute("xml:/ParentLPN1/@PalletId", sPalletId);
		request.setAttribute("xml:/ParentLPN1/@BarCodedata", "");						
		request.setAttribute("xml:/ParentLPN/@CaseId", sCaseId);
		request.setAttribute("xml:/ParentLPN/@PalletId", sPalletId);
		request.setAttribute("xml:/ParentLPN/@BarCodedata", "");

		YFCDocument countDoc = YFCDocument.createDocument("CountResult");
		YFCElement countInXML = countDoc.getDocumentElement();
		countInXML.setAttribute("CountRequestKey",getValue("TaskList","xml:/TaskList/Task/TaskReferences/@CountRequestKey"));
		countInXML.setAttribute("Node",sNode);
		countInXML.setAttribute("LatestSummaryTask","Y");				
					
		%><yfc:callAPI apiName="getCountResultList" inputElement='<%=countInXML%>' /><%
		errorDesc=checkForError();
		
		if(errorDesc == null) {
			YFCElement countOutElem = (YFCElement)request.getAttribute("CountResultList");
			if(countOutElem != null) {
				int dCountIter = countOutElem.getIntAttribute("CountIteration");
				rootMap.put("CountIteration",String.valueOf(dCountIter));
			}
		}

		if (errorDesc == null) {		
			YFCDocument tempDocForZone = YFCDocument.parse("<Zone/>");
			YFCDocument zoneDoc = YFCDocument.createDocument("Zone");
			YFCElement zoneInXML = zoneDoc.getDocumentElement();
			zoneInXML.setAttribute("ZoneId",getValue("TaskList","xml:/TaskList/Task/@SourceZoneId"));
			zoneInXML.setAttribute("Node",sNode);
			
			%><yfc:callAPI apiName="getZoneDetails" inputElement='<%=zoneInXML%>' 
			templateElement='<%=tempDocForZone.getDocumentElement()%>' /><%
			errorDesc=checkForError() ;		

			if(errorDesc == null) {
				YFCElement zoneOutElem = (YFCElement)request.getAttribute("Zone");
				if(zoneOutElem != null) {
					sForceLocation  = zoneOutElem.getAttribute("ForceLocnScanOnVisit");
					sTrackCaseLPN = zoneOutElem.getAttribute("TrackCaseLPN");
					sTrackPalletLPN = zoneOutElem.getAttribute("TrackPalletLPN");
				}
			}
		}
		rootMap.put("ForceLocationScan",sForceLocation);
		rootMap.put("TrackCaseLPN",sTrackCaseLPN);
		rootMap.put("TrackPalletLPN",sTrackPalletLPN);
		
		if (errorDesc == null) {
			try {
				addToTempQ("RecordCountResult", "1" ,rootMap, false);				
			}catch (Exception e) {
				try {
					replaceInTempQ("RecordCountResult", "1" ,rootMap);
				}catch(Exception ee){
					errorDesc="Mobile_Duplicate_Values";
				}			
			}
		}

		if(errorDesc==null){
			errorDesc = getParameter("xml:/CountTask/@ErrorDesc");
			errorField = "txtBarCodeData";
			if(isVoid(errorDesc)){ 
				errorDesc = null;
			}
		}
		
		if(errorDesc == null) {
			YFCDocument ydoc = getForm(formName);
			if(equals("01",getValue("TaskList","xml:/TaskList/Task/TaskType/@SourceTaskSuggestion"))){
				YFCElement skipButton=getField(ydoc,"Skip");
				if(skipButton != null){
					skipButton.setAttribute("value",getI18N("Tasks"));				
					skipButton.setAttribute("url","/console/rfcount.ppc?action=frmInitiateUserDrivenCount");
				}
				YFCElement emptyButton1=getField(ydoc,"Empty");
				if(emptyButton1 != null){
					emptyButton1.setAttribute("type","hidden");				
				}
			}else if(equals("1300",getValue("TaskList","xml:/TaskList/Task/@TaskStatus"))){
				YFCElement emptyButton=getField(ydoc,"Empty");
				if(emptyButton != null){
					emptyButton.setAttribute("value",getI18N("Complete"));				
					// emptyButton.setAttribute("col","7");	
					// CR 492 - button position/size fix
					emptyButton.setAttribute("col","5");
					emptyButton.setAttribute("size","9");
				}
				YFCElement skipButton=getField(ydoc,"Skip");
				if(skipButton != null){
					skipButton.setAttribute("type","hidden");				
					skipButton.setAttribute("subtype","Hidden");				
				}
			}

			if(hasItemClassification){
				setItemClassificationBinding(ydoc);	
			}

			if(equals("N", sForceLocation)) {
				YFCElement locationScanElem=getField(ydoc,"txtLocationId");
				if(locationScanElem!=null){
					locationScanElem.setAttribute("validate",false);		
					locationScanElem.setAttribute("type","text");
					locationScanElem.setAttribute("subtype","ProtectedText");	
					locationScanElem.setAttribute("value",sLocationId);									
					locMap.put("LocationId",sLocationId);			
					try{					
						addToTempQ("Location", "1" ,locMap,false);	
					}catch(Exception e){					
						errorDesc="Mobile_Duplicate_Value";
					}
					request.setAttribute("xml:/Location1/@LocationId", sLocationId); 
					request.setAttribute("xml:/Location1/@BarCodeData", sLocationId); 
					focusField="txtPalletId";
				}				
                                getTempQ().getDocumentElement().getChildElement("Location",true).setAttribute("LocationId",sLocationId);
			}else if(!isVoid(sScannedLocationId)) {
				if(sScannedLocationId.equals(sTaskLocationId)){
					YFCElement locationScanElem=getField(ydoc,"txtLocationId");
					/* CR 492 -- resolve issue for
					          -- locationID field being pre-populated and not scannable
					if(locationScanElem!=null){
						locationScanElem.setAttribute("validate",false);		
						locationScanElem.setAttribute("type","text");
						locationScanElem.setAttribute("subtype","ProtectedText");	
						locationScanElem.setAttribute("value",sLocationId);
						locMap.put("LocationId",sLocationId);			
						try{					
							addToTempQ("Location", "1" ,locMap,false);	
						}catch(Exception e){					
							errorDesc="Mobile_Duplicate_Value";
						}
						request.setAttribute("xml:/Location1/@LocationId", sLocationId); 
						request.setAttribute("xml:/Location1/@BarCodeData", sLocationId); 
						focusField="txtPalletId";
					}
					*/
					getTempQ().getDocumentElement().getChildElement("Location",true).setAttribute("LocationId",sLocationId);
				}
			}
			if(equals("txtLocationId",focusField)){
				paintCaseOrPallet(ydoc,sTrackPalletLPN,sTrackCaseLPN);
			}else{
				focusField = paintCaseOrPallet(ydoc,sTrackPalletLPN,sTrackCaseLPN);
			}

			/* Added to remove Item ID---CR63503*/
			if(YFCObject.isVoid(sSuggestedItem) && !hasItemClassification){
				YFCElement lblItemId = getField(ydoc,"lblItemId");
				if(!isVoid(lblItemId)){
					lblItemId.setAttribute("type","hidden");
					lblItemId.setAttribute("subtype","Hidden");
				}				
				YFCElement txtItemId = getField(ydoc,"txtItemId");
				if(!isVoid(txtItemId)){
					txtItemId.setAttribute("type","hidden");
					txtItemId.setAttribute("subtype","Hidden");
				}
			}else if(!YFCObject.isVoid(sSuggestedItem) &&
				!YFCObject.isVoid((String)taskMap.get("UnitOfMeasure")) && 							!YFCObject.isVoid((String)taskMap.get("EnterpriseCode"))){
				YFCDocument tempDocForItem = YFCDocument.parse("<Item ItemID=\"\" OrganizationCode=\"\" TagCapturedInInventory=\"\" DisplayItemId=\"\" ><PrimaryInformation DisplayItemDescription=\"\" /><InventoryParameters/></Item>");
			
				YFCDocument itemDoc = YFCDocument.createDocument("Item");
				YFCElement itemInXML = itemDoc.getDocumentElement();
				itemInXML.setAttribute("ItemID",sSuggestedItem);
				itemInXML.setAttribute("UnitOfMeasure",(String)taskMap.get("UnitOfMeasure"));
				itemInXML.setAttribute("OrganizationCode",(String)taskMap.get("EnterpriseCode"));
				itemInXML.setAttribute("Node", sNode);

				%>
				<yfc:callAPI apiName="getNodeItemDetails" inputElement='<%=itemInXML%>' 
				templateElement='<%=tempDocForItem.getDocumentElement()%>'/>
				<%

				errorDesc=checkForError() ;	
				if(errorDesc==null){
					sSuggestedItem = getValue("Item","xml:/Item/@DisplayItemId");
					sDisplayItemDesc = getValue("Item","xml:/Item/PrimaryInformation/@DisplayItemDescription");
				}
			}

			if(!YFCObject.isVoid(sSuggestedCase)) {
				YFCElement completeElem=getField(ydoc,"txtSuggestedLpn");
				if(completeElem!=null){
					completeElem.setAttribute("tag","binding=xml:/TaskList/Task/Inventory/@SourceCaseId");			
					completeElem.setAttribute("inputbinding","xml:/TaskList1/Task/Inventory/@SourceCaseId");
					completeElem.setAttribute("outputbinding","xml:/TaskList/Task/Inventory/@SourceCaseId");
				}
				completeElem=getField(ydoc,"lblPalletId");
				if(completeElem!=null){
					completeElem.setAttribute("value","Case");									
				}
				request.setAttribute("xml:/TaskList1/Task/@SourceLocationId", sLocationId);
				request.setAttribute("xml:/TaskList1/Task/Inventory/@SourceCaseId",sSuggestedCase);
				request.setAttribute("xml:/TaskList1/Task/Inventory/@ItemId",sSuggestedItem);				
                                request.setAttribute("xml:/RecordCountResult1/@TaskType",getValue("TaskList","xml:/TaskList/Task/@TaskType")); 
				out.println(sendForm(ydoc, focusField,true)) ;
			}else if(!YFCObject.isVoid(sSuggestedPallet)){
				YFCElement completeElem=getField(ydoc,"lblPalletId");
				if(completeElem!=null){
					completeElem.setAttribute("value","Pallet");									
				}
				request.setAttribute("xml:/TaskList1/Task/@SourceLocationId", sLocationId);
				request.setAttribute("xml:/TaskList1/Task/Inventory/@SourcePalletId",sSuggestedPallet);
				request.setAttribute("xml:/TaskList1/Task/Inventory/@ItemId",sSuggestedItem);			
                                request.setAttribute("xml:/RecordCountResult1/@TaskType",getValue("TaskList","xml:/TaskList/Task/@TaskType")); 
				out.println(sendForm(ydoc, focusField,true)) ;
			}else{
				YFCElement txtSuggestedLPNElem=getField(ydoc,"txtSuggestedLpn");
				txtSuggestedLPNElem.setAttribute("type","hidden");
				txtSuggestedLPNElem.setAttribute("subtype","Hidden");	
				request.setAttribute("xml:/TaskList1/Task/@SourceLocationId", sLocationId);
				request.setAttribute("xml:/TaskList1/Task/Inventory/@ItemId",sSuggestedItem);			
                                request.setAttribute("xml:/TaskList1/Task/Inventory/@DisplayItemDescription",sDisplayItemDesc);				
                                request.setAttribute("xml:/RecordCountResult1/@TaskType",getValue("TaskList","xml:/TaskList/Task/@TaskType")); 
				out.println(sendForm(ydoc, focusField,true)) ;
			}
		}
	}
	if(errorDesc!=null){
		String errorXML = getErrorXML(errorDesc, errorField);
		%><%=errorXML%><%
	}
%>