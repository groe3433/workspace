<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.XMLUtil" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.w3c.dom.*" %>

<%
	String incidentKey = getParameter("incidentKey");
	URLDecoder decoder = new URLDecoder();
	String nwcgIncidentXML = decoder.decode(incidentKey,"UTF-8");
	Document doc = XMLUtil.getDocument(nwcgIncidentXML);
	String key = doc.getDocumentElement().getAttribute("IncidentKey");
	if(StringUtil.isEmpty(key)){
%>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="tablecolumnheader">
            <yfc:i18n>Type</yfc:i18n>
        </td>
		<td class="tablecolumnheader" >
            <yfc:i18n>Contact</yfc:i18n>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Phone Number / Contact Info</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
</tbody>
</table>

<% }else{
%>
<yfc:callAPI apiID="AP1"/>

<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="tablecolumnheader">
            <yfc:i18n>Type</yfc:i18n>
        </td>
		<td class="tablecolumnheader" >
            <yfc:i18n>Contact</yfc:i18n>
        </td>
        <td class="tablecolumnheader" >
            <yfc:i18n>Phone Number / Contact Info</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
<%
	java.util.ArrayList list = getLoopingElementList("xml:/NWCGIncidentOrderConInfoList/@NWCGIncidentOrderConInfo");
	for(int a=0;a<list.size();a++){
	    com.yantra.yfc.dom.YFCElement elem = (com.yantra.yfc.dom.YFCElement)list.get(a);
%>
  	<tr> 
        <td class="tablecolumn"><%=elem.getAttribute("ContactType") %></td>
		<td class="tablecolumn"><%=elem.getAttribute("Contact") %></td>
        <td class="tablecolumn"><%=elem.getAttribute("ContactInfo") %></td>
    </tr>
 <% } %> 
</tbody>
</table>
<% } %>