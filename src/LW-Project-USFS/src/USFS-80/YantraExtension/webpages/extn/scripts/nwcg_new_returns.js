//-----added component details and the PC and UOM are defaulting

//-------------adding code to populate incident returns

function setParamForIncName(elem){
	var currentRow = elem.parentElement.parentElement.parentElement;
	var incidentNo = "";
	var incidentYear = "";
	var returnArray = new Object();

	//alert("displaying current row:-"+currentRow.innerHTML);

	//--------------
	var InputList = currentRow.getElementsByTagName("input");

	for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@IncidentNo') != -1)
			{
				 incidentNo = InputList[cnt1].value;
			}//end if
			if(InputList[cnt1].name.indexOf('@IncidentYear') != -1)
			{
				 incidentYear = InputList[cnt1].value;
			}//end if
	}//End For Loop
	
	//alert("incidentNo:-"+incidentNo);

	returnArray["xml:ExtnIncidentNo"] = incidentNo;
	returnArray["xml:ExtnIncidentYear"] = incidentYear;
	//alert ("Incident Year : " +incidentYear);

	return returnArray;
	//--------------

}//End setParamForIncName

function populateIncName(elem,xmlDoc){

	var IncidentName = "";
	var IncidentYear= "";
	var CustomerId = "";
	var currentRow = elem.parentElement.parentElement.parentElement;
	var IncidentFSAcctCode = "";
    var IncidentOverrideCode = "";
    var IncidentBlmAcctCode = "";
    var IncidentOtherAcctCode = "";
	var RecvOwnerAgency = document.getElementById("xml:/Receipt/@RecvOwnerAgency").value;

	//alert("displaying current row:-"+currentRow.innerHTML);
	
	var NWCGIncidentOrderListNodes = xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		if(NWCGIncidentOrderListNodes!=null && NWCGIncidentOrderListNodes.length > 0 )
		{	
			var ResultNode = NWCGIncidentOrderListNodes(0);
			IncidentName = ResultNode.getAttribute("IncidentName") ;
			//alert("IncidentName "+IncidentName);
			if (IncidentName == null)
			{
				IncidentName = "";
			}
			CustomerId = ResultNode.getAttribute("CustomerId");
			if (CustomerId  == null)
			{
				CustomerId  = "";
			}
			IncidentYear = ResultNode.getAttribute("Year");
			if (IncidentYear  == null)
			{
				IncidentYear  = "";
			}
			//alert("Year = "+(ResultNode.getAttribute("Year")));
            IncidentFSAcctCode = ResultNode.getAttribute("IncidentFsAcctCode");
            IncidentOverrideCode = ResultNode.getAttribute("IncidentOverrideCode");
            IncidentBlmAcctCode = ResultNode.getAttribute("IncidentBlmAcctCode");
            IncidentOtherAcctCode = ResultNode.getAttribute("IncidentOtherAcctCode");
			//alert("IncidentFSAcctCode : "+IncidentFSAcctCode);

			if (RecvOwnerAgency == null || RecvOwnerAgency == "")
			{
		    	alert("Only Node Users can Process Return !!!");
				document.getElementById("xml:/Receipt/@IncidentNo").value = "";
				document.getElementById("xml:/Receipt/@IncidentYear").value = "";
				document.getElementById("xml:/Receipt/@IncidentName").value = "";
				document.getElementById("xml:/Receipt/@CustomerId").value = "";
				return false;
			}

			if (RecvOwnerAgency == "BLM" && (IncidentBlmAcctCode == null || IncidentBlmAcctCode == ""))
			{
				alert("This Incident doesn't have a BLM Account Code. Enter Incident BLM Account Code and Process Return !!!");
				document.getElementById("xml:/Receipt/@IncidentNo").value = "";
				document.getElementById("xml:/Receipt/@IncidentYear").value = "";
				document.getElementById("xml:/Receipt/@IncidentName").value = "";
				document.getElementById("xml:/Receipt/@CustomerId").value = "";
				return false;
			}
			else if (RecvOwnerAgency == "FS" && (IncidentFSAcctCode == null || IncidentFSAcctCode == ""))
			{
				alert("This Incident doesn't have a FS Account Code. Enter Incident FS Account Code and Process Return !!!");
				document.getElementById("xml:/Receipt/@IncidentNo").value = "";
				document.getElementById("xml:/Receipt/@IncidentYear").value = "";
				document.getElementById("xml:/Receipt/@IncidentName").value = "";
				document.getElementById("xml:/Receipt/@CustomerId").value = "";
				return false;
			}
			else if (RecvOwnerAgency == "OTHER" && (IncidentOtherAcctCode == null || IncidentOtherAcctCode == ""))
			{
	            alert("This Incident doesn't have an OTHER Account Code. Enter Incident OTHER Account Code and Process Return !!!");
				document.getElementById("xml:/Receipt/@IncidentNo").value = "";
				document.getElementById("xml:/Receipt/@IncidentYear").value = "";
				document.getElementById("xml:/Receipt/@IncidentName").value = "";
				document.getElementById("xml:/Receipt/@CustomerId").value = "";
				return false;
			}

		}
		else
	   {
        alert ("Invalid Incident !!!");
		document.getElementById("xml:/Receipt/@IncidentNo").value = "";
		document.getElementById("xml:/Receipt/@IncidentYear").value = "";
		document.getElementById("xml:/Receipt/@IncidentName").value = "";
		document.getElementById("xml:/Receipt/@CustomerId").value = "";
		return false;
	   }
	//alert("NWCGIncidentOrderList.length:-"+NWCGIncidentOrderListNodes.length+" IncidentName:-"+IncidentName);
		
			var InputList = currentRow.getElementsByTagName("input");

			for(cnt1=0;cnt1<InputList.length;cnt1++){
					if(InputList[cnt1].name.indexOf('@IncidentName') != -1)
					{
						 InputList[cnt1].value=IncidentName;
					}//end if
					if(InputList[cnt1].name.indexOf('@CustomerId') != -1)
					{
						 InputList[cnt1].value=CustomerId;
					}
					if(InputList[cnt1].name.indexOf('@IncidentYear') != -1)
					{
						 InputList[cnt1].value=IncidentYear;
					}
					//end if
			}//End For Loop
	
		return true;
	
}//end populateIncName()


