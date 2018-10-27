<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

	<!-- Begin CR383 -->
	<yfc:callAPI apiID="AP2" />
	<!-- End CR383 -->
<%
	String openOrder = "Y";
%>
	<!-- hidden input to indicate other orders -->
	<table class="view">
		<tr>
			<td class="searchlabel"><yfc:i18n>Cache_ID</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@NodeQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
					name="QueryTypeDesc" value="QueryType"
					selected="xml:/NWCGBillingTransaction/@NodeQryType" />
				</select> 
				<!-- CR 138 GN 12/19/2008 --> 
					<!-- input type="text"  value='<%=getValue("CurrentUser","xml:/User/@Node")%>' class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@Node")%>/ -->
					<input type="text" value='<%=getValue("CurrentUser","xml:/User/@Node")%>' class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@Node")%> /> 
				<!-- CR 138 GN 12/19/2008 -->
				<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%> />
			</td>
		</tr>
		<tr>
			<td class="searchlabel"><yfc:i18n>Master Work Order/Return #</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNoQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
						name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNoQryType" />
				</select> 
				<input size="12" maxLength="50" class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@MasterWorkOrderNo")%> />
			</td>
		</tr>
		<!-- Begin CR383 -->
		<tr>
			<td class="searchlabel"><yfc:i18n>Master Work Order Type</yfc:i18n></td>
		</tr>
		<tr>
			<td class="searchcriteriacell"><select class=combobox <%=getComboOptions("xml:/NWCGMasterWorkOrder/@MasterWorkOrderType")%>>
				<yfc:loopOptions binding="xml:MWOTypeList:/CommonCodeList/@CommonCode"
					name="CodeShortDescription" value="CodeShortDescription"
					selected="xml:/NWCGMasterWorkOrder/@MasterWorkOrderType" isLocalized="Y" />
				</select>
			</td>
		</tr>
<%
%>
		<!-- End CR383 -->
		<tr>
			<td class="searchlabel"><yfc:i18n>Incident_No</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@IncidentNoQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
						name="QueryTypeDesc" value="QueryType"
						selected="xml:/NWCGMasterWorkOrder/@IncidentNoQryType" />
				</select> 
				<input type="text" size="25" maxLength="50" class="unprotectedinput"
					<%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentNo")%> />
			</td>
		</tr>
		<!-- add <tr> tag below -->
		<tr>
			<td class="searchlabel"><yfc:i18n>Incident Year</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@IncidentYearQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
						name="QueryTypeDesc" value="QueryType"
						selected="xml:/NWCGMasterWorkOrder/@IncidentYearQryType" />
				</select> 
				<input type="text" size="4" maxLength="50" class="unprotectedinput"
					<%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentYear")%> /></td>
		</tr>
		<!-- </tr> -->
		<tr>
			<td class="searchlabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@IncidentNameQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
						name="QueryTypeDesc" value="QueryType"
						selected="xml:/NWCGMasterWorkOrder/@IncidentNameQryType" />
				</select> 
				<input type="text" size="30" maxLength="50" class="unprotectedinput"
					<%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentName")%> /></td>
			
		<!-- BEGIN CR372 -->
		<tr>
			<td class="searchlabel"><yfc:i18n>Item_ID</yfc:i18n></td>
		</tr>
		<tr>
			<td nowrap="true" class="searchcriteriacell">
				<select name="xml:/NWCGMasterWorkOrder/@ItemIDQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"
						name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGMasterWorkOrder/@ItemIDQryType" />
				</select> 
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@ItemID")%> /> 
<%
 	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode"));
%>
				<img class="lookupicon" name="search"
					onclick="callItemLookup('xml:/NWCGMasterWorkOrder/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM','item','<%=extraParams%>')"
						<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
			</td>
		<!-- added ending </tr> tag below -->	
		</tr>
		
		<!-- BEGIN - CR 835 - Jan 18, 2013 -->
		<!-- xml:/NWCGMasterWorkOrder/@FromCreatets -->
		<!-- xml:/NWCGMasterWorkOrder/@ToCreatets -->
		<!-- xml:/NWCGMasterWorkOrder/@CreatetsQryType -->
		<tr>
        	<td>
            	<input type="hidden" name="xml:/NWCGMasterWorkOrder/@CreatetsQryType" value="BETWEEN"/>
        	</td>
    	</tr>
    	<tr>
        	<td class="searchlabel">
            	<yfc:i18n>Refurb_Date_Search_Range</yfc:i18n>
        	</td>
    	</tr>
    	<tr>
        	<td nowrap="true">
            	<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@FromCreatets_YFCDATE")%>/>
            	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
            	<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@FromCreatets_YFCTIME")%>/>
            	<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
            	<yfc:i18n>To</yfc:i18n>
       		<td>
    	</tr>
    	<tr>
        	<td nowrap="true">
            	<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@ToCreatets_YFCDATE")%>/>
            	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
            	<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@ToCreatets_YFCTIME")%>/>
            	<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
        	</td>
    	</tr>
		<!-- END - CR 835 - Jan 18, 2013 -->
		
		<tr>
		<!-- END CR372 -->
			<td class="searchcriteriacell"><input type="radio"
				<%=getRadioOptions("xml:/NWCGMasterWorkOrder/@OpenOrder", openOrder,"Y")%>><yfc:i18n>Open</yfc:i18n>
				<input type="radio"
					<%=getRadioOptions("xml:/NWCGMasterWorkOrder/@OpenOrder", openOrder,"N")%>><yfc:i18n>Completed</yfc:i18n>
				<input type="radio"
					<%=getRadioOptions("xml:/NWCGMasterWorkOrder/@OpenOrder", openOrder,"P")%>><yfc:i18n>Partially Completed</yfc:i18n>
				<input type="radio" checked="checked"
					<%=getRadioOptions("xml:/NWCGMasterWorkOrder/@OpenOrder", openOrder,"A")%>><yfc:i18n>All</yfc:i18n>
			</td>
		</tr>
		<!-- <tr> -->
		<!-- </tr> -->
	</table>
