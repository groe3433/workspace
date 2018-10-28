<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ page import="com.yantra.yfc.dom.YFCDocument" %>
<%@ page import="com.yantra.yfc.dom.YFCElement" %>
<%@ page import="org.w3c.dom.*" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_incident.js"></script>

<script language="javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<script language="javascript">
function setIsActiveModified(inObj){
	if (inObj.checked == inObj.getAttribute('OldChecked')) {
		document.getElementById("xml:/NWCGIncidentOrder/@IsActiveModified").value = "N";
	}
	else {
		document.getElementById("xml:/NWCGIncidentOrder/@IsActiveModified").value = "Y";
	}
}

function popUpOrderLines() 
{
		var incNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo").value;
		var incYr = document.getElementById("xml:/NWCGIncidentOrder/@Year").value;
		var extraParameters = "NWCG";
		extraParameters += "&" + "xml:/Order/Extn/@ExtnIncidentNo=" + incNo + "&" + "xml:/Order/Extn/@ExtnIncidentYear=" + incYr;
		yfcShowListPopupWithParams('NWGYOML010', '', '1280', '800', '', 'ISUorder', extraParameters);
		return false;	
}
</script>
<%
	String incidentKeyVal = resolveValue("xml:/NWCGIncidentOrder/@IncidentKey");
	String sLookup = "NWCGCustomerLookUp";
	
	String strIncidentNo = StringUtil.nonNull(request.getParameter("xml:/NWCGIncidentOrder/@IncidentNo"));
	String strIncidentYear = StringUtil.nonNull(request.getParameter("xml:/NWCGIncidentOrder/@IncidentYear"));
	String strIncidentAction = StringUtil.nonNull(request.getParameter("xml:/NWCGIncidentOrder/@IncidentAction"));
	
	YFCElement getOrderListInput = null;
	getOrderListInput = YFCDocument.parse("<Order EnterpriseCode=\"NWCG\"><Extn ExtnIncidentNo=\"" + strIncidentNo + "\" ExtnIncidentYear=\"" + strIncidentYear + "\" /></Order>").getDocumentElement();
	
	//This document is used in the popup screen from issue entry.
	//It is the last if in this jsp page
	YFCDocument yfcDoc = YFCDocument.createDocument("NWCGIncidentOrder");
	yfcDoc.getDocumentElement().setAttribute("IncidentNo",strIncidentNo);
	yfcDoc.getDocumentElement().setAttribute("Year",strIncidentYear);
	yfcDoc.getDocumentElement().setAttribute("IncidentAction",strIncidentAction);
	
	// Adding the Operation Type attribute. This is used by incident creation screen 
	// coming out of issue creation screen. Here, the call should be synchronous to ROSS,
	// so setting the operation type to sync. This variable will be used in  XSLT
	yfcDoc.getDocumentElement().setAttribute("OperationType", "sync");
	yfcDoc.getDocumentElement().setAttribute("RegisterInterestInROSS", "Y");
%>
	
<!-- Adding a if condition which checks if incident action is null. If it is not null then it implies
     that the incident details page is getting invoked from the tab out of the year field in the
	 create incident screen. If it is null then it implies that the details page is getting invoked
	 after the user navigates to the details page from the list page -->
