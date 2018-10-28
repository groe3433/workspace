<%@ include file="/yfc/NWCGrfutil.jspf" %>

<%
	String sItemID=getParameter("xml:/Item/@ItemID");
	String sLPNNo=getParameter("xml:/Item/@LPNNo");
	String sQuant=getParameter("xml:/Item/@Quant");
	deleteItemFromList(sItemID,sQuant,sLPNNo);
	String forwardPage = "frmNRFIReturnList.jsp" ;	
%>

<jsp:forward page='<%=forwardPage%>' ></jsp:forward>