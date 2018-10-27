<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>
<%@ page import="com.yantra.yfs.util.YFSConsts" %>
<script language="javascript">
	yfcDoNotPromptForChanges(true);
</script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_reportRecordReceipt.js"></script>
<!--
the way we are displaying the unit cost
1. for items which are not serially controlled - fire the api, get the supplier item details, render it as default value
2. for items which are serially controlled - fire the api, pass it on to the included page as UnitCost
3. incase of shipment - when the user will type in the item id, we are populating the unit cost on the fly using ajax
-->
<%
	String sIsSingleOrder = "";
	String sIsLinesEntered = "";
	String sOrderAvailableOnSystem = "";
	String sOrderNo = "";
	String sAllowNewItemReceiptFromShipment = "";
	String sAllowNewItemReceiptFromRcvPref = "";
	String applicationCode = "";

	if(isVoid(resolveValue("xml:/Receipt/@ApplicationCode"))){
		applicationCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	}else{
			applicationCode = resolveValue("xml:/Receipt/@ApplicationCode");
	}
	HashMap oMap = new HashMap();
	if (!isVoid(resolveValue("xml:/Receipt/@AfterSave"))) {
		YFCElement root = getRequestDOM();
		//System.out.println("ROOTTTTTTTTTTTT ===>>> "+root );
		YFCElement oReceiptElement = root.getChildElement("Receipt");
		YFCElement oReceiptLines = oReceiptElement.getChildElement("ReceiptLines"); 
		//System.out.println("oReceiptLines===>"+oReceiptLines);
		// jaust thought of commenting this code... 
		/*
		This code is OOB and is return to conver the TagSerial tag to the ReceiptLine tags
		Example :
		This to be converted to an format which is understood by the receiveOrder API
		<ReceiptLines>
			<ReceiptLine DispositionCode="RFI" ItemID="001100"
				OrderLineKey="20060818154002522487" ProductClass="Supply"
				ShipmentLineKey="" SuggestedQuantity="3.0" UnitOfMeasure="EACH" YFC_NODE_NUMBER="1">
				<SerialDetail SecondarySerial1="asfsadf" YFC_NODE_NUMBER="1"/>
				<SerialDetail SecondarySerial1="asfdsdf" YFC_NODE_NUMBER="2"/>
				<TagSerial SerialNo="asfdasdf" YFC_NODE_NUMBER="1">
					<Extn ReceivingPrice="12"/>
				</TagSerial>
				<TagSerial SerialNo="asfsadf" YFC_NODE_NUMBER="2">
					<Extn ReceivingPrice="111"/>
				</TagSerial>
			</ReceiptLine>
			<ReceiptLine DispositionCode="RFI" ItemID="001100"
				OrderLineKey="20060809155705491771" ProductClass="Supply"
				ShipmentLineKey="" SuggestedQuantity="0.0" UnitOfMeasure="EACH" YFC_NODE_NUMBER="2"/>
		</ReceiptLines>
		
		Above XML is converted to 

		<Receipt AfterSave="Y" ApplicationCode="oms" CaseId="" CloseCase="N"
    ClosePallet="N" DocumentType="0005" JunkId="N" PalletId=""
    ReceiptHeaderKey="20060809155806491784" ReceiptNo="100000691-1" ReceivingNode="RMK">
		<ReceiptLines>
			<ReceiptLine DispositionCode="RFI" ItemID="001100"
				OrderLineKey="20060818154002522487" ProductClass="Supply"
				Quantity="1" SerialNo="asfdasdf" ShipmentLineKey=""
				SuggestedQuantity="3.0" UnitOfMeasure="EACH" YFC_NODE_NUMBER="1">
				<SerialDetail SecondarySerial1="asfsadf" YFC_NODE_NUMBER="1"/>
				<Extn ReceivingPrice="12"/>
			</ReceiptLine>
			<ReceiptLine DispositionCode="RFI" ItemID="001100"
				OrderLineKey="20060818154002522487" ProductClass="Supply"
				Quantity="1" SerialNo="asfsadf" ShipmentLineKey=""
				SuggestedQuantity="3.0" UnitOfMeasure="EACH" YFC_NODE_NUMBER="2">
				<SerialDetail SecondarySerial1="asfdsdf" YFC_NODE_NUMBER="2"/>
				<Extn ReceivingPrice="111"/>
			</ReceiptLine>
		</ReceiptLines>
	</Receipt>
		What we are doing out here is pulling all the TagSerial attributes and putting it under ReceiptLine SerialDetail reamins the same 
		*/
		if(oReceiptLines != null) 
		{
		for (Iterator i = oReceiptLines.getChildren(); i.hasNext();) {
			YFCElement oReceiptLine = (YFCElement) i.next();
			boolean bLineHasChild = false;
			YFCNodeList nl = oReceiptLine.getElementsByTagName("SerialDetail");
			//System.out.println("NODE LENGTH == >>> "+nl.getLength());
			if(!isVoid(nl)){
			for(int j=0;j<nl.getLength();j++) { /*Constructing input when secondary serial information is mandatory*/
				YFCElement oSerialDetail = (YFCElement)nl.item(j);
	            bLineHasChild=true;
				YFCElement oReceiptLineClone = oReceiptLines.createChild("ReceiptLine");
				oReceiptLineClone.setAttributes(oReceiptLine.getAttributes());
				YFCElement oSerialDetailClone = oReceiptLineClone.createChild("SerialDetail");
				oSerialDetailClone.setAttributes(oSerialDetail.getAttributes());
				YFCNodeList n = oReceiptLine.getElementsByTagName("TagSerial");
			    for(int k=0;k<n.getLength();k++) {
			    YFCElement oTagSerial = (YFCElement)n.item(k);
				if(k==j){
                YFCElement oTagSerialDetailClone = oReceiptLineClone.createChild("TagSerial");
				oTagSerialDetailClone.setAttributes(oTagSerial.getAttributes());
                oReceiptLineClone.setAttributes(oTagSerialDetailClone.getAttributes());
				oReceiptLineClone.removeChild(oTagSerialDetailClone);

				if(!YFCObject.isVoid(oTagSerial.getAttribute("SerialNo"))){
				  oReceiptLineClone.setAttribute("SerialNo",oTagSerial.getAttribute("SerialNo"));
				  oReceiptLineClone.setAttribute("Quantity","1");
				}
				if(!YFCObject.isVoid(oTagSerial.getAttribute("ShipByDate"))){
				  oReceiptLineClone.setAttribute("ShipByDate",oTagSerial.getAttribute("ShipByDate"));
				}
                //System.out.println("oSerialDetail ==> CHECKING FOR NODE =====>>>>> "+oTagSerial.getNodeName());
				if(oTagSerial != null && oTagSerial.getChildElement("Extn") != null ){
				//System.out.println("setting it upppppp ");
				oReceiptLineClone.createChild("Extn").setAttributes(oTagSerial.getChildElement("Extn").getAttributes());
				}
				//System.out.println("oSerialDetail.getAttributes()  ==> "+oSerialDetail.getAttributes());
				if(YFCObject.equals(oSerialDetail.getNodeName(), "Extn")){
				  //System.out.println("oSerialDetail.getAttributes()  ==> "+oSerialDetail.getAttributes());
				  oReceiptLineClone.createChild("Extn").setAttributes(oSerialDetail.getAttributes());
				}
				
				}
			  } 
			} 
			if(nl.getLength()==0){ /* Constructing input when no secondary serials are mandatory */
				YFCNodeList serialTrack = oReceiptLine.getElementsByTagName("TagSerial");
			    for(int s=0;s<serialTrack.getLength();s++) {
				  YFCElement oSerial = (YFCElement)serialTrack.item(s);
				  bLineHasChild=true;
				  YFCElement oReceiptLineCloneSerial = oReceiptLines.createChild("ReceiptLine");
				  oReceiptLineCloneSerial.setAttributes(oReceiptLine.getAttributes());
				  YFCElement oSerialClone = oReceiptLineCloneSerial.createChild("TagSerial");
				  oSerialClone.setAttributes(oSerial.getAttributes());
				  oReceiptLineCloneSerial.setAttributes(oSerialClone.getAttributes());

				  if(!YFCObject.isVoid(oSerial.getAttribute("SerialNo"))){
				  oReceiptLineCloneSerial.setAttribute("SerialNo",oSerial.getAttribute("SerialNo"));
				  oReceiptLineCloneSerial.setAttribute("Quantity","1");
				  //System.out.println("YFCObject.equals(oSerial.getNodeName()  ==> "+oSerial);
				  
				  if(oSerial != null && oSerial.getChildElement("Extn") != null ){
					//System.out.println("setting it upppppp ");
					YFCElement elemExtn = oReceiptLineCloneSerial.getChildElement("Extn",true);
					elemExtn.setAttributes(oSerial.getChildElement("Extn",false).getAttributes(true));
					
				  }
				}
				 if(!YFCObject.isVoid(oSerial.getAttribute("ShipByDate"))){
				  oReceiptLineCloneSerial.setAttribute("ShipByDate",oSerial.getAttribute("ShipByDate"));
				} 
				oReceiptLineCloneSerial.removeChild(oSerialClone);	
			  }
			}
		  }
		if(bLineHasChild){
				oReceiptLines.removeChild(oReceiptLine);				
			}
		   } 
		 } 
		//System.out.println("BEFORE   POSTINGGGGGGGGGGGGGG ==== >>> "+oReceiptElement);
%>
		<yfc:callAPI apiID="AP2"/>	 <!--getShipmentLineList-->
<%		YFCElement oShipmentLinesElement = (YFCElement) request.getAttribute("ShipmentLines");
		if (oShipmentLinesElement != null) {
			for (Iterator oIter = oShipmentLinesElement.getChildren(); oIter.hasNext();) {
				YFCElement oShipmentLineElement = (YFCElement) oIter.next();
				oMap.put(oShipmentLineElement.getAttribute("ShipmentLineKey"),oShipmentLineElement.getChildElement("ShipmentTagSerials"));
			}
		}
		if (oReceiptLines != null) {
			for (Iterator oIter = oReceiptLines.getChildren(); oIter.hasNext();) {
				YFCElement oReceiptLine = (YFCElement) oIter.next();
				if (oReceiptLine.getDoubleAttribute("Quantity") > 0 || oReceiptLine.hasAttribute("SerialNo")|| oReceiptLine.hasAttribute("ShipByDate")){
					if (YFCCommon.equals(oReceiptLine.getAttribute("SuggestedQuantity"),oReceiptLine.getAttribute("Quantity")) && oMap.containsKey(oReceiptLine.getAttribute("ShipmentLineKey"))) {
						YFCElement oShipmentTagSerials = (YFCElement) oMap.get(oReceiptLine.getAttribute("ShipmentLineKey"));
						if (oShipmentTagSerials.hasChildNodes() && !isTagAlreadyEntered(oReceiptLine)) {
							for (Iterator oIter1 = oShipmentTagSerials.getChildren(); oIter1.hasNext();) {
								YFCElement oShipmentTagSerialElement = (YFCElement) oIter1.next();
								copyTagSerials(oReceiptLines.createChild("ReceiptLine"),oReceiptLine,oShipmentTagSerialElement);
							}
							oReceiptLines.removeChild(oReceiptLine);
						}
					}
				} else {
					oReceiptLines.removeChild(oReceiptLine);
				}
			}
		} 
		YFCElement oTempElement = YFCDocument.parse("<Receipt ReceiptHeaderKey=\"\" />").getDocumentElement(); 
		//System.out.println("POSTINGGGGGGGGGGGGGG ==== >>> "+oReceiptElement);
		%>
		<yfc:callAPI apiName="receiveOrder" inputElement="<%=oReceiptElement%>" templateElement="<%=oTempElement%>" outputNamespace="ReceiveOrder"/>
		<script>
			window.returnValue = "Refresh";
		</script>
<%	}

	%>
		<yfc:callAPI apiID="AP3"/>	 <!--getShipmentDetails-->
	<%		
	
	YFCElement oShipmentElement = (YFCElement) request.getAttribute("Shipment");
	if (oShipmentElement != null) {
		sIsSingleOrder = oShipmentElement.getAttribute("IsSingleOrder");
		sIsLinesEntered = oShipmentElement.getAttribute("LinesEntered");
		sOrderAvailableOnSystem = oShipmentElement.getAttribute("OrderAvailableOnSystem");
		sOrderNo = oShipmentElement.getAttribute("OrderNo");
		sAllowNewItemReceiptFromShipment = oShipmentElement.getAttribute("AllowNewItemReceipt");
	}
	
	//Get the Node Receiving Preference
	%>
		<yfc:callAPI apiID="AP4"/>	 <!--getNodeReceivingPreferenceList-->
	<%
	
	YFCElement oNodeReceivingPrefsElement = (YFCElement) request.getAttribute("NodeReceivingPreferences");
	if (!isVoid(oNodeReceivingPrefsElement)) {
		YFCElement oNodeReceivingPrefElement = (YFCElement) oNodeReceivingPrefsElement.getChildElement ("NodeReceivingPreference");
		if (!isVoid(oNodeReceivingPrefElement)) {
			sAllowNewItemReceiptFromRcvPref = oNodeReceivingPrefElement.getAttribute("AllowNewItemReceipt");
		}
	}
