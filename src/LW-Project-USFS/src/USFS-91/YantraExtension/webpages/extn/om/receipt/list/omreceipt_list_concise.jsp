<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<% if (resolveValue("xml:yfcsearchcriteria:/Receipt/Shipment/@OrderNo")!=null)
{ %>
 <yfc:callAPI apiID="AP2"/>
<% } %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript">
	function checkAndMovetoReceive(key,ViewId){
	
		if(yfcAllowSingleSelection(key)){
			var ReceiptOpenKey;
			var keyval;
			var eleArray = document.getElementsByName(key);
			for ( var i =0; i < eleArray.length; i++ ) 
			{
				if ( eleArray.item(i).type =="checkbox" && eleArray.item(i).checked )
					{
						keyval= eleArray.item(i).value;
						ReceiptOpenKey = eleArray[i].getAttribute("ReceiptOpenKey");
					}
			}
			if(ReceiptOpenKey=="Y"){
			showDetailForOnAdvancedList('omreceipt', ViewId, keyval, "") ;
			}else{
			alert('<%=getI18N("Receipt_not_open.")%>');
			return false;
			}
		}
	}
</script>
<table class="table" editable="false" width="100%" cellspacing="0">
    <thead>
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Shipment_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Incident #</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Seller</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Start_Date</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Receiving_Node</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Receipt_Open</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Receipt_Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
	    <yfc:loopXML binding="xml:/ReceiptList/@Receipt" id="Receipt" >
            <tr>
				<yfc:makeXMLInput name="receiptKey">
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/Receipt/Shipment/@DocumentType"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ShipmentKey" value="xml:/Receipt/@ShipmentKey"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@OrderNo" value="xml:/Receipt/Shipment/@OrderNo"> </yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode"> 
					</yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@BillToCustomerId" value="xml:/Receipt/Shipment/@BillToCustomerId"> 
					</yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@SellerOrganizationCode" value="xml:/Receipt/Shipment/@SellerOrganizationCode">
					</yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ExtnIncidentNo" value="xml:/Receipt/Extn/@ExtnIncidentNo"> </yfc:makeXMLKey>
				</yfc:makeXMLInput>
	           <td class="checkboxcolumn">
					<input type="checkbox" value='<%=getParameter("receiptKey")%>' name="EntityKey"
					ReceiptOpenKey='<%=resolveValue("xml:/Receipt/@OpenReceiptFlag")%>'/>
				</td>
	            <td class="tablecolumn">
				<a   href="javascript:showDetailFor('<%=getParameter("receiptKey")%>')"   >
  					<yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/>
				</a>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@ShipmentNo"/>
				</td>

				<% 
					//String IncidentNo = resolveValue("xml:/Receipt/Extn/@ExtnIncidentNo");
				%>

				<td class="tablecolumn"> <%=resolveValue("xml:/Receipt/Extn/@ExtnIncidentNo")%>
					
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@OrderNo"/>
				</td>
				<td class="tablecolumn"> 
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@EnterpriseCode"/>
				</td>
				<% 
					//String CustId = resolveValue("xml:/Receipt/Shipment/@BillToCustomerId");
				%>
				<td class="tablecolumn"> <%=resolveValue("xml:/Receipt/Shipment/@BillToCustomerId")%>
					
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@SellerOrganizationCode"/>
				</td>
	            <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Receipt/@ReceiptDate")%>"><yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/></td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB binding="xml:/Receipt/Status/@StatusName"/>
				</td>
	          </tr>
        </yfc:loopXML>
	</tbody>
</table>