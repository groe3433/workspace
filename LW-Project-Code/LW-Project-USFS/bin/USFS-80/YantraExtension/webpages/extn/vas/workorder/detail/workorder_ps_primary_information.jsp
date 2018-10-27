<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/workorder.js"></script>

<script>
	function checkNodeUser()
	{
		/*if("<%=isShipNodeUser()%>"=="false"){
			alert('<%=getI18N("Only_Node_User_can_perform_this_Operation")%>');
			return false;
		}*/
		return true;
	}
	function enterActionCancellationReason(cancelReasonViewID, cancelReasonCodeBinding, cancelReasonTextBinding) {

		var myObject = new Object();
		myObject.currentWindow = window;
		myObject.reasonCodeInput = document.all(cancelReasonCodeBinding);
		myObject.reasonTextInput = document.all(cancelReasonTextBinding);       
		
		yfcShowDetailPopup(cancelReasonViewID, "", "550", "255", myObject, "workorder", "<WorkOrder />");

		var returnValue = myObject["OKClicked"];
		if ( "YES" == returnValue ) {
			return true;
		} else {
			return false;
		}
	}
</script>

<%
	YFCElement organizationInput = null;

	organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" />").getDocumentElement();

	YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization InventoryOrganizationCode=\"\"/> </OrganizationList>").getDocumentElement(); 
%>
	<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>
	<Input type="hidden" name="xml:/WorkOrder/@EnterpriseInvOrg" value="<%=resolveValue("xml:OrganizationList:/OrganizationList/Organization/@InventoryOrganizationCode")%>"/>
<%	
	String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String createView = resolveValue("xml:/WorkOrder/@WorkOrderMode");
    createView = createView == null ? "" : createView;

%>
	<Input type="hidden" name="xml:/WorkOrder/@ReasonCode" />
	<Input type="hidden" name="xml:/WorkOrder/@ReasonText" />
	<yfc:makeXMLInput name="WorkOrderKey">
		<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="OrderLineKey">
		<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/WorkOrder/OrderLine/@OrderLineKey" />
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="resPoolKey">
		<yfc:makeXMLKey binding="xml:/ResourcePool/@Node" value="xml:/WorkOrder/@NodeKey"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@ItemGroupCode" value="xml:/WorkOrder/OrderLine/@ItemGroupCode"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/ServicedItem/@ItemID" value="xml:/WorkOrder/OrderLine/Item/@ItemID"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@ResourcePoolKey" value="xml:/WorkOrder/OrderLine/@ResourcePoolKey"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@OrganizationCode" value="xml:/WorkOrder/@SellerOrganizationCode"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@ApptStartTimestamp" value="xml:/WorkOrder/@StartNoEarlierThan"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@ApptEndTimestamp" value="xml:/WorkOrder/@FinishNoLaterThan"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@Timezone" value="xml:/WorkOrder/OrderLine/@Timezone"/>
		<yfc:makeXMLKey binding="xml:/ResourcePool/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
	</yfc:makeXMLInput>

<table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@NodeKey")%></td>
		<Input type="hidden" name="xml:/WorkOrder/@NodeKey" value="<%=resolveValue("xml:/WorkOrder/@NodeKey")%>"/>
		<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%></td>
		<Input type="hidden" name="xml:/WorkOrder/@EnterpriseCode" value="<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>"/>
		<td/><td/>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%></td>
		<input type="hidden" name="xml:/WorkOrder/@WorkOrderNo" value="<%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%>"/>
		<td class="detaillabel" ><yfc:i18n>Quantity_Requested</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@QuantityRequested")%></td>
		<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
		<td class="detaillabel" ></td>
		<td class="protectedtext"></td>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<td class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@Priority")%> class="combobox">
				<yfc:loopOptions binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/WorkOrder/@Priority" isLocalized="Y"/>
			</select>
			<Input type="hidden" name="xml:/WorkOrder/@Priority" value="<%=resolveValue("xml:/WorkOrder/@Priority")%>"/>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Appointment</yfc:i18n>
		</td>
		<td class="protectedtext">
			<% String sTimeWindow = displayTimeWindow(resolveValue("xml:/WorkOrder/@StartNoEarlierThan"), resolveValue("xml:/WorkOrder/@FinishNoLaterThan"), resolveValue("xml:/WorkOrder/OrderLine/@Timezone"));
			if(!isVoid(sTimeWindow)) {%> 
				<%=sTimeWindow%>
				<%=showTimeZoneIcon(resolveValue("xml:/WorkOrder/OrderLine/@Timezone"), getLocale().getTimezone())%>
			<%}%>			
			<a <%=getDetailHrefOptions("L02", getParameter("resPoolKey"), "")%>>
			<%=showAppointmentIcon(resolveValue("xml:/WorkOrder/OrderLine/@ApptStatus"), getI18N("Plan_Service_Appointment") ) %></a>
		</td>

	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Service_Item_ID</yfc:i18n></td>
		<td class="protectedtext">
			<a <%=getDetailHrefOptions("L01", getValue("WorkOrder", "xml:/WorkOrder/OrderLine/Order/@DocumentType"), getParameter("OrderLineKey"),"")%>>
				<yfc:getXMLValue binding="xml:/WorkOrder/@ServiceItemID"/>
			</a>
		</td>
		<Input type="hidden" name="xml:/WorkOrder/@ServiceItemID" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemID")%>"/>
		<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@Purpose")%></td>
		<Input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
		<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
		<td class="protectedtext"><%=getDBString(resolveValue("xml:/WorkOrder/@StatusDescription"))%>
            <% if (isTrue(resolveValue("xml:/WorkOrder/@HoldFlag")) )   { %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held")%>/>
            <% } %>
        </td>
		<Input type="hidden" name="xml:/WorkOrder/@Status" value="<%=resolveValue("xml:/WorkOrder/@Status")%>"/>
		<Input type="hidden" name="xml:/WorkOrder/@HoldFlag" value="<%=resolveValue("xml:/WorkOrder/@HoldFlag")%>"/>
	</tr>	
	<%if(modifyView != "") {
		String sReasonCode = resolveValue("xml:/WorkOrder/@ReasonCode");
		String sReasonCodeDesc = "";
		YFCElement reasonCodeDoc = (YFCElement)request.getAttribute("ReasonCodeList");
		if (reasonCodeDoc != null)
		{
			for (Iterator i=reasonCodeDoc.getChildren();i.hasNext();)
			{
				YFCElement oElem = (YFCElement)i.next();
				if(YFCCommon.equals(oElem.getAttribute("CodeValue"),sReasonCode))
				{
					sReasonCodeDesc = oElem.getAttribute("CodeShortDescription");
					break;
				}
			}
		}
		if (YFCCommon.isVoid(sReasonCodeDesc))
			sReasonCodeDesc = sReasonCode;

		String sReasonText = resolveValue("xml:/WorkOrder/@ReasonText");
		if(!isVoid(sReasonCode) || !isVoid(sReasonText)){%> 
			<tr>
				<td class="detaillabel" ><yfc:i18n>Reason_Code</yfc:i18n></td>
				<td class="protectedtext" nowrap="true">
					<%=sReasonCodeDesc%>
				</td>	
				<td class="detaillabel" ><yfc:i18n>Reason_Text</yfc:i18n></td>
				<td class="protectedtext" nowrap="true">
					<%=sReasonText%>
				</td>	
				<td/>
				<td/>
			</tr>
		<%}
	}%>
</table>