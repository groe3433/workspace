<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table class="table" ID="OrderLines" cellspacing="0" width="100%">
    <thead>
        <tr>
            <td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
            <td class="tablecolumnheader"><yfc:i18n>Option_Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>UOM</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/WorkOrder/WorkOrderOptions/@WorkOrderOption" id="WorkOrderOption">
			<tr>
				<yfc:makeXMLInput name="WorkOrderOptionKey" >
					<yfc:makeXMLKey binding="xml:/WorkOrderOption/@WorkOrderOptionKey" value="xml:/WorkOrderOption/@WorkOrderOptionKey" />
				</yfc:makeXMLInput>
				<td class="checkboxcolumn" >
					<input type="checkbox" value='<%=getParameter("WorkOrderOptionKey")%>' name="chkEntityKey" />
				</td>					
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderOption/@OptionItemID"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderOption/@OptionDescription"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderOption/@OptionUom"/></td>
			</tr>
        </yfc:loopXML>
    </tbody>
</table>
