function setItemParam(ele,node,from)
{
	var supplier = document.getElementById("SUPPLIER");
	var returnArray = new Object();
	var elemNode = document.getElementById(node);

	if(eval(elemNode))
	{
		returnArray["xml:ShipNode"] = elemNode.value
	}
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

function setItemParamForWO(ele)
{
	var eleItemID = document.getElementById("xml:/WorkOrder/@ItemID");
	var returnArray = new Object();
	returnArray["xml:ItemID"] = eleItemID.value;
	returnArray["xml:Uom"] = ele.value;
	return returnArray;
}

function setItemParamQtyAvailToBuild(ele)
{
	var eleItemID = document.getElementById("xml:/WorkOrder/@ItemID");
	var eleUom = document.getElementById("xml:/WorkOrder/@Uom");
	var eleNode = document.getElementById("xml:/WorkOrder/@NodeKey");
	var returnArray = new Object();
	returnArray["xml:ItemID"] = eleItemID.value;
	returnArray["xml:Uom"] = eleUom.value;
	returnArray["xml:PC"] = ele.value;
	returnArray["xml:Node"] = eleNode.value;
	return returnArray;
}

function setIncidentParams(elemYear)
{
	var elemIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var returnArray = new Object();
	returnArray[elemIncidentNo.name] = elemIncidentNo.value;
	returnArray[elemYear.name] = elemYear.value;
	return returnArray;
}

function setIncidentParamsForRefurb(elemYear)
{
	var elemIncidentNo = document.getElementById("xml:/NWCGMasterWorkOrder/@IncidentNo");
	var returnArray = new Object();
	returnArray[elemIncidentNo.name] = elemIncidentNo.value;
	returnArray[elemYear.name] = elemYear.value;
	return returnArray;
}

// this method is used to set the parametes for AJAX call
function setParam(ele,node)
{
	var finalElem = '' ;
	var recvNode = document.getElementById("xml:/Order/@ReceivingNode");
	var shipNode = document.getElementById("xml:/Order/@ShipNode");
	
	//alert("recvNode==>"+recvNode+" shipNode==>"+shipNode);

	if(node != null &&  node  != '' && node != 'undefined')
		finalElem = document.getElementById(node);
	else if(shipNode && shipNode != null)
		finalElem = shipNode;
	else
		finalElem = recvNode;

	var returnArray = new Object();
	returnArray["xml:ShipNode"] = finalElem.value;
	returnArray["xml:ItemID"] = ele.value;
	return returnArray;
}

// generic function, looks out for an save button and calls the yfcCallSave which invokes the save button
function invokeSave()
{
	var elem = document.getElementsByTagName("input");
	for(i=0;i < elem.length;i++)
	{
		if(elem[i].type == 'button' && elem[i].value =='Save')
		{
			yfcCallSave(elem[i]);
		}//end if
	}//end for
}// end function

// function for validating the Request Number
function validateRemote(elem)
{
	var incidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var incidentYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	var orderExtnOrigin = document.getElementById("xml:/Order/Extn/@ExtnSystemOfOrigin");
	var returnArray = new Object();
	//var str = elem.value;
	/*var temp = str.substring(0,2);
	if(temp != 'S-')
		str = 'S-' + str;
	elem.value = str ;
	*/
	returnArray["xml:ExtnRequestNumber"] = elem.value;
	returnArray["xml:ExtnIncidentNo"] = incidentNo.value;
	returnArray["xml:ExtnIncidentYear"] = incidentYear.value;
	returnArray["xml:ExtnSystemOfOrigin"] = orderExtnOrigin.value;
	fetchDataWithParams(elem,'ValidateRequestNumber',SNumberValidationResult,returnArray);
}

// call back function from AJAX
function SNumberValidationResult(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("ValidationResponse");
	if(nodes!=null && nodes.length > 0 )
	{	
		var result = nodes(0);
		var strResult = result.getAttribute("Result");
		if(strResult != null && strResult == 'N')
		{
			alert(result.getAttribute("Message"));
			elem.value = 'S-';
			elem.focus();
			return ;
		}
	}
}

function checkRequestNo(elem, issueType)
{
	var requestNoElement;
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	
	for(i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/@ExtnRequestNo') != -1)
		{
			requestNoElement = InputList[i];
			break;
		}
	}	
	
	if((issueType == 'Normal' || issueType == 'INCDT_REPLACEMENT') && requestNoElement.value.length < 3)
	{
		alert('If Issue Type is \"Normal\" or \"Incident Replacement\", you must enter Request Number.');
		requestNoElement.focus();
	}
}

// base method to be invoked, intern this will check for unique request number locally and on server
function validateRequestNumber(elem)
{
	var str = elem.value;
	
	if(str != null && str != 'undefined' && str.length > 0)
	{
		if (str.length < 3) {
			elem.value='S-';
			return ;		
		}
		
		var regExp = /^[0-9]+$/;
		
		if (str.indexOf('.') != -1) {
			var sysOfOrigin = document.getElementById("xml:/Order/Extn/@ExtnSystemOfOrigin").value;
			if(sysOfOrigin == "ICBSR")
			{
				alert("The sequence portion of the request should not have '.' notation!");
				elem.focus();
				return false;
			}
			var temp = str.slice(2, str.indexOf('.'));
			if (!regExp.test(temp)) {
				alert("The sequence portion of the request must only be numeric!");
				elem.value = 'S-';
				elem.focus();
				return false;
			}								
		}
		else {
			var temp = str.substr(2, str.length-1);
			if (!regExp.test(temp)) {
				alert("The sequence portion of the request must only be numeric!");
				elem.value = 'S-';
				elem.focus();				
				return false;
			}
		}
		
		if(!validateLocally(elem,"/Extn/@ExtnRequestNo"))
		{
			alert('Request Number Already Exists On Issue');
			elem.value = 'S-';
			elem.focus();
			return ;
		}
		else
		{
			//if system of origin is ROSS, ensure that the newly entered
			//request number is a subordinate of an existing request number
			//already on the issue
			var sysOfOrigin = document.getElementById("xml:/Order/Extn/@ExtnSystemOfOrigin");
			if (sysOfOrigin.value != null && sysOfOrigin.value == 'ROSS')
			{
				if (!validateReqContainsDot(elem, "/Extn/@ExtnRequestNo")) {
					alert("You may only enter in subordinate request numbers of an existing Request Number for ROSS initiated issues!");
					elem.value = 'S-';					
					elem.focus();
					return false;
				}
				
				validateRemote(elem);
				
				return;		
			}

			opVal = validateReqInIncidentBlock(elem);
			if (opVal == -1){
				var reqBlockNoStart = document.getElementById("RequestNoBlockStart");
				var reqBlockNoEnd = document.getElementById("RequestNoBlockEnd");

				alert('Request number is not with in the incident block ' + reqBlockNoStart.value + ' and ' + reqBlockNoEnd.value);
				elem.focus();
				return;
			}
			else if (opVal == -2){
				alert('Incident block start and end is not defined.');
				return;
			}
			else {
				validateRemote(elem);
			}
		}// end else
	}// end if str exists
}

// return true if its a valid request number
// return false if its invalid requeste number
function validateLocally(elem,path)
{
	var InputList = document.getElementsByTagName("Input");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].type == 'text' && InputList[i].name.indexOf(path) != -1)
		{
			var current = InputList[i];
			if(current.value == elem.value && current.name != elem.name)
			{
				return false ;
			}
		}//end if
	}
	return true ;
}

function validateReqContainsDot(elem,path)
{
	// a subordinate request, by definition, must have at least 1 dot
	if (elem.value.indexOf('.') == -1) {
		return false;
	}
	return true;
}

//returns true if request number is with in the incident blocks
function validateReqInIncidentBlock(elem)
{
	var reqBlockNoStart = document.getElementById("RequestNoBlockStart");
	var reqBlockNoEnd = document.getElementById("RequestNoBlockEnd");

	if (isNaN(reqBlockNoStart.value)){
		return -2;
	}
	if (isNaN(reqBlockNoEnd.value)){
		return -2;
	}

	var reqNo = elem.value;
	if (reqNo.indexOf('-') != -1)
	{
		reqNo= reqNo.substring(2);
	}
	if (reqBlockNoStart && reqBlockNoEnd){
		if ((parseInt(reqBlockNoStart.value) <= parseInt(reqNo)) &&
		    (parseInt(reqNo) <= parseInt(reqBlockNoEnd.value)))
		{
			return 0;
		}
		else {
			return -1;
		}
	}
	return -1;
}