//===============validating date format of "DLT" 
function checkDLTFormat(elem){
	var currentRow = elem.parentElement.parentElement;
	//alert("currentRow:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");

	for(cnt1=0;cnt1<InputList.length;cnt1++){
		if(InputList[cnt1].name.indexOf('@TagAttribute4') != -1 || InputList[cnt1].name.indexOf('@RevisionNo') != -1)
		{
			var tempRevisionNo = InputList[cnt1].value;
			//alert("tempRevisionNo=["+tempRevisionNo+"]");
			if (tempRevisionNo.length > 0) // validate only when a value is entered
			{
				if (tempRevisionNo.indexOf('/') != 2 || tempRevisionNo.length != 10)
				{
					alert("Please enter a valid date in \"mm/dd/yyyy\" format");
					return false;
				}
			}
		}//end if
	}//End For Loop
	return true;
}

/*===============validating the total of Return Qty */
function checkRFI(elem)
{
	var currentRow = elem.parentElement.parentElement;
	var RFI = 0;
	var NRFI = 0;
	var UnsRet = 0;
	var UnsRetNWT = 0;
	var retQty = 0;
//	alert("In CheckRFI");
//	alert("currentRow:-"+currentRow.innerHTML);

	var InputList = currentRow.getElementsByTagName("input");

	var last = "";
	for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@RFI') != -1)
			{
				 RFI = parseInt(InputList[cnt1].value);
				 if(isNaN(RFI))
				 {
					 InputList[cnt1].value = "";
				 }
			}//end if

			if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
			{
				 retQty = parseInt(InputList[cnt1].value);
				 if(isNaN(retQty))
				 {
					 InputList[cnt1].value = "";
				 }
			}//end if
	}//End For Loop

			//alert("RFI"+RFI+" retQty:-"+retQty);
			if(RFI>retQty)
			{
					alert("RFI QTY exceeding Return Quantity");
					//------------add lines to return control
					for(cnt1=0;cnt1<InputList.length;cnt1++)
					{
							if(InputList[cnt1].name.indexOf('@RFI') != -1)
							{
								 //alert("reseting control");
									InputList[cnt1].value ="";
							}//End If 
					}//End for loop	
				//------------End lines
					return true;
			}//Range check

	//alert("RFI:-"+RFI+" NRFI:-"+NRFI+" UnsRet:-"+UnsRet+" UnsRetNWT:-"+UnsRetNWT);
	return true;
}//end checkRFI 

function checkNRFI(elem)
{
	//alert("In CheckNRFI");
	
	var NRFI = 0;
	var retQty = 0;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");

		//alert("check called"+currentRow.innerHTML);
		
		for(cnt1=0;cnt1<InputList.length;cnt1++)
		{
			if(InputList[cnt1].name.indexOf('@NRFI') != -1)
			{
				 NRFI = parseInt(InputList[cnt1].value);
				 if(isNaN(NRFI))
				 {
					 InputList[cnt1].value = "";
				 }
				 //return true;
			}//end if

			if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
			{
			 retQty = parseInt(InputList[cnt1].value);
			 if(isNaN(retQty))
			 {
				   InputList[cnt1].value = "";
			 }
			}//end if
		}//End For
	
		//alert("NRFI"+NRFI+" retQty:-"+retQty);
		if(NRFI>retQty)
		{
				alert("NRFI QTY exceeding Return Quantity");
				//------------add lines to return control
					for(cnt1=0;cnt1<InputList.length;cnt1++)
					{
							if(InputList[cnt1].name.indexOf('@NRFI') != -1)
							{
								 //alert("reseting control");
									InputList[cnt1].value ="";
							}//End If 
					}//End for loop	
				//------------End lines
				return true;
		}//Range check
}//End chechNRFI 


function checkUnsRet(elem)
{
	//alert("testing--checkUnsRet");
	var UnsRet = 0;
	var retQty = 0;
	var currentRow = elem.parentElement.parentElement;
	var InputList = currentRow.getElementsByTagName("input");

		//alert("check called"+currentRow.innerHTML);
		
		for(cnt1=0;cnt1<InputList.length;cnt1++)
		{
			if(InputList[cnt1].name.indexOf('@UnsRet') != -1)
			{
				 UnsRet = parseInt(InputList[cnt1].value);
				 if(isNaN(UnsRet))
				 {
					 InputList[cnt1].value = "";
				 }
				// alert("test--------"+UnsRet);
				 break;
			}//end if

			if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
			{
			 retQty = parseInt(InputList[cnt1].value);
			 if(isNaN(retQty))
			 {
				   InputList[cnt1].value = "";
			 }
			}//end if
		}//End For
	
		//alert("UnsRet"+UnsRet+" retQty:-"+retQty);
		if(UnsRet>retQty)
		{
				alert("UnsRet QTY exceeding Return Quantity");
				//parseInt(InputList[cnt1].value = "";
				//------------add lines to return control
					for(cnt1=0;cnt1<InputList.length;cnt1++){
							if(InputList[cnt1].name.indexOf('@UnsRet') != -1){
								 //alert("reseting control");
									InputList[cnt1].value ="";
							}//End If 
					}//End for loop	
				//------------End lines
				return true;
		}//Range check
}//End chechUnsRet


