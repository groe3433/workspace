<%@ include file="/yfc/rfutil.jspf" %>

<%
	String errorDesc=null;
	String errorField="txtReturnNo";
%>
	<yfc:callAPI apiID='RN'/>
<% 
	String ValidReturnNo = resolveValue("xml:/Result/@ValidReturnNo");
	if(ValidReturnNo.equalsIgnoreCase("True")) {
		// its valid return no dont throw error
	} else {
		// invalid return no throw error
		errorDesc="Invalid Return No";
	}
	if(errorDesc==null) {
		// its valid return no dont throw error , error desc is null
		String formName = "/frmUNSHeader" ;				
		out.println(sendForm(formName, "txtIncYear")) ;
	} else {
		String errorXML = getErrorXML(errorDesc, errorField);		
%>
		<%=errorXML%>
<% 
	} 				
%>