function populateItemDetails(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strQty = '' ;
	var strUOM = '' ;
	var strDesc = '' ;

	var ItemID = '';
	var strProductLine = '';
	var shipNode = document.getElementById("xml:/Order/@ShipNode");

	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);

		ItemID = elem.value ;

		strPC = item.getAttribute("ProductClass") ;
		strQty = item.getAttribute("AvailableQty") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strDesc = item.getAttribute("ShortDescription");

		strProductLine = item.getAttribute("ProductLine");	

		if(strPC == null && strUOM == null)
		{
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
		}
		
		if(shipNode.value != "IDGBK" && strProductLine =="NIRSC Communications")
		{
			alert('Item ( '+elem.value+' ) can be processed only at IDGBK Cache');
			elem.value = '';
			return false;

		}
	
		var documentType = document.getElementById("DocumentType");
		var orderType = document.getElementById("OrderType");
		var sysOfOrigin = document.getElementById("xml:/Order/Extn/@ExtnSystemOfOrigin");
		if ((documentType.value == '0001' || documentType.value == '0007.ex') && (orderType.value != 'Refurbishment'))
		{
			strRossResourceItem = item.getAttribute("ExtnRossResourceItem");
			if ((strRossResourceItem == 'Y') && (sysOfOrigin.value == 'ICBSR')){
				strItemId = item.getAttribute("ItemID");
				alert('Only non ROSS tracked resource items can be placed as part of ICBSR initiated issue. Invalid item id ' + elem.value + '. Please enter non ROSS tracked resource item');
				elem.focus();
				return;
			}
		}
	}
	
	var currentRow = elem.parentNode.parentNode;
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 )
	{
		label(0).innerText= strDesc;
		//label(1).innerText = strQty;
	}
	
	var InputList = currentRow.getElementsByTagName("Input");
	var availRFIValue='';
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ExtnQtyRfi') != -1)
		{
			if (strQty.indexOf('.') != -1){
				InputList[i].value = strQty.substring(0, strQty.indexOf('.'));
			}
			else {
				InputList[i].value = strQty;
			}
			availRFIValue = InputList[i].value;
		}//end if

		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Item/@ProductClass') != -1)
		{
			InputList[i].value = strPC;
		}//end if
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@TransactionalUOM') != -1)
		{
			InputList[i].value = strUOM;
		}//end if
	}
	
	var availRFIAnchor = currentRow.getElementsByTagName("a");
	if(availRFIAnchor != null && availRFIAnchor != 'undefined' && availRFIAnchor.length > 0 )
	{
		var rfiParams = 'xml:/Item/@ItemID=' + ItemID + '&xml:/Item/@ProductClass=' + strPC + '&xml:/Item/@TransactionalUOM=' + strUOM;
		availRFIAnchor[0].innerText = availRFIValue;				
		availRFIAnchor[0].onclick = new Function("yfcShowDetailPopupWithParams(\"ISUYOMD095\", \"\", \"900\", \"500\", '" + rfiParams + "');return false;");
	}	
}



function validateIssueInput(elem)
{
	var documentType = document.getElementById("xml:/Order/@DocumentType");
	var shippingType = document.documentElement.getAttribute("SelectedShippingType");
	var extnNavInfo = document.getElementById("xml:/Order/Extn/@ExtnNavInfo");
	var oldNavInfo = document.getElementById("xml:/Order/Extn/@OldExtnNavInfo");
	var addrLine1 = document.getElementById("xml:/Order/PersonInfoShipTo/@AddressLine1");
	var state = document.getElementById("xml:/Order/PersonInfoShipTo/@State");

	var extnWillPickUpName = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpName");
	var extnWillPickUpInfo = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpInfo");
	var extnReqDelDate = document.getElementById("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE");
	
	var extnShippingInstructions = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions");	
	var extnShippingInstrCity = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity");
	var extnShippingInstrState = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState");	
	var status = document.getElementById("xml:/Order/@Status");
	
	// Means it's an issue entry, there is no old nav info
	if (oldNavInfo == null)  {
			oldNavInfo = new Object();
			oldNavInfo.value = 'SHIP_ADDRESS';
	}

	if(documentType != null && documentType != 'undefined' && documentType.value == '0001')
	{
		if (extnReqDelDate == null || extnReqDelDate == 'undefined' || extnReqDelDate.value == '')
		{
			if (!(status != null && status.value == 'Shipped')){
				alert('Please enter Requested Delivery Date and place the order again');
				return false;
			}
		}
	}
	
	if (extnNavInfo == null || extnNavInfo == 'undefined' || extnNavInfo.value == '' || extnNavInfo.value == ' ')
	{
		document.getElementById("xml:/Order/Extn/@ExtnNavInfo").value = 'SHIP_ADDRESS';	
	}
	else if (shippingType == null || shippingType == 'undefined' || shippingType.value == '' || shippingType.value == ' ') {
		shippingType = 'SHIP_ADDRESS';
	}
	
	if (extnNavInfo != null && extnNavInfo != 'undefined' && oldNavInfo !=null && oldNavInfo != 'undefined')
	{
		if (extnNavInfo.value == 'WILL_PICK_UP')
		{
			if (((extnWillPickUpName) && (extnWillPickUpName == 'undefined' || extnWillPickUpName.value =='')) ||
			    ((extnWillPickUpInfo) && (extnWillPickUpInfo == 'undefined' || extnWillPickUpInfo.value =='')) ||
			    ((extnReqDelDate) && (extnReqDelDate == 'undefined' || extnReqDelDate.value ==''))	){
				alert('Please enter WILL PICK UP address details');
				return false;
			}
			
		}
		else if (extnNavInfo.value == 'NAV_INST')
		{
			if (extnShippingInstrCity != null && extnShippingInstrCity != 'undefined' && extnShippingInstrCity.value != '' &&
				extnShippingInstrState != null && extnShippingInstrState != 'undefined' && extnShippingInstrState.value != '')
			{
				return true;
			}
			else {
				alert('Please enter all SHIPPING INSTRUCTION details');
				return false;
			}	
		}
		else if (extnNavInfo.value == 'SHIP_ADDRESS')
		{
				if (((addrLine1) && (addrLine1 == 'undefined' || addrLine1.value == '')) ||
				((state) && (state == 'undefined' || state.value == ''))){
				alert('Please enter SHIP TO address details');
				return false;
			}					
		}
	}
	else {
		alert('Shipping Method is not set for this Issue');
		return false;
	}	
	return true;
}

function populateItemDetailsForWO(elem,xmlDoc)
{
	// CR 556 - check for component for the given itemID and UOM
	var itemIDField = document.getElementById("xml:/WorkOrder/@ItemID");
	var uomField = document.getElementById("xml:/WorkOrder/@Uom");
	var productClassField = document.getElementById("xml:/WorkOrder/@ProductClass");
	var nodes=xmlDoc.getElementsByTagName("Item");
	var ItemID = '';
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		ItemID = item.getAttribute("ItemID");
		var component_node=xmlDoc.getElementsByTagName("Component");
		if (component_node == null || component_node.length == 0)
		{
			if(ItemID != null)
			{
				alert('Item ( '+ItemID+' ) does not have configured components. Please enter a valid Item.');
			} else {
				alert('Item does not have configured components. Please enter valid Item.');
			}
			itemIDField.value = ""; // blank out so user cannot proceed without entering it
			uomField.value = ""; // blank out so user cannot proceed without entering it
			productClassField.value = ""; // blank out so user cannot proceed without entering it
			itemIDField.focus();
			return false;
		}
	} else {
		alert('Item does not exist. Please enter a valid Item.');
		itemIDField.value = ""; // blank out so user cannot proceed without entering it
		uomField.value = ""; // blank out so user cannot proceed without entering it
		productClassField.value = ""; // blank out so user cannot proceed without entering it
		itemIDField.focus();
		return false;
	}
	return true;
}

function populateQtyAvailToBuild(elem,xmlDoc)
{
	// CR 58 - Re-written due to the CR 556 resolution
	var QtyAvailToBuildField = document.getElementById("xml:/WorkOrder/@QtyAvailToBuild");
	var nodes=xmlDoc.getElementsByTagName("Item");
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		QtyAvailToBuildField.value = item.getAttribute("QtyAvailToBuild");
	} else {
		return false;
	}
	return true;
}

