<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table width="100%" class="view">
<tr>
	<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
        <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@ItemId"/>		
    </td>
	<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
         <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@ProductClass"/>		
    </td>
	<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@UnitOfMeasure"/>
	</td>		
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@PalletId"/>
    </td>		
	<td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@CaseId"/>
   	</td>
	<td class="detaillabel" ><yfc:i18n>Ship_By_Date</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@ShipByDate"/>
   	</td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Inventory_Status</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
			<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@InventoryStatus"/>
    </td>
	<td class="detaillabel" ><yfc:i18n>Segment_Type</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@SegmentType"/>
   	</td>
	<td class="detaillabel" ><yfc:i18n>Segment</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
	    <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@Segment"/>
    </td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Country_Of_Origin</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
	    <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@CountryOfOrigin"/>
    </td>		
	<td class="detaillabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
    <yfc:makeXMLInput name="receiptHeaderKey" >
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/MoveRequestLine/@ReceiptHeaderKey" />
	</yfc:makeXMLInput>
    <a <%=getDetailHrefOptions("L01",getParameter("receiptHeaderKey"),"")%> ><yfc:getXMLValue  binding="xml:/MoveRequestLine/Receipt/@ReceiptNo"/></a>	
    </td>
	<td class="detaillabel" ><yfc:i18n>Serial_#</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
         <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@SerialNo"/>
    </td>
</tr>
<%String sStatus = resolveValue("xml:/MoveRequestLine/@CancelledFlag");
if(equals("Y",sStatus))
{	
	String sReasonCode = resolveValue("xml:/MoveRequestLine/@ReasonCode");
	String sReasonCodeDesc = "";
	YFCElement reasonCodeDoc = (YFCElement)request.getAttribute("ReasonCodeList");
	if (reasonCodeDoc != null)
	{
		for (Iterator i=reasonCodeDoc.getChildren();i.hasNext();)
		{
			YFCElement oElem = (YFCElement)i.next();
			if(YFCCommon.equals(oElem.getAttribute("CodeValue"),sReasonCode))
			{
				sReasonCodeDesc = oElem.getAttribute("CodeShortDescription");
				break;
			}
		}
	}
	if (YFCCommon.isVoid(sReasonCodeDesc))
		sReasonCodeDesc = sReasonCode;%>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Cancellation_Reason_Code</yfc:i18n></td>
		<td class="protectedtext">
			<%=sReasonCodeDesc%>
		</td>
		<td class="detaillabel" ><yfc:i18n>Cancellation_Reason</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ReasonText" name="MoveRequestLine"></yfc:getXMLValue></td>
	</tr>
<%}%>

</table>