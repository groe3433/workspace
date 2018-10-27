function setParamForIncName(elem) {
	var currentRow = elem.parentElement.parentElement.parentElement;
	var incidentNo = "";
	var incidentYear = "";
	var returnArray = new Object();
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@IncidentNo') != -1) {
			incidentNo = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('@IncidentYear') != -1) {
			incidentYear = InputList[cnt1].value;
		}
	}
	returnArray["xml:ExtnIncidentNo"] = incidentNo;
	returnArray["xml:ExtnIncidentYear"] = incidentYear;
	return returnArray;
}

function populateIncName(elem,xmlDoc) {
	var IncidentName = "";
	var IncidentYear= "";
	var CustomerId = "";
	var currentRow = elem.parentElement.parentElement.parentElement;
	var IncidentFSAcctCode = "";
    var IncidentOverrideCode = "";
    var IncidentBlmAcctCode = "";
    var IncidentOtherAcctCode = "";
	var RecvOwnerAgency = document.getElementById("xml:/Receipt/@RecvOwnerAgency").value;	
	var NWCGIncidentOrderListNodes = xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	if (NWCGIncidentOrderListNodes != null && NWCGIncidentOrderListNodes.length > 0) {	
		var ResultNode = NWCGIncidentOrderListNodes(0);
		IncidentName = ResultNode.getAttribute("IncidentName") ;
		if (IncidentName == null) {
			IncidentName = "";
		}
		CustomerId = ResultNode.getAttribute("CustomerId");
		if (CustomerId  == null) {
			CustomerId  = "";
		}
		IncidentYear = ResultNode.getAttribute("Year");
		if (IncidentYear  == null) {
			IncidentYear  = "";
		}
		IncidentFSAcctCode = ResultNode.getAttribute("IncidentFsAcctCode");
		IncidentOverrideCode = ResultNode.getAttribute("IncidentOverrideCode");
		IncidentBlmAcctCode = ResultNode.getAttribute("IncidentBlmAcctCode");
		IncidentOtherAcctCode = ResultNode.getAttribute("IncidentOtherAcctCode");
		if (RecvOwnerAgency == null || RecvOwnerAgency == "") {
			alert("Only Node Users can Process Return !!!");
			document.getElementById("xml:/Receipt/@IncidentNo").value = "";
			document.getElementById("xml:/Receipt/@IncidentYear").value = "";
			document.getElementById("xml:/Receipt/@IncidentName").value = "";
			document.getElementById("xml:/Receipt/@CustomerId").value = "";
			return false;
		}
		// BEGIN - CR 1751 - August 28, 2015
		// NOTE: if a BLM Acc Code is NOT (Null, Empty String, or Valid) it COULD still be a ("..")
		if (RecvOwnerAgency == "BLM" && (IncidentBlmAcctCode == null || IncidentBlmAcctCode == "" || IncidentBlmAcctCode == "..")) {
			// END - CR 1751 - August 28, 2015
			alert("This Incident doesn't have a BLM Account Code. Enter Incident BLM Account Code and Process Return !!!");
			document.getElementById("xml:/Receipt/@IncidentNo").value = "";
			document.getElementById("xml:/Receipt/@IncidentYear").value = "";
			document.getElementById("xml:/Receipt/@IncidentName").value = "";
			document.getElementById("xml:/Receipt/@CustomerId").value = "";
			return false;
		}
		// BEGIN - CR 1751 - August 28, 2015
		else if(RecvOwnerAgency == "BLM" && (IncidentBlmAcctCode.length != 40 && IncidentBlmAcctCode.length != 28)) {
			// Validate BLM Account Code
			// 40 = LL00000009.L00005000.000005.LFSPH0KS0000 (with Cost Center and Functional Area and WBS)
			// 28 = LL00000009.L00005000.000005. (with Cost Center and Functional Area)
			alert ("The BLM Account Code on this incident is incorrect. Either make sure it has (Cost Center + Functional Area), OR (Cost Center + Functional Area + WBS). ");
			document.getElementById("xml:/Receipt/@IncidentNo").value = "";
			document.getElementById("xml:/Receipt/@IncidentYear").value = "";
			document.getElementById("xml:/Receipt/@IncidentName").value = "";
			document.getElementById("xml:/Receipt/@CustomerId").value = "";
			return false;
		}
		// END - CR 1751 - August 28, 2015
		else if (RecvOwnerAgency == "FS" && (IncidentFSAcctCode == null || IncidentFSAcctCode == "")) {
			alert("This Incident doesn't have a FS Account Code. Enter Incident FS Account Code and Process Return !!!");
			document.getElementById("xml:/Receipt/@IncidentNo").value = "";
			document.getElementById("xml:/Receipt/@IncidentYear").value = "";
			document.getElementById("xml:/Receipt/@IncidentName").value = "";
			document.getElementById("xml:/Receipt/@CustomerId").value = "";
			return false;
		} else if (RecvOwnerAgency == "OTHER" && (IncidentOtherAcctCode == null || IncidentOtherAcctCode == "")) {
			alert("This Incident doesn't have an OTHER Account Code. Enter Incident OTHER Account Code and Process Return !!!");
			document.getElementById("xml:/Receipt/@IncidentNo").value = "";
			document.getElementById("xml:/Receipt/@IncidentYear").value = "";
			document.getElementById("xml:/Receipt/@IncidentName").value = "";
			document.getElementById("xml:/Receipt/@CustomerId").value = "";
			return false;
		}
	} else {
        alert ("Invalid Incident !!!");
		document.getElementById("xml:/Receipt/@IncidentNo").value = "";
		document.getElementById("xml:/Receipt/@IncidentYear").value = "";
		document.getElementById("xml:/Receipt/@IncidentName").value = "";
		document.getElementById("xml:/Receipt/@CustomerId").value = "";
		return false;
	}
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@IncidentName') != -1) {
			InputList[cnt1].value=IncidentName;
		}
		if(InputList[cnt1].name.indexOf('@CustomerId') != -1) {
			InputList[cnt1].value=CustomerId;
		}
		if(InputList[cnt1].name.indexOf('@IncidentYear') != -1) {
			InputList[cnt1].value=IncidentYear;
		}
	}
	return true;
}

