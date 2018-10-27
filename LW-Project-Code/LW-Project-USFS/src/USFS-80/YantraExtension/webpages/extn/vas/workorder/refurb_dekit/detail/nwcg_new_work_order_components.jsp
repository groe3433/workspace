<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
	function refreshPageAnddelete(key)
	{
		var eleArray = document.forms["containerform"].elements;
		for ( var i = 0; i < eleArray.length; i++ ) {
			if ( eleArray[i].name == key ) {

				if (eleArray[i].checked) {
					var counterValue = eleArray[i].getAttribute('yfcMultiSelectCounter');
					var multiInputValue = eleArray[i].getAttribute('yfcMultiSelectValue1');
					var name="xml:/WorkOrder/Deleted/@Item_"+multiInputValue;
					var hiddenKeyInput = document.createElement("<INPUT type='hidden' name='" + name + "'>");
					hiddenKeyInput.value = "Y";
					eleArray.appendChild(hiddenKeyInput);
				}
			}
		}
		yfcChangeDetailView(getCurrentViewId());
	}
</script>

<%
    String sSOME_TIME_TAG_CTRL = YCMConstants.YCM_ITEM_TAG_SOMETIMES_TAG_CONTROLLED;
    String sTAG_CTRL = YCMConstants.YFS_YES;

	YFCElement workOrder = (YFCElement) request.getAttribute("WorkOrder");
	int tfootCounter = 1;
	String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;
	if(modifyView == ""){
%>
		<yfc:callAPI apiID='AP4'/>
<%
	}
%>
<%String className="oddrow";%>

<div style="height:250px;width:485px;overflow:auto">
<table class="table" ID="WorkOrderComponents" cellspacing="0" width="100%">
    <thead>
        <tr>
			<% if(modifyView == ""){%>
				<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
			<%}%>
            <td class="tablecolumnheader"><yfc:i18n>Tag_Details</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderComponent/Item/PrimaryInformation/@Description")%>"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Total_Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
	<% if(modifyView != ""){
		%>
			<yfc:loopXML binding="xml:/WorkOrder/WorkOrderComponents/@WorkOrderComponent" id="WorkOrderComponent">
				<%if (equals("oddrow",className))
						className="evenrow";
					else
						className="oddrow";
					boolean eligibleItem=false;%>

					<yfc:hasXMLNode binding="xml:/WorkOrderComponent/WorkOrderComponentTag">
						<%if(!isVoid(resolveValue("xml:/WorkOrderComponent/@ItemID")) && 			!isVoid(resolveValue("xml:/WorkOrderComponent/@Uom"))){
							workOrder.setAttribute("CurrentComponentItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"));
							workOrder.setAttribute("CurrentComponentUom", resolveValue("xml:/WorkOrderComponent/@Uom"));
							%>
								<yfc:callAPI apiID='AP4'/>
							<%
							eligibleItem=true;
						}%>
					</yfc:hasXMLNode>
				<tr>
					<td>
<%
							if(eligibleItem){
							String tagControlFlag = resolveValue("xml:ComponentTagDetails:/Item/InventoryParameters/@TagControlFlag");

							if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){
								YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\" ItemKey=\"\" TagCapturedInShipping=\"\"><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/><InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item>").getDocumentElement();
								YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement();
								oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/WorkOrder/@EnterpriseCode"));
								oItemDetailsElement.setAttribute("Node",resolveValue("xml:/WorkOrder/@NodeKey"));

								oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"));
								oItemDetailsElement.setAttribute("ProductClass",resolveValue("xml:/WorkOrderComponent/@ProductClass"));					oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/WorkOrderComponent/@Uom"));
%>
								<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>
