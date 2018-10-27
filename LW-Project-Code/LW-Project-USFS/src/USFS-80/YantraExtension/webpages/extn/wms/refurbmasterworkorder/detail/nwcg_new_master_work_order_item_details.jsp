<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<%@ page import="java.text.DecimalFormat" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>

<script language="javascript">

function setLocation(){
	var locationSelect = document.getElementById("LocationIdSelect");
	<%
	String strRefurbLocation = resolveValue("xml:ItemRefurbLocation:/CommonCodeList/CommonCode/@CodeShortDescription");
	YFCElement ele = (YFCElement) request.getAttribute("ItemRefurbLocation");
	if(ele != null )
	{
		YFCNodeList nlItem = ele.getElementsByTagName("CommonCode");
		if (nlItem != null && nlItem.getLength() == 1){
		%>
			locationSelect.value = "<%=strRefurbLocation%>";
		<%}
	}%>
}
window.attachEvent("onload", setLocation);
</script>

<table class="view" width="100%">
<tr>
	<%
	String strItemID = resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemID");
	String strUOM = resolveValue("xml:/NWCGMasterWorkOrderLine/@UnitOfMeasure");
	YFCDocument inputDoc = YFCDocument.parse("<Item/>");
	YFCElement elemItem = inputDoc.getDocumentElement();
	elemItem.setAttribute("ItemID",strItemID);
	elemItem.setAttribute("UnitOfMeasure",strUOM);

	YFCDocument rt_ItemList = YFCDocument.parse("<ItemList/>");
	YFCElement el_ItemList  = rt_ItemList.getDocumentElement();
	YFCElement el_Item      = rt_ItemList.createElement("Item");
	el_ItemList.appendChild(el_Item);
	el_Item.setAttribute("ItemID","");
	// CR 576 -- added node
	YFCElement elemPrimInfo = el_Item.createChild("PrimaryInformation");
	elemPrimInfo.setAttribute("KitCode","");

	YFCElement el_Components=rt_ItemList.createElement("Components");
	el_Item.appendChild(el_Components);

	YFCElement el_Component=rt_ItemList.createElement("Component");
	el_Components.appendChild(el_Component);
	el_Component.setAttribute("ComponentItemID","");
	el_Component.setAttribute("KitQuantity","");
	el_Component.setAttribute("ComponentUnitOfMeasure","");
	el_Component.setAttribute("ComponentItemKey",""); // CR 576 - added
	%>

	<yfc:callAPI apiName='getItemList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=rt_ItemList.getDocumentElement()%>' outputNamespace='GetItemList'/>

	<%
	String tagControlFlag = "";
	String revisionNo = "";
	String kitcode = "";
	String nestedTI_UpdateDLT = "";
	kitcode = getValue("GetItemList","xml:/ItemList/Item/PrimaryInformation/@KitCode");
	if("PK".equalsIgnoreCase(kitcode)){
		YFCElement elemItemList = (YFCElement) request.getAttribute("GetItemList");
		YFCNodeList nlComponent  = null;
		// this code checks for the tagControlled and Revision (DLT) flags
		// and sets the nestedTI_UpdateDLT flag that is used later
		if(elemItemList != null){
			nlComponent  = elemItemList.getElementsByTagName("Component"); 
			for(int index = 0 ; index < nlComponent.getLength() ; index++){
				YFCElement elemComponent = (YFCElement) nlComponent.item(index);
				String itemKey = elemComponent.getAttribute("ComponentItemKey");

				//if (itemKey != null && !itemKey.equals("")){
				if (itemKey != null && !itemKey.equals("") && nestedTI_UpdateDLT.equals("")){
					YFCDocument getID_inputDoc = YFCDocument.parse("<Item/>");
					YFCElement getID_elemItem = getID_inputDoc.getDocumentElement();
					getID_elemItem.setAttribute("ItemKey",itemKey);
					getID_elemItem.setAttribute("OrganizationCode","NWCG");

					YFCDocument inputTemlDoc = YFCDocument.parse("<Item/>");
					YFCElement elemTemlItem = inputTemlDoc.getDocumentElement();
					elemTemlItem.setAttribute("ItemID","");
					YFCElement elemInvParam = elemTemlItem.createChild("InventoryParameters");
					elemInvParam.setAttribute("TagControlFlag","");
					YFCElement elemInvTagAtt = elemTemlItem.createChild("InventoryTagAttributes");
					%>

					<yfc:callAPI apiName="getItemDetails" inputElement="<%=getID_inputDoc.getDocumentElement()%>" templateElement="<%=inputTemlDoc.getDocumentElement()%>" outputNamespace="GetItemDetails"></yfc:callAPI>

					<%
					tagControlFlag = getValue("GetItemDetails","xml:/Item/InventoryParameters/@TagControlFlag");
					revisionNo = getValue("GetItemDetails","xml:/Item/InventoryTagAttributes/@RevisionNo");
					if (tagControlFlag != null && tagControlFlag.equals("Y") && revisionNo != null && (!revisionNo.equals("03"))) {
						nestedTI_UpdateDLT = "Y"; // CR 576 - set it here
					}
					tagControlFlag = "";
					revisionNo = "";
				}
			}
		}
	}
	%>

