/*
main function to prepare the row to be displayed in the table
a bit messy code, but make sense 
*/
function prepareTableRow(orderLine,OrderLineCounter,documentType,driverDate,orderHeaderKey,strPrefix,strPostfix,strImage)
{

	//alert("XML in prepTableRow = " + orderLine.xml);

	var strLineKey = orderLine.getAttribute("OrderLineKey");
	var strPrimeLineNo = orderLine.getAttribute("PrimeLineNo");
	var elemQtyList = orderLine.getElementsByTagName("OrderLineTranQuantity");
	var strUOM = '';
	var strQty = '' ;
	var strActPricQty = '';
	//var	 = '';


	//CR 510 logic 

	var lnPriceTotal = orderLine.getElementsByTagName("LineOverallTotals");

	if(lnPriceTotal  != null && lnPriceTotal .length > 0) {


	var elemLineOverallTotals = lnPriceTotal.item(0);
		strActPricQty = checkForNull(elemLineOverallTotals.getAttribute("PricingQty")) ;


	}
	
	
	var bModifyLineQty = true ; 
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


	if(elemQtyList != null && elemQtyList.length > 0 )
	{
		
		var elemQty = elemQtyList(0);
		strUOM = elemQty.getAttribute("TransactionalUOM");
		strQty = elemQty.getAttribute("OrderedQty");
		strIssueQty = elemQty.getAttribute("OrderedQty");
		var nQty = strQty * 1 ;
		// if the user can modify the line quantity
		if(nQty <= 0 || bModifyLineQty)
		{
			strQty = '<td class="numerictablecolumn" > <input onBlur="checkIssuedQuantity(this)" style="width:40px" type="text" class=unprotectedoverrideinput value="'+nQty+'" OldValue="'+nQty+'"	name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/OrderLineTranQuantity/@OrderedQty" tabindex="2"/> </td> '
		}
	}

	var strPrimeLink = "<A onclick='yfcShowDetailPopup(\"ISUYOMD170\", \"\", \"1010\", \"650\", new Object(), \"ISUorderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22"+orderHeaderKey+"%22+OrderLineKey%3D%22"+strLineKey+"%22%2F%3E\");return false;' href=\"\">"+strPrimeLineNo+"</A> ";
	var primeLinkObj = new Object();
	primeLinkObj.text = strPrimeLink;
	primeLinkObj.sortValue = strPrimeLineNo;
	primeLinkObj.className = "tablecolumn";

	var strMethodCall = 'yfcShowDetailPopupWithParams(\"YOMD255\", \"\", \"1010\", \"650\", \"ShowReleaseNo=Y\", \"orderline\", \"%3COrderLineDetail+OrderHeaderKey%3D%22'+ orderHeaderKey+'%22+OrderLineKey%3D%22';
	
	var status = orderLine.getAttribute("MaxLineStatusDesc");

/* CR 285 ks 2008-10-01 */
	if (status == null)
	{
	var strMaxLineStatusDesc = '<TD class=protectedtext>Cancelled </TD> ' ;
	} else {
	var strMaxLineStatusDesc = '<TD class=protectedtext> <A onclick=\''+strMethodCall+orderLine.getAttribute("OrderLineKey")+'%22%2F%3E");return false;'+'\' href="">'+orderLine.getAttribute("MaxLineStatusDesc")+'</A> </TD> ' ;
	}
/* end CR 285 */
	
	var elemTotalList = orderLine.getElementsByTagName("LineOverallTotals");
	var strReqNo = '' ;
	var strTotal = '' ;
	
	if(elemTotalList != null && elemTotalList.length > 0 )
	{
		var elemTotal = elemTotalList(0);
		strTotal = '<td class="numerictablecolumn" nowrap="true"  sortValue="'+ elemTotal.getAttribute("LineTotal")+'">'
		+ strPrefix+'&nbsp;' + elemTotal.getAttribute("LineTotal")+ '&nbsp;' +strPostfix+ ' </td> ';
	}

	var elemExtnList = orderLine.getElementsByTagName("Extn");
	var strReqNo = '' ;
	var strUTFQtyValue = '';
	var strFwdQtyValue = '';
	var strBOQtyValue = '';	
	
	//added for 4390
	var strRequQty='';
	if(elemExtnList!=null && elemExtnList.length > 0 )
	{
		elemExtn = elemExtnList(0);
		strReqNo = elemExtn.getAttribute("ExtnRequestNo");
		strReqNo  = checkForNull(strReqNo );
		strRequQty = elemExtn.getAttribute("ExtnOrigReqQty")
		strRequQty  = checkForNull(strRequQty);

		strUTFQtyValue = checkForNull(elemExtn.getAttribute("ExtnUTFQty"));
		strBOQtyValue = checkForNull(elemExtn.getAttribute("ExtnBackorderedQty"));			
	
		strReqQty = '<td> <INPUT class=unprotectedoverrideinput  name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnOrigReqQty" style="width:40px"  value="'+checkForNull(elemExtn.getAttribute("ExtnOrigReqQty"))+'" dataType="STRING"  OldValue='+checkForNull(elemExtn.getAttribute("ExtnOrigReqQty"))+' tabindex="1" /> </td>';


		//strUTFQty = strUTFQtyValue;
		strUTFQty  = '<td align="center"> <INPUT class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnUTFQty" style="width:40px" value=\"' + strUTFQtyValue +'\" dataType="STRING" OldValue=' + strUTFQtyValue +' tabindex="3" />';

		//strBackQty = strBOQtyValue;
		strBackQty = '<td align="center"> <INPUT onBlur="validateC2CQuantity(this)" class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnFwdQty" style="width:40px" value=\"' + strBOQtyValue +'\" dataType="STRING" OldValue=' + strBOQtyValue +' tabindex="4" />';
	}
	
	strBackQty = strBackQty + '<INPUT type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Extn/@ExtnBackOrderFlag"   value='+checkForNull(elemExtn.getAttribute("ExtnBackOrderFlag"))+' dataType="STRING" OldValue='+checkForNull(elemExtn.getAttribute("ExtnBackOrderFlag"))+' />';

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

		strNote = "<td> <INPUT type=text class=unprotectedoverrideinput style='width:100px' value='"+checkForNull(elemNote.getAttribute("NoteText"))+"'  name='xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/Notes/Note/@NoteText' dataType='STRING' OldValue='"+checkForNull(elemNote.getAttribute("NoteText"))+"' tabindex=6> </td>";
	}
	else
	{
		strNote = '<td> <INPUT type=text class=unprotectedoverrideinput style="width:100px" value="" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/Notes/Note/@NoteText" dataType="STRING" OldValue="" tabindex=6> </td>';
	}

	var elemItemList = orderLine.getElementsByTagName("Item");
	var strItemID = '' ;
	var strProductClass = '' ;
	var strDesc = '' ;
	//added for 4390
	var strItemDesc = '';
	var itemObj = new Object();
	itemObj.className = "tablecolumn";	

	if(elemItemList!= null && elemItemList.length > 0 )
	{
		elemItem = elemItemList(0);
		
		strItemID = elemItem.getAttribute("ItemID") + "     ";
		strItemID = checkForNull(strItemID);		
		itemObj.text = strItemID;
		itemObj.sortValue = strItemID;

		strProductClass = elemItem.getAttribute("ProductClass");
		strProductClass = checkForNull(strProductClass);
		
		strDesc = elemItem.getAttribute("ItemShortDesc");
		strDesc = checkForNull(strDesc);

		strItemDesc = elemItem.getAttribute("ItemShortDesc");

	}

	var rfiQty = '';
	var strQtyRfi = '';
	if(elemExtnList!=null && elemExtnList.length > 0 )
	{
		elemExtn = elemExtnList(0);
		rfiQty = checkForNull(elemExtn.getAttribute("ExtnQtyRfi")) ;
		var rfiParams = 'xml:/Item/@ItemID=' + strItemID + '&xml:/Item/@ProductClass=' + strProductClass + '&xml:/Item/@TransactionalUOM=' + strUOM;
		var rfiMethodCall = 'yfcShowDetailPopupWithParams(\"ISUYOMD095\", \"\", \"900\", \"500\", \"' + rfiParams + '\");return false;';		
		var strQtyRfiHidden = '<td class="tablecolumn" tabindex="-1"><INPUT type="hidden" style="width:40px" tabindex="-1" name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnQtyRfi" value="' + rfiQty + '" dataType="STRING"/></td>';	
		strQtyRfi = '<td class=protectedtext><a onclick=\'' + rfiMethodCall + '\' href="">' + rfiQty + '</a></td>' + strQtyRfiHidden;
	}

	var orderLineKey = orderLine.getAttribute("OrderLineKey") ;
	var primeLineNo = orderLine.getAttribute("PrimeLineNo") ;
	var subLineNo = orderLine.getAttribute("SubLineNo") ;
	var strValue = '%3COrderLineDetail+OrderHeaderKey%3D%22'+orderHeaderKey+'%22+OrderLineKey%3D%22';


	
	var arr = new Array();


	var checkBoxCol = '<td style="width:5px" class="checkboxcolumn" >';
	checkBoxCol = checkBoxCol  + '<input type="checkbox" value="'+ strValue + orderLineKey +'%22+strReqNo%3D%22'+ strReqNo +'%22+primeLineNo%3D%22'+ primeLineNo +'%22+strUOM%3D%22'+ strUOM +'%22+strReqQty%3D%22'+ strRequQty + '%22+strIssueQty%3D%22'+ strIssueQty + '%22+strItemID%3D%22'+ strItemID + '%22+strNot%3D%22'+ strNot +'%22%2F%3E"  name="chkEntityKey" />' ;
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderLineKey_'+ OrderLineCounter+ '" value="'+orderLineKey+'" /> </td>' ;

	

	checkBoxCol = checkBoxCol  + '<input type="hidden" name="OrderHeaderKey_'+OrderLineCounter+'" value="'+orderHeaderKey+'" /> ' ;
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter + '/@OrderLineKey" value='+orderLineKey+' />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@PrimeLineNo" value="'+primeLineNo+'" />';
	checkBoxCol = checkBoxCol  + '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_' + OrderLineCounter + '/@SubLineNo" value="'+subLineNo+'" /></td>';
	
	//alert("CheckBoxCol = "+ checkBoxCol);

	arr[0] = checkBoxCol ;

	


	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	var imageTag = "<img class='lookupicon' onclick='invokeCalendar(this)'" + strImage +" />" ;
	if (driverDate == "02")
	{ 
		strDate = strDate + '<input style="width:70px" nowrap="true" type="text" class=unprotectedoverrideinput OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqDeliveryDate"))) +'"  name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqDeliveryDate" tabindex="5" />' +imageTag;
	} 
	else 
	{
		strDate = strDate + '<input type="text" style="width:70px" nowrap="true" OldValue="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'" class=unprotectedoverrideinput   name="xml:/Order/OrderLines/OrderLine_'+ OrderLineCounter +'/@ReqShipDate" value="'+formatDate(checkForNull(orderLine.getAttribute("ReqShipDate"))) +'" tabindex="5" /> 	' + imageTag ;
	}
	
	strDate = strDate +'</td>' ;

	var requestNoCol = '' ;
	
    // Added by GN for CR-655

	if (checkForNull(strReqNo) == '' || checkForNull(strReqNo) == 'S-')
	{
		
          strReqNo = "S-";
		  requestNoCol = '<td><INPUT class=unprotectedoverrideinput OldValue=\"'+strReqNo+'\"  name="xml:/Order/OrderLines/OrderLine_'+OrderLineCounter+'/Extn/@ExtnRequestNo" onBlur="validateRequestNumber(this)"   value=\"'+strReqNo+'\" maxLength=10 size=5 dataType="STRING"/></td>';
	}
	else
	{
	   requestNoCol = strReqNo;
	}

	arr.push(primeLinkObj);
