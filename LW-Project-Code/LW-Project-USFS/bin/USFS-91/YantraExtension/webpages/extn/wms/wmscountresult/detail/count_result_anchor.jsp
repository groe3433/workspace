<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%
int total=0;
int item=0;
%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >	
<yfc:loopXML binding="xml:/CountResultList/@SummaryResultList" id="SummaryResultList"> 
<%if(!isVoid(resolveValue("xml:/SummaryResultList/CountResult/@ItemID"))){
	item++;
	}
	total++;
%>
</yfc:loopXML> 
<% if(item>0){%>
	<tr>
		<td colspan="3" valign="top">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
<%} if((total-item)>0){%>
	<tr>
		<td colspan="3" valign="top">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
<%}%>
<% if((total==0)&&(item==0)){%>

	<tr>
		<td colspan="3" valign="top">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td colspan="3" valign="top">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
<%}%>
</table>  
