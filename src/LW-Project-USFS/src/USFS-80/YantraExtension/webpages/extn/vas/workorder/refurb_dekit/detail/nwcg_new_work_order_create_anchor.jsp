<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<script language="javascript">
	function moveToNextPage()
	{
		var startDate = document.all("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE_Restore").value;
		var startTime = document.all("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME_Restore").value;
		var finishDate = document.all("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE_Restore").value;
		var finishTime = document.all("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME_Restore").value;

		var extraParams = "xml:/WorkOrder/@WorkOrderMode=menu";
		extraParams = extraParams + "&xml:/WorkOrder/@StartNoEarlierThan_YFCDATE=" + startDate;
		extraParams = extraParams + "&xml:/WorkOrder/@StartNoEarlierThan_YFCTIME=" + startTime;
		extraParams = extraParams + "&xml:/WorkOrder/@FinishNoLaterThan_YFCDATE=" + finishDate;
		extraParams = extraParams + "&xml:/WorkOrder/@FinishNoLaterThan_YFCTIME=" + finishTime;
		
		var sVal = document.all("myEntityKey");
		var containerForm= document.all("containerform");
		var hiddenCall = document.createElement("<INPUT type='hidden' name='xml:/WorkOrder/@CallApi' value=''/>");
		var hiddenMode1 = document.createElement("<INPUT type='hidden' name='xml:/WorkOrder/@WorkOrderMode' value='menu'/>");
		var ServiceItemGroupCode = document.createElement("<INPUT type='hidden' name='xml:/WorkOrder/@ServiceItemGroupCode' value='DKIT'/>");
		containerForm.insertBefore(hiddenCall);
		containerForm.insertBefore(hiddenMode1);
		containerForm.insertBefore(ServiceItemGroupCode);
		document.all("xml:/WorkOrder/@CallApi").value="";
		showDetailForOnAdvancedList('workorder', 'NWCYVSD011', sVal.value, extraParams); 
	}
	window.attachEvent("onload",doNormalWindow);
	function proceedToChange(obj) {

		// Jay : Changes for CR# 267
		var elemIncdtNo = document.getElementById("xml:/WorkOrder/Extn/@ExtnIncidentNo");
		if(eval(elemIncdtNo) && elemIncdtNo.value == '' || elemIncdtNo.value == null)
		{
			alert('Please enter Incident # ');
			return false ;
		}
		// end

//		var ServiceItemGroupCode=document.all("xml:/WorkOrder/@ServiceItemGroupCode").value;
		var workOrderItemId=document.all("xml:/WorkOrder/@ItemID").value;
		var workOrderNo=document.all("xml:/WorkOrder/@WorkOrderNo").value;
		if(!document.all("xml:/WorkOrder/@NodeKey").value){
			alert(YFCMSG077);//Node not passed
			doNormalWindow();
		}else if(!document.all("xml:/WorkOrder/@EnterpriseCode").value){
			alert(YFCMSG078);//YFCMSG078 = "Enterprise not Passed";
			doNormalWindow();
		}else if(!document.all("xml:/WorkOrder/@ServiceItemGroupCode").value){
			alert(YFCMSG092);//Work Order Service Item Group is mandatory
			doNormalWindow();
		}else if(!document.all("xml:/WorkOrder/@ServiceItemID").value){
			alert(YFCMSG093);//Work Order Service Item Not Passed
			doNormalWindow();
		}else if((!document.all("xml:/WorkOrder/@SegmentType").value && document.all("xml:/WorkOrder/@Segment").value) ||
				  (document.all("xml:/WorkOrder/@SegmentType").value && !document.all("xml:/WorkOrder/@Segment").value)){
			alert(YFCMSG093);//Both Segment Type and Segment must be passed
			doNormalWindow();
		}else if(document.all("xml:/WorkOrder/@ServiceItemGroupCode").value == "KIT" ||
				document.all("xml:/WorkOrder/@ServiceItemGroupCode").value == "DKIT" ||
				document.all("xml:/WorkOrder/@ServiceItemGroupCode").value == "INVC"){
				if(!document.all("xml:/WorkOrder/@ItemID").value){
					alert(YFCMSG095);//Item id is mandatory
					doNormalWindow();
				}
				else{
					var containerForm= document.all("containerform");
					var hidCallApi = document.createElement("<INPUT type='hidden' name='xml:/WorkOrder/@CallApi' value='Y'/>");
					containerForm.insertBefore(hidCallApi);
					yfcChangeDetailView(getCurrentViewId());
				}

		}else{
			var containerForm= document.all("containerform");
			var hiddenCallApi = document.createElement("<INPUT type='hidden' name='xml:/WorkOrder/@CallApi' value='Y'/>");
			containerForm.insertBefore(hiddenCallApi);
			yfcChangeDetailView(getCurrentViewId());
		}
		
	}

	function doNormalWindow()
	{
		var tmp = '<%=getParameter("hidPrepareEntityKey")%>';
		var oError = document.all("isError");
		if(tmp == "Y" && oError.value == "N")
		{
			oError.value="Y";
			yfcChangeDetailView('NWCYVSD011');
		}
	}

