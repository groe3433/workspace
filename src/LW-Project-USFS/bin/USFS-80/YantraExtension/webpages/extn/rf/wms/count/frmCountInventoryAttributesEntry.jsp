<%@ include file="/yfc/rfutil.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>

<%
    String formName = "/frmCountInventoryAttributesEntry" ;
	String sProductClass = "";
	String sInventoryStatus = "",displayItemId="",sDisplayItemDesc="";
	String sLocationId = "",sPalletId = "",sCaseId = "";
	HashSet ProductClassSet= new HashSet();
	HashSet InvStatusSet= new HashSet();
	String errorDesc = null ;
	String errorField = null ;
	String entitybase= getParameter("entitybase") ;
	String forwardPage="";


	YFCDocument ydoc = getTempQ();
	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
    int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");
	YFCElement itemElem= (YFCElement)((getTempQ()).getElementsByTagName("Inventory")).item(0);
	YFCElement bcItemElem= getStoredElement(ydoc,"BC_ItemContextualInfo", String.valueOf("1"));
	YFCElement bcInvElem= getStoredElement(ydoc,"BC_Inventory", String.valueOf("1"));
	YFCElement taskElem= (YFCElement)((getTempQ()).getElementsByTagName("Task")).item(0);
	YFCElement locationElem= (YFCElement)((getTempQ()).getElementsByTagName("Location")).item(0);	
	if(locationElem != null) {
		sLocationId = locationElem.getAttribute("LocationId");
	}
	// 2D bar code handling for PC and Inventory Status...
	if(bcItemElem != null){
		sProductClass = bcItemElem.getAttribute("ProductClass");
	}
	if(bcInvElem != null){
		sInventoryStatus = bcInvElem.getAttribute("InventoryStatus");
	}
	
	if(itemElem != null){
		displayItemId = itemElem.getAttribute("DisplayItemId");
		sDisplayItemDesc = itemElem.getAttribute("DisplayItemDescription");
		sPalletId = itemElem.getAttribute("PalletId");
		sCaseId = itemElem.getAttribute("CaseId");
	}
	if(!isVoid(sProductClass) && !isVoid(sInventoryStatus)){
		if(itemElem != null) {
			HashMap itemMap= new HashMap(itemElem.getAttributes());
			itemMap.put("ProductClass", sProductClass);
			itemMap.put("InventoryStatus", sInventoryStatus);
			try {
				replaceInTempQ("Inventory", String.valueOf("1"),itemMap);
			}catch(Exception ee){
				errorDesc="Mobile_Duplicate_Values";
			}
			if(errorDesc == null){
				request.setAttribute("xml:/Inventory/@ItemID", itemMap.get("ItemID"));
				request.setAttribute("xml:/Inventory/@DisplayItemId", displayItemId);
				request.setAttribute("xml:/Inventory/@DisplayItemDescription", sDisplayItemDesc);
				request.setAttribute("xml:/Inventory/@UnitOfMeasure", itemMap.get("UnitOfMeasure"));
				forwardPage= entitybase + "/" + "frmCountSKUQtyEntry.jsp" ;
				forwardPage=checkExtension(forwardPage);
				%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%				
			}
		}
	}
	
	//if not passed in barcode get it from Task...
	if(taskElem != null) {
		if(isVoid(sProductClass)) 
			sProductClass = taskElem.getAttribute("ProductClass");
		if(isVoid(sInventoryStatus))
			sInventoryStatus = taskElem.getAttribute("InventoryStatus");
	}
	
	if(isVoid(sProductClass) || isVoid(sInventoryStatus)) {

		 YFCDocument tempDocForGetNode = YFCDocument.parse("<NodeInventory Node=\"\"><LocationInventoryList><LocationInventory LocationId=\"\"><InventoryItem ProductClass=\"\"/><ItemInventoryDetailList><ItemInventoryDetail InventoryStatus=\"\" /></ItemInventoryDetailList></LocationInventory></LocationInventoryList></NodeInventory>");
		 YFCElement getNodeTempInXML= tempDocForGetNode.getDocumentElement();
		 YFCDocument outDoc = YFCDocument.createDocument("NodeInventory");
		 YFCElement inXML = outDoc.getDocumentElement();
		 inXML.setAttribute("Node", countResult.getAttribute("Node"));
		 inXML.setAttribute("EnterpriseCode", countResult.getAttribute("InventoryOrganizationCode"));
		//if inventory is within Case/Pallet, we need to look for corresponding inventory to populate IS/PC.
		 //If inventory is counted as loose, we need to look for location inventory populate IS/PC.
		 YFCElement barElement = inXML.createChild("Inventory");
		  if (!isVoid(sCaseId)) {
			 barElement.setAttribute("CaseId",sCaseId);
		 }else if (!isVoid(sPalletId)) {
			 barElement.setAttribute("PalletId",sPalletId);
		 }else{
			 inXML.setAttribute("LocationId", sLocationId);
		 }
		 YFCElement inventoryItemElem = barElement.createChild("InventoryItem");
		 inventoryItemElem.setAttribute("ItemID",itemElem.getAttribute("ItemID"));
		 inventoryItemElem.setAttribute("UnitOfMeasure",itemElem.getAttribute("UnitOfMeasure"));
		 %><yfc:callAPI apiName="getNodeInventory" 
			inputElement='<%=inXML%>' 
			templateElement='<%=getNodeTempInXML%>'/><%					
			errorDesc=checkForError();
		 if(errorDesc == null) {
			YFCElement NodeInvElem =(YFCElement)request.getAttribute("NodeInventory");
			YFCElement LocnInvListElem = NodeInvElem.getChildElement("LocationInventoryList");
			YFCNodeList LocnInvList = LocnInvListElem.getElementsByTagName("LocationInventory");
			for(int i=0;i<LocnInvList.getLength();i++) {
				YFCElement LocnInvElem = (YFCElement)LocnInvList.item(i);
				YFCElement invItemElem = LocnInvElem.getChildElement("InventoryItem");
				String sProClass = invItemElem.getAttribute("ProductClass");
				ProductClassSet.add(sProClass);
				YFCElement itemDetailListElem = LocnInvElem.getChildElement("ItemInventoryDetailList");
				YFCNodeList itemDetailList = itemDetailListElem.getElementsByTagName("ItemInventoryDetail");
				for(int j=0;j<itemDetailList.getLength();j++) {
					YFCElement itemDetailElem = (YFCElement)itemDetailList.item(j);
					String sInvStatus = itemDetailElem.getAttribute("InventoryStatus");
					InvStatusSet.add(sInvStatus);
				}
			}
		 
			if(isVoid(sProductClass)){
				//CR 49408. For empty location count,  Product class will be defaulted from Catalog. For locations with multiple product class, the first in the list of product class available at the location  will be defaulted.

				if(ProductClassSet.size() == 0) {	

					YFCDocument  inputDoc= YFCDocument.parse("<Item  Node=\""+ getValue("CurrentUser","xml:CurrentUser:/User/@Node")+ "\" ItemID=\"" + itemElem.getAttribute("ItemID") +"\" CallingOrganizationCode=\"" + itemElem.getAttribute("OrganizationCode") +"\"  UnitOfMeasure= \"" + itemElem.getAttribute("UnitOfMeasure")+ "\" /> ",true);

					YFCDocument templateDoc = YFCDocument.parse("<Item><PrimaryInformation DefaultProductClass=\"\" /></Item>");
 
					 %>
						<yfc:callAPI apiName="getNodeItemDetails" inputElement='<%=inputDoc.getDocumentElement()%>' templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace='Item'/>
					 <%  

					errorDesc=checkForError() ;
					if (errorDesc == null ){
						sProductClass=getValue("Item", "xml:/Item/PrimaryInformation/@DefaultProductClass");
					}

				}else {	
					Iterator proClassItr = ProductClassSet.iterator();
					sProductClass = (String)proClassItr.next();	
				}
			}
			if(isVoid(sInventoryStatus)){
				if(InvStatusSet.size() == 0) {
					//CR 90456 - for new inventory see if default inventory status is set in properties.
					sInventoryStatus = YFCConfigurator.getInstance().getProperty("yfs.default.inventorystatus.for.newinventory","");					
					if(isVoid(sInventoryStatus)){
						//CR 49408. For empty location count,  Inventory status will be defaulted to the first in the list of WMS Inventory Statuses(got from GetInventoryStatusList API). For locations with multiple inventory statuses the first in the list of inventory statuses available at the location  will be defaulted.
						YFCDocument inputDoc= YFCDocument.parse("<InventoryStatus Node=\"" +  getValue("CurrentUser","xml:CurrentUser:/User/@Node") + "\"/>",true);
						YFCDocument templateDoc = YFCDocument.parse("<InventoryStatusList ><InventoryStatus InventoryStatus=\"\" /> </InventoryStatusList>");
	 
						 %><yfc:callAPI apiName="getInventoryStatusList" inputElement='<%=inputDoc.getDocumentElement()%>' templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace='InventoryStatusList'/><%  

						errorDesc=checkForError() ;
						if (errorDesc == null ){
							sInventoryStatus=getValue("InventoryStatusList", "xml:/InventoryStatusList/InventoryStatus/@InventoryStatus");
							sInventoryStatus="RFI"; // default it if it's not
						}
					}
				}else {	
					Iterator invStatusItr = InvStatusSet.iterator();
					sInventoryStatus = (String)invStatusItr.next();	
				}

			}
		}		
	}
	// RF - Count Flow - Consider PC, CC, and Manual??? i.e., NRFI
	if(isVoid(sInventoryStatus))
		sInventoryStatus = "RFI";
	if(isVoid(sProductClass))
		sProductClass = "Supply";
	if(!isVoid(sInventoryStatus))
		getTempQ().getDocumentElement().getChildElement("Inventory",true).setAttribute("InventoryStatus",sInventoryStatus);
	if(!isVoid(sProductClass))
		getTempQ().getDocumentElement().getChildElement("Inventory",true).setAttribute("ProductClass",sProductClass);

	if(errorDesc == null) {
		request.setAttribute("xml:/Inventory/@DisplayItemId", displayItemId);
		request.setAttribute("xml:/Inventory/@DisplayItemDescription", sDisplayItemDesc);
		request.setAttribute("xml:/Inventory/@ProductClass", sProductClass);
		request.setAttribute("xml:/Inventory/@InventoryStatus", sInventoryStatus);
		request.setAttribute("xml:/RecentLPN/@LastScannedLPN",getParameter("xml:/RecentLPN/@LastScannedLPN"));
		request.setAttribute("xml:/RecordCountResult/@TaskType",countResult.getAttribute("TaskType")); 
		out.println(sendForm(formName, "txtInvStatus"));
	}else{
		String errorXML = getErrorXML(errorDesc, errorField);
		%><%=errorXML%><%
	}
%>
