package com.nwcg.icbs.yantra.api.refurb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSanitizedCacheList implements YIFCustomApi {
	public static final String HVT = "HIGH_VOLUME_TEMPLATE";
	public static final String LVT = "LOW_VOLUME_TEMPLATE";

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document getSanitizedCacheList(YFSEnvironment env, Document inDoc)
			throws Exception {

		Document ipTemplate = XMLUtil
				.getDocument("<ShipNodeList TotalNumberOfRecords=\"\"><ShipNode ShipNode=\"\" /></ShipNodeList>");
		Document opDoc = CommonUtilities.invokeAPI(env, ipTemplate,
				"getShipNodeList", inDoc);

		Element shipNodeListElement = opDoc.getDocumentElement();
		NodeList shipNodeList = opDoc.getElementsByTagName("ShipNode");

		for (int i = 0; i < shipNodeList.getLength(); i++) {
			Element currentShipNodeElement = (Element) shipNodeList.item(i);
			if (currentShipNodeElement.getAttribute("ShipNode").equals(HVT)
					|| currentShipNodeElement.getAttribute("ShipNode").equals(
							LVT)) {
				shipNodeListElement.removeChild(currentShipNodeElement);
			}
		}

		return opDoc;
	}
}