<%@ include file="/yfc/rfutil.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>
<%

	String errorDesc = null ;
	String errorField = null;
	String entitybase= getParameter("entitybase") ;
	String forwardPage= "";	
	String skuValtxtBarCodeData = "";
	
	//System.out.println("TempQ:" + getTempQ().getDocumentElement());	
	YFCElement taskElem = getTempQ().getDocumentElement().getChildElement("Task");
	if(!isVoid(taskElem)){
		request.setAttribute("xml:/CountTask/@TaskId",taskElem.getAttribute("TaskId"));
	}
	
	YFCElement locnElem = getTempQ().getDocumentElement().getChildElement("Location",true);
	locnElem.setAttribute("LocationId",getTempQ().getDocumentElement().getChildElement("Task").getAttribute("SourceLocationId"));
	
	request.setAttribute("xml:/Location1/@BarCodeData",locnElem.getAttribute("LocationId"));
	request.setAttribute("xml:/Location/@BarCodeData",locnElem.getAttribute("LocationId"));
	request.setAttribute("xml:/RecentLPN/@LastScannedLPN","");

	// -- Top of CR 492 -- redirect when ItemID is displayed on 2nd, 3rd counts...
	//YFCElement recordCountResultElem = getTempQ().getDocumentElement().getChildElement("RecordCountResult",true);
        YFCElement recordCountResultElem= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	String sNoOfItemsScanned = recordCountResultElem.getAttribute("NoOfItemsScanned");
	if(equals("9999",sNoOfItemsScanned)){
		// set the noOfItemsCanned back to original value of zero
		getTempQ().getDocumentElement().getChildElement("RecordCountResult",true).setAttribute("NoOfItemsScanned","0");
		forwardPage= entitybase + "/" + "frmCountEntryForCaseOrSKUValtxtBarCodeData.jsp" ;
		forwardPage=checkExtension(forwardPage);
		if (errorDesc==null){
		%>
			<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
		<%}
	}
	// -- Bottom of CR 492 -- redirect when ItemID is displayed on 2nd, 3rd counts...

	forwardPage= entitybase + "/" + "frmCountProceedTask.jsp" ;
	forwardPage=checkExtension(forwardPage);
	//System.out.println("The forwardpage is:" + forwardPage);
	if (errorDesc==null){
	%>
		<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
	<%}

	if (errorDesc != null ){
		String errorXML = getErrorXML(errorDesc, errorField);
		%><%=errorXML%><%
	}
%>



