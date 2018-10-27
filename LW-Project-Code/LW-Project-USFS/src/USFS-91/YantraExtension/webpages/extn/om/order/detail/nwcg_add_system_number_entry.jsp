<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_prepareOrderLine.js"></script>
<script language="javascript">
    function window.onload() {
		document.getElementById("SysNoEntry").focus();
		if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
			return;
		}
	}
	
	// just a wrapper function to check for null values
	function checkForNull(str)
	{
		if(str == null || str == 'null' || str == 'undefined')
				str = '' ;
		return str ;
	}
	
	function addEnteredSysNoToAllLines()
	{
		var olExtnSystemNo = null;
		var InputList = document.getElementsByTagName("Input");
		for (i = 0 ; i < InputList.length ; i++)
		{
			if (InputList[i].name.indexOf('@ExtnSystemNo') != -1) 
			{
				olExtnSystemNo = InputList[i].value;
				break;			
			}
		}

		var regExp = /^[0-9a-zA-Z]+$/i;
		var clientSideSysNo = olExtnSystemNo;
					
		if (!regExp.test(clientSideSysNo)) 
		{
			alert('System Number must be specified must be alphanumeric!');
			return 0;
		}		
		return 1;				
	}
	
	function handleEnterOnSysNoPopUp(inField, e) 
	{
	    var charCode;
	    
	    if(e && e.which)
	    {
	        charCode = e.which;
	    }
	    else if(window.event)
	    {
	        e = window.event;
	        charCode = e.keyCode;
	    }
	
	    if(charCode == 13) 
	    {
	        if (inField.id == 'SysNoEntry') {
				if (addEnteredSysNoToAllLines()) {			
					invokeSave();
					window.close();
				}
			}
	    }
	}
	
	function invokeSave()
	{
		var elem = document.getElementsByTagName("input");
		for(i=0;i < elem.length;i++)
		{
			if(elem[i].type == 'button' && elem[i].value =='Save')
			{
				yfcCallSave(elem[i]);
			}//end if
		}//end for
	}// end function
	
</script>

<%
	String orderHeaderKey = resolveValue("xml:/Order/@OrderHeaderKey");
	String[] entityKeyArray;
	entityKeyArray = request.getParameterValues("EntityKey");
	String strDocType = NWCGConstants.ORDER_DOCUMENT_TYPE;	
	
	//Create the input document for getOrderLineList utilizing a complex query
	//to gather order line details for all selected lines from the prior screen
	YFCDocument orderLinesComplexQry = YFCDocument.createDocument(NWCGConstants.ORDER_LINE);
	YFCElement orderLinesComplexQryElem = orderLinesComplexQry.getDocumentElement();	
	orderLinesComplexQryElem.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
	YFCElement complexQryElem = orderLinesComplexQry.createElement("ComplexQuery");
	YFCElement orElem = orderLinesComplexQry.createElement("Or");
	
	if ( entityKeyArray != null ) {		
		orderLinesComplexQryElem.appendChild(complexQryElem);
		complexQryElem.appendChild(orElem);		
		complexQryElem.setAttribute("Operator", "AND");		
		String ohk = NWCGConstants.EMPTY_STRING;	   		

		YFCDocument entityKeyDoc = YFCDocument.parse(URLDecoder.decode(entityKeyArray[0]));	
		YFCElement entityKeyElem = entityKeyDoc.getDocumentElement();
		ohk = entityKeyElem.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		
		YFCNodeList orderLineNodeList = entityKeyElem.getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0 ; i < orderLineNodeList.getLength(); i++) {
			YFCElement currOrderLine = (YFCElement) orderLineNodeList.item(i);
			String currOLK = currOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			String inputNameXpath = "xml:/Order/OrderLines/OrderLine_"+(i+1)+"/@OrderLineKey";
			%>
			<input type="hidden" name="<%=inputNameXpath%>" value="<%=currOLK%>" />
			<%			
			YFCElement expElem = orderLinesComplexQry.createElement("Exp");
			orElem.appendChild(expElem);
			
			expElem.setAttribute("Name", NWCGConstants.ORDER_LINE_KEY);
			expElem.setAttribute("Value", currOLK);
	
		    orderLinesComplexQryElem.setAttribute(NWCGConstants.ORDER_HEADER_KEY, ohk);		    
		}
	}
	else 
		// Do Nothing...
	
	
	boolean bOtherIssues = false ;
	if(strDocType != null && (!strDocType.equals(NWCGConstants.ORDER_DOCUMENT_TYPE)))
		bOtherIssues = true ;

	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation, NWCGConstants.YES) || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;
	
	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
	
	YFCDocument templateDoc = YFCDocument.parse("<OrderLineList>"+
		    "<OrderLine ConditionVariable1=\"\" MaxLineStatus=\"\" MaxLineStatusDesc=\"\" MultipleStatusesExist=\"\" OrderHeaderKey=\"\" OrderLineKey=\"\" PrimeLineNo=\"\" ReceivingNode=\"\" "+
		    "ReqCancelDate=\"\" ReqDeliveryDate=\"\" ReqShipDate=\"\" ShipNode=\"\" Status=\"\" SubLineNo=\"\" isHistory=\"\">"+
			"<Extn ExtnRequestNo=\"\" ExtnOrigReqQty=\"\" ExtnSystemNo=\"\"/>"+
	        "<Order DriverDate=\"\" DocumentType=\"\" BuyerOrganizationCode=\"\" EnterpriseCode=\"\" HoldFlag=\"\" OrderNo=\"\" SellerOrganizationCode=\"\"><PriceInfo Currency=\"\" TotalAmount=\"\"/></Order>"+
	        "<Item ItemDesc=\"\" ItemID=\"\" ItemShortDesc=\"\" ProductClass=\"\"/>"+
	        "<OrderLineTranQuantity OrderedQty=\"\" TransactionalUOM=\"\"/></OrderLine></OrderLineList>");
%>

<input type="hidden" name="xml:/Order/@OrderHeaderKey" value='<%=orderHeaderKey%>'/>
<yfc:callAPI apiName="getOrderLineList" inputElement="<%=orderLinesComplexQry.getDocumentElement()%>" templateElement="<%=templateDoc.getDocumentElement()%>" outputNamespace="SOL"/>

<table editable="false" width="100%">
<thead>
<tr>
<td border="1">
<p align=center class="screentitle"><yfc:i18n>Please_enter_a_System_Number_for_the_Lines_Below</yfc:i18n>
</p>
</td>
</tr>
</thead>
</table>

<table class="table" id="OrderLines" cellSpacing="0" yfcMaxSortingRecords="1000" >
    <thead>
        <tr>     
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
		</yfc:loopXML>
	</tbody>
	<tfoot>
		<table width="100%" class="view">		  
			<br/>
			<br/>
			<tr>
				<td class="detaillabel"><yfc:i18n>System_Number</yfc:i18n></td>
				<td>
					<input id="SysNoEntry" type="text" class="unprotectedinput" onkeypress="handleEnterOnSysNoPopUp(this, event)" <%=getTextOptions("xml:/Order/@ExtnSystemNo")%> tabindex="1"/>					
				</td>
			<tr/>		    
		</table>
	</tfoot>
</table>