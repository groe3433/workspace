<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ page import="java.math.BigDecimal" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<%
String sPath = request.getParameter("Path");
if (isVoid(sPath)) {
sPath="xml:PCSale";
}

String strTotalShippingCost = getValue("ShipmentList", "xml:/ShipmentList/Shipment/@TotalActualCharge");
String strTotalIssueCost = resolveValue("xml:/Order/PriceInfo/@TotalAmount");
String sCurrencySymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol");

String sCustomerName = getValue("Customer", "xml:/Customer/Extn/@ExtnCustomerName");
String sCustomerID = getValue("Customer", "xml:/Customer/@CustomerID");
String sFirstName = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@FirstName");
String sLastName = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@LastName");
String sState = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@State");
String sZipCode = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@ZipCode");
String sCountry = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@Country");
String sCity = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@City");

String sAddress1 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1");
String sAddress2 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2");
String sAddress3 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3");
String sAddress4 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine4");
String sAddress5 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine5");
String sAddress6 = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@AddressLine6");


String sCompany = getValue("Customer", "xml:/Customer/Consumer/BillingPersonInfo/@Company");
String sTransTypeResult = "Organization";

if(sCompany == null || sCompany.equals(""))
{
	sTransTypeResult = "Individual";
}


if(strTotalShippingCost.equals("") || strTotalShippingCost==null)
{
	strTotalShippingCost="0.00";
}

if(strTotalIssueCost.equals("") || strTotalIssueCost==null)
{
	strTotalIssueCost="0.00";
}

BigDecimal bdTotalShippingCost = new BigDecimal(strTotalShippingCost.replaceAll(",", ""));

BigDecimal bdTotalIssueCost = new BigDecimal(strTotalIssueCost.replaceAll(",", ""));


BigDecimal total = bdTotalShippingCost.add(bdTotalIssueCost);

%>


<script>
            
function twoDecimalPlaces (e) {
    var regex = /^\d+(\.\d{0,2})?$/g;
    if (!regex.test(e.value)) {
        e.value = e.value.substring(0, e.value.length - 1);
    }
}

function updateAmountToBeCharged(value){
	
	var rx = /^\d+(?:\.\d{1,2})?$/ 
	
	if(value == null || value.match(/^\s*$/) )
	{
		alert('Shipping cost must be greater than or equal to 0');
		document.getElementById("totalShippingCost").focus();
		document.getElementById("totalShippingCost").value='0';
		value = 0;
	}
	
	if(value < 0)
	{
		alert('No negative values allowed');
		document.getElementById("totalShippingCost").focus();
		document.getElementById("totalShippingCost").value='';
		value = 0;
	}
	
	if( isNaN(value) == true)
	{
		alert('Shipping Cost must contain only digits, e.g. 99.99');
		document.getElementById("totalShippingCost").focus();
		document.getElementById("totalShippingCost").value='';
		value = 0;
	}
	
	var rx = /^\d+(?:\.\d{1,2})?$/ 
	
	if( !rx.test(value) )
	{
		alert('Shipping Cost cannot have more than 2 decimal places, e.g. 500.12');
		document.getElementById("totalShippingCost").focus();
		document.getElementById("totalShippingCost").value = '';
		value = 0;
	}
	
	var shipping = parseFloat(value);

	var sTotalIssueCost = "<%=resolveValue("xml:/Order/PriceInfo/@TotalAmount")%>";
	
	var strTotalIssueCost = parseFloat( sTotalIssueCost.replace(/,/g , "") );
	var z=strTotalIssueCost+shipping
	var sCurrency = "<%=getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol")%>";
	
	document.getElementById('amount1').innerHTML=sCurrency+z.toFixed(2);
	document.getElementById("transactionAmount").value=z.toFixed(2);
	
	return true;
}

function checkCreditCardNumber(value){
	
	if( isNaN(value) == true)
	{
		alert('Illegal number');
		document.getElementById("creditCardNo").focus();
		document.getElementById("creditCardNo").value='';
		value = 0;
	}
}

