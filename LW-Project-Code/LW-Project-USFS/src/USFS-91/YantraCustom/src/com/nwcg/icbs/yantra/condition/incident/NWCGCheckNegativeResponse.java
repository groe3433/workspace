/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.condition.incident;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import com.yantra.yfc.log.YFCLogCategory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckNegativeResponse implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCheckNegativeResponse.class);

	public boolean evaluateCondition(YFSEnvironment arg0, String str, Map map,
			Document doc) {
		Element docElm = doc.getDocumentElement();
		String rootNodeName = docElm.getNodeName();
		if (!rootNodeName
				.contains(NWCGConstants.REGISTER_INCIDENT_INTEREST_RESP)) {
			docElm = getCorrectNode(docElm);
		}

		NodeList rootNL = docElm.getChildNodes();
		if (rootNL != null) {
			int nlLen = rootNL.getLength();
			for (int i = 0; i < nlLen; i++) {
				Node node = rootNL.item(i);
				if (node.getNodeName().contains(NWCGConstants.RESPONSE_STATUS)) {
					NodeList childNL = node.getChildNodes();
					int childLen = childNL.getLength();
					for (int j = 0; j < childLen; j++) {
						Node childNode = childNL.item(j);
						if (childNode.getNodeName().equalsIgnoreCase(
								NWCGConstants.RETURN_CODE)) {
							String rtnResp = childNode.getFirstChild()
									.getNodeValue();
							int respIntVal = new Integer(rtnResp).intValue();
							if (respIntVal == -1) {
								return true;
							} else {
								return false;
							}
						} // end of if (childNode == ReturnCode)
					} // end of for(childLen)
				} // end of if (nodeName equals ResponseStatus)
			} // end of for(nodeList)
		} // end of if rootNL != null
		else {
		}
		return true;
	}

	/**
	 * This method will return the node corresponding to
	 * RegisterIncidentInterestResp
	 */
	private Element getCorrectNode(Element elm) {
		NodeList nl = elm.getChildNodes();
		if (nl != null) {
			int nlLen = nl.getLength();
			for (int i = 0; i < nlLen; i++) {
				Node childNode = nl.item(i);
				if (childNode.getNodeName().contains(
						NWCGConstants.REGISTER_INCIDENT_INTEREST_RESP)) {
					return (Element) childNode;
				}
			}
		}
		return elm;
	}

	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuffer content = new StringBuffer();
		Document tmpDoc = null;
		// File f = new
		// File("C:\\Clients\\USFS\\Testing\\RegisterIncidentInterestResp.xml");
		// File f = new
		// File("C:\\Clients\\USFS\\Testing\\temp-DeliverOper-Neg-RegisterIncidentIntersetResp.xml");
		File f = new File(
				"C:\\Clients\\USFS\\Testing\\temp-DeliverOper-Pos-RegisterIncidentIntersetResp.xml");
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			// content.toString().replace("<", "\\<");
			tmpDoc = XMLUtil.getDocument(content.toString());
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}
		NWCGCheckNegativeResponse negResp = new NWCGCheckNegativeResponse();
		boolean respVal = negResp.evaluateCondition(null, null, null, tmpDoc);
	}
}