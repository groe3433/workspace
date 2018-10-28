<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmNRFIReturnHeaderSaved.jsp");

	// clear the existing TempQ
	clearTempQ();
	String formName = "/frmNRFIHeader" ;	
	// call the Html ;			
	out.println(sendForm(formName, "")) ;			
%>