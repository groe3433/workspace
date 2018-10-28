<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.ycp.wf.util.YCPWorkFlowConsts" %>
<%@ page import="com.yantra.yfc.date.*" %>
<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script language="javascript" >

<%
	//System.out.println("JDBG: Here I am in nwcg_extended_common_printconsole_popup.jsp" );
	YFCLocale localeObj = (YFCLocale)(request.getSession().getAttribute("YFC_LOCALE"));
    String slocale = localeObj.getLocaleCode();
	System.out.println("locale "+ slocale);
%>
function OKForPrintClicked(){
		yfcChangeDetailView(getCurrentViewId());		
		//window.close();
}

function checkQty(elem){
var currentRow = elem.parentElement.parentElement;
var Qty = 0;

//alert("currentRow:-"+currentRow.innerHTML);

var InputList = currentRow.getElementsByTagName("input");

	var last = "";
	for(cnt1=0;cnt1<InputList.length;cnt1++){
			if(InputList[cnt1].name.indexOf('@NewStandardPackQty') != -1)
			{
				 Qty = parseInt(InputList[cnt1].value);
				 if(isNaN(Qty))
				 {
					 InputList[cnt1].value = "";
				 }
			}//end if
	}//End For Loop
}
</script>

<%
	String sOrgCode = "";		
	String sEnterpriseCode = "";
	String sBuyer = "";
	String sSeller = "";
	String sScac = "";
	String sFlowName = resolveValue("xml:/Print/@FlowName");
	String sPrinter = resolveValue("xml:/Print/PrinterPreference/@PrinterId");
	String sLoadKey = resolveValue("xml:/Print/Load/@LoadKey");
	String sShipmentKey = resolveValue("xml:/Print/Shipment/@ShipmentKey");
	String sShipmentContainerKey = resolveValue("xml:/Print/Container/@ShipmentContainerKey");
	String sWaveNo = resolveValue("xml:/Print/Wave/@WaveNo");	
	String sManifestKey = resolveValue("xml:/Print/Manifest/@ManifestKey");	
	String sBatchNo = resolveValue("xml:/Print/Batch/@BatchNo");
	String sActivityGroupId = resolveValue("xml:/Print/Batch/@ActivityGroupId");
	String sShipmentType = resolveValue("xml:/Print/Shipment/@ShipmentType");
	String sPickListNo = resolveValue("xml:/Print/Shipment/@PickListNo");
	String sItemKey = resolveValue("xml:/Print/Item/@ItemKey");
	String sItemUnitWt = resolveValue("xml:/Print/Item/@UnitWeight");
	String sItemStdPack = resolveValue("xml:/Print/Item/@StandardPack");
	String sItemIsSerial = resolveValue("xml:/Print/Item/@IsSerialTracked");
	String sItemId = resolveValue("xml:/Print/Item/@ItemID");
	String sShipmentNo = resolveValue("xml:/DataLPNLabelList/DataLPNLabelLine/@ShipmentNo");
	String sReceiptHeaderKey = resolveValue("xml:/Print/Receipt/@ReceiptHeaderKey");
	//System.out.println("ReceiptHeaderKey "+sReceiptHeaderKey );
	String sIncidentKey = resolveValue("xml:/Print/Incident/@IncidentKey");
	String sIncidentNo = resolveValue("xml:/Print/Incident/@IncidentNo");
	String sYear = resolveValue("xml:/Print/Incident/@Year");
	String sTOrderKey = resolveValue("xml:/Print/TOrder/@OrderKey");
	String sITOrderKey = resolveValue("xml:/Print/ITOrder/@OrderKey");
	String sIOrderKey = resolveValue("xml:/Print/IOrder/@OrderHeaderKey");
	System.out.println("sIOrderKey: "+sIOrderKey);
	String sLocationKey = resolveValue("xml:/Print/Location/@LocationKey");
	String strUOM = resolveValue("xml:/Print/Item/@UnitOfMeasure");

	
	//Prepare input for getFlowList API, ProcessTypeKey is being figured out here based on the i/p passed to this JSP.
	YFCElement oFlowElement = YFCDocument.createDocument("Flow").getDocumentElement();
	YFCElement oTemplateElement = YFCDocument.parse("<FlowList> <Flow FlowName=\" \"  IsPrintService=\" \" ProcessTypeKey=\" \" FlowGroupName=\"\" /> </FlowList>").getDocumentElement();
	oFlowElement.setAttribute("IsPrintService","Y");
	if((sLoadKey!=null) && (!isVoid(sLoadKey))){			
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_LOAD);
	}else if((sShipmentKey!=null) && (!isVoid(sShipmentKey))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_SHIPMENT);
		oFlowElement.setAttribute("SystemDefined","N");
	}else if((sShipmentContainerKey!=null) && (!isVoid(sShipmentContainerKey))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_PACK);
	}else if((sWaveNo!=null) && (!isVoid(sWaveNo))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_OUTBOUND_PICKING);
	}else if((sManifestKey!=null) && (!isVoid(sManifestKey))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_MANIFESTING);
	}else if((sBatchNo!=null) && (!isVoid(sBatchNo))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_OUTBOUND_PICKING);
	}else if((sItemKey!=null) && (!isVoid(sItemKey))){
		oFlowElement.setAttribute("ProcessTypeKey",YCPWorkFlowConsts.BASE_PROCESS_TYPE_GENERAL);
	}else if((sItemId!=null) && (!isVoid(sItemId))){
		oFlowElement.setAttribute("ProcessTypeKey","WMS_INVENTORY");		
	}else if((sReceiptHeaderKey!=null) && (!isVoid(sReceiptHeaderKey))){
		oFlowElement.setAttribute("ProcessTypeKey","PO_RECEIPT");
	}else if((sIncidentNo!=null) && (!isVoid(sIncidentNo))){//Added by GN for NWCG Incident Label
		oFlowElement.setAttribute("ProcessTypeKey","ORDER_FULFILLMENT");
	}else if((sTOrderKey!=null) && (!isVoid(sTOrderKey))){//Added by GN for NWCG Incident Transfer Report
		oFlowElement.setAttribute("FlowGroupName","NWCG_IncidentTransfer_Reports");
	}else if((sITOrderKey!=null) && (!isVoid(sITOrderKey))){//Added by JSK for NWCGIssueTransferReport-I
		oFlowElement.setAttribute("FlowGroupName","NWCG_IssueTransferReport-I");
	}else if((sIOrderKey!=null) && (!isVoid(sIOrderKey))){//Added by JSK for NWCG Issue Report-I
		oFlowElement.setAttribute("FlowGroupName","NWCG_IssueReport-I");
	}else if((sLocationKey!=null) && (!isVoid(sLocationKey))){//Added by KS for NWCG Location
		oFlowElement.setAttribute("FlowGroupName","NWCG-Location");
    } 
	//Call getFlowList API to populate Print_Service_Name field.
