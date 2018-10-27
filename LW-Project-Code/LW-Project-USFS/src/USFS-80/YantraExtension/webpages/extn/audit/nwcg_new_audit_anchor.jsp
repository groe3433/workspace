<%--
Steps to impliment the auditing without making any changes in the jsp
1. If want to ignore any specific attributes pass the IgnoreList parameter to the inner panel
for example <jsp:param name="IgnoreList" value="Modifyprogid,Modifytimestamp"/>
2. If the list have few labels but you want to display them, create a new anchor page and 
edit the IgnoreList variable accordingly
3. Configure an inner panel with panel ID as AD1 (for instance instead of giving I01 or I011 make is AD1)
4. Configure in the UI getAuditList API and fetch the records required for the entiey being audited
5. set the link and you are all set, have a lok over the supplier item configurations for more details
--%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="AD1"/>
        </jsp:include>
    </td>
</tr>
</table>