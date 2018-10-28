<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<yfc:callAPI apiID="AP1"/>

<%
	YFCElement root = getElement("IB");
	int countElem = countChildElements(root);
%>

<script language="javascript">
	setRetrievedRecordCount(<%=countElem%>);
</script>



   <table class="table" editable="false" width="100%" cellspacing="0">
       <thead> 
           <tr>
               <td sortable="no" class="checkboxheader">
                   <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
               </td>

               <td class="tablecolumnheader"><yfc:i18n>Distribution_ID</yfc:i18n></td>
               <td class="tablecolumnheader"><yfc:i18n>Initial_Message_Received</yfc:i18n></td>
               <td class="tablecolumnheader"><yfc:i18n>Message_Type</yfc:i18n></td>
               <td class="tablecolumnheader"><yfc:i18n>Message_Status</yfc:i18n></td>
               <td class="tablecolumnheader"><yfc:i18n>Message_Name</yfc:i18n></td>
               <td class="tablecolumnheader"><yfc:i18n>Entity_Value</yfc:i18n></td>

           </tr>   
       </thead>

   	<tbody>
   		<yfc:loopXML binding="xml:IB:/NWCGInboundMessageList/@NWCGInboundMessage" id="NWCGInboundMessage">
   		<tr>
		  <yfc:makeXMLInput name="messageKey">
			<yfc:makeXMLKey binding="xml:IB:/NWCGInboundMessage/@MessageKey" value="xml:/NWCGInboundMessage/@MessageKey" />
		  </yfc:makeXMLInput>     
		  


		  <td class="checkboxcolumn">                     
			<input type="checkbox" value='<%=getParameter("messageKey")%>' name="EntityKey" />
		  </td>

		<!--<td class="tablecolumn">
			<a href="javascript:showDetailFor('<%=getParameter("messageKey")%>');">
			<yfc:getXMLValue binding="xml:/NWCGInboundMessage/@DistributionID"/>
			</a>
		</td>-->

		<td class="tablecolumn">
			<a href="javascript:showDetailForViewGroupId('NWCG_Message_Store','MSGSTRD010','<%=getParameter("messageKey")%>');">
			<yfc:getXMLValue binding="xml:/NWCGInboundMessage/@DistributionID"/>
			</a>
		</td>
<%
		//Code to display date and time of createts
		String strDate = NWCGInboundMessage.getAttribute("Createts") ;
		java.text.SimpleDateFormat sdfYantra = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		//08/12/2009 15:19:12
		java.text.SimpleDateFormat sdfUI = new java.text.SimpleDateFormat("MM/dd/yyyy  HH:mm:ss");
		Date dateYantra = sdfYantra.parse(strDate);
		String strDateUI = sdfUI.format(dateYantra) ;
		%>
		<td class="tablecolumn" sortValue="<%=strDateUI%>"><%=strDateUI%></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageType"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageStatus"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageName"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@EntityValue"/></td>


    	   	</tr>
   	    </yfc:loopXML>

   </tbody>
</table>