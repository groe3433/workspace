<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmNRFILinesInit.jsp");
	HashMap receiptHeaderMap= new HashMap();

	//get existing TempQ
	String strIncNo = resolveValue("xml:/Receipt/@IncNo");
%>
	<yfc:callAPI apiID='VI'/>
<%	
	String incName = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentName");
	String incNo = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentNo");

	if(!strIncNo.equals("")) {	
		if(!incName.equalsIgnoreCase("") && !incNo.equalsIgnoreCase("")) {
			YFCDocument yinTempdoc = getTempQ();
			receiptHeaderMap.put("CacheID", resolveValue("xml:CurrentUser:/User/@Node"));
			receiptHeaderMap.put("ReceiptNo", resolveValue("xml:/NextReceiptNo/@ReceiptNo"));
			receiptHeaderMap.put("IncidentYear", resolveValue("xml:/Receipt/@IncYear"));
			receiptHeaderMap.put("IncidentNo", resolveValue("xml:/Receipt/@IncNo"));
			receiptHeaderMap.put("CustomerId", resolveValue("xml:/Receipt/@CustomerId"));
			receiptHeaderMap.put("IncidentName", resolveValue("xml:/Receipt/@IncidentName"));
			receiptHeaderMap.put("ReturnHeaderNotes", resolveValue("xml:/Receipt/@Notes"));
			receiptHeaderMap.put("ReceivingDock","RETURN-1");
			receiptHeaderMap.put("AITReturns","Y");

			// Add new receipt header to TempQ
			addToTempQ("Receipt","1",receiptHeaderMap, false);
			String formName = "/frmNRFILinesInit" ;	
			
			// call the html file		
			out.println(sendForm(formName, "")) ;	
		} else {
			String errorDescs = "Invalid Ic No or Year";
			String errorXML = getErrorXML(errorDescs, "");
%>
			<%=errorXML%>
<%
		}
	} else {
		String errorDesc = "Enter Inc No";
		String errorXML = getErrorXML(errorDesc, "");
%>
		<%=errorXML%>
<%
	}	
%>