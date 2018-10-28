<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">

function CheckInput(viewId,chkName)
{
	if (!chkName) {
		chkName="EntityKey";
	}
	var eleArray = document.forms["containerform"].elements;
	var foundChk = false;
	var count=0;
	var sEntityKey;
	for ( var i =0; i < eleArray.length; i++ ) {
		if ( eleArray[i].name == chkName ) {
			foundChk=true;
			if (eleArray[i].checked ) {
				count++;
				sEntityKey=eleArray[i].value;
			}
		}
	}
	if ( foundChk && count >1 ) {
		alert(YFCMSG013); //YFCMSG013="Select only one record for this action";
		document.body.style.cursor='auto';
		return false;
	}

	answer = confirm("Do you really want to Delete this Customer ?");

	if (!answer)
	{
		return false;
	}
	
	return true;
}

</script>

<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
<tr>
<td sortable="no" class="checkboxheader">
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="ResetDetailPageDocumentType" value="Y"/>	<%-- cr 35413 --%>
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader"><yfc:i18n>Customer_ID</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Customer_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>City</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>State</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Zip_Code</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Is_Active</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/CustomerList/@Customer" id="Customer">
<tr>
<yfc:makeXMLInput name="customerKey">
<yfc:makeXMLKey binding="xml:/Customer/@CustomerKey" value="xml:/Customer/@CustomerKey"/>
</yfc:makeXMLInput>
<td class="checkboxcolumn">
<input type="checkbox" value='<%=getParameter("customerKey")%>' name="EntityKey" isHistory='<%=getValue("Customer","xml:/Customer/@isHistory")%>' 	/>
</td>
<td class="tablecolumn">
<a href="javascript:showDetailFor('<%=getParameter("customerKey")%>');">
<yfc:getXMLValue binding="xml:/Customer/@CustomerID"/>
</a>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Extn/@ExtnCustomerName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Consumer/BillingPersonInfo/@City"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Consumer/BillingPersonInfo/@State"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Consumer/BillingPersonInfo/@ZipCode"/></td>
<!-- CR 140 ks-->
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Extn/@ExtnActiveFlag"/></td>
<!-- end CR 140 -->
</tr>
</yfc:loopXML>
</tbody>
</table>
