<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil"%>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript">
<%
	String nextReqNo = "";
	String strDoc = getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType");
	String driverDate = getValue("OrderLine", "xml:/OrderLine/Order/@DriverDate");
	String strUOM = "<Option value=\"\"></Option>" ;
	YFCElement uomDoc =  (YFCElement) request.getAttribute("UnitOfMeasureList");
	Iterator itr = uomDoc.getChildren();
	while(itr.hasNext()) {
		YFCElement elem = (YFCElement) itr.next();
		String uom = elem.getAttribute("UnitOfMeasure") ; 
		strUOM = strUOM + "<Option value=\""+uom+"\">"+uom+"</Option>";
	}
	String strPC = "<Option value=\"\"></Option>" ;
	YFCElement pcDoc =  (YFCElement) request.getAttribute("ProductClassList");
	itr = pcDoc.getChildren();
	while(itr.hasNext()) {
		YFCElement elem = (YFCElement) itr.next();
		String pc = elem.getAttribute("CodeValue") ; 
		strPC = strPC+ "<Option value=\""+pc+"\">"+pc+"</Option>";
	}
	String sHiddenDraftOrderFlag = getValue("OrderLine", "xml:/OrderLine/Order/@DraftOrderFlag");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("OrderLine", "xml:/OrderLine/Order/@EnterpriseCode"));
	extraParams += "&" + getExtraParamsForTargetBinding("xml:/OrderLine/Order/@OrderHeaderKey", resolveValue("xml:/OrderLine/Order/@OrderHeaderKey"));
	extraParams += "&" + getExtraParamsForTargetBinding("IsStandaloneService", "Y");
	extraParams += "&" + getExtraParamsForTargetBinding("hiddenDraftOrderFlag", sHiddenDraftOrderFlag);
	YFCElement orderLineElm = (YFCElement) request.getAttribute("OrderLine");
	YFCElement orderElm = null;
	YFCElement orderExtnElm = null;
	itr = orderLineElm.getChildren();
	while (itr.hasNext()) {
		YFCElement elem = (YFCElement) itr.next();
		String orderNo = elem.getAttribute("OrderNo");
		String incNo = elem.getAttribute("IncidentNo");
		if (orderNo != null && orderNo.length() !=0) {
			orderElm = elem;
			break;
		}
	}
	String incidentNo = getValue("OrderLine","xml:/OrderLine/Order/Extn/@ExtnIncidentNo");
	String incidentYear = getValue("OrderLine", "xml:/OrderLine/Order/Extn/@ExtnIncidentYear");
	String reqNoStart = getValue("NWCGIncidentOrder", "xml:/NWCGIncidentOrder/@RequestNoBlockStart");
	String reqNoEnd = getValue("NWCGIncidentOrder", "xml:/NWCGIncidentOrder/@RequestNoBlockEnd");
	String shipNode = getValue("OrderLine", "xml:/OrderLine/@ShipNode");
	String strMax = "1";
	String primeLineNo = getValue("OrderLine", "xml:/OrderLine/@PrimeLineNo");
	String maxLineStatus = getValue("OrderLine", "xml:/OrderLine/@MaxLineStatus");
	String subbedItem = getValue("OrderLine", "xml:/OrderLine/Item/@ItemID");
%>

// display the rows by default when the entire order details page loads
function window.onload() {
	if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
		return;
	}
	var clrBtn = document.getElementById("clearButton");
	clrBtn.disabled = true;
	focusOnAddButton();
}

function focusOnAddButton() {
	var addBtn = document.getElementById("Add");
	addBtn.focus();
}

