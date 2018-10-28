<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
	String scacAndServiceKey=getValue("Shipment","xml:/Shipment/@ScacAndServiceKey");
	String docType= getValue("Shipment","xml:/Shipment/@DocumentType");
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
%>
<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript">
function checkForUserNode()
{
	var shipNode = '<%=resolveValue("xml:/Shipment/@ShipNode")%>';
	var userNode = '<%=resolveValue("xml:CurrentUser:/User/@Node")%>';
	if(shipNode != userNode) {
		alert('Cannot confirm shipment for other cache');
		return false;
	}
	return true;
}
</script>

<%
	Date now = new Date();
	SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
%>

<table width="100%" class="view">
<tr>
	<yfc:makeXMLInput name="startReceiptKey" >
		<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentNo" value="xml:/Shipment/@ShipmentNo" />
		<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		<yfc:makeXMLKey binding="xml:/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
		<yfc:makeXMLKey binding="xml:/Shipment/@ExpectedDeliveryDate" value="xml:/Shipment/@ExpectedDeliveryDate" />
		<yfc:makeXMLKey binding="xml:/Shipment/@ReceivingNode" value="xml:/Shipment/@ReceivingNode" />
		<yfc:makeXMLKey binding="xml:/Shipment/@BuyerOrganizationCode" value="xml:/Shipment/@BuyerOrganizationCode" />
		<yfc:makeXMLKey binding="xml:/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode" />
		<yfc:makeXMLKey binding="xml:/Shipment/@DocumentType" value="xml:/Shipment/@DocumentType" />
	</yfc:makeXMLInput>
	<input name="startReceiptKey" type="hidden" value='<%=getParameter("startReceiptKey")%>'/>

	<yfc:makeXMLInput name="findReceiptKey" >
		<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/Shipment/@DocumentType" />
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Shipment/@ReceivingNode" />
		<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode"/>
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="orderKey" >
		<yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/Shipment/@OrderNo" />
		<yfc:makeXMLKey binding="xml:/Order/@DocumentType" value="xml:/Shipment/@DocumentType" />
		<yfc:makeXMLKey binding="xml:/Order/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
	</yfc:makeXMLInput>
	<input name="FindReceiptKey" type="hidden" value='<%=getParameter("findReceiptKey")%>'/>

	<yfc:makeXMLInput name="containerPackKey" >
		<yfc:makeXMLKey binding="xml:/Container/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ReceivingNode" />
		<%
			if( "0001".equals(docType) ) { %>
				<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ShipNode" />
		<% } %>
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="ContainerHSDEPackKey" >
		<yfc:makeXMLKey binding="xml:/Container/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ShipNode" />
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="shipmentPrintKey">
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipNode" value="xml:/Shipment/@ShipNode" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@SCAC" value="xml:/Shipment/@SCAC" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@BuyerOrganizationCode" value="xml:/Shipment/@BuyerOrganizationCode" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipmentType" value="xml:/Shipment/@ShipmentType" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@PickListNo" value="xml:/Shipment/@PickListNo" />
		<yfc:makeXMLKey binding="xml:/Print/Shipment/@HazardousMaterialFlag" value="xml:/Shipment/@HazardousMaterialFlag" />
	</yfc:makeXMLInput>
	<input type="hidden" name="PrintEntityKey" value='<%=getParameter("shipmentPrintKey")%>'/>
	<input name="containerPackKey" type="hidden" value='<%=getParameter("containerPackKey")%>'/>
	<input name="containerHSDEPackKey" type="hidden" value='<%=getParameter("ContainerHSDEPackKey")%>'/>
	<input type="hidden" value='<%=userHasOverridePermissions()%>' name="userHasOverridePermissions" />
	<input type="hidden" name="xml:/Shipment/@DataElementPath" value="xml:/Shipment"/>
	<input type="hidden" name="xml:/Shipment/@ApiName" value="getShipmentDetails"/>
	<input type="hidden" name="xml:/OrderRelease/Order/@BuyerOrganizationCode" value='<%=resolveValue("xml:/Shipment/@BuyerOrganizationCode")%>' />
	<input type="hidden" name="xml:/OrderRelease/Order/@SellerOrganizationCode" value='<%=resolveValue("xml:/Shipment/@SellerOrganizationCode")%>' />
	<input type="hidden" name="pbReceiptKey" value='<%=getParameter("findReceiptKey")%>'/>

    <td class="detaillabel" >
        <yfc:i18n>Shipment_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipmentNo","xml:/Shipment/@ShipmentNo")%> />
         <input type="hidden" name="xml:/Shipment/@OverrideModificationRules" value="N"/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Shippers_Ref_#</yfc:i18n>
    </td>
	 <% if (!isVoid(modifyView)) {%>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@PickticketNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
    <% } else { %>
		<td class="protectedtext">
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@PickticketNo"/>
		</td>
	<% } %>

    <td class="detaillabel" >
        <yfc:i18n>Plan_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:makeXMLInput name="DeliveryPlanKey" >
			<yfc:makeXMLKey binding="xml:/DeliveryPlan/@DeliveryPlanKey" value="xml:/Shipment/@DeliveryPlanKey" />
		</yfc:makeXMLInput>
		<% if(showDeliveryPlanNo(resolveValue("xml:/Shipment/DeliveryPlan/@OwnerOrganizationCode"))) { %>
			<a <%=getDetailHrefOptions("L02",getParameter("DeliveryPlanKey"),"")%> ><yfc:getXMLValue binding="xml:/Shipment/DeliveryPlan/@DeliveryPlanNo"/></a>
		<% } else { %>
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/DeliveryPlan/@DeliveryPlanNo"/>
		<%}%>
	</td>
