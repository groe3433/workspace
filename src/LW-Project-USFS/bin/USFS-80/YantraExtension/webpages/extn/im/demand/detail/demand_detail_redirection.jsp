<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript">

    function redirectToDetailScreen() {

        var linkInputObj = document.all("orderhref");
        entityType = linkInputObj.yfsTargetEntity;
        showDetailFor(linkInputObj.value);
    }
    
    function window.onload() {
        if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
            return;
        }
        redirectToDetailScreen();
    }
</script>

<table>
    <tr style="display:none">
        <td>
            <% if (!isVoid(resolveValue("xml:/Order/@OrderHeaderKey"))) { %>
                <yfc:makeXMLInput name="orderKey">
                    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
                </yfc:makeXMLInput>
				
                <input type="hidden" id="orderhref" value="<%=getParameter("orderKey")%>" yfsTargetEntity="ISUorder"/>
            <% } else { %>
                <yfc:makeXMLInput name="orderLineKey">
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLineDetail/@OrderLineKey"/>
                </yfc:makeXMLInput>
                <input type="hidden" id="orderhref" value="<%=getParameter("orderLineKey")%>" yfsTargetEntity="ISUorderline"/>
            <% } %>
        </td>
    </tr>
</table>