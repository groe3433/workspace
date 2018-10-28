<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<yfc:callAPI apiID="AP1"/>
 <table class="view" width="100%">
	<tr>
		<yfc:makeXMLInput name="LineKey">
			<yfc:makeXMLKey binding="xml:/MoveRequestLine/@MoveRequestLineKey" value="xml:/MoveRequestLine/@MoveRequestLineKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequestLine/MoveRequest/@MoveRequestNo" value="xml:/MoveRequestLine/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequestLine/MoveRequest/@Node" value="xml:/MoveRequestLine/MoveRequest/@Node" />			
        </yfc:makeXMLInput>
		<td></td>
		<input type="hidden" name="xml:/MoveRequestLine/@MoveRequestLineKey" value='<%=resolveValue("xml:/MoveRequestLine/@MoveRequestLineKey")%>'/>
		<td></td>
		<td></td>
		<td></td>
		<td></td>		
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Has_Exceptions</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@HasExceptions" > </yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Is_Tasked</yfc:i18n></td>
		
			<% if(( !( isVoid(resolveValue("xml:/MoveRequestLine/@PalletId"))) || !( isVoid(resolveValue("xml:/MoveRequestLine/@CaseId")))) && ( isTrue(resolveValue("xml:/MoveRequestLine/@ReleasedFlag")) )){%> 

				<td class="protectedtext"><yfc:i18n>Y</yfc:i18n></td> 	

			<% }else if( !( isVoid(resolveValue("xml:/MoveRequestLine/@ItemId")))  && (0<getNumericValue("xml:/MoveRequestLine/@ReleasedQuantity") )){%> 

				<td class="protectedtext"><yfc:i18n>Y</yfc:i18n></td> 	

			<%}else{%>
				<td class="protectedtext"><yfc:i18n>N</yfc:i18n></td> 					

			<%}%>        
		
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Source_Location</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@SourceLocationId" > </yfc:getXMLValue></td> 
		<td class="detaillabel" ><yfc:i18n>Target_Location</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@DestLocationId" > </yfc:getXMLValue></td> 		
		<td></td>
	</tr>
	<tr>
						<td colspan="5">
							<fieldset>
								<legend><yfc:i18n>Inventory_Details</yfc:i18n></legend> 
									<table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>Organization_Code</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@EnterpriseCode" > </yfc:getXMLValue></td> 
		<td class="detaillabel" ><yfc:i18n>Receipt_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ReceiptHeaderKey" > </yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Case_Id</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@CaseId" ></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Pallet_Id</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@PalletId"></yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Item_Id</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ItemId"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@UnitOfMeasure" > </yfc:getXMLValue></td> 
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ProductClass" > </yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Inventory_Status</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@InventoryStatus" > </yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Segment_Type</yfc:i18n></td>
		<td class="protectedtext" ><yfc:getXMLValue binding="xml:/MoveRequestLine/@SegmentType"></yfc:getXMLValue></td>
		<td class="detaillabel"><yfc:i18n>Segment</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@Segment"></yfc:getXMLValue></td> 
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Ship_By_Date</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ShipByDate"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Serial_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@SerialNo"></yfc:getXMLValue></td>
		<td></td>
	</tr>

								</table>
							</fieldset>
						</td>
					</tr>
	<tr>


						<td colspan="5">
							<fieldset>
								<legend><yfc:i18n>Inventory_Tag_Details</yfc:i18n></legend> 
									<table class="view" width="100%">

	<tr>
		<td class="detaillabel" ><yfc:i18n>Lot_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequestLineTag/@LotNumber"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Batch_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequestLineTag/@BatchNo"></yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Revision_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequestLineTag/@RevisionNo"></yfc:getXMLValue></td>
		<td></td>		
		<td></td>
		<td></td>
	</tr>



								</table>
							</fieldset>
						</td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Quantity_To_Move</yfc:i18n></td>
		<td nowrap="true"><input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequestLine/@RequestQuantity") %> /></td>
		<td class="detaillabel" ><yfc:i18n>Quantity_Tasked</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/@ReleasedQuantity" > </yfc:getXMLValue></td>
		<td><input type="hidden" value='<%=getParameter("LineKey")%>' name="LineKey"/></td>
	</tr>
</table>