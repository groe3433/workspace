<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>

<script language="Javascript">
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
Date now = new Date();
SimpleDateFormat formatDate = 
            new SimpleDateFormat("MM/dd/yyyy");

SimpleDateFormat formatTime = 
            new SimpleDateFormat("HH:mm:ss");


// the same jsp being invoked from the deletion of an incident as well as when the incident is created
// when the user hits on the create incident button, we are reloading the same page and if the key exists we are 
// redirecting the control to the details page
// when the user hits on the delete button the API returns the <Junk /> as an output which is placed in the Junk 
// namespace. In the following code we are checking for the same Junk Namespace if there is any xml there 
// it indicates the control is comming from the delete screen and hence keeping the current view

String incidentKeyVal = resolveValue("xml:/NWCGIncidentOrder/@IncidentKey");
String incidentNo = resolveValue("xml:/NWCGIncidentOrder/@IncidentNo");
String strYear = resolveValue("xml:/NWCGIncidentOrder/@IncidentYear");
String iCreateNewIncidentVal = resolveValue("xml:/NWCGIncidentOrder/@iCreateNewIncident");
if(isVoid(strYear)){
	strYear = resolveValue("xml:/NWCGIncidentOrder/@Year");
}
Object obj = request.getAttribute("Junk");
String newCustomerID = resolveValue("xml:/NWCGIncidentOrder/@CustomerId");
String sLookup = "NWCGCustomerLookUp";
%>

<script language="javascript">
<% {
	if (!isVoid(incidentKeyVal))
	{
		YFCDocument incidentDoc = YFCDocument.createDocument("NWCGIncidentOrder");
		incidentDoc.getDocumentElement().setAttribute("IncidentKey",resolveValue("xml:/NWCGIncidentOrder/@IncidentKey"));
		%>
		function showIncidentDetail() 
		{
			<%if(obj == null && isVoid(iCreateNewIncidentVal)) { %>
				showDetailFor('<%=incidentDoc.getDocumentElement().getString(false)%>');
			<%}	else {%>
				yfcChangeDetailView('NWCGYOMD040');
			<%}%>
		}
		window.attachEvent("onload", showIncidentDetail);
	<%}
	if(newCustomerID != null && (!newCustomerID.equals("")))
	{ %>
		function showCustomerDetails()
		{
			fetchDataFromServer(document.getElementById('xml:/NWCGIncidentOrder/@CustomerId'),'getCustShipAddress',updateCustShipAddressFromIncident);
		}
		window.attachEvent("onload", showCustomerDetails);
	<% }
}%>

<!-- set the incident number -->
function setInciNo(){
	var inci_no=document.getElementById('xml:/NWCGIncidentOrder/@IncidentNoState').value;
	inci_no += '-'+ document.getElementById('xml:/NWCGIncidentOrder/@IncidentNoUnit').value;
	inci_no += '-'+ document.getElementById('xml:/NWCGIncidentOrder/@IncidentNoOrder').value;
	document.getElementById('xml:/NWCGIncidentOrder/@IncidentNo').value=inci_no;
}
</script>

<table class="view" width="100%">
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="N"/>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_No</yfc:i18n>
</td>
<% if (isVoid(iCreateNewIncidentVal)){ %>
<td>
<table>
<tr>
<% if(newCustomerID != null && (!newCustomerID.equals(""))) {%>
<td><input type="text" class="unprotectedinput" size="2" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoState")%>/>-<input type="text" class="unprotectedinput" size="6" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoUnit")%>/>-<input type="text" class="unprotectedinput" size="6" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoOrder")%> onblur="javascript:setInciNo();"/></td>
<% } else { %>
<td><input type="text" class="unprotectedinput" size="2" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoState")%> onblur="javascript:setInciNo();"/></td>
<td>-<input type="text" class="unprotectedinput" size="6" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoUnit")%> onblur="javascript:setInciNo();"/></td>
<td>-<input type="text" class="unprotectedinput" size="6" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNoOrder")%> onblur="javascript:setInciNo();"/></td>
<% } %>
</tr>
</table>
<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%> id="xml:/NWCGIncidentOrder/@IncidentNo"/>
<input type="hidden" name="xml:/NWCGIncidentOrder/@OwnerAgency" value='<%=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency")%>'/>

