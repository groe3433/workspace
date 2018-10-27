IgnoreChangeNames();
yfcDoNotPromptForChanges(false);

function proceedClicked()
{
	////alert('***** 1 *****');
	
	var orgCodeObj = document.all("xml:/NWCGSupplierItem/@OrganizationCode");
	var proceedClicked = document.all("ProceedClicked");
	//alert("proceed clicked "+proceedClicked.value);
	proceedClicked.value="CLICKED";
	if (orgCodeObj != null) {
		var orgCodeVal = orgCodeObj.value;
		if (orgCodeVal == "") {
			// Need to popup an error here instead of defaulting.
			orgCodeObj.value = '<%=orgCode%>';
		}
	}
	//alert('***** 2 *****');
	 var itemInput = document.all("xml:/NWCGSupplierItem/@ItemID");
	 //alert('***** 2.1 *****' + orgCodeObj);
	 if (orgCodeObj != null) {
		var itemInputVal = itemInput.value;
		//alert('***** 2.2 *****');
		itemInputVal = itemInputVal.replace(/^\s*/, '').replace(/\s*$/, '');
		 if (itemInputVal != "") {
			if(validateControlValues()) {			
				yfcChangeDetailView(getCurrentViewId());
			}
		 } else {
				//alert(YFCMSG037);
		 }
	 }
	 //alert('***** 3 *****');
}

function selectClicked()
{
	var proceed = document.all("ProceedClicked");
	if(proceed != null)
	{
		proceed.value="NOTCLICKED";
	}
	yfcChangeDetailView(getCurrentViewId());
}
function setItemParam(ele)
{
	var returnArray = new Object();
	returnArray["xml:ItemID"] = ele.value;

	return returnArray;
}

function populateItemDetails(elem,xmlDoc)
{
	
	var nodes=xmlDoc.getElementsByTagName("Item");

	var strPC = '' ;
	var strQty = '' ;
	var strUOM = '' ;
	var strShortDesc = '' ;
	var strNSN = '';

	var currentRow = elem.parentElement.parentElement.parentElement;
	

	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		var listPrimaryInformation = item.getElementsByTagName("PrimaryInformation");
		
		if(listPrimaryInformation != null && listPrimaryInformation.length > 0) 
		{

			var PrimaryInformation = listPrimaryInformation(0);
			
					

			strPC = PrimaryInformation.getAttribute("DefaultProductClass") ;
			strUOM = item.getAttribute("UnitOfMeasure");
			strShortDesc = PrimaryInformation.getAttribute("ShortDescription");
			strNSN = item.getAttribute("GlobalItemID");
		
		
			
		}
	}
	
	if( (strPC == '' && strUOM == '') || (strPC == null && strUOM == null) )
	{
		strPC = "" ;
		strUOM = "" ;
		alert('Item ( '+elem.value+' ) does not exists or not published');
	}
	if(  ( strNSN =='') || (strNSN == null) )
	{
		
		strNSN = "";
		alert('Item ( '+elem.value+' ) does not have an established NSN.');
		
	}
	if(  ( strShortDesc =='') || (strShortDesc == null) )
	{
		strShortDesc = "";
		alert('Item ( '+elem.value+' ) does not have an established Cache Item Description.');
		
	}
	var InputList = document.getElementsByTagName("SELECT");
	var InputList1 = currentRow.getElementsByTagName("input");
	
	for (i = 0 ; i < InputList.length ; i++)
	{
		
		if(InputList[i].name.indexOf('NWCGSupplierItem/@UnitOfMeasure') != -1)
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
		if(InputList[i].name.indexOf('NWCGSupplierItem/@ProductClass') != -1)
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

	for(k = 0; k < InputList1.length; k++)
	{
		if(InputList1[k].name.indexOf('NWCGSupplierItem/@GlobalItemID') != -1)
		{
		
				 InputList1[k].value=strNSN;

		}//end if
		if(InputList1[k].name.indexOf('NWCGSupplierItem/@ShortDescription') != -1)
		{
		
				 InputList1[k].value=strShortDesc;
		}//end if
	}

}
