<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>

<%
String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node")) ;
YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");
YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> </Organization>");
if(!strNode.equals(""))
{%>
<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>
<%}%>

<%
String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
%>
<input type="hidden" name="xml:/Receipt/@RecvOwnerAgency" value="<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>"/>

<% 
   String ErrorMessage = resolveValue("xml:/Receipt/@ErrorMessage");
   String ReceiptNo = "";
   String IncidentNo = "";
   String IncidentYear = "";
   String IncidentName = "";
   String CustomerId = "";
   String IssueNo = "";
   String ReceivingDock = "RETURN-1";

   YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser");
   if (ErrorMessage != "")
   {
      ReceiptNo = resolveValue("xml:/Receipt/@ReceiptNo"); 
	  IncidentNo = resolveValue("xml:/Receipt/@IncidentNo");
	  IncidentYear = resolveValue("xml:/Receipt/@IncidentYear");
      IncidentName = resolveValue("xml:/Receipt/@IncidentName");
	  CustomerId = resolveValue("xml:/Receipt/@CustomerId");
	  IssueNo = resolveValue("xml:/Receipt/@IssueNo");
	  ReceivingDock = resolveValue("xml:/Receipt/@ReceivingDock");
   }
   else
   {
 %>
   <!-- <yfc:callAPI apiID="AP1"/> !-->
 <%
	  ReceiptNo = resolveValue("xml:/NextReceiptNo/@ReceiptNo"); 
   }
 %>
<input type="hidden" <%=getTextOptions("xml:/Receipt/@UserId",curUsr.getAttribute("Loginid"))%>/>
<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingDock","RETURN-1")%>/>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_returns.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<!-- Get Header Details-->
<table>
<td allign="left">
<TABLE class=table>
				<TBODY>
				<tr>
				    <td/>
					<td class=tablecolumn nowrap size=1000 ><font color="red"> <%=resolveValue("xml:/Receipt/@ErrorMessage")%> </font>
				    </td>
				<tr>
					<TR class=evenrow>
						<TD width="100%">&nbsp; </TD>
						<TD style="BORDER-RIGHT: black 1px solid; BORDER-TOP: black 1px solid; BORDER-LEFT: black 1px solid; BORDER-BOTTOM: black 1px solid" width="100%">
							<!--<td>--> 
							<TABLE class=table cellSpacing=0 width="100%" border=1 editable="true">
							<TBODY>
							    <!--<TR>
								<TD class="tablecolumnheader" selectIndex="1">Return No</TD>
								<TD class=tablecolumn noWrap><%=ReceiptNo%> 
								</TD>
								</TR> !-->
								<TR>
								<TD class="tablecolumnheader" selectIndex="1">CacheID</TD>
