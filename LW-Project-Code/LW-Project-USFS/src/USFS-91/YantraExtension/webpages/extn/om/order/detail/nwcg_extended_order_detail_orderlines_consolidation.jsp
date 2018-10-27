<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_prepareOrderLine.js"></script>
<script language="javascript">
    function window.onload() {
		if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
			return;
		}
	}
</script>

<%
	String orderHeaderKey = resolveValue("xml:/Order/@OrderHeaderKey");
	String[] entityKeyArray;
	entityKeyArray = request.getParameterValues("EntityKey");
	String strDocType = "0001";	
	
	//Create the input document for getOrderLineList utilizing a complex query
	//to gather order line details for all selected lines from the prior screen
	YFCDocument orderLinesComplexQry = YFCDocument.createDocument("OrderLine");
	YFCElement orderLinesComplexQryElem = orderLinesComplexQry.getDocumentElement();	
	orderLinesComplexQryElem.setAttribute("OrderHeaderKey", orderHeaderKey);
	YFCElement complexQryElem = orderLinesComplexQry.createElement("ComplexQuery");
	YFCElement orElem = orderLinesComplexQry.createElement("Or");
	
	if ( entityKeyArray != null ) {		
		orderLinesComplexQryElem.appendChild(complexQryElem);
		complexQryElem.appendChild(orElem);		
		complexQryElem.setAttribute("Operator", "AND");		
		String ohk = "";		   		

		YFCDocument entityKeyDoc = YFCDocument.parse(URLDecoder.decode(entityKeyArray[0]));	
		YFCElement entityKeyElem = entityKeyDoc.getDocumentElement();
		ohk = entityKeyElem.getAttribute("OrderHeaderKey");
		
		YFCNodeList orderLineNodeList = entityKeyElem.getElementsByTagName("OrderLine");
		for (int i = 0 ; i < orderLineNodeList.getLength(); i++) {
			YFCElement currOrderLine = (YFCElement) orderLineNodeList.item(i);
			String currOLK = currOrderLine.getAttribute("OrderLineKey");
			String inputNameXpath = "xml:/Order/OrderLinesToConsolidate/OrderLine_"+(i+1)+"/@OrderLineKey";
			%>
			<input type="hidden" name="<%=inputNameXpath%>" value="<%=currOLK%>" />
			<%			
			YFCElement expElem = orderLinesComplexQry.createElement("Exp");
			orElem.appendChild(expElem);
			
			expElem.setAttribute("Name", "OrderLineKey");
			expElem.setAttribute("Value", currOLK);
	
		    orderLinesComplexQryElem.setAttribute("OrderHeaderKey", ohk);		    
		}
	}
	else {
		// Do Nothing...
	}	
	
	boolean bOtherIssues = false ;
	if(strDocType != null && (!strDocType.equals("0001")))
	{
		bOtherIssues = true ;
	}
	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;
	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
	
	YFCDocument templateDoc = YFCDocument.parse("<OrderLineList>"+
		    "<OrderLine ConditionVariable1=\"\" MaxLineStatus=\"\" MaxLineStatusDesc=\"\" MultipleStatusesExist=\"\" OrderHeaderKey=\"\" OrderLineKey=\"\" PrimeLineNo=\"\" ReceivingNode=\"\" "+
		    "ReqCancelDate=\"\" ReqDeliveryDate=\"\" ReqShipDate=\"\" ShipNode=\"\" Status=\"\" SubLineNo=\"\" isHistory=\"\">"+
			"<Extn ExtnRequestNo=\"\" ExtnOrigReqQty=\"\"/>"+
	        "<Order DriverDate=\"\" DocumentType=\"\" BuyerOrganizationCode=\"\" EnterpriseCode=\"\" HoldFlag=\"\" OrderNo=\"\" SellerOrganizationCode=\"\"><PriceInfo Currency=\"\" TotalAmount=\"\"/><Extn ExtnSystemOfOrigin=\"\"/></Order>"+
	        "<Item ItemDesc=\"\" ItemID=\"\" ItemShortDesc=\"\" ProductClass=\"\"/>"+
	        "<OrderLineTranQuantity OrderedQty=\"\" TransactionalUOM=\"\"/></OrderLine></OrderLineList>");
