<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<script>
yfcDoNotPromptForChanges(true);
</script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>


<%@ page import="com.yantra.yfc.core.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/yfcscripts/yfc.js"></script>

<%
String strRenderReadOnly = (String)request.getParameter("RenderReadOnly") ; 
boolean bRenderReadOnly = ((strRenderReadOnly != null && strRenderReadOnly.equals("true")) ? true:false );
String strPath = (String)request.getParameter("Path") ;
String driverDate = getValue("Order", "xml:/Order/@DriverDate");
String navInfo = getValue("Order", "xml:/Order/Extn/@ExtnNavInfo");
String sRequestDOM = request.getParameter("getRequestDOM");
String modifyView = request.getParameter("ModifyView");
modifyView = modifyView == null ? "" : modifyView;
boolean canBeModified = isModificationAllowed("xml:/Order/Extn/@ExtnNavInfo", "xml:/Order/AllowedModifications");

String disabledAttr = "";
if (!navInfo.equals("WILL_PICK_UP")) {
	disabledAttr = "READONLY";
}
String willPickUpInfo = getValue("Order", "xml:/Order/Extn/@ExtnWillPickUpInfo");
%>

<% if (navInfo.equals("WILL_PICK_UP")) {%>
<input type="hidden" name="xml:/Order/Extn/@ExtnNavInfo" value="WILL_PICK_UP"/>
<%}%>

<table class="view" cellSpacing="0" cellPadding="0" height="80%">
<tr>
<td/><td/>
</tr>
<tr>
    <td class="detaillabel" <%=disabledAttr%> title="Read only when shipping method is not Will Pick Up on the Issue.">
        <yfc:i18n>Will_Pick_Up</yfc:i18n>
    </td>

	<% if (navInfo.equals("WILL_PICK_UP")) {%>
    <td class="checkboxcolumn"> 
            <input type="checkbox" disabled="disabled" class="protectedinput" checked="checked" />			
    </td>	
	<% } else { %>
    <td class="checkboxcolumn" title="Read only when shipping method is not Will Pick Up on the Issue."> 
            <input type="checkbox" disabled="disabled" class="protectedinput"/>			
	</td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
</tr> 

<tr>    
    <td class="detaillabel" <%=disabledAttr%>>
        <yfc:i18n>Name</yfc:i18n>
    </td>
	<% if (navInfo.equals("WILL_PICK_UP")) {%>
		<% if(canBeModified) {%>
		<td align="left">
			<input size="65" maxlength="100" type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnWillPickUpName")%> size="100"/>
		</td>
		<%}else{%>
		<td align="left">
			<input size="65" maxlength="100" type="text" class="protectedinput" <%=disabledAttr%> <%=getTextOptions("xml:/Order/Extn/@ExtnWillPickUpName")%> size="100"/>
		</td>
		<% } %>
		<% } else { %>
    <td align="left" title="Read only when shipping method is not Will Pick Up on the Issue."> 
            <input <%=disabledAttr%> size="65" maxlength="100" type="text" class="protectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnWillPickUpName")%> size="100"/>			
	</td>
		<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
</tr>

<tr>    
    <td class="detaillabel" <%=disabledAttr%>>
        <yfc:i18n>Information</yfc:i18n>
    </td>

	<% if (navInfo.equals("WILL_PICK_UP")) {%>
		<%if(canBeModified){%>
		<td align="left" width="100%">
			<textarea class="unprotectedtextareainput" rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnWillPickUpInfo" Value="<%=willPickUpInfo%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnWillPickUpInfo"/></textarea>
		</td>
		<%}else{%>
		<td align="left" width="100%">
			<textarea class="unprotectedtextareainput" disabled="disabled" rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnWillPickUpInfo" Value="<%=willPickUpInfo%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnWillPickUpInfo"/></textarea>
		</td>
		<%}%>
	<% } else { %>
		<td width="100%" title="Read only when shipping method is not Will Pick Up on the Issue.">
		<textarea class="unprotectedtextareainput" <%=disabledAttr%> rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnWillPickUpInfo" Value="<%=willPickUpInfo%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnWillPickUpInfo"/></textarea>
		</td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>			
</tr>
<tr>    

	<td class="detaillabel" <%=disabledAttr%>><yfc:i18n>Time</yfc:i18n></td>

	<%if (navInfo.equals("WILL_PICK_UP")){%>
		<%if(canBeModified){%>
		<td>
			<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
			<input type="text" class="unprotectedinput" onBlur="setRequestDeliverDate(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
			<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
			<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
		</td>
		<%}else{%>
		<td>
			<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
			<input type="text" class="unprotectedinput" <%=disabledAttr%> <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
			<img class="lookupicon" disabled="disabled" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			<input type="text" class="unprotectedinput" <%=disabledAttr%> <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
			<img class="lookupicon" disabled="disabled" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
		</td>
		<%}%>
	<% } else { %>
		<td width="100%" title="Read only when shipping method is not Will Pick Up on the Issue.">
			<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
			<input type="text" title="Read only when shipping method is not Will Pick Up on the Issue." class="unprotectedinput" <%=disabledAttr%> <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
			<img class="lookupicon" disabled="disabled" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			<input type="text" title="Read only when shipping method is not Will Pick Up on the Issue." class="unprotectedinput" <%=disabledAttr%> <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
			<img class="lookupicon"  disabled="disabled" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
		</td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
</tr>
</table>
