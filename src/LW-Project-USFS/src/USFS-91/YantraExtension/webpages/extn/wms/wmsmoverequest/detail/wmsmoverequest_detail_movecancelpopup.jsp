<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%if(isShipNodeUser()){%>
<yfc:callAPI apiID="AP1"/>
<%}else{%>
<yfc:callAPI apiID="AP2"/>
<%}%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/cancellationreasonpopup.js"></script>

<table width="100%" class="view">
    <tr>
        <td>
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
        <td>
            <select name="xml:/ModificationReason/@ReasonCode" class="combobox">
                <yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" 
                    name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
    </tr>
    <tr>
        <td>
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
        <td>
            <textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
        </td>
    </tr>
    <tr>
        <td></td>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setOKClicked();return true;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
    <tr>
</table>