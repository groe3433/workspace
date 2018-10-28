<%@ include file="/yfsjspcommon/yfsutil_mb.jspf" %>
<%@ include file="/console/jsp/modificationutils_mb.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools_mb.js"></script>
<script language="javascript">
    function showAddCommentPopup(viewId) {
        return doEMPopupDialog('<%=getDefaultDetailViewForGroup("YEMD013")%>',600,440);
    }
</script>

<table class="table" width="100%" >
<thead> 
    <tr> 
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Comment/@InstructionText")%>">
            <yfc:i18n>Comment_Text</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Comment/@Createts")%>">
            <yfc:i18n>Logged_On</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Logged_By</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody> 
  	<yfc:loopXML  binding="xml:/Inbox/InboxReferencesList/@InboxReferences" id="InboxReferences"> 
  		<%String referenceType = getValue("InboxReferences","xml:/InboxReferences/@ReferenceType");%>
    	<%String referenceName = getValue("InboxReferences","xml:/InboxReferences/@Name");%>
    	<%String referenceValue = getValue("InboxReferences","xml:/InboxReferences/@Value");%>
    	<%String createts = getValue("InboxReferences","xml:/InboxReferences/@Createts");%>
    	<%String createuserid = getValue("InboxReferences","xml:/InboxReferences/@Createuserid");%>
    	<%if ( "COMMENT".equals(referenceType) ) {%>
      		<tr> 
            	<td class="tablecolumn"><%=referenceValue%></td>
            	<td class="tablecolumn" sortValue="<%=createts%>" ><%=createts%></td>
            	<td class="tablecolumn"><%=createuserid%></td>
      		</tr>
    	<%}%>
 	</yfc:loopXML> 

    <yfc:loopXML  binding="xml:/Inbox/Comments/@Comment" id="Comment"> 
    <tr> 
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/Comment/@InstructionText"/>
        </td>
        <td class="tablecolumn" sortValue="<%=getDateValue("xml:Comment:/Comment/@Createts")%>">
            <yfc:getXMLValue binding="xml:/Comment/@Createts"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/Comment/@Createuserid"/>
        </td>
    </tr>
    </yfc:loopXML> 
</tbody> 
</table>
