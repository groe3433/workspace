<%@ include file="/yfc/rfutil.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>

<%!
	int totalcases =0;
%>
<%
String errorDesc = null ;
YFCElement invElement = null;
YFCElement lpnElement = null;
HashSet invUOMSet= new HashSet();
String sVarianceType="No Variance";
String isTagTracked="N";
String serialTracked="N";
String timeSensitive="N";
String displayItemId="";
String sDisplayItemDesc="";
String scannedLoc="";
String sInvOrgCode="";
String sEnterpriseCode = getParameter("xml:/Enterprise/@EnterpriseCode");
String sInventoryOrgCode="";
String errorField = "txtBarCodeData" ;
String suggestedItem= getParameter("xml:/TaskList/Task/inventory/@ItemId");
String barCode=getParameter("xml:/RecordCountResult/@BarCodeData");
String caseID="";
String formName = "/frmCountEntryForCaseOrSKU" ;	
String itemID="";
String invUOM="";
String sItemId="";
String entitybase= getParameter("entitybase") ;
String forwardPage="";
String sRecentPalletId="";
String sRecentCaseId="";
String sScanned2DBarCode = "N";
double dCountQty = 0;
String sTaskId = "";
String sUserId = resolveValue("xml:CurrentUser:/User/@Loginid");
String sScannedLocation= getParameter("xml:/CountRequest/@LocationId");
String sScannedPalletId= getParameter("xml:/CountRequest/@PalletId");
String sScannedCaseId= getParameter("xml:/CountRequest/@CaseId");
String sScannedContext = getParameter("xml:/CountRequest/@ScannedContext");
String sUOM = getParameter("xml:/CountRequest/@UnitOfMeasure");
boolean bVarianceTask = false;
int NoOfItemsScanned = 0;
int NoOfCasesScanned = 0;

YFCElement itemElem = null;
//YFCElement itemInfoElem = null;
String sRequestType = getParameter("xml:/CountRequest/@RequestType");

if(equals(sRequestType,"MANUAL-COUNT") && isVoid(barCode)){
	//Enterprise code is mandatory for an Item level count. 
	//Forwards to manual count criteria screen.
	forwardPage= entitybase + "/" + "frmManualCountEntryForSKUProceedData.jsp" ;
	forwardPage=checkExtension(forwardPage);
	%>
	 <jsp:forward page='<%=forwardPage%>' >
		<jsp:param name="xml:/Enterprise/@EnterpriseCode" value='<%=sEnterpriseCode%>'/>
		<jsp:param name="xml:/CountRequest/@RequestType" value='<%=sRequestType%>'/>
		<jsp:param name="xml:/CountRequest/@LocationId" value='<%=sScannedLocation%>'/>
		<jsp:param name="xml:/CountRequest/@ItemID" value='<%=getParameter("xml:/Inventory/@ItemID")%>'/>
		<jsp:param name="xml:/CountRequest/@UnitOfMeasure" value='<%=sUOM%>'/>
		<jsp:param name="xml:/CountRequest/@PalletId" value='<%=sScannedPalletId%>'/>
		<jsp:param name="xml:/CountRequest/@CaseId" value='<%=sScannedCaseId%>'/>
		<jsp:param name="xml:/CountRequest/@ScannedContext" value='<%=sScannedContext%>'/>
	 </jsp:forward> 
	<%							
}

HashMap itemMap= new HashMap();
HashMap caseMap= new HashMap();

YFCDocument yinTempdoc = getTempQ();

if(YFCObject.isVoid(barCode)) {
	errorDesc="Mobile_Item_Or_Case_must_be_Scanned";
}

