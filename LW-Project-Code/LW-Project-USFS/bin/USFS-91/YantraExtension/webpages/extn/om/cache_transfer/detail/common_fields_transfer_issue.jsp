<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
String screenType = getParameter("ScreenType");
if (!equals(screenType, "detail")) {
// There are only 2 values that are supported for screenType: "search" & "detail"
// If anything other than "detail" is passed, then assume ScreenType is "search"
screenType = "search";
}

// Set the appropriate parameters based on the screen type. Defaults assume search screen.
String labelTdClass = "searchlabel";
String inputTdClass = "searchcriteriacell";
String refreshJavaScript = "changeSearchView(getCurrentSearchViewId())";
String columnLayout = "1";
String acrossEnterprisesAllowed = "true";
String acrossAllNodesAllowed = "true";

if (equals(screenType, "detail")) {

labelTdClass = "detaillabel";
inputTdClass = "";
refreshJavaScript = "updateCurrentView()";
columnLayout = "3";
acrossEnterprisesAllowed = "false";
}

String overrideColumnLayout = getParameter("ColumnLayout");
if (!isVoid(overrideColumnLayout)) {
columnLayout = overrideColumnLayout;
}

String showDocumentType = getParameter("ShowDocumentType");
if (isVoid(showDocumentType)) {
// The default value of ShowDocumentType is "true"
showDocumentType = "true";
}

String showEnterpriseCode = getParameter("ShowEnterpriseCode");
if (isVoid(showEnterpriseCode)) {
// The default value of ShowEnterpriseCode is "true"
showEnterpriseCode = "true";
}

String enterpriseCodeBinding = getParameter("EnterpriseCodeBinding");
if (isVoid(enterpriseCodeBinding)) {
// The default value of EnterpriseCodeBinding is "xml:/Order/@EnterpriseCode"
enterpriseCodeBinding = "xml:/Order/@EnterpriseCode";
}

String showNode = getParameter("ShowNode");
if (isVoid(showNode)) {
// The default value of ShowNode is "false"
showNode = "false";
}

String doNotProtectNode = getParameter("DoNotProtectNode");
if (isVoid(doNotProtectNode)) {
// The default value of doNotProtectNode is "false"
doNotProtectNode = "false";
}

String selectedNode = null;

// Get the user ID from the special namespace "CurrentUser"
String userID = resolveValue("xml:CurrentUser:/User/@Loginid");
String disableEnterprise = getParameter("DisableEnterprise");
if (isVoid(disableEnterprise)) {
disableEnterprise = "N";
}
%>

<script language="javascript">

function refreshIfValueChanged(controlObj) {

if (yfcHasControlChanged(controlObj)) {
<%=refreshJavaScript%>;
}
}

function acrossAllEntChange(acrossAllSelected) {

var entField = document.all("enterpriseFieldObj");
if (entField != null) {

if (acrossAllSelected == true) {
// Blank out the enterprise code.
entField.value = " ";
}
<%=refreshJavaScript%>;
}
}

function acrossAllNodeChange(acrossAllSel) {

var nodeField = document.all("nodeFieldObj");
if (nodeField != null) {

if (acrossAllSel == true) {
// Blank out the Node.
nodeField.value = " ";
}
<%=refreshJavaScript%>;
}
}
</script>

