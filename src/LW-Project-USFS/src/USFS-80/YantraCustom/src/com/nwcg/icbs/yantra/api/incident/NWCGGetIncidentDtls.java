package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetIncidentDtls implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This class is created to get the incident details. This makes a service call to
	 * get the incident details.
	 * This is called from NWCGSaveIncidentOrderService. We can call a composite service
	 * of NWCGGetIncidentOrderService with in NWCGSaveIncidentOrderService. If we do that,
	 * we will not receive the output of composite service in NWCGSaveIncidentOrderService.
	 * So, making a call from this class and returning the XML.
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document getIncidentDtls(YFSEnvironment env, Document ipDoc) throws Exception {
		NWCGLoggerUtil.Log.finer("NWCGGetIncidentDtls::getIncidentDtls, Entered");
		Element ipDocRootElm = ipDoc.getDocumentElement();
		String incNo = ipDocRootElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
		String incYr = ipDocRootElm.getAttribute(NWCGConstants.YEAR_ATTR);
		
		Document ipGetIncDtlsDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element ipGetIncDtlsElm = ipGetIncDtlsDoc.getDocumentElement();
		ipGetIncDtlsElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
		ipGetIncDtlsElm.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
		
		Document opIncDtlsDoc = CommonUtilities.invokeService(env, 
									NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, ipGetIncDtlsDoc);
		NWCGLoggerUtil.Log.finer("NWCGGetIncidentDtls::getIncidentDtls, Returning");
		return opIncDtlsDoc;
	}

}
