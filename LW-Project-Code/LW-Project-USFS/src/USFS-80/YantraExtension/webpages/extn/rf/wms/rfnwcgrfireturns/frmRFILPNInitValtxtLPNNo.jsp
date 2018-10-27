<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFILPNInitValtxtLPNNo.jsp");
	// call API to validate the LPN no.
%>
	<yfc:callAPI apiID='VLN'/>
<%
	String strLPNNo = resolveValue("xml:/LPNs/LPN/@CaseId");
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
		String formName = "/frmRFILPN" ;
		YFCDocument formDoc = getForm(formName) ;
		out.println(sendForm(formDoc, "",true)) ;
	}
%>