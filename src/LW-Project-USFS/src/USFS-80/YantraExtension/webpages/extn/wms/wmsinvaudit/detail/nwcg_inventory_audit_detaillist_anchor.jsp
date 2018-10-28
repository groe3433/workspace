<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<% 
int total=0;
int item=0;
String divRequired="Y"; 
String serachcriteria = (String) request.getAttribute("SearchCriteriaIs");
String countsearch = (String) request.getAttribute("ModeType");
if(serachcriteria!=null)
{
if (equals("ByLPN",serachcriteria)) {
	request.setAttribute("SearchCriteriaIs","ByLPN");
}
}
%>
<%if(!(isVoid(countsearch)) && equals("Count",countsearch)) { %>
<yfc:loopXML binding="xml:LocationInventoryAudit:/LocationInventoryAudits/@LocationInventoryAudit" id="LocationInventoryAudit"> 
<%if(!(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID")))){
	item++;
	}
	total++;
%>
</yfc:loopXML> 
<% if(item>0){%>
<% if((total-item)>0){
		divRequired="Y";
   } 
   else {
		divRequired="N";
}%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I03"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%} if((total-item)>0){%>
<% if(item>0){
	divRequired="Y";
	} 
	else{
		divRequired="N";
}%>
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%}%>
<% if((total==0)&&(item==0)){%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I03"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%}%>
<%}else { %>
<yfc:loopXML binding="xml:LocationInventoryAudit:/LocationInventoryAudits/@LocationInventoryAudit" id="LocationInventoryAudit"> 
<%if(!(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID")))){
	item++;
	}
	total++;
%>
</yfc:loopXML>
<% if(item>0){%>
<% if((total-item)>0){
		divRequired="Y";
   } 
   else {
		divRequired="N";
}%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%} if((total-item)>0){%>
<% if(item>0){
	divRequired="Y";
	} 
	else{
		divRequired="N";
}%>
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%}%>
<% if((total==0)&&(item==0)){%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
			<jsp:param name="DivRequired" value="<%=divRequired%>"/>
        </jsp:include>
    </td>
</tr>
<%}%>
<%}%>
</table>  