<%
	YFCElement resultElemCommonCode = getElement("CommonIncidentCodeList");
	
	YFCDocument yfcDoc1 = YFCDocument.createDocument("NWCGIncidentOrder");
	yfcDoc1.getDocumentElement().setAttribute("IncidentKey",incidentKeyVal);

	yfcDoc1.getDocumentElement().setAttribute("IncidentNo",strIncidentNo);
	yfcDoc1.getDocumentElement().setAttribute("Year",strIncidentYear);
	yfcDoc1.getDocumentElement().setAttribute("IncidentAction",strIncidentAction);
	
	if(strIncidentAction.trim().equals("")) { 	
		YFCElement incidentEntityKeyFromROSS = getElement("IncidentEntityKeyFromROSS");
		if(incidentEntityKeyFromROSS == null){	
%>
<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>

<yfc:makeXMLInput name="incidentPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>


<table class="view" width="100%">

<tr>
<td>
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="N"/>
<input type="hidden" name="PrintEntityKey" value='<%=getParameter("incidentPrintKey")%>'/>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyIncidentEntityKey" value='<%=getParameter("incidentKey")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentKey" value='<%=incidentKeyVal%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsOtherOrder")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSIncidentStatus" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSIncidentStatus")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSDispatchID" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSDispatchID")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsComplexIndicator" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsComplexIndicator")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialCode" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialCode")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockStart" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockStart")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockEnd" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockEnd")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ContactName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ContactName")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@PhoneNo" value='<%=resolveValue("xml:/NWCGIncidentOrder/@PhoneNo")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@LastUpdatedFromROSS" value='<%=resolveValue("xml:/NWCGIncidentOrder/@LastUpdatedFromROSS")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerId" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@OwnerAgency" value='<%=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency")%>'/>
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentSource" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsSuppressive" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsSuppressive")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsActiveModified" value='N' />
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IsOtherOrder")%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Incident_No</yfc:i18n></td>
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>
</td>
<!-- Surya: Made Incident Name field read only -->
<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentName")%></td>
<!-- Surya: Made Incident Year field read only -->
<td class="detaillabel"><yfc:i18n>Year</yfc:i18n></td>
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@Year")%></td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Suppressive Incident</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IsSuppressive")%></td>
<td class="detaillabel"><yfc:i18n>ICBS Fiscal Year</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ICBSFiscalYear")%>/>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%></td>
<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
<input type='hidden' <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerName")%> />
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%></td>
<td class="detaillabel"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentHost")%>/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentType")%>
</td>

<td class="detaillabel"><yfc:i18n>Incident_Team_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentTeamType")%>>
<yfc:loopOptions binding="xml:CommonIncidentTeamList:/CommonCodeList/@CommonCode" selected="xml:/NWCGIncidentOrder/@IncidentTeamType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>

<!-- Adding Incident Team Name as part of CR 139 -->
<td class="detaillabel"><yfc:i18n>Incident_Team_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentTeamName")%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Incident_Source</yfc:i18n></td><td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%></td>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onBlur="validateOverrideCodeLength(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>

</tr>

<tr>
	<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onBlur="checkCostCenter(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@CostCenter")%>/></td>
	<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onBlur="checkFunctionalArea(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@FunctionalArea")%>/></td>
	<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onBlur="checkWBS(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@WBS")%>/></td>
</tr>

<tr>
<td class="detaillabel" tabindex="-1"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
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
%>
<input type="text" class="protectedinput" size="70" tabindex="-1" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%> />
</td>

<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n>
</td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@DateStarted")%></td>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsActive","xml:/NWCGIncidentOrder/@IsActive","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N' onclick="setIsActiveModified(this)"/>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Last_Incident_1</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_1</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear"/>
        </td>
<td class="detaillabel" ><yfc:i18n>Register Interest in ROSS</yfc:i18n></td>
<td  class="checkbox" >
<input type="checkbox" onclick="this.checked=!this.checked"
<%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@RegisterInterestInROSS","xml:/NWCGIncidentOrder/@RegisterInterestInROSS","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentLocked" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentLocked")%>' />

</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Last_Incident_2</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo2")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_2</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear2")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear2"/> 
        </td>

<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<!-- Sunjay Gunda: Adding Last Incident No 3 and Year 3 -->
<tr>
	<td class="detaillabel"><yfc:i18n>Last_Incident_3</yfc:i18n></td>
	<td>
		<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo3")%>/>
	</td>
	<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_3</yfc:i18n></td>
	<td>
		<input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear3")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear3" />
	</td>
	<td/>
	<td align="left" class="searchcriteriacell">
	<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%>/>
	<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
		<A onclick="popUpOrderLines();return false;" href=""><yfc:i18n>View_Issues_For_Incident</yfc:i18n></A>
	<td/>
