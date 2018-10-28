<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>
<%@ include file="/console/jsp/order.jspf"%>
<%@ include file="/console/jsp/currencyutils.jspf"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.XMLUtil"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.w3c.dom.NodeList"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.XPathUtil"%>
<%@ page import="com.yantra.yfc.dom.YFCDocument"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ page import="com.yantra.yfc.util.*"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.*"%>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf"%>
<%@ page import="com.yantra.shared.ycp.YFSContext"%>
<%@ page import="com.yantra.yfs.japi.YFSEnvironment"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_master_work_order.js"></script>

<script language="javascript">
	function setValue(checkboxval,moLineKey) {
		var lineKey = moLineKey;
		var objArr = new Object();
		if(checkboxval.checked == true) {
			objArr['xml:MasterWorkOrderLineKey'] = lineKey;
			objArr['xml:DisplayMasterWOComponents']= 'Y';	
			fetchDataWithParams(checkboxval,'UpdateNWCGMasterWorkOrderLine',populateMasterWOXML,objArr);
		} else if(checkboxval.checked == false) {
			objArr['xml:MasterWorkOrderLineKey'] = lineKey;
			objArr['xml:DisplayMasterWOComponents']= 'N';			
			fetchDataWithParams(checkboxval,'UpdateNWCGMasterWorkOrderLine',populateMasterWOXML,objArr);
		}
	}

	function populateMasterWOXML(elem,xmlDoc) {
		//do nothing...
	}
	
	function validateSingleSelection(key) {
		var eKey = key;
		if(yfcAllowSingleSelection(eKey)) {
			if(checkLineStatus(eKey, 'P')) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	function validateLineStatus(key) {
		var eKey = key;
		if(checkLineStatus(eKey,'T')) {
			return true;
		} else {
			return false;
		}
	}

	function checkLineStatus(chkName,flag) {
		var eleArray = document.forms["containerform"].elements;
		var count=0;
		var statusCheck = 1;
		var mwoSelectedLines = "";
		for ( var i =0; i < eleArray.length; i++ ) {
			if ( eleArray[i].name == chkName ) {
				if (eleArray[i].checked ) {
					var elementValue = eleArray[i].value;
					var startIndex = elementValue.indexOf("%22") + 3;
					var endIndex = elementValue.lastIndexOf("%22");
					var mwolKey = elementValue.substring(startIndex, endIndex);
					var mwolStatus = document.getElementById('status' + mwolKey);
					mwoSelectedLines = mwoSelectedLines + "%3CSelectedLine+SelectedKey%3D%22" + mwolKey + "%22%2F%3E";
					if((mwolStatus.value != 'Awaiting Work Order Creation') && (mwolStatus.value != 'Work Order Partially Completed')) {
						statusCheck = 0;
					}					
					count++;					
				}
			}
		}
		if (count < 1) {
			alert(YFCMSG002);
			document.body.style.cursor='auto';
		} else if(statusCheck != 1) {
			alert('Only lines with \'Awaiting Work Order Creation\' or \'Work Order Partially Completed\' status can be selected.');
		} else {
			if(flag == 'T') {
				var params = "mwoSelectedLines="+mwoSelectedLines;
				yfcShowDetailPopupWithParams("NMWNWCYVSD050", "", "1010", "650", params, "", "", "");		
			}
			else return true;
		}
	}
</script>

<table class="table" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000">
	<thead>
		<tr>
			<td class="checkboxheader" sortable="no">
				<input type="hidden" id="userOperation" name="userOperation" value="" /> 
				<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" /> 
				<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);" />
			</td>
			<td class="tablecolumnheader" style="width:10px"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:20px"><yfc:i18n>Trackable ID</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:10px"><yfc:i18n>Actual Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:10px"><yfc:i18n>RFI Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:10px"><yfc:i18n>UNS Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:10px"><yfc:i18n>UNS NWT Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@OrderedQty")%>"><yfc:i18n>Total Refurbished  Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:10px"><yfc:i18n>Transfer Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@OrderedQty")%>"><yfc:i18n>Remaining Qty</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>"><yfc:i18n>Refurb Amount</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:150px"><yfc:i18n>Status</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:15px"><yfc:i18n>Display Kit Components</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
