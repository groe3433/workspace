<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
	   <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>Extract_File_Name</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>CostCenter</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>FunctionalArea</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>WBS</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>TransactionNo</yfc:i18n>
        </td>
<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransExtract/@ExtractFileName")%>">
            <yfc:i18n>Amount</yfc:i18n>
        </td>
		




   </tr>
</thead>
<tbody>
<% 
%>
    <yfc:loopXML binding="xml:/NWCGBillingTransExtractList/@NWCGBillingTransExtract" id="NWCGBillingTransExtract"> 
    <tr> 
        <yfc:makeXMLInput name="PostExtractSequenceKey">
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey" value="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey" />
		</yfc:makeXMLInput>                
        <td class="checkboxcolumn">                     
         <input type="checkbox" value='<%=getParameter("PostExtractSequenceKey")%>' name="EntityKey"/>
        </td>        
    <% 
    %>

		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@ExtractFileName"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@CostCenter"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@FunctionalArea"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@WBS"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@TransactionNo"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGBillingTransExtract/@AmtInDocCurrency"/></td>

		 
    </tr>
    </yfc:loopXML> 
</tbody>
</table>