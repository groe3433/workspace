<%@ include file="/yfc/rfutil.jspf" %>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmUNSNWTHeaderValtxtReturnNo.jsp");

	String errorDesc=null;
	String errorField="txtReturnNo";
%>
	<yfc:callAPI apiID='RF'/>
<%	
	String ValidReturnNo = resolveValue("xml:/Result/@ValidReturnNo");
	if(!ValidReturnNo.equalsIgnoreCase("True")) {
		errorDesc="Invalid Return No";
	}
	if(errorDesc==null) {
		String formName = "/frmUNSNWTHeader" ;	
		out.println(sendForm(formName, "txtIncYear")) ;
	} else {
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<% 
	} 				
%>