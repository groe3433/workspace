<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>

<% String URL = ResourceUtil.get("nwcg.icbs.training.elearning.url"); 
    %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/pm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 

<input type="hidden" <%=getTextOptions("xml:/SavedSearch/@URL",ResourceUtil.get("nwcg.icbs.training.elearning.url"))%>/>

<table class="table">
    <tr align="center" valign="middle">
       <td>
       <input type="button" class="button" value="E-Learning" onclick="javascript:goToURL('xml:/SavedSearch/@URL');"/>
       </td>
    </tr>
</table>


<%
    String savedSearchKey=getValue("SavedSearchList","xml:/SavedSearchList/SavedSearch/@SavedSearchKey");
    String entityName="";
%>


<% if (!isVoid(savedSearchKey) ) {%>
<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
    <tr> 
        <td sortable="no" class="checkboxheader">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            <input type="hidden" name="xml:/SavedSearch/@SavedSearchKey" />
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Saved_Search_Name</yfc:i18n>
        </td>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/SavedSearchList/@SavedSearch" id="SavedSearch">
        <tr>
			<yfc:makeXMLInput name="deleteSavedSearchKey" >
		        <yfc:makeXMLKey binding="xml:/SavedSearch/@SavedSearchKey" value="xml:/SavedSearch/@SavedSearchKey" />
	        </yfc:makeXMLInput>
			<td class="checkboxcolumn"> 
				<input type="checkbox" value='<%=getParameter("deleteSavedSearchKey")%>' name="savedSearchEntityKey"/>
	        </td>
            <td class="tablecolumn">
		    <%
			    entityName=getValue("SavedSearch","xml:/SavedSearch/@SearchContext");
		        savedSearchKey=getValue("SavedSearch","xml:/SavedSearch/@SavedSearchKey");
			%>
				<a href="javascript:yfcShowSavedSearchView('<%=entityName%>', '<%=savedSearchKey%>');">
					<yfc:getXMLValue name="SavedSearch" binding="xml:/SavedSearch/@SearchName"/>
				</a>
            </td>
        </tr>    
    </yfc:loopXML>
</tbody>
</table>
<%} else {%>
<table class="table">
    <tr align="center" valign="middle">
        <td>
            <yfc:i18n>You_have_not_created_any_favorite_searches</yfc:i18n>
        </td>
    </tr>
</table>
<%}%>

