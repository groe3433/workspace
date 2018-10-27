<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.YFCException"%>
<%@ page import="com.yantra.yfc.dom.YFCElement"%>
<%@ page import="com.yantra.yfs.core.YFSSystem"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ page import="com.yantra.yfc.ui.backend.YFCFilterManager"%>
<%@ page import="com.yantra.yfc.ui.backend.util.*"%>


<script>
function runReport(reportName) {
	document.all["m_obj"].value="/content/folder[@name='Custom Reports']/report[@name='" + reportName + "']";
}
</script>

<%
	YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
	String sAnalyticsNamespace = getAnalyticsNameSpace();
%>
     <%--<input type="hidden" name="CAMUsername" value="cognosadmin"/>
	<input type="hidden" name="CAMPassword" value="Zaq12WsxCde?"/>  
	<input type="hidden" name="CAMNamespace" value="local"  /> --%>
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
							
								String actionTypeStr = "<CommonCode CodeType=\"NWCG_ANALYTICS_NISCC\" IgnoreOrdering=\"N\" MaximumRecords=\"5000\">  <OrderBy> <Attribute Name=\"CodeValue\" Desc=\"N\"/> </OrderBy> 	</CommonCode>";
								YFCElement actionTypeInput = YFCDocument.parse(actionTypeStr).getDocumentElement();
								YFCElement actionTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
							%>
							<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=actionTypeInput%>" templateElement="<%=actionTypeTemplate%>" outputNamespace="ActionList"/>
									
								<yfc:loopXML binding="xml:ActionList:/CommonCodeList/@CommonCode" id="CommonCode">
									<tr>
										<td width="100%">										
	        							
	        									<input readonly=true class=protectedinput size=65 maxLength=90 value="<yfc:getXMLValue name="CommonCode" binding="xml:/CommonCode/@CodeValue" />">
	        								
										</td>
										<td>
       									  <input type="image" SRC='../console/icons/go.gif' NAME=<yfc:getXMLValue name="CommonCode" binding="xml:/CommonCode/@CodeValue" />  
       										onClick='runReport("<yfc:getXMLValue name="CommonCode" binding="xml:/CommonCode/@CodeValue" />"); this.form.target="YantraReports"; this.form.action="<%=wmsAnalyticsURL%>";'> 
										</td>
									</tr>
								</yfc:loopXML>

    						</tbody>
						</table>
					</td>
				</tr>
				
		</tbody>
	</table>
	</td>
</table>