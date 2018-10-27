<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>


<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Locations/Location/@LocationId")%>">
            <yfc:i18n>Location_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Locations/Location/@ZoneId")%>">
            <yfc:i18n>Zone_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Locations/Location/@Node")%>">
            <yfc:i18n>Cache_ID</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML name="Locations" binding="xml:/Locations/@Location" id="Location"> 
    <tr> 
		<yfc:makeXMLInput name="LocationKey">
			<yfc:makeXMLKey binding="xml:/Location/@LocationKey" value="xml:/Location/@LocationKey" />
			<yfc:makeXMLKey binding="xml:/Location/@LocationId" value="xml:/Location/@LocationId" />
			<yfc:makeXMLKey binding="xml:/Location/@ZoneId" value="xml:/Location/@ZoneId" />
            <yfc:makeXMLKey binding="xml:/Location/@Node" value="xml:/Location/@Node" />
        </yfc:makeXMLInput>
		<yfc:makeXMLInput name="LocationPrintKey">
			<yfc:makeXMLKey binding="xml:/Print/Location/@LocationKey" value="xml:/Location/@LocationKey" />
			<yfc:makeXMLKey binding="xml:/Print/Location/@LocationId" value="xml:/Location/@LocationId" />
		</yfc:makeXMLInput>
		
		<td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("LocationKey")%>' name="EntityKey" 
			PrintEntityKey='<%=getParameter("LocationPrintKey")%>'/>
		</td>
        <td class="tablecolumn"><yfc:getXMLValue name="Location" binding="xml:/Location/@LocationId"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="Location" binding="xml:/Location/@ZoneId"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Location" binding="xml:/Location/@Node"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>