<% 
String strOwnerAgency=resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency");
String strUserNode=resolveValue("xml:CurrentUser:/User/@Node");
%>
</td>
<% } else { %>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
<td class="protectedtext"> <%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%></td>
<% } %>

<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
</td>
<%
if (isVoid(strYear)){
	strYear = new java.text.SimpleDateFormat("yyyy").format(new java.util.Date());
}
%>
<td class="detaillabel"><yfc:i18n>Year</yfc:i18n></td>
<% if (isVoid(iCreateNewIncidentVal)){ %>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year",strYear)%>/>
</td>
<% } else { %>
<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@IncidentYear")%>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year",strYear)%>/>
</td>
<% } %> 
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Customer_ID</yfc:i18n></td>
<td>
<!-- CR 610 FIX -->
<!--<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%> id="xml:/NWCGIncidentOrder/@CustomerId" onblur="fetchDataFromServer(this,'getCustShipAddress',updateCustShipAddressFromIncident);"/>-->
<input type="text" class="unprotectedinput" name="xml:/NWCGIncidentOrder/@CustomerId" dataType="STRING" value="<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>" onblur="makeUppercase(this);fetchDataFromServer(this,'getCustShipAddress',updateCustShipAddressFromIncident);"/>
<!-- end of fix -->
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
<td>
<label class="protectedtext" id="xml:/NWCGIncidentOrder/@CustomerName"><%=resolveValue("xml:/NWCGIncidentOrder/@CustomerName")%></label>
<input type='hidden' <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerName")%> />
</td>
<td class="detaillabel"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentHost")%>/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentType")%>>
<yfc:loopOptions binding="xml:CommonIncidentCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
<td class="detaillabel"><yfc:i18n>Incident_Team_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@IncidentTeamType")%>>
<yfc:loopOptions binding="xml:CommonIncidentTeamList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
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
<td>
<input class="protectedtext" name="xml:/NWCGIncidentOrder/@IncidentSource" value="I"/>
</td>

<td class="detaillabel"><yfc:i18n>Fs_Acct_Code_1</yfc:i18n></td>
<td>
<!-- Removing the formating of FS Acount Code, we dont need it -->
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onBlur="validateOverrideCodeLength(this)"  <%=getTextOptions("xml:/NWCGIncidentOrder/@OverrideCode")%>/>
</td>

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


<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
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
<input type="text" class="unprotectedinput" size="50"<%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%> />
</td>
<!-- Removing the formating of Other Acount Code, we dont need it -->
<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%>/>
</td>

		 <!-- CR 321 ks 2008-09-29 -->
		<td class="detaillabel"><yfc:i18n>Date_Started</yfc:i18n></td>
		<td>
		<input type="hidden" name="xml:/NWCGIncidentOrder/@DateStarted"/>
		<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/NWCGIncidentOrder/@DateStarted",formatDate.format(now))%>/>
		<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		</td>
		<!-- end of CR 321 -->

</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Date_Closed</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@DateClosed")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Shared_Cost</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsCostShared","xml:/NWCGIncidentOrder/@IsCostShared","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
<td class="checkbox" sortable="no">
<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsActive","Y", "xml:/NWCGIncidentOrder/@IsActive")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
</td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Last_Incident_1</yfc:i18n></td>
	<td>
		<input type="text" size="30" maxLength="40" readonly="true" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentNo"/>
		<img class="lookupicon" onclick="callIncidentLookup('xml:/NWCGIncidentOrder/@ReplacedIncidentNo','xml:/NWCGIncidentOrder/@ReplacedIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
	</td>

	<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_1</yfc:i18n></td>
	<td>
		<input type="text" readonly="true" class="unprotectedinput" onBlur="fetchDataWithParams(this,'getIncidentDetails',setDerivedIncident,setIncidentParam(this));" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear")%>  id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear"/> 
	</td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>Last_Incident_2</yfc:i18n></td>
	<td>
		<input type="text" size="30" maxLength="40" readonly="true" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentNo2")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentNo2"/>
	</td>

	<td class="detaillabel" ><yfc:i18n>Last_Incident_Year_2</yfc:i18n></td>
	<td>
		<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/NWCGIncidentOrder/@ReplacedIncidentYear2")%> id="xml:/NWCGIncidentOrder/@ReplacedIncidentYear2"/>
	</td>
</tr>

<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId","xml:CurrentUser:/User/@Node")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
</table>