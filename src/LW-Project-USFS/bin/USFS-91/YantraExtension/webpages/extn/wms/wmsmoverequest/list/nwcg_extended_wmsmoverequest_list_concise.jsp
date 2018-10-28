<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<script language="javascript">
function enterActionModificationReason(modReasonViewID, modReasonCodeBinding, modReasonTextBinding,screenType,key) {

    var myObject = new Object();
    myObject.currentWindow = window;
    myObject.reasonCodeInput = document.all(modReasonCodeBinding);
    myObject.reasonTextInput = document.all(modReasonTextBinding);       
	
		
	if(screenType=='LIST'){
		<%if(isShipNodeUser()){%>
		yfcShowDetailPopupWithKeys(modReasonViewID,"","550","255",myObject,key,"wmsmoverequest", "") ;
		<%}else{%>
			if(isMultipleRecordsSelected(key)){
				alert('<%=getI18N("Node_User_Can_Only_Cancel_Multiple_Requests_Select_Only_one_Record")%>');
				return false;
			}else{
				yfcShowDetailPopupWithKeys(modReasonViewID,"","550","255",myObject,key,"wmsmoverequest", "") ;
			}
		<%}%>
	}else{
		yfcShowDetailPopupWithKeys(modReasonViewID, "", "550", "255", myObject, key,"wmsmoverequest", "");
	}

	var retVal = myObject["EMReturnValue"];
    var returnValue = myObject["OKClicked"];
    if ( "YES" == returnValue ) {
        return retVal;
    } else {
        return false;
    }
}

function chkForCorrectMoveRequest(entityKey,modReasonViewID, modReasonCodeBinding, modReasonTextBinding){
	var eleArray = document.forms["containerform"].elements;
	for ( var i =0; i < eleArray.length; i++ ) {
		if ( eleArray[i].name == "EntityKey") {
			if (eleArray[i].checked) {
				var moveRequestStatus = eleArray[i].moverequestStatus;
				if(moveRequestStatus == 'CLOSED'){
					alert(YFCMSG039);	//alert("Please select open MoveRequest(s) to cancel."); 
					return false;
				}
			}
		}
	}
	return enterActionModificationReason(modReasonViewID, modReasonCodeBinding, modReasonTextBinding,'LIST',entityKey);		
}
</script>
<%
	YFCElement oMoveRequests = (YFCElement) request.getAttribute("MoveRequests");
	if (oMoveRequests != null) {
		oMoveRequests.setAttribute("ForHasExceptions","Y");
	}
%>
<table class="table" border="0" cellspacing="0" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Move_Request_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Shipment_No</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Activity_Group</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>For_Activity_Code</yfc:i18n>
        </td>
        <td class="tablecolumnheader"  nowrap="true" >
            <yfc:i18n>Priority</yfc:i18n>
        </td>
		<td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Requesting_User_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader"  nowrap="true" >
            <yfc:i18n>Has_Exceptions</yfc:i18n>
        </td>		
		<td class="tablecolumnheader">
            <yfc:i18n>Start_No_Earlier_Than</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Status</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/MoveRequests/@MoveRequest" id="MoveRequest"> 
    <tr> 
        <yfc:makeXMLInput name="MoveRequestKey">
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/MoveRequest/@MoveRequestKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestNo" value="xml:/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/Shipment/@ShipmentNo" value="xml:/MoveRequest/Shipment/@ShipmentNo" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@FromActivityGroup" value="xml:/MoveRequest/@FromActivityGroup" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Priority" value="xml:/MoveRequest/@Priority" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@RequestUserId" value="xml:/MoveRequest/@RequestUserId" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@HasExceptions" value="xml:/MoveRequest/@HasExceptions" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Status" value="xml:/MoveRequest/@Status" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Node" value="xml:/MoveRequest/@Node" />
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="MoveRequestForExceptionKey">
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/MoveRequest/@MoveRequestKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestNo" value="xml:/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/Shipment/@ShipmentNo" value="xml:/MoveRequest/Shipment/@ShipmentNo" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@FromActivityGroup" value="xml:/MoveRequest/@FromActivityGroup" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Priority" value="xml:/MoveRequest/@Priority" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@RequestUserId" value="xml:/MoveRequest/@RequestUserId" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@HasExceptions" value="xml:/MoveRequest/@HasExceptions" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Status" value="xml:/MoveRequest/@Status" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Node" value="xml:/MoveRequest/@Node" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@ListLinesWithException" value="xml:/MoveRequests/@ForHasExceptions" />
        </yfc:makeXMLInput>
        <td class="checkboxcolumn">
            <input type="checkbox" value='<%=getParameter("MoveRequestForExceptionKey")%>' name="EntityKey" MoveRequestNo='<%=resolveValue("xml:/MoveRequest/@MoveRequestNo")%>' moverequestStatus='<%=getValue("MoveRequest","xml:/MoveRequest/@Status")%>'
			moveRequestWithoutException='<%=resolveValue("MoveRequestKey")%>'/>
		</td>
        <td class="tablecolumn">
			<a href="javascript:showDetailFor('<%=getParameter("MoveRequestForExceptionKey")%>');"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/@MoveRequestNo"/></a>
        </td>
		<td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/Shipment/@ShipmentNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/FromActivityGroup/@Description"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/ForActivityCode/@Description"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/Priority/@Description"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/@RequestUserId"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/@HasExceptions"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="MoveRequest" binding="xml:/MoveRequest/@StartNoEarlierThan"/></td>
		<td class="tablecolumn"><yfc:getXMLValueI18NDB name="MoveRequest" binding="xml:/MoveRequest/Status/@Description"/></td>				
    </tr>
    </yfc:loopXML> 
		<input type="hidden" name="xml:/Cancel/MoveRequest/@ReasonCode" />
        <input type="hidden" name="xml:/Cancel/MoveRequest/@ReasonText"/>
</tbody>
</table>