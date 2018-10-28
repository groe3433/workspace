<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@include file="/console/jsp/primarytaskreference.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="/yantra/console/scripts/taskmanagement.js"></script>
<script language="javascript" src="/yantra/console/scripts/exceptionutils.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<script language="javascript">
	function changeToLoadDetails() {
	showDetailForOnAdvancedList('ISUorder','OISUNWGYOMD290','','') ;
    }
	window.attachEvent("onload", changeToLoadDetails);
</script>

<%
  //System.out.println(getRequestDOM()); 
  YFCElement myElement = getRequestDOM();
  session.setAttribute("ReservedItems",myElement);
%>


	