function checkForIncidentOrder(elem)
{
	var documentType = document.getElementById("xml:/Order/@DocumentType");
	var isActive = document.getElementById("IS_ACTIVE") ;
	var incidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var issueType = document.getElementById("xml:/Order/@OrderType");
	var customerId = document.getElementById("xml:/Order/@BillToID");
	var customerId1 = document.getElementById("xml:/NWCGIncidentOrder/@CustomerId");
	var NWCGIncidentNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo");

	var FsAcctCode = document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode");
	var FsOverrideCode = document.getElementById("xml:/Order/Extn/@ExtnOverrideCode");
	var BlmAcctCode = document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode");
	var OtherAcctCode = document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode");
	var ShipAcctCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode");
	var SAOverrideCode = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode");
	var objOwnerAgency = document.getElementById("xml:/Order/@OwnerAgency");	
    var specialChars = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?~_"; 

	// for normal issues (document type sales order), the incident number is mandatory
	// Jay : As per the CR # 317 - incident no, incident name, incdt type and customer id shuld be mandatory

	// CR 600 NEW VALID CUSTOMER VARIABLE ***************************************
	// Commented by gaurav as this attribute is not present on other order entry screen and moreover this variable is not being used anywhere in this js.
	//var isValidCustomer = document.getElementById("xml:/Order/@InvalidCustomer");
	//****************************************************************************************

	if(documentType!= null && documentType != 'undefined' && documentType.value == '0001')
	{
		if(incidentNo != null && incidentNo != 'undefined')
		{
			if(incidentNo.value == '' )
			{
				alert('Please enter the Incident #');
				incidentNo.focus();
				return false;
			}
		}
	}

	if(isActive)
	{
		if(isActive.value == 'N')
		{
			alert('Incident not Active');
			incidentNo.focus();
			return false;
		}
	}
	
	if(customerId != null && customerId != 'undefined')
	{
		if(customerId.value == '')
		{
			alert('Please enter a Customer ID');
			customerId.focus();
			return false;
		}
	}

	if(customerId1 != null && customerId1 != 'undefined')
	{
		if(customerId1.value == '')
		{
			alert('Please enter a Customer ID');
			customerId1.focus();
			return false;
		}
	}
	
	if (issueType != null && issueType != 'undefined')
	{
		if(issueType.value == '')
		{
			//alert("ExtnOwnerAgency.value " + ExtnOwnerAgency.value);
			alert('Please select an Issue Type');
			issueType.focus();
			return false;
		}
	}

	// validate the account code combination //
	// FS --> FS acct code, ORcode, SA, SAORcode
	// BLM -> BLM acct code, SA
	// Othr-> Oth acct coce, SA

	// FS acct
	var validAcctCode = false;
	if(FsAcctCode != null && FsAcctCode != 'undefined'){
		if(FsAcctCode.value != ''){
			if(FsOverrideCode != null && FsOverrideCode != 'undefined'){
				if(FsOverrideCode.value != ''){
					if(ShipAcctCode != null && ShipAcctCode != 'undefined'){
						if(ShipAcctCode.value != ''){
							if(SAOverrideCode != null && SAOverrideCode != 'undefined'){
								if(SAOverrideCode.value != ''){
									validAcctCode = true;
								}else {
								
									if(objOwnerAgency.value == 'FS'){
									alert('Shipping Account Override Code is not entered');
									SAOverrideCode.focus();
									return false;
									}
								}
							}
						}else{
							alert('Shipping Account Code is not entered');
							ShipAcctCode.focus();
							return false;
						}
					}
				}else{
					alert('FS Override Code is not entered');
					FsOverrideCode.focus();
					return false;
				}
			}
		}
	}
	// BLM acct
	if(!validAcctCode && (BlmAcctCode != null && BlmAcctCode != 'undefined')){
		if(BlmAcctCode.value != ''){
			if(ShipAcctCode != null && ShipAcctCode != 'undefined'){
				if(ShipAcctCode.value != ''){
					validAcctCode = true;
				}else {
					alert('Shipping Account Code is not entered');
					ShipAcctCode.focus();
					return false;
				}
			} 
		}
	}

	// Other acct
	if(!validAcctCode && (OtherAcctCode != null && OtherAcctCode != 'undefined')){
		if(OtherAcctCode.value != ''){
			if(ShipAcctCode != null && ShipAcctCode != 'undefined'){
				if(ShipAcctCode.value != ''){
					validAcctCode = true;
				}else {
					alert('Shipping Account Code is not entered');
					ShipAcctCode.focus();
					return false;
				}
			}
		}
	}

    //if objOwnerAgency is BLM BLMAcctCode is mandatory
    //alert("objOwnerAgency = "+objOwnerAgency.value);
	if(objOwnerAgency != null && objOwnerAgency.value == 'BLM') 
	{
		if(BlmAcctCode.value == '') {
			alert("BLM Account Code is Null! Please enter in the FBMS elements to create the BLM Acct Code");
			return false;
		}
	}
	else if(objOwnerAgency != null && objOwnerAgency.value == 'FS')
	{
		if((FsAcctCode.value == '') || (FsOverrideCode.value == '')) {
		                alert("FS Account Code or FS Override Code is Null!");
			return false;
		}
	}
	else 
	{
		if(OtherAcctCode != null && OtherAcctCode.value == ''){
			alert("Other Account Code is Null!");
			return false;		
        }
	}

	//CR 589 , cost center , fa , wbs check
	var costCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter");
	var FunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea");
	var WBS = document.getElementById("xml:/Order/Extn/@ExtnWBS");

	if(objOwnerAgency != null && objOwnerAgency.value == 'BLM') 
	{
		if(costCenter.value == '' || FunctionalArea.value == ''){
			alert("Cost Center or Functional Area is Null !!!");
			return false;
		}
		var strCostCenter = costCenter.value;
		var strLL = strCostCenter.substring(0,2);

		if(strCostCenter != null && strCostCenter != '') { 
			for (var i = 0; i < strCostCenter.length; i++) {
				if (specialChars.indexOf(strCostCenter.charAt(i)) != -1) {
				alert ("Please do not enter special characters for Cost Center code.");
				return false;
				}
	        }
			if((strLL != "LL") || (strCostCenter.length != 10))
			{
				alert("Enter a ten digit code for Cost Center that begins with LL");
				return false;
			}
		}

		var strFunctionalArea = FunctionalArea.value;
		var strSubStringFA = strFunctionalArea.substring(0,1);
		var strDotFA = strFunctionalArea.substring(9,10);
		if(strFunctionalArea != '') { 
			for (var i = 0; i < strFunctionalArea.length; i++) {
				if (specialChars.indexOf(strFunctionalArea.charAt(i)) != -1) 
				{	
					if(!(i==9 && strFunctionalArea.charAt(i) == "." ))
					{
						alert ("Please do not enter special characters for Functional Area code.");
						return false;
					}
				}
			}
		
			if((strSubStringFA != "L") || (strDotFA != ".") || (strFunctionalArea.length != 16))
			{
				alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
				return false;
			}
		}

		//WBS	
		var strWBS = WBS.value;
		var strSubStringWBS = strWBS.substring(0,1);
		
		if(strWBS != null && strWBS != '') { 
			for (var i = 0; i < strWBS.length; i++) {
				if (specialChars.indexOf(strWBS.charAt(i)) != -1) 
				{
					alert ("Please do not enter special characters for WBS code.");
					return false;
				}
			}
		
			if((strSubStringWBS != "L") || (strWBS.length != 12))
			{
				alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
				return false;
			}
		}
	}//end if OwnerAgency is BLM
	
	var extnNavInfo = document.getElementById("xml:/Order/Extn/@ExtnNavInfo");

	if (extnNavInfo != null && extnNavInfo != 'undefined' && (extnNavInfo.value == extnNavInfo.getAttribute('OldValue'))) {
		return validateIssueInput(elem);
	}
	else {
		return true;
	}
}

function updateBillToaddress(elem,xmlDoc){
	return updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","BillingPersonInfo");
}

