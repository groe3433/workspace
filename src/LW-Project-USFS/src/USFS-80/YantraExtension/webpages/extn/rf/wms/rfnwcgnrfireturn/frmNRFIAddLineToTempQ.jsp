<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmNRFIAddLineToTempQ.jsp");

	String sQty = resolveValue("xml:/ReturnLine/@Qty");
	String sItemID = resolveValue("xml:/ReturnLine/@ItemID");
	
	String strError ="";
	if(sItemID.equals("")) {
		strError = "Please enter Item ID";
	} else if(sQty.equals("")) {
		strError = "Please enter Qty";
	}
	
    try {
        int number = Integer.parseInt(sQty);
    } catch(NumberFormatException e) {
    	strError = "Invalid Qty Entered!";
    }
	
	if(strError.equals("")) {
		String sIsSerialTracked = resolveValue("xml:/ItemList/Item/InventoryParameters/@IsSerialTracked");
		
		// adding the return line starts here
		HashMap receiptLineMap= new HashMap();
		
		// isSeriallyTracked
		receiptLineMap.put("isSeriallyTracked", sIsSerialTracked);
		receiptLineMap.put("ItemID", resolveValue("xml:/ReturnLine/@ItemID"));
		receiptLineMap.put("ShortDescription", resolveValue("xml:/ReturnLine/@ItemDesc"));
		receiptLineMap.put("QtyReturned", resolveValue("xml:/ReturnLine/@Qty"));
		receiptLineMap.put("NRFI", resolveValue("xml:/ReturnLine/@Qty"));
		receiptLineMap.put("UOM", resolveValue("xml:/ReturnLine/@UnitOfMeasure"));
		receiptLineMap.put("ProductClass", resolveValue("xml:/ReturnLine/@ProductClass"));
		receiptLineMap.put("DispositionCode", "NRFI");		

		YFCNodeList rlNL = (getTempQ()).getElementsByTagName("ReceiptLine");
		int rlNLLen = rlNL.getLength();
		if(!sItemID.equalsIgnoreCase("")) { 
			addToTempQ("ReceiptLine",Integer.toString(rlNLLen+2),receiptLineMap, true);
		} else {
			System.out.println("!!!!! sItemID is blank ");
		}
		
		YFCDocument doc=getForm("/frmNRFILinesInit");
		YFCNodeList lsLine = (getTempQ()).getElementsByTagName("ReceiptLine");
		if(lsLine != null) {
			String totalNoOfLines = Integer.toString(lsLine.getLength());
			YFCElement dropoffLocationElem = getField(doc,"lblTotalLinesVal");
			dropoffLocationElem.setAttribute("value",totalNoOfLines);
			YFCElement eleItemID = getField(doc,"txtItemID");
			eleItemID.setAttribute("value"," ");
		} 
		
		YFCDocument returnLineDoc = YFCDocument.createDocument("ReturnLine");
		request.setAttribute("ReturnLine",returnLineDoc.getDocumentElement());
		request.setAttribute("xml:/ReturnLine/@ItemID","");
		request.setAttribute("xml:/ReturnLine/@Qty","");
		String formName = "/frmNRFILinesInit" ;				
		out.println(sendForm(doc, "",true)) ;	
	} else {
		String errorXML = getErrorXML(strError, "");
%>
		<%=errorXML%>
<%
	}				
%>