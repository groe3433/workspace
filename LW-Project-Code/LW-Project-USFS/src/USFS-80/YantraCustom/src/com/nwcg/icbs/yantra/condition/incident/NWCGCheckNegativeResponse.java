package com.nwcg.icbs.yantra.condition.incident;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckNegativeResponse implements YCPDynamicConditionEx {

	public boolean evaluateCondition(YFSEnvironment arg0, String str,
			Map map, Document doc) {
		Element docElm = doc.getDocumentElement();
		String rootNodeName = docElm.getNodeName();
		if (!rootNodeName.contains(NWCGConstants.REGISTER_INCIDENT_INTEREST_RESP)){
			docElm = getCorrectNode(docElm);
		}
		System.out.println("NWCGCheckNegativeResponse::evaluateCondition, Root Node Name: " + docElm.getNodeName());
		NodeList rootNL = docElm.getChildNodes();
		if (rootNL != null){
			int nlLen = rootNL.getLength();
			System.out.println("NWCGCheckNegativeResponse::evaluateCondition, No of elements: " + nlLen);
			for (int i=0; i < nlLen; i++){
				Node node = rootNL.item(i);
				System.out.println("NWCGCheckNegativeResponse::evaluateCondition, Node Name: " + node.getNodeName());
				if (node.getNodeName().contains(NWCGConstants.RESPONSE_STATUS)){
					NodeList childNL = node.getChildNodes();
					int childLen = childNL.getLength();
					System.out.println("NWCGCheckNegativeResponse::evaluateCondition, No of child elements: " + childLen);
					for (int j=0; j < childLen; j++){
						Node childNode = childNL.item(j);
						if (childNode.getNodeName().equalsIgnoreCase(NWCGConstants.RETURN_CODE)){
							String rtnResp = childNode.getFirstChild().getNodeValue();
							System.out.println("NWCGCheckNegativeResponse::evaluateCondition, Resp Vale: " + rtnResp);
							int respIntVal = new Integer(rtnResp).intValue();
							if (respIntVal == -1){
								return true;
							}
							else {
								return false;
							}
						} // end of if (childNode == ReturnCode)
					} // end of for(childLen)
				} // end of if (nodeName equals ResponseStatus)
			} // end of for(nodeList)
		} // end of if rootNL != null
		else {
			System.out.println("NWCGCheckNegativeResponse::evaluateCondition, Root element is NULL");
		}
		return true;
	}
	
	/**
	 * This method will return the node corresponding to RegisterIncidentInterestResp
	 */
	private Element getCorrectNode(Element elm){
		NodeList nl = elm.getChildNodes();
		if (nl != null){
			int nlLen = nl.getLength();
			for (int i=0; i < nlLen; i++){
				Node childNode = nl.item(i);
				if (childNode.getNodeName().contains(NWCGConstants.REGISTER_INCIDENT_INTEREST_RESP)){
					return (Element) childNode;
				}
			}
			System.out.println("NWCGCheckNegativeResponse::getCorrectNode, Unable to retreive the RegisterIncidentInterestResp node");
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
		//File f = new File("C:\\Clients\\USFS\\Testing\\RegisterIncidentInterestResp.xml");
		//File f = new File("C:\\Clients\\USFS\\Testing\\temp-DeliverOper-Neg-RegisterIncidentIntersetResp.xml");
		File f = new File("C:\\Clients\\USFS\\Testing\\temp-DeliverOper-Pos-RegisterIncidentIntersetResp.xml");
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine())!= null){
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			//content.toString().replace("<", "\\<");
			//System.out.println("Content : " + content.toString());
			tmpDoc = XMLUtil.getDocument(content.toString());
		}
		catch(Exception e){
			System.out.println("Exception e : " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Content : " + content.toString());
		NWCGCheckNegativeResponse negResp = new NWCGCheckNegativeResponse();
		boolean respVal = negResp.evaluateCondition(null, null, null, tmpDoc);
		System.out.println("Response Value : " + respVal);
	}

}
