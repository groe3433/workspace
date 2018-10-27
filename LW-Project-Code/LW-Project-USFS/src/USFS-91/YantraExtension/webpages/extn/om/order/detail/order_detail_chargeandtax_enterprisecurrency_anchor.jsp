<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="DisplayInEnterpriseCurrency" value="Y"/>
</jsp:include>
</td>
</tr>
<%	if (isTrue("xml:/Order/@HasProductLines") ) { //add following inner panel only if there are any Provided service lines in the order %>
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
<jsp:param name="DisplayInEnterpriseCurrency" value="Y"/>
</jsp:include>
</td>
</tr>
<% }
if (isTrue("xml:/Order/@HasServiceLines") ) { //add following inner panel only if there are any Provided service lines in the order %>
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
<jsp:param name="DisplayInEnterpriseCurrency" value="Y"/>
</jsp:include>
</td>
</tr>
<% }
if (isTrue("xml:/Order/@HasDeliveryLines") ) { 	//add following inner panel only if there are any Delivery service lines in the order %>
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I05"/>
<jsp:param name="DisplayInEnterpriseCurrency" value="Y"/>
</jsp:include>
</td>
</tr>
<% } %>
</table>
