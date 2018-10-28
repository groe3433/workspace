<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript">

function validateFBMS()
{
	var valFunctionalArea = document.getElementById("xml:/NWCGBillingTransaction/@FunctionalArea").value;
	var valWBS = document.getElementById("xml:/NWCGBillingTransaction/@WBS").value;
	var valCostCenter = document.getElementById("xml:/NWCGBillingTransaction/@CostCenter").value;
	for (var i = 0; i < valCostCenter.length; i++)
	{
		if (specialChars.indexOf(valCostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center code.");
			return false;
		}
	}
	for (var i = 0; i < valWBS.length; i++)
	{
		if (specialChars.indexOf(valWBS.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for WBS code.");
			return false;
		}
	}
	for (var i = 0; i < valFunctionalArea.length; i++)
	{
		if (specialChars.indexOf(valFunctionalArea.charAt(i)) != -1) 
		{
			if(!(i==9 && valFunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area code.");
				return false;
			}
		}
	}
	var strLL = valCostCenter.substring(0,2);
	if((strLL != "LL") || (valCostCenter.length != 10))
	{
		alert("Enter a ten digit code for Cost Center that begins with LL");
		return false;
	}
	if(valWBS != "")
	{
		var strSubStringWBS = valWBS.substring(0,1);
		if((strSubStringWBS != "L") || (valWBS.length != 12))
		{
			alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
			return false;
		}
	}
	var strSubStringFA = valFunctionalArea.substring(0,1);
	var strDotFA =valFunctionalArea.substring(9,10);
	if((strSubStringFA != "L") || (strDotFA != ".") || (valFunctionalArea.length != 16))
	{
		alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
		return false;
	}

	var formattedBLMCode =  "";
	formattedBLMCode = valCostCenter+"."+valFunctionalArea+"."+valWBS;
	document.all("xml:/NWCGBillingTransaction/@IncidentBlmAcctCode").value = formattedBLMCode;
	
	return true;
}
</script>

<table class="view" width="100%" >
<yfc:makeXMLInput name="SeqKey">
<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey"/>
</yfc:makeXMLInput>
<% System.out.println("Anchor request DOM Header ==>> "+ getRequestDOM());%>
<% String TDate = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate"); 
		   String TDateStr1 = TDate.substring(0,4);
		   String TDateStr2 = TDate.substring(5,7);
           String TDateStr3 = TDate.substring(8,10);
           TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
		   String str = resolveValue("xml:/NWCGBillingTransaction/@SequenceKey"); 
		   String totalTransactionAmount = resolveValue("xml:/NWCGBillingTransaction/@TotalBillingTransAmount"); 
		   System.out.println("totalTransactionAmount= " + totalTransactionAmount); 
		   System.out.println("%%%%%%%%%%%%%%% str %%%%%%%%%%%%  " + str); 
%>
<input type="hidden" name="xml:/NWCGBillingTransaction/@IncidentKey" value='<%=resolveValue("xml:/NWCGBillingTransaction/@IncidentKey")%>' />
<tr>

<td class="detaillabel" ><yfc:i18n>Transaction_No</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransactionNo"/></td>
<td class="detaillabel" ><yfc:i18n>Trans_Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransType"/></td>
<td class="detaillabel" ><yfc:i18n>BLM_Acct_Code</yfc:i18n></td>
<td><input type="text" readonly="true" maxlength="50" size="50" class="protectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@IncidentBlmAcctCode")%>/></td>

</tr>
<tr>

<td class="detaillabel" ><yfc:i18n>Cost_Center</yfc:i18n></td>
<td><input type="text" maxlength="10" size="30" class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@CostCenter")%>/></td>
<td class="detaillabel" ><yfc:i18n>Functional_Area</yfc:i18n></td>
<td><input type="text" maxlength="16" size="30" class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@FunctionalArea")%>/></td>
<td class="detaillabel" ><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
<td><input type="text" maxlength="12" size="30" class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@WBS")%>/></td>

</tr>
<tr>
<%--
<td class="detaillabel" ><yfc:i18n>Trans_Date</yfc:i18n></td>
<td class="protectedtext"><%=TDate%></td>
--%>
<td class="detaillabel" ><yfc:i18n>Trans_Amount</yfc:i18n></td>
<td class="protectedtext">$<%=totalTransactionAmount%></td>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@CacheId"/></td>
</tr>
</table>