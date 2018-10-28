<%@ include file="/yfsjspcommon/yfsutil_mb.jspf" %>
<%@ include file="/console/jsp/modificationutils_mb.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.net.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools_mb.js"></script>

<%
	String	keyName = null;
	String	keyValue = null;
	String	linkNo = null;
%>
<yfc:loopXML  binding="xml:/Inbox/InboxReferencesList/@InboxReferences" id="InboxReferences"> 
<%
	String referenceType = getValue("InboxReferences","xml:/InboxReferences/@ReferenceType");
	String referenceName = getValue("InboxReferences","xml:/InboxReferences/@Name");
	String referenceValue = getValue("InboxReferences","xml:/InboxReferences/@Value");
%>
<%
    if ("INTEGRATION_ERROR_ID".equals(referenceName)) {
        keyName = referenceName;
        keyValue = referenceValue;
        linkNo = "L08";
%>
        <yfc:makeXMLInput name="linkKey" >
            <yfc:makeXMLKey binding="xml:/IntegrationError/@ErrorTxnId" value="xml:/InboxReferences/@Value"/>
        </yfc:makeXMLInput>
<%
    } else if ("INTEGRATION_ERROR_GROUP_ID".equals(referenceName)) {
        keyName = referenceName;
        keyValue = referenceValue;
        linkNo = "L09";
%>
        <yfc:makeXMLInput name="linkKey" >
            <yfc:makeXMLKey binding="xml:/IntegrationErrorGroup/@ExceptionGroupId" value="xml:/InboxReferences/@Value"/>
        </yfc:makeXMLInput>
<%
	}
%>
</yfc:loopXML> 

