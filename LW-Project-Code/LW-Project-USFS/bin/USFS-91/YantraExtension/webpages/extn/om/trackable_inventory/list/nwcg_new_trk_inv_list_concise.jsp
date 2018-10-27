<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
	   <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@SerialNo")%>">
            <yfc:i18n>Trackable_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@ItemID")%>">
            <yfc:i18n>Cache_Item</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@ItemShortDescription")%>">
            <yfc:i18n>Cache_Item_Description</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Order/@SystemNumber")%>">
            <yfc:i18n>System_Number</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@SerialStatusDesc")%>">
            <yfc:i18n>Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@StatusIncidentNo")%>">
            <yfc:i18n>Order_No</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGTrackableItem/@StatusCacheID")%>">
            <yfc:i18n>Cache_ID</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGTrackableItemList/@NWCGTrackableItem" id="NWCGTrackableItem"> 
    <tr> 
        <yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/NWCGTrackableItem/@TrackableItemKey" value="xml:/NWCGTrackableItem/@TrackableItemKey" />
		</yfc:makeXMLInput>                
        <td class="checkboxcolumn">                     
         <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey"/>
         </td>        
		<td class="tablecolumn">
		<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
			<yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@SerialNo"/>
        </a>               
		</td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@ItemShortDescription"/></td>
		 <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@SystemNo"/></td>
        <td class="tablecolumn">(<yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@SerialStatus"/>)&nbsp;&nbsp;&nbsp;<yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@SerialStatusDesc"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@StatusIncidentNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGTrackableItem:/NWCGTrackableItem/@StatusCacheID"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>