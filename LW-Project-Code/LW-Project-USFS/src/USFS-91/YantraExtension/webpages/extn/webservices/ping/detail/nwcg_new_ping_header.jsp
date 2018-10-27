<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<%
YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
String strUser = curUsr.getAttribute("Loginid");

%>
<script language="Javascript">

function rossResponse(elem,respDocument)
{
	getLabel().innerHTML="Response Received";
	document.getElementById('ProgressBar1').value = 60;
	var nodes =  respDocument.getElementsByTagName("ServicePingReq");
	if(nodes!=null && nodes.length > 0 )
	{
		for(i = 0 ; i < nodes.length  ; i++)
		{
			var ping = nodes(i);
			var strResult = ping.getAttribute("Result");
			if(strResult == "SUCCESS")
			{
				getLabel().innerHTML="SUCCESS" + " <br>[Timestamp :  " +ping.getAttribute("TimeStamp") + "]";
				getLabel().style.color="green";
			}
			else
			{
				getLabel().innerHTML=strResult + " <br>[Message : " + ping.getAttribute("TimeStamp") + "]";
				getLabel().style.color="red";
			}

		}
	}
	document.getElementById('ProgressBar1').value = 100;
	// reset cache object so that when user clicks on test connectivity button next time we call ROSS instead of reading from cache
	 cache = new Object();
}
function setPingServiceParam()
{
	getLabel().innerHTML="Sending Message to ROSS";
	getLabel().style.color="blue";
	document.getElementById('ProgressBar1').value = 10;
	var returnArray = new Object(); 
	returnArray["xml:UserId"] = '<%=strUser%>';
	return returnArray;
	document.getElementById('ProgressBar1').value = 20;
}

var myLabel ;

function getLabel()
{
	
	if(myLabel == null || myLabel == "null")
	{
		
		var myDiv = document.getElementById('status');
		myLabel = myDiv.getElementsByTagName("label")[0];
		return myLabel;
	}
	
	return myLabel;
	
}
</script>
<!-- Get Header Details-->
<table>
<td allign="left">
<table class="view" width="100%">
				<TBODY>
					<TR>
					<td class="detaillabel">
					<input type="button" value='Test Connectivity' name="Test Connectivity" onClick="fetchDataWithParams(this,'PingROSSWebservice',rossResponse,setPingServiceParam());"/>
					</td>
					<td>
					<object classid="clsid:35053A22-8589-11D1-B16A-00C0F0283628" id="ProgressBar1" height="20" width="400">
					    <param name="Min" value="0">
					    <param name="Max" value="100">
					    <param name="STYLE" value="background-color:green">
				</object>
					</td>
					</TR>
					<TR>
					<TD class="detaillabel">
					<label>Connectivity Test Status</label>
					</TD>
					<TD class="detaillabel">
					<Div id="status" style='color:blue'>
					<label>Inactive</label>
					</Div>
					</TD>
					</TR>
				</TBODY>
		</TABLE>
	</td>
	<table>
</table>
<!-- table ENDDS starts here -->