<!-- Begin CR844 11302012-->
<!-- <TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=40 name=xml:/Receipt/@CacheID dataType="STRING" Value="<%=resolveValue("xml:CurrentUser:/User/@Node")%>"> -->
<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=40 name=xml:/Receipt/@CacheID dataType="STRING" Value="<%=resolveValue("xml:CurrentUser:/User/@Node")%>" onblur="javascript:if(this.value==''){alert('Error: Field CacheID cannot be blank. Please enter a value.')}"> 
<!-- End CR844 11302012 -->
								<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>
								</TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="0">Return No</TD>
								<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=40 Size=30 name=xml:/Receipt/@ReceiptNo dataType="STRING" value="<%=ReceiptNo%>"  onblur="fetchDataWithParams(this,'validateReturnNo',PopupReturnMesg,setReturnParam(this));"> 
								</TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="0">Incident Year</TD>
								<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=4 size=4 name=xml:/Receipt/@IncidentYear dataType="STRING" value="<%=IncidentYear%>" OldValue=""></TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="0">Incident/Other Order#</TD>
								<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=40 size=30 id="OrderNum" onBlur="fetchDataWithParams(this,'getIncidentDetailsForReturns',populateIncName,setParamForIncName(this));" name=xml:/Receipt/@IncidentNo dataType="STRING" value="<%=IncidentNo%>" OldValue=""><img class="lookupicon" onclick="callIncidentLookup('xml:/Receipt/@IncidentNo','xml:/Receipt/@IncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> /></TD>
								</TR>
     							<TR>
								<TD class="tablecolumnheader" selectIndex="0">Incident Name</TD>
								<TD class=tablecolumn noWrap><INPUT readonly=true class=protectedinput maxLength=50 size=30 name=xml:/Receipt/@IncidentName dataType="STRING" value="<%=IncidentName%>" OldValue=""></TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="0">Customer ID</TD>
								<TD class=tablecolumn noWrap><INPUT readonly=true class=protectedinput maxLength=40 size=30 name=xml:/Receipt/@CustomerId value="<%=CustomerId%>" dataType="STRING" OldValue=""></TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="2">Issue#</TD>
								<TD class=tablecolumn noWrap><INPUT class=unprotectedinput maxLength=4000 size=30 name=xml:/Receipt/@IssueNo dataType="STRING" OldValue="" value="<%=IssueNo%>"  onBlur="fetchDataWithParams(this,'getOrderList',populateIncNo,setParam3(this));"><img class="lookupicon" onclick = "callOrderNoLookup('xml:/Receipt/@IssueNo', null,'order', 'yfcListViewGroupId=YOML011')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Issue") %> /> </TD>
								</TR>
								<TR>
								<TD class="tablecolumnheader" selectIndex="2">Receiving Dock</TD>
								<TD class=tablecolumn noWrap><INPUT readonly=true class=protectedinput maxLength=4000 size=30 dataType="STRING" OldValue="" value="<%=ReceivingDock%>" >
								<!--<IMG class=lookupicon onclick="callLookup(this,'location','<%=resolveValue("xml:CurrentUser:/User/@Node")%>')" alt="Search for Location" src="<%=request.getContextPath()%>/console/icons/lookup.gif"> !-->
								</TD> 
								</tr>
								<TR>
                                <TD class="tablecolumnheader" selectIndex="2">Return Notes</TD>
								<TD rowspan="3" >
                                <textarea name=xml:/Receipt/@ReturnHeaderNotes id="Notes" class="unprotectedtextareainput" rows="3" cols="35" onfocus="setRange(this,0,0);" >
		                        </textarea>
                                </td>
								</TR>
								<tr>
									<table class=table cellSpacing=0 width="100%" border=1 editable="true">
										<tbody>
											<tr class=evenrow> 
												<td align="center" colspan="2" class=protectedtext><label id='SPLIT_ACC_LINK_LABEL'><A id='SPLIT_ACC_LINK_REF' onclick='' style="visibility:hidden">Split Account Codes</A></label> </td>
											</tr>
										</tbody>
									</table> 
								</tr>
							</TBODY>
							
							</TABLE>
						</TD>
					</TR>
				</TBODY>
		</TABLE>
	</td>
</table>
<table>
<input type="hidden" <%=getTextOptions("xml:/Shipment/@EnterpriseCode")%> />
<input type="hidden" <%=getTextOptions("xml:/Shipment/@DocumentType")%> />
</table>
<!-- table ENDDS starts here -->
<div id="splitAccountCodeDataDiv">
		
	<input name="xml:/Receipt/SplitAccountCodeData/@AccountCount" id="xml:/Receipt/SplitAccountCodeData/@AccountCount" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@SaveAccounts" id="xml:/Receipt/SplitAccountCodeData/@SaveAccounts" value="N"/>
	
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode1" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode1" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount1"  id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount1" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage1"  id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage1" value=""/>
	
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode2" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode2" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount2"  id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount2" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage2"  id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage2" value=""/>
	
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode3" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode3" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount3"  id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount3" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage3"  id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage3" value=""/>
	
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode4" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode4" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount4"  id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount4" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage4"  id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage4" value=""/>
	
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode5" id="xml:/Receipt/SplitAccountCodeData/@RefundAcctCode5" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount5"  id="xml:/Receipt/SplitAccountCodeData/@RefundChargedAmount5" value=""/>
	<input name="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage5"  id="xml:/Receipt/SplitAccountCodeData/@ReturnPercentage5" value=""/>
</div>