<tr>
<% if (equals("true", showDocumentType)) {

String documentTypeBinding = getParameter("DocumentTypeBinding");
if (isVoid(documentTypeBinding)) {
// The default value of DocumentTypeBinding is "xml:/Order/@DocumentType"
documentTypeBinding = "xml:/Order/@DocumentType";
}

String refreshOnDocumentType = getParameter("RefreshOnDocumentType");
if (isVoid(refreshOnDocumentType)) {
// The default value of RefreshOnDocumentType is "false"
refreshOnDocumentType = "false";
}

String hardCodeDocumentType = getParameter("HardCodeDocumentType");

String applicationCode = getParameter("ApplicationCode");
if (isVoid(applicationCode)) {
// The default value of ApplicationCode comes from the entity resource definition
applicationCode = resolveValue("xml:/CurrentEntity/@ApplicationCode", false);
}

String showOnlyTemplateDocTypes = getParameter("ShowOnlyTemplateDocTypes");
if (isVoid(showOnlyTemplateDocTypes)) {
// The default value of ShowOnlyTemplateDocTypes is "false"
showOnlyTemplateDocTypes = "false";
}

String templateInputString = " IsTemplateDocument=\"N\"";
if ("true".equals(showOnlyTemplateDocTypes)) {
templateInputString = " IsTemplateDocument=\"Y\"";
}

//get the doc type output namespace and check for validity
boolean validDocTypeOutputNamespace = false;
String docTypeOutputNamespace = getParameter("DocTypeOutputNamespace");
YFCElement docTypeListElement = (YFCElement) request.getAttribute(docTypeOutputNamespace);
if(docTypeListElement != null){
validDocTypeOutputNamespace = true;
}

String docTypeListOutputNamespace = "CommonDocumentTypeList";

if(!validDocTypeOutputNamespace){
// Call API to get the data for the Document Type field.
String documentTypeInputStr = "<DocumentParams DocumentType=\"";
if (!isVoid(hardCodeDocumentType)) {
documentTypeInputStr = documentTypeInputStr + hardCodeDocumentType;
}
documentTypeInputStr = documentTypeInputStr + "\"" + templateInputString + "><DataAccessFilter UserId=\"" + userID + "\" ApplicationCode=\"" + applicationCode + "\" ModuleCode=\" \" AddToConsole=\"Y\"/></DocumentParams>";

YFCElement documentTypeInput = YFCDocument.parse(documentTypeInputStr).getDocumentElement();
YFCElement documentTypeTemplate = YFCDocument.parse("<DocumentParamsList TotalNumberOfRecords=\"\" DefaultDocTypeForApplication=\"\"><DocumentParams DocumentType=\"\" Description=\"\"/></DocumentParamsList>").getDocumentElement();
%>

<yfc:callAPI apiName="getDocumentTypeList" inputElement="<%=documentTypeInput%>" templateElement="<%=documentTypeTemplate%>" outputNamespace="<%=docTypeListOutputNamespace%>"/>

<%
}//end of if validDocTypeOutputNamespace
else{
docTypeListOutputNamespace = docTypeOutputNamespace;
}

YFCElement allDocTypeListElement = (YFCElement) request.getAttribute(docTypeListOutputNamespace);

double numberOfDocTypes = getNumericValue("xml:" + docTypeListOutputNamespace + ":/DocumentParamsList/@TotalNumberOfRecords");

String selectedDocumentType = resolveValue(documentTypeBinding);
if (isVoid(selectedDocumentType) || equals("Y", getParameter("ResetDocumentType") ) ) {	//	cr 35413
// Default the document type to the default one as returned by the API
if (numberOfDocTypes > 0) {

selectedDocumentType = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/@DefaultDocTypeForApplication");

// Check that the user actually has data security rights to the default document
// type. If the user does not have rights, then default the first document type returned.
boolean hasAccessToDefaultDocType = false;

if (null != allDocTypeListElement) {
for (Iterator i = allDocTypeListElement.getChildren(); i.hasNext();) {

YFCElement singleDocType = (YFCElement)i.next();
if (equals(selectedDocumentType, singleDocType.getAttribute("DocumentType"))) {
hasAccessToDefaultDocType = true;
}
}
}

if (!hasAccessToDefaultDocType) {
// Default the document type to the first one in the API output.
selectedDocumentType = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/DocumentParams/@DocumentType");
}
}
}
// Set the appropriate document type in the CommonFields namespace
setCommonFieldAttribute("DocumentType", selectedDocumentType);

// Set the document type description if there is only one document type
String docTypeDesc = "";
if (numberOfDocTypes == 1) {

docTypeDesc = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/DocumentParams/@Description");
}
%>

<td class="<%=labelTdClass%>">
<yfc:i18n>Document_Type</yfc:i18n>
</td>

<% if (equals(screenType, "search")) { %>
</tr>
<tr>
<% } %>

<% if (numberOfDocTypes > 1) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<select class="combobox"
<% if (equals(refreshOnDocumentType, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %>
<%=getComboOptions(documentTypeBinding)%>>
<% if (null != allDocTypeListElement) {
for (Iterator i = allDocTypeListElement.getChildren(); i.hasNext();) {
YFCElement singleDocType = (YFCElement)i.next(); %>

<% if (equals(selectedDocumentType, singleDocType.getAttribute("DocumentType"))) { %>
<option value="<%=singleDocType.getAttribute("DocumentType")%>"
selected="Y"
<% } %>
><%=getDBString(singleDocType.getAttribute("Description"))%></option>
<% }
} %>
</select>
</td>
<% } else { %>
<td class="protectedtext" nowrap="true">
<%=getDBString(docTypeDesc)%>
<input type="hidden" <%=getTextOptions(documentTypeBinding, selectedDocumentType)%>/>
</td>
<% } %>

<% if (equals(columnLayout, "1")) { %>
</tr>
<tr>
<% } %>
<% } %>

<% if (equals("true", showNode)) {

String nodeBinding = getParameter("NodeBinding");
if (isVoid(nodeBinding)) {
// The default value of NodeBinding is "xml:/Order/@ShipNode"
nodeBinding = "xml:/Order/@ShipNode";
}

String refreshOnNode = getParameter("RefreshOnNode");
if (isVoid(refreshOnNode)) {
// The default value of RefreshOnNode is "false"
refreshOnNode = "false";
}

String nodeLabel = getParameter("NodeLabel");
if (isVoid(nodeLabel)) {
// The default value of NodeLabel is "Node"
nodeLabel = "Node";
}

selectedNode = resolveValue(nodeBinding);

double numberOfNodes = 0;
%>

<td class="<%=labelTdClass%>"><yfc:i18n><%=nodeLabel%></yfc:i18n></td>

<% if (equals(screenType, "search")) { %>
</tr>
<tr>
<% }

String defaultNodeBinding ="";
if (isShipNodeUser()) {

numberOfNodes = 1;
selectedNode = resolveValue("xml:CurrentUser:/User/@Node");
defaultNodeBinding = "xml:CurrentUser:/User/@Node";

} else {

defaultNodeBinding = "xml:CommonNodeList:/ShipNodeList/ShipNode/@ShipNode";
selectedNode = resolveValue(nodeBinding);
String acrossAllNodesValue = getParameter("acrossAllNodesAllowed");
if (!isVoid(acrossAllNodesValue)) {
acrossAllNodesAllowed = acrossAllNodesValue;
}
if ((equals("true", acrossAllNodesAllowed)) && (!equals("false", request.getParameter("AcrossNodes")))) {
defaultNodeBinding = " ";
}
// Call API to get the data for the Node field.
YFCElement nodeInput = null;
if (isHub()) {
// Get all nodes for HUB user.
nodeInput = YFCDocument.parse("<ShipNode/>").getDocumentElement();
}
else {
// Get all nodes owned by the logged in organization for any other user.
nodeInput = YFCDocument.parse("<ShipNode OwnerKey=\"" + resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode")  + "\"/>").getDocumentElement();
}

YFCElement nodeTemplate = YFCDocument.parse("<ShipNodeList TotalNumberOfRecords=\"\"><ShipNode ShipNode=\"\" OwnerKey=\"\"/></ShipNodeList>").getDocumentElement();
%>

<yfc:callAPI apiName="getShipNodeList" inputElement="<%=nodeInput%>" templateElement="<%=nodeTemplate%>" outputNamespace="CommonNodeList"/>

<%
numberOfNodes = getNumericValue("xml:CommonNodeList:/ShipNodeList/@TotalNumberOfRecords");

// If the number of nodes is 1, then make sure this one is selected and protected.
if ((equals("true",doNotProtectNode)) && (!equals("true", request.getParameter("AcrossNodes")))) {
if(numberOfNodes == 1){
selectedNode = resolveValue("xml:CommonNodeList:/ShipNodeList/ShipNode/@ShipNode");
}
}
}

if (!equals("true", request.getParameter("AcrossNodes"))) {
selectedNode = getSearchCriteriaValueWithDefaulting(nodeBinding, defaultNodeBinding);
}
boolean acrossAllNodes = false;
// If Node is void at this point, then we need to make
// the Across All Nodes Option selected (if it will be shown).
if (isVoid(selectedNode)) {
acrossAllNodes = true;
// Set the default Node the CommonFields namespace even
// though it will not appear selected in the Node field
setCommonFieldAttribute("Node", resolveValue(nodeBinding, false));
}
else {
// Set the appropriate node in the CommonFields namespace
setCommonFieldAttribute("Node", selectedNode);
}

%>

<% if (acrossAllNodes && (equals("true",doNotProtectNode))) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="false"/>
<input type="hidden" id="nodeFieldObj" <%=getTextOptions(nodeBinding, selectedNode)%>/>
<select class="protectedinput" disabled="true" name="justForDisplay">
<yfc:loopOptions binding="xml:CommonNodeList:/ShipNodeList/@ShipNode" name="ShipNode"
value="ShipNode"/>
</select>
</td>
<% } else { %>
<% if (numberOfNodes > 20) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
<input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
<% } %>
<input type="text" class="protectedinput" contenteditable="false" <%=getTextOptions(nodeBinding, selectedNode)%>  id="nodeFieldObj" />
<img class="lookupicon" name="search"
onclick="callLookup(this,'shipnode','xml:/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey=<%=resolveValue("xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>')
<% if (equals(refreshOnNode, "true")) { %>;refreshIfValueChanged(document.all('<%=nodeBinding%>'))<% } %>"
<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
</td>
<% } else if ((numberOfNodes <= 20) && (numberOfNodes > 1)) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
<input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
<% } %>
<select class="combobox"  id="nodeFieldObj"
<% if (equals(refreshOnNode, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %>
<%=getComboOptions(nodeBinding)%>>
<yfc:loopOptions binding="xml:CommonNodeList:/ShipNodeList/@ShipNode" name="ShipNode"
value="ShipNode" selected='<%=selectedNode%>'/>
</select>
</td>
<% } else { %>
<td class="protectedtext" nowrap="true">

<% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
<input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
<% } %>
<%=selectedNode%>
<input type="hidden" id="nodeFieldObj" <%=getTextOptions(nodeBinding, selectedNode)%>/>
</td>
<% } %>
<%}%>
<% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
</tr><tr>
<td class="<%=inputTdClass%>" nowrap="true">
<input type="radio" name="AcrossNodes" value="true" onclick="acrossAllNodeChange(true)" <% if (acrossAllNodes) {%> checked="true" <%}%>/>
<yfc:i18n>Across_Nodes</yfc:i18n>
</td>
<% } %>

