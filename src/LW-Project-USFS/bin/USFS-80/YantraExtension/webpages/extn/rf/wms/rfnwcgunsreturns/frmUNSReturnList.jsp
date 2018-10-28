<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmUNSReturnList.jsp");

	YFCElement TempQ = (YFCElement)((getTempQ()).getElementsByTagName("TempQ")).item(0);
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {
		request.setAttribute("TempQ",TempQ);
%>
		<yfc:callAPI apiID='RI' />
<%
		String formName = "/frmUNSList" ;	
		out.println(sendForm(formName,"gridReturnItemList")) ;
	} else {
		String formName = "/frmUNSLinesInit" ;
		out.println(sendForm(formName,"")) ;
	}	
%>