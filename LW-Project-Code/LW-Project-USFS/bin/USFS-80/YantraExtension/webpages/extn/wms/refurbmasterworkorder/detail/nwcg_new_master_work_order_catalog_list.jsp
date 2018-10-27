<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.yantra.yfc.dom.YFCElement"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XPathWrapper"%>
<%@page import="com.yantra.yfc.dom.YFCNodeList"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XMLUtil"%>
<%@page import="org.w3c.dom.Node"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="org.w3c.dom.NodeList"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XPathUtil"%>
<%@page import="com.yantra.yfc.dom.YFCDocument"%>
<%@page import="com.yantra.yfs.japi.YFSEnvironment"%>
<%@page import="com.yantra.shared.ycp.YFSContext"%>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_master_work_order.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_returns.js"></script>
<script>
// :TODO Shift this to js file 
//window.attachEvent("onload", showhtml);


function setPriceForItem(elem,xmlDoc)
{
	var list =	xmlDoc.getElementsByTagName("ItemPriceSet");
	var priceValue = "0.0" ;
	if(list != null && eval(list) && list.length > 0)
	{
		var elemItemPriceSet = list(0);
		priceValue = elemItemPriceSet.getAttribute("ListPrice");
	}
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("INPUT");
	for (var i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@RefurbCost') != -1)
		{
			InputList[i].value = (parseFloat(priceValue) * elem.value) ;
		}//end if

		if(InputList[i].name.indexOf('@ItemUnitPrice') != -1)
		{
			
			InputList[i].value = (parseFloat(priceValue));

		}

	}//end for  select inout list
}



// Suryasnat: function added for Issue 502
function setPricingParamsForRefurbLines(elem){
	var objArr = new Object() ; 
	
	var currentRow = elem.parentNode.parentNode;

	var InputList = currentRow.getElementsByTagName("SELECT");

	for (var i = 0 ; i < InputList.length ; i++)
	{
		
		if(InputList[i].name.indexOf('@ComponentUnitOfMeasure') != -1)
		{	
			objArr['xml:Uom'] =  InputList[i].value ;
		}//end if

		if(InputList[i].name.indexOf('@ComponentDefaultProductClass') != -1)
		{
			objArr['xml:ProductClass'] =  InputList[i].value;	
		}//end if
	}//end for  select inout list
	
	var InputList = currentRow.getElementsByTagName("INPUT");

	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@ComponentItemID') != -1)
		{
			objArr['xml:ItemID'] =  InputList[i].value ;
		}//end if
	}//end for  select inout list

	return objArr ;
}

function setPricingParams(elem)
{
	var objArr = new Object() ; 
	
	var currentRow = elem.parentNode.parentNode;

	var InputList = currentRow.getElementsByTagName("SELECT");

	for (var i = 0 ; i < InputList.length ; i++)
	{
		
		if(InputList[i].name.indexOf('@Uom') != -1)
		{
			objArr['xml:Uom'] =  InputList[i].value ;
		}//end if

		if(InputList[i].name.indexOf('@ProductClass') != -1)
		{
			objArr['xml:ProductClass'] =  InputList[i].value;	
		}//end if
	}//end for  select inout list
	
	var InputList = currentRow.getElementsByTagName("INPUT");

	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@ItemID') != -1)
		{
			objArr['xml:ItemID'] =  InputList[i].value ;
			
		}//end if
	}//end for  select inout list
	return objArr ;
	
}

function deleteRows(elem){
//Added by GN - 12/20/06

 var i=elem.parentNode.parentNode.rowIndex
 document.getElementById('WorkOrderComponents').deleteRow(i)
	 
}//End Function DeleteRows


function setParamForRefurb(ele)
{
	var itemID = ele.value;
	returnArray["xml:ItemID"] = ele.value;

	////alert("ItemID"+itemID);
	return returnArray;
}

</script>

<!-- Suryasnat: To display component refurb lines of a master work order if the flag is checked. -->
<%
	String itemID = resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemID");
	String uom = resolveValue("xml:/NWCGMasterWorkOrderLine/@UnitOfMeasure");
	String pc = resolveValue("xml:/NWCGMasterWorkOrderLine/@ProductClass");
	String checkval = resolveValue("xml:/NWCGMasterWorkOrderLine/@DisplayMasterWOComponents");
	
	YFCDocument inputDoc = YFCDocument.parse("<Item/>");
	YFCElement elemItem = inputDoc.getDocumentElement();
	elemItem.setAttribute("ItemID",itemID);
	elemItem.setAttribute("UnitOfMeasure",uom);
	elemItem.setAttribute("ProductClass",pc);
	elemItem.setAttribute("OrganizationCode","NWCG");
		
