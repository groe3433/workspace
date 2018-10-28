<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/workorder.js"></script>

<script>
    function checkNodeUser()
    {
        /*if("<%=isShipNodeUser()%>"=="false"){
            alert('<%=getI18N("Only_Node_User_can_perform_this_Operation")%>');
            return false;
        }*/
        return true;
    }
    function showMoveRequestListSearch(sViewID,sChkName)
    {   
        var entity="wmsmoverequest";
        var sAddnParams = "";
        var eleArray = document.getElementsByName(sChkName);
        if(yfcAllowSingleSelection(sChkName))
        {
           for ( var i =0; i < eleArray.length; i++ ) 
            {
               if(eleArray.item(i).checked)
                {
                   var MoveRequestNodeKey = eleArray[i].getAttribute("NodeKey");    
                   var MoveRequestEnterpriseCode = eleArray[i].getAttribute("EnterpriseCode");
				   var MoveRequestNo = eleArray[i].getAttribute("WorkOrderNo");
                   var sAddnParams = "&xml:yfcSearchCriteria:/MoveRequest/@Node="+MoveRequestNodeKey;
                    sAddnParams = sAddnParams + "&xml:yfcSearchCriteria:/MoveRequest/MoveRequestLines/MoveRequestLine/@EnterpriseCode="+MoveRequestEnterpriseCode;
                    sAddnParams = sAddnParams + "&xml:/MoveRequest/WorkOrder/@WorkOrderNo="+MoveRequestNo;
                }
            }
           yfcShowListPopupWithParams(sViewID,"",'900', '550','',entity, sAddnParams);
        }else
        {
            return false;
        }
    }
    function showTaskListSearch(sViewID,sChkName)
    {   
        var entity="task";
        var sAddnParams = "";
        var eleArray = document.getElementsByName(sChkName);
        if(yfcAllowSingleSelection(sChkName))
        {
           for ( var i =0; i < eleArray.length; i++ ) 
            {
               if(eleArray.item(i).checked)
                {
                   var TaskNodeKey = eleArray[i].getAttribute("NodeKey");   
                   var TaskEnterpriseCode = eleArray[i].getAttribute("EnterpriseCode");             
                   var taskRefNo = eleArray[i].getAttribute("WorkOrderNo");      
                   var taskRefKey = eleArray[i].getAttribute("WorkOrderKey");        
                   var sAddnParams = "&xml:/Task/@Node="+TaskNodeKey;                   
                   sAddnParams = sAddnParams + "&xml:/Task/TaskReferences/@WorkOrderKey="+taskRefKey ;
                }
            }
           yfcShowListPopupWithParams(sViewID,"",'900', '550','',entity, sAddnParams);
        }else
        {
            return false;
        }
    }
    function enterActionCancellationReason(cancelReasonViewID, cancelReasonCodeBinding, cancelReasonTextBinding, sChkName) {
        var eleArray = document.getElementsByName(sChkName);
        var getReasonCode = "N";
       for ( var i =0; i < eleArray.length; i++ ) 
        {
           if(eleArray.item(i).checked)
            {
                getReasonCode = "Y"
            }
        }
        if(getReasonCode == "Y"){
            var myObject = new Object();
            myObject.currentWindow = window;
            myObject.reasonCodeInput = document.all(cancelReasonCodeBinding);
            myObject.reasonTextInput = document.all(cancelReasonTextBinding);       
            
            yfcShowDetailPopup(cancelReasonViewID, "", "550", "255", myObject, "workorder", "<WorkOrder />");

            var returnValue = myObject["OKClicked"];
            if ( "YES" == returnValue ) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }

    }
    function deleteCheckedAndRefresh()
    {
        return true;
    }

</script>
    <Input type="hidden" name="xml:/WorkOrder/@ModificationReasonCode" />
    <Input type="hidden" name="xml:/WorkOrder/@ModificationReasonText" />
	<Input type="hidden" name="xml:/WorkOrder/@ServiceItemID" />
<%  

    YFCElement organizationInput = null;
    Hashtable enterpriseInvOrg = new Hashtable();

    organizationInput = YFCDocument.parse("<Organization/>").getDocumentElement();

    YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization  OrganizationCode=\"\" InventoryOrganizationCode=\"\"/> </OrganizationList>").getDocumentElement(); 
%>
    <yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

    <yfc:loopXML binding="xml:OrganizationList:/OrganizationList/@Organization" id="Organization">
        <% String key = resolveValue("xml:/Organization/@OrganizationCode");
            String value = resolveValue("xml:/Organization/@InventoryOrganizationCode");
            enterpriseInvOrg.put(key, value);
        %>
    </yfc:loopXML>
