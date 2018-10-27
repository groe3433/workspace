/*
main function to prepare the row to be displayed in the table
a bit messy code, but make sense 
*/
var drawSys = 0;
function prepareTableRow(orderLine,OrderLineCounter,documentType,driverDate,orderHeaderKey,strPrefix,strPostfix,strImage, drawSysCol)
{
	//debugger;
	drawSys = drawSysCol;
	var strLineKey = orderLine.getAttribute("OrderLineKey");
	var strPrimeLineNo = orderLine.getAttribute("PrimeLineNo");
	var elemQtyList = orderLine.getElementsByTagName("OrderLineTranQuantity");
	var strUOM = '';
	var strQty = '' ;
	var strActPricQty = '';
	var strLineStatus = '';
	var draftOrCreatedFound = 0;	
	var disabledText = ' ';
	var elemItem = null
	var elemItemList = orderLine.getElementsByTagName("Item");
	var isSerialTracked = 0;
	var clsForSerialTrackedItems = "unprotectedoverrideinput";
	if(elemItemList!= null && elemItemList.length > 0 ){
		elemItem = elemItemList(0);
		if('Y' == elemItem.getAttribute("IsSerialTracked")){ //
			clsForSerialTrackedItems="protectedtext";
			isSerialTracked = 1; //1== YES, 0 == NO
		}
	}

	//CR 510 logic 

	var lnPriceTotal = orderLine.getElementsByTagName("LineOverallTotals");

	if(lnPriceTotal != null && lnPriceTotal.length > 0) 
	{
		//alert("LineOverallTotals not null ");
	
		var elemLineOverallTotals = lnPriceTotal.item(0);
	
		strActPricQty = checkForNull(elemLineOverallTotals.getAttribute("PricingQty")) ;
	
		//alert(strActPricQty);
	}

	var bModifyLineQty = true ; 
	// check the modification rules for the add quantity
	// get the Modification Rules
	var lstModification = orderLine.getElementsByTagName("Modification");
	// if exist
	if(lstModification  != null && lstModification.length > 0)
	{
		for(index = 0 ; index < lstModification.length ; index++)
		{
			var elemModification = lstModification.item(index);
			var strModificationType = elemModification.getAttribute("ModificationType");
			// if modification type is add quantity
			if(strModificationType == "ADD_QUANTITY")
			{
				var strThroughOverride = elemModification.getAttribute("ThroughOverride");
				// and the user is allowed to change the value ONLY through over ride
				// dont allow the user to modify the values
				
				if(strThroughOverride == "Y")
				{
					bModifyLineQty = false ;
				}
			}
		}// end for
	}// end if 
	
	//added for 4390
	var strIssueQty = '';
		
	//Build up the string for each line's tooltip showing instructions
	var titleAttrib = '';
	var instr = orderLine.getElementsByTagName("Instruction");
	var instructionText = '';
	for (index = 0; index < instr.length; index++) {
		var currInstrElm = instr.item(index);
		titleAttrib = titleAttrib + '['+(index+1)+'] Type: '+ currInstrElm.getAttribute("InstructionType");
		titleAttrib = titleAttrib + ': '+ currInstrElm.getAttribute("InstructionText");
		if (index != 0) {
			 titleAttrib = titleAttrib +'\n\r';	
		}	
	}
	var condVarOne = checkStringForNull(orderLine.getAttribute("ConditionVariable1"));
	var condVarTwo = checkStringForNull(orderLine.getAttribute("ConditionVariable2"));
	var strPrimeLink = "<A onclick='yfcShowDetailPopup(\"ISUYOMD170\", \"\", \"1010\", \"650\", new Object(), \"ISUorderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22"+orderHeaderKey+"%22+OrderLineKey%3D%22"+strLineKey+"%22%2F%3E\");return false;' href=\"\">"+strPrimeLineNo+"</A> ";

	var primeLinkObj = new Object();
	primeLinkObj.text = strPrimeLink;
	primeLinkObj.sortValue = strPrimeLineNo;
	primeLinkObj.className = "numerictablecolumn";

	var strMethodCall = 'yfcShowDetailPopupWithParams(\"YOMD255\", \"\", \"1010\", \"650\", \"ShowReleaseNo=Y\", \"orderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22'+ orderHeaderKey+'%22+OrderLineKey%3D%22';
	var status = orderLine.getAttribute("MaxLineStatusDesc");
	
	// Manish K: This is causing an item qty to be editable or not
	if (status != null)
		draftOrCreatedFound = 1;
	
	//debugger;	
	if (!draftOrCreatedFound) {
		disabledText = 'disabled="disabled"';
	}
	
	
	var elemTotalList = orderLine.getElementsByTagName("LineOverallTotals");
	var strReqNo = '' ;
	var strTotal = '' ;
	var issueQty = '';
	var totalObj = new Object();
	totalObj.text = strTotal;
	
	if(elemTotalList != null && elemTotalList.length > 0 )
	{
		var elemTotal = elemTotalList(0);
		strTotal = strPrefix + '&nbsp;' + elemTotal.getAttribute("LineTotal")+ '&nbsp;' + strPostfix;
		totalObj.text = strTotal;
		totalObj.sortValue = elemTotal.getAttribute("LineTotal");
		totalObj.nowrap = "true";
	}
	
	if(elemQtyList != null && elemQtyList.length > 0 )
	{		
		var elemQty = elemQtyList(0);
		strUOM = elemQty.getAttribute("TransactionalUOM");
		strQty = elemQty.getAttribute("OrderedQty");
		strIssueQty = elemQty.getAttribute("OrderedQty");
		var nQty = strQty * 1 ;
		issueQty = nQty;
		
		// If a draft or created line is found
		// BEGIN: Changed by Manish K to allow Trackable items to be UTF/Fwd/Back
		//if (draftOrCreatedFound == 1 && isSerialTracked==0) {		
		if (draftOrCreatedFound == 1) {
		// END: Changed by Manish K to allow Trackable items to be UTF/Fwd/Back 
			// and if the user can modify the line quantity
			if (condVarTwo != null && condVarTwo != '' && condVarTwo != ' ' && (condVarTwo  == 'XLD-Retrieval')) {
				strQty = '<td align="center" class="protectednumber"> <input align="center" style="width:40px" class=numericprotectedinput value="'+nQty+'" OldValue="'+nQty+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty" disabled="disabled"/>';
			}
			else if(nQty <= 0 || bModifyLineQty)
			{
				strQty = '<td> <input onBlur="checkIssuedQuantity(this)" style="width:40px" class=numericunprotectedinput value="'+nQty+'" OldValue="'+nQty+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty"/>';			
			}
			else {
				strQty = '<td> <input onBlur="checkIssuedQuantity(this)" style="width:40px" class=numericunprotectedinput value="'+nQty+'" OldValue="'+nQty+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty"/>';			
			}									
		}
		else {
			if (condVarTwo != null && condVarTwo != '' && condVarTwo != ' ' && (condVarTwo  == 'XLD-Retrieval')) {
				strQty = '<td align="center" class="protectednumber"> <input align="center" style="width:40px" class=numericprotectedinput value="'+nQty+'" OldValue="'+nQty+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty" disabled="disabled"/>';
			}
			else {
				strQty = '<td align="center"> <input align="center" style="width:40px" class=numericprotectedinput value="'+nQty+'" OldValue="'+nQty+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty"/>';			
			}
		}
	}

	var elemExtnList = orderLine.getElementsByTagName("Extn");
	var strReqNo = '' ;
	var strUTFQtyValue = '';
	var strFwdQtyValue = '';
	var strBOQtyValue = '';	
	//added for 4390
	var strRequQty='';
	var elemExtn = null;
	var systemNo = '';
	var sysNoCell = '';

	//debugger;
	if(elemExtnList!=null && elemExtnList.length > 0 )
	{
		elemExtn = elemExtnList(0);
		strReqNo = elemExtn.getAttribute("ExtnRequestNo");
		strReqNo  = checkForNull(strReqNo);
		strRequQty = checkForNull(elemExtn.getAttribute("ExtnOrigReqQty"));
		strUTFQtyValue = checkForNull(elemExtn.getAttribute("ExtnUTFQty"));
		strFwdQtyValue = checkForNull(elemExtn.getAttribute("ExtnFwdQty"));
		strBOQtyValue = checkForNull(elemExtn.getAttribute("ExtnBackorderedQty"));			
		systemNo = checkForNull(elemExtn.getAttribute("ExtnSystemNo"));

		//strReqQty = '<td class=numericunprotectedinput> <INPUT class=numericprotectedinput  name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnOrigReqQty" style="width:40px"  value="' + strRequQty + '" dataType="STRING"  OldValue='+ strRequQty +' />';
		strReqQty = '<td align="center"> <INPUT style="width:40px" align="center" class=numericprotectedinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnOrigReqQty" value="' + strRequQty + '" dataType="STRING" OldValue="' + strRequQty +'" />';
		//strReqQty = strRequQty;
		
		// BEGIN: Changed by Manish K to allow Trackable items to be UTF/Fwd/Back
		//if (draftOrCreatedFound == 1 && isSerialTracked==0) {		
		if (draftOrCreatedFound == 1) {
		// END: Changed by Manish K to allow Trackable items to be UTF/Fwd/Back 
			strUTFQty  = '<td align="center"> <INPUT class=numericunprotectedinput '+disabledText+' name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnUTFQty" style="width:40px"   value=\"' + strUTFQtyValue +'\" dataType="STRING"  OldValue=' + strUTFQtyValue +' />';
			strBackQty = '<td align="center"> <INPUT class=numericunprotectedinput '+disabledText+' name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnBackorderedQty" style="width:40px" onBlur="setBackOrderFlag(this)"  value=\"'+ strBOQtyValue +'\" dataType="STRING"  OldValue=' + strBOQtyValue +' />';
			strFwdQty  = '<td align="center"> <INPUT onBlur="validateQuantity(this)" class=numericunprotectedinput '+disabledText+' name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnFwdQty" style="width:40px"  onBlur="setForwardOrderFlag(this)"  value=\"' + strFwdQtyValue +'\" dataType="STRING" OldValue=' + strFwdQtyValue +' />';
		} else {
			//strUTFQty = strUTFQtyValue;
			strUTFQty  = '<td align="center"> <INPUT class=numericprotectedinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnBackorderedQty" style="width:40px" value=\"' + strUTFQtyValue +'\" dataType="STRING" OldValue=' + strUTFQtyValue +' />';
			
			//strBackQty = strBOQtyValue;
			strBackQty = '<td align="center"> <INPUT class=numericprotectedinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnFwdQty" style="width:40px" value=\"' + strBOQtyValue +'\" dataType="STRING" OldValue=' + strBOQtyValue +' />';
						
			//strFwdQty = strFwdQtyValue;
			strFwdQty = '<td align="center"> <INPUT onBlur="validateQuantity(this)" class=numericprotectedinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnFwdQty" style="width:40px" value=\"' + strFwdQtyValue +'\" dataType="STRING" OldValue=' + strFwdQtyValue +' />';
		}

		strBackQty = strBackQty + '<INPUT type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnBackOrderFlag"   value='+checkForNull(elemExtn.getAttribute("ExtnBackOrderFlag"))+' dataType="STRING" OldValue='+checkForNull(elemExtn.getAttribute("ExtnBackOrderFlag"))+' />';
		strFwdQty = strFwdQty + '<INPUT type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnForwardOrderFlag"   value='+checkForNull(elemExtn.getAttribute("ExtnForwardOrderFlag"))+' dataType="STRING" OldValue='+checkForNull(elemExtn.getAttribute("ExtnForwardOrderFlag"))+' />';
	}
	
	/* Added by Danilo for handling of order line sub,cons, & retrieval */	
	if (issueQty > 0) 
		strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">'+status+'</A>';			
	else
		strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Cancelled</A>';				
	
	if (condVarTwo != null && condVarTwo != '' && condVarTwo != ' ') {
		if (condVarTwo == 'XLD-Substituted') {
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Cancelled due to substitution</A>';
		}
		else if (condVarTwo == 'XLD-Consolidated' || condVarTwo == 'No-Cancellation') {
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Cancelled due to consolidation</A>';		
		}
		else if (condVarTwo == 'XLD-Retrieval') {
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Cancelled due to ROSS retrieval</A>';				
		}
	}else{
		var nQty = strUTFQtyValue * 1 ;
		if (nQty > 0 && issueQty == 0)
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">UTF</A>';
		
		var nQty = strFwdQtyValue * 1 ;
		if (nQty > 0 && issueQty == 0)
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Forwarded</A>';
	
		var nQty = strBOQtyValue * 1 ;
		if (strBOQtyValue > 0 && issueQty == 0)
			strLineStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">Backordered</A>';
	}
	var elemNoteList = orderLine.getElementsByTagName("Note");
	var strNote = '' ;
	//added for 4390
	var strNot = '';	
	if(elemNoteList != null && elemNoteList.length > 0 )
	{
		var iSequenceNo = 0 ;
		var elemNoteFinal = null ;
		for(index = 0 ;index < elemNoteList.length ; index++)
		{
			var elemNote = elemNoteList(index);
			var iSequenceNoActual = elemNote.getAttribute("SequenceNo");
			if(parseInt(iSequenceNoActual) > parseInt(iSequenceNo) )
			{
				elemNoteFinal = elemNote ;
				iSequenceNo = iSequenceNoActual ;
			}
		}
		
		strNot = checkForNull(elemNote.getAttribute("NoteText"));
		
		if (condVarTwo == 'XLD-Consolidated' || status == null || condVarTwo.indexOf("XLD") != -1) {
			strNote = '<td><INPUT type=text title="'+checkForNull(elemNote.getAttribute("NoteText"))+'" disabled="disabled" alt="'+ checkForNull(elemNote.getAttribute("NoteText")) +'" class=unprotectedinput style="width:100px" value="' + checkForNull(elemNote.getAttribute("NoteText")) + '" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue="'+checkForNull(elemNote.getAttribute("NoteText"))+'">';
		}
		else {
			strNote = '<td><INPUT type=text title="'+checkForNull(elemNote.getAttribute("NoteText"))+'" alt="'+ checkForNull(elemNote.getAttribute("NoteText")) +'" class=unprotectedinput +'+disabledText+' style="width:100px" value="' + checkForNull(elemNote.getAttribute("NoteText")) + '" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue="'+checkForNull(elemNote.getAttribute("NoteText"))+'">';
		}
	}
	else
	{
		strNote = '<td> <INPUT type=text class=unprotectedinput '+disabledText+' style="width:100px" value="" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue="">';		
	}
	
	
	var strItemID = '' ;
	var strProductClass = '' ;
	var strDesc = '' ;
	//added for 4390
	var strItemDesc = '';
	
	if(elemItem!= null)
	{	
		strItemID = elemItem.getAttribute("ItemID");
		strItemID = checkForNull(strItemID);		
		isSerialTracked = elemItem.getAttribute("IsSerialTracked");
		
		strItemIDCell = '<INPUT title="'+titleAttrib+'" class=protectedtext OldValue="'+strItemID+'" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Item/@ItemID" value="'+strItemID+'"/>';

		var itemObj = new Object();
		itemObj.text = strItemIDCell;
		itemObj.sortValue = strItemID;
		itemObj.className = "numerictablecolumn";

		strProductClass = elemItem.getAttribute("ProductClass");
		strProductClass = checkForNull(strProductClass);
		
		strProductClassCell = '<td><INPUT class=protectedtext OldValue="'+strProductClass+'" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Item/@ProductClass" value="'+strProductClass+'"/>';

		strUOMCell = '<td><INPUT class=protectedtext OldValue="'+strUOM+'" style="width:15px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Item/@UnitOfMeasure" value="'+strUOM+'"/>';
		
		strDesc = elemItem.getAttribute("ItemShortDesc");
		strDesc = checkForNull(strDesc);
		if (titleAttrib.length > 2) {
			var strInstrDtls = 'yfcShowDetailPopupWithParams(\"YOMD200\", \"\", \"1010\", \"650\", new Object(), \"ISUorderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22'+ orderHeaderKey+'%22+OrderLineKey%3D%22';
			
			strDesc = '<A onclick=\''+strInstrDtls+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">' + strDesc + '</A>';
			setSpecialInstructionsReqNo(strReqNo);
		}
		
		strItemDesc = elemItem.getAttribute("ItemShortDesc");
		
	}

	var rfiQty = ''; 
	var strQtyRfi = '';
	if(elemExtnList!=null && elemExtnList.length > 0 )
	{
		rfiQty = checkForNull(elemExtn.getAttribute("ExtnQtyRfi")) ;
		var rfiParams = 'xml:/Item/@ItemID=' + strItemID + '&xml:/Item/@ProductClass=' + strProductClass + '&xml:/Item/@TransactionalUOM=' + strUOM;
		var rfiMethodCall = 'yfcShowDetailPopupWithParams(\"ISUYOMD095\", \"\", \"900\", \"500\", \"' + rfiParams + '\");return false;';		
		var strQtyRfiHidden = '<td class="tablecolumn" tabindex="-1"><INPUT type="hidden" style="width:40px" tabindex="-1" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnQtyRfi" value="' + rfiQty + '" dataType="STRING"/></td>';	
		strQtyRfi = '<td class=protectedtext><a onclick=\'' + rfiMethodCall + '\' href="">' + rfiQty + '</a></td>' + strQtyRfiHidden;
	}
	
	var bOtherIssues = true ;
	if(eval(documentType) && documentType == '0001')
		bOtherIssues = false ; 
	var orderLineKey = orderLine.getAttribute("OrderLineKey") ;
	var primeLineNo = orderLine.getAttribute("PrimeLineNo") ;
	var subLineNo = orderLine.getAttribute("SubLineNo") ;
	var strValue = '%3COrderLineDetail+OrderHeaderKey%3D%22'+orderHeaderKey+'%22+OrderLineKey%3D%22';
	
	var arr = new Array();

	var checkBoxCol = '<td style="width:5px" class="checkboxcolumn" >';
	checkBoxCol = checkBoxCol  + '<input type="checkbox" value="'+ strValue + orderLineKey +'%22+strReqNo%3D%22'+ strReqNo +'%22+primeLineNo%3D%22'+ primeLineNo +'%22+strUOM%3D%22'+ strUOM +'%22+strReqQty%3D%22'+ strRequQty + '%22+strIssueQty%3D%22'+ strIssueQty + '%22+strItemID%3D%22'+ strItemID + '%22+strNot%3D%22'+ strNot +'%22%2F%3E"  name="chkEntityKey" />' ;	
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderLineKey_'+ OrderLineCounter+ '" value="'+orderLineKey+'" />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderHeaderKey_'+OrderLineCounter+'" value="'+orderHeaderKey+'" />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter + '/@OrderLineKey" value='+orderLineKey+' />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@PrimeLineNo" value="'+primeLineNo+'" />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@SubLineNo" value="'+subLineNo+'" />';
	arr[0] = checkBoxCol;

	var strDate = '<td class="tablecolumn" nowrap="true"> ';
	var imageTag = '<img class="lookupicon" onclick="invokeCalendar(this)" ' + strImage +' />';
	var disabledImageTag = '<img class="lookupicon" disabled=disabled onclick="invokeCalendar(this)" ' + strImage +' />' ;
	if (driverDate == "02")
	{ 
		if (checkStringForNull(condVarTwo) != '') {
			if ((condVarTwo.indexOf("XLD") != -1) || status == null) {
			strDate = strDate + '<input style="width:70px" type="text" class=unprotectedinput disabled=disabled OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqDeliveryDate"/>' +disabledImageTag;
			}
		}else {
			strDate = strDate + '<input style="width:70px" type="text" class=unprotectedinput OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqDeliveryDate"/>' +imageTag;
		}		
	} 
	else 
	{
		if (checkStringForNull(condVarTwo) != '') {
			if ((condVarTwo.indexOf("XLD") != -1) || status == null) {
			strDate = strDate + '<input style="width:70px" class=unprotectedinput disabled=disabled OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqShipDate"/>' +disabledImageTag;
			}
		}else {
			strDate = strDate + '<input style="width:70px" class=unprotectedinput ' + disabledText + ' OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqShipDate"/>' +imageTag;
		}		
	}	

	var requestNoCol = '';
	var requestNoObj = new Object();

    // Added by GN for CR-655

	if (checkForNull(strReqNo) == '' || checkForNull(strReqNo) == 'S-')
	{
		if(!bOtherIssues)
	    {
          strReqNo = "S-";
		  requestNoCol = '<INPUT style="width:80px" class=unprotectedinput OldValue=\"'+strReqNo+'\"  name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnRequestNo" onBlur="validateRequestNumber(this)"   value=\"'+strReqNo+'\" dataType="STRING"/>';
		  requestNoObj.text = requestNoCol;
		  requestNoObj.sortValue = strReqNo;
		  requestNoObj.className = "tablecolumn";	
	    }
		else
		{
          requestNoCol = '<INPUT style="width:80px" class=unprotectedinput OldValue=\"'+strReqNo+'\" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnRequestNo" value=\"'+strReqNo+'\" dataType="STRING"/>';  
		  requestNoObj.text = requestNoCol;
		  requestNoObj.sortValue = strReqNo;
		  requestNoObj.className = "tablecolumn";	
		}
    }
	else
	{
	   //requestNoCol = strReqNo;
	   requestNoCol = '<INPUT style="width:70px" title="'+titleAttrib+'" class=protectedtext OldValue="'+strReqNo+'" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnRequestNo" value="'+strReqNo+'" dataType="STRING"/>';  
	   requestNoObj.text = requestNoCol;
	   requestNoObj.sortValue = strReqNo;
	   requestNoObj.className = "tablecolumn";	
	}

	//debugger;
	arr.push(primeLinkObj);
	//arr.push(strLineKey);
	arr.push(requestNoObj);	
	arr.push(itemObj);
	//ROLLBACK of CR 338 BEGIN	
	arr.push(strProductClassCell);
	//ROLLBACK of CR 338 END	
	arr.push(strUOMCell);
	arr.push(strDesc);
	if (drawSys == 1) {
		arr.push(systemNo);
	}
	arr.push(strReqQty) ;
	arr.push(strQtyRfi);
	arr.push(strQty);
	//CR 510 added actual pricing qty here
	arr.push(strActPricQty);	
	arr.push(strUTFQty);
	arr.push(strBackQty);
	arr.push(strFwdQty);
	arr.push(strDate);
	arr.push(strNote);
	arr.push(totalObj) ;
	arr.push(strLineStatus);
	return arr ;
}

