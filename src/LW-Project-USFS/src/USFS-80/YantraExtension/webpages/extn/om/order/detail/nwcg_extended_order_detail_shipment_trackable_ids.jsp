<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript">
function checkShipmentStatus()
{
	alert('Checking Shipment status...');	
	return true;	
}
</script>

<%!
public char counter = 'a';

public String getLogicalValue(String inStr)
{
	String inputStr = resolveValue(inStr);
	System.out.println("DebugSS: inputStr: " + inputStr);
	String count =  Character.toString(counter++);	
	
//	inputStr = inputStr.replace("-","");
//	System.out.println("DebugSS: inputStr: " + inputStr);

	System.out.println("DebugSS: count: " + count);
	return count;
}
%>

<table class="table" width="100%">
	<thead>
		<tr>
			<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@OrderNo")%>"><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="yes" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@PickticketNo")%>"><yfc:i18n>Item</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipmentNo")%>"><yfc:i18n>Shipment_#</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="yes" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@SerialNo")%>">
				<yfc:i18n>Trackable_ID</yfc:i18n>
			</td>
		</tr>
	</thead>

	<tbody>

	<!-- Loop over Shipment records from getShipmentListForOrder() API output list. -->
	<yfc:loopXML binding="xml:/ShipmentList/@Shipment" id="Shipment">

		<yfc:makeXMLInput name="shipmentKey" >
			<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		</yfc:makeXMLInput>

		<!-- For each Shipment record in the list, call getShipmentDetails() API. -->
		<% request.setAttribute("Shipment", pageContext.getAttribute("Shipment")); %>

		<yfc:callAPI apiID="AP2"/>

		<!-- For each ShipmentDetails record loop over ShipmentLine records -->
		<yfc:loopXML binding="xml:ShipmentDetails:/ShipmentDetails/@ShipmentLine" id="ShipmentLine">

				<tr>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ShipmentLine/@OrderNo"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ShipmentLine/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Shipment/@ShipmentNo"/></td>
					<td class="tablecolumn" sortValue="<%=getLogicalValue("xml:/ShipmentLine/@SerialNo")%>">
						<yfc:getXMLValue binding="xml:/ShipmentLine/@SerialNo"/>
					</td>				
				</tr>

		</yfc:loopXML>

	</yfc:loopXML>

	</tbody>
</table>