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
<table class="view" width="100%">
<tr>
	<td class="detaillabel">Item to be Refurbished</td><td class="protectedtext">001760</td>
	<td class="detaillabel">Item Description</td><td class="protectedtext">KIT - First Aid; 100-Person</td>
	<td class="detaillabel">Qty to be refurbed</td><td><input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/></td>
</tr>
<tr>
	<td class="detaillabel">Refurb Location</td>
	<td colspan=2  class="protectedtext"><select class="combobox" style="width:150px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >FIRST-AID-1</option>
				</select></td>
</tr>
</table>
<div>
<br>
<table class="table" ID="WorkOrderComponents" cellspacing="0" width="100%">
    <thead>
        <tr>
			<td style="width:80px" class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:150px"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:60px"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Refurb Cost</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px" colspan="10"><yfc:i18n>&nbsp;</yfc:i18n></td>
        </tr>
    </thead>
    <tbody	>
	<% if(modifyView == ""){%>
		<tfoot>
			<tr TemplateRow="true" class="oddrow">
			
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px"  value="000338" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> CARTON - fiberboard; 37'' x 18'' x 7''<yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >Supply</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				<option >EA</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<%
			  String qtyPerKit = resolveValue("xml:/Component/@KitQuantity");
			  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
			  float fQtyPerKit = 0;
			  if (qtyPerKit != null && qtyPerKit.length() > 0){
				fQtyPerKit = (new Float(qtyPerKit)).floatValue();
			  }

			  float fReqQty = 0;
			  if (reqQty != null && reqQty.length() > 0){
				fReqQty = (new Float(reqQty)).floatValue();
			  }
			  float total = fQtyPerKit * fReqQty;
			%>
			</tr>
			<tr TemplateRow="true" class="evenrow">
			
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" value="000771 " <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> CLIPBOARD - 9'' x 12 1/2'' <yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >Supply</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				<option >EA</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			</tr>
			<tr>
			<tr TemplateRow="true" class="oddrow">
			
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" value="000069"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> FLASHLIGHT - general service; 2 cell<yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >Supply</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				<option >EA</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			</tr>
			<tr>
			<tr TemplateRow="true" class="evenrow">
			
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" value="000033 " <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> BATTERY - size D;1.5v;alkaline;general purpose<yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >Supply</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				<option >PG</option>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
			</td>
			<td colspan="8">
				<table class="table" >
				<tbody>
					<tr>
						<td width="100%" style="border:1px solid black">
							<table class="table" editable="true" width="100%" cellspacing="0">
								<thead> 
									<tr>
										<td class="tablecolumnheader">Expiration Date</td>
										
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="tablecolumn"><input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/></td>
									</tr>
								</tbody>
							</table>
				</tbody>
				</table>
			</td>
			
			</tr>
			<tr>
			<!---->
			</tr>
				<td nowrap="true" colspan="20">
					<jsp:include page="/common/editabletbl.jsp" flush="true">
					</jsp:include>
				</td>
			</tr>
		</tfoot>
	<%}%>
</table>
</div>