<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<%
String FromDate = "";
String ToDate = "";
String TotExtCnt = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TotalExtractCount");
String BlankCnt = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@BlankCount");
String LengthCnt = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@LengthCount");
String AfterExtract = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@AfterExtract");
String MessageText = "";
int totextcnt = 0;
int blankcnt = 0;
int lengthcnt = 0;

if (TotExtCnt.length() > 0)
{
  totextcnt = Integer.parseInt(TotExtCnt);
}

if (BlankCnt.length() > 0)
{
  blankcnt = Integer.parseInt(BlankCnt);
}

if (LengthCnt.length() > 0)
{
  lengthcnt = Integer.parseInt(LengthCnt);
}

//if (AfterExtract.equals("Y"))
//{
  if (blankcnt == 0 && lengthcnt == 0 && totextcnt > 0)
  {
    MessageText = "Extraction is Completed " + TotExtCnt + " Records were Successfully Extracted ";
  }

  if (blankcnt > 0 && lengthcnt == 0 && totextcnt > 0)
  {
    MessageText = "Extraction is Completed " + TotExtCnt + " Records were Successfully Extracted, " + blankcnt + 		          " Records were not Extracted as the Account Codes are Blank. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }

  if (blankcnt == 0 && lengthcnt > 0 && totextcnt > 0)
  {
    MessageText = "Extraction is Completed " + TotExtCnt + " Records were Successfully Extracted, " + lengthcnt +		          " Records were not Extracted as the Account Codes are not 21 Characters. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }

  if (blankcnt > 0 && lengthcnt > 0 && totextcnt > 0)
  {
    MessageText = "Extraction is Completed " + TotExtCnt + " Records were Successfully Extracted, " + blankcnt +	          " Records were not Extracted as the Account Codes are Blank, " + lengthcnt + " Records were not Extracted as the Account Codes are not 21 Characters. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }

  if (blankcnt > 0 && lengthcnt > 0 && totextcnt == 0)
  {
    MessageText = "Extraction is Completed, No Records were Successfully Extracted, " + blankcnt + " Records were not Extracted as the Account Codes are Blank, " + lengthcnt + " Records were not Extracted as the Account Codes are not 21 Characters. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }

  if (blankcnt > 0 && lengthcnt == 0 && totextcnt == 0)
  {
    MessageText = "Extraction is Completed, No Records were Successfully Extracted, " + blankcnt + " Records were not Extracted as the Account Codes are Blank. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }

  if (blankcnt == 0 && lengthcnt > 0 && totextcnt == 0)
  {
    MessageText = "Extraction is Completed, No Records were Successfully Extracted, " + lengthcnt + " Records were not Extracted as the Account Codes are not 21 Characters. Check the Alert Console for Alert Type" +"\"BILLING_TRANS_EXTRACT\" for more information";
  }
//}

YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser"); 
String UserNode = curUsr.getAttribute("Node");
YFCElement organizationInput = null;
organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + UserNode + "\" />").getDocumentElement();

YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationCode=\"\" PrimaryEnterpriseKey=\"\" > <Extn /> </Organization> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<input type="hidden" name="xml:/NWCGBillingTransaction/@PrimaryEnterpriseKey" value='<%=resolveValue("xml:/OrganizationList/Organization/@PrimaryEnterpriseKey")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@OwnerAgency" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@BillingTransFileName" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnBillingTransFileName")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@DocumentNo" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnDocumentNo")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@FiscalYear" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnFiscalYear")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@CurrentSeqNo" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnCurrentSeqNo")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@OffsetAcctCode" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOffsetAcctCode")%>'/>
<input type="hidden" name="xml:/NWCGBillingTransaction/@OffsetAcctCode" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOffsetAcctCode")%>'/>

<!-- Get Header Details-->
<table>
<td allign="left">
<TABLE class=table>
				<TBODY>
				<tr>
				<td/>
					<td colspan="4" class="tablecolumnheader"><font color="red"> <%=MessageText%> </font>
					</td>
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
                             <input class="dateinput" type="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@FromTransDate")%> OldValue="" value="<%=FromDate%>"/>
                             <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
                             <yfc:i18n>To</yfc:i18n>
                             <input class="dateinput" type="unprotectedinput" <%=getTextOptions("xml:/NWCGBillingTransaction/@ToTransDate")%> OldValue="" value="<%=ToDate%>"/>
                             <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
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