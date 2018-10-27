<%@ include file="/yfc/rfutil.jspf" %>

<%
	String formName="/frmCountCriteria";
	String errorDesc = null;
	String errorField="";
	String entitybase= getParameter("entitybase") ;
	String forwardPage = checkExtension(entitybase + "/frmCountCriteriaUpdGo.jsp");
	String sStatus = "1200";
	String sStatusQryType = "EQ";
	String sAction = "frmRejectTask";	
	String sFocusComponent = "gridTaskList";
	String sTaskId = getParameter("xml:/CountTask/@TaskId");
	String sUserId = resolveValue("xml:CurrentUser:/User/@Loginid");

	if(!isVoid(request.getAttribute("ForwardPage"))){
		forwardPage = checkExtension(entitybase +"/"+ request.getAttribute("ForwardPage"));
	}
	String sCaseId = "";
	String sPalletId = "";
	String sParentLPNNo = "";
	String sLastScannedLPN=getParameter("xml:/RecentLPN/@LastScannedLPN");

	YFCElement parentLPN = getStoredElement(getTempQ(),"ParentLPN",sLastScannedLPN);
	YFCNodeList LPNList = (YFCNodeList)((getTempQ()).getElementsByTagName("LPN"));
	
	if(!isVoid(parentLPN)){
		if((LPNList.getLength()>0)){
	        YFCElement  LPNElem = (YFCElement)LPNList.item(LPNList.getLength()-1);
		    if (!isVoid(LPNElem)) {
				sCaseId = LPNElem.getAttribute("CaseId");
				if(!isVoid(parentLPN.getAttribute("PalletId"))){
					sParentLPNNo = parentLPN.getAttribute("PalletId");
				}else{
					sParentLPNNo = parentLPN.getAttribute("CaseId");
				}
		    } 
		}
		else {
			sCaseId = parentLPN.getAttribute("CaseId");
			sPalletId = parentLPN.getAttribute("PalletId");
		}
	}else{
		if((LPNList.getLength()>0)){
	        YFCElement  LPNElem = (YFCElement)LPNList.item(LPNList.getLength()-1);
		    if (!isVoid(LPNElem)) {
				sCaseId = LPNElem.getAttribute("CaseId");
		    } 
		}
	}

	YFCNodeList tagList =(YFCNodeList)((getTempQ()).getElementsByTagName("BC_TagDetail"));
	if(isVoid(tagList) || tagList.getLength() ==0){
		tagList =(YFCNodeList)((getTempQ()).getElementsByTagName("TagAttributes"));
	}
	YFCNodeList serialList =(YFCNodeList)((getTempQ()).getElementsByTagName("BC_SerialDetail"));	
	if(isVoid(serialList) || serialList.getLength() ==0) {
		serialList =(YFCNodeList)((getTempQ()).getElementsByTagName("SerialDetail"));
	}

	YFCElement multiApi = YFCDocument.parse("<MultiApi ><API Name=\"recordCount\"><Input><Count UserId=\""+sUserId+"\" TaskId=\""+ sTaskId+"\" ></Count></Input></API></MultiApi>",true).getDocumentElement();
	YFCElement recordCountInput = multiApi.getChildElement("API").getChildElement("Input").getChildElement("Count");
	
	//YFCElement recordCountInput = YFCDocument.parse("<Count UserId=\""+sUserId+"\" TaskId=\""+ sTaskId+"\" ></Count>").getDocumentElement();
	/* YFCNodeList nl = getTempQ().getDocumentElement().getElementsByTagName("Inventory");
	if(!isVoid(nl)){
	   int nlLength = nl.getLength();
	   if(nlLength > 1){
		recordCountInput.setAttributes(nl.item(nlLength -1).getAttributes());
	   }
	} */

	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	recordCountInput.setAttributes(countResult.getAttributes());
	
	int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");	
	int iCountIteration = countResult.getIntAttribute("CountIteration");	
	recordCountInput.setAttribute("LocationId",getTempQ().getDocumentElement().getChildElement("Task").getAttribute("SourceLocationId"));	
	YFCElement itemElem= getStoredElement(getTempQ(),"Inventory",String.valueOf("1"));
	if(!isVoid(itemElem)) {
		recordCountInput.setAttributes(itemElem.getAttributes());
		if(!isVoid(itemElem.getAttribute("InventoryOrganizationCode"))){
			recordCountInput.setAttribute("OrganizationCode",itemElem.getAttribute("InventoryOrganizationCode"));
		}else{
			recordCountInput.setAttribute("OrganizationCode",itemElem.getAttribute("OrganizationCode"));
		}
	}
	recordCountInput.setAttribute("CaseId",sCaseId);
	recordCountInput.setAttribute("PalletId",sPalletId);
	recordCountInput.setAttribute("ParentLPNNo",sParentLPNNo);
	String sItemValue = recordCountInput.getAttribute("value");	
	YFCElement itemTagElem = null;

	/* -- Top of CR 492 -- fix - this created duplicate CountTag nodes --
	for(int m=0; m < tagList.getLength();m++) {
		YFCElement tagElem =  (YFCElement)tagList.item(m);
		String sTagValue = tagElem.getAttribute("value");
		if(equals(sTagValue,sItemValue)) {
			itemTagElem = recordCountInput.createChild("CountTag");
			itemTagElem.setAttributes(tagElem.getAttributes());
			YFCElement extnElem = tagElem.getChildElement("Extn");
			if(!isVoid(extnElem)){
				itemTagElem.getChildElement("Extn", true).setAttributes(extnElem.getAttributes());
			}
		}
	}
	-- Bottom of CR 492 -- comment out: this creates duplicate CountTag nodes -- */

	String sIsGranularityVariance="";
	if(recordCountInput!=null){
		YFCDocument TaskTypeDoc = YFCDocument.createDocument("TaskType");
		YFCElement TaskTypeInXML = TaskTypeDoc.getDocumentElement();
		TaskTypeInXML.setAttribute("TaskType",recordCountInput.getAttribute("TaskType"));
		TaskTypeInXML.setAttribute("OrganizationCode",recordCountInput.getAttribute("Node"));
		YFCDocument tempDocForTaskType = YFCDocument.parse("<TaskType ActivityCode=\"\" ActivityGroupId=\"\"><Count IsGranularityVariance=\"\"/></TaskType>");
		%>
			<yfc:callAPI apiName="getTaskTypeDetails" inputElement='<%=TaskTypeInXML%>' 
			templateElement='<%=tempDocForTaskType.getDocumentElement()%>' outputNamespace="TaskTypeDetails"/>
		<%
		errorDesc=checkForError();	
	}
	if(errorDesc==null){
		YFCElement oTaskTypeDetails = (YFCElement)request.getAttribute("TaskTypeDetails");
		if(oTaskTypeDetails!=null){
			YFCElement oCount = oTaskTypeDetails.getChildElement("Count");
			if(oCount!=null){
				sIsGranularityVariance = oCount.getAttribute("IsGranularityVariance"); 
			}
		}
	}
	boolean bIsGranular = false;				
	if(equals("Y",sIsGranularityVariance)) {											
		bIsGranular = true;
	}

	if(equals("Y",recordCountInput.getAttribute("SerialTracked")) && bIsGranular){
		recordCountInput.setAttribute("CountQuantity",1);
	}
	YFCElement origElem = recordCountInput;
	boolean bLineFound = false;
	for(int j=0; j < serialList.getLength();j++) {
		YFCElement serailElem =  (YFCElement)serialList.item(j);
		String sSerialValue = serailElem.getAttribute("ItemValue");
		if(isVoid(sSerialValue)){
			sSerialValue = serailElem.getAttribute("value");
		}
		if(equals(sSerialValue,sItemValue)) {
			if(bLineFound){
				YFCElement apiElem  = multiApi.createChild("API");
				apiElem.setAttribute("Name","recordCount");
				recordCountInput = apiElem.createChild("Input").createChild("Count");
				recordCountInput.setAttributes(origElem.getAttributes());
			}
			bLineFound = true;
			recordCountInput.setAttributes(serailElem.getAttributes());
			/* -- Top of CR 492 -- fix - this created duplicate CountTag nodes -- */
			itemTagElem = (YFCElement)tagList.item(j);
			if(!isVoid(itemTagElem)){
				YFCElement countTagElem = recordCountInput.createChild("CountTag");
				countTagElem.setAttributes(itemTagElem.getAttributes());
				YFCElement extnElem = countTagElem.getChildElement("Extn");
				if(!isVoid(extnElem)){
					countTagElem.getChildElement("Extn", true).setAttributes(extnElem.getAttributes());
				}
			}
			/* -- Bottom of CR 492 -- comment out: this creates duplicate CountTag nodes -- */
		}
	}

	YFCNodeList nlSerialRange = (getTempQ()).getElementsByTagName("SerialRange");
	if(!isVoid(nlSerialRange) && nlSerialRange.getLength()>0 ){
		YFCElement eFirstRange = (YFCElement)nlSerialRange.item(0);
		if(!isVoid(eFirstRange.getAttribute("FromSerialNo")) && !isVoid(eFirstRange.getAttribute("ToSerialNo"))){
			YFCElement apiElem  = multiApi.createChild("API");
			apiElem.setAttribute("Name","recordCount");
			recordCountInput = apiElem.createChild("Input").createChild("Count");
			recordCountInput.setAttributes(origElem.getAttributes());
			recordCountInput.removeAttribute("SerialNo");
			YFCElement serialRangeListElem = recordCountInput.createChild("SerialRangeList");
			for (int irange = 0 ; irange < nlSerialRange.getLength() ; irange ++) {
				YFCElement serialRangeElement = (YFCElement)nlSerialRange.item(irange);
				if(!isVoid(serialRangeElement.getAttribute("FromSerialNo")) &&		!isVoid(serialRangeElement.getAttribute("ToSerialNo"))){
					serialRangeListElem.importNode(serialRangeElement);
				}
			}
		}
	}

	if(equals("Y",recordCountInput.getAttribute("SerialTracked")) && bIsGranular){
		YFCNodeList nlAPI = multiApi.getElementsByTagName("API");
		if(!isVoid(nlAPI) && nlAPI.getLength()>0){
			for (int iAPI = 0 ; iAPI < nlAPI.getLength() ; iAPI ++) {
				YFCElement eAPI = (YFCElement)nlAPI.item(iAPI);
				if(isVoid(eAPI.getChildElement("Input").getChildElement("Count").getAttribute("SerialNo")) &&
					isVoid(eAPI.getChildElement("Input").getChildElement("Count").getChildElement("SerialRangeList"))){
					multiApi.removeChild(eAPI);
				}
			}		
		}
	}
	YFCElement recordCountTemplate = YFCDocument.parse("<multiApi/>").getDocumentElement();
	%>
		<yfc:callAPI apiName="multiApi" inputElement="<%=multiApi%>" templateElement="<%=recordCountTemplate%>" outputNamespace="recordCount"/>
		<!-- ******* By this a Count Task has been complted ******* -->
	<%
	YFCElement taskListInput = YFCDocument.parse("<Task AssignedToUserId=\""+sUserId+"\" TaskStatus=\""+ sStatus+"\" TaskStatusQryType=\"" + sStatusQryType + "\"><TaskType ActivityGroupId=\"COUNT\"/></Task>",true).getDocumentElement();
    
	YFCElement taskListTemplate = YFCDocument.parse("<TaskList TotalNumberOfRecords=\"\"><Task><Inventory></Inventory><TaskReferences/><TaskType/></Task></TaskList>").getDocumentElement();
	%>
		<yfc:callAPI apiName="getTaskList" inputElement="<%=taskListInput%>" templateElement="<%=taskListTemplate%>" outputNamespace="TaskList"/>		
	<%

	if(equals(resolveValue("xml:TaskList:/TaskList/@TotalNumberOfRecords"),"0")) {
		formName="/frmNoOpenTasks";
		sFocusComponent = "OK";
	}
	errorDesc=checkForError();

	if(errorDesc==null){
		if(!isVoid(request.getAttribute("ResetLastScannedLPN"))){
			request.setAttribute("xml:/RecentLPN/@LastScannedLPN","");
		}
		YFCElement invElement = getTempQ().getDocumentElement().getChildElement("Inventory");
		while(!isVoid(invElement)){
			getTempQ().getDocumentElement().removeChild(invElement);
			invElement = getTempQ().getDocumentElement().getChildElement("Inventory");                        
		}

		YFCElement serialElement = getTempQ().getDocumentElement().getChildElement("SerialDetail");
			while(!isVoid(serialElement)){
				getTempQ().getDocumentElement().removeChild(serialElement);
				serialElement = getTempQ().getDocumentElement().getChildElement("SerialDetail");                        
			}

		YFCElement tagElement = getTempQ().getDocumentElement().getChildElement("TagAttributes");
			while(!isVoid(tagElement)){
				getTempQ().getDocumentElement().removeChild(tagElement);
				tagElement = getTempQ().getDocumentElement().getChildElement("TagAttributes");                        
			}

		YFCElement locnElem = getTempQ().getDocumentElement().getChildElement("Location",true);
		locnElem.setAttribute("LocationId",getTempQ().getDocumentElement().getChildElement("Task").getAttribute("SourceLocationId"));
		
		try{
			YFCElement taskElem = getTempQ().getDocumentElement().getChildElement("Task");
			if(!isVoid(taskElem)){
				request.setAttribute("xml:/CountTask/@TaskId",taskElem.getAttribute("TaskId"));
			}			
			request.setAttribute("xml:/Location1/@BarCodeData",locnElem.getAttribute("LocationId"));
			request.setAttribute("xml:/Location/@BarCodeData",locnElem.getAttribute("LocationId"));
			%>
			<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
			<%	
		}catch(Exception e){
			errorDesc="Mobile_Access_Denied";
		}
	}else{
		String errorXML = getErrorXML(errorDesc, errorField);
		%>
		<%=errorXML%>
		<%
	}%>