// the incident details updater
function updateIncidentDetails(elem,xmlDoc){
	var docType = document.getElementById("xml:/Order/@DocumentType");
	var incNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var incYr = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	//alert("in updateIncidentDetails() No/Yr = ["+incNo.value+"/"+incYr.value+"]");
	//alert("in updateIncidentDetails() - docType: " + docType.value);
	//alert("incNo: " + incNo.value);
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	if(nodes.length == 0)
	{
		if (docType.value == '0007.ex'){
			var answer = confirm('This is an invalid Order#, would you like to create an auto-generated Other Order #?');
			if (answer)
			{
				var iparams = "xml:/Order/Extn/@ExtnIncidentNo=" + incNo.value;
				iparams += "&xml:/NWCGIncidentOrder/@iCreateNewIncident=Y";
				yfcShowDetailPopup('OTHNWCGYOMD040',"",1030,650,iparams,'OTHNWCGIncident');
				nodes = "" ;
				return;
			} else { // answered No here
				nodes = "" ;
				return;
			}			
			// alert('Other Order# cannot be blank. Please enter valid Other Order Number');
		}
		else if (docType.value == '0001'){
			var answer = confirm('Incident may exist in ROSS would you like to check?');
			if (answer)
			{
				var iparams = "xml:/NWCGIncidentOrder/@IncidentNo=" + incNo.value;
				iparams += "&xml:/NWCGIncidentOrder/@IncidentYear=" + incYr.value;
				iparams += "&xml:/NWCGIncidentOrder/@iCreateNewIncident=Y";
				iparams += "&xml:/NWCGIncidentOrder/@IncidentAction=CREATE";
				yfcShowDetailPopupWithParams('NWCGYOMD040',"",1030,650,iparams,'NWCGIncident',"",1);
				//yfcShowDetailPopupWithParams('NWCGYOMD050',"",1030,650,iparams,'NWCGIncident',"",1);
				nodes = "" ;
				return;
			} else { // answered No here
				nodes = "" ;
				return;
			}
		}
		nodes = "" ;
	}
	var CType = nodes(0);
	var isOtherOrder = CType.getAttribute("IsOtherOrder");
	if(isOtherOrder == "Y")
	{
		//alert("elem.name from updateIncidentDetails is: " + elem.name);
		populateCustomerInfo(elem,nodes,"xml:/Order/Extn/@ExtnCustomerName");
	
		//moved this below code to the beginning of this function
		//var docType = document.getElementById("xml:/Order/@DocumentType");
		if(nodes!=null && nodes.length >0 ) {	
			var incidentOrder = nodes(0);
	
			var active = incidentOrder.getAttribute("IsActive")
			
			var isActive = document.getElementById("IS_ACTIVE") ;
			
			if(isActive)
				isActive.value = active ;
	
			//populate the address
			var obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentName") ;
	
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentName"));
	
			obj = document.getElementById("xml:/Order/Extn/@ExtnPoNo");
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerPONo"));
	
			obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentType");
			if(obj && checkStringForNull(incidentOrder.getAttribute("OtherOrderType")) != "")
			{
				obj.value = checkStringForNull(incidentOrder.getAttribute("OtherOrderType"));
			}	
			else if(obj && checkStringForNull(incidentOrder.getAttribute("IncidentType")) != "")
			{
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentType"));
			}
			obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentTeamType");
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentTeamType"));
	
			objFS = document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode") ;
			if(objFS)
				objFS.value = checkStringForNull(incidentOrder.getAttribute("IncidentFsAcctCode"));
	
			objBLM = document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode");
			if(objBLM)
				objBLM.value = checkStringForNull(incidentOrder.getAttribute("IncidentBlmAcctCode"));
	
			objOther = document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode") ;
			if(objOther)
				objOther.value = checkStringForNull(incidentOrder.getAttribute("IncidentOtherAcctCode"));
	
			obj = document.getElementById("xml:/Order/@BillToID") ;
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));
	
			//populate ShipNode for Ship_Cache field
			obj = document.getElementById("xml:/Order/@ShipNode") ;
			if(obj)
				//obj.value = checkStringForNull(incidentOrder.getAttribute("PrimaryCacheId"));
			//Suryasnat: commented for issue 518
		
			obj = document.getElementById("xml:/NWCGIncidentOrder/@CustomerId");
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));
	
			obj = document.getElementById("xml:/Order/@BuyerOrganizationCode") ;
			if(obj)
				obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));
	
			objOverRide = document.getElementById("xml:/Order/Extn/@ExtnOverrideCode") ;
	
			if(objOverRide)
				objOverRide.value = checkStringForNull(incidentOrder.getAttribute("OverrideCode"));
	
			objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter") ;
			if(objCostCenter)
				objCostCenter.value = checkStringForNull(incidentOrder.getAttribute("CostCenter"));
					
			objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea") ;
			if(objFunctionalArea)
				objFunctionalArea.value = checkStringForNull(incidentOrder.getAttribute("FunctionalArea"));
						
			objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS") ;
			if(objWBS)
				objWBS.value = checkStringForNull(incidentOrder.getAttribute("WBS"));
	
	
			obj = document.getElementById("xml:/Order/@OwnerAgency") ;
			objShipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode") ;
			objSAOverRide = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode") ;
	
			if(obj.value == 'FS')
			{
				// extract the FS Account code
				objShipCode.value = objFS.value;
				objSAOverRide.value = objOverRide.value ;
			}
			else if(obj.value == 'BLM')
			{
				// extract the BLM Account code
				objShipCode.value = objBLM.value ;
				objSAOverRide.value = objShipCode.value ;
	
				//Suryasnat: Added for Issue 469
				objSAOverRide.value = "";
			}
			else if(obj.value == 'OTHER')
			{
				objShipCode.value = objOther.value ;
			}
			updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","YFSPersonInfoShipTo");
			updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","YFSPersonInfoBillTo");
			
			// Code to populate Shipping Instructions
			var strShipInst =  incidentOrder.getAttribute("ShippingInstructions");
			var strShipStrCity = incidentOrder.getAttribute("ShipInstrCity");
			var strShipStrState = incidentOrder.getAttribute("ShipInstrState");
			
			obj = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions") ;
			if(obj && strShipInst != null)
				obj.value = strShipInst;
	
			obj = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity") ;
			if(obj && strShipStrCity != null)
				obj.value = strShipStrCity;
	
			obj = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState") ;
			if(obj && strShipStrState != null)
				obj.value = strShipStrState;
		}
	} else 
	{
		alert("Please Enter an Other Order Number");
	}
	return false;
}

function updateIncidentDetailsAcctsOnly(elem,xmlDoc)
{
		nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		
		var screenIncNumber = document.getElementById("xml:/NWCGMasterWorkOrder/@IncidentNo").value;
		var screenIncYear = document.getElementById("xml:/NWCGMasterWorkOrder/@IncidentYear").value;
	
		if(nodes.length == 0)
		{
			alert('Please enter a valid Incident# and Year');
			nodes = "" ;
		}
		
		if(nodes!=null && nodes.length >0 )
		{
		
			for (x = 0; x < nodes.length; x++)
			{
				var incidentOrder = nodes(x);
				var incNo = incidentOrder.getAttribute("IncidentNo");
				var incYr = incidentOrder.getAttribute("Year");

				if(incYr != null && incNo != null)
				{
					//alert("x is: " + x + "\nincYr is: " + incYr + "\nDocument year is: " + screenIncYear + "\nincNo is: " + incNo + "\nDocument number is: " + screenIncNumber);
					if (incYr==screenIncYear && incNo==screenIncNumber)
					{
				
						var active = incidentOrder.getAttribute("IsActive");
						var incName = incidentOrder.getAttribute("IncidentName");
						var incNo = incidentOrder.getAttribute("IncidentNo");
						var fsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
						var overrideCode = incidentOrder.getAttribute("OverrideCode");
						var blmAcctCode = incidentOrder.getAttribute("IncidentBlmAcctCode");
						var otherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");
			
			
						var isActive = document.getElementById("IS_ACTIVE") ;
						
						if(isActive)
							isActive.value = active ;
			
						var obj = document.getElementById("xml:/NWCGMasterWorkOrder/@IncidentName") ;
						if(obj)
							obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentName"));
						
						obj = document.getElementById("xml:/NWCGMasterWorkOrder/@FSAccountCode") ;
						if(obj)
							obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentFsAcctCode"));
						
						obj = document.getElementById("xml:/NWCGMasterWorkOrder/@OverrideCode");
						if(obj)
							obj.value = checkStringForNull(incidentOrder.getAttribute("OverrideCode"));
			
						obj = document.getElementById("xml:/NWCGMasterWorkOrder/@BLMAccountCode") ;
						if(obj)
							obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentBlmAcctCode"));
			
						obj = document.getElementById("xml:/NWCGMasterWorkOrder/@OtherAccountCode");
						if(obj)
							obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentOtherAcctCode"));
			
						break;
					}
				}				
			}
			//var incidentOrder = nodes(0);
	}
}

//helper function to conver the date format
function formatDate(dateStr)
{
	if(dateStr == '')
		return dateStr ;
	var splitDate = dateStr.split('-');
	var time = splitDate[2];
	if(time != '' && time != 'undefined' && time != null)
	{
		splitTime = time.split('T');
	}

	return splitDate[1]+"/"+splitTime[0]+"/"+splitDate[0];
}

