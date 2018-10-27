<%@ include file="/yfc/rfutil.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>
<%
    String entitybase= getParameter("entitybase");
    String forwardPage= "";
    HashMap itemMap= new HashMap();
    String errorDesc = null;
    String errorField = null;
    String formName = null;
	String sInvStatus = getParameter("xml:/Inventory/@InventoryStatus");
	String sProClass = getParameter("xml:/Inventory/@ProductClass");

	if(isVoid(sInvStatus))
		sInvStatus = "RFI";
	if(isVoid(sProClass))
		sProClass = "Supply";

	YFCDocument yinTempdoc = getTempQ();
    YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");
	YFCElement locElem= (YFCElement)((getTempQ()).getElementsByTagName("Location")).item(0);

	YFCDocument ydoc = getTempQ();
    YFCElement itemElem= getStoredElement(ydoc,"Inventory", String.valueOf("1"));
	if(itemElem != null) {
        itemMap=new HashMap(itemElem.getAttributes());
		// CR - 492 Count Flow
		if(isVoid(sInvStatus)){
			request.setAttribute("xml:/Inventory/@InventoryStatus", itemMap.get("InventoryStatus"));
			sInvStatus = getParameter("xml:/Inventory/@InventoryStatus");
		}
		if(isVoid(sProClass)){
			request.setAttribute("xml:/Inventory/@ProductClass", itemMap.get("ProductClass"));
			sProClass = getParameter("xml:/Inventory/@ProductClass");
		}
	}

	if(isVoid(sInvStatus)){
		errorDesc = "Mobile_Inventory_Status_Is_Mandatory";
		errorField="txtInvStatus";
	}else if(isVoid(sProClass)){
		errorDesc = "Mobile_Product_Class_Is_Mandatory";
		errorField="txtProClass";
	}

	if (errorDesc == null) {
		itemMap.put("InventoryStatus", sInvStatus);
		itemMap.put("ProductClass", sProClass);
		try {
			addToTempQ("Inventory",String.valueOf("1"), itemMap, false);
		} catch(Exception e) {
			try {
				replaceInTempQ("Inventory", String.valueOf("1"), itemMap);
			} catch(Exception ee) {
				errorDesc="Mobile_Session_Failed";
			}
		}	
	}
	if(errorDesc == null){
		request.setAttribute("xml:/Inventory/@ItemID", itemMap.get("ItemID"));
		request.setAttribute("xml:/Inventory/@DisplayItemId", itemMap.get("DisplayItemId"));
		request.setAttribute("xml:/Inventory/@UnitOfMeasure", itemMap.get("UnitOfMeasure"));
		forwardPage= entitybase + "/" + "frmCountSKUQtyEntry.jsp" ;
		forwardPage=checkExtension(forwardPage);
		%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%				
	}
   
    if(errorDesc != null ) {
        String errorXML = getErrorXML(errorDesc, errorField);
        %><%=errorXML%><%
    }
%>
