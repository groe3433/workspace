<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%@page import="com.yantra.yfc.dom.YFCElement"%>
<%@page import="com.yantra.ycp.common.VersionManager"%>
<%
	YFCElement versions = VersionManager.getVersionsXML();
	request.setAttribute("Versions", versions);
%>
<script>
	window.dialogHeight="465px";
</script>
<html XMLNS:yantra>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="<%="../css/"+getTheme()+".css"%>" type="text/css">
		<link REL="SHORTCUT ICON" HREF="<%=YFSUIBackendConsts.YANTRA_ICON%>">
		<title><yfc:i18n>Yantra_7x</yfc:i18n></title>
	</head>
	<style>
		A:active { color:white }
		A:visited { color:white }
		A:hover { color:white }
		A:link { color:white }
	</style>
	<body scrolling=no background="<%=YFSUIBackendConsts.YANTRA_ABOUT_BOX%>">
		<form name="containerform" method='POST'>
			<div class="aboutmain">
				<span class="aboutdetailmain">
					<div style="align:center;left:110;position:absolute;top:150;font:normal normal bold 12pt Tahoma;color:#FFFFFF;">
						<yfc:i18n>Application_Consoles</yfc:i18n>
					</div>
					<div style="align:center;left:30;position:absolute;top:175;font:normal normal bold 8pt Tahoma;color:#FFFFFF;">
						<table class="table" ID="Versions">
							<yfc:loopXML name="Versions" binding="xml:/Versions/@Version" id="Version">
							<%
									String sName = resolveValue("xml:/Version/@Name");
									String sVersion = resolveValue("xml:/Version/@Version");
									String sIsPCA = resolveValue("xml:/Version/@IsPCA");
							%>
									<tr>
										<td style="font-size: 11;font-family: Tahoma;font-weight: bold;color: #FFFFFF;border: 0;height: 17px;background-color: transparent;text-align: center;"><%=sName%>
										<yfc:i18n>_Version_</yfc:i18n> <%=sVersion%>
										</td>
									</tr>
							</yfc:loopXML>
							<tr>
								<td style="font-size: 11;font-family: Tahoma;font-weight: bold;color: #FFFFFF;border: 0;height: 17px;background-color: transparent;text-align: center;"><jsp:include page="/extn/NWCGversion.txt" />
								</td>
							</tr>
						</table>
					</div>
					<div style="position:absolute;left:15;top:280;" class="aboutdetailcopyright">
						<yfc:i18n>_Copyright_</yfc:i18n> &copy; <yfc:i18n>About_Copyright_Console</yfc:i18n> <a style=":visited:#FFFFFF;:link:#FFFFFF;:active:#FFFFFF;:hover:#FFFFFF;" href="http://www.yantra.com"><yfc:i18n>Sterling_Commerce_Inc</yfc:i18n></a>
						&nbsp;<yfc:i18n>All_rights_reserved</yfc:i18n>
					</div>
				</span>
			</div>
		</form>
	</body>
</html>