// check if the user has made any changes in screen, submit the form only when user has made any changes
function checkForFormSubmitAndSave()
{
	if(yfcFormHasChanged() == true )
	{
		invokeSave();
	}
}

function setBackOrderFlag(elem)
{
	var inti = elem.value * 1 ;
	var value = "N";
	if( inti > 0)
	{
		value = "Y";
	}
	var bFwdFlagSet = false;

	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ExtnBackOrderFlag') != -1)
		{
			InputList[i].value = value ;

		}//end if
		if(InputList[i].name.indexOf('/Extn/@ExtnForwardOrderFlag') != -1)
		{
			if(InputList[i].value == "Y")
				bFwdFlagSet = true;
		}
	}
	// BR#2 CR 16
	if(bFwdFlagSet && inti > 0)
	{
		alert('Either Forward or Backorder Qty can be entered');
		elem.value = "0";
	}
}

function setForwardOrderFlag(elem)
{
	var inti = elem.value * 1 ;
	var value = "N";
	if( inti > 0)
	{
		value = "Y";
	}
	var bBOFlagSet = false ;
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ExtnForwardOrderFlag') != -1)
		{
			InputList[i].value = value ;
		}//end if
		if(InputList[i].name.indexOf('/Extn/@ExtnBackOrderFlag') != -1)
		{
			if(InputList[i].value == "Y")
				bBOFlagSet = true;
		}
	}
	// BR#2 CR 16
	if(bBOFlagSet && inti > 0)
	{
		alert('Either Forward or Backorder Qty can be entered');
		elem.value = "0";
	}
}

// will be invoked on tab out of serial number on incident to incident transfer
function checkSerialExists(elem,xmlDoc) {
	var serialList = xmlDoc.getElementsByTagName("Serial");
	// serial already exists in the system, dont allow user to enter this serial number
	if(serialList != null && serialList.length == 0) {
		alert("Invalid Trackable # "+elem.value);
		elem.focus();
	} else if(serialList(0).getAttribute("AtNode") == 'Y') {
		alert("Trackable # "+elem.value + " exists at cache "+ serialList(0).getAttribute("ShipNode"));
		elem.focus();
	} 
}

// setting the param before submitting to server
function setSerialNumber(elem)
{
	var returnArray = new Object();	
	returnArray["xml:SerialNo"] = elem.value;
	return returnArray;
}

function validateIncident(elem)
{
	var documentType = document.getElementById("xml:/Order/@DocumentType");
	var isActive = document.getElementById("IS_ACTIVE") ;
	var incidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var shipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode") ;
	var issueType = document.getElementById("xml:/Order/@OrderType");
	
	// for normal issues (document type sales order), the incident number is mandatory
	if(documentType!= null && documentType != 'undefined' && documentType.value == '0001')
	{
		if(incidentNo != null && incidentNo != 'undefined')
		{
			if(incidentNo.value == '' )
			{
				alert('Please enter the Incident #');
				incidentNo.focus();
				return false;
			}
		}

		if(shipCode != null && shipCode != 'undefined')
		{
			if(shipCode.value == '' )
			{
				alert('Please enter the Shipping Account Code#');
				shipCode.focus();
				return false;
			}
		}
	}
	if(isActive)
	{
		if(isActive.value == 'N')
		{
			alert('Incident not Active');
			incidentNo.focus();
			return false;
		}
	}

	if (issueType != null && issueType != 'undefined')
	{
		if(issueType.value == '')
		{
			alert('Please select an Issue Type');
			issueType.focus();
			return false;
		}
	}
	return true;
}

function validateQuantity(elemFwdQty)
{
	var currentRow = elemFwdQty.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var strReqQty = '',strOrdQty ='',strExtnUTFQty='',strExtnBackorderedQty='';
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ExtnOrigReqQty') != -1)
		{
			elemReqQty = InputList[i].value;
		}
		if(InputList[i].name.indexOf('@OrderedQty') != -1)
		{
			strOrdQty = InputList[i].value;
		}
		if(InputList[i].name.indexOf('/Extn/@ExtnUTFQty') != -1)
		{
			strExtnUTFQty = InputList[i].value;
		}
		if(InputList[i].name.indexOf('/Extn/@ExtnBackorderedQty') != -1)
		{
			strExtnBackorderedQty = InputList[i].value;
		}
	}// end for
	//if(((elemFwdQty.value*1)+(strOrdQty*1)+(strExtnUTFQty*1)+(strExtnBackorderedQty*1)) < (elemReqQty*1) )
	if(((elemFwdQty.value*1)+(strOrdQty*1)+(strExtnUTFQty*1)+(strExtnBackorderedQty*1)) != (elemReqQty*1) )
	{
		alert('Issue Qty + UTF Qty + Backordered Qty + Fwd Qty should equal to or greater than Requested Qty');
		return false;
	}
	//return true;
	setForwardOrderFlag(elemFwdQty);
}

function checkMaxQty(qty)
{
//	if (qty.value*1 > 9999){
//		alert('Max quantity should be less than 10000');
// 	Changed for CR 847 Manish K
	if (qty.value*1 > 999999){
		alert('Max quantity should be less than 1000000');
		qty.focus();
		return false;
	}
}

function populateShippingAndSACode(elem)
{
	var objOwnerAgency = document.getElementById("xml:/Order/@OwnerAgency");
	var strOwnerAgency = objOwnerAgency.value ;
	// fs account code
	if(strOwnerAgency == 'BLM')
	{		
		objBLM = document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode") ;
		objShipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode") ;
		objSAOverRide = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode") ;
		objShipCode.value =	objBLM.value ;
		objSAOverRide.value= "" ;

	}
	else if(strOwnerAgency == 'FS')
	{
		objFS = document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode") ;
		objOverRide = document.getElementById("xml:/Order/Extn/@ExtnOverrideCode") ;

		objShipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode") ;
		objSAOverRide = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode") ;

		objSAOverRide.value = objOverRide.value;
		objShipCode.value =	objFS.value ;
	} else 	{

		objOtherCode = document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode") ;
		objShipCode.value =	objOtherCode.value ;
	}	
}

function setOwnerAgency(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("Extn");
	if(nodes!=null && nodes.length > 0 )
	{	
		var result = nodes(0);
		var strExtnOwnerAgency = result.getAttribute("ExtnOwnerAgency");
		var strExtnShippingAccountCode = result.getAttribute("ExtnShipAcctCode");
		var obj = document.getElementById("xml:/Order/@OwnerAgency");
		
		if(strExtnOwnerAgency != null && strExtnOwnerAgency != 'undefined' && strExtnOwnerAgency != 'null')
		{
			if(eval(obj))
				obj.value = strExtnOwnerAgency ; 
		}
		obj = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode");
		if(strExtnShippingAccountCode != null && strExtnShippingAccountCode != 'undefined' && strExtnShippingAccountCode != 'null')
		{			

			if(eval(obj))
				obj.value = strExtnShippingAccountCode ; 
		}
	}
}

