<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%@ include file="/console/jsp/modificationutils.jspf" %>

<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="table" width="100%" editable="true" ID="ActivityDemandList">

<thead>

<tr>

<td class="checkboxheader" sortable="no">

<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>

</td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ForActivityCode")%>"><yfc:i18n>Activity_Code</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ItemId")%>"><yfc:i18n>Item_ID</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ItemId")%>"><yfc:i18n>Description</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ItemId")%>"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@Priority")%>"><yfc:i18n>Priority</yfc:i18n></td>

<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/ActivityDemand/@DemandQty")%>"><yfc:i18n>Open_Demand_Quantity</yfc:i18n>

</td>

<td class="tablecolumnheader" nowrap="true" style="width:<%= getUITableSize("xml:/ActivityDemand/@DemandLocationId")%>"><yfc:i18n>Location_Id</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@PalletId")%>"><yfc:i18n>Pallet_Id</yfc:i18n></td>

<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@CaseId")%>"><yfc:i18n>CaseId</yfc:i18n></td>

</tr>

</thead>

<tbody>

<yfc:loopXML binding="xml:ActivityDemandList:/ActivityDemands/@ActivityDemand" id="ActivityDemand">

<%

String sItemDesc = resolveValue("xml:/ActivityDemand/@ItemId");

if(!isVoid(sItemDesc)) {

sItemDesc = "";

YFCDocument inDoc = YFCDocument.parse("<Item/>");

YFCElement inElem = inDoc.getDocumentElement();

inElem.setAttribute("ItemID",resolveValue("xml:/ActivityDemand/@ItemId"));

inElem.setAttribute("CallingOrganizationCode",resolveValue("xml:/ActivityDemand/@OrganizationCode"));

inElem.setAttribute("UnitOfMeasure",resolveValue("xml:/ActivityDemand/@UnitOfMeasure"));

YFCElement tempElem = YFCDocument.parse("<ItemList><Item ItemID=\"\"><PrimaryInformation Description=\"\" ShortDescription=\"\"/></Item></ItemList>").getDocumentElement();

%>

<yfc:callAPI apiName="getItemList" inputElement="<%=inElem%>"

templateElement="<%=tempElem%>"/>



<%

if(!isVoid(resolveValue("xml:/ItemList/Item/PrimaryInformation/@Description"))){

sItemDesc = resolveValue("xml:/ItemList/Item/PrimaryInformation/@Description");

}else if(!isVoid(resolveValue("xml:/ItemList/Item/PrimaryInformation/@ShortDescription"))){

sItemDesc = resolveValue("xml:/ItemList/Item/PrimaryInformation/@ShortDescription");

}

}%>



<tr>

<yfc:makeXMLInput name="activitydemandKey">

<yfc:makeXMLKey binding="xml:/ActivityDemand/@ActivityDemandKey" value="xml:ActivityDemand:/ActivityDemand/@ActivityDemandKey"/>

</yfc:makeXMLInput>

<td class="checkboxcolumn">

<input type="checkbox" value='<%=getParameter("activitydemandKey")%>' name="chkEntityKey"

/>

</td>

<%

//getComboText("xml:ActivityList:/Activities/@Activity" ,"Description" ,"ActivityCode" ,"xml:ActivityDemand:/ActivityDemand/@ForActivityCode")

%>

<td class="tablecolumn" nowrap="true">

<a <%=getDetailHrefOptions("L01",getParameter("activitydemandKey"),"")%>>

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@ForActivityCode"/>

</a>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@ItemId"/>

</td>

<td class="tablecolumn" nowrap="true">

<%=sItemDesc%>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@UnitOfMeasure"/>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@Priority"/>

</td>

<td class="tablecolumn">

<%if(isTrue(resolveValue("xml:/ActivityDemand/@IsInfiniteDemand"))){%>

<yfc:i18n>ALL</yfc:i18n>

<%}else{%>

<%=(getNumericValue("xml:ActivityDemand:/ActivityDemand/@DemandQty")-

getNumericValue("xml:ActivityDemand:/ActivityDemand/@SatisfiedQty"))%>

<%}%>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@DemandLocationId"/>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@PalletId"/>

</td>

<td class="tablecolumn" nowrap="true">

<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@CaseId"/>

</td>

</tr>

</yfc:loopXML>

</tbody>

</table>

