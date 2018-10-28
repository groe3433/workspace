<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/moverequest.js"></script>
<script language="javascript">
function setycpFireAction(chkName) {
	var yfcCheckBoxName = "";
    if(checkForOuboundContainerFlag(chkName)) {
		if ( !yfcCheckForSelection(chkName)) 
                    		return false;
			    
				yfcCheckBoxName = chkName;
                var retVal = eval("getCorrectEntityKeyForContainers('LPNListEntityKey','4','');");
                if ( retVal ) {
                    yfcAddToForm(retVal);
                } else {
                    return false;
                }
        	   
			           yfcShowDetailPopupWithKeys('YOMD340', 'Container_Details', '1010', '650',new Object(), chkName, 'wmsinventory', chkName);                
	                                 
	}else  if(checkForInventoryContainerFlag(chkName)) {
	    if ( !yfcCheckForSelection(chkName)) 
                    		return false;
			
				yfcCheckBoxName = chkName;
                var retVal = eval("getCorrectEntityKeyForContainers('LPNListEntityKey','','1');");
                if ( retVal ) {
                    yfcAddToForm(retVal);
                } else {
                    return false;
                }
        	 
						yfcShowDetailPopupWithKeys('YWMD048', 'Container_Details', '1010', '650',new Object(), chkName, 'wmsinventory', chkName);                
	                   
        	    } else {
		        alert(YFCMSG131); //YFCMSG131 = "You must select Containers belonging to one group";
         return;
	 }
  }