function checkDLTFormat(elem) {
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@TagAttribute4') != -1 || InputList[cnt1].name.indexOf('@RevisionNo') != -1) {
			var tempRevisionNo = InputList[cnt1].value;
			if (tempRevisionNo.length > 0) {
				if (tempRevisionNo.indexOf('/') != 2 || tempRevisionNo.length != 10) {
					alert("Please enter a valid date in \"mm/dd/yyyy\" format");
					return false;
				}
			}
		}
	}
	return true;
}

function checkRFI(elem) {
	var currentRow = elem.parentElement.parentElement;
	var RFI = 0;
	var NRFI = 0;
	var UnsRet = 0;
	var UnsRetNWT = 0;
	var retQty = 0;
	var InputList = currentRow.getElementsByTagName("input");
	var last = "";
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@RFI') != -1) {
			RFI = parseInt(InputList[cnt1].value);
			if(isNaN(RFI)) {
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@QtyReturned') != -1) {
			retQty = parseInt(InputList[cnt1].value);
			if(isNaN(retQty)) {
				InputList[cnt1].value = "";
			}
		}
	}
	if(RFI > retQty) {
		alert("RFI QTY exceeding Return Quantity");
		for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
			if(InputList[cnt1].name.indexOf('@RFI') != -1) {
				InputList[cnt1].value ="";
			}
		}
		return true;
	}
	return true;
}

function checkNRFI(elem) {
	var NRFI = 0;
	var retQty = 0;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@NRFI') != -1) {
			NRFI = parseInt(InputList[cnt1].value);
			if(isNaN(NRFI)) {
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@QtyReturned') != -1) {
			retQty = parseInt(InputList[cnt1].value);
			if(isNaN(retQty)) {
				InputList[cnt1].value = "";
			}
		}
	}
	if(NRFI>retQty) {
		alert("NRFI QTY exceeding Return Quantity");
		for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
			if(InputList[cnt1].name.indexOf('@NRFI') != -1) {
				InputList[cnt1].value ="";
			}
		}	
		return true;
	}
}

function checkUnsRet(elem) {
	var UnsRet = 0;
	var retQty = 0;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@UnsRet') != -1) {
			UnsRet = parseInt(InputList[cnt1].value);
			if(isNaN(UnsRet)) {
				InputList[cnt1].value = "";
			}
			break;
		}
		if(InputList[cnt1].name.indexOf('@QtyReturned') != -1) {
			retQty = parseInt(InputList[cnt1].value);
			if(isNaN(retQty)) {
				InputList[cnt1].value = "";
			}
		}
	}
	if(UnsRet > retQty) {
		alert("UnsRet QTY exceeding Return Quantity");
		for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
			if(InputList[cnt1].name.indexOf('@UnsRet') != -1) {
				InputList[cnt1].value ="";
			} 
		}	
		return true;
	}
}

function checkUnsRetNWT(elem) {
	var UnsRetNWT = 0;
	var retQty = 0;
	var RFI = 0;
	var NRFI = 0;
	var UnsRet = 0;
	var total = 0;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@UnsRetNWT') != -1) {
			UnsRetNWT = parseInt(InputList[cnt1].value);
			if(isNaN(UnsRetNWT)) {
				UnsRetNWT = 0;
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@RFI') != -1) {
			RFI = parseInt(InputList[cnt1].value);
			if(isNaN(RFI)) {
				RFI = 0;
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@NRFI') != -1) {
			NRFI = parseInt(InputList[cnt1].value);
			if(isNaN(NRFI)) {
				NRFI = 0;
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@UnsRet') != -1) {
			UnsRet = parseInt(InputList[cnt1].value);
			if(isNaN(UnsRet)) {
				UnsRet = 0;
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@QtyReturned') != -1) {
			retQty = parseInt(InputList[cnt1].value);
			if(isNaN(retQty)) {
				retQty = 0;
				InputList[cnt1].value = "";
			}
		}
		if(InputList[cnt1].name.indexOf('@serialID') != -1) {
			return true;
		}
		if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1) {
			return true;
		}
	}
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@UnsRet') != -1) {
			UnsRet = parseInt(InputList[cnt1].value);
			if(isNaN(UnsRet)) {
				UnsRet = 0;
				InputList[cnt1].value = "";
			}
			break;
		}
	}
	total = RFI + NRFI + UnsRet + UnsRetNWT;
	if(total!=retQty) {   
		alert("TOTAL (=RFI+NRFI+UnsRet+UnsRetNWT) not equal to \"Return Quantity\"");
		return true;
	}
	if(UnsRetNWT>retQty) {
		alert("UnsNWT QTY exceeding Return Quantity");
		for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
			if(InputList[cnt1].name.indexOf('@UnsRetNWT') != -1) {
				InputList[cnt1].value ="";
				break;
			}
		}
		return true;
	}	
}

