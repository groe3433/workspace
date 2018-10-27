<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script language="javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
	
	//CR 733 BEGIN - ML
	function setIsActiveModified(inObj)
	{
		if (inObj.checked == inObj.getAttribute('OldChecked')) 
		{
			document.getElementById("xml:/NWCGIncidentOrder/@IsActiveModified").value = "N";
		}
		else
		{
			document.getElementById("xml:/NWCGIncidentOrder/@IsActiveModified").value = "Y";
		}
	}
	//CR733 END
	
//CR 808 - GN
function popUpOrderLines()
{
  var incNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo").value;
  var incYr = ' ';
  var extraParameters = "NWCG";
  extraParameters += "&" + "xml:/Order/Extn/@ExtnIncidentNo=" + incNo + "&" + "xml:/Order/Extn/@ExtnIncidentYear=" + incYr;
  yfcShowListPopupWithParams('NWGYOML010', '', '1280', '800', '', 'ISUorder', extraParameters);
  return false;
}


</script>

<%
String incidentKeyVal = resolveValue("xml:/NWCGIncidentOrder/@IncidentKey");
//System.out.println("IncidentKey:" + incidentKeyVal);
String sLookup = "NWCGCustomerLookUp";
%>
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
YFCElement myElement = getRequestDOM();
session.setAttribute("otherOrderNumber",myElement);


%>
<!-- indicator that it is the other order -->
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="Y"/>

<table class="view" width="100%">
<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>

<yfc:makeXMLInput name="otherOrderPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>

<input type="hidden" name="PrintEntityKey" value='<%=getParameter("otherOrderPrintKey")%>'/>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyIncidentEntityKey" value='<%=getParameter("incidentKey")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentKey" value='<%=incidentKeyVal%>' />
<!--  BR 733 - BEGIN - ML -->
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsActiveModified" value='N' />
<!--  BR 733 - BEGIN - ML -->
<tr>
<td class="detaillabel"><yfc:i18n>Other_Order_Number</yfc:i18n></td>

<td class="protectedtext">
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>

<input type="hidden" name="xml:/NWCGIncidentOrder/@OwnerAgency" value='<%=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency")%>'/>
</td>

<% System.out.println("owner agency in other order detail page " + resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency"));

//xml:CurrentUser:/User/@Node

System.out.println("user node " + resolveValue("xml:CurrentUser:/User/@Node"));

%>
</td>
<td class="detaillabel"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  size="30" maxLength="50" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%> />
</td>
<td class="detaillabel"><yfc:i18n>Other_Order_Type</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/NWCGIncidentOrder/@OtherOrderType")%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Customer_PO_No</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerPONo")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<!-- CR 386 KS -->
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%></td>
<!-- end of CR 386 -->
<td class="detaillabel"><yfc:i18n>Customer_Name</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%></td>

<%--
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%>/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
--%>
</tr>
<!-- FBMS elements -->
<tr>
	<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onBlur="checkCostCenter(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@CostCenter")%>/></td>
	<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onBlur="checkFunctionalArea(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@FunctionalArea")%>/></td>
	<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onBlur="checkWBS(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@WBS")%>/></td>
</tr>
<!-- FBMS elements -->
<tr>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  size="50" maxLength="40"  <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  size="10" maxLength="4"  <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="50" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%> />
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  size="50" maxLength="40" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@DateStarted")%></td>

<%--
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateStarted")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
--%>

<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n>
</td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<!-- CR 733 BEGIN - ML -->
<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsActive","xml:/NWCGIncidentOrder/@IsActive","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N' onclick="setIsActiveModified(this)"/>
</td>
<!-- CR 733 BEGIN - ML -->
</tr>
<!-- CR 808 BEGIN - GN -->
<tr>
<td/>
<td/>
<td/>
<td/>
<td/>
<td align="left" class="searchcriteriacell">
<A onclick="popUpOrderLines();return false;" href=""><yfc:i18n>View Issues For Other Order</yfc:i18n></A>
<td/>
</tr>
<!-- CR 808 END - GN -->
</table>
