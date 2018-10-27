<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/Order/@OrderDateQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/Order/@ReqDeliveryDateQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/Order/@ReqShipDateQryType" value="DATERANGE"/>
<input type='hidden' id='FromOrderDate' name='xml:/Order/@FromOrderDate'/>
<input type='hidden' id='ToOrderDate' name='xml:/Order/@ToOrderDate'/>
<input type='hidden' id='FromReqShipDate' name='xml:/Order/@FromReqShipDate'/>
<input type='hidden' id='ToReqShipDate' name='xml:/Order/@ToReqShipDate'/>
<input type='hidden' id='FromReqDeliveryDate' name='xml:/Order/@FromReqDeliveryDate'/>
<input type='hidden' id='ToReqDeliveryDate' name='xml:/Order/@ToReqDeliveryDate'/>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
</jsp:include>

<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/Extn/@ExtnIncidentNumQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNumQryType"/>
</select>
<input type="text" size=25 class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%>/>
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Issue_#</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>

<tr>
<td class="searchlabel" ><yfc:i18n>Total_Amount</yfc:i18n></td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select class="combobox" name="xml:/Order/PriceInfo/@TotalAmountQryType">
<yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Order/PriceInfo/@TotalAmountQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PriceInfo/@TotalAmount")%> />
</td>
</tr>
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
<tr>
<td class="searchlabel" >
<yfc:i18n>Requested_Ship_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input class="dateinput" id="fieldFromReqShipDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldFromReqShipDate','FromReqShipDate')"/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" id="fieldToReqShipDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldToReqShipDate','ToReqShipDate')"/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Requested_Delivery_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" >
<input class="dateinput" id="fieldFromReqDeliveryDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldFromReqDeliveryDate','FromReqDeliveryDate')"/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" id="fieldToReqDeliveryDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldToReqDeliveryDate','ToReqDeliveryDate')"/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
</table>
