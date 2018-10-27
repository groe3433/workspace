// JSK: js function added for CR 474
/*
this is called from Adjust Inventory Module
*/
function ItemDetailsForAdjInv(elem,xmlDoc)
{
	var strUOM = '' ;
	var strPC = '';
	var strKitCode = '' ;
	var strIsSerialTracked = "N";
	var strProductLine = '';
	var strNode = document.all("xml:/AdjustLocationInventory/@Node").value;


	// populating the values
	var nodes=xmlDoc.getElementsByTagName("Item");
	//alert('nodes.length=['+nodes.length+']');
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);		
		strUOM = item.getAttribute("UnitOfMeasure");
		
		//alert('strUOM=['+strUOM+']');

		var primNode = xmlDoc.getElementsByTagName("PrimaryInformation");
		if(primNode != null && primNode.length > 0)
		{
			var elemprimNode = primNode(0);
			strKitCode = elemprimNode.getAttribute("KitCode") ;
			strPC = elemprimNode.getAttribute("DefaultProductClass");
			strProductLine = elemprimNode.getAttribute("ProductLine");
			//alert('strProductLine=['+strProductLine+']');
			//alert('strNode=['+strNode+']');

			if(strProductLine == 'NIRSC Communications' && strNode != 'IDGBK'){
			alert("This Item Can Be Processed Only at IDGBK");
			elem.value='';
			document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure").value ='';
			document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass").value = '';
			return false;
			}
		}
	}

	var nlInventoryParameters = xmlDoc.getElementsByTagName("InventoryParameters");
	if(nlInventoryParameters != null && nlInventoryParameters.length > 0) {
		var elemInventoryParameters = nlInventoryParameters(0)
		strIsSerialTracked = elemInventoryParameters.getAttribute("IsSerialTracked");
		//alert('IsSerialTracked=['+strIsSerialTracked+']');
	}
	document.getElementById("xml:/ItemList/Item/PrimaryInformation/@KitCode").value = strKitCode;
	document.getElementById("xml:/ItemList/Item/InventoryParameters/@IsSerialTracked").value = strIsSerialTracked;
	document.getElementById("xml:/ItemList/Item/@UnitOfMeasure").value = strUOM;	

	//xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure
	//alert("strUOM " + strUOM);
	//xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass

	if(null != document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure")){

	document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure").value = strUOM;

	}

if(null != document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass")){

	document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass").value = strPC;

}


}

function setItemParamsAdjInv(ele)
{
	var returnArray = new Object();

	returnArray["xml:ItemID"] = ele.value;

	return returnArray;
}


