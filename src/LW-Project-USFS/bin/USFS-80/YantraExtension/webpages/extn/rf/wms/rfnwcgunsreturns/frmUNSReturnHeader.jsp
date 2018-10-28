<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmUNSReturnHeader.jsp");
	// call API to generate the receipt no.
%>
	<yfc:callAPI apiID='GR'/>
<%
	// clear existing tempQ
	clearTempQ();
	String formName = "/frmUNSHeader" ;	
	// call html file
	out.println(sendForm(formName, "txtIncYear")) ;			
%>