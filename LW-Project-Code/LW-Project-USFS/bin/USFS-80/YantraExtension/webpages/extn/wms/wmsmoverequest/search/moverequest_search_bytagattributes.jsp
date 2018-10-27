<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<table width="100%" class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="ShowNode" value="true"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@EnterpriseCode"/>
		<jsp:param name="NodeBinding" value="xml:/MoveRequest/@Node"/>
        <jsp:param name="RefreshOnNode" value="true"/>
        <jsp:param name="EnterpriseListForNodeField" value="true"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
		<jsp:param name="ScreenType" value="search"/>
</jsp:include>
<yfc:callAPI apiID="AP3"/>
<yfc:callAPI apiID="AP4"/>
<yfc:callAPI apiID="AP5"/>
<% 
	YFCElement activityListElem = (YFCElement) request.getAttribute("ActivityGroupList");
	for(Iterator listItr = activityListElem.getChildren();listItr.hasNext();) {
		YFCElement activityElem = (YFCElement)listItr.next();
		String sActivityGroup = activityElem.getAttribute("ActivityGroupId");
		if( sActivityGroup.equals("RETRIEVAL")|| sActivityGroup.equals("PUTAWAY")) {
			activityListElem.removeChild(activityElem);
		}
	}
	request.setAttribute("ActivityGroupList",activityListElem);
%>
<tr>
    <td class="searchlabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>
</tr>
<tr >
     <td class="searchcriteriacell" nowrap="true">
       <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@FromActivityGroup")%> >
            <yfc:loopOptions binding="xml:ActivityGroupList:/BaseActivityGroupList/@BaseActivityGroup" 
                name="ActivityGroupName" isLocalized="Y" value="ActivityGroupId" selected="xml:/MoveRequest/@FromActivityGroup"/>
        </select>   
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>For_Activity_Code</yfc:i18n></td>
</tr>
<tr >
     <td class="searchcriteriacell" nowrap="true">
       <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@ForActivityCode")%> >
            <yfc:loopOptions binding="xml:ActivityCodeList:/Activities/@Activity" 
                name="ActivityCode" value="ActivityCode" selected="xml:/MoveRequest/@ForActivityCode"/>
        </select>   
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemIdQryType") %> >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemId") %> />
		<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
        <img class="lookupicon" onclick="callItemLookup('xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemId','xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ProductClass', 'xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ProductClass")%> >
            <yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
                name="CodeValue" value="CodeValue" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ProductClass"/>
        </select>    
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@UnitOfMeasure")%> class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Tag_#</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TagNumberQryType") %> >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TagNumberQryType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TagNumber") %> />
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Serial_#</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">    
		<select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SerialNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SerialNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SerialNo") %> />
    </td>
</tr>
   <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
    <tr>
		<td>
			<fieldset>
				<jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
	                <jsp:param name="NoOfColumns" value='1'/>
	                <jsp:param name="Modifiable" value='true'/>
	                <jsp:param name="LabelTDClass" value='searchlabel'/>
	                <jsp:param name="InputTDClass" value='searchcriteriacell'/>
	                <jsp:param name="BindingPrefix" value='xml:yfcSearchCriteria:/MoveRequest/MoveRequestLines/MoveRequestLine/MoveRequestLineTag'/>
	            </jsp:include>
	      	</fieldset>
		</td>
    </tr>
   </yfc:hasXMLNode>
</table>