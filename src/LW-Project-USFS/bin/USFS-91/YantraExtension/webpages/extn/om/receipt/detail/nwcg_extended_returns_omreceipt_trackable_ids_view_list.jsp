<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%!
public String checkNull(String sCheck)
{
	if (sCheck==null) return("");
	else return sCheck;
}

%>

<%
YFCElement root = (YFCElement)request.getAttribute("Receipt");
String documentType=" ";
String enterpriseCode=" ";

	if(root != null)
	{
		YFCElement oItem = root.getChildElement("ReceiptLines"); 
		if(oItem!= null)
			{
				YFCElement oShipment = root.getChildElement("ReceiptLines"); 
				if(oShipment != null){
					documentType = oShipment.getAttribute("DocumentType");
					enterpriseCode = oShipment.getAttribute("EnterpriseCode");
				}

			}
	}
%>
<table class="table">
<thead>
    <tr>  
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/@ReceiptNo")%>">
            <yfc:i18n>Return No</yfc:i18n>
        </td> 
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>Disposition_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNumber")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine"> 
	<%	
	    String sSerialNo = resolveValue("xml:/ReceiptLine/@SerialNo");	 
	    sSerialNo = checkNull(sSerialNo);
	    
		if(!equals("",sSerialNo)) {

				%>
					<tr> 
					    <td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ItemID"/>
						</td>
						<% 
						   String dispositionCode = resolveValue("xml:/ReceiptLine/@DispositionCode");
						   if(!isVoid(dispositionCode)){	
							   YFCElement dispositionCodeElem = YFCDocument.createDocument("ReturnDisposition").getDocumentElement();
							   dispositionCodeElem.setAttribute("DispositionCode", dispositionCode);
							   dispositionCodeElem.setAttribute("DocumentType", documentType);
							   dispositionCodeElem.setAttribute("OrganizationCode", enterpriseCode);
							   YFCElement templateElem = YFCDocument.parse("<ReturnDisposition DispositionCode=\"\" Description=\"\" DispositionKey=\"\" />").getDocumentElement();
						%>
							   <yfc:callAPI apiName="getReturnDispositionList" inputElement="<%=dispositionCodeElem%>" templateElement="<%=templateElem%>" outputNamespace=""/>
							   <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/ReturnDispositionList/ReturnDisposition/@Description" /></td>
						<% } else { %>
						<td class="tablecolumn">&nbsp;</td>
						<% } %>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
						</td>
					 </tr>
		<%		
			
		}%>
	   </yfc:loopXML> 
</tbody>
</table>