%>

<yfc:callAPI serviceName="NWCGGetItemDetailsTransService" inputElement="<%=inputDoc.getDocumentElement()%>" outputNamespace="GetItemDetails"></yfc:callAPI>

			<!-- Suryasnat: After the service call, loop through the component item lines and populate the refurb lines 
			with the component lines -->
			<%
				//System.out.println("value ::"+checkval);
				String strItemID = null; 
			 	if(checkval.trim().equalsIgnoreCase("Y")){
			 	//if(true){
			%> <!-- Suryasnat: Static condition-->

 <table class="table" ID="WorkOrderComponents" cellspacing="0" width="100%" GenerateID="true">
    <thead>
        <tr>
			<td style="width:50px" class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:150px"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:60px"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Qty Consumed</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Kit Quantity</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:0px"></td>
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Replace Component</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px" colspan="10"><yfc:i18n>&nbsp;</yfc:i18n></td>
        </tr>
    </thead>
			
	<tbody>
		
			<%
				YFCElement elem = (YFCElement) request.getAttribute("GetItemDetails");
				//System.out.println("XML is"+elem.toString());
				Document doc = elem.getOwnerDocument().getDocument();
				System.out.println("doc :"+XMLUtil.getXMLString(doc));
				XPathWrapper xpWrapItemDtl = new XPathWrapper(doc);
				NodeList nlComponent = xpWrapItemDtl.getNodeList("/Item/Components/Component");
				for(int i=0;i<nlComponent.getLength();i++){
					Element compElem = (Element)nlComponent.item(i);
					System.out.println("compElem:"+compElem.getAttribute("ComponentItemID"));
					String kitqty = compElem.getAttribute("KitQuantity");
					String modkitqty = Double.valueOf(kitqty).toString();
					//String dpc = compElem.getAttribute("ComponentDefaultProductClass");
					// Hardcoding to Supply, as there is no ComponentDefaultProductClass in getItemDetails Output in 8.0 
					String dpc = "Supply";
					String cuom = compElem.getAttribute("ComponentUnitOfMeasure");
					String compItemKey = compElem.getAttribute("ComponentItemKey");
					int count=i+1;
			%>
						
		<tr TemplateRow="false" style="display:all;">
			<td nowrap="true" class="tablecolumn" style="width:90px" >
				
				<!-- Added By Suryasnat for issue # 501 -->
				<IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif">
				<!-- Added By Suryasnat for issue # 501 -->
				<input type="hidden" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_<%=count%>/@Count" Value="<%=count%>"/>

				<input type="text" class="unprotectedinput"  style="width:50px" onblur="var objArr = new Object() ; objArr['xml:ItemID'] =  this.value;fetchDataWithParams(this,'getItemList',populateItemDetailsForRefurbLines,objArr);validateComponentItemForRefurbLines(this,<%=count%>)"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/@ItemID",compElem.getAttribute("ComponentItemID"))%>/>

				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  'NWCG' )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"><label><%=compElem.getAttribute("ComponentShortDescription")%></label></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="<%=dpc%>"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_<%=count%>/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" selected="<%=cuom%>"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/@ComponentQuantity")%>/>
				<br>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" readonly="true" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/@KitQuantity",kitqty)%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:0px"> 
				<!-- <input type="hidden" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/Extn/@RefurbCost")%>/>
				<input type="hidden" class="unprotectedinput"  style="width:50px" value="" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_"+count+"/@ItemUnitPrice")%>/>
				<br>-->
			</td>
			<td>
				<input type='hidden' name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_<%=count%>/@ReplaceComponent" value="NA"/>
				<input id='replacecompcheck' onClick="setReplaceComponentValueForRefurbLines(this)" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_<%=count%>/@CheckBox" type="checkbox" >
			</td>
		</tr>
				
		<% } %>
		
	</tbody>
	<% //if(false){ %>		
	<tfoot>
		<tr TemplateRow="true" style="display:none;">
			<td nowrap="true" class="tablecolumn" style="width:90px" >
				
				<!-- Added By Suryasnat for issue # 501 -->
				<IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif">
				<!-- Added By Suryasnat for issue # 501 -->

				<input type="text" class="unprotectedinput"  style="width:50px" onblur="var objArr = new Object() ; objArr['xml:ItemID'] =  this.value;fetchDataWithParams(this,'getItemList',populateItemDetails,objArr);validateComponentItem(this)"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>

				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  'NWCG' )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
			<td class="tablecolumn" style="width:150px"><label></label></td>
			<td nowrap="true" class="tablecolumn" style="width:60px">
				<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
				<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
					name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
					<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
				</select>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
				<br>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:50px">
				<input type="text" class="unprotectedinput"  style="width:50px" readonly="true" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@KitQuantity")%>/>
			</td>
			<td nowrap="true" class="tablecolumn" style="width:0px"> 
				<!-- <input type="hidden" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/Extn/@RefurbCost")%>/>
				<input type="hidden" class="unprotectedinput"  style="width:50px" value="" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemUnitPrice")%>/>
				<br>-->
			</td>
			<td>
				<input type='hidden' name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ReplaceComponent" value="NA"/>
				<input onClick="setReplaceComponentValue(this)" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@CheckBox" type="checkbox">
			</td>
		</tr>
		<tr>
			<td nowrap="true" colspan="20">
				<jsp:include page="/common/editabletbl.jsp" flush="true">
				</jsp:include>
			</td>
		</tr>
	</tfoot>
	<% //} %>