function addMultipleRows() {
	debugger;
	var numRowsToCr = document.getElementById("rowCount");
	var clrBtn = document.getElementById("clearButton");
	var addBtn = document.getElementById("addButton");
	addBtn.disabled = true;
	numRowsToCr.disabled = true;
	clrBtn.disabled = false;
	var fetchArray = new Object();
	fetchArray["xml:OrderHeaderKey"] = '<%=resolveValue("xml:/OrderLine/Order/@OrderHeaderKey")%>';
	fetchArray["xml:OrderLineKey"] = '<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>';	
	fetchArray["xml:ExtnIncidentNo"] = '<%=incidentNo%>';
	fetchArray["xml:ExtnIncidentYear"] = '<%=incidentYear%>';
	fetchArray["USECACHE"] = 'false';
	fetchArray["xml:NumberOfRequests"] = numRowsToCr.value;	
	fetchArray["xml:ExtnRequestNo"] = '<%=resolveValue("xml:/OrderLine/Extn/@ExtnRequestNo")%>';	
	fetchArray["xml:PrimeLineNo"] = '<%=resolveValue("xml:/OrderLine/@PrimeLineNo")%>';
	//Call to get the next "dot" number for substitution request numbers. e.g.: S-0000782
	fetchDataWithParams(addBtn,'getNextSubstRequestNoForOrderLine', populateMultipleRows, fetchArray);	
}

// xmlDoc e.g.: <RequestNumbers><RequestNoLine RequestNo="S-000011.1/><RequestLine RequestNo="S-000011.2/> ...
function populateMultipleRows (elm, xmlDoc) {
	var addBtn = document.getElementById("addButton");	
	var numRowsToCr = document.getElementById("rowCount");
	addBtn.disabled = true;	
	numRowsToCr.disabled = true;
	var nl = xmlDoc.getElementsByTagName("RequestLine");
	for (i=0;  i < nl.length; i++) {
		var newRow = populateBlankRowForSubstitutionUI('<%=strDoc%>','<%=driverDate%>','<%=strPC%>','<%=strUOM%>',i, nl[i].getAttribute("RequestNo"));	
		populateTable(newRow);
	}
	document.getElementById("itemid").focus();
}

// just a wrapper function to check for null values. TODO: extn.js has this as checkStringForNull(str) already
function checkForNull(str) {
	if(str == null || str == 'null' || str == 'undefined')
		str = '' ;
	return str ;
}

// this function populates the table with the ONE ROW at a time
function populateTable(tableRow) {
	var tbl = document.getElementById("OrderLines");
	// the last row would be the buttons so inserting one row before that
	var lastRow = tbl.rows.length - 1;
	var row = tbl.insertRow(lastRow);
	for (r = 0; r < tableRow.length; r++) {   
         var cell = row.insertCell(r);
		 cell.innerHTML = tableRow[r];
    }
}

function clearTableData() {
	var clrBtn = document.getElementById("clearButton");
	clrBtn.disabled = true;
	var addBtn = document.getElementById("addButton");
	addBtn.disabled = false;
	var addBtn = document.getElementById("rowCount");
	addBtn.disabled = false;
	var tbl = document.getElementById("OrderLines");
	var lastRow = tbl.rows.length;
	// invoke this only if the table has any data by default it will have two rows
	// 1. the header row
	// 2. the last row with the buttons
	if(lastRow > 2 ) {
		// till the table has only two rows the header and the buttons rows
		while (tbl.rows.length > 2) {
			// since the last row is the one with buttons
			// deleting always the second last row
			tbl.deleteRow(tbl.rows.length - 2);
		}
    }
}

function setRequestedDateAndRequestedQty(elem) {
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var dateCurrentDate = new Date();
	var strCurrentDate = (dateCurrentDate.getMonth()+1)+"/"+dateCurrentDate.getDate()+"/"+dateCurrentDate.getFullYear();
	for(i = 0 ; i < InputList.length ; i++) {
		if(InputList[i].name.indexOf('/@ReqDeliveryDate') != -1) {
			stdPack = InputList[i] ;
			InputList[i].value = strCurrentDate ;
			break;
		}
		if(InputList[i].name.indexOf('/@ReqShipDate') != -1) {
			stdPack = InputList[i] ;
			InputList[i].value = strCurrentDate ;
		}		
	}
}

