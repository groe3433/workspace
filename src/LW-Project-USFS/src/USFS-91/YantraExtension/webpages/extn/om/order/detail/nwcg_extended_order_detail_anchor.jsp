<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%
setHistoryFlags((YFCElement) request.getAttribute("Order"));
setHoldFuncFlags((YFCElement) request.getAttribute("Order"), isTrue("xml:/Rules/@RuleSetValue"));
%>
<yfc:callAPI apiID="AP1"/> <!-- getOrderInvoiceList [Populates DerivedOrderList] -->
<yfc:callAPI apiID="AP2"/> <!-- getOrderLineList [Populates ChainedOrderLineList] -->
<yfc:callAPI apiID="AP3"/> <!-- getOrderLineList [Populates DerivedOrderLineList] -->
<yfc:callAPI apiID="AP4"/> <!-- getShipmentListForOrder -->

<%
    prepareOrderElement((YFCElement) request.getAttribute("Order"),(YFCElement) request.getAttribute("DerivedOrderLineList"),(YFCElement) request.getAttribute("ChainedOrderLineList"));
    getTotalNumInvoices((YFCElement) request.getAttribute("OrderInvoiceList"),(YFCElement) request.getAttribute("DerivedOrderInvoiceList"));
    setActiveAlerts((YFCElement) request.getAttribute("InboxList"));
	String strDocumentType = resolveValue("xml:/Order/@DocumentType");
	boolean bOtherOrder = false ;
	if(strDocumentType != null && strDocumentType != "" && (!strDocumentType.equals("0001")))
	{
		bOtherOrder = true ;
	}
	String strOrderLineMsg = resolveValue("xml:OrderLineMsg:/Order/OrderLineQtyMsg/@Msg");	
%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
<% if(strOrderLineMsg.equals("Y")) { %>

<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I11"/>
			<jsp:param name="RenderReadOnly" value="true"/>
			<jsp:param name="Path" value="xml:/Order/Extn/@Extn"/>
        </jsp:include>
    </td>
</tr>
<% }  %>
<tr>
    <td>
        <yfc:makeXMLInput name="orderHeaderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("orderHeaderKey")%>' name="OrderEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey")%>/>
    </td>
</tr>
<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I10"/>
			<jsp:param name="RenderReadOnly" value="true"/>
			<jsp:param name="Path" value="xml:/Order/Extn/@Extn"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="2">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I11"/>
			<jsp:param name="RenderReadOnly" value="true"/>
			<jsp:param name="Path" value="xml:/Order/Extn/@Extn"/>
        </jsp:include>
    </td>
	<td height="100%" colspan="2" addressip="true">
	<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
		<jsp:param name="CurrentInnerPanelID" value="I12"/>
		<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo/"/>
		<jsp:param name="DataXML" value="Order"/>
		<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
	</jsp:include>
	</td>
</tr>
<tr>
    <%
        String addressAnswerSetFound = resolveValue("xml:/QuestionType/@AnswerSetFound");
        String permitAnswerSetFound = resolveValue("xml:PermitQuestions:/QuestionType/@AnswerSetFound");
        String answerSetFound = "N";
        if ((equals("Y", addressAnswerSetFound)) || (equals("Y", permitAnswerSetFound))) {
            answerSetFound = "Y";
        }
    %>

    <td height="100%" colspan="1" addressip="true">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoShipTo/"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
            <jsp:param name="ShowAnswerSetOptions" value='<%=answerSetFound%>'/>
            <jsp:param name="AnswerSetOptionsBinding" value="xml:/Order/@RetainAnswerSets/"/>
        </jsp:include>
    </td>

	<td height="100%" colspan="1" addressip="true">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I09"/>
			<jsp:param name="Path" value="xml:/Order/AdditionalAddresses/AdditionalAddress/PersonInfo/"/>
			<jsp:param name="AddressTypePath" value="xml:/Order/AdditionalAddresses/AdditionalAddress"/>
			<jsp:param name="DataXML" value="Order"/>
			<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
			<jsp:param name="ShowAnswerSetOptions" value='<%=answerSetFound%>'/>
			<jsp:param name="AnswerSetOptionsBinding" value="xml:/Order/@RetainAnswerSets"/>
			</jsp:include>
		</td>
	<td height="100%"colspan="1" addressip="true">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoBillTo/"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
        </jsp:include>
    </td>
    <td height="100%" colspan="1">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="4">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
        </jsp:include>
    </td>
</tr>
<%	if (equals(getValue("Order","xml:/Order/@HasServiceLines"),"Y")) { //add following inner panel only if there are any Provided service lines in the order %>
	<tr>
		<td colspan="4">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I07"/>
			</jsp:include>
		</td>
	</tr>
<% } %>
<% if (equals(getValue("Order","xml:/Order/@HasDeliveryLines"),"Y")) { 	//add following inner panel only if there are any Delivery service lines in the order %>
	<tr>
		<td colspan="4">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I08"/>
			</jsp:include>
		</td>
	</tr>
<% } %>
</table>