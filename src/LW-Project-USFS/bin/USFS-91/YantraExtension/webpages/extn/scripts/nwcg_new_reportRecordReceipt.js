function setParam(ele)
{
	var supplier = document.getElementById("SUPPLIER");
	var returnArray = new Object();
	returnArray["xml:SupplierID"] = supplier.value;
	returnArray["xml:ItemID"] = ele.value;
	return returnArray;
}


function populateSupplierItemUnitPrice(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("NWCGSupplierItem");
	var bItemDoesNotExists = false;
	// no items found
	if(nodes.length <=0 )
	{
		nodes=xmlDoc.getElementsByTagName("Item");
		bItemDoesNotExists = true ;
		alert('Item ( '+elem.value+' ) does not exist or not published' );
	}
	var nodesPI=xmlDoc.getElementsByTagName("PrimaryInformation");
	var strDesc = '' ;
	if(nodesPI != null && nodesPI.length > 0)
	{
		var desc = nodes(0);
		strDesc = desc.getAttribute("ShortDescription");
	}
	var strPC = '';
	var strUOM = '';
	var strPrice = '';
	var strSupStdPack = '' ;
	var strStdPack = '' ;
	var strNSN = '' ;
	var strItemID = '';
	var strRFIQty = '' ;
	/* Begin CR 572 ML */
	var strCacheID = '';
	var strProductLine = '';
	/* End CR 572 ML */
	
	
	if(nodes!=null && nodes.length > 0 )
	{	
		var item = nodes(0);
		
		strPC = item.getAttribute("ProductClass") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strPrice = item.getAttribute("UnitCost");
		strSupStdPack = item.getAttribute("SupplierStandardPack");
		strStdPack = item.getAttribute("ExtnStdPack");
		strSupplierUOM = item.getAttribute("SupplierUOM");
		strRFIQty = item.getAttribute("AvailableQty");
		strItemID = item.getAttribute("ItemID");
		 
		/* Begin CR 572 ML */ 
		strCacheId= document.all("xml:/Order/@ReceivingNode").value;
		strProductLine = item.getAttribute("ProductLine");

 
 /* Stub Testing for CR 572 
		
		if (strProductLine == null)
			alert("strProductLine is returning null.");
		else 
			alert("strProductLine = " + strProductLine);
		
		
		if (strCacheId == null)
			alert("strCacheId is returning null.");
		else
			alert("strCacheId = " + strCacheId);
*/
		
		if(strCacheId != "IDGBK" && strProductLine == "NIRSC Communications")
		{
			alert("Item : " + strItemID + " can only be received at the IDGBK node.");
			elem.value ="";
			return false;
		}
		/* End CR 572 - ML */


		if(strDesc == '' || strDesc == null)
		{
			strDesc = item.getAttribute("ShortDescription");
		}

		if(strSupStdPack == null) strSupStdPack = '';
		if(strStdPack == null) strStdPack = '';
		if(strSupplierUOM == null) strSupplierUOM = '';

		strNSN = item.getAttribute("ExtnNSN");
		strItemID = item.getAttribute("ItemID");

		if(strPrice == null || strPrice=='null')
			strPrice = '';
	}
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 )
	{
		label(0).innerText = strDesc;
	}
	
	for(i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ReceivingPrice') != -1)
		{
			InputList[i].value = strPrice ;
			
		}
		if(InputList[i].name.indexOf('/Extn/@ExtnNSN') != -1)
		{
			if(strNSN == null )
				strNSN = "" ;
			InputList[i].value = strNSN ;
			
		}
		if(InputList[i].name.indexOf('/Extn/@ExtnSupplierStdPack') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strSupStdPack ;
			
		}
		
		if(InputList[i].name.indexOf('/Extn/@ExtnStdPack') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strStdPack ;
			
		}

		if(InputList[i].name.indexOf('/@ItemID') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strItemID ;
			
		}
		if(InputList[i].name.indexOf('/@ExtnQtyRfi') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strRFIQty ;
			
		}
		if(InputList[i].name.indexOf('/@ReqDeliveryDate') != -1 && bItemDoesNotExists )
		{
			InputList[i].value = "" ;
			
		}
		//cr 440 ks
		if(InputList[i].name.indexOf('/Extn/@ExtnSupplierUOM') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strSupplierUOM ;
			
		}

	}

	InputList = currentRow.getElementsByTagName("SELECT");
	if(strUOM == null)
	{
		strUOM = "";
	}
	if(strPC == null)
	{
		strPC = "" ;
	}
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@UnitOfMeasure') != -1 || InputList[i].name.indexOf('@TransactionalUOM'))
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

	var availRFIAnchor = currentRow.getElementsByTagName("a");
	if(availRFIAnchor != null && availRFIAnchor != 'undefined' && availRFIAnchor.length > 0 )
	{
		var rfiParams = 'xml:/Item/@ItemID=' + strItemID + '&xml:/Item/@ProductClass=' + strPC + '&xml:/Item/@TransactionalUOM=' + strUOM;
		availRFIAnchor[0].innerText = strRFIQty;				
		availRFIAnchor[0].onclick = new Function("yfcShowDetailPopupWithParams(\"ISUYOMD095\", \"\", \"900\", \"500\", '" + rfiParams + "');return false;");
	}	
}