if(errorDesc == null) {
	YFCElement parentLPNElem= getStoredElement(yinTempdoc,"ParentLPN", getParameter("xml:/RecentLPN/@LastScannedLPN")); 
	//YFCElement parentLPNElem= (YFCElement)((getTempQ()).getElementsByTagName("ParentLPN")).item(0); 
	if(parentLPNElem != null) {
		sRecentPalletId = parentLPNElem.getAttribute("PalletId");	
		sRecentCaseId = parentLPNElem.getAttribute("CaseId");	
	}
			
	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	HashMap recordMap= new HashMap(countResult.getAttributes());
	NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");
	NoOfCasesScanned = countResult.getIntAttribute("NoOfCasesScanned");
	YFCElement locationElem= (YFCElement)((getTempQ()).getElementsByTagName("Location")).item(0);
	YFCElement taskElem= (YFCElement)((getTempQ()).getElementsByTagName("Task")).item(0);
	
	//Set InvOrgCode to the scanned/selected enterprise.
	sInvOrgCode = sEnterpriseCode;
	String sSuggestedEnterpriseOverrided = getParameter("xml:/Enterprise/@SuggEnterpriseOverrided");
	
	//get the TaskType details to decide the granularity of the task
	String sIsGranularityVariance="";
	if(countResult!=null){
		YFCDocument TaskTypeDoc = YFCDocument.createDocument("TaskType");
		YFCElement TaskTypeInXML = TaskTypeDoc.getDocumentElement();
		TaskTypeInXML.setAttribute("TaskType",countResult.getAttribute("TaskType"));
		TaskTypeInXML.setAttribute("Node",countResult.getAttribute("Node"));	TaskTypeInXML.setAttribute("OrganizationCode",getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@OrganizationCode"));
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

	//int dCountIter = countResult.getIntAttribute("CountIteration");						
	if(equals("Y",sIsGranularityVariance)) {											
		bVarianceTask = true;							
	}
	
	invUOM = taskElem.getAttribute("UnitOfMeasure");
	sTaskId = taskElem.getAttribute("TaskId");
	if(YFCObject.isVoid(sEnterpriseCode)) {
		sEnterpriseCode = taskElem.getAttribute("EnterpriseCode");
	}

	//clear all barcode related attrs
	deleteAllFromTempQ("BarCode");
	deleteAllFromTempQ("BC_ContainerContextualInfo");
	deleteAllFromTempQ("BC_ItemContextualInfo");
	deleteAllFromTempQ("BC_Inventory");
	deleteAllFromTempQ("BC_TagDetail");
	deleteAllFromTempQ("BC_SerialDetail");

	YFCDocument inDocToBarCode = YFCDocument.createDocument("BarCode");
	YFCElement inXMLToBarCode = inDocToBarCode.getDocumentElement();
	inXMLToBarCode.setAttribute("BarCodeData", getParameter("xml:/RecordCountResult/@BarCodeData"));
	inXMLToBarCode.setAttribute("BarCodeType", "ItemOrCase");
	YFCElement contextInfoElement = inXMLToBarCode.createChild("ContextualInfo");
	contextInfoElement.setAttribute("OrganizationCode", getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@OrganizationCode"));
	contextInfoElement.setAttribute("EnterpriseCode", sEnterpriseCode);
	
	if(parentLPNElem != null){
		YFCElement contCtxElem = inXMLToBarCode.createChild("ContainerContextualInfo");
		contCtxElem.setAttribute("PalletId",parentLPNElem.getAttribute("PalletId"));
		contCtxElem.setAttribute("CaseId",parentLPNElem.getAttribute("CaseId"));
	}

	if(locationElem != null){
		YFCElement locnCtxElem = inXMLToBarCode.createChild("LocationContextualInfo");
		locnCtxElem.setAttribute("LocationId",locationElem.getAttribute("LocationId"));
	}
	
	//Call transalte BarCode API for the scanned bar code......	
	%><yfc:callAPI apiName="translateBarCode" inputElement='<%=inXMLToBarCode%>' /><%	
	errorDesc=checkForError();

	if(errorDesc == null) {
		double numOfTranslations=getNumericValue("xml:/BarCode/Translations/@TotalNumberOfRecords");				
		if(numOfTranslations==0){	//throw exception if invalid bar code is scanned....
			errorDesc="Mobile_Unidentifiable_Barcode"	;
			if ("Y".equals (sSuggestedEnterpriseOverrided)) {
				errorDesc="Mobile_Invalid_Enterprise";
			}
		}else {
			YFCElement barCodeElem =(YFCElement)request.getAttribute("BarCode");
			YFCElement transElement = barCodeElem.getChildElement("Translations");
			YFCNodeList tranElemList = transElement.getElementsByTagName("Translation");
			String sBarCodeTranslationSource = transElement.getAttribute ("BarCodeTranslationSource");
			if(YFCObject.isVoid(invUOM)) {
				for(int i=0;i<tranElemList.getLength();i++) {
					YFCElement firstTranElem = (YFCElement)tranElemList.item(i);
					YFCElement ItemContextElem = firstTranElem.getChildElement("ItemContextualInfo");
					if(ItemContextElem != null) {
						String sInventoryUOM = ItemContextElem.getAttribute("InventoryUOM");
						invUOMSet.add(sInventoryUOM);
					}
				}
				if(invUOMSet != null && invUOMSet.size() == 1) {
					for(Iterator i = invUOMSet.iterator(); i.hasNext();) {
						invUOM = (String)i.next();
					}
				}
			}

			YFCElement translationElem = (YFCElement)tranElemList.item(0);
			YFCElement itemInfoElem = translationElem.getChildElement("ItemContextualInfo");
			YFCElement containerInfoElem = translationElem.getChildElement("ContainerContextualInfo");
			YFCElement contextualInfoElem = translationElem.getChildElement("ContextualInfo");
			if(itemInfoElem != null) {
				itemID=itemInfoElem.getAttribute("ItemID");
			}
			if(containerInfoElem != null && !"SerialTranslator".equals(sBarCodeTranslationSource)) {
				//Serial translator can also give ContainerContextual info. We need to discard this.
				caseID=containerInfoElem.getAttribute("CaseId");
			}
			if(contextualInfoElem != null) {
				if (isVoid(sInvOrgCode)) {
					sInvOrgCode=contextualInfoElem.getAttribute("EnterpriseCode");
				}
				if(isVoid(sInvOrgCode)){
					sInvOrgCode=contextualInfoElem.getAttribute("OrganizationCode");
				}
			}

			if( !isVoid(caseID)) { //if scanned barcode is Case.....	
				YFCDocument tempDocForGetNode = YFCDocument.parse("<NodeInventory><LPNList><LPN><LPNLocation/></LPN></LPNList></NodeInventory>");
				YFCElement getNodeTempInXML= tempDocForGetNode.getDocumentElement();

				YFCDocument outDoc = YFCDocument.createDocument("NodeInventory");
				YFCElement inXML = outDoc.getDocumentElement();
				inXML.setAttribute("Node", countResult.getAttribute("Node"));
				inXML.setAttribute("EnterpriseCode", sEnterpriseCode);
				YFCElement InventoryElement = inXML.createChild("Inventory");
				InventoryElement.setAttribute("CaseId",caseID);
		
				%><yfc:callAPI apiName="getNodeInventory" inputElement='<%=inXML%>' 
					templateElement='<%=getNodeTempInXML%>'/><%					
				errorDesc=checkForError() ;		
				
				if(errorDesc == null) {
					if(locationElem != null) {
						scannedLoc = locationElem.getAttribute("LocationId");
					}
					String sLPNLoc = getValue("NodeInventory", "xml:/NodeInventory/LPNList/LPN/LPNLocation/@LocationId");
					if(!YFCObject.equals(scannedLoc,sLPNLoc)){
						sVarianceType="New";
					}		

					caseMap.put("CaseId", caseID);
					caseMap.put("OrganizationCode",sInvOrgCode);
					caseMap.put("VarianceType", sVarianceType);		
					YFCNodeList list = (YFCNodeList)((getTempQ()).getElementsByTagName("LPN"));		
					
					YFCDocument ydoc = getForm(formName) ;				
							
					YFCElement Elem=getField(ydoc,"lblTotal");
					if(Elem!=null){
						Elem.setAttribute("type","text");
						Elem.setAttribute("subtype","Label");				
					}
						
					Elem=getField(ydoc,"txtCaseScan");
					if(Elem!=null){
						Elem.setAttribute("type","text");
						Elem.setAttribute("subtype","ProtectedText");
						Elem.setAttribute("value",list.getLength());
					}
				
					try {							
						addToTempQ("LPN", caseID , caseMap, false); 
					}catch(Exception e) {
						errorDesc="Mobile_Duplicate_Case_Scanned";	
					}
					if(errorDesc == null){
						YFCElement recordCountInput = YFCDocument.parse("<Count UserId=\""+sUserId+"\" CaseId=\""+caseID+"\" OrganizationCode=\""+sInvOrgCode+"\" TaskId=\""+ sTaskId+"\" ></Count>",true).getDocumentElement();
						recordCountInput.setAttributes(getTempQ().getDocumentElement().getChildElement("RecordCountResult").getAttributes());
						recordCountInput.setAttribute("LocationId",getTempQ().getDocumentElement().getChildElement("Location").getAttribute("LocationId"));
						YFCElement recordCountTemplate = YFCDocument.parse("<Count/>").getDocumentElement();									   
						%>
						<jsp:include page="/rf/wms/count/count_task_switch.jsp" flush="false">
							<jsp:param name="TaskId" value='<%=sTaskId%>'/>
							<jsp:param name="OrgCode" value='<%=sInvOrgCode%>'/>
							<jsp:param name="CaseId" value='<%=caseID%>'/>
						</jsp:include>		
						<%						
						NoOfCasesScanned++;
						request.setAttribute("xml:/RecordCountResult/@TotalCasesScanned", String.valueOf(NoOfCasesScanned));
						recordMap.put("NoOfCasesScanned",String.valueOf(NoOfCasesScanned));
						try {
							 replaceInTempQ("RecordCountResult", "1" ,recordMap);
						}catch(Exception ee){
							errorDesc="Mobile_Duplicate_Values";
						}

					}
					
					if(errorDesc == null ) {
						request.setAttribute("xml:/RecordCountResult/@BarCodeData",""); 
						request.setAttribute("RecordCountResult", countResult);
						request.setAttribute("xml:/RecordCountResult/@TaskType",countResult.getAttribute("TaskType")); 
						out.println(sendForm(ydoc, "txtBarCodeData", true)) ;							
					}
				}
			}else { // if scanned barcode is an item........
				if(taskElem != null) {
					sItemId = taskElem.getAttribute("ItemId");
				}
				if(errorDesc == null) {
					if(!isVoid(sEnterpriseCode)) {
						YFCDocument orgTempDoc = YFCDocument.createDocument("Organization");
						YFCElement orgTempInXML = orgTempDoc.getDocumentElement();
						orgTempInXML.setAttribute("OrganizationCode","");
						orgTempInXML.setAttribute("InventoryOrganizationCode","");						
						
						YFCDocument orgDoc = YFCDocument.createDocument("Organization");
						YFCElement orgInXML = orgDoc.getDocumentElement();
						orgInXML.setAttribute("OrganizationCode",sEnterpriseCode);
						%><yfc:callAPI apiName="getOrganizationHierarchy" inputElement='<%=orgInXML%>' templateElement='<%=orgTempInXML%>'/><%	
						errorDesc=checkForError() ;							
						if(errorDesc == null) {
							sInventoryOrgCode= getValue("Organization","xml:/Organization/@InventoryOrganizationCode");
						}
					}
				}

				if(errorDesc == null) {
					NoOfItemsScanned++;
					// add the values to session
					if(itemInfoElem != null) {
						dCountQty = itemInfoElem.getDoubleAttribute("Quantity");
						itemMap.put("CountQuantity", getFormattedDouble(dCountQty));
						addToTempQ("BC_ItemContextualInfo", String.valueOf("1"), itemInfoElem.getAttributes() ,false);
						//add barcode inv info 
						YFCElement bcInvElem = itemInfoElem.getChildElement("Inventory");
						if(bcInvElem!=null){
							sScanned2DBarCode = "Y";
							addToTempQ("BC_Inventory", String.valueOf("1"), bcInvElem.getAttributes() ,false);
							//add barcode inv tag info
							YFCElement bcInvTag=bcInvElem.getChildElement("TagDetail");
							if(bcInvTag!=null){
								addToTempQ("BC_TagDetail", String.valueOf("1"), bcInvTag.getAttributes() ,false);
							}
							//add barcode inv serial detail 
							YFCElement bcInvSerialDtl=bcInvElem.getChildElement("SerialDetail");
							if(bcInvSerialDtl!=null){
								addToTempQ("BC_SerialDetail",String.valueOf("1"), bcInvSerialDtl.getAttributes() ,false);								
							}
						}
					}
					itemMap.put("ItemID", itemID);
					itemMap.put("UnitOfMeasure",invUOM);
					itemMap.put("CatalogOrganizationCode",sInvOrgCode);
					itemMap.put("OrganizationCode",sInvOrgCode);
					itemMap.put("InventoryOrganizationCode",sInventoryOrgCode);
					itemMap.put("CaseId", sRecentCaseId);
					itemMap.put("PalletId",sRecentPalletId);
					itemMap.put("ProductClass", taskElem.getAttribute("ProductClass"));
					itemMap.put("InventoryStatus", taskElem.getAttribute("InventoryStatus"));
					
					request.setAttribute("xml:/Inventory/@ItemID", itemID);		
					request.setAttribute("xml:/Inventory/@UnitOfMeasure", invUOM);
				
					try {
						addToTempQ("Inventory",String.valueOf("1"),itemMap, false);
					}catch (Exception e){
						try {
							replaceInTempQ("Inventory",String.valueOf("1"),itemMap);
						}catch(Exception ee){
							errorDesc="Mobile_Duplicate_Values";
						}								
					}
					
					if(errorDesc == null){
						recordMap.put("NoOfItemsScanned",String.valueOf(NoOfItemsScanned));
						try {
							replaceInTempQ("RecordCountResult", "1" ,recordMap);
						}catch(Exception ee){
							errorDesc="Mobile_Duplicate_Values";
						}	
					}
					
					if(errorDesc == null) {
						YFCElement bcSerialElem= getStoredElement(getTempQ(),"BC_SerialDetail", String.valueOf(NoOfItemsScanned)); 
						if(bcSerialElem != null) {
							YFCElement bcSerialDtl= getStoredElement(getTempQ(),"SerialDetail", 
								bcSerialElem.getAttribute("SerialNo"));
							if(bcSerialDtl != null){
								errorDesc="Mobile_Duplicate_Serial";
							}
						}
					}

					if(errorDesc == null){	
						if(isVoid(invUOM)) {	//If inventory UOM is void and unique item is not determined, get the INVENTORY UOM.		
							forwardPage=entitybase+"/"+"frmCountSKUInventoryUOM.jsp" ;
							forwardPage=checkExtension(forwardPage);
							%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
						} else { 
							YFCDocument tempDocForItem = YFCDocument.parse("<Item ItemID=\"\" OrganizationCode=\"\" TagCapturedInInventory=\"\" DisplayItemId=\"\" ><PrimaryInformation DisplayItemDescription=\"\" SerialCapturedInReceiving=\"\" SerialCapturedInInventory=\"\" SerialCapturedInShipping=\"\" SerialCapturedInReturns=\"\" /><InventoryParameters/></Item>");
							
							YFCDocument itemDoc = YFCDocument.createDocument("Item");
							YFCElement itemInXML = itemDoc.getDocumentElement();
							itemInXML.setAttribute("ItemID",itemID);
							itemInXML.setAttribute("UnitOfMeasure",invUOM);
							itemInXML.setAttribute("OrganizationCode",sInvOrgCode);
							itemInXML.setAttribute("Node", getValue("CurrentUser","xml:CurrentUser:/User/@Node"));
							%>
							<yfc:callAPI apiName="getNodeItemDetails" inputElement='<%=itemInXML%>' 
							templateElement='<%=tempDocForItem.getDocumentElement()%>'/>
							<%
							errorDesc=checkForError() ;	
							if(errorDesc==null){
								isTagTracked= getValue("Item","xml:/Item/@TagCapturedInInventory");
								serialTracked= getValue("Item","xml:/Item/InventoryParameters/@IsSerialTracked");
								timeSensitive = getValue("Item","xml:/Item/InventoryParameters/@TimeSensitive");
								displayItemId=getValue("Item","xml:/Item/@DisplayItemId");
								sDisplayItemDesc=getValue("Item","xml:/Item/PrimaryInformation/@DisplayItemDescription");
								itemMap.put("DisplayItemId", displayItemId);
								itemMap.put("DisplayItemDescription", sDisplayItemDesc);
								itemMap.put("UnitOfMeasure", invUOM);
								itemMap.put("TagControlFlag",isTagTracked);
								itemMap.put("SerialTracked", serialTracked);
								itemMap.put("TimeSensitive", timeSensitive);

								try {	
									replaceInTempQ("Inventory",String.valueOf("1"), itemMap);
								}catch (Exception ee) {
									errorDesc="Mobile_Session_failed_while_loading_UOM";
								}
							} 
							if( errorDesc == null ) {
								request.setAttribute("ItemId", itemID);
								request.setAttribute("DisplayItemId", displayItemId);
								request.setAttribute("DisplayItemDescription", sDisplayItemDesc);
								if (!bVarianceTask) { // if count task .. go to quantity entry screen.............
									//check for task switching switch with Item Id Context									
								%>								 
									<jsp:include page="/rf/wms/count/count_task_switch.jsp" flush="false">
										<jsp:param name="TaskId" value='<%=sTaskId%>'/>
										<jsp:param name="OrgCode" value='<%=sInventoryOrgCode%>'/>
										<jsp:param name="ItemId" value='<%=itemID%>'/>
										<jsp:param name="DisplayItemId" value='<%=displayItemId%>'/>
										<jsp:param name="DisplayItemDescription" value='<%=sDisplayItemDesc%>'/>
										<jsp:param name="UOM" value='<%=invUOM%>'/>
										<jsp:param name="CountQty" value='<%=String.valueOf(dCountQty)%>'/>
										<jsp:param name="CaseId" value='<%=sRecentCaseId%>'/>
										<jsp:param name="PalletId" value='<%=sRecentPalletId%>'/>
										<jsp:param name="Scanned2DBarCode" value='<%=sScanned2DBarCode%>'/>
									</jsp:include>		
								<%
								}else{ // if task is variance task.. check for tag control and serial control flag
								%>
									<jsp:include page="/rf/wms/count/count_task_switch.jsp" flush="false">
										<jsp:param name="TaskId" value='<%=sTaskId%>'/>
										<jsp:param name="OrgCode" value='<%=sInventoryOrgCode%>'/>
										<jsp:param name="ItemId" value='<%=itemID%>'/>
										<jsp:param name="DisplayItemId" value='<%=displayItemId%>'/>
										<jsp:param name="DisplayItemDescription" value='<%=sDisplayItemDesc%>'/>
										<jsp:param name="UOM" value='<%=invUOM%>'/>
										<jsp:param name="CaseId" value='<%=sRecentCaseId%>'/>
										<jsp:param name="PalletId" value='<%=sRecentPalletId%>'/>
										<jsp:param name="IsVarianceTask" value="Y"/>
									</jsp:include>		
								<%
									if(!isVoid(request.getAttribute("hasError"))){
										errorDesc = "Mobile_Scanned_field_doesnt_match";
									}
									if(errorDesc == null && ("Y".equals(timeSensitive))) {
										forwardPage= checkExtension(entitybase + "/" + "frmCountSKUShipByDateEntry.jsp" );
										%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
									}else if(errorDesc == null && (("Y".equals(isTagTracked)) || ("S".equals(isTagTracked)))){	
										YFCDocument outDoc = YFCDocument.createDocument("NodeInventory");
										YFCElement inXML = outDoc.getDocumentElement();
										inXML.setAttribute("Node", countResult.getAttribute("Node"));
										inXML.setAttribute("LocationId", locationElem.getAttribute("LocationId"));
										YFCElement barElement = inXML.createChild("Inventory");
										YFCElement inventoryItemElem = barElement.createChild("InventoryItem");
										inventoryItemElem.setAttribute("ItemID", itemID);
										inventoryItemElem.setAttribute("UnitOfMeasure",  invUOM);
							
										%><yfc:callAPI apiName="getNodeInventory" inputElement='<%=inXML%>' /><%	
										errorDesc=checkForError() ;	
										
										if (errorDesc==null) {								
											String sTagNumber = null;
											double dTotNoOfRecords = getNumericValue("xml:/NodeInventory/LocationInventoryList/@TotalNumberOfRecords");
											if (dTotNoOfRecords  == 1) {
												String sMultipleSummaryAttributes = getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/@MultipleSummaryAttributes"); 
												if( equals(sMultipleSummaryAttributes,"Y")) {
													sTagNumber = getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/TagSummary/@TagNumber"); 
												}
											}
											if (sTagNumber == null || isVoid(sTagNumber)){			
												forwardPage=checkExtension(entitybase + "/" + "frmCountUpdateInitializeForTag.jsp" );
												%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
											}else {
												itemMap.put("TagNumber", sTagNumber);
												try {	
													replaceInTempQ("Inventory",String.valueOf("1"), itemMap);
												}catch (Exception ee) {
													errorDesc="Mobile_Session_failed_while_loading_Tag_Number"; 
												}
											}									
										}
									}
									
									// If item is serial tracked.....
									if(errorDesc == null && equals(serialTracked,"Y")) {									
										forwardPage= checkExtension(entitybase + "/" + "frmCountSKUSerialScan.jsp" );
										%><jsp:forward page='<%=forwardPage%>' >
										<jsp:param name="DisplayItemId" value='<%=displayItemId%>'/>
										</jsp:forward><%									
									}

									//if Item is neither serial tracked not tag tracked.....									
									if (errorDesc == null) {																				
										request.setAttribute("xml:/Inventory/@ItemID", itemID);	
										request.setAttribute("xml:/Inventory/@DiplayItemId", displayItemId);
										request.setAttribute("xml:/Inventory/@DiplayItemDescription", sDisplayItemDesc);
										request.setAttribute("xml:/Inventory/@UnitOfMeasure", invUOM);
										request.setAttribute("xml:/Inventory/@CatalogOrganizationCode", sInvOrgCode);
										request.setAttribute("xml:/RecordCountResult/@TaskType", countResult.getAttribute("TaskType")); 
										forwardPage=checkExtension(entitybase + "/" + "frmCountInventoryAttributesEntry.jsp") ;
										%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
if (errorDesc != null ){
	String errorXML = getErrorXML(errorDesc, errorField);
	%><%=errorXML%><%
}
%>


