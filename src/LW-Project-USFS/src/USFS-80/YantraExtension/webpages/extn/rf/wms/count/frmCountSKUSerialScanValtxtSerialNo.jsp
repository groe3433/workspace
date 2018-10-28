<%@ page import="java.util.*" %>
<%@ include file="/yfc/rfutil.jspf" %>

<%
	String errorDesc = null ;
	String errorField = null ;
	String formName ="/frmCountSKUSerialScan";
	String forwardPage="";
	YFCElement serialElem = null;
	HashMap mp= new HashMap();
	
	String sSerial = getParameter("xml:/SerialDetail/@SerialNo");
	int numSecSerials=(int)getNumericValue(getParameter("xml:/Item/PrimaryInformation/@NumSecondarySerials"));

	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");		
	
	YFCElement storedSerialElem= getStoredElement(getTempQ(),"SerialDetail", sSerial); 

	YFCNodeList serialRangeList = ((getTempQ()).getElementsByTagName("SerialRange"));
	if(serialRangeList != null) {
		int i=0;
		for(i =0; i<serialRangeList.getLength();i++){
			YFCElement serialRange = (YFCElement)serialRangeList.item(i);
			String sTempQFromSerialNo = serialRange.getAttribute("FromSerialNo");
			String sTempQToSerialNo = serialRange.getAttribute("ToSerialNo");
			if(equals(sTempQFromSerialNo,sSerial) || equals(sTempQToSerialNo,sSerial)) {
				errorDesc="Mobile_Duplicate_Serial";
			}
		}
	}
	if(!isVoid(storedSerialElem)){
		errorDesc="Mobile_Duplicate_Serial";
	}

	if(errorDesc == null){
		YFCElement itemElem= getStoredElement(getTempQ(),"Inventory",String.valueOf("1")); 
		String eCode="";
		if(itemElem != null){
			eCode=itemElem.getAttribute("OrganizationCode");
		}
		// CR854, AtNode="Y" attribute added in input XML to getSerialList API.  This validates that serial# is atNode when user do count.
		YFCElement input=YFCDocument.parse("<Serial AtNode=\"Y\" SerialNo=\""+ sSerial + "\" ><InventoryItem ItemID=\""+getParameter("xml:/Inventory/@ItemID")+"\" CallingOrganizationCode=\"" + eCode+"\" /></Serial>",true).getDocumentElement();
		YFCDocument templateDoc=YFCDocument.parse("<SerialList><Serial SerialNo=\"\" /></SerialList>");	
		%>
			<yfc:callAPI apiName='getSerialList' inputElement='<%=input%>' templateElement='<%=templateDoc.getDocumentElement()%>' />
		<%	

		errorDesc=checkForError() ;
		if(errorDesc == null) {
			request.setAttribute("xml:/RecentLPN/@LastScannedLPN", getParameter("xml:/RecentLPN/@LastScannedLPN"));
			request.setAttribute("xml:/RecordCountResult/@TaskType",countResult.getAttribute("TaskType"));

			if(!(isVoid(getValue("SerialList","xml:/SerialList/Serial/@SerialNo")))) {
				//serial exists in inventory. don't ask for sec serials. Ask next serial.
				forwardPage=checkExtension(getParameter("entitybase")+"/frmCountSKUSerialEntry.jsp");
			}else{
				/* -- CR 492 -- if the entered SerialNo not exist in yfs_global_serial_num, display error
				if(numSecSerials>0){
					forwardPage= checkExtension(getParameter("entitybase")+"/frmCountSKUSecondarySerialEntry.jsp");
				}
				else{
					request.setAttribute("xml:/SerialDetail/@SerialNo","");
					forwardPage=checkExtension(getParameter("entitybase")+"/frmCountSKUSerialEntry.jsp");
				}
				*/
				errorDesc="Invalid Trackable ID is entered, Please enter valid one";
				request.setAttribute("xml:/SerialList/Serial/@SerialNo", "");
			}
			if(errorDesc == null) {
				//add serial info to session variable. form a hash map	
				mp.put("SerialNo",getParameter("xml:/SerialDetail/@SerialNo"));
				mp.put("ItemID",getParameter("xml:/Inventory/@ItemID"));
				mp.put("ItemValue",String.valueOf("1"));
				try{
					addToTempQ("SerialDetail", getParameter("xml:/SerialDetail/@SerialNo"), mp, false) ;
				}catch(Exception e){		
					errorDesc="Mobile_Duplicate_Serial";		
				}

				if(errorDesc == null) {
					%>
						<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
					<%
				}
			}
		}
	}
	if(errorDesc != null) {
		String errorXML = getErrorXML(errorDesc, errorField);
		%><%=errorXML%><%
	}
%>