<%
		YFCElement elemList = (YFCElement) request.getAttribute("NWCGMasterWorkOrderLineList");
		YFCNodeList nl = null;
		if (elemList != null)
			nl = elemList.getElementsByTagName("NWCGMasterWorkOrderLine");
			if (nl != null && nl.getLength() > 0) {// if node list not null
				int iTotal = nl.getLength();
				for (int index = 0; index < iTotal; index++) {
					YFCElement elemLine = (YFCElement) nl.item(index);
					String statusName = "status" + elemLine.getAttribute("MasterWorkOrderLineKey");
%>
					<tr>
						<input type='hidden' name='xml:/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey' value='<%=elemLine.getAttribute("MasterWorkOrderLineKey")%>'>
						<td class="checkbox" sortable="no">
							<input type=checkbox value='%3CNWCGMasterWorkOrderLine+MasterWorkOrderLineKey%3D%22<%=elemLine.getAttribute("MasterWorkOrderLineKey")%>%22%2F%3E' name="MWOLEntityKey">
						</td>
						<td><%=elemLine.getAttribute("ItemID")%></td>
						<td><%=elemLine.getAttribute("ProductClass")%></td>
						<td><%=elemLine.getAttribute("UnitOfMeasure")%></td>
						<td><%=elemLine.getAttribute("ItemDesc")%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("PrimarySerialNo"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("ActualQuantity"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("RFIRefurbQuantity"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("UNSRefurbQuantity"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("UNSNWTRefurbQuantity"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("RefurbishedQuantity"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("TransferQty"))%></td>
<%
						String strRefurbishedQty = StringUtil.nonNull(elemLine.getAttribute("RefurbishedQuantity"));
						String strTransferQty = StringUtil.nonNull(elemLine.getAttribute("TransferQty"));
						if (strRefurbishedQty.equals(""))
							strRefurbishedQty = "0.0";
						if (strTransferQty.equals(""))
							strTransferQty = "0.0";
						double dActualQty = Double.parseDouble(StringUtil.nonNull(elemLine.getAttribute("ActualQuantity")));
						double dRefurbishedQty = Double.parseDouble(strRefurbishedQty);
						double dTransferQty = Double.parseDouble(strTransferQty);
						String strRemainingQty = Double.toString(dActualQty - dRefurbishedQty - dTransferQty);
%>
						<td><%=strRemainingQty%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("RefurbCost"))%></td>
						<td><%=StringUtil.nonNull(elemLine.getAttribute("Status"))%></td>
						<input type="hidden" name='<%=statusName%>' value='<%=StringUtil.nonNull(elemLine.getAttribute("Status"))%>' />
<%
						YFCDocument inputDoc = YFCDocument.parse("<Item/>");
						YFCElement elemItem = inputDoc.getDocumentElement();
						elemItem.setAttribute("ItemID", elemLine.getAttribute("ItemID"));
						elemItem.setAttribute("UnitOfMeasure", elemLine.getAttribute("UnitOfMeasure"));
						elemItem.setAttribute("ProductClass", elemLine.getAttribute("ProductClass"));
						elemItem.setAttribute("OrganizationCode", "NWCG");
			
						YFCDocument inputTemlDoc = YFCDocument.parse("<Item/>");
						YFCElement elemTemlItem = inputTemlDoc.getDocumentElement();
						YFCElement elemPrimInfo = elemTemlItem.createChild("PrimaryInformation");
						elemPrimInfo.setAttribute("KitCode", "");
%>
						<yfc:callAPI apiName="getItemDetails" inputElement="<%=inputDoc.getDocumentElement()%>" templateElement="<%=inputTemlDoc.getDocumentElement()%>" outputNamespace="GetItemDetails"></yfc:callAPI>
<%
						YFCElement elem = (YFCElement) request.getAttribute("GetItemDetails");
						Document doc = elem.getOwnerDocument().getDocument();
						Element PrimInfoElem = (Element) doc.getDocumentElement().getElementsByTagName("PrimaryInformation").item(0);
						String kitcode = PrimInfoElem.getAttribute("KitCode");
						if ("PK".equalsIgnoreCase(kitcode)) {
%>
							<td class="searchcriteriacell"><input type="checkbox" name="checkboxvalue" id="check1" name="xml:/NWCGMasterWorkOrderLine/@DisplayMasterWOComponents" onclick="setValue(this,'<%=elemLine.getAttribute("MasterWorkOrderLineKey")%>')" /></td>
<%
						} else {
%>
							<td class="searchcriteriacell"><input type="checkbox" disabled />
<%
						}
%>
			
					</tr>
<%
				}
			}
%>
	</tbody>
	<tfoot>
<%
		if (isModificationAllowed("xml:/@AddLine", "xml:/Order/AllowedModifications")) {
%>
			<tr>
				<td nowrap="true" colspan="13"><jsp:include page="/common/editabletbl.jsp" flush="true">
					<jsp:param name="ReloadOnAddLine" value="Y" />
					</jsp:include>
				</td>
			</tr>
<%
		}
%>
	</tfoot>
</table>