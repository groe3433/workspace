<%@ include file="/yfc/rfutil.jspf" %>

<%
	// call API to generate the receipt no.
%>
	<yfc:callAPI apiID='GR'/>
<%
	// clear existing tempQ
	clearTempQ();
	String formName = "/frmNRFIHeader" ;	
	// call html file
	out.println(sendForm(formName, "txtIncYear")) ;			
%>