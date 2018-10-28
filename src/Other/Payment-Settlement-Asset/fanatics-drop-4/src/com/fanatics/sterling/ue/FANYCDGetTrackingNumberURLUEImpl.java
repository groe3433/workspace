package com.fanatics.sterling.ue;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.pca.ycd.japi.ue.YCDGetTrackingNumberURLUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class FANYCDGetTrackingNumberURLUEImpl implements YCDGetTrackingNumberURLUE {

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	@Override
	public Document getTrackingNumberURL(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {

		log.info("Input XML: \n"+XMLUtil.getXMLString(inDoc));
		
		String strIpGetCommonCodeList = "<CommonCode CodeType='TrackingUrls'></CommonCode>";
		Document docIpGetCommonCodeList = null;
		try 
		{  
			docIpGetCommonCodeList = XMLUtil.getDocument(strIpGetCommonCodeList);
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		
		Document docOPGetCommonCodeList=null;
		try {
			docOPGetCommonCodeList = CommonUtil.invokeAPI(env, FANConstants.API_getCommonCodeList, docIpGetCommonCodeList);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		Document outDoc = null;
		try {
			String trackingTemplate = "<TrackingNumber URL=\"\" RequestNo=\"\" />";
			outDoc = XMLUtil.createDocument("TrackingNumbers");

			NodeList nltrackingNodeList = XPathUtil.getXpathNodeList(inDoc, "//TrackingNumbers/TrackingNumber");
			for(int nltackingCount =0 ; nltackingCount < nltrackingNodeList.getLength(); nltackingCount++){
				Element intrackingNumber = (Element) nltrackingNodeList.item(nltackingCount);
				String sscan = intrackingNumber.getAttribute("SCAC");
				Element TrackingNumber = XMLUtil.createChild(outDoc.getDocumentElement(), "TrackingNumber");
				String surl = null;
				NodeList nlCommandCodeList = XPathUtil.getXpathNodeList(docOPGetCommonCodeList, "//CommonCodeList/CommonCode");
				for(int commandcodeListCount =0; commandcodeListCount<nlCommandCodeList.getLength(); commandcodeListCount++){
					log.info("Command Code Length: "+nlCommandCodeList.getLength());
					Element nCommandCode = (Element) nlCommandCodeList.item(commandcodeListCount);
					String sCodeValue = XMLUtil.getAttribute(nCommandCode, "CodeValue");
					if(sCodeValue.compareToIgnoreCase(sscan)==0){
						surl = XMLUtil.getAttribute(nCommandCode, "CodeShortDescription");
						break;
					}
				}
				XMLUtil.setAttribute(TrackingNumber, "URL", surl);
				XMLUtil.setAttribute(TrackingNumber, "RequestNo", XMLUtil.getAttribute(intrackingNumber, "RequestNo"));
				log.info("trackingDoc: "+XMLUtil.getElementXMLString(TrackingNumber));
				//outDoc.createElement("TrackingNumber").appendChild(TrackingNumber);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Output XML: \n"+XMLUtil.getXMLString(outDoc));
		return outDoc;
	}

}
