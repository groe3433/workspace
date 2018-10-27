<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table cellSpacing=0 id="anchortable" class="anchor" cellpadding="7px">
<tr>
    <td>
		<input type="hidden" name="xml:/Order/@OrderHeaderKey" id="xml:/Order/@OrderHeaderKey" value="<%=resolveValue("xml:/Order/@OrderHeaderKey")%>"/>
		<input type="hidden" name="xml:/RossInfo/@MessageSelected" id="xml:/RossInfo/@MessageSelected" value="<%=resolveValue("xml:/RossInfo/@MsgType")%>"/>
		<input type="hidden" name="xml:/RossInfo/@OrderType" id="xml:/RossInfo/@OrderType" value="<%=resolveValue("xml:/RossInfo/@OrderType") %>" />

	</td>
</tr>
<tr>
    <td colspan="2">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
        	<jsp:param name="CurrentInnerPanelID" value="I01"/>
      </jsp:include>
    </td>
</tr>

<% //System.out.println("OrderHeaderKey ==> "+ resolveValue("xml:/Order/@OrderHeaderKey"));	
   if (!equals(resolveValue("xml:/Order/@OrderHeaderKey"),"")) { //add following inner panel only if the order header key has been returned %>
	<tr>
		<td colspan="2">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<% //System.out.println("MessageSelected ==> "+ resolveValue("xml:/RossInfo/@MessageSelected"));
				if (equals(resolveValue("xml:/RossInfo/@MessageSelected"),"CreateRequest")){ //If Createrequest then readonly %>
				<jsp:param name="RenderReadOnly" value="true"/>
				<jsp:param name="AllowedModValue" value='N'/>
				<% } %>
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
<% } %>




</table>