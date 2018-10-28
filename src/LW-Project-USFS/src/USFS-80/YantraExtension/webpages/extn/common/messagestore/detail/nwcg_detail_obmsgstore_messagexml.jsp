<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils.js"></script>

<table class="view" width="100%" >
	<tr>
		<td width="100%">
        <textarea contenteditable='false' class="unprotectedtextareainput" rows="20" cols="100" style="word-wrap:break-word;"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@Message"/>
        </textarea>
    </tr>
</table>
