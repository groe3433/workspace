<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/workorder.js"></script>

<table width="100%" class="view">
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
			<jsp:param name="ShowDocumentType" value="false"/>
			<jsp:param name="ScreenType" value="search"/>
			<jsp:param name="ShowNode" value="true"/>
			<jsp:param name="EnterpriseCodeBinding" value="xml:/WorkOrder/@EnterpriseCode"/>
			<jsp:param name="NodeBinding" value="xml:/WorkOrder/@NodeKey"/>
			<jsp:param name="RefreshOnNode" value="true"/>
			<jsp:param name="EnterpriseListForNodeField" value="false"/>
			 <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
	</jsp:include>
    
    <%
		YFCElement listElement = (YFCElement)request.getAttribute("HoldTypeList");

		YFCDocument document = listElement.getOwnerDocument();
		YFCElement newElement = document.createElement("HoldType");

		newElement.setAttribute("HoldType", " ");
		newElement.setAttribute("HoldTypeDescription", getI18N("All_Held_Work_Orders"));

		YFCElement eFirst = listElement.getFirstChildElement();
		if(eFirst != null)	{
			listElement.insertBefore(newElement, eFirst);
		}	else	{
			listElement.appendChild(newElement);
		}
		
	%>
	<jsp:include page="/vas/workorder/search/work_order_search_common.jsp" flush="true"/>	
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Service_Item_Group_Code</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <select <%=getComboOptions("xml:/WorkOrder/@SerItemGroupCode")%> class="combobox" onchange="onServiceItemGroupCodeChange(this.value)" id="ServiceItemLkp">
                <yfc:loopOptions binding="xml:ServiceItemGroupList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" selected="xml:/WorkOrder/@SerItemGroupCode" isLocalized="Y"/>
            </select>
        </td>
    </tr>
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Service_Item_ID</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true" >
			<select name="xml:/WorkOrder/@ServiceItemIDQryType" class="combobox" >
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/WorkOrder/@ServiceItemIDQryType"/>
			</select>
			<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/@ServiceItemID")%>/>
			<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" name="search"
			onclick="callWorkOrderServiceItemLookup('xml:/WorkOrder/@ServiceItemID','xml:/WorkOrder/@ServiceProductClass','xml:/WorkOrder/@ServiceUOM',
			'item','xml:/WorkOrder/@ServiceItemGroupCode','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Service_Item") %> />
		</td>
	</tr>

        <td class="searchlabel" >
            <yfc:i18n>Work_Order_Status</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <select <%=getComboOptions("xml:/WorkOrder/@FromStatus")%> class="combobox">
                <yfc:loopOptions binding="xml:WorkOrderStatusList:/StatusList/@Status" name="Description"
                value="Status" selected="xml:/WorkOrder/@FromStatus" isLocalized="Y"/>
            </select>&nbsp;<yfc:i18n>To</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <select <%=getComboOptions("xml:/WorkOrder/@ToStatus")%> class="combobox">
                <yfc:loopOptions binding="xml:WorkOrderStatusList:/StatusList/@Status" name="Description"
                value="Status" selected="xml:/WorkOrder/@ToStatus" isLocalized="Y"/>
            </select>
        </td>
    </tr>
    
    <tr>
		<td class="searchcriteriacell">
			<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@HoldFlag", "xml:/WorkOrder/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' onclick="manageHoldOpts(this, 'xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/')" ><yfc:i18n>Held_Work_Orders_With_Hold_Type</yfc:i18n></input>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell">
			<select resetName="xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@HoldType" name="xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@HoldType" class="combobox" <%if(isTrue("xml:/WorkOrder/@HoldFlag") ) {%> ENABLED <%} else {%> disabled="true" <%}%> >
				<yfc:loopOptions binding="xml:/HoldTypeList/@HoldType" name="HoldTypeDescription" value="HoldType" suppressBlank="Y" selected="xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@HoldType" isLocalized="Y"/>
			</select>
		</td>
	</tr>
    
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Work_Order_#</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/WorkOrder/@WorkOrderNoQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/WorkOrder/@WorkOrderNoQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@WorkOrderNo")%>/>
		</td>
	</tr>

	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Segment_Type</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@SegmentType")%> class="combobox">
				<yfc:loopOptions binding="xml:WorkOrderSegmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/WorkOrder/@SegmentType" isLocalized="Y"/>
			</select>
		</td>
	</tr>

	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Segment_#</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@SegmentQryType")%> class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/WorkOrder/@SegmentQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@Segment")%>/>
		</td>
	</tr>

	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@OrderNoQryType")%> class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/WorkOrder/@OrderNoQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@OrderNo")%>/>
		</td>
	</tr>

	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Priority</yfc:i18n>
		</td>
	</tr>

	<tr>
		<td class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@Priority")%> class="combobox">
				<yfc:loopOptions binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/WorkOrder/@Priority" isLocalized="Y"/>
			</select>
		</td>
	</tr>
		<tr>
			<td class="searchlabel" >
				<yfc:i18n>Item_ID</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="searchcriteriacell" nowrap="true" >
				<select name="xml:/WorkOrder/@ItemIDQryType" class="combobox">
					<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
					value="QueryType" selected="xml:/WorkOrder/@ItemIDQryType"/>
				</select>
				<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/@ItemID")%>/>
				<% extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
				<img class="lookupicon" name="search" 
				onclick="callItemLookup('xml:/WorkOrder/@ItemID','xml:/WorkOrder/@ProductClass','xml:/WorkOrder/@UOM',
				'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
			</td>
		</tr>
	
		<!-- BEGIN - CR 1109 - Nov 6, 2013 -->
		<!-- xml:/WorkOrder/@FromCreatets -->
		<!-- xml:/WorkOrder/@ToCreatets -->
		<!-- xml:/WorkOrder/@CreatetsQryType -->
    	<tr>
        	<td class="searchlabel">
            	<yfc:i18n>Created</yfc:i18n>
        	</td>
    	</tr>
    	<tr>
        	<td nowrap="true">
            	<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FromCreatets_YFCDATE")%>/>
            	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
               	<yfc:i18n>To</yfc:i18n>
               	<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@ToCreatets_YFCDATE")%>/>
            	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
       		<td>
    	</tr>
    	<tr>
        	<td nowrap="true">
        	</td>
    	</tr>
		<!-- END - CR 1109 - Nov 6, 2013 -->
		<input type="hidden" name="xml:/WorkOrder/@CreatetsQryType" value="BETWEEN"/>
		<input type="hidden" name="xml:/WorkOrder/@ProductClass" value=""/>
		<input type="hidden" name="xml:/WorkOrder/@UOM" value=""/>
		<input type="hidden" name="xml:/WorkOrder/@StatusQryType" value="BETWEEN"/>				
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_0/@Name" value="ServiceItemGroupCode"/>
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_0/@QryType" value="NE"/>
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_0/@Value" value="PS"/>		
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_1/@Name" value="ServiceItemGroupCode"/>
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_1/@QryType" value="NE"/>
		<input type="hidden" name="xml:/WorkOrder/ComplexQuery/And/Exp_1/@Value" value="DS"/>
		<input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@SerItemGroupCode")%>"/>
		<input type="hidden" name="xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@Status" value=""/>
		<input type="hidden" name="xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@StatusQryType" value="" />
</table>