<script>
yfcDoNotPromptForChanges(true);
var bCalled = false;
var returnMap = new Object();

// added for CR 502
/**
 * DHTML date validation script. 
 */
// Declaring valid date character, minimum year and maximum year
var dtCh= "/";
var minYear=1900;
var maxYear=2500;

function isInteger(s){
	var i;
    for (i = 0; i < s.length; i++){   
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    } // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag){
	var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function daysInFebruary (year){
	// February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) {
	for (var i = 1; i <= n; i++) {
		this[i] = 31
		if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
		if (i==2) {this[i] = 29}
   } 
   return this
}

function isDate(dtStr){
	var daysInMonth = DaysArray(12)
	var pos1=dtStr.indexOf(dtCh)
	var pos2=dtStr.indexOf(dtCh,pos1+1)
	var strMonth=dtStr.substring(0,pos1)
	var strDay=dtStr.substring(pos1+1,pos2)
	var strYear=dtStr.substring(pos2+1)
	strYr=strYear
	if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
	if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
	for (var i = 1; i <= 3; i++) {
		if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
	}
	month=parseInt(strMonth)
	day=parseInt(strDay)
	year=parseInt(strYr)
	if (pos1==-1 || pos2==-1){
		alert("The date format should be : mm/dd/yyyy")
		return false
	}
	if (strMonth.length<1 || month<1 || month>12){
		alert("Please enter a valid month")
		return false
	}
	if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
		alert("Please enter a valid day")
		return false
	}
	if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
		alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
		return false
	}
	if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
		alert("Please enter a valid date")
		return false
	}
	return true
}

function ValidateForm(){
	var dt=document.getElementById("DLU");
	if (isDate(dt.value)==false){
		dt.focus()
		return false
	}
    return true
 }

