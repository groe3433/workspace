<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table height="100%" class="view" cellSpacing=0 cellPadding=0  >
    <tr>
        <td valign="top" class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@Company"/>
		</td>
     </tr>
	 <%
	 String strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@FirstName");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		 <td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@FirstName"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@MiddleName");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
         <td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@MiddleName"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@LastName");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@LastName"/>
		</td>    
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine1");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine1"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine2");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine2"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine3");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine3"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine4");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine4"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine5");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
	        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine5"/>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine6");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@AddressLine6"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@City");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@City"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@State");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@State"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@Country");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@Country"/>
		</td>
	</tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@DayPhone");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext"><yfc:i18n>Day_Phone</yfc:i18n>
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@DayPhone"/>
		
		<td/>
       </tr>
	   <%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@MobilePhone");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		<td class="protectedtext"><yfc:i18n>Mobile_Phone</yfc:i18n>
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@MobilePhone"/>
		<td/>
    </tr>
	<%}%>
	<%
	 strValue= resolveValue("xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@EveningPhone");
	 if(strValue != null && (!strValue.equals("")))
	 {
	 %>
	 <tr>
		 <td class="protectedtext"><yfc:i18n>Evening_Phone</yfc:i18n>
			<yfc:getXMLValue binding="xml:SupplierContact:/OrganizationList/Organization/ContactPersonInfo/@EveningPhone"/>
		</td>
    </tr>
	<%}%> 
</table>