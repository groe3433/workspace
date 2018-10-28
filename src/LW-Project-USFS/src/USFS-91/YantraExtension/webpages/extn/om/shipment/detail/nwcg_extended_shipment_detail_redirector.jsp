<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String sViewGrp = "NWCYOMD710";	// initial
	if(isTrue("xml:/Shipment/@IsProvidedService") )	
	{
		sViewGrp = "YOMD333";	// initial
	}

	goToDetailView(response, sViewGrp);
%>

