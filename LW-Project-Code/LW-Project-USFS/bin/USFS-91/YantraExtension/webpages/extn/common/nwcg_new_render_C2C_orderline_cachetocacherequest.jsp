<!-- /extn/common/C2C_render_orderline.jsp --> 
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/yfc/util.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%
	String strMax = "2";

	YFCElement elem = (YFCElement) request.getAttribute("TotalNumberOfOrderLines");
	
	String strTotalNumberOfRecords = null ;

	if(elem != null )
	{
		// get the total numbre of records
		strTotalNumberOfRecords = elem.getAttribute("TotalNumberOfRecords");

	}
	
	// if total number of records is null make it zero
	if(strTotalNumberOfRecords == null || strTotalNumberOfRecords.equals(""))
		strTotalNumberOfRecords = "0" ;


	int totalNumberOfRecords = Integer.parseInt(strTotalNumberOfRecords) , recordsPerPage = Integer.parseInt(strMax), totalNumberOfPages = 0 ;
	
	// check the mod, if the total number of records is in multiples of maximum records we dont have to add a page
	// explictly as the remainder will be zero

	if(totalNumberOfRecords % recordsPerPage > 0)
	{
		// add one page 
		totalNumberOfPages = (totalNumberOfRecords/recordsPerPage ) + 1 ;
	}
	else
	{
		totalNumberOfPages = totalNumberOfRecords/recordsPerPage; 
	}
	// when the user doesnt have any of the lines we will display him page no 1 insated of page 1 of 0 
	if(totalNumberOfPages == 0)
	{
		totalNumberOfPages = 1 ;
	}
%>
    <tr>
	<%
	String strCurrentPageNumber = (String)request.getParameter("CurrentPageNumber");
	//added for CR 475 starts here
	String sDraftOrderFlag = resolveValue("xml:/Order/@DraftOrderFlag");
	//added for CR 475 ends here
	if(strCurrentPageNumber == null || strCurrentPageNumber.equals(""))
		strCurrentPageNumber = "1" ;
	%>

	<td class="tablecolumnheader" nowrap="true" style="width:5px"></td>

	<td colspan=3 align=left>
<!--	<%if (isModificationAllowed("xml:/@AddLine","xml:/Order/AllowedModifications")) { %>

    <% //added for CR 475 starts here
		//if(sDraftOrderFlag.equalsIgnoreCase("Y")) {
			//added for CR 475 ends here%>	
	<input type=button value='New' name="New" onClick="addBlankRows(this);"/>

	<%// } //end if(sDraftOrderFlag.equalsIgnoreCase("Y"))
	}%> 
-->
	<input type=button value='New' name="New" onClick="addBlankRows_CacheToCacheRequest(this);"/>
	</td>

	<td colspan="15" align="center">	
	<input type=button value='Previous' name="Previous" onClick="fetchDataWithParams(this,'getOrderLineList',populateOrderLines,setOrderLineParam(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=strMax%>'));"/>
	&nbsp;&nbsp;&nbsp;&nbsp;
	Page &nbsp; 
	<input type='text' style="width:25px" onBlur="verifyPageNumberAndFetchData(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=strMax%>');" value='<%=strCurrentPageNumber%>' name="CurrentPageNumber"/>
	&nbsp; Of &nbsp;
	<input type='text' style="width:25px" readonly value='<%=totalNumberOfPages%>' name="TotalPageNumber"/>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<input type=submit value='Next' name="Next" onClick="checkForFormSubmitAndSave();fetchDataWithParams(this,'getOrderLineList',populateOrderLines,setOrderLineParam(this,'<%=resolveValue("xml:/Order/@OrderHeaderKey")%>','<%=strMax%>'));"/>
	</td>	
</tr>
<!-- End /extn/common/render_C2C_orderline.jsp --> 