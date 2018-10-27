<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %> 
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_master_work_order.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>

<script language="javascript">
//CR-805 Changes - GN
function setParamLocal(ele)
{
	//alert("In set param local");
	var shipNode = document.all['xml:/MoveRequest/@Node'].value;
	//alert("ShipNode : "+ shipNode);
	var returnArray = new Object();
	returnArray["xml:ShipNode"] = shipNode;
	returnArray["xml:ItemID"] = ele.value;
	//alert("ItemID : "+ ele.value);
	//alert("End set param local");
	return returnArray;
}

function popItemDetails(elem,xmlDoc)
{
	//alert("popItemDetails");
	var nodes= xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		strPC = item.getAttribute("ProductClass") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strDesc = item.getAttribute("ShortDescription");
	}
    
	//alert("Desc " + strPC + " " + strUOM);
	var currentRow = elem.parentNode.parentNode;
	//alert("displaying current row:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("Input");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@Description') != -1)
		{
			InputList[i].value = strDesc;
		}//end if

		/*if(InputList[i].name.indexOf('@ProductClass') != -1)
		{
			InputList[i].value = strPC;
		}//end if
		if(InputList[i].name.indexOf('@UnitOfMeasure') != -1)
		{
			InputList[i].value = strUOM;
		}*/
	}
	
	
	var InputListPC = currentRow.getElementsByTagName("Select");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputListPC[i].name.indexOf('@ProductClass') != -1)
		{
			InputListPC[i].value = strPC;
		}//end if
}
}
//CR-805 Changes End - GN
</script>

<table class="table">
<thead>
    <tr> 
		<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>	
		
        <td class="tablecolumnheader">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>        
		<td class="tablecolumnheader">
            <yfc:i18n>Item_Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Inventory_Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>		
    </tr>
</thead>
<tbody>	
	 <yfc:loopXML binding="xml:/MoveRequest/MoveRequestLines/@MoveRequestLine" id="MoveRequestLine">	 
    <tr>		
		<td class="checkboxcolumn">
			<input type="checkbox" value="" name="chkEntityKey" />
		</td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="MoveRequestLine" binding="xml:/MoveRequestLine/@ItemId"/>
        </td>        
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@Description"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ProductClass"/>
        </td>
		<td class="tablecolumn">		
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@UnitOfMeasure"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@InventoryStatus"/>
        </td>
        <td class="tablecolumn">            
			<yfc:getXMLValue binding="xml:/MoveRequestLine/@RequestQuantity"/>
        </td>
    </tr>	
	</yfc:loopXML>
</tbody>
<tfoot>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>
    <tr style='display:none' TemplateRow="true">
       <td class="checkboxcolumn" >
	   &nbsp;

<!--onblur="var objArr = new Object();objArr['xml:ItemID'] = this.value;fetchDataWithParams(this,'getItemList',popItemDetails,objArr);"-->

       </td>       
		<td nowrap="true" class="tablecolumn">
		<!-- CR 805 Changes - GN -->
		                                                    
		<input type="text" class="unprotectedinput" onblur="fetchDataWithParams(this,'PopulateItemDetails',popItemDetails,setParamLocal(this))" 	
		<%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@ItemId")%> />  
		
		<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemId','ProductClass','UnitOfMeasure','item' ,'xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/MoveRequest/@EnterpriseCode'].value)" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
        </td>		
		<td nowrap="true" class="tablecolumn">
            <input class="protectedtext"  size=100 <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@Description")%>/>
        </td> 
        <!-- CR 805 Changes End - GN -->
		<td nowrap="true" class="tablecolumn">		 
			<select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@ProductClass")%> >
            <yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
                name="CodeValue" value="CodeValue" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@ProductClass"/>
        </select>    
        </td>
		<td nowrap="true" class="tablecolumn">
			 <select name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@UnitOfMeasure"					class="combobox">
				<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" 	value="UnitOfMeasure" 																				selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@UnitOfMeasure"/>
			</select>
        </td>
		<td nowrap="true" class="tablecolumn">
		 	<select name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@InventoryStatus" class="combobox" >
				<yfc:loopOptions binding="xml:InventoryStatusList:/InventoryStatusList/@InventoryStatus" 
					name="InventoryStatus" value="InventoryStatus" />
			</select>
        </td>
		<td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_/@RequestQuantity")%>/>
        </td>
	</tr>
    <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
    		</jsp:include>
    	</td>
    </tr>
</tfoot>
</table>