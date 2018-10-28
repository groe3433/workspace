<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript">
function enterActionCancellationReason(modReasonViewID, modReasonCodeBinding, modReasonTextBinding,screenType,key) {

    var myObject = new Object();
    myObject.currentWindow = window;
    myObject.reasonCodeInput = document.all(modReasonCodeBinding);
    myObject.reasonTextInput = document.all(modReasonTextBinding);       
	if(screenType=='LIST'){
		<%if(isShipNodeUser()){%>
		yfcShowDetailPopupWithKeys(modReasonViewID,"","550","255",myObject,key,"count", "") ;
		<%}else{%>
			if(isMultipleRecordsSelected(key)){
				alert('<%=getI18N("Node_User_Can_Only_Cancel_Multiple_Requests_Select_Only_one_Record")%>');
				return false;
			}else{
				yfcShowDetailPopupWithKeys(modReasonViewID,"","550","255",myObject,key,"count", "") ;
			}
		<%}%>
	}else{
		yfcShowDetailPopupWithKeys(modReasonViewID, "", "550", "255", myObject, key,"count", "");
	}

	var retVal = myObject["EMReturnValue"];
    var returnValue = myObject["OKClicked"];
    if ( "YES" == returnValue ) {
        return retVal;
    } else {
        return false;
    }
}
</script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/countrequest.js"></script>
	<%
		boolean bCallApi = false;
		YFCElement elemClassificationPurpose = null;
		if(!isVoid(resolveValue("xml:/CountRequest/@EnterpriseCode"))){
			bCallApi = true;
	%>
		<yfc:callAPI apiID="AP1"/>

	<%
		elemClassificationPurpose = (YFCElement) request.getAttribute("ClassificationPurpose");
		//System.out.println(" elemClassificationPurpose --> is "+elemClassificationPurpose.toString());

	}%>
	<table  editable="false" class="table">
		<thead>
			<tr>
				<td class="checkboxheader" sortable="no">
					<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
				</td>
				<td class="tablecolumnheader"><yfc:i18n>Count_Request_#</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Count_Program_Name</yfc:i18n></td>
				<%if(isShipNodeUser()){%>
				<td class="tablecolumnheader"><yfc:i18n>Location</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
				<%}else{%>
				<td class="tablecolumnheader"><yfc:i18n>Node</yfc:i18n></td>
				<%}%>
				<td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>
<%
			if((!isVoid(elemClassificationPurpose)) && elemClassificationPurpose.hasChildNodes()){
		        String root[] = new String[1];
			    root[0] = "ClassificationPurposeCode";
				elemClassificationPurpose.sortChildren(root, true);
					for(Iterator j = elemClassificationPurpose.getChildren();j.hasNext();){
					YFCElement childElem = (YFCElement)j.next();
					if(!isVoid(childElem.getAttribute("ClassificationPurposeCode"))){
					%>
					<td class="tablecolumnheader">
				        <yfc:i18n> <%=childElem.getAttribute("AttributeName")%></yfc:i18n>
				    </td>
					<%}
				}
			}

%>

				<td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Requesting_User</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
			</tr>
		</thead>
		<tbody>
			<yfc:loopXML binding="xml:/CountRequestList/@CountRequest" id="CountRequest">
				<tr>
					<yfc:makeXMLInput name="countrequestkey">
						<yfc:makeXMLKey binding="xml:/CountRequest/@CountRequestKey" value="xml:/CountRequest/@CountRequestKey" />
						<yfc:makeXMLKey binding="xml:/CountRequest/@Node" value="xml:/CountRequest/@Node" />
					</yfc:makeXMLInput>
					<td class="checkboxcolumn">
						<input type="checkbox" value='<%=getParameter("countrequestkey")%>' name="EntityKey"/>
					</td>
					<td class="tablecolumn">
						<a href="javascript:showDetailFor('<%=getParameter("countrequestkey")%>');">
						<yfc:getXMLValue binding="xml:/CountRequest/@CountRequestNo"/>
						 </a>
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/@CountProgramName"/></td>
					<%if(isShipNodeUser()){%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/@LocationId"/></td>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/@EnterpriseCode"/></td>
						<%}else{%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/@Node"/></td>
					<%}%>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/Item/@Description"/></td>			
<%
			if(!isVoid(elemClassificationPurpose)){
					int iCounter = 1;
					for(Iterator j = elemClassificationPurpose.getChildren();j.hasNext();){
					YFCElement childElem = (YFCElement)j.next();
					if(!isVoid(childElem.getAttribute("ClassificationPurposeCode"))){
							String sAttrBinding = "xml:/CountRequest/@ItemClassification"+String.valueOf(iCounter);
							++iCounter; 
						%>
						<td class="tablecolumn" nowrap="true">
							<%=resolveValue(sAttrBinding)%>
						</td>
					<%}
				}
			}

%>
					<td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/CountRequest/Priority/@Description"/></td>
					<td class="tablecolumn"  sortValue="<%=getDateValue("xml:/CountRequest/@StartNoEarlierThan")%>"><yfc:getXMLValue binding="xml:/CountRequest/@StartNoEarlierThan"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountRequest/RequestingUser/@UserName"/></td>
					<td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/CountRequest/Status/@Description"/></td>					
				</tr>
			</yfc:loopXML>
		</tbody>
		<input type="hidden" name="xml:/CountRequest/@CountCancellationReasonCode" />
        <input type="hidden" name="xml:/CountRequest/@CountReasonText"/>
	</table>