<%
								tagControlFlag = getValue("ItemDetails","xml:/Item/@TagCapturedInInventory");
							}

							if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){

%>
								<img onclick="expandCollapseDetails('componentOptionSet_<%=WorkOrderComponentCounter%>','<%=getI18N("Click_To_See_Tag_Info")%>','<%=getI18N("Click_To_Hide_Tag_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Tag_Info")%> />
							<%}
						}%>
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/@ProductClass"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/@Uom"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/@ComponentQuantity"/></td>
					<%
					  String qtyPerKit = resolveValue("xml:/WorkOrderComponent/@ComponentQuantity");
					  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
					  float fQtyPerKit = 0;
					  if (qtyPerKit != null && qtyPerKit.length() > 0){
						//CR1562
						  if(qtyPerKit.length() > 3)
						  fQtyPerKit = Float.parseFloat(qtyPerKit.replace(",", ""));
							else
						fQtyPerKit = (new Float(qtyPerKit)).floatValue();
					  }

					  float fReqQty = 0;
					  if (reqQty != null && reqQty.length() > 0){
						// CR1562
							if(reqQty.length() > 3)
						  fReqQty = Float.parseFloat(reqQty.replace(",", ""));
							else
						fReqQty = (new Float(reqQty)).floatValue();
					  }
					  float total = fQtyPerKit * fReqQty;
					%>
			        <td class="tablecolumn"><%=total%></td>
					<%String totBind="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+WorkOrderComponentCounter+"/WorkOrderComponentTag";
							%>
						<tr id='componentOptionSet_<%=WorkOrderComponentCounter%>' class='<%=className%>' style="display:none">
							<td colspan="9" >
								<jsp:include page="/vas/workorder/detail/vas_detail_includetag.jsp" flush="true">
									<jsp:param name="componentOptionSetBelongingToLine" value='<%=String.valueOf(WorkOrderComponentCounter)%>'/>
									<jsp:param name="TotalBinding" value='<%=totBind%>'/>
								</jsp:include>
							</td>
						</tr>
				</tr>
			</yfc:loopXML>
		<%}else if(equals("Y",resolveValue("xml:/WorkOrder/@comingThroughDOM"))){
			Hashtable componentUniqueKeys = new Hashtable();%>
			<yfc:loopXML binding="xml:/WorkOrder/WorkOrderComponents/@WorkOrderComponent" id="WorkOrderComponent">
				<%
				String binding="xml:/WorkOrder/Deleted/@Item_"+resolveValue("xml:/WorkOrderComponent/@ItemID")+resolveValue("xml:/WorkOrderComponent/@Uom");
				String componentUniqueKey = resolveValue("xml:/WorkOrderComponent/@ItemID")+resolveValue("xml:/WorkOrderComponent/@Uom");
				boolean eligibleItem=false;
				tfootCounter++;
				if (equals("oddrow",className))
						className="evenrow";
					else
						className="oddrow";
				if(!(equals("Y",resolveValue(binding))) || equals("Y",resolveValue("xml:/WorkOrderComponent/@New"))){
					if(!isVoid(resolveValue("xml:/WorkOrderComponent/@ItemID")) && !componentUniqueKeys.containsValue(componentUniqueKey)){
						componentUniqueKeys.put(componentUniqueKey, componentUniqueKey);
						if(!isVoid(resolveValue("xml:/WorkOrderComponent/@Uom"))){
							workOrder.setAttribute("CurrentComponentItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"));
							workOrder.setAttribute("CurrentComponentUom", resolveValue("xml:/WorkOrderComponent/@Uom"));
							%>
								<yfc:callAPI apiID='AP5'/>
							<%
								eligibleItem=true;
							}
						%>
						<tr>
							<yfc:makeXMLInput name="WorkOrderComponentKey" >
								<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent/@WorkOrderComponentKey" value="xml:/WorkOrderComponent/@WorkOrderComponentKey" />
							</yfc:makeXMLInput>
							<td class="checkboxcolumn" >
								<input type="checkbox" value='<%=WorkOrderComponentCounter%>' name="chkEntityKey" 	yfcMultiSelectCounter='<%=WorkOrderComponentCounter%>' yfcMultiSelectValue1='<%=componentUniqueKey%>'/>
							</td>
							<td>
							<% if(eligibleItem){
								String tagControlFlag = resolveValue("xml:ComponentTagDetails:/Item/InventoryParameters/@TagControlFlag");

								if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){
								YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\" ItemKey=\"\" TagCapturedInShipping=\"\"><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/><InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item>").getDocumentElement();
								YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement();
								oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/WorkOrder/@EnterpriseCode"));
								oItemDetailsElement.setAttribute("Node",resolveValue("xml:/WorkOrder/@NodeKey"));

								oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"));
								oItemDetailsElement.setAttribute("ProductClass",resolveValue("xml:/WorkOrderComponent/@ProductClass"));					oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/WorkOrderComponent/@Uom"));
%>
								<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>
<%
								tagControlFlag = getValue("ItemDetails","xml:/Item/@TagCapturedInInventory");
							}
								if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){%>
									<img onclick="expandCollapseDetails('componentOptionSet_<%=WorkOrderComponentCounter%>','<%=getI18N("Click_To_See_Tag_Info")%>','<%=getI18N("Click_To_Hide_Tag_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Tag_Info")%> />
								<%}
							}%>
							</td>
							<td><yfc:getXMLValue binding="xml:/WorkOrderComponent/@ItemID"/></td>
							<td><yfc:getXMLValue binding="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
							<td nowrap="true" class="tablecolumn">
								<select class="combobox"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+WorkOrderComponentCounter+"/@ProductClass")%> >
								<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
									name="CodeValue" value="CodeValue" selected="xml:/WorkOrderComponent/@ProductClass"/>
								</select>
							</td>
							<td><yfc:getXMLValue binding="xml:/WorkOrderComponent/@Uom"/></td>
							<td nowrap="true" class="tablecolumn">
								<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+WorkOrderComponentCounter+"/@ComponentQuantity","xml:/WorkOrderComponent/@ComponentQuantity")%>/>
							</td>
							<%
							  String qtyPerKit = resolveValue("xml:/Component/@KitQuantity");
							  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
							  float fQtyPerKit = 0;
							  if (qtyPerKit != null && qtyPerKit.length() > 0){
								//CR1562
								  if(qtyPerKit.length() > 3)
								  fQtyPerKit = Float.parseFloat(qtyPerKit.replace(",", ""));
									else
								fQtyPerKit = (new Float(qtyPerKit)).floatValue();
							  }

							  float fReqQty = 0;
							  if (reqQty != null && reqQty.length() > 0){
								// CR1562
									if(reqQty.length() > 3)
								  fReqQty = Float.parseFloat(reqQty.replace(",", ""));
									else
								fReqQty = (new Float(reqQty)).floatValue();
							  }
							  float total = fQtyPerKit * fReqQty;
							%>
							<td class="tablecolumn"><%=total%></td>

							<%String totBind="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ WorkOrderComponentCounter+"/WorkOrderComponentTag";%>

							<tr id='componentOptionSet_<%=WorkOrderComponentCounter%>' class='<%=className%>' style="display:none">
								<td colspan="8" >
									<jsp:include page="/vas/workorder/detail/vas_detail_includetag.jsp" flush="true">
										<jsp:param name="componentOptionSetBelongingToLine" value='<%=String.valueOf(WorkOrderComponentCounter)%>'/>
										<jsp:param name="TotalBinding" value='<%=totBind%>'/>
									</jsp:include>
								</td>
							</tr>
						</tr>
						<input type="hidden" 	<%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+WorkOrderComponentCounter+"/@ItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"))%>/>
						<input type="hidden" 	<%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+WorkOrderComponentCounter+"/@Uom",resolveValue("xml:/WorkOrderComponent/@Uom"))%> />
					<%}
				}else{
					Object temp = componentUniqueKeys.remove(componentUniqueKey);%>
					<input type="hidden" 	<%=getTextOptions(binding,resolveValue(binding))%> />
				<%}%>
			</yfc:loopXML>
		<%}else{%>
			<yfc:loopXML binding="xml:WorkOrderComponentList:/Item/Components/@Component" id="Component">
				<%
				String binding="xml:/WorkOrder/Deleted/@Item_" + resolveValue("xml:/Component/@ComponentItemID") + resolveValue("xml:/Component/@ComponentUnitOfMeasure") ;
				boolean eligibleItem=false;
				tfootCounter++;
				if (equals("oddrow",className))
						className="evenrow";
					else
						className="oddrow";
				%>
				<!-- We need to get the description of the item, so moving it from if loop to above -->
				<yfc:callAPI apiID='AP5'/>

				<%
				if(!(equals("Y",resolveValue(binding)))){
					if(!isVoid(resolveValue("xml:/Component/@ComponentItemID")) && 			!isVoid(resolveValue("xml:/Component/@ComponentUnitOfMeasure"))){
					workOrder.setAttribute("CurrentComponentItemID",resolveValue("xml:/Component/@ComponentItemID"));
					workOrder.setAttribute("CurrentComponentUom", resolveValue("xml:/Component/@ComponentUnitOfMeasure"));
					%>
						<!--<yfc:callAPI apiID='AP5'/>-->
					<%
						eligibleItem=true;
					}
					String checkBoxKey = resolveValue("xml:/Component/@ComponentItemID") + resolveValue("xml:/Component/@ComponentUnitOfMeasure");
					%>
					<tr>
						<yfc:makeXMLInput name="ComponentKey" >
							<yfc:makeXMLKey binding="xml:WorkOrderComponentList:/Item/Components/Component/@ItemKey" value="xml:/WorkOrderComponent/@ItemKey" />
						</yfc:makeXMLInput>
						<td class="checkboxcolumn" >
							<input type="checkbox" value='<%=getParameter("ComponentKey")%>' name="chkEntityKey" 	yfcMultiSelectCounter='<%=ComponentCounter%>'
								yfcMultiSelectValue1='<%=checkBoxKey%>'/>
						</td>
						<td>
						<% if(eligibleItem){
							String tagControlFlag = resolveValue("xml:ComponentTagDetails:/Item/InventoryParameters/@TagControlFlag");

							if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){
								YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\" ItemKey=\"\" TagCapturedInShipping=\"\"><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/><InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item>").getDocumentElement();
								YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement();
								oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/WorkOrder/@EnterpriseCode"));
								oItemDetailsElement.setAttribute("Node",resolveValue("xml:/WorkOrder/@NodeKey"));

								oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/WorkOrderComponent/@ItemID"));
								oItemDetailsElement.setAttribute("ProductClass",resolveValue("xml:/WorkOrderComponent/@ProductClass"));					oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/WorkOrderComponent/@Uom"));
%>
								<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>
<%
								tagControlFlag = getValue("ItemDetails","xml:/Item/@TagCapturedInInventory");
							}
							if(equals(sSOME_TIME_TAG_CTRL, tagControlFlag) || equals(sTAG_CTRL, tagControlFlag)){%>
								<img onclick="expandCollapseDetails('componentOptionSet_<%=ComponentCounter%>','<%=getI18N("Click_To_See_Tag_Info")%>','<%=getI18N("Click_To_Hide_Tag_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Tag_Info")%> />
							<%}
						}%>
						</td>
						<td><yfc:getXMLValue binding="xml:/Component/@ComponentItemID"/></td>
						<td><%=resolveValue("xml:ComponentTagDetails:/Item/PrimaryInformation/@Description")%></td>
						<!--<td nowrap="true" class="tablecolumn">
							<select class="combobox"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/@ProductClass")%> >
							<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
								name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
							</select>
						</td>-->
						<!--Replace for 7.7-->
						<td nowrap="true" class="tablecolumn">
													<select class="combobox"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/@ProductClass", "xml:ComponentTagDetails:/Item/PrimaryInformation/@DefaultProductClass")%>
													<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
 								name="CodeValue" value="CodeValue" selected="xml:ComponentTagDetails:/Item/PrimaryInformation/@DefaultProductClass"/>
						</td>
						<!--End Replace for 7.7-->
						<td><yfc:getXMLValue binding="xml:/Component/@ComponentUnitOfMeasure"/></td>
						<td nowrap="true" class="tablecolumn">
							<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/@ComponentQuantity","xml:/Component/@KitQuantity")%>/>
						</td>
						<%
						  String qtyPerKit = resolveValue("xml:/Component/@KitQuantity");
						  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
						  float fQtyPerKit = 0;
						  if (qtyPerKit != null && qtyPerKit.length() > 0){
							//CR1562
							  if(qtyPerKit.length() > 3)
							  fQtyPerKit = Float.parseFloat(qtyPerKit.replace(",", ""));
								else
							fQtyPerKit = (new Float(qtyPerKit)).floatValue();
						  }

						  float fReqQty = 0;
						  if (reqQty != null && reqQty.length() > 0){
							// CR1562
								if(reqQty.length() > 3)
							  fReqQty = Float.parseFloat(reqQty.replace(",", ""));
								else
							fReqQty = (new Float(reqQty)).floatValue();
						  }
						  float total = fQtyPerKit * fReqQty;
						%>
						<td class="tablecolumn"><%=total%></td>

						<%String totBind="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/WorkOrderComponentTag";%>

						<tr id='componentOptionSet_<%=ComponentCounter%>' class='<%=className%>' style="display:none">
							<td colspan="8" >
								<jsp:include page="/vas/workorder/detail/vas_detail_includetag.jsp" flush="true">
									<jsp:param name="componentOptionSetBelongingToLine" value='<%=String.valueOf(ComponentCounter)%>'/>
									<jsp:param name="TotalBinding" value='<%=totBind%>'/>
								</jsp:include>
							</td>
						</tr>
					</tr>
					<input type="hidden" 	<%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/@ItemID",resolveValue("xml:/Component/@ComponentItemID"))%>/>
					<input type="hidden" 	<%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+ComponentCounter+"/@Uom",resolveValue("xml:/Component/@ComponentUnitOfMeasure"))%> />
				<%}else{%>
					<input type="hidden" 	<%=getTextOptions(binding,resolveValue(binding))%> />
				<%}%>
			</yfc:loopXML>
		<%}%>
    </tbody>
	<% if(modifyView == ""){%>
		<tfoot>
			<tr style='display:none' TemplateRow="true">
				<td class="checkboxcolumn">&nbsp;</td>
			<td>
				<img onclick="updateCurrentView()"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Tag_Info")%> />
			</td>
			<td nowrap="true" class="tablecolumn">
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
			<td nowrap="true" class="tablecolumn">
				<select class="combobox"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn">
				 <select name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn">
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<%
			  String qtyPerKit = resolveValue("xml:/Component/@KitQuantity");
			  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
			  float fQtyPerKit = 0;
			  if (qtyPerKit != null && qtyPerKit.length() > 0){
				//CR1562
				  if(qtyPerKit.length() > 3)
				  fQtyPerKit = Float.parseFloat(qtyPerKit.replace(",", ""));
					else
				fQtyPerKit = (new Float(qtyPerKit)).floatValue();
			  }

			  float fReqQty = 0;
			  if (reqQty != null && reqQty.length() > 0){
				// CR1562
					if(reqQty.length() > 3)
				  fReqQty = Float.parseFloat(reqQty.replace(",", ""));
					else
				fReqQty = (new Float(reqQty)).floatValue();
			  }
			  float total = fQtyPerKit * fReqQty;
			%>
			<td class="tablecolumn"><%=total%></td>
			<!--<td class="checkboxcolumn">&nbsp;</td>-->
			</tr>
			<tr>
				<td nowrap="true" colspan="9">
					<jsp:include page="/common/editabletbl.jsp" flush="true">
					</jsp:include>
				</td>
			</tr>
		</tfoot>
	<%}%>
</table>
</div>