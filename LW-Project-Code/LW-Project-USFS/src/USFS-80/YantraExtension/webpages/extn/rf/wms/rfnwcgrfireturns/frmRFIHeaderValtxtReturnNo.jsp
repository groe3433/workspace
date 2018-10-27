<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFIHeaderValtxtReturnNo.jsp");
	String errorDesc=null;
	String errorField="txtReturnNo";
%>
	<yfc:callAPI apiID='RN'/><%
	String ValidReturnNo = resolveValue("xml:/Result/@ValidReturnNo");
	if(ValidReturnNo.equalsIgnoreCase("True")) {
		// Its valid return screen, do not throw an error. 
	} else {
		errorDesc="Invalid Return No";
	}
	if(errorDesc==null) {
		// its valid return no dont throw error , error desc is null
		String formName = "/frmRFIHeader" ;					
		out.println(sendForm(formName, "txtIncYear")) ;
	} else {
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<% 
	} 				
%>