<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>

<script language="javascript">

function SaveReturnAcct() 
{
	var totalReturnPercentage = parseFloat("0");
	for (var i=1; i <= 5; i++) 
	{ 
		var idReturnPercentage = "xml:/Receipt/SplitAccountCodeData/@ReturnPercentage" + i;
		totalReturnPercentage += parseFloat(document.getElementById(idReturnPercentage).value);
	}
	
	if (totalReturnPercentage.toFixed(2) != 100.00)
	{ 
		alert('Total of Return Percentages must be 100%. Current total is ' + totalReturnPercentage + '%'); 
		return false;
	}
	else
	{
		var myDialogArgs = window.dialogArguments;
		var idSave = "xml:/Receipt/SplitAccountCodeData/@SaveAccounts";
		
		for (var i=1; i <= 5; i++) 
		{ 
			var idAccountCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + i;
			var idAmountCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
			var idReturnPercentage = "xml:/Receipt/SplitAccountCodeData/@ReturnPercentage" + i;

			myDialogArgs.parentWindow.document.getElementById(idAccountCode).value = document.getElementById(idAccountCode).value;
			myDialogArgs.parentWindow.document.getElementById(idAmountCharged).value = document.getElementById(idAmountCharged).value;
			myDialogArgs.parentWindow.document.getElementById(idReturnPercentage).value = document.getElementById(idReturnPercentage).value;
		}

		myDialogArgs.parentWindow.document.getElementById(idSave).value="Y";

		var r = confirm("Values have been temporarily saved. They will become permanent when you Process Return. Do you want to close this window?");
		if (r == true){ window.close();}
		else{ return false;}
	}
}

function setTotalReturnPercentage(n) 
{
	var totalReturnPercentage = 0;
	for (var i=1; i <= n; i++) 
	{ 
		var idReturnPercentage = "xml:/Receipt/SplitAccountCodeData/@ReturnPercentage" + i;
		var returnPercentage = parseFloat(document.getElementById(idReturnPercentage).value);
		totalReturnPercentage += returnPercentage;
	}
	document.getElementById("TotalReturnPercentage").value = totalReturnPercentage.toFixed(2);
}
</script>
<script language="Javascript" >
	yfcDoNotPromptForChanges(true);
</script>
<%	
YFCElement currencyListInput = YFCDocument.parse("<Currency Currency=\"USD\" />").getDocumentElement();
%>
<yfc:callAPI apiName="getCurrencyList" inputElement="<%=currencyListInput%>" />
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<% 
	String strNumAccounts = request.getParameter("numAccounts");
	int numAccounts = (strNumAccounts != null)? Integer.parseInt(strNumAccounts) : 0;
	
	Map map = new HashMap();
	Double totalAmountCharged = new Double(0.0);
	
	for (int i=1; i <= 5 ; i++) 
	{
		String idAccountCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + i;
		String idAmountCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
		String idReturnPercentage = "xml:/Receipt/SplitAccountCodeData/@ReturnPercentage" + i;
		String isReadOnlyStr = "isReadOnly" + i;
		String isReadOnly = new String("readonly");
		
		
		String accountCode = "";
		Double amountCharged = new Double(0.0);
		
		if(i <= numAccounts)
		{
			accountCode = request.getParameter("accountCode" + i);			
			amountCharged = new Double((-1.0) * (Double.valueOf(request.getParameter("amountCharged"+i))).doubleValue());
			isReadOnly = "";
		}

		totalAmountCharged = new Double(totalAmountCharged.doubleValue() + amountCharged.doubleValue());

		map.put(idAccountCode, accountCode);
		map.put(idAmountCharged, amountCharged);
		map.put(isReadOnlyStr, isReadOnly);
		
		Double returnPercentage = (i==1)? new Double(100.0): new Double(0.0);
		map.put(idReturnPercentage, returnPercentage);
	}

	map.put("TotalAmountCharged", totalAmountCharged);
	map.put("TotalReturnPercentage", new Double(100.0));
	
	DecimalFormat df = new DecimalFormat("#.##");
	for (int i=1; i <=5 ; i++) 
	{
		String idIssuePercentage = "issuePercentage" + i;
		String idAmountCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
		Double amtCharged = (Double) map.get(idAmountCharged);
		Double issuePercentage = new Double(0.0);
		
		if(i <= numAccounts)
			issuePercentage = new Double((amtCharged.doubleValue()/totalAmountCharged.doubleValue()) * 100);		
		
		map.put(idIssuePercentage, df.format(issuePercentage));
	}
%>
	

<tr>
	<td class="detaillabel"><yfc:i18n>Account_Code1</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=50 maxLength=50 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundAcctCode1")%>" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode1" name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode1" />
	</td>
	<td class="detaillabel"><yfc:i18n>Issue_Amount1</yfc:i18n></td>
	<td class="protectedtext">
		<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount1") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount1" name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount1" />
	</td>		
	<td class="detaillabel"><yfc:i18n>Issue_Percentage1</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("issuePercentage1") %>" id="issuePercentage1" name="issuePercentage1" />%
	</td>
	<td class="detaillabel"><yfc:i18n>Return_Percentage1</yfc:i18n></td>
	<td class="protectedtext">
		<input <%=map.get("isReadOnly1")%> type="text" class="unprotectedinput" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@ReturnPercentage1") %>" id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage1" name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage1" onkeyup="setTotalReturnPercentage(<%=numAccounts%>);" />%
	</td>		
