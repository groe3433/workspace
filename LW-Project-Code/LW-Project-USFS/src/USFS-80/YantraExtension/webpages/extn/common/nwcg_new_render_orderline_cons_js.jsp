<!-- /extn/common/nwcg_new_render_orderline_cons_js.jsp --> 
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">
<%
	String nextReqNo = "";
	String sHiddenDraftOrderFlag = getValue("SOL", "xml:/OrderLine/Order/@DraftOrderFlag");
	String shipNode = getValue("SOL", "xml:/OrderLineList/OrderLine/@ShipNode");

	String incidentNo = "";
	String incidentYear = "";
	String strPC = "";
	String extraParams = "";	
%>
// Display the selected order lines from the order lines inner panel
// of the issue details screen and paint 1 blank order line
function window.onload()
{
	if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
		return;
	}
	populateSingleRow();
    selectFirstRadioButton();
}

function populateSingleRow () {
	
	var newRow = populateBlankRowForConsolidationUI();	
	populateTable(newRow);	
	document.getElementById("itemid").focus();
}

// this function populates the table with the ONE ROW at a time
function populateTable(tableRow)
{
	var tbl = document.getElementById("OrderLineToCreate");
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
	var tbl = document.getElementById("OrderLineToCreate");
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
    populateSingleRow();
    document.getElementById("itemid").focus();
    selectFirstRadioButton();
}

function selectFirstRadioButton() {
	var orderLinesTable = document.getElementById("OrderLines");
	var InputList = orderLinesTable.getElementsByTagName("Input");
	for (var j = 0; j < InputList.length; j++) 
	{
		if(InputList[j].name.indexOf('RadioSelectedLine') != -1){
			var firstbutton = InputList[j];
			firstbutton.checked = true;
			firstbutton.fireEvent("onclick");			
			break;
		}
	}
}

function setRequestedDateAndRequestedQty(elem)
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
		}		
	}
}

function populateBlankRowForConsolidationUI()
{
	var rowCellsArray = new Array();
	var bOtherIssues = true ;
	var documentType = '<%=getValue("SOL", "xml:/OrderLineList/OrderLine/Order/@DocumentType")%>';
	var driverDate = '<%=getValue("SOL", "xml:/OrderLineList/OrderLine/Order/@DriverDate")%>';
	if(eval(documentType) && documentType == '0001')
		bOtherIssues = false ;
	var requestNoCol= '';

	//No validation done on request number column for consolidation
	requestNoCol = '<td><INPUT class=unprotectedoverrideinput name="xml:/Order/OrderLines/OrderLine/Extn/@ExtnRequestNo" dataType="STRING" maxLength=11 size=10  value=""/>'; 

	var strItemID = '<td class="tablecolumn" style="width:50px" nowrap="true"> <input class=unprotectedoverrideinput id="itemid" type="text" style="width:40px" dataType="STRING" name="xml:/Order/OrderLines/OrderLine/Item/@ItemID" ';
	strItemID = strItemID + 'onBlur="itemBlurHandler(this);setRequestedDateAndRequestedQty(this)" tabindex="1"/> <img class="lookupicon" ' ;
	strItemID= strItemID +  'onclick="templateRowCallItemLookup(this,\'ItemID\',\'ProductClass\',\'TransactionalUOM\',\'item\',\'<%=extraParams%>\');returnFocusToItemId();" src=\'/yantra/console/icons/lookup.gif\'/>' ;
	
	var strProductClass = '<input type="hidden" name="xml:/Order/OrderLines/OrderLine/Item/@ProductClass" dataType="STRING">'; 
	var strUOM = '<td class="tablecolumn"><INPUT class=unprotectedoverrideinput readonly="true" tabindex="-1" style="width:40px" name="xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@TransactionalUOM" dataType="STRING">'; 
	var strDesc = '<td class="tablecolumn" style="width:120px"><label style="width:140px" value=""/>';
	
	//var strQtyRfi = '<td class="tablecolumn" style="width:120px"><A onclick="checkAvailableRFI(this)" title="Check Available RFI" href=""><label style="width:45px" value="abc" id="qtyRfi"/></A></td>' ;
	var strQtyRfi = '<td class="tablecolumn" style="width:120px">' +
							'<A onclick="checkAvailableRFI(this)" href=""><INPUT type="text" class=protectedinput tabindex="-1" title="Currently Available RFI Quantity\rClick for details." name="xml:/Order/OrderLines/OrderLine/Extn/@ExtnQtyRfi" dataType="STRING"/></A>';	
							
	var strQty = '<td class="numerictablecolumn"> <INPUT style="width:40px" tabindex="2" type="text" class=unprotectedoverrideinput	name="xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@OrderedQty"/>';

	var strBackQty = '<td class="tablecolumn"><INPUT onBlur="validateReqAndBOQty(this);setBackOrderFlag(this)" tabindex="5" class=unprotectedoverrideinput style=\'width:40px\' name=\'xml:/Order/OrderLines/OrderLine/Extn/@ExtnBackorderedQty\' dataType="STRING" >';
	strBackQty = strBackQty + '<INPUT class=unprotectedoverrideinput type="hidden"  name="xml:/Order/OrderLines/OrderLine/Extn/@ExtnBackOrderFlag"  value="" dataType="STRING" OldValue="" />';
	
	var strDate = '<td class="tablecolumn" nowrap="true">';
	if (driverDate == "02") 
	{ 
		strDate = strDate + '<input type="text" tabindex="7" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine/@ReqDeliveryDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif" />' ;
	} 
	else 
	{ 
		strDate = strDate + '<input type="text" tabindex="7" OldValue="" style=\'width:60px\' class=\'unprotectedoverrideinput\'  name="xml:/Order/OrderLines/OrderLine/@ReqShipDate"/> <img class="lookupicon" onclick="invokeCalendar(this)" src="/yantra/console/icons/calendar.gif"/> ' ;
	} 

	var strNote = '<td> <INPUT class=unprotectedoverrideinput  tabindex="8" style=\'width:100px\' name="xml:/Order/OrderLines/OrderLine/Notes/Note/@NoteText" dataType="STRING" OldValue=""/>';

	rowCellsArray.push(requestNoCol) ;	
	rowCellsArray.push(strItemID );
	rowCellsArray.push(strUOM );
	rowCellsArray.push(strDesc );
	rowCellsArray.push(strQtyRfi );
	rowCellsArray.push(strQty );
	rowCellsArray.push(strBackQty );
	rowCellsArray.push(strDate );
	rowCellsArray.push(strNote );
	rowCellsArray.push(strProductClass);	
	return rowCellsArray;
}