function validateTrackableID(elem, xmlDoc) {
	var nodes = xmlDoc.getElementsByTagName("NWCGTrackableItem");

	if( nodes != null && nodes.length > 0 ) {	
		var result = nodes(0);
		var strStatusIncidentNo = result.getAttribute("StatusIncidentNo");		
		var strStatusIncidentYear = result.getAttribute("StatusIncidentYear");
		var strSerialStatus = result.getAttribute("SerialStatus");
		var strSerialStatusDesc = result.getAttribute("SerialStatusDesc");

		var objIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
		var objIncidentYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
		
		var strIncidentNo = objIncidentNo.value;
		var strIncidentYear = objIncidentYear.value;
		
		var currentRow = elem.parentNode.parentNode;
		var InputList = currentRow.getElementsByTagName("Input");
		for (i = 0; i < InputList.length; i++) {
			if(InputList[i].name.indexOf('/Item/@ItemID') != -1) {
				var strItemIDEntered = InputList[i].value;
			}
		}
		var strTrackableIDEntered = elem.value;
		
		var strItemIDStored = result.getAttribute("ItemID");
		var strTrackableIDStored = result.getAttribute("SerialNo");

		if (strStatusIncidentNo == null) {
			elem.focus();
			return false;
		}
		if (strStatusIncidentNo != strIncidentNo || strStatusIncidentYear != strIncidentYear) {
			alert("This Trackable ID is from Incident Number " + strStatusIncidentNo + " in " + strStatusIncidentYear);
			elem.focus();
			return false;
		}
		if (strSerialStatus == 'T') {
			alert("This Trackable ID is intransit in Cache Transfer. Please enter valid Trackable ID.");
			elem.focus();
			return false;
		}
		if(strItemIDEntered != strItemIDStored || strTrackableIDEntered != strTrackableIDStored) {
			alert("The Item ID that you Entered, " + strItemIDEntered + " Does NOT equal the Item ID that is recorded in the database, " + strItemIDStored + 
			". OR the The Trackable ID that you Entered, " + strTrackableIDEntered + " Does NOT equal the Trackable ID that is recorded in the database, " + strTrackableIDStored + 
			". Both have to MATCH in order to continue. Please enter the appropriate Trackable ID for the Item ID that you entered, contact your system administrator if you need further assistance. ");
			elem.focus();
			return false;		
		} 
		if(nodes.length > 1) {
			var result;
			var strItemIDStored = new Array();
			var strTrackableIDStored = new Array();
			for(i = 0; i < nodes.length; i++) {
				result = nodes(i);
				strItemIDStored[i] = result.getAttribute("ItemID");
				strTrackableIDStored[i] = result.getAttribute("SerialNo");
			}
			k = 1;
			for(j = 0; j < strItemIDStored.length; j++) {
				while(k < strItemIDStored.length) {
					if(strItemIDStored[j] == strItemIDStored[k]) {
						if(strTrackableIDStored[j] == strTrackableIDStored[k]) {
							alert("There appears to be 2 or MORE of the same Trackable ID in the database with the SAME Item ID that MATCH the Trackable ID and Item ID that you Entered. " +
									"The system is confused and does not know which one to use, " +
									"hence it is throwing this alert so that it does not try to process them all and crash the server. " +
									"Compare First Item ID: " + strItemIDStored[j] + " with Second Item ID: " + strItemIDStored[k] + " " + 
									"Compare First Trackable ID: " + strTrackableIDStored[j] + " with Second Trackable ID: " + strTrackableIDStored[k] + " " + 
									"Please contact your system administrator in order to correct this duplicate record issue. ");
							elem.focus();
							return false;	
						}
					}
				}
				k++;
			}							
		}
	}
}

// the incident details updater
function updatedIncidentDetails(elem,xmlDoc)
{
	var docType = document.getElementById("xml:/Order/@DocumentType");
	var incNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var incYr = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	//alert("in updateIncidentDetails() No/Yr = ["+incNo.value+"/"+incYr.value+"]");
	//alert("in updateIncidentDetails() - docType: " + docType.value);
	var nodes = xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	//alert("in updateIncidentDetails() nodes.length =[" + nodes.length + "]");
	if(nodes.length == 0)
	{
		if (docType.value == '0007.ex')
		{
			alert('Other Order# cannot be blank. Please enter valid Other Order Number');
		}
		else if (docType.value == '0001')
		{
			var answer = confirm('Incident may exist in ROSS would you like to check?');
			if (answer)
			{
				var iparams = "xml:/NWCGIncidentOrder/@IncidentNo=" + incNo.value;
				iparams += "&xml:/NWCGIncidentOrder/@IncidentYear=" + incYr.value;
				iparams += "&xml:/NWCGIncidentOrder/@iCreateNewIncident=Y";
				iparams += "&xml:/NWCGIncidentOrder/@IncidentAction=CREATE";
				//yfcShowDetailPopupWithParams('NWCGYOMD040',"",1030,650,iparams,'NWCGIncident',"",1);
				yfcShowDetailPopupWithParams('NWCGYOMD050',"",1030,650,iparams,'NWCGIncident',"",1);
				nodes = "" ;
				return true;
			} else { // answered No here
				nodes = "" ;
				incNo.value = "";
				incYr.value = "";
				incNo.focus();
				return false;
			}
		}
		nodes = "" ;
	}
	//alert("elem.name from updateIncidentDetails is: " + elem.name);
	populateCustomerInfo(elem,nodes,"xml:/Order/Extn/@ExtnCustomerName");
	//moved this below code to the beginning of this function

	if(nodes!=null && nodes.length >0 )
	{	
		// We take the first NWCGIncidentOrder node (b/c there really 
		// are 2) bc a NWCGIncidentOrder element exists underneath
		// the NWCGRossAccountCodesList element returned from
		// Get Incident for Issue Creation SDF service
		var incidentOrder = nodes(0);

		var errorDesc = incidentOrder.getAttribute("ErrorDesc");
		if (errorDesc != null && errorDesc != 'undefined'){
			alert(errorDesc);
			return false;
		}
		
		var primaryROSSFinAcctCode = incidentOrder.getAttribute("IsPrimaryROSSFinAcctCode");
		if (primaryROSSFinAcctCode == 'N'){
			var holdVar = document.getElementById("xml:Order/OrderHoldTypes/OrderHoldType/@HoldType");
			if (holdVar) {
				holdVar.value = "NULL_PRIM_FIN_CODE";
			}
			
			holdVar = document.getElementById("xml:Order/OrderHoldTypes/OrderHoldType/@ReasonText");
			if (holdVar) {
				holdVar.value = "Incident doesn't have primary ROSS financial code";
			}
			
			//alert('Primary ROSS financial code is not defined for this Incident. System will place this issue on HOLD until account code issue is resolved');
			alert('Incident does not have Primary ROSS financial code, please enter correct incident to proceed further');
			incNo.focus();
			return false;
		}
		else {
			var financialCode = document.getElementById("xml:/Order/Extn/@ExtnROSSFinancialCode");
			if (financialCode)
				financialCode.value = checkStringForNull(incidentOrder.getAttribute("FinancialCode"));

			var owningAgency = document.getElementById("xml:/Order/Extn/@ExtnROSSOwningAgency");
			if (owningAgency)
				owningAgency.value = checkStringForNull(incidentOrder.getAttribute("OwningAgency"));

			var fiscalYear = document.getElementById("xml:/Order/Extn/@ExtnROSSFiscalYear");
			if (fiscalYear)
				fiscalYear.value = checkStringForNull(incidentOrder.getAttribute("FiscalYear"));
		}	

		var active = incidentOrder.getAttribute("IsActive");		
		var isActive = document.getElementById("IS_ACTIVE") ;
		
		if(isActive)
			isActive.value = active ;

		//populate the address
		var obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentName") ;

		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentName"));

		obj = document.getElementById("xml:/Order/Extn/@ExtnPoNo");
		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerPONo"));

		obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentType");
		if(obj && checkStringForNull(incidentOrder.getAttribute("OtherOrderType")) != "")
		{
			obj.value = checkStringForNull(incidentOrder.getAttribute("OtherOrderType"));
		}	
		else if(obj && checkStringForNull(incidentOrder.getAttribute("IncidentType")) != "")
		{
			obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentType"));
		}
		obj = document.getElementById("xml:/Order/Extn/@ExtnIncidentTeamType");
		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("IncidentTeamType"));

		objFS = document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode") ;
		if(objFS)
			objFS.value = checkStringForNull(incidentOrder.getAttribute("IncidentFsAcctCode"));

		objBLM = document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode");
		if(objBLM)
			objBLM.value = checkStringForNull(incidentOrder.getAttribute("IncidentBlmAcctCode"));

		objOther = document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode") ;
		if(objOther)
			objOther.value = checkStringForNull(incidentOrder.getAttribute("IncidentOtherAcctCode"));

		obj = document.getElementById("xml:/Order/@BillToID") ;
		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));

		//populate ShipNode for Ship_Cache field
		obj = document.getElementById("xml:/Order/@ShipNode") ;
		if(obj)
			//obj.value = checkStringForNull(incidentOrder.getAttribute("PrimaryCacheId"));
		//Suryasnat: commented for issue 518
	
		obj = document.getElementById("xml:/NWCGIncidentOrder/@CustomerId");
		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));

		obj = document.getElementById("xml:/Order/@BuyerOrganizationCode") ;
		if(obj)
			obj.value = checkStringForNull(incidentOrder.getAttribute("CustomerId"));

		objOverRide = document.getElementById("xml:/Order/Extn/@ExtnOverrideCode") ;

		if(objOverRide)
			objOverRide.value = checkStringForNull(incidentOrder.getAttribute("OverrideCode"));

		objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter") ;
		if(objCostCenter)
			objCostCenter.value = checkStringForNull(incidentOrder.getAttribute("CostCenter"));
				
		objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea") ;
		if(objFunctionalArea)
			objFunctionalArea.value = checkStringForNull(incidentOrder.getAttribute("FunctionalArea"));
					
		objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS") ;
		if(objWBS)
			objWBS.value = checkStringForNull(incidentOrder.getAttribute("WBS"));
			
		obj = document.getElementById("xml:/Order/@OwnerAgency") ;
		objShipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode") ;
		objSAOverRide = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode") ;

		if(obj.value == 'FS')
		{
			// extract the FS Account code
			objShipCode.value = objFS.value;
			objSAOverRide.value = objOverRide.value ;
		}
		else if(obj.value == 'BLM')
		{
			// extract the BLM Account code
			objShipCode.value = objBLM.value ;
			objSAOverRide.value = objOverRide.value ;
		}
		else if(obj.value == 'OTHER')
		{
			objShipCode.value = objOther.value ;
			objSAOverRide.value = objOverRide.value ;
		}
		updateAddress(elem,nodes(0),"xml:/Order/PersonInfoShipTo","YFSPersonInfoShipTo");
		updateAddress(elem,nodes(0),"xml:/Order/PersonInfoBillTo","YFSPersonInfoBillTo");

		
		// Code to populate Shipping Instructions
		var strShipInst =  incidentOrder.getAttribute("ShippingInstructions");
		var strShipStrCity = incidentOrder.getAttribute("ShipInstrCity");
		var strShipStrState = incidentOrder.getAttribute("ShipInstrState");
		
		obj = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions") ;
		if(obj && strShipInst != null)
			obj.value = strShipInst;

		obj = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity") ;
		if(obj && strShipStrCity != null)
			obj.value = strShipStrCity;

		obj = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState") ;
		if(obj && strShipStrState != null)
			obj.value = strShipStrState;

		// End  shipping instructions
				
		// Commenting the below code since we don't have DeliverTo panel in issue entry screen
		// If we uncomment it, it is going to throw javascript error
		//updateAddress(elem,xmlDoc,"xml:/Order/AdditionalAddresses/AdditionalAddress/PersonInfo","YFSPersonInfoDeliverTo");
	}
    var returnArray = new Object();
	var IncType = document.getElementById("xml:/Order/Extn/@ExtnIncidentType").value;
        returnArray["xml:CommonCode"] = IncType ;
        returnArray["xml:CodeType"] = "INCIDENT_TYPE" ;

	fetchDataWithParams(elem,'getCommonCodeList',updateShortDesc,returnArray);
}

