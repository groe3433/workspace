<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_workorderDetails.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>

<%  
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String createView = resolveValue("xml:/WorkOrder/@WorkOrderMode");
    createView = createView == null ? "" : createView;

	YFCElement rootElem = (YFCElement)request.getAttribute("WorkOrder");

%>

<table class="view" width="100%">
	<tr>
        <td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
		<% if(modifyView != "" || createView != "") {%> 
				<yfc:makeXMLInput name="ItemKey" >
					<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/@ItemID" value="xml:/WorkOrder/@ItemID" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/@Uom" value="xml:/WorkOrder/@Uom" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/@ProductClass" value="xml:/WorkOrder/@ProductClass" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/@EnterpriseCode" value="xml:/WorkOrder/@EnterpriseCode" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@BatchNo" value="xml:/WorkOrder/WorkOrderTag/@BatchNo"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@LotAttribute1" value="xml:/WorkOrder/WorkOrderTag/@LotAttribute1"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@LotAttribute2" value="xml:/WorkOrder/WorkOrderTag/@LotAttribute2"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@LotAttribute3" value="xml:/WorkOrder/WorkOrderTag/@LotAttribute3"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@LotExpirationDate" value="xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@LotNumber" value="xml:/WorkOrder/WorkOrderTag/@LotNumber"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@ManufacturingDate" value="xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@RevisionNo" value="xml:/WorkOrder/WorkOrderTag/@RevisionNo"/>
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderTag/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey"/>
				</yfc:makeXMLInput>

			<input type="hidden" name="ItemKey" value='<%=getParameter("ItemKey")%>'/>

			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@ItemID")%></td>
			<input type="hidden" name="xml:/WorkOrder/@ItemID" value="<%=resolveValue("xml:/WorkOrder/@ItemID")%>"/>
		<%}
		else
		{%>
			<td class="tablecolumn" nowrap="true" >
				<!--<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/@ItemID")%>/>-->
<input class="unprotectedinput" type="text" OldValue="" onBlur="fetchDataWithParams(this,'getItemList',populateIncidentItemDetailsForWO,setItemParam(this))"<%=getTextOptions("xml:/WorkOrder/@ItemID")%>/>		

				<img class="lookupicon" name="search" 				onclick="callItemLookup('xml:/WorkOrder/@ItemID','xml:/WorkOrder/@ProductClass','xml:/WorkOrder/@Uom',
				'item','xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCode'].value)" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
			</td>
		<%}%>
		<td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
		<% if(modifyView != "" || createView != "") {%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@Uom")%></td>
			<input type="hidden" name="xml:/WorkOrder/@Uom" value="<%=resolveValue("xml:/WorkOrder/@Uom")%>"/>
		<%}
		else
		{%>
			<td nowrap="true" class="tablecolumn">
				<select name="xml:/WorkOrder/@Uom" class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/WorkOrder/@Uom"/>
				</select>
			</td>
		<%}%>
		<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
		<%if(modifyView != ""){%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@ProductClass")%></td>
			<input type="hidden" name="xml:/WorkOrder/@ProductClass" value="<%=resolveValue("xml:/WorkOrder/@ProductClass")%>"/>
		<%}else if(createView != ""){
			String selectedProductClass = "";%>			
			<yfc:loopXML binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode" id="ProductClassList">
				<%if(equals(resolveValue("xml:ProductClassList:/CommonCode/@CodeValue"), resolveValue("xml:/WorkOrder/@ProductClass"))){
					selectedProductClass=resolveValue("xml:ProductClassList:/CommonCode/@CodeValue");
				}%>
			</yfc:loopXML>
			<td class="protectedtext"><%=selectedProductClass%></td>
			<input type="hidden" name="xml:/WorkOrder/@ProductClass" value="<%=resolveValue("xml:/WorkOrder/@ProductClass")%>"/>
		<%}else{%>
			<td nowrap="true" class="tablecolumn">
				<select <%=getComboOptions("xml:/WorkOrder/@ProductClass")%> class="combobox">
					<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
					value="CodeValue" selected="xml:/WorkOrder/@ProductClass"/>
				</select>
			</td>
		<%}%>
	</tr>
	<tr>
		<% if(modifyView != ""){%>
			<td class="detaillabel" ><yfc:i18n>Description</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/Item/PrimaryInformation/@Description")%></td>
			<input type="hidden" name="xml:/WorkOrder/Item/PrimaryInformation/@Description" value="<%=resolveValue("xml:/WorkOrder/Item/PrimaryInformation/@Description")%>"/>
		<%}else{%>
			<td class="detaillabel" ><yfc:i18n>Requested_Quantity</yfc:i18n></td>
			<td nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@QuantityRequested")%>/>
			</td>
			<Input type="hidden" name="xml:/WorkOrder/@QuantityRequested" value="<%=resolveValue("xml:/WorkOrder/@QuantityRequested")%>"/>
		<%}%>
        <td class="detaillabel" ><yfc:i18n>Segment_Type</yfc:i18n></td>
		<%if(modifyView != ""){%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@SegmentType")%></td>
			<input type="hidden" name="xml:/WorkOrder/@SegmentType" value="<%=resolveValue("xml:/WorkOrder/@SegmentType")%>"/>
		<%}else if(createView != ""){
			String selectedSegmentType = "";%>			
			<yfc:loopXML binding="xml:WorkOrderSegmentTypeList:/CommonCodeList/@CommonCode" id="WorkOrderSegmentTypeList">
				<%if(equals(resolveValue("xml:WorkOrderSegmentTypeList:/CommonCode/@CodeValue"), resolveValue("xml:/WorkOrder/@SegmentType"))){
					selectedSegmentType=resolveValue("xml:WorkOrderSegmentTypeList:/CommonCode/@CodeShortDescription");
				}%>
			</yfc:loopXML>
			<td class="protectedtext"><%=selectedSegmentType%></td>
			<input type="hidden" name="xml:/WorkOrder/@SegmentType" value="<%=resolveValue("xml:/WorkOrder/@SegmentType")%>"/>
		<%}else{%>
			<td class="tablecolumn">
				<select <%=getComboOptions("xml:/WorkOrder/@SegmentType")%> class="combobox">
					<yfc:loopOptions binding="xml:WorkOrderSegmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" selected="xml:/WorkOrder/@SegmentType"  isLocalized="Y" />
				</select>
			</td>
		<%}%>
		<td class="detaillabel" ><yfc:i18n>Segment</yfc:i18n></td>
		<% if(modifyView != "" || createView != "") {%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@Segment")%></td>
			<input type="hidden" name="xml:/WorkOrder/@Segment" value="<%=resolveValue("xml:/WorkOrder/@Segment")%>"/>
		<%}
		else
		{%>
			<td nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@Segment")%>/>
			</td>
		<%}%>
	</tr>
	<% if(modifyView != "" && !isVoid(resolveValue("xml:/WorkOrder/OrderLine/Order/@OrderNo"))){%>
		<tr>
			<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="protectedtext">
				<%if(!isVoid(resolveValue("xml:/WorkOrder/OrderLine/Order/@OrderNo"))){%>
					<yfc:makeXMLInput name="OrderLineKey" >
						<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/WorkOrder/@OrderLineKey" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L01",getParameter("OrderLineKey"),"")%> >
						<yfc:getXMLValue binding="xml:/WorkOrder/OrderLine/Order/@OrderNo"/>
					</a>
				<%}%>
			</td>
		</tr>
      <%}%>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@BatchNo" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@BatchNo")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@LotAttribute1" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@LotAttribute1")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@LotAttribute2" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@LotAttribute2")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@LotAttribute3" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@LotAttribute3")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@LotExpirationDate" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@LotExpirationDate")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@LotNumber" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@LotNumber")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@ManufacturingDate" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@ManufacturingDate")%>"/>
	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@RevisionNo" value="<%=resolveValue("xml:/WorkOrder/WorkOrderTag/@RevisionNo")%>"/>
	<yfc:hasXMLNode binding="xml:ItemDetailsTagControl:/Item/InventoryTagAttributes/Extn">
<%
	YFCElement extnElem = ((YFCElement)request.getAttribute("ItemDetailsTagControl")).getChildElement("InventoryTagAttributes").getChildElement("Extn");
	Map oExtnAttrMap = extnElem.getAttributes();
	for(Iterator i = oExtnAttrMap.keySet().iterator();i.hasNext();){
		String sName = (String)i.next();
		String sValue = (String)oExtnAttrMap.get(sName);
		if(!(YFCObject.equals("01", sValue) || YFCObject.equals("02", sValue))){
			continue;
		}
		String sBinding = "xml:/WorkOrder/WorkOrderTag/Extn/@"+sName ;
%>
		<Input type=hidden name="<%=sBinding%>" value="<%=resolveValue(sBinding)%>"/>
<%
	}
%>		
    </yfc:hasXMLNode>

	<Input type=hidden name="xml:/WorkOrder/WorkOrderTag/@WorkOrderKey" value="<%=resolveValue("xml:/WorkOrder/@WorkOrderKey")%>"/>
</table>
