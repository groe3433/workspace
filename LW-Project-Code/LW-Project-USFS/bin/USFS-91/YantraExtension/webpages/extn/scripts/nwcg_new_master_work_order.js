
// Suryasnat: function added for Issue 502
function populateItemDetailsForRefurbLines(elem,xmlDoc)
{

	//var row = getParentObject(elem, "TR");
	//var rIndex = row.rowIndex;
	
	var rIndex="";
	var PItemRow = elem.parentElement;
   	var PItemList = PItemRow.getElementsByTagName("input");

   	if(PItemList[0].name.indexOf('@Count') != -1)
	    {
		 rIndex = PItemList[0].value;
		}

    //alert("Row index:"+rIndex);
	var nodes=xmlDoc.getElementsByTagName("Item");

	var strPC = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	var iTotalSerial = 0 ;
	// populating the values
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		
		strUOM = item.getAttribute("UnitOfMeasure");
		//alert(strUOM);
		var primNode = xmlDoc.getElementsByTagName("PrimaryInformation");
		
		if(primNode != null && primNode.length > 0)
		{
			var elemprimNode = primNode(0);
			strPC = elemprimNode.getAttribute("DefaultProductClass") ;
			//alert(strPC);
			strDesc = elemprimNode.getAttribute("ShortDescription") ;
			//alert(strDesc);
			iTotalSerial = elemprimNode.getAttribute("NumSecondarySerials");
			//alert(iTotalSerial);
		}
		
		if(strPC == null && strUOM == null)
		{
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
		}
	}
	var currentRow = elem.parentNode.parentNode;
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 )
	{
		//alert(strDesc);
		label(0).innerText= strDesc;
	}
	
	InputList = currentRow.getElementsByTagName("SELECT");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('@Uom') != -1)
		{	
			//alert('in uom');
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
			//alert('in pc');
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
	
	// changes for tag attributes
	var nlInventoryParameters = xmlDoc.getElementsByTagName("InventoryParameters");
	var strIsSerialTracked = "N";
	var strTimeSensitive = "N" ;
	if(nlInventoryParameters  != null && nlInventoryParameters.length > 0)
	{

		var elemInventoryParameters = nlInventoryParameters(0)
		strIsSerialTracked = elemInventoryParameters.getAttribute("IsSerialTracked");
		//alert('IsSerialTracked'+strIsSerialTracked);
		strTimeSensitive = elemInventoryParameters.getAttribute("TimeSensitive");
	}
	
	//Suryasnat: commented
	//var strId = currentRow.getAttribute("id");
	//alert('strId:'+strId);
	
	
	// all serial numbers will goto one column
	// total there are 8 columns starting from index 0 
	var iCurrentColumn = 8 ;
	// first of all blank out all the columns which are generated dynamically
	for(var cellIndex = 8 ; cellIndex < currentRow.cells.length ; cellIndex++)
	{
		// blanking out everything from the cell before we start displaying them
		currentRow.cells[cellIndex].innerHTML = "" ;
	}

	// if the item is serially tracked we need to add one label and one text field
	if(strIsSerialTracked == "Y")
	{
		var x = currentRow.insertCell(iCurrentColumn);
		iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
		
		x.Style = 'width:100px';
		
		// the old serial number will be used for dekitting, while new one will be used to kitting
		x.innerHTML = 'Old Trackable ID# <br> <input class=unprotectedinput type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@OldSerialNo"><br><br><br><br><br>' ;

	}
	// add all the secondary serials in the list, each sec serials will appear in seperate column
	for(var index = 0 ; index < parseInt(iTotalSerial) ; index++)
	{
		var x = currentRow.insertCell(iCurrentColumn);
		
		x.Style = 'width:100px';
		
		var displayIndex = parseInt(index) + 1 ;

		//alert('displayIndex:'+displayIndex);

		//alert('index:'+index);

		x.innerHTML ='<br>New Trackable ID# <br> <input type="text" class=unprotectedinput name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@SerialNo" onblur="fetchDataWithParams(this,\'getSerialList\',SetTagSerials,setParamForSerialNo(this));"><br>Manufacturers Serial# '+displayIndex+'<br> <input type="text" class=unprotectedinput name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@SecondarySerialNo'+index+'"><br><br><br><br>';
		
		// adding this just to make sure we are displaying the tag attributes in new lines 
		iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
	}
	
	var iDisplayedAttributes = 0 ;
	// get all the attributes - attributes are being controlled by the template
	var nlInventoryTagAttributes =	xmlDoc.getElementsByTagName("InventoryTagAttributes");
	if(nlInventoryTagAttributes != null && nlInventoryTagAttributes.length > 0)
	{
		var elemInventoryTagAttributes = nlInventoryTagAttributes(0);
		var attributes = elemInventoryTagAttributes.attributes;
		// counter to keep track of total number of atributes displayed on UI - two per column

		for(var indexAttr = 0 ; indexAttr < attributes.length ; indexAttr++)
		{
			var elemAttr = attributes(indexAttr);
			if(elemAttr != null)
			{
				var strAttr = elemAttr.nodeName;
				var strAttrName = strAttr ;
				if(strAttr == "BatchNo")
					strAttrName = "Batch#";
				if(strAttr == "LotAttribute1")
					strAttrName = "Manufacturer Name";
				if(strAttr == "LotAttribute2")
					strAttrName = "Owner Unit ID";
				if(strAttr == "LotAttribute3")
					strAttrName = "Manufacturer Model";
				if(strAttr == "LotNumber")
					strAttrName = "Trackable ID";
				if(strAttr == "ManufacturingDate")
					strAttrName = "Manufacturing Date";
				if(strAttr == "DateLastTested")
					strAttrName = "Date Last Tested";

//				alert(elemAttr.nodeValue);
				// display if and only if the item is tag controlled by this tag
				// value 03 means the item is not tag controlled
				if(elemAttr.nodeValue != "03")
				{

					// this item is tagged on this tag identifier
					if(iDisplayedAttributes > 2)
					{
						// changes to control the flow of the conponents on the screen
						iDisplayedAttributes = 0;
						iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
					}
					// will hold on the newly created row or the old one
					var x ;
					if(iDisplayedAttributes == 0)
					{
						// this is the first item create a new column
						x = currentRow.insertCell(iCurrentColumn);
						x.Style = 'width:100px;class:tablecolumn';
					}
					else
					{
						// append the attribute on the same column
						x = currentRow.cells(iCurrentColumn);
					}
					// append the string with attribute name and the corresponding value
					x.innerHTML = x.innerHTML + '<br>' + strAttrName+ '<br><input class=unprotectedinput type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/WorkOrderComponentTag/@'+strAttr+'">';
					
					// incriment the displayed attribute counter
					iDisplayedAttributes = iDisplayedAttributes + 1 ;	
		
				}
			
			} //if(elemAttr != null)
		}
	}
	
	// add the ship by date tag if the item is time sensitive
	if(strTimeSensitive == "Y")
	{
		if(iDisplayedAttributes > 2)
		{
			// changes to control the flow of the conponents on the screen
			iDisplayedAttributes = 0;
			iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
		}
		

		var x ;
		if(iDisplayedAttributes == 0)
		{
			// this is the first item create a new column
			x = currentRow.insertCell(iCurrentColumn);
			x.Style = 'width:100px;class:tablecolumn';
		}
		else
		{
			// append the attribute on the same column
			x = currentRow.cells(iCurrentColumn);
		}
		// append the string with attribute name and the corresponding value
		x.innerHTML = x.innerHTML + '<br> Ship By Date <br><input class="dateinput" type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@ShipByDate"><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="/smcfs/console/icons/calendar.gif" name=search/>';
	}
}


