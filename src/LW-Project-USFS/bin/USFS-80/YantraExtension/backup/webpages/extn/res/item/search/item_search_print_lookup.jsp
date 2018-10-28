<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="ShowNode" value="false"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:yfcSearchCriteria:/Item/@CallingOrganizationCode"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
		<jsp:param name="ScreenType" value="search"/>
    </jsp:include>

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
    <td class="searchlabel" ><yfc:i18n>Default_Product_Class</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/PrimaryInformation/@DefaultProductClass" class="combobox" >
            <yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
            name="CodeValue" value="CodeValue" selected="xml:/Item/PrimaryInformation/@DefaultProductClass"/>
        </select>
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
<tr>
    <td class="searchlabel" ><yfc:i18n>Master_Catalog_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/PrimaryInformation/@MasterCatalogIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/PrimaryInformation/@MasterCatalogIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/PrimaryInformation/@MasterCatalogID") %> />            
    </td>
</tr>
   
<tr>
    <td class="searchlabel" ><yfc:i18n>Global_Item_Id</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/@GlobalItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/@GlobalItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/@GlobalItemID") %> />            
    </td>
	<input type="hidden" name="xml:/Item/@IsShippingCntr" value='N'/>
</tr>

</table>
