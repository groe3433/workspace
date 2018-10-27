<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<table class="table">
<thead>
    <tr> 
		<td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Source_Location</yfc:i18n>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Target_Location</yfc:i18n>
        </td>        
		<td class="tablecolumnheader" >
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>        
        <td class="tablecolumnheader" >
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>


		<td class="tablecolumnheader" >
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>		
		<td class="tablecolumnheader" >
            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" >
            <yfc:i18n>Released</yfc:i18n>
        </td>		
		<td class="tablecolumnheader">
            <yfc:i18n>Is_Cancelled</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>	
	<yfc:loopXML binding="xml:/MoveRequest/MoveRequestLines/@MoveRequestLine" id="MoveRequestLine"> 
	<%
		if(isVoid(resolveValue("xml:/MoveRequestLine/@ItemId")))
		{
	%>
    <tr>
		<yfc:makeXMLInput name="moveRequestLineKey">
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/MoveRequest/@MoveRequestKey" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@MoveRequestLineKey" value="xml:/MoveRequestLine/@MoveRequestLineKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestNo" value="xml:/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Node" value="xml:/MoveRequest/@Node" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@FromActivityGroup" value="xml:/MoveRequest/@FromActivityGroup" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Priority" value="xml:/MoveRequest/@Priority" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@RequestUserId" value="xml:/MoveRequest/@RequestUserId" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@HasExceptions" value="xml:/MoveRequest/@HasExceptions" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Status" value="xml:/MoveRequest/@Status" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@StartNoEarlierThan" value="xml:/MoveRequest/@StartNoEarlierThan" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@FinishNoLaterThan" value="xml:/MoveRequest/@FinishNoLaterThan" />
        </yfc:makeXMLInput>

	
		<td class="checkboxcolumn">
            <input type="checkbox" name="chkEntityKeyCon" value='<%=getParameter("moveRequestLineKey")%>' yfcMultiSelectCounter='<%=MoveRequestLineCounter%>'/>
        </td>		
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@SourceLocationId"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@TargetLocationId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@EnterpriseCode"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@PalletId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@CaseId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/Receipt/@ReceiptNo"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ReleasedFlag"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@CancelledFlag"/>
        </td>
		<input type="hidden" name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_10000<%=MoveRequestLineCounter%>/@MoveRequestLineKey" value='<%=resolveValue("xml:/MoveRequestLine/@MoveRequestLineKey")%>'/>	
			<input type="hidden" name="xml:/JS/JSLine_<%=MoveRequestLineCounter%>/@MoveRequestLineLPNKey" value='<%=resolveValue("xml:/MoveRequestLine/@MoveRequestLineKey")%>'/>		
	
    </tr>
	<%
		}
	%>
    </yfc:loopXML> 	
	
</tbody>
</table>