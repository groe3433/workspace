
<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
                <input type="hidden" name="xml:/Order/@Override" value="N"/>
                <input type="hidden" name="ResetDetailPageDocumentType" value="Y"/>	<%-- cr 35413 --%>
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Tranfer_Order_No</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>From_Incident_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>From_Incident_Year</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>To_Incident_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>To_Incident_Year</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Transferring_Cache</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Transferred_To_Cache</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Order_Date</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/OrderList/@Order" id="Order">
            <tr>
                <yfc:makeXMLInput name="orderKey">
                    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
                </yfc:makeXMLInput> 
				<yfc:makeXMLInput name="TransferPrintKey">
                <yfc:makeXMLKey binding="xml:/Print/TOrder/@OrderKey" value="xml:/Order/@OrderHeaderKey" />
                </yfc:makeXMLInput>
                <td class="checkboxcolumn">                     
                    <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" isHistory='<%=getValue("Order","xml:/Order/@isHistory")%>' PrintEntityKey='<%=getParameter("TransferPrintKey")%>'	/>
                </td>
                <td class="tablecolumn">
                    <a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
                        <yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
                    </a>               
                </td>
                <td class="tablecolumn">
                    <% if (isVoid(getValue("Order", "xml:/Order/@Status"))) { %>
                        [<yfc:i18n>Draft</yfc:i18n>]
                    <% } else { %>
                       <%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%>
                    <% } %>
                    <% if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) { %>
                        <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>/>
                    <% } %>
                    <% if(equals("Y", getValue("Order","xml:/Order/@isHistory") )){ %>
                        <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
                    <% } %>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentNo"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentYear"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnToIncidentNo"/></td>         
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnToIncidentYear"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentCacheId"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnToIncidentCacheId"/></td>  
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Order/@OrderDate")%>"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>