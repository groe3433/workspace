<!-- /extn/common/render_orderline_js.jsp --> 
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@include file="/yfc/util.jspf" %>
<%
String maximumLinesToDisplay = (String)request.getParameter("MaxRecords");
if(maximumLinesToDisplay == null || maximumLinesToDisplay.equals(""))
	maximumLinesToDisplay = ResourceUtil.get("com.nwcg.icbs.yantra.numberofrecords");
if(maximumLinesToDisplay == null || maximumLinesToDisplay.equals(""))
	maximumLinesToDisplay = "20";
%>
<script language="javascript">
<%
	String strDoc = resolveValue("xml:/Order/@DocumentType");
	String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String strUOM = "<Option value=\"\"></Option>" ;
	YFCElement uomDoc =  (YFCElement) request.getAttribute("UnitOfMeasureList");
	Iterator itr = uomDoc.getChildren();
	while(itr.hasNext())
	{
		YFCElement elem = (YFCElement) itr.next();
		String uom = elem.getAttribute("UnitOfMeasure") ; 
		strUOM = strUOM + "<Option value=\""+uom+"\">"+uom+"</Option>";
	}
	String strPC = "<Option value=\"\"></Option>" ;
	YFCElement pcDoc =  (YFCElement) request.getAttribute("ProductClassList");
	itr = pcDoc.getChildren();
	while(itr.hasNext())
	{
		YFCElement elem = (YFCElement) itr.next();
		String pc = elem.getAttribute("CodeValue") ; 
		strPC = strPC+ "<Option value=\""+pc+"\">"+pc+"</Option>";
	}	
%>
// this function will populate the order lines - a call back function from AJAX framework
/*
steps 
1. clear the current table
2. populate the rows
2. render the data in table
*/

function populateOrderLines(elem,xmlDoc)
{
	//if (xmlDoc == null) { alert("xmlDoc to populateOrderLines is null"); }
	// get the current page number
	var pageNumber = document.getElementById("CurrentPageNumber");
	var linesPerPageElm = document.getElementById("numberRowsDisplay");
	var linesPerPage = <%=maximumLinesToDisplay%>;
	if (linesPerPageElm != null)
		linesPerPage = linesPerPageElm.value;
		
	var totalLinesOnOrderElm = document.getElementById("totalLinesOnOrder");
	var totalLinesOnOrder = 0;
	if (totalLinesOnOrderElm != null)
		totalLinesOnOrder = totalLinesOnOrderElm.value;
		
	// get the order line tag from output xml
	var nodes = xmlDoc.getElementsByTagName("OrderLine");
	var strOnLoad = elem.getAttribute("ON_LOAD");
	elem.setAttribute("ON_LOAD","");

	if(nodes!=null && nodes.length > 0 )
	{
		if(totalLinesOnOrder % linesPerPage > 0)
		{
			// add one page 
			document.getElementById("TotalPageNumber").value = Math.floor((totalLinesOnOrder/linesPerPage )) + 1 ;
		}
		else
		{
			document.getElementById("TotalPageNumber").value = totalLinesOnOrder/linesPerPage; 
		}
		// when the user doesnt have any of the lines we will display him page no 1 insated of page 1 of 0 
		if(totalLinesOnOrder == 0)
		{
			document.getElementById("TotalPageNumber").value = 1 ;
		}	
	
		var temp = pageNumber.value * 1; // to convert this to an int
		// if user has clicked on next
		if(strOnLoad != 'true')
		{
			if(elem.value == 'Next')
			{
				pageNumber.value =  temp + 1 ;
//Begin CR845 02012013
				if(temp == document.getElementById("TotalPageNumber").value) {
                } else {
                   if(objNext.disabled == true) { objNext.disabled = false; }				
                }
//End CR845 02012013
			}
			else if(elem.value == 'Previous')// assumming the user has clicked on previous
			{
				pageNumber.value =  temp - 1 ;
				if(temp >= 1) {
                   if(objNext.disabled == true) { objNext.disabled = false; }
                } else {
                }
			}
		}
		// clearing the existing table
		clearTableData();
		
		// loop over all line/extn to see if we have at least 1 with a system number
		var drawSysCol = 0;
		for(i = 0 ; i < nodes.length  ; i++)
		{
			var curOL = nodes(i);
			var elemExtnList = curOL.getElementsByTagName("Extn");
			if(elemExtnList!=null && elemExtnList.length > 0 )
			{
				elemExtn = elemExtnList(0);
				strReqNo = elemExtn.getAttribute("ExtnSystemNo");
				if (checkForNull(strReqNo) != '') {
					drawSysCol = 1;
					break;
				}
			}
		}

		for(i = 0 ; i < nodes.length  ; i++)
		{
			var orderLine = nodes(i);
			var strImage = "src='/yantra/console/icons/calendar.gif' alt='Calendar'";
			// this is a prepare a row in the table
			var tableRow = prepareTableRow(orderLine,(i+1),'<%=resolveValue("xml:/Order/@DocumentType")%>','<%=resolveValue("xml:/Order/@DriverDate")%>','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=resolveValue("xml:/CurrencyList/Currency/@PrefixSymbol")%>','<%=resolveValue("xml:/CurrencyList/Currency/@PostfixSymbol")%>',strImage,drawSysCol);
			// display this newly created row in the table
			///Set the Rcv Checkboxes?
			populateTable(tableRow);
			
			elemExtnList = orderLine.getElementsByTagName("Extn");
						
			if(elemExtnList!=null && elemExtnList.length > 0 )
			{
				var elemExtn = elemExtnList(0);
				var toRcv =  elemExtn.getAttribute("ExtnToReceive");
				
				var combobx = document.getElementById("xml:/Order/OrderLines/OrderLine_" + (i+1) + "/Extn/@ExtnToReceive");
				
				//alert("toRcvX = "+toRcv);
				
				if(combobx != null && toRcv == "N")
				combobx.value = "N";
			}
		}
	}
	else 
	{
		if(strOnLoad != 'true')
		{
			// if user has clicked on next
			if(elem.value == 'Next')
			{
				//alert("1");
				var objNext = document.getElementById("Next");
				//objNext.disabled = true ;
				alert('Last record reached');
				return true;
			}
			// if the user has clicked on previous
			else if(elem.value == 'Previous')
			{
				var objNext = document.getElementById("Previous");
				//objNext.disabled = true ;
				alert('First record reached');
				return false;
			}
		}
	}
}