//	arr.push(requestNoCol) ;	
	arr.push(itemObj) ;
	//ROLLBACK of CR 338 BEGIN	
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END	
	arr.push(strUOM );
	arr.push(strDesc );
	arr.push(strReqQty) ;
	arr.push(strQtyRfi );
	arr.push(strQty );
	arr.push(strActPricQty);	
	arr.push(strUTFQty );
	arr.push(strBackQty);
	arr.push(strDate );
	arr.push(strNote );
	arr.push(strTotal) ;
	arr.push(strMaxLineStatusDesc );
	return arr ;
}



function populateBlankRow(documentType,driverDate,strPCData,strUOMData,counter)
{
        //alert("populateBlankRow documentType="+ documentType);
        //alert("populateBlankRow driverDate="+ driverDate);

	counter = counter + 1 ;
	var arr = new Array();
	var strChkBox = '<td style="width:5px" class="checkboxcolumn" />';
	var strBlank = '<td style="width:5px"/>' ;
	var requestNoCol= '' ;
	var ExtnRequestNo = document.getElementById("xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo");
	
	if (ExtnRequestNo != null)
    {
        ExtnRequestNo.focus();
    }
	
	requestNoCol = '<td><INPUT class=unprotectedoverrideinput OldValue="S-"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo" onBlur="validateRequestNumber(this)"   value="S-" maxLength=13 size=5 dataType="STRING"/></td> ' ;
	
	var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedoverrideinput type="text" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" OldValue="" CacheToCache="true" ';
	strItemID = strItemID + 'onBlur="fetchDataWithParams(this,\'PopulateItemDetails\',populateItemForCacheTransferDetails,setC2CParam(this))"/> <img class="lookupicon" ' ;
	strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\')"   src=\'/yantra/console/icons/lookup.gif\'/></td>' ;

	var strProductClass = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:40px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ProductClass" dataType="STRING"> </td> '; 

	var strUOM = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:40px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@TransactionalUOM" dataType="STRING"> </td> '; 

	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:140px" value=""/></td>' ;
	
	var strReqQty = '<td class="tablecolumn"> <INPUT class=unprotectedoverrideinput onBlur="checkMaxQty(this)" maxLength=10 size=5 style="width:40px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnOrigReqQty" dataType="STRING"> </td> ' ;

//	var strQtyRfi = '<td class="tablecolumn" style="width:120px"><A onclick="checkAvailableRFI(this)" title="Check Available RFI" href=""><label style="width:45px" value="" id="qtyRfi"/></A></td>' ;

	var strQtyRfiHidden =	'<td class="tablecolumn" tabindex="-1"> <INPUT type="hidden" class=unprotectedoverrideinput tabindex="-1" style="width:40px" name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnQtyRfi" value="" dataType="STRING" OldValue="" /> </td>';
	var strQtyRfi = '<td class=protectedtext><a id="availRFIQty" tabindex="-1" href=""></a></td>' + strQtyRfiHidden;
	
	var strQty =    '<td class="tablecolumn"> <INPUT onBlur="checkIssuedQuantity(this);checkMaxQty(this)" class=unprotectedoverrideinput style="width:40px" type="text" name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OrderedQty"/> </td>';

	//strQty = strQty + '<INPUT class=unprotectedoverrideinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnQtyRfi"   value="" dataType="STRING" OldValue="" /> </td>';
	
	var strUTFQty = '<td class="tablecolumn"> <INPUT class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnUTFQty\' dataType="STRING">';
	
	var strBackQty = '<td class="tablecolumn"><INPUT onBlur="validateC2CQuantity(this);checkMaxQty(this)"  class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnBackorderedQty\' dataType="STRING" >';
	strBackQty = strBackQty + '<INPUT class=unprotectedinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnBackOrderFlag"   value="" dataType="STRING" OldValue="" />';

	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	if (driverDate == "02") 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:70px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif" />' ;
	} 
	else 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:70px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif"/> ' ;
	} 
	strDate = strDate + '</td> ' ;
	var strNote = '<td> <INPUT class=unprotectedoverrideinput  style=\'width:100px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Notes/Note/@NoteText" dataType="STRING" OldValue=""/> </td>' ;
	var strActPricingQty = '<td class="tablecolumn" style="width:30px"><label style="width:30px" value=""/>';

	arr.push(strChkBox);
	arr.push(strBlank);
