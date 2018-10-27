<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/workorder.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td class="tablecolumnheader" sortable="no"></td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Segment_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Segment_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/WorkOrders/@WorkOrder" id="WorkOrder">
            <tr>
                <yfc:makeXMLInput name="WorkOrderKey">
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@NodeKey" value="xml:/WorkOrder/@NodeKey" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@EnterpriseCode" value="xml:/WorkOrder/@EnterpriseCode" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderNo" value="xml:/WorkOrder/@WorkOrderNo" />
                </yfc:makeXMLInput> 
				<% String enterpriseCode = resolveValue("xml:/WorkOrder/@EnterpriseCode");
				 %>                
				<td class="tablecolumn">										
						<img class="icon"  onclick="setWorkOrderLookupValue('<%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%>')"   value='<%=resolveValue("xml:/WorkOrder/@WorkOrderKey")%>' <%=getImageOptions(YFSUIBackendConsts.GO_ICON, "Click_to_Select")%>/>
				</td>	
                <td class="tablecolumn">					
					<yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>     
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@EnterpriseCode"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@ServiceItemGroupCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@ItemID"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Uom"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Priority"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@SegmentType"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Segment"/></td>
                <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/WorkOrder/Status@StatusName"/>
                    <% if (isTrue("xml:/WorkOrder/@HoldFlag") )   { %>
    					<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held")%>/>
    				<% } %>
                </td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>