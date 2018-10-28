package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class NWCGItemValidator {
	private static Logger logger = Logger.getLogger(NWCGItemValidator.class.getName());

	static public void validateItemDescription (YFSEnvironment env, String strShortDesc, String strItemID) throws YFSUserExitException {
		
		
		Document inputXMLDoc = null;
		
		try {
			inputXMLDoc = XMLUtil.createDocument("Item");
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			e.printStackTrace();
		}
		        
		Element eleRoot = inputXMLDoc.getDocumentElement();
		Element elePrimaryInformation = inputXMLDoc.createElement("PrimaryInformation");
		elePrimaryInformation.setAttribute("ShortDescription", strShortDesc);		
		eleRoot.appendChild(elePrimaryInformation);
			
		Document docItemList = null;
		try{
			
		docItemList = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ITEM_LIST, inputXMLDoc);
		//System.out.println("docItemList returned: " + XMLUtil.getXMLString(docItemList));
				
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//Element eleItemListRoot = docItemList.getDocumentElement();
		NodeList nlItem = docItemList.getElementsByTagName("Item");
		
		if(nlItem != null && nlItem.getLength() > 1){
			if (logger.isVerboseEnabled())logger.verbose("nlItem.getLength is: " + nlItem.getLength());
			if (logger.isVerboseEnabled())logger.verbose("strShortDesc is: " + strShortDesc);
			if (logger.isVerboseEnabled())logger.verbose("strItemID is: " + strItemID);
			
			throw new YFSUserExitException("Please enter a unique Short Description.");
		}
		else if(nlItem != null && nlItem.getLength() == 1){
		
			Element eleItemReturned = (Element) nlItem.item(0);
			String strItemIDReturned = eleItemReturned.getAttribute("ItemID");
			if (logger.isVerboseEnabled())logger.verbose("strItemID : " + strItemID);
			if (logger.isVerboseEnabled())logger.verbose("strItemIDReturned: " + strItemIDReturned);
			if (!(strItemIDReturned.equals(strItemID))){
				throw new YFSUserExitException("Please enter a unique Short Description.  Your description matches ItemID: " + strItemIDReturned);
			}
		}
				
		
	}

}