// display the rows by default when the entire order details page loads
function window.onload()
{
	if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
		return;
	}

	var numRowsToShow = document.getElementById("numberRowsDisplay");
	var getAvailRFI = document.getElementById("UseLiveRFI");
	var getAvailRFIBool = -1;
	if (getAvailRFI != null) {
		var boolStr = getAvailRFI.value;
		if (boolStr == "true") {
			getAvailRFIBool = 0;//true
		}		
	}
	
	var objNext = document.getElementById("Next");
	objNext.setAttribute("ON_LOAD","true");
	
	if (numRowsToShow != null) {
		var numToDisplay = numRowsToShow.value;		
		if (getAvailRFIBool == 0) {
			//alert("onload 1");
			fetchDataWithParams(objNext,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));
		}
		else {
			//alert("onload 2");
			fetchDataWithParams(objNext,'getOrderLineList',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));
		}
	}
	else {
		if (getAvailRFIBool == 0) {
			//alert("onload 3");
			fetchDataWithParams(objNext,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', '<%=maximumLinesToDisplay%>'));
		}
		else {
			//alert("onload 4");		
			fetchDataWithParams(objNext,'getOrderLineList',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=maximumLinesToDisplay%>'));		
		}
	}
}

function addBlankRows(element)
{
	var totalRows = '<%=maximumLinesToDisplay%>' * 1 ;
	for(i = 0 ; i < totalRows ; i++)
	{
		var row = populateBlankRow('<%=strDoc%>','<%=driverDate%>','<%=strPC%>','<%=strUOM%>',i);
		populateTable(row);
	}
	var objNext = document.getElementById("Next");
	objNext.disabled = false ;
}

// just a wrapper function to check for null values
function checkForNull(str)
{
	if(str == null || str == 'null' || str == 'undefined')
			str = '' ;
	return str ;
}

// this function populates the table with the ONE ROW at a time
function populateTable(tableRow)
{

	var tbl = document.getElementById("OrderLines");
	// the last row would be the buttons so inserting one row before that
	var lastRow = tbl.rows.length - 1;
	var row = tbl.insertRow(lastRow);
	for (r = 0; r < tableRow.length; r++) 
	{   
         var cell = row.insertCell(r);
		 cell.innerHTML = tableRow[r];
    }
}

function clearTableData()
{
	var tbl = document.getElementById("OrderLines");
	var lastRow = tbl.rows.length;
	// invoke this only if the table has any data
	// by default it will have two rows
	// 1. the header row
	// 2. the last row with the buttons
	if(lastRow > 2 )
	{
		// till the table has only two rows
		// the header and the buttons rows
		while (tbl.rows.length > 2) 
		{
			// since the last row is the one with buttons
			// deleting always the second last row
			tbl.deleteRow(tbl.rows.length - 2);
		}
    }
}
// this function checks if the page number entered is more then the total number of pages
// if yes then we wont submit the request to fetch the lines from the server
function verifyPageNumberAndFetchData(elem,orderHeaderKey,maximumLinesToDisplay)
{
	var pageNumber = document.getElementById("CurrentPageNumber");
	var obj = document.getElementById("TotalPageNumber") ;
	var totalPages = obj.value;
	var strPageNo = pageNumber.value;	

	// if user has clicked on next
	if(elem.value == 'Next')
	{
		if (strPageNo*1 == totalPages*1) {
			var objNext = document.getElementById("Next");
			objNext.disabled = "true" ;			
			alert('Last record reached');
			return false;
		}		
		document.getElementById("CurrentPageNumber").value = document.getElementById("CurrentPageNumber").value*1 +1;
	}	
	else if(elem.value == 'Previous')
	{
		// if the user has clicked on previous
		if (strPageNo*1 == 1) {
			var objNext = document.getElementById("Previous");
			//objNext.disabled = true ;
			alert('First record reached');
//Begin CR845 02012013		
			 var objPrevious = document.getElementById("Previous");
			if(objPrevious.disabled == false) {
               objPrevious.disabled = true;
            }
//End CR845 02012013		
			return false;
		}
		document.getElementById("CurrentPageNumber").value = document.getElementById("CurrentPageNumber").value*1 -1;
	}

	if(eval(obj) && (strPageNo * 1 ) > (obj.value * 1 ))
	{
		alert("Page # "+strPageNo+" doesn't exist ");
		document.getElementById("CurrentPageNumber").value = 1;
		document.getElementById("CurrentPageNumber").select();
		document.getElementById("CurrentPageNumber").focus();
		return false;
	}
	
	var getAvailRFI = document.getElementById("UseLiveRFI");
	var getAvailRFIBool = -1;
	if (getAvailRFI != null) {
		var boolStr = getAvailRFI.value;
		if (boolStr == "true") {
			getAvailRFIBool = 0;
			//alert("CommonCodeShortDescription == true");
		}		
		else {
			//alert("CommonCodeShortDescription == false");
		}
	}else {
		getAvailRFIBool = -1;
	}
	
	var numRowsToShow = document.getElementById("numberRowsDisplay");
	elem.setAttribute("ON_LOAD","true");
	
	if (numRowsToShow != null) {
		var numToDisplay = numRowsToShow.value;
		if (getAvailRFIBool == 0) {
			//alert("verifyPageNumberAndFetchData 1");
			fetchDataWithParams(elem,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));
		}
		else {
			//alert("verifyPageNumberAndFetchData 2");
			fetchDataWithParams(elem,'getOrderLineList',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));
		}
	}
	else {
		if (getAvailRFIBool == 0) {
			//alert("verifyPageNumberAndFetchData 3");
			fetchDataWithParams(elem,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam(elem,orderHeaderKey,maximumLinesToDisplay));
		}
		else {
			//alert("verifyPageNumberAndFetchData 4");
			fetchDataWithParams(elem,'getOrderLineList',populateOrderLines,setOrderLineParam(elem,orderHeaderKey,maximumLinesToDisplay));
		}
	}	
}
// this is a generic function to set the parameters before we invoke the data from server
function setOrderLineParam(elem,orderHeaderKey,maximumLinesToDisplay)
{
	//invokeSave();
	var returnArray = new Object();
	var pageNumber = document.getElementById("CurrentPageNumber");
	var strPageNo = pageNumber.value;	
	
	var numRowsToShow = document.getElementById("numberRowsDisplay");
	var numToDisplay = 20;
	if (numRowsToShow != null) {
		numToDisplay = numRowsToShow.value;
	}

	// if user has clicked on the next button
	if(elem.value == 'Next')
	{
		var objNext = document.getElementById("Previous");
		//objNext.disabled = false ;
		// increase the page number - just to pass on the value
		// the textbox will display the value only when the call back function is invoked
		strPageNo = (strPageNo * 1 ) + 1;
	}
	// if the user has clicked on previous button
	else if(elem.value == 'Previous')
	{
		var objNext = document.getElementById("Next");
		//objNext.disabled = false;
		// similarly reduce the page number by one when the user hits on previous button
//Begin CR845 02012013			 
			if(objNext.disabled = true) {
                   objNext.disabled = false;
            }
//End CR845 02012013
		strPageNo = (strPageNo * 1 ) - 1;
//Begin CR845 020122013			 
	} else {
		var objNext = document.getElementById("Next");
            objNext.disabled = false;
		var objPrevious = document.getElementById("Previous");
            objPrevious.disabled = false;    
//End CR845 02012013
	}
	// calculating the to and from page numbers

	var strTo = strPageNo * numToDisplay ;
	var strFrom = strTo - (numToDisplay - 1);
	returnArray["xml:/OrderHeaderKey"] = orderHeaderKey;
	returnArray["xml:/FromPrimeLineNo"] = strFrom ;
	returnArray["xml:/ToPrimeLineNo"] = strTo ;
	returnArray["USECACHE"] = 'false';
	return returnArray;
}

