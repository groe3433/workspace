<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>

<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<%
String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node")) ;
YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");
YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> <BillingPersonInfo/> </Organization>");
if(!strNode.equals(""))
{%>
<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>
<%}%>

<%
String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
%>

<%
YFCDocument inputDoc2 = YFCDocument.parse("<CommonCode CodeType=\"ACCOUNT_CODE\"/>");
YFCDocument templateDoc2 = YFCDocument.parse("<CommonCodeList> <CommonCode/></CommonCodeList>");
if(!strNode.equals(""))
{%>
<yfc:callAPI apiName='getCommonCodeList' inputElement='<%=inputDoc2.getDocumentElement()%>' templateElement='<%=templateDoc2.getDocumentElement()%>' outputNamespace='AccountCodeCommonCodeList'/>
<%}%>

<%
YFCElement elem = (YFCElement) request.getAttribute("AdjustLocationDocumentNumber");
String strDocumentNo = null ;
if(elem != null )
{
	strDocumentNo = elem.getAttribute("DocumentNumber");
}
if(strDocumentNo == null)
	strDocumentNo = "" ;
%>


<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Reference_1</yfc:i18n>
    </td>
    <td>
        <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference1",strDocumentNo)%> />
    </td>
</tr>
<tr>
    <td class="detaillabel" >
		<yfc:i18n>Reference_5</yfc:i18n>
	</td>
    <td>
        <input type="text" class="unprotectedinput"   <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference5", "")%> />
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Reference_2</yfc:i18n>
    </td>
    <td >
        <input type="text" class="unprotectedinput"  onblur="javascript:fetchDataWithParams(this,'getIncidentDetailsInAdjustLocation',populateIncidentDetailsOnAdjustInventory,setAdjustLocationParams(this));" <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference2", "")%> />
		<img class="lookupicon" onclick="callIncidentLookup('xml:/AdjustLocationInventory/Audit/@Reference2','xml:/AdjustLocationInventory/Audit/@Reference5','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
    </td>
</tr>
<%
YFCElement elem2 = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
String strFundCode = "" , strObjectClass = "" ;
if(elem2 != null)
{
	Iterator itr = elem2.getChildren();

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
<tr>
	<yfc:callAPI apiID='AP1'/>
    <td class="detaillabel" >
        <yfc:i18n>Reference_3</yfc:i18n>
    </td>
    <td>
		<%
		if(OwnerAgency.equals("BLM")){ %>
			<input type="text" class="unprotectedinput" size="50" <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference3", "xml:/OrganizationList/Organization/Extn/@ExtnAdjustAcctCode")%> />
		<%} else { %>
			<input type="text" class="unprotectedinput" style="width:<%=getUITableSize("xml:/OrganizationList/Organization/Extn/@ExtnCacheAdjustAcctCode")%>" <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference3", "xml:/OrganizationList/Organization/Extn/@ExtnAdjustAcctCode")%> />
		<% } %>
    </td>
</tr>
<%
//String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
if(OwnerAgency.equals("FS")){
%>
<tr>
    <td class="detaillabel" >
		<yfc:i18n>Override_Code</yfc:i18n>
	</td>
    <td>
        <input type="text" class="unprotectedinput" onBlur="validateFSOverrideCodeLength(this)" <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@OverrideCode","xml:/OrganizationList/Organization/Extn/@ExtnFSOverrideCode")%> />
    </td>
</tr>
<% } %>
<tr>
    <td class="detaillabel" >
		<yfc:i18n>Incident_Name</yfc:i18n>
	</td>
    <td>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/AdjustLocationInventory/Audit/@Reference4", "")%> />
    </td>
</tr>

<input type="hidden" name="xml:/AdjustLocationInventory/Audit/@Reference3" value="<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnAdjustAcctCode")%>" />

<input type="hidden" name="xml:/AdjustLocationInventory/Audit/@OwnerAgency" value="<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>" />

</table>
