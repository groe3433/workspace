<%@ include file="/yfc/rfutil.jspf" %>

<%
	// clear the existing TempQ
	clearTempQ();
	String formName = "/frmNRFIHeader" ;	
	// call the Html ;			
	out.println(sendForm(formName, "")) ;			
%>