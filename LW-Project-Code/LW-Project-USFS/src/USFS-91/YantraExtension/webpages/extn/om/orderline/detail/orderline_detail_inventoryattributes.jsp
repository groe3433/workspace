<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%
String hasTagAttributes = "N";
YFCElement itemDetailsElem = (YFCElement) request.getAttribute("ItemDetails");
if (itemDetailsElem != null) {
YFCElement inventoryTagAttributesElem = itemDetailsElem.getChildElement("InventoryTagAttributes");
if (inventoryTagAttributesElem != null) {
hasTagAttributes = "Y";
}
}

boolean isSerialTracked = false;
String serialTracked = resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerializedFlag");
if (equals("Y", serialTracked)) {
isSerialTracked = true;
}
%>

<table width="100%" class="view">
<tr>
<td class="detaillabel" >
<yfc:i18n>Segment_Type</yfc:i18n>
</td>
<td nowrap="true">
<select class="combobox" disabled="true" <%=getComboOptions("xml:/Order/OrderLines/OrderLine/@SegmentType", "xml:/OrderLine/@SegmentType")%>>
<yfc:loopOptions binding="xml:SegmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@SegmentType" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@SegmentType"/>
</select>
</td>
<td class="detaillabel" >
<yfc:i18n>Segment</yfc:i18n>
</td>
<td class="protectedtext">
<yfc:getXMLValue binding="xml:/OrderLine/@Segment"/>
<input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey", "xml:/OrderLine/Order/@OrderHeaderKey")%>/>
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%>/>
</td>
<% if (isSerialTracked) { %>
<td class="detaillabel" >
<yfc:i18n>Serial_#</yfc:i18n>
</td>
<td nowrap="true">
<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@SerialNo","xml:/OrderLine/@SerialNo","xml:/OrderLine/AllowedModifications")%> />
</td>
<% } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
<% } %>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<% if(equals(hasTagAttributes,"Y")) { %>
<tr>
<td colspan="8">
<jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
<jsp:param name="TagContainer" value="OrderLine"/>
<jsp:param name="TagElement" value="OrderLineInvAttRequest"/>
<jsp:param name="Modifiable" value='true'/>
<jsp:param name="BindingPrefix" value='xml:/OrderLine/OrderLineInvAttRequest'/>
<jsp:param name="TargetBindingPrefix" value='xml:/Order/OrderLines/OrderLine/OrderLineInvAttRequest'/>
<jsp:param name="AllowModBinding" value='xml:/OrderLine/AllowedModifications'/>
</jsp:include>
</td>
</tr>
<% } %>
</table>
