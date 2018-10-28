<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.ycp.wf.util.YCPWorkFlowConsts" %>
<%@ page import="com.yantra.yfc.date.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" >

function OKForPrintClicked(){
		yfcChangeDetailView(getCurrentViewId());		
		//window.close();
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
	String sItemId = resolveValue("xml:/Print/Item/@ItemID");
	String sShipmentNo = resolveValue("xml:/DataLPNLabelList/DataLPNLabelLine/@ShipmentNo");
	String sReceiptHeaderKey = resolveValue("xml:/Print/Receipt/@ReceiptHeaderKey");
	String sIncidentKey = resolveValue("xml:/Print/Incident/@IncidentKey");
	String sIncidentNo = resolveValue("xml:/Print/Incident/@IncidentNo");
	String sYear = resolveValue("xml:/Print/Incident/@Year");

	YFCLocale localeObj = (YFCLocale)(request.getSession().getAttribute("YFC_LOCALE"));
    String slocale = localeObj.getLocaleCode();
	
	//Prepare input for getFlowList API, ProcessTypeKey is being figured out here based on the i/p passed to this JSP.
	YFCElement oFlowElement = YFCDocument.createDocument("Flow").getDocumentElement();
	YFCElement oTemplateElement = YFCDocument.parse("<FlowList> <Flow FlowName=\" \"  IsPrintService=\" \" ProcessTypeKey=\" \" /> </FlowList>").getDocumentElement();
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
		}else if((sIncidentNo!=null) && (!isVoid(sIncidentNo))){//Added by GN for NWCG Incident Label
			rootElement.getChildElement("Incident", true).setAttribute("IncidentKey",sIncidentKey);
		    rootElement.getChildElement("Incident").setAttribute("IncidentNo",sIncidentNo);
			rootElement.getChildElement("Incident").setAttribute("Year",sYear);
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
	<tr>        
        <td align="center" colspan="2">
            <input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="OKForPrintClicked();"/>
        <td>
    <tr>
</table>