function setParamForSerialNo(elem){
	var currentRow = elem.parentElement.parentElement;
	var itemID = "";
	var serialNo = "";
	var returnArray = new Object();

	//alert("displaying current row:-"+currentRow.innerHTML);

	//--------------
	var InputList = currentRow.getElementsByTagName("input");

	for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@SerialNo') != -1)
			{
				 serialNo = InputList[cnt1].value;
			}//end if
	}//End For Loop
	
	//alert("itemID:-"+itemID);

	returnArray["xml:SerialNo"] = serialNo;
	//alert ("serialNo : " +serialNo);

	return returnArray;
	//--------------

}//End setParamForIncName

function SetTagSerials(elem,xmlDoc){

	var AtNode ="";

	var nodes=xmlDoc.getElementsByTagName("Serial");
	if(nodes!=null && nodes.length > 0 )
	{	
		var ResultNode = nodes(0);
		var AtNode = ResultNode.getAttribute("AtNode");
		var LocationID = ResultNode.getAttribute("LocationId");
		var SecSerial = ResultNode.getAttribute("SecondarySerial1");
	}//End Nodes
	else
	{
	  alert("INVALID TRACKABLE ID !!!");
	  return false;
	}

	// <TagDetail BatchNo="" LotAttribute1="RELM/BK" LotAttribute2="IDGBK" LotAttribute3="EPH5102X" LotKeyReference="" LotNumber="4322-AG01712844" ManufacturingDate="2002-06-29" RevisionNo="02/01/2010" TotalDemand="0.00" TotalOnhandSupply="1.00" TotalOtherSupply="0.00" /> 


    if (AtNode == "Y")
    {
		var tagnodes=xmlDoc.getElementsByTagName("TagDetail");
		var TNode = tagnodes(0);
		var LotNumber = TNode.getAttribute("LotNumber"); //Trackable ID
		var LotAttribute1 = TNode.getAttribute("LotAttribute1");
		var LotAttribute2 = TNode.getAttribute("LotAttribute2"); //Owner Unit ID
		var LotAttribute3 = TNode.getAttribute("LotAttribute3"); //Model
		var RevisionNo = TNode.getAttribute("RevisionNo"); //DLT
		var ManufacturingDate = TNode.getAttribute("ManufacturingDate");

		if(ManufacturingDate.length == 10)
		{
			var sYear = ManufacturingDate.substring(0,4);
			var sMon = ManufacturingDate.substring(5,7);
			var sDate = ManufacturingDate.substring(8,10);
			ManufacturingDate = sMon + "/" + sDate + "/" + sYear ;
		}
		
		var TRow = elem.parentElement.parentElement;
		//alert("TRow.innerHTML"+TRow.innerHTML);
		var TList = TRow.getElementsByTagName("input");
		for (tcnt=0;tcnt<TList.length;tcnt++)
		{
			if(TList[tcnt].name.indexOf('@SecondarySerialNo') != -1)
		    {
		     TList[tcnt].value=SecSerial;
		     }//end if
			if(TList[tcnt].name.indexOf('@LotAttribute1') != -1)
		     {
		       TList[tcnt].value=LotAttribute1;
		     }//end if
	        if(TList[tcnt].name.indexOf('@LotAttribute2') != -1)
		     {
		       TList[tcnt].value=LotAttribute2;
		     }//end if
	        if(TList[tcnt].name.indexOf('@LotAttribute3') != -1)
		     {
		       TList[tcnt].value=LotAttribute3;
		     }//end if
	        if(TList[tcnt].name.indexOf('@RevisionNo') != -1)
		     {
			   TList[tcnt].value=RevisionNo;
		     }//end if
			 if(TList[tcnt].name.indexOf('@ManufacturingDate') != -1)
		     {
			  TList[tcnt].value=ManufacturingDate;
			 }//end if
			 if(TList[tcnt].name.indexOf('@LotNumber') != -1)
		     {
		      TList[tcnt].value=LotNumber;
		     }//end if
		}

		return true;
    }
	else
	{
	  alert("TRACKABLE ID NOT AT NODE !!!");
	  return false;
	}//End IF 
 return true;
}//PopupMessage