</tr>
<tr>
	<td class="detaillabel"><yfc:i18n>Account_Code2</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=50 maxLength=50 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundAcctCode2") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode2" name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode2" />
	</td>
	<td class="detaillabel"><yfc:i18n>Issue_Amount2</yfc:i18n></td>
	<td class="protectedtext">
		<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount2") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount2" name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount2" />
	</td>		
	<td class="detaillabel"><yfc:i18n>Issue_Percentage2</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("issuePercentage2") %>" id="issuePercentage2" name="issuePercentage2" />%
	</td>
	<td class="detaillabel"><yfc:i18n>Return_Percentage2</yfc:i18n></td>
	<td class="protectedtext">
		<input <%=map.get("isReadOnly2")%> type="text" class="unprotectedinput" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@ReturnPercentage2") %>" id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage2" name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage2" onkeyup="setTotalReturnPercentage(<%=numAccounts%>);" />%
	</td>		
</tr>
<tr>
	<td class="detaillabel"><yfc:i18n>Account_Code3</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=50 maxLength=50 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundAcctCode3") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode3" name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode3" />
	</td>
	<td class="detaillabel"><yfc:i18n>Issue_Amount3</yfc:i18n></td>
	<td class="protectedtext">
		<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount3") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount3" name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount3" />
	</td>		
	<td class="detaillabel"><yfc:i18n>Issue_Percentage3</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("issuePercentage3") %>" id="issuePercentage3" name="issuePercentage3" />%
	</td>
	<td class="detaillabel"><yfc:i18n>Return_Percentage3</yfc:i18n></td>
	<td class="protectedtext">
		<input <%=map.get("isReadOnly3")%>  type="text" class="unprotectedinput" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@ReturnPercentage3") %>" id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage3" name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage3" onkeyup="setTotalReturnPercentage(<%=numAccounts%>);" />%
	</td>		
</tr>
<tr>
	<td class="detaillabel"><yfc:i18n>Account_Code4</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=50 maxLength=50 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundAcctCode4") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode4" name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode4" />
	</td>
	<td class="detaillabel"><yfc:i18n>Issue_Amount4</yfc:i18n></td>
	<td class="protectedtext">
		<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount4") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount4" name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount4" />
	</td>		
	<td class="detaillabel"><yfc:i18n>Issue_Percentage4</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("issuePercentage4") %>" id="issuePercentage4" name="issuePercentage4" />%
	</td>
	<td class="detaillabel"><yfc:i18n>Return_Percentage4</yfc:i18n></td>
	<td class="protectedtext">
		<input <%=map.get("isReadOnly4")%> type="text" class="unprotectedinput" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@ReturnPercentage4") %>" id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage4" name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage4" onkeyup="setTotalReturnPercentage(<%=numAccounts%>);" />%
	</td>		
</tr>
<tr>
	<td class="detaillabel"><yfc:i18n>Account_Code5</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=50 maxLength=50 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundAcctCode5") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode5" name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode5" />
	</td>
	<td class="detaillabel"><yfc:i18n>Issue_Amount5</yfc:i18n></td>
	<td class="protectedtext">
		<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount5") %>" id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount5" name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount5" />
	</td>		
	<td class="detaillabel"><yfc:i18n>Issue_Percentage5</yfc:i18n></td>
	<td class="protectedtext">
		<input tabindex="-1" readonly="true" type="text" class="protectedtext" size=4 maxLength=4 value="<%=map.get("issuePercentage5") %>" id="issuePercentage5" name="issuePercentage5" />%
	</td>
	<td class="detaillabel"><yfc:i18n>Return_Percentage5</yfc:i18n></td>
	<td class="protectedtext">
		<input <%=map.get("isReadOnly5")%> type="text" class="unprotectedinput" size=4 maxLength=4 value="<%=map.get("xml:/Receipt/SplitAccountCodeData/@ReturnPercentage5") %>" id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage5" name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage5" onkeyup="setTotalReturnPercentage(<%=numAccounts%>);" />%
	</td>		
</tr>
<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td colspan="8" align="center">
		<table width="60%">
			<tr>
				<td class="detaillabel">Total_Issue_Amount</td>				
				<td>
					<span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol" /></span>	
					<input readonly="true" type="text" class="totaltext" id="TotalAmountCharged" value="<%=map.get("TotalAmountCharged") %>" size=8 maxLength=8 />
				</td>
				<td class="detaillabel">Total_Return_Percentage</td>				
				<td>
					<input readonly="true" type="text" class="totaltext" id="TotalReturnPercentage" value="<%=map.get("TotalReturnPercentage") %>" size=8 maxLength=8 />%
				</td>
			</tr>
		</table>
	</td>
</tr>