function setParamCompSerial(elem) {
	var ItemID = "";
	var returnArray = new Object();
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@ItemID') != -1) {
			ItemID = InputList[cnt1].value;
		}
	}
	returnArray["xml:ItemID"] = ItemID;
	return returnArray;
}

function setParamPrimSerial(elem) {
	var PrimeSerail = "";
	var ItemID = "";
	var PC	 = "";
	var UOM = "";
	var returnArray = new Object();
	var currentRowID = "";
	var checkArray = new Array();
	var tempRowID = "";
	var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
	var currentRow = elem.parentElement.parentElement;
	var validateRow = elem.parentElement.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1) {
			PrimeSerail = InputList[cnt1].value;
			currentRowID = InputList[cnt1].id;
		}
		if(InputList[cnt1].name.indexOf('PC') != -1) {
			PC = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('UOM') != -1) {
			UOM = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('ITEMID') != -1) {
			ItemID = InputList[cnt1].value;
		}
	}
	if(PrimeSerail==""||PrimeSerail==null){
		//alert("INVALID VALUES FOR PRIMARY SERIAL");
		//return true;
	}
    if(currentRowID != "0") {
		var InputList2 = validateRow.getElementsByTagName("input");
		var arr_counter=0;
		for(cnt2 = 0; cnt2 <= InputList2.length; cnt2++) {
			if(InputList2[cnt2].name.indexOf('@PrimarySerialNo') != -1) {
				temp = InputList2[cnt2].value;
				checkArray.push(temp);
				arr_counter= arr_counter +1;
				tempRowID = InputList2[cnt2].id;
				if (tempRowID != "0") { 
					for(cnt3 = 0; cnt3 < arr_counter-1; cnt3++) {
						if(temp == checkArray[cnt3]) {
							alert("DUPLICATE ENTRY :-"+temp);
							break;
						}
					}
				}
				if(tempRowID == currentRowID) {
					break;
				}
			}
		}
	} 
	returnArray["xml:IncidentNo"] = IncidentNo;
    returnArray["xml:IncidentYear"] = IncidentYear;
	returnArray["xml:IssueNo"] = IssueNo;
	returnArray["xml:SerialNo"] = PrimeSerail;
	returnArray["xml:ItemID"] = ItemID;
	returnArray["xml:PC"] = PC;
	returnArray["xml:UOM"] = UOM;
	return returnArray;
}

function setParamSecSerial(elem) {
	var PrimeSerail = "";
	var SecondarySerial= "";
	var ItemID = "";
	var PC	 = "";
	var UOM = "";
	var returnArray = new Object();
    var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1) {
			PrimeSerail = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('@serialID') != -1) {
			PrimeSerail = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('@SecondarySerialNo') != -1) {
			SecondarySerial = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('PC') != -1) {
			PC = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('UOM') != -1) {
			UOM = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('ITEMID') != -1) {
			ItemID = InputList[cnt1].value;
		}
	}
	if(ItemID == "" || ItemID == null || PrimeSerail == "" || PrimeSerail == null || SecondarySerial == "" || SecondarySerial == null) {
		//alert("PLEASE ENTER VALID VALUES FOR PRIMARY AND SECONDARY SERIAL");
		//return true;
	}
    returnArray["xml:IncidentNo"] = IncidentNo;
    returnArray["xml:IncidentYear"] = IncidentYear;
	returnArray["xml:IssueNo"] = IssueNo;
	returnArray["xml:SecSerialNo"] = SecondarySerial;
	returnArray["xml:SerialNo"] = PrimeSerail;
	returnArray["xml:ItemID"] = ItemID;
	returnArray["xml:PC"] = PC;
	returnArray["xml:UOM"] = UOM;
	return returnArray;
}

