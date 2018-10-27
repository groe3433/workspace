<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">


<tr>
    <td class="searchlabel" ><yfc:i18n>Extract_File_Name</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	  <select name="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType" class="combobox" >
           <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
               name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@ExtractFileNameQryType"/>
      </select>
		<input type="text" class="unprotectedinput" maxLength=100 size=40 <%=getTextOptions("xml:/NWCGBillingTransExtract/@ExtractFileName")%>/>
		
    </td>
</tr>
<!--
<tr>
    <td class="searchlabel" ><yfc:i18n>Document_No</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGBillingTransExtract/@DocumentNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@DocumentNoQryType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransExtract/@DocumentNo")%>/>
	</td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Account_Code</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
	    <select name="xml:/NWCGBillingTransExtract/@AcctCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGBillingTransExtract/@AcctCodeQryType"/>
        </select>
		<input type="text" class="unprotectedinput" maxLength=40 size=30 <%=getTextOptions("xml:/NWCGBillingTransExtract/@AcctCode")%>/>
    </td>
</tr>
-->
</table>