%>
<%
	boolean bOrderAvailableOnSystem = false;
	if(equals("Y",sOrderAvailableOnSystem)) {
		bOrderAvailableOnSystem = true;
	}
%>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP5"/>
<yfc:callAPI apiID="AP6"/>
<input type="hidden" name="xml:/Receipt/@AfterSave" Value="Y"/> 
<table with="100%" class="view" cellpadding=0 cellspacing=0 >
<% if(equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))){%>
<tr>
	<td style="padding:5px;width:70%">
		<table width="100%" border=0 cellspacing=0 cellpadding=0>
		<tr>
		   <td class="searchlabel" width="20px">
		   <input type="Radio" class="radiobutton" onclick="setpalletattributes();" <%=getRadioOptions("xml:/Receipt/@JunkId","xml:/Receipt/@JunkId","N")%>/>
		   </td><td class="searchlabel">
			<yfc:i18n>Pallet_ID</yfc:i18n>&nbsp;
		   </td>
			<td >
			<div id="receive_pallet_id" class="searchlabel" style="visibility:hidden">
			<input type="text"  class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@PalletId")%>/>&nbsp;
			<yfc:i18n>Pallet_Completely_Received</yfc:i18n>
		   <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@ClosePallet","N","Y")%>/>
		   </div>
			</td>
		</tr>
		<tr>
			<td class="searchlabel" width="20px">
			<input type="Radio" class="radiobutton" onclick="setcaseattributes()"  <%=getRadioOptions("xml:/Receipt/@JunkId","xml:/Receipt/@JunkId","N")%>/>
		   </td><td class="searchlabel">
			<yfc:i18n>Case_ID</yfc:i18n>&nbsp;
			</td>
			<td>
			<div class="searchlabel" id="receive_case_id" style="visibility:hidden">
			<input type="text"  class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@CaseId")%>/>&nbsp;
			<yfc:i18n>Case_Completely_Received</yfc:i18n>&nbsp;
			<input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@CloseCase","N","Y")%>/>
			</div>
			</td>
		</tr>
		<tr>
		   <td>
		   <input type="Radio" class="radiobutton" onclick="setnoneattributes();" checked=true <%=getRadioOptions("xml:/Receipt/@JunkId","xml:/Receipt/@JunkId","N")%>/>
		   </td><td class="searchlabel">
			<yfc:i18n>None</yfc:i18n>&nbsp;
		   </td>
		</tr>
	</table>
</td>
<td></td>
</tr>
<tr>
<%}%>
<td colspan="2">
<div style="height:200px;overflow:auto">
<table class="table" width="100%" ID="LinesToReceive">
<thead>
    <tr>
      	<td class="tablecolumnheader" sortable="no">&nbsp;
		</td>
		<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ShipmentLineNo")%>"><yfc:i18n>Shipment_Line_#</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderNo")%>"><yfc:i18n><%=resolveValue("xml:/Receipt/@DocumentType") + "_"%>Order_#</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@TotalQuantity")%>"><yfc:i18n>Total_Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ReceivedQuantity") %>"><yfc:i18n>Received_Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:100px"><yfc:i18n>Quantity_To_Be_Received</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" ><yfc:i18n>Disposition_Code</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:100px"><yfc:i18n>Receiving_Quantity</yfc:i18n></td>
		<td class="tablecolumnheader" sortable="no" style="width:100px"><yfc:i18n>Receiving_Price</yfc:i18n></td>
    </tr>
