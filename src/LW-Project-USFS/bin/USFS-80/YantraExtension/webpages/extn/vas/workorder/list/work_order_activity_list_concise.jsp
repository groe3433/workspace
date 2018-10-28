<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
	
	<table  editable="false" class="table">
		<thead>
			<tr>
				<td class="tablecolumnheader">
				</td>
				<td class="tablecolumnheader">
					<yfc:i18n>Work_Order_Activity_Code</yfc:i18n>
				</td>
				<td class="tablecolumnheader">
					<yfc:i18n>Instruction</yfc:i18n>
				</td>
			</tr>
		</thead>
		<tbody>
			<yfc:loopXML binding="xml:/Activities@Activity" id="Activity">
				<tr>
					<td class="tablecolumn">
							<yfc:makeXMLInput  name="activityKey">
								<yfc:makeXMLKey  value="xml:/Activity/@ActivityCode" binding="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/@ActivityCode">
								</yfc:makeXMLKey>
							</yfc:makeXMLInput>
							<img class="icon"  onClick="setLookupValue(this.value)"  value='<%=resolveValue("xml:/Activity/@ActivityCode")%>' <%=getImageOptions(YFSUIBackendConsts.GO_ICON, "Click_to_Select")%>/>
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/Activity/@ActivityCode">
							</yfc:getXMLValue>
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/Activity/@ActivityDescription">
							</yfc:getXMLValue>
					</td>
				</tr>
			</yfc:loopXML>
		</tbody>
	</table>
