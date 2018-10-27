<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% /* This is the inner table for the order detail audit list */ %>
<table class="table" >
<tbody>
    <tr>
        <td width="10%" >
            &nbsp;
        </td>
        <td width="80%" class="bordertablecolumn">
            <table class="table" SuppressRowColoring="true">
            <thead>
				<td class="tablecolumnheader" >
					<yfc:i18n>Attribute_Name</yfc:i18n>
				</td>
                <td class="tablecolumnheader" >
                    <yfc:i18n>Old_Value</yfc:i18n>
                </td>
                <td class="tablecolumnheader" >
                    <yfc:i18n>New_Value</yfc:i18n>
                </td> 
            </thead>
            <tbody>
                <yfc:loopXML binding="xml:ItemAuditAttribute:/Audit/AuditDetail/Attributes/@Attribute" id="ItemAuditAttribute"> 
				<%
						String strName = resolveValue("xml:ItemAuditAttribute:/Attribute/@Name");
						if(strName != null  && (!strName.equals("Modifyprogid")) && (!strName.equals("ModificationDesc") && (!strName.equals("ModificationCode"))) && (!strName.equals("AdditionalInfo"))  )
						{
						%>
						<tr>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ItemAuditAttribute" binding="xml:ItemAuditAttribute:/Attribute/@Name"/>
								&nbsp;
							</td>
							<%
								String strDatatype = resolveValue("xml:ItemAuditAttribute:/Attribute/@DataType");
								if(strDatatype != null && strDatatype.equals("class com.yantra.yfc.date.YTimestamp"))
								{
									String date = resolveValue("xml:ItemAuditAttribute:/Attribute/@OldValue");
									if(date != null && (!date.equals("")) && (date.length() > 13) )
										date = date.substring(4,6)+"/"+date.substring(6,8)+"/"+date.substring(0,4);
									%>
									<td class="tablecolumn">
									<%=date%>
									</td>
									<td class="tablecolumn">
									<%
									date = resolveValue("xml:ItemAuditAttribute:/Attribute/@NewValue");
									if(date != null && (!date.equals("")) && (date.length() > 13) )
										date = date.substring(4,6)+"/"+date.substring(6,8)+"/"+date.substring(0,4);
									%>
									<%=date%>
									</td>
								<%
								}else
								{
								%>
								<td>
								<yfc:getXMLValue name="ItemAuditAttribute" binding="xml:ItemAuditAttribute:/Attribute/@OldValue"/>
								&nbsp;
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ItemAuditAttribute" binding="xml:ItemAuditAttribute:/Attribute/@NewValue"/>
								&nbsp;
							</td>
						<%}%>
                    </tr>
					<%}%>
                </yfc:loopXML>
				<tr>
				<td>
				Modification_Desc
				</td>
				<td>
				<%
					YFCElement elem = (YFCElement) request.getAttribute("ItemAuditAttribute");
					String strReference = elem.getAttribute("Reference2");
				%>
				<%=strReference%>
				</td>
				</tr>
            </tbody>
            </table>
        </td>
        <td width="10%" >
            &nbsp;
        </td>
    </tr>
</tbody>
</table>