</thead>
<tbody>
	<input type="hidden" <%=getTextOptions("xml:/Receipt/@ApplicationCode", "xml:/Receipt/@ApplicationCode","xml:/CurrentEntity/@ApplicationCode")%>/>
<%	YFCElement tempItemElement = YFCDocument.parse("<ItemList><Item ItemID=\"\" ItemKey=\"\"> <PrimaryInformation NumSecondarySerials=\"\" SerializedFlag=\"\" /><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/> <InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item></ItemList>").getDocumentElement();
	YFCElement oDispositionElement = YFCDocument.createDocument("ReturnDisposition").getDocumentElement();
	YFCElement tempElem = YFCDocument.parse("<ReturnDispositionList> <ReturnDisposition Description=\"\" DispositionCode=\"\" DispositionKey=\"\" OrganizationCode=\"\" ProductClass=\"\" /> </ReturnDispositionList>").getDocumentElement();
	oDispositionElement.setAttribute("DocumentType",resolveValue("xml:/Receipt/@DocumentType"));
	oDispositionElement.setAttribute("CallingOrganizationCode", resolveValue("xml:Receipt:/Receipt/Shipment/@EnterpriseCode")); 
    String className="oddrow"; %>
	<yfc:callAPI apiName="getReturnDispositionList" inputElement="<%=oDispositionElement%>" templateElement="<%=tempElem%>" outputNamespace="DispositionList"/>
    <yfc:loopXML binding="xml:/GetLinesToReceive/ReceivableLineList/@ReceivableLine" id="ReceivableLine">
        <tr class='<%=className%>'>
		 <tr/><tr/>
