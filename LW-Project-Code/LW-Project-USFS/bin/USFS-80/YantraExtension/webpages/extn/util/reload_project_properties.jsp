<%

	try {
		
		//Reloads resources/extn/yantraimpl.properties, and 
		//resource bundles extnbundle and messagecode
		//Also reloads yfs.properties, but this reload will be available only to components
		//using ResourceUtil.get(). Yantra's getProperty() API would still return the old values.
		
		com.nwcg.icbs.yantra.util.common.ResourceUtil.loadDefaultResources();
	
		%><%="Project specific properties reloaded<br>"%><%

	} catch (Exception e) {
		%><%=e%><%
	}

%>