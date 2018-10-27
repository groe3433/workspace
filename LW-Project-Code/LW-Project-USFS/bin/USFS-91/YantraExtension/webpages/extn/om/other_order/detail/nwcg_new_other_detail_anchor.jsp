<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<yfc:callAPI apiID="AP1"/>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td>
<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>
<input type="hidden" value='<%=getParameter("incidentKey")%>' name="IncidentEntityKey"/>
<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentKey")%>/>
</td>
</tr>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="ModifyView" value="true"/>
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
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I07"/>
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