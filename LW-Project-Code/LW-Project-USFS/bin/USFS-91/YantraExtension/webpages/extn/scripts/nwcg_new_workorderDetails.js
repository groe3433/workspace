function populateIncidentItemDetailsForWO(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strSerialTracked = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	if(nodes!=null && nodes.length > 0 )
	{	
		var item = nodes(0);
		strUOM = item.getAttribute("UnitOfMeasure");
		obj = document.getElementById("xml:/WorkOrder/@ItemID");
		if(obj)
			obj.value = checkStringForNull(item.getAttribute("ItemID"));
		
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

	var InputList = currentRow.getElementsByTagName("SELECT");

	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@Uom') != -1)
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

		if(InputList[i].name.indexOf('@ProductClass') != -1)
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


function updateIncidentDetailsForWO(elem,xmlDoc){
		nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		var docType = document.getElementById("xml:/WorkOrder/@DocumentType");
		
		//traverse(nodes(0));
		if(nodes!=null && nodes.length >0 ){	
			var incidentOrder = nodes(0);
			
			
			//var active = incidentOrder.getAttribute("IsActive")
			
			//var isActive = document.getElementById("IS_ACTIVE") ;
			
			//if(isActive)
				//isActive.value = active ;

			//populate the address
			//var obj = document.getElementById("xml:/WorkOrder/Extn/@ExtnIncidentName") ;
			//if(obj)
			//	obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentName"));
			
			obj = document.getElementById("xml:/WorkOrder/Extn/@ExtnFsAcctCode") ;
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentFsAcctCode"));

			obj = document.getElementById("xml:/WorkOrder/Extn/@ExtnBlmAcctCode");
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentBlmAcctCode"));

			obj = document.getElementById("xml:/WorkOrder/Extn/@ExtnOtherAcctCode") ;
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentOtherAcctCode"));
		}
}

