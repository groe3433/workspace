<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%!
    public static String menuCorner = null;


    public String createMenuCorner(String bgClass, String fgClass, int size) {
		if (menuCorner == null) { // if not created, create it just once
			StringBuffer buf1 = new StringBuffer();
			String clsname = bgClass;
			for (int i=0; i<size; i++) {
				for (int j=0; j<(i+1); j++) {
					clsname = (j<(i+1)) ? bgClass : fgClass;
					buf1.append("<span class=\""+ clsname + "\" style=\"left:"+ j +";top:"+ i +";\"></span>");
				}
			}
			menuCorner = buf1.toString();
		}
        return menuCorner;
    }
%>
<script language="javascript" src="/yantra/console/scripts/menubar.js" ></script>
<script language="javascript" src="/yantra/yfcscripts/popup.js" ></script>
<script language="javascript">
	var contextPath = "<%=request.getContextPath()%>";
	window.attachEvent("onload",setMenuCorner);

function callNWCGAboutBox()	{
	var features = "dialogHeight:375px;dialogWidth:385px;dialogLeft:325px;dialogTop:200px;scroll:no;resizable:no;help:no;status:no;edge:sunken;unadorned:yes";
	window.showModalDialog("/yantra/extn/console/about.jsp","",features);
}

</script>
<div id="divMenu">
<div class="menumainbar">
	<div class="menubehindbox">
	</div>
	<div class="menubox1">
	<table cellpadding=0 cellspacing=0 border=0 class="menubox1box1">
		<tr class="menutoppart">
			<td class="menutopsysname"><%=getI18N("MENU_Yantra")%>
			</td>
  			<td class="menutopsysname" aligh="left">
            <table cellpadding=0 cellspacing=0 border=0 class="menubox211">
             	<tr>
              		<td class="menutopsysname" align="left" nowrap >
               		<font size="2" color="white"><b>For accessibility help with this application, contact the Interagency Help Desk at 1-866-224-7677 and a subject matter expert will assist you.</b>&nbsp</font>
               		</td>
               		<td class="menutopsysname" align="right" nowrap>
               		<font size="3" color="white"><b><%=getValue((YFCElement)session.getAttribute("CurrentUser"),"xml:/User/@Username")%></b></font>
               		</td>
            	</tr>
             </table>
           	</td>
			<td class="menutoplogo" rowspan=2 colspan=2 >
				<img class="menubottomicon" src="<%=YFSUIBackendConsts.YANTRA_LOGO%>" alt="<%=getI18N("MENU_About_Yantra")%>" onclick="callNWCGAboutBox();"/>
			</td>
		</tr>
		<tr class="menubox2" >
			<td class="menubox21">
				<table cellpadding=0 cellspacing=0 border=0 class="menubox211" >
					<tr>
						<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Back")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_BACK%>" onClick="javascript:window.history.back();"/>
						</td>
						<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Forward")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_FORWARD%>" onClick="javascript:window.history.forward();"/>
						</td>
						<!--<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Stop")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_STOP%>" onClick="javascript:window.stop();"/>
						</td>-->
						<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Refresh")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_REFRESH%>" onClick="javascript:window.location.reload();"/>
						</td>
						<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Home")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_HOME%>" onClick="callDtlEntView('home');" />
						</td>
						<td style="padding-right:5px" >
							<img class="menubottomicon" alt="<%=getI18N("MENU_Signout")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_SIGNOUT%>" onClick="javascript:logout();"/>
						</td>
					</tr>
				</table>
			</td>
			<td class="menubox22" align="left" >
				<table cellpadding=0 cellspacing=0 border=0 class="menubox221">
					<tr>
						<td id="tdMenuBar" class="menubox2211">
							<table id="tblMenuBar" border="0" cellspacing=0 cellpadding=0 >
								<tr id="mytr"> 
									  <yfc:loopXML binding="xml:MenuData:/Menu/SubMenu/@Menu" id="mymenu"  keyName='MenuKey'> 
										<%
											int reportItem = 0;
										%>
											<%String menukey=getValue(mymenu,"xml:/Menu@MenuKey");
											String activeFlag=getValue(mymenu,"xml:/Menu@Active");
											if (equals("Y", activeFlag))	{
											%>						
                                            <td id="PanelTable" onmouseover="this.className='menulevel1hl';" onmouseout="this.className='menulevel1norm'"  class="menulevel1norm" onclick="PopupWin('Left',divMenu<%=menukey%>,this,'.menuitempopuprownormal','.menuitempopuprowhighlight','','.menuitempopupscroll');">
												<%if (!isVoid(mymenu.getAttribute("Icon")))	{	%>
												<img src='<yfc:getXMLValue name="mymenu" binding="xml:/Menu/@Icon" />' class="icon" >	
												<%	}	%>
												&nbsp;<yfc:i18n>MENU_<yfc:getXMLValue binding="xml:mymenu:/Menu/@MenuDescription" /></yfc:i18n>&nbsp;
											</td>
												<div id="divMenu<%=menukey%>" name="actiondiv" style="visibility:hidden;position:absolute;border:">
														<yfc:loopXML binding="xml:mymenu:/Menu/SubMenu/@Menu" id="submenu" keyName='MenuKey' >

                                                             <% String linkparam = "window.parent.location.href='";
																String resourceType = getValue(submenu,"xml:/Menu/Resource/@ResourceType");
                                                                String resourceID = getValue(submenu,"xml:/Menu/Resource/@ResourceId");

                                                                if (("ENTITY".equals(resourceType)) && (!"system".equals(resourceID))) {
																	linkparam += "/yantra/console/" + getValue(submenu,"xml:/Menu/Resource/@ResourceId") + ".search";
																	if(getValue(submenu,"xml:/Menu/Resource/@ResourceId").startsWith("ycn")){
																		reportItem ++;
																	}
																}
																else if("DETAIL_VIEW".equals(resourceType)){
																	String viewID = getValue(submenu,"xml:/Menu/Resource/@ResourceId");
																	String entityID = getEntityID(viewID);
																	linkparam += "/yantra/console/" + entityID + ".detail?CurrentDetailViewID=" + viewID ;
																}
																else{
																	linkparam += getValue(submenu,"xml:/Menu/Resource/@Url");
																}
															linkparam +="';";
															String subactiveFlag=getValue(submenu,"xml:/Menu@Active");
															if (equals("Y", subactiveFlag))	{
															%>
															<%if(reportItem == 1){
																reportItem++;
																%>
																<DIV  myonclick=""> 		
																	<hr>
																</DIV>
															<%}%>
															<DIV  myonclick="<%=linkparam%>"> 		
																<%
																	if (!isVoid(submenu.getAttribute("Icon")))	{	%>
																	<img src='<%=submenu.getAttribute("Icon")%>'>&nbsp;
																	<%	}	%>
																<%=getI18N("MENU_" + submenu.getAttribute("MenuDescription"))%>
															</DIV>
															<%	}	%>
														</yfc:loopXML> 
												</div>
											<%	}	%>
									  </yfc:loopXML>													
								  </tr>
							</table>

						</td>
						<td class="menubox222">
							<table cellpadding=0 cellspacing=0 border=0 class="menubox2221">
								<tr>
									<% if(hasPermission("personinfo")) {%>
									<td class="menubottommyprofileicon" id="PanelTable"> 
										<img class="menubottomicon" alt="<%=getI18N("MENU_User_Profile")%>" src="<%=YFSUIBackendConsts.YANTRA_TITLE_PROFILE%>" 
											onClick="callDtlEntView('personinfo');"/>
									</td>
									<%}%>
									<td class="menubottommyprofileicon"> 
                                        <img class="menubottomicon"  id="PanelTable" alt="<%=getI18N("MENU_Change_Locale")%> (<%=getCurrentLocale()%>)" src="<%=YFSUIBackendConsts.YANTRA_TITLE_LOCALE%>" onClick='PopupWin("Right",localeselection,this,".menuiconspopuprownormal",".menuiconspopuprowhighlight","",".menuiconspopupscroll");'/>
									</td>
									<td class="menubottommyprofileicon" > 
                                        <img class="menubottomicon" id="PanelTable" alt="<%=getI18N("MENU_Change_Theme")%> (<%=getTheme()%>)" src="<%=YFSUIBackendConsts.YANTRA_TITLE_THEME%>" onClick='PopupWin("Right",themeselection,this,".menuiconspopuprownormal",".menuiconspopuprowhighlight","",".menuiconspopupscroll");'/>
									</td>
									<td class="menubottomhelpicon"> 
										<img class="menubottomicon" alt="<%=getI18N("MENU_Help")%>" src="<%=YFSUIBackendConsts.YANTRA_HELP%>" onclick='showHelp();'/>
									</td>
									
									<td class="menuboxcorner"><div style="position:absolute;" id="MenuCorner">
									</div>
									</td>									
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</div>
</div>
<div id="localeselection" class="menulocalemainsection">
	<% List locales = getLocaleList();
        for (Iterator i = locales.iterator();i.hasNext();) {
        	String locale = (String)i.next();%>
			<div class="menulocalelistitem"   myonclick="parent.ChangeLocale('<%=locale%>');"> 	<%=locale%>
			</div>
        <%}%>
</div>
<div id="themeselection" class="menuthememainsection">
	<yfc:loopXML binding="xml:ThemeList:/Themes/@Theme" id="theme"  keyName='Name'> 
		<div class="menuthemelistitem"  myonclick="parent.ChangeTheme('<%=getValue(theme,"xml:/Theme/@ID")%>');"> <yfc:i18n><%=getValue(theme,"xml:/Theme/@ID")%></yfc:i18n>
		</div>
	</yfc:loopXML>													
</div>
</div>
