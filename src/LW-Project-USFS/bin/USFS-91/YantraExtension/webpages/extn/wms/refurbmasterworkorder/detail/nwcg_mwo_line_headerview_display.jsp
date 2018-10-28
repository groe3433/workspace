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
	String PrimarySerialNo = resolveValue("xml:/NWCGMasterWorkOrderLine/@PrimarySerialNo");
	String RefurbishedQuantity = resolveValue("xml:/NWCGMasterWorkOrderLine/@RefurbishedQuantity");
	String ItemID = resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemID");
	String UnitOfMeasure = resolveValue("xml:/NWCGMasterWorkOrderLine/@UnitOfMeasure");
	String ProductClass = resolveValue("xml:/NWCGMasterWorkOrderLine/@ProductClass");
	String RefurbCost = resolveValue("xml:/NWCGMasterWorkOrderLine/@RefurbCost");		
	String ItemDesc = resolveValue("xml:/NWCGMasterWorkOrderLine/@ItemDesc");
%>

<table class="table" ID="NWCGBillingTransactionList" cellspacing="0" width="50%" GenerateID="true">
    <thead>
        	<tr>
           		<td style="width:45px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_ItemId</yfc:i18n></td>
            	<td style="width:55px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_UOM</yfc:i18n></td>
           	 	<td style="width:55px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_ProductClass</yfc:i18n></td>            	
            	<td style="width:120px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_SerialNo</yfc:i18n></td>
				<td style="width:85px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_ItemDesc</yfc:i18n></td>      
				<td style="width:85px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_TotalRefurbished</yfc:i18n></td>    
				<td style="width:85px" class="tablecolumnheader"><yfc:i18n>MWO_CompHeaderDisplay_TotalRefurbishCost</yfc:i18n></td>          	
        	</tr>
    </thead>
	<tbody>
			<tr TemplateRow="false" style="display:all" >
				<td nowrap="true" class="tablecolumn" style="width:45px">
					<label><%=ItemID%></label>
				</td>
				<td nowrap="true" class="tablecolumn" style="width:55px">
					<label><%=UnitOfMeasure%></label>
				</td>
				<td nowrap="true" class="tablecolumn" style="width:55px">
					<label><%=ProductClass%></label>
				</td>						
				<td nowrap="true" class="tablecolumn" style="width:120px">
					<label><%=PrimarySerialNo%></label>
				</td>
				<td nowrap="true" class="tablecolumn" style="width:85px">
					<label><%=ItemDesc%></label>
				</td>
				<td nowrap="true" class="tablecolumn" style="width:75px">
					<label><%=RefurbishedQuantity%></label>
				</td>
				<td nowrap="true" class="tablecolumn" style="width:85px">
					<label><%=RefurbCost%></label>
				</td>
			</tr>
	</tbody>
</table>
</div>