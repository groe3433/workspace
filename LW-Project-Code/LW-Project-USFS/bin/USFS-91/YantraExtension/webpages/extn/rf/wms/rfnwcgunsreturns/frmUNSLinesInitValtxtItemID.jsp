<%@ include file="/yfc/rfutil.jspf" %>

<%	
	String sItemID=getParameter("xml:/ReturnLine/@ItemID").trim();
	String totalNoOfLines = "0";
%>
	<yfc:callAPI apiID='IT'/>
<%
	String sIsSerialTracked = resolveValue("xml:/ItemList/Item/InventoryParameters/@IsSerialTracked");
	String formName = "/frmUNSLinesInit" ;	
	YFCDocument ydoc = getForm("/frmUNSLinesInit");
	
	if(sIsSerialTracked.equalsIgnoreCase("Y")) {
		formName = "/frmUNSLinesTrkID" ;
	 	ydoc=getForm("/frmUNSLinesTrkID");
	} else {
		// calculate total no of lines 
		formName = "/frmUNSLines" ;	
	}
	
	String sValidateMyItemID = "";
	YFCDocument myItemInputDoc = YFCDocument.parse("<InventoryItem ItemID=\"" + sItemID + "\" OrganizationCode=\"NWCG\"></InventoryItem>");
	YFCDocument myItemTemplateDoc = YFCDocument.parse("<InventoryList><InventoryItem><Item ItemID=\"\"></Item></InventoryItem></InventoryList>");
%>
	<yfc:callAPI apiName="getInventoryItemList" inputElement='<%=myItemInputDoc.getDocumentElement()%>' templateElement="<%=myItemTemplateDoc.getDocumentElement()%>" outputNamespace='InventoryList'/>
<%	
	sValidateMyItemID = getValue("InventoryList", "xml:/InventoryList/InventoryItem/Item/@ItemID");
	
	if(sValidateMyItemID.equals("")) {
		String errorDesc = "Invalid Item ID - Item ID NOT in System";
		String errorField = "txtItemID";
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<%
	} else if(sItemID.equals("")) {
		String errorDesc = "Invalid Item ID - Please enter more than just spaces";
		String errorField = "txtItemID";
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<%
	} else {
		YFCNodeList rlNL = (getTempQ()).getElementsByTagName("ReceiptLine");
		if(rlNL != null) {
			totalNoOfLines = Integer.toString(rlNL.getLength());
		} 
		ydoc=getForm(formName);
		YFCElement dropoffLocationElem = getField(ydoc,"lblTotalLinesVal");
		dropoffLocationElem.setAttribute("value",totalNoOfLines);
		if(formName.equalsIgnoreCase("/frmUNSLines")) {
			out.println(sendForm(ydoc, "txtQty",true));	
		} else {
			out.println(sendForm(ydoc, "",true)) ;
		}
	}
%>