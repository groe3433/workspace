package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessIncidents implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
  /*<NWCGIncidentOrder RegisterInterestInROSS="Y" IsActive="N" IncidentNo="AK-ACC-000001" Year="2011" IncidentKey="2011032108273938011584" NWCGUSERID="jbilliard" /> */

	

	public Document processIncidents(YFSEnvironment env, Document ipDoc) throws Exception {
		NWCGLoggerUtil.Log.finer("NWCGGetIncidentDtls::getIncidentDtls, Entered");
		Element ipDocRootElm = ipDoc.getDocumentElement();
		NodeList IncList = ipDocRootElm.getElementsByTagName("NWCGIncidentOrder");
		
    	for(int i=0; i<IncList.getLength(); i++)
		{
			Element curIncListElm = (Element) IncList.item(i);
		    String IncNo = curIncListElm.getAttribute("IncidentNo");
		    String IncYr = curIncListElm.getAttribute("Year");
		    String RegisterinROSS = curIncListElm.getAttribute("RegisterInterestInROSS");
		    String ActiveFlag = curIncListElm.getAttribute("IsActive");
		    String IncKey = curIncListElm.getAttribute("IncidentKey");
		    String UserID = curIncListElm.getAttribute("NWCGUSERID");
		
		    Document ipGetIncDtlsDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		    Element ipGetIncDtlsElm = ipGetIncDtlsDoc.getDocumentElement();
		    ipGetIncDtlsElm.setAttribute("IncidentNo", IncNo);
		    ipGetIncDtlsElm.setAttribute("Year", IncYr);
		    ipGetIncDtlsElm.setAttribute("RegisterInterestInROSS", RegisterinROSS);
		    ipGetIncDtlsElm.setAttribute("IsActive", ActiveFlag);
		    ipGetIncDtlsElm.setAttribute("IncidentKey", IncKey);
		    ipGetIncDtlsElm.setAttribute("NWCGUSERID", UserID);
		    
		    Document opIncDtlsDoc = CommonUtilities.invokeService(env, 
									"NWCGPostInActiveIncidentService", ipGetIncDtlsDoc);
		    NWCGLoggerUtil.Log.finer("NWCGGetIncidentDtls::getIncidentDtls, Returning");
		}
      return ipDoc;
	}

}