</tr>

<tr>
	<td class="detaillabel">
		<yfc:i18n>Enterprise</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/Shipment/@EnterpriseCode"/>
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@EnterpriseCode","xml:/Shipment/@EnterpriseCode")%> />
	</td>
    <td class="detaillabel" >
		<%if(equals("omr",sAppCode)){%>
			<yfc:i18n>Return_From_Node</yfc:i18n>
		<%}else{%>
			<yfc:i18n>Ship_Node</yfc:i18n>
		<%}%>
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="ShipNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ShipNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("ShipNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipNode"/></a>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipNode","xml:/Shipment/@ShipNode")%> />
    </td>
	<td class="detaillabel" >
		<yfc:i18n>Seller</yfc:i18n>
	</td>
	<td class="protectedtext" >
		<yfc:makeXMLInput name="sellerOrganizationKey" >
			<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Shipment/@SellerOrganizationCode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L03",getParameter("sellerOrganizationKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@SellerOrganizationCode"/></a>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@SellerOrganizationCode","xml:/Shipment/@SellerOrganizationCode")%> />
	</td>
</tr>

<tr>
	<td class="detaillabel" >
		<yfc:i18n>Buyer</yfc:i18n>
	</td>
	<td class="protectedtext" >
		<yfc:getXMLValue  binding="xml:/Shipment/@BillToCustomerId"/>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@BillToCustomerId","xml:/Shipment/@BillToCustomerId")%> />
	</td>
    <td class="detaillabel" >
		<%if(equals("omr",sAppCode)){%>
			<yfc:i18n>Return_To_Node</yfc:i18n>
		<%}else{%>
			<yfc:i18n>Receiving_Node</yfc:i18n>
		<%}%>
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="receivingNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ReceivingNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("receivingNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ReceivingNode"/></a>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Merge_Node</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:makeXMLInput name="MergeNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@MergeNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("MergeNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@MergeNode"/></a>
    </td>

</tr>
<tr>
	<% if(isVoid(resolveValue("xml:/Shipment/@OrderNo") ) )	{ %>	<!-- cr 35459 -->
		<td></td>
		<td></td>
	<% } else { %>
		<td class="detaillabel" >
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
		<td class="protectedtext" >
	<% if(!isVoid(resolveValue("xml:/Shipment/@OrderHeaderKey"))){%>
				<a <%=getDetailHrefOptions("L05",getParameter("orderKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@OrderNo"/></a>
		<%}else{%>
			<yfc:getXMLValue  binding="xml:/Shipment/@OrderNo"/>
		<%}%>
			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@OrderNo","xml:/Shipment/@OrderNo")%>/ >
		</td>
	<% }

	if(equals("0",resolveValue("xml:/Shipment/@ReleaseNo")))	{	%>	<!-- cr 35459 -->
		<td></td>
		<td></td>
	<% } else { %>
		<td class="detaillabel" >
			<yfc:i18n>Release_#</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:getXMLValue  binding="xml:/Shipment/@ReleaseNo"/>
			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ReleaseNo","xml:/Shipment/@ReleaseNo")%> />
		</td>
	<%}%>
	<td ></td>
	<td ></td>
</tr>
<tr>
	<td class="detaillabel" nowrap="true" >
        <yfc:i18n>Has_Hazardous_Item(s)</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/Shipment/@HazardousMaterialFlag"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Status</yfc:i18n>
    </td>
	<td class="protectedtext">
      <a <%=getDetailHrefOptions("L04",getParameter("startReceiptKey"),"")%> ><yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@StatusName"/></a>
    </td>
	<td class="detaillabel">
		<yfc:i18n>Shipping_Account_Code</yfc:i18n>
	</td>
	<td>
		<input type="input" class="unprotectedinput" size="50" <%=getTextOptions("xml:/Shipment/Extn/@ExtnShipAcctCode", "xml:/Shipment/Extn/@ExtnShipAcctCode")%>/>
	</td>
</tr>
<tr>
	<td class="detaillabel">
		<yfc:i18n>Actual_Shipment_Date</yfc:i18n>
	</td>
	<td class="protectedtext" >
		<input type="text" class="unprotectedinput" <%=yfsGetTextOptions("xml:/Shipment/@ActualShipmentDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@ActualShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
		<input type="text" class="unprotectedinput" <%=yfsGetTextOptions("xml:/Shipment/@ActualShipmentDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
		<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@ActualShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>

	<td class="detaillabel">
		<yfc:i18n>Estimated_Depart_Date</yfc:i18n>
	</td>
<!--
	<td class="protectedtext" >
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCDATE", formatDate.format(now))%>/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCTIME", formatTime.format(now))%>/>
		<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
	</td>
-->
	<!-- Top of CR 542 -->
	<td class="protectedtext" >
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCDATE")%>/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCTIME")%>/>
		<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
	</td>
	<!-- CR 542 -->

	<td class="detaillabel">
		<yfc:i18n>Estimated_Arrival_Date</yfc:i18n>
	</td>
	<td class="protectedtext" >
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedArrivalDate_YFCDATE")%>/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnEstimatedArrivalDate_YFCTIME")%>/>
		<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
	</td>
</tr>
<tr>
	<td class="detaillabel">
		<yfc:i18n>Driver_Name</yfc:i18n>
	</td>
	<td>
		<input type="input" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnDriversName", "xml:/Shipment/Extn/@ExtnDriversName")%>/>
	</td>
	<td class="detaillabel">
		<yfc:i18n>Shipment_Tracking_Number</yfc:i18n>
	</td>
	<td >
		<textArea rows="3" cols="20" onfocus="setRange(this,0,0);" onkeypress="return maxLength(this,250);" onpaste="return maxLengthPaste(this,250);" <%=getTextAreaOptions("xml:/Shipment/Extn/@ExtnTrackingNumbers")%>> <yfc:getXMLValue binding="xml:/Shipment/Extn/@ExtnTrackingNumbers"/> </textArea>
	</td>
	<td class="detaillabel">
	<yfc:i18n>Shipment_Pieces</yfc:i18n>
	</td>
	<td>
		<Input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Shipment/Extn/@ExtnShipmentPieces")%>/>
	</td>
</tr>
<tr>
	<td class="detaillabel">
		<yfc:i18n>Vehicle_ID</yfc:i18n>
	</td>
	<td>
		<input type="input" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Extn/@ExtnVehicleId", "xml:/Shipment/Extn/@ExtnVehicleId")%>/>
	</td>
</tr>
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@DocumentType","xml:/Shipment/@DocumentType")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@OrderHeaderKey","xml:/Shipment/@OrderHeaderKey")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@ActualShipmentDate","xml:/Shipment/@ActualShipmentDate")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@OrderAvailableOnSystem","xml:/Shipment/@OrderAvailableOnSystem")%> />
</table>
