<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
<% // Now call the APIs that are dependent on the common fields (Enterprise Code) %>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>

<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/Extn/@ExtnIncidentNumQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNumQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNum")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGIncident')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_NWCG_Incident") %>/>&nbsp;
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Order_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Item_ID</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/Item/@ItemIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/OrderLine/Item/@ItemIDQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@ItemID")%>/>
<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/Order/OrderLine/Item/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Product_Class</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/Item/@ProductClass" class="combobox">
<yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
value="CodeValue" selected="xml:/Order/OrderLine/Item/@ProductClass"/>
</select>
</td>
<tr>
<td class="searchlabel" >
<yfc:i18n>Unit_Of_Measure</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/@OrderingUOM" class="combobox">
<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure"
value="UnitOfMeasure" selected="xml:/Order/OrderLine/@OrderingUOM"/>
</select>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Item_Description</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/Item/@ItemDescQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/OrderLine/Item/@ItemDescQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@ItemDesc")%>/>
</td>
</tr>
</table>
