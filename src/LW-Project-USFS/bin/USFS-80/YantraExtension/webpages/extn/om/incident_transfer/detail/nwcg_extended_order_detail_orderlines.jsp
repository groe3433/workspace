<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_populateItem.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>

<%
	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;
	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
%>

<table class="table" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
	<thead>
		<tr>
			<td class="checkboxheader" sortable="no">
				<input type="hidden" id="userOperation" name="userOperation" value="" />
				<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
				<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
			</td>
			<td class="tablecolumnheader" nowrap="true" style="width:30px"  sortable="no">&nbsp;</td>
			<td class="numerictablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnRequestNo")%>"><yfc:i18n>Request_Number</yfc:i18n></td>
			<td class=tablecolumnheader nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>" sortable="no"><yfc:i18n>PC</yfc:i18n></td> 
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>" sortable="no"><yfc:i18n>UOM</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemDesc")%>" sortable="no"><yfc:i18n>Description</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnTrackableId")%>" sortable="no"><yfc:i18n>Trackable_ID</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@OrderedQty")%>" sortable="no"><yfc:i18n>Line_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>" sortable="no"><yfc:i18n>Amount</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>" sortable="no"><yfc:i18n>Status</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
 		<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="OrderLine">
<%	
			//Set variables to indicate the orderlines dependency situation. 
			boolean isDependentParent = false;
			boolean isDependentChild = false;
			if (equals(getValue("OrderLine","xml:/OrderLine/@ParentOfDependentGroup"),"Y")) {  
				isDependentParent = true;
			}
			if (!isVoid(getValue("OrderLine","xml:/OrderLine/@DependentOnLineKey"))) {
				isDependentChild = true;
			}
			if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {  
				// display line in this inner panel only if item has ItemGroupCode = PROD
				if (equals(getValue("OrderLine","xml:/OrderLine/@ItemGroupCode"),"PROD")) {
					if(bAppendOldValue) {
						String sOrderLineKey = resolveValue("xml:OrderLine:/OrderLine/@OrderLineKey");
						if(oMap.containsKey(sOrderLineKey))
							request.setAttribute("OrigAPIOrderLine",(YFCElement)oMap.get(sOrderLineKey));
					} else 
						request.setAttribute("OrigAPIOrderLine",(YFCElement)pageContext.getAttribute("OrderLine"));
%>
					<tr>
						<yfc:makeXMLInput name="orderLineKey">
							<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
							<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
						</yfc:makeXMLInput>
						<td class="checkboxcolumn">
<%	
							String orderStatus = resolveValue("xml:/Order/@Status");
							if(!orderStatus.equals("Incident Transfer Completed")) { 
%>
								<input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="chkEntityKey" 
<% 
									if (isDependentParent || isDependentChild) {
%> 
										inExistingDependency="true" 
<%
									}
%>
								/>
<%
								// This hidden input is required by yfc to match up each line attribute that is editable in this row against the appropriate order line # on the server side once you save. 
%>
								<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/Order/@OrderHeaderKey")%>' />		
								<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />					
                        		<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%> />
								<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@PrimeLineNo", "xml:/OrderLine/@PrimeLineNo")%> />
								<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@SubLineNo", "xml:/OrderLine/@SubLineNo")%> />
<% 
							}	 
%>
						</td>
						<td class="tablecolumn" nowrap="true">
							<yfc:hasXMLNode binding="xml:/OrderLine/Instructions/Instruction">
								<a <%=getDetailHrefOptions("L01", getParameter("orderLineKey"), "")%>>
									<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTIONS_COLUMN, "Instructions")%>>
								</a>
							</yfc:hasXMLNode>
							<yfc:hasXMLNode binding="xml:/OrderLine/KitLines/KitLine">
								<a <%=getDetailHrefOptions("L02", getParameter("orderLineKey"), "")%>>
									<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Kit_Components")%>>
								</a>
							</yfc:hasXMLNode>
							<yfc:makeXMLInput name="orderLineKey">
								<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
								<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
							</yfc:makeXMLInput>
						</td>
						<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
<% 
							if(showOrderLineNo("Order","Order")) {
%>
								<a <%=getDetailHrefOptions("L03", getParameter("orderLineKey"), "")%>>
									<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
								</a>
<%
							} else {
%>
								<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
<%
							}
%>
						</td>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnRequestNo"/></td>
						<td class="tablecolumn" sortValue="<%=resolveValue("xml:/OrderLine/Item/@ItemID")%>"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td> 
						<td class="tablecolumn">
                        	<yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/>
                        	<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/OrderLineTranQuantity/@TransactionalUOM", "xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%> />
                   		</td>
						<td class="tablecolumn"><%=getLocalizedOrderLineDescription("OrderLine")%></td>
	                    <td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnTrackableId"/>                       
            	        </td>    
						<td class="numerictablecolumn">
                    		<input type="text" readonly <%if(bAppendOldValue) { %> OldValue="<%=resolveValue("xml:OrigAPIOrderLine:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/AllowedModifications")%> style='width:40px' title='<%=getI18N("Open_Qty")%>: <%=getValue("OrderLine", "xml:/OrderLine/OrderLineTranQuantity/@OpenQty")%>'/>
						</td>
						<td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/LineOverallTotals/@LineTotal")%>">
							<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
						</td>
						<td class="tablecolumn">
							<a <%=getDetailHrefOptions("L04", getParameter("orderLineKey"),"ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"))%></a>
						</td>
					</tr>
<%
				}
            } else if(isVoid(resolveValue("xml:OrderLine:/OrderLine/@OrderLineKey"))) {
%>
				<tr DeleteRowIndex="<%=OrderLineCounter%>">
					<td class="checkboxcolumn"> 
						<img class="icon" onclick="setDeleteOperationForRow(this,'xml:/Order/OrderLines/OrderLine')" <%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%>/>
					</td>
                    <td class="tablecolumn">
						<input type="hidden" OldValue="" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/@Action", "xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/@Action", "CREATE")%> />
						<input type="hidden"  <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/@DeleteRow",  "")%> />
                    </td>
                    <td class="tablecolumn">&nbsp;</td>
					<td class="tablecolumn" nowrap="true">
						<input type="text" OldValue="" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/Extn/@ExtnRequestNo","xml:/OrderLine/Extn/@ExtnRequestNo","xml:/Order/AllowedModifications","ADD_LINE","text")%> />
					</td>
                    <td class="tablecolumn" nowrap="true">
						<input type="text" OldValue="" onBlur="fetchDataWithParams(this,'getItemList',populateIncidentItemDetails,setItemParam(this));"   
							<%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/Item/@ItemID","xml:/OrderLine/Item/@ItemID","xml:/Order/AllowedModifications","ADD_LINE","text")%> />		
						<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','TransactionalUOM','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%> />
                    </td>
                    <td class="tablecolumn">
		                <select OldValue="" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/Item/@ProductClass","xml:/OrderLine/Item/@ProductClass", "xml:/Order/AllowedModifications","ADD_LINE","combo")%>>
                            <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue" value="CodeValue" selected="xml:/OrderLine/Item/@ProductClass"/>
                        </select>
                    </td>
                    <td class="tablecolumn">
		                <select OldValue="" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/OrderLineTranQuantity/@TransactionalUOM","xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM","xml:/Order/AllowedModifications","ADD_LINE","combo")%>>
                           <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/>
                        </select>
                    </td>
                    <td class="tablecolumn"><label value=""/></td>
                    <td class="tablecolumn">
                        <yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnTrackableId"/>
                        <input 	style="visibility:hidden" class=unprotectedoverrideinput type="text" 
                        		onBlur="if(this.value != ''){
											if(!validateLocally(this,'Extn/@ExtnTrackableId')){
												alert('Duplicate Entry');
												this.focus();
												return false;
											}
										};
										validTrackID(this);
										fetchDataWithParams(this,'getSerialList',checkSerialExists,setSerialNumber(this),false);
										fetchDataWithParams(this,'NWCGGetTrackableItemListService',validateTrackableID,setSerialNumber(this))" 
								name='xml:/Order/OrderLines/OrderLine_<%=OrderLineCounter%>/Extn/@ExtnTrackableId' 
								value='<%=resolveValue("xml:/OrderLine/Extn/@ExtnTrackableId")%>' OldValue="" maxLength=50 size=10/>
                    </td>
                    <td class="numerictablecolumn">
						<input type="text" OldValue="" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine_"+OrderLineCounter+"/OrderLineTranQuantity/@OrderedQty","xml:/OrderLine/OrderLineTranQuantity/@OrderedQty","xml:/Order/AllowedModifications","ADD_LINE","text")%> style='width:40px'/>
                    </td>
                    <td class="tablecolumn">&nbsp;</td>
                    <td class="tablecolumn">&nbsp;</td>
                </tr>
<%
			}
%>
        </yfc:loopXML>
    </tbody>
    <tfoot>        
<%
		if (isModificationAllowed("xml:/@AddLine","xml:/Order/AllowedModifications")) { 
%>
			<tr>          
<%
				String orderStatus = resolveValue("xml:/Order/@Status");
				if(!orderStatus.equals("Incident Transfer Completed")) { 
%>
        			<td nowrap="true" colspan="13">
        				<jsp:include page="/common/editabletbl.jsp" flush="true">
						<jsp:param name="ReloadOnAddLine" value="Y"/>
        				</jsp:include>
        			</td>
<% 
				} 
%>
			</tr>
<%
		}
%>
    </tfoot>
</table>