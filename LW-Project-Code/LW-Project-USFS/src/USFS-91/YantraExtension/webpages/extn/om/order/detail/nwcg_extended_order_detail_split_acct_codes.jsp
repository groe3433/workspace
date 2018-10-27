<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/extn.js"></script>

<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>

<%	
YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser"); 
String UserNode = curUsr.getAttribute("Node");
YFCElement organizationInput = null;
organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + UserNode + "\" />").getDocumentElement();

YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationCode=\"\" > <Extn ExtnOwnerAgency=\"\" /> </Organization> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="xml:/Order/@ModificationReasonCode" />
<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
<input type="hidden" name="xml:/Order/@Override" value="N"/>
<input type="hidden" name="xml:/Extn/@OwnerAgency" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>'/>


<%
YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
String strFundCode = "" , strObjectClass = "" ; 
if(elem != null)
{
	Iterator itr = elem.getChildren();

	if(itr != null)
	{
		while(itr.hasNext())
		{
			YFCElement child = (YFCElement) itr.next();
			String strDesc = child.getAttribute("CodeShortDescription");
			if(strDesc.equals("FUND_CODE"))
			{
				strFundCode = child.getAttribute("CodeValue");
			}
			else if(strDesc.equals("OBJECT_CLASS"))
			{
				strObjectClass = child.getAttribute("CodeValue");
			}

		}
	}
}

String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
String AcctCode1 = "";
String OverrideCode1 = "";
String CostCenter1 = "";
String WBS1 = "";
String FA1 = "";
AcctCode1 = resolveValue("xml:/Order/Extn/@ExtnAcctCode1");
OverrideCode1 = resolveValue("xml:/Order/Extn/@ExtnOverrideCode1");

if ((AcctCode1.equals("")) && (OwnerAgency.equals("FS")))
{
   AcctCode1 = resolveValue("xml:/Order/Extn/@ExtnFsAcctCode");
}

if ((OverrideCode1.equals("")) && (OwnerAgency.equals("FS")))
{
   OverrideCode1 = resolveValue("xml:/Order/Extn/@ExtnOverrideCode");
}

if ((AcctCode1.equals("")) && (OwnerAgency.equals("BLM")))
{
   AcctCode1 = resolveValue("xml:/Order/Extn/@ExtnBlmAcctCode");
   CostCenter1 = resolveValue("xml:/Order/Extn/@ExtnCostCenter");
   WBS1 = resolveValue("xml:/Order/Extn/@ExtnWBS");
   FA1 = resolveValue("xml:/Order/Extn/@ExtnFunctionalArea");
}

if ((AcctCode1.equals("")) && (OwnerAgency.equals("")))
{
   AcctCode1 = resolveValue("xml:/Order/Extn/@ExtnOtherAcctCode");
}
%>

<!-- SPLIT ACCOUNT CODES -->
<tr>

<% if (OwnerAgency.equals("BLM"))
{ %>
  <!------------- --------------------->
  <td  class="detaillabel" >
  <yfc:i18n>Cost_Center1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" readonly="true" size=15 maxLength=10 <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>Functional_Area1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" readonly="true" size=20 maxLength=16 <%=getTextOptions("xml:/Order/Extn/@ExtnFunctionalArea")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>WBS1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" readonly="true" size=15 maxLength=12 <%=getTextOptions("xml:/Order/Extn/@ExtnWBS")%>/>
  <input type="hidden" class="protectedinput" readonly="true" size=10 maxLength=40 value="<%=AcctCode1%>" onblur="formatAccountCode(this,true,'<%=strFundCode%>','<%=strObjectClass%>')" <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode1")%>/>
  </td>
  <!------------- --------------------->
  

  <!-- <td class="detaillabel" >
  <yfc:i18n>BLM_Override_Code1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode1")%>/>
  </td> !-->

<%}else if (OwnerAgency.equals("FS"))
{%>
  <td  class="detaillabel" >
  <yfc:i18n>FS_Acct_Code1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="protectedinput" size=20 maxLength=40 value="<%=AcctCode1%>"  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode1")%>/>
  </td>

  <td class="detaillabel" >
  <yfc:i18n>FS_Override_Code1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="protectedinput"  size=20 maxLength=40 value="<%=OverrideCode1%>" onblur="checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode1')" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode1")%>/>
  </td>

<%} else {%>
  <td  class="detaillabel" >
  <yfc:i18n>OTHER_Acct_Code1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=25 maxLength=40 value="<%=AcctCode1%>"  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode1")%>/>
  </td>

  <!-- <td class="detaillabel" >
  <yfc:i18n>OTHER_Override_Code1</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode1")%>/>
  </td> !-->

<%}%>