function setRequestedDate(elem)
{
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var dateCurrentDate = new Date();
	
	var strCurrentDate = (dateCurrentDate.getMonth()+1)+"/"+dateCurrentDate.getDate()+"/"+dateCurrentDate.getFullYear();
	
	for(i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/@ReqDeliveryDate') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strCurrentDate ;
			break;
		}
		if(InputList[i].name.indexOf('/@ReqShipDate') != -1)
		{
			stdPack = InputList[i] ;
			InputList[i].value = strCurrentDate ;
			break;
		}
	}
}

function validateSubLineSelection(chkName) {
		if (!chkName) {
			chkName="chkEntityKey";
		}
		var eleArray = document.forms["containerform"].elements;
		var substitutionAllowed = 1;
		var selectedRowCount = 0;
		var selectedRow = null;
		var unselectedRows = new Array();
		var draftOrCreatedFound = 0;				
		for (var i = 0; i < eleArray.length; i++ ) {
			if (eleArray[i].name == chkName ) {
				if (eleArray[i].checked) {
					selectedRowCount++;
					if (selectedRowCount > 1) { 
						substitutionAllowed = 0;
						break;
					}
					selectedRow = eleArray[i].parentNode.parentNode;						
				} else {
					unselectedRows.push(eleArray[i].parentNode.parentNode);
				}
			}//end if checkbox
		}//end outer for

		if (!substitutionAllowed) {
			if (selectedRowCount > 1) {
				alert(YFCMSG013);
			} 
			else if (selectedRowCount < 1) {
				alert(YFCMSG002);
			}
			return substitutionAllowed;
		}
				
		var aHrefList = selectedRow.getElementsByTagName('A');

		for (i = 0; i < aHrefList.length; i++) {
			if (aHrefList[i].innerHTML.indexOf('raft') != -1) {
				draftOrCreatedFound = 1;
				break;
			}
			else if (aHrefList[i].innerHTML.indexOf('reated') != -1) {
				if (aHrefList[i].innerHTML.indexOf('was created') != -1) {
					continue; }
					
				draftOrCreatedFound = 1;		
				break;
			}		
		}
		
		if (draftOrCreatedFound == 0) { 
			alert("Item substitution allowed only on Draft or Created issue lines!");
			return 0; 
		}		

		var rowInputList = selectedRow.getElementsByTagName("Input");
		var selectedReqNumWDots = new Array();

		//See if the selected line's request number has a dot
		for (var j = 0; j < rowInputList.length; j++) {
			if (rowInputList[j].name.indexOf('@ExtnRequestNo') != -1) {
				var dotIndex = rowInputList[j].value.lastIndexOf('.');
				if (dotIndex != -1) {
					//S-042012.1.1
					//01234567890
					var baseReqNo = rowInputList[j].value.substring(0, dotIndex);
					selectedReqNumWDots.push(baseReqNo);
					break;
				}
			}
		}
		
		//If the selected row's req no has a dot, we need to make sure it doesn't match
		//up to any of the other unselected issue lines by removing all characters from
		//the non selected request number past the last index of a dot '.', inclusive
		if (selectedReqNumWDots.length == 1) {
			var unselectedInputsList = null;
			for (var i = 0; i < unselectedRows.length; i++) {
				unselectedInputsList = unselectedRows[i].getElementsByTagName("Input");
				for (var j = 0; j < unselectedInputsList.length; j++) {
					if (unselectedInputsList[j].name.indexOf('@ExtnRequestNo') != -1) 
					{
						var dotIndex = unselectedInputsList[j].value.lastIndexOf('.');
						var baseReqNo = '';
						if (dotIndex != -1) {
							baseReqNo = unselectedInputsList[j].value.substring(0,dotIndex);
						}
						else {
							baseReqNo = unselectedInputsList[j].value;
						}
						//Now check and see if this baseReqNo exists in the selectedReqNumWDots array
						if (baseReqNo == selectedReqNumWDots[0]) {
							alert("Item substitution not allowed on issue lines created\n\rfrom a previous substitution or consolidation!");
							substitutionAllowed = 0;
							return substitutionAllowed;
						} else {
							break;
						}
					} 			
				}				
			}//end outer for			
		}
		return substitutionAllowed;
}