</tr>


<tr>
<!-- Begin CR868 12052012 -->
<td class="detaillabel"><yfc:i18n>IncidentID</yfc:i18n></td>
<td>
   <input type="text" size="30" maxLength="10" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentId")%>/>
</td>
<!-- End CR868 12052012 -->

<!-- Sunjay Gunda: Added Incident Lock Field -->
<td class="detaillabel"><yfc:i18n>Incident_Locked</yfc:i18n></td>
<td class="checkbox" sortable="no">
	<input type="checkbox" onclick="this.checked=!this.checked" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IncidentLocked","xml:/NWCGIncidentOrder/@IncidentLocked","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<!-- OG: Added Incident Lock Reason Field -->
<%
	//display reason only if incident is locked
    if (getTextOptions("xml:/NWCGIncidentOrder/@IncidentLocked").indexOf("Value=\"Y\"")>0)
		{
%>
<td class="detaillabel" ><yfc:i18n>Lock_Reason</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@LockReason")%>/>
<td colspan="3" class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@LockReason")%>
</td>
<%
		}
//end if - if incidentEntityKeyFromROSS is null
%>
</tr>

</table>

<%
	}else{
		
		String tagName1 = incidentEntityKeyFromROSS.getTagName();
		if(tagName1.equals("Inbox")){
			String errorString1 = incidentEntityKeyFromROSS.getAttribute("DetailDescription");
%>
	<table class="simpletable" width="100%">
		<tr>
			<td class="errortext">ERROR Message from ROSS:
			<%=errorString1%>
			</td>
		</tr>
	</table>
<%
		}else{
%>			

<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>
<yfc:makeXMLInput name="incidentPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>
<table class="view" width="100%">
<tr>
	<td>
	<input type="hidden" name="PrintEntityKey" value='<%=getParameter("incidentPrintKey")%>'/>
	<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>	
	<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsOtherOrder")%>' />	
	<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSIncidentStatus" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSIncidentStatus")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSDispatchID" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSDispatchID")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@IsComplexIndicator" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsComplexIndicator")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialCode" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialCode")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockStart" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockStart")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockEnd" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockEnd")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@ContactName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ContactName")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@PhoneNo" value='<%=resolveValue("xml:/NWCGIncidentOrder/@PhoneNo")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@LastUpdatedFromROSS" value='<%=resolveValue("xml:/NWCGIncidentOrder/@LastUpdatedFromROSS")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerId" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%>' />	
	<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentSource" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@IsSuppressive" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsSuppressive")%>' />
	<input type="hidden" name="xml:/NWCGIncidentOrder/@IsActiveModified" value='N' />
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IsOtherOrder")%>/>
	</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>
</td>

<!-- Surya: Made Incident Name field read only -->
<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentName")%></td>

<!-- Surya: Made Incident Year field read only -->
<td class="detaillabel"><yfc:i18n>Year</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@Year")%></td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Suppressive Incident</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IsSuppressive")%></td>
<td class="detaillabel"><yfc:i18n>ICBS Fiscal Year</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@ICBSFiscalYear")%></td>
<td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%>/>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%></td>
<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
<input type='hidden' <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerName")%> />
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%></td>
<td class="detaillabel"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentHost")%>/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%> />
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentType")%>>
<yfc:loopOptions binding="xml:CommonIncidentCodeList:/CommonCodeList/@CommonCode" selected="xml:/NWCGIncidentOrder/@IncidentType" name="CodeShortDescription" value="CodeValue"  isLocalized="Y"/>
</select>
</td>
<td class="detaillabel"><yfc:i18n>Incident_Team_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentTeamType")%>>
<yfc:loopOptions binding="xml:CommonIncidentTeamList:/CommonCodeList/@CommonCode" selected="xml:/NWCGIncidentOrder/@IncidentTeamType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>