<% if (equals(columnLayout, "1")) { %>
</tr>
<tr>
<% } %>
<% } %>

<% if (equals("true", showEnterpriseCode)) {

String enterpriseCodeLabel = getParameter("EnterpriseCodeLabel");
if (isVoid(enterpriseCodeLabel)) {
// The default value of EnterpriseCodeLabel is "Enterprise"
enterpriseCodeLabel = "Enterprise";
}

String refreshOnEnterpriseCode = getParameter("RefreshOnEnterpriseCode");
if (isVoid(refreshOnEnterpriseCode)) {
// The default value of RefreshOnEnterpriseCode is "false"
refreshOnEnterpriseCode = "false";
}

// The EnterpriseListForNodeField parameter determines if the list of enterprises
// that are displayed in the screen should be based on the node selection. If passed as
// true, then the list of enterprises will be all the enterprises for which this node
// organization participates.
String enterpriseListForNodeField = getParameter("EnterpriseListForNodeField");
if (isVoid(enterpriseListForNodeField)) {
// The default value of EnterpriseListForNodeField is "false"
enterpriseListForNodeField = "false";
}

String organizationListForInventory = getParameter("OrganizationListForInventory");
if (isVoid(organizationListForInventory)) {
// The default value of OrganizationListForInventory is "false"
organizationListForInventory = "false";
}

if (equals("true", organizationListForInventory)) {
acrossEnterprisesAllowed = "false";
}

String organizationListForCapacity = getParameter("OrganizationListForCapacity");
if (isVoid(organizationListForCapacity)) {
// The default value of OrganizationListForCapacity is "false"
organizationListForCapacity = "false";
}

if (equals("true", organizationListForCapacity)) {
acrossEnterprisesAllowed = "false";
}

// The DefaultEnterpriseBinding parameter is used to properly default the correct enterprise
// (or organization code) in the Enteprise field when the end user initially navigates to
// the screen.
String defaultEnterpriseBinding = getParameter("DefaultEnterpriseBinding");
if (isVoid(defaultEnterpriseBinding)) {
// The default value of DefaultEnterpriseBinding is "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey"
defaultEnterpriseBinding = "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey";
// If an Org user(user's Org is not a Ship Node or an Enterprise) is logged in and organizationListForInventory,
//	we want to default the Organization field to the logged in user's Org.
if (equals("true", organizationListForInventory)) {
if((!isShipNodeUser()) && (!isEnterprise())) {
defaultEnterpriseBinding = "xml:CurrentOrganization:/Organization/@OrganizationCode";
}
}
}

String finalDefaultEnterpriseBinding = defaultEnterpriseBinding;
// The AcrossEnterprisesAllowed parameter is used to show an additional radio button
// next to the enterprise field where the user can choose to search across all
// enterprises (basically, this is the equivalent to searching with enterprise code
// as blanks). The default value for this field depends on other parameters.
// If "ScreenType" is detail, then the default value is "false"
// If "OrganizationListForInventory" is true, then the default value is "false"
// Otherwise, the default value is "true".
String acrossEnterprisesAllowedValue = getParameter("AcrossEnterprisesAllowed");
if (!isVoid(acrossEnterprisesAllowedValue)) {
acrossEnterprisesAllowed = acrossEnterprisesAllowedValue;
}

if ((equals("true", acrossEnterprisesAllowed)) && (!equals("false", request.getParameter("AcrossEnterprises")))) {
finalDefaultEnterpriseBinding = " ";
}

// Call API(s) to get the data for the Enterprise field.
YFCElement enterpriseInput = null;
YFCElement enterpriseTemplate = YFCDocument.parse("<OrganizationList TotalNumberOfRecords=\"\"><Organization OrganizationCode=\"\" OrganizationKey=\"\" OrganizationName=\"\"/></OrganizationList>").getDocumentElement();

if ((equals("true", enterpriseListForNodeField)) && (!isVoid(selectedNode))) {
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\"><RelatedOrgList><OrgEnterprise OrganizationKey=\"" + selectedNode + "\"/></RelatedOrgList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement(); %>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
<% }
else if (equals("true", organizationListForInventory)) {
//if logged in user's org is Ship Node or Enterprise, we want a list of enterprises in which the user's org participates.
if((isShipNodeUser()) || (isEnterprise())) {
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\"><OrgRoleList><OrgRole RoleKey=\"ENTERPRISE\"/></OrgRoleList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement();
} else {
//if logged in user's org is neither a Ship Node or Enterprise, we only want that org.
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\"><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement();
} %>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
<% }
else if (equals("true", organizationListForCapacity)) {
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyCapacityOrganizations=\"Y\"/>").getDocumentElement(); %>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
<% }
else {
// Get a list of enterprises from the data security configuration
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\"><OrgRoleList><OrgRole RoleKey=\"ENTERPRISE\"/></OrgRoleList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement(); %>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseListDataSecurity"/>

<%
if (!isShipNodeUser()) {
// Get a list of enterprises this organization participates in (don't do this
// for a ship node user though).
enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\"><RelatedOrgList><OrgEnterprise OrganizationKey=\"" + resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode") + "\"/></RelatedOrgList></Organization>").getDocumentElement(); %>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseListParticipation"/>
<% }

