<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.YFCException" %>
<%@ page import="com.yantra.yfc.dom.YFCElement" %>
<%@ page import="com.yantra.yfs.core.YFSSystem" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.ui.backend.YFCFilterManager" %>

<%
if (isShipNodeUser()) { 
	YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
	String sAnalyticsNamespace = getAnalyticsNameSpace();
	//System.out.println("Node : " + curUsr.getAttribute("Node"));
	//System.out.println("CurUsrList : " + curUsr.toString());
%>
		<input type="hidden" id="CU" name="CAMUsername" value="<%=curUsr.getAttribute("Loginid")%>"/>
		<input type="hidden" id="CP" name="CAMPassword" value="<%=session.getId()%>"/>
		<input type="hidden" id="CN" name="CAMNamespace" value="<%=sAnalyticsNamespace%>"  />
		<input type="hidden" id="b_action" name="b_action" value="xts.run"/>
		<input type="hidden" id="m" name="m" value="qs/qs.xts"/>
		<input type="hidden" id="method" name="method" value="newQuery"/>
<%
	YFCFilterManager fm = YFCFilterManager.getInstance(pageContext.getServletContext());
	if ( !fm.processRequest(request,response))
		return;
	String wmsAnalyticsURL = getAnalyticsReportNetUrl();
	YFCElement errors = null;
	if (isVoid(wmsAnalyticsURL)){
		YFCException ex = new YFCException(getI18N("analytics.reportnet.url_is_not_configured_in_the_properties_file_"));
		errors = (ex.getXML()).getDocumentElement();
	}	
%>

<script language="javascript">
	function changeToLoadDetails() {
	var URLis = document.getElementById("URL").value;
	var CAMUsername = document.getElementById("CU").value;
    var CAMPassword = document.getElementById("CP").value;
	var CAMNamespace = document.getElementById("CN").value;
	//var location=URLis + '?b_action=xts.run&m=qs/qs.xts&method=newQuery';
	var location=URLis + '?b_action=xts.run&m=qs/qs.xts&method=newQuery&CAMUsername='+CAMUsername+'&CAMPassword='+CAMPassword+'&CAMNamespace='+CAMNamespace;
	window.open(location);
    }
	window.attachEvent("onload", changeToLoadDetails);
</script>


<head>
<body>
<center><h1>Launched Query Studio</h1></center>
<input id="URL" type="hidden" name="Dest" value="<%=wmsAnalyticsURL%>">
</body>
</head>

<%	
	} else { 
%>
<script>
	alert('<%=getI18N("Only_Node_User_can_perform_this_Operation")%>');
	window.history.back();
</script>
<%	
	}	
%>




