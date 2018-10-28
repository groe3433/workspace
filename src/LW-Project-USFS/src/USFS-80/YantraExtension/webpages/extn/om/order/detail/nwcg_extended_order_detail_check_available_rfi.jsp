<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/extn.js"></script>

<%	
YFCElement curUsr = (YFCElement)session.getAttribute(NWCGConstants.CURRENT_USER); 
String UserNode = curUsr.getAttribute(NWCGConstants.NODE);
String strInventoryStatus = NWCGConstants.RFI_STATUS;
String strOrganizationCode = NWCGConstants.ENTERPRISE_CODE;
String description = NWCGConstants.EMPTY_STRING;

String strItemID = resolveValue("xml:/Item/@ItemID");
String strPC = resolveValue("xml:/Item/@ProductClass");
String strUOM = resolveValue("xml:/Item/@TransactionalUOM");

YFCElement getNodeInventoryInput = YFCDocument.parse("<NodeInventory Node=\"" + UserNode + "\"><Inventory InventoryStatus=\"" + strInventoryStatus + "\"><InventoryItem ItemID=\"" + strItemID + "\"/></Inventory></NodeInventory>").getDocumentElement();
YFCElement nodeInventoryTemplate = YFCDocument.parse("<NodeInventory><LocationInventoryList TotalNumberOfRecords=\"\"><LocationInventory LocationId=\"\" PendInQty=\"\" PendOutQty=\"\" Quantity=\"\" ZoneId=\"\"><InventoryItem ItemID=\"\"><Item ItemID=\"\"></Item></InventoryItem></LocationInventory></LocationInventoryList></NodeInventory>").getDocumentElement();
YFCElement getATPInput = YFCDocument.parse("<GetATP  ShipNode=\"" + UserNode + "\" ItemID=\"" + strItemID + "\" ProductClass=\"" + strPC + "\" UnitOfMeasure=\"" + strUOM + "\" OrganizationCode=\"" + strOrganizationCode + "\" />").getDocumentElement();

double TotalLocationInvQty = 0;
double TotalPendingIN = 0;
double TotalPendingOUT = 0;
double TotalDemand = 0;
double TotalAvailRFI = 0;
%>

<yfc:callAPI apiName="getNodeInventory" inputElement="<%=getNodeInventoryInput%>" templateElement="<%=nodeInventoryTemplate%>" outputNamespace="NodeInventory"/>
<yfc:callAPI apiName="getATP" inputElement="<%=getATPInput%>"  outputNamespace="InventoryInformation"/>

<yfc:loopXML binding="xml:/NodeInventory/LocationInventoryList/@LocationInventory" id="LocationInventory"> 
<%
		YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory"); 
  	 	
		double dQty = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@Quantity"));
		TotalLocationInvQty = TotalLocationInvQty + dQty;
		double dPendingIN = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@PendInQty"));
	   	TotalPendingIN = TotalPendingIN + dPendingIN;
	   	double dPendingOUT = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@PendOutQty"));
	   	TotalPendingOUT = TotalPendingOUT + dPendingOUT;
%>
</yfc:loopXML> 

<div style="height:440px;overflow:auto">
<br/>
<table class="table" ID="rfiTable" cellspacing="0" yfcMaxSortingRecords="100" border="0">

<thead>

<tr>
	<td class="tablecolumn">
	  <yfc:i18n>Item_ID</yfc:i18n>
	</td>
	<td class="tablecolumn">
	  <input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Item/@ItemID")%>/>
	</td>
</tr>

<tr>
	<td class="tablecolumn">
	  <yfc:i18n>Node</yfc:i18n>
	</td>
	<td class="tablecolumn">
	  <input type="text" class="protectedinput" readonly="true" <%=getTextOptions(UserNode)%>/>
	</td>
</tr>

<tr>
	<td class="tablecolumn" style="width:<%=getUITableSize(new String("Total Location Inventory Qty"))%>">
		<input type="text" class="protectedinput" readonly="true" value="Total Location Inventory Qty"/>
	</td>
	<td class="tablecolumn" align="left">
		<input type="text" class="protectedinput" readonly="true" value="<%=(new String("+ ".concat(Double.toString(TotalLocationInvQty))))%>"/>
	</td>
</tr>