function validateConsLineSelection(chkName) {
		if (!chkName) {
			chkName="chkEntityKey";
		}
		var eleArray = document.forms["containerform"].elements;
		var consolidationAllowed = 1;
		var selectedRowCount = 0;
		var selectedRows = new Array();
		var draftOrCreatedFnd = 0;	
		var unselectedRows = new Array();
		for (var i =0; i < eleArray.length; i++ ) {
			if (eleArray[i].name == chkName ) {
				if (eleArray[i].checked) {
					selectedRowCount++;
					selectedRows.push(eleArray[i].parentNode.parentNode);
				}
				else {
					unselectedRows.push(eleArray[i].parentNode.parentNode);
				}//end if checkbox.checked
			}//end if checkbox
		}//end outer for
		
		if (selectedRowCount < 2) {
			alert("At least 2 issue lines in valid status must be chosen for item consolidation");
			consolidationAllowed = 0;
			return consolidationAllowed;
		}

		var aHrefList = null;	
		for (var i = 0; i < selectedRows.length; i++) {
			aHrefList = selectedRows[i].getElementsByTagName('A');
			for (var j = 0; j < aHrefList.length; j++) {
				if (aHrefList[j].innerHTML.indexOf('raft') != -1) {
					draftOrCreatedFnd = 1;
					break;
				}
				else if (aHrefList[j].innerHTML.indexOf('reated') != -1) 
				{
					if (aHrefList[j].innerHTML.indexOf('was created') != -1) 
					{
						continue; 
					} else {
						draftOrCreatedFnd = 1;
						break;
					}
				}		
			}		
			if (draftOrCreatedFnd == 0) { 
				alert("Item consolidation allowed only on Draft or Created issue lines!");
				return 0; 
			}
		}

		//Build up an array of selected issue lines with request numbers
		//containing at least 1 dot, store the string minus the last 
		//number and dot. e.g. S-001.2.5 gets stored in array as S-001.2
		var rowsInputList = null;
		var selectedReqNumsWDots = new Array();
		for (var i = 0; i < selectedRows.length; i++) {		
			rowsInputList = selectedRows[i].getElementsByTagName("Input");
			for (var j = 0; j < rowsInputList.length; j++) {
				if (rowsInputList[j].name.indexOf('@ExtnRequestNo') != -1) {
					var dotIndex = rowsInputList[j].value.lastIndexOf('.');
					if (dotIndex != -1) {
						//S-042012.1.1
						//01234567890
						var baseReqNo = rowsInputList[j].value.substring(0, dotIndex);
						selectedReqNumsWDots.push(baseReqNo);
						break;//we got the request no from this row already, move onto the next row
					}
				}
			}
		}

		//If we have any selected request lines that contain dots
		//then we need to make sure that none of them exist already
		//on the current order.
		if (selectedReqNumsWDots.length > 0) {
			var unselectedInputsList = null;
			for (var i = 0; i < unselectedRows.length; i++) {
				unselectedInputsList = unselectedRows[i].getElementsByTagName("Input");
				for (var j = 0; j < unselectedInputsList.length; j++) {
					if (unselectedInputsList[j].name.indexOf('@ExtnRequestNo') != -1) 
					{
						var dotIndex = unselectedInputsList[j].value.lastIndexOf('.');
						var baseReqNo = '';
						if (dotIndex != -1) {
							baseReqNo = unselectedInputsList[j].value.substring(0,dotIndex);
						}
						else {
							baseReqNo = unselectedInputsList[j].value;
						}
						//Now check and see if this baseReqNo exists in the selectedReqNumsWDots array
						for (var k = 0; k < selectedReqNumsWDots.length; k++) 
						{
							if (baseReqNo == selectedReqNumsWDots[k]) {
								alert("Item consolidation not allowed on issue lines created\n\rfrom a previous consolidation or substitution!\n\r\tRequest Number: "+baseReqNo);
								consolidationAllowed = 0;								
								return consolidationAllowed;
							}
						}
						break;
					} 			
				}
			}//end outer for			
		}
		
		if (!consolidationAllowed) { return consolidationAllowed; }
		
		var orderLineKeys = new Array();
		var hiddenInputs = null;
		var ohk = '<%=resolveValue("xml:/Order/@OrderHeaderKey")%>';
		var chosenLines = '%3COrder+OrderHeaderKey%3D%22' + ohk + '%22%3E';	
		chosenLines = chosenLines + '%3COrderLines%3E';

		for (var i = 0; i < selectedRows.length; i++) {		
			hiddenInputs = selectedRows[i].getElementsByTagName("INPUT");
			for (var j = 0; j < hiddenInputs.length; j++) {				
				if (hiddenInputs[j].name.indexOf('xml') != -1) {
					if (hiddenInputs[j].name.indexOf('OrderLineKey') != -1) {								
						chosenLines = chosenLines + '%3COrderLine+OrderHeaderKey%3D%22' + ohk + '%22+OrderLineKey%3D%22';			
						chosenLines = chosenLines + hiddenInputs[j].value + '%22%2F%3E';
						break;
					}
					else { continue; }
				}
				else { continue; }
			}		
		}	
		chosenLines = chosenLines + '%3C%2FOrderLines%3E';
		chosenLines = chosenLines + '%3C%2FOrder%3E';			
		for (var i =0; i < eleArray.length; i++ ) {
			if (eleArray[i].name == chkName ) {
				if (eleArray[i].checked) {
					eleArray[i].value = chosenLines;
				}//end if checkbox.checked
			}//end if checkbox
		}//end outer for		
		
		yfcShowDetailPopupWithKeysAndParams('ISUYOMD405', '', 800, 600, new Object(), 'chkEntityKey', 'ISUorderline', null, 'x=x'); 
		
		//Need to call the JS window.onload() in order to re-draw the order lines inner panel with the old chkEntityKey		
		window.onload();
		return consolidationAllowed;
}