// cr 207
function updateShortDesc(elem,xmlDoc)
{
	nodes=xmlDoc.getElementsByTagName("CommonCode");
	var CType = nodes(0);
	var CommonCode = CType.getAttribute("CodeShortDescription");
        document.getElementById("xml:/Order/Extn/@TITD").value = CommonCode;		
}
// end of cr 207

// UC 916
function checkHoldForScheduleAndRelease(viewId){

	// CR 18 - Shipping Contact Name and Phone are Mandatory fields
	var objShpCntName = document.getElementById("xml:/Order/Extn/@ExtnShippingContactName");
	var objShpCntPhone = document.getElementById("xml:/Order/Extn/@ExtnShippingContactPhone");
	var documentType = document.getElementById("xml:/Order/@DocumentType");
	
	// put focus on Name if both are blank
	if (documentType.value == "0001")
	{
		if(objShpCntName.value == "" && objShpCntPhone.value == "")
		{
			alert('Please enter Shipping Contact Name and phone');
			objShpCntName.focus();
			return false;
		}
	
		if(objShpCntName.value == "")
		{
			alert('Please enter Shipping Contact Name');
			objShpCntName.focus();
			return false;
		}
		// put focus on phone if name is filled and phone is not
		else if(objShpCntPhone.value == "")
		{
			alert('Please enter Shipping Contact Phone');
			objShpCntPhone.focus();
			// do not let user continue
			return false;
		}
		// end CR 18
	
		if(objShpCntName.OldValue == "")
		{
			alert('Please save the Shipping Contact Name and perform Schedule and Release');
			objShpCntName.focus();
			return false;
		}
		
	
		if(objShpCntPhone.OldValue == "")
		{
			alert('Please save the Shipping Contact Phone and perform Schedule and Release');
			objShpCntPhone.focus();
			return false;
		}
	}
	

	var elemIncidentKey = document.getElementById("IncidentKey");
	// Need to pass an object as the first parameter for fetchDataWithParams. So, passing incident key	
	fetchDataWithParams(elemIncidentKey, 'checkPrimFinCodeAndUpdateIssue', setPrimaryFinancialCode, setIssueInfoForIncHoldChk());
	// Commenting for QC1100
	//var myObject = new Object();
	//myObject.currentWindow = window;
	//yfcShowDetailPopup(viewId, "", "570", "250", myObject);

	//var retVal = myObject["OMReturnValue"];
	//var returnValue = myObject["OKClicked"];
	//if ( 'YES' == returnValue ) {
		//return (retVal);
	//} else {
		//return (false);	    
	//}	
	return (true);
}

function setIssueInfoForIncHoldChk()
{
	var elemIncidentKey = document.getElementById("IncidentKey");
	var elemOrderHdrKey = document.getElementById("OrderHeaderKey");
	var holdType = document.getElementById("HoldType");

	var returnArray = new Object();
	returnArray["xml:IncidentKey"] = elemIncidentKey.value;
	returnArray["xml:OrderHeaderKey"] = elemOrderHdrKey.value;
	if (holdType != null && holdType.value == 'NULL_PRIM_FIN_CODE'){
		returnArray["xml:HoldDueToFinCode"] = 'Y';
	}
	else {
		returnArray["xml:HoldDueToFinCode"] = 'N';
	}
	return returnArray;
}

//This is a callback function for checkPrimFinCodeAndUpdateIssue
function setPrimaryFinancialCode(elem, xmlDoc)
{
	//var nodes=xmlDoc.getElementsByTagName("NWCGROSSAcctCodes");
	//if(nodes!= null && nodes.length > 0 )
	//{	
	//	var primaryFinCode = nodes(0);
	//	elem.value = primaryFinCode.getAttribute("IsPrimaryROSSFinAcctCode");
	//}
}

// This function is used by order_detail_orderentry.jsp and willpickup.jsp
function setExtnReqDelDateInDiffPanels(elem, updateXPathValue)
{
	extnReqDelDate = document.getElementById(updateXPathValue);
	if (extnReqDelDate)
	{
		extnNavInfo = document.documentElement.getAttribute("SelectedShippingType");
		if (extnNavInfo) {
			if(extnNavInfo == 'WILL_PICK_UP') {
				extnReqDelDate.value = elem.value;
			}
			else {
				emptyDate = document.getElementById("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE");
				if (emptyDate) {
					emptyDate.value = "";
				}
				emptyTime = document.getElementById("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME");
				if (emptyTime) {
					emptyTime.value = "";
				}
			}
		}
	}
}

function blankOutShipToFields()
{
	var shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@AddressLine1");
	if (shipToField)
		shipToField.value = "";
		
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@AddressLine2");
	if (shipToField)
		shipToField.value = "";

	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@City");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@State");
	if (shipToField)
		shipToField.value = "";

	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@ZipCode");
	if (shipToField)
		shipToField.value = "";

	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@FirstName");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@LastName");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@DayPhone");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@EveningPhone");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@MobilePhone");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@DayFaxNo");
	if (shipToField)
		shipToField.value = "";
	
	shipToField = document.getElementById("xml:/Order/PersonInfoShipTo/@EmailID");
	if (shipToField)
		shipToField.value = "";	
}

function blankOutWillPickUpFields()
{
	var willPickUpField = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpName");
	if (willPickUpField)
		willPickUpField.value = "";
	
	var willPickUpField = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpInfo");
	if (willPickUpField)
		willPickUpField.value = "";
	
	var willPickUpField = document.getElementById("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE");
	if (willPickUpField)
		willPickUpField.value = "";	

	var willPickUpField = document.getElementById("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME");
	if (willPickUpField)
		willPickUpField.value = "";	
}

function blankOutShippingInstructionsFields()
{
	var shippingInstField = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions");
	if (shippingInstField)
		shippingInstField.value = "";	

	var shippingInstCity = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity");
	if (shippingInstCity)
		shippingInstCity.value = "";	

	var shippingInstState = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState");
	if (shippingInstState)
		shippingInstState.value = "";			
}