</script>
<div style="height:200px;overflow:auto">
<table width="100%" border="0" cellspacing="0"     class="table">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no" >
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>

        <td class="tablecolumnheader"  nowrap="true"  >
            <yfc:i18n>Location</yfc:i18n>
        </td>
	
        <td class="tablecolumnheader" nowrap="true"   >
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>

		<td class="tablecolumnheader"  nowrap="true"  >
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader"  nowrap="true"  >
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
		
        <td class="tablecolumnheader" nowrap="true"  >

            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true"  >

            <yfc:i18n>Is_Outbound_Container</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NodeInventory/LPNList/@LPN" id="LPN" > 
	<% // Call API "getShipmentContainerList" to get the ShipmentContainerKey 
   String sIsOutboundContainer=resolveValue("xml:/LPN/@IsOutboundContainer");
   String sContainerScmCase = resolveValue("xml:/LPN/@CaseId");
   String sContainerScmPallet = resolveValue("xml:/LPN/@PalletId");
  if(equals("Y",sIsOutboundContainer)){
	   YFCElement containerInput = YFCDocument.parse("<Container/>").getDocumentElement();
        if(!isVoid(sContainerScmCase)){
		containerInput.setAttribute("ContainerScm",sContainerScmCase);
		containerInput.setAttribute("GetLoadAndLoadShipmentContainers","Y");
		}
		if(!isVoid(sContainerScmPallet)){
		containerInput.setAttribute("ContainerScm",sContainerScmPallet);
		containerInput.setAttribute("GetLoadAndLoadShipmentContainers","Y");
		}
        YFCElement containerTemplate = YFCDocument.parse("<Containers   TotalNumberOfRecords=\"\"><Container  ShipmentContainerKey=\"\"/></Containers>").getDocumentElement();
  %>		
		<yfc:callAPI apiName="getShipmentContainerList" inputElement="<%=containerInput%>" templateElement="<%=containerTemplate%>" outputNamespace=""/>
  <%}
	String sRecords=resolveValue("xml:/Containers/@TotalNumberOfRecords");
  %>
    <tr> <!--  make input to getShipmentContainerdetails API by passing the ShipmentContainerKey -->
	     <yfc:makeXMLInput name="shipmentcontainerKey" >
            <yfc:makeXMLKey binding="xml:/Container/@ShipmentContainerKey"  value="xml:/Containers/Container/@ShipmentContainerKey"/>
         </yfc:makeXMLInput>

		<yfc:makeXMLInput name="lpnKey" >
			<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@PalletId" value="xml:/LPN/@PalletId" />
			<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/LPN/@OrganizationCode" />
			<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/NodeInventory/@Node" />
			<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/LPN/@CaseId"  />
		</yfc:makeXMLInput>
		<% YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory");

		   if(nodeElement!=null) nodeElement.setAttribute("ChangeInventoryMode","list");
		  
		%>      
   <yfc:makeXMLInput name="containerInventoryKey">
            <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/NodeInventory/@Node" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ChangeInventoryMode" value="xml:/NodeInventory/@ChangeInventoryMode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ZoneId" value="xml:/LPN/LPNLocation/@ZoneId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/LPN/LPNLocation/@LocationId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/LPN/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" value="xml:/LPN/@PalletId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId" value="xml:/LPN/@CaseId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptHeaderKey" value="xml:/LPN/@ReceiptHeaderKey" />    
    </yfc:makeXMLInput>	
        <td class="checkboxcolumn">
			<input type="checkbox"  name="LPNListEntityKey" yfcMultiSelectCounter='<%=LPNCounter%>' yfcMultiSelectValue1='<%=getValue("NodeInventory", "xml:/NodeInventory/@Node")%>' yfcMultiSelectValue30='<%=getValue("LPN", "xml:/LPN/LPNLocation/@LocationId")%>' yfcMultiSelectValue31='<%=getValue("LPN", "xml:/LPN/@OrganizationCode")%>' yfcMultiSelectValue32='<%=getValue("LPN", "xml:/LPN/@PalletId")%>' yfcMultiSelectValue33='<%=getValue("LPN", "xml:/LPN/@CaseId")%>' yfcMultiSelectValue34='<%=getValue("LPN", "xml:/LPN/Receipt/@ReceiptNo")%>' yfcMultiSelectValue35='<%=getValue("LPN", "xml:/LPN/@ReceiptHeaderKey")%>'
			yfcMultiSelectValue36='<%=getValue("LPN", "xml:/LPN/@IsOutboundContainer")%>' multipleSummaryAttributes='N'
			yHiddenInputName1="LPNKey_<%=LPNCounter%>" yHiddenInputName2="ContainerInventoryKey_<%=LPNCounter%>"  
			yHiddenInputName3="IsOutbound_<%=LPNCounter%>" yHiddenInputName4="ShipmentContainerKey_<%=LPNCounter%>"/>

            <input type="hidden" value='<%=getParameter("containerInventoryKey")%>' name="ContainerInventoryKey_<%=LPNCounter%>" />
            <input type="hidden" value='<%=getParameter("shipmentcontainerKey")%>' name="ShipmentContainerKey_<%=LPNCounter%>"/>
			<input type="hidden" value='<%=getParameter("lpnKey")%>' name="LPNKey_<%=LPNCounter%>"/> 
			<input type="hidden" value='<%=getValue("LPN", "xml:/LPN/@IsOutboundContainer")%>' name="IsOutbound_<%=LPNCounter%>"/> 
        </td>
        <td class="tablecolumn" ><yfc:getXMLValue  binding="xml:/LPN/LPNLocation/@LocationId"/>
		</td>    
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/LPN/@OrganizationCode"/></td> 
		
		<%if(equals("Y",sIsOutboundContainer) && !isVoid(sContainerScmPallet) && equals("1",sRecords)){%>
         <td class="tablecolumn"> <a <%=getDetailHrefOptions("L02", getParameter("shipmentcontainerKey"),"")%>> <yfc:getXMLValue binding="xml:/LPN/@PalletId"/> </a> </td>
		<%}else{%>
        <td class="tablecolumn"> <a <%=getDetailHrefOptions("L01", getParameter("lpnKey"),"")%>> <yfc:getXMLValue binding="xml:/LPN/@PalletId"/> </a> </td>
		<%}%>

		<%if(equals("Y",sIsOutboundContainer) && !isVoid(sContainerScmCase) && equals("1",sRecords)){%>
         <td class="tablecolumn"> <a <%=getDetailHrefOptions("L02", getParameter("shipmentcontainerKey"),"")%>> <yfc:getXMLValue binding="xml:/LPN/@CaseId"/> </a> </td>
		<%}else{%>
        <td class="tablecolumn"> <a <%=getDetailHrefOptions("L01", getParameter("lpnKey"),"")%>> <yfc:getXMLValue binding="xml:/LPN/@CaseId"/> </a> </td>
		<%}%>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/LPN/Receipt/@ReceiptNo"/></td>     
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/LPN/@IsOutboundContainer"/></td>     

    </tr>
    </yfc:loopXML> 			
</tbody>
</table>
</div >
