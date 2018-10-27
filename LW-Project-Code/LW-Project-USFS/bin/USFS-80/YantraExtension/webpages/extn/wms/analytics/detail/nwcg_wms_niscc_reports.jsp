<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.YFCException"%>
<%@ page import="com.yantra.yfc.dom.YFCElement"%>
<%@ page import="com.yantra.yfs.core.YFSSystem"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ page import="com.yantra.yfc.ui.backend.YFCFilterManager"%>

<script>
function runReport(reportName) {
	document.all["m_obj"].value="/content/folder[@name='Custom Reports']/report[@name='" + reportName + "']";
}
</script>

<%
	YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
	String sAnalyticsNamespace = getAnalyticsNameSpace();
%>
    <input type="hidden" name="CAMUsername" value="CognosAdmin"/>
	<input type="hidden" name="CAMPassword" value="Zaq12@wsxcde"/>
	<input type="hidden" name="CAMNamespace" value="local"  />
	<input type="hidden" name="b_action" value="xts.run"/>
	<input type="hidden" name="m" value="portal/report-viewer.xts"/>
	<input type="hidden" name="method" value="execute"/>
	<input type="hidden" name="nh" value="1"/>
	<input type="hidden" name="prompt" value="true"/>
	<input type="hidden" name="p_User" value="<%=curUsr.getAttribute("Loginid")%>"/>
	<input name="m_obj" id="m_obj" type="hidden" value="/content/package[@name='Yantra-Analytics']/folder[@name='WMS']/report[@name='Dummy Report.xml']"  />
<%
	YFCFilterManager fm = YFCFilterManager.getInstance(pageContext.getServletContext());
	if ( !fm.processRequest(request,response))
		return;
	String wmsAnalyticsURL = getAnalyticsReportNetUrl();
	YFCElement errors = null;
	if (isVoid(wmsAnalyticsURL)) {
		YFCException ex = new YFCException(getI18N("analytics.reportnet.url_is_not_configured_in_the_properties_file_"));
		errors = (ex.getXML()).getDocumentElement();
	}	
%>
<table>
	<td valign="center">
		<table class=table>
			<tbody>
				<tr class=evenrow>
					<td width="100%">&nbsp; </td>
					<td style="BORDER-RIGHT: black 1px solid; BORDER-TOP: black 1px solid; BORDER-LEFT: black 1px solid; BORDER-BOTTOM: black 1px solid" width="100%">		
						<table class=table cellSpacing=0 width="100%" border=1 editable="true">
							<thead>
								<tr>
        							<td class="tablecolumnheader" align=center><yfc:i18n>Report_Name</yfc:i18n></td>
     								<td class="tablecolumnheader" align=center><yfc:i18n>Run_Report</yfc:i18n></td>
    							</tr>
    						</thead>
							<tbody>
