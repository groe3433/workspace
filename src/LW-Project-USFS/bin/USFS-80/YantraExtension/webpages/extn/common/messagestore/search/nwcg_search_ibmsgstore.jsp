<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.core.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<input type="hidden" name="xml:/NWCGInboundMessage/@ModifytsQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/NWCGInboundMessage/@CreatetsQryType" value="DATERANGE"/>

<yfc:callAPI apiID="AP2"/>


<table class="view">

<tr>
	<td class="searchlabel" >
	<yfc:i18n>Distribution_ID</yfc:i18n>
	</td>
</tr>
<tr>
	<td nowrap="true" class="searchcriteriacell">
	<select name="xml:/NWCGInboundMessage/@DistributionIDQryType" class="combobox">
	<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
	value="QueryType" selected="xml:/NWCGInboundMessage/@DistributionIDQryType"/>
	</select>
	<input maxLength="100" class="unprotectedinput" type="text" size="50" <%=getTextOptions("xml:/NWCGInboundMessage/@DistributionID")%>/>
	</td>
</tr>



    <tr>
        <td class="searchlabel">
            <yfc:i18n>Last_Message_Received</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGInboundMessage/@FromModifyts")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>

			<yfc:i18n>To</yfc:i18n>

            <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGInboundMessage/@ToModifyts")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
    </tr>





    <tr>
        <td class="searchlabel">
            <yfc:i18n>Initial_Message_Received</yfc:i18n>
        </td>
    </tr>

    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGInboundMessage/@FromCreatets")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>

			<yfc:i18n>To</yfc:i18n>

            <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGInboundMessage/@ToCreatets")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
        
    </tr>



<tr>
    <td class="searchlabel" ><yfc:i18n>Message_Name</yfc:i18n></td>
</tr>

<tr>

	<td class="searchcriteriacell" nowrap="true">
		<select name="xml:/NWCGInboundMessage/@MessageName" class="combobox">
			<yfc:loopOptions binding="xml:IBMessageNames:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/NWCGInboundMessage/@MessageName" isLocalized="Y"/>
		</select>
	</td>

</tr>


<tr>
    <td class="searchlabel" ><yfc:i18n>Message_Status</yfc:i18n></td>
</tr>

<tr>

	<td class="searchcriteriacell" nowrap="true">
		<select name="xml:/NWCGInboundMessage/@MessageStatus" class="combobox">
			<yfc:loopOptions binding="xml:IBMessageStatus:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/NWCGInboundMessage/@MessageStatus" isLocalized="Y"/>
		</select>
	</td>

</tr>


<tr>
    <td class="searchlabel" ><yfc:i18n>Entity_Name</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name=xml:/NWCGInboundMessage/@EntityName> 
	     <option value="" Selected></option> 
		 <option value="Catalog">Catalog</option> 
		 <option value="Incident">Incident</option>
		 <option value="Issue">Issue</option>		 
	   </select> 
    </td>
</tr>




<tr>
	<td class="searchlabel" >
	<yfc:i18n>Entity_Value</yfc:i18n>
	</td>
</tr>

<tr>
	<td nowrap="true" class="searchcriteriacell">
	<select name="xml:/NWCGInboundMessage/@EntityValueQryType" class="combobox">
	<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
	value="QueryType" selected="xml:/NWCGInboundMessage/@EntityValueQryType"/>
	</select>
	<input maxLength="50" class="unprotectedinput" <%=getTextOptions("xml:/NWCGInboundMessage/@EntityValue")%>/>
	</td>
</tr>



<tr>
</tr>
</table>
