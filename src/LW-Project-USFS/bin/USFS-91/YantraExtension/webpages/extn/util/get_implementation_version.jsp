<%@page import="com.nwcg.icbs.yantra.util.common.*,
							com.yantra.interop.japi.YIFClientFactory,
							com.yantra.yfs.japi.YFSEnvironment,
							com.yantra.yfc.dom.*,
							org.w3c.dom.*"%>
<%

String yantraVersion = "";
String strPointVersion = "";
String strMajorVersion = "" ;
String strMinorVersion = "" ;
try 
{
	YFSEnvironment env = CommonUtilities.createEnvironment("GetVersion","GetVersion");

	Document doc1 = CommonUtilities.invokeAPI(env, "getProperty", YFCDocument.parse("<Property PropertyName=\"yfs.application.version\"/>").getDocument());

	yantraVersion = doc1.getDocumentElement().getAttribute("PropertyValue");
	
	//strMajorVersion = ResourceUtil.get("build.version.major");
	//strMinorVersion = ResourceUtil.get("build.version.minor");
	//strPointVersion = ResourceUtil.get("build.version.point");

} 
catch (Exception e) 
{
	%><%=e%><%
	//Ignore Exception
}
%>
<b>Yantra Version : </b><%=yantraVersion%><br>
<b>Installed Application(s) Version(s): </b><%@include file="/extn/version.txt"%><br>
<b>Build Detail(s): </b><%@include file="/extn/NWCGversion.txt"%><br>