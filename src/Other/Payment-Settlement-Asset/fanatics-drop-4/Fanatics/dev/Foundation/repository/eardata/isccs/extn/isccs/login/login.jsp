<%
// Licensed Materials - Property of IBM
// IBM Call Center for Commerce (5725-P82)
// (C) Copyright IBM Corp. 2013 , 2015 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.sterlingcommerce.security.dv.SCEncoder"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils"%>
<%@page import="com.sterlingcommerce.ui.web.framework.helpers.SCUILocalizationHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIUtils"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="com.ibm.sterling.afc.dojo.util.ProfileUtil" %>
<%@page import="javax.servlet.http.Cookie"%>
<%@ taglib uri="/WEB-INF/scui.tld" prefix="scuitag" %>
<%@ taglib uri="/WEB-INF/scuiimpl.tld" prefix="scuiimpltag" %>
<%@ page import="java.util.Locale "%>

<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	response.setDateHeader("Expires", 0); // Proxies.

			 
	SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);
	String localeCode = "";
	if(!SCUIUtils.isVoid(uiContext)){
	 localeCode = uiContext.getUserPreferences().getLocale().getLocaleCode();
	}
	if(SCUIUtils.isVoid(localeCode) && !SCUIUtils.isVoid(request.getParameter("LocaleCode"))  ){
		localeCode = request.getParameter("LocaleCode");  
	}else if(SCUIUtils.isVoid(localeCode)){
		localeCode = request.getLocale().toString();
	}
	localeCode = localeCode.toString().replace('_', '-').toLowerCase();

	//Strip the -Ext at the end of the locale.
	//If the locale string contains a - then take the first 5 letters xx-xx
	//Else leave it as is to avoid  
	if (localeCode.length()>5 && localeCode.contains("-"))
		localeCode=localeCode.substring(0,5);

	response.setContentType("text/html;charset=UTF-8");
	String errorMsg = request.getParameter("ErrorMsg");
	if(SCUIUtils.isVoid(errorMsg)){
		errorMsg = (String)uiContext.getAttribute("ERROR_MESSAGE");
	}
	
	if(SCUIUtils.isVoid(errorMsg)){
		errorMsg = "";
	}