function validateSystemNumberLineSelection(chkName) {
		if (!chkName) {
			chkName="chkEntityKey";
		}
		var eleArray = document.forms["containerform"].elements;
		var sysNoEntryAllowed = 1;
		var selectedRowCount = 0;
		var selectedRows = new Array();
		var draftOrCreatedFnd = 0;	
		var unselectedRows = new Array();
		for (var i =0; i < eleArray.length; i++ ) {
			if (eleArray[i].name == chkName ) {
				if (eleArray[i].checked) {
					selectedRowCount++;
					selectedRows.push(eleArray[i].parentNode.parentNode);
				}
				else {
					unselectedRows.push(eleArray[i].parentNode.parentNode);
				}//end if checkbox.checked
			}//end if checkbox
		}//end outer for
		
		if (selectedRowCount < 1) {
			alert("At least 1 issue lines in valid status must be chosen for system number entry");
			sysNoEntryAllowed = 0;
			return sysNoEntryAllowed;
		}

		var aHrefList = null;	
		for (var i = 0; i < selectedRows.length; i++) {
			aHrefList = selectedRows[i].getElementsByTagName('A');
			for (var j = 0; j < aHrefList.length; j++) {
				if (aHrefList[j].innerHTML.indexOf('raft') != -1) {
					draftOrCreatedFnd = 1;
					break;
				}
				else if (aHrefList[j].innerHTML.indexOf('reated') != -1) 
				{
					if (aHrefList[j].innerHTML.indexOf('was created') != -1) 
					{
						continue; 
					} else {
						draftOrCreatedFnd = 1;
						break;
					}
				}		
			}		
			if (draftOrCreatedFnd == 0) { 
				alert("System Number Entry allowed only on Draft or Created issue lines!");
				sysNoEntryAllowed = 0;
				return sysNoEntryAllowed; 
			}
		}
	
		var orderLineKeys = new Array();
		var hiddenInputs = null;
		var ohk = '<%=resolveValue("xml:/Order/@OrderHeaderKey")%>';
		var chosenLines = '%3COrder+OrderHeaderKey%3D%22' + ohk + '%22%3E';	
		//22 "
		//2F /
		//3C <
		//3D =
		//3E >
		chosenLines = chosenLines + '%3COrderLines%3E';

		for (var i = 0; i < selectedRows.length; i++) {		
			hiddenInputs = selectedRows[i].getElementsByTagName("INPUT");
			for (var j = 0; j < hiddenInputs.length; j++) {				
				if (hiddenInputs[j].name.indexOf('xml') != -1) {
					if (hiddenInputs[j].name.indexOf('OrderLineKey') != -1) {								
						chosenLines = chosenLines + '%3COrderLine OrderHeaderKey%3D%22' + ohk + '%22 OrderLineKey%3D%22';			
						chosenLines = chosenLines + hiddenInputs[j].value;
						chosenLines = chosenLines + '%22%3E';
						chosenLines = chosenLines + '%3CExtn ExtnSystemNo%3D%22%22%2F%3E';
						chosenLines = chosenLines + '%3C%2FOrderLine%3E';
						break;
					}					
					else { continue; }
				}
				else { continue; }
			}		
		}	
		chosenLines = chosenLines + '%3C%2FOrderLines%3E';
		chosenLines = chosenLines + '%3C%2FOrder%3E';			
		for (var i =0; i < eleArray.length; i++ ) {
			if (eleArray[i].name == chkName ) {
				if (eleArray[i].checked) {
					eleArray[i].value = chosenLines;
				}//end if checkbox.checked
			}//end if checkbox
		}//end outer for		
		
		yfcShowDetailPopupWithKeysAndParams('ISUYOMD406', '', 800, 400, new Object(), 'chkEntityKey', 'ISUorderline', null, 'x=x'); 
		
		//Need to call the JS window.onload() in order to re-draw the order lines inner panel with the old chkEntityKey		
		window.onload();
		return 0;
}

function changeNoRecsToDisplay()
{
	var numSelected = document.getElementById("numberRowsDisplay");
	var getAvailRFI = document.getElementById("UseLiveRFI");
	var getAvailRFIBool = -1;
	if (getAvailRFI != null) {
		var boolStr = getAvailRFI.value;
		if (boolStr == "true") {
			getAvailRFIBool = 0;
			//alert("getAvailRFIBool == 0,true");
		}		
	}
	
	var objNext = document.getElementById("Next");
	objNext.setAttribute("ON_LOAD","true");
	
	if (numSelected != null) {
		var numToDisplay = numSelected.value;	
		document.getElementById("CurrentPageNumber").value = 1;	
		if (getAvailRFIBool == 0) {
			fetchDataWithParams(objNext,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));
		}
		else {
			fetchDataWithParams(objNext,'getOrderLineList',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>', numToDisplay));						
		}
	}
}

</script>
<!-- End /extn/common/render_orderline_js.jsp --> 