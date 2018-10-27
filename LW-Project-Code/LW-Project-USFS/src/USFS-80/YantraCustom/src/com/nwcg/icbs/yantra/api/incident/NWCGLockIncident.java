package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGLockIncident implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public Document lockIncident(YFSEnvironment env, Document ipDoc, String modReason) throws Exception {
		NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, Entered");
		if (ipDoc == null){
			NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, Input Document is NULL");
			return ipDoc;
		}
		try {
			Element ipDocElm = ipDoc.getDocumentElement();
			String incidentNumber = ipDocElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			String year = ipDocElm.getAttribute(NWCGConstants.YEAR_ATTR);
			
			// Adding the below logic for register/deregister. We need to set the RegisterInterestInROSS
			// flag appropriately. In Register/Deregister, we are sending the flag RegisterInterestInROSS = Y
			// and RegisterInterestInROSS = N respectively. I will be using this flags to update the DB.
			// Here, this flags are set in configurator and not from DB. So, we need to update it. In other
			// scenarios of active/inactive, it doesn't matter as we will be setting whatever value it is. Also,
			// I am making a null check + length check before passing this element.
			String regIntInROSS = ipDocElm.getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);
			
			Document xmlDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elm = xmlDoc.getDocumentElement();
			elm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNumber);
			elm.setAttribute(NWCGConstants.YEAR_ATTR, year);
			elm.setAttribute(NWCGConstants.INCIDENT_LOCKED, NWCGConstants.YES);
			elm.setAttribute("LockReason", modReason);
			elm.setAttribute("ModificationDesc", modReason);
			elm.setAttribute("ModificationCode", "Sent to ROSS");
			if (regIntInROSS != null && 
					(regIntInROSS.equalsIgnoreCase("Y") || regIntInROSS.equalsIgnoreCase("N"))){
				elm.setAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, regIntInROSS);
			}
			NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, Input xml to " + 
					NWCGConstants.SVC_UPDT_INC_ORDER_SVC + " is " + XMLUtil.getXMLString(xmlDoc));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_UPDT_INC_ORDER_SVC, xmlDoc);
		} catch (ParserConfigurationException pce) {
			NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, " +
					"ParserConfigurationException while invoking " + NWCGConstants.SVC_UPDT_INC_ORDER_SVC);
			pce.printStackTrace();
			NWCGLoggerUtil.printStackTraceToLog(pce);
		} catch (Exception e2) {
			NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, " +
					"Exception while invoking " + NWCGConstants.SVC_UPDT_INC_ORDER_SVC);
			e2.printStackTrace();
			NWCGLoggerUtil.printStackTraceToLog(e2);
		}
		NWCGLoggerUtil.Log.info("NWCGLockIncident::lockIncident, Returning");
		return ipDoc;
	}
	
	/**
	 * Setting the modification reason and locked reason for locking the incident
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document lockActiveInactiveIncident(YFSEnvironment env, Document ipDoc) throws Exception{
		NWCGLoggerUtil.Log.info("NWCGLockIncident::lockActiveInActiveIncident, Entered");
		String isActive = ipDoc.getDocumentElement().getAttribute(NWCGConstants.IS_ACTIVE);
		String modReason = "";
		if (isActive.equalsIgnoreCase(NWCGConstants.YES)){
			modReason = "Made Active Incident Call";
		}
		else {
			modReason = "Made Inactive Incident Call";
		}
		return lockIncident(env, ipDoc, modReason);
	}

	/**
	 * Setting the modification reason and locked reason for locking the incident
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document lockRegisterDeregisterIncident(YFSEnvironment env, Document ipDoc) throws Exception{
		NWCGLoggerUtil.Log.info("NWCGLockIncident::lockRegisterDeRegisterIncident, Entered");
		String regIntInROSS = ipDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);		
		String modReason = "";
		if (regIntInROSS != null && regIntInROSS.equalsIgnoreCase(NWCGConstants.YES)){
			modReason = "Made Register Incident Call";
		}
		else if (regIntInROSS != null && regIntInROSS.equalsIgnoreCase(NWCGConstants.NO)){
			modReason = "Made Deregister Incident Call";
		}
		else {
			modReason = "RegisterInterestInROSS is not set. Unable to decipher the Reg/Dereg call for incident lock";
		}
		return lockIncident(env, ipDoc, modReason);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