<!-- Adding Incident Team Name as part of CR 139 -->
<td class="detaillabel"><yfc:i18n>Incident_Team_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentTeamName")%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Incident_Source</yfc:i18n></td><td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%></td>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onBlur="validateOverrideCodeLength(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>

</tr>

<tr>
	<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onBlur="checkCostCenter(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@CostCenter")%>/></td>
	<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onBlur="checkFunctionalArea(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@FunctionalArea")%>/></td>
	<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onBlur="checkWBS(this)" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@WBS")%>/></td>	
</tr>

<tr>
<td class="detaillabel" tabindex="-1"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
<%
YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
String strFundCode = "" , strObjectClass = "" ;
if(elem != null) {
	Iterator itr = elem.getChildren();

	if(itr != null)	{
		while(itr.hasNext()) {
			YFCElement child = (YFCElement) itr.next();
			String strDesc = child.getAttribute("CodeShortDescription");
			if(strDesc.equals("FUND_CODE"))	{
				strFundCode = child.getAttribute("CodeValue");
			}
			else if(strDesc.equals("OBJECT_CLASS"))	{
				strObjectClass = child.getAttribute("CodeValue");
			}
		}
	}
}
%>
<input type="text" class="protectedinput" size="70" tabindex="-1" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%> />
</td>

<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n>
</td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@DateStarted")%></td>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsActive","xml:/NWCGIncidentOrder/@IsActive","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N' onclick="setIsActiveModified(this)"/>
</tr>
<tr>

<td class="detaillabel"><yfc:i18n>Last_Incident_1</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_1</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear"/> 
        </td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Last_Incident_2</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo2")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_2</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear2")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear2"/> 
        </td>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>

<!-- Sunjay Gunda: Added Incident Lock Field -->
<tr>
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentLocked" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentLocked")%>' />

<td class="detaillabel"><yfc:i18n>Incident_Locked</yfc:i18n></td>
<td class="checkbox" sortable="no">
	<input type="checkbox" onclick="this.checked=!this.checked" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IncidentLocked","xml:/NWCGIncidentOrder/@IncidentLocked","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<!-- OG: Added Incident Lock Reason Field -->
<%
	//display reason only if incident is locked
    if (getTextOptions("xml:/NWCGIncidentOrder/@IncidentLocked").indexOf("Value=\"Y\"")>0)
		{
%>
<td class="detaillabel" ><yfc:i18n>Lock_Reason</yfc:i18n></td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@LockReason")%>/>
<td colspan="3" class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@LockReason")%>
</td>
<%
		}
