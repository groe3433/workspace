<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/currencyutils.jspf" %>
<table class="view" width="100%">
	<tr>
			<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@NodeKey"/></td>
			<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@EnterpriseCode"/></td>
			<td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/></td>

	</tr>
	<tr>			
			<td class="detaillabel" ><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>
			<td class="protectedtext"><%=getComboText("xml:ServiceItemGroupList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/WorkOrder/@ServiceItemGroupCode", true)%>
			</td>
			<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/WorkOrder/Status/@Description"/>
                    <% if (isTrue("xml:/WorkOrder/@HoldFlag") )   { %>
                        <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held")%>/>
    				<% } %>
            </td>
			<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
			<%YFCDocument oDoc = YFCDocument.createDocument("CommonCode");
				YFCElement oTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeType=\"\" CodeValue=\"\" CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
				YFCElement oCommonCode = oDoc.getDocumentElement();
				oCommonCode.setAttribute("CodeType","WORK_ORDER_PURPOSE");
				oCommonCode.setAttribute("CodeValue",resolveValue("xml:/WorkOrder/@Purpose")); %>
				<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=oCommonCode%>" templateElement="<%=oTemplate%>" outputNamespace="PurposeDesc"/>
				<td class="protectedtext">
					<yfc:getXMLValueI18NDB binding="xml:PurposeDesc:/CommonCodeList/CommonCode/@CodeShortDescription"/>	
				</td>
				<Input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
	</tr>
	<tr>
		<td  class="detaillabel" ><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" class="protectedtext">
			<yfc:getXMLValue binding="xml:/WorkOrder/@StartNoEarlierThan"/>
		</td>
		<td  class="detaillabel"><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td nowrap="true" class="protectedtext">
			<yfc:getXMLValue binding="xml:/WorkOrder/@FinishNoLaterThan"/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Service_Item_ID</yfc:i18n></td>
			<td class="protectedtext">
				<yfc:getXMLValue binding="xml:/WorkOrder/@ServiceItemID"/>
			</td>
	</tr>
</table>