//added for CR 502 ends here
function prepareComponentMap(){	
	if(bCalled == true)	{
		return returnMap;
	}
	<%
		elemItem = (YFCElement) request.getAttribute("GetItemList");
		YFCNodeList nlComponent = elemItem.getElementsByTagName("Component");
		if(nlComponent != null && nlComponent.getLength() > 0)
		{
			for(int index = 0 ; index < nlComponent.getLength() ; index++ )
			{
				YFCElement elemComponent = (YFCElement) nlComponent.item(index);
				%>
				returnMap['<%=elemComponent.getAttribute("ComponentItemID")%>'] = '<%=elemComponent.getAttribute("KitQuantity")%>';
				<%
			}
		}
	%>
	return returnMap;
	bCalled = true
}
</script>

	<!-- TOP of CR 448 add a new field that displays the remaining qty for refurbishment -->
	<%!
	String TrimCommas(String Str1){
	  String [] Ctfields = Str1.split(",");
	  int scnt = Ctfields.length;
	  String newStr1 = "";
	  for (int i=0;i<scnt;i++){
		newStr1 = newStr1 + Ctfields[i];
	  }
	  return newStr1;
	}
	%>

	<%
	String strActQty = resolveValue("xml:/NWCGMasterWorkOrderLine/@ActualQuantity");
	//System.out.println("strActQty Before " + strActQty);
	strActQty = TrimCommas(strActQty);
	//System.out.println("strActQty After " + strActQty);
	String strRefurbQty = resolveValue("xml:/NWCGMasterWorkOrderLine/@RefurbishedQuantity");
	String transferQty = resolveValue("xml:/NWCGMasterWorkOrderLine/@TransferQty");
	
	strRefurbQty = TrimCommas(strRefurbQty);
	if (strRefurbQty == null || strRefurbQty.equals(""))
		strRefurbQty = "0";
		
	if((transferQty == null) || (transferQty != null && transferQty.trim().equals(""))) transferQty = "0.0";
	float fTransferQty = Float.valueOf(transferQty.trim()).floatValue();
		
	float actQty = Float.valueOf(strActQty.trim()).floatValue();
	float refurbQty = Float.valueOf(strRefurbQty.trim()).floatValue();
	float remainQty = actQty - refurbQty - fTransferQty;
	DecimalFormat df1 = new DecimalFormat("#.00");
	String strRemainQty = df1.format(remainQty);
	%>

	<td class="detaillabel">Item to be Refurbished</td><td class="protectedtext"><%=resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemID")%></td>
	<td class="detaillabel">Item Description</td><td class="protectedtext"><%=resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemDesc")%> </td>
	<td class="detaillabel">Actual Qty</td><td class="protectedtext"><%=strActQty%> </td>
	<td class="detaillabel">Remaining Qty</td><td class="protectedtext"><%=strRemainQty%> </td>
	<!-- Bottom of CR 448 add a new field that displays the remaining qty for refurbishment -->

	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@MasterWorkOrderKey" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@MasterWorkOrderKey")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbishedQuantity" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@RefurbishedQuantity")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@TransferQty" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@TransferQty")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@ActualQuantity" value="<%=strActQty%>"/>
	<!-- added for CR 462 starts here -->
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@RFIRefurbQuantity" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@RFIRefurbQuantity")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@UNSRefurbQuantity" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@UNSRefurbQuantity")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@UNSNWTRefurbQuantity" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@UNSNWTRefurbQuantity")%>'/>
	<!-- added for CR 462 ends here -->

	<!-- using javascript populate this varialble - add lines quantity to this value -->
	<input type="hidden" name="xml:/NWCGMasterWorkOrderLine/@RefurbCost" id="xml:/NWCGMasterWorkOrderLine/@RefurbCost" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@RefurbCost")%>'/>
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbCost" id="xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbCost" value='<%=resolveValue("xml:/WorkOrder/NWCGMasterWorkOrderLine/@RefurbCost")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@EnterpriseCode" value='NWCG'/>
	<input type="hidden" name="xml:/WorkOrder/@DocumentType" value='7001'/>
	<input type="hidden" name="xml:/WorkOrder/@EnterpriseInvOrg" value='NWCG'/>
	<input type="hidden" name="xml:/WorkOrder/@UserID" value='<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@EnterpriseCodeForComponent" value='NWCG'/>
	<input type="hidden" name="xml:/WorkOrder/@ItemID" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemID")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@Uom" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@UnitOfMeasure")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@ProductClass" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@ProductClass")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@Node" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@Node")%>'/>

	<input type="hidden" name="xml:/WorkOrder/@Createuserid" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@Createuserid")%>'/>
	<input type="hidden" name="xml:/WorkOrder/@Modifyuserid" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@Modifyuserid")%>'/>
	
	<!-- added for CR 606 starts here -->
	<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@IsReplacedItem" id="xml:/WorkOrder/NWCGMasterWorkOrderLine/@IsReplacedItem" value='<%=resolveValue("xml:/NWCGMasterWorkOrderLine/@IsReplacedItem")%>'/>
	<!-- added for CR 606 ends here -->
	</td>