%>
<html lang="<%=localeCode%>">
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<title><%= SCUILocalizationHelper.getString(uiContext, "ISCCS_Product_Name")%></title>
		<link rel="SHORTCUT ICON" href="<%=request.getContextPath()%>/isccs/resources/css/icons/images/logo_window.ico"/>

		<script type="text/javascript">
			var contextPath = '<%=request.getContextPath()%>';
			var dojoConfig = {
			    async:true,
			    locale:'<%=SCEncoder.getEncoder().encodeForJavaScript(localeCode)%>',
			    waitSeconds:5,
			    packages : [ {
						name : 'sc/plat',
						location :contextPath + "/platform/scripts/sc/plat"
					},
					{
						name : 'idx',
						location : contextPath + "/ibmjs/idx"
					},
					{
						name : 'scbase',
						location : contextPath + "/platform/scripts/sc/base"
					},
					{
						name : 'isccs',
						location : contextPath + "/isccs"
					},
					{
						name : 'gridx',
						location : contextPath + '/dojo/gridx'
					}

				]
			};
			dojoConfig.paths = {"sc/plat":contextPath+"/platform/scripts/sc/plat"};
		</script>


		<script src="<%=request.getContextPath()%>/dojo/dojo/dojo.js"></script>
		<script src="<%=request.getContextPath()%>/platform/scripts/sc/plat/dojo/base/loader.js"></script>

		<%
			if(SCUIUtils.isDevMode()) {
		%>
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/dojo/dojo/resources/dojo.css"></link>
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/dojo/dijit/themes/dijit.css"></link>
				<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/ibmjs/idx/themes/oneui/oneui.css"></link>
		<%
			} else {
		%>
				<link rel="stylesheet" type="text/css" href="<%=ProfileUtil.getInstance().getCSSFilePath("dojo","dojo/resources/dojo.css",false)%>"></link>
				<link rel="stylesheet" type="text/css" href="<%=ProfileUtil.getInstance().getCSSFilePath("dojo","dijit/themes/dijit.css",false)%>"></link>
				<link rel="stylesheet" type="text/css" href="<%=ProfileUtil.getInstance().getCSSFilePath("idx","idx/themes/oneui/oneui.css",false)%>"></link>
				<script src="<%=ProfileUtil.getInstance().getJSFilePath("dojo")%>"></script>
		<%
			}
		%>
		
	  	<script language="javascript" >
		
			scRequire(["scbase/loader!dojo/dom",
			           "scbase/loader!sc/plat/dojo/utils/BundleUtils",
			           "scbase/loader!isccs/login/Login",
					   "dojo/ready","dojo/request/script"],
			           function(dDom,
			           	        scBundleUtils,
			           	        isccsLogin,ready,script){

			           	           scBundleUtils.registerGlobalBundles("isccs.resources.bundle","isccs_login_bundle");
			           	           isccsLogin.init('<%=SCEncoder.getEncoder().encodeForJavaScript(errorMsg)%>');
			           	           
			           	           <% if(!SCUIUtils.isVoid(request.getParameter("EnterpriseCode"))) { %>
			           	              dDom.byId("enterpriseCode").value = '<%=SCEncoder.getEncoder().encodeForJavaScript(request.getParameter("EnterpriseCode"))%>';
			           	           <% } 
								   else {
			           	        	Cookie[] cookies = request.getCookies();
			           	        	if(cookies != null){
										for(int i=0; i<cookies.length; i++){
				           					Cookie cookie = cookies[i];
				           					String cookieName = cookie.getName();
				           					String cookieValue = cookie.getValue();
				           					if(cookieName.equalsIgnoreCase("ISCCSLoginEnterpriseCode")){ %>
				           						dDom.byId("enterpriseCode").value = '<%=SCEncoder.getEncoder().encodeForJavaScript(cookieValue)%>';
					           				<% }
					           				}
					           			}
			           				}%>
								   ready(function(){
										isccsLogin.setFocusOnUsername();
										
										<%
											if(!SCUIUtils.isDevMode()) {
										%>
										script.get("<%=ProfileUtil.getInstance().getJSFilePath("idx")%>");
										script.get("<%=ProfileUtil.getInstance().getJSFilePath("sc/plat")%>");
										script.get("<%=ProfileUtil.getInstance().getJSFilePath("isccs")%>");

										script.get("<%=ProfileUtil.getInstance().getCSSFilePath("dojo","gridx/resources/Gridx.css",false)%>");
										script.get("<%=ProfileUtil.getInstance().getCSSFilePath("idx","idx/themes/oneui/idx/gridx/Gridx.css",false)%>");
										script.get("<%=ProfileUtil.getInstance().getCSSFilePath("idx","idx/themes/oneui/idx/gridx/pagination.css",false)%>");
										script.get("<%=ProfileUtil.getInstance().getCSSFilePath("sc/plat","sc/plat/dojo/themes/claro/platform.css",false)%>");
										script.get("<%=ProfileUtil.getInstance().getCSSFilePath("isccs","isccs/resources/css/isccs/isccs.css",false)%>");

										<%
											}
										%>
     							  });
			  
			});
	 			
		</script>

		<style>
			.isccsLoginError{
				margin-top: 10px;
				color: #FF0000;
				font-weight: bold;
			}

				/*The entire section is a temp fix for idx. should remove when upgrade to new idx.
				The issue is in IE, the tool tip does not display at proper location when any error on field */
				
				.isccs .idxOneuiHoverHelpTooltip {				
					position: absolute;
					z-index: 2000;
					display: block;
					left:0px;
					top: -10000px;
					overflow: visible; 
				}
				
				.isccs .idxOneuiHoverHelpTooltipRight {
					padding:5px 5px 5px 9px;
					.idxOneuiHoverHelpTooltipCloseIcon{
						right: -8px;
						top: -8px;
					}
				}
				
				.isccs .idxOneuiHoverHelpTooltip, .isccs .idxOneuiHoverHelpTooltipDialog {
					background: transparent;
				}
				
				.isccs .idxOneuiHoverHelpTooltipContainer {
					background-color: #ffffff;
					background-repeat: repeat-x;
					background-position: bottom;
					border: 3px solid #999999;
					padding: 7px;
					-moz-border-radius: 4px;
					border-radius: 4px;
					-webkit-box-shadow: 0 1px 6px rgba(0, 0, 0, 0.25);
					-moz-box-shadow: 0 1px 6px rgba(0, 0, 0, 0.25);
					box-shadow: 0 1px 6px rgba(0, 0, 0, 0.25);
					font-size: 1em;
					color: #222222;
					max-width: 600px;
				}
				
				.isccs .idxOneuiHoverHelpTooltipRight .idxOneuiHoverHelpTooltipConnector {
					left:-6px;
					background-position: -54px 0;
				}
				
				.isccs .idxOneuiHoverHelpTooltipConnector {
					position: absolute;
					border: 0;
					z-index: 2;
					background-image: url("../ibmjs/idx/themes/oneui/idx/widget/images/popupSharkfin.png");
					background-repeat: no-repeat;
					width: 18px;
					height: 18px;
				}
				
				/* Temp fix ends  */
		</style>

 	</head>
	<body  class="oneui isccs">
		<div id="appHeader"></div>
		<div role="main" aria-label="Login Frame" aria-live="assertive">
		<div id="loginFrame" />
		<form id="fieldsForm"  class="isccsLoginForm" method="POST" action="processLogin.do" aria-live="assertive">
			<input id="displayUserId" name="DisplayUserID" type="hidden" />
			<input id="password" name="Password" type="hidden" />
			<a href="https://fanassist4.fanatics.corp/Account/ForgotPassword">Forgot Password</a>
			<input id="enterpriseCode" name="EnterpriseCode" type="hidden" />
		</form>
		</div>
	</body> 

</html>