function populateItemDetails(elem,xmlDoc)
{
		
	var nodes=xmlDoc.getElementsByTagName("Item");

	var strPC = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	var iTotalSerial = 0 ;
	// populating the values
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		
		strUOM = item.getAttribute("UnitOfMeasure");
		
		var primNode = xmlDoc.getElementsByTagName("PrimaryInformation");
		
		if(primNode != null && primNode.length > 0)
		{
			var elemprimNode = primNode(0);
			strPC = elemprimNode.getAttribute("DefaultProductClass") ;
		
			strDesc = elemprimNode.getAttribute("ShortDescription") ;
		
			iTotalSerial = elemprimNode.getAttribute("NumSecondarySerials");
		
		}
		
		if(strPC == null && strUOM == null)
		{
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
		}
	}
	var currentRow = elem.parentNode.parentNode;
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 )
	{
		//alert(strDesc);
		label(0).innerText= strDesc;
	}
	
	InputList = currentRow.getElementsByTagName("SELECT");
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
	
	// changes for tag attributes
	var nlInventoryParameters = xmlDoc.getElementsByTagName("InventoryParameters");
	var strIsSerialTracked = "N";
	var strTimeSensitive = "N" ;
	if(nlInventoryParameters  != null && nlInventoryParameters.length > 0)
	{

		var elemInventoryParameters = nlInventoryParameters(0)
		strIsSerialTracked = elemInventoryParameters.getAttribute("IsSerialTracked");
		
		strTimeSensitive = elemInventoryParameters.getAttribute("TimeSensitive");
	}
	
	var strId = currentRow.getAttribute("id");

	// all serial numbers will goto one column
	// total there are 8 columns starting from index 0 
	var iCurrentColumn = 8 ;
	// first of all blank out all the columns which are generated dynamically
	for(var cellIndex = 8 ; cellIndex < currentRow.cells.length ; cellIndex++)
	{
		// blanking out everything from the cell before we start displaying them
		currentRow.cells[cellIndex].innerHTML = "" ;
	}

	// if the item is serially tracked we need to add one label and one text field
	if(strIsSerialTracked == "Y")
	{
		var x = currentRow.insertCell(iCurrentColumn);
		iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
		
		x.Style = 'width:100px';
		
		// the old serial number will be used for dekitting, while new one will be used to kitting
		x.innerHTML = 'Old Trackable ID# <br> <input class=unprotectedinput type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+strId+'/@OldSerialNo"><br><br><br><br><br>' ;

	}
	// add all the secondary serials in the list, each sec serials will appear in seperate column
	for(var index = 0 ; index < parseInt(iTotalSerial) ; index++)
	{
		var x = currentRow.insertCell(iCurrentColumn);
		
		x.Style = 'width:100px';
		
		var displayIndex = parseInt(index) + 1 ;

		x.innerHTML ='<br>New Trackable ID# <br> <input type="text" class=unprotectedinput name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+strId+'/@SerialNo" onblur="fetchDataWithParams(this,\'getSerialList\',SetTagSerials,setParamForSerialNo(this));"><br>Manufacturers Serial# '+displayIndex+'<br> <input type="text" class=unprotectedinput name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+strId+'/@SecondarySerialNo'+index+'"><br><br><br><br>';
		
		// adding this just to make sure we are displaying the tag attributes in new lines 
		iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
	}
	
	var iDisplayedAttributes = 0 ;
	// get all the attributes - attributes are being controlled by the template
	var nlInventoryTagAttributes =	xmlDoc.getElementsByTagName("InventoryTagAttributes");
	if(nlInventoryTagAttributes != null && nlInventoryTagAttributes.length > 0)
	{
		var elemInventoryTagAttributes = nlInventoryTagAttributes(0);
		var attributes = elemInventoryTagAttributes.attributes;
		// counter to keep track of total number of atributes displayed on UI - two per column

		for(var indexAttr = 0 ; indexAttr < attributes.length ; indexAttr++)
		{
			var elemAttr = attributes(indexAttr);
			if(elemAttr != null)
			{
				var strAttr = elemAttr.nodeName;
				var strAttrName = strAttr ;
				if(strAttr == "BatchNo")
					strAttrName = "Batch#";
				if(strAttr == "LotAttribute1")
					strAttrName = "Manufacturer Name";
				if(strAttr == "LotAttribute2")
					strAttrName = "Owner Unit ID";
				if(strAttr == "LotAttribute3")
					strAttrName = "Manufacturer Model";
				if(strAttr == "LotNumber")
					strAttrName = "Trackable ID";
				if(strAttr == "ManufacturingDate")
					strAttrName = "Manufacturing Date";
				if(strAttr == "DateLastTested")
					strAttrName = "Revision#";

//				alert(elemAttr.nodeValue);
				// display if and only if the item is tag controlled by this tag
				// value 03 means the item is not tag controlled
				if(elemAttr.nodeValue != "03")
				{

					// this item is tagged on this tag identifier
					if(iDisplayedAttributes > 2)
					{
						// changes to control the flow of the conponents on the screen
						iDisplayedAttributes = 0;
						iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
					}
					// will hold on the newly created row or the old one
					var x ;
					if(iDisplayedAttributes == 0)
					{
						// this is the first item create a new column
						x = currentRow.insertCell(iCurrentColumn);
						x.Style = 'width:100px;class:tablecolumn';
					}
					else
					{
						// append the attribute on the same column
						x = currentRow.cells(iCurrentColumn);
					}
					// append the string with attribute name and the corresponding value
					x.innerHTML = x.innerHTML + '<br>' + strAttrName+ '<br><input class=unprotectedinput type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+strId+'/WorkOrderComponentTag/@'+strAttr+'">';
					
					// incriment the displayed attribute counter
					iDisplayedAttributes = iDisplayedAttributes + 1 ;	
		
				}
			
			} //if(elemAttr != null)
		}
	}
	
	// add the ship by date tag if the item is time sensitive
	if(strTimeSensitive == "Y")
	{
		if(iDisplayedAttributes > 2)
		{
			// changes to control the flow of the conponents on the screen
			iDisplayedAttributes = 0;
			iCurrentColumn = parseInt(iCurrentColumn) + 1 ;
		}
		

		var x ;
		if(iDisplayedAttributes == 0)
		{
			// this is the first item create a new column
			x = currentRow.insertCell(iCurrentColumn);
			x.Style = 'width:100px;class:tablecolumn';
		}
		else
		{
			// append the attribute on the same column
			x = currentRow.cells(iCurrentColumn);
		}
		// append the string with attribute name and the corresponding value
		x.innerHTML = x.innerHTML + '<br> Ship By Date <br><input class="dateinput" type="text" name="xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+strId+'/@ShipByDate"><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="/smcfs/console/icons/calendar.gif" name=search/>';
	}
}

