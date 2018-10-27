<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
function setOrderCompleteFlag(value){
	var oOrderComplete = document.all("xml:/Order/@OrderComplete");
	if (oOrderComplete != null)
		oOrderComplete.value = value;
}
function setDraftOrderFlag(value){
	document.all('xml:/Order/@DraftOrderFlag').value = value;
}
</script>
<%
	String bReadFromHistory = resolveValue("xml:/Order/@ReadFromHistory");
    if (isVoid(bReadFromHistory) ) {
        bReadFromHistory = "N";
    }
	String sOrderComplete = resolveValue("xml:/Order/@OrderComplete");
    if (isVoid(sOrderComplete) && "N".equals(bReadFromHistory)   ) { // If values of radio buttons gets changed, this condition need to be revisited.
        sOrderComplete = "N";
    }

	String draftOrderFlag = resolveValue("xml:/Order/@DraftOrderFlag");
    if (isVoid(draftOrderFlag) ) {
        draftOrderFlag = "N";
    }
    
%>


<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@OrderDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@ReqDeliveryDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@ReqShipDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value=<%=draftOrderFlag%> />
            <!-- TODO pull this value from constansts file -->
            <input type="hidden" name="xml:/Order/@DocumentType" value="0008.ex"/>
            <input type='hidden' id='FromOrderDate' name='xml:/Order/@FromOrderDate'/>
			<input type='hidden' id='ToOrderDate' name='xml:/Order/@ToOrderDate'/>

        </td>
    </tr>
	<%--
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
    </jsp:include>
	--%>

	<!-- Transfer Order Number -->
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Transfer_Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>

	<!-- From Incident Number -->
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>From_Incident_No</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/Extn/@ExtnIncidentNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
            <img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
            
        </td>
    </tr>

	<!-- From Incident Year -->
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>From_Incident_Year</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%>/>
		</td>
	</tr>

	<!-- To Incident Number -->
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>To_Incident_No</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/Extn/@ExtnToIncidentNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/Extn/@ExtnToIncidentNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentNo")%>/>
            <img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnToIncidentNo','xml:/Order/Extn/@ExtnToIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
            
        </td>
    </tr>

	<!-- To Incident Year -->
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>To_Incident_Year</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentYear")%>/>
		</td>
	</tr>

	<!-- Transferring cache -->
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Ship_Cache</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/@ShipNodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@ShipNodeQryType"/>
            </select>
            <input class="unprotectedinput" type="text" value='<%=getValue("CurrentUser","xml:/User/@Node")%>'  <%=getTextOptions("xml:/Order/@ShipNode")%>/>
			<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>

        </td>
    </tr>

	<!-- Order Date -->
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" id="fieldFromOrderDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldFromOrderDate','FromOrderDate')"/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <yfc:i18n>To</yfc:i18n>
            <input class="dateinput" id="fieldToOrderDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldToOrderDate','ToOrderDate')"/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        </td>
    </tr>
    
	<!-- Order State -->
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_State</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
			<input type="radio" onclick="setDraftOrderFlag('Y');setOrderCompleteFlag(' ')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "DO")%>><yfc:i18n>Draft</yfc:i18n> 
			<input type="radio" onclick="setDraftOrderFlag('N');setOrderCompleteFlag('N')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "N")%>><yfc:i18n>Open</yfc:i18n> 
			<input type="radio" onclick="setDraftOrderFlag('N');setOrderCompleteFlag(' ')"  <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "NO")%>><yfc:i18n>Recent</yfc:i18n><!-- The use of 'NO' is done intentionally, getOrderList API returns history orders only if ReadFromHistory =='Y'  -->
			<input type="radio" onclick="setDraftOrderFlag('N');setOrderCompleteFlag(' ')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "Y")%>><yfc:i18n>History</yfc:i18n>
            <input type="radio" onclick="setDraftOrderFlag('N');setOrderCompleteFlag(' ')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "B")%>><yfc:i18n>All</yfc:i18n>
            <input type="hidden" name="xml:/Order/@OrderComplete" value="<%=sOrderComplete%>"/>
        </td>
    </tr>

	<tr>
        <td class="searchlabel">
            <yfc:i18n>Selecting_All_may_be_slow</yfc:i18n>
        </td>
    </tr>
    
</table>
