<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="java.util.*" %>
<script language="javascript">
	function proceedClickedForSelect(obj) {
		if (yfcAllowSingleSelection('chkEntityKey')) {
			var myObject = new Object();
			yfcShowDetailPopupWithKeys(getCurrentViewId()," ","900", "650", myObject,"chkEntityKey","shipmentbasedreceipt","chkEntityKey");
		}
	}

	function enableSave() 
	{
		var showTagDetailsGrid = document.getElementById('xml:/Receipt/@ShowTagDetailsGrid');		
		if(showTagDetailsGrid.value == 'true'){
			if (validateTagDetails()){
				if (document.all("proceedBtn")){
					alert('<%=getI18N("Select_A_Receipt_And_Press_Proceed")%>');
					return false;
				}
				if(validateControlValues()){
					yfcChangeDetailView(getCurrentViewId());
				}
			}
		}
		else{
			if (document.all("proceedBtn")){
				alert('<%=getI18N("Select_A_Receipt_And_Press_Proceed")%>');
				return false;
			}
			if(validateControlValues()){
				yfcChangeDetailView(getCurrentViewId());
			}
		}
		return false;
	}
	
	function popupStartReceipt() {
		var myObject = new Object();
		yfcShowDetailPopupWithKeys("YOMD8021"," ","650","400",myObject,"startReceiptKey");
	}
	
	function validateTagDetails()
	{
		var availableToReceiveQty = document.getElementById('AvailableToReceiveQty');		
		var numReceivableLines = document.getElementById('NumReceivableLines');		
		var documentType = document.getElementById('xml:/Receipt/@DocumentType');		
		
		if(documentType.value == '0005')
		{
			for(i = 1; i <= numReceivableLines.value; i++)
			{
				for(j = 1; j <= availableToReceiveQty.value; j++)
				{
					var trackableID = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@LotNumber');		
					var manufacturingDate = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@ManufacturingDate');		
					var dateLastTested = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@RevisionNo');		
					var manufacturerModel = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@LotAttribute3');		
					var ownerUnitID = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@LotAttribute2');		
					var manufacturerName = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@LotAttribute1');		
					var trackableIdNum = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/TagSerial_' + j + '/@SerialNo');		
					var manufacturerSerialNum = document.getElementById('xml:/Receipt/ReceiptLines/ReceiptLine_' + i + '/SerialDetail_' + j + '/@SecondarySerial1');		
					
					//alert('trackableID ' + i + ' ' + j + '= ' + trackableID.value);
					
					if(trackableID && trackableID.value == '') 
					{
						alert('Trackable ID is a mandatory field');
						trackableID.focus();
						return false;
					}
					
					if(manufacturingDate && manufacturingDate.value == '') 
					{
						alert('Manufacturing Date is a mandatory field');
						manufacturingDate.focus();
						return false;
					}
					
					if(dateLastTested && dateLastTested.value == '') 
					{
						alert('Date Last Tested is a mandatory field');
						dateLastTested.focus();
						return false;
					}
					
					if(manufacturerModel && manufacturerModel.value == '') 
					{
						alert('Manufacturer Model is a mandatory field');
						manufacturerModel.focus();
						return false;
					}
					
					if(ownerUnitID && ownerUnitID.value == '') 
					{
						alert('Owner Unit ID is a mandatory field');
						ownerUnitID.focus();
						return false;
					}

					if(manufacturerName && manufacturerName.value == '') 
					{
						alert('Manufacturer Name is a mandatory field');
						manufacturerName.focus();
						return false;
					}

					if(trackableIdNum && trackableIdNum.value == '') 
					{
						alert('Trackable ID # is a mandatory field');
						trackableIdNum.focus();
						return false;
					}

					if(manufacturerSerialNum && manufacturerSerialNum.value == '') 
					{
						alert('Manufacturer Serial # is a mandatory field');
						manufacturerSerialNum.focus();
						return false;
					}
				}		
			}
		}		
		return true;
	}
