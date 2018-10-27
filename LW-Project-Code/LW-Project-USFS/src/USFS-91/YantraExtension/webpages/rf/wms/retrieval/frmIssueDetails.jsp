<%@ include file="/yfc/rfutil.jspf" %>

<%


	String errorDesc = null ;
	String errorField = null ;
	String formName ="/frmIssueDetail";
	String focusField="txtBarCodeData";
	String strOrderNo = null;
	
	request.setAttribute("xml:/Task/@NewTaskKey",getParameter("xml:/Task/@TaskKey") );


	
	YFCElement taskRef=null;

	String palletId=null;
	String sLocationId = getParameter("xml:/Task/@SourceLocationId");	

	try{
		
		taskRef=getStoredElement(getTempQ(), "TaskReferences", getParameter("xml:/Task/@TaskKey"));

		if (taskRef!= null){
		strOrderNo = taskRef.getAttribute("OrderNo");
		}
	
		
		if(taskRef==null){
			errorDesc="Mobile_Task_Inventory_Not_Found";
		}
		//put last executed task's source location in session.
		deleteAllFromTempQ("LastExecutedLocation");
		addToTempQ("LastExecutedLocation" ,sLocationId, false);


	}catch(Exception e){

		errorDesc="Mobile_Session_error";
	}


	if(errorDesc==null){

		String orderDtlsInput = "<Order DocumentType=\"0001\" EnterpriseCode=\"NWCG\" OrderNo=\"" + strOrderNo + "\" />";

		YFCDocument orderDoc = YFCDocument.parse(orderDtlsInput);

		YFCDocument orderTemplateDoc = YFCDocument.parse("<Order><PersonInfoShipTo></PersonInfoShipTo><Extn></Extn></Order>");


			%>

				<yfc:callAPI apiName="getOrderDetails" inputElement='<%=orderDoc.getDocumentElement()%>'  templateElement='<%=orderTemplateDoc.getDocumentElement()%>' outputNamespace='Order' />




			<%		
				
			errorDesc=checkForError() ;	
		
	}
	
	if (errorDesc == null )
	{

			
			YFCDocument ydoc = getForm(formName) ;
			
			YFCElement eleOrder=(YFCElement)request.getAttribute("Order");

			if(eleOrder!=null){

				YFCElement elePersonInfoShipTo=eleOrder.getChildElement("PersonInfoShipTo");
				YFCElement eleExtn=eleOrder.getChildElement("Extn");

				if(elePersonInfoShipTo!=null){

				String addressLine1 = elePersonInfoShipTo.getAttribute("AddressLine1");
				String addressLine2 = elePersonInfoShipTo.getAttribute("AddressLine2");
				String city = elePersonInfoShipTo.getAttribute("City");
				String state = elePersonInfoShipTo.getAttribute("State");
				
				request.setAttribute("xml:/TaskList/Task/Address/@AddressLine1", addressLine1);			
				request.setAttribute("xml:/TaskList/Task/Address/@AddressLine2", addressLine2);			
				request.setAttribute("xml:/TaskList/Task/Address/@City", city);			
				request.setAttribute("xml:/TaskList/Task/Address/@State", state);

				}

			if(eleExtn!=null){

				String incNumber = eleExtn.getAttribute("ExtnIncidentNo");
				String incName = eleExtn.getAttribute("ExtnIncidentName");
				String incFSAcctCode = eleExtn.getAttribute("ExtnFsAcctCode");
				String incOverrideCode = eleExtn.getAttribute("ExtnOverrideCode");
				String incBlmAcctCode = eleExtn.getAttribute("ExtnBlmAcctCode");
				String incOtherAcctCode = eleExtn.getAttribute("ExtnOtherAcctCode");

				
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnIncidentNo", incNumber);	
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnIncidentName", incName);	
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnFsAcctCode", incFSAcctCode);	
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnOverrideCode", incOverrideCode);
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnBlmAcctCode", incBlmAcctCode);
				request.setAttribute("xml:/TaskList/Task/Incident/@ExtnOtherAcctCode", incOtherAcctCode);
								
			}


			}

		out.println(sendForm(ydoc,"Back", true)) ;	
		
	

}

if(errorDesc!=null)
{
	String errorXML=getErrorXML(errorDesc,errorField);

	%>
	<%=errorXML%>
	<%

}

%>