//end if 
%>
</tr>
</table>
<%
		}
	}
 } else { 
 %>

<yfc:callAPI serviceName='NWCGPostRegisterIncidentInterestSyncService' inputElement='<%=yfcDoc.getDocumentElement()%>' outputNamespace="SOAPResponse"/>

<%
	YFCElement regRespElem = getElement("SOAPResponse");
	String retnCode = "";
	if (regRespElem == null){
%>
		<table class="simpletable" width="100%">
			<tr>
				<td class="errortext">ROSS returned NULL as part of Register Incident Interest Sync service!</td>
			</tr>
		</table>
<%
		return;
	}
	else if (regRespElem.getNodeName().indexOf("RegisterIncidentInterestResp") != -1){
		YFCNodeList retnCodeNL = regRespElem.getElementsByTagName("ReturnCode"); 
		retnCode = retnCodeNL.item(0).getNodeValue();
		if (retnCode.equalsIgnoreCase("-1")){
			YFCNodeList errorDescNL = regRespElem.getElementsByTagName("Description"); 
			String errorDesc = errorDescNL.item(0).getNodeValue(); 
%> 
			<table class="simpletable" width="100%"> 
				<tr> 
					<td class="errortext">ERROR Message from ROSS: <%=errorDesc%></td>
				</tr>
			</table>
<%
			return;
		}
		// else, make the call to get incident details for displaying it on the screen.
	}
	else {
		//System.out.println("JSP - Local Name is not RegisterIncidentInterestResp ");
		String errorDesc = regRespElem.getAttribute("Message");
%>
                <table class="simpletable" width="100%">
                        <tr>
                                <td class="errortext">SOAP Fault: <%=errorDesc%></td>
                        </tr>
                </table><%
		return;
	}

	YFCDocument getDtslDoc = YFCDocument.createDocument("NWCGIncidentOrder");
	getDtslDoc.getDocumentElement().setAttribute("IncidentNo",strIncidentNo);
	getDtslDoc.getDocumentElement().setAttribute("Year",strIncidentYear);
%>
<yfc:callAPI serviceName='NWCGGetIncidentOrderService' inputElement='<%=getDtslDoc.getDocumentElement()%>' outputNamespace="NWCGIncidentOrder"/>
<% 
	YFCElement resultElem = getElement("NWCGIncidentOrder");
	String tagName = "";
	if (resultElem == null) {
		tagName = "";
	}
	else {
		tagName = resultElem.getTagName();
	}
	
	if(tagName.equals("")) {
%>
		<table class="simpletable" width="100%">
			<tr>
				<td class="errortext">ROSS returned NULL as part of Register Incident Interest Sync service</td>
			</tr>
		</table>
<%
	} else {
	String resultStr = resultElem.getString();
	String incKey = resolveValue("xml:/NWCGIncidentOrder/@IncidentKey");	
%>

<table class="view" width="100%">

<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>
<yfc:makeXMLInput name="incidentPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" /></yfc:makeXMLInput>
<tr>
<td>
<input type="hidden" name="PrintEntityKey" value='<%=getParameter("incidentPrintKey")%>'/>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyIncidentEntityKey" value='<%=getParameter("incidentKey")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentKey" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentKey")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentNo" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentName")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@Year" value='<%=resolveValue("xml:/NWCGIncidentOrder/@Year")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsOtherOrder")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSIncidentStatus" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSIncidentStatus")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSDispatchID" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSDispatchID")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsComplexIndicator" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsComplexIndicator")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialCode" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialCode")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ROSSFinancialFiscalYear")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockStart" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockStart")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@RequestNoBlockEnd" value='<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockEnd")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@ContactName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@ContactName")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@PhoneNo" value='<%=resolveValue("xml:/NWCGIncidentOrder/@PhoneNo")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@LastUpdatedFromROSS" value='<%=resolveValue("xml:/NWCGIncidentOrder/@LastUpdatedFromROSS")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerId" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@CustomerName" value='<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentSource" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsSuppressive" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IsSuppressive")%>' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsActiveModified" value='N' />
<input type="hidden" name="xml:/NWCGIncidentOrder/@OwnerAgency" value='<%=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency")%>'/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>
</td>
<!-- Surya: Made Incident Name field read only -->
<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentName")%></td>
<!-- Surya: Made Incident Year field read only -->
<td class="detaillabel"><yfc:i18n>Year</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@Year")%></td>
</tr>
<tr>
<%
	String val = "N";
	String incidentType = resolveValue("xml:/NWCGIncidentOrder/@IncidentType");

	Document resultElemDoc = resultElemCommonCode.getOwnerDocument().getDocument();
	//System.out.println("document :"+com.nwcg.icbs.yantra.util.common.XMLUtil.getXMLString(resultElemDoc));
	NodeList nodeList = resultElemDoc.getChildNodes().item(0).getChildNodes();
	//System.out.println("length :"+nodeList.getLength());
	for(int a=0;a<nodeList.getLength();a++){
	    Element element = (Element)nodeList.item(a);
	    String codeValue = element.getAttribute("CodeValue");
	    //System.out.println("codeValue :"+codeValue);
		if(incidentType.equals(codeValue)){
			val = "Y";
		}
	}	
%>
<td class="detaillabel"><yfc:i18n>Suppressive Incident</yfc:i18n></td>
<td class="protectedtext"><%=val%></td>
<td class="detaillabel"><yfc:i18n>ICBS Fiscal Year</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@ICBSFiscalYear")%></td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%></td>
<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%></td>
<td class="detaillabel"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentHost")%>/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentType")%>
</td>
<td class="detaillabel"><yfc:i18n>Incident_Team_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentTeamType")%>>
<yfc:loopOptions binding="xml:CommonIncidentTeamList:/CommonCodeList/@CommonCode" selected="xml:/NWCGIncidentOrder/@IncidentTeamType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>

