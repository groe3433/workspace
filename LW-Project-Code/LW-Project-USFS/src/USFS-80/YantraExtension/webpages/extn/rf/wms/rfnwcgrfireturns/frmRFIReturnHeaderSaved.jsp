<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFIReturnHeaderSaved.jsp");
	// clear the existing TempQ
	clearTempQ();
	String formName = "/frmRFIHeader";	
	// call the Html			
	out.println(sendForm(formName, ""));			
%>