</script>
<%	double NumberOfRecords =  getNumericValue("xml:ReceiptList:/Receipts/@TotalNumberOfRecords");
if (1 == NumberOfRecords) { %>
<yfc:callAPI apiID="AP2"/>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="2" >
		<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I01"/>
		</jsp:include>
    </td>
</tr>
<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
</table>
<%	} else {
	if (0 == NumberOfRecords) {
		if (!isVoid(resolveValue("xml:Receipt:/Receipt/@OrderReleaseKey"))) { %>
			<yfc:makeXMLInput name="ReleaseKey">
				<yfc:makeXMLKey binding="xml:/OrderRelease/@OrderReleaseKey" value="xml:Receipt:/Receipt/@OrderReleaseKey" ></yfc:makeXMLKey>
			</yfc:makeXMLInput>
			<input name="startReceiptKey" type="hidden" value='<%=getParameter("ReleaseKey")%>'/>
			<script>
				window.attachEvent("onload",popupStartReceipt);
			</script>
	<%		} else if (!isVoid(resolveValue("xml:Receipt:/Receipt/Shipment/@ShipmentKey"))) {
				YFCElement oShipmentElement = YFCDocument.createDocument("Shipment").getDocumentElement();
			YFCElement tempElem = YFCDocument.parse("<Item><PrimaryInformation DefaultProductClass=\"\" CountryOfOrigin=\"\" Description=\"\" ShortDescription=\"\" SerializedFlag=\"\" NumSecondarySerials=\"\"/><InventoryParameters />  <ClassificationCodes /> <ItemAliasList /> <ItemExclusionList /><AdditionalAttributeList />   <LanguageDescriptionList /> <Components /><InventoryTagAttributes/>  <AlternateUOMList TotalNumberOfRecords=\"\" ><AlternateUOM  />  </AlternateUOMList> <ItemInstructionList TotalNumberOfRecords=\"\">  <ItemInstruction  />   </ItemInstructionList>  <ItemOptionList />   <ItemServiceAssocList /> <CategoryList />   </Item>").getDocumentElement();
				oShipmentElement.setAttribute("ShipmentKey",resolveValue("xml:Receipt:/Receipt/Shipment/@ShipmentKey"));%>
			<yfc:callAPI apiName="getShipmentDetails" templateElement="<%=tempElem%>" inputElement="<%=oShipmentElement%>" outputNamespace="Shipment"/>
			<yfc:makeXMLInput name="startReceiptKey" >
				<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentNo" value="xml:Shipment:/Shipment/@ShipmentNo" />
				<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:Shipment:/Shipment/@ShipmentKey" />
				<yfc:makeXMLKey binding="xml:/Shipment/@EnterpriseCode" value="xml:Shipment:/Shipment/@EnterpriseCode" />
				<yfc:makeXMLKey binding="xml:/Shipment/@ExpectedDeliveryDate" value="xml:Shipment:/Shipment/@ExpectedDeliveryDate"/>
				<yfc:makeXMLKey binding="xml:/Shipment/@ReceivingNode" value="xml:Shipment:/Shipment/@ReceivingNode"/>
				<yfc:makeXMLKey binding="xml:/Shipment/@BuyerOrganizationCode" value="xml:Shipment:/Shipment/@BuyerOrganizationCode"/>
				<yfc:makeXMLKey binding="xml:/Shipment/@SellerOrganizationCode" value="xml:Shipment:/Shipment/@SellerOrganizationCode"/>
				<yfc:makeXMLKey binding="xml:/Shipment/@DocumentType" value="xml:Shipment:/Shipment/@DocumentType" />
			</yfc:makeXMLInput>
			<input name="startReceiptKey" type="hidden" value='<%=getParameter("startReceiptKey")%>'/>
			<script>
				window.attachEvent("onload",popupStartReceipt);
			</script>
<%		} else if (!isVoid(resolveValue("xml:Receipt:/Receipt/@OrderHeaderKey"))) { %>
			<yfc:makeXMLInput name="orderKey">
				<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:Receipt:/Receipt/@OrderHeaderKey" ></yfc:makeXMLKey>
			</yfc:makeXMLInput>
			<input name="startReceiptKey" type="hidden" value='<%=getParameter("orderKey")%>'/>
			<script>
				window.attachEvent("onload",popupStartReceipt);
			</script>
<%			}
	}%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="2" >
		<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I03"/>
		</jsp:include>
    </td>
</tr>
<tr>
    <td colspan="2" align="center">
        <input class="button" type="button" name="proceedBtn" value="<%=getI18N("Proceed")%>" onclick="proceedClickedForSelect(this);" />
    </td>
</tr>
</table>
<% }
%>
