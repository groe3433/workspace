<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="Javascript">
	<%--
	function setDummyOrder(elem){
	 var orderNo = document.getElementById('xml:/OrderLine/Order/Extn/@ExtnIncidentNo');
	 orderNo.value='NO_ORDER';
	 var incidentNoLabelRow = document.getElementById('incidentNoLabelRow');
	 var incidentNoRow = document.getElementById('incidentNoRow');
	 incidentNoLabelRow.style.display='none';
	 incidentNoRow.style.display='none'
	}
	function clearDummyOrder(elem){
	 var orderNo = document.getElementById('xml:/OrderLine/Order/Extn/@ExtnIncidentNo');
	 orderNo.value='';
	 var incidentNoLabelRow = document.getElementById('incidentNoLabelRow');
	 var incidentNoRow = document.getElementById('incidentNoRow');
	 incidentNoLabelRow.style.display='';
	 incidentNoRow.style.display=''
	}
	--%>
	function clearIsNull(elem){
	 document.getElementById('xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType').value='';
	 var incidentNoLabelRow = document.getElementById('incidentNoLabelRow');
	 var incidentNoRow = document.getElementById('incidentNoRow');
	 incidentNoLabelRow.style.display='';
	 incidentNoRow.style.display=''

	}
	function setIsNull(elem){
	 document.getElementById('xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType').value='ISNULL';
	 var incidentNoLabelRow = document.getElementById('incidentNoLabelRow');
	 var incidentNoRow = document.getElementById('incidentNoRow');
	 //incidentNoLabelRow.style.display='none';
	 //incidentNoRow.style.display='none'
	}
	
</script>

<table class="view">
<tr>
<td>
<!-- <input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="N"/> -->
<input type="hidden" name='xml:/OrderLine/@ItemGroupCode' value='PROD'/>

</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
<jsp:param name="DocumentTypeBinding" value="xml:/OrderLine/Order/@DocumentType"/>
<jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLine/Order/@EnterpriseCode"/>
</jsp:include>
<% // Now call the APIs that are dependent on the common fields (Enterprise Code) %>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>
<tr id="incidentNoLabelRow">
<td class="searchlabel" >
<yfc:i18n>Incident_Other_Order_No</yfc:i18n>
</td>
</tr>
<tr id="incidentNoRow">
<td nowrap="true" class="searchcriteriacell">

<% 
String strQryType = resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType");
if(strQryType == null)
	strQryType = "" ;

boolean isNullSelected = false, isNotNullSelected = false ;
if("ISNULL".equals(strQryType))
	isNullSelected = true ;
else if ("NOTNULL".equals(strQryType))
	isNotNullSelected = true ;
%>
<select name="xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType" onchange="makeIncidentNumberReadOnly(this,'xml:/OrderLine/Order/Extn/@ExtnIncidentNo')" class="combobox">
<option value="" selected> </option>
<%
YFCElement elem = (YFCElement) request.getAttribute("QueryTypeList");
YFCElement elemStringQryType = (YFCElement) elem.getChildElement("StringQueryTypes")  ;
Iterator elemQryType = elemStringQryType.getChildren()  ;
while(elemQryType.hasNext())
{           

            YFCElement child = (YFCElement) elemQryType.next();
            String strType =             child.getAttribute("QueryType");
            String strTypeDesc =     child.getAttribute("QueryTypeDesc");
%>
<option value="<%=strType%>" <%=strQryType.equals(strType) ? "selected":""%> > <%=strTypeDesc%> </option>
<%}%>
<option value="ISNULL" <%= isNullSelected == true ? "selected":"" %> ><yfc:i18n>Is_Null</yfc:i18n></option>
<option value="NOTNULL" <%= isNotNullSelected == true ? "selected":"" %> ><yfc:i18n>Not_Null</yfc:i18n></option>
</select>
<input size="30" maxlength="40" class="unprotectedinput" <%=isNullSelected || isNotNullSelected ? "readonly":""%>  type="text" <%=getTextOptions("xml:/OrderLine/Order/Extn/@ExtnIncidentNo")%> yfcMandatoryMessage="Incident/Other Order no is Mandatory"/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Issue_Other_Issue_No</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@OrderNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>/>
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@Year")%>/>
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Customer_ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@BillToIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@BillToIDQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@BillToID")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
</tr>
<!-- ShipNode -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Ship_Cache</yfc:i18n>
</td>
</tr>

<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@ShipNodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@ShipNodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@ShipNode","xml:/OrderLine/Order/@ShipNode","xml:CurrentUser:/User/@Node")%> />
<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'NODE','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>


<tr>
<td class="searchlabel" >
<yfc:i18n>Item_ID</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Item/@ItemIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Item/@ItemIDQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Item/@ItemID")%>/>
<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
<img class="lookupicon" onclick="callItemLookup('xml:/OrderLine/Item/@ItemID','xml:/OrderLine/Item/@ProductClass','xml:/OrderLine/@OrderingUOM','item','<%=extraParams%>')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Product_Class</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Item/@ProductClass" class="combobox">
<yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
value="CodeValue" selected="xml:/OrderLine/Item/@ProductClass"/>
</select>
</td>
<tr>
<td class="searchlabel" >
<yfc:i18n>Unit_Of_Measure</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/@OrderingUOM" class="combobox">
<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure"
value="UnitOfMeasure" selected="xml:/OrderLine/@OrderingUOM"/>
</select>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Item_Description</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Item/@ItemDescQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Item/@ItemDescQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Item/@ItemDesc")%>/>
</td>
</tr>
<%-- the order exists radio 
<tr>
<td class="searchcriteriacell">
<input type="radio" name="OrderExists" CHECKED onclick="clearIsNull(this);"><yfc:i18n>Order_Exists</yfc:i18n>
<input type="radio" name="OrderExists" onclick="setIsNull(this);"><yfc:i18n>No_Order</yfc:i18n>
</td>
</tr>
--%>
<!-- Set the back order flag -->
<input type="hidden" name="xml:/OrderLine/Extn/@ExtnBackOrderFlag" value="Y"/>
</table>