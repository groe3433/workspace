<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
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
<table class="anchor" cellpadding="7px"  cellSpacing="0">
<%
YFCElement elemShip = (YFCElement) request.getAttribute("MoveRequest");
if(elemShip != null){
    if (!(equals("CREATED",elemShip.getAttribute("Status")))) {
		elemShip.setAttribute("ConfirmFlag", false);
   	}
	else { 
        elemShip.setAttribute("ConfirmFlag", true);
    }
   if (equals("CLOSED",elemShip.getAttribute("Status"))) {
		elemShip.setAttribute("ConfirmStatus", false);
   }
	else { 
        elemShip.setAttribute("ConfirmStatus", true);
    }

	elemShip.setAttribute("EnableShipment",false);
	if(!isVoid(resolveValue("xml:/MoveRequest/@ShipmentKey"))){
		elemShip.setAttribute("EnableShipment",true);
	}   
}
YFCElement taskList = (YFCElement) request.getAttribute("MoveRequestTaskList");
String numberOfTasks = taskList.getAttribute("TotalNumberOfRecords");
elemShip.setAttribute("HasTasks", false);
if(!(equals("0", numberOfTasks))){
	elemShip.setAttribute("HasTasks", true);
}
%>

 <tr height="70%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>


        </jsp:include>
    </td>
</tr>
<tr height="30%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
 <tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
 </tr>
 <tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
        </jsp:include>
    </td>
 </tr>
</table>