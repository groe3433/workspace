<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<%
YFCElement root = (YFCElement)request.getAttribute("WorkOrder");
if(!isVoid(root)){
	root.setAttribute("EnableShipment",false);
	if(!isVoid(resolveValue("xml:/WorkOrder/@ShipmentKey"))){
		root.setAttribute("EnableShipment",true);
	}
}		
if(!isVoid(root)){
	root.setAttribute("AllocationAllowed", true);

	String workOrderStatus = resolveValue("xml:/WorkOrder/@Status");
	if(workOrderStatus.startsWith("1400") || workOrderStatus.startsWith("1600")){
		root.setAttribute("WorkOrderNotCompleted", "");
		root.setAttribute("ReleaseAllowed", false);
		root.setAttribute("AllocationAllowed", false);
	}else if(root.getDoubleAttribute("QuantityRequested") == root.getDoubleAttribute("QuantityAllocated")){
		root.setAttribute("AllocationAllowed", false);
	}
}

if(!isVoid(root)){
	YFCElement workOrderActivities = (YFCElement)root.getChildElement("WorkOrderActivities");
	if(!isVoid(workOrderActivities)){
		String workOrderSts = resolveValue("xml:/WorkOrder/@Status");
		if(workOrderSts.startsWith("1100")&&   !equals(workOrderActivities.getAttribute("TotalNumberOfRecords"),"0")){
		  root.setAttribute("WorkOrderNotCompleted", true);
	}else{ 
	      root.setAttribute("WorkOrderNotCompleted", "");
	     } 
	      root.setAttribute("NumberOfActivities", workOrderActivities.getAttribute("TotalNumberOfRecords"));
    }else{
		  root.setAttribute("NumberOfActivities", "0");
	}
}

String sSOME_TIME_TAG_CTRL = YCMConstants.YCM_ITEM_TAG_SOMETIMES_TAG_CONTROLLED;
String sTAG_CTRL = YCMConstants.YFS_YES;
String sNOT_TAG_CTRL = YCMConstants.YFS_NO;

String itemTagControlFlag = resolveValue("xml:ItemDetailsTagControl:/Item/@TagCapturedInInventory");
if(equals(sSOME_TIME_TAG_CTRL, itemTagControlFlag) || equals(sTAG_CTRL, itemTagControlFlag)){
	itemTagControlFlag = sTAG_CTRL;
}else{
	itemTagControlFlag = sNOT_TAG_CTRL;
}

if(!isVoid(root)){
	root.setAttribute("ItemTagControlFlag", true);
	if(equals(sNOT_TAG_CTRL, itemTagControlFlag)){
		root.setAttribute("ItemTagControlFlag", false);
	}
}

YFCElement itemTagElem = null;
YFCElement itemComponentElem = null;
YFCElement individualComponent = null;
if(root != null){
	itemTagElem = root.getChildElement("WorkOrderTag");
	itemComponentElem= root.getChildElement("WorkOrderComponents");
}
if(itemComponentElem != null){
	individualComponent = itemComponentElem.getChildElement("WorkOrderComponent");
}
String ItemId=resolveValue("xml:/WorkOrder/@ItemID");
String ServiceItemGroupCode=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode");
String involvesSegmentChange = "Y";
if(ServiceItemGroupCode.equalsIgnoreCase("COMPL")){
	YFCElement itemDetailsInput = null;
	itemDetailsInput = YFCDocument.parse("<Item ItemID=\"" + resolveValue("xml:/WorkOrder/@ServiceItemID") + "\" CallingOrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" />").getDocumentElement();

	YFCElement itemDetailsTemplate = YFCDocument.parse("<ItemList><Item> <PrimaryInformation InvolvesSegmentChange=\"\"/> </Item></ItemList>").getDocumentElement(); 
%>
	<yfc:callAPI apiName="getItemList" inputElement="<%=itemDetailsInput%>" templateElement="<%=itemDetailsTemplate%>" outputNamespace="ItemDetails"/>
<%    
	involvesSegmentChange = resolveValue("xml:ItemDetails:/ItemList/Item/PrimaryInformation/@InvolvesSegmentChange");
}
	//Set attribute for visible binding....
	double dblQuantityRequested = root.getDoubleAttribute("QuantityRequested");
	double dblQuantityCompleted = root.getDoubleAttribute("QuantityCompleted");
	double dblQuantityRemoved = root.getDoubleAttribute("QuantityRemoved");
	boolean allowConfirmAndCancel = true;

	if (dblQuantityRequested == (dblQuantityCompleted + dblQuantityRemoved)) {
		allowConfirmAndCancel = false;
	}
	root.setAttribute("AllowConfirmAndCancel",allowConfirmAndCancel);


%>
<script language="javascript">
 yfcDoNotPromptForChanges(true);
</script>

<% if(!ServiceItemGroupCode.equalsIgnoreCase("PS")) { %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td colspan="2">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
	<%
	if(ServiceItemGroupCode.equalsIgnoreCase("KIT") ||
		ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
		ServiceItemGroupCode.equalsIgnoreCase("INVC")|| 
		(ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (involvesSegmentChange.equalsIgnoreCase("Y")))){%>
			<tr>
				<td width="50%" height="100%">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
					</jsp:include>
				</td>
				<td width="50%" height="100%">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I06"/>
					</jsp:include>
				</td>
			</tr>
	<%}else if(!isVoid(ItemId)){%>
			<tr>
				<td colspan="2">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
					</jsp:include>
				</td>
			</tr>
	<%}
	if (ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
		(ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (!involvesSegmentChange.equalsIgnoreCase("Y")))){%>
		<tr>
			<td colspan="2">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I05"/>
				</jsp:include>
			</td>
		</tr>
	<%}
	if(ServiceItemGroupCode.equalsIgnoreCase("KIT") ||
		ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
		ServiceItemGroupCode.equalsIgnoreCase("INVC")|| 
		(ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (involvesSegmentChange.equalsIgnoreCase("Y")))){%>
		<tr>
			<td width="50%" height="100%">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I03"/>
				</jsp:include>
			</td>
			<td width="50%" height="100%">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I04"/>
				</jsp:include>
			</td>
		</tr>
	<%}else{%>
		<tr>
			<td>
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I04"/>
				</jsp:include>
			</td>
		</tr>
	<%}%>
</table>

<% } else { %>
	<table  class="anchor" cellSpacing="0" cellpadding="7px">
	<tr>
		<td>
			<jsp:include  page="/yfc/innerpanel.jsp" flush='true' >
				<jsp:param  name="CurrentInnerPanelID" value="I07" />
				<jsp:param  name="ModifyView" value="true" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td addressip="true">
			<%String outputPath = "xml:/WorkOrder/PersonInfo";%>
			<jsp:include  page="/yfc/innerpanel.jsp" flush='true' >
				<jsp:param  name="CurrentInnerPanelID" value="I08" />
				<jsp:param  name="Path" value="xml:/WorkOrder/PersonInfo" />
				<jsp:param name="OutputPath" value='<%=outputPath%>'/>
				<jsp:param  name="DataXML" value="WorkOrder" />
			</jsp:include>
		</td>
	</tr>	
</table>
<% } %>