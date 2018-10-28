<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<table class="view">


<tr>
    <td class="searchlabel" ><yfc:i18n>Extract_File_Name</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	  <select name="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType" class="combobox" >
           <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
               name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType"/>
      </select>
		<input type="text" class="unprotectedinput" maxLength=100 size=40 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ExtractFileName")%>/>
		<img class="lookupicon" onclick="callLookup(this,'NWCGBTransExtractLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Extract_File_Name")%>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Transaction_No</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransExtract/@TransactionNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@TransactionNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@TransactionNo")%>/>
	</td>
</tr>
<!--
<tr>
    <td class="searchlabel" ><yfc:i18n>Item_Text</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	    <select name="xml:/NWCGBillingTransExtract/@ItemText" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@ItemText"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ItemText")%>/>
    </td>
</tr>
-->
<!-- FBMS Elements -->

<tr>
    <td class="searchlabel" ><yfc:i18n>Cost_Center</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	    <select name="xml:/NWCGBillingTransExtract/@AcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@CostCenterQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@CostCenter")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	    <select name="xml:/NWCGBillingTransExtract/@AcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@WBSQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@WBS")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Functional_Area</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	    <select name="xml:/NWCGBillingTransExtract/@AcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@FunctionalAreaQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@FunctionalArea")%>/>
    </td>
</tr>
<!-- FBMS Elements -->
</table>