/*
this will be called when ever the user clicks on replace component check box
*/
function setReplaceComponentValue(element)
{
	var currentRow = element.parentNode.parentNode;
	var list = currentRow.getElementsByTagName("input");
	var elemReplaceComponent = null ;
	
	for(var index = 0 ; index < list.length ; index++)
	{
//		alert('index::'+index);
		var elem = list[index];
		if(elem.name.indexOf("/@ReplaceComponent") != -1)
		{
			elemReplaceComponent = elem ;
		}
	}
	//var x = document.getElementsByName('xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@CheckBox');
	if(element.checked)
	{
		elemReplaceComponent.value="Y";
	}
	else
	{
		elemReplaceComponent.value="N";
	}
}

//Suryasnat: added function for issue 502
function setReplaceComponentValueForRefurbLines(element)
{
	//Suryasnat: added for 502 issue
	//var row = getParentObject(element, "TR");
	//var rIndex = row.rowIndex;
	var rIndex="";
	var PItemRow = element.parentElement;
   	var PItemList = PItemRow.getElementsByTagName("input");

   	if(PItemList[0].name.indexOf('@Count') != -1)
	    {
		 rIndex = PItemList[0].value;
		}

    //alert("Row index:"+rIndex);
	//alert(rIndex);
	
	var currentRow = element.parentNode.parentNode;
	var list = currentRow.getElementsByTagName("input");
	var elemReplaceComponent = null ;
	
	for(var index = 0 ; index < list.length ; index++)
	{
//		alert('index::'+index);
		var elem = list[index];
		if(elem.name.indexOf("/@ReplaceComponent") != -1)
		{
			elemReplaceComponent = elem ;
		}
	}
	var x = document.getElementsByName('xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+rIndex+'/@CheckBox');
	if(element.checked)
	{
		//alert('Y');
		x[0].value = 'Y';
		//elemReplaceComponent.value="Y";
	}
	else
	{
		//alert('N');
		x[0].value = 'N';
		//elemReplaceComponent.value="N";
	}
}


/*
Invoked when ever user tabs out from item id field, displays/disables the check box based on user selection
*/
function validateComponentItem(element)
{
	prepareComponentMap();

	var currentRow = element.parentNode.parentNode;
	var list = currentRow.getElementsByTagName("input");
	var elemKitQty = null ;
	var elemCheckBox = null ;
	var elemReplaceComponent = null ;

	for(var index = 0 ; index < list.length ; index++)
	{
		var elem = list[index];
		if(elem.name.indexOf("/@KitQuantity") != -1)
		{
			elemKitQty = elem ;
		}

		if(elem.name.indexOf("/@CheckBox") != -1)
		{
			elemCheckBox = elem ;
		}

		if(elem.name.indexOf("/@ReplaceComponent") != -1)
		{
			elemReplaceComponent = elem ;
		}
	}

	if(eval(returnMap[element.value]))
	{
		elemKitQty.value = returnMap[element.value];
		elemCheckBox.disabled = false;
		elemReplaceComponent.value = 'N';
	}
	else
	{
		elemCheckBox.disabled=true;
		elemKitQty.value = "";
	}
}

// Suryasnat: Added function to validate component items of already published components.
function validateComponentItemForRefurbLines(element,count)
{
	//Suryasnat: we need to make a change in this function.
	var varcount = count;
	
	prepareComponentMap();

	var currentRow = element.parentNode.parentNode;
	
	var list = currentRow.getElementsByTagName("input");

	
	var elemKitQty = null ;
	var elemCheckBox = null ;
	var elemReplaceComponent = null ;

	for(var index = 0 ; index < list.length ; index++)
	{
		var elem = list[index];
	
		if(elem.name.indexOf("/@KitQuantity") != -1)
		{
			elemKitQty = elem ;
		}
	
		if(elem.name.indexOf("/@CheckBox") != -1)
		{
			elemCheckBox = elem ;
		}
	
		if(elem.name.indexOf("/@ReplaceComponent") != -1)
		{
			elemReplaceComponent = elem ;
		}
	}

	if(eval(returnMap[element.value]))
	{
		
		var x = document.getElementsByName('xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+varcount+'/@CheckBox');
	
		x[0].disabled=false;
		
		elemKitQty.value = returnMap[element.value];
	
		//elemReplaceComponent.value = 'N';
		x[0].value = 'N';
	}
	else
	{
		var x = document.getElementsByName('xml:/WorkOrder/WorkOrderComponents/WorkOrderComponent_'+varcount+'/@CheckBox');

		x[0].disabled=true;

		elemKitQty.value = "";
		
		
	}

}



function sumRefurbCost(elem, xmlDoc){

		
	var qtyToRefurb=parseInt(document.getElementById("xml:/WorkOrder/@QuantityRequested").value);
	var qtyToRefurbBox=document.getElementById("xml:/WorkOrder/@QuantityRequested");
	var actualRefurb=parseInt(document.getElementById("xml:/WorkOrder/NWCGMasterWorkOrderLine/@ActualQuantity").value);
	var refurbQty=parseInt(document.getElementById("xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbishedQuantity").value);

	//alert("refurbQty is: " + refurbQty);
	if (!(parseInt(refurbQty) >= 0) || refurbQty == null)
	{
		//alert("setting refurbQty to 0");
		refurbQty = 0;
	}

	if (!(parseInt(qtyToRefurb) >= 1) || qtyToRefurb == null)
	{
		//alert("qtyTorefurb is not >= 1 or is null " + qtyToRefurb);
		alert("Please enter a valid quantity to be refurbished.");
		qtyToRefurbBox.focus();
		return false;
		
	}
	
	if (!(parseInt(actualRefurb) >= 0) || actualRefurb == null)
	{
		
		//alert("actualRefurb is not >= 0 or is null " + actualRefurb);
		alert("Actual Quantity to be refurbished is invalid.");
		return false;
	}

	var totalRefurb = parseInt(qtyToRefurb) + parseInt(refurbQty);

	if (parseInt(totalRefurb) > parseInt(actualRefurb))
	{
		
	//	alert("totalRefurb is > actual Refurb " + totalRefurb + "  " + parseInt(actualRefurb));
		alert("Quantity to Refurb can not be greater than the actual quantity");
		return false;
		
	}
	else{

		document.getElementById("xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbishedQuantity").value = totalRefurb;

	}


	var initialRefurbCost=document.getElementById("xml:/NWCGMasterWorkOrderLine/@RefurbCost").value;
	
	if (Number(initialRefurbCost) >= 0)
	{
		var totalRefurbCost = Number(initialRefurbCost);
		
	}
	else {
		var totalRefurbCost = 0;
	}

	nodes=document.getElementsByTagName("input");
		if(nodes != null && nodes.length > 0 )
		{	
			var item = nodes(0);		
			var itemValue = item.getAttribute("input");
			//var times = 1;
			
				
				for(var index = 0 ; index < nodes.length ; index++)

            {
                var elem = nodes[index];

                        if(elem.name.indexOf("/Extn/@RefurbCost") != -1)

                        {
							if (elem.value >= 0)
							{
							}
							else{
							alert("An invalid Refurb Cost has been entered.");
							return false;
							}

							totalRefurbCost = Number(totalRefurbCost) + Number(elem.value);
							//alert("I have been here " + times + " times./nTotal refurb cost is now: " + totalRefurbCost);
							//times++;
                                   // Total cost = Elem.value + total cost
                                   
                       }

            }
			document.getElementById("xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbCost").value = totalRefurbCost;
			
		}
return true;
}

