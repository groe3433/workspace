package com.nwcg.icbs.yantra.api.incident;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessNegativeResponse implements YIFCustomApi {
	private static Logger log = Logger.getLogger(NWCGProcessNegativeResponse.class.getName());

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public Document processNegativeResponse(YFSEnvironment env, Document doc) throws Exception{
		if (doc == null){
			log.info("NWCGProcessNegativeResponse::processNegativeResponse, Input Document is NULL");
			return null;
		}
		
		HashMap map = new HashMap();
		String errDesc = "";
		
		if (log.isVerboseEnabled()){
			log.verbose("NWCGProcessNegativeResponse::processNegativeResponse, Input Document: " + XMLUtil.getXMLString(doc));
		}
		
		NodeList respMsgNL = doc.getElementsByTagName(NWCGAAConstants.RESPONSE_MESSAGE);
		if (respMsgNL != null){
			// There will be only one ResponseMessage element in the xml
			Node respMsgNode = respMsgNL.item(0);
			NodeList respMsgChildNodesNL = respMsgNode.getChildNodes();
			for (int i=0; i < respMsgChildNodesNL.getLength(); i++){
				Node respMsgChildNode = respMsgChildNodesNL.item(i);
				String nodeName = respMsgChildNode.getNodeName();
				if (nodeName.equalsIgnoreCase(NWCGAAConstants.CODE)){
					map.put(NWCGAAConstants.CODE, respMsgChildNode.getFirstChild().getNodeValue());
				}
				else if (nodeName.equalsIgnoreCase(NWCGAAConstants.SEVERITY)){
					map.put(NWCGAAConstants.SEVERITY, respMsgChildNode.getFirstChild().getNodeValue());
				}
				else if (nodeName.equalsIgnoreCase(NWCGAAConstants.DESCRIPTION)){
					errDesc = respMsgChildNode.getFirstChild().getNodeValue();
					map.put(NWCGAAConstants.DESCRIPTION, errDesc);
				}
			}
		}
		
		NodeList incKeyNL = doc.getElementsByTagName(NWCGAAConstants.NATURAL_INCIDENT_KEY);
		String prefixVal = "", suffixVal = "", seqNum = "", yearCreated = "";
		if (incKeyNL != null){
			int incKeyNLLen = incKeyNL.getLength();
			log.info("NWCGProcessNegativeResponse::processNegativeResponse, NodeList size : " + incKeyNLLen);
			if (incKeyNLLen > 0){
				Node incKeyNode = incKeyNL.item(0);
				String nodeName = incKeyNode.getNodeName();
				if (nodeName.equalsIgnoreCase(NWCGAAConstants.SEQUENCE_NUMBER)){
					seqNum = incKeyNode.getFirstChild().getNodeValue();
				}
				else if (nodeName.equalsIgnoreCase(NWCGAAConstants.YEAR_CREATED)){
					yearCreated = incKeyNode.getFirstChild().getNodeValue();
				}
				else if (nodeName.equalsIgnoreCase(NWCGAAConstants.HOST_ID)){
					NodeList hostIDNL = incKeyNode.getChildNodes();
					for (int i=0; i < hostIDNL.getLength(); i++){
						Node hostNode = hostIDNL.item(i);
						if (hostNode.getNodeName().equalsIgnoreCase(NWCGAAConstants.UNIT_ID_PREFIX)){
							prefixVal = hostNode.getFirstChild().getNodeValue();
						}
						else if (hostNode.getNodeName().equalsIgnoreCase(NWCGAAConstants.UNIT_ID_SUFFIX)){
							suffixVal = hostNode.getFirstChild().getNodeValue();
						}
					} // end of for hostIDNL
				} // end of if (HostID)
			} // end of if (incKeyNLLen > 0)
			String incKey = getIncidentNo (prefixVal, suffixVal, seqNum);
			map.put(NWCGAAConstants.NWCG_INCIDENT_NO, incKey);
			map.put(NWCGAAConstants.NWCG_INCIDENT_YEAR, yearCreated);
		}
		
		if (!map.isEmpty()){
			log.info("NWCGProcessNegativeResponse::processNegativeResponse, Calling CommonUtilities.raiseAlert");
			// Call CommonUtilities.raiseAlert
			CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, errDesc, doc, null, map);
		}
		else {
			log.info("NWCGProcessNegativeResponse::processNegativeResponse, Incident details are not present, so not raising an alert");
		}
		return doc;
	}

	private String getIncidentNo (String prefixVal, String suffixVal, String seqNum){
		StringBuffer incNo = new StringBuffer(prefixVal).append("-").append(suffixVal).append("-");
		
		int seqLength = 6;
		int len = seqNum.length();
		for (int i=0; i < seqLength - len; i++){
			incNo.append("0");
		}
		incNo.append(seqNum);
		
		return incNo.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