function checkUnsRetNWT(elem){
	//alert("testing---checkUnsRetNWT");
var UnsRetNWT = 0;
var retQty = 0;
var RFI = 0;
var NRFI = 0;
var UnsRet = 0;
var total = 0;
var currentRow = elem.parentElement.parentElement;
var InputList = currentRow.getElementsByTagName("input");

		//alert("check called"+currentRow.innerHTML);
		
		for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@UnsRetNWT') != -1)
			{
				 UnsRetNWT = parseInt(InputList[cnt1].value);
				 //alert("UnsRetNWT "+ UnsRetNWT);
				 
				 if(isNaN(UnsRetNWT))
				 {
					 UnsRetNWT = 0;
					 InputList[cnt1].value = "";
				 }
				 //return true;
			}//end if
			if(InputList[cnt1].name.indexOf('@RFI') != -1)
			{
				 RFI = parseInt(InputList[cnt1].value);
				 //alert("RFI "+ RFI);
				 if(isNaN(RFI))
				 {
					 RFI = 0;
					 InputList[cnt1].value = "";
				 }
				 //return true;
			}//end if
			if(InputList[cnt1].name.indexOf('@NRFI') != -1)
			{
				 NRFI = parseInt(InputList[cnt1].value);
				 //alert("NRFI "+ NRFI);
				 if(isNaN(NRFI))
				 {
					 NRFI = 0;
					 InputList[cnt1].value = "";
				 }
				 //return true;
			}//end if

		    if(InputList[cnt1].name.indexOf('@UnsRet') != -1)
			{
				 UnsRet = parseInt(InputList[cnt1].value);
				 //alert("UnsRet "+ UnsRet);
				 if(isNaN(UnsRet))
				 {
					 UnsRet = 0;
					 InputList[cnt1].value = "";
				 }
				 //return true;
			}//end if
			

			if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
			{
			 retQty = parseInt(InputList[cnt1].value);
			 if(isNaN(retQty))
				 {
					 retQty = 0;
					 InputList[cnt1].value = "";
				 }
			}//end if

			if(InputList[cnt1].name.indexOf('@serialID') != -1)
			{
				return true;
			}

			if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1)
			{
				return true;
			}

		}//End For

		for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@UnsRet') != -1)
			{
				 UnsRet = parseInt(InputList[cnt1].value);
				 if(isNaN(UnsRet))
				 {
					 UnsRet = 0;
					 InputList[cnt1].value = "";
				 }
				 break;
				 //return true;
			}//end if
		}//End For Loop 

        
		total = RFI + NRFI + UnsRet + UnsRetNWT;
		//alert("retQty:-"+retQty+ "RFI:-"+RFI+" NRFI:-"+NRFI+" UnsRet:-"+UnsRet+" UnsRetNWT:-"+UnsRetNWT+" Total:-"+total);

	   if(total!=retQty){
	   
			alert("TOTAL (=RFI+NRFI+UnsRet+UnsRetNWT) not equal to \"Return Quantity\"");
			return true;
	   }//End IF

  

		if(UnsRetNWT>retQty){
				alert("UnsNWT QTY exceeding Return Quantity");
				//parseInt(InputList[cnt1].value = "";
				//------------add lines to return control
					for(cnt1=0;cnt1<InputList.length;cnt1++){
							if(InputList[cnt1].name.indexOf('@UnsRetNWT') != -1){
								 //alert("reseting control");
									InputList[cnt1].value ="";
									break;
							}//End If 
					}//End for loop	
				//------------End lines
				return true;
		}//Range check
		
}//End chechUnsRet
//===================================================End validating total QTY
//--------------------javascript for validating the Total quantity

//function for populating serials for the components
function setParamCompSerial(elem){

	var ItemID = "";
	var returnArray = new Object();

	var currentRow = elem.parentElement.parentElement;
	////alert("component serials :-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");

	for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@ItemID') != -1)
			{
				 ItemID = InputList[cnt1].value;
			}//end if
	}//End For Loop

	//alert("ItemID:-"+ItemID);
	returnArray["xml:ItemID"] = ItemID;

	return returnArray;
}//-----------------------End setParamCompSerial


//-------End populating component serials
//----------------for validating primary serial
function setParamPrimSerial(elem){

	var PrimeSerail = "";
	var ItemID = "";
	var PC	 = "";
	var UOM = "";
	var returnArray = new Object();
	var currentRowID = "";
	var checkArray = new Array();
	var tempRowID = "";

	//alert("setParamSecSerial reached");
	var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
	
	var currentRow = elem.parentElement.parentElement;
	var validateRow = elem.parentElement.parentElement.parentElement;

	//alert("suresh...testingin validateRow:-"+validateRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");
	
	//return true;
	for(cnt1=0;cnt1<InputList.length;cnt1++){
			//var name = InputList[0].name;
			
			//alert("InputList[cnt1].name:-"+InputList[cnt1].name + "length:-"+InputList.length);
			if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1)
			{
				 PrimeSerail = InputList[cnt1].value;
				 currentRowID = InputList[cnt1].id;
				// alert("RowID:-"+currentRowID);
			}//end if

			if(InputList[cnt1].name.indexOf('PC') != -1)
			{
				 PC = InputList[cnt1].value;

			}//end if

			if(InputList[cnt1].name.indexOf('UOM') != -1)
			{
				 UOM = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('ITEMID') != -1)
			{
				 ItemID = InputList[cnt1].value;
			}//end if
	}//End for Loop

	if(PrimeSerail==""||PrimeSerail==null){
		//alert("INVALID VALUES FOR PRIMARY SERIAL");
		//return true;
	}
//======================Make sure the serial# are  unique
    if(currentRowID!="0"){
		var InputList2 = validateRow.getElementsByTagName("input");
		//alert("input list length:-"+InputList2.length);
		
		var arr_counter=0;
		for(cnt2=0;cnt2 <= InputList2.length;cnt2++){
			//alert("cnt2:-"+cnt2);
			if(InputList2[cnt2].name.indexOf('@PrimarySerialNo') != -1)
			{
				 temp = InputList2[cnt2].value;
				 checkArray.push(temp);
				 arr_counter= arr_counter +1;
				 tempRowID = InputList2[cnt2].id;
				 //alert("tempRowID:-"+tempRowID + "Value:-"+temp);
				 
				 if (tempRowID != "0")
				 { //alert("cnt2:-"+cnt2+"arr_counter:-"+arr_counter);
					for(cnt3=0;cnt3<arr_counter-1;cnt3++){
					//alert("cnt3:-"+cnt3+"currentRowID:-"+currentRowID+"checkArray[cnt3]:-"+checkArray[cnt3]);
						if(temp == checkArray[cnt3] ){
							alert("DUPLICATE ENTRY :-"+temp);
							break;
						}//End IF
					//	alert("continue on for loop-cnt3"+cnt3+"arr_counter:-"+arr_counter);
					}//End inner For Loop
				 }//End If - outer

				 if(tempRowID == currentRowID){
					 //alert("breaking out of for loop");
					break;
				 }
			}//End If
		}//End for Loop
	//---------------End finding duplicate value
	}//End IF 
//====================End make sure the serial# is unique

	//returnArray["xml:SecSerialNo"] = SecondarySerial;
	returnArray["xml:IncidentNo"] = IncidentNo;
    returnArray["xml:IncidentYear"] = IncidentYear;
	returnArray["xml:IssueNo"] = IssueNo;
	returnArray["xml:SerialNo"] = PrimeSerail;
	returnArray["xml:ItemID"] = ItemID;
	returnArray["xml:PC"] = PC;
	returnArray["xml:UOM"] = UOM;

	//alert("CAlling ajax ---PrimeSerail:-"+PrimeSerail+" ITEMID:="+ItemID+"PC:-"+PC+"UOM:-"+UOM);

	return returnArray;

}//========================End setParamPrimSerial

