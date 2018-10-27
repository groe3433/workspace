<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
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
	<td class="detaillabel">Item to be Refurbished</td><td class="protectedtext">001372 </td>
	<td class="detaillabel">Item Description</td><td class="protectedtext">PACK - field; firefighter; unisex; complete</td>
	<td class="detaillabel">Qty to be refurbed</td><td><input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID","1")%>/></td>
</tr>
<tr>
	<td class="detaillabel">Refurb Location</td>
	<td colspan=2  class="protectedtext"><select class="combobox" style="width:150px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					<option >GENERAL-REFURB-1</option>
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
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Qty Consumed</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Refurb Cost</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px" colspan="10"><yfc:i18n>&nbsp;</yfc:i18n></td>
        </tr>
    </thead>
    <tbody	>
	<% if(modifyView == ""){%>
		<tfoot>
			<tr TemplateRow="true" class="oddrow">
			
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px"  value="001557 " <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> HARNESS - field firefighter pack; unisex<yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
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
				<input type="text" class="unprotectedinput"  style="width:50px" value="001559  " <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/WorkOrder/@EnterpriseCodeForComponent'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"> PACK - field; firefighter; replacement <yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/></td>
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