function setItemParam(ele,node,from)
{
	var supplier = document.getElementById("SUPPLIER");
	var returnArray = new Object();
	if(supplier)
	{
		returnArray["xml:SupplierID"] = supplier.value;
	}
	if(from == 'NSN')
		returnArray["xml:NSN"] = ele.value;
	else
		returnArray["xml:ItemID"] = ele.value;

	return returnArray;

}

function populateIncidentItemDetails(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strSerialTracked = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	if(nodes.length <= 0)
	{
		alert('item ('+elem.value+') does not exist or not published');
	}
	if(nodes!=null && nodes.length > 0 )
	{	
		var item = nodes(0);
		strUOM = item.getAttribute("UnitOfMeasure");
		
		var primaryInfoList = item.getElementsByTagName("PrimaryInformation");
		
		if(primaryInfoList != null && primaryInfoList.length > 0)
		{
			var info = primaryInfoList(0);
			strPC = info.getAttribute("DefaultProductClass");
			strDesc = info.getAttribute("ShortDescription");
		}

		var invParam = item.getElementsByTagName("InventoryParameters");
		if(invParam != null && invParam.length > 0)
		{
			var info = invParam(0);
			strSerialTracked = info.getAttribute("IsSerialTracked");
		}
	}
	var currentRow = elem.parentNode.parentNode;
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 )
	{
		label(0).innerText= strDesc;
	}
	var InputList = currentRow.getElementsByTagName("Input");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnTrackableId') != -1)
		{
			if(strSerialTracked == 'Y')
			{
				InputList[i].style.visibility = 'visible';
				InputList[i].focus(); //Added for CR 587 ML
			}
			else
			{
				InputList[i].style.visibility = 'hidden';
			}
		}//end if

		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1)
		{
			var isCtC = elem.getAttribute("CacheToCache") ;
			
			if(isCtC == null)
			{
				isCtC = false ;
			}

			if(!isCtC)
			{
				if(strSerialTracked == 'Y')
				{
					InputList[i].value = '1';
					InputList[i].readOnly = true;
				}
				else
				{
					InputList[i].value = '';
					InputList[i].readOnly = false ;
				}
			}
		}//end if

	}
	InputList = currentRow.getElementsByTagName("SELECT");

	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('OrderLineTranQuantity/@TransactionalUOM') != -1)
		{
			var option = InputList[i].options;
			for(j = 0 ; j < option.length ; j++)
			{
				if(option[j].value == strUOM || option[j].text == strUOM )
				{
					InputList[i].selectedIndex = j;
				}
			}
		}//end if

		if(InputList[i].name.indexOf('Item/@ProductClass') != -1)
		{
			var option = InputList[i].options;
			for(j = 0 ; j < option.length ; j++)
			{
				if(option[j].value == strPC || option[j].text == strPC )
				{
					InputList[i].selectedIndex = j;
				}
			}
		}//end if
	}//end for  select inout list
}