%>

	<yfc:callAPI apiName="getFlowList" inputElement='<%=oFlowElement%>' templateElement='<%=oTemplateElement%>' outputNamespace="FlowList" />

<%	//Prepare i/p for the flow based on the input passed to this JSP only after user presses OK button.
	if((sFlowName!=null) && (!isVoid(sFlowName))){		
		YFCElement rootElement = YFCDocument.createDocument("Print").getDocumentElement();
		if((sLoadKey!=null) && (!isVoid(sLoadKey))){			
				String sOriginNode = resolveValue("xml:/Print/Load/@OriginNode");
				String sLoadType = resolveValue("xml:/Print/Load/@LoadType");
				String sMultipleLoadStop = resolveValue("xml:/Print/Load/@MultipleLoadStop");
				String sHazMat = resolveValue("xml:/Print/Load/@HazardousMaterial");
				sScac = resolveValue("xml:/Print/Load/@Scac");
				sEnterpriseCode = resolveValue("xml:/Print/Load/@EnterpriseCode");
				sBuyer = resolveValue("xml:/Print/Load/@BuyerOrganizationCode");
			if(isShipNodeUser()) {
				sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
			}else{
				sOrgCode = sOriginNode;
			}
			rootElement.getChildElement("Load", true).setAttribute("LoadKey",sLoadKey);
			rootElement.getChildElement("Load").setAttribute("LoadType",sLoadType);
			rootElement.getChildElement("Load").setAttribute("MultipleLoadStop",sMultipleLoadStop);
			rootElement.getChildElement("Load").setAttribute("HazardousMaterial",sHazMat);
		}else if((sShipmentKey!=null) && (!isVoid(sShipmentKey))){
				String sShipNode = resolveValue("xml:/Print/Shipment/@ShipNode");
				String sHazMatFlag = resolveValue("xml:/Print/Shipment/@HazardousMaterialFlag");
				sScac = resolveValue("xml:/Print/Shipment/@SCAC");				
				sEnterpriseCode = resolveValue("xml:/Print/Shipment/@EnterpriseCode");
				sBuyer = resolveValue("xml:/Print/Shipment/@BuyerOrganizationCode");
				sSeller = resolveValue("xml:/Print/Shipment/@SellerOrganizationCode");				
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sShipNode;
				}
				rootElement.getChildElement("Shipment", true).setAttribute("ShipmentKey",sShipmentKey);
				rootElement.getChildElement("Shipment").setAttribute("PickListNo",sPickListNo);
				rootElement.getChildElement("Shipment").setAttribute("HazardousMaterialFlag",sHazMatFlag);
				
		}else if((sShipmentContainerKey!=null) && (!isVoid(sShipmentContainerKey))){
				String sShipNode = resolveValue("xml:/Print/Container/@ShipNode");
				sScac = resolveValue("xml:/Print/Container/@SCAC");				
				sEnterpriseCode = resolveValue("xml:/Print/Container/@EnterpriseCode");
				sBuyer = resolveValue("xml:/Print/Container/@BuyerOrganizationCode");
				sSeller = resolveValue("xml:/Print/Container/@SellerOrganizationCode");
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sShipNode;
				}
				rootElement.getChildElement("Container", true).setAttribute("ShipmentContainerKey",sShipmentContainerKey);
				rootElement.getChildElement("Container").setAttribute("SCAC",sScac);
				rootElement.getChildElement("Container").setAttribute("Scac",sScac);
		}else if((sWaveNo!=null) && (!isVoid(sWaveNo))){
				String sShipNode = resolveValue("xml:/Print/Wave/@Node");				
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sShipNode;
				}
				rootElement.getChildElement("Wave", true).setAttribute("WaveNo",sWaveNo);
				rootElement.getChildElement("Wave").setAttribute("Node",sOrgCode);
		}else if((sManifestKey!=null) && (!isVoid(sManifestKey))){
				String sShipNode = resolveValue("xml:/Print/Manifest/@ShipNode");
				sScac = resolveValue("xml:/Print/Manifest/@SCAC");		
				String sIsHazmat = resolveValue("xml:/Print/Manifest/@IsHazmat");
				String sIsParcel = resolveValue("xml:/Print/Manifest/@IsParcel");
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sShipNode;
				}
				rootElement.getChildElement("Manifest", true).setAttribute("ManifestKey",sManifestKey);
				rootElement.getChildElement("Manifest").setAttribute("SCAC",sScac);
				rootElement.getChildElement("Manifest").setAttribute("Scac",sScac);
				rootElement.getChildElement("Manifest").setAttribute("IsHazmat",sIsHazmat);
				rootElement.getChildElement("Manifest").setAttribute("IsParcel",sIsParcel);
		}else if((sBatchNo!=null) && (!isVoid(sBatchNo))){
				String sShipNode = resolveValue("xml:/Print/Batch/@Node");				
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sShipNode;
				}
				rootElement.getChildElement("Batch", true).setAttribute("BatchNo",sBatchNo);
				rootElement.getChildElement("Batch").setAttribute("Node",sOrgCode);
				rootElement.getChildElement("Batch").setAttribute("ActivityGroupId",sActivityGroupId);
		}else if((sItemKey!=null) && (!isVoid(sItemKey))){
			sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
			rootElement.getChildElement("Item", true).setAttribute("ItemKey",sItemKey);  
			rootElement.getChildElement("Item").setAttribute("ItemWeight",sItemUnitWt);
			rootElement.getChildElement("Item").setAttribute("OldStandardPack",sItemStdPack);
			rootElement.getChildElement("Item").setAttribute("NewStandardPackQty",resolveValue("xml:/Print/Item/@NewStandardPackQty"));
			rootElement.getChildElement("Item").setAttribute("NewStandardPack",resolveValue("xml:/Print/Item/@NewStandardPack"));
			rootElement.getChildElement("Item").setAttribute("NewExpDate",resolveValue("xml:/Print/Item/@NewExpDate"));
			rootElement.getChildElement("Item").setAttribute("DateLastTested",resolveValue("xml:/Print/Item/@DateLastTested"));
			rootElement.getChildElement("Item").setAttribute("NewTrackableID",resolveValue("xml:/Print/Item/@NewTrackableID"));	rootElement.getChildElement("Item").setAttribute("NewNote",resolveValue("xml:/Print/Item/@NewNote"));
			 //location label printing change -kspellazza
		}else if((sLocationKey!=null) && (!isVoid(sLocationKey))){
			sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
			rootElement.getChildElement("Location", true).setAttribute("LocationKey",sLocationKey);  
			rootElement.getChildElement("Location").setAttribute("LocationId",resolveValue("xml:/Print/Location/@LocationId"));
			rootElement.getChildElement("Location").setAttribute("LabelSize",resolveValue("xml:/Print/Location/@LabelSize"));
		}else if((sItemId!=null) && (!isVoid(sItemId))){

			String sOrganizationCode = resolveValue("xml:/Print/Item/@OrganizationCode");				
			String sNode = resolveValue("xml:/Print/Item/@Node");
			double sNoOfLabels = Double.parseDouble(resolveValue("xml:/Print/Item/@NoOfLabels"));
			String sUnitOfMeasure = resolveValue("xml:/Print/Item/@UnitOfMeasure");
			double sQuantity = Double.parseDouble(resolveValue("xml:/Print/Item/@Quantity"));
							
			sEnterpriseCode = sOrganizationCode;
			if(isShipNodeUser()) {
				sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
			}else{
				sOrgCode = sNode;
			}

			rootElement.getChildElement("Item", true).setAttribute("OrganizationCode",sEnterpriseCode);
			rootElement.getChildElement("Item").setAttribute("ItemID",sItemId);
			rootElement.getChildElement("Item").setIntAttribute("Quantity",(int)sQuantity);
			rootElement.getChildElement("Item").setAttribute("Node",sOrgCode);
			rootElement.getChildElement("Item").setIntAttribute("NoOfLabels",(int)sNoOfLabels);			
		}else if((sShipmentNo!=null) && (!isVoid(sShipmentNo))){
			String sNode = "";
			String sOrganizationCode = "";
		
			YFCElement DataLPNLabelList = (YFCElement)request.getAttribute("DataLPNLabelList");		
			if(!isVoid(DataLPNLabelList)){
				YFCElement rootElementList = rootElement.createChild("DataLPNLabelList");
			 for(Iterator itr = DataLPNLabelList.getChildren(); itr.hasNext();) {
		            YFCElement dataLPNLabelLine = (YFCElement) itr.next();
						
				String pItemID = dataLPNLabelLine.getAttribute("ItemID");
				String sItemDesc = dataLPNLabelLine.getAttribute("ItemDesc");				
				String sQtyPerLabel = dataLPNLabelLine.getAttribute("QtyPerLabel");
				String sQuantity = dataLPNLabelLine.getAttribute("Quantity");				
				String sShipNode = dataLPNLabelLine.getAttribute("ShipNode");
				 sNode = dataLPNLabelLine.getAttribute("Node");
				 sOrganizationCode = dataLPNLabelLine.getAttribute("EnterpriseCode");
				String sUnitOfMeasure = dataLPNLabelLine.getAttribute("UnitOfMeasure");
				String pShipmentKey = dataLPNLabelLine.getAttribute("ShipmentKey");
				String pShipmentNo = dataLPNLabelLine.getAttribute("ShipmentNo");
				String sShipmentLineKey = dataLPNLabelLine.getAttribute("ShipmentLineKey");
				String sOrderHeaderKey = dataLPNLabelLine.getAttribute("OrderHeaderKey");	
				String sOrderLineKey = dataLPNLabelLine.getAttribute("OrderLineKey");				
				String sOrderNo = dataLPNLabelLine.getAttribute("OrderNo");
				String sDocumentType = dataLPNLabelLine.getAttribute("DocumentType");
				String sPrintLabelsAt = dataLPNLabelLine.getAttribute("PrintLabelsAt");
				String sStandardQty = dataLPNLabelLine.getAttribute("StandardQty");

				YFCElement rootElementLine = rootElementList.createChild("DataLPNLabelLine");
				rootElementLine.setAttribute("QtyPerLabel",sQtyPerLabel);
				rootElementLine.setAttribute("Quantity",sQuantity);
				rootElementLine.setAttribute("Node",sNode);
				rootElementLine.setAttribute("ShipNode",sShipNode);
				rootElementLine.setAttribute("EnterpriseCode",sOrganizationCode);
				rootElementLine.setAttribute("DocumentType",sDocumentType);
				rootElementLine.setAttribute("ItemID",pItemID);
				rootElementLine.setAttribute("ItemDesc",sItemDesc);
				rootElementLine.setAttribute("UnitOfMeasure",sUnitOfMeasure);
				rootElementLine.setAttribute("ShipmentKey",pShipmentKey);
				rootElementLine.setAttribute("ShipmentNo",pShipmentNo);
				rootElementLine.setAttribute("ShipmentLineKey",sShipmentLineKey);
				rootElementLine.setAttribute("OrderHeaderKey",sOrderHeaderKey);
				rootElementLine.setAttribute("OrderNo",sOrderNo);
				rootElementLine.setAttribute("OrderLineKey",sOrderLineKey);
				rootElementLine.setAttribute("PrintLabelsAt",sPrintLabelsAt);
				rootElementLine.setAttribute("StandardQty",sStandardQty);

			}
		}
			sEnterpriseCode = sOrganizationCode;
				if(isShipNodeUser()) {
					sOrgCode = resolveValue("xml:CurrentUser:/User/@Node");
				}else{
					sOrgCode = sNode;
				}
		}else if((sReceiptHeaderKey!=null) && (!isVoid(sReceiptHeaderKey))){//Added by GN for Print WorkSheet
			String sRArrivalTime = resolveValue("xml:/Print/Receipt/@ArrivalDateTime");
			String sRDriverName = resolveValue("xml:/Print/Receipt/@DriverName");
			String sRNumOfCartons = resolveValue("xml:/Print/Receipt/@NumOfCartons");
			String sRNumOfPallets = resolveValue("xml:/Print/Receipt/@NumOfPallets");
			String sROpenReceiptFlag = resolveValue("xml:/Print/Receipt/@OpenReceiptFlag");
			String sRShipmentKey = resolveValue("xml:/Print/Receipt/@ShipmentKey");
			//System.out.println("sRShipmentKey "+sRShipmentKey );
			String sRShipmentNo = resolveValue("xml:/Print/Receipt/@ShipmentNo");
			//System.out.println("sRShipmentNo "+sRShipmentNo );
			String sRReceiptDate = resolveValue("xml:/Print/Receipt/@ReceiptDate");
			//System.out.println("sRReceiptDate "+ sRReceiptDate);
		    String TDateStr1 = sRReceiptDate.substring(0,2);
		    String TDateStr2 = sRReceiptDate.substring(3,5);
            String TDateStr3 = sRReceiptDate.substring(6,10);
            sRReceiptDate = TDateStr3+TDateStr1+TDateStr2;
			String sRReceiptNo = resolveValue("xml:/Print/Receipt/@ReceiptNo");
			String sRReceivingNode = resolveValue("xml:/Print/Receipt/@ReceivingNode");
			String sRReceivingDock = resolveValue("xml:/Print/Receipt/@ReceivingDock");
			String sRStatus = resolveValue("xml:/Print/Receipt/@Status");
			String sRTrailerLPNNo = resolveValue("xml:/Print/Receipt/@TrailerLPNNo");
			rootElement.getChildElement("Receipt", true).setAttribute("ReceiptHeaderKey",sReceiptHeaderKey);
		    rootElement.getChildElement("Receipt").setAttribute("ArrivalDateTime",sRArrivalTime);
			rootElement.getChildElement("Receipt").setAttribute("DriverName",sRDriverName);
			rootElement.getChildElement("Receipt").setAttribute("NumOfCartons",sRNumOfCartons);
			rootElement.getChildElement("Receipt").setAttribute("NumOfPallets",sRNumOfPallets);
			rootElement.getChildElement("Receipt").setAttribute("OpenReceiptFlag",sROpenReceiptFlag);
			rootElement.getChildElement("Receipt").setAttribute("ShipmentKey",sRShipmentKey);
			rootElement.getChildElement("Receipt").setAttribute("ShipmentNo",sRShipmentNo);
			rootElement.getChildElement("Receipt").setAttribute("ReceiptDate",sRReceiptDate);
			rootElement.getChildElement("Receipt").setAttribute("ReceiptNo",sRReceiptNo);
			rootElement.getChildElement("Receipt").setAttribute("ReceivingNode",sRReceivingNode);
			rootElement.getChildElement("Receipt").setAttribute("ReceivingDock",sRReceivingDock);
			rootElement.getChildElement("Receipt").setAttribute("Status",sRStatus);
			rootElement.getChildElement("Receipt").setAttribute("TrailerLPNNo",sRTrailerLPNNo);
		}else if((sIncidentNo!=null) && (!isVoid(sIncidentNo))){//Added by GN for NWCG Incident Label
			rootElement.getChildElement("Incident", true).setAttribute("IncidentKey",sIncidentKey);
		    rootElement.getChildElement("Incident").setAttribute("IncidentNo",sIncidentNo);
			rootElement.getChildElement("Incident").setAttribute("Year",sYear);
			rootElement.getChildElement("Incident").setAttribute("GenerateReturnNo",resolveValue("xml:/Print/Incident/@GenerateReturnNo"));
			YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
			sOrgCode =  curUsr.getAttribute("Node");
			//System.out.println("PRINT POPUP CACHE " +sOrgCode);
		}else if((sTOrderKey!=null) && (!isVoid(sTOrderKey))){//Added by GN for NWCG Transfer Report
			rootElement.getChildElement("TOrder", true).setAttribute("TransferOrderKey",sTOrderKey);
		}else if((sITOrderKey!=null) && (!isVoid(sITOrderKey))){//Added by JSK for NWCGIssueTransferReport-I
			rootElement.getChildElement("ITOrder", true).setAttribute("TransferOrderKey",sITOrderKey);
		}else if((sIOrderKey!=null) && (!isVoid(sIOrderKey))){//Added by JSK for NWCG Issue Report-I
			rootElement.getChildElement("IOrder", true).setAttribute("IssueOrderKey",sIOrderKey);
		}

		rootElement.getChildElement("PrinterPreference", true).setAttribute("OrganizationCode",sOrgCode);
		rootElement.getChildElement("PrinterPreference").setAttribute("PrinterId",resolveValue("xml:/Print/PrinterPreference/@PrinterId"));		
		rootElement.getChildElement("PrinterPreference").setAttribute("UserId",resolveValue("xml:CurrentUser:/User/@Loginid"));	
		rootElement.getChildElement("PrinterPreference").setAttribute("UserLocale",slocale);	

		rootElement.getChildElement("LabelPreference", true).setAttribute("Node",sOrgCode);
		rootElement.getChildElement("LabelPreference").setAttribute("SCAC",sScac);
		rootElement.getChildElement("LabelPreference").setAttribute("Scac",sScac);
		rootElement.getChildElement("LabelPreference").setAttribute("EnterpriseCode",sEnterpriseCode);
		rootElement.getChildElement("LabelPreference").setAttribute("BuyerOrganizationCode",sBuyer);
		rootElement.getChildElement("LabelPreference").setAttribute("SellerOrganizationCode",sSeller);
		rootElement.getChildElement("LabelPreference").setAttribute("NoOfCopies",resolveValue("xml:/Print/LabelPreference/@NoOfCopies"));
		rootElement.getChildElement("LabelPreference").setAttribute("IsTrackable",resolveValue("xml:/Print/LabelPreference/@IsTrackable"));


