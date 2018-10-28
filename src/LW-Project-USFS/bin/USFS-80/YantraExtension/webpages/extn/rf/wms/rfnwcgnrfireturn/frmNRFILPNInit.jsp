<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmNRFILPNInit.jsp");
	// call API to generate the LPN no.
%>
	<yfc:callAPI apiID='LP'/>
<%
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {	
		String LPN = resolveValue("xml:/NextReceiptNo/@ReceiptNo");
		String formName = "/frmNRFILPNInit" ;	
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