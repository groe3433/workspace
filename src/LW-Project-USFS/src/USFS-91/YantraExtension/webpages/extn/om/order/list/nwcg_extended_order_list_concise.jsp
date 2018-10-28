<%@taglib prefix="yfc" uri="/WEB-INF/yfc.tld"%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
	function checkForIncidents() {
		var strIncidentNo= "" ;
		// get all the inputs
		var inputList = document.getElementsByTagName("input");
		if(inputList != null) {
			for(i=0;i < inputList.length;i++) {
				// get all the checkboxes which are checked, except the header checkbox which is select all 
				if(inputList[i].type == 'checkbox' && inputList[i].name != 'checkbox' && inputList[i].checked == true) {	
					var elem = inputList[i];
					var tableRow = elem.parentNode.parentNode;
					// finding the hidden variable which is having the incident number
					var elemInc = tableRow.getElementsByTagName("input");
					if(elemInc != null) {
						for(j = 0 ; j < elemInc.length;j++) {
							if(elemInc[j].name == 'xml:/Order/Extn/@ExtnIncidentNo') {
								// get the incident number's value
								if(strIncidentNo == '') {
									strIncidentNo = elemInc[j].value ;
								} else if(strIncidentNo !=  elemInc[j].value) {
									// if the values are not same, raise an alert
									alert('Please make sure all Issues belongs to same Incident');
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

    function showAssignIncidentPopup(viewId) {
		// check if all the issues belongs to the same incident or no
		var bReturn = checkForIncidents();
		if(bReturn == false) {
			return false;
		}
        var myObject = new Object();
		myObject.currentWindow = window;
		yfcShowDetailPopup(viewId, "", 480,200, myObject,'order','<Order/>');
		var retVal = myObject["OMReturnValue"];
		var returnValue = myObject["OKClicked"];
		if ( "YES" == returnValue ) {
			return retVal;
		} else {
			return false;
		}
    }
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
	<thead>
		<tr>
			<td sortable="no" class="checkboxheader">
				<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>' /> 
				<input type="hidden" name="xml:/Order/@Override" value="N" /> 
				<input type="hidden" name="ResetDetailPageDocumentType" value="Y" /> 
				<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);" />
			</td>
			<td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Cache_ID</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Other_Order_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Order_Date</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:/OrderList/@Order" id="Order">
			<tr>
				<yfc:makeXMLInput name="orderKey">
					<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
					<yfc:makeXMLKey binding="xml:/Order/Extn/@ExtnLastIncidentNo1" value="xml:/Order/Extn/@ExtnLastIncidentNo1" />
					<yfc:makeXMLKey binding="xml:/Order/Extn/@ExtnLastIncidentNo2" value="xml:/Order/Extn/@ExtnLastIncidentNo2" />
					<yfc:makeXMLKey binding="xml:/Order/Extn/@ExtnIncidentNo" value="xml:/Order/Extn/@ExtnIncidentNo" />
					<yfc:makeXMLKey binding="xml:/Order/Extn/@ExtnIncidentYear" value="xml:/Order/Extn/@ExtnIncidentYear" />
					<yfc:makeXMLKey binding="xml:/Order/@Status" value="xml:/Order/@Status" />
				</yfc:makeXMLInput>
				<yfc:makeXMLInput name="issuePrintKey">
					<yfc:makeXMLKey binding="xml:/Print/IOrder/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
				</yfc:makeXMLInput>
				<td class="checkboxcolumn">
					<input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" PrintEntityKey='<%=getParameter("issuePrintKey")%>' />
				</td>
				<td class="tablecolumn">
					<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');"><yfc:getXMLValue binding="xml:/Order/@OrderNo" /></a>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@ShipNode" />
				</td>
				<td class="tablecolumn">
<%
					if (isVoid(getValue("Order", "xml:/Order/@Status"))) {
%> 
						[<yfc:i18n>Draft</yfc:i18n>]
<%
					} else {
%> 					
						<!--<%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%> -->
<%
						if (equals("Partially Shipped", getValue("Order", "xml:/Order/@MaxOrderStatusDesc"))) {
%>
							<%=displayOrderStatus("N", getValue("Order", "xml:/Order/@MaxOrderStatusDesc"))%>
<%
						} else {
%>
 							<%=displayOrderStatus(getValue("Order", "xml:/Order/@MultipleStatusesExist"), getValue("Order", "xml:/Order/@MaxOrderStatusDesc"))%>
<%
						}
 					}
 					if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) {
%>
						<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%> />
<%
					}
 					if (equals("Y", getValue("Order", "xml:/Order/@isHistory"))) {
%>
						<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%> />
<%
					}
%>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@EnterpriseCode" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Order/@BillToID" />
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:/Order/@OrderDate")%>">
					<yfc:getXMLValue binding="xml:/Order/@OrderDate" />
				</td>
				<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/Order/PriceInfo/@TotalAmount")%>">
					<%=displayAmount(getValue("Order", "xml:/Order/PriceInfo/@TotalAmount"), (YFCElement) request.getAttribute("CurrencyList"), getValue("Order", "xml:/Order/PriceInfo/@Currency"))%>
				</td>
			</tr>
		</yfc:loopXML>
	</tbody>
</table>