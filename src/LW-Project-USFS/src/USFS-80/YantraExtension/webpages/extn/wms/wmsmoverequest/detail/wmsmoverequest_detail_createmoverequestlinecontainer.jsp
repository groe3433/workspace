<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table">
<thead>
    <tr> 
		<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>	
		<td class="tablecolumnheader">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>	
		<td class="tablecolumnheader">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>          
    </tr>
</thead>
<tbody>	
	 <yfc:loopXML binding="xml:/MoveRequest/MoveRequestLines/@MoveRequestLine" id="MoveRequestLine">
    <tr>	
		<td class="checkboxcolumn">
			<input type="checkbox" value="" name="chkEntityKey" />
		</td>
		 <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@PalletId"/>
        </td>
		<td class="tablecolumn">
			<yfc:getXMLValue binding="xml:/MoveRequestLine/@CaseId"/>
		</td>               
    </tr>	
	</yfc:loopXML>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">				
       <td class="checkboxcolumn" >
	   &nbsp;
       </td>
        <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_10000/@PalletId")%>/>			
        </td>		
       <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_10000/@CaseId")%>/>
       </td>
	</tr>
    <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
    		</jsp:include>
    	</td>
    </tr>
</tfoot>
</table>