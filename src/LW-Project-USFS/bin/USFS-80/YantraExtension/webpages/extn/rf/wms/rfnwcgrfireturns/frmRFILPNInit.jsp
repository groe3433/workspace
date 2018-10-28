<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFILPNInit.jsp");
	// call API to generate the LPN no.
%>
	<yfc:callAPI apiID='LPN'/>
<%
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {	
		String LPN = resolveValue("xml:/NextReceiptNo/@ReceiptNo");
		String formName = "/frmRFILPNInit" ;	
		out.println(sendForm(formName, "")) ;
	} else {
		String errorDes = "No Item Found";
		String errorFiel = "";
		String errorXM = getErrorXML(errorDes, errorFiel);
%>
		<%=errorXM%>
<%
	}	
%>