<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@page import="com.yantra.yfc.ui.backend.util.*"%>
<%@ page import="com.yantra.yfc.log.*" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<%
	String sOrderStatus = "";
	String sTransactionStatus = "";
	boolean bOrderCharged = false;
	
	sOrderStatus = resolveValue("xml:/Order/@Status");
	
	if(sOrderStatus != null && !sOrderStatus.equals(""))
	{
		
		if( sOrderStatus.equals("ShippedAndCharged") )
		{
			bOrderCharged = true;
			
			sTransactionStatus = resolveValue("xml:/PCSale/@TransactionStatus");
			
			if(sTransactionStatus != null && !sTransactionStatus.equals(""))
			{
				if(sTransactionStatus.equals("Success"))
				{
					%>
					<script type="text/javascript"> alert('Transaction was successful!'); </script>
					<% 
				}
			}
		}
	}
	
	String sCustomerName = getValue("Customer", "xml:/Customer/Extn/@ExtnCustomerName");
	String sCustomerID = getValue("Order", "xml:/Order/@BillToID");
		
%>


<%if(bOrderCharged == false){ %>
<input type="hidden" name="xml:PCSale/@OrderHeaderKey" value="<%=resolveValue("xml:/Order/@OrderHeaderKey")%>"/>
<input type="hidden" name="xml:PCSale/@SCAC" value="<%=resolveValue("xml:/Order/@SCAC")%>"/>
<input type="hidden" name="xml:PCSale/@ScacAndService" value="<%=resolveValue("xml:/Order/@ScacAndService")%>"/>
<input type="hidden" name="xml:PCSale/@BillToID" value="<%=resolveValue("xml:/Order/@BillToID")%>"/>

<%} %>

<table width="100%" cellspacing="0">
<tbody>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Other_Order_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentNo"/></td>
		
		<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></td>
	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Status</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@Status"/></td>
		
		<td class="detaillabel"><yfc:i18n>Cache</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ShipNode"/></td>
	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Bill Customer ID/Name</yfc:i18n></td>
		<td class="protectedtext"><%= sCustomerID  + " / " +  sCustomerName%></td>
		
		<td class="detaillabel"><yfc:i18n>BLM_Account_Code</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnBlmAcctCode"/></td>
	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Commodity</yfc:i18n></td>
		<%
			String sCommodity = "";
			sCommodity = YFCConfigurator.getInstance().getProperty("nwcg.paygov.commodity");
			
			if(isVoid(sCommodity))
			{
				sCommodity = "STATE ASSIST";
			}

		%>
		<td class="protectedtext"><%=sCommodity%></td>
		
		<td class="detaillabel"><yfc:i18n>Subject</yfc:i18n></td>
		<%
			String sSubject = "";
			sSubject = YFCConfigurator.getInstance().getProperty("nwcg.paygov.subject");
			
			if(isVoid(sSubject))
			{
				sSubject = "TRAINING/PUBLICATIONS";
			}

		%>
		<td class="protectedtext"><%=sSubject%></td>
		
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Action</yfc:i18n></td>
		<% 
		if(bOrderCharged == false)
		{
			String actionTypeStr = "<CommonCode CodeType=\"ACTION_CODES\"/>";
			YFCElement actionTypeInput = YFCDocument.parse(actionTypeStr).getDocumentElement();
			YFCElement actionTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
		%>
		<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=actionTypeInput%>" templateElement="<%=actionTypeTemplate%>" outputNamespace="ActionList"/>


		<td>
			<select class="combobox" name="xml:/PCSale/@Action" id="cbAction">
			<yfc:loopOptions binding="xml:ActionList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
			value="CodeValue" isLocalized="Y" selected="xml:/PCSale/@Action" />
			</select>
		</td>
		<%} 
		else
		{%>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/PaymentMethods/PaymentMethod/@PaymentReference3"/>
		</td>
		<%} %>
		
		<%if(bOrderCharged == true){ %>
		<td class="detaillabel"><yfc:i18n>Authorization Code</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnAuthorizationCode"/></td>
		<%} %>
		
	</tr>
</tbody>
</table>
