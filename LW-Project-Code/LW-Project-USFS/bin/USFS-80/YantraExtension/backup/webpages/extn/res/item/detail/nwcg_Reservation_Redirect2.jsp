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

<!-- Step4 for redirection - call this script on load to redirect-->
<!-- Note: The jsp gets executed in the server side and when it comes back to the client ie. whin the-->
<!-- Dummy.jsp page gets rendered the script gets executed on the load of the page-->
<script>
	function changeToLoadDetails() {
    // alert('1');
	var itemKey = document.all("myEntityKey1").value;
	//alert(itemKey);
	showDetailForOnAdvancedList('order', 'YOMD010',itemKey,'') ;
    }
	window.attachEvent("onload", changeToLoadDetails);
</script>
<!--   Accessing the multiple form elements  
Note :- I am doing this to get the list of keys that represent the lines selected. The multiselect to single API sends the selected information in the form of hidden elements. The tagelement <checkbox> repeats with the same name but different values. To I do getparametervalues to get the array of values-->

<!-- Step1 for redirection - call the API and set the output namespace-->
<yfc:callAPI serviceName='NWCGcreateOtherIssueUsingRes' inputNamespace='ItemList' outputNamespace='CreatedOrder'/>

<!-- Step2 for redirection - Create Entity Key-->
<yfc:makeXMLInput name="TestEntityKey">
          <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:CreatedOrder:/Order/@OrderHeaderKey" />
</yfc:makeXMLInput>

<!-- Step3 for redirection - Set it as a hidden parameter in the HTML-->
<input type="hidden" value='<%=getParameter("TestEntityKey")%>' name="myEntityKey1"/>


	