function PopupMessage(elem,xmlDoc) {
	var Result ="";
	var nodes=xmlDoc.getElementsByTagName("Result");
	if(nodes != null && nodes.length > 0) {	
		var ResultNode = nodes(0);
		var Result = ResultNode.getAttribute("SerialPresent");
		var SecSerial = ResultNode.getAttribute("SecondarySerial1");
	}
    if (Result == "False") {
		var tagnodes=xmlDoc.getElementsByTagName("TagDetail");
		var TNode = tagnodes(0);
		var LotNumber = TNode.getAttribute("LotNumber");
		var LotAttribute1 = TNode.getAttribute("LotAttribute1");
		var LotAttribute2 = TNode.getAttribute("LotAttribute2");
		var LotAttribute3 = TNode.getAttribute("LotAttribute3");
		var RevisionNo = TNode.getAttribute("RevisionNo");
		var BatchNo = TNode.getAttribute("BatchNo");
		var seriallist = document.getElementById("xml:/Receipt/@SerialList").value;
		var TRow = elem.parentElement.parentElement;
		var TList = TRow.getElementsByTagName("input");
		for (tcnt = 0; tcnt < TList.length; tcnt++) {
			if(TList[tcnt].name.indexOf('@PrimarySerialNo') != -1) {
				document.getElementById("xml:/Receipt/@SerialList").value = seriallist+','+TList[tcnt].value;
		    }
			if(TList[tcnt].name.indexOf('@serialID') != -1) {
				document.getElementById("xml:/Receipt/@SerialList").value = seriallist+','+TList[tcnt].value;
			}
			if(TList[tcnt].name.indexOf('@SecondarySerialNo') != -1) {
				TList[tcnt].value=SecSerial;
		    }
			if(TList[tcnt].name.indexOf('@TagAttribute1') != -1) {
				TList[tcnt].value=LotAttribute1;
		    }
	        if(TList[tcnt].name.indexOf('@TagAttribute2') != -1) {
	        	TList[tcnt].value=LotAttribute3;
		    }
	        if(TList[tcnt].name.indexOf('@TagAttribute3') != -1) {
	        	TList[tcnt].value=LotNumber;
		    }
	        if(TList[tcnt].name.indexOf('@TagAttribute4') != -1) {
	        	TList[tcnt].value=RevisionNo;
	        }
	        if(TList[tcnt].name.indexOf('@TagAttribute5') != -1) {
	        	TList[tcnt].value=BatchNo;
	        }
	        if(TList[tcnt].name.indexOf('@TagAttribute6') != -1) {
	        	TList[tcnt].value=LotAttribute2;
	        }
		}
		return true;
    } else {
    	var currentRow = elem.parentElement.parentElement;
    	var InputList = currentRow.getElementsByTagName("input");
    	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
    		if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@serialID') != -1) {
    			InputList[cnt1].value="";
    		}	
    		if(InputList[cnt1].name.indexOf('@SecondarySerialNo') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute1') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute2') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute3') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute4') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute5') != -1) {
    			InputList[cnt1].value="";
    		}
    		if(InputList[cnt1].name.indexOf('@TagAttribute6') != -1) {
    			InputList[cnt1].value="";
    		}
    	}
    	if (Result == "True") {
    		alert("TRACKABLE ID ALREADY AT NODE !!!");
    		return false;
    	}
    	if (Result == "NotInIncident") {
    		alert("INVALID TRACKABLE ID FOR THIS INCIDENT !!!");
    		return false;
    	}
    	if (Result == "Invalid Input") {
    		alert("INVALID TRACKABLE ID !!!");
    		return false;
    	}
    } 
    return true;
}

//Added by GN for CR-354, Return No Validation
function setReturnParam(elem) {
	var CacheID = "";
	var ReturnNo = "";
	var returnArray = new Object();
    var CacheID = document.getElementById("xml:/Receipt/@CacheID").value;
	var ReturnNo = document.getElementById("xml:/Receipt/@ReceiptNo").value;
	if(CacheID == "" || CacheID == null) {
		//alert("PLEASE ENTER VALID VALUES FOR PRIMARY AND SECONDARY SERIAL");
		//return true;
	}
    returnArray["xml:CacheID"] = CacheID ;
	returnArray["xml:ReturnNo"] = ReturnNo ;
	return returnArray;
}

//Added by GN for CR-354, Return No Validation
function PopupReturnMesg(elem,xmlDoc) {
	var Result ="";
	var nodes=xmlDoc.getElementsByTagName("Result");
	if(nodes != null && nodes.length > 0) {	
		var ResultNode = nodes(0);
		var Result = ResultNode.getAttribute("ValidReturnNo");
	}
	if (Result == "False") {
		alert("Invalid Return No !!!");
		document.getElementById("xml:/Receipt/@ReceiptNo").value = "";
		document.getElementById("xml:/NextReceiptNo/@ReceiptNo").value = "";
		return false;
	}
	if (Result == "Received") {
		alert("This Return No is already received, Please enter another Return No !!!");
		document.getElementById("xml:/Receipt/@ReceiptNo").value = "";
		document.getElementById("xml:/NextReceiptNo/@ReceiptNo").value = "";
		return false;
	}
	return true;
}

function setParam(ele) {
	var EntCode = document.getElementById("xml:/Shipment/@EnterpriseCode");
	var test = document.all['xml:/Shipment/@EnterpriseCode'].value;
	var returnArray = new Object();
	var ECode = EntCode.value;
	var itemID = ele.value;
	if(itemID == null || itemID == "") {
		//alert("PLEASE ENTER VALID ITEMID");
	}
	returnArray["xml:EntCode"] = EntCode.value;
	returnArray["xml:ItemID"] = ele.value;
	return returnArray;
}