function checkZipCode(value){
	
	var sCountry = document.getElementById("billCountry").value;

	if(sCountry == "840")//USA
	{
		var objRegex = /^\d{5}([\-]\d{4})?$/;
		
		if( objRegex.test(value) != true)
		{
			alert('Please Enter Only Digits & Hyphen, e.g. 28445-3653 or 28445');
			document.getElementById("billZipCode").focus();
			document.getElementById("billZipCode").value='';
			value = 0;
		}
		
		if(value.length > 10)
		{
			alert('US ZipCode entered exceeds allowed length, e.g. 28445 or 28445-3653');
			document.getElementById("billZipCode").focus();
			document.getElementById("billZipCode").value='';
			value = 0;
		}
	}
}

function compareCountryAndZipCode(value){

	var sZipCode = document.getElementById("billZipCode").value;
	
	if(value == "840")//USA
	{
		var objRegex = /^\d{5}([\-]\d{4})?$/;
		
		if( objRegex.test(sZipCode) != true)
		{
			alert('Please Enter Only Digits or Digits and Hyphen, e.g. 28445-3653 or 28445');
			document.getElementById("billZipCode").focus();
			document.getElementById("billZipCode").value='';
			value = 0;
		}
		
		if(sZipCode.length > 10)
		{
			alert('US ZipCode entered exceeds allowed length, e.g. 28445 or 28445-3653');
			document.getElementById("billZipCode").focus();
			document.getElementById("billZipCode").value='';
			value = 0;
		}
	
	}
}

function clearAddressFields(){
	
	if (document.getElementById("cAddress").checked){
	
		document.getElementById("billCustomerName").value="<%=resolveValue("xml:/Customer/Extn/@ExtnCustomerName")%>";
		document.getElementsByName("xml:/Order/@BillToID")[0].setAttribute("value","<%=resolveValue("xml:/Customer/@CustomerID")%>");
		document.getElementById("billAddress1").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1")%>";
		document.getElementById("billAddress2").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2")%>";
		document.getElementById("billAddress3").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3")%>";
		document.getElementById("billFirstName").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@FirstName")%>";
		document.getElementById("billLastName").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@LastName")%>";	
		document.getElementById("billCity").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@City")%>";
		document.getElementById("billState").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@State")%>";
		document.getElementById("billCountry").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@Country")%>";
		
		var textToFind = '<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@Country")%>';

		var dd = document.getElementById('billCountry');
		for (var i = 0; i < dd.options.length; i++) {
		    if (dd.options[i].text === textToFind) {
		        dd.selectedIndex = i;
		        break;
		    }
		}
		
		document.getElementById("billZipCode").value="<%=resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode")%>";
	}
	else{
		document.getElementById("billCustomerName").value="";
		document.getElementsByName("xml:/Order/@BillToID")[0].setAttribute("value","");
		document.getElementById("billAddress1").value="";
		document.getElementById("billAddress2").value="";
		document.getElementById("billAddress3").value="";
		document.getElementById("billFirstName").value="";
		document.getElementById("billLastName").value="";	
		document.getElementById("billCity").value="";
		document.getElementById("billState").value="";
		document.getElementById("billCountry").value="";
		document.getElementById("billZipCode").value="";
	}

}

function dtval(d,e) 
{
	var pK = e ? e.which : window.event.keyCode;
	
	var dt = d.value;
	
	if (dt.length == 2) 
	{
		dt += '-';
	}
	
	d.value = dt;
}

function handleRadioClick(myRadio) {
	
    var currentValue = myRadio.value;
    
    var customerRecordTransType = document.getElementById("transactionType").value;
    
    if(customerRecordTransType != currentValue)
    {
    	alert('Warning: Customer Record is different from Transaction you selected, check you selected the correct Transaction Type!');
    }

    if(currentValue == "Individual")
    {
    	document.getElementById("hiddenFields").style.display='block';
    	document.getElementById("hiddenFields2").style.display='block';
    	document.getElementById("billCustomerName").disabled = true;
    	
		var sBusinessName = document.getElementById("billCustomerName").value;
    	
        var splitResult = sBusinessName.split(" ");
        
    	document.getElementById("iFirstName").value = splitResult[0];
    	document.getElementById("iLastName").value = splitResult[1];
    }
    else
    {
    	document.getElementById("hiddenFields").style.display='none';
    	document.getElementById("hiddenFields2").style.display='none';
    	document.getElementById("billCustomerName").disabled = false;
    	
    	document.getElementById("iFirstName").value = "";
    	document.getElementById("iLastName").value = "";
    }

	//document.getElementById("transactionType").value = currentValue;
    
}

