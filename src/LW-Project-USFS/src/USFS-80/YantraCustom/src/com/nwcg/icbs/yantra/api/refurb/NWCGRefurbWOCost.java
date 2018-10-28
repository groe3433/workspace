/**
 * @author sgunda
 */

package com.nwcg.icbs.yantra.api.refurb;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRefurbWOCost implements YIFCustomApi{

	private Properties _properties;
	private static Logger log = Logger.getLogger(NWCGRefurbWOCost.class.getName()); 	
	
	public void setProperties(Properties prop) throws Exception{
		_properties = prop;
	}
	

	/**
	 * This method gets the refurb cost of the refurb issue and updates on the work order
	 * @param env
	 * @param orderOPDoc
	 * @return
	 * @throws NWCGException
	 */
	public Document updateWORefurbCost(YFSEnvironment env, Document orderOPDoc)
	throws NWCGException {
		try {
			log.verbose("NWCGRefurbWOCost::updateWORefurbCost, Entered");
			Element orderElm = orderOPDoc.getDocumentElement();
			//Element orderElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Order");
			//Element rootNode = orderOPDoc.getDocumentElement();
			//Element orderElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Order");
			String totalCost = orderElm.getAttribute("OriginalTotalAmount");
			String enterpriseCode = orderElm.getAttribute("EnterpriseCode");
			String nodeKey = orderElm.getAttribute("ShipNode");
			Element extnElm = (Element) XMLUtil.getChildNodeByName(orderElm, "Extn");
			String woNumber = extnElm.getAttribute("ExtnRefurbWO");
			
			Document woDoc = XMLUtil.newDocument();
			Element woElem = woDoc.createElement("WorkOrder");
			woDoc.appendChild(woElem);
			woElem.setAttribute("EnterpriseCode", enterpriseCode);
			woElem.setAttribute("NodeKey", nodeKey);
			woElem.setAttribute("WorkOrderNo", woNumber);
			
			Element woExtnElem = woDoc.createElement("Extn");
			woElem.appendChild(woExtnElem);
			woExtnElem.setAttribute("ExtnRefurbCost", totalCost);
			
			log.verbose("NWCGRefurbWOCost::updateWORefurbCost, Calling Modify Work Order");
			CommonUtilities.invokeAPI(env, NWCGConstants.API_MODIFY_WO, woDoc);
			log.verbose("NWCGRefurbWOCost::updateWORefurbCost, Exiting");
		}
  	catch (ParserConfigurationException pce){
  		log.error("NWCGRefurbWOCost::updateWORefurbCost, Parser Conf Exc : " + pce.getMessage());
  		log.error("NWCGRefurbWOCost::updateWORefurbCost, Parser StackTrace : " + pce.getStackTrace());
  		throw new NWCGException(pce);
  	}
  	catch (Exception e){
  		log.error("NWCGRefurbWOCost::updateWORefurbCost, Exception Message : " + e.getMessage());
  		log.error("NWCGRefurbWOCost::updateWORefurbCost, StackTrace : " + e.getStackTrace());
  		throw new NWCGException(e);
  	}
  	// Note: We need to pass createOrder output document as input to changeOrderStatus. So, we are
  	// returning the document that we received as input
		return orderOPDoc;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
