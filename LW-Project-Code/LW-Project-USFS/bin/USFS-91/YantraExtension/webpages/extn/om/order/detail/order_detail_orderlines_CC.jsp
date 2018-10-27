<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ page import="java.math.BigDecimal" %>

<%
	String sShippingTotalAmount = "";
	String sShippingTotalRecords = "";
	String sIssueTotalAmount = "";
	String sIssueTotalRecords = "";
	String sOrderStatus = "";
	
	BigDecimal bdTotalAmountCharged = null;
	BigDecimal bdTotalIssueCost = null;
	BigDecimal bdOldShippingCost = null;
	BigDecimal bdNewShippingCost = null;
	
	sShippingTotalAmount = resolveValue("xml:/ShipmentList/Shipment/@TotalActualCharge");
	sShippingTotalRecords = resolveValue("xml:/ShipmentList/Shipment/ShipmentLines/@TotalNumberOfRecords");
	
	sIssueTotalAmount = resolveValue("xml:/Order/PriceInfo/@TotalAmount");
	sIssueTotalRecords = resolveValue("xml:/Order/OrderLines/@TotalNumberOfRecords");
	
	if(sShippingTotalAmount.equals("") || sShippingTotalAmount==null)
	{
		sShippingTotalAmount="0.00";
	}
	
	if(sShippingTotalRecords.equals("") || sShippingTotalRecords==null)
	{
		sShippingTotalRecords="0";
	}

	bdOldShippingCost = new BigDecimal(sShippingTotalAmount.replaceAll(",", ""));
	bdTotalIssueCost = new BigDecimal(sIssueTotalAmount.replaceAll(",", ""));
	sOrderStatus = resolveValue("xml:/Order/@Status");
	if(sOrderStatus != null && !sOrderStatus.equals(""))
	{
		if( sOrderStatus.equals("ShippedAndCharged") )
		{
			String sTotalAmountCharged = getValue("Order", "xml:/Order/ChargeTransactionDetails/ChargeTransactionDetail/@CreditAmount");
			if(!sTotalAmountCharged.equals("") && sTotalAmountCharged != null)
			{
				bdTotalAmountCharged = new BigDecimal(sTotalAmountCharged.replaceAll(",", ""));
				bdNewShippingCost = bdTotalAmountCharged.subtract(bdTotalIssueCost);
				if( !bdNewShippingCost.equals(BigDecimal.ZERO) && bdNewShippingCost != null)
				{
					if(bdNewShippingCost != bdOldShippingCost)
					{
						bdOldShippingCost = bdNewShippingCost;
					}	
				}
			}

		}

	}
%>

<input type="hidden" name="xml:PCSale/@IssueQuantity" value="<%=sIssueTotalRecords%>"/>
<input type="hidden" name="xml:PCSale/@IssueTotalCost" value="<%=sIssueTotalAmount%>"/>
<input type="hidden" name="xml:PCSale/@ShippingQuantity" value="<%=sShippingTotalRecords%>"/>

<table class="sortableTable" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000">
	<thead>
		<tr>
			<td class="numerictablecolumnheader" nowrap="true" style="width:5px"><yfc:i18n>Line</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="false" style="width:10px"><yfc:i18n>Description</yfc:i18n></td>
			<td class="numerictablecolumnheader" nowrap="false" style="width:10px"><yfc:i18n>Quantity</yfc:i18n></td>
			<td class="numerictablecolumnheader" nowrap="false" style="width:10px"><yfc:i18n>Subtotal</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<tr>
		<td class="protectedtext">1</td>
		<td class="protectedtext">NIFC CREDIT CARD ORDERS - ISSUES</td>
		<td class="protectedtext">1</td>
		<td class="protectedtext"><%=sIssueTotalAmount%></td>
		</tr>
		<tr>
		<td class="protectedtext">2</td>
		<td class="protectedtext">NIFC CREDIT CARD ORDERS - SHIPPING</td>
		<td class="protectedtext">1</td>
		<td class="protectedtext"><%=bdOldShippingCost%></td>
		</tr>
	</tbody>
	<tfoot>

	</tfoot>
</table>