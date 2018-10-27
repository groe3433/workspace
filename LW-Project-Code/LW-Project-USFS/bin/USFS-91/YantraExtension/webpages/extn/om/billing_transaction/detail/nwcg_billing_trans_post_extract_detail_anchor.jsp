<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td>
        <yfc:makeXMLInput name="ExtractSeqKey">
            <yfc:makeXMLKey binding="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey" value="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("ExtractSeqKey")%>' name="BillingTransExtractEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/NWCGBillingTransExtract/@PostExtractSequenceKey")%>/>
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