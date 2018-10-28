<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="org.w3c.dom.*" %>


<input type="hidden" <%=getTextOptions("xml:/NWCGBillingTransExtract/@PostExtractSequenceKey")%> />

<input type="hidden" <%=getTextOptions("xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@TransactionNo")%> />

<input type="hidden" name="xml:/NWCGBillingTransExtract/@ExtractFileName" value='<%=resolveValue("xml:/NWCGBillingTransExtract/@ExtractFileName")%>'/>
<% 
String ExtractFileName = resolveValue("xml:/NWCGBillingTransExtract/@ExtractFileName");
//xml:/NWCGBillingTransExtract/@AmtInDocCurrency
String AmountInDocCurrency = resolveValue("xml:/NWCGBillingTransExtract/@AmtInDocCurrency1");
YFCElement getExtractFileListInput = YFCDocument.parse("<NWCGBillingTransExtract ExtractFileName=\"" + ExtractFileName + "\" />").getDocumentElement();
YFCElement extractFileListTemplate = YFCDocument.parse("<NWCGBillingTransExtractList> <NWCGBillingTransExtract /> </NWCGBillingTransExtractList>").getDocumentElement();
%>


<yfc:callAPI serviceName="NWCGGetBillingTransExtractListService" inputElement="<%=getExtractFileListInput %>" templateElement="<%=extractFileListTemplate %>" outputNamespace="NWCGBillingTransExtractList"/>




<table class="view" width="100%">

<td class="detaillabel"><yfc:i18n>Business_Area</yfc:i18n></td>
<td>
	<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=4  <%=getTextOptions("xml:/NWCGBillingTransExtract/@BusinessArea")%>/>
</td> 

<td class="detaillabel" ><yfc:i18n>Fiscal_Year</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=4 <%=getTextOptions("xml:/NWCGBillingTransExtract/@FiscalYear")%>/>
</td>

<td class="detaillabel" ><yfc:i18n>Interface_Type</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=2 <%=getTextOptions("xml:/NWCGBillingTransExtract/@InterfaceType")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Document_Date</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=10 <%=getTextOptions("xml:/NWCGBillingTransExtract/@DocumentDate")%>/>
</td>
</tr>

<tr>
<!--<td class="detaillabel" ><yfc:i18n>Posting_Key</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=2 <%=getTextOptions("xml:/NWCGBillingTransExtract/@PostingKey")%>/>
</td>-->
<td class="detaillabel" ><yfc:i18n>GL_Account_Code</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=10 <%=getTextOptions("xml:/NWCGBillingTransExtract/@GLAccountCode")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Earmarked_Funds</yfc:i18n></td>
<td class="protectedtext">
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=6 <%=getTextOptions("xml:/NWCGBillingTransExtract/@EarmarkedFundsDocItem")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Item_Text</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=15 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ItemText")%>/>
</td>

</tr>

<tr>
<!--
<td class="detaillabel" ><yfc:i18n>Amount</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxlength=6 <%=getTextOptions("xml:/NWCGBillingTransExtract/@AmtInDocCurrency")%>/>
</td>-->
<td class="detaillabel" ><yfc:i18n>Company_ID</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=4 <%=getTextOptions("xml:/NWCGBillingTransExtract/@TradingPartnerCompanyId")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Funds_Center</yfc:i18n></td>
<td class="protectedtext">
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=5 <%=getTextOptions("xml:/NWCGBillingTransExtract/@Funds_Center")%>/>
</td> 
<td class="detaillabel" ><yfc:i18n>Fund</yfc:i18n></td>
<td class="protectedtext">
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=4 <%=getTextOptions("xml:/NWCGBillingTransExtract/@Fund")%>/>
</td> 

</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>

<td class="protectedtext"><input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=4 <%=getTextOptions("xml:/NWCGBillingTransExtract/@OrderNum")%>/></td> 
<td class="detaillabel" ><yfc:i18n>Commitment_Item</yfc:i18n></td>
<td class="protectedtext">
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=4 <%=getTextOptions("xml:/NWCGBillingTransExtract/@CommitmentItem")%>/>
</td> 
<td class="detaillabel" ><yfc:i18n>Document_Number</yfc:i18n></td>
<td class="protectedtext">
<input type="text" class=protectedinput readonly="TRUE" size=15 maxLength=6 <%=getTextOptions("xml:/NWCGBillingTransExtract/@DocNumForEarmarkedFunds")%>/>
</td>

<td class="detaillabel" ><yfc:i18n>TransactionNo</yfc:i18n></td>
<td class="protectedtext">
<yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@TransactionNo"/>

</td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Cost_Center</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size=15 maxLength=10 onBlur="checkExtractCostCenter(this)" <%=getTextOptions("xml:/NWCGBillingTransExtract/@CostCenter")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Functional_Area</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size=20 maxLength=16 onBlur="checkExtractFunctionalArea(this)" <%=getTextOptions("xml:/NWCGBillingTransExtract/@FunctionalArea")%>/>
</td> 
<td class="detaillabel" ><yfc:i18n>WBS</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size=15 maxLength=12 onBlur="checkExtractWBS(this)" <%=getTextOptions("xml:/NWCGBillingTransExtract/@WBS")%>/>
</td> 

<tr>
<td class="detaillabel" ><yfc:i18n>Reference_Doc_Number</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=40 maxlength=35 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ReferenceDocNumber")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Doc_Header</yfc:i18n></td>
<td>
<input type="text" class=protectedinput readonly="TRUE" size=30 maxlength=25  <%=getTextOptions("xml:/NWCGBillingTransExtract/@DocHeaderText")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Extract_File_Name</yfc:i18n></td>
<td>
	<input type="text" class=protectedinput  readonly=true size=50 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ExtractFileName")%>/>
</td>
</tr>
<hr>
 
 </table>


