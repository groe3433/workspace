<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmUNSNWTDeleteFromTempQ.jsp");

	String sItemID=getParameter("xml:/Item/@ItemID");
	String sLPNNo=getParameter("xml:/Item/@LPNNo");
	String sQuant=getParameter("xml:/Item/@Quant");
	deleteItemFromList(sItemID,sQuant,sLPNNo);
	String forwardPage = "frmUNSNWTReturnList.jsp" ;	
%>

<jsp:forward page='<%=forwardPage%>' ></jsp:forward>