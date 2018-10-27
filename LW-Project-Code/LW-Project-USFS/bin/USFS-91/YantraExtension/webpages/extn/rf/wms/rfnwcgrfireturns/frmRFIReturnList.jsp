<%@ include file="/yfc/rfutil.jspf" %>

<%
	YFCElement TempQ = (YFCElement)((getTempQ()).getElementsByTagName("TempQ")).item(0);
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {
		request.setAttribute("TempQ",TempQ);
%>
		<yfc:callAPI apiID='RL' />
<%
		String formName = "/frmRFIList" ;	
		out.println(sendForm(formName,"gridReturnItemList")) ;
	} else {
		String formName = "/frmRFILinesInit" ;
		out.println(sendForm(formName,"")) ;
	}	
%>
