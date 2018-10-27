<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>

<script language="Javascript" >

IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
Date now = new Date();
SimpleDateFormat formatDate = 
            new SimpleDateFormat("MM/dd/yyyy");

SimpleDateFormat formatTime = 
            new SimpleDateFormat("HH:mm:ss");

String incidentKeyVal = resolveValue("xml:/NWCGIncidentOrder/@IncidentKey");
String incidentNoVal = resolveValue("xml:/NWCGIncidentOrder/@IncidentNo");

String sLookup = "NWCGCustomerLookUp";
String otherOrderNo = resolveValue("xml:/Order/Extn/@ExtnIncidentNo");
%>


<script language="JavaScript">
function showOtherOrderDetail(){
var incOrder = "" ;

<%
if(incidentNoVal != null && (!incidentNoVal.equals("")))
{%>

incOrder  = '<%=incidentNoVal%>' ;
<%}%>

if(incOrder!=""){
		yfcChangeDetailView('OTHNWCGYOMD050');
	
}

}
window.attachEvent("onload", showOtherOrderDetail);
</script>




<script language="javascript">
<% 
{
if (!isVoid(incidentKeyVal)) {
YFCDocument incidentDoc = YFCDocument.createDocument("NWCGIncidentOrder");
incidentDoc.getDocumentElement().setAttribute("IncidentKey",resolveValue("xml:/NWCGIncidentOrder/@IncidentKey"));

%>


function showIncidentDetail() {
showDetailFor('<%=incidentDoc.getDocumentElement().getString(false)%>');
}
window.attachEvent("onload", showIncidentDetail);
<%				}
}%>
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

function checkForBLM(){
	
	var ownerAgency = document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");

	//if (ownerAgency.value == "BLM")
	//{
		formatAccountCode(document.getElementById("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode"),true,'<%=strFundCode%>','<%=strObjectClass%>');
	//}
}

</script>

<script language="JavaScript">

</script>
<!-- indicator that it is the other order -->
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="Y"/>

<table class="view" width="100%">

<tr>
<td class="detaillabel"><yfc:i18n>Other_Order_Number</yfc:i18n></td>
<td>
<label class="protectedtext"> <%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>
<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%> />
<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentKey")%>/>
<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%> />
<input type="hidden" name="xml:/NWCGIncidentOrder/@OwnerAgency" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>'/>

<% 
String strOwnerAgency=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency");
String strUserNode=resolveValue("xml:CurrentUser:/User/@Node");
%>
</td>

<td class="detaillabel"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="30" maxLength="50" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
</td>

<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>/>
</td>


<td class="detaillabel"><yfc:i18n>Other_Order_Type</yfc:i18n></td>
<td>
<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/NWCGIncidentOrder/@OtherOrderType","NORMAL")%>/>
</td>
</tr>

<tr>

<td class="detaillabel"><yfc:i18n>Customer_PO_No</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerPONo")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<td>
<!-- CR 610 FIX -->
<!-- <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%> id="xml:/NWCGIncidentOrder/@CustomerId" onblur="fetchDataFromServer(this,'getCustShipAddress',updateCustShipAddress);"/> -->
<input type="text" class="unprotectedinput" name="xml:/NWCGIncidentOrder/@CustomerId" dataType="STRING" value="<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>" onblur="makeUppercase(this);fetchDataFromServer(this,'getCustShipAddress',updateCustShipAddress);"/>
<!-- END OF FIX-->
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
<td class="detaillabel"><yfc:i18n>Customer_Name</yfc:i18n></td>
<td>
<label id="xml:/NWCGIncidentOrder/@CustomerName" class="protectedtext" ></label>
<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerName")%>/>
</td>
</tr>
<!-- FBMS elements -->
<tr>
	<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onblur="checkCostCenter(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@CostCenter")%>/></td>
	<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onblur="checkFunctionalArea(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@FunctionalArea")%>/></td>
	<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
	<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onblur="checkWBS(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@WBS")%>/></td>
</tr>
<!-- FBMS elements -->
<tr>

<td class="detaillabel"><yfc:i18n>Fs_Acct_Code_1</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="30" maxLength="40" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" maxLength=4 onBlur="validateOverrideCodeLength(this)" <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="50" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="30" maxLength="40" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%>/>
		 <!-- CR 321 ks 2008-09-29 -->
		<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
		<td>
		<input type="hidden" name="xml:/NWCGIncidentOrder/@DateStarted"/>
		<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/NWCGIncidentOrder/@DateStarted",formatDate.format(now))%>/>
		<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		</td>
		<!-- end of CR 321 -->
<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId","xml:CurrentUser:/User/@Node")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>

</tr>
</table>