<% String totamtstr = resolveValue("xml:/Order/OverallTotals/@GrandTotal"); 
   String [] CLfields = totamtstr.split(",");
   String UList = "";
   for (int i=0;i<CLfields.length;i++)
   {
	UList = UList+CLfields[i];
   }
   totamtstr = UList;
   %>
<input type="hidden" name="xml:/Extn/@GrandTotal" value="<%=totamtstr%>"/>
<td class="detaillabel" >
<yfc:i18n>Dollar_Amount1</yfc:i18n>
</td>
<td class="protectedtext"><span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" class="unprotectedinput" onblur="setPercentValue(this,'xml:/Extn/@ExtnPercent1','xml:/Order/Extn/@ExtnSplitAmount2','xml:/Extn/@ExtnPercent2','<%=totamtstr%>','xml:/Extn/@RunTotal')"
<%=getTextOptions("xml:/Order/Extn/@ExtnSplitAmount1")%>/>
</td>

<% String amtstr1 = resolveValue("xml:/Order/Extn/@ExtnSplitAmount1");
   double tot_amt = Double.parseDouble(totamtstr);
   double amt1 = Double.parseDouble(amtstr1);
   double percent1 = (amt1/tot_amt) * 100;
   percent1 = Math.round(percent1*100.00) / 100.00;
%>
   
<td class="detaillabel" >
<yfc:i18n>Percent</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="protectedtext" readonly="true" value="<%=percent1%>%" <%=getTextOptions("xml:/Extn/@ExtnPercent1")%>/>
</td>
</tr>

<tr>
<% if (OwnerAgency.equals("BLM"))
{ %>
<!------------- --------------------->
  <td  class="detaillabel" >
  <yfc:i18n>Cost_Center2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=10 onBlur="checkCostCenter2splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter2")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>Functional_Area2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=16 onBlur="checkFA2splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnFA2")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>WBS2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=12 onBlur="checkWBS2splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS2")%>/>
  <input type="hidden" class="protectedinput" readonly="true" size=10 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode2")%>/>
  </td>
<!------------- --------------------->

  <!-- <td class="detaillabel" >
  <yfc:i18n>BLM_Override_Code2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode2")%>/>
  </td> !-->

<%}else if (OwnerAgency.equals("FS"))
{%>
  <td  class="detaillabel" >
  <yfc:i18n>FS_Acct_Code2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=40   <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode2")%>/>
  </td>

  <td class="detaillabel" >
  <yfc:i18n>FS_Override_Code2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 onblur="checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode2')" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode2")%>/>
  </td>
<%} else {%>
  <td  class="detaillabel" >
  <yfc:i18n>OTHER_Acct_Code2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=25 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode2")%>/>
  </td>

  <!-- <td class="detaillabel" >
  <yfc:i18n>OTHER_Override_Code2</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode2")%>/>
  </td> !-->

<%}%>


<td class="detaillabel" >
<yfc:i18n>Dollar_Amount2</yfc:i18n>
</td>
<td class="protectedtext"><span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" class="unprotectedinput"  onblur="setPercentValue(this,'xml:/Extn/@ExtnPercent2','xml:/Order/Extn/@ExtnSplitAmount3','xml:/Extn/@ExtnPercent3','<%=totamtstr%>','xml:/Extn/@RunTotal')" <%=getTextOptions("xml:/Order/Extn/@ExtnSplitAmount2")%>/>
</td>

<% String amtstr2 = resolveValue("xml:/Order/Extn/@ExtnSplitAmount2");
   double amt2 = Double.parseDouble(amtstr2);
   double percent2 = (amt2/tot_amt) * 100;
   percent2 = Math.round(percent2*100.00) / 100.00;
%>

<td class="detaillabel" >
<yfc:i18n>Percent</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="protectedinput"  readonly="true" value="<%=percent2%>%" <%=getTextOptions("xml:/Extn/@ExtnPercent2")%>/>
</td>
</tr>

<tr>
<% if (OwnerAgency.equals("BLM"))
{ %>
<!------------- --------------------->
  <td  class="detaillabel" >
  <yfc:i18n>Cost_Center3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=10 onBlur="checkCostCenter3splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter3")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>Functional_Area3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=16 onBlur="checkFA3splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnFA3")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>WBS3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=12 onBlur="checkWBS3splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS3")%>/>
  <input type="hidden" class="protectedinput" readonly="true" size=10 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode3")%>/>
  </td>