</table>
</div>
	<%
		} 
		else 
		{

	%>
			
<table class="table" ID="WorkOrderComponents" cellspacing="0" width="100%" GenerateID="true">
    <thead>
        <tr>
			<td style="width:50px" class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:150px"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:60px"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Qty Consumed</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Kit Quantity</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:0px"></td>			
			<td class="tablecolumnheader" style="width:50px"><yfc:i18n>Replace Component</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:50px" colspan="10"><yfc:i18n>&nbsp;</yfc:i18n></td>
        </tr>
    </thead>
			
	<tbody>
		<tfoot>
			<tr TemplateRow="true" class="evenrow" style="display:none" >
			
				<td nowrap="true" class="tablecolumn" style="width:90px">
					
					<!-- Added By Suryasnat for issue # 501 -->
					<IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif">
					<!-- Added By Suryasnat for issue # 501 -->
	
					<input type="text" class="unprotectedinput"  style="width:50px" onblur="var objArr = new Object() ; objArr['xml:ItemID'] =  this.value;fetchDataWithParams(this,'getItemList',populateItemDetails,objArr);validateComponentItem(this)"  <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemID")%>/>
	
					<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','Uom','item' ,'xml:/Item/@CallingOrganizationCode=' +  'NWCG' )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
				</td><!-- Item ID tag -->
				
				<td class="tablecolumn" style="width:150px"><label></label></td> <!-- Item Desc tag -->
				
				<td nowrap="true" class="tablecolumn" style="width:60px">
					<select class="combobox" style="width:60px"  <%=getComboOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass")		%>>
					<yfc:loopOptions binding="xml:WorkOrderProductClassList:/CommonCodeList/@CommonCode"
						name="CodeValue" value="CodeValue" selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ProductClass"/>
					</select>
				</td><!-- Item PC tag -->
				
				<td nowrap="true" class="tablecolumn" style="width:50px">
					 <select style="width:50px" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"					class="combobox">
						<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 											selected="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@Uom"/>
					</select>
				</td><!-- Item UOM tag -->
				
				<td nowrap="true" class="tablecolumn" style="width:50px">
					<input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ComponentQuantity")%>/>
					<br>
				</td><!-- Qty Consumed tag -->
				
				<td nowrap="true" class="tablecolumn" style="width:50px">
					<input type="text" class="unprotectedinput"  style="width:50px" readonly="true" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@KitQuantity")%>/>
				</td><!-- Kit Qty tag -->
				
				<td nowrap="true" class="tablecolumn" style="width:0px"> 
					<!-- <input type="hidden" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/Extn/@RefurbCost")%>/>
					<input type="hidden" class="unprotectedinput"  style="width:50px" value="" <%=getTextOptions("xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ItemUnitPrice")%>/>
					<br>-->
				</td><!-- Refurb Cost textbox -->
				
				<td>
					<input type='hidden' name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@ReplaceComponent" value="NA"/>
					<input onClick="setReplaceComponentValue(this)" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_/@CheckBox" type="checkbox">
				</td><!-- Replace comp checkbox -->
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
	</table>
</div>
<% } %>

	