function setParamSecSerial(elem){

	var PrimeSerail = "";
	var SecondarySerial= "";
	var ItemID = "";
	var PC	 = "";
	var UOM = "";
	var returnArray = new Object();

    var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
	//alert("Incident No "+ IncidentNo);

	////alert("setParamSecSerial reached");
	
	var currentRow = elem.parentElement.parentElement;
	//alert("in setparamSecSerial:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");
	
	for(cnt1=0;cnt1<InputList.length;cnt1++){
			//var name = InputList[0].name;
			
			////alert("InputList[cnt1].name:-"+InputList[cnt1].name);
			if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1)
			{
				 PrimeSerail = InputList[cnt1].value;
			}//end if

			//Following line is added for the component serials
			if(InputList[cnt1].name.indexOf('@serialID') != -1)
			{
				 PrimeSerail = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('@SecondarySerialNo') != -1)
			{
				 SecondarySerial = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('PC') != -1)
			{
				 PC = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('UOM') != -1)
			{
				 UOM = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('ITEMID') != -1)
			{
				 ItemID = InputList[cnt1].value;
			}//end if

	}//End for Loop

	if(ItemID==""||ItemID==null||PrimeSerail==""||PrimeSerail==null||SecondarySerial==""||SecondarySerial==null){
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

	////alert("SecondarySerial:-"+SecondarySerial+" PrimeSerail:-"+PrimeSerail+" ITEMID:="+ItemID+"PC:-"+PC+"UOM:-"+UOM);

	return returnArray;

}//=============End setParamSecSerial

