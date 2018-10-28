<%@ include file="/yfc/NWCGrfutil.jspf"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.XPathUtil"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.XMLUtil"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.CommonUtilities"%>
<%@ page import="com.yantra.yfs.japi.YFSEnvironment"%>
<%@ page import="org.w3c.dom.NodeList"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="org.w3c.dom.Node"%>

<%
	System.out.println("@@@@@ /webpages/extn/rf/wms/rfnwcgrfireturns/frmRFILinesTrkIDValtxtTID.jsp");
	String strPrimarySerialNo = getParameter("xml:/ReceiptLine/@TID");

	//check if trackable id already in tempq if yes throw duplicate trackable id error 
	YFCElement tempQYFCElem =(YFCElement)getTempQ().getElementsByTagName("TempQ").item(0);
	NodeList rLine = XPathUtil.getNodeList(tempQYFCElem.getDOMNode(), "/TempQ/ReceiptLine[@PrimarySerialNo='"+strPrimarySerialNo+"']");
	int rLineLen = rLine.getLength();
	
	// should only have 1 node in the node list
	if(rLineLen > 0) {	
		String strErrorDescription = "Duplicate Trackable ID";
		String strErrorField = "";
		String xmlError = getErrorXML(strErrorDescription, strErrorField);
%>
		<%=xmlError%>
<%
	} else {
		YFCElement eleReceiptHeader = (YFCElement)tempQYFCElem.getElementsByTagName("Receipt").item(0);
		String strIncidentYear = eleReceiptHeader.getAttribute("IncidentYear");
		String strIncident = eleReceiptHeader.getAttribute("IncidentNo");
		
		String sItemID = getParameter("xml:/ItemList/Item/@ItemID");
		String sProductClass = getParameter("xml:/ItemList/Item/PrimaryInformation/@DefaultProductClass");
		String sUnitOfMeasure = getParameter("xml:/ItemList/Item/@UnitOfMeasure");

		YFCDocument inDoc = YFCDocument.createDocument("Serial");
		YFCElement eleSerial = inDoc.getDocumentElement();
		eleSerial.setAttribute("AtNode", "Y");
		eleSerial.setAttribute("IncidentNo", strIncident);
		eleSerial.setAttribute("IncidentYear", strIncidentYear);
		eleSerial.setAttribute("SerialNo", strPrimarySerialNo);
		
		YFCElement eleInventoryItem = inDoc.createElement("InventoryItem");
		eleInventoryItem.setAttribute("InventoryOrganizationCode", "NWCG");
		eleInventoryItem.setAttribute("ItemID", sItemID);
		eleInventoryItem.setAttribute("ProductClass", sProductClass);
		eleInventoryItem.setAttribute("UnitOfMeasure", sUnitOfMeasure);
		eleSerial.appendChild(eleInventoryItem);
		request.setAttribute("SerialInput",eleSerial);
%>
		<yfc:callAPI apiID='TID' />
<%
		String strSerialPresent = resolveValue("xml:/Result/@SerialPresent");
		String strErrorDescription = "";
				
		if (strSerialPresent.equals("True")) {
			strErrorDescription = "TRACKABLE ID ALREADY AT NODE !!!";
		}
		if (strSerialPresent.equals("NotInIncident")) {
			strErrorDescription = "INVALID TRACKABLE ID FOR THIS INCIDENT !!!";
		}
		if (strSerialPresent.equals("Invalid Input")) {
			strErrorDescription = "INVALID TRACKABLE ID !!!";
		}
		
		String sNo = resolveValue("xml:/SerialList/Serial/@SerialNo");
		
		if (!strErrorDescription.equalsIgnoreCase("")) {
			String strErrorField = "";
			String xmlError = getErrorXML(strErrorDescription, strErrorField);
%>
			<%=xmlError%>
<%
		} else {
%>
			<yfc:callAPI apiID='GS' />
<%
			// adding the return line starts here
			HashMap receiptLineMap = new HashMap();
			receiptLineMap.put("ItemID", resolveValue("xml:/ItemList/Item/@ItemID"));
			receiptLineMap.put("isSeriallyTracked", "Y");
			receiptLineMap.put("sTID", strPrimarySerialNo);
			receiptLineMap.put("ProductClass", resolveValue("xml:/ItemList/Item/PrimaryInformation/@DefaultProductClass"));
			receiptLineMap.put("ShortDescription", resolveValue("xml:/ItemList/Item/PrimaryInformation/@Description"));
			receiptLineMap.put("UOM", resolveValue("xml:/ItemList/Item/@UnitOfMeasure"));
			receiptLineMap.put("PrimarySerialNo", resolveValue("xml:/SerialList/Serial/@SerialNo"));
			receiptLineMap.put("SecondarySerialNo", resolveValue("xml:/SerialList/Serial/@SecondarySerial1"));
			receiptLineMap.put("TagAttribute1", resolveValue("xml:/SerialList/Serial/TagDetail/@LotAttribute1"));
			receiptLineMap.put("TagAttribute2", resolveValue("xml:/SerialList/Serial/TagDetail/@LotAttribute3"));
			receiptLineMap.put("TagAttribute3", resolveValue("xml:/SerialList/Serial/TagDetail/@LotNumber"));
			receiptLineMap.put("TagAttribute4", resolveValue("xml:/SerialList/Serial/TagDetail/@RevisionNo"));
			receiptLineMap.put("TagAttribute5", resolveValue("xml:/SerialList/Serial/TagDetail/@BatchNo"));
			receiptLineMap.put("TagAttribute6", resolveValue("xml:/SerialList/Serial/TagDetail/@LotAttribute2"));
			receiptLineMap.put("CountTrackId", "Y");
			receiptLineMap.put("DispositionCode", "RFI");

			YFCNodeList rlNL = (getTempQ()).getElementsByTagName("ReceiptLine");
			int rlNLLen = rlNL.getLength();
			
			addToTempQ("ReceiptLine", Integer.toString(rlNLLen + 2), receiptLineMap, true);
			
			YFCDocument doc = getForm("/frmRFILinesTrkID");
			String strValue = "0";
			YFCNodeList lsLine = (getTempQ()).getElementsByTagName("ReceiptLine");
			
			if (lsLine != null) {
				String totalNoOfLines = Integer.toString(lsLine.getLength());
				strValue = calculateTotalTrackableLines(lsLine);
				YFCElement dropoffLocationElem = getField(doc, "lblTotalLinesVal");
				dropoffLocationElem.setAttribute("value", totalNoOfLines);
				YFCElement eleTotalTrackableLines = getField(doc, "lblTotalTracableLinesVal");
				eleTotalTrackableLines.setAttribute("value", strValue);
			}
			String formName = "/frmRFILinesTrkID";
			out.println(sendForm(doc, "", true));
		}
	}
%>