<table class="table" width="100%" >
  <thead> 
  <tr> 
    <td class="tablecolumnheader" height="20" style="width:<%=getUITableSize("xml:/InboxReferences/@Name")%>"><yfc:i18n>ReferenceName</yfc:i18n></td>
    <td class="tablecolumnheader" height="20" style="width:<%=getUITableSize("xml:/InboxReferences/@Value")%>"><yfc:i18n>ReferenceValue</yfc:i18n></td>
  </tr>
  </thead> 
  <tbody> 
  
	  <yfc:loopXML  binding="xml:/Inbox/InboxReferencesList/@InboxReferences" id="InboxReferences"> 
		<%
			String referenceType = getValue("InboxReferences","xml:/InboxReferences/@ReferenceType");
			String referenceName = getValue("InboxReferences","xml:/InboxReferences/@Name");
			String referenceValue = getValue("InboxReferences","xml:/InboxReferences/@Value");
		%>
		<%if ( !"COMMENT".equals(referenceType) ) {%>
	  <tr> 
	  <% if(!(linkNo !=null && keyName != null && keyName.equals(referenceName))){ %>
		<td class="tablecolumn"><%=getI18N(referenceName)%></td>
			<%if ( "URL".equalsIgnoreCase(referenceType) ) {%>
				<td class="tablecolumn"><a href='<%=referenceValue%>' ><%=referenceValue%></a></td>
			<%} else {
				if (YFCCommon.equals("WaveKey",referenceName)) {%>
					<yfc:makeXMLInput name="waveKey" >
						<yfc:makeXMLKey binding="xml:/Wave/@WaveKey" value="xml:/InboxReferences/@Value" />
					</yfc:makeXMLInput>	  
					<td class="tablecolumn">
						<a <%=getDetailHrefOptions("L06", " ", getParameter("waveKey"), "")%>>	   
							<yfc:getXMLValue binding="xml:/InboxReferences/@Value"/>
						</a>
					</td>
				<%	} else if (YFCCommon.equals("WorkOrderKey",referenceName)) {%>
					<yfc:makeXMLInput name="WorkOrderKey" >
						<yfc:makeXMLKey binding="xml:/Wave/@WorkOrderKey" value="xml:/InboxReferences/@Value" />
					</yfc:makeXMLInput>	  
					<td class="tablecolumn">
						<a <%=getDetailHrefOptions("L07", " ", getParameter("WorkOrderKey"), "")%>>	   
							<yfc:getXMLValue binding="xml:/InboxReferences/@Value"/>
						</a>
					</td>
				<%	} else { %>
					<td class="tablecolumn"><%=getLocalizedValue(referenceName,referenceValue)%></td>
				<%	}  %>
			<%}%>
			<% } %>
		 </tr>
		<%}%>
	   </yfc:loopXML> 
	  <%
		String orderHeaderKey = getValue("Inbox","xml:/Inbox/@OrderHeaderKey");
		if ( !isVoid(orderHeaderKey) ) {
			String documentType = getValue("Inbox","xml:/Inbox/@OrderDocumentType");
	  %>
	  <tr> 
		<% if ( !isVoid(documentType) ) {%>
			<td class="tablecolumn"><yfc:i18n><%=documentType%>_Order</yfc:i18n></td>
		<% } else { %>
			<td class="tablecolumn"><yfc:i18n>Order</yfc:i18n></td>
		<%}%>
		<td class="tablecolumn">
			<yfc:makeXMLInput name="OrderHeaderKey" >
			    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Inbox/@OrderHeaderKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L01", getValue("Inbox", "xml:/Inbox/@OrderDocumentType"), getParameter("OrderHeaderKey"), "")%>>	   
				<yfc:getXMLValue binding="xml:/Inbox/@OrderNo"/>
            </a>
		</td>
	  </tr>
	  <%}%>
	  <%
		String orderLineKey = getValue("Inbox","xml:/Inbox/@OrderLineKey");
		if ( !isVoid(orderLineKey) ) {
			String documentType = getValue("Inbox","xml:/Inbox/@OrderDocumentType");
	  %>
	  <tr> 
		<% if ( !isVoid(documentType) ) {%>
			<td class="tablecolumn"><yfc:i18n><%=documentType%>_Order_Line</yfc:i18n></td>
		<% } else {%>
			<td class="tablecolumn"><yfc:i18n>Order_Line</yfc:i18n></td>
		<%}%>
		<td class="tablecolumn">
			<yfc:makeXMLInput name="OrderLineKey" >
			    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/Inbox/@OrderLineKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L02", getValue("Inbox", "xml:/Inbox/@OrderDocumentType"), getParameter("OrderLineKey"), "")%>>	   
				<%=orderLineKey %>
            </a>
		</td>
	  </tr>
	  <%}%>
	  <%
		String shipmentNo = getValue("Inbox","xml:/Inbox/@ShipmentNo");
		if ( !isVoid(shipmentNo) ) {
			String documentType = getValue("Inbox","xml:/Inbox/@ShipmentDocumentType");
	  %>
	  <tr> 
		<% if ( !isVoid(documentType) ) {%>
			<td class="tablecolumn"><yfc:i18n><%=documentType%>_Shipment</yfc:i18n></td>
		<% } else { %>
			<td class="tablecolumn"><yfc:i18n>Shipment</yfc:i18n></td>
		<%}%>
		<td class="tablecolumn">
			<yfc:makeXMLInput name="ShipmentKey" >
			    <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Inbox/@ShipmentKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L03", getValue("Inbox", "xml:/Inbox/@ShipmentDocumentType"), getParameter("ShipmentKey"), "")%>>	   
				<yfc:getXMLValue binding="xml:/Inbox/@ShipmentNo"/>
            </a>
		</td>
	  </tr>
	  <%}%>
	  <%
		String loadNo = getValue("Inbox","xml:/Inbox/@LoadNo");
		if ( !isVoid(loadNo) ) {
			String documentType = getValue("Inbox","xml:/Inbox/@LoadDocumentType");
	  %>
	  <tr> 
		<% if ( !isVoid(documentType) ) {%>
			<td class="tablecolumn"><yfc:i18n><%=documentType%>_Load</yfc:i18n></td>
		<% } else {%>
			<td class="tablecolumn"><yfc:i18n>Load</yfc:i18n></td>
		<%}%>
		<td class="tablecolumn">
			<yfc:makeXMLInput name="LoadKey" >
			    <yfc:makeXMLKey binding="xml:/Load/@LoadNo" value="xml:/Inbox/@LoadNo" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L04", getValue("Inbox", "xml:/Inbox/@LoadDocumentType"), getParameter("LoadKey"), "")%>>	   
				<yfc:getXMLValue binding="xml:/Inbox/@LoadNo"/>
            </a>
		</td>
	  </tr>
	  <%}%>
	  <%
		String moveRequestKey = getValue("Inbox","xml:/Inbox/@MoveRequestKey");
		if ( !isVoid(moveRequestKey) ) {
	  %>
	  <tr> 
		<td class="tablecolumn"><yfc:i18n>Move_Request</yfc:i18n></td>
		<td class="tablecolumn">
			<yfc:makeXMLInput name="MoveRequestKey" >
			    <yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/Inbox/@MoveRequestKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L05", " ", getParameter("MoveRequestKey"), "")%>>	   
				<yfc:getXMLValue binding="xml:/Inbox/@MoveRequestKey"/>
            </a>
		</td>
	  </tr>
	  <%}
	if ( linkNo != null ) {%>
		<td class="tablecolumn"><%=keyName%></td>
		<td class="tablecolumn">
            <a <%=getDetailHrefOptions(linkNo, " ", getParameter("linkKey"), "")%>><%=keyValue %></a>
        </td>
  <%}%>
  </tbody> 
</table>
