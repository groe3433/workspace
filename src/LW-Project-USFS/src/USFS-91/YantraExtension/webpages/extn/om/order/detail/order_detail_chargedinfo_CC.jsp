<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ page import="java.math.BigDecimal" %>

<%

String sTotalShippingCost = resolveValue("xml:/ShipmentList/Shipment/@TotalActualCharge");
String sTotalIssueCost = resolveValue("xml:/Order/PriceInfo/@TotalAmount");

String sCurrencySymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol");

BigDecimal bdTotalShippingCost = new BigDecimal(sTotalShippingCost.replaceAll(",", ""));
BigDecimal bdTotalIssueCost = new BigDecimal(sTotalIssueCost.replaceAll(",", ""));

String sTotalAmountCharged = "";
ArrayList chargeTransactionDetailList = getLoopingElementList("xml:/Order/ChargeTransactionDetails/@ChargeTransactionDetail");
	
for (int ChargeTransCounter = 0; ChargeTransCounter < chargeTransactionDetailList.size(); ChargeTransCounter++) 
{
    YFCElement singleAdditionalAddress = (YFCElement) chargeTransactionDetailList.get(ChargeTransCounter);
    pageContext.setAttribute("ChargeTransactionDetail", singleAdditionalAddress); 
	request.setAttribute("ChargeTransactionDetail", pageContext.getAttribute("ChargeTransactionDetail"));
	sTotalAmountCharged = getValue("Order", "xml:/Order/ChargeTransactionDetails/ChargeTransactionDetail/@CreditAmount");
}

if(!sTotalAmountCharged.equals("") && sTotalAmountCharged != null)
{
	BigDecimal bdTotalAmountCharged = new BigDecimal(sTotalAmountCharged.replaceAll(",", ""));
	bdTotalShippingCost = bdTotalAmountCharged.subtract(bdTotalIssueCost);
}

BigDecimal amountCharged = bdTotalShippingCost.add(bdTotalIssueCost);

String sCustomerName = getValue("Customer", "xml:/Customer/Extn/@ExtnCustomerName");
String sRemitterFirstName = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@FirstName");
String sRemitterLastName = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@LastName");
String sState = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@State");
String sZipCode = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@ZipCode");
String sCountry = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@Country");
String sCity = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@City");
String sAddress1 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1");
String sAddress2 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2");
String sAddress3 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3");

	ArrayList additionalAddressList = getLoopingElementList("xml:/Order/AdditionalAddresses/@AdditionalAddress");
	
	for (int AdditionalAddressCounter = 0; AdditionalAddressCounter < additionalAddressList.size(); AdditionalAddressCounter++) 
	{
	    YFCElement singleAdditionalAddress = (YFCElement) additionalAddressList.get(AdditionalAddressCounter);
	    pageContext.setAttribute("AdditionalAddress", singleAdditionalAddress); 
			request.setAttribute("AdditionalAddress", pageContext.getAttribute("AdditionalAddress"));
			String sAddressType = getValue("AdditionalAddress", "xml:/AdditionalAddress/@AddressType");
			
			if(sAddressType.equals("CREDIT_CARD"))
			{
				sRemitterFirstName = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@FirstName");
				sRemitterLastName = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@LastName");
				sState = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@State");
				sZipCode = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@ZipCode");
				sCountry = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@Country");
				sCity = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@City");
				sAddress1 = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@AddressLine1");
				sAddress2 = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@AddressLine2");
				sAddress3 = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@AddressLine3");
				sCustomerName = getValue("AdditionalAddress", "xml:/AdditionalAddress/PersonInfo/@Company");
			}
	}

	String sFirstName = getValue("PaymentList", "xml:/PaymentList/Payment/@FirstName");
	String sLastName = getValue("PaymentList", "xml:/PaymentList/Payment/@LastName");
	
%>

<table class="table" editable="false" width="100%" cellspacing="0">
<tbody>
	<tr>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Order_#</yfc:i18n></td>
		<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
		</td>
		<td class="detaillabel"><yfc:i18n>Business Name</yfc:i18n></td>
		<td><input type="text" size="60" value="<%=sCustomerName%>"  class="protectedinput" ></td>
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Issue Cost</yfc:i18n></td>
		<td class="protectedtext">
		<%=sCurrencySymbol + sTotalIssueCost%>
		</td>
		
		<td class="detaillabel"><yfc:i18n>First Name</yfc:i18n></td>
		<td><input type="text" value="<%=sFirstName%>" class="protectedinput" ></td>	
		
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Shipping Cost</yfc:i18n></td>
		<td><%=sCurrencySymbol + bdTotalShippingCost%></td>
	
		<td class="detaillabel"><yfc:i18n>Last Name</yfc:i18n></td>
		<td><input type="text" value="<%=sLastName%>" class="protectedinput" ></td>	

	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Amount Charged</yfc:i18n></td>
		<td class="protectedtext"><%=sCurrencySymbol + amountCharged%></td>

		<td class="detaillabel"><yfc:i18n>Credit Address 1</yfc:i18n></td>
		<td><input type="text" size="40" value="<%=sAddress1%>" class="protectedinput"></td>
		
	</tr>
	
	<tr>
	<td class="detaillabel"><yfc:i18n>Remitter First Name</yfc:i18n></td>
	<td><input type="text" value="<%=sRemitterFirstName%>" class="protectedinput" ></td>
	
	<td class="detaillabel"><yfc:i18n>Credit Address 2</yfc:i18n></td>
	<td><input type="text" size="40" value="<%=sAddress2%>" class="protectedinput"></td>
	
	</tr>
	
	<tr>
	<td class="detaillabel"><yfc:i18n>Remitter Last Name</yfc:i18n></td>
	<td><input type="text" value="<%=sRemitterLastName%>"  class="protectedinput" ></td>
	
	<td class="detaillabel"><yfc:i18n>Credit Address 3</yfc:i18n></td>
	<td><input type="text" size="40" value="<%=sAddress3%>" class="protectedinput"></td>
	
	</tr>
	
	<tr>
	<td class="detaillabel"><yfc:i18n>Credit Card No</yfc:i18n></td>
	<td><input type="text" value="****************" class="protectedinput" size="30" maxLength='16'/></td>

	<td class="detaillabel"><yfc:i18n>City</yfc:i18n></td>
	<td><input type="text" value="<%=sCity%>" class="protectedinput"></td>

	</tr>
	
	<tr>
	<td class="detaillabel"><yfc:i18n>Credit Card Exp Date</yfc:i18n></td>
	<td><input type="text" value="**-****" class="protectedinput"/></td>

	<td class="detaillabel"><yfc:i18n>State</yfc:i18n></td>
	<td><input type="text" value="<%=sState%>" class="protectedinput" ></td>

	</tr>	
	
	<tr>
	<td class="detaillabel"><yfc:i18n>Country</yfc:i18n></td>
	<td><input type="text" value="<%=sCountry%>" class="protectedinput" ></td>
	<td class="detaillabel"><yfc:i18n>Zip Code</yfc:i18n></td>
	<td><input type="text" value="<%=sZipCode%>" class="protectedinput"></td>
	</tr>
	
</tbody>
</table>
