<!-- /extn/common/render_orderline.jsp --> 
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/yfc/util.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<tr>
<%
	String maximumLinesToDisplay = (String)request.getParameter("MaxRecords");
	if(maximumLinesToDisplay == null || maximumLinesToDisplay.equals(NWCGConstants.EMPTY_STRING))
		maximumLinesToDisplay = ResourceUtil.get("com.nwcg.icbs.yantra.numberofrecords");
	if (maximumLinesToDisplay == null || maximumLinesToDisplay.trim().equals(NWCGConstants.EMPTY_STRING))
		maximumLinesToDisplay = NWCGConstants.MAX_DISPLAY_LINES;
	
	String modifyView = request.getParameter("ModifyView");
	modifyView = modifyView == null ? NWCGConstants.EMPTY_STRING : modifyView;
	
	YFCElement elem = (YFCElement) request.getAttribute("totalLinesOnOrder");	
	String strTotalNumberOfRecords = null ;
	if(elem != null )
	{
		// Get the total number of records
		strTotalNumberOfRecords = elem.getAttribute("TotalNumberOfRecords");
	}
	
	// if total number of records is null make it zero
	if(strTotalNumberOfRecords == null || strTotalNumberOfRecords.equals(NWCGConstants.EMPTY_STRING)) {
		strTotalNumberOfRecords = "0" ;		
	}
	
	int totalNumberOfRecords = Integer.parseInt(strTotalNumberOfRecords) , totalNumberOfPages = 0;

	String strCurrentPageNumber = (String)request.getParameter("CurrentPageNumber");
	//Following used to display New button only on MaxOrderStatus <= Created 
	String sDraftOrderFlag = resolveValue("xml:/Order/@DraftOrderFlag");
	String maxOrderStatus = resolveValue("xml:/Order/@MaxOrderStatus");
	String extnSystemOfOrigin = resolveValue("xml:/Order/Extn/@ExtnSystemOfOrigin");

	if(strCurrentPageNumber == null || strCurrentPageNumber.equals(NWCGConstants.EMPTY_STRING)) {
		strCurrentPageNumber = "1";		
	}
	%>
	<td colspan="5" align=left>
	<%
		if ( (isModificationAllowed("xml:/@AddLine","xml:/Order/AllowedModifications")) &&  
			 (sDraftOrderFlag.equalsIgnoreCase("Y") || maxOrderStatus.startsWith("1100")) && !extnSystemOfOrigin.equals("ROSS")) { 
	%>
			<input type=button value='New' name="New" onClick="clearTableData();addBlankRows(this);"/>	
	<% 
		}	
%>		
	<select class="combobox" name="numberRowsDisplay" onchange="changeNoRecsToDisplay()" title="Select the number of lines to display at a time">
		<option value="10">10</option>
		<option selected="selected" value="20">20</option>				
		<option value="30">30</option>
		<option value="50">50</option>										
	</select>
	<font size=1px><yfc:i18n>Number_of_Records_to_Display</yfc:i18n></font>
	</td>	
	<td colspan="3" align=right>
		<input type=button value="Previous" name="Previous" onClick="checkForFormSubmitAndSave();verifyPageNumberAndFetchData(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=maximumLinesToDisplay%>')"/>
	</td>
	
	<input type="hidden" name="curPageNumberOnLoad" value="<%=strCurrentPageNumber%>"/>
	
	<td colspan="7" align="left">
	<yfc:i18n><font size=1px>&nbsp;&nbsp;&nbsp;Page&nbsp;</font></yfc:i18n>
	<input type="text" size="1" class="unprotectedinput" onBlur="verifyPageNumberAndFetchData(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=maximumLinesToDisplay%>');" value='<%=strCurrentPageNumber%>' name="CurrentPageNumber"/>
	<yfc:i18n><font size=1px>Of</font></yfc:i18n>
	<input type="text" size="2" class="protectedinput" readonly="true" value="" name="TotalPageNumber"/>
	<input type="submit" value="Next" name="Next" onClick="checkForFormSubmitAndSave();verifyPageNumberAndFetchData(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=maximumLinesToDisplay%>')" />
	</td>	
</tr>
<!-- End /extn/common/render_orderline.jsp --> 