function populateBlankRowForSubstitutionUI(documentType,driverDate,strPCData,strUOMData,counter, requestNo) {
	counter = counter + 1 ;
	var arr = new Array();
	var bOtherIssues = true ;
	if(eval(documentType) && documentType == '0001')
		bOtherIssues = false ;
	var requestNoCol= '' ;
	var isBoQtyDisabled = 0;
	var subbedItem = '<%=subbedItem%>';
	if (checkForNull(subbedItem) != '') {
		if (subbedItem.indexOf('004390') != -1) {
			isBoQtyDisabled = 1;
		}		
	}
	var boDisabled = '';
	if (isBoQtyDisabled == 1) {
		boDisabled = 'disabled="disabled" title="Backorder Quantity can not be entered when substituting a trackable resource: 004390 Radio Kit"';	
	}
	if(!bOtherIssues) {
		// BEGIN - CR 1324 - July 2, 2015
		// Making this text box readonly so the user cannot modify it. 
		requestNoCol = '<td><INPUT readonly class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo" dataType="STRING" maxLength=12 size=12  value="'; 
		requestNoCol = requestNoCol + requestNo + '"/>';
	} else {
		// Making this text box readonly so the user cannot modify it. 
		requestNoCol = '<td><INPUT readonly class=unprotectedoverrideinput  name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnRequestNo" maxLength=12 size=12 dataType="STRING" value="';
		requestNoCol = requestNoCol + requestNo + '"/>';
		// END - CR 1324 - July 2, 2015
	}
	var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedoverrideinput id="itemid" type="text" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ItemID" counter="'+counter+ '" ';
	strItemID = strItemID + 'onBlur="itemBlurHandler(this);setRequestedDateAndRequestedQty(this)" tabindex="1"/> <img class="lookupicon" ' ;
	strItemID = strItemID + 'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\');returnFocusToItemId();" src=\'/smcfs/console/icons/lookup.gif\'/>' ;
	var strROSSTrackable = '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ROSSTrackable" dataType="STRING">'; 
	var strProductClass = '<input type="hidden" name="xml:/Order/OrderLines/OrderLine_'+counter+'/Item/@ProductClass" dataType="STRING">'; 
	var strUOM = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:40px" name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@TransactionalUOM" dataType="STRING">'; 
	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:140px" value=""/>' ;
	//var strQtyRfi = '<td class="tablecolumn" style="width:120px"><A onclick="checkAvailableRFI(this)" title="Check Available RFI" href=""><label style="width:45px" value="abc" id="qtyRfi"/></A></td>' ;
	var strQtyRfi = '<td class="tablecolumn" style="width:120px"><A onclick="checkAvailableRFI(this)" href=""><INPUT type="text" class=protectedinput tabindex="-1" title="Currently Available RFI Quantity\rClick for details." name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnQtyRfi" dataType="STRING"/></A>';								
	var strQty = '<td class="numerictablecolumn"> <INPUT style="width:40px" onBlur="validateROSSTrackable(this)" tabindex="1" type="text" class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OrderedQty"/>';
	//var strQty = '<td class="numerictablecolumn"> <INPUT style="width:40px" tabindex="1" type="text" class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine_'+counter+'/OrderLineTranQuantity/@OrderedQty"/>';
	var strBackQty = '<td class="tablecolumn"><INPUT '+boDisabled+' onBlur="setBackOrderFlag(this);validateReqAndBOQty(this);" tabindex="1" class=unprotectedoverrideinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnBackorderedQty\' dataType="STRING" >';
	strBackQty = strBackQty + '<INPUT class=unprotectedoverrideinput type="hidden"  name="xml:/Order/OrderLines/OrderLine_' + counter + '/Extn/@ExtnBackOrderFlag"   value="" dataType="STRING" OldValue="" />';
	var strUTFQty = '<td class="numerictablecolumn"> <INPUT onBlur="validateReqAndBOQty(this)" style="width:40px" tabindex="1" type="text" class=unprotectedoverrideinput	name="xml:/Order/OrderLines/OrderLine_'+counter+'/Extn/@ExtnUTFQty"/>';
	var strDate = '<td class="tablecolumn" nowrap="true"> ' ;
	if (driverDate == "02") { 
		strDate = strDate + '<input type="text" tabindex="7" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/smcfs/console/icons/calendar.gif" />';
	} else { 
		strDate = strDate + '<input type="text" tabindex="7" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine_'+counter+'/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/smcfs/console/icons/calendar.gif"/>';
	} 
	var strNote = '<td> <INPUT class=unprotectedoverrideinput  tabindex="1" style=\'width:100px\' name="xml:/Order/OrderLines/OrderLine_'+counter+'/Notes/Note/@NoteText" dataType="STRING" OldValue=""/>' ;
	arr.push(requestNoCol) ;	
	arr.push(strItemID );
	arr.push(strUOM );
	arr.push(strDesc );
	arr.push(strQtyRfi );
	arr.push(strQty );
	arr.push(strBackQty );
	arr.push(strUTFQty);
	arr.push(strDate );
	arr.push(strNote );
	arr.push(strProductClass);	
	arr.push(strROSSTrackable );
	return arr;
}

