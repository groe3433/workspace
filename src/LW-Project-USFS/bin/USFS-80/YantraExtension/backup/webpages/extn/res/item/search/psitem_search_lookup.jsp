<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<table class="view">

<%
	String sIsStandaloneService = getParameter("IsStandaloneService");
	if(isVoid(sIsStandaloneService) )	{
		sIsStandaloneService = resolveValue("xml:/Item/PrimaryInformation/@IsStandaloneService");
	}
	String sOHKey = resolveValue("xml:/Order/@OrderHeaderKey");

	YFCElement itemGroupCodeElem = YFCDocument.createDocument("ItemGroupCode").getDocumentElement();
	request.setAttribute("ItemGroupCode", itemGroupCodeElem);
	itemGroupCodeElem.setAttribute("ItemGroupCode", "PS");

    String callingOrgCode = getValue("Item", "xml:/Item/@CallingOrganizationCode");
	if(isVoid(callingOrgCode)){
		callingOrgCode = getValue("CurrentOrganization", getSelectedOrgCodeValue("xml:/Item/@CallingOrganizationCode"));
	}
%>

<script language="javascript">
	window.dialogArguments.parentWindow.defaultOrganizationCode = "<%=callingOrgCode%>";
</script>

<tr>
	<td>
		<input type="hidden" name="xml:/Order/@OrderHeaderKey" value='<%=sOHKey%>'/>
		<input type="hidden" name="xml:/Item/@ItemGroupCode" value='PS'/>
		<input type="hidden" name="xml:/Item/PrimaryInformation/@IsStandaloneService" value='<%=sIsStandaloneService%>'/>
		<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getParameter("hiddenDraftOrderFlag")%>'/>
	</td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Organization</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
		<input type="text" class="protectedinput" contenteditable="false" <%=getTextOptions("xml:/Item/@CallingOrganizationCode", callingOrgCode)%>/>
    </td>
</tr>

    <% // Now call the APIs that are dependent on the calling organization code %>
	<yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/@ItemID") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/Item/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Item/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Short_Description</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/PrimaryInformation/@ShortDescriptionQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/PrimaryInformation/@ShortDescriptionQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/PrimaryInformation/@ShortDescription") %> />
    </td>
</tr>

</table>
