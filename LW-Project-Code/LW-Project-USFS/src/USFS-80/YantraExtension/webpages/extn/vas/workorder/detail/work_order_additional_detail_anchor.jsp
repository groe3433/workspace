<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript">
	yfcDoNotPromptForChanges(true);
</script>
<script language="javascript">
    function setClickedAttribute() {
		var parentWindow = window.dialogArguments.parentWindow;

		if (document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"].value = document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"].value = document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"].value = document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"].value = document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"].value = document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"].value = document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"].value = document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"].value;
		}
		if (document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"] != null) {
			parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"].value = document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"].value;
		}
		
		var eleArray = document.forms["containerform"].elements;
		var str = "";
		var flag = false;
		var foundChk = false;
		for ( var i =0; i < eleArray.length; i++ ) {
			str  = eleArray[i].name;
			//alert("eleArray[i].name"+str);
			if(str != null && str.indexOf('WorkOrderTag/Extn') != -1){
				if (document.all[str] != null) {
					parentWindow.document.all[str].value = document.all[str].value
				}			
			}		
		}
    }
</script>
<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
				<jsp:param name="ModifyView" value="false"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
        <td align=center colspan=2>
            <input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setClickedAttribute();window.close();"/>
        <td>
    <tr>
</table>
<script language="javascript">
	var parentWindow = window.dialogArguments.parentWindow;

	if (document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@BatchNo"].value
	}

	if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute1"].value
	}

	if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute2"].value
	}
	if (document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotAttribute3"].value
	}
	if (document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotExpirationDate"].value
	}
	if (document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@LotNumber"].value
	}
	if (document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@ManufacturingDate"].value
	}
	if (document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"] != null) {
		document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"].value = parentWindow.document.all["xml:/WorkOrder/WorkOrderTag/@RevisionNo"].value
	}


   
    var eleArray = document.forms["containerform"].elements;
    var str = "";
    var flag = false;
    var foundChk = false;
    for ( var i =0; i < eleArray.length; i++ ) {
		str  = eleArray[i].name;
		//alert("eleArray[i].name"+str);
		if(str != null && str.indexOf('WorkOrderTag/Extn') != -1){
			if (document.all[str] != null) {
				document.all[str].value = parentWindow.document.all[str].value
			}			
		}		
    }
</script>
