<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
    String acrossAllRules = resolveValue("xml:/InventoryInformation/Item/@ConsiderAllNodes");
	String acrossAllSegments = resolveValue("xml:/InventoryInformation/Item/@ConsiderAllSegments");
	String segmentType = resolveValue("xml:/InventoryInformation/Item/@SegmentType");
	String segment = resolveValue("xml:/InventoryInformation/Item/@Segment");
	if (equals(acrossAllSegments,"N")) {
		if ((isVoid(segmentType)) && (isVoid(segment))) {
			acrossAllSegments = " ";
		}
	}
%>

<table width="100%" border="0" cellpadding="0" cellSpacing="7px">
    <tr>
        <td width="50%" height="100%">
            <table class="view" width="100%">
                <tr>
                    <td><input type="hidden" <%=getTextOptions("xml:/InventoryInformation/Item/@ItemID")%> /></td>
                    <td><input type="hidden" <%=getTextOptions("xml:/InventoryInformation/Item/@UnitOfMeasure")%> /></td>
                    <td><input type="hidden" <%=getTextOptions("xml:/InventoryInformation/Item/@ProductClass")%> /></td>
                    <td><input type="hidden" <%=getTextOptions("xml:/InventoryInformation/Item/@OrganizationCode")%> /></td>
                </tr>
				<tr>
					<td class="detaillabel" ><yfc:i18n>Organization_Code</yfc:i18n></td>
                    <td class="protectedtext" >
                        <yfc:getXMLValue binding="xml:/InventoryInformation/Item/@OrganizationCode" name="InventoryInformation"></yfc:getXMLValue>
                    </td>
                    <% if (equals(resolveValue("xml:/InventoryInformation/@ShowEnableSourcing"),"Y")) {%>
                        <td class="protectedtext" colspan="2">
                            <img <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "")%>/>
                            <yfc:i18n>Sourcing_is_currently_disabled_for_this_Node/Item_until</yfc:i18n>
                            <yfc:i18n><%=resolveValue("xml:/InventoryInformation/@ShowUntilDate")%></yfc:i18n>
                        </td>
                    <% } else {%>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    <% } %>
				</tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@ItemID" name="InventoryInformation"></yfc:getXMLValue></td>
                    <td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@ProductClass" name="InventoryInformation"></yfc:getXMLValue></td>
                </tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@UnitOfMeasure" name="InventoryInformation"></yfc:getXMLValue></td>
                    <td class="detaillabel" ><yfc:i18n>Tracked_Everywhere</yfc:i18n></td>
                    <td class="protectedtext"><%=displayFlagAttribute(getValue("InventoryInformation","xml:/InventoryInformation/Item/@TrackedEverywhere"))%></td>
                </tr>
                <tr>
                    <td class="detaillabel"><yfc:i18n>Description</yfc:i18n></td>
                    <td class="protectedtext" colspan="3"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@Description" name="InventoryInformation"></yfc:getXMLValue></td>    
                </tr>
                <tr>    
                    <td class="detaillabel" ><yfc:i18n>Lead_Days</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@LeadTime" name="InventoryInformation"></yfc:getXMLValue></td>
                    <td class="detaillabel" ><yfc:i18n>Processing_Days</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@ProcessingTime" name="InventoryInformation"></yfc:getXMLValue></td>
                </tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Expired_Supply</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InventoryInformation/Item/@TotalExpiredSupply" name="InventoryInformation"></yfc:getXMLValue></td>
                    <% if (!isVoid(getParameter("ShowShipNode"))) {%>
                        <td class="detaillabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
                        <td class="protectedtext">
                            <yfc:getXMLValue binding="xml:/InventoryInformation/Item/@ShipNode" name="InventoryInformation"></yfc:getXMLValue>
							<input type="hidden" <%=getTextOptions("xml:/InventoryInformation/Item/@ShipNode")%> />
                        </td>
                    <%}%>
                </tr>
            </table>
        </td>
        <td width="50%" height="100%" valign="top" style="border:1px ridge black">
                <table class="view" width="100%">                    
					<% if (isVoid(getParameter("ShowShipNode"))) { %>
					<tr>
                        <td>
                            <input type="radio" onclick="setDistributionState('xml:/InventoryInformation/Item/@DistributionRuleId', 'true')" <%=getRadioOptions("xml:/InventoryInformation/Item/@ConsiderAllNodes", "xml:/InventoryInformation/Item/@ConsiderAllNodes", "Y")%>><yfc:i18n>Consider_All_Nodes</yfc:i18n>
                        </td>                   
                        <td nowrap="true">
                            <input type="radio" onclick="setDistributionState('xml:/InventoryInformation/Item/@DistributionRuleId', '')" <%=getRadioOptions("xml:/InventoryInformation/Item/@ConsiderAllNodes", "xml:/InventoryInformation/Item/@ConsiderAllNodes", "N")%>><yfc:i18n>Distribution_Group</yfc:i18n>
						</td>
						<td>
                            <select class="combobox" name="xml:/InventoryInformation/Item/@DistributionRuleId" <%if (equals(acrossAllRules, "Y")) { %> disabled="true" <% } %>>
                                <yfc:loopOptions binding="xml:/DistributionRuleList/@DistributionRule" 
                                    name="Description" value="DistributionRuleId" selected="xml:/InventoryInformation/Item/@DistributionRuleId"/>
                            </select>
                        </td>
					</tr>					
					<%}%>
					<tr>
						<td colspan="3">
							<fieldset>
								<legend><yfc:i18n>Segmentation</yfc:i18n></legend> 
									<table class="view" width="100%">
										<tr>
											<td>
												<input type="radio" onclick="setSegmentState('xml:/InventoryInformation/Item/@SegmentType','xml:/InventoryInformation/Item/@Segment','true')" <%=getRadioOptions("xml:/InventoryInformation/Item/@ConsiderAllSegments", acrossAllSegments, "Y")%>><yfc:i18n>All_Inventory</yfc:i18n>
											</td>
											<td nowrap="true">
												<input type="radio" onclick="setSegmentState('xml:/InventoryInformation/Item/@SegmentType','xml:/InventoryInformation/Item/@Segment','true')" <%=getRadioOptions("xml:/InventoryInformation/Item/@ConsiderAllSegments", acrossAllSegments, " ")%>><yfc:i18n>Unsegmented_Inventory</yfc:i18n>
											</td>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td nowrap="true">
												<input type="radio" onclick="setSegmentState('xml:/InventoryInformation/Item/@SegmentType','xml:/InventoryInformation/Item/@Segment', '')" <%=getRadioOptions("xml:/InventoryInformation/Item/@ConsiderAllSegments", acrossAllSegments, "N")%>><yfc:i18n>Consider_Segment_Type</yfc:i18n>
											</td>
											<td>
												<select <%if (!equals(acrossAllSegments,"N")) { %> disabled="true" <% } %> <%=getComboOptions("xml:/InventoryInformation/Item/@SegmentType")%> class="combobox" >
													<yfc:loopOptions binding="xml:SegmentTypeList:/CommonCodeList/@CommonCode"  name="CodeShortDescription" value="CodeValue" selected="xml:/InventoryInformation/Item/@SegmentType" isLocalized="Y"/>
												</select>
											</td>
											<td class="numericprotectedtext">
												<yfc:i18n>Segment</yfc:i18n>
												<input <%if (!equals(acrossAllSegments,"N")) { %> disabled="true" <% } %> type="text" class="unprotectedinput" <%=getTextOptions("xml:/InventoryInformation/Item/@Segment") %> />
											</td>											
										</tr>
									</table>
							</fieldset>
						</td>
					</tr>
					<tr>                   
						<td class="searchlabel">&nbsp;&nbsp;<yfc:i18n>Horizon_End_Date</yfc:i18n></td>
						<td class="protectedtext" nowrap="true">
							<input type="text" class="dateinput" onkeydown="return checkKeyPress(event)" <%=getTextOptions("xml:/InventoryInformation/Item/@EndDate","xml:/InventoryInformation/Item/@EndDate","")%> />
							<img class="lookupicon"  onclick="invokeCalendar(this);return false"  <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON,"View_Calendar")%> />
							<input type="button" class="button" value="<%=getI18N("GO")%>"  onclick="if(validateControlValues())yfcChangeDetailView(getCurrentViewId())"/>
						</td>
						<td>&nbsp;</td>
					</tr>					
                </table>
        </td>
    </tr>
</table>