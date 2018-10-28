<%@ include file="/yfc/rfutil.jspf" %>

<%
	// clear the existing TempQ
	clearTempQ();
	String formName = "/frmRFIHeader";	
	// call the Html			
	out.println(sendForm(formName, ""));			
%>