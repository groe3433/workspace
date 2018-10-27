<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%
String errorMsg1 = (String)getParameter("ErrorMsg");
String errorMsgDetail = (String)getParameter("ErrorMsgDetail");
String errorMsg = errorMsg1 + " " + errorMsgDetail;
%>
<script for="window" event="onload" language="javascript">


if (window.dialogHeight) {
//This is a dialog.  So, we need to close.
window.returnValue = "Refresh";
window.close();
}
else {
window.status = '<%=getI18N("Please_sign_in_to_Yantra")%>';
window.focus();
loginform.UserId.focus();
}
document.body.scroll = "no"
</script>
<style>
.page {
background-color="#ffffff";
width:300px;
filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#ffffff, endColorstr=#c0c0c0);
}
.loginbutton {
background-color: #ffffff;
color: #000000;
filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#ffffff, endColorstr=#c0c0c0);
border-bottom: 2px solid black;
font: normal normal bold 8pt Tahoma;
}
.loginlabel {
font:normal normal bold 8pt Tahoma;
text-align:right;
align: right;
}
.logininput   {
font: normal normal normal 8pt Tahoma;
border-width:1px;
border-style:solid;
}
.loginmiddlepanel	{
width:300px;
}
.loginmiddletable	{
background-color:#ffffff;
width:300px;
border:0;
vertical-align:bottom;
filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#c0c0c0, endColorstr=#ffffff);
}
.errormsgstyle	{
font: normal normal normal 8pt Tahoma;
color: #DC143C;
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
font: normal normal normal 10pt Tahoma;
border-width:0px;
border-style:solid;
}

a:visited{color:#FFFFFF}
a:hover{color:#FFFFFF}
a:link{color:#FFFFFF}
a:active{color:#FFFFFF}

</style>
<script language="javascript" src="/yantra/console/scripts/login.js" >
</script>
<html>
<head>
<link REL="SHORTCUT ICON" HREF="<%=YFSUIBackendConsts.YANTRA_ICON%>">
<title><yfc:i18n>Yantra_7x</yfc:i18n></title>
</head>
<body style="margin:0;">
<form name="loginform" method='POST' action="login.jsp">
<div style="position:relative;top:0;left:0;height:100%;width:100%;">
<span style="height:25px;border:0;">&nbsp;</span>
<span class="logleft5" style="">
<!--
DATE     : 2005-08-02
COMMENTS : Added by Ram Doraiswamy
-->
<img style="margin-left:25%;vertical-align:center" src="/yantra/console/icons/banner_r1_c2_f2.jpeg" width="125px" height="110px"/>
<img src="/yantra/console/icons/ICBS_Logo.jpeg" width="416px" height="171px"/>
</span>
<span style="height:282px;width:100%;border:0;text-align:center;vertical-align:bottom;horizontal-align:center;" class="bottompanel1" >
<span style="height:185px;border:0;"></span>
<font size="1" color="black" style="text-align:left" align="left">Disclaimer: Unauthorized access to this United States Government Computer System and software is prohibited by the Title 18, United States Code 1030. This statute states that: Whoever knowingly, or intentionally accesses a computer without authorization or exceeds authorized access, and by means of such conduct, obtains, alters, damages, destroys, or discloses information or prevents authorized use of data or a computer owned by or operated for the Government of the United States shall be punished by a fine under this title or imprisonment for not more than 10 years, or both. All activities on this system and network may be monitored, intercepted, recorded, read, copied, or captured in any manner and disclosed in any manner, by authorized personnel. THERE IS NO RIGHT OF PRIVACY IN THIS SYSTEM. System personnel may give to law enforcement officials any potential evidence of crimes found on this USDA computer system. USE OF THIS SYSTEM BY ANY USER, AUTHORIZED OR UNAUTHORIZED, CONSTITUTES CONSENT TO THIS MONITORING, INTERCEPTION, RECORDING, READING, COPYING OR CAPTURING AND DISCLOSURE. REPORT UNAUTHORIZED USE TO AN INFORMATION SYSTEM SECURITY OFFICER.
</font>
<br><br><br>
<!--<img src="<%=YFSUIBackendConsts.YANTRA_LOGIN_LEFT%>" width="169px" height="141px"/><img src="<%=YFSUIBackendConsts.YANTRA_LOGIN_MIDDLE%>" width="41%" height="141px"/>
<img src="<%=YFSUIBackendConsts.YANTRA_LOGIN_RIGHT%>" width="400px" height="120px"/><br>
<span class="copyright">
<yfc:i18n>_Copyright_</yfc:i18n> &copy; <yfc:i18n>About_Copyright_Console</yfc:i18n> <a href="http://www.yantra.com"><yfc:i18n>Yantra_Corporation</yfc:i18n></a></span>
-->
</span>
</div>
<div style="position:absolute;top:200;left:0;">
<span class="logleft6" style="margin:0;border:0;height:86px;">
<img src="<%=YFSUIBackendConsts.YANTRA_LOGIN_LINES%>" width="100%" height="86px"/>
</span>
</div>
<div style="position:absolute;top:215;left:38%;">
<table class="loginmiddletable" cellspacing=8 cellpadding=2>
<tr>
<td style="padding-top:20px" class="loginlabel"><yfc:i18n>Login_ID</yfc:i18n>&nbsp;
<INPUT type="text" class="logininput" value='' name="UserId" >
</td>
</tr>
<tr>
<td class="loginlabel"><yfc:i18n>Password</yfc:i18n>&nbsp;
<INPUT type=password class="logininput" value='' name="Password" >
</td>
</tr>
<tr>
<td align="right" class="errormsgstyle"><yfc:i18n><%=errorMsgDetail%></yfc:i18n>&nbsp;
<input class=loginbutton type=submit name="btnLogin" value='<yfc:i18n>Sign_In</yfc:i18n>' title="<yfc:i18n>Click_to_sign_in_to_Yantra</yfc:i18n>" onclick="window.status='<yfc:i18n>Signing_in._Please_wait...</yfc:i18n>';doLogin();">
</td>
</tr>
</table>
</div>
</form>
</body>
</html>