</tr>
<tr>
	<%
	String strPrimarySerialNo   = resolveValue("xml:/NWCGMasterWorkOrderLine/@PrimarySerialNo");
	String strSecondarySerialNo = resolveValue("xml:/NWCGMasterWorkOrderLine/@SecondarySerialNo1");
	if(strPrimarySerialNo != null && (!strPrimarySerialNo.equals("")))
	{%>			
		<td class="detaillabel">Qty to be refurbed</td>
		<td type="text" class="protectedtext"  style="width:50px">1</td>
		<input type="hidden" name="xml:/WorkOrder/@QuantityRequested" value='1'/>
	<%} else {%>
		<td class="detaillabel">Qty to be refurbed</td><td><input type="text" class="unprotectedinput"  style="width:50px" <%=getTextOptions("xml:/WorkOrder/@QuantityRequested")%>/>
	<%}%>

	<td class="detaillabel">Refurb Location</td>
	<%
	YFCElement elemEquipmentList = (YFCElement) request.getAttribute("EquipmentList");
	YFCNodeList nlEquipmentDetail = null ;
	if(elemEquipmentList != null ){
		nlEquipmentDetail = elemEquipmentList.getElementsByTagName("EquipmentDetail");
	}
	%>
	<td class="protectedtext">
		<select class="combobox" style="width:150px" id="LocationIdSelect" <%=getComboOptions("xml:/WorkOrder/NWCGMasterWorkOrderLine/@LocationID")%>>
		<option/>
		<% if(nlEquipmentDetail  != null){
			for(int index = 0 ; index < nlEquipmentDetail.getLength() ; index ++){
				YFCElement elem = (YFCElement) nlEquipmentDetail.item(index);
			%>
				<option value="<%=elem.getAttribute("LocationId")%>"><%=elem.getAttribute("LocationId")%></option>
			<%}
		}%>
		</select>
	</td>

	<td class="detaillabel">Destination Inventory Status</td>
	<td class="protectedtext">
		<select class="combobox" style="width:150px" <%=getComboOptions("xml:/WorkOrder/NWCGMasterWorkOrderLine/@DestinationInventoryStatus")%>>
		<yfc:loopOptions binding="xml:RefurbStatus:/CommonCodeList/@CommonCode" selected="xml:/WorkOrder/NWCGMasterWorkOrderLine/@DestinationInventoryStatus" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>

	<%
	int iColumnCount = 0;
	YFCElement elemItemList = (YFCElement) request.getAttribute("ItemList");
	YFCNodeList nlInventoryTagAttributes  = null;
	HashMap hm = new HashMap();
	// this code will determine the tag attributes - if the item is serial tracked or not will be determined seperately
	if(elemItemList != null){
		nlInventoryTagAttributes  = elemItemList.getElementsByTagName("InventoryTagAttributes"); 
		for(int index = 0 ; index < nlInventoryTagAttributes.getLength() ; index++){
			YFCElement elemInventoryTagAttributes = (YFCElement) nlInventoryTagAttributes.item(index);

			// value 03 represents the item is not using this tag attribute - enter this item if and only if the item controlled by this tag
			// value 01 represents its tag attribute - not mandatory
			// value 02 represents its tag controlled - mandatory

			String str = elemInventoryTagAttributes.getAttribute("LotAttribute1");
			if(str != null && (!str.equals("03")))
				hm.put("LotAttribute1",resolveValue("xml:/NWCGMasterWorkOrderLine/@ManufacturerName"));

			str = elemInventoryTagAttributes.getAttribute("LotAttribute2");
			if(str != null && (!str.equals("03")))
				hm.put("LotAttribute2",resolveValue("xml:/NWCGMasterWorkOrderLine/@OwnerUnitID"));

			str = elemInventoryTagAttributes.getAttribute("LotAttribute3");
			if(str != null && (!str.equals("03")))
				hm.put("LotAttribute3",resolveValue("xml:/NWCGMasterWorkOrderLine/@ManufacturerModel"));

			str = elemInventoryTagAttributes.getAttribute("LotKeyReference");
			if(str != null && (!str.equals("03")))
				hm.put("LotKeyReference",resolveValue("xml:/NWCGMasterWorkOrderLine/@LotKeyReference"));

			str = elemInventoryTagAttributes.getAttribute("LotNumber");
			if(str != null && (!str.equals("03")))
				hm.put("LotNumber",resolveValue("xml:/NWCGMasterWorkOrderLine/@LotNo"));

			str = elemInventoryTagAttributes.getAttribute("ManufacturingDate");
			if(str != null && (!str.equals("03")))
				hm.put("ManufacturingDate",resolveValue("xml:/NWCGMasterWorkOrderLine/@ManufacturingDate"));

			str = elemInventoryTagAttributes.getAttribute("RevisionNo");
			if(str != null && (!str.equals("03")))
				hm.put("RevisionNo",resolveValue("xml:/NWCGMasterWorkOrderLine/@RevisionNo"));

			str = elemInventoryTagAttributes.getAttribute("BatchNo");
			if(str != null && (!str.equals("03")))
				hm.put("BatchNo",resolveValue("xml:/NWCGMasterWorkOrderLine/@BatchNo"));
		}
	}
	// get the iterator and display all the attributes one by one
	Iterator itr = null;
	itr = hm.keySet().iterator();
	if(itr != null && itr.hasNext()){
		while(itr.hasNext()){
			String strKey = (String) itr.next();
			String strValue = (String)hm.get(strKey);
			if(iColumnCount == 0){
			%>
				<tr>
			<%}
			if(iColumnCount == 3){
			%>
				</tr>
			<%
				iColumnCount = 0 ;
			}%>

			<td class="detaillabel"><yfc:i18n><%=strKey%></yfc:i18n></td>
			<%
			if(strKey.equals("LotAttribute1")) strKey = "ManufacturerName" ;
			if(strKey.equals("LotAttribute2")) strKey = "OwnerUnitID" ;
			if(strKey.equals("LotAttribute3")) strKey = "ManufacturerModel" ;
			if(strKey.equals("LotNumber")) strKey = "LotNo" ;
			%>
			<td class="unprotectedtext">
			<input type="text" <%=(strKey.equals("RevisionNo") || strKey.equals("RevisionNumber") || strValue.equals("")) ? "":" readonly=true "	%>class="unprotectedinput" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@<%=strKey%>" 
			
			<%//added for CR 502
			if((strKey.equals("RevisionNo") || strKey.equals("RevisionNumber"))) { %>
				onblur="return ValidateForm()" id="DLU"
			<% } %>
			value="<%=strValue%>" /></td>

			<%
			if((strKey.equals("RevisionNo") || strKey.equals("RevisionNumber"))) { %>
				<!-- send the old revision number as hidden field -->
				<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@OldRevisionNo" value='<%=strValue%>'/>
				<td class="detaillabel">Update Date Last Tested</td>
				<td>
				<input type="checkbox" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@UpdateDLT" value="Checked" />
				</td>
			<% } %>

			<%if( strKey.equals("LotNo") && (strPrimarySerialNo != null && (!strPrimarySerialNo.equals("")))) {%>
				<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@PrimarySerialNo" value="<%=strPrimarySerialNo%>">
			<%}%>
			<%if( strKey.equals("LotNo") && (strSecondarySerialNo != null && (!strSecondarySerialNo.equals("")))) {%>
				<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@SecondarySerialNo1" value="<%=strSecondarySerialNo%>">
			<%}%>

			<% //CR 502 testing remove later
			iColumnCount++;
		} // while

	} else {%>
		<!-- this is when the item is Non Tag Controlled or a KIT item, within this Item, a component can be a KIT item -->
		<%if (nestedTI_UpdateDLT.equals("Y")){ // CR 576 %>
			<td class="detaillabel">Update Date Last Tested on Component</td>
			<td>
			<input type="checkbox" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@UpdateDLT" value="Checked" />
			</td>
		<%}%>

		<%if(strPrimarySerialNo != null && (!strPrimarySerialNo.equals(""))){%>
			<tr>
				<td class="detaillabel">Trackable ID</td>
				<td class="protectedtext"><%=strPrimarySerialNo%>
				<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@PrimarySerialNo"  value="<%=strPrimarySerialNo%>">
				</td>
			</tr>
		<%}%>

		<%
		//int iColumnCount = 0;
		if(strSecondarySerialNo != null && (!strSecondarySerialNo.equals(""))){
			iColumnCount++;
		%>
			<tr>
			<td class="detaillabel">Manufacturer Serial #</td>
			<td class="protectedtext"><%=strSecondarySerialNo%>
			<input type="hidden" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@SecondarySerialNo1" value="<%=strSecondarySerialNo%>">
			</td>
		<%}%>

		<%
		String strShipByDate = resolveValue("xml:/NWCGMasterWorkOrderLine/@ShipByDate");
		if(strShipByDate != null && (!strShipByDate.equals(""))){
			iColumnCount++;
		%>
			<td class="detaillabel">Ship By Date</td>
			<td class="protectedtext">
			<input type="text" class="dateinput" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@ShipByDate" value="<%=strShipByDate%>">
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			</td>
		<%}%>
	<%}%>

	<!--<td class="detaillabel">New Date Last Tested</td>
	<td class="protectedtext">
	<input type="text" class="dateinput" name="xml:/WorkOrder/NWCGMasterWorkOrderLine/@DateLastTested">
	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
	</td>-->
</table>