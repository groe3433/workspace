<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*,com.yantra.shared.ycp.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<%  YFCElement nodeElement= (YFCElement) request.getAttribute("GetLPNDetails");
	if(nodeElement!=null){ 
		nodeElement.setDateTimeAttribute("FromCreatets", new YFCDate(YCPConstants.YCP_LOW_DATE));
		nodeElement.setDateTimeAttribute("ToCreatets", new YFCDate(YCPConstants.YCP_HIGH_DATE) );
		nodeElement.setAttribute("CreatetsQryType","BETWEEN" );
	}

%> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
 <yfc:makeXMLInput name="locationKey">
     <yfc:makeXMLKey binding="xml:/Location/@Node" value="xml:/GetLPNDetails/@Node" />
     <yfc:makeXMLKey binding="xml:/Location/@LocationId" value="xml:/GetLPNDetails/LPN/LPNLocation/@LocationId" />
 </yfc:makeXMLInput>
<input type="hidden" name="LocEntityKey" value='<%=getParameter("locationKey")%>'/>

<yfc:makeXMLInput name="invAudKey">
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@LocationId" value="xml:/GetLPNDetails/LPN/LPNLocation/@LocationId" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@Node" value="xml:/GetLPNDetails/@Node" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@FromCreatets" value="xml:/GetLPNDetails/@FromCreatets" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@ToCreatets" value="xml:/GetLPNDetails/@ToCreatets" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@CreatetsQryType" value="xml:/GetLPNDetails/@CreatetsQryType" />
 	 <%if(!(isVoid(resolveValue("xml:/GetLPNDetails/LPN/@CaseId")))){%>
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@CaseId"  value="xml:/GetLPNDetails/LPN/@CaseId" />
	 <%}else{%>
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@PalletId"  value="xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId" />
	 <%}%>
</yfc:makeXMLInput>


<input type="hidden" name="WmsInvAudKey" value='<%=getParameter("invAudKey")%>'/>

     <yfc:makeXMLInput name="invSerialKey">
		<yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode"/> 
		<yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/GetLPNDetails/@Node"/> 
		<yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId"	value="xml:/GetLPNDetails/LPN/LPNLocation/@LocationId" />
		<%if(!(isVoid(resolveValue("xml:/GetLPNDetails/LPN/@CaseId")))) {%>
		<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId" value="xml:/GetLPNDetails/LPN/@CaseId"/>
		<%}else{%>
		<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" value="xml:/GetLPNDetails/LPN/@PalletId"/>
		<%}%>
	 </yfc:makeXMLInput>


<input type="hidden" name="WmsInvSerialKey" value='<%=getParameter("invSerialKey")%>'/>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/@PalletId"/>
		</td>
        <td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
        <td class="protectedtext">
				<yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/@CaseId"/>
		</td>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext">
			<%if(isVoid(resolveValue("xml:/GetLPNDetails/@EnterpriseCode"))){%>
				<yfc:getXMLValue binding="xml:/GetLPNDetails/@InventoryOrganizationCode"/>
			<%}else{%>
				<yfc:getXMLValue binding="xml:/GetLPNDetails/@EnterpriseCode"/>
			<%}%>
		</td>

	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/GetLPNDetails/@Node"/></td>
        <td class="detaillabel" ><yfc:i18n>Location</yfc:i18n></td>
        <td class="protectedtext"><a <%=getDetailHrefOptions("L02",getParameter("locationKey"),"")%> >                     	  <yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/LPNLocation/@LocationId"/></a></td>
        <td class="detaillabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue  binding="xml:/GetLPNDetails/LPN/Receipt/@ReceiptNo"/></td>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Parent_Container_ID</yfc:i18n></td>
        <td class="protectedtext">
        	<yfc:makeXMLInput name="LPNKey" >
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/GetLPNDetails/LPN/ParentLPN/@CaseId" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/GetLPNDetails/@Node" />

			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
				<yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/ParentLPN/@CaseId"/>
			</a>
		</td>
	
        <td class="detaillabel" ><yfc:i18n>Outer_Most_Container_ID</yfc:i18n></td>
        <td class="protectedtext">
		<%	if (!(isVoid(resolveValue("xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId"))))  {
				if(!(equals(resolveValue("xml:/GetLPNDetails/LPN/@CaseId"),resolveValue("xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId") ) ) )	{	%>
					<yfc:makeXMLInput name="LPNKey" >
						<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId" />
						<%if(isVoid(resolveValue("xml:/GetLPNDetails/@EnterpriseCode"))){%>
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@InventoryOrganizationCode"/>
						<%}else{%>
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode"/>
						<%}%>
						<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/GetLPNDetails/@Node" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
					    <yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId"/>
					</a>
			<%	} else {	%>
				    <yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId"/>
			<%	}	
			} else if (!(isVoid(resolveValue("xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId")))) {	
				if(!(equals(resolveValue("xml:/GetLPNDetails/LPN/@PalletId"), resolveValue("xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId") ) ) )	{	%>
					<yfc:makeXMLInput name="LPNKey" >
						<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@PalletId" value="xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId" />
						<%if(isVoid(resolveValue("xml:/GetLPNDetails/@EnterpriseCode"))){%>
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@InventoryOrganizationCode"/>
						<%}else{%>
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode"/>
						<%}%>
						<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/GetLPNDetails/@Node" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
					    <yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId"/>
					</a>
			<%	} else {	%>
				    <yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/OuterMostLPN/@PalletId"/>
			<%	}
			} %>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Is_Outbound_Container</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/GetLPNDetails/LPN/@IsOutboundContainer"/>
		</td>
<%		if (!equals(resolveValue("xml:/GetLPNDetails/LPN/@CaseId"),"")) { %>
			<input name="ContainerScm" type="hidden" value="<%=resolveValue("xml:/GetLPNDetails/LPN/@CaseId")%>"/> 
<%		} else { %>
			<input name="ContainerScm" type="hidden" value="<%=resolveValue("xml:/GetLPNDetails/LPN/@PalletId")%>"/>
<%		} %>
	</tr>
</table>
