<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%  
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String createView = resolveValue("xml:/WorkOrder/@WorkOrderMode");
    createView = createView == null ? "" : createView;

	String serviceItemGroupCode=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode");
%>
<table class="view" width="100%">
	<tr>
        <td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
		<% if(modifyView != "") {%> 
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@PalletId"/></td>
		<%}
		else
		{%>
			<td nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@PalletId")%>/>
			</td>
		<%}%>
        <td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
		<% if(modifyView != "") {%> 
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@CaseId"/></td>
		<%}
		else
		{%>
			<td nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@CaseId")%>/>
			</td>
		<%}
		if(!equals("COMPL", serviceItemGroupCode)){%>
			<td class="detaillabel" ><yfc:i18n>Serial_#</yfc:i18n></td>
			<% if(modifyView != "") {%> 
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@SerialNo"/></td>
			<%}
			else
			{%>
				<td nowrap="true" >
					<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@SerialNo")%>/>
				</td>
			<%}
		}%>
	</tr>
</table>
