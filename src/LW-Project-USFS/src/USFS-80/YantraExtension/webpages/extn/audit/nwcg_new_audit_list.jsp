<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="/yantra/console/scripts/om.js"></script> 
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<%
String strParam = request.getParameter("IgnoreList");
%>
<table class="view" width="33%">
<tr>
    <td colspan="2">
        <input class="button" type="button" value="<%=getI18N("Expand_All")%>" onclick="expandAll('TR','yfsOrderAudit_','<%=getI18N("Click_To_Collapse_Audit_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>' )"/>
        <input class="button" type="button" value="<%=getI18N("Collapse_All")%>" onclick="collapseAll('TR','yfsOrderAudit_','<%=getI18N("Click_To_Expand_Audit_Info")%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>' )"/>
    </td>
</tr>
</table>
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader">
            <yfc:i18n>Audit_#</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderAudit/@Createts")%>">
            <yfc:i18n>Date</yfc:i18n>
        </td> 
        <td class="tablecolumnheader">
            <yfc:i18n>Modified_By</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" sortable="no">
            
        </td>
        <td class="tablecolumnheader" sortable="no">
            
        </td> 
        <td class="tablecolumnheader" sortable="no">
            
        </td> 
    </tr>
</thead> 
<tbody>
    <yfc:loopXML name="AuditList" binding="xml:/AuditList/@Audit" id="ItemAudit" > 

        <%
		//System.out.println("getAudit "+ItemAudit);
		String divToDisplay = "yfsOrderAudit_" + ItemAuditCounter; %>
        <tr>
            <td class="tablecolumn" sortValue="<%=ItemAuditCounter%>">
                <%=ItemAuditCounter %>
				<img id="<%=divToDisplay+"_img"%>" onclick="expandCollapseDetails('<%=divToDisplay%>','<%=getI18N("Click_To_Expand_Audit_Info")%>','<%=getI18N("Click_To_Collapse_Audit_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')" style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_Expand_Audit_Info")%> />
            </td>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:ItemAudit:/Audit/@Createts")%>">
                <yfc:getXMLValue name="ItemAudit" binding="xml:ItemAudit:/Audit/@Createts"/>
            </td>
            <td class="tablecolumn">
                <%
                    String sUser = resolveValue("xml:ItemAudit:/Audit/@CreateUserName");
                    if(isVoid(sUser) )
                        sUser = resolveValue("xml:ItemAudit:/Audit/@Createuserid");
                %>
                <%=sUser%>
            </td>
            <td class="tablecolumn">
                <yfc:hasXMLNode binding="xml:/OrderAudit/@ReasonCode">
                    <%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderAudit/@ReasonCode",true)%>
                    <br>
                </yfc:hasXMLNode>
                <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@ReasonText"/>
            </td>
            <td class="tablecolumn">
                <% /* Loop through all modification levels of all the levels, but only display unique set */ %>
                <% /* Slightly non-standard formatting in the loop to get the comma to look right */ %>
                <% HashSet modLevelSet = new HashSet(); %>
                <% String comma = ""; %>
                <yfc:loopXML binding="xml:ItemAudit:/Audit/AuditDetail/Attributes/@Attribute" id="ItemAuditAttribute"><%
                    String modLevel = resolveValue("xml:/ItemAuditAttribute/@DataType");
                    if( ! isVoid( modLevel ) && modLevelSet.add( modLevel ) ) {
                        %><%=comma%> <%=modLevel%><%
                        if( "".equals(comma) ) {
                            comma = getI18N(",");
                        }
                    }
                %></yfc:loopXML>
            </td>
            <td class="tablecolumn">
                <% /* Loop through all modification types of all the levels, but only display unique set */
                   /* Display a maximum of MAX items, followed by an "..." if there are more. */
                   HashSet modTypeSet = new HashSet();
                   int MAX = 3;
                   int count = 1;
                %>
                <yfc:loopXML binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel">
                    <yfc:loopXML binding="xml:/OrderAuditLevel/ModificationTypes/@ModificationType" id="ModificationType" > 
                        <%
                        String modType = resolveValue("xml:/ModificationType/@ScreenName");
                        if( modTypeSet.add( modType ) ) { /* Only display the type if it's not already there */
                            if( count <= MAX ) {
                                if( count > 1 ) {
                                    %><br/><%             /* Only put in a line break once something has been written */
                                }
                                %><%=modType%><%          /* Output the type */
                            } else if( count == MAX+1 ) { /* Only display "..." if there really are more records */
                                %><yfc:i18n>...</yfc:i18n><%
                            }
                            count++;
                        }
                        %>
                    </yfc:loopXML> 
                </yfc:loopXML>
            </td>
        </tr>
        <tr id="<%=divToDisplay%>" style="display:none" BypassRowColoring="true">
            <td class="tablecolumn" colspan="6">
                <%
                    YFCElement auditElem = (YFCElement)pageContext.getAttribute("Audit");
                    request.setAttribute("Audit", auditElem);

					String hideLineInfo = request.getParameter("HideLineInfo");
					if(!equals("Y", hideLineInfo)) {
						hideLineInfo = "N";
					}
					//System.out.println("setting up");
					request.setAttribute("ItemAuditAttribute",ItemAudit);

                %>
                <jsp:include page="/extn/audit/nwcg_new_audit_list_expand.jsp" flush="true">
					<jsp:param name="HideLineInfo" value="<%=hideLineInfo%>"/>
					<jsp:param name="IgnoreList" value="<%=strParam%>"/>
		        </jsp:include>
                &nbsp;
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
