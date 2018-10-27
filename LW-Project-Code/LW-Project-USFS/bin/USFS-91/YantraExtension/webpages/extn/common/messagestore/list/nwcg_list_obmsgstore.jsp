<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<yfc:callAPI apiID="AP2"/>
<%
	YFCElement root = getElement("OB");
	int countElem = countChildElements(root);
%>
<script language="javascript">
	setRetrievedRecordCount(<%=countElem%>);
</script>
<table class="table" editable="false" width="100%" cellspacing="0">
	<thead> 
		<tr>
			<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
			<td class="tablecolumnheader"><yfc:i18n>Distribution_ID</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Initial_Message_Sent</yfc:i18n></td>                          
			<td class="tablecolumnheader"><yfc:i18n>Message_Type</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Message_Status</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Message_Name</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Entity_Value</yfc:i18n></td>
		</tr>   
	</thead>
   	<tbody>
		<yfc:loopXML binding="xml:OB:/NWCGOutboundMessageList/@NWCGOutboundMessage" id="NWCGOutboundMessage">
			<tr>
				<yfc:makeXMLInput name="messageKey">
					<yfc:makeXMLKey binding="xml:OB:/NWCGOutboundMessage/@MessageKey" value="xml:/NWCGOutboundMessage/@MessageKey" />
				</yfc:makeXMLInput>                
				<td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("messageKey")%>' name="EntityKey" /></td>
				<td class="tablecolumn">
					<a href="javascript:showDetailForViewGroupId('NWCG_Message_Store','MSGSTRD020','<%=getParameter("messageKey")%>');">
						<yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@DistributionID"/>
					</a>
				</td>
<%
				//Code to display date and time of createts
				String strDate = NWCGOutboundMessage.getAttribute("Createts");
				String pattern1 = "yyyy-MM-dd'T'HH:mm:ss";
				String pattern2 = "MM/dd/yyyy  HH:mm:ss";
				java.text.SimpleDateFormat sdfYantra = new java.text.SimpleDateFormat(pattern1);
				java.text.SimpleDateFormat sdfUI = new java.text.SimpleDateFormat(pattern2);
				Date dateYantra = sdfYantra.parse(strDate);
				String strDateUI = sdfUI.format(dateYantra);
%>
				<td class="tablecolumn" sortValue="<%=strDateUI%>"><%=strDateUI%></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageType"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageStatus"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageName"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@EntityValue"/></td>
			</tr>
		</yfc:loopXML>
	</tbody>
</table>