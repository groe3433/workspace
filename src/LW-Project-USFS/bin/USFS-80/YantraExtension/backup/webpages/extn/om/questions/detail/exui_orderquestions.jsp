<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript">

function makeRefreshTrue() {
window.returnValue = "Refresh";
window.close();
}

</script>

<%
String sCurrentLocaleCode = getLocale().getLocaleCode();

// Get API output called from the view definition
YFCElement questionsElem = (YFCElement) request.getAttribute("QuestionType");
if (null == questionsElem) {
questionsElem = YFCDocument.createDocument("QuestionType").getDocumentElement();
}

// Set the ModificationAllowedFlag on the input element based on which
// AllowedModifications element exists in the request.
String modificationFlag = "Y";
if (!isVoid(resolveValue("xml:/Order/AllowedModifications/Modification/@ModificationType"))) {
if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/Order/AllowedModifications")) {
modificationFlag = "N";
}
}
else if (!isVoid(resolveValue("xml:/OrderLine/Order/AllowedModifications/Modification/@ModificationType"))) {
if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/OrderLine/Order/AllowedModifications")) {
modificationFlag = "N";
}
}
else if (!isVoid(resolveValue("xml:/WorkOrder/Order/AllowedModifications/Modification/@ModificationType"))) {
if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/WorkOrder/Order/AllowedModifications")) {
modificationFlag = "N";
}
}
if ((equals("Y", modificationFlag)) && (userHasOverridePermissions())) {
modificationFlag = "O";
}
questionsElem.setAttribute("ModificationAllowedFlag", modificationFlag);

String javaCodebase = getJavaPluginCodebase();
if( isVoid(javaCodebase) )
javaCodebase = "http://java.sun.com/update/1.4.2/jinstall-1_4_2_03-windows-i586.cab#Version=1,4,2,3";
String javaClassid = getJavaPluginClassid();
if( isVoid(javaClassid) )
javaClassid = "clsid:CAFEEFAC-0014-0002-0003-ABCDEFFEDCBA";
%>
<OBJECT classid = "<%=javaClassid%>"
WIDTH = "100%" HEIGHT = "425" NAME = "OrderQuestionsApplet"  ID="OrderQuestionsApplet"
codebase = "<%=javaCodebase%>" >
<PARAM NAME = CODE VALUE = "com.yantra.app.exui.YantraExuiApplet">
<PARAM NAME = CODEBASE  VALUE = "/yantra/yfscommon" >
<PARAM NAME = NAME VALUE  = "OrderQuestionsApplet" >
<PARAM NAME = ID VALUE = "OrderQuestionsApplet" >
<PARAM NAME ="cache_option" VALUE = "Plugin" >
<PARAM NAME ="cache_archive" VALUE = "yantrabundle.jar,yfcui.jar,ycpexui.jar,xml-apis.jar,xercesImpl.jar,yompexui.jar,yantraiconsbe.jar,comm.jar" >
<PARAM NAME="type" VALUE="application/x-java-applet;jpi-version=1.4.2_03">
<PARAM NAME ="MAYSCRIPT" VALUE="true">
<PARAM NAME="scriptable" VALUE="true">
<PARAM NAME="TransactionClass" VALUE="com.yantra.omp.exui.ui.screens.OMPOrderQuestions">
<PARAM NAME="ActionUrl" VALUE="/yantra/console/order.exui">
<PARAM NAME="BundleList" VALUE="ycpapibundle">
<PARAM NAME="LocaleCode" VALUE="<%=sCurrentLocaleCode%>">
<PARAM NAME="FormInput" VALUE='<%=replaceString(replaceString(questionsElem.toString(),"&", "&amp;") ,"'","&apos;") %>'>
<PARAM NAME="Debug" VALUE="Y">
<PARAM NAME="YVSE001EX01" VALUE="com.yantra.wms.exui.ui.screens.TagEntry">
<PARAM NAME="YVSE001EX02" VALUE="com.yantra.wms.exui.ui.screens.ExpirationDateEntry">
<PARAM NAME="YVSE001EX03" VALUE="com.yantra.wms.exui.ui.screens.SerialEntry">
<PARAM NAME="YVSE001EX04" VALUE="com.yantra.wms.exui.ui.screens.DispositionCodeLookUp">

</OBJECT>
<script language="javascript">
window.returnValue = "Refresh";
</script>
