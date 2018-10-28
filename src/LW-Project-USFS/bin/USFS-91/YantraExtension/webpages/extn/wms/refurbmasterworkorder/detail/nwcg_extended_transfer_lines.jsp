<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="JavaScript">
function setDestinationCache()
{
	var destinationCache = document.getElementById('xml:/NWCGMasterWorkOrderLineList/@DestinationCache');
}

function checkInput()
{
	if(checkCacheSelection())
	{
		if(checkQuantities()) return true;
		else return false;
	}
	else return false;
}

function checkCacheSelection()
{
	var nodeName;
	var destinationCache = document.getElementById('xml:/NWCGMasterWorkOrderLineList/@DestinationCache');
	var inputElementList = document.getElementsByTagName("input");					
	for (i = 0 ; i < inputElementList.length ; i++)
	{
		if(inputElementList[i].name.indexOf('/@Node') != -1)
		{
			nodeName = inputElementList[i].value;	
			break;
		}
	}
	
	if(destinationCache.value == 'Select Cache' || destinationCache.value == '')
	{
		alert('Select a Destination Cache');
		return false;
	}
	else if(destinationCache.value == nodeName)
	{
		alert('Shipping and Receiving Nodes cannot be the same.\nPlease select a different Destination Cache.');
		return false;
	}
	else return true;
}

function checkQuantities()
{
	var error = false;
	var inputElementList = document.getElementsByTagName("input");					

	for (i = 0 ; i < inputElementList.length ; i++)
	{
		if(inputElementList[i].name.indexOf('/@TransferQuantity') != -1)
		{
			//alert(inputElementList[i].name+' = '+inputElementList[i].value);	
			var newTransferQtyStr = inputElementList[i];
			var endIndex = inputElementList[i].name.lastIndexOf("/@TransferQuantity");
			var currentPrefix = inputElementList[i].name.substring(0, endIndex);
			
			var actualQtyStr = document.getElementById(currentPrefix + '/@ActualQuantity');
			var refurbQtyStr = document.getElementById(currentPrefix + '/@RefurbishedQuantity');
			var transferQtyStr = document.getElementById(currentPrefix + '/@TransferredQuantity');
			
			//alert(currentPrefix + '/@ActualQuantity = ' + actualQtyStr.value);
			//alert(currentPrefix + '/@RefurbishedQuantity = ' + refurbQtyStr.value);
			//alert(currentPrefix + '/@TransferredQuantity = ' + transferQtyStr.value);
			
			var actualQty = parseFloat(actualQtyStr.value);
			var refurbQty, transferQty, newTransferQty;

			if(refurbQtyStr.value == ''){refurbQty = 0.0;}
			else{refurbQty = parseFloat(refurbQtyStr.value);}
			
			if(transferQtyStr.value == ''){transferQty = 0.0;}
			else{transferQty = parseFloat(transferQtyStr.value);}

			if(newTransferQtyStr.value == ''){newTransferQty = 0.0;}
			else{newTransferQty = parseFloat(newTransferQtyStr.value);}

			var remainingQty = actualQty - refurbQty - transferQty;
			
			if((newTransferQty == 0.0) || (newTransferQty > remainingQty))
			{
				error = true;
				alert('Enter a valid quantity between 1 and ' + remainingQty);	
				inputElementList[i].focus();
				break;
			}
		}
	}
	
	if(error) return false;
	else return true;
}
</script>

<%
	String orderByElement = "<OrderBy><Attribute Name=\"ShipNode\"/></OrderBy>";
	YFCElement nodeInput = YFCDocument.parse("<ShipNode IgnoreOrdering=\"N\">" + orderByElement + "</ShipNode>").getDocumentElement();
%>
<yfc:callAPI serviceName="NWCGSanitizedCacheList" inputElement="<%=nodeInput%>" outputNamespace="CommonNodeList"/>

