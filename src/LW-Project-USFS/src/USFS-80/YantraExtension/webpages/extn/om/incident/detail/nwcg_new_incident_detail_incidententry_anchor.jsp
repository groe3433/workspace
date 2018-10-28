<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td colspan="2">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="2">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I06"/>
<jsp:param name="Path" value="xml:/NWCGIncidentOrder/@"/>
<jsp:param name="RenderReadOnly" value="true"/>
</jsp:include>
</td>
</tr>
<tr>
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="Path" value="xml:/NWCGIncidentOrder/YFSPersonInfoShipTo"/>
<jsp:param name="DataXML" value="NWCGIncidentOrder"/>
<jsp:param name="AllowedModValue" value='Y'/>
</jsp:include>
</td>
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
<jsp:param name="Path" value="xml:/NWCGIncidentOrder/YFSPersonInfoBillTo"/>
<jsp:param name="DataXML" value="NWCGIncidentOrder"/>
<jsp:param name="AllowedModValue" value='Y'/>
</jsp:include>
</td>
</tr>
<tr>
<td height="100%" width="50%" addressip="true">
<%-- <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
<jsp:param name="Path" value="xml:/NWCGIncidentOrder/YFSPersonInfoDeliverTo"/>
<jsp:param name="DataXML" value="NWCGIncidentOrder"/>
<jsp:param name="AllowedModValue" value='Y'/> --%>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
</jsp:include>
</td>

<!-- additional info view -->
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I05"/>
<jsp:param name="DataXML" value="NWCGIncidentOrder"/>
<jsp:param name="AllowedModValue" value='Y'/>
</jsp:include>
</td>

</tr>
</table>