<!------------- --------------------->  

  <!-- <td class="detaillabel" >
  <yfc:i18n>BLM_Override_Code3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode3")%>/>
  </td> !-->

<%}else if (OwnerAgency.equals("FS"))
{%>
  <td  class="detaillabel" >
  <yfc:i18n>FS_Acct_Code3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=40   <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode3")%>/>
  </td>

  <td class="detaillabel" >
  <yfc:i18n>FS_Override_Code3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 onblur="checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode3')" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode3")%>/>
  </td>
<%} else {%>
  <td  class="detaillabel" >
  <yfc:i18n>OTHER_Acct_Code3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=25 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode3")%>/>
  </td>

  <!-- <td class="detaillabel" >
  <yfc:i18n>OTHER_Override_Code3</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode3")%>/>
  </td> !-->

<%}%>

<td class="detaillabel" >
<yfc:i18n>Dollar_Amount3</yfc:i18n>
</td>
<td class="protectedtext"><span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" class="unprotectedinput" onblur="setPercentValue(this,'xml:/Extn/@ExtnPercent3','xml:/Order/Extn/@ExtnSplitAmount4','xml:/Extn/@ExtnPercent4','<%=totamtstr%>','xml:/Extn/@RunTotal')" <%=getTextOptions("xml:/Order/Extn/@ExtnSplitAmount3")%>/>
</td>

<% String amtstr3 = resolveValue("xml:/Order/Extn/@ExtnSplitAmount3");
   double amt3 = Double.parseDouble(amtstr3);
   double percent3 = (amt3/tot_amt) * 100;
   percent3 = Math.round(percent3*100.00) / 100.00;
%>

<td class="detaillabel" >
<yfc:i18n>Percent</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="protectedinput"  readonly="true" value="<%=percent3%>%" <%=getTextOptions("xml:/Extn/@ExtnPercent3")%>/>
</td>
</tr>

<tr>
<% if (OwnerAgency.equals("BLM"))
{ %>
<!------------- --------------------->
  <td  class="detaillabel" >
  <yfc:i18n>Cost_Center4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=10 onBlur="checkCostCenter4splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter4")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>Functional_Area4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=16 onBlur="checkFA4splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnFA4")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>WBS4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=12 onBlur="checkWBS4splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS4")%>/>
  <input type="hidden" class="protectedinput" readonly="true" size=10 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode4")%>/>
  </td>
<!------------- --------------------->  

  <!-- <td class="detaillabel" >
  <yfc:i18n>BLM_Override_Code4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode4")%>/>
  </td> !-->

<%}else if (OwnerAgency.equals("FS"))
{%>
  <td  class="detaillabel" >
  <yfc:i18n>FS_Acct_Code4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=40   <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode4")%>/>
  </td>

  <td class="detaillabel" >
  <yfc:i18n>FS_Override_Code4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 onblur="checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode4')" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode4")%>/>
  </td>
<%} else {%>
  <td  class="detaillabel" >
  <yfc:i18n>OTHER_Acct_Code4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=25 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode4")%>/>
  </td>

  <!-- <td class="detaillabel" >
  <yfc:i18n>OTHER_Override_Code4</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode4")%>/>
  </td> !-->

<%}%>

<td class="detaillabel" >
<yfc:i18n>Dollar_Amount4</yfc:i18n>
</td>
<td class="protectedtext"><span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" class="unprotectedinput" onblur="setPercentValue(this,'xml:/Extn/@ExtnPercent4','xml:/Order/Extn/@ExtnSplitAmount5','xml:/Extn/@ExtnPercent5','<%=totamtstr%>','xml:/Extn/@RunTotal')" <%=getTextOptions("xml:/Order/Extn/@ExtnSplitAmount4")%>/>
</td>

<% String amtstr4 = resolveValue("xml:/Order/Extn/@ExtnSplitAmount4");
   double amt4 = Double.parseDouble(amtstr4);
   double percent4 = (amt4/tot_amt) * 100;
   percent4 = Math.round(percent4*100.00) / 100.00;
%>

<td class="detaillabel" >
<yfc:i18n>Percent</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="protectedinput" readonly="true" value="<%=percent4%>%" <%=getTextOptions("xml:/Extn/@ExtnPercent4")%>/>
</td>
</tr>

