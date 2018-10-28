<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="view">
	<!-- other order hidden doc type: 0007.ex -->
	<input type="hidden" name="xml:/NWCGIncidentOrder/@DocumentType" value="0007.ex" />
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Other_Order_No</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
<%
			String strQryType = resolveValue("xml:/NWCGIncidentOrder/@IncidentNoQryType");
			if(strQryType == null)
				strQryType = "" ;
			boolean isNullSelected = false, isNotNullSelected = false ;
			if("ISNULL".equals(strQryType))
				isNullSelected = true ;
			else if ("NOTNULL".equals(strQryType))
				isNotNullSelected = true ;
%>
			<select name="xml:/NWCGIncidentOrder/@IncidentNoQryType" onchange="makeIncidentNumberReadOnly(this,'xml:/NWCGIncidentOrder/@IncidentNo')" class="combobox">
				<option value="" selected> </option>
<%
				YFCElement elem = (YFCElement) request.getAttribute("QueryTypeList");
				YFCElement elemStringQryType = (YFCElement) elem.getChildElement("StringQueryTypes")  ;
				Iterator elemQryType = elemStringQryType.getChildren()  ;
				while(elemQryType.hasNext()) {
					YFCElement child = (YFCElement) elemQryType.next();
					String strType = 	child.getAttribute("QueryType");
					String strTypeDesc = 	child.getAttribute("QueryTypeDesc");
%>
					<option value="<%=strType%>" <%=strQryType.equals(strType) ? "selected":""%> > <%=strTypeDesc%> </option>
<%
				}
%>
				<option value="ISNULL" <%= isNullSelected == true ? "selected":"" %> ><yfc:i18n>Is_Null</yfc:i18n></option>
				<option value="NOTNULL" <%= isNotNullSelected == true ? "selected":"" %> ><yfc:i18n>Not_Null</yfc:i18n></option>
			</select>
			<input class="unprotectedinput" <%=isNullSelected || isNotNullSelected ? "readonly":""%>  type="text" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
		</td>
	</tr>
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Other_Order_Name</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
			<select name="xml:/NWCGIncidentOrder/@IncidentNameQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
					value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentNameQryType"/>
			</select>
			<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
		</td>
	</tr>
	<tr>
    	<td class="searchlabel" ><yfc:i18n>Other_Order_Type</yfc:i18n></td>
	</tr>
	<tr>
    	<td nowrap="true" class="searchcriteriacell" >
        	<select name="xml:/NWCGIncidentOrder/@OtherOrderTypeQryType" class="combobox" >
            	<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGIncidentOrder/@OtherOrderTypeQryType"/>
       		</select>
			<input class="unprotectedinput"  type="text" <%=getTextOptions("xml:/NWCGIncidentOrder/@OtherOrderType")%>/>
    	</td>
	</tr>
	<tr>
    	<td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
	</tr>
	<tr>
    	<td nowrap="true" class="searchcriteriacell" >
        	<select name="xml:/NWCGIncidentOrder/@PrimaryCacheIdQryType" class="combobox" >
            	<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGIncidentOrder/@PrimaryCacheIdQryType"/>
       		</select>
			<input value='<%=getValue("CurrentUser","xml:/User/@Node")%>' type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%> />
			<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
    	</td>
	</tr>	
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Customer_Id</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/NWCGIncidentOrder/@CustomerIdQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGIncidentOrder/@CustomerIdQryType"/>
			</select>
			<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%>/>
			<img class="lookupicon" name="search" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%>/>
		</td>
	</tr>
	<tr>
		<td class="searchlabel"><yfc:i18n>Customer_PO_No</yfc:i18n></td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true" >
			<select name="xml:/NWCGIncidentOrder/@CustomerPONoQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
					value="QueryType" selected="xml:/NWCGIncidentOrder/@CustomerPONoQryType"/>
			</select>
			<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerPONo")%>/>
		</td>
	</tr>
</table>