// Combine the two lists
YFCElement completeOrgList = combineOrganizationLists((YFCElement) request.getAttribute("CommonEnterpriseListDataSecurity"), (YFCElement) request.getAttribute("CommonEnterpriseListParticipation"));
request.setAttribute("CommonEnterpriseList", completeOrgList);
}
%>

<%
String selectedEnterprise = " ";
if (!equals("true", request.getParameter("AcrossEnterprises"))) {
selectedEnterprise = getSearchCriteriaValueWithDefaulting(enterpriseCodeBinding, finalDefaultEnterpriseBinding);
}
boolean acrossAllEnt = false;
// If enterprise code is void at this point, then we need to make
// the Across All Enterprises Option selected (if it will be shown).
if (isVoid(selectedEnterprise)) {
acrossAllEnt = true;
// Set the default enterprise code in the CommonFields namespace even
// though it will not appear selected in the enteprise field
setCommonFieldAttribute("EnterpriseCode", resolveValue(defaultEnterpriseBinding, false));
}
else {
// Set the appropriate enterprise code in the CommonFields namespace
setCommonFieldAttribute("EnterpriseCode", selectedEnterprise);
setCommonFieldAttribute("OrganizationCode", selectedEnterprise);
}

double numberOfEnterprises = getNumericValue("xml:CommonEnterpriseList:/OrganizationList/@TotalNumberOfRecords");
%>

