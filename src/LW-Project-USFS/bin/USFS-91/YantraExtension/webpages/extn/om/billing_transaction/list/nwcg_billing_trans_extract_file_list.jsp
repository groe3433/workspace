<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="tablecolumnheader"></td>
	   	<td class="tablecolumnheader">
            <yfc:i18n>Extract_File_Name</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGBillingTransExtractList/@NWCGBillingTransExtract" id="NWCGBillingTransExtract"> 
    <tr> 
        <yfc:makeXMLInput name="ExtractSeqKey">
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey" value="xml:/NWCGBillingTransExtract/@PostExtractSequenceKey" />
		</yfc:makeXMLInput>                
       <td class="tablecolumn">
       <img class="icon" onClick="setLookupValue(this.value)"  value="<%=resolveValue("xml:/NWCGBillingTransExtract/@ExtractFileName")%>" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />
       </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransExtract:/NWCGBillingTransExtract/@ExtractFileName"/></td>
        </td> 
    </tr>
    </yfc:loopXML> 
</tbody>
</table>