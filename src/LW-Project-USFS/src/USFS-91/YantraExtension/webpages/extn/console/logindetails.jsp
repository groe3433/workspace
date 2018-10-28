<%
/*******************************************************************************
   IBM and Sterling Commerce Confidential 
   OCO Source Materials 
   Sterling Platform AFC - Heathrow Release 
   (C) Copyright Sterling Commerce, an IBM Company 2011, 2012 
   The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/
%>

<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%@page import="com.yantra.yfc.ui.backend.YFCUIBackendInstance"%>
<%
	String errorMsg = (String)getParameter("ErrorMsg");
    String errorMsgDetail = (String)getParameter("ErrorMsgDetail");
	YFCUIBackendInstance.getInstance().setContextPath(request.getContextPath());
	String url = "http://www.ibm.com";
	if(!YFCCommon.isVoid(pageContext.getServletContext().getInitParameter("sci-aboutbox-url"))) {
		url = pageContext.getServletContext().getInitParameter("sci-aboutbox-url");
	}
%>

<style>
.page {
	background-color:#ffffff;
	width:300px;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#ffffff, endColorstr=#c0c0c0);
}
.bottompanel	{
	height:325px;
	width:100%;
}
.bottompanel1	{
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#c0c0c0, endColorstr=#024C92 );
	font: normal normal normal 16pt Verdana;
	color: #FFFFFF;
}
.logleft1	{
	width :100%;
	height:100%;
	border:0;
}
.logleft2	{
	height:25px;
}
.logleft3	{
	height:141px;
}
.logleft4	{
	vertical-align:top;
}
.logleft5	{
	height:141px;
	width:100%;
	padding:0;
}
.logleft6	{
	height:86px;
	width:100%;
	padding:0;
}
.copyright {
    font: normal normal normal 9pt Arial;
    border-width:0px; 
    border-style:solid; 
}
a:visited{color:#0000CC}
a:hover{color:#0000CC}
a:link{color:#0000CC}
a:active{color:#0000CC}
</style>

<script language="javascript">
    var contextPath="<%=request.getContextPath()%>";
	var urlString = "<%=url%>";
	document.onclick = function(event) {
		var eventObj = event || window.event;
		var targetNodeName = "";
		if(eventObj.target) {
			targetNodeName = eventObj.target.nodeName;
		} else if(eventObj.srcElement) {
			targetNodeName = eventObj.srcElement.nodeName;
		}
		if(targetNodeName && targetNodeName.toLowerCase() === "a") {
			window.open(urlString, "", "height=600, width=900, left=0, top=0, status=no, toolbar=no, menubar=no, location=no, resizable=yes, scrollbars=yes");
		}
	}
</script>

<html>
	<head>
		<link REL="SHORTCUT ICON" HREF="<%=YFSUIBackendConsts.YANTRA_ICON%>">
		<title><yfc:i18n>Yantra_7x</yfc:i18n></title>
	</head>
	<body style="margin:0;">
		<div style="position:relative;top:0;left:0;height:100%;width:100%;">
			<span style="height:25px;border:0;">&nbsp;</span>
			<span class="logleft5" style="">
				<img style="margin-left:34%;vertical-align:center" src="<%=request.getContextPath()%>/extn/console/icons/banner_r1_c2_f2.jpeg" width="125px" height="110px"/>
				<img src="<%=request.getContextPath()%>/extn/console/icons/ICBS_Logo.jpeg" />
			</span>
			<span style="height:282px;width:100%;border:0;text-align:center;vertical-align:bottom;horizontal-align:center;" class="bottompanel1" >		
				<span style="height:235px;border:0;"></span>
				<font size="1" color="black" style="text-align:left" align="left">Disclaimer: Unauthorized access to this United States Government Computer System and software is prohibited by the Title 18, United States Code 1030. This statute states that: Whoever knowingly, or intentionally accesses a computer without authorization or exceeds authorized access, and by means of such conduct, obtains, alters, damages, destroys, or discloses information or prevents authorized use of data or a computer owned by or operated for the Government of the United States shall be punished by a fine under this title or imprisonment for not more than 10 years, or both. All activities on this system and network may be monitored, intercepted, recorded, read, copied, or captured in any manner and disclosed in any manner, by authorized personnel. THERE IS NO RIGHT OF PRIVACY IN THIS SYSTEM. System personnel may give to law enforcement officials any potential evidence of crimes found on this USDA computer system. USE OF THIS SYSTEM BY ANY USER, AUTHORIZED OR UNAUTHORIZED, CONSTITUTES CONSENT TO THIS MONITORING, INTERCEPTION, RECORDING, READING, COPYING OR CAPTURING AND DISCLOSURE. REPORT UNAUTHORIZED USE TO AN INFORMATION SYSTEM SECURITY OFFICER.</font>
				<br><br><br>
			</span>
		</div>
		<div style="position:absolute;top:180;left:0;">
			<span class="logleft6" style="margin:0;border:0;height:86px;">
				<img src="<%=request.getContextPath()%>/extn/console/icons/grey_lines.gif" width="100%" height="86px"/>
			</span>
		</div>
		<div style="position:absolute;top:215;left:0%;">
			<TABLE  width=100% height=100%>
				<TR>
					<TD  align=center valign=middle colspan=3>
						<jsp:include page='<%=getActualPath("/console/logininputs.jsp")%>' flush="true" />
					</TD>
				</TR>
			</TABLE>
		</div>
	</body>
</html>