<%		
			String strItemId = resolveValue("xml:/ReceivableLine/@ItemID");
			String strSeller = resolveValue("xml:/Receipt/Shipment/@SellerOrganizationCode");
			YFCElement eleSupItem = YFCDocument.createDocument("NWCGGetSupplierItem").getDocumentElement(); 
			eleSupItem.setAttribute("ItemID",strItemId);
			eleSupItem.setAttribute("SupplierID",strSeller);
			%>

			<yfc:callAPI serviceName="NWCGGetSupplierItemDetail" inputElement="<%=eleSupItem%>"  outputNamespace="SupplierItemDetails"/>
			
			<%
			YFCElement record = (YFCElement) request.getAttribute("SupplierItemDetails") ;
			String strUnitCost = "" ;
			
			if(record != null)
				strUnitCost = record.getAttribute("UnitCost");

			//System.out.println("strUnitCost===>"+strUnitCost);

			YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement(); 
			oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:Receipt:/Receipt/Shipment/@EnterpriseCode"));
			oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/ReceivableLine/@ItemID"));
			oItemDetailsElement.setAttribute("Node",resolveValue("xml:/Receipt/@ReceivingNode"));
			oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/ReceivableLine/@UnitOfMeasure")); %>
			<yfc:callAPI apiName="getItemList" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>			
