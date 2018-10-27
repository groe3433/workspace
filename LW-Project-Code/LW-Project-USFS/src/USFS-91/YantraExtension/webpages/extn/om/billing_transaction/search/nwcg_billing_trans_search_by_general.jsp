<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">

function SetFDate()
{
	var FromDate = document.getElementById("xml:/NWCGBillingTransaction/@FTransDate").value;
	var FromDtFields = FromDate.split("/");
    var FromMonth = FromDtFields[0];
    var FromDay = FromDtFields[1];
    var FromYear = FromDtFields[2];
	FromDate = FromYear+FromMonth+FromDay;

	document.getElementById("xml:/NWCGBillingTransaction/@FromTransDate").value = FromDate;
	return true;
}

function SetTDate()
{
	var ToDate = document.getElementById("xml:/NWCGBillingTransaction/@TTransDate").value;
	var ToDtFields = ToDate.split("/");
    var ToMonth = ToDtFields[0];
    var ToDay = ToDtFields[1];
    var ToYear = ToDtFields[2];
	ToDate = ToYear+ToMonth+ToDay;

	document.getElementById("xml:/NWCGBillingTransaction/@ToTransDate").value = ToDate;
	return true;
}
</script> 

<input type="hidden" name="xml:/NWCGBillingTransaction/@FromTransDate" value=""/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@ToTransDate" value=""/> 

<table class="view">
<yfc:callAPI apiID="AP2"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@CacheIdQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@CacheIdQryType"/>
        </select>
		<!-- CR 80 ks 2009-09-29 -->
		<input type="text"  value='<%=getValue("CurrentUser","xml:/User/@Node")%>' class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@CacheId")%>/>
		<!-- CR 80 ks 2009-09-29 -->
		<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Incident/Other Order Number</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@IncidentNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@IncidentNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@IncidentNo")%>/>
		<img class="lookupicon" onclick="callIncidentLookup('xml:/NWCGBillingTransaction/@IncidentNo','xml:/NWCGBillingTransaction/@IncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Incident_Year</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
		<input type="text" class="unprotectedinput" maxLength=4 size=4 <%=getTextOptions("xml:/NWCGBillingTransaction/@IncidentYear")%>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Reviewed</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name=xml:/NWCGBillingTransaction/@IsReviewed> 
	     <option value="" Selected></option> 
	     <option value="Y">Yes</option> 
		 <option value="N">No</option>
	   </select> 
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Extracted</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name=xml:/NWCGBillingTransaction/@IsExtracted> 
	     <option value="" Selected></option> 
	     <option value="Y">Yes</option> 
		 <option value="N">No</option>
	   </select> 
    </td>
</tr>

<tr>
    <td  class="searchlabel" ><yfc:i18n>Document_Type</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select <%=getComboOptions("xml:/NWCGBillingTransaction/@DocumentType")%> class="combobox" >
            <yfc:loopOptions binding="xml:DocType:/DocumentParamsList/@DocumentParams" name="Description" value="DocumentType" selected="xml:/NWCGBillingTransaction/@DocumentType"/>
        </select>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Trans_Type</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name=xml:/NWCGBillingTransaction/@TransType> 
	     <option value="" Selected></option> 
		 <option value="ADJ LOCATION INVENTORY">ADJ LOCATION INVENTORY</option> 
		 <option value="CONFIRM INCIDENT TO">CONFIRM INCIDENT TO</option>
		 <option value="CONFIRM INCIDENT FROM">CONFIRM INCIDENT FROM</option>		 
		 <option value="ISSUE CONFIRM SHIPMENT">ISSUE CONFIRM SHIPMENT</option>		 
		 <option value="RECEIVE PO">RECEIVE PO</option>
		 <option value="RECEIVE CACHE TO">RECEIVE CACHE TO</option>
		 <option value="RETURNS">RETURNS</option>
		 <option value="SHIP CACHE TO">SHIP CACHE TO</option>
		 <option value="WO-DEKITTING">WO-DEKITTING</option>
		 <option value="WO-KITTING">WO-KITTING</option>
		 <option value="WO-REFURB">WO-REFURB</option>
	   </select> 
    </td>
</tr>
<!-- FBMS elements -->
<tr>
    <td class="searchlabel" ><yfc:i18n>Cost_Center</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@CostCenterQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@CostCenterQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@CostCenter")%>/>
	 </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>WBS</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@WBSQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@WBSQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@WBS")%>/>
	 </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Functional_Area</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@FunctionalAreaQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@FunctionalAreaQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@FunctionalArea")%>/>
	 </td>
</tr>

<!-- end of FBMS elements -->

<tr>
    <td class="searchlabel" ><yfc:i18n>Transaction_No</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@TransactionNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@TransactionNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@TransactionNo")%>/>
	 </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>BLM_Acct_Code</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@IncidentBlmAcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@IncidentBlmAcctCodeQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@IncidentBlmAcctCode")%>/>
	 </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>FS_Acct_Code</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@IncidentFSAcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@IncidentFSAcctCodeQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=25 <%=getTextOptions("xml:/NWCGBillingTransaction/@IncidentFsAcctCode")%>/>
	 </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Trans_Date</yfc:i18n></td>
</tr>

<tr>
<input type="hidden" name="xml:/NWCGBillingTransaction/@TransDateQryType" value="DATERANGE"/>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGBillingTransaction/@FTransDate")%> />
<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetFDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar" ) %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGBillingTransaction/@TTransDate")%> />
<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetTDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_Item</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransaction/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransaction/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@ItemId")%> />
		<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", 
		getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>

<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/NWCGBillingTransaction/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
</tr>


</table>