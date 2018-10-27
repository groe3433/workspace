<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<script language="javascript">

function CheckInput(viewId,chkName)
{
	if (!chkName) {
		chkName="EntityKey";
	}
	var eleArray = document.forms["containerform"].elements;
	var foundChk = false;
	var count=0;
	var sEntityKey;
	for ( var i =0; i < eleArray.length; i++ ) {
		if ( eleArray[i].name == chkName ) {
			foundChk=true;
			if (eleArray[i].checked ) {
				count++;
				sEntityKey=eleArray[i].value;
			}
		}
	}
	if ( foundChk && count >1 ) {
		alert(YFCMSG013); //YFCMSG013="Select only one record for this action";
		document.body.style.cursor='auto';
		return false;
	}

	answer = confirm("Do you really want to Delete this Incident?");

	if (!answer)
	{
		return false;
	}
	
	return true;
}

</script>

<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
<td sortable="no" class="checkboxheader">
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader"><yfc:i18n>Incident_No</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Year</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Source</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/NWCGIncidentOrderList/@NWCGIncidentOrder" id="NWCGIncidentOrder">
<yfc:makeXMLInput name="incidentKey">
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo"/>
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>
<tr>
<yfc:makeXMLInput name="incidentPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>
<td class="checkboxcolumn">
<input type="checkbox" value='<%=getParameter("incidentKey")%>' name="EntityKey" 
PrintEntityKey='<%=getParameter("incidentPrintKey")%>'/>
</td>
<td class="tablecolumn">
<a href="javascript:showDetailFor('<%=getParameter("incidentKey")%>');">
<yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentNo"/>
</a>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@Year"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentHost"/></td>
<td class="tablecolumn">
<%
YFCElement commonCodeInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/NWCGIncidentOrder/@IncidentType") + "\"  />").getDocumentElement();
YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();

%>
<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="CommonCodeList"/>
<!-- displays the value from returned API call -->
<yfc:getXMLValue binding="xml:CommonCodeList:/CommonCodeList/CommonCode/@CodeShortDescription"/>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentSource"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
