function updateBillToaddress(elem,xmlDoc){
	//alert("Entering BillTo/ShipTo Address");
	//update billing address
	var billaddRet = updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","ShipNodePersonInfo");
    //update shipping address
	var shipaddRet = updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","ShipNodePersonInfo");
}


function updateIncidentDetails(elem,xmlDoc){
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	//alert(nodes.length);
	//traverse(nodes(0));
	if(nodes!=null && nodes.length >0 ){	
		var incidentOrder = nodes(0);
		//populate the IncidentName
		document.getElementById("xml:/Order/Extn/@ExtnIncidentName").value = incidentOrder.getAttribute("IncidentName");
	}
}


function updateIncidentDetailsForCacheToCache(elem,xmlDoc){
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	//alert("nodes.length=["+nodes.length+"]");
	//traverse(nodes(0));

	if(document.getElementById("xml:/Order/Extn/@ExtnValidIncident") != null){
	document.getElementById("xml:/Order/Extn/@ExtnValidIncident").value = true;}
	if(nodes!=null && nodes.length >0 ){	
		var incidentOrder = nodes(0);
		//populate the IncidentName, SA, and SAOC
		document.getElementById("xml:/Order/Extn/@ExtnIncidentName").value = incidentOrder.getAttribute("IncidentName");

		nodes=xmlDoc.getElementsByTagName("YFSPersonInfoShipTo");
		//alert("nodes=["+nodes+"] nodes.length=["+nodes.length+"]");
		if(nodes!=null && nodes.length > 0){
			updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","YFSPersonInfoShipTo");
		}
		nodes=xmlDoc.getElementsByTagName("YFSPersonInfoBillTo");
		//alert("nodes=["+nodes+"] nodes.length=["+nodes.length+"]");
		if(nodes!=null && nodes.length > 0){
			updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","YFSPersonInfoBillTo");
		}
	} else {
		//Begin  CR817 
                var incNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
                if(incNo.value!=null && incNo.value!='') {  
  		//end CR817
                   // added this else statement for trapping invalid Incident/OtherOrder# entry
		   //alert("its this alert message ");
                   if(document.getElementById("xml:/Order/Extn/@ExtnValidIncident") != null) {
		      document.getElementById("xml:/Order/Extn/@ExtnValidIncident").value = false; 
                    }
                    alert("Please enter valid Incident/Year or OtherOrder#1234567890" +incNo.value);
                //Begin CR817
                } else {
                }
                //End CR817
	}
}


function setParam(ele)
{
	//This Java Script function is called on Tab out of Receiving Cache
	//Pass ele which is nothing but ReceivingNode implicitly, explicitly set Incident No as a part of the client request.
	//alert("ele.id=["+ele.id+"] ele.value=["+ele.value+"]");
	var incNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var incYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	//alert("incNo.value=["+incNo.value+"] incYear.value=["+incYear.value+"]");
	var returnArray = new Object();
	returnArray["xml:ExtnIncidentNo"] = incNo.value;
	returnArray["xml:ReceivingNode"] = ele.value;
	returnArray["xml:ExtnIncidentYear"] = incYear.value;
	//alert("returnArray =" + returnArray);
	return returnArray;
}

function setParam2(ele) {
	var returnArray = new Object();
	returnArray["xml:ShipNode"] = ele.value;
	return returnArray;
}

function setIncidentParamCT(ele)
{
	// CR 399 -- changed the function name -- this is used from Cache Transfer module
	//This Java Script function is called on Tab out of Incident Number
	//Pass ele which is nothing but IncidentNo implicitly, explicitly set Receiving Cache as a part of the client request.
	// CR 399 - populate incident info
	var recvCache = document.getElementById("xml:/Order/@ReceivingNode");
	var eleIncNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var eleIncYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	//alert("ele.id=["+ele.id+"] ele.value(Year)=["+ele.value+"]");
	//alert("eleIncNo.value=["+eleIncNo.value+"] recvCache.value=["+recvCache.value+"]");
	var returnArray = new Object();
	returnArray["xml:ReceivingNode"] = recvCache.value;
	returnArray["xml:ExtnIncidentYear"] = eleIncYear.value;
	returnArray["xml:ExtnIncidentNo"] = eleIncNo.value;
	return returnArray;
}