//	arr.push(requestNoCol) ;	
	arr.push(strItemID );
	//ROLLBACK of CR 338 BEGIN
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END
	arr.push(strUOM );
	arr.push(strDesc );
	arr.push(strReqQty) ;
	arr.push(strQtyRfi );
	arr.push(strQty );
	arr.push(strActPricingQty );
	arr.push(strUTFQty );
	arr.push(strBackQty );
	arr.push(strDate );
	arr.push(strNote );

        //alert("populateBlankRow end");
	return arr ;
}



function populateBlankRow_CreateCacheRequest(documentType,driverDate,strPCData,strUOMData,counter)
{
        //alert("populateBlankRow_CacheToCacheRequest documentType="+ documentType);
        //alert("populateBlankRow_CacheToCacheRequest driverDate="+ driverDate);

	counter = counter + 1 ;
	var arr = new Array();
	var strChkBox = '<td style="width:5px" class="checkboxcolumn" />';
	var strBlank = '<td style="width:5px"/>' ;
	var requestNoCol= '' ;
	var ExtnRequestNo = document.getElementById("xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo");
	
	if (ExtnRequestNo != null)
    {
        ExtnRequestNo.focus();
    }
	
	requestNoCol = '<td><INPUT class=unprotectedoverrideinput OldValue="S-"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo" onBlur="validateRequestNumber(this)"   value="S-" maxLength=13 size=5 dataType="STRING"/></td> ' ;


	var strItemID = '<td class="tablecolumn" style="width:100px" nowrap="true"> <input class=unprotectedoverrideinput type="text" style="width:100px; height:26px; font-size:20px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" OldValue="" CacheToCache="true" ';
	strItemID = strItemID + 'onBlur="fetchDataWithParams(this,\'PopulateItemDetails\',populateItemForCacheTransferDetails,setC2CParam(this))"/> <img class="lookupicon" ' ;
	strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\')"   src=\'/yantra/console/icons/lookup.gif\'/><font size=7></font></td>' ;

	var strProductClass = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:70px; height:25px; font-size:14px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ProductClass" dataType="STRING"> </td> '; 

	var strUOM = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:70px; height:26px; font-size:20px"  name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@TransactionalUOM" dataType="STRING"> </td> '; 

	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:500px; height:26px; font-size:20px" value=""/></td>' ;
	
	var strReqQty = '<td class="tablecolumn"> <INPUT class=unprotectedoverrideinput onBlur="checkMaxQty(this)" maxLength=10 size=5 style="width:70px; height:26px; font-size:20px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnOrigReqQty" dataType="STRING"> </td> ' ;

//	var strQtyRfi = '<td class="tablecolumn" style="width:120px"><A onclick="checkAvailableRFI(this)" title="Check Available RFI" href=""><label style="width:45px" value="" id="qtyRfi"/></A></td>' ;

	var strQtyRfiHidden =	'<td class="tablecolumn" tabindex="-1"> <INPUT type="hidden" class=unprotectedoverrideinput tabindex="-1" style="width:40px" name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnQtyRfi" value="" dataType="STRING" OldValue="" /> </td>';
	var strQtyRfi = '<td class=protectedtext><a id="availRFIQty" tabindex="-1" href=""></a></td>' + strQtyRfiHidden;
	
	var strQty =    '<td class="tablecolumn"> <INPUT onBlur="checkIssuedQuantity(this);checkMaxQty(this)" class=unprotectedoverrideinput style="width:40px" type="text" name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OrderedQty"/> </td>';

	//strQty = strQty + '<INPUT class=unprotectedoverrideinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnQtyRfi"   value="" dataType="STRING" OldValue="" /> </td>';
	
	var strUTFQty = '<td class="tablecolumn"> <INPUT class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnUTFQty\' dataType="STRING">';
	
	var strBackQty = '<td class="tablecolumn"><INPUT onBlur="validateC2CQuantity(this);checkMaxQty(this)"  class=unprotectedinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnBackorderedQty\' dataType="STRING" >';
	strBackQty = strBackQty + '<INPUT class=unprotectedinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnBackOrderFlag"   value="" dataType="STRING" OldValue="" />';

	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	if (driverDate == "02") 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:90px\' style=\'height:25px\' style=\'font-size:14px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif" />' ;
	} 
	else 
	{ 
		strDate = strDate + '<input type="text" OldValue="" style=\'width:90px\' style=\'height:25px\' style=\'font-size:14px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif"/> ' ;
	}
	strDate = strDate + '</td> ' ;
	var strNote = '<td> <INPUT class=unprotectedoverrideinput  style=\'width:500px\' style=\'height:26px\' style=\'font-size:20px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Notes/Note/@NoteText" dataType="STRING" OldValue=""/> </td>' ;
	var strActPricingQty = '<td class="tablecolumn" style="width:30px"><label style="width:30px" value=""/>';

	arr.push(strChkBox);
	arr.push(strBlank);
//	arr.push(requestNoCol) ;	
	arr.push(strItemID );
	//ROLLBACK of CR 338 BEGIN
	arr.push(strProductClass);
	//ROLLBACK of CR 338 END
	arr.push(strUOM );
	arr.push(strDesc );
	arr.push(strReqQty) ;
	arr.push(strQtyRfi );
	arr.push(strQty );
	arr.push(strActPricingQty );
	arr.push(strUTFQty );
	arr.push(strBackQty );
	arr.push(strDate );
	arr.push(strNote );

        //alert("populateBlankRow_CacheToCacheRequest end");
	return arr ;
}