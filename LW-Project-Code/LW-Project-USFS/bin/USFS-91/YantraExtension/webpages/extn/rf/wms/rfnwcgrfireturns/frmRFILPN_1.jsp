<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	String strLPNNo=getParameter("xml:/LPN/@LPNNo");
	String formName = "/frmRFILPN" ;	
	out.println(sendForm(formName, "")) ;
%>