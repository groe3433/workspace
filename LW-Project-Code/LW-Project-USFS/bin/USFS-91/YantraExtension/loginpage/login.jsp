<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%@page import="com.yantra.yfc.ui.backend.util.*"%>
<%
	String errorMsg = (String)getParameter("ErrorMsg");
    if (isVoid(errorMsg)) {
		errorMsg = (String)request.getAttribute("ErrorMsg");
	}
 	String errorMsgDetail = (String)getParameter("ErrorMsgDetail");	
    String localecode = (String)getParameter("LocaleCode");
	if (!isVoid(localecode)) {
		addCookie(response,"LocaleCode",localecode);
		changeSessionLocale(localecode);
	} else {
		localecode = getCookie("LocaleCode");
		changeSessionLocale(localecode);
	}
	String userid = (String)getParameter("UserId");
    String token = (String) getParameter("token");
	if (!isVoid(userid) || !isVoid(token)) {
		YFSUILoginManager.getInstance().Login(request,response);
	} else {
		//NAP Custom Redirection capability added as part of NAP CR
		String sRedirectURL = YFCConfigurator.getInstance().getProperty("yfs.login.redirect.url");
		if(!isVoid(sRedirectURL)) {
        	response.sendRedirect(sRedirectURL);
		}
	}	
	String loginurl = getActualPath("/console/logindetails.jsp");
	String userAgent = request.getHeader("User-Agent").toLowerCase(); 
    if ((userAgent.indexOf("msie") == -1) && (userAgent.indexOf("mozilla") == -1) && (userAgent.indexOf("opera") == -1)) {
		loginurl = getActualPath("/mobile/login.jsp");
	}
	if (!isVoid(errorMsg)) {
		loginurl += "?ErrorMsg="+HTMLEncode.htmlEscape(errorMsg);
        if (!isVoid(errorMsgDetail)) {
			loginurl += "&ErrorMsgDetail="+HTMLEncode.htmlEscape(errorMsgDetail);
		}
		//Custom Redirection capability added - CR 90005
		//String sRedirectURL = YFCConfigurator.getInstance().getProperty("yfs.login.redirect.url");
		//if(!isVoid(sRedirectURL)) {
        	//response.sendRedirect(sRedirectURL);
		//}
	}
%>
<jsp:include page="<%=loginurl%>" flush="true" />