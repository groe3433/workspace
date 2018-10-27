<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script>
function processSaveRecordsForChildNode() {
   var addRow = window.document.getElementById("userOperation");
    var numRowsToAdd = window.document.getElementById("numRowsToAdd");
    if(addRow)
    {
        if(addRow.value != 'Y')
        {
            //reset numRowsToAdd attribute
            if(numRowsToAdd)
                numRowsToAdd.value="";
            yfcSpecialChangeNames("specialChange", false);
        }
    }
    else
        yfcSpecialChangeNames("specialChange", false);
}
</script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>
<% String height = request.getParameter("height"); 
String currentSerialCounter = request.getAttribute("SerialCounter").toString();%>
<%int currentCounter = Integer.parseInt(currentSerialCounter);%>
<%double numSecondarySerials=getNumericValue("xml:ItemDetails:/Item/PrimaryInformation/@NumSecondarySerials");
String tagControlledFlag = resolveValue("xml:ItemDetails:/Item/InventoryParameters/@TagControlFlag");
//System.out.println("tagControlledFlag:" + tagControlledFlag);
%>
<div style="height:120px;overflow:auto">
<table class="table" ID="specialChange" cellspacing="0" width="100%" >
    <thead>
        <tr>
            <td class="checkboxheader" sortable="no">&nbsp;
				<input type="hidden" id="userOperation" name="userOperation" value="" />
				<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
				<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@SerialNo")%>"><yfc:i18n>Serial_#</yfc:i18n></td>
	
		   <% for (int i=1; i < numSecondarySerials+1 ; i++){
			  String serLabel= "Secondary_Serial_" + i;
			%>
			<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@SecondarySerial"+i)%>"><yfc:i18n><%=serLabel%></yfc:i18n></td>
			<%}%>
        </tr>
    </thead>
    <tbody>

<!-- cr 102 ks -->
<% if (tagControlledFlag.equals("Y")) { %>
<tr>
			<td class="checkboxcolumn"> 
				<img class="icon" onclick="setDeleteOperationForRow(this,'xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail')" <%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%>/>
			</td>
			<td nowrap="true" class="tablecolumn">
				<input type="hidden"  <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@DeleteRow",  "")%> />
				<input type="hidden"  <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@Action","xml:/SerialDetail/@Action","CREATE")%> />
				<input type="text" maxLength="20" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@SerialNo","xml:/SerialDetail/@SerialNo")%>/>
				<input type="hidden" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@Quantity","xml:/SerialDetail/@Quantity","1")%>/> 
			</td>
			<td nowrap="true" class="tablecolumn">
				<!-- cr 426 -->
				<input type="text" maxLength="20" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail/@SecondarySerial1","xml:/SerialDetail/@SecondarySerial1")%>/>
				<!-- cr 426 end--> 
			</td>
<% } else { %>


<yfc:loopXML name="AdjustLocationInventory" binding="xml:/AdjustLocationInventory/Source/Inventory/SerialList/@SerialDetail" id="SerialDetail" > 
<%	if(YFCCommon.isVoid(resolveValue("xml:SerialDetail:/SerialDetail/@Quantity")) || (1 == getNumericValue("xml:SerialDetail:/SerialDetail/@Quantity"))){ %>
		<tr DeleteRowIndex="<%=SerialDetailCounter%>">
		<%int serialCounter = SerialDetailCounter.intValue();
            String newCounter = String.valueOf(serialCounter+(currentCounter-1)); %>
			<td class="checkboxcolumn"> 
				<img class="icon" onclick="setDeleteOperationForRow(this,'xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail')" <%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%>/>
			</td>
			<td nowrap="true" class="tablecolumn">
				<input type="hidden"  <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_"+newCounter+"/@DeleteRow",  "")%> />
				<input type="hidden"  <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_"+newCounter+"/@Action","xml:/SerialDetail/@Action","CREATE")%> />
				<input type="text" maxLength="20" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + newCounter +"/@SerialNo","xml:/SerialDetail/@SerialNo")%>/>
				<input type="hidden" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + newCounter  +"/@Quantity","xml:/SerialDetail/@Quantity","1")%>/> 
			
			</td>
		   <% for (int i=1; i < numSecondarySerials+1 ; i++){
			  String serLabel= "Secondary_Serial_" + i;
			%>
			<td nowrap="true" class="tablecolumn">
				<!-- cr 426 -->
				<input type="text" maxLength="20" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + newCounter  +"/@SecondarySerial"+i,"xml:/SerialDetail/@SecondarySerial"+i)%>/>
				<!-- cr 426 end --> 
			</td>
			<%}%>
<%	}	%>
</yfc:loopXML>
<% } %>
<!-- end cr 102 ks -->

    </tbody>
	<tfoot>
        <tr style='display:none' TemplateRow="true">
            <td class="checkboxcolumn">
            </td>
            <td class="tablecolumn" nowrap="true">
                <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + currentSerialCounter + "/@SerialNo","")%>/>
				  <input type="hidden" name="<%=buildBinding("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_", currentSerialCounter,"/@Quantity")%>" value="1"/>
            </td>
	        <% for (int i=1; i < numSecondarySerials+1 ; i++){
			  String serLabel= "Secondary_Serial_" + i;
			%>
			<td class="tablecolumn" nowrap="true">
                <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + currentSerialCounter + "/@SecondarySerial"+i,"")%>/>
            </td>
			<%}%>
        </tr>
        <tr>
        	<td nowrap="true" colspan="5">
        		<jsp:include page="/extn/wms/wmsadjustinventory/detail/nwcg_editabletbl.jsp" flush="true">
					<jsp:param name="ReloadOnAddLine" value="Y"/>
        		</jsp:include>
        	</td>
        </tr>
    </tfoot>
</table>
</div>
