<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/extn/scripts/nwcg_billingTransaction.js"></script>

<table class="table" width="100%" editable="false">
<thead>
   <tr> 
	   <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@ItemId")%>">
            <yfc:i18n>Cache_Item</yfc:i18n>
        </td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@ItemDescription")%>">
            <yfc:i18n>Cache_Item_Description</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransactionNo")%>">
            <yfc:i18n>Transaction_No</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransType")%>">
            <yfc:i18n>Trans_Type</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransAmount")%>">
            <yfc:i18n>Trans_Amount</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransDate")%>">
            <yfc:i18n>Trans_Date</yfc:i18n>
        </td>
        <!-- CR 522 KS -->
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransQty")%>">
            <yfc:i18n>Trans_Qty</yfc:i18n>
        </td>
		<!-- END OF 522 -->
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@IncidentNo")%>">
            <yfc:i18n>Incident_No</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@IncidentYear")%>">
            <yfc:i18n>Incident_Year</yfc:i18n>
        </td>


        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@CacheId")%>">
            <yfc:i18n>Cache_ID</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@IsReviewed")%>">
            <yfc:i18n>Reviewed</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@IsExtracted")%>">
            <yfc:i18n>Extracted</yfc:i18n>
        </td>

   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGBillingTransactionList/@NWCGBillingTransaction" id="NWCGBillingTransaction"> 
    <tr> 
        <yfc:makeXMLInput name="SeqKey">
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey" />
			<yfc:makeXMLKey binding="xml:NWCGListPage:/NWCGBillingTransaction/@IsReviewed" value="xml:/NWCGBillingTransaction/@IsReviewed" />
			<yfc:makeXMLKey binding="xml:NWCGListPage:/NWCGBillingTransaction/@IsExtracted" value="xml:/NWCGBillingTransaction/@IsExtracted" />
			<yfc:makeXMLKey binding="xml:NWCGListPage:/NWCGBillingTransaction/@TransactionNo" value="xml:/NWCGBillingTransaction/@TransactionNo" />
			<yfc:makeXMLKey binding="xml:NWCGListPage:/NWCGBillingTransaction/@ItemId" value="xml:/NWCGBillingTransaction/@ItemId" />
		</yfc:makeXMLInput>                
        <td class="checkboxcolumn">                     
         <input type="checkbox" value='<%=getParameter("SeqKey")%>' name="EntityKey"/>
        </td>        
		<input type="hidden" name="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate" value=""/> 

		<% String TDate = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate"); 
		   String TDateStr1 = TDate.substring(0,4);
		   String TDateStr2 = TDate.substring(5,7);
           String TDateStr3 = TDate.substring(8,10);
           TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
		   //System.out.println("TDate " + TDate); 
		%> 

        <input type="hidden" name="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate" value="<%=TDate%>"/> 
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@ItemId"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@ItemDescription"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransactionNo"/></td>
		<td class="tablecolumn"><yfc:getXMLValue
		binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransType"/></td>
		<td class="tablecolumn">$<yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransAmount"/></td>
		<td class="tablecolumn"><%=TDate%></td> 

	    <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransQty"/></td>

	    <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IncidentNo"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IncidentYear"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@CacheId"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsReviewed"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsExtracted"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>