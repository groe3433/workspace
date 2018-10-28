<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<% String mode= "" ;%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" height="300" width="100%">

<%  mode=resolveValue("xml:yfcSearchCriteria:/NodeInventory/@Mode");
	if(!isVoid(mode)){%>

<yfc:callAPI apiID='AP1'/>
<%
    YFCElement locationInventoryElement = (YFCElement)getElement("NodeInventory").getFirstChild();
	int countElem = countChildElements(locationInventoryElement);
%>
<tr><td class="pagereccountCustom">Retrieved&nbsp;<%=countElem%>&nbsp;record(s)</td></tr>
<div style="overflow:auto">
 <tr height="70%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>


        </jsp:include>
    </td>
</tr>
</div>
<div style="overflow:auto">
<tr height="30%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>


        </jsp:include>
    </td>
</tr>
</div>
<% } else %> 

<% mode=resolveValue("xml:yfcSearchCriteria:/GetLPNDetails/@Mode");
   if(!isVoid (mode) ){%>
   <yfc:callAPI apiID='AP3'/>
   <%
	YFCElement pLPN= (YFCElement) request.getAttribute("GetLPNDetails");
	YFCElement cLPN=null;
	YFCElement pcLPN=null; 
	YFCElement ccLPN=null; 
	if(pLPN!=null){
		 cLPN =(YFCElement) pLPN.getChildElement("LPN");
			if(cLPN!=null){
				pcLPN =(YFCElement) cLPN.getChildElement("ItemInventoryDetailList");
				if(pcLPN!=null){
					ccLPN =(YFCElement) pcLPN.getChildElement("ItemInventoryDetail");
				   if(ccLPN==null){
					   pcLPN.setAttribute("Visible","N");
				   }else{
						pcLPN.setAttribute("Visible","Y");
				   }
				}
			}
		}
	%>
 <tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
  
        </jsp:include>
    </td>
 </tr>
 <tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
          </jsp:include>
    </td>
 </tr>
<% } else %> 

<% mode=resolveValue("xml:/Receipt/@ReceiptNo");
	if(!isVoid (mode) ){%>
<yfc:callAPI apiID='AP2'/>

<div style="overflow:auto">
 <tr height="70%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>


        </jsp:include>
    </td>
</tr>
</div>
 <div style="overflow:auto">
<tr height="30%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>


        </jsp:include>
    </td>
</tr>
</div>
<% } %> 
</table>