<%				
            
			YFCElement itemDetailsElem = (YFCElement)request.getAttribute("ItemDetails");
			if(itemDetailsElem != null){
				request.setAttribute("ItemDetails",(YFCElement)itemDetailsElem.getElementsByTagName("Item").item(0));
			}%>
			<% boolean bshowGrid=equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag")) ||equals("S",getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag"))|| equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked"))|| equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))||
			(equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerializedFlag")) && equals("omr",applicationCode)); %>
			<td/>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:/ReceivableLine/@ShipmentLineNo"/>
			</td>            
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:/ReceivableLine/@OrderNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:/ReceivableLine/@ReleaseNo"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@ItemID"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@UnitOfMeasure"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@ProductClass"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@TotalQuantity"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@ReceivedQuantity"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue binding="xml:/ReceivableLine/@AvailableToReceiveQuantity"/>
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@ItemID" %>" value="<%=resolveValue("xml:/ReceivableLine/@ItemID")%>"/>
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@ProductClass" %>" value="<%=resolveValue("xml:/ReceivableLine/@ProductClass")%>"/>
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@UnitOfMeasure" %>" value="<%=resolveValue("xml:/ReceivableLine/@UnitOfMeasure")%>"/>
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@OrderLineKey" %>" value="<%=resolveValue("xml:/ReceivableLine/@OrderLineKey")%>"/>
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@ShipmentLineKey" %>" value="<%=resolveValue("xml:/ReceivableLine/@ShipmentLineKey")%>"/> 
				<input type="hidden" name="<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@SuggestedQuantity" %>" value="<%=resolveValue("xml:/ReceivableLine/@AvailableToReceiveQuantity") %>"/>
			</td>
			<td class="tablecolumn">
				<select <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ReceivableLineCounter+"/@DispositionCode")%> class="combobox">
					<yfc:loopOptions binding="xml:DispositionList:/ReturnDispositionList/@ReturnDisposition" name="Description" 
					value="DispositionCode" selected='<%=resolveValue("xml:/ReceivableLine/@SuggestedDispositionCode" )%>' isLocalized="Y"/>
				 </select>
			</td>
			<td class="tablecolumn">
			   <% if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked")) || (equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerializedFlag")) && equals("omr",applicationCode))){ %>
			</td>
			<td align='center' class="tablecolumn">
				N/A
			</td>
			<%}else{%>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/@Quantity","xml:/ReceivableLine/@AvailableToReceiveQuantity")%> style='width:100px'/>
			</td>
				<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter+ "/Extn/@ReceivingPrice",strUnitCost)%> style='width:100px'/>
			</td>
			<%}%>
				<%if(bshowGrid){%>
			<tr id='<%="optionSet_"+ReceivableLineCounter%>' class='<%=className%>' style="display:">
			<tr/>
				<td colspan="9" cellspacing="0" cellpadding="0">
					<jsp:include page="/extn/om/receipt/detail/nwcg_extended_shipmentbasedreceipt_detail_includetag.jsp" flush="true" >
						<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(ReceivableLineCounter)%>'/>
						<jsp:param name="Modifiable" value='true'/>
						<jsp:param name="UnitCost" value='<%=strUnitCost%>'/>
						<jsp:param name="LabelTDClass" value='detaillabel'/>
						<jsp:param name="InputTDClass" value='searchcriteriacell'/>
						<jsp:param name="BindingPrefix" value='<%="xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter%>'/>
						<jsp:param name="SerialTracked" value='<%=getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked")%>'/>
						<jsp:param name="Serialized" value='<%=getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerializedFlag")%>'/>
						<jsp:param name="applicationCode" value='<%=applicationCode%>'/>
						<jsp:param name="NumSecondarySerials" value='<%=getValue("ItemDetails","xml:/Item/PrimaryInformation/@NumSecondarySerials")%>'/>
						<jsp:param name="TimeSensitive" value='<%=getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive")%>'/>
						<jsp:param name="ItemID" value='<%=resolveValue("xml:/ReceivableLine/@ItemID")%>'/>
						<jsp:param name="ProductClass" value='<%=resolveValue("xml:/ReceivableLine/@ProductClass")%>'/>
						<jsp:param name="UnitOfMeasure" value='<%=resolveValue("xml:/ReceivableLine/@UnitOfMeasure")%>'/>
						<jsp:param name="OrderLineKey" value='<%=resolveValue("xml:/ReceivableLine/@OrderLineKey")%>'/>
						<jsp:param name="ShipmentLineKey" value='<%=resolveValue("xml:/ReceivableLine/@ShipmentLineKey")%>'/>
					</jsp:include>
				</td>
			</tr>
	<%}%>
