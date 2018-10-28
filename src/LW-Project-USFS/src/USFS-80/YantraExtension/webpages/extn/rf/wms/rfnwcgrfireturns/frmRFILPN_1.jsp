<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFILPN_1.jsp");
	String strLPNNo=getParameter("xml:/LPN/@LPNNo");
	String formName = "/frmRFILPN" ;	
	out.println(sendForm(formName, "")) ;
%>