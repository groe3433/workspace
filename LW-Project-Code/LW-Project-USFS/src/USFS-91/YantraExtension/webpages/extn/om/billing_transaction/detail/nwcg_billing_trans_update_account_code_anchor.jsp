<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td>
        <yfc:makeXMLInput name="SeqKey">
            <yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey"/>
		</yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("SeqKey")%>' name="BillingReviewEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/NWCGBillingTransaction/@SequenceKey")%>/>
		 <input type="hidden" <%=getTextOptions("xml:/NWCGBillingTransaction/@TransactionNo")%>/>
    </td>
</tr>
<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
    </td>
</tr>
</table>