<%  
								String reportDisplayNameArr[] = new String[]{
									"ABOVE MAXIMUM REPORT","ACCOUNT TRANSACTION REPORT","BACK ORDER REPORT",
									"BACK ORDER BY ITEM REPORT","BELOW MINIMUM REPORT","BILLING INFORMATION REPORT",
									"BILLED TRANSACTION LISTING REPORT (ALL ACCT CODES)","BILLED TRANSACTION LISTING REPORT (BLM ACCT CODE)",
						            "BILLED TRANSACTION LISTING REPORT (FS ACCT CODE)","BILLED TRANSACTION LISTING REPORT (OTHER ACCT CODE)",
									"CACHE ITEM TRANSACTION REPORT","CACHE ITEM TRANSACTION REPORT (POST INVENTORY)","CACHE ITEM IN KIT REPORT",
									"CACHE TRANSFER SUMMARY REPORT","CATALOG INFO REPORT","CUSTOMER REPORT","DEDICATED LOCATION VIOLATIONS REPORT",
									"DLA PURCHASE ORDER REPORT","DOCUMENT FACE SHEET REPORT","DRAFT ISSUE REPORT","ENTERPRISE ACCOUNT TRANSACTION REPORT", "ENTERPRISE BILLING INFORMATION REPORT", "ENTERPRISE CACHE TRANSFER SUMMARY REPORT",
									"ENTERPRISE LOSS USE REPORT","ENTERPRISE INCIDENT OTHER ORDER SUMMARY REPORT",
									"ENTERPRISE INCIDENT SUMMARY AND LOSS USE REPORT","ENTERPRISE OUTSTANDING SURPLUS REPORT",
								    "ENTERPRISE REDISTRIBUTION REPORT","ENTERPRISE STOCK STATUS BY ITEM REPORT","EXTERNAL AFFAIRS REPORT",
									"GSA PURCHASE ORDER REPORT","INCIDENT OTHER ORDER REPORT","INCIDENT OTHER ORDER SUMMARY REPORT",
									"INCIDENT OTHER ORDER SUMMARY REPORT (WITH KIT SUMMARY)","INCIDENT SUMMARY AND LOSS USE REPORT",
									"INVENTORY BY ZONE REPORT","ITEM EXPIRATION REPORT",
							        "ITEM BY SUPPLIER REPORT","ITEM PURCHASE HISTORICAL REPORT","KIT CONTENTS REPORT","KIT PACKING REPORT",
									"KIT RETURN WORKSHEET REPORT","KIT SUMMARY REPORT","LOCAL MANAGED ITEM LIST","LOSS USE REPORT",
									"LOSS USE REPORT (WITH KIT SUMMARY)","NATIONAL CACHE ITEM REPOSITORY REPORT","NFES MANAGED ITEM LIST",
								    "NON-TRACKABLE INVENTORY ADJUSTMENT REPORT","OPEN WORKORDER REPORT","OPEN WORKORDER REPORT (BY INCIDENT/OTHER ORDER NO)",
								    "OPEN WORKORDER WORKSHEET REPORT","OPEN PO/REQUISITION BY ITEM REPORT","OPEN PO/REQUISITION BY ORDER REPORT",
								    "OUTSTANDING/SURPLUS REPORT","PHYSICAL COUNT LOCATIONS NOT COUNTED REPORT","PHYSICAL COUNT PREP REPORT",
									"PHYSICAL COUNT RESULTS REPORT","PHYSICAL COUNT RESULTS BY ITEM REPORT","PHYSICAL COUNT STATUS REPORT",
								    "PMS SUMMARY REPORT","PMS DOCUMENT FACE SHEET REPORT","PRE DOCUMENT FACE SHEET REPORT (ALL ACCT CODES)",
								    "PRE DOCUMENT FACE SHEET REPORT (BLM ACCT CODE)","PRE DOCUMENT FACE SHEET REPORT (FS ACCT CODE)",
									"PRE DOCUMENT FACE SHEET REPORT (OTHER ACCT CODE)","RECEIPT DETAIL REPORT","REDISTRIBUTION REPORT","RESERVED ITEMS REPORT",
									"RETURN REPORT","RETURN REPORT (BY RETURN NO)","RETURN WORKSHEET BY REFURB TYPE REPORT","RETURN WORKSHEET REPORT",
								    "RETURN WORKSHEET REPORT (BLANK)","RETURN WORKSHEET REPORT (BLANK WITH INCIDENT DETAILS)","STOCK STATUS REPORT",
									"STOCK STATUS BY ITEM REPORT","SUPPLIER REPORT","SUPPLIER BY ITEM REPORT","TRACKABLE ID TRANSACTION REPORT",
								    "TRACKABLE INVENTORY ADJUSTMENT REPORT","TRACKABLE INVENTORY REPORT","TRACKABLE INVENTORY STATUS REPORT","TRACKABLE ITEM LOCATION REPORT",
									"TRACKABLE KIT PACKING REPORT","TRACKABLE KIT PACKING REPORT", "WORKORDER CONSUMABLE REPORT"};
								String reportNameArr[] = new String[]{
									"ABOVE MAXIMUM REPORT","ACCOUNT TRANSACTION REPORT","BACK ORDER REPORT",
									"BACK ORDER BY ITEM REPORT","BELOW MINIMUM REPORT","BILLING INFORMATION REPORT",
									"BILLED TRANSACTION LISTING REPORT (ALL ACCT CODES)","BILLED TRANSACTION LISTING REPORT (BLM ACCT CODE)",
						            "BILLED TRANSACTION LISTING REPORT (FS ACCT CODE)","BILLED TRANSACTION LISTING REPORT (OTHER ACCT CODE)",
									"CACHE ITEM TRANSACTION REPORT","CACHE ITEM TRANSACTION REPORT (POST INVENTORY)","CACHE ITEM IN KIT REPORT",
									"CACHE TRANSFER SUMMARY REPORT","CATALOG INFO REPORT","CUSTOMER REPORT","DEDICATED LOCATION VIOLATIONS REPORT",
									"DLA PURCHASE ORDER REPORT","DOCUMENT FACE SHEET REPORT","DRAFT ISSUE REPORT","ENTERPRISE ACCOUNT TRANSACTION REPORT", "ENTERPRISE BILLING INFORMATION REPORT","ENTERPRISE CACHE TRANSFER SUMMARY REPORT",
								    "ENTERPRISE LOSS USE REPORT","ENTERPRISE INCIDENT OTHER ORDER SUMMARY REPORT",
								    "ENTERPRISE INCIDENT SUMMARY AND LOSS USE REPORT","ENTERPRISE OUTSTANDING SURPLUS REPORT",
								    "ENTERPRISE REDISTRIBUTION REPORT","ENTERPRISE STOCK STATUS BY ITEM REPORT","EXTERNAL AFFAIRS REPORT",
									"GSA PURCHASE ORDER REPORT","INCIDENT OTHER ORDER REPORT","INCIDENT OTHER ORDER SUMMARY REPORT",
									"INCIDENT OTHER ORDER SUMMARY REPORT (WITH KIT SUMMARY)","INCIDENT SUMMARY AND LOSS USE REPORT",
									"INVENTORY BY ZONE REPORT","ITEM EXPIRATION REPORT",
								    "ITEM BY SUPPLIER REPORT","ITEM PURCHASE HISTORICAL REPORT","KIT CONTENTS REPORT","KIT PACKING REPORT",
								    "KIT RETURN WORKSHEET REPORT","KIT SUMMARY REPORT","LOCAL MANAGED ITEM LIST","LOSS USE REPORT",
									"LOSS USE REPORT (WITH KIT SUMMARY)","NATIONAL CACHE ITEM REPOSITORY REPORT","NFES MANAGED ITEM LIST",
								    "NON-TRACKABLE INVENTORY ADJUSTMENT REPORT","OPEN WORKORDER REPORT","OPEN WORKORDER REPORT (BY INCIDENT/OTHER ORDER NO)",
								    "OPEN WORKORDER WORKSHEET REPORT","OPEN PO/REQUISITION BY ITEM REPORT","OPEN PO/REQUISITION BY ORDER REPORT",
								    "OUTSTANDING SURPLUS REPORT","PHYSICAL COUNT LOCATIONS NOT COUNTED REPORT","PHYSICAL COUNT PREP REPORT",
									"PHYSICAL COUNT RESULTS REPORT","PHYSICAL COUNT RESULTS BY ITEM REPORT","PHYSICAL COUNT STATUS REPORT",
								    "PMS SUMMARY REPORT","PMS DOCUMENT FACE SHEET REPORT","PRE DOCUMENT FACE SHEET REPORT (ALL ACCT CODES)",
								    "PRE DOCUMENT FACE SHEET REPORT (BLM ACCT CODE)","PRE DOCUMENT FACE SHEET REPORT (FS ACCT CODE)",
									"PRE DOCUMENT FACE SHEET REPORT (OTHER ACCT CODE)","RECEIPT DETAIL REPORT","REDISTRIBUTION REPORT","RESERVED ITEMS REPORT",
								    "RETURN REPORT","RETURN REPORT (BY RETURN NO)","RETURN WORKSHEET BY REFURB TYPE REPORT","RETURN WORKSHEET REPORT",
								    "RETURN WORKSHEET REPORT (BLANK)","RETURN WORKSHEET REPORT (BLANK WITH INCIDENT DETAILS)","STOCK STATUS REPORT",
								    "STOCK STATUS BY ITEM REPORT","SUPPLIER REPORT","SUPPLIER BY ITEM REPORT","TRACKABLE ID TRANSACTION REPORT",
								    "TRACKABLE INVENTORY ADJUSTMENT REPORT","TRACKABLE INVENTORY REPORT","TRACKABLE INVENTORY STATUS REPORT","TRACKABLE ITEM LOCATION REPORT",
								    "TRACKABLE KIT PACKING REPORT","TRACKABLE KIT PACKING REPORT","WORKORDER CONSUMABLE REPORT"};
								String reportDisplayName; String reportName;
								for ( int i = 0; i < reportNameArr.length ; i++ ) {
									reportDisplayName = reportDisplayNameArr[i]; 
									reportName = reportNameArr[i];
%>
									<tr>
										<td>
	        								<input readonly=true class=protectedinput size=65 maxLength=90 value="<%=reportDisplayName%>">
										</td>
										<td>
       									<input type="image" SRC='../console/icons/go.gif' NAME=<%=reportName%>   
       										onClick='runReport("<%=reportName%>"); this.form.target="YantraReports"; this.form.action="<%=wmsAnalyticsURL%>";'> 
										</td>
									</tr>
<%
								}
%>
    						</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
	</td>
</table>