<%			if (equals("oddrow",className)) 
				className="evenrow";
			else 
				className="oddrow";	%>
			</tr>
    </yfc:loopXML> 
</tbody>
<%  if (YFCObject.equals(sAllowNewItemReceiptFromRcvPref, "SHIPMENT") ||
		YFCObject.equals(sAllowNewItemReceiptFromShipment, "SHIPMENT")) {
		if (YFCObject.equals(YFSConsts.YFS_YES, sIsLinesEntered) 
			|| (!YFCObject.equals(YFSConsts.YFS_YES, sIsLinesEntered) && !YFCObject.equals(YFSConsts.YFS_YES, sIsSingleOrder))
			|| (!YFCObject.equals(YFSConsts.YFS_YES, sIsLinesEntered) && !bOrderAvailableOnSystem)) {	
%>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" >
        </td>
		<td class="tablecolumn" >
			</td> 			
			<td class="tablecolumn" nowrap="true">				
			<%	if(bOrderAvailableOnSystem) { 
				if (YFCObject.equals(YFSConsts.YFS_YES, sIsSingleOrder)) {%>
					<yfc:getXMLValue binding="xml:/ReceivableLine/@OrderNo"/>
					<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@OrderNo", "xml:/ReceivableLine/@OrderNo")%>/>
				<% }
				else { %>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@OrderNo")%>/>
				<img class="lookupicon" onclick="callLookup(this,'polookup','xml:/Order/@DocumentType=<%=resolveValue("xml:/Shipment/@DocumentType")%>&xml:/Order/@EnterpriseCode=<%=resolveValue("xml:/Shipment/@EnterpriseCode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/> 
				<% }
			} %> 
			</td>
            <td class="tablecolumn" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@ReleaseNo")%>/>
			</td>
            <td class="tablecolumn" nowrap="true">
				<input type="text" class="unprotectedinput" onBlur="fetchDataWithParams(this,'PopulateItemPODetails',populateSupplierItemUnitPrice,setParam(this))"  <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@ItemID")%>/>
				<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item','xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
			</td>
            <td class="tablecolumn">
			<select <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@UnitOfMeasure")%> class="combobox"  >
				<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Receipt/ReceiptLines/ReceiptLine_/@UnitOfMeasure"/>
			</select>
			</td>
            <td class="tablecolumn">
				<select class="combobox"  <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@ProductClass")%>>
				<yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" 
					name="CodeValue" value="CodeValue" selected="xml:/Receipt/ReceiptLines/ReceiptLine_/@ProductClass"/>
				</select>
			</td>
            <td class="tablecolumn">
			</td>
            <td class="tablecolumn">
			</td>
			<td class="tablecolumn">
			</td>
			<td class="tablecolumn">
				<select <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@DispositionCode")%> class="combobox">
					<yfc:loopOptions binding="xml:DispositionList:/ReturnDispositionList/@ReturnDisposition" name="Description" 
					value="DispositionCode" selected='<%=resolveValue("xml:/ReceivableLine/@SuggestedDispositionCode" )%>' isLocalized="Y"/>
				 </select>
			</td>
		    <td class="tablecolumn">
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/@Quantity")%> style='width:100px'/>
			</td>

		    <td class="tablecolumn">
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_/Extn/@ReceivingPrice")%> style='width:100px'/>
			</td>
        
		</tr>
	<tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
    		</jsp:include>
    	</td>
    </tr>
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@EnterpriseCode")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@DocumentType")%> />
	
</tfoot>
<% 
		}
	}
