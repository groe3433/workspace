<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	// call API to validate the LPN no.
%>
	<yfc:callAPI apiID='VL'/>
<%
	String strLPNNo = resolveValue("xml:/LPNs/LPN/@PalletId");
	if(strLPNNo != null && strLPNNo != "") {
		String errorDesc = "Invalid LPN No";
		String errorField = "txtLPNNo";
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<%
	} else {
		strLPNNo=getParameter("xml:/LPN/@LPNNo");
		addLPN(strLPNNo);
		String formName = "/frmNRFILPN" ;
		YFCDocument formDoc = getForm(formName) ; 
		out.println(sendForm(formDoc, "",true)) ;
	}
%>