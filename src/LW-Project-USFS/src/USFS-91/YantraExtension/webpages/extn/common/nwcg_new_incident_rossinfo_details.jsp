<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<script>
yfcDoNotPromptForChanges(true);
</script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
String strRenderReadOnly = (String)request.getParameter("RenderReadOnly") ; 
boolean bRenderReadOnly = ((strRenderReadOnly != null && strRenderReadOnly.equals("true")) ? true:false );
String strPath = (String)request.getParameter("Path") ;
%>

<table class="view" width="100%">	
<tr>
	<td class="detaillabel" ><yfc:i18n>ROSS_Financial_Code</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"ROSSFinancialCode")%> >
	<label class="protectedtext" id="<%=strPath+"ROSSFinancialCode"%>"><%=resolveValue(strPath+"ROSSFinancialCode")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"ROSSFinancialCode")%> >
	<%}%>
	</td>
	
	<td/>
	<td/>

	<td class="detaillabel" ><yfc:i18n>Request_Initiated_By</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"SystemOfOrigin")%> >
	<label class="protectedtext" id="<%=strPath+"SystemOfOrigin"%>"><%=resolveValue(strPath+"SystemOfOrigin")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"SystemOfOrigin")%> >
	<%}%>
	</td>
	<td>


</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>ROSS_Billing_Organization</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"ROSSBillingOrganization")%> >
	<label class="protectedtext" id="<%=strPath+"ROSSBillingOrganization"%>"><%=resolveValue(strPath+"ROSSBillingOrganization")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"ROSSBillingOrganization")%> >
	<%}%>
	</td>

	<td/>
	<td/>

	<td class="detaillabel" ><yfc:i18n>ROSS_Fiscal_Year</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"ROSSFiscalYear")%> >
	<label class="protectedtext" id="<%=strPath+"ROSSFiscalYear"%>"><%=resolveValue(strPath+"ROSSFiscalYear")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"ROSSFiscalYear	")%> >
	<%}%>
	</td>
	<td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>ROSS_Owning_Agency</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"ROSSOwningAgency")%> >
	<label class="protectedtext" id="<%=strPath+"ROSSOwningAgency"%>"><%=resolveValue(strPath+"ROSSOwningAgency")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"ROSSOwningAgency")%> >
	<%}%>
	</td>

	<td/>
	<td/>

	<td class="detaillabel" ><yfc:i18n>ROSS_Dispatch_Unit_Id</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"RossDispatchUnitId")%> >
	<label class="protectedtext" id="<%=strPath+"RossDispatchUnitId"%>"><%=resolveValue(strPath+"RossDispatchUnitId")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"RossDispatchUnitId")%> >
	<%}%>
	</td>
</tr>
</table>