function validateROSSTrackable (elem) {
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var issueQty = '';
	var boQty = '';
	var utfQty = '';
	var isTrackble = '';
	for (i = 0 ; i < InputList.length ; i++) {
		if(InputList[i].type == 'hidden' && InputList[i].name.indexOf('/Item/@ROSSTrackable') != -1) {
			isROSSTrackable = InputList[i];
		}
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1) {
			issueQty = InputList[i];
		}
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnBackorderedQty') != -1) {
			boQty = InputList[i];
		}
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnUTFQty') != -1) {
			utfQty = InputList[i];
		}
	}
	if (issueQty.value =='') {
		issueQty.value = '0';
	}
	if (boQty.value == '') {
		boQty.value = '0';
	}
	if (utfQty.value == '') {
		utfQty.value = '0';
	}
	if ((issueQty.value > 1) && (isROSSTrackable.value == 'Y')) {
		alert("Issue Qty must be 1 for ROSS Trackable Items !!!");
		issueQty.value = '1';
		boQty.value= '';
		utfQty.value = '';
        issueQty.focus();
	}
}

function validateReqAndBOQty (elem) {
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var issueQty = '';
	var boQty = '';
	var utfQty = '';
	var totQty = '';
	for (i = 0 ; i < InputList.length ; i++) {
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1) {
			issueQty = InputList[i];
		}
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnBackorderedQty') != -1) {
			boQty = InputList[i];
		}		
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnUTFQty') != -1) {
			utfQty = InputList[i];
		}
	}
	if (issueQty.value =='') {
		issueQty.value = '0';
	}
	if (boQty.value == '') {
		boQty.value = '0';
	}
	if (utfQty.value == '') {
		utfQty.value = '0';
	}	
	if ((issueQty.value == '0') && (boQty.value == '0') && (utfQty.value == '0')) {
		alert("Issue Qty or Backorder Qty or UTF Qty must be specified!");
		issueQty.value = '';
		boQty.value= '';
		utfQty.value = '';
		issueQty.focus();
	}	
	totQty=issueQty.value*1+utfQty.value*1+boQty.value*1;    
	if ((totQty > 1) && (isROSSTrackable.value == 'Y')) {
		alert("Issue Qty or Backorder Qty or UTF Qty must be 1 for ROSS Tracked Items!");
		issueQty.value = '';
		boQty.value= '';
		utfQty.value = '';
		issueQty.focus();
	}	
}

function itemBlurHandler(elem) {
	fetchDataWithParams(elem,'PopulateItemDetailsWithRFIQuantities',populateItemDetailsForSub,setParamLocal(elem));
}

function setParamLocal(ele,node) {
	var shipNode = '<%=getValue("OrderLine", "xml:/OrderLine/@ShipNode")%>';
	var returnArray = new Object();
	returnArray["xml:ShipNode"] = shipNode;
	returnArray["xml:ItemID"] = ele.value;
	return returnArray;
}

