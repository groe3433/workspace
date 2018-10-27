<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.shared.ycm.*" %>

<script language="javascript">
    yfcDoNotPromptForChanges(true);
</script>

    
<%  YFCElement root1 = getRequestDOM();
    YFCElement workOrderElem = root1.getChildElement("WorkOrder");
    if(workOrderElem.hasAttribute("ServiceItemGroupCode")){
        workOrderElem.setAttribute("comingThroughDOM", "Y");
        request.setAttribute("WorkOrder",workOrderElem);
    }
    YFCElement root = (YFCElement)request.getAttribute("WorkOrder");

    String sEntity="workorder";
    String ItemId=resolveValue("xml:/WorkOrder/@ItemID");
    String ServiceItemGroupCode=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode");
    String Uom=resolveValue("xml:/WorkOrder/@Uom");
    String EnterpriseCode=resolveValue("xml:/WorkOrder/@EnterpriseCode");
    String WorkOrderKey=resolveValue("xml:WO:/WorkOrder/@WorkOrderKey");
    String WorkOrderNo=resolveValue("xml:WO:/WorkOrder/@WorkOrderNo");

    if((!isVoid(WorkOrderNo))&&(!isVoid(WorkOrderKey))){
    
        YFCDocument workOrderDoc = YFCDocument.createDocument("WorkOrder");
        workOrderDoc.getDocumentElement().setAttribute("WorkOrderKey",resolveValue("xml:WO:/WorkOrder/@WorkOrderKey"));
        workOrderDoc.getDocumentElement().setAttribute("NodeKey",resolveValue("xml:WO:/WorkOrder/@NodeKey"));
        workOrderDoc.getDocumentElement().setAttribute("EnterpriseCode",resolveValue("xml:WO:/WorkOrder/@EnterpriseCode"));
        workOrderDoc.getDocumentElement().setAttribute("WorkOrderNo",resolveValue("xml:WO:/WorkOrder/@WorkOrderNo"));
  
%>
        <script>
        function changeToWorkOrderDetailView() {
            entityType = '<%=sEntity%>';
            //showDetailFor('<%=workOrderDoc.getDocumentElement().getString(false)%>');
			showDetailForViewGroupId('workorder', 'NWGYVSD001','<%=workOrderDoc.getDocumentElement().getString(false)%>');
        }
        window.attachEvent("onload", changeToWorkOrderDetailView);
        </script>

<%}
    String involvesSegmentChange = "N";

    String sSOME_TIME_TAG_CTRL = YCMConstants.YCM_ITEM_TAG_SOMETIMES_TAG_CONTROLLED;
    String sTAG_CTRL = YCMConstants.YFS_YES;
    String sNOT_TAG_CTRL = YCMConstants.YFS_NO;

    String itemTagControlFlag = resolveValue("xml:ItemDetailsTagControl:/Item/@TagCapturedInInventory");
    if(equals(sSOME_TIME_TAG_CTRL, itemTagControlFlag) || equals(sTAG_CTRL, itemTagControlFlag)){
        itemTagControlFlag = sTAG_CTRL;
    }else{
        itemTagControlFlag = sNOT_TAG_CTRL;
    }
    if(!isVoid(root)){
        root.setAttribute("ItemTagControlFlag", true);
        if(equals(sNOT_TAG_CTRL, itemTagControlFlag)){
            root.setAttribute("ItemTagControlFlag", false);
        }
    }

    if(ServiceItemGroupCode.equalsIgnoreCase("COMPL")){
        YFCElement itemDetailsInput = null;
        itemDetailsInput = YFCDocument.parse("<Item ItemID=\"" + resolveValue("xml:/WorkOrder/@ServiceItemID") + "\" CallingOrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" />").getDocumentElement();

        YFCElement itemDetailsTemplate = YFCDocument.parse("<ItemList><Item> <PrimaryInformation InvolvesSegmentChange=\"\"/> </Item></ItemList>").getDocumentElement(); 
    %>
        <yfc:callAPI apiName="getItemList" inputElement="<%=itemDetailsInput%>" templateElement="<%=itemDetailsTemplate%>" outputNamespace="ItemDetails"/>
    <%    
        involvesSegmentChange = resolveValue("xml:ItemDetails:/ItemList/Item/PrimaryInformation/@InvolvesSegmentChange");
    }%>

<input type="hidden" name="xml:/WorkOrder/@EnterpriseCode" value='<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>'/>
<table cellSpacing=0 class="anchor" cellpadding="7px">
    <tr>
        <td colspan="2">
            <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                <jsp:param name="CurrentInnerPanelID" value="I01"/>
            </jsp:include>
        </td>
    </tr>
    <% String modifyView = "";
        modifyView = WorkOrderKey == null ? "" : WorkOrderKey;
    if(ServiceItemGroupCode.equalsIgnoreCase("KIT") ||
        ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
        ServiceItemGroupCode.equalsIgnoreCase("INVC")|| 
        (ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (involvesSegmentChange.equalsIgnoreCase("Y"))) ||
        (!equals("",ItemId.trim()))){
        if(modifyView != ""){%>
            <tr>
                <td width="50%" height="100%">
                    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                        <jsp:param name="CurrentInnerPanelID" value="I02"/>
                    </jsp:include>
                </td>
                <td width="50%" height="100%">
                    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                        <jsp:param name="CurrentInnerPanelID" value="I07"/>
                    </jsp:include>
                </td>
            </tr>
        <%}else if(equals("COMPL", ServiceItemGroupCode)){
            if(equals("Y", involvesSegmentChange)){%>
                <tr>
                    <td colspan="2">
                        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                            <jsp:param name="CurrentInnerPanelID" value="I02"/>
                        </jsp:include>
                    </td>
                </tr>
            <%}
        }else{%>
            <tr>
                <td colspan="2">
                    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                        <jsp:param name="CurrentInnerPanelID" value="I02"/>
                    </jsp:include>
                </td>
            </tr>
        <%}
    }
    if (ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
        (ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (!involvesSegmentChange.equalsIgnoreCase("Y")))){%>
        <tr>
            <td colspan="2">
                <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                    <jsp:param name="CurrentInnerPanelID" value="I05"/>
                </jsp:include>
            </td>
        </tr>
    <%}
    if(ServiceItemGroupCode.equalsIgnoreCase("KIT") ||
        ServiceItemGroupCode.equalsIgnoreCase("DKIT") ||
        ServiceItemGroupCode.equalsIgnoreCase("INVC")|| 
        (ServiceItemGroupCode.equalsIgnoreCase("COMPL") && (involvesSegmentChange.equalsIgnoreCase("Y")))){%>
        <tr>
            <%if(modifyView != ""){%>
                <td width="50%" height="100%">
                    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                        <jsp:param name="CurrentInnerPanelID" value="I06"/>
                    </jsp:include>
                </td>
            <%} else{%>
                <td width="50%" height="100%">
                    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                        <jsp:param name="CurrentInnerPanelID" value="I03"/>
                    </jsp:include>
                </td>
            <%}%>
            <td width="50%" height="100%">
                <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                    <jsp:param name="CurrentInnerPanelID" value="I04"/>
                </jsp:include>
            </td>
        </tr>
    <%}else{%>
        <tr>
            <td>
                <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                    <jsp:param name="CurrentInnerPanelID" value="I04"/>
                </jsp:include>
            </td>
        </tr>
    <%}%>
    <tr>
        <td colspan="6" align="center">
            <input type="hidden" <%=getTextOptions("xml:/WorkOrder/@WorkOrderMode","xml:/WorkOrder/@WorkOrderMode","menu")%>  />
        </td>
    </tr>
</table>
