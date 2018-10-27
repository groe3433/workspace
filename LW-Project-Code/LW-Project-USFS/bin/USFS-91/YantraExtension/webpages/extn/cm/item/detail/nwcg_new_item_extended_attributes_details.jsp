<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view" width="100%">
<tr>
<td>
<%
// get the item
YFCElement elemItem = (YFCElement)request.getAttribute("Item");
// get the Extn element
YFCNodeList nlItem = elemItem.getElementsByTagName("Extn");
if(nlItem != null && nlItem.getLength() > 0)
{
	YFCNode elemExtn = nlItem.item(0);
	// get all the attributes
	java.util.Map mapAttributes = elemExtn.getAttributes();
	// get the entry set
	java.util.Set set = mapAttributes.entrySet();
	// get the iterator
	java.util.Iterator itr = set.iterator();
	// counter to keep track of td within tr - HTML
	int iCounter = 0 ;
	
	while(itr.hasNext())
	{
		java.util.Map.Entry entry = (java.util.Map.Entry) itr.next();
		// allow only 4 labels within one row (tr)
		if(iCounter >= 4)
		{
			iCounter = 0;%>
			</tr>

		<%}
		if(iCounter == 0)
		{%>
			<tr>
		<%}
		String strKey = (String)entry.getKey();
		// these attributes are alredy covered under Hazadrous Panel
		if(strKey.equals("IATAHazardClass") || strKey.equals("IATAPackingGroup") || strKey.equals("IATAProperShippingName") || strKey.equals("IATAUNNumber") ||
		strKey.equals("PreferenceCriterion") )
		{
			continue ;
		}
		else
		{%>
		
		<!-- print the attributes-->
		<td class="detaillabel" ><yfc:i18n><%=strKey%></yfc:i18n></td>
		<td class="protectedtext"><%=entry.getValue()%></td>
		<%}

		// atlast increase the counter
		iCounter++ ;
	}
}%>
</td>
</tr>
</table>