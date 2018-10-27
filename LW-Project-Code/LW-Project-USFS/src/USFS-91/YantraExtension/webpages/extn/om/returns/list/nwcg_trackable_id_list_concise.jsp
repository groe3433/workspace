<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="tablecolumnheader"></td>
	   	<td class="tablecolumnheader">
            <yfc:i18n>Trackable_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Manufacturer_Serial_No</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Incident_No</yfc:i18n>
        </td>
	    <td class="tablecolumnheader">
            <yfc:i18n>Incident_Year</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Issue_No</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>		
		<td class="tablecolumnheader">
            <yfc:i18n>Cache_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Manufacturing_Date</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>

    <yfc:loopXML binding="xml:/SerialList/@Serial" id="Serial"> 
    <tr> 

	 <td class="tablecolumn">
       <img class="icon" onClick="setTrackableIDLookupValue('<%=resolveValue("xml:/SerialList/@CurrentSerialList")%>','<%=resolveValue("xml:Serial:/Serial/@SerialNo")%>','<%=resolveValue("xml:Serial:/Serial/@SecSerialNo")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute1")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute2")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute3")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute4")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute5")%>','<%=resolveValue("xml:Serial:/Serial/@TagAttribute6")%>','<%=resolveValue("xml:Serial:/Serial/@ManufacturingDate")%>')"  value="<%=resolveValue("xml:Serial:/Serial/@SerialNo")%>" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />
     </td>

     <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@SerialNo"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@SecSerialNo"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@IncidentNo"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@IncidentYear"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@IssueNo"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@ItemID"/>
	 </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@CacheID"/>
	 </td>
	  </td>
	 <td class="tablecolumn"><yfc:getXMLValue binding="xml:Serial:/Serial/@ManufacturingDate"/>
	 </td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>