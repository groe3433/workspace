<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>

<script language="javascript">



function callAndRefreshPage()
{
	//set the hidden field for the ajax call
	var elem = document.getElementById("xml:/RossInfo/@OrderNo");
	
	if(elem.value != '')
	{
		document.getElementById("xml:/RossInfo/@OrderNumber").value = elem.value;
		document.getElementById("xml:/RossInfo/@MessageSelected").value = document.getElementById("xml:/RossInfo/@MsgType").value
		fetchDataFromServer(document.getElementById("xml:/RossInfo/@OrderNumber"),"getOrderDetails",updateManualROSSOrderInfo);
	}
}

function updateMsgType(elem)
{
	document.getElementById('xml:/RossInfo/@MessageSelected').value=elem.value
	
}
</script>
<%

YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser"); 
String UserNode = curUsr.getAttribute("Node");
YFCElement organizationInput = null;
organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + UserNode + "\" />").getDocumentElement();

YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationCode=\"\" PrimaryEnterpriseKey=\"\" > <Extn /> </Organization> </OrganizationList>").getDocumentElement();
System.out.println("MessageText "+organizationTemplate);
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<table>
<tr>
<td>
	<input type="hidden" name="xml:/RossInfo/@OrderNumber" id="xml:/RossInfo/@OrderNumber" value="<%=resolveValue("xml:/RossInfo/@OrderNo") %>" />
	<input type="hidden" name="xml:/Order/@OrderType" id="xml:/Order/@OrderType" value="<%=resolveValue("xml:/Order/@OrderType") %>" />	
	<input type="hidden" name="xml:/Order/@Order" id="xml:/Order/@OrderType" value="<%=resolveValue("xml:/Order/@OrderType") %>" />	
	<input type="hidden" name="xml:/Order/Extn/@ExtnIncidentNo" id="xml:/Order/Extn/@ExtnIncidentNo" value="<%=resolveValue("xml:/Order/Extn/@ExtnIncidentNo") %>" />	
	<input type="hidden" name="xml:/Order/Extn/@ExtnIncidentYear" id="xml:/Order/Extn/@ExtnIncidentYear" value="<%=resolveValue("xml:/Order/Extn/@ExtnIncidentYear") %>" />	
	<input type="hidden" name="xml:/Order/Extn/@ExtnSystemOfOrigin" id="xml:/Order/Extn/@ExtnSystemOfOrigin" value="<%=resolveValue("xml:/Order/Extn/@ExtnSystemOfOrigin") %>" />		
	
</td>
</tr>
</table>
<table class=table align="left">
<tr>
	<td class="label" >
			<yfc:i18n>Message_Name</yfc:i18n>
	
	  <select name="xml:/RossInfo/@MsgType" id="xml:/RossInfo/@MsgType" value="<%=resolveValue("xml:/RossInfo/@MessageSelected") %>" onchange="updateMsgType(this);" class="combobox" >
	  <option id="xml:/RossInfo/MsgType/@BlankSpace"  <%if(equals(resolveValue("xml:/RossInfo/@MsgType"),"BlankSpace")){%>selected <%}%>  value="BlankSpace"> </option>
	  <option id="xml:/RossInfo/MsgType/@CreateRequest" <%if(equals(resolveValue("xml:/RossInfo/@MsgType"),"CreateRequest")){%>selected <%}%> value="CreateRequest">Create Request</option>
      <option id="xml:/RossInfo/MsgType/@UpdateRequest" <%if(equals(resolveValue("xml:/RossInfo/@MsgType"),"UpdateRequest")){%>selected <%}%> value="UpdateRequest">Update Request(Fill)</option>	    
	  </select>
    </td>
    <td>
    <td class="label" >
		<yfc:i18n>Order_Number</yfc:i18n>
		<input class="textinput" type="unprotectedinput" id="xml:/RossInfo/@OrderNo" <%=getTextOptions("xml:/RossInfo/@OrderNo")%> onblur="callAndRefreshPage();"/>
		<img class="lookupicon" id="lookupicon" onclick="callOrderLookup('xml:/RossInfo/@OrderNo','','0001','orderlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order") %> />
    </td>

</tr>	

</table>
