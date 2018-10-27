<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmUNSAddItemInput.jsp");

	removeCountTrackId();
	YFCDocument doc=getForm("/frmUNSLinesInit");
	YFCNodeList lsLine = (getTempQ()).getElementsByTagName("ReceiptLine");
	if(lsLine != null) {
		String totalNoOfLines = Integer.toString(lsLine.getLength());
		YFCElement dropoffLocationElem = getField(doc,"lblTotalLinesVal");
		dropoffLocationElem.setAttribute("value",totalNoOfLines);
		YFCElement eleItemID = getField(doc,"txtItemID");
		eleItemID.setAttribute("value"," ");
	}
	out.println(sendForm(doc, "",true)) ;
%>