function populateBlankRow(documentType,driverDate,strPCData,strUOMData,issueType,counter)
{
	counter = counter + 1 ;
	var arr = new Array();
	var strChkBox = '<td style="width:5px" class="checkboxcolumn" />';
	var strBlank = '<td style="width:5px"/>' ;
	var bOtherIssues = true ;
	if(eval(documentType) && documentType == '0001')
		bOtherIssues = false ;
	var requestNoCol= '' ;
	var ExtnRequestNo = document.getElementById('xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo');
	var requestNoObj = new Object();

	var sysNo = '<td><INPUT class=unprotectedinput OldValue=""  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnSystemNo" value="" maxLength=10 size=11 dataType="STRING"/>' ;
	
	if (ExtnRequestNo != null)
    {
        ExtnRequestNo.focus();
    }
	
	if(!bOtherIssues)
	{
		requestNoCol = '<INPUT class=unprotectedinput OldValue="S-"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo"  onblur="validateRequestNumber(this)"   value="S-" maxLength=20 size=21 dataType="STRING"/>' ;
		requestNoObj.text = requestNoCol;
		requestNoObj.className = "tablecolumn";	
	}
	else
	{
		requestNoCol = '<INPUT class=unprotectedinput  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo" maxLength=20 size=21 dataType="STRING"/>' ;
		requestNoObj.text = requestNoCol;
		requestNoObj.className = "tablecolumn";	
	}

	

//Begin CR833
	var idItemID = "itemid"+counter;
	//var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedinput type="text" style="width:40px" id="itemid" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" ';
	var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedinput type="text" style="width:40px" id="'+idItemID+'" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" ';
//End CR833

        strItemID = strItemID + 'onClick="checkRequestNo(this,\''+issueType+'\')"  onFocus="checkRequestNo(this,\''+issueType+'\')" onBlur="fetchDataWithParams(this,\'PopulateItemDetails\',populateItemDetails,setParam(this));setRequestedDate(this)"/> <img class="lookupicon" ' ;

//Begin CR833
	//strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\');returnFocusToItemId();" src=\'/yantra/console/icons/lookup.gif\'/>' ;
	strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\');returnFocusToItemId(\''+counter+'\');" src=\'/yantra/console/icons/lookup.gif\'/>' ;
//End CR833


	var strProductClass = '<td class="tablecolumn"><INPUT class=unprotectedinput readonly="true" tabindex="-1" style="width:40px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ProductClass" dataType="STRING">'; 

	var strUOM = '<td class="tablecolumn"><INPUT class=unprotectedinput readonly="true" tabindex="-1" style="width:40px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@TransactionalUOM" dataType="STRING">'; 

	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:140px" value=""/></td>' ;
	
	var strReqQty = '<td class="tablecolumn"> <INPUT class=unprotectedinput onBlur="checkMaxQty(this)" maxLength=10 size=5 style="width:40px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnOrigReqQty" dataType="STRING">' ;

	//var strQtyRfi = '<td class="tablecolumn" style="width:120px"><INPUT class=protectedinput readonly="true" tabindex="1" <A onclick="checkAvailableRFI(this)" title="Check Available RFI" href=""><label style="width:45px" //value="" id="qtyRfi"/></A>' ;

	var strQtyRfiHidden = '<td class="tablecolumn" tabindex="-1"><INPUT type="hidden" class=protectedinput style="width:40px" tabindex="-1" title="Currently Available RFI Quantity\rClick for details." name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnQtyRfi" dataType="STRING"/></td>';	
	var strQtyRfi = '<td class=protectedtext><a id="availRFIQty" tabindex="-1" href=""></a></td>' + strQtyRfiHidden;
	
	var strQty = '<td class="numerictablecolumn" > <input onBlur="checkIssuedQuantity(this);checkMaxQty(this)" style="width:40px" type="text" class=unprotectedinput	name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OrderedQty"/>';
	
	var strUTFQty = '<td class="tablecolumn"> <INPUT class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnUTFQty\' dataType="STRING">';

	var strBackQty = '<td class="tablecolumn"><INPUT onBlur="setBackOrderFlag(this);checkMaxQty(this)"  class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnBackorderedQty\' dataType="STRING" >';
	
	strBackQty = strBackQty + '<INPUT class=unprotectedinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnBackOrderFlag"   value="" dataType="STRING" OldValue="" />';

	var strFwdQty = '<td class="tablecolumn"><input type="text" onBlur="validateQuantity(this);checkMaxQty(this)" class=\'unprotectedoverrideinput\' style=\'width:40px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnFwdQty"/>'; 

	strFwdQty = strFwdQty + '<INPUT class=unprotectedinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnForwardOrderFlag"   value="" dataType="STRING" OldValue="" />';
	
	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	if (driverDate == "02") 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif" />' ;
	} 
	else 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif"/> ' ;
	} 
	strDate = strDate + '</td> ' ;
	var strNote = '<td> <INPUT class=unprotectedinput  style=\'width:100px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Notes/Note/@NoteText" dataType="STRING" OldValue=""/>';
	var strActPricingQty = '<td class="tablecolumn" style="width:30px"><label style="width:30px" value=""/>';

	arr.push(strChkBox);
	arr.push(strBlank);
	//arr.push(strBlank);
	arr.push(requestNoObj) ;	
	arr.push(strItemID );
	//ROLLBACK of CR 338 BEGIN
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END	
	arr.push(strUOM );
	arr.push(strDesc );
	//arr.push(sysNo);
	arr.push(strReqQty) ;
	arr.push(strQtyRfi);
	//arr.push(strBlank);	
	arr.push(strQty );
	//add pricing qty read only here
	arr.push(strActPricingQty );
	//add pricing qty read only here ends	
	arr.push(strUTFQty );
	arr.push(strBackQty );
	arr.push(strFwdQty );
	arr.push(strDate );
	arr.push(strNote );
	return arr ;
}