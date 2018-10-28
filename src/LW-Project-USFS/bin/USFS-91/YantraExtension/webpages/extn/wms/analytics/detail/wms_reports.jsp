<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.YFCException" %>
<%@ page import="com.yantra.yfc.dom.YFCElement" %>
<%@ page import="com.yantra.yfs.core.YFSSystem" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.ui.backend.YFCFilterManager" %>

<%
if (isShipNodeUser()) { 
	YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
	String sAnalyticsNamespace = getAnalyticsNameSpace();
%>
		<input type="hidden" name="CAMUsername" value="<%=curUsr.getAttribute("Loginid")%>"/>
		<input type="hidden" name="CAMPassword" value="<%=session.getId()%>"/>
		<input type="hidden" name="CAMNamespace" value="<%=sAnalyticsNamespace%>"  />
		<input type="hidden" name="b_action" value="xts.run"/>
		<input type="hidden" name="m" value="portal/report-viewer.xts"/>
		<input type="hidden" name="method" value="execute"/>
		<input type="hidden" name="p_Parameter1" value="<%=curUsr.getAttribute("Node")%>"/>
		<input type="hidden" name="p_nodeKey" value="<%=curUsr.getAttribute("Node")%>"/>
		<input type="hidden" name="p_nodeKeyList" value="<%=curUsr.getAttribute("Node")%>"/>
		<input type="hidden" name="p_User" value="<%=curUsr.getAttribute("Loginid")%>"/>
		<input type="hidden" name="p_View" value="hide"/>
		<input type="hidden" name="prompt" value="true"/>
		<input type="hidden" name="nh" value="1"/>
		<input name="m_obj" id="m_obj" type="hidden" value="/content/package[@name='Yantra-Analytics']/folder[@name='WMS']/report[@name='Dummy Report.xml']"  />
<%
	YFCFilterManager fm = YFCFilterManager.getInstance(pageContext.getServletContext());
	if ( !fm.processRequest(request,response))
		return;
	String wmsAnalyticsURL = getAnalyticsReportNetUrl();
	YFCElement errors = null;
	if (isVoid(wmsAnalyticsURL)){
		YFCException ex = new YFCException(getI18N("analytics.reportnet.url_is_not_configured_in_the_properties_file_"));
		errors = (ex.getXML()).getDocumentElement();
	}	
%>
<script>
function runReport(reportName) {
	//alert(reportName);
	//alert(sAnalyticsNamespace);
	//document.all["m_obj"].value="/content/package[@name='Yantra-Analytics']/folder[@name='WMS']/report[@name='" + reportName + ".xml']";
	document.all["m_obj"].value="/content/folder[@name='Custom Reports']/report[@name='" + reportName + "']";

}
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
			<td class="tablecolumnheader">&nbsp;</td>
            <td class="tablecolumnheader"><yfc:i18n>Report_Name</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Run_Report</yfc:i18n></td>
        </tr>
    </thead>
	<br>
	<tbody>
<%  
	String reportDisplayNameArr[] = new String[]{"ABOVE MAXIMUM REPORT", 
											"ACCOUNT TRANSACTION REPORT",
											"BACK ORDER REPORT",
											"BELOW MINIMUM REPORT",
											"BILLING INFORMATION REPORT",
											"CACHE ITEM TRANSACTION REPORT",
											"CRITICAL ITEMS REPORT",
		                                    "EXTERNAL AFFAIRS REPORT",
		                                    "INCIDENT OTHER ORDER SUMMARY REPORT",
		                                    "ITEM EXPIRATION REPORT",
		                                    "ITEM PURCHASE HISTORICAL REPORT",
		                                    "LOSS USE REPORT",
		                                    "NON-TRACKABLE INVENTORY DISPOSAL REPORT",
		                                    "OPEN WORKORDER REPORT",
                                            "OPEN PO/REQ BY ITEM REPORT",
	                                        "OPEN PO/REQ BY ORDER REPORT",
		                                    "OUTSTANDING SURPLUS REPORT",
		                                    "REDISTRIBUTION REPORT",
		                                    "RESERVED ITEMS REPORT",
		                                    "RETURN REPORT",
		                                    "RETURN WORKSHEET REPORT",
		                                    "STOCK STATUS REPORT",
		                                    "TRACKABLE INVENTORY DISPOSAL REPORT",
		                                    "TRACKABLE INVENTORY REPORT",
		                                    "TRACKABLE INVENTORY STATUS REPORT"
										  };

String reportNameArr[] = new String[]{"ABOVE MAXIMUM REPORT", 
											"ACCOUNT TRANSACTION REPORT",
											"BACK ORDER REPORT",
											"BELOW MINIMUM REPORT",
											"BILLING INFORMATION REPORT",
											"CACHE ITEM TRANSACTION REPORT",
											"CRITICAL ITEMS REPORT",
		                                    "EXTERNAL AFFAIRS REPORT",
		                                    "INCIDENT OTHER ORDER SUMMARY REPORT",
		                                    "ITEM EXPIRATION REPORT",
		                                    "ITEM PURCHASE HISTORICAL REPORT",
		                                    "LOSS USE REPORT",
		                                    "NON-TRACKABLE INVENTORY DISPOSAL REPORT",
		                                    "OPEN WORKORDER REPORT",
                                            "OPEN PO/REQ BY ITEM REPORT",
	                                        "OPEN PO/REQ BY ORDER REPORT",
		                                    "OUTSTANDING SURPLUS REPORT",
		                                    "REDISTRIBUTION REPORT",
		                                    "RESERVED ITEMS REPORT",
		                                    "RETURN REPORT",
		                                    "RETURN WORKSHEET REPORT",
		                                    "STOCK STATUS REPORT",
		                                    "TRACKABLE INVENTORY DISPOSAL REPORT",
		                                    "TRACKABLE INVENTORY REPORT",
		                                    "TRACKABLE INVENTORY STATUS REPORT"
										  };

	
	String reportDisplayName; String reportName;
	for ( int i = 0; i < reportNameArr.length ; i++ ) {
		reportDisplayName = reportDisplayNameArr[i]; 
		reportName = reportNameArr[i];
%>
		<tr>
			<td>&nbsp;</td>
			<td class="tablecolumn">
				<yfc:i18n><%=reportDisplayName%></yfc:i18n>
			</td>
			<td class="tablecolumn">
				<INPUT type="image" SRC='../console/icons/go.gif' NAME=<%=reportName%> ALIGN='LEFT'  onClick='runReport("<%=reportName%>");this.form.target="YantraReports";this.form.action="<%=wmsAnalyticsURL%>";'> 
				</INPUT>
				<br>
			</td>
		</tr>
<%
	}
%>
	</tbody>
</table>
<%	
	} else { 
%>
<script>
	alert('<%=getI18N("Only_Node_User_can_perform_this_Operation")%>');
	window.history.back();
</script>
<%	
	}	
%>