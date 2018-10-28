<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.XMLUtil" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.w3c.dom.*" %>

<%
	//System.out.println("XML for customer details :"+getRequestDOM());
	String incidentKey = getParameter("incidentKey");
	//System.out.println("incident Key param:"+incidentKey);
	URLDecoder decoder = new URLDecoder();
	String nwcgIncidentXML = decoder.decode(incidentKey,"UTF-8");
	//System.out.println("nwcgIncidentXML :"+nwcgIncidentXML);
	Document doc = XMLUtil.getDocument(nwcgIncidentXML);
	String key = doc.getDocumentElement().getAttribute("IncidentKey");
	//System.out.println("key :::"+key);

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
		//System.out.println("in else");
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
		//System.out.println("element >"+elem.toString());
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