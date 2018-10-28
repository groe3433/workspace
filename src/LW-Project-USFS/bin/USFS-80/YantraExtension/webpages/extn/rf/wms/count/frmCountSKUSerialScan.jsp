<%@ page import="java.util.*" %>
<%@ include file="/yfc/rfutil.jspf" %>

<%
	String formName="/frmCountSKUSerialScan";
	String entitybase=getParameter("entitybase");
	String errorDesc = null ;
	String errorField = null ;
	String sItemId = "";
	String sLotNumber = "";

	YFCDocument ydoc = getForm(formName);
	Map itemMap = new HashMap(); 
	Map serialMap = new HashMap();

	YFCDocument itemDoc = YFCDocument.createDocument("Item");
	YFCElement itemInXML = itemDoc.getDocumentElement();

	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");

	request.setAttribute("RecordCountResult", countResult);
	request.setAttribute("xml:/Inventory/@DisplayItemId", getParameter("DisplayItemId"));

	YFCDocument tempDocForItem = YFCDocument.parse("<Item><PrimaryInformation NumSecondarySerials=\"\" SerialCapturedInReceiving=\"\" SerialCapturedInInventory=\"\" SerialCapturedInShipping=\"\" SerialCapturedInReturns=\"\"/><InventoryParameters IsSerialTracked=\"\" /></Item>");

	YFCElement bcSerialElem= getStoredElement(getTempQ(),"BC_SerialDetail", String.valueOf("1"));
	YFCElement itemElem= getStoredElement(getTempQ(),"Inventory",String.valueOf("1")); 
	YFCElement serialRange = getStoredElement(getTempQ(),"SerialRange",String.valueOf("1"));

	// -- Top of CR 492 - add an element to TagAttributes node if any available
	// -- first check in ScannedTagAttribute node to see if any value is available
	YFCElement scannedTagAttrElem = (YFCElement)((getTempQ()).getElementsByTagName("ScannedTagAttribute")).item(0);
	if(scannedTagAttrElem != null){
		String sAttrValue = scannedTagAttrElem.getAttribute("value");
		if(!isVoid(sAttrValue)){
			// add this attribute to TagAttributes node
			YFCElement tagAttributesElem= getStoredElement(getTempQ(),"TagAttributes",String.valueOf("1"));
			if(tagAttributesElem != null){
				tagAttributesElem.setAttribute(sAttrValue,"");
				deleteAllFromTempQ("ScannedTagAttribute");        //remove ScannedTagAttribute node
				deleteAllFromTempQ("InventoryTagAttributes");     //remove InventoryTagAttributes node
				deleteAllFromTempQ("InventoryTagExtnAttributes"); //remove InventoryTagExtnAttributes node
			}
		}
	}
	// -- Bottom of CR 492

	if(itemElem != null){
		sItemId = itemElem.getAttribute("ItemID");
		itemInXML.setAttribute("ItemID",itemElem.getAttribute("ItemID"));
		itemInXML.setAttribute("UnitOfMeasure",itemElem.getAttribute("UnitOfMeasure"));
		itemInXML.setAttribute("OrganizationCode",itemElem.getAttribute("OrganizationCode"));
		itemInXML.setAttribute("Node",getValue("CurrentUser","xml:CurrentUser:/User/@Node"));
		itemMap = new HashMap(itemElem.getAttributes());
	}
		
	%>
	<yfc:callAPI apiName="getNodeItemDetails" inputElement='<%=itemInXML%>' 
									templateElement='<%=tempDocForItem.getDocumentElement()%>'/>
	<%
	int numSecondarySerials = Integer.parseInt(getValue("Item","xml:/Item/PrimaryInformation/@NumSecondarySerials"));
	// CR 492 - disable Serial Range Entry button - done in form html
	/* if(numSecondarySerials > 0){
		YFCElement eToggleEntry = getField(ydoc,"ToggleEntry");
		eToggleEntry.setAttribute("type","hidden");
		eToggleEntry.setAttribute("subtype","Hidden");	
	 } */

	errorDesc=checkForError();	
	if(errorDesc==null)
	{
		boolean serialTracked=equals("Y",getValue("Item","xml:/Item/InventoryParameters/@IsSerialTracked"));
		if(serialTracked){
			if(bcSerialElem != null) {
				itemMap.put("CountQuantity",String.valueOf(1));
				serialMap = new HashMap(bcSerialElem.getAttributes());
				serialMap.remove("value");
				serialMap.put("ItemID",sItemId);
				serialMap.put("ItemValue",String.valueOf("1"));
				try{
					addToTempQ("SerialDetail",  bcSerialElem.getAttribute("SerialNo"), serialMap, false) ;
				}catch(Exception e){		
					errorDesc="Mobile_Duplicate_Serial";		
				}
				if(errorDesc == null) {
					try{
						replaceInTempQ("Inventory", String.valueOf("1"),itemMap);
					}catch(Exception e){		
						errorDesc="Mobile_Session_Error";		
					}
				}
				
				if(errorDesc == null){
					String forwardPage=checkExtension(entitybase + "/" + "frmCountInventoryAttributesEntry.jsp") ;
					%>
					<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
					<%
				}
			}
			if(errorDesc == null){
				out.println(sendForm(ydoc, "txtSerialNo", true)) ;
			}
		}else{
			String forwardPage=checkExtension(entitybase + "/" + "frmCountInventoryAttributesEntry.jsp") ;
			%>
			<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
			<%
		}
	}

	if (errorDesc!= null ) {
		String errorXML=getErrorXML(errorDesc,errorField);
		%><%=errorXML%><%
	}%>
