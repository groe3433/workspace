<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/DemandDetails/@ItemID"/>
            <input type="hidden" name="xml:/DemandDetails/@DemandShipDateQryType" value="BETWEEN"/>
            <input type="hidden" <%=getTextOptions("xml:/DemandDetails/@ItemID")%>/>
			<input type="hidden" <%=getTextOptions("xml:/DemandDetails/@DistributionRuleId")%>/>
			<input type="hidden" <%=getTextOptions("xml:/DemandDetails/@ConsiderAllNodes")%>/>
			<input type="hidden" <%=getTextOptions("xml:/DemandDetails/@OrganizationCode")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Unit_Of_Measure</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/DemandDetails/@UnitOfMeasure"/>
            <input type="hidden" <%=getTextOptions("xml:/DemandDetails/@UnitOfMeasure")%>/>
        </td>
    </tr>
    <tr>
		<td class="searchlabel">
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/DemandDetails/@ProductClass"/>
            <input type="hidden" <%=getTextOptions("xml:/DemandDetails/@ProductClass")%>/>
        </td>
    </tr>
    <% if (!isVoid(resolveValue("xml:/DemandDetails/@ShipNode"))) { %>
        <tr>
            <td class="searchlabel">
                <yfc:i18n>Ship_Node</yfc:i18n>
            </td>
            <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/DemandDetails/@ShipNode"/>
                <input type="hidden" <%=getTextOptions("xml:/DemandDetails/@ShipNode")%>/>
            </td>
        </tr>
    <% } %>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Demand_Type</yfc:i18n>
        </td>
        <td class="protectedtext">
			<%=getComboText("xml:DemandTypeList:/InventoryDemandTypeList/@InventoryDemandType","Description","DemandType",resolveValue("xml:/DemandDetails/@DemandType"),true)%>
            <input type="hidden" <%=getTextOptions("xml:/DemandDetails/@DemandType")%>/>
        </td>
    </tr>
	<tr>
		<td class="searchlabel" ><yfc:i18n>Demand_Ship_Date</yfc:i18n></td>
	</tr>
	<tr>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/DemandDetails/@FromDemandShipDate")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<yfc:i18n>To</yfc:i18n>
			<input class="dateinput" type="text" <%=getTextOptions("xml:/DemandDetails/@ToDemandShipDate")%> />
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input type="hidden" <%=getTextOptions("xml:/DemandDetails/@DemandShipDateQryType","BETWEEN")%>/>
		</td>
	</tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/DemandDetails/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/DemandDetails/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/DemandDetails/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Document_Type</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <select name="xml:/DemandDetails/@DocumentType" class="combobox">
                <yfc:loopOptions binding="xml:/DocumentParamsList/@DocumentParams" name="Description" value="DocumentType" selected="xml:/DemandDetails/@DocumentType" isLocalized="Y"/>
            </select>
        </td>
    </tr>
</table>