%>
	<yfc:callAPI serviceName="<%=sFlowName%>" inputElement='<%=rootElement%>' outputNamespace="PrintOutput" />
<%
        YFCElement printOutputElem = (YFCElement) request.getAttribute("PrintOutput");
        if(printOutputElem != null) {%>
            <script>window.close();</script>
        <%}
	}
%>
<yfc:callAPI apiID="AP2"/>
<table width="100%" class="view">

    <% if((sIncidentNo!=null) && (!isVoid(sIncidentNo))){
	%>
	<tr>
	<td class="detaillabel">
	<yfc:i18n>Generate_Return_No</yfc:i18n>
	</td>
    <td nowrap="true" class="searchcriteriacell" >
       <select class=combobox name="xml:/Print/Incident/@GenerateReturnNo"> 
	     <option value="" Selected></option> 
	     <option value="Y">Yes</option> 
		 <option value="N">No</option>
	   </select> 
    </td>
    </tr>
	<% } %>


    <% if((sItemKey!=null) && (!isVoid(sItemKey))){
    %>
	<yfc:callAPI apiID="AP3"/>
	<tr>
	<td class="detaillabel">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>
	<td><input type="text" class="protectedinput" readonly="true" name="xml:/Print/Item/@ItemID" value=<%=sItemId%>>
	</td>
	</tr>

	<tr>
	<td class="detaillabel">
	<yfc:i18n>Catalog_Standard_Pack</yfc:i18n>
	</td>
	<td><input type="text" class="protectedinput" readonly="true" name="xml:/Print/Item/@StandardPack" value=<%=sItemStdPack%>>
	</td>
	</tr>

    <tr>
	<td class="detaillabel">
	<yfc:i18n>Unit Weight</yfc:i18n>
	</td>
	<td><input type="text" class="protectedinput" readonly="true" name="xml:/Print/Item/@UnitWeight" value=<%=sItemUnitWt%>>
	</td>
	</tr>

    <tr>
	<td class="detaillabel">
	<yfc:i18n>Trackable</yfc:i18n>
	</td>
	<td><input type="text" class="protectedinput" readonly="true" name="xml:/Print/Item/@IsSerialTracked" value=<%=sItemIsSerial%>>
	</td>
	</tr>

    <tr>
    <td class="detaillabel">
	<yfc:i18n>Container_Standard_Pack</yfc:i18n>
	</td>
	<td><input type="text" class="unprotectedinput" maxLength=5 Size=5 name="xml:/Print/Item/@NewStandardPackQty" onblur="checkQty(this);">
    <select name="xml:/Print/Item/@NewStandardPack" class="combobox" Sort="Asc">
       <yfc:loopOptions binding="xml:StandardPack:/CommonCodeList/@CommonCode" 
       name="CodeValue" value="CodeValue"/>
       </select>
    </td>
	</tr>

	<tr>
    <td class="detaillabel">
	<yfc:i18n>Expiration_Date</yfc:i18n>
	</td>
	<td><input type="text" class="unprotectedinput" maxLength=10 Size=10 name="xml:/Print/Item/@NewExpDate" ><img class="lookupicon" name="search" onclick="invokeCalendar(this);" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar" ) %> />
	</td>
	</tr>

	<tr>
    <td class="detaillabel">
	<yfc:i18n>Date_Last_Tested</yfc:i18n>
	</td>
	<td><input type="text" class="unprotectedinput" maxLength=10 Size=10 name="xml:/Print/Item/@DateLastTested" ><img class="lookupicon" name="search" onclick="invokeCalendar(this);" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar" ) %> />
	</td>
	</tr>

	<%if(sItemIsSerial.equals("Y")) {%>

	<tr>
    <td class="detaillabel">
	<yfc:i18n>Trackable_ID</yfc:i18n>
	</td>
	<td><input type="text" class="unprotectedinput" maxLength=20 Size=20 name="xml:/Print/Item/@NewTrackableID" >
	</td>
	</tr>

	<%}%>

    <tr>
    <td class="detaillabel">
	<yfc:i18n>Note</yfc:i18n>
	</td>
	<td><input type="text" class="unprotectedinput" maxLength=30 Size=30 name="xml:/Print/Item/@NewNote" >
	</td>
	</tr>
 	<% } %>

    <% if((sLocationKey!=null) && (!isVoid(sLocationKey))){
    %>
	<yfc:callAPI apiID="AP3"/>
	<tr>
	<td class="detaillabel">
	<yfc:i18n>Label_Size</yfc:i18n>
	</td>
	<td>
    <select name="xml:/Print/Location/@LabelSize" class="combobox" Sort="Asc">
	   	<option name="LabelSize" value ="SMALL">Small</option>
	  <option name="LabelSize"value ="LARGE">Large</option>
       </select>
	</td>
	</tr>
 	<% } %>

	<tr>
		<td class="detaillabel">
			<yfc:i18n>Print_Service_Name</yfc:i18n>
		</td>
		<td>
			<select name="xml:/Print/@FlowName" class="combobox">
				<yfc:loopOptions binding="xml:FlowList:/FlowList/@Flow" 
					name="FlowName" value="FlowName"/>
			</select>
		</td>
	</tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Printer_Name</yfc:i18n>
        </td>
        <td>
            <select name="xml:/Print/PrinterPreference/@PrinterId" class="combobox">
                <yfc:loopOptions binding="xml:Device:/Devices/@Device" 
                    name="DeviceId" value="DeviceId"/>
            </select>
        </td>
    </tr>    
	<tr>
        <td class="detaillabel">
            <yfc:i18n>No_of_Copies</yfc:i18n>
        </td>
        <td>
			<input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Print/LabelPreference/@NoOfCopies","")%> />
        </td>
    </tr>    

    <% if((strUOM!=null) && (strUOM.equals("KT"))){ %>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Print_Trackable_ID</yfc:i18n>
        </td>
		<td>
		   <select class=combobox name="xml:/Print/LabelPreference/@IsTrackable"> 
			 <option value="" Selected></option> 
			 <option value="Y">Yes</option> 
			 <option value="N">No</option>
		   </select> 
		</td>
	</tr>		
	<%}%>

	<tr>        
        <td align="center" colspan="2">
            <input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="OKForPrintClicked();"/>
        <td>
    </tr>

</table>