<table width="100%" class="view">
	<tr>
        <td class="detaillabel"><yfc:i18n>Destination Cache</yfc:i18n></td>
		<td>
			<select class="combobox" name="xml:/NWCGMasterWorkOrderLineList/@DestinationCache" onchange="setDestinationCache();">
				<yfc:loopOptions binding="xml:CommonNodeList:/ShipNodeList/@ShipNode" name="ShipNode" value="ShipNode" selected='Select Cache' />
			</select>
		</td>
	</tr>
	<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/@DestinationCache","xml:/NWCGMasterWorkOrderLineList/@DestinationCache")%>
</table>

<%
	String selectedLines = getParameter("mwoSelectedLines");
	YFCElement ipElement = YFCDocument.parse("<SelectedLines>" + selectedLines + "</SelectedLines>").getDocumentElement();
%>

<yfc:callAPI serviceName="NWCGShowSelectedTransferLines" inputElement="<%=ipElement%>" />

<table class="table" ID="SelectedTranferLines" cellspacing="0" width="100%" >
<thead>
	<tr>
		<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@SerialNo")%>"><yfc:i18n>Trackable_ID</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@ActualQuantity")%>"><yfc:i18n>Actual_Qty</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@ActualQuantity")%>"><yfc:i18n>Remaining_Qty</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@TransferQty")%>"><yfc:i18n>Transferred_Qty</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine/@TransferQty")%>"><yfc:i18n>Transfer_Qty</yfc:i18n></td>
	</tr>
</thead>
<tbody>
	<yfc:loopXML binding="xml:/NWCGMasterWorkOrderLineList/@NWCGMasterWorkOrderLine" id="MasterWorkOrderLine">
		<tr>
		    <td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ItemID"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@PrimarySerialNo"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ItemDesc"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ProductClass"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@UnitOfMeasure"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ActualQuantity"/></td>
<%
	String mwoKey = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@MasterWorkOrderKey");
	String mwoLineKey = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey");

	String actualQty = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ActualQuantity");
	String refurbQty = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@RefurbishedQuantity");
	String transferQty = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@TransferQty");
	String primarySerialNo = resolveValue("xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@PrimarySerialNo");

	if((refurbQty == null) || (refurbQty != null && refurbQty.trim().equals(""))) refurbQty = "0.0";
	if((transferQty == null) || (transferQty != null && transferQty.trim().equals(""))) transferQty = "0.0";

	double dActualQty = Double.parseDouble(actualQty);
	double dRefurbQty = Double.parseDouble(refurbQty);
	double dTransferQty = Double.parseDouble(transferQty);
	String remainingQty = Double.toString(dActualQty-dRefurbQty-dTransferQty);
	
%>
			<td class="tablecolumn"><%=remainingQty%></td>
			<td class="tablecolumn">
				<yfc:getXMLValue binding="xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@TransferQty"/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@TransferredQuantity","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@TransferQty")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@MasterWorkOrderLineKey","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@PrimarySerialNo","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@PrimarySerialNo")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@Node","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@Node")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@ActualQuantity","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ActualQuantity")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@RefurbishedQuantity","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@RefurbishedQuantity")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@ItemID","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ItemID")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@ItemDesc","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ItemDesc")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@ProductClass","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@ProductClass")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@UnitOfMeasure","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@UnitOfMeasure")%>/>
				<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@TaskKey","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@TaskKey")%>/>
			</td>
			<td class="tablecolumn">
			<% if(primarySerialNo != null && !primarySerialNo.trim().equals("")){%>
				<input type="text" class="protectedtext" readonly <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@TransferQuantity","1")%> />
			<%}else{%>			
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine_" + MasterWorkOrderLineCounter + "/@TransferQuantity","xml:MasterWorkOrderLine:/NWCGMasterWorkOrderLine/@NewTransferQty")%> />
			<%}%>
			</td>
		</tr>
	</yfc:loopXML>
	<input type="hidden" <%=getTextOptions("xml:/NWCGMasterWorkOrderLineList/@MasterWorkOrderKey","xml:/NWCGMasterWorkOrder/@MasterWorkOrderKey")%>
</tbody>
</table>