<%@taglib prefix="yfc" uri="/WEB-INF/yfc.tld"%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>
<%@ page import="java.math.BigDecimal" %>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>


<table class="table" editable="false" width="100%" cellspacing="0">
	<thead>
		<tr>
			<td sortable="no" class="checkboxheader" name="OrderChecked">
				<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>' /> 
				<input type="hidden" name="xml:/Order/@Override" value="N" /> 
				<input type="hidden" name="ResetDetailPageDocumentType" value="Y" /> 
			</td>
			<td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Cache_ID</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Other_Order_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Order_Date</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Order_Type</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Charged Date</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Total Cost</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:/OrderList/@Order" id="Order">
		
			<tr>
				<yfc:makeXMLInput name="orderKey">
					<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
					<yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/Order/@OrderNo" />
					<yfc:makeXMLKey binding="xml:/Order/Extn/@ExtnIncidentNo" value="xml:/Order/Extn/@ExtnIncidentNo" />
					<yfc:makeXMLKey binding="xml:/Order/@Status" value="xml:/Order/@Status" />
					<yfc:makeXMLKey binding="xml:/Order/@OrderType" value="xml:/Order/@OrderType" />
				</yfc:makeXMLInput>
				<td class="checkboxcolumn">
					<input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" 
					Status='<%=resolveValue("xml:/Order/@Status")%>'
					OrderNo='<%=resolveValue("xml:/Order/@OrderNo")%>'
					OrderHeaderKey='<%=resolveValue("xml:/Order/@OrderHeaderKey")%>'
					totalAmount='<%=resolveValue("xml:/Order/PriceInfo/@TotalAmount")%>'
					/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@OrderNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@ShipNode" />
				</td>
				<td class="tablecolumn">
				<% if ( isVoid(getValue("Order", "xml:/Order/@Status") ) ) 
				{ 
				%>
					<yfc:i18n>Draft</yfc:i18n>
				<% } else { %>
					<yfc:getXMLValue binding="xml:/Order/@Status" />
					<%} %>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@EnterpriseCode" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@BillToID" />
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:/Order/@OrderDate")%>">
					<yfc:getXMLValue binding="xml:/Order/@OrderDate" />
				</td>
				<td class="tablecolumn">
					  <%
						String sOrderType = resolveValue("xml:/Order/@OrderType");
					  
						if(sOrderType.equals("CREDIT_CARD"))
						{
							%>
							<yfc:i18n>Credit Card</yfc:i18n>
					  <%}
						else
						{
						%>
							<yfc:getXMLValue binding="xml:/Order/@OrderType" />
						<%
						} 
						%>
				</td>
				<!--  Jeri requested this be removed as part of defect 1582
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/Extn/@ExtnAuthorizationCode" />
				</td>
				-->
				<!--  defect 1582 - Add Transaction Date column. 
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/ChargeTransactionDetails/ChargeTransactionDetail/@TransactionDate" />
				</td>-->
				
				<!-- defect 1582 - Add Transaction Date column and Add StatusDate as ChargedDate for MarkAsCharged orders -->
				<td class="tablecolumn">
					  <%
					  String sTotalAmount = resolveValue("xml:/Order/PriceInfo/@TotalAmount");
					  String sStatus = resolveValue("xml:/Order/@Status");
					  String sStatusDate = resolveValue("xml:/Order/OrderStatuses/OrderStatus/@StatusDate");
					  String sTransactionDate = resolveValue("xml:/Order/ChargeTransactionDetails/ChargeTransactionDetail/@TransactionDate");
					  if(!sTransactionDate.equals(""))
					  {
					  %>
						<yfc:getXMLValue binding="xml:/Order/ChargeTransactionDetails/ChargeTransactionDetail/@TransactionDate" />
					  <%						  
					  }	
                      else if (sTotalAmount.equals("0.00") && (sStatus.equals("ShippedAndCharged")))
					  {
					  %>
							<yfc:getXMLValue binding="xml:/Order/OrderStatuses/OrderStatus/@StatusDate" />
					  <%
					  } 
					  %>
				</td>				
				
				<%
					String sShippingTotalAmount = "100";
					String sIssueTotalAmount = "100";
					
					String sOrderStatus = "";
					
					BigDecimal bdTotalAmountCharged = null;
					BigDecimal bdTotalIssueCost = null;
					BigDecimal bdOldShippingCost = null;
					BigDecimal bdNewShippingCost = null;
					BigDecimal bdTotalShipAndIssue = null;
										
					YFCElement shipmentListForOrderInput = YFCDocument.parse("<Order OrderHeaderKey=\"" + resolveValue("xml:/Order/@OrderHeaderKey") + "\" />").getDocumentElement();

					YFCElement shipmentListForOrderTemplate = YFCDocument.parse("<ShipmentList><Shipment TotalActualCharge=\"\"><ShipmentLines TotalNumberOfRecords=\"\"><ShipmentLine OrderNo=\"\"></ShipmentLine> </ShipmentLines></Shipment></ShipmentList>").getDocumentElement();
					%>
					
					<yfc:callAPI apiName="getShipmentListForOrder" inputElement="<%=shipmentListForOrderInput%>" templateElement="<%=shipmentListForOrderTemplate%>" outputNamespace="ShipmentListForOrder"/>
					
					<%
					
					sShippingTotalAmount = resolveValue("xml:ShipmentListForOrder:/ShipmentList/Shipment/@TotalActualCharge");
				
					sIssueTotalAmount = resolveValue("xml:/Order/PriceInfo/@TotalAmount");
				
					if(sShippingTotalAmount.equals("") || sShippingTotalAmount==null)
					{
						sShippingTotalAmount="0.00";
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
					
					bdTotalShipAndIssue = bdOldShippingCost.add(bdTotalIssueCost);
					
					
				%>
				
				
				<!--  <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/Order/PriceInfo/@TotalAmount")%>"> -->
				<td class="numerictablecolumn" sortValue="<%= bdTotalShipAndIssue %>">
				<%=displayAmount( bdTotalShipAndIssue.toString(), (YFCElement) request.getAttribute("CurrencyList"), getValue("Order", "xml:/Order/PriceInfo/@Currency"))%>
				</td>
			</tr>
		</yfc:loopXML>
	</tbody>
</table>