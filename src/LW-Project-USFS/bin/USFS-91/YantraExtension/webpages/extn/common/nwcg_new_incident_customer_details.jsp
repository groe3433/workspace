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

<yfc:callAPI apiID="AP1"/> <!-- getCustomerDetails  -->

<%
String strRenderReadOnly = (String)request.getParameter("RenderReadOnly") ; 
boolean bRenderReadOnly = ((strRenderReadOnly != null && strRenderReadOnly.equals("true")) ? true:false );
String strPath = (String)request.getParameter("Path") ;
%>

<table class="view" width="100%">
<tr>
	<td class="detaillabel" ><yfc:i18n>Unit_Type</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"UnitType")%> >
	<label class="protectedtext" id="<%=strPath+"UnitType"%>"><%=resolveValue(strPath+"UnitType")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"UnitType")%> >
	<%}%>
	</td>
	<td class="detaillabel" ><yfc:i18n>Department</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"Department")%> >
	<label  class="protectedtext"   id="<%=strPath+"Department"%>"> <%=resolveValue(strPath+"Department")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"Department")%> >
	<%}%>
	</td>
	<td class="detaillabel" ><yfc:i18n>Agency</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"Agency")%> >
	<label class="protectedtext" id="<%=strPath+"Agency"%>"><%=resolveValue(strPath+"Agency")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"Agency")%> >
	<%}%>
	</td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>GACC</yfc:i18n></td>
	<td>
	<%if(bRenderReadOnly){%>
	<input type="hidden" <%=getTextOptions(strPath+"GACC")%> >
	<label class="protectedtext" id="<%=strPath+"GACC"%>"><%=resolveValue(strPath+"GACC")%> </label>
	<%}else{%>
	<input class="unprotectedinput" type="text" <%=getTextOptions(strPath+"GACC")%> >
	<%}%>
	</td>
</tr>
</table>