function populateItemDetailsForSub(elem,xmlDoc) {
	debugger;
	var nodes= xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strQty = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	var isSerialTracked = '';
    var strRossResourceItem = '';
	if(nodes!= null && nodes.length > 0 ) {	
		var item = nodes(0);
		strPC = item.getAttribute("ProductClass") ;
		strQty = item.getAttribute("AvailableQty") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strDesc = item.getAttribute("ShortDescription");
		isSerialTracked = item.getAttribute("IsSerialTracked");
		strRossResourceItem = item.getAttribute("ExtnRossResourceItem");
		if(strPC == null && strUOM == null) {
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
			elem.focus();
			elem.select();			
			return;
		}
		var documentType = '<%=resolveValue("xml:/OrderLine/Order/@DocumentType")%>';
		var orderType = '<%=resolveValue("xml:/OrderLine/Order/@OrderType")%>';
		if (documentType.value == '0001' && (orderType.value != 'Refurbishment')) {
			strPublishToROSS = item.getAttribute("ExtnPublishToRoss");
			if (strPublishToROSS == 'N'){
				alert('Item ' + elem.value + ' is not published to ROSS. Please enter an item that is published to ROSS');
				elem.focus();
				elem.select();
				return;
			}
			if (strRossResourceItem == 'Y') {
				strItemId = item.getAttribute("ItemID");
				alert('Invalid item id ' + elem.value + '. Please enter non ROSS tracked resource item');
				elem.focus();
				elem.select();
				return;
			}
		}
	}
	var currentRow = elem.parentNode.parentNode;
	var label = currentRow.getElementsByTagName("label");
	if(label != null && label != 'undefined' && label.length > 0 ) {
		label(0).innerText= strDesc;		
	}
	var InputList = currentRow.getElementsByTagName("Input");
	var clsForSerialTrackedItems = "unprotectedoverrideinput";
	var qtyForSerialTrackedItems = "";
	if ('Y' == strRossResourceItem) {
		qtyForSerialTrackedItems = "1";
	}
	for (i = 0 ; i < InputList.length ; i++) {
		if(InputList[i].name.indexOf('/Extn/@ExtnQtyRfi') != -1) {
			if (strQty.indexOf('.') != -1) {
				InputList[i].value = ""+strQty.substring(0, strQty.indexOf('.'));				
			} else {
				InputList[i].value = ""+strQty;				
			}
		}
		if(InputList[i].type == 'hidden' && InputList[i].name.indexOf('/Item/@ProductClass') != -1) {
			InputList[i].value = strPC;
		}
		if(InputList[i].type == 'hidden' && InputList[i].name.indexOf('/Item/@ROSSTrackable') != -1) {
			InputList[i].value = strRossResourceItem;
		}
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@TransactionalUOM') != -1) {
			InputList[i].value = strUOM;
		}
		if(InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1) {
			var myObject = InputList[i];
			InputList[i].value = qtyForSerialTrackedItems;
		}
	}
}