%>
</table>
</div>
</td>
</tr>
<table>

<%!
	private void copyTagSerials(YFCElement oNewReceiptLine, YFCElement oOldReceiptLine,YFCElement oShipmentTagSerialElement) {
		oNewReceiptLine.setAttributes(oOldReceiptLine.getAttributes());
		if (oNewReceiptLine.getDoubleAttribute("Quantity") > oShipmentTagSerialElement.getDoubleAttribute("Quantity")) {
			oNewReceiptLine.setAttribute("Quantity",oShipmentTagSerialElement.getAttribute("Quantity"));
		}
		oNewReceiptLine.setAttribute("BatchNo",oShipmentTagSerialElement.getAttribute("BatchNo"));
		oNewReceiptLine.setAttribute("RevisionNo",oShipmentTagSerialElement.getAttribute("RevisionNo"));
		oNewReceiptLine.setAttribute("LotNumber",oShipmentTagSerialElement.getAttribute("LotNumber"));
		oNewReceiptLine.setAttribute("LotAttribute1", oShipmentTagSerialElement.getAttribute("LotAttribute1"));	
		oNewReceiptLine.setAttribute("LotAttribute2", oShipmentTagSerialElement.getAttribute("LotAttribute2"));	
		oNewReceiptLine.setAttribute("LotAttribute3", oShipmentTagSerialElement.getAttribute("LotAttribute3"));	
		oNewReceiptLine.setDateAttribute("LotExpirationDate", oShipmentTagSerialElement.getDateAttribute("LotExpirationDate"));	
		oNewReceiptLine.setAttribute("LotKeyReference", oShipmentTagSerialElement.getAttribute("LotKeyReference"));	
		oNewReceiptLine.setDateAttribute("ManufacturingDate", oShipmentTagSerialElement.getDateAttribute("ManufacturingDate"));			
		YFCElement extnTagElem = oShipmentTagSerialElement.getChildElement("Extn");
		if(!YFCObject.isVoid(extnTagElem)){
			oNewReceiptLine.getChildElement("Extn", true).setAttributes(extnTagElem.getAttributes(true));
		}
		oNewReceiptLine.setAttribute("SerialNo",oShipmentTagSerialElement.getAttribute("SerialNo"));
		oNewReceiptLine.setAttribute("ShipByDate",oShipmentTagSerialElement.getAttribute("ShipByDate"));
	}
