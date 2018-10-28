<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">


<tr>
    <td class="searchlabel" ><yfc:i18n>Extract_File_Name</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	  <select name="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType" class="combobox" >
           <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
               name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType"/>
      </select>
		<input type="text" class="unprotectedinput" maxLength=100 size=40 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ExtractFileName")%>/>
		<img class="lookupicon" onclick="callLookup(this,'NWCGBTransExtractLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Extract_File_Name")%>/>
    </td>
</tr>

</table>