function updateAccountCodes(elem,xmlDoc)
{
	var bRecvNode = false;
	var elemDocumentType = document.getElementById("xml:/Order/@DocumentType");
	var shipOwnerAgency = document.getElementById("xml:/Order/@ShippingOwnerAgency").value;
    //alert("shipOwnerAgency ::" + shipOwnerAgency);
	

	
	//alert("elemDocumentType.value=["+elemDocumentType.value+"]");
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	//alert("nodes.length=["+nodes.length+"]");
	if(elem.id == "xml:/Order/@ReceivingNode") {
		bRecvNode = true ;
		var detailPage = elem.getAttribute("IsDetailPage") ;
		
		if(isContainError(xmlDoc, "YFS10001")){ //if receiving cache is not found
			//var isShowNoRecFoundMsg = !(elem.value == null || elem.value.replace(/^\s+|\s+$/, '').length == 0);
			//if(isShowNoRecFoundMsg){
				alert("Invalid Receiving Cache: '" + elem.value + "'.");
			//}
			//elem.focus();
			//clear the fields
			clearFields();
			
		} else if(detailPage != true || detailPage == null){
			updateBillToaddress(elem,xmlDoc);
		}
	} else if(elemDocumentType.value = '0006') {
		//alert("updateIncidentDetailsForCacheToCache called ");
		updateIncidentDetailsForCacheToCache(elem,xmlDoc); //Cache Transfer
	} else {
		updateIncidentDetails(elem,xmlDoc);
	}

	if(bRecvNode == false) {
		var recvNode = document.getElementById("xml:/Order/@ReceivingNode");
		if(recvNode.value == "" || recvNode.length == 0)
		{
			alert("Receiving Cache must be entered before entering Incident No");
		}
	}

	//xmlDoc will have Output xml of NWCGUpdateAccountCodes Service
	nodes=xmlDoc.getElementsByTagName("Extn");
	//alert("nodes=["+nodes+"] nodes.length=["+nodes.length+"]");
	if(nodes!=null && nodes.length > 0){
		var extn = nodes(0);
		if (extn != null)
		{
			//alert("Extn -> ExtnRecvAcctCode: ["+extn.getAttribute("ExtnRecvAcctCode")+"]");
			//populate the account codes
			var recvCode = extn.getAttribute("ExtnRecvAcctCode");
			var shipCode = extn.getAttribute("ExtnShipAcctCode");
			var recvOverrideCode = extn.getAttribute("ExtnRAOverrideCode");
			var shipOverrideCode = extn.getAttribute("ExtnSAOverrideCode");
			var ExtnFSTransferShipAcctCode = extn.getAttribute("ExtnFSTransferShipAcctCode");
            var ExtnFSTransferSAOverrideCode = extn.getAttribute("ExtnFSTransferSAOverrideCode");
		    var ExtnBLMTransferShipAcctCode = extn.getAttribute("ExtnBLMTransferShipAcctCode");
            var ExtnOtherTransferShipAcctCode = extn.getAttribute("ExtnOtherTransferShipAcctCode");
			var ExtnFSTransferOrderAcctCode = extn.getAttribute("ExtnFSTransferOrderAcctCode");
			var ExtnFSTransferOrderOverrideCode = extn.getAttribute("ExtnFSTransferOrderOverrideCode");
			var ExtnBLMTransferOrderAcctCode = extn.getAttribute("ExtnBLMTransferOrderAcctCode");
			var ExtnOtherTransferOrderAcctCode = extn.getAttribute("ExtnOtherTransferOrderAcctCode");
			var recvOwnerAgency = extn.getAttribute("ExtnOwnerAgency");
			var IncidentFSAcctCode = extn.getAttribute("IncidentFSAcctCode");
            var IncidentOverrideCode = extn.getAttribute("IncidentOverrideCode");
            var IncidentBlmAcctCode = extn.getAttribute("IncidentBlmAcctCode");
            var IncidentOtherAcctCode = extn.getAttribute("IncidentOtherAcctCode");
			//alert("recvOwnerAgency ::" + recvOwnerAgency);

			var shipacctcode = "";
			var shipoverridecode = "";
			var fsorderacctcode = "";
			var blmorderacctcode = "";
			var otherorderacctcode = "";
			var fsorderoverridecode = "";

			if (recvOwnerAgency == shipOwnerAgency)
			{
				if (shipOwnerAgency == "BLM")
				{
					if (IncidentBlmAcctCode != null && IncidentBlmAcctCode != "")
					{
						shipacctcode = IncidentBlmAcctCode;
						blmorderacctcode = IncidentBlmAcctCode;
					}
					else
					{
						shipacctcode = shipCode;
						blmorderacctcode = recvCode;
					}
				}
				else if (shipOwnerAgency == "FS")
				{
					if (IncidentFSAcctCode != null && IncidentFSAcctCode != "")
					{
						shipacctcode = IncidentFSAcctCode;
						shipoverridecode = IncidentOverrideCode;
						fsorderacctcode = IncidentFSAcctCode;
						fsorderoverridecode = IncidentOverrideCode;
					}
					else
					{
						shipacctcode = shipCode;
						shipoverridecode = shipOverrideCode;
						fsorderacctcode = recvCode;
                        fsorderoverridecode = recvOverrideCode;
					}
				}
				else 
				{
					if (IncidentOtherAcctCode != null && IncidentOtherAcctCode != "")
					{
						shipacctcode = IncidentOtherAcctCode;
						otherorderacctcode = IncidentOtherAcctCode;
					}
					else
					{
						shipacctcode = shipCode;
						otherorderacctcode = recvCode;
					}
				}
			}
            else if (recvOwnerAgency != shipOwnerAgency)
			{
				if (shipOwnerAgency == "BLM")
				{
					if (IncidentBlmAcctCode != null && IncidentBlmAcctCode != "")
					{
						shipacctcode = IncidentBlmAcctCode;
						blmorderacctcode = IncidentBlmAcctCode;
					}
					else
					{
						shipacctcode = ExtnBLMTransferShipAcctCode;
						blmorderacctcode = ExtnBLMTransferOrderAcctCode;
					}
				}
				else if (shipOwnerAgency == "FS")
				{
					if (IncidentFSAcctCode != null && IncidentFSAcctCode != "")
					{
						shipacctcode = IncidentFSAcctCode;
						shipoverridecode = IncidentOverrideCode;
						fsorderacctcode = IncidentFSAcctCode;
						fsorderoverridecode = IncidentOverrideCode;
					}
					else
					{
						shipacctcode = ExtnFSTransferShipAcctCode;
						shipoverridecode = ExtnFSTransferSAOverrideCode;
						fsorderacctcode = ExtnFSTransferOrderAcctCode;
                        fsorderoverridecode = ExtnFSTransferOrderOverrideCode;
					}
				}
				else 
				{
					if (IncidentOtherAcctCode != null && IncidentOtherAcctCode != "")
					{
						shipacctcode = IncidentOtherAcctCode;
						otherorderacctcode = IncidentOtherAcctCode;
					}
					else
					{
						shipacctcode = ExtnOtherTransferShipAcctCode;
						otherorderacctcode = ExtnOtherTransferOrderAcctCode;
					}
				}
			}
            if(recvCode != null && recvCode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnRecvAcctCode").value = recvCode;
			if(recvOverrideCode != null && recvOverrideCode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnRAOverrideCode").value = recvOverrideCode;
			if(shipacctcode != null && shipacctcode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = shipacctcode;
			if(shipoverridecode != null && shipoverridecode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value = shipoverridecode;
			if(blmorderacctcode != null && blmorderacctcode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value = blmorderacctcode;
			if(otherorderacctcode != null && otherorderacctcode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode").value = otherorderacctcode;
			if(fsorderacctcode != null && fsorderacctcode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode").value = fsorderacctcode;
			if(fsorderoverridecode != null && fsorderoverridecode != "")
			   document.getElementById("xml:/Order/Extn/@ExtnOverrideCode").value = fsorderoverridecode;


			/*if(elemDocumentType.value = '0006') { // cache transfer document
				if(recvCode != null && recvCode != "")
					document.getElementById("xml:/Order/Extn/@ExtnRecvAcctCode").value = recvCode;
				if(shipCode != null && shipCode != "")
					document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = shipCode;
				if(recvOverrideCode != null && recvOverrideCode != "")
					document.getElementById("xml:/Order/Extn/@ExtnRAOverrideCode").value = recvOverrideCode;
				if(shipOverrideCode != null && shipOverrideCode != "")
					document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value = shipOverrideCode;
			} else { // rest of documents
				if(recvCode != null && recvCode != "") {
					var objRecvCode = document.getElementById("xml:/Order/Extn/@ExtnRecvAcctCode");
					if(eval(objRecvCode) != null && objRecvCode.value == "")
						objRecvCode.value = recvCode;
				}
				if(shipCode != null && shipCode != ""){
					var objShipCode = document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode");
					if(eval(objShipCode) != null && objShipCode.value == "")
						objShipCode.value = shipCode;
				}
				//override codes if the agent is FS and override code is filled in
				if(recvOverrideCode != null && recvOverrideCode != "") {
					var objrecvOvrCode = document.getElementById("xml:/Order/Extn/@ExtnRAOverrideCode");
					if(eval(objrecvOvrCode) != null && objrecvOvrCode.value == "")
						objrecvOvrCode.value = recvOverrideCode;
				}
				if(shipOverrideCode != null && shipOverrideCode != "") {
					var objshipOvrCode = document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode");
					if(eval(objshipOvrCode) != null && objshipOvrCode.value == "")
						objshipOvrCode.value = shipOverrideCode;
				}
			} */
		}
	}
}

function check_ctc_fields()
{

	//xml:/Order/@ReceivingNode

	var recNode = document.getElementById("xml:/Order/@ReceivingNode").value;

	var validIncident = 'true';

	if(document.getElementById("xml:/Order/Extn/@ExtnValidIncident") != null){
		
	validIncident = document.getElementById("xml:/Order/Extn/@ExtnValidIncident").value;

	}
	var IncidentYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear").value;
	var IncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo").value;


	if(recNode == null || recNode == "") { 


	alert("Error: Recieving Cache is NULL");
	 return false;

	} 
	
//alert(validIncident == false);
//alert(IncidentYear != "" || IncidentNo != "");

	if(IncidentYear != "" || IncidentNo != ""){

		//alert(IncidentYear == "" && IncidentNo == "");

		if(validIncident == false ) {
		//valid case

		//alert(" 1 ");
		
	alert("Please Enter Valid Incident/Other# ,Null is allowed");
	return false;

		} else {	
			//alert(" 2 ");
if(IncidentYear != "" && IncidentNo == ""){

	//alert(" 3 ");
	alert("Please Enter Valid Incident/Other# ,Null is allowed");
	return false;

		}
			}
				}

	return true;

}
//For new dynamic C2C orderlines
function setC2CParam(ele,node)
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

	//alert("ele.value = "+ ele.value);
	return returnArray;
}

function checkNodes(elem) {
	var recvNode = document.getElementById("xml:/Order/@ReceivingNode");
	var shipNode = document.getElementById("xml:/Order/@ShipNode");
	if(recvNode.value == shipNode.value) {
		return true;
	}
	return false;
}

/**
 * Checks if all inputs are valid before creating order. 
 * @return false if one or more invalid input. Order will not be created
 * True: if all inputs are valid. Order will be created
 */
function validateRequiredInput()
{
	var isValid = true;
	var recvCache = document.getElementById("xml:/Order/@ReceivingNode");
	if(recvCache.value == null || recvCache.value.replace(/^\s+|\s+$/, '').length == 0)
	{
		isValid = false;
		alert("Receiving Cache is NULL.");
		recvCache.focus();
	}
	else{ //check if valid specified receiving cache value is correct. 
		
	}
	
	return isValid;
}




function clearFields() {
	document.getElementById("xml:/Order/@ReceivingNode").value = '';
	document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = '';
	document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value = '';
}

function clearFields2() {
	document.getElementById("xml:/Order/@ShipNode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode").value = '';
	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode").value = '';
}

function clearAddressFields(path){
	//debugger;
	document.getElementById(path+"/@AddressLine1").value = '';
	document.getElementById(path+"/@AddressLine2").value = '';
	document.getElementById(path+"/@AddressLine3").value = '';
	document.getElementById(path+"/@AddressLine4").value = '';
	document.getElementById(path+"/@AddressLine5").value = '';
	document.getElementById(path+"/@AddressLine6").value = '';
	document.getElementById(path+"/@City").value = '';
	document.getElementById(path+"/@State").value = '';
	document.getElementById(path+"/@ZipCode").value = '';
	document.getElementById(path+"/@Company").value = '';		
	document.getElementById(path+"/@DayFaxNo").value = '';
	// the country combo
	document.getElementById(path+"/@Country").value = '';
	document.getElementById(path+"/@FirstName").value = '';
	document.getElementById(path+"/@LastName").value ='';
	document.getElementById(path+"/@DayPhone").value = '';
	document.getElementById(path+"/@MobilePhone").value = '';
	document.getElementById(path+"/@EMailID").value = '';	
}

function isContainError(xmlDoc, errCode){
	var isContainErr = false;
	
	var errNodes = xmlDoc.getElementsByTagName("Error");
	if(errNodes != null && errNodes.length > 0){
		for (var i=0; i < errNodes.length; i++) { 
			if(errCode == errNodes[i].getAttribute("ErrorCode")){
				isContainErr = true;
				break;
			}
		}
	}
	else if(xmlDoc.xml.length==0){
		isContainErr = true;
	}
	
	return isContainErr;
}

function validateC2CQuantity(elemBoQty)
{
	var strReqQty = '';
	var strOrdQty ='';
	var strExtnUTFQty='';

	var currentRow = elemBoQty.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	
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
	}

	if(((elemBoQty.value*1)+(strOrdQty*1)+(strExtnUTFQty*1)) != (elemReqQty*1) )
	{
		alert('Issue Qty + UTF Qty + Backordered Qty should equal to or greater than Requested Qty');
		return false;
	}
	
	setBackOrderFlag(elemBoQty);
}

function updateAccountCodesUsingShipNode(elem,xmlDoc) {
	var bRecvNode = false;
	var elemDocumentType = document.getElementById("xml:/Order/@DocumentType");
	var shipOwnerAgency = document.getElementById("xml:/Order/@ShippingOwnerAgency").value;  
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
        if(elem.id == "xml:/Order/@ReceivingNode") {
            bRecvNode = true ;
            var detailPage = elem.getAttribute("IsDetailPage");
            if(isContainError(xmlDoc, "YFS10001")) { //if receiving cache is not found
            	clearFields();			
	    } else if(detailPage != true || detailPage == null){
        		//updateBillToaddress(elem,xmlDoc);
	    }
	} else if(elemDocumentType.value = '0006') {
		//updateIncidentDetailsForCacheToCache(elem,xmlDoc); //Cache Transfer
	} else {
		//updateIncidentDetails(elem,xmlDoc);
	}
	if(bRecvNode == false) {
		var recvNode = document.getElementById("xml:/Order/@ReceivingNode");
		if((recvNode.value == "") || (recvNode.length == 0)) {
			alert("Receiving Cache must be entered before entering Incident No");
		}
	}
	//xmlDoc will have Output xml of NWCGUpdateAccountCodes Service
	nodes=xmlDoc.getElementsByTagName("Extn");
	if((nodes!=null) && (nodes.length > 0)) {
		var extn = nodes(0);
		if (extn != null) {
			//populate the account codes
			document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = extn.getAttribute("ExtnShipAcctCode");
                        if((document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value =='null') || (document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value.length ==0)) {
                          document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = "";
                        }
			document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value = extn.getAttribute("ExtnSAOverrideCode");
			if((document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value =='null') || (document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value.length ==0)) {
                          document.getElementById("xml:/Order/Extn/@ExtnSAOverrideCode").value = "";
                        }
			document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value = extn.getAttribute("ExtnBLMTransferShipAcctCode");
                        if((document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value =='null') || (document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value.length ==0)) {
                          document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value = "";
                        }			
                }
        }
}

function updateAccountCodesUsingRequestingNode(elem,xmlDoc) {
	var bShipNode = false;
	//var elemDocumentType = document.getElementById("xml:/Order/@DocumentType");
	//var shipOwnerAgency = document.getElementById("xml:/Order/@ShippingOwnerAgency").value;  
	//nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
        if(elem.id == "xml:/Order/@ShipNode") {
            bShipNode = true ;
            var detailPage = elem.getAttribute("IsDetailPage");
            if(isContainError(xmlDoc, "YFS10001")) { //if receiving cache is not found
            	clearFields2();			
	    } 
	} 
	if(bShipNode == false) {
		var shipNode = document.getElementById("xml:/Order/@ShipNode");
		if((shipNode.value == "") || (shipNode.length == 0)) {
			alert("Shipping Cache must be entered before entering Incident No");
		}
	}
	nodes=xmlDoc.getElementsByTagName("Extn");
	if((nodes!=null) && (nodes.length > 0)) {
		var extn = nodes(0);
		if (extn != null) {
			document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode").value = extn.getAttribute("ExtnBLMTransferOrderAcctCode");
       			if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode").value.length ==0)) {
        			document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode").value = "";
        		}		
			document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode").value = extn.getAttribute("ExtnRecvAcctCode");
        		if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode").value.length ==0)) {
        			document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode").value = "";
        		}
        		document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode").value = extn.getAttribute("ExtnRAOverrideCode");
			if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode").value.length ==0)) {
			       	document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode").value = "";
        		}
        		document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode").value = extn.getAttribute("ExtnOtherTransferOrderAcctCode");
			if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode").value.length ==0)) {
			        document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode").value = "";
        		}
        		document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode").value = extn.getAttribute("ExtnFSTransferOrderAcctCode");
			if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode").value.length ==0)) {
			        document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode").value = "";
        		}
        		document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode").value = extn.getAttribute("ExtnFSTransferOrderOverrideCode");
			if((document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode").value =='null') || (document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode").value.length ==0)) {
			        document.getElementById("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode").value = "";
        		}
        	}
        }
}

function checkNodesUsingShipNode(elem)
{
    	//alert("function checkNodesUsingShipNode::Begin" + elem.value);
   	   var shipNode = elem.value;
	   var recvNode = document.getElementById("xml:/Order/@ReceivingNode");
	   if(shipNode == recvNode.value)
	   {
		alert("Shipping and Receiving Nodes cannot be the same.");
		elem.focus();
	   }
   	//alert("function checkNodesUsingShipNode::End");
	return false;
}
//End CR817

