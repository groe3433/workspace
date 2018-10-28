<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<input type="hidden" name="xml:/Order/@EnterpriseCode" value="NWCG">
<input type="hidden" name="xml:/Order/@DocumentType" value="0007.ex">
<input type="hidden" name="xml:/Order/@ShipNode" value="IDGBK">
<input type="hidden" name="xml:/Order/@OrderType" value="CREDIT_CARD">

<table class="view">

	<tr>
	    <td  class="searchlabel" >
	    <yfc:i18n>Document_Type</yfc:i18n>
	    </td>
	</tr>
	<tr>
	    <td class="searchcriteriacell" nowrap="true">
	        <select class="combobox" >
	        <option disabled="disabled">Other Issues</option>
	        </select>
	    </td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Cache_ID</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select class="combobox">
	 			<option disabled="disabled">IDGBK</option>
			</select>
		</td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Issue_No</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Order/@OrderNoQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
			</select>
			<input type="text" class="unprotectedinput" size=30 maxLength=30 <%=getTextOptions("xml:/Order/@OrderNo")%>/>
		</td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Customer_Id</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Order/@BillToIDQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/@BillToIDQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID")%>/>
		</td>
	</tr>
	
	<tr>
    	<td  class="searchlabel" ><yfc:i18n>Payment_Type</yfc:i18n></td>
	</tr>
	<tr>
	    <td class="searchcriteriacell" nowrap="true">
	        <select class="combobox" >
	        	<option value="OtherOrders" disabled="disabled">Credit Card</option>
	        </select>
	    </td>
	</tr>
	
	<tr>
    	<td  class="searchlabel" ><yfc:i18n>Status</yfc:i18n></td>
	</tr>
	<tr>
	    <td class="searchcriteriacell" nowrap="true">
	        <select name="xml:/Order/@Status" class="combobox" >
	        	<yfc:loopOptions binding="xml:StatusList:/StatusList/@Status" name="StatusName" value="Status" selected="xml:/Order/@Status" isLocalized="Y"/>
		 	</select>
	    </td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Other_Order_No</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
		
			<select name="xml:/Order/Extn/@ExtnIncidentNoQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNo"/>
			</select>
			
			<input type="text" class="unprotectedinput" size=30 maxLength=30 <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
		</td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Other_Order_Name</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Order/Extn/@ExtnIncidentNameQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNameQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
		</td>
	</tr>
	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Authorization_No</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Order/Extn/@ExtnAuthorizationCodeQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/Extn/@ExtnAuthorizationCodeQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnAuthorizationCode")%>/>
		</td>
	</tr>

</table>