function validateSubstitutionItemSubstitutionInput() {
	var InputList = document.getElementsByTagName("Input");
	var missingRequiredFieldsFound = 0;
	var clientSideSysNo = '';
	var sysNoRequired= 0;
	for (i = 0 ; i < InputList.length ; i++) {
		if (InputList[i].name.indexOf('EntityKey') != -1) {
			if (InputList[i].value.indexOf('strItemID') != -1)
				if (InputList[i].value.indexOf('004390') != -1)
					sysNoRequired = 1;				
		} else if (InputList[i].name.indexOf('/Extn/@ExtnBackorderedQty') != -1) {
			if (InputList[i].value == null || InputList[i].value == 'undefined' || InputList[i].value == '') {
				InputList[i].value = 0;
			} else {
				//Backorder qty has a value, if its on a radio kit item pop up an alert saying that BO is not alllowed for radio kit items
				if (sysNoRequired) {
					if (InputList[i].value != 0 || InputList[i].value == '') {
						alert("Backorder quantity not allowed for Radio Kit Items!");
						InputList[i].value = 0;
					}
				}
			}	
		} else if ((InputList[i].name.indexOf('/Extn/@ExtnRequestNo') != -1) || (InputList[i].name.indexOf('/Item/@ItemID') != -1) || (InputList[i].name.indexOf('/OrderLineTranQuantity/@TransactionalUOM') != -1) || (InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1) || (InputList[i].name.indexOf('/@ReqShipDate') != -1) || (InputList[i].name.indexOf('@ExtnSystemNo') != -1)) {		
			if (InputList[i].name.indexOf('@ExtnSystemNo') != -1) {
				// The following RegEx matches for non-zero length alphanumeric values
				var regExp = /^[0-9a-zA-Z]+$/i;
				var clientSideSysNo = InputList[i].value;
				// System Number entry REQUIRED! Validation done to ensure that the entry is alpha numeric.
				if (sysNoRequired == 1) {
					if (!regExp.test(clientSideSysNo)) {
						setMandatorySubstitutionField (InputList[i]);
						alert('System Number must be specified for the 004390 Radio Kit Item and it must be alphanumeric!');
						missingRequiredFieldsFound = 1;
						InputList[i].focus();
						InputList[i].select();
					}
				} else {
					// System Number entry not required, validation done though if one is entered for a non 4390 item.
					if (!checkForNull(clientSideSysNo) == '') {
						if (!regExp.test(clientSideSysNo)) {
							alert('System Number, if specified, must be alphanumeric!');
							setMandatorySubstitutionField (InputList[i]);
							missingRequiredFieldsFound = 1;
							InputList[i].focus();
							InputList[i].select();
						}
					}
				}
			} else if (InputList[i].value == null || InputList[i].value == 'undefined' || InputList[i].value == '') {
				alert('Please enter all required fields denoted with a red star!');
				setMandatorySubstitutionField (InputList[i]);
				missingRequiredFieldsFound = 1;
				InputList[i].focus();
				InputList[i].select();
			} else {
				unsetMandatorySubstitutionField (InputList[i]);
			}
		}
	}
	if (missingRequiredFieldsFound == 1) {		
		return 0;
	} else {
		return 1;
	}
}

function setMandatorySubstitutionField(obj) {
	if(eval(obj)) {
		if (obj.parentNode != null) {
			var objHtml = new String(obj.parentNode.innerHTML) ;
			if(eval(objHtml) && objHtml.indexOf("<FONT") == -1 ) {
				obj.parentNode.innerHTML = obj.parentNode.innerHTML + '<FONT color="red">*</FONT>' ;
			}
		}	
	}
}

function handleEnterOnSubPopUp(inField, e) {
    var charCode;
    if(e && e.which)
    {
        charCode = e.which;
    }
    else if(window.event)
    {
        e = window.event;
        charCode = e.keyCode;
    }
    if(charCode == 13) 
    {
		if (inField.id == "rowCount")
        	addMultipleRows();    
        else (inField.id == "ItemSubSysNoEntry")
        {
       		if(yfcFormHasChanged() == true )
			{
				invokeSave();
				return 1;
			}
			else
				return 0;
		}
	}
}
	
function invokeSave() {
	var elem = document.getElementsByTagName("Input");
	for(i=0;i < elem.length;i++) {
		if(elem[i].type == 'button' && elem[i].value =='Save') {
			yfcCallSave(elem[i]);
		}
	}
}
	
function unsetMandatorySubstitutionField(obj) {
	if (obj.parentNode != null) {
		var str = new String(obj.parentNode.innerHTML) ;
		if(eval(obj) && str.indexOf('<FONT') != -1 )
			obj.parentNode.innerHTML = obj.parentNode.innerHTML.substring(0,str.indexOf('<FONT'));		
	}
}

function onkeypressIncidentI01Panel(inField, e) {
    var charCode;
    if(e && e.which) {
        charCode = e.which;
    } else if(window.event) {
        e = window.event;
        charCode = e.keyCode;
    }
    if(charCode == 13) {
		if(yfcFormHasChanged() == true ) {
			invokeSave();
			return 1;
		} else {
			return 0;
		}
	}
}
</script>