%>

<input type="hidden" name="xml:/Order/@OrderHeaderKey" value='<%=orderHeaderKey%>'/>
<yfc:callAPI apiName="getOrderLineList" inputElement="<%=orderLinesComplexQry.getDocumentElement()%>" templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace="SOL"/>

<table editable="false" width="100%"><thead>
<tr><td border="1">
<p align=center class="screentitle"><yfc:i18n>Please_Select_Surviving_Request_Number_From_Lines_Chosen_Below</yfc:i18n>
</p></td></tr>
</thead></table>

<table class="table" id="OrderLines" cellSpacing="0" yfcMaxSortingRecords="1000" >
    <thead>
        <tr>     
            <td class="checkboxheader" sortable="no" style="width:5px"/>
            <td class="tablecolumnheader" nowrap="true" style="width:60px"><yfc:i18n>S_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:60px"><yfc:i18n>Item_ID</yfc:i18n></td>			
            <td class="tablecolumnheader" sortable="no" style="width:30px"><yfc:i18n>UOM</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>Requested Quantity</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>            
        </tr>
    </thead>
    <tbody>
    	<yfc:loopXML binding="xml:SOL:/OrderLineList/@OrderLine" id="OrderLine" keyName="OrderLineKey">
    		<tr>
		        <yfc:makeXMLInput name="orderLineKey">
			        <yfc:makeXMLKey binding="xml:/OrderLine/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
		        </yfc:makeXMLInput> 
	
				<td style="width:3px">
						<input type="radio" onclick='radioBtnClick(this);' name='RadioSelectedLine' value='<%=getParameter("orderLineKey")%>'/></td>
				
				<td nowrap="true" id="RequestNo">
					<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/Extn/@ExtnRequestNo"/></td>
				
				<td style="width:50px" nowrap="true">
					<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/Item/@ItemID"/></td>
				
				<td>
					<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>

				<td>
					<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/Extn/@ExtnOrigReqQty"/></td>			
						
				<td>
					<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/Item/@ItemShortDesc"/></td>								

			</tr>
			<input type='hidden' name='xml:/OrderLine/@ConditionVariable1' value='<%=getValue("SOL", "xml:/OrderLineList/OrderLine/@ConditionVariable1")%>'/>
			<input type='hidden' name='xml:/Order/@ExtnSystemOfOrigin' value='<%=getValue("SOL", "xml:/OrderLineList/OrderLine/Order/Extn/@ExtnSystemOfOrigin")%>'/>
		</yfc:loopXML>
	</tbody>
    <tfoot>
    </tfoot>
</table>
<br/>
<br/>
<table class="table" id="OrderLineToCreate" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
    <thead>
        <tr>            
            <td class="tablecolumnheader" nowrap="true" style="width:50px"><yfc:i18n>S_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:60px"><yfc:i18n>Item_ID</yfc:i18n></td>
			
            <td class="tablecolumnheader" style="width:width:30px"><yfc:i18n>UOM</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:180px"><yfc:i18n>Description</yfc:i18n></td>            
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>RFI_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>Issue_Qty</yfc:i18n></td>
			
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>"><yfc:i18n>Backordered_Qty</yfc:i18n></td>			
			<% String driverDate = getValue("SOL", "xml:/OrderLineList/OrderLine/Order/@DriverDate");  
               if (equals(driverDate, "02")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } else { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:100px"><yfc:i18n>Comments</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
		<tr>
			<td colspan=1 align=left>
				<input type=button id="clearButton" value='Clear' name="Clear" tabindex="-1" onClick="clearTableData();"/>
			</td>
		</tr>
	</tbody>
    <tfoot>
    </tfoot>
</table>
<jsp:include page="/extn/common/nwcg_new_render_orderline_cons_js.jsp" flush="true"/>
