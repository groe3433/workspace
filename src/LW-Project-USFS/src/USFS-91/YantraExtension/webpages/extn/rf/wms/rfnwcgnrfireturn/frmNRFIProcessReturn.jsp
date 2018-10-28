<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	YFCNodeList receiptLines = (getTempQ()).getElementsByTagName("ReceiptLine");
	int rLineNLLen = receiptLines.getLength();
	if(rLineNLLen > 0) {	
		YFCElement TempQ = (YFCElement)((getTempQ()).getElementsByTagName("TempQ")).item(0);
		request.setAttribute("TempQ",TempQ);
%>
		<yfc:callAPI apiID='CS' />
<%
		String ValidReturnProcess = resolveValue("xml:/Receipt/@ErrorMessage");
		if(ValidReturnProcess != null && !ValidReturnProcess.equals("")) {
			String errorDesc = ValidReturnProcess;
			String errorXML = getErrorXML(errorDesc, "");
%>
			<%=errorXML%>
<%		
		} else {
			String formName = "/frmNRFIProcessReturn" ;			
			out.println(sendForm(formName, "")) ;
		}
	} else {
		String errorDes = "No Item Found";
		String errorFiel = "";
		String errorXM = getErrorXML(errorDes, errorFiel);
%>
		<%=errorXM%>
<%
	}
%>