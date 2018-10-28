<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/yfcscripts/yfc.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
	String sOHKey = resolveValue("xml:/OrderLine/@OrderHeaderKey");
	if(isVoid(sOHKey) )	{
		sOHKey = resolveValue("xml:/Order/@OrderHeaderKey");
	}

	String sOrderLineKey = resolveValue("xml:OrderLineDetail:/OrderLine/@OrderLineKey");
%>

<script language="javascript">
	function showLineDetail()	{
		<%	YFCDocument orderDoc = YFCDocument.createDocument("OrderLineDetail");
			orderDoc.getDocumentElement().setAttribute("OrderLineKey", sOrderLineKey);
			orderDoc.getDocumentElement().setAttribute("OrderHeaderKey", sOHKey);

			request.setAttribute("OrderLineDetail", orderDoc.getDocumentElement()  );
		%>
        window.CloseOnRefresh = "Y";
		yfcShowDetailPopupWithParams('', '', '900', '550', null, 'servicerequest' , '<%=orderDoc.getDocumentElement().getString(false)%>' );
        window.close();
	}

	<%	if (!isVoid(sOrderLineKey)) {	%>
			window.attachEvent("onload", showLineDetail);
	<%	}%>
</script>

<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="lookupiconheader" sortable="no">&nbsp;
			<input type="hidden" name="xml:/OrderLine/@OrderHeaderKey" value='<%=sOHKey%>'/>
			<input type="hidden" name="xml:/OrderLine/@ModificationReasonCode" />
            <input type="hidden" name="xml:/OrderLine/@ModificationReasonText"/>
            <input type="hidden" name="xml:/OrderLine/@Override" value="N"/>

            <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
		</td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Short_Description</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML name="ItemList" binding="xml:/ItemList/@Item" id="Item"> 
    <tr> 
		<yfc:makeXMLInput name="itemEntityKey">
			<yfc:makeXMLKey binding="xml:/OrderLine/@OrderLineKey" value="xml:/OrderLineDetail/@OrderLineKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLine/@OrderHeaderKey" value="xml:/OrderLineDetail/@OrderHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLine/Item/@ItemID" value="xml:/Item/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/OrderLine/Item/@UnitOfMeasure" value="xml:/Item/@UnitOfMeasure"/>
		</yfc:makeXMLInput>
        <td class="tablecolumn">
			<input type="checkbox" value='<%=getParameter("itemEntityKey")%>' name="EntityKey" />
        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Item/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Item/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/Item/PrimaryInformation/@ShortDescription"/></td>
    </tr>
    </yfc:loopXML> 
	<input	name="CurrentEntityKey" type='hidden' value='<%=getParameter("itemEntityKey")%>' />        
</tbody>
</table>