</script>
<%
	YFCElement elemServiceItem=(YFCElement)request.getAttribute("ServiceItemGroupList");
	if(elemServiceItem!=null){
		for(Iterator oItr=elemServiceItem.getChildren();oItr.hasNext();) {
			YFCElement elemCode=(YFCElement)oItr.next();
			String sCodeValue=elemCode.getAttribute("CodeValue");
			if(equals("PS",sCodeValue)) {
				elemServiceItem.removeChild((YFCNode)elemCode);
			}
		}
	}
%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td align="center">
	     	<input class="button" type="button" value="<%=getI18N("Proceed")%>" onclick="proceedToChange(this);" />
			<input type="hidden" name="xml:/WorkOrder/@StartNoEarlierThan_YFCDATE_Restore" value="<%=request.getParameter("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE")%>" />
			<input type="hidden" name="xml:/WorkOrder/@StartNoEarlierThan_YFCTIME_Restore" value="<%=request.getParameter("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME")%>" />
			<input type="hidden" name="xml:/WorkOrder/@FinishNoLaterThan_YFCDATE_Restore" value="<%=request.getParameter("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE")%>" />
			<input type="hidden" name="xml:/WorkOrder/@FinishNoLaterThan_YFCTIME_Restore" value="<%=request.getParameter("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME")%>" />
		</td>
	</tr>
<%
if(!isVoid(resolveValue("xml:/WorkOrder/@CallApi")))
{
	YFCElement itemDetailsInput = null;
	itemDetailsInput = YFCDocument.parse("<Item ItemID=\"" + resolveValue("xml:/WorkOrder/@ServiceItemID") + "\" CallingOrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" />").getDocumentElement();

	YFCElement itemDetailsTemplate = YFCDocument.parse("<ItemList TotalNumberOfRecords=\"\"><Item> <PrimaryInformation InvolvesSegmentChange=\"\"/> </Item></ItemList>").getDocumentElement(); 
%>
	<yfc:callAPI apiName="getItemList" inputElement="<%=itemDetailsInput%>" templateElement="<%=itemDetailsTemplate%>" outputNamespace="ItemDetails"/>
<%
	if(equals("0",resolveValue("xml:ItemDetails:/ItemList/@TotalNumberOfRecords"))||isVoid(resolveValue("xml:ItemDetails:/ItemList/@TotalNumberOfRecords"))){
%>
<script>
	alert(YFCMSG096);//Service Item ID passed is invalid
</script>
	<%}else if(equals("Y",resolveValue("xml:ItemDetails:/ItemList/Item/PrimaryInformation/@InvolvesSegmentChange")) && equals("COMPL",resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))){						
		if(isVoid(resolveValue("xml:/WorkOrder/@ItemID"))){%>
<script>
	alert(YFCMSG095);//Item Id is mandatory
</script>
<input type="hidden" name="xml:/WorkOrder/@WorkOrderMode" value=""/>
		<%}else{%>

<script>
	moveToNextPage();
</script>
	<%}}
else{%>
<script>
	moveToNextPage();
</script>

<%

	}
}%>
</table>