<yfc:loopXML binding="xml:/InventoryInformation/Item/InventoryTotals/Demands/@Demand" id="Demand"> 
<tr>
	<% YFCElement nodeElement= (YFCElement) request.getAttribute("InventoryInformation"); %>
 		<td class="tablecolumn">
			<%
			String demandType = resolveValue("xml:/Demand/@DemandType");
			String demandTypeDescription ="";
			if(!demandType.equals(""))
			{
				if(demandType.equals("ALLOCATED")) {
					demandTypeDescription = "Allocated";
					description = "Included in Shipment from an Incident/Other Order/Cache Transfer/Work Order";
				} else if (demandType.equals("OPEN_ORDER")) {
					demandTypeDescription = "Open Order";
					description = "Assigned to orders (Incident/Other Order/Cache Transfer/Work Order) in Draft Order/Created Status";
				} else if (demandType.equals("RESERVED")) {
					demandTypeDescription = "Reserved";
					description = "Reserved from Inventory console that has not been included in an Incident/Other Order/Cache Transfer/Work Order";
				} else if (demandType.equals("BACKORDER")) {
					demandTypeDescription = "Backorder";
				} else if (demandType.equals("DEMAND_FOR_RELEASE")) {
					demandTypeDescription = "Demand to look for during release";	
				} else if (demandType.equals("DMD_FOR_CTC_RELEASE.ex")) {
					demandTypeDescription = "Demand to look for during release of CTC transfers";	
				} else if (demandType.equals("FIRM_FORECAST")) {
					demandTypeDescription = "Firm Forecast";	
				} else if (demandType.equals("FORECAST")) {
					demandTypeDescription = "Forecast";	
				} else if (demandType.equals("FORECAST_NEGOTIATED")) {
					demandTypeDescription = "Forecast Negotiated";
				} else if (demandType.equals("RSRV_ORDER")) {
					demandTypeDescription = "Reserved Order";
					description = "Reserved and included in an Incident/Other Order/Cache Transfer/Work Order";
				} else if (demandType.equals("SCHEDULED")) {
					demandTypeDescription = "Scheduled";
				} else if (demandType.equals("SCHEDULED_CTC.ex")) {
					demandTypeDescription = "Scheduled for CTC transfers";
				} else {
					demandTypeDescription = "";
				}
			}			
			%>
			<input type="text" class="protectedinput" readonly="true" value="<%=demandTypeDescription%>"/>
		</td>
		<td class="tablecolumn">
			<% 
				double dblDemand = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/Demand/@Quantity"));
				TotalDemand = TotalDemand + dblDemand;
				StringBuffer sb = new StringBuffer("- ");
				sb.append(Double.toString(dblDemand));
				String demandStrDisplay = sb.toString();
			%>
			<input type="text" class="protectedinput" readonly="true" value="<%=demandStrDisplay%>"/>
		</td>
</tr>
</yfc:loopXML>

<tr>
		<td class="tablecolumn">
			<input type="text" class="protectedinput" readonly="true" value="Available RFI Qty"/>
		</td>
		<td class="tablecolumn">
			<% TotalAvailRFI = (TotalLocationInvQty - TotalDemand); 
			   String totalAvailRFIStr = Double.toString(TotalAvailRFI);
			%>
			<input type="text" class="protectedinput" readonly="true" value="<%=totalAvailRFIStr%>"/>		
		</td>        
</tr>

<tr>
		<td class="tablecolumn">
			<input type="text" class="protectedinput" readonly="true" value="Total Pending IN"/>
		</td>
		<td class="tablecolumn">
			<input type="text" class="protectedinput" readonly="true" value="<%=TotalPendingIN%>"/>
		</td>
</tr>

<tr>
		<td class="tablecolumn" >
			<input type="text" class="protectedinput" readonly="true" value="Total Pending OUT"/>
		</td>
		<td class="tablecolumn" >
			<input type="text" class="protectedinput" readonly="true" value="<%=TotalPendingOUT%>"/>
		</td>
</tr>

</thead>

</table>

<br/>

<table class="table" border="0" cellspacing="0">
<thead>
    <tr> 
    
        <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/LocationInventory/@LocationId")%>">
            <yfc:i18n>Location</yfc:i18n>
        </td>
        <td class="numerictablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/LocationInventory/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
   		<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/LocationInventory/@PendInQty")%>">
            <yfc:i18n>Pending_In</yfc:i18n>
        </td>
  		<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/LocationInventory/@PendOutQty")%>">
            <yfc:i18n>Pending_Out</yfc:i18n>
        </td>
  
    </tr>
</thead>

<tbody>
    <yfc:loopXML binding="xml:/NodeInventory/LocationInventoryList/@LocationInventory" id="LocationInventory" > 
    <tr> 
    
		<% YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory"); %>
		<td class="tablecolumn" >
			<input type="text" class="protectedinput" <%=getTextOptions("xml:/LocationInventory/@LocationId")%>/>
        </td>
        <td class="tablecolumn">
		   <input type="text" class="protectedinput" <%=getTextOptions("xml:/LocationInventory/@Quantity")%>/>
		   	<% 	
		   		double dQty = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@Quantity"));
		   		TotalLocationInvQty = TotalLocationInvQty + dQty;
			%>
		</td>
		<td class="tablecolumn" >
			<input type="text" class="protectedinput" <%=getTextOptions("xml:/LocationInventory/@PendInQty")%>/>
			<% 	
		   		double dPendingIN = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@PendInQty"));
		   		TotalPendingIN = TotalPendingIN + dPendingIN;
			%>
        </td>
        <td class="tablecolumn" >
			<input type="text" class="protectedinput" <%=getTextOptions("xml:/LocationInventory/@PendOutQty")%>/>
			<% 	
		   		double dPendingOUT = getDoubleFromLocalizedString(getLocale(),resolveValue("xml:/LocationInventory/@PendOutQty"));
		   		TotalPendingOUT = TotalPendingOUT + dPendingOUT;
			%>
        </td>   
             
	</tr>	
    </yfc:loopXML> 
</tbody>
</table>
</div>