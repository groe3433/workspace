<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmNRFIReturnList.jsp");

	YFCElement TempQ = (YFCElement)((getTempQ()).getElementsByTagName("TempQ")).item(0);
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {
		request.setAttribute("TempQ",TempQ);
%>
		<yfc:callAPI apiID='RL' />
<%
		String formName = "/frmNRFIList" ;	
		out.println(sendForm(formName,"gridReturnItemList")) ;
	} else {
		String formName = "/frmNRFILinesInit" ;
		out.println(sendForm(formName,"")) ;
	}
%>