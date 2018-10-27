<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="Javascript" >
yfcDoNotPromptForChanges(true);
yfcChangeOrderLineKeyName('xml:/Order/OrderLines/OrderLine/@OrderLineKey');
</script>
<% //yfcDoNotPromptForChanges(true); //Used because we are deliberately changing Order Line Key binding and therefore if we exit without saving, we get a wrong pop up prompting changes have been made

//following code is being added to handle condition where user has override permissions, that the override flag will get passed into the changeOrder xml. Normal enterModificationReason will not set the override flag properly in this case as no input Value is being changed in this screen
if (isModificationAllowedWithModType("ADD_LINE", "xml:/OrderLine/Order/AllowedModifications")) {	%>
<input type="hidden" name="UserHasOverridePermissionForADD_LINE" value="Y"/>
<% }	%>
<table class="table" cellspacing="0" width="100%">
<thead>
<tr>
<td class="checkboxheader" sortable="no" style="width:10px">
<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckFirstLevel(this);"/>
</td>
<td class="tablecolumnheader" sortable="no" style="width:30px"><yfc:i18n>Options</yfc:i18n></td>
<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:AdditionalServiceItem:/OrderLine/AdditionalServiceItems/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:AdditionalServiceItem:/OrderLine/AdditionalServiceItems/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:AdditionalServiceItem:/OrderLine/AdditionalServiceItems/Item/PrimaryInformation/@ShortDescription")%>"><yfc:i18n>Item_Description</yfc:i18n></td>
<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:AdditionalServiceItem:/OrderLine/AdditionalServiceItems/Item/@Price")%>"><yfc:i18n>Price</yfc:i18n></td>
</tr>
</thead>
<tbody>
<%String className="oddrow";%>
<yfc:loopXML binding="xml:AdditionalServiceItem:/OrderLine/AdditionalServiceItems/@Item" id="Item">
<%	
if (equals(getValue("Item","xml:/Item/@ItemGroupCode"),"PS") )
//display line in this inner panel only if it is Item Group Code Provided service
{
if (className.equals("oddrow"))
className="evenrow";
else
className="oddrow";
%>
<tr class='<%=className%>'>
<input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey","xml:/OrderLine/@OrderHeaderKey")%> />
<td class="checkboxcolumn"><input type="checkbox" name="chkEntityKeyPS" yfcMultiSelectServiceCounter='<%=ItemCounter%>'
yfcMultiSelectValue2='<%=ItemCounter%>'
yfcMultiSelectValue3='<%=getValue("Item","xml:Item/@ItemID")%>' yfcMultiSelectValue4='<%=getValue("Item","xml:Item/@UnitOfMeasure")%>' yfcMultiSelectValue5='<%=getValue("OrderLine","xml:/OrderLine/@OrderLineKey")%>' yfcMultiSelectValue6='<%=ItemCounter%>'
yfcMultiSelectValue7='1'
<%
%>
onclick="setChildCheckBoxes('chkEntityKeyPS','yfcMultiSelectServiceCounter','chkEntityPSOPT','yfcOptionBelongsToLineNo',false,true,true)" /></td>
<td>
<%	YFCElement ItemElem = (YFCElement)pageContext.getAttribute("Item");
YFCElement optionsElem = ItemElem.getChildElement("ItemOptions");
if (optionsElem != null)
{
YFCElement optionElem = optionsElem.getChildElement("ItemOption");
if (optionElem != null)
{	%>
<img  onclick="expandCollapseDetails('<%="optionSet_" + ItemCounter%>','<%=getI18N("Click_To_See_Options")%>','<%=getI18N("Click_To_Hide_Options")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Options")%> />
<input type="hidden" name="HasAtleastOneOption" value="true" />
<%	}
}	%>
</td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/Item/@ItemID" />
</td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/Item/@UnitOfMeasure" />
</td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ShortDescription" />
</td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/Item/@Price" />&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<%
if (optionsElem != null)
{
YFCElement optionElem = optionsElem.getChildElement("ItemOption");
if (optionElem != null)
{
request.setAttribute("ItemOptions", optionsElem);	%>
<tr id='<%="optionSet_" + ItemCounter%>' class='<%=className%>' style="display:none">
<td colspan="6" >
<jsp:include page="/om/orderline/detail/orderline_detail_serviceitemoptions.jsp" flush="true">
<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(ItemCounter)%>'/>
</jsp:include>
</td>
</tr>
<%	}
}	%>
<%	}   //end of if loop %>
</yfc:loopXML>
</tbody>
</table>