function validateReqAndBOQty (elem) 
{
	var currentRow = elem.parentNode.parentNode;
	var InputList = currentRow.getElementsByTagName("Input");
	var issueQty = '';
	var boQty = '';
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@OrderedQty') != -1)
		{
			issueQty = InputList[i];
		}//end if
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/Extn/@ExtnBackorderedQty') != -1)
		{
			boQty = InputList[i];
		}//end if		
	}

	if ((issueQty.value == ''  && boQty.value == '') ||
		(issueQty.value == '0' && boQty.value == '0')) {
		alert("Issue Qty or Backorder Qty must be specified!");
		issueQty.value = '';
		boQty.value= '';
		issueQty.focus();
	}	
}

function radioBtnClick(obj) {
	var tableRow = obj.parentNode.parentNode;
	var rowCells = tableRow.getElementsByTagName("TD");
	var requestNo = '';
	for (var i = 0 ; i < rowCells.length; i++) {
		if (rowCells[i].id == 'RequestNo') {
			requestNo = rowCells[i].innerHTML;		
			break;			
		}
	}
	
	//Append a .1 to the end of the chosen Request Number
	requestNo = requestNo + '.1';
	
	var tblLineToCreate = document.getElementById("OrderLineToCreate");
	var InputList = tblLineToCreate.getElementsByTagName("Input");
	for (var j = 0; j < InputList.length; j++) {
		if(InputList[j].name.indexOf('/@ExtnRequestNo') != -1) {
			InputList[j].value = requestNo;
		}
		else if (InputList[j].name.indexOf('/@ItemID') != -1) {
			InputList[j].focus();					
		}
	}
}

function itemBlurHandler(elem) {
	var itemId = checkStringForNull(elem.value);
	if (itemId == '')
		return;
	fetchDataWithParams(elem,'PopulateItemDetails',populateItemDetailsForCons,setParamLocal(elem));
}

function setParamLocal(ele)
{
	var shipNode = '<%=shipNode%>';
	var returnArray = new Object();
	returnArray["xml:ShipNode"] = shipNode;
	returnArray["xml:ItemID"] = ele.value;
	return returnArray;
}

function populateItemDetailsForCons(elem,xmlDoc)
{
	var nodes= xmlDoc.getElementsByTagName("Item");
	var strPC = '' ;
	var strQty = '' ;
	var strUOM = '' ;
	var strDesc = '' ;
	if(nodes!= null && nodes.length > 0 )
	{	
		var item = nodes(0);
		strPC = item.getAttribute("ProductClass") ;
		strQty = item.getAttribute("AvailableQty") ;
		strUOM = item.getAttribute("UnitOfMeasure");
		strDesc = item.getAttribute("ShortDescription");
		if(strPC == null && strUOM == null)
		{
			strPC = "" ;
			strUOM = "" ;
			alert('Item ( '+elem.value+' ) does not exists or not published');
			var itemidbox = document.getElementById("itemid");
			itemidbox.value = '';
			itemidbox.focus();			
		}
		
		var documentType = '<%=resolveValue("xml:/OrderLine/Order/@DocumentType")%>';
		var orderType = '<%=resolveValue("xml:/OrderLine/Order/@OrderType")%>';

		if (documentType.value == '0001' && (orderType.value != 'Refurbishment'))
		{
			strPublishToROSS = item.getAttribute("ExtnPublishToRoss");
			if (strPublishToROSS == 'N'){
				alert('Item ' + elem.value + ' is not published to ROSS. Please enter an item that is published to ROSS');
				elem.focus();
				return;
			}
			
			strRossResourceItem = item.getAttribute("ExtnRossResourceItem");
			if (strRossResourceItem == 'Y'){
				strItemId = item.getAttribute("ItemID");
				alert('Invalid item id ' + elem.value + '. Please enter non ROSS tracked resource item');
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
	for (i = 0 ; i < InputList.length ; i++)
	{
		if(InputList[i].name.indexOf('/Extn/@ExtnQtyRfi') != -1)
		{
			if (strQty.indexOf('.') != -1){
				InputList[i].value = ""+strQty.substring(0, strQty.indexOf('.'));
			}
			else {
				InputList[i].value = ""+strQty;
			}
		}//end if
	
		if(InputList[i].type == 'hidden' && InputList[i].name.indexOf('/Item/@ProductClass') != -1)
		{
			InputList[i].value = strPC;
		}//end if
		if(InputList[i].type == 'text' && InputList[i].name.indexOf('/OrderLineTranQuantity/@TransactionalUOM') != -1)
		{
			InputList[i].value = strUOM;
		}
	}
}
</script>
<!-- End /extn/common/nwcg_new_render_orderline_cons_js.jsp --> 