window.onload = function() {

	//Populate Country Dropdown box
	var textToFind = '<%= sCountry %>';

	var dd = document.getElementById('billCountry');
	for (var i = 0; i < dd.options.length; i++) {
	    if (dd.options[i].text === textToFind) {
	        dd.selectedIndex = i;
	        break;
	    }
	}
}

</script>

<input type="hidden" name="xml:PCSale/@AgencyTrackingId" value="<%=resolveValue("xml:/Order/@OrderNo")%>"/>
<input type="hidden" id="transactionAmount" name="xml:PCSale/@TransactionAmount" value="<%=total%>"/>
<input type="hidden" id="transactionType" name="xml:PCSale/@TransactionType" value="<%=sTransTypeResult%>"/>

<table class="table" editable="false" width="100%" cellspacing="0">
<tbody>
	<tr>
		<td colspan="4" align="center" size="2"><font color="red" size="2"><b>NOTICE:</b> The collection and disposition of payment card information outside of this system by whatever means (e.g. hard copy form, fax, etc.) is subject to the policies and procedures prescribed by the agency/bureau responsible for its collection and entry into ICBS.</font></td>
		<!-- <td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		-->
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Order_#</yfc:i18n></td>
		<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
		</td>
		
		<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
		<td>
		<input id="billCustomerName" type="text" onblur="makeUppercase(this)" size="60" maxlength="80" value="<%=sCustomerName%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@CustomerName")%> tabindex=7/>
		</td>
		
	</tr>
	
	<tr id="hiddenFields" style="display:none">
	
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td></td>
		
		<td class="detaillabel"><yfc:i18n>First Name</yfc:i18n></td>
		<td><input id="iFirstName" type="text" onblur="makeUppercase(this)"  size="40" maxlength="40" value="" class="unprotectedinput" <%=getTextOptions(sPath+"/@iFirstName")%> tabindex=8 ></td>
		
	</tr>
	
	<tr id="hiddenFields2" style="display:none">
		
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td></td>
		
		<td class="detaillabel"><yfc:i18n>Last Name</yfc:i18n></td>
		<td><input id="iLastName" type="text" onblur="makeUppercase(this)"  size="40" maxlength="40" value="" class="unprotectedinput" <%=getTextOptions(sPath+"/@iLastName")%> tabindex=9 ></td>
	
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Issue Cost</yfc:i18n></td>
		<td class="protectedtext"><%=sCurrencySymbol + bdTotalIssueCost%></td>
		
		<td class="detaillabel"><yfc:i18n>Address 1</yfc:i18n></td>
		<td>
		<input id="billAddress1" type="text" onblur="makeUppercase(this)" size="40" maxlength="80" value="<%=sAddress1%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@AddressLine1")%> tabindex=10>
		</td>
	
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Shipping Cost</yfc:i18n></td>
		<td><%=sCurrencySymbol%><input id="totalShippingCost" type="text" onblur="updateAmountToBeCharged(this.value)" class="unprotectedinput" value="<%=bdTotalShippingCost%>" <%=getTextOptions(sPath+"/@ShippingTotalCost")%> onkeyup="twoDecimalPlaces(this)" tabindex=1></input></td>
		
		<td class="detaillabel"><yfc:i18n>Address 2</yfc:i18n></td>
		<td><input id="billAddress2" type="text" onblur="makeUppercase(this)" size="40" maxlength="80" value="<%=sAddress2%>" class="unprotectedinput"  <%=getTextOptions(sPath+"/@AddressLine2")%> tabindex=11></td>
	
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Total Amount To Be Charged</yfc:i18n></td>
		<td id="amount1" class="protectedtext"><%=sCurrencySymbol + total%></td>
		
		<td class="detaillabel"><yfc:i18n>Address 3</yfc:i18n></td>
		<td><input id="billAddress3" type="text" onblur="makeUppercase(this)" size="40" maxlength="80" value="<%=sAddress3%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@AddressLine3")%> tabindex=12></td>
	
	</tr>
	
	<tr>
		<td class="detaillabel" ><yfc:i18n>Customer_Id</yfc:i18n></td>
		<td>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID", sCustomerID)%> name="xml:/Order/@BillToID"
		onblur="makeUppercase(this);fetchDataFromServer(this,'getCustomerDetails',updateCustDetailsForCreditPayment);" tabindex=2/>


		<img class="lookupicon" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%>/>
		</td>
		
		<td class="detaillabel"><yfc:i18n>City</yfc:i18n></td>
		<td><input id="billCity" type="text" onblur="makeUppercase(this)" size="40" maxlength="40" value="<%=sCity%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@City")%> tabindex=13></td>
		
	</tr>
	
	<tr>
	
		<td class="detaillabel"><yfc:i18n>Remitter First Name</yfc:i18n></td>
		<td><input id="billFirstName" type="text" onblur="makeUppercase(this)" maxlength="40" value="<%=sFirstName%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@FirstName")%> tabindex=3></td>
		
		<td class="detaillabel"><yfc:i18n>State</yfc:i18n></td>
		<td><input id="billState" type="text" onblur="makeUppercase(this)" size="7" maxlength="2" value="<%=sState%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@State")%> tabindex=14 ></td>
	
	</tr>
	
	<tr>
	
		<td class="detaillabel"><yfc:i18n>Remitter Last Name</yfc:i18n></td>
		<td><input id="billLastName" type="text" onblur="makeUppercase(this)" maxlength="50" value="<%=sLastName%>"  class="unprotectedinput" <%=getTextOptions(sPath+"/@LastName")%> tabindex=4></td>
		
		<td class="detaillabel"><yfc:i18n>Zip Code</yfc:i18n></td>
		<td><input id="billZipCode" type="text" onblur="checkZipCode(this.value);makeUppercase(this);" size="15" maxLength='10' value="<%=sZipCode%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@ZipCode")%> tabindex=15 ></td>
	
	</tr>
	
	<tr>
		<td class="detaillabel"><yfc:i18n>Credit Card No</yfc:i18n></td>
		<td><input id="creditCardNo" type="text"  onblur="checkCreditCardNumber(this.value)" class="unprotectedinput" size="20" maxLength='16'  <%=getTextOptions(sPath+"/@account_number")%> tabindex=5 /></td>
	
		<td class="detaillabel"><yfc:i18n>Country</yfc:i18n></td>
		
		<%
			String countryTypeStr = "<CommonCode CodeType=\"COUNTRY_CODE\"/>";
			YFCElement countryTypeInput = YFCDocument.parse(countryTypeStr).getDocumentElement();
			YFCElement countryTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
		%>
		<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=countryTypeInput%>" templateElement="<%=countryTypeTemplate%>" outputNamespace="CountryList"/>


		<td>
			<select class="combobox" name="xml:/PCSale/@Country" id="billCountry" onblur="compareCountryAndZipCode(this.value);makeUppercase(this);" tabindex=16 >
			<yfc:loopOptions binding="xml:CountryList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
			value="CodeValue" isLocalized="Y" selected="xml:/PCSale/@Country" />
			</select>
		</td>
		
		<!--
		<td><input id="billCountry" type="text" onblur="compareCountryAndZipCode(this.value);makeUppercase(this);" size="7" maxlength="2" value="<%=sCountry%>" class="unprotectedinput" <%=getTextOptions(sPath+"/@Country")%> /></td>
		-->
	
	</tr>
	
	<tr>
	
		<td class="detaillabel"><yfc:i18n>Credit Card Exp Date</yfc:i18n></td>
		<td><input type="text" class="unprotectedinput" size="7" maxlength="7" <%=getTextOptions(sPath+"/@credit_card_expiration_date")%> onkeyup="dtval(this,event)" tabindex=6/></td>
		
		<td class="detaillabel">
			<yfc:i18n>Credit Card Address Is The Same As Billing Address</yfc:i18n>
			
		</td>
		<td>
		<input type="checkbox" id="cAddress" checked="checked" name="cAddress" onClick="clearAddressFields();" tabindex=17 >
		</td>
	
	</tr>
	<tr>
	
		<td class="detaillabel"><yfc:i18n></yfc:i18n></td>
		<td></td>
		
		<td class="detaillabel">
			<yfc:i18n>Transaction Type:</yfc:i18n>
			
		</td>
		<td>
		
		<input id="radioBIndividual" type="radio" name="transType" onclick="handleRadioClick(this);" value="Individual" tabindex=18 >Individual<br>
		<input id="radioBOrganization" type="radio" name="TransType" onclick="handleRadioClick(this);" value="Organization" tabindex=19 >Organization

		</td>
	
	</tr>
	
</tbody>
</table>
