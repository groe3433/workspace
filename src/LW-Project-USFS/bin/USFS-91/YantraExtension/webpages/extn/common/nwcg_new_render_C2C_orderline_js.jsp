<!-- /extn/common/render_orderline_js.jsp --> 
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@include file="/yfc/util.jspf" %>
<%
String strMax = "50";



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
	// get the current page number
	var pageNumber = document.getElementById("CurrentPageNumber");
	// get the order line tag from output xml
	var nodes = xmlDoc.getElementsByTagName("OrderLine");
	var strOnLoad = elem.getAttribute("ON_LOAD");
	elem.setAttribute("ON_LOAD","");
	if(nodes!=null && nodes.length > 0 )
	{
		var temp = pageNumber.value * 1; // to convert this to an int
		// if user has clicked on next
		if(strOnLoad != 'true')
		{
			if(elem.value == 'Next')
			{
				pageNumber.value =  temp + 1 ;
			}
			else if(elem.value == 'Previous')// assumming the user has clicked on previous
			{
				pageNumber.value =  temp - 1 ;
			}
		}
		// clearing the existing table
		clearTableData();
		for(i = 0 ; i < nodes.length  ; i++)
		{
			var orderLine = nodes(i);
			var strImage = "src='/smcfs/console/icons/calendar.gif' alt='Calendar'";
			// this is a prepare a row in the table
			var tableRow = prepareTableRow(orderLine,(i+1),'<%=resolveValue("xml:/Order/@DocumentType")%>','<%=resolveValue("xml:/Order/@DriverDate")%>','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=resolveValue("xml:/CurrencyList/Currency/@PrefixSymbol")%>','<%=resolveValue("xml:/CurrencyList/Currency/@PostfixSymbol")%>',strImage);
			// display this newly created row in the table
			//populateTable(tableRow);
			populateSortableTable(tableRow);
		}
	}
	else // no lines returned
	{
		if(strOnLoad != 'true')
		{
			// if user has clicked on next
			if(elem.value == 'Next')
			{
				var objNext = document.getElementById("Next");
				objNext.disabled = true ;
				alert('Last record reached');
			}
			// if the user has clicked on previous
			else if(elem.value == 'Previous')
			{
				var objNext = document.getElementById("Previous");
				objNext.disabled = true ;
				alert('First record reached');
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
	
	var objNext = document.getElementById("Next");
	objNext.setAttribute("ON_LOAD","true");
	//fetchDataWithParams(objNext,'getOrderLineList',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=strMax%>'));
	fetchDataWithParams(objNext,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam('','<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=strMax%>'));
	//addBlankRows();
}

function addBlankRows(element)
{
	var totalRows = '<%=strMax%>' * 1 ;
	for(i = 0 ; i < totalRows ; i++)
	{
		var row = populateBlankRow('<%=strDoc%>','<%=driverDate%>','<%=strPC%>','<%=strUOM%>',i);
		//populateTable(row);
		populateSortableTable(row);
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

function populateSortableTable(tableRow)
{
	var tbl = document.getElementById("OrderLines");
	var lastRow = tbl.rows.length - 1;
	var row = tbl.insertRow(lastRow);
	
	for (r = 0; r < tableRow.length; r++) 
	{   
         var cell = row.insertCell(r);
		 
		 try
		 {
			 if(tableRow[r] instanceof Object)
			 {
				//standard attributes
				if(tableRow[r].text) cell.innerHTML = tableRow[r].text;
				if(tableRow[r].className) cell.className = tableRow[r].className;
				if(tableRow[r].align) cell.align = tableRow[r].align;
				if(tableRow[r].style) cell.style = tableRow[r].style;
				if(tableRow[r].nowrap) cell.nowrap = tableRow[r].nowrap;
				
				//custom attributes
				if(tableRow[r].sortValue) cell.sortValue = tableRow[r].sortValue;
			 }
			 else
			 {
				cell.innerHTML = tableRow[r];
			 }
                          <!-- Begin CR817 -->
                          cell.style.fontSize = "11px";
                          <!-- End CR817 -->

		 }
		 catch(ex)
		 {
			//alert('Exception thrown: ' + ex.message);
		 }
		 //if(r==3) alert('tableRow:\n\n' + tableRow[r] + '\n\ncell.innerHTML:\n\n' + cell.innerHTML + '\n\ncell.sortValue: ' + cell.sortValue);		 
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
function verifyPageNumberAndFetchData(elem,orderHeaderKey,strMax)
{
	var pageNumber = document.getElementById("CurrentPageNumber");
	var obj = document.getElementById("TotalPageNumber") ;

	var strPageNo = pageNumber.value;

	if(eval(obj) && (strPageNo * 1 ) > (obj.value * 1 ))
	{
		alert("Page #"+strPageNo+" doesn't exists ");
		return false;
	}
	//fetchDataWithParams(elem,'getOrderLineList',populateOrderLines,setOrderLineParam(elem,orderHeaderKey,strMax));
	fetchDataWithParams(elem,'getOrderLineListWithRFI',populateOrderLines,setOrderLineParam(elem,orderHeaderKey,strMax));
	
}
// this is a generic function to set the parameters before we invoke the data from server
function setOrderLineParam(elem,orderHeaderKey,strMax)
{
	//invokeSave();
	var returnArray = new Object();
	var pageNumber = document.getElementById("CurrentPageNumber");
	

	var strPageNo = pageNumber.value;

	// if user has clicked on the next button
	if(elem.value == 'Next')
	{
		var objNext = document.getElementById("Previous");
		objNext.disabled = false ;
		// increase the page number - just to pass on the value
		// the textbox will display the value only when the call back function is invoked
		strPageNo = (strPageNo * 1 ) + 1;
	
	}
	// if the user has clicked on previous button
	else if(elem.value == 'Previous')
	{
		var objNext = document.getElementById("Next");
		objNext.disabled = false;
		// similarly reduce the page number by one when the user hits on previous button
		strPageNo = (strPageNo * 1 ) - 1;
	}
	// calculating the to and from page numbers
	var strTo = strPageNo * strMax ;
	var strFrom = strTo - (strMax - 1);
	returnArray["xml:/OrderHeaderKey"] = orderHeaderKey ;
	returnArray["xml:/FromPrimeLineNo"] = strFrom ;
	returnArray["xml:/ToPrimeLineNo"] = strTo ;
	
//	alert(strFrom + "  " + strTo);
	
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
</script>

<!-- End /extn/common/render_orderline_js.jsp --> 