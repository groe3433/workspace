<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/yfcscripts/yfc.js"></script>
<%
String navInfo = getValue("Order", "xml:/Order/Extn/@ExtnNavInfo");
String shipInstr = getValue("Order", "xml:/Order/Extn/@ExtnShippingInstructions");
boolean canBeModified = isModificationAllowed("xml:/Order/Extn/@ExtnNavInfo", "xml:/Order/AllowedModifications");
String disabledAttr = "";
if (!navInfo.equals("NAV_INST")) {
	disabledAttr = "READONLY";
}
%>

<% if (navInfo.equals("NAV_INST")) {%>
<input type="hidden" name="xml:/Order/Extn/@ExtnNavInfo" value="NAV_INST"/>
<%}%>

<table height="100%" class="view" cellSpacing=0 cellPadding=0>
<tr>
<td/>
</tr>
<tr>
    <td colspan="1" class="detaillabel" <%=disabledAttr%> title="Read only when shipping method is not Shipping Instructions on the Issue.">
        <yfc:i18n>Shipping_Instructions</yfc:i18n>
    </td>
	<% if (navInfo.equals("NAV_INST")) {%>
    <td colspan="1" class="checkboxcolumn"> 
            <input type="checkbox" disabled="disabled" class="protectedinput" checked="checked" />			
    </td>	
	<% } else { %>
    <td colspan="1" class="checkboxcolumn"> 
            <input type="checkbox" disabled="disabled" class="protectedinput"/>			
	</td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
</tr> 

<tr>    
    <td colspan="1" class="detaillabel" <%=disabledAttr%>>
        <yfc:i18n>Information</yfc:i18n>
    </td>
	
	<%if (navInfo.equals("NAV_INST")){%>
		<%if (canBeModified) {%>
		<td colspan="1">		
		<textarea class="unprotectedtextareainput" rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnShippingInstructions" Value="<%=shipInstr%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnShippingInstructions"/></textarea>
		</td>
		<%} else {%>
		<td colspan="1">		
		<textarea class="unprotectedtextareainput" <%=disabledAttr%> rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnShippingInstructions" Value="<%=shipInstr%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnShippingInstructions"/></textarea>
		</td>
		<%}%>
	<% } else { %>
		<td colspan="1">
		<textarea class="unprotectedtextareainput" <%=disabledAttr%> title="Read only when shipping method is not Shipping Instructions on the Issue." rows="5" cols="65" style="word-wrap:break-word;" Name="xml:/Order/Extn/@ExtnShippingInstructions" Value="<%=shipInstr%>"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnShippingInstructions"/></textarea>
		</td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
<tr>

<tr>

    <td colspan="1" class="detaillabel" <%=disabledAttr%>>
        <yfc:i18n>City</yfc:i18n>
    </td>
	<%if (navInfo.equals("NAV_INST")){%>    
		<%if (canBeModified) {%>
		<td colspan="1" align="left" > 
			<input type="text" class="unprotectedinput" size="30" MaxLength="35" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrCity")%>/>
		</td>		
		<%} else {%>
		<td colspan="1" align="left" > 
			<input type="text" class="unprotectedinput" <%=disabledAttr%> size="30" MaxLength="35" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrCity")%>/>
		</td>		
		<%}%>
 	<% } else { %>
 	<td colspan="1" align="left" title="Read only when shipping method is not Shipping Instructions on the Issue." > 
		<input type="text" class="unprotectedinput" <%=disabledAttr%> size="30" MaxLength="35" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrCity")%>/>
    </td>
	<%}%>
	<td>&nbsp;</td>
	<td>&nbsp;</td>		
</tr>

<tr>
    <td class="detaillabel" <%=disabledAttr%>>
        <yfc:i18n>State</yfc:i18n>
    </td>
	<%if (navInfo.equals("NAV_INST")){%>    
		<%if (canBeModified) {%>
		<td colspan="1" align="left" > 
			<!-- CR 1333 - Add (onchange="invokeSave()") - May 20, 2015 -->
			<select onchange="invokeSave()" class="combobox" <%=yfsGetComboOptions("xml:/Order/Extn/@ExtnShipInstrState", "xml:/Order/Extn/@ExtnShipInstrState", "xml:/Order/AllowedModifications")%>>
				<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/Extn/@ExtnShipInstrState" isLocalized="Y"/>
			</select>
			<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrState")%> />					
		</td>		
		<%} else {%>
		<td colspan="1" align="left" > 
			<select disabled="disabled" class="combobox" <%=getComboOptions("xml:/Order/Extn/@ExtnShipInstrState", "xml:/Order/Extn/@ExtnShipInstrState")%>>
				<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/Extn/@ExtnShipInstrState" isLocalized="Y"/>
			</select>
		</td>		
		<%}%>
 	<% } else { %>
    <td colspan="1" align="left" title="Can't be modified unless Shipping Instructions method is set for the Issue"> 
		<select disabled="disabled" class="combobox" <%=getComboOptions("xml:/Order/Extn/@ExtnShipInstrState", "xml:/Order/Extn/@ExtnShipInstrState")%>>
			<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/Extn/@ExtnShipInstrState" isLocalized="Y"/>
		</select>
    </td>
	<%}%>
	<td>&nbsp;</td>	
	<td>&nbsp;</td>		
</tr>
</table>