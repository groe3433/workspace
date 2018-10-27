<%@include file="/yfsjspcommon/yfsutil_mb.jspf"%>
<%@include file="/console/jsp/currencyutils_mb.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils_mb.js"></script>
<script language="javascript">
    function showAssignToUserPopup_mb(viewId) {
        return doEMPopupDialog_mb('<%=getDefaultDetailViewForGroup("YEMD011")%>',440,180);
    }
</script>
<script language="javascript">
    function showAssignToQueuePopup_mb(viewId) {
        return doEMPopupDialog_mb('<%=getDefaultDetailViewForGroup("YEMD012")%>',440,180);
    }
</script>


<%
    YFCElement root = (YFCElement)request.getAttribute("InboxList");
    int countElem = countChildElements(root);
%>
<script language="javascript">
    setRetrievedRecordCount_mb(<%=countElem%>);
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll_mb(this);" />
            </td>
            
            <td class="tablecolumnheader"><yfc:i18n>Alert_ID</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Alert_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Queue</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Assigned_To_User</yfc:i18n></td>            
            <td class="tablecolumnheader"><yfc:i18n>Last_Raised_On</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Alert_Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Created_For</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Enterprise_Code</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Seller</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>CarrierService</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Payment_Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Details</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/InboxList/@Inbox" id="Inbox">
            <tr>
                <yfc:makeXMLInput name="inboxKey">
                    <yfc:makeXMLKey binding="xml:/Inbox/@InboxKey" />
                </yfc:makeXMLInput>

                <!-- Alert_ID -->
                <td class="checkboxcolumn" > 
                    <input type="checkbox" value='<%=getParameter("inboxKey")%>' name="EntityKey"/>
                </td>
                <td class="tablecolumn"><a href="javascript:showDetailFor_mb('<%=getParameter("inboxKey")%>');">
                    <yfc:getXMLValue binding="xml:/Inbox/@InboxKey"/></a>
                </td>

                <!-- Alert_Type -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/@ExceptionType"/>
                </td>

                <!-- Queue -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/Queue/@QueueId"/>
                </td>
                
                <!-- Assigned_To_User -->
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/User/@Loginid"/></td>
                
                <!-- Raised_On -->
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Inbox/@LastOccurredOn")%>">
                    <yfc:getXMLValue binding="xml:/Inbox/@LastOccurredOn"/>
                </td>
                
                <!-- Alert_Status -->
                <%
                String alertStatus = getValue("Inbox","xml:/Inbox/@Status");
                
                if (YFCObject.isVoid(alertStatus)) {
                    alertStatus = "OPEN";
                }
                alertStatus = "ALERT_STATUS_" + alertStatus;
                %>
                <td class="tablecolumn">
                    <yfc:i18n><%=alertStatus%></yfc:i18n>
                </td>

                <!-- Created_For -->
                <%
                    boolean bListcontainsScript = false;
                    YFCElement elem = getElement("Inbox");
                    String sListDesc = elem.getAttribute("ListDescription");
                    if ( sListDesc.toUpperCase().indexOf("<SCRIPT") != -1 ){
                        bListcontainsScript = true;
                    }
                %>
                <% if ( bListcontainsScript ) { %>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@ListDescription"/></td>
                <% }else {%>
                    <td class="tablecolumn"><%=sListDesc%></td>
                <% } %>

                <!-- Enterprise_Code -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/@EnterpriseKey"/>
                </td>

                <!-- Buyer -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/Order/@BuyerOrganizationCode"/>
                </td>

                <!-- Seller -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/Order/@SellerOrganizationCode"/>
                </td>

                <!-- Total_Amount -->
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/Inbox/Order/PriceInfo/@TotalAmount")%>">
                    <%=displayAmount(getValue("Inbox", "xml:/Inbox/Order/PriceInfo/@TotalAmount"), (YFCElement) request.getAttribute("CurrencyList"), getValue("Inbox", "xml:/Inbox/Order/PriceInfo/@Currency"))%>
                </td>

                <!-- Carrier_Service -->
                <td class="tablecolumn">                    <%=getComboText("xml:/CarrierServiceList/@CarrierService", "CarrierServiceDesc", "CarrierServiceCode", "xml:/Inbox/Order/@CarrierServiceCode",true)%>
                </td>

                <!-- Payment_Status -->
                <td class="tablecolumn">
                    <%= getI18N(resolveValue("xml:/Inbox/Order/@PaymentStatus")) %>
                </td>

                <!-- Requested_Delivery_Date -->
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/Order/@ReqDeliveryDate"/>
                </td>

                <!-- Details -->
                <%	
                    boolean bDetcontainsScript = false;
                    YFCElement elemTemp = getElement("Inbox");
                    String sDetailDesc = elemTemp.getAttribute("DetailDescription");
                    if ( sDetailDesc.toUpperCase().indexOf("<SCRIPT") != -1 ){
                        bDetcontainsScript = true;
                    }
				%>
                <% if ( bDetcontainsScript ) { %>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@DetailDescription"/></td>
                <% }else {%>
                    <td class="tablecolumn"><%=sDetailDesc%></td>
                <% } %>
            </tr>
        </yfc:loopXML> 
   </tbody>
</table>