%>

<%!
	private boolean isTagAlreadyEntered(YFCElement oReceiptLine) {
		if (!isVoid(oReceiptLine.getAttribute("BatchNo")) || !isVoid(oReceiptLine.getAttribute("RevisionNo")) || !isVoid(oReceiptLine.getAttribute("LotNumber")) || !isVoid(oReceiptLine.getAttribute("LotAttribute1")) || !isVoid(oReceiptLine.getAttribute("LotAttribute2")) || !isVoid(oReceiptLine.getAttribute("LotAttribute3")) || !isVoid(oReceiptLine.getAttribute("LotExpirationDate")) || !isVoid(oReceiptLine.getAttribute("LotKeyReference")) || !isVoid(oReceiptLine.getAttribute("ManufacturingDate")) || hasExtnAttributes(oReceiptLine)) {
			return true;
		}
		return false;
	}

	private boolean hasExtnAttributes(YFCElement oReceiptLine){
		YFCElement extnElem = oReceiptLine.getChildElement("Extn");
		if(!YFCObject.isVoid(extnElem)){
			return extnElem.getAttributes(true).size() > 0 ;
		}
		return false ;
	}
%>
<% if(equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))){%>
<script>
		document.all("xml:/Receipt/@CaseId").value="";
		document.all["xml:/Receipt/@CloseCase"].checked=false;
		document.all("xml:/Receipt/@PalletId").value="";
		document.all["xml:/Receipt/@ClosePallet"].checked=false;
</script>
<%}%>