<tr>
<% if (OwnerAgency.equals("BLM"))
{ %>
<!------------- --------------------->
  <td  class="detaillabel" >
  <yfc:i18n>Cost_Center5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=10 onBlur="checkCostCenter5splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter5")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>Functional_Area5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=16 onBlur="checkFA5splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnFA5")%>/>
  </td>
  <td  class="detaillabel" >
  <yfc:i18n>WBS5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=15 maxLength=12 onBlur="checkWBS5splitcodes()" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS5")%>/>
  <input type="hidden" class="protectedinput" readonly="true" size=10 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode5")%>/>
  </td>
<!------------- --------------------->  

  <!-- <td class="detaillabel" >
  <yfc:i18n>BLM_Override_Code5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode5")%>/>
  </td> !-->

<%}else if (OwnerAgency.equals("FS"))
{%>
  <td  class="detaillabel" >
  <yfc:i18n>FS_Acct_Code5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=20 maxLength=40   <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode5")%>/>
  </td>

  <td class="detaillabel" >
  <yfc:i18n>FS_Override_Code5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40 onblur="checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode5')" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode5")%>/>
  </td>
<%} else {%>
  <td  class="detaillabel" >
  <yfc:i18n>OTHER_Acct_Code5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput" size=25 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode5")%>/>
  </td>

  <!-- <td class="detaillabel" >
  <yfc:i18n>OTHER_Override_Code5</yfc:i18n>
  </td>
  <td class="protectedtext">
  <input type="text" class="unprotectedinput"  size=20 maxLength=40  <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode5")%>/>
  </td> !-->

<%}%>

<td class="detaillabel" >
<yfc:i18n>Dollar_Amount5</yfc:i18n>
</td>
<td class="protectedtext"><span><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" class="unprotectedinput" onblur="setPercentValue(this,'xml:/Extn/@ExtnPercent5','xml:/Order/Extn/@ExtnSplitAmount1','xml:/Extn/@ExtnPercent1','<%=totamtstr%>','xml:/Extn/@RunTotal')" <%=getTextOptions("xml:/Order/Extn/@ExtnSplitAmount5")%>/>
</td>

<% String amtstr5 = resolveValue("xml:/Order/Extn/@ExtnSplitAmount5");
   double amt5 = Double.parseDouble(amtstr5);
   double percent5 = (amt5/tot_amt) * 100;
   percent5 = Math.round(percent5*100.00) / 100.00;
%>

<td class="detaillabel" >
<yfc:i18n>Percent</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="protectedinput" readonly="true" value="<%=percent5%>%" <%=getTextOptions("xml:/Extn/@ExtnPercent5")%>/>
</td>
</tr>

<tr>
<td class="protectedtext">
<input type="hidden"/>
</td>
</tr>

<tr>
<td class="protectedtext">
<input type="hidden"/>
</td>
</tr>

<tr>
<td class="protectedtext">
<input type="hidden"/>
</td>
</tr>

<tr>

<!-- <td class="detaillabel" ><yfc:i18n>Issue_Total</yfc:i18n></td>
<td class="totaltext"><span class="protectednumber" ><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp; <%=getValue("Order","xml:/Order/OverallTotals/@GrandTotal")%>&nbsp; <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></span><span class="protectedtext"  style="width:8px">&nbsp;</span>
</td>  !-->

<% tot_amt = Math.round(tot_amt*100.00)/100.00; 
String totstr = Double.toString(tot_amt);
String [] totfields = totstr.split("[.]");
 if (totfields[1].length() < 2)
   {
	 totstr = totstr + "0";
   } 
%>

<td class="detaillabel" ><yfc:i18n>Issue_Total</yfc:i18n></td>
<td ><input class="totaltext" value="$ <%=totstr%>" <%=getTextOptions("xml:/Extn/@CurrentTotal2")%>/> 
</td> 

<td class="protectedtext">
<input type="hidden"/>
</td>
<td class="protectedtext">
<input type="hidden"/>
</td> 

<% double currenttotamt = amt1+amt2+amt3+amt4+amt5;
   currenttotamt = Math.round(currenttotamt*100.00) / 100.00;
   String ctotstr = Double.toString(currenttotamt);
   String [] Ctfields = ctotstr.split("[.]");
   if (Ctfields[1].length() < 2)
   {
	 ctotstr = ctotstr + "0";
   } 
%> 

<td class="detaillabel" ><yfc:i18n>Current_Total</yfc:i18n></td>
<td ><input class="totaltext" value="$ <%=ctotstr%>" <%=getTextOptions("xml:/Extn/@CurrentTotal")%>/>
</td>

<input type="hidden" name="xml:/Extn/@RunTotal" value="<%=currenttotamt%>"/>

</tr>

