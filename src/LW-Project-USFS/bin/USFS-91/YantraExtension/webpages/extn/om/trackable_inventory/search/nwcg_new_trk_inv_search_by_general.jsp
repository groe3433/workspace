<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">

function SetFDate()
{
	var FromDate = document.getElementById("xml:/NWCGTrackableItem/@FAcquisitionDate").value;
	var FromDtFields = FromDate.split("/");
    var FromMonth = FromDtFields[0];
    var FromDay = FromDtFields[1];
    var FromYear = FromDtFields[2];
	FromDate = FromYear+FromMonth+FromDay;

	document.getElementById("xml:/NWCGTrackableItem/@FromAcquisitionDate").value = FromDate;
	return true;
}

function SetTDate()
{
	var ToDate = document.getElementById("xml:/NWCGTrackableItem/@TAcquisitionDate").value;
	var ToDtFields = ToDate.split("/");
    var ToMonth = ToDtFields[0];
    var ToDay = ToDtFields[1];
    var ToYear = ToDtFields[2];
	ToDate = ToYear+ToMonth+ToDay;

	document.getElementById("xml:/NWCGTrackableItem/@ToAcquisitionDate").value = ToDate;
	return true;
}
</script> 
<table class="view">
<!-- Jay : just a place holder, incase this line is removed the framework will throw the null pointer exception-->
<input type='hidden' <%=getTextOptions("xml:/NWCGTrackableItem/@CallingOrganizationCode")%> />
<input type="hidden" name="xml:/NWCGTrackableItem/@AcquisitionDateQryType" value="DATERANGE"/>
<input type='hidden' name='xml:/NWCGTrackableItem/@FromAcquisitionDate' value = ""/>
<input type='hidden' name='xml:/NWCGTrackableItem/@ToAcquisitionDate'  value = ""/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Trackable_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@SerialNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@SerialNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@SerialNo") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select  name="xml:/NWCGTrackableItem/@CacheIdQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@CacheIdQryType"/>
        </select>
		<!-- CR 133 ks 2009-09-29 -->
		<input type="text"  value='<%=getValue("CurrentUser","xml:/User/@Node")%>' class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@StatusCacheID")%>/>
		<!-- end CR 133 ks 2009-09-29 -->
				<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Cache_ID")%>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Status</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name=xml:/NWCGTrackableItem/@SerialStatus> 
	     <option value="" Selected></option> 
	     <option value="A">Available</option> 
		 <option value="K">Available in KIT</option>
		 <option value="X">Cancelled - Destroyed</option>
		 <option value="E">Cancelled - Issued</option>
		 <option value="L">Cancelled - Lost/Stolen</option>
		 <option value="R">Cancelled - Transferred</option>
		 <option value="O">Cancelled - Other</option>
		 <option value="D">Disposed</option>
		 <option value="I">Issue</option> 
	     <option value="N">NRFI</option>
		 <option value="T">Transfer</option> 
		 <option value="W">WorkOrdered</option>
	   </select> 
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_Item</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@ItemID")%> />
		<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", 
		getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>

<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/NWCGTrackableItem/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />

    </td>
</tr>



<tr>
    <td class="searchlabel" ><yfc:i18n>System_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@SystemNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@SystemNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@SystemNo") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Manufacturer_Serial</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@SecondarySerialQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@SecondarySerialQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@SecondarySerial") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Manufacturer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LotAttribute1QryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LotAttribute1QryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@LotAttribute1") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Model_Name_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LotAttribute3QryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LotAttribute3QryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@LotAttribute3") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Acquisition_Cost</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@AcquisitionCostQryType" class="combobox" >
          <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Order/PriceInfo/@AcquisitionCostQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@AcquisitionCost") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Acquisition_Date</yfc:i18n></td>
</tr>
<tr>
<td nowrap="true">
 		<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@FAcquisitionDate")%> />
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetFDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar" ) %> />
		<yfc:i18n>To</yfc:i18n>
				<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@TAcquisitionDate")%> />
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetTDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Owner_Unit_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@OwnerUnitIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@OwnerUnitIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@OwnerUnitID") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Owner_Unit_Name</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@OwnerUnitNameQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@OwnerUnitNameQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@OwnerUnitName") %> />
    </td>
</tr>

</table>