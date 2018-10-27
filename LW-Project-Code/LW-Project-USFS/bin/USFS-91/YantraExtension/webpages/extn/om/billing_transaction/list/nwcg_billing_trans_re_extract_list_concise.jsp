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





   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGBillingTransExtractList/@NWCGBillingTransExtract" id="NWCGBillingTransExtract"> 
    <tr> 
        <yfc:makeXMLInput name="ExtractSeqKey">
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransExtract/@ExtractFileName" value="xml:/NWCGBillingTransExtract/@ExtractFileName" />
		</yfc:makeXMLInput>                
        <td class="checkboxcolumn">                     
         <input type="checkbox" value='<%=getParameter("ExtractSeqKey")%>' name="EntityKey"/>
        </td>        
        
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@ExtractFileName"/></td>
 
		</td> 
    </tr>
    </yfc:loopXML> 
</tbody>
</table>