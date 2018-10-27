<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.ycm.*" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.yantra.yfc.dom.YFCElement"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XPathWrapper"%>
<%@page import="com.yantra.yfc.dom.YFCNodeList"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XMLUtil"%>
<%@page import="org.w3c.dom.Node"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="org.w3c.dom.NodeList"%>
<%@page import="com.nwcg.icbs.yantra.util.common.XPathUtil"%>
<%@page import="com.yantra.yfc.dom.YFCDocument"%>
<%@page import="com.yantra.yfs.japi.YFSEnvironment"%>
<%@page import="com.yantra.shared.ycp.YFSContext"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_master_work_order.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_returns.js"></script>

<%
	String MasterWorkOrderKey = resolveValue("xml:/NWCGMasterWorkOrderLine/@MasterWorkOrderKey");
	String MasterWorkOrderLineKey = resolveValue("xml:/NWCGMasterWorkOrderLine/@MasterWorkOrderLineKey");
	String TransType = resolveValue("WO-REFURB");
	
	YFCDocument inputDoc = YFCDocument.parse("<NWCGBillingTransaction/>");
	YFCElement elemItem = inputDoc.getDocumentElement();
	elemItem.setAttribute("TransType", TransType);
	elemItem.setAttribute("TransHeaderKey", MasterWorkOrderKey);
	elemItem.setAttribute("TransLineKey", MasterWorkOrderLineKey);
%>

<yfc:callAPI serviceName="NWCGGetBillingTransactionListService" inputElement="<%=inputDoc.getDocumentElement()%>" outputNamespace="NWCGBillingTransaction"></yfc:callAPI>

<table class="table" ID="NWCGBillingTransactionList" cellspacing="0" width="50%" GenerateID="true">
    <thead>
        	<tr>
           		<td style="width:45px" class="tablecolumnheader"><yfc:i18n>MWO_CompDisplay_Item_Id</yfc:i18n></td>
            	<td style="width:55px" class="tablecolumnheader"><yfc:i18n>MWO_CompDisplay_Trans_Qty</yfc:i18n></td>
           	 	<td style="width:55px" class="tablecolumnheader"><yfc:i18n>MWO_CompDisplay_Trans_Amount</yfc:i18n></td>            	
            	<td style="width:500px" class="tablecolumnheader"><yfc:i18n>MWO_CompDisplay_Item_Description</yfc:i18n></td>            	
        	</tr>
    </thead>
	<tbody>
			<%
				YFCElement elem = (YFCElement) request.getAttribute("NWCGBillingTransaction");
				Document doc = elem.getOwnerDocument().getDocument();
				NodeList nlComponent = doc.getElementsByTagName("NWCGBillingTransaction");
				String[][] tempArray = new String[nlComponent.getLength()][5];
				for(int j = 0; j < tempArray.length; j++) {
					tempArray[j][0] = "";
					tempArray[j][1] = "";
					tempArray[j][2] = "";
					tempArray[j][3] = "";
				}
				int k = 0;
				for(int i = 0; i < nlComponent.getLength(); i++) {
					Element compElem = (Element)nlComponent.item(i);
					String ItemId = compElem.getAttribute("ItemId");
					String TransAmount = compElem.getAttribute("TransAmount");
					String TransQty = compElem.getAttribute("TransQty");
					double dlTransQty = Double.parseDouble(TransQty);
					String ItemDescription = compElem.getAttribute("ItemDescription");
					if(dlTransQty < 0) {
						for(int j = 0; j < tempArray.length; j++) {
							if(tempArray[j][0].equals(ItemId)) {
								// TransQty
								tempArray[j][1] = "" + Math.round((Double.parseDouble(TransQty) + Double.parseDouble(tempArray[j][1]))*100)/100.0d;
								// TransAmount
								tempArray[j][2] = "" + Math.round((Double.parseDouble(TransAmount) + Double.parseDouble(tempArray[j][2]))*100)/100.0d;
								break;
							} 
							if(tempArray[j][0].equals("")) {
								tempArray[j][0] = ItemId;
								tempArray[j][1] = TransQty;
								tempArray[j][2] = TransAmount;
								tempArray[j][3] = ItemDescription;
								k++;
								break;
							}
						}
					}
				}
				for(int j = 0; j < k; j++) {
			%>
					<tr TemplateRow="false" style="display:all" >
						<td nowrap="true" class="tablecolumn" style="width:45px">
							<label><%=tempArray[j][0]%></label>
						</td>
						<td nowrap="true" class="tablecolumn" style="width:55px">
							<label><%=tempArray[j][1]%></label>
						</td>
						<td nowrap="true" class="tablecolumn" style="width:55px">
							<label><%=tempArray[j][2]%></label>
						</td>						
						<td nowrap="true" class="tablecolumn" style="width:500px">
							<label><%=tempArray[j][3]%></label>
						</td>
					</tr>
			<% 
				}
			%>
	</tbody>
</table>
</div>