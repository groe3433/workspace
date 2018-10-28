/*
main function to prepare the row to be displayed in the table
a bit messy code, but make sense 
*/
function prepareTableRow(orderLine,OrderLineCounter,documentType,driverDate,orderHeaderKey,strPrefix,strPostfix,strImage)
{
	var strLineKey = orderLine.getAttribute("OrderLineKey");
	//var documentType = '<%=resolveValue("xml:/Order/@DocumentType")%>';
	//var driverDate = '<%=resolveValue("xml:/Order/@DriverDate")%>';
	var elemQtyList = orderLine.getElementsByTagName("OrderLineTranQuantity");
	var strPrimeLineNo = orderLine.getAttribute("PrimeLineNo");
	var strUOM = '';
	var strOrderedQty = '' ;
	var strOriginalOrderedQty = '' ;
	var strRcvdQty = '' ;
	var strPartialRcvd = '';
	var strOrderLineStatus = '';
	if(elemQtyList != null && elemQtyList.length > 0 )
	{
		var elemQty = elemQtyList(0);
		strUOM = elemQty.getAttribute("TransactionalUOM");
		strOrderedQty = elemQty.getAttribute("OrderedQty");
                //alert("strOrderedQty = "+strOrderedQty);

		strOriginalOrderedQty = elemQty.getAttribute("OriginalOrderedQty");
		strRcvdQty = elemQty.getAttribute("ReceivedQty");
		// strOrderLineStatus = orderLine.getAttribute("MaxLineStatusDesc");
		strOrderLineStatus = orderLine.getAttribute("Status");
		//alert("strOrderLineStatus=["+strOrderLineStatus+"] strOriginalOrderedQty=["+strOriginalOrderedQty+"] strRcvdQty=["+strRcvdQty+"] strOrderedQty=["+strOrderedQty+"]");
		// strOriginalOrderedQty is ORIGINAL_ORDERED_QTY and strRcvdQty is RECEIVED_QUANTITY
		if( (parseInt(strRcvdQty)>0)&& (parseInt(strOriginalOrderedQty) > parseInt(strRcvdQty)) ) //partially received
		{
			var strPartialRcvd = 'Partially Received';
		}
	}
	// check the modification rules for the add quantity
	// get the Modification Rules
	var lstModification = orderLine.getElementsByTagName("Modification");
	// if exist
	if(lstModification  != null && lstModification .length > 0)
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
				if(strThroughOverride == "N")
				{
					strOriginalOrderedQty = '<td class="numerictablecolumn" > <input style="width:40px" type="text" class=unprotectedoverrideinput value="'+strOriginalOrderedQty+'" OldValue="'+strOriginalOrderedQty+'"	name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OriginalOrderedQty"/> </td> ';
					//strRcvdQty = '<td class="numerictablecolumn" nowrap="true" sortValue="'+strRcvdQty+'"> </td> ';
				}
			}
		}// end for
	}// end if 
	
	var strLink = "<A onclick='yfcShowDetailPopup(\"YOMD3170\", \"\", \"1010\", \"650\", new Object(), \"poline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22"+orderHeaderKey+"%22+OrderLineKey%3D%22"+strLineKey+"%22%2F%3E\");return false;' href=\"\">"+strPrimeLineNo+"</A>" ;

	var strMethodCall = 'yfcShowDetailPopupWithParams(\"YOMD255\", \"\", \"1010\", \"650\", \"ShowReleaseNo=Y\", \"orderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22'+ orderHeaderKey+'%22+OrderLineKey%3D%22';
	
	// CR 167 - Partially Received
	/* if (strPartialRcvd != '')
	{
		var strMaxLineStatusDesc = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">'+strPartialRcvd+'</A> </TD> ' ;
	} else {
		var strMaxLineStatusDesc = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">'+orderLine.getAttribute("MaxLineStatusDesc")+'</A> </TD> ' ;
	}
	*/
	// CR 507 - display line status
	var strStatus = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">'+orderLine.getAttribute("Status")+'</A> </TD> ' ;
	// - End of - CR 167 - Partially Received

	var elemTotalList = orderLine.getElementsByTagName("LineOverallTotals");
	var strReqNo = '' ;
	var strTotal = '' ;
	//var strPrefix = '<%=resolveValue("xml:/CurrencyList/Currency/@PrefixSymbol")%>';
	//var strPostfix = '<%=resolveValue("xml:/CurrencyList/Currency/@PostfixSymbol")%>';
	
	if(elemTotalList != null && elemTotalList.length > 0 )
	{
		var elemTotal = elemTotalList(0);
		strTotal = '<td class="numerictablecolumn" nowrap="true"  sortValue="'+ elemTotal.getAttribute("LineTotal")+'">'
		+ strPrefix+'&nbsp;' + elemTotal.getAttribute("LineTotal")+ '&nbsp;' +strPostfix+ ' </td> ';
	}

	var elemExtnList = orderLine.getElementsByTagName("Extn");
	var strReqNo = '' ;
	var supStdPack = '' ;
	var stdPack = '' ;
	var supplierUOM = '' ;
	var strGSASerialNum = '';
	var strGSAQty = '';
	//var saveCheckBoxCol = '';
	var saveComboCol = '';
	
	if(elemExtnList!=null && elemExtnList.length > 0 )
	{
		elemExtn = elemExtnList(0);
		strReqNo = elemExtn.getAttribute("ExtnNSN");
		strReqNo  = checkForNull(strReqNo );
		//strGSASerialNum = elemExtn.getAttribute("ExtnGSANo");
		//strGSASerialNum  = checkForNull(strGSASerialNum );
		strReqQty = '<td> <INPUT class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnOrigReqQty" style="width:40px"  value="'+checkForNull(elemExtn.getAttribute("ExtnOrigReqQty"))+'" dataType="STRING" name="" OldValue='+checkForNull(elemExtn.getAttribute("ExtnOrigReqQty"))+' /> </td>';

		supStdPack = '<td> <INPUT readonly="true" name=\"xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnSupplierStdPack\" class=unprotectedoverrideinput  size=20 maxLength=100 value=\"'+checkForNull(elemExtn.getAttribute("ExtnSupplierStdPack"))+'\" dataType="STRING" OldValue=\"'+checkForNull(elemExtn.getAttribute("ExtnSupplierStdPack"))+'\" /> </td>';
		
		strGSASerialNum = '<td> <INPUT readonly="true" name=\"xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnGSANo\" class=unprotectedoverrideinput  size=6 maxLength=6 value=\"'+checkForNull(elemExtn.getAttribute("ExtnGSANo"))+'\" dataType="STRING" OldValue=\"'+checkForNull(elemExtn.getAttribute("ExtnGSANo"))+'\" /> </td>';
		//cr 440 ks - supplier uom
		supplierUOM = '<td> <INPUT readonly="true" name=\"xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnSupplierUOM\" class=unprotectedoverrideinput  size=5 maxLength=10 value=\"'+checkForNull(elemExtn.getAttribute("ExtnSupplierUOM"))+'\" dataType="STRING" OldValue=\"'+checkForNull(elemExtn.getAttribute("ExtnSupplierUOM"))+'\" /> </td>';

		stdPack ='<td> <INPUT name=\"xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnStdPack\" class=unprotectedoverrideinput  size=20 maxLength=20 value=\"'+checkForNull(elemExtn.getAttribute("ExtnStdPack"))+'\" dataType="STRING" OldValue=\"'+checkForNull(elemExtn.getAttribute("ExtnStdPack"))+'\" /> </td>';
		
		strGSAQty = '<td> <INPUT class=unprotectedoverrideinput  name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnGSAQty" style="width:40px"  value="'+checkForNull(elemExtn.getAttribute("ExtnGSAQty"))+'" dataType="STRING" name="" OldValue='+checkForNull(elemExtn.getAttribute("ExtnGSAQty"))+' /> </td>';
	
		//saveCheckBoxCol = '<td style="width:5px" class="chekcboxcolumn">';
		//saveCheckBoxCol = saveCheckBoxCol  + '<INPUT type="checkbox" id="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnToReceive" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnToReceive" value="'+elemExtn.getAttribute("ExtnToReceive")+'" dataType="STRING" OldValue="'+checkForNull(elemExtn.getAttribute("ExtnToReceive"))+'" onclick="setChkBox(this);" /> </td>';
		saveComboCol =	'<td style="width:20px" class="chekcboxcolumn">';
		saveComboCol = saveComboCol + ' <select name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnToReceive" id="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnToReceive" value="'+elemExtn.getAttribute("ExtnToReceive")+'" dataType="STRING" OldValue="'+checkForNull(elemExtn.getAttribute("ExtnToReceive"))+'"onchange="setChkBox(this);" />';
		saveComboCol = saveComboCol + '<option name="yes" value = "Y">Y</option><option name="no" value = "N">N</option></select></td>';
	}

	var elemNoteList = orderLine.getElementsByTagName("Note");
	var strNote = '' ;
	if(elemNoteList != null && elemNoteList.length > 0 )
	{
		var elemNote = elemNoteList(elemNoteList.length-1);
		strNote = '<td> <INPUT type=text class=unprotectedoverrideinput style="width:100px" value=\"'+checkForNull(elemNote.getAttribute("NoteText"))+'\" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue=\"'+checkForNull(elemNote.getAttribute("NoteText"))+'\"> </td>';
	}
	else
	{
		strNote = '<td> <INPUT type=text class=unprotectedoverrideinput style="width:100px" value="" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue=""> </td>';
	}

	var elemItemList = orderLine.getElementsByTagName("Item");
	var strItemID = '' ;
	var strProductClass = '' ;
	var strDesc = '' ;

	if(elemItemList!= null && elemItemList.length > 0 )
	{
		elemItem = elemItemList(0);
		
		strItemID = elemItem.getAttribute("ItemID");
		strItemID = checkForNull(strItemID);		

		strProductClass = elemItem.getAttribute("ProductClass");
		strProductClass = checkForNull(strProductClass);

		strDesc = elemItem.getAttribute("ItemShortDesc");
		strDesc = checkForNull(strDesc);

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

	var checkBoxCol = '<td style="width:5px" class="chekcboxcolumn" >';
	checkBoxCol = checkBoxCol  + '<input type="checkbox" value="'+ strValue + orderLineKey +'%22%2F%3E"  name="chkEntityKey" />' ;
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderLineKey_'+ OrderLineCounter+ '" value="'+orderLineKey+'" /> </td>' ;
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderHeaderKey_'+OrderLineCounter+'" value="'+orderHeaderKey+'" /> ' ;
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter + '/@OrderLineKey" value='+orderLineKey+' />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@PrimeLineNo" value="'+primeLineNo+'" />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@SubLineNo" value="'+subLineNo+'" /></td>';
	arr[0] = checkBoxCol ;
		
	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	var imageTag = "<img class='lookupicon' onclick='invokeCalendar(this)'" + strImage +" />" ;
	if (driverDate == "02")
	{ 
		strDate = strDate + '<input style="width:70px" type="text" class=unprotectedoverrideinput        OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqDeliveryDate"/>' +imageTag;
	} 
	else 
	{
		strDate = strDate + '<input type="text" style="width:70px" OldValue="'+checkForNull(orderLine.getAttribute("ReqShipDate")) +'" class=unprotectedoverrideinput   name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqShipDate" value="'+checkForNull(orderLine.getAttribute("ReqShipDate")) +'" /> 	' + imageTag ;
	}

	strDate = strDate +'</td>' ;

	//var requestNoCol = '<td> <INPUT name=\"xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnNSN\" class=unprotectedoverrideinput  size=5 maxLength=10 value=\"'+strReqNo+'\" dataType="STRING" OldValue=\"'+strReqNo+'\" /> </td>';
	
	arr.push(saveComboCol);
	arr.push(strLink);
	arr.push(strItemID) ;
	arr.push(strReqNo) ;
	arr.push(supplierUOM) ;	
	arr.push(supStdPack) ;
	arr.push(strGSAQty );
	arr.push(strDesc );
	arr.push(strGSASerialNum );
	//ROLLBACK of CR 338 BEGIN	
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END	
	arr.push(strUOM );
	arr.push(strQtyRfi);
	// supplier std pack
	//arr.push(strReqQty) ; // commented as per issue 234

//Begin CR849 BR1.2
	//arr.push(strOriginalOrderedQty);
	arr.push(strOrderedQty);
//End CR849 BR1.2

	arr.push(strRcvdQty);  // CR 507 - add received quantity
	arr.push(stdPack); // std pack
	arr.push(strDate );
	arr.push(strTotal) ;
	//arr.push(strMaxLineStatusDesc);
	arr.push(strStatus); // CR 507 - display line status
	arr.push(strNote);
	return arr ;
}

