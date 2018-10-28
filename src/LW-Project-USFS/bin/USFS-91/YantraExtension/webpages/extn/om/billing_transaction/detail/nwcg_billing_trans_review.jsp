<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>



<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>


<!-- Get Header Details-->
<table>
<td allign="left">
<TABLE class=table>
				<TBODY>
				<tr>

				<tr>
					<TR class=evenrow>
						<TD width="100%">&nbsp; </TD>
						<TD style="BORDER-RIGHT: black 1px solid; BORDER-TOP: black 1px solid; BORDER-LEFT: black 1px solid; BORDER-BOTTOM: black 1px solid" width="100%">
							<!--<td>-->
							<TABLE class=table cellSpacing=0 width="100%" border=1 editable="true">
							<TBODY>
								<TR>
								<TD class="tablecolumnheader" selectIndex="1"><yfc:i18n>CacheID</yfc:i18n></TD>
								</TR>
								<TR>
								<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=40 name=xml:/NWCGBillingTransaction/@CacheID dataType="STRING" Value="<%=resolveValue("xml:CurrentUser:/User/@Node")%>"> 
								<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>
								</TD>
								</TR>
							  <tr>
                             <td class="tablecolumnheader" ><yfc:i18n>Trans_Date</yfc:i18n></td>
                             </tr>
                             <tr>
                             <td nowrap="true">
                             <yfc:i18n>From</yfc:i18n>
                             <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGBillingTransaction/@FromTransDate")%>/>
                             <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
                             <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/NWCGBillingTransaction/@FromTransTime")%>/>
                             <img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
                             </td>
                             </tr>
                             <tr>
                             <td>
                             <yfc:i18n>To&nbsp;&nbsp;&nbsp;&nbsp;</yfc:i18n>
                             <input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGBillingTransaction/@ToTransDate")%>/>
                             <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
                             <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/NWCGBillingTransaction/@ToTransTime")%>/>
                             <img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
                             </td>
                             </tr>
						  </TBODY>
					    </TABLE>
						</TD>
					</TR>
				</TBODY>
		</TABLE>
	</td>
	<table>

</table>
<!-- table ENDDS starts here -->