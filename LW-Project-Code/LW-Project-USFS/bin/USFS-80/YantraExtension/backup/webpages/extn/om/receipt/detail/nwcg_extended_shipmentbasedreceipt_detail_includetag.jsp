<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%
  int tagtrack=0;
  int NoSecSerials = 0;
  String sNoSecSerials = request.getParameter("NumSecondarySerials");
  if(!isVoid(sNoSecSerials)){
	 NoSecSerials = (new Integer(sNoSecSerials)).intValue();
  }	 

	String strUnitCost = request.getParameter("UnitCost");
%>
<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
<td width="80%" style="border:1px solid black">
<table class="table" editable="true" border="1" width="100%" cellspacing="0">
<%
	Map identifierAttrMap=null;
	Map descriptorAttrMap=null;
	Map extnIdentifierAttrMap=null;
	Map extnDescriptorAttrMap=null;
	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}
	%> 
	<yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
		extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
	%>
	 </yfc:hasXMLNode>
	<%
	String modifiable = request.getParameter("Modifiable");
	boolean isModifiable = false;
	if (equals(modifiable,"true")) {
		isModifiable = true;
	}%>
   <thead> 
   <tr>
	<% 	if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))){%>
	   <td class="tablecolumnheader">&nbsp;
		</td>
   <%}%>
	<%
		int i = 0;
		while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>				
				<% 	if(equals("Y",request.getParameter("SerialTracked"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))){%>
					<td sortable="no" class="tablecolumnheader"> 
						<yfc:i18n>Serial_#</yfc:i18n>
					</td>
					<% for (int s=1; s <= NoSecSerials ; s++){
					  String serLabel= "Secondary_Serial_"+s; %>
						<td sortable="no" class="tablecolumnheader">
							<yfc:i18n><%=serLabel%></yfc:i18n>
						</td>
					<%}%>	
				<%}%>
                
                <% 	if(equals("Y",request.getParameter("TimeSensitive"))){%>
					<td sortable="no" class="tablecolumnheader"> 
						<yfc:i18n>Ship_By_Date</yfc:i18n>
					</td>
				<%}%> 
				<td sortable="no" class="tablecolumnheader">
					Receiving_Price
				</td>
</tr>
</thead>
<tbody>
</tbody>
<% if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))){%>
 <tfoot>
	 <tr style='display:none' TemplateRow="true">
		<td class="checkboxcolumn" >
	    </td>
<%}%> 
	<%
	String binding ="xml:/Receipt/ReceiptLines/ReceiptLine";
	String sOptionCounter = request.getParameter("optionSetBelongingToLine");
	
	i = 0;
	while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
	
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
	
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											String sTagSerialExtnBinding = "/@" ;
											if(isExtn){
												sTagSerialExtnBinding = "/Extn/@" ;
											}
%>
	    <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr) %>/>
			<%	if(currentAttr=="ManufacturingDate"){%>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<%}%>
		</td>
											
									    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }}
		i++;
}%> 
    
	<%if(equals("Y",request.getParameter("SerialTracked"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr", request.getParameter("applicationCode")))){%>
	   <td nowrap="true" class="tablecolumn">
           <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@SerialNo")%>/>
       </td>
	  <% for (int s=1; s <= NoSecSerials; s++){%>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/SerialDetail_"+"/@SecondarySerial"+s)%>/>
			</td>
		<%}%>
	<%}%>
	<%if(equals("Y",request.getParameter("TimeSensitive"))){%>
	   <td nowrap="true" class="tablecolumn">
           <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@ShipByDate")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
       </td>
	 <%}%>
	  <td class="tablecolumn">
	  <input type="text" class="unprotectedinput" <%=getTextOptions(binding+"_"+sOptionCounter+"/TagSerial_/Extn/@ReceivingPrice",strUnitCost)%> style='width:100px'/>
	 </td>
	   <!--
	   <td nowrap="true" class="tablecolumn">
      		<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@IsNewRow","Y")%>/> 
	      	<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ItemID", request.getParameter("ItemID"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ProductClass", request.getParameter("ProductClass"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@UnitOfMeasure", request.getParameter("UnitOfMeasure"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@InventoryStatus", request.getParameter("InventoryStatus"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@DispositionCode")%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@OrderLineKey", request.getParameter("OrderLineKey"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ShipmentLineKey", request.getParameter("ShipmentLineKey"))%>/>
		</td>
	 -->
	 </tr>
	<% if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))){%>
	 <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
    		</jsp:include>
    	</td>
    </tr>
</tfoot>
<%}%>
</table>
</td>
</tr>
</tbody>
</table>