function populateBlankRow(documentType,driverDate,strPCData,strUOMData,counter)
{
	counter = counter + 1 ;
	var arr = new Array();
	var strChkBox = '<td style="width:5px" class="checkboxcolumn" />';
	var strExtnToRecv = '<td style="width:20px" class="checkboxcolumn" />';
	var strBlank = '<td style="width:5px"/>' ;
	var bOtherIssues = true ;
	
	if(eval(documentType) && documentType == '0001')
		bOtherIssues = false ;

	var requestNoCol = '<td><INPUT class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnNSN" maxLength=20 size=10 dataType="STRING"/></td> ' ;

	// function populateSupplierItemUnitPrice is in reportRecordReceipt.js and setItemParam is in orderDetails.js

	var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedoverrideinput type="text" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" ';
	strItemID = strItemID + 'onBlur="fetchDataWithParams(this,\'PopulateItemPODetails\',populateSupplierItemUnitPrice,setItemParam(this,\'xml:/Order/@ReceivingNode\',\'\'));setRequestedDate(this)"/> <img class="lookupicon" ' ;
	strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\')"   src=\'/smcfs/console/icons/lookup.gif\'/></td>' ;

	var strProductClass = '<td class="tablecolumn"> <select tabindex="-1" class=comboboxoverride  OldValue="" style="width:50px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ProductClass">'+strPCData+'</select></td> ' ;
	var strUOM = '<td class="tablecolumn"> <select OldValue="" tabindex="-1"   style="width:50px"  class=comboboxoverride  name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@TransactionalUOM">'+strUOMData+'</select> </td> ';
	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:140px" value=""/></td>' ;
	var strReqQty = '<td class="tablecolumn"> <INPUT class=unprotectedoverrideinput maxLength=10 size=5 style="width:40px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnOrigReqQty" dataType="STRING"> </td> ' ;

	var strQtyRfiHidden = '<td class="tablecolumn"><INPUT type="hidden" class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:40px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnQtyRfi" dataType="STRING"> </td> '; 
	var strQtyRfi = '<td class=protectedtext><a id="availRFIQty" tabindex="-1" href=""></a></td>' + strQtyRfiHidden;
	
	var strOriginalOrderedQty = '<td class="numeri	ctablecolumn" > <input style="width:40px" type="text" class=unprotectedoverrideinput	name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OriginalOrderedQty"/> </td> ';
	
	//var strRcvdQty = '<td class="numeri	ctablecolumn" > <input readonly="true" style="width:40px" type="text" class=unprotectedoverrideinput	name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@ReceivedQty"/> </td> ';

	var strUTFQty = '<td class="tablecolumn"> <INPUT class=unprotectedoverrideinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnUTFQty\' dataType="STRING"> </td> ';

	var strBackQty = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnBackorderedQty\' dataType="STRING" ></td>';
	
	var strFwdQty = '<td class="tablecolumn"><input type="text"  class=\'unprotectedoverrideinput\' style=\'width:40px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnFwdQty"/></td> '; 

	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	if (driverDate == "02") 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/smcfs/console/icons/calendar.gif" />' ;
	} 
	else 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/smcfs/console/icons/calendar.gif"/> ' ;
	} 
	strDate = strDate + '</td> ' ;
	var strNote = '<td> <INPUT class=unprotectedoverrideinput  style=\'width:100px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Notes/Note/@NoteText" dataType="STRING" OldValue=""/> </td>' ;

	var supStdPack = '<td> <INPUT tabindex="-1" readonly="true" name=\"xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnSupplierStdPack\" class=unprotectedoverrideinput size=20 maxLength=100 dataType="STRING" /> </td>';

	<!-- CR 440 ks -->
	var supplierUOM = '<td><INPUT class=unprotectedoverrideinput tabindex="-1" readonly="true" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnSupplierUOM" maxLength=40 size=15 dataType="STRING"/></td> ' ;
	<!-- CR 440 ks -->

	var stdPack = '<td> <INPUT name=\"xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnStdPack\" class=unprotectedoverrideinput size=20 maxLength=20 dataType="STRING" OldValue="" /> </td>';
	var strGSASerialNum = '<td> <INPUT name=\"xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnGSANo\" class=unprotectedoverrideinput size=6 maxLength=6 dataType="STRING" OldValue="" /> </td>';
	var	strGSAQty = '<td> <INPUT class=unprotectedoverrideinput  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnGSAQty" style="width:40px"  value="" dataType="STRING" name=""  /> </td>';

	arr.push("");
	arr.push(strChkBox);  // Line Added for CR 557 - Addl checkbox for Receive Line
	arr.push(strExtnToRecv);	
	arr.push(strItemID) ;
	arr.push(requestNoCol) ;
	arr.push(supplierUOM) ;	
	arr.push(supStdPack) ;
	arr.push(strGSAQty );
	arr.push(strDesc );
	arr.push(strGSASerialNum );
	//ROLLBACK of CR 338 BEGIN	
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END	
	arr.push(strUOM );
	arr.push(strQtyRfi );
	// supplier std pack
	//	arr.push(strReqQty) ; // as per issue 234
	arr.push(strOriginalOrderedQty);
	arr.push("");  // CR 507 - add received quantity
	arr.push(stdPack); // std pack
	arr.push(strDate );
	arr.push("") ;
	arr.push("" );
	arr.push(strNote);
	return arr ;
}
function setChkBox(comBox)
{
	//alert("comBox.value = "+ comBox.value);
	var selIdx = comBox.selectedIndex;
	
	comBox.value = comBox.options[selIdx].text;
		
	//alert("Now comBox.value = " + comBox.value);
	return;	
}