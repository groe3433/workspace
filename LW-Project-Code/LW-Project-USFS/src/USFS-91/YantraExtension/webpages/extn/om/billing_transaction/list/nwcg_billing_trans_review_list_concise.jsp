<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/modificationutils.jspf" %>

<table class="table" width="100%" editable="false">
	<thead>
	<tr> 
		<% String sRetrievedRecords = resolveValue("xml:/NWCGBillingTransactionList/@TotalNumberOfRecords");
		out.println("Retrieved " + sRetrievedRecords + " record(s) ");%>
	</tr>
	</thead>
</table>
<table class="table" width="100%" editable="false">
	<thead>
   		<tr> 
	  		<td sortable="no" class="checkboxheader">
            	<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
			</td>
			<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransactionNo")%>">
            	<yfc:i18n>Transaction_No</yfc:i18n>
        	</td>
			<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransType")%>">
            	<yfc:i18n>Trans_Type</yfc:i18n>
        	</td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/NWCGBillingTransaction/@IncidentBlmAcctCode")%>">
            	<yfc:i18n>BLM Account Code</yfc:i18n>
        	</td>
			<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransAmount")%>">
            	<yfc:i18n>Trans_Amount</yfc:i18n>
        	</td>
			<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGBillingTransaction/@TransDate")%>">
            	<yfc:i18n>Trans_Date</yfc:i18n>
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
			<% 
				String strTempNLLen = resolveValue("xml:/NWCGBillingTransaction/@tempNLLen");  
				String strTempSequenceKey = "";
				int intTempNLLen = Integer.parseInt(strTempNLLen);
				String testStr = "xml:/NWCGBillingTransaction/@SequenceKey_2";
				String noOfSKeys = Integer.toString(intTempNLLen);
				for(int i = 0; i < intTempNLLen; i++) {
					strTempSequenceKey = resolveValue("xml:/NWCGBillingTransaction/@SequenceKey_"+i);  
				}
		 	%>
        	<yfc:makeXMLInput name="SeqKey">
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey" />
			<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@strTempNLLen" value="xml:/NWCGBillingTransaction/@tempNLLen" />
			<% 
				for(int k = 0; k < intTempNLLen; k++) { 
			 		testStr = "xml:/NWCGBillingTransaction/@SequenceKey_"+k;
			%>
					<yfc:makeXMLKey binding='<%=testStr%>' value='<%=testStr%>' />
			<% 
				} 
			%>
			</yfc:makeXMLInput>     
			           
        	<td class="checkboxcolumn">                     
         		<input type="checkbox" value='<%=getParameter("SeqKey")%>' name="EntityKey"
				transNo='<%=resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransactionNo")%>'
				reviewed='<%=resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsReviewed")%>'
				extracted='<%=resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsExtracted")%>'/>
        	</td>        
			<input type="hidden" name="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate" value=""/> 
			<% 
				String TDate = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate"); 
		   		String TDateStr1 = TDate.substring(0,4);
		   		String TDateStr2 = TDate.substring(5,7);
           		String TDateStr3 = TDate.substring(8,10);
           		TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
			%> 
        	<input type="hidden" name="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate" value="<%=TDate%>"/> 
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransactionNo"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransType"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IncidentBlmAcctCode"/></td>
			<td class="tablecolumn">$<yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@douTransAmountTotal"/></td>
			<td class="tablecolumn"><%=TDate%></td> 
	    	<!-- <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate"/></td>  !-->
        	<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@CacheId"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsReviewed"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGBillingTransaction:/NWCGBillingTransaction/@IsExtracted"/></td>
    	</tr>
    	</yfc:loopXML> 
	</tbody>
</table>