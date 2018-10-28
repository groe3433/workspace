<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<% YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser"); %>
<table class="view">
<tr>
<td class="searchlabel" ><yfc:i18n>Node</yfc:i18n></td>
</tr>
<tr>
<td class=tablecolumn noWrap><input readonly=true class=protectedtext size=10 name="xml:/NWCGReservations/@Node" Value="<%=curUsr.getAttribute("Node")%>"></td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Reservation ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGReservations/@ReservationIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGReservations/@ReservationIDQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGReservations/@ReservationID")%>/>
</td>
</tr>

<tr>
<td class="searchlabel" ><yfc:i18n>Item ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGReservations/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGReservations/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGReservations/@ItemID") %> />
		<IMG class=lookupicon onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item')" alt="Search for Item" src="/yantra/console/icons/lookup.gif">
    </td>
</tr>

</table>
