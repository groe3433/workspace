<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="org.w3c.dom.*" %>


<input type="hidden" <%=getTextOptions("xml:/NWCGBillingTransExtract/@PostExtractSequenceKey")%> />
<input type="hidden" name="xml:/NWCGBillingTransExtract/@ExtractFileName" value='<%=resolveValue("xml:/NWCGBillingTransExtract/@ExtractFileName")%>'/>
<% 
String ExtractFileName = resolveValue("xml:/NWCGBillingTransExtract/@ExtractFileName");
%>



<table class="view" width="100%">

<td class="detaillabel" align="left">
<yfc:i18n>
The Re-Extract for file "<%=ExtractFileName%>" was successfully executed.
</yfc:i18n>
</td>
<td>
</td>
</table>
