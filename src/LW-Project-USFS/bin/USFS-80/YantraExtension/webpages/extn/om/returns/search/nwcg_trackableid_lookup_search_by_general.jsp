<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>


<!--<input type="hidden" name="xml:/NWCGBillingTransaction/@FromTransDate" value=""/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@ToTransDate" value=""/>  !-->

<% String ItemId = resolveValue("xml:/Serial/InventoryItem/@ItemID");
   String PItemId = resolveValue("xml:/Serial/InventoryItem/@ParentItemID");
   String IncidentNo = resolveValue("xml:/Serial/@IncidentNo");
   String IncidentYear = resolveValue("xml:/Serial/@IncidentYear");
   String IssueNo = resolveValue("xml:/Serial/@IssueNo");
   String CacheId = resolveValue("xml:/Serial/@CacheID");
   String NoOfSecSerials = resolveValue("xml:/Serial/@NoOfSecSerials");
%>

<table class="view">

<tr>
    <td class="searchlabel" ><yfc:i18n>Trackable_ID</yfc:i18n></td>
</tr>

<input type="hidden" name="xml:/Serial/@AtNode" value="N" />
<input type="hidden" name="xml:/Serial/InventoryItem/@ItemID" value='<%=ItemId%>' />
<input type="hidden" name="xml:/Serial/InventoryItem/@ParentItemID" value='<%=PItemId%>' />
<input type="hidden" name="xml:/Serial/@IncidentNo" value='<%=IncidentNo%>' />
<input type="hidden" name="xml:/Serial/@IncidentYear" value='<%=IncidentYear%>' />
<input type="hidden" name="xml:/Serial/@IssueNo" value='<%=IssueNo%>' />
<input type="hidden" name="xml:/Serial/@CacheID" value='<%=CacheId%>' />
<input type="hidden" name="xml:/Serial/@NoOfSecSerials" value='<%=NoOfSecSerials%>' />

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Serial/@SerialNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Serial/@SerialNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Serial/@SerialNo")%>/>
		
    </td>
</tr>

</table>