<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
                <input type="hidden" name="xml:/WorkOrder/@Override" value="N"/>
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
            <!-- Top of CR 591 -->
	    <td class="tablecolumnheader" ><yfc:i18n>Description</yfc:i18n></td>
            <!-- Bottom of CR 591 -->
	    <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
	    <!-- added for CR 536 -->
	    <td class="tablecolumnheader"><yfc:i18n>Requested Qty</yfc:i18n></td>
	    <td class="tablecolumnheader"><yfc:i18n>Confirmed Qty</yfc:i18n></td>
	    <td class="tablecolumnheader"><yfc:i18n>Creation Date</yfc:i18n></td>
	    <!-- added for CR 536 ends here-->
            <td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>
	    <!-- Top of CR 591
            <td class="tablecolumnheader"><yfc:i18n>Segment_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Segment_#</yfc:i18n></td>
            -- Bottom of CR 591 -->
            <td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/WorkOrders/@WorkOrder" id="WorkOrder">
            <tr>
                <yfc:makeXMLInput name="WorkOrderKey">
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@NodeKey" value="xml:/WorkOrder/@NodeKey" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@EnterpriseCode" value="xml:/WorkOrder/@EnterpriseCode" />
                    <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderNo" value="xml:/WorkOrder/@WorkOrderNo" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/@ServiceItemID" value="xml:/WorkOrder/@ServiceItemID" />
                </yfc:makeXMLInput> 
                <% String enterpriseCode = resolveValue("xml:/WorkOrder/@EnterpriseCode");
				%>
				
                <td class="checkboxcolumn">                     
                    <input type="checkbox" value='<%=getParameter("WorkOrderKey")%>' name="EntityKey" isHistory='<%=getValue("WorkOrder","xml:/WorkOrder/@isHistory")%>'
                    NodeKey='<%=resolveValue("xml:/WorkOrder/@NodeKey")%>' 
                    EnterpriseCode='<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>' 
                    EnterpriseInvOrg='<%=enterpriseInvOrg.get(enterpriseCode)%>' 
                    WorkOrderNo='<%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%>' 
                    WorkOrderKey='<%=resolveValue("xml:/WorkOrder/@WorkOrderKey")%>'
					ServiceItemID='<%=resolveValue("xml:/WorkOrder/@ServiceItemID")%>'
                    />
                </td>

				
                <td class="tablecolumn">
                    <% if(equals("Y", getValue("WorkOrder","xml:/WorkOrder/@isHistory") )){ %>
                        <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                    <%}else if (equals("REFURBISHMENT" , resolveValue("xml:/WorkOrder/@ServiceItemID")) && equals("KIT", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))) {%>
					<a href="javascript:showDetailForViewGroupId('workorder','NWRYVSD001','<%=getParameter("WorkOrderKey")%>');">
                            <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                        </a>
                    <%} else if (equals("REFURB-DEKITTING" , resolveValue("xml:/WorkOrder/@ServiceItemID")) && equals("DKIT", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))){%>
					<a href="javascript:showDetailForViewGroupId('workorder','NWCYVSD001','<%=getParameter("WorkOrderKey")%>');">
                            <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                        </a>
					<% } else {%>
					<a href="javascript:showDetailFor('<%=getParameter("WorkOrderKey")%>');">
                        <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                        </a>
				<% } %>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@EnterpriseCode"/></td>

               <td class="tablecolumn">                 
                    <%=getComboText("xml:ServiceItemGroupList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/WorkOrder/@ServiceItemGroupCode", true)%>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@ItemID"/></td>
		<!-- Top of CR 591 -->
		<td class="tablecolumn">
		        <%
			YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\"><PrimaryInformation Description=\"\" ShortDescription=\"\"/></Item>").getDocumentElement();
			YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement(); 
			oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/WorkOrder/@EnterpriseCode"));
			oItemDetailsElement.setAttribute("Node",resolveValue("xml:/WorkOrder/@NodeKey"));
			oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/WorkOrder/@ItemID"));
			oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/WorkOrder/@Uom"));	
			%>
			<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>
			<yfc:getXMLValue binding="xml:ItemDetails:/Item/PrimaryInformation/@ShortDescription"/>
		</td>
		<!-- Bottom of CR 591 -->
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Uom"/></td>
				<!-- added for CR 536 -->
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityRequested"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityCompleted"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@StatusDate"/></td>
				<!-- added for CR 536 ends here-->
               <td class="tablecolumn">                 
                    <%=getComboText("xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/WorkOrder/@Priority", true)%>
                </td>
		<!-- Top of CR 591
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@SegmentType"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Segment"/>
                </td>
		-- Bottom of CR 591 -->
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/OrderLine/Order/@OrderNo"/></td>
                <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/WorkOrder/Status/@StatusName"/>
                    <% if (isTrue("xml:/WorkOrder/@HoldFlag") )   { %>
    					<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held")%>/>
    				<% } %>
                </td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>