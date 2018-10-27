<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

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

	answer = confirm("Do you really want to Delete this Item ?");

	if (!answer)
	{
		return false;
	}
	
	return true;
}

</script>


<table class="table" width="100%" editable="false">
<thead>
   <tr> 
	 
	   <td sortable="yes" class="checkboxheader">
		<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>

       </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierID")%>">
            <yfc:i18n>Supplier_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierID")%>">
            <yfc:i18n>Supplier_Name</yfc:i18n>
        </td>
       <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierPartNo")%>">
            <yfc:i18n>Supplier_Part_No</yfc:i18n>
        </td>
        
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@GlobalItemID")%>">
	        <yfc:i18n>NSN_Number</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierStandardPack")%>">
            <yfc:i18n>Supplier_Standard_Pack</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@Preferred")%>">
            <yfc:i18n>Preferred</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@UnitCost")%>">
            <yfc:i18n>Unit_Cost</yfc:i18n>
        </td>




        
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGSupplierItemList/@NWCGSupplierItem" id="NWCGSupplierItem"> 
    <tr> 
        <yfc:makeXMLInput name="SupplierItemKey">
		<yfc:makeXMLKey binding="xml:/NWCGSupplierItem/@SupplierItemkey" value="xml:/NWCGSupplierItem/@SupplierItemkey" />
		</yfc:makeXMLInput>            
<%	//String organizationID = 	resolveValue("xml:/NWCGSupplierID/@SupplierID");
YFCElement organizationInput = null;
		organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + resolveValue("xml:/NWCGSupplierItem/@SupplierID") + "\" />").getDocumentElement();

		YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationName=\"\"/> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<% 
	String organizationID = 	resolveValue("xml:/OrganizationList/Organization/@OrganizationName"); 
%>


	
        <td class="checkboxcolumn">    
		<input type="checkbox" value='<%=getParameter("SupplierItemKey")%>' name="EntityKey" isHistory='<%=getValue("NWCGSupplierItem","xml:/NWCGSupplierItem/@isHistory")%>' 
        </td>        
		<td class="tablecolumn">
		<a href="javascript:showDetailFor('<%=getParameter("SupplierItemKey")%>');">
			<yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierID"/>
        </a>               
		</td>
 <!-- Supplier Name 2.1 enchancement -->       
        <!--<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierID"/></td>-->

		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrganizationList/Organization/@OrganizationName"/></td>
<!-- Supplier Name 2.1 enchancement -->
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@ItemID"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierPartNo"/></td>
<!-- BEGIN - CR 832 - 18/03/2013 -->
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@GlobalItemID"/></td>
<!-- END - CR 832 - 18/03/2013 -->
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierStandardPack"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@Preferred"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@UnitCost"/></td>



    </tr>
    </yfc:loopXML> 
</tbody>
</table>