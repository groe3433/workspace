<%@ include file="/yfc/rfutil.jspf" %>

<%
	String strNode = resolveValue("xml:CurrentUser:/User/@Node") ;
	YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");
	YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> </Organization>");
	if(!strNode.equals("")) {
%>
		<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>
<%	
	}
	String RecvOwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
	String errorDesc=null;
	String errorField="txtIncNo";
%>
	<yfc:callAPI apiID='VI'/>
<%
	String incName = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentName");
	String incNo = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentNo");
	String strIncidentBlmAcctCode = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentBlmAcctCode");
	String strIncidentFsAcctCode = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentFsAcctCode");
	String strIncidentOtherAcctCode = resolveValue("xml:/NWCGIncidentOrderList/NWCGIncidentOrder/@IncidentOtherAcctCode");
	if(RecvOwnerAgency == null || RecvOwnerAgency.equals("")) {
		errorDesc = "Only Node Users can Process Return !!!";
	}
	if(RecvOwnerAgency.equals("BLM") && (strIncidentBlmAcctCode == null || strIncidentBlmAcctCode.equals(""))) {
		errorDesc = "This Incident doesn't have a BLM Account Code !!!";
	}
	if(RecvOwnerAgency.equals("FS") && (strIncidentFsAcctCode == null || strIncidentFsAcctCode.equals(""))) {	
		errorDesc = "This Incident doesn't have a FS Account Code !!!";
	}
	if(RecvOwnerAgency.equals("OTHER") && (strIncidentOtherAcctCode == null || strIncidentOtherAcctCode.equals(""))) {	
		errorDesc = "This Incident doesn't have a FS Account Code !!!";
	}
	if(incName.equalsIgnoreCase("") || incNo.equalsIgnoreCase("")) {	
		errorDesc = "Invalid Inc No - Year";
 	}
	if(errorDesc != null) {
		String errorXML = getErrorXML(errorDesc, errorField);
%>
		<%=errorXML%>
<%
	} else {
		String formName = "/frmUNSHeader" ;			
		out.println(sendForm(formName, "txtNotes")) ;	
	}
%>