function setParamQty(elem) {
	var ItemID = "";
	var RetQty = "";
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for(cnt1 = 0; cnt1 < InputList.length; cnt1++) {
		if(InputList[cnt1].name.indexOf('@ItemID') != -1) {
			ItemID = InputList[cnt1].value;
		}
		if(InputList[cnt1].name.indexOf('@QtyReturned') != -1) {
			RetQty = InputList[cnt1].value;
		}
	}
	if(ItemID == null || ItemID == "") {
		return;
	}
	if(RetQty <= 0) {
		//alert("PLEASE ENTER VALID RETURN QUANTITY");
	}
	var returnArray = new Object();
	returnArray["xml:ItemID"] = ItemID;
	returnArray["xml:RetQty"] = RetQty;
	return returnArray;
}

function setParam3(elem) {
	var value = elem.value;
	var returnArray = new Object();
	returnArray["xml:OrderNo"] = value;
	return returnArray;
}

function populateIncNo(elem,xmlDoc) {
	//CR 615 BEGIN M.Lathom
	var ordNode=xmlDoc.getElementsByTagName("Order");
	var nodes=xmlDoc.getElementsByTagName("Extn");
	var documentType =  "";
	var ExtnIncidentNo = "" ;
	var ExtnIncidentYear = "" ;
	var ExtnIncidentName = "" ;
	if(ordNode != null && ordNode.length > 0) {
		fetchDataWithParams(elem,'getBilllingAccountListForReturns', handleSplitAccCodeLink,setParamBillTrans(elem));
		documentType = ordNode(0).getAttribute("DocumentType");
		if(documentType == "0008.ex") { 
			if(nodes != null && nodes.length > 0) {	
				var ExtnNode = nodes(0);	
				ExtnIncidentNo = ExtnNode.getAttribute("ExtnToIncidentNo") ;
				ExtnIncidentYear = ExtnNode.getAttribute("ExtnToIncidentYear") ;
				ExtnIncidentName = ExtnNode.getAttribute("ExtnToIncidentName");
			}
		} else {
			if(nodes != null && nodes.length > 0) {	
				var ExtnNode = nodes(0);
				ExtnIncidentNo = ExtnNode.getAttribute("ExtnIncidentNo") ;
				ExtnIncidentYear = ExtnNode.getAttribute("ExtnIncidentYear") ;
				ExtnIncidentName = ExtnNode.getAttribute("ExtnIncidentName");		
			}	
		}
	}
	//CR 615 END M.Lathom
	var currentRow = elem.parentNode.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("input");
	for (i = 0; i < InputList.length; i++) {
		var name = InputList[i].name;
		if(InputList[i].name.indexOf('@IncidentNo') != -1) {
			if(ExtnIncidentNo != null && ExtnIncidentNo != "") {
				InputList[i].value = ExtnIncidentNo;
			}		
		}
		if(InputList[i].name.indexOf('@IncidentYear') != -1) {
			if(ExtnIncidentYear != null && ExtnIncidentYear != "") {
				InputList[i].value = ExtnIncidentYear;
			}
		}
		if(InputList[i].name.indexOf('@IncidentName') != -1) {
			if(ExtnIncidentName != null && ExtnIncidentName != "") {
				InputList[i].value = ExtnIncidentName;
			}	
		}
	}
	// Begin CR 600 - ML
	document.getElementById('OrderNum').focus();
	document.getElementById('Notes').focus();
	return true;
	// End CR 600 - ML
}

function populateItemDetails1(elem,xmlDoc) {
	var nodes=xmlDoc.getElementsByTagName("Item");
	// BEGIN CR 883 Manish K
	// Moved setting CacheID up here
	var CacheId = document.all("xml:/Receipt/@CacheID").value;
	// In case the cacheid is still null
	if(CacheId == undefined || CacheId == null || CacheId == '') {
		CacheId =  document.getElementById("xml:/Receipt/@CacheID").value;
	}
	//END CR 883 Manish K
	if(nodes != null && nodes.length > 0) {
		var item = nodes(0);
		var UOM = item.getAttribute("UOM") ;
		var ProductClass = item.getAttribute("ProductClass") ;
		var ItemDescription = item.getAttribute("ShortDescription");
		var sProductLine = item.getAttribute("sProductLine");
		//BEGIN CR 883 Manish K moving setting CacheID up
		//var CacheId =  document.all("xml:/Receipt/@CacheID").value;
		//END CR 883 Manish K moving the following up
		var sItemID = item.getAttribute("ItemID");
		var currentRow = elem.parentNode.parentNode;
		var InputList = currentRow.getElementsByTagName("input");
		var length = InputList.length;
		var rowNum = parseInt(InputList[0].value)-1;
		var rowIndex = currentRow.rowIndex;
		if(CacheId != "IDGBK" && sProductLine == "NIRSC Communications") {
			alert("Item : " + sItemID + " can be Returned only at Cache IDGBK");			
			document.getElementById('ChildTable1').deleteRow(rowIndex)
			return false;		
		}
		if(ProductClass == "" || ProductClass == null || UOM == "" || UOM == null) {
			return true;
		}
		for (i = 0; i < InputList.length; i++) {
			var name = InputList[i].name;
			if(InputList[i].name.indexOf('@ShortDescription') != -1) {
				InputList[i].value = ItemDescription;
			}
		}
		for (i = 0 ; i < InputList.length ; i++) {
			var name = InputList[i].name;
			if(InputList[i].name.indexOf('@ProductClass') != -1) {
				InputList[i].value = ProductClass;
			}
		}
		for (i = 0; i < InputList.length; i++) {
			var name = InputList[i].name;
			if(InputList[i].name.indexOf('@UOM') != -1) {
				InputList[i].value = UOM;
			}
		}
		return true;
	} else {
		var currentRow = elem.parentNode.parentNode;
		var InputList = currentRow.getElementsByTagName("input");
		for (i = 0; i < InputList.length; i++) {
			var name = InputList[i].name;
			if(InputList[i].name.indexOf('@ProductClass') != -1) {
				InputList[i].value = "";
			}
		}
		for (i = 0; i < InputList.length; i++) {
			var name = InputList[i].name;
			if(InputList[i].name.indexOf('@UOM') != -1) {
				InputList[i].value = "";
			}
		}
	}
	return true;
}