<!-- Adding Incident Team Name as part of CR 139 -->
<td class="detaillabel"><yfc:i18n>Incident_Team_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentTeamName")%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Incident_Source</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentSource")%></td>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onBlur="validateOverrideCodeLength(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>
</tr>
<tr>
	<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onBlur="checkCostCenter(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@CostCenter")%>/></td>
	<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onBlur="checkFunctionalArea(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@FunctionalArea")%>/></td>
	<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onBlur="checkWBS(this)" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@WBS")%>/></td>	
</tr>

<tr>
<td class="detaillabel" tabindex="-1"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
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
%>
<input type="text" class="protectedinput" size="70" tabindex="-1" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%> />
</td>

<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n>
</td>
<td>
<input type="text" class="unprotectedinput" onkeypress="onkeypressIncidentI01Panel(this, event)" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@DateStarted")%></td>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsActive","xml:/NWCGIncidentOrder/@IsActive","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N' onclick="setIsActiveModified(this)"/>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Last_Incident_1</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_1</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear"/> 
        </td>
<td class="detaillabel" ><yfc:i18n>Register Interest in ROSS</yfc:i18n></td>
<td  class="checkbox" >
<input type="checkbox" onclick="this.checked=!this.checked"
<%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@RegisterInterestInROSS","xml:/NWCGIncidentOrder/@RegisterInterestInROSS","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Last_Incident_2</yfc:i18n></td>
<td>
<input type="text" size="30" maxLength="40" disabled="disabled" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo2")%>/>
</td>
		<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_2</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" disabled="disabled" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear2")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear2"/> 
        </td>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<!-- Sunjay Gunda: Added Incident Lock Field -->
<tr>
<input type="hidden" name="xml:/NWCGIncidentOrder/@IncidentLocked" value='<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentLocked")%>' />

<td class="detaillabel"><yfc:i18n>Incident_Locked</yfc:i18n></td>
<td class="checkbox" sortable="no">
	<input type="checkbox" onclick="this.checked=!this.checked" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IncidentLocked","xml:/NWCGIncidentOrder/@IncidentLocked","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>

<!-- OG: Added Incident Lock Reason Field -->
<%
	//display reason only if incident is locked
    if (getTextOptions("xml:/NWCGIncidentOrder/@IncidentLocked").indexOf("Value=\"Y\"")>0) {
%>
			<td class="detaillabel" ><yfc:i18n>Lock_Reason</yfc:i18n></td>
			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@LockReason")%>/>
			<td colspan="3" class="protectedtext">Waiting on:<%=resolveValue("xml:/NWCGIncidentOrder/@LockReason")%></td>
<%
	}
//end if 
%>
</tr>
</table>
<%
	}
}
%>

<script language="javascript">
	document.getElementById("xml:/NWCGIncidentOrder/@IncidentTeamType").focus();
	
	function onkeypressIncidentI01Panel(inField, e) {
	    var charCode;
	    
	    if(e && e.which)
	    {
	        charCode = e.which;
	    }
	    else if(window.event)
	    {
	        e = window.event;
	        charCode = e.keyCode;
	    }
	
	    if(charCode == 13) {
			if(yfcFormHasChanged() == true ) {
				var elem = document.getElementsByTagName("input");
				for(i=0;i < elem.length;i++) {
					if(elem[i].type == 'button' && elem[i].value =='Save') {
						yfcCallSave(elem[i]);
						break;
					}
				}
				return 1;
			}		
			else {
				return 0;
			}
		}
	}
</script>
