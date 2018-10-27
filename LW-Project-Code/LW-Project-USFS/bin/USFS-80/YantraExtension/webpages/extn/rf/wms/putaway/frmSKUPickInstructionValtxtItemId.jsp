<%@ include file="/yfc/rfutil.jspf" %>
<%@ include file="/console/jsp/rftasktypeutils.jspf" %>

<%	String formName = "/frmSKUPickInstruction" ;
	String errorDesc=null;
	String errorField="txtItemId";
	String entitybase= getParameter("entitybase") ;
	String forwardPage="";
	String suggestedSKU=getParameter("xml:/TaskList/Task/Inventory/@ItemId"); 
	String pickedSKU=getParameter("xml:/Task/Inventory/@ItemId");
	String enterprise=getParameter("xml:/Task/@EnterpriseKey");
	String sSourceLocationId=getParameter("xml:/Task/@SourceLocationId");
	boolean fixForwardPage = false;
	boolean bAutoAccept = true;
	boolean bSerialNoScanned = false;

	if(isVoid(pickedSKU)) {
		errorDesc="Mobile_Please_Scan_A_SKU.";
	}

	if(errorDesc==null){ //translate bar code 
		YFCDocument inputDoc = YFCDocument.parse("<BarCode BarCodeData=\"" +pickedSKU+ "\" BarCodeType=\"Item\" ><ContextualInfo EnterpriseCode=\"" + enterprise +"\"  OrganizationCode=\"" + getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@OrganizationCode")+"\"/><LocationContextualInfo LocationId=\"" + sSourceLocationId +"\"/></BarCode>",true);
		%>
			<yfc:callAPI apiName="translateBarCode" inputElement='<%=inputDoc.getDocumentElement()%>'  outputNamespace='BarCode'/>
		<%		
		errorDesc=checkForError() ;
	}

	if(errorDesc==null){	
		YFCElement criteria=getStoredElement(getTempQ(),"Criteria", "criteria");
		if(!isVoid(criteria)){
			bAutoAccept = criteria.getBooleanAttribute("AutoAccept", true);
		}
		// Top of CR 546 -- TI entry validation
		String barCodeData=getValue("BarCode","xml:/BarCode/@BarCodeData");
		String pickedSerialNo=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/Inventory/SerialDetail/@SerialNo");
		if(barCodeData.equals(pickedSerialNo)){ // barcode data is the serial number
			bSerialNoScanned = true; // user scanned Trackable ID instead of ItemID
		}
		// Bottom of CR 546 - TI entry validation
		pickedSKU=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/@ItemID");
		if(isVoid(pickedSKU)){
			errorDesc="Mobile_Unidentifiable_Barcode";
		} else {
			if(!pickedSKU.equals(suggestedSKU)){
				// find if the picked SKU is at the same location
				String sPC=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/@ProductClass");
				String sUOM=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/@InventoryUOM");

				request.setAttribute("xml:/Task/Inventory/@ItemId",pickedSKU);
				request.setAttribute("xml:/Task/Inventory/@UnitOfMeasure",sUOM);
				request.setAttribute("xml:/Task/Inventory/@ProductClass",sPC);

				YFCDocument inputDoc = YFCDocument.parse("<NodeInventory LocationId=\"" + sSourceLocationId + "\" EnterpriseCode=\"" + enterprise +"\"   Node=\""+ getValue("CurrentUser","xml:CurrentUser:/User/@Node")+"\" > <Inventory " + "> <InventoryItem ItemID=\"" + pickedSKU +"\" ProductClass=\"" + sPC + "\" UnitOfMeasure= \"" + sUOM+"\" /> </Inventory></NodeInventory>");
			
				YFCDocument templateDoc = YFCDocument.parse("<NodeInventory Node=\"\"> <LocationInventoryList> <LocationInventory LocationId=\"\" InventoryItemKey=\"\" MultipleSummaryAttributes=\"\" PendInQty=\"\" PendOutQty=\"\" ZoneId=\"\" Quantity=\"\"> <InventoryItem ItemID=\"\" OrganizationCode=\"\" ProductClass=\"\" UnitOfMeasure=\"\" ><Item ItemID=\"\" ItemKey=\"\" OrganizationCode=\"\" UnitOfMeasure=\"\"> <PrimaryInformation Description=\"\" ShortDescription=\"\"/> </Item> </InventoryItem> <SummaryAttributes CaseId=\"\" CountryOfOrigin=\"\" FifoNo=\"\" InventoryTagKey=\"\" PalletId=\"\" ReceiptHeaderKey=\"\" Segment=\"\" SegmentType=\"\" InventoryStatus=\"\" ShipByDate=\"\" > <Receipt ReceiptNo=\"\" /> <TagSummary LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" LotExpirationDate=\"\" LotKeyReference=\"\" LotNumber=\"\" ManufacturingDate=\"\" RevisionNo=\"\" TagNumber=\"\"><Extn/>  </TagSummary>					   </SummaryAttributes></LocationInventory> </LocationInventoryList> </NodeInventory>");

				%>
				<yfc:callAPI apiName="getNodeInventory" inputElement='<%=inputDoc.getDocumentElement()%>'  templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace='NodeInventory'/>
				<%	
				errorDesc=checkForError();

				String SKULocation= getValue("NodeInventory","xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId");

				if(equals(SKULocation, getParameter("xml:/Task/@SourceLocationId"))){
					//reject task (API: rejectTasks) at hand only when task is accepted during getNextTask call
					if(bAutoAccept){
						%>
						<yfc:callAPI apiID='AP6' />
						<%
						errorDesc=checkForError();
					}
					if(errorDesc==null){
						// find if an open task exists for the scanned SKU. call getNextTask() with this SKU id
						YFCDocument inDocTogetNextTask = YFCDocument.createDocument("GetNextTask");
						YFCElement inXMLTogetNextTask = inDocTogetNextTask.getDocumentElement();
						inXMLTogetNextTask.setAttribute("ActivityGroupId","PUTAWAY");
						inXMLTogetNextTask.setAttribute("TaskType",resolveValue("xml:/Task/@TaskType"));
						inXMLTogetNextTask.setAttribute("CurrentLocationId", SKULocation);
						inXMLTogetNextTask.setAttribute("AutoAccept","Y");
						inXMLTogetNextTask.setAttribute("UserId",resolveValue("xml:CurrentUser:/User/@Loginid"));
						inXMLTogetNextTask.setAttribute("OrganizationCode",resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode"));
						if(!isVoid(resolveValue("xml:/Task/@EquipmentId"))){
							inXMLTogetNextTask.setAttribute("EquipmentId",resolveValue("xml:/Task/@EquipmentId"));
						}
						YFCElement taskElement = inXMLTogetNextTask.createChild("Task");
						YFCElement inventoryElement = taskElement.createChild("Inventory");
						inventoryElement.setAttribute("ItemId", pickedSKU);
						inventoryElement.setAttribute("SourceCaseId", resolveValue("xml:/Task/Inventory/@SourceCaseId"));
						inventoryElement.setAttribute("SourcePalletId", resolveValue("xml:/Task/Inventory/@SourcePalletId"));
		
						//YFCElement criteria=getStoredElement(getTempQ(),"Criteria", "criteria");
						if(!isVoid(criteria)){
							String sShipmentNo = criteria.getAttribute("ShipmentNo");
							YFCElement taskRefElem = taskElement.createChild("TaskReferences");
							taskRefElem.setAttribute("ShipmentNo", sShipmentNo);
						}
		
						YFCElement getNextTaskTemplate = YFCDocument.parse("<TaskList TotalNumberOfRecords=\"\" ><Task EnterpriseKey=\"\" EquipmentId=\"\" EquipmentLocationId=\"\"  ExceptionHold=\"\"  HoldReasonCode=\"\"  Node=\"\"  OrganizationCode=\"\" SourceLocationId=\"\" TargetCaseId=\"\"  TargetLocationId=\"\"  TargetPalletId=\"\"  TaskId=\"\"  TaskType=\"\"  TaskStatus=\"\"  TaskKey=\"\" IsConsolidatedTaskTaskKey=\"\"><Inventory><TagAttributes><Extn/> </TagAttributes></Inventory><TaskReferences/><Batch/><TaskType/></Task></TaskList>").getDocumentElement();
							//System.out.println("$$$$$$$$$$$$$$$$$$$ inXMLTogetNextTask SKU *********"+inXMLTogetNextTask);
						//call getNextTask API
						%>
						<yfc:callAPI apiName="getNextTask" inputElement='<%=inXMLTogetNextTask%>' templateElement="<%=getNextTaskTemplate%>" outputNamespace="TaskList"/>
						<%		
						errorDesc=checkForError() ;
					}
				} else {
					errorDesc="Mobile_SKU_Picked_Not_At_Source_Location";
				}
				if(errorDesc==null){
					//if no open task exists for this SKU, error
					if(!(pickedSKU.equals( getValue("TaskList","xml:/TaskList/Task/Inventory/@ItemId")))){
						errorDesc="Mobile_No_Putaway_Task_Open_For_This_SKU";
					}else{
						request.setAttribute("xml:/Task/@TaskKey",(String)getValue("TaskList","xml:/TaskList/Task/@TaskKey"));				
						//forward to frmOverride.jsp
						//To set tag details etc from barcode data forwarding should be done later.
						fixForwardPage = true;						
					}
				}
			}else if(bSerialNoScanned){ // Serial number is scanned/entered
				// Top of CR 546 - TI entry validation
				// find if the picked SerianNo is at the same location
				String sPC=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/@ProductClass");
				String sUOM=getValue("BarCode","xml:/BarCode/Translations/Translation/ItemContextualInfo/@InventoryUOM");

				request.setAttribute("xml:/Task/Inventory/@ItemId",pickedSKU);
				request.setAttribute("xml:/Task/Inventory/@UnitOfMeasure",sUOM);
				request.setAttribute("xml:/Task/Inventory/@ProductClass",sPC);

				YFCDocument inputDoc = YFCDocument.parse("<NodeInventory LocationId=\"" + sSourceLocationId + "\" EnterpriseCode=\"" + enterprise +"\"   Node=\""+ getValue("CurrentUser","xml:CurrentUser:/User/@Node")+"\" > <Inventory " + "> <InventoryItem ItemID=\"" + pickedSKU +"\" ProductClass=\"" + sPC + "\" UnitOfMeasure= \"" + sUOM + "\" /> <SerialList> <SerialDetail SerialNo=\"" + pickedSerialNo + "\" /> </SerialList> </Inventory></NodeInventory>");
				YFCDocument templateDoc = YFCDocument.parse("<NodeInventory Node=\"\"> <LocationInventoryList> <LocationInventory LocationId=\"\" InventoryItemKey=\"\" MultipleSummaryAttributes=\"\" PendInQty=\"\" PendOutQty=\"\" ZoneId=\"\" Quantity=\"\"> <InventoryItem ItemID=\"\" OrganizationCode=\"\" ProductClass=\"\" UnitOfMeasure=\"\" ><Item ItemID=\"\" ItemKey=\"\" OrganizationCode=\"\" UnitOfMeasure=\"\"> <PrimaryInformation Description=\"\" ShortDescription=\"\"/> </Item> </InventoryItem> <SummaryAttributes CaseId=\"\" CountryOfOrigin=\"\" FifoNo=\"\" InventoryTagKey=\"\" PalletId=\"\" ReceiptHeaderKey=\"\" Segment=\"\" SegmentType=\"\" InventoryStatus=\"\" ShipByDate=\"\" > <Receipt ReceiptNo=\"\" /> <TagSummary LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" LotExpirationDate=\"\" LotKeyReference=\"\" LotNumber=\"\" ManufacturingDate=\"\" RevisionNo=\"\" TagNumber=\"\"><Extn/>  </TagSummary>					   </SummaryAttributes></LocationInventory> </LocationInventoryList> </NodeInventory>");

				%>
				<yfc:callAPI apiName="getNodeInventory" inputElement='<%=inputDoc.getDocumentElement()%>'  templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace='NodeInventory'/>
				<%	
				errorDesc=checkForError();

				String SKULocation= getValue("NodeInventory","xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId");
				if(equals(SKULocation, getParameter("xml:/Task/@SourceLocationId"))){
					//reject task (API: rejectTasks) at hand only when task is accepted during getNextTask call
					if(bAutoAccept){
						%>
						<yfc:callAPI apiID='AP6' />
						<%
						errorDesc=checkForError();
					}
					if(errorDesc==null){
						// find if an open task exists for the scanned SKU or SerialNo. call getNextTask()
						YFCDocument inDocTogetNextTask = YFCDocument.createDocument("GetNextTask");
						YFCElement inXMLTogetNextTask = inDocTogetNextTask.getDocumentElement();
						//inXMLTogetNextTask.setAttribute("ActivityGroupId","RECEIPT");
						//inXMLTogetNextTask.setAttribute("TaskType",resolveValue("xml:/Task/@TaskType"));
						inXMLTogetNextTask.setAttribute("CurrentLocationId", SKULocation);
						inXMLTogetNextTask.setAttribute("AutoAccept","Y");
						inXMLTogetNextTask.setAttribute("UserId",resolveValue("xml:CurrentUser:/User/@Loginid"));
						inXMLTogetNextTask.setAttribute("OrganizationCode",resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode"));
						if(!isVoid(resolveValue("xml:/Task/@EquipmentId"))){
							inXMLTogetNextTask.setAttribute("EquipmentId",resolveValue("xml:/Task/@EquipmentId"));
						}
						YFCElement taskElement = inXMLTogetNextTask.createChild("Task");
						YFCElement inventoryElement = taskElement.createChild("Inventory");
						inventoryElement.setAttribute("ItemId", pickedSKU);
						inventoryElement.setAttribute("SerialNo", pickedSerialNo); // This is the SerialNo
						inventoryElement.setAttribute("SourceCaseId", resolveValue("xml:/Task/Inventory/@SourceCaseId"));
						inventoryElement.setAttribute("SourcePalletId", resolveValue("xml:/Task/Inventory/@SourcePalletId"));
		
						//YFCElement criteria=getStoredElement(getTempQ(),"Criteria", "criteria");
						if(!isVoid(criteria)){
							String sShipmentNo = criteria.getAttribute("ShipmentNo");
							YFCElement taskRefElem = taskElement.createChild("TaskReferences");
							taskRefElem.setAttribute("ShipmentNo", sShipmentNo);
						}
						//System.out.println("$$$$$$$$$$$$$$$$$$$ inXMLTogetNextTask "+inXMLTogetNextTask);
						YFCElement getNextTaskTemplate = YFCDocument.parse("<TaskList TotalNumberOfRecords=\"\" ><Task EnterpriseKey=\"\" EquipmentId=\"\" EquipmentLocationId=\"\"  ExceptionHold=\"\"  HoldReasonCode=\"\"  Node=\"\"  OrganizationCode=\"\" SourceLocationId=\"\" TargetCaseId=\"\"  TargetLocationId=\"\"  TargetPalletId=\"\"  TaskId=\"\"  TaskType=\"\"  TaskStatus=\"\"  TaskKey=\"\" IsConsolidatedTaskTaskKey=\"\"><Inventory><TagAttributes><Extn/> </TagAttributes></Inventory><TaskReferences/><Batch/><TaskType/></Task></TaskList>").getDocumentElement();
						//call getNextTask API
						%>
						<yfc:callAPI apiName="getNextTask" inputElement='<%=inXMLTogetNextTask%>' templateElement="<%=getNextTaskTemplate%>" outputNamespace="TaskList"/>
						<%		
						errorDesc=checkForError() ;
					}
				} else {
					errorDesc="Mobile_SKU_Picked_Not_At_Source_Location";
				}
				if(errorDesc==null){
					//if no open task exists for this SKU, error
					String str1= getValue("TaskList","xml:/TaskList/Task/Inventory/@ItemId");
					if(!str1.equals(""))
					{
						//System.out.println("$$$$$$$$$$$$$$$$$$$ str1 and pickedSKU are "+str1+"   "+pickedSKU);
						if(!(pickedSKU.equals( getValue("TaskList","xml:/TaskList/Task/Inventory/@ItemId")))){
							errorDesc="Mobile_No_Putaway_Task_Open_For_This_SKU";
						}else{
							request.setAttribute("xml:/Task/@TaskKey",(String)getValue("TaskList","xml:/TaskList/Task/@TaskKey"));				
							//forward to frmOverride.jsp
							//To set tag details etc from barcode data forwarding should be done later.
							fixForwardPage = true;						
						}
					}
				}
				// Bottom of CR 546 - TI entry validation
			}else if(!bAutoAccept){
				//accepting task by calling acceptTask API
				%>
				<yfc:callAPI apiID='AP4' />
				<%
			}	
		}
	}

	if(errorDesc==null){ //set item scanned
		YFCElement taskElem=getStoredElement(getTempQ(),"Task", getParameter("xml:/Task/@TaskKey"));
		if(taskElem!=null){
			YFCElement taskInv = taskElem.getChildElement("Inventory");
			if(taskInv!=null){
				taskInv.setAttribute("ItemId" , pickedSKU);
			}
		}

		String sTotalNumOfRecords = getValue("BarCode","xml:/BarCode/Translations/@TotalNumberOfRecords");
		YFCElement barCodeTranOutElem = (YFCElement)request.getAttribute ("BarCode");
		if (!isVoid(barCodeTranOutElem) && "1".equals(sTotalNumOfRecords)) {				
			barCodeTranOutElem.setAttribute("BarCodeTranslationSource", getValue("BarCode","xml:/BarCode/Translations/@BarCodeTranslationSource"));
			try {
				addToTempQ("BarCode", getParameter("xml:/Task/@TaskKey"), barCodeTranOutElem.getAttributes() ,false);
			} catch (Exception e){
				replaceInTempQ("BarCode", getParameter("xml:/Task/@TaskKey"), barCodeTranOutElem.getAttributes());
			}
			YFCElement bcItemCtxtInf = barCodeTranOutElem.getChildElement("Translations").getChildElement("Translation").getChildElement("ItemContextualInfo");
			if (bcItemCtxtInf !=null){
				try {
					addToTempQ("BC_ItemContextualInfo", getParameter("xml:/Task/@TaskKey"), bcItemCtxtInf.getAttributes() ,false);
				} catch (Exception e){
					replaceInTempQ("BC_ItemContextualInfo", getParameter("xml:/Task/@TaskKey"), bcItemCtxtInf.getAttributes());
				}
			} 
			YFCElement barCodeInvElem = barCodeTranOutElem.getChildElement("Translations").getChildElement("Translation").getChildElement("ItemContextualInfo").getChildElement("Inventory");
			if(!isVoid(barCodeInvElem)) {
				try{
					addToTempQ("BC_Inventory", getParameter("xml:/Task/@TaskKey"), barCodeInvElem.getAttributes(), false);
				}catch(Exception e){
					replaceInTempQ("BC_Inventory", getParameter("xml:/Task/@TaskKey"), barCodeInvElem.getAttributes());
				}
				YFCElement tagDetailElem = barCodeInvElem.getChildElement("TagDetail");
				if (!isVoid(tagDetailElem)) {
					try{
						addToTempQ("BC_TagDetail", getParameter("xml:/Task/@TaskKey"), tagDetailElem.getAttributes(), false);
					}catch(Exception e){
						replaceInTempQ("BC_TagDetail", getParameter("xml:/Task/@TaskKey"), tagDetailElem.getAttributes());
					}					
				}
				YFCElement serialDetailElem = barCodeInvElem.getChildElement("SerialDetail");
				if (!isVoid(serialDetailElem) && serialDetailElem.hasAttribute("SerialNo")) {
					try{
						addToTempQ("BC_SerialDetail", getParameter("xml:/Task/@TaskKey"), serialDetailElem.getAttributes(), false);
					}catch(Exception e){
						replaceInTempQ("BC_SerialDetail", getParameter("xml:/Task/@TaskKey"), serialDetailElem.getAttributes());
					}					
				}				
			}			
		}

		//forward to task controller for checking task attributes 
		if(errorDesc == null) {
			if(fixForwardPage){
				//item is overrided...forward to override jsp
				forwardPage=checkExtension(entitybase+ "/frmOverride.jsp");
			}else{
				HashMap attrMp=(HashMap)TaskTypeGroupFormNameMap.get("TASK");
				try{
					forwardPage=checkExtension(getForwardPage((String)attrMp.get("Entity"),(String) attrMp.get("itemattr")) );
					Map extraParamsMap = new HashMap();
					extraParamsMap.put("xml:/Task/Inventory/@ItemId",pickedSKU);
					forwardPage+=encodeParams(extraParamsMap, forwardPage);
				}catch(Exception e){
					errorDesc="Mobile_Access_Denied";
				}
			}

			%>
			<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
			<%
		}	
	}

	if(errorDesc!=null)	{
		// if no putaway task open error, accept task at hand and proceed.
		if("Mobile_No_Putaway_Task_Open_For_This_SKU".equals(errorDesc)){
			%>
			<yfc:callAPI apiID='AP4' /> 
			<%	
		}
		String errorXML = getErrorXML(errorDesc, errorField);
		%>
		<%=errorXML%>
	<%}%>