function PopupMessage(elem,xmlDoc){

	var Result ="";

	var nodes=xmlDoc.getElementsByTagName("Result");
	if(nodes!=null && nodes.length > 0 )
	{	
		var ResultNode = nodes(0);
		var Result = ResultNode.getAttribute("SerialPresent");
		//alert("Result "+Result);
		var SecSerial = ResultNode.getAttribute("SecondarySerial1");
	}//End Nodes
   //Change by GN on 01/24/07 - False To True

    if (Result == "False")
    {
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
		//alert("TRow.innerHTML"+TRow.innerHTML);
		var TList = TRow.getElementsByTagName("input");
		for (tcnt=0;tcnt<TList.length;tcnt++)
		{
			if(TList[tcnt].name.indexOf('@PrimarySerialNo') != -1)
		     {
		       document.getElementById("xml:/Receipt/@SerialList").value = seriallist+','+TList[tcnt].value;
		     }//end if
			if(TList[tcnt].name.indexOf('@serialID') != -1)
			{
			   document.getElementById("xml:/Receipt/@SerialList").value = seriallist+','+TList[tcnt].value;
			}//end if

			if(TList[tcnt].name.indexOf('@SecondarySerialNo') != -1)
		    {
		     TList[tcnt].value=SecSerial;
		     }//end if
			if(TList[tcnt].name.indexOf('@TagAttribute1') != -1)
		     {
		       TList[tcnt].value=LotAttribute1;
		     }//end if
	        if(TList[tcnt].name.indexOf('@TagAttribute2') != -1)
		     {
		       TList[tcnt].value=LotAttribute3;
		     }//end if
	        if(TList[tcnt].name.indexOf('@TagAttribute3') != -1)
		     {
		       TList[tcnt].value=LotNumber;
		     }//end if
	        if(TList[tcnt].name.indexOf('@TagAttribute4') != -1)
		     {
		       TList[tcnt].value=RevisionNo;
		     }//end if
	        if(TList[tcnt].name.indexOf('@TagAttribute5') != -1)
		     {
		      TList[tcnt].value=BatchNo;
		     }//end if
			 if(TList[tcnt].name.indexOf('@TagAttribute6') != -1)
		     {
		      TList[tcnt].value=LotAttribute2;
		     }//end if
		}

		return true;
    }
	else
	{
      var currentRow = elem.parentElement.parentElement;
	  ////alert("inside POPUP resutl was false :-"+currentRow.innerHTML);
	  var InputList = currentRow.getElementsByTagName("input");
	  for(cnt1=0;cnt1<InputList.length;cnt1++){
	  //alert("InputList[cnt1].name:-"+InputList[cnt1].name);
	  if(InputList[cnt1].name.indexOf('@PrimarySerialNo') != -1)
		{
	 	 InputList[cnt1].value="";
		}//end if

      if(InputList[cnt1].name.indexOf('@serialID') != -1)
		 {
		  InputList[cnt1].value="";
		 }

	  if(InputList[cnt1].name.indexOf('@SecondarySerialNo') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  if(InputList[cnt1].name.indexOf('@TagAttribute1') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  if(InputList[cnt1].name.indexOf('@TagAttribute2') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  if(InputList[cnt1].name.indexOf('@TagAttribute3') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  if(InputList[cnt1].name.indexOf('@TagAttribute4') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  if(InputList[cnt1].name.indexOf('@TagAttribute5') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
      if(InputList[cnt1].name.indexOf('@TagAttribute6') != -1)
		{
		 InputList[cnt1].value="";
		}//end if
	  }//End For Loop

	  if (Result == "True")
	  {
		 alert("TRACKABLE ID ALREADY AT NODE !!!");
		 return false;
	  }
	  if (Result == "NotInIncident")
	  {
		 alert("INVALID TRACKABLE ID FOR THIS INCIDENT !!!");
		 return false;
	  }
	  if (Result == "Invalid Input")
	  {
		  alert("INVALID TRACKABLE ID !!!");
		  return false;
	  }

	}//End IF 
 return true;
}//PopupMessage

//Added by GN for CR-354, Return No Validation
function setReturnParam(elem){

	var CacheID = "";
	var ReturnNo = "";
	var returnArray = new Object();

    var CacheID = document.getElementById("xml:/Receipt/@CacheID").value;
	var ReturnNo = document.getElementById("xml:/Receipt/@ReceiptNo").value;

	if(CacheID==""||CacheID==null){
		//alert("PLEASE ENTER VALID VALUES FOR PRIMARY AND SECONDARY SERIAL");
		//return true;
	}

    returnArray["xml:CacheID"] = CacheID ;
	returnArray["xml:ReturnNo"] = ReturnNo ;

	return returnArray;

}//=============End setReturnParam

//Added by GN for CR-354, Return No Validation
function PopupReturnMesg(elem,xmlDoc){

	var Result ="";

	var nodes=xmlDoc.getElementsByTagName("Result");
	if(nodes!=null && nodes.length > 0 )
	{	
		var ResultNode = nodes(0);
		var Result = ResultNode.getAttribute("ValidReturnNo");
		//alert("Result "+Result);
	}//End Nodes

	  if (Result == "False")
	  {
		  alert("Invalid Return No !!!");
		  document.getElementById("xml:/Receipt/@ReceiptNo").value = "";
		  document.getElementById("xml:/NextReceiptNo/@ReceiptNo").value = "";
		  return false;
	  }

	  if (Result == "Received")
	  {
		  alert("This Return No is already received, Please enter another Return No !!!");
		  document.getElementById("xml:/Receipt/@ReceiptNo").value = "";
		  document.getElementById("xml:/NextReceiptNo/@ReceiptNo").value = "";
		  return false;
	  }

	  return true;

}

//------------------------End validating serials
//------------------------for populating item details ------
function setParam(ele)
{
	var EntCode = document.getElementById("xml:/Shipment/@EnterpriseCode");
	
	var test = document.all['xml:/Shipment/@EnterpriseCode'].value;
	var returnArray = new Object();

	var ECode = EntCode.value;
	var itemID = ele.value;
	////alert("ECode"+test);

	if(itemID==null||itemID==""){
		//alert("PLEASE ENTER VALID ITEMID");
	}

	returnArray["xml:EntCode"] = EntCode.value;
	returnArray["xml:ItemID"] = ele.value;

	////alert("ItemID"+itemID);
	return returnArray;
}//setParam

function setParamQty(elem){
	
	var ItemID = "";
	var RetQty = "";
	////alert("setParam3 reached");
	var currentRow = elem.parentElement.parentElement;
	
	//alert("in setparamQTY:-"+currentRow.innerHTML);
	//return true;

	var InputList = currentRow.getElementsByTagName("input");
	
	for(cnt1=0;cnt1<InputList.length;cnt1++){
			//var name = InputList[0].name;

			if(InputList[cnt1].name.indexOf('@ItemID') != -1)
			{
				 ItemID = InputList[cnt1].value;
			}//end if

			if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
			{
				 RetQty = InputList[cnt1].value;
			}//end if
	}//End for Loop

	//var value = InputList[0].value;
	if(ItemID==null|| ItemID==""){
		//alert("PLEASE ENTER ITEM ID");
		return;
	}//End IF--Check if itemId is present

	if(RetQty<=0){
		//alert("PLEASE ENTER VALID RETURN QUANTITY");
	}//End IF 

	//alert("ItemID:-"+ItemID+" @QtyReturned:-"+RetQty);

	//return true;

	var returnArray = new Object();
	returnArray["xml:ItemID"] = ItemID;
	returnArray["xml:RetQty"] = RetQty;
	return returnArray;

}//End setParam3




function setParam3(elem){
	var value = elem.value;
	var returnArray = new Object();
	returnArray["xml:OrderNo"] = value;
	return returnArray;

}//End setParam3

function populateIncNo(elem,xmlDoc){
	//-- CR 615 BEGIN M.Lathom
	
	var ordNode=xmlDoc.getElementsByTagName("Order");
	var nodes=xmlDoc.getElementsByTagName("Extn");
	var documentType =  "";
	var ExtnIncidentNo = "" ;
	var ExtnIncidentYear = "" ;
	var ExtnIncidentName = "" ;
	
	if(ordNode != null && ordNode.length > 0)
	{
		fetchDataWithParams(elem,'getBilllingAccountListForReturns', handleSplitAccCodeLink,setParamBillTrans(elem));
		documentType = ordNode(0).getAttribute("DocumentType");
		if(documentType == "0008.ex")
		{ // Then This is a Incident Xfer Issue
			if(nodes!=null && nodes.length > 0 )
			{	
				var ExtnNode = nodes(0);	
				ExtnIncidentNo = ExtnNode.getAttribute("ExtnToIncidentNo") ;
				ExtnIncidentYear = ExtnNode.getAttribute("ExtnToIncidentYear") ;
				ExtnIncidentName = ExtnNode.getAttribute("ExtnToIncidentName");
			}
		}
		else
		{ // Normal Issue
			if(nodes!=null && nodes.length > 0 )
			{	
				var ExtnNode = nodes(0);
				ExtnIncidentNo = ExtnNode.getAttribute("ExtnIncidentNo") ;
				ExtnIncidentYear = ExtnNode.getAttribute("ExtnIncidentYear") ;
				ExtnIncidentName = ExtnNode.getAttribute("ExtnIncidentName");		
			}
				
		}
	}
	// -- CR 615 END M.Lathom
	var currentRow = elem.parentNode.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("input");

		for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			////alert(name);
			if(InputList[i].name.indexOf('@IncidentNo') != -1)
			{
				if(ExtnIncidentNo != null && ExtnIncidentNo != "")
				{
					InputList[i].value = ExtnIncidentNo;
				}
					
			}//end if

			if(InputList[i].name.indexOf('@IncidentYear') != -1)
			{
				if(ExtnIncidentYear != null && ExtnIncidentYear != "")
				{
					InputList[i].value = ExtnIncidentYear;
				}
			}//end if

			if(InputList[i].name.indexOf('@IncidentName') != -1)
			{
				if(ExtnIncidentName != null && ExtnIncidentName != "")
				{
					InputList[i].value = ExtnIncidentName;
				}
				
			}//end if
		}//End For Loop

// Begin CR 600 - ML
document.getElementById('OrderNum').focus();
document.getElementById('Notes').focus();
	return true;
// End		 CR 600 - ML
}//End populateIncNo



function populateItemDetails1(elem,xmlDoc)
{
	
	//Based on the input xml perform the following :-
	//1) Add the line to capture tag attributes :-
		
		//addTagRows(elem,xmlDoc);

	//2) Add lines to capture serial numbers(primary and secondary) and expiration date
		
		//addSerialRows(elem,xmlDoc);

	//3) Add lines to Receive kit as components
		
		//receiveKitComponents(elem,xmlDoc);

//	alert("xmlDoc:-"+xmlDoc.innerHTML);
//	alert("Inside populate details");
	var nodes=xmlDoc.getElementsByTagName("Item");
// BEGIN CR 883 Manish K
// Moved setting CacheID up here
	var CacheId = document.all("xml:/Receipt/@CacheID").value;
	//alert("MPK1 CacheId:-"+CacheId);
	// In case the cacheid is still null
	if(CacheId==undefined || CacheId==null || CacheId==''){
	    //alert("MPK2 CacheId:-"+CacheId);
		CacheId =  document.getElementById("xml:/Receipt/@CacheID").value;
	}
	//alert("MPK3 CacheId:-"+CacheId);
//END CR 883 Manish K
	
	if(nodes!=null && nodes.length > 0 )
	{
		
		//alert("should not reach here : nodes:="+nodes.length)
		var item = nodes(0);
		var UOM = item.getAttribute("UOM") ;
		var ProductClass = item.getAttribute("ProductClass") ;
		var ItemDescription = item.getAttribute("ShortDescription");
		//sProductLine 

		var sProductLine = item.getAttribute("sProductLine");
		//BEGIN CR 883 Manish K moving setting CacheID up
		//var CacheId =  document.all("xml:/Receipt/@CacheID").value;
		//END CR 883 Manish K moving the following up
		var sItemID = item.getAttribute("ItemID");

		var currentRow = elem.parentNode.parentNode;
		
		var InputList = currentRow.getElementsByTagName("input");
		var length = InputList.length;
		//alert("currentRow " + currentRow);

		// try this 
		var rowNum = parseInt(InputList[0].value)-1;
		var rowIndex = currentRow.rowIndex;

		if(CacheId != "IDGBK" && sProductLine == "NIRSC Communications")
		{

			
			alert("Item : " + sItemID + " can be Returned only at Cache IDGBK");			
			//document.getElementById("xml:/Receipt/ReceiptLines/ReceiptLine_"+rowNum+"/@ItemID").value = "";
			
			 document.getElementById('ChildTable1').deleteRow(rowIndex)
			return false;
					
		}

		if(ProductClass==""||ProductClass==null||UOM==""||UOM==null){
			////alert("testing flow 1");
			return true;
		}//End IF

	   for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			//alert(name);
			if(InputList[i].name.indexOf('@ShortDescription') != -1)
			{
				InputList[i].value = ItemDescription;
			}//end if
		}//End For Loop

		for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			////alert(name);
			if(InputList[i].name.indexOf('@ProductClass') != -1)
			{
				InputList[i].value = ProductClass;
			}//end if
		}//End For Loop

		//Set UOM
		for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			////alert(name);
			if(InputList[i].name.indexOf('@UOM') != -1)
			{
				InputList[i].value = UOM;
			}//end if
		}//End For Loop

		//return true;

		
		//Suresh Commented this to perform population on tabout of retQTY
		//addTagRows(elem,xmlDoc);

		return true ;
	}
	else
	{
		//alert('PLEASE ENTER VALID ITEM ID.. ITEM ID NOT IN RECORDS');
		var currentRow = elem.parentNode.parentNode;
		////alert(currentRow.innerHTML);
		var InputList = currentRow.getElementsByTagName("input");
		//var length = InputList.length;
		////alert(length);
		//Set product class
		for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			////alert(name);
			if(InputList[i].name.indexOf('@ProductClass') != -1)
			{
				InputList[i].value = "";
			}//end if
		}//End For Loop

		//Set UOM
		for (i = 0 ; i < InputList.length ; i++)
		{
			var name = InputList[i].name;
			////alert(name);
			if(InputList[i].name.indexOf('@UOM') != -1)
			{
				InputList[i].value = "";
			}//end if
		}//End For Loop
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
		
		////alert(obj);
        if (cssText)
            obj.style.visibility = "visible";
        else
            obj.style.cssText = "visibility:visible";
    }
   
   function setcaseattributes(elem){
		////alert(elem.parentElement.parentElement.innerHTML);
		var tableRow = elem.parentElement.parentElement ;
		var divList = tableRow.getElementsByTagName("Div");
		for(index = 0 ; index < divList.length ; index++)
		{
			////alert(divList[index].innerHTML);
			var show = divList[index].style.visibility ;
			
			 if(show=="hidden")
				divList[index].style.visibility = "visible";
			 else
				divList[index].style.visibility = "hidden";
		}
	}
//-------------------------------------------------SetPArams
function setParam2(elem)
{
	var itemID = "";
	var RetQty = "";
	var currentRow = elem.parentElement.parentElement;
	//alert("elem.parent:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");
	
	for (i = 0 ; i < InputList.length ; i++)
	{
			var name = InputList[i].name;
			var value = InputList[i].value;
			////alert("name:-"+name+"value:-"+value);

			if(InputList[i].name.indexOf('@ItemID') != -1)
			{
				 itemID = InputList[i].value;
			}//end if

			if(InputList[i].name.indexOf('@QtyReturned') != -1)
			{
				 RetQty = InputList[i].value;
			}//end if

	}//End For Loop

	if(itemID==null|| itemID==""){
		//alert("PLEASE ENTER ITEM ID");
		return;
	}else{
		//alert("should not reach here");
	}//End IF--Check if itemId is present

	var returnArray = new Object();
	returnArray["xml:ItemID"] = itemID;
	returnArray["xml:RetQty"] = RetQty ;
	//alert("ItemID--&&&&calling ajax now"+itemID+" returned qty:-"+RetQty);
	return returnArray;
}//setParam
//----------------------------------------------------------------
//Setting checkbox values
function getKitDetails(elem,id){
		var tableRow = elem.parentElement.parentElement;
		
		//alert("ID = " + id);
		
		var itemElement = document.getElementById("ITEM"+id);
		var lookupElement = document.getElementById("LOOKUP"+id);
		
		itemElement.disabled=true;
		lookupElement.onclick=null;
				
		var RFI_Qty = 0;
		////alert("check:-"+ tableRow.innerHTML);
		var inputList = tableRow.getElementsByTagName("input");
			////alert("inputList.length:-"+inputList.length);
			
			for(index = 0 ; index < inputList.length ; index++)
			{
				////alert("check:-"+inputList[index].type);
					if(inputList[index].type=="checkbox"){
						if(inputList[index].checked){
							//alert("set to yes");
							//Call the ajax function
							//--------------Check if ItemID is entered
								var itemID = "";
								var currentRow = elem.parentElement.parentElement;
								////alert("elem.parent:-"+currentRow.innerHTML);
								var InputList = currentRow.getElementsByTagName("input");
								
								for (i = 0 ; i < InputList.length ; i++)
								{
										var name = InputList[i].name;
										var value = InputList[i].value;
										////alert("name:-"+name+"value:-"+value);
										if(InputList[i].name.indexOf('@ItemID') != -1)
										{
											 itemID = InputList[i].value;
											// //alert("itemID:-------"+itemID);
										}//end if

										if(InputList[i].name.indexOf('@RFI') != -1)
			                            {
				                          RFI_Qty = parseInt(InputList[i].value);
				 
				                          if(!isNaN(RFI_Qty))
				                          {
                                             inputList[index].checked = false;
											 alert("Cannot Receive as Components for RFI Qty");
					                         return true;
				                          }
			                            }//end if
								}//End For Loop
							//-------End Check if Item Entered
						
							if(itemID==null||itemID==""){
								inputList[index].checked = false;
								//alert("PLEASE ENTER ITEMID BEFORE CHECKING 'CHECKBOX'");
							}else{
								//alert("ajax getting called");
                            fetchDataWithParams(elem,'getItemCompDetailsForRet',receiveKitComponents,setParam2(elem));
							}//End Else part

							return true;
						}else{	
							//alert("THIS ITEM IS NOT A KIT");
						}//End IF- value of checkbox is 'checked'
					}//End IF
			}//End For Loop
}
//Adding function to create lines for the kit


function addRows(elem){
		var tableRow = elem.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement;
		var divList = tableRow.getElementsByTagName("table");

        if (!check_fields_addRows())
        {
			return false;
        }
		///alert("counter:-"+divList[index].counter);
		var row = elem.parentElement.parentElement.parentElement.parentElement;
		////alert("ROWS:-"+row.innrHTML);

		inputList = row.getElementsByTagName("input");

		////alert("inputList[0]"+inputList[0].value);
		var rowNum = parseInt(inputList[0].value);

		for(index = 0 ; index < divList.length ; index++)
		{
			////alert("test:-"+divList[index].tBodies[0].innerHTML+"rows to add:"+rows);
			if(divList[index].id=="ChildTable1"){
				////alert("ChildTable1"+divList[index].innerHTML);
						var tblBody = divList[index].tBodies[0];
						
						var newRow = tblBody.insertRow(-1);
						newRow.setAttribute('id',inputList[0].value);
						newRow.setAttribute('background-color'," #EAEAF6");
						var newCell0 = newRow.insertCell(0);
						//newCell0.setAttribute('colspan',"2");					
						//var newCell0 = newRow.insertCell(0);

						//alert(" inputList[0].value " + inputList[0].value);

						newCell0.innerHTML = '<td><IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif"></td>&nbsp;<td class="tablecolumn" nowrap="true"><input type="text" size="10" class="unprotectedinput" id="ITEM'+(inputList[0].value - 1)+'"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value -1)+'/@ItemID"  onblur="fetchDataWithParams(this,\'getItemCompDetailsForRet\',populateItemDetails1,setParam(this));"/><img class="lookupicon" id="LOOKUP'+(inputList[0].value -1)+'" onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'UnitOfMeasure\',\'item\',\'xml:/Item/@CallingOrganizationCode=\' +  document.all[\'xml:/Shipment/@EnterpriseCode\'].value )" src="/yantra/console/icons/lookup.gif\" alt="Search for Item"/></td>  &nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" readOnly="yes" value=""  size=20 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@ShortDescription")/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" readOnly="yes" value=""  size=5 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@ProductClass")/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" readOnly="yes" value="" size=5 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UOM")/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5  value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@RFI" id="RFIQTY'+(inputList[0].value - 1)+'" onblur="updateLineTotalQty('+(inputList[0].value - 1)+');"/></td>&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5 onblur="updateLineTotalQty('+(inputList[0].value - 1)+');" id="NRFIQTY'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@NRFI"/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" class="unprotectedinput" size=5 onblur="updateLineTotalQty('+(inputList[0].value - 1)+');" id="UNSQTY'+(inputList[0].value - 1)+'"name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UnsRet"/></td>&nbsp;&nbsp;&nbsp;&nbsp;<td style="background-color:#EAEAF6" bgcolor=#EAEAF6 bgcolor="#EAEAF6" class="tablecolumn" ><input type="text" class="unprotectedinput" value="" size="5" id="NWTQTY'+(inputList[0].value - 1)+'" onblur="updateLineTotalQty('+(inputList[0].value - 1)+');"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@UnsRetNWT"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td class="tablecolumn" ><input type="text" readonly class="protectedinput" size=5 id="TOTQTY'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@QtyReturned" onblur="fetchDataWithParams(this,\'getItemCompDetailsForRet\',addTagRows,setParamQty(this));"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td class="searchlabel" width="30px"><input type="checkbox" class="" id="'+(inputList[0].value - 1)+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@RecdAsComp" onClick="getKitDetails(this,'+(inputList[0].value - 1)+');" value="Y"/></td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" value="" size=30 maxLength=120 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+(inputList[0].value - 1)+'/@LineNotes")/></td></tr><tr><td colspan="120" background-color="#EAEAF6" ><table class="table" width="100%" size=5 id="KitItems'+(inputList[0].value - 1)+'"><tbody></tbody></table></td></tr>';			}//End IF
		}//End For Loop

		inputList[0].value = rowNum + 1 ;
}//End Funciton Addrows

function updateLineTotalQty(lineNumber)
{
	// function updates the total Qty returned for the line
	var RFIsubtotal		= 0;
	var NRFIsubtotal	= 0;
	var UNSsubtotal		= 0;
	var NWTsubtotal     = 0;
	var total			= 0;

	if(isNaN(parseInt(document.getElementById("RFIQTY"+lineNumber).value)))
	{ //RFI Subtotal
		RFIsubtotal = 0;
	}
	else
	{
		RFIsubtotal = parseInt(document.getElementById("RFIQTY"+lineNumber).value);
	}

	if (isNaN(parseInt(document.getElementById("NRFIQTY"+lineNumber).value)))
	{//NRFI Subtotal
		NRFIsubtotal = 0;
	}
	else
	{
		NRFIsubtotal = parseInt(document.getElementById("NRFIQTY"+lineNumber).value);
	}

	if (isNaN(parseInt(document.getElementById("UNSQTY"+lineNumber).value)))
	{//Unserviceable Subtotal
		UNSsubtotal = 0;
	}
	else
	{
		UNSsubtotal = parseInt(document.getElementById("UNSQTY"+lineNumber).value);
	}

	//Normal Wear & Tear Subtotal
	if (isNaN(parseInt(document.getElementById("NWTQTY"+lineNumber).value)))
	{
		NWTsubtotal = 0;
	}
	else
	{
		NWTsubtotal=parseInt(document.getElementById("NWTQTY"+lineNumber).value);
	}

	total = parseInt(RFIsubtotal) + parseInt(NRFIsubtotal) + parseInt(UNSsubtotal) + parseInt(NWTsubtotal);

	document.getElementById("TOTQTY"+lineNumber).value = parseInt(total);
			
}
function check_fields()
{
 //Added by GN - 12/20/06
 //alert("In Check Fields");
 var Receive_Dock = document.all("xml:/Receipt/@ReceivingDock").value;
 var Incident_No =  document.all("xml:/Receipt/@IncidentNo").value;
 var Issue_No =  document.all("xml:/Receipt/@IssueNo").value;
 var CacheId =  document.all("xml:/Receipt/@CacheID").value;
 var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;

if (ReturnNo == "")
 {
	 alert("Error: Cannot Process Return!!! Return No is NULL");
	 return false;
 }


if (CacheId == "")
 {
	 alert("Error: Cannot Process Return!!! Cache ID is NULL");
	 return false;
 }


 if ((Incident_No == "") && (Issue_No == ""))
 {
	 alert("Error: Cannot Process Return!!! Need Either Incident No Or Issue No");
	 return false;
 }

if (Receive_Dock == "")
 {
	 alert("Error: Cannot Process Return!!! Receiving Dock is NULL");
	 return false;
 }
 return true;
}  

function check_fields_addRows()
{
 //Added by GN - 01/11/08
 var Receive_Dock = document.all("xml:/Receipt/@ReceivingDock").value;
 var Incident_No =  document.all("xml:/Receipt/@IncidentNo").value;
 var Issue_No =  document.all("xml:/Receipt/@IssueNo").value;
 var CacheId =  document.all("xml:/Receipt/@CacheID").value;
 var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;

 if (ReturnNo == "")
   {
      alert("Return No is NULL !!! Please Enter Return No or Generate New No Before Adding Return Lines");
	  return false;
   }

if (CacheId == "")
 {
	 alert("Cache ID is NULL !!! Please Enter Cache ID Before Adding Return Lines");
	 return false;
 }


 if ((Incident_No == "") && (Issue_No == ""))
 {
	 alert("Incident/Issue No is NULL !!! Please Enter Either Incident No Or Issue No Before Adding Return Lines ");
	 return false;
 }

if (Receive_Dock == "")
 {
	 alert("Receiving Dock is NULL !!! Please Enter Receiving Dock Before Adding Return Lines");
	 return false;
 }
 return true;
}  

function check_ReturnNo()
{
 //Added by GN - 01/15/08

//Begin CR844 11302012
   var varCacheID = document.all("xml:/Receipt/@CacheID").value;
   if ((varCacheID == "") || (varCacheID == "null") || (varCacheID.length == 0))
   {
       alert("Error: Field CacheID cannot be blank. Please enter a value."); 
       return false;    
   } 
//End CR844 11302012

 var ReturnNo = document.all("xml:/Receipt/@ReceiptNo").value;
 if (ReturnNo != "")
   {
	  var ret
                ret = confirm("Return No Already Exists. Do you still want to replace with a new one?");
	  return ret;
   }
   return true;
}
function deleteRows(elem){
//Added by GN - 12/20/06

 var i=elem.parentNode.parentNode.rowIndex
 document.getElementById('ChildTable1').deleteRow(i)
	 
}//End Function DeleteRows

//Begin- Added by aoadio CR 727

function setParamBillTrans(elem){
	var value = elem.value;
	var returnArray = new Object();
	returnArray["xml:IssueNo"] = value;
	return returnArray;
}

function handleSplitAccCodeLink(elem,xmlDoc)	{
	var nodes = xmlDoc.getElementsByTagName("NWCGBillingAccountList");
	if(nodes != null && nodes.length > 0 ){
		var item = nodes(0);
		var isAccountSplit = item.getAttribute("IsAccountSplit") ;
		if("Y" == isAccountSplit){
			createSplitAccCodeElements(xmlDoc);
			toggleSplitAccCodeLink('visible');
		}
		else{
			removeSplitAccCodeElements();
			toggleSplitAccCodeLink('hidden');
		}
	}
}
function toggleSplitAccCodeLink(isVisible){
	var varElem = document.getElementById('SPLIT_ACC_LINK_REF');
	varElem.style.visibility = isVisible;
	if(isVisible == 'hidden'){
		removeSplitAccCodeElements();
		varElem.removeAttribute('href');
	}
	else{
		varElem.setAttribute('href', 'javascript: openPopup()')
	}
}

function openPopup()	
{
	var varElem = document.getElementById('SPLIT_ACC_LINK_REF');
	
	if(varElem != null && varElem.style.visibility != 'hidden')
	{
		var extraParams = "";
		var numAccounts = document.getElementById('xml:/Receipt/SplitAccountCodeData/@AccountCount').value;
		extraParams += "numAccounts=" + numAccounts;
		
		//alert('numAccounts=' + numAccounts);
		
		for (i=1; i <=numAccounts; i++)
		{
			var idAcctCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + i;
			var idAmountCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + i;
			
			var acctCodeElem = document.getElementById(idAcctCode);
			var amountChargedElem = document.getElementById(idAmountCharged);
			
			if(acctCodeElem.value!= null && acctCodeElem.value != "" )
			{
				extraParams += "&accountCode" + i + "=" + acctCodeElem.value + "&amountCharged" + i + "=" + amountChargedElem.value;
			}
		}
		
		//alert('extraParams:\n' + extraParams);

		var myObject=new Object();
		myObject.parentWindow=window;
		yfcShowDetailPopupWithParams("NWCRTNSPLD010"," ","1000","300",extraParams,null," ",myObject);
	}    
}

function createSplitAccCodeElements(xmlDoc)
{
	removeSplitAccCodeElements();

	var divElem = document.getElementById('splitAccountCodeDataDiv');
	var nodes = xmlDoc.getElementsByTagName("NWCGBillingAccount");
	
	var idAcctCount = "xml:/Receipt/SplitAccountCodeData/@AccountCount";
	var idSaveAccts = "xml:/Receipt/SplitAccountCodeData/@SaveAccounts";
	
	document.getElementById(idAcctCount).value = nodes.length;
	document.getElementById(idSaveAccts).value = "N";
	
	for (var i=0; i < nodes.length; i++) 
	{ 
		var idIndex = i + 1;
		
		var idAcctCode = "xml:/Receipt/SplitAccountCodeData/@RefundAcctCode" + idIndex;
		var idTotalAmtCharged = "xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount" + idIndex;
		
		var acctCode = "";
		var totalAmtCharged = "";
		
		if(nodes[i] != null)
		{
			acctCode = nodes[i].getAttribute("AccountCode");
			totalAmtCharged = nodes[i].getAttribute("TotalAmountCharged");
		}
		
		var acctCodeElem = document.getElementById(idAcctCode);
		var totalAmtChargedElem = document.getElementById(idTotalAmtCharged);
		
		acctCodeElem.value = acctCode;
		totalAmtChargedElem.value = totalAmtCharged;
	}
}

function removeSplitAccCodeElements()
{
	document.getElementById('xml:/Receipt/SplitAccountCodeData/@AccountCount').value = 0;
	document.getElementById("xml:/Receipt/SplitAccountCodeData/@SaveAccounts").value = "N"; 
	
	for (var i=1; i <= 5; i++) 
	{ 
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

//End- Added by aoadio CR 727


//-------------------
