<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>

<%
	YFCElement inboxElem = (YFCElement) request.getAttribute("Inbox");

	String status = inboxElem.getAttribute("Status");

	if (YFCObject.equals(status,"WIP")) {
		inboxElem.setAttribute("OpenVisibleFlag",  "Y");
		inboxElem.setAttribute("WipVisibleFlag",   "N");
		inboxElem.setAttribute("ClosedVisibleFlag","Y");
	} else if (YFCObject.equals(status,"CLOSED")) {
		inboxElem.setAttribute("OpenVisibleFlag",  "N");
		inboxElem.setAttribute("WipVisibleFlag",   "N");
		inboxElem.setAttribute("ClosedVisibleFlag","N");
	} else {
		// 'OPEN' status
		inboxElem.setAttribute("OpenVisibleFlag",  "N");
		inboxElem.setAttribute("WipVisibleFlag",   "Y");
		inboxElem.setAttribute("ClosedVisibleFlag","Y");
	}
	
	String excpType = inboxElem.getAttribute("ExceptionType");
	if ((YFCObject.equals(excpType, "CreateRequestAndPlaceReq") || 
	 	 YFCObject.equals(excpType, "CreateRequestAndPlaceResp") || 
	 	 YFCObject.equals(excpType, "UpdateNFESResourceRequestReq") ||
		 YFCObject.equals(excpType, "UpdateNFESResourceRequestResp")) &&
				!(YFCObject.equals(status,"CLOSED"))){
		inboxElem.setAttribute("DisplaySendMessageToROSS", "Y");
	}
%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="2" width='100%' >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td width='50%' >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
    <td width='50%' >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="2" width='100%' >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
</table>
