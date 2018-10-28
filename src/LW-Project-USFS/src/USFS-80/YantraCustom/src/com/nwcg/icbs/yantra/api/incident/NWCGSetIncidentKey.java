package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSetIncidentKey implements YIFCustomApi {
	private static Logger log = Logger.getLogger(NWCGSetIncidentKey.class.getName());

	private Properties props;
	
	public void setProperties(Properties arg0) throws Exception {
		this.props = arg0;
	}

	/**
	 * This method will get the document that is set as part of create incident. Retrieves the
	 * incident key and sets it to the input document
	 * @param env
	 * @param inputDoc
	 * @return
	 */
	public Document setIncidentKey(YFSEnvironment env, Document inputDoc) 
	throws Exception{
		log.info("NWCGSetIncidentKey::setIncidentKey, Entered");
		if (log.isVerboseEnabled()){
			log.verbose("NWCGSetIncidentKey::setIncidentKey, " +
					"Input Document : " + XMLUtil.getXMLString(inputDoc));
		}
		Element incidentOrderElm = inputDoc.getDocumentElement();
		String incidentKey = incidentOrderElm.getAttribute(NWCGConstants.INCIDENT_KEY);
		if (incidentKey == null || incidentKey.length() < 1){
			Document rossAcctCodeMergedDoc = getDocumentFromEnv(env);
			if (rossAcctCodeMergedDoc != null){
				// Get the incident key. There can be a scenario where the incident
				// key will not be present. Format of this document will be
				// <MergedDocument>
				//	<InputDocument>
				//		<NWCGIncidentOrder IncidentKey ="" other attributes/>
				//  </InputDocument>
				//	<EnvironmentDocument>
				//		<NWCGIncidentOrder other attributes - incident key is not present here/>
				//	</EnvironmentDocument>
				// </MergedDocument>
				incidentKey = XPathUtil.getString(rossAcctCodeMergedDoc, "/MergedDocument/InputDocument/NWCGIncidentOrder/@IncidentKey");
			} // end of if(rossAcctCodeMergedDoc != null)
		} // end of if (incidentKey == null
		
		if (incidentKey != null && incidentKey.length() > 0){
			log.verbose("NWCGSetIncidentKey::setIncidentKey, Incident Key: " + incidentKey);
			incidentOrderElm.setAttribute(NWCGConstants.INCIDENT_KEY, incidentKey);
			if (log.isVerboseEnabled()){
				log.verbose("NWCGSetIncidentKey::setIncidentKey, " +
						"Input Document after setting the incident key: " + XMLUtil.getXMLString(inputDoc));
			}
		}
		else {
			log.info("NWCGSetIncidentKey::setIncidentKey, Unable to obtain incident key");
		}
		return inputDoc;
	}
	
	public Document getDocumentFromEnv(YFSEnvironment env)
	throws Exception {
		log.verbose("Entering GetDocumentFromEnvAPI.getDocumentFromEnv()");
		// if (logger.isVerboseEnabled())
		// logger.verbose("Got Document: " + XMLUtil.getXMLString(inDoc));
		String docid = props.getProperty(NWCGConstants.KEY_GETDOC_ENV_KEY);
		
		Object obj = CommonUtilities.getContextObject(env, docid);
		
		Document doc = null;
		if (obj instanceof Document) {
			doc = (Document) obj;
		} else if (obj instanceof YFCDocument) {
			doc = ((YFCDocument) obj).getDocument();
		} else {
			YFCDocument tmpDoc = YFCDocument.parse("<"
					+ NWCGConstants.TAG_INTERNAL_ENVELOPE + "/>");
			YFCNode tnode = tmpDoc.createTextNode(obj.toString());
			tmpDoc.getDocumentElement().appendChild(tnode);
			doc = tmpDoc.getDocument();
		}
		
		if (doc != null) {
			if (log.isVerboseEnabled()){
				log.verbose("Document Found in Env for Key [" + docid
						+ "] Document [" + XMLUtil.getXMLString(doc) + "]");
			}
		} else {
			log.info("No document found in Env for Key [" + docid
					+ "] Values [" + obj + "]");
		}
		
		log.verbose("Exiting GetDocumentFromEnvAPI.getDocumentFromEnv()");
		return doc;
	}

}