//cr 442 -  check available rfi
function checkAvailableRFI(elem)
{
	//alert("made it into checkAvailableRFI: " + elem.value);
	var currentRow = elem.parentNode.parentNode;
	//var currentRow = elem.parentElement.parentElement.parentElement;
	//alert("displaying current row:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("INPUT");
	
	var ItemID = '';
	var PC = '';
	var UOM = '';
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Item/@ProductClass') != -1)
		{
			PC = InputList[i].value;
		}
		else if(InputList[i].name.indexOf('/Item/@UnitOfMeasure') != -1)
		{
			UOM = InputList[i].value;
		} 
		else if(InputList[i].name.indexOf('/Item/@ItemID') != -1)
		{
			ItemID = InputList[i].value;		
		}//end if
		
		if (PC != '' && UOM != '' && ItemID != '')
			break;
	}	

	var extraParams='xml:/Item/@ItemID='+ItemID+'&xml:/Item/@ProductClass='+PC+'&xml:/Item/@TransactionalUOM='+UOM;
	yfcShowDetailPopupWithParams('ISUYOMD095','',900,500,extraParams);
	return false;
}

function populateItemForCacheTransferDetails(elem,xmlDoc)
{
	var nodes=xmlDoc.getElementsByTagName("Item");
	var strPC = '';
	var strQty = '';
	var strUOM = '';
	var strDesc = '';
	var strProductLine = '';
	var shipNode = document.getElementById("xml:/Order/@ShipNode");
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		strPC = item.getAttribute("ProductClass") ;
		strQty = item.getAttribute("AvailableQty") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strDesc = item.getAttribute("ShortDescription");
		strProductLine = item.getAttribute("ProductLine");
		if(strPC == null && strUOM == null)
		{
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
		}
		//alert("strProductLine " + strProductLine);
		//alert("shipNode " + shipNode.value);

		if(shipNode.value != "IDGBK" && strProductLine =="NIRSC Communications")
		{
			alert('Item ( '+elem.value+' ) can be processed only at IDGBK Cache');
			elem.value = '';
			return false;

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
		if(InputList[i].name.indexOf('/Extn/@ExtnQtyRfi') != -1)
		{
			InputList[i].value = strQty;
		}//end if

		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Item/@ProductClass') != -1)
		{
			InputList[i].value = strPC;
		}//end if
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@TransactionalUOM') != -1)
		{
			InputList[i].value = strUOM;
		}//end if
	}
	
	var availRFIAnchor = currentRow.getElementsByTagName("a");
	if(availRFIAnchor != null && availRFIAnchor != 'undefined' && availRFIAnchor.length > 0 )
	{
		var rfiParams = 'xml:/Item/@ItemID=' + elem.value + '&xml:/Item/@ProductClass=' + strPC + '&xml:/Item/@TransactionalUOM=' + strUOM;
		availRFIAnchor[0].innerText = strQty;				
		availRFIAnchor[0].onclick = new Function("yfcShowDetailPopupWithParams(\"ISUYOMD095\", \"\", \"900\", \"500\", '" + rfiParams + "');return false;");
	}	
}

function validTrackID(elem)
{

	var currentRow = elem.parentNode.parentNode;
	var rowIndex = currentRow.rowIndex;
	//alert("rowIndex :: " + rowIndex);
	// BEGIN CR 587 - ML
	if(elem.value == null || elem.value =='')
	{
			alert("Trackable ID Is Mandatory");
			document.getElementById('OrderLines').deleteRow(rowIndex);
			return true;
			//elem.focus();
			
	}
	// END CR 587 - ML 	
}

function validateScreenTID(elem)
{
	//	alert("In validateScreenTID()");


	if(elem.value != '')
	{
		if(!validateLocally(this,'Extn/@ExtnTrackableId'))
		{
			alert('Trackable ID already exists within this transfer. Check other order lines for a duplicate Trackable # entry');
			elem.focus();
		}
	}
	
	var currentRow = elem.parentNode.parentNode;
	var rowIndex = currentRow.rowIndex;

	if(elem.value == null || elem.value =='')
	{
			alert("Trackable ID Is Mandatory");
			document.getElementById('OrderLines').deleteRow(rowIndex);
				
	}
}


// This function is used by nwcg_new_prepareOrderLine js to set field ROSSSpecialNeeds
function setSpecialInstructionsReqNo(reqNo)
{
/*
Begin CR830 01252013
	var documentType = document.getElementById("DocumentType");
	if (documentType != null && documentType.value != '0001'){
		return;
	}

	specialInstrReqNos = document.getElementById("xml:/Order/Extn/@SpecialInstructionsRequestNos");
	if (specialInstrReqNos)
	{
		// This will set the original value of special instructions to empty string
		if (reqNo.length < 2){
			specialInstrReqNos.value = reqNo;
		}
		
		if(specialInstrReqNos.value.length < 2){
			specialInstrReqNos.value = reqNo;
		}  else {
			specialInstrReqNos.value = specialInstrReqNos.value + ':' + reqNo;
		}
                }
End CR830 01252013 
*/
}

 
//Begin CR848 
function validateIssueInputDuringCreateOrder(elem)
{
	var documentType = document.getElementById("xml:/Order/@DocumentType");
	var shippingType = document.documentElement.getAttribute("SelectedShippingType");
	var extnNavInfo = document.getElementById("xml:/Order/Extn/@ExtnNavInfo");
	var oldNavInfo = document.getElementById("xml:/Order/Extn/@OldExtnNavInfo");
	var addrLine1 = document.getElementById("xml:/Order/PersonInfoShipTo/@AddressLine1");
	var state = document.getElementById("xml:/Order/PersonInfoShipTo/@State");

	var extnWillPickUpName = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpName");
	var extnWillPickUpInfo = document.getElementById("xml:/Order/Extn/@ExtnWillPickUpInfo");
	var extnReqDelDate = document.getElementById("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE");
	
	var extnShippingInstructions = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions");	
	var extnShippingInstrCity = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity");
	var extnShippingInstrState = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState");	
	var status = document.getElementById("xml:/Order/@Status");
	
	//Mandatory that an Issue Type is selected before submitting the page. 
	if (document.getElementById("xml:/Order/@OrderType").selectedIndex == 0)
	{
		alert('Please select an Issue Type before submitting your order.');
		return false;
	}	

	// Means it's an issue entry, there is no old nav info
	if (oldNavInfo == null)  {
			oldNavInfo = new Object();
			oldNavInfo.value = 'SHIP_ADDRESS';
	}

	if(documentType != null && documentType != 'undefined' && documentType.value == '0001')
	{
		if (extnReqDelDate == null || extnReqDelDate == 'undefined' || extnReqDelDate.value == '')
		{
			if (!(status != null && status.value == 'Shipped')){
				alert('Please enter Requested Delivery Date and place the order again');
				return false;
			}
		}
	}
	
	if (extnNavInfo == null || extnNavInfo == 'undefined' || extnNavInfo.value == '' || extnNavInfo.value == ' ')
	{
		document.getElementById("xml:/Order/Extn/@ExtnNavInfo").value = 'SHIP_ADDRESS';	
	}
	else if (shippingType == null || shippingType == 'undefined' || shippingType.value == '' || shippingType.value == ' ') {
		shippingType = 'SHIP_ADDRESS';
	}
	
	if (extnNavInfo != null && extnNavInfo != 'undefined' && oldNavInfo !=null && oldNavInfo != 'undefined')
	{
		if (extnNavInfo.value == 'WILL_PICK_UP')
		{
			if (((extnWillPickUpName) && (extnWillPickUpName == 'undefined' || extnWillPickUpName.value =='')) ||
			    ((extnWillPickUpInfo) && (extnWillPickUpInfo == 'undefined' || extnWillPickUpInfo.value =='')) ||
			    ((extnReqDelDate) && (extnReqDelDate == 'undefined' || extnReqDelDate.value ==''))	){
				alert('Please enter WILL PICK UP address details');
				return false;
			}
			
		}
		else if (extnNavInfo.value == 'NAV_INST')
		{
			if (extnShippingInstrCity != null && extnShippingInstrCity != 'undefined' && extnShippingInstrCity.value != '' &&
				extnShippingInstrState != null && extnShippingInstrState != 'undefined' && extnShippingInstrState.value != '')
			{
				return true;
			}
			else {
				alert('Please enter all SHIPPING INSTRUCTION details');
				return false;
			}	
		}
		else if (extnNavInfo.value == 'SHIP_ADDRESS')
		{
				if (((addrLine1) && (addrLine1 == 'undefined' || addrLine1.value == '')) ||
				((state) && (state == 'undefined' || state.value == ''))){
				alert('Please enter SHIP TO address details');
				return false;
			}					
		}
	}
	else {
		alert('Shipping Method is not set for this Issue');
		return false;
	}	
	return true;
}
//End CR848	

