<%@ include file="/yfc/rfutil.jspf" %>
<%@ include file="/console/jsp/rftasktypeutils.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>
<%
	HashMap itemMap= new HashMap();
	HashMap tagAttrMap= new HashMap();
	HashMap serialRangeMap= new HashMap();
	String errorDesc = null ;
	String errorField = null ;
	String entitybase= getParameter("entitybase") ;
	String forwardPage = "";
	double totalSerial = 0;
	
	String sSerialValue = "";
	String sItemValue = "";
	String formName = "/frmCountSKUQtyEntry" ;	
	boolean isSerialRangeScanned = false;
	String sTagControlFlag = "";
	YFCElement tagElem = null;
	YFCElement newTagElem = null;

	String sSerialNo = getParameter("xml:/SerialDetail/@SerialNo");
	String sFromSerial = getParameter("xml:/SerialRange/@FromSerialNo");
	String sToSerial = getParameter("xml:/SerialRange/@ToSerialNo");

	if(!isVoid(getParameter("xml:/SerialRange/@FromSerialNo")) && isVoid(getParameter("xml:/SerialRange/@ToSerialNo"))){
	   errorDesc = "Mobile_To_Serial_No_Is_Blank";
        }

	YFCElement countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
	int NoOfItemsScanned = countResult.getIntAttribute("NoOfItemsScanned");

	YFCElement itemElem= getStoredElement(getTempQ(),"Inventory", String.valueOf("1")); 
	if(itemElem != null) {
		itemMap=new HashMap(itemElem.getAttributes());	
		sItemValue = itemElem.getAttribute("value");
		sTagControlFlag = itemElem.getAttribute("TagControlFlag");
	}
	
	// YFCNodeList serialRange = ((getTempQ()).getElementsByTagName("SerialRange"));
	// if(serialRange != null) {
	//	YFCElement serialRangeElement = (YFCElement)serialRange.item(0);
	// }

	if(equals(sTagControlFlag,"Y")){ // if the item is tag controlled
		tagElem = (YFCElement)((getTempQ()).getElementsByTagName("TagAttributes")).item(0);
		if(tagElem != null) {
			tagAttrMap = new HashMap(tagElem.getAttributes());
		}
	}

	YFCNodeList serialList = ((getTempQ()).getElementsByTagName("SerialDetail"));
	int i=0;
	for(i =0; i<serialList.getLength();i++){
		YFCElement serialElem = (YFCElement)serialList.item(i);
		sSerialValue = serialElem.getAttribute("ItemValue");
		if(equals(sSerialValue,sItemValue)) {
			totalSerial++;
		}
		// --- Top of CR 492: since the TagEntry screen is skipped ---
		// set LotNumber element in TagAttributes node from SerialNo
		if(equals(sTagControlFlag,"Y")){ // if the item is tag controlled
			if(tagElem != null){
				String sLotNumber = tagElem.getAttribute("LotNumber");
				if(isVoid(sLotNumber)){
					// if this field is null, then set it with the serialNo value
					tagElem.setAttribute("LotNumber",serialElem.getAttribute("SerialNo"));
				}
				if(totalSerial > 1){ // multiple serialNo captured
					tagAttrMap.put("LotNumber",serialElem.getAttribute("SerialNo"));
					addToTempQ("TagAttributes",String.valueOf("1"),tagAttrMap,true);
				}
			}
		}
		// --- Bottom of CR 492: since the TagEntry screen is skipped ---
	}

	if((sFromSerial != null) || (sToSerial != null)){
			isSerialRangeScanned = true;
			serialRangeMap.put("FromSerialNo", sFromSerial);
			serialRangeMap.put("ToSerialNo", sToSerial);		
		try {
			addToTempQ("SerialRange", String.valueOf("1"), serialRangeMap, false);
		}catch(Exception e) {
			try {
				replaceInTempQ("SerialRange", String.valueOf("1"), serialRangeMap);
			}catch(Exception ee) {
				errorDesc="Mobile_Session_Failed_while_adding_system_quantity";
			}
		}
	}
	if(i==0 && !isSerialRangeScanned){
		//if no serials scanned, error
		errorDesc="Mobile_Serial_scan_mandatory_Please_scan_a_serial_number";
	}
	
	if(errorDesc==null){
		double rangeCount = getDoubleFromLocalizedString(getLocale(),itemElem.getAttribute("CountQuantity"));
		if(rangeCount>0){
			double totalQuantity = rangeCount + totalSerial;
			itemMap.put("CountQuantity",getFormattedDouble(totalQuantity));
		}else{
			itemMap.put("CountQuantity", Double.toString(totalSerial));
		}

		try {
			addToTempQ("Inventory", String.valueOf("1"), itemMap, false);
		}catch(Exception e) {
			try {
				replaceInTempQ("Inventory", String.valueOf("1"), itemMap);
			}catch(Exception ee) {
				errorDesc="Mobile_Session_Failed_while_adding_system_quantity";
			}
		}
	}

	if(errorDesc == null) {
		forwardPage= entitybase + "/" + "frmCountInventoryAttributesEntry.jsp" ;
		forwardPage=checkExtension(forwardPage);
		%><jsp:forward page='<%=forwardPage%>' ></jsp:forward><%
	}
	if(errorDesc != null ) {
		String errorXML = getErrorXML(errorDesc, errorField);
		%><%=errorXML%><%
	}	
									
%>