function setHidden(obj) {
	var cssText = obj.style.cssText;
	if (cssText)
		obj.style.visibility = "hidden";
	else
		obj.style.cssText = "visibility:hidden";
}

function setVisible(obj) {
	var cssText = obj.style.cssText;
	if (cssText)
		obj.style.visibility = "visible";
	else
		obj.style.cssText = "visibility:visible";
}
   
function setcaseattributes(elem) {
	var tableRow = elem.parentElement.parentElement ;
	var divList = tableRow.getElementsByTagName("Div");
	for(index = 0; index < divList.length; index++) {
		var show = divList[index].style.visibility;	
		if(show=="hidden")
			divList[index].style.visibility = "visible";
		else
			divList[index].style.visibility = "hidden";
	}
}

function setParam2(elem) {
	var itemID = "";
	var RetQty = "";
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");
	for (i = 0; i < InputList.length; i++) {
		var name = InputList[i].name;
		var value = InputList[i].value;
		if(InputList[i].name.indexOf('@ItemID') != -1) {
			 itemID = InputList[i].value;
		}
		if(InputList[i].name.indexOf('@QtyReturned') != -1) {
			RetQty = InputList[i].value;
		}
	}
	if(itemID == null || itemID == "") {
		return;
	} else {
		//alert("should not reach here");
	}
	var returnArray = new Object();
	returnArray["xml:ItemID"] = itemID;
	returnArray["xml:RetQty"] = RetQty ;
	return returnArray;
}

function getKitDetails(elem,id) {
	var tableRow = elem.parentElement.parentElement;
	var itemElement = document.getElementById("ITEM"+id);
	var lookupElement = document.getElementById("LOOKUP"+id);
	itemElement.disabled=true;
	lookupElement.onclick=null;		
	var RFI_Qty = 0;
	var inputList = tableRow.getElementsByTagName("input");
	for(index = 0; index < inputList.length; index++) {
		if(inputList[index].type == "checkbox") {
			if(inputList[index].checked) {
				var itemID = "";
				var currentRow = elem.parentElement.parentElement;
				var InputList = currentRow.getElementsByTagName("input");
				for (i = 0; i < InputList.length; i++) {
					var name = InputList[i].name;
					var value = InputList[i].value;
					if(InputList[i].name.indexOf('@ItemID') != -1) {
						itemID = InputList[i].value;
					}
					if(InputList[i].name.indexOf('@RFI') != -1) {
						RFI_Qty = parseInt(InputList[i].value);
						if(!isNaN(RFI_Qty)) {
							inputList[index].checked = false;
							alert("Cannot Receive as Components for RFI Qty");
							return true;
						}
					}
				}
				if(itemID == null || itemID == "") {
					inputList[index].checked = false;
				} else {
					fetchDataWithParams(elem,'getItemCompDetailsForRet',receiveKitComponents,setParam2(elem));
				}
				return true;
			} else {	
				//alert("THIS ITEM IS NOT A KIT");
			}
		}
	}
}

