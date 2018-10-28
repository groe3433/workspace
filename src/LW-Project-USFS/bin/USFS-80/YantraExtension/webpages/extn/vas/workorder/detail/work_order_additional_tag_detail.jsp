<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%  
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String createView = resolveValue("xml:/WorkOrder/@WorkOrderMode");
    createView = createView == null ? "" : createView;

%>

<table class="view" width="100%">
<% if(modifyView == "" ){%>
   <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
    <tr>
		<td>
			<fieldset>
				<jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
	                <jsp:param name="NoOfColumns" value='3'/>
	                <jsp:param name="Modifiable" value='true'/>
	                <jsp:param name="LabelTDClass" value='detaillabel'/>
	                <jsp:param name="InputTDClass" value='detail'/>
	                <jsp:param name="BindingPrefix" value='xml:/WorkOrder/WorkOrderTag'/>
	            </jsp:include>
	      	</fieldset>
		</td>
    </tr>
    </yfc:hasXMLNode>
<%}else{%>
    <tr>
		<td>
			<fieldset>
				<jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
	                <jsp:param name="NoOfColumns" value='3'/>
	                <jsp:param name="Modifiable" value='false'/>
	                <jsp:param name="LabelTDClass" value='detaillabel'/>
	                <jsp:param name="InputTDClass" value='detail'/>
					<jsp:param name="TagContainer" value="WorkOrder"/>
					<jsp:param name="TagElement" value="WorkOrderTag"/>
				</jsp:include>
	      	</fieldset>
		</td>
    </tr>
<%}%>
</table>