<td class="<%=labelTdClass%>">
<yfc:i18n><%=enterpriseCodeLabel%></yfc:i18n>
</td>

<% if (equals(screenType, "search")) { %>
</tr>
<tr>
<% } %>

<% if (acrossAllEnt) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="false"/>
<input type="hidden" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
<select class="protectedinput" disabled="true" name="justForDisplay">
<yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode"
value="OrganizationCode"/>
</select>
<% if (!equals(screenType, "search")) { %>
</td>
<% } %>
<% } else { %>
<% if (numberOfEnterprises > 20) {

// Show a protected text field with a lookup button next to it. The extra parameters sent
// to the lookup are different for the inventory organization code field.
String extraParams = "lookupForSingleRole=ENTERPRISE&applyDataSecurity=Y";
if (equals("true", organizationListForInventory)) {
extraParams = "applyDataSecurity=Y";
} %>

<td class="<%=inputTdClass%>" nowrap="true">
<% if (equals("true", acrossEnterprisesAllowed)) { %>
<input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
<% } %>

<input type="text" class="protectedinput" contenteditable="false" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
<% if ((!equals("Y", disableEnterprise))) { %>
<img class="lookupicon" name="search"
onclick="callLookup(this,'organization','<%=extraParams%>')
<% if (equals(refreshOnEnterpriseCode, "true")) { %>;refreshIfValueChanged(document.all('<%=enterpriseCodeBinding%>'))<% } %>"
<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
<% } %>
<% if (!equals(screenType, "search")) { %>
</td>
<% } %>
<% } else if ((numberOfEnterprises <= 20) && (numberOfEnterprises > 1) && (!equals(disableEnterprise,"Y"))) { %>
<td class="<%=inputTdClass%>" nowrap="true">

<% if (equals("true", acrossEnterprisesAllowed)) { %>
<input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
<% } %>

<select class="combobox" id="enterpriseFieldObj"
<% if (equals(refreshOnEnterpriseCode, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %>
<%=getComboOptions(enterpriseCodeBinding)%>>
<yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode"
value="OrganizationCode" selected='<%=selectedEnterprise%>'/>
</select>
<% if (!equals(screenType, "search")) { %>
</td>
<% } %>
<% } else { %>
<td class="protectedtext" nowrap="true">

<% if (equals("true", acrossEnterprisesAllowed)) { %>
<input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
<% } %>

<%=selectedEnterprise%>
<input type="hidden" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
<% if (!equals(screenType, "search")) { %>
</td>
<% } %>
<% } %>
<% } %>
<% if (equals("true", acrossEnterprisesAllowed)) { %>
<% if (!equals(screenType, "search")) { %>
<td class="<%=inputTdClass%>" nowrap="true">
<% } %>
<input type="radio" name="AcrossEnterprises" value="true" onclick="acrossAllEntChange(true)" <% if (acrossAllEnt) {%> checked="true" <%}%>/>
<yfc:i18n>Across_Enterprises</yfc:i18n>
</td>
<% } else if(equals("3", columnLayout) )	{%>
<% if (equals(screenType, "search")) { %>
</td>
<% } %>
<td></td>
<td></td>
<% } else {%>
<% if (equals(screenType, "search")) { %>
</td>
<% } %>
<% } %>
<% } %>
</tr>
