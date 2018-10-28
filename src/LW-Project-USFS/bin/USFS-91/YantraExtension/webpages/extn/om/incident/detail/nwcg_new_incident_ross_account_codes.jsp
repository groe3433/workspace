<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="table" width="100%" editable="false">
	<thead>
	   <tr> 
	        <td class="tablecolumnheader">
	            <yfc:i18n>Financial_Code</yfc:i18n>
	        </td>
			<td class="tablecolumnheader" >
	            <yfc:i18n>Fiscal_Year</yfc:i18n>
	        </td>
	        <td class="tablecolumnheader" >
	            <yfc:i18n>Owning_Agency_Name</yfc:i18n>
	        </td>
	        <td class="tablecolumnheader" >
	            <yfc:i18n>Primary_Indicator</yfc:i18n>
	        </td>
	        <td class="tablecolumnheader">
	            <yfc:i18n>Last_Financial_Code</yfc:i18n>
	        </td>
	   </tr>
	</thead>
	<tbody>
		<%
		java.util.ArrayList list = getLoopingElementList("xml:/NWCGIncidentOrder/NWCGRossAccountCodesList/@NWCGRossAccountCodes");
		for(int i=0; i < list.size(); i++){
		    com.yantra.yfc.dom.YFCElement elem = (com.yantra.yfc.dom.YFCElement)list.get(i);
		%>
	  	<tr> 
	        <td class="tablecolumn"><%=elem.getAttribute("FinancialCode") %></td>
			<td class="tablecolumn"><%=elem.getAttribute("FiscalYear") %></td>
	        <td class="tablecolumn"><%=elem.getAttribute("OwningAgencyName") %></td>
	        <td class="tablecolumn"><%=elem.getAttribute("PrimaryIndicator") %></td>
	        <td class="tablecolumn"><%=elem.getAttribute("LastFinancialCode") %></td>
	    </tr>
	 <% } %> 
	</tbody>
</table>