function addRows(elem) {
	var tableRow = elem.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement;
	var divList = tableRow.getElementsByTagName("table");
	if (!check_fields_addRows()) {
		return false;
	}
	var row = elem.parentElement.parentElement.parentElement.parentElement;
	inputList = row.getElementsByTagName("input");
	var rowNum = parseInt(inputList[0].value);
	for(index = 0; index < divList.length; index++) {
		if(divList[index].id == "ChildTable1") {
			var tblBody = divList[index].tBodies[0];
			var newRow = tblBody.insertRow(-1);
			newRow.setAttribute('id',inputList[0].value);
			newRow.setAttribute('background-color'," #EAEAF6");
			var newCell0 = newRow.insertCell(0);
			newCell0.innerHTML = '<td><IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif"></td>&nbsp;<td class="tablecolumn" nowrap="true"><input type="text" size="10" class="unprotectedinput" id="ITEM'+(inputList[0].value - 1)+'"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value -1)+'/@ItemID"  onblur="fetchDataWithParams(this,\'getItemCompDetailsForRet\',populateItemDetails1,setParam(this));"/><img class="lookupicon" id="LOOKUP'+(inputList[0].value -1)+'" onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'UnitOfMeasure\',\'item\',\'xml:/Item/@CallingOrganizationCode=\' +  document.all[\'xml:/Shipment/@EnterpriseCode\'].value )" src="/smcfs/console/icons/lookup.gif\" alt="Search for Item"/></td>  &nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" readOnly="yes" value=""  size=20 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@ShortDescription")/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" readOnly="yes" value=""  size=5 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@ProductClass")/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" readOnly="yes" value="" size=5 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UOM")/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5  value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@RFI" id="RFIQTY'+(inputList[0].value - 1)+'" onblur="updateLineTotalQty('+(inputList[0].value - 1)+');"/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5 onblur="updateLineTotalQty('+(inputList[0].value - 1)+');" id="NRFIQTY'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@NRFI"/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5 onblur="updateLineTotalQty('+(inputList[0].value - 1)+');" id="UNSQTY'+(inputList[0].value - 1)+'"name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UnsRet"/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td style="background-color:#EAEAF6" bgcolor=#EAEAF6 bgcolor="#EAEAF6" class="tablecolumn" ><input type="text" class="unprotectedinput" value="" size="5" id="NWTQTY'+(inputList[0].value - 1)+'" onblur="updateLineTotalQty('+(inputList[0].value - 1)+');"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UnsRetNWT"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" readonly class="protectedinput" size=5 id="TOTQTY'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@QtyReturned" onblur="fetchDataWithParams(this,\'getItemCompDetailsForRet\',addTagRows,setParamQty(this));"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td class="searchlabel" width="30px"><input type="checkbox" class="" id="'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@RecdAsComp" onClick="getKitDetails(this,'+(inputList[0].value - 1)+');" value="Y"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" value="" size=30 maxLength=120 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@LineNotes")/></td></tr><tr><td colspan="120" background-color="#EAEAF6" ><table class="table" width="100%" size=5 id="KitItems'+(inputList[0].value - 1)+'"><tbody></tbody></table></td></tr>';	
		}
	}
	inputList[0].value = rowNum + 1;
}

function updateLineTotalQty(lineNumber) {
	var RFIsubtotal	= 0;
	var NRFIsubtotal = 0;
	var UNSsubtotal	= 0;
	var NWTsubtotal = 0;
	var total = 0;
	if(isNaN(parseInt(document.getElementById("RFIQTY"+lineNumber).value))) { 
		RFIsubtotal = 0;
	} else {
		RFIsubtotal = parseInt(document.getElementById("RFIQTY"+lineNumber).value);
	}
	if (isNaN(parseInt(document.getElementById("NRFIQTY"+lineNumber).value))) {
		NRFIsubtotal = 0;
	} else {
		NRFIsubtotal = parseInt(document.getElementById("NRFIQTY"+lineNumber).value);
	}
	if (isNaN(parseInt(document.getElementById("UNSQTY"+lineNumber).value))) {
		UNSsubtotal = 0;
	} else {
		UNSsubtotal = parseInt(document.getElementById("UNSQTY"+lineNumber).value);
	}
	if (isNaN(parseInt(document.getElementById("NWTQTY"+lineNumber).value))) {
		NWTsubtotal = 0;
	} else {
		NWTsubtotal=parseInt(document.getElementById("NWTQTY"+lineNumber).value);
	}
	total = parseInt(RFIsubtotal) + parseInt(NRFIsubtotal) + parseInt(UNSsubtotal) + parseInt(NWTsubtotal);
	document.getElementById("TOTQTY"+lineNumber).value = parseInt(total);
}

function check_fields() {
	var Receive_Dock = document.all("xml:/Receipt/@ReceivingDock").value;
	var Incident_No =  document.all("xml:/Receipt/@IncidentNo").value;
	var Issue_No =  document.all("xml:/Receipt/@IssueNo").value;
	var CacheId =  document.all("xml:/Receipt/@CacheID").value;
	var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;
	if (ReturnNo == "") {
		alert("Error: Cannot Process Return!!! Return No is NULL");
		return false;
	}
	if (CacheId == "") {
		alert("Error: Cannot Process Return!!! Cache ID is NULL");
		return false;
	}
	if ((Incident_No == "") && (Issue_No == "")) {
		alert("Error: Cannot Process Return!!! Need Either Incident No Or Issue No");
		return false;
	}
	if (Receive_Dock == "") {
		alert("Error: Cannot Process Return!!! Receiving Dock is NULL");
		return false;
	}
	return true;
}  

function check_fields_addRows() {
	var Receive_Dock = document.all("xml:/Receipt/@ReceivingDock").value;
	var Incident_No =  document.all("xml:/Receipt/@IncidentNo").value;
	var Issue_No =  document.all("xml:/Receipt/@IssueNo").value;
	var CacheId =  document.all("xml:/Receipt/@CacheID").value;
	var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;
	if (ReturnNo == "") {
		alert("Return No is NULL !!! Please Enter Return No or Generate New No Before Adding Return Lines");
		return false;
	}
	if (CacheId == "") {
		alert("Cache ID is NULL !!! Please Enter Cache ID Before Adding Return Lines");
		return false;
	}
	if ((Incident_No == "") && (Issue_No == "")) {
		alert("Incident/Issue No is NULL !!! Please Enter Either Incident No Or Issue No Before Adding Return Lines ");
		return false;
	}
	if (Receive_Dock == "") {
		alert("Receiving Dock is NULL !!! Please Enter Receiving Dock Before Adding Return Lines");
		return false;
	}
	return true;
}  

function check_ReturnNo() {
	//Begin CR844 11302012
	var varCacheID = document.all("xml:/Receipt/@CacheID").value;
	if ((varCacheID == "") || (varCacheID == "null") || (varCacheID.length == 0)) {
		alert("Error: Field CacheID cannot be blank. Please enter a value."); 
		return false;    
	} 
	//End CR844 11302012
	var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;
	if (ReturnNo != "") {
		var ret
		ret = confirm("Return No Already Exists. Do you still want to replace with a new one?");
		return ret;
	}
	return true;
}

function deleteRows(elem) {
	var i=elem.parentNode.parentNode.rowIndex
	document.getElementById('ChildTable1').deleteRow(i) 
}

//Begin - Added by aoadio - CR 727
function setParamBillTrans(elem) {
	var value = elem.value;
	var returnArray = new Object();
	returnArray["xml:IssueNo"] = value;
	return returnArray;
}

function handleSplitAccCodeLink(elem,xmlDoc) {
	var nodes = xmlDoc.getElementsByTagName("NWCGBillingAccountList");
	if(nodes != null && nodes.length > 0 ) {
		var item = nodes(0);
		var isAccountSplit = item.getAttribute("IsAccountSplit");
		if("Y" == isAccountSplit) {
			createSplitAccCodeElements(xmlDoc);
			toggleSplitAccCodeLink('visible');
		} else {
			removeSplitAccCodeElements();
			toggleSplitAccCodeLink('hidden');
		}
	}
}

function toggleSplitAccCodeLink(isVisible) {
	var varElem = document.getElementById('SPLIT_ACC_LINK_REF');
	varElem.style.visibility = isVisible;
	if(isVisible == 'hidden') {
		removeSplitAccCodeElements();
		varElem.removeAttribute('href');
	} else {
		varElem.setAttribute('href', 'javascript: openPopup()')
	}
}

function openPopup() {
	var varElem = document.getElementById('SPLIT_ACC_LINK_REF');
	if(varElem != null && varElem.style.visibility != 'hidden') {
		var extraParams = "";
		var numAccounts = document.getElementById('xml:/Receipt/SplitAccountCodeData/@AccountCount').value;
		extraParams += "numAccounts=" + numAccounts;
		for (i = 1; i <= numAccounts; i++) {
			var idAcctCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + i;
			var idAmountCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
			var acctCodeElem = document.getElementById(idAcctCode);
			var amountChargedElem = document.getElementById(idAmountCharged);
			if(acctCodeElem.value != null && acctCodeElem.value != "" ) {
				extraParams += "&accountCode" + i + "=" + acctCodeElem.value + "&amountCharged" + i + "=" + amountChargedElem.value;
			}
		}
		var myObject=new Object();
		myObject.parentWindow=window;
		yfcShowDetailPopupWithParams("NWCRTNSPLD010"," ","1000","300",extraParams,null," ",myObject);
	}    
}

function createSplitAccCodeElements(xmlDoc) {
	removeSplitAccCodeElements();
	var divElem = document.getElementById('splitAccountCodeDataDiv');
	var nodes = xmlDoc.getElementsByTagName("NWCGBillingAccount");
	var idAcctCount = "xml:/Receipt/SplitAccountCodeData/@AccountCount";
	var idSaveAccts = "xml:/Receipt/SplitAccountCodeData/@SaveAccounts";
	document.getElementById(idAcctCount).value = nodes.length;
	document.getElementById(idSaveAccts).value = "N";
	for (var i=0; i < nodes.length; i++) { 
		var idIndex = i + 1;
		var idAcctCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + idIndex;
		var idTotalAmtCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + idIndex;
		var acctCode = "";
		var totalAmtCharged = "";
		if(nodes[i] != null) {
			acctCode = nodes[i].getAttribute("AccountCode");
			totalAmtCharged = nodes[i].getAttribute("TotalAmountCharged");
		}
		var acctCodeElem = document.getElementById(idAcctCode);
		var totalAmtChargedElem = document.getElementById(idTotalAmtCharged);
		acctCodeElem.value = acctCode;
		totalAmtChargedElem.value = totalAmtCharged;
	}
}

function removeSplitAccCodeElements() {
	document.getElementById('xml:/Receipt/SplitAccountCodeData/@AccountCount').value = 0;
	document.getElementById("xml:/Receipt/SplitAccountCodeData/@SaveAccounts").value = "N"; 
	for (var i = 1; i <= 5; i++) { 
		var idAcctCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + i;
		var idTotalAmtCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
		var acctCode = "";
		var totalAmtCharged = "";
		var acctCodeElem = document.getElementById(idAcctCode);
		var totalAmtChargedElem = document.getElementById(idTotalAmtCharged);
		acctCodeElem.value = acctCode;
		totalAmtChargedElem.value = totalAmtCharged;
	}
}
//End - Added by aoadio - CR 727