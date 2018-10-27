<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

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
							if(elemInc[j].name == 'xml:/NWCGIncidentOrder/@IncidentNo') {
								// get the incident number's value
								if(strIncidentNo == '') {
									strIncidentNo = elemInc[j].value ;
								}
								// if the values are not same, raise an alert
								else if(strIncidentNo !=  elemInc[j].value) {
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
				return false ;
			}
            var myObject = new Object();
		    myObject.currentWindow = window;
			yfcShowDetailPopup(viewId, "", 480,200, myObject,'order','<NWCGIncidentOrderList/>');
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
			<td class="tablecolumnheader" sortable="no">&nbsp;</td>
			<td class="tablecolumnheader"><yfc:i18n>Other_Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Other_Order_Type</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Customer_PO_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Customer_Id</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:/NWCGIncidentOrderList/@NWCGIncidentOrder" id="NWCGIncidentOrder">
		<tr>
			<td class="tablecolumn"><img class="icon" onClick="setOtherLookupValue('<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>')" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> /></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentNo"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentName"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@OtherOrderType"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@CustomerPONo"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@CustomerId"/></td>
		</tr>
		</yfc:loopXML>
	</tbody>
</table>