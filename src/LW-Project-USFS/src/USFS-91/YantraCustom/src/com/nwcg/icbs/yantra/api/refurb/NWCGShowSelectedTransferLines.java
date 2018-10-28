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

public class NWCGShowSelectedTransferLines implements YIFCustomApi {
	public void setProperties(Properties arg0) throws Exception {
	}

	public Document showSelectedTransferLines(YFSEnvironment env, Document inDoc)
			throws Exception {

		Document masterWorkOrderLineListDoc = XMLUtil
				.createDocument("NWCGMasterWorkOrderLineList");
		Element rootElement = masterWorkOrderLineListDoc.getDocumentElement();

		Element selectedLinesElement = inDoc.getDocumentElement();
		NodeList selectedLineList = selectedLinesElement
				.getElementsByTagName("SelectedLine");

		if (selectedLineList != null && selectedLineList.getLength() > 0) {
			for (int i = 0; i < selectedLineList.getLength(); i++) {
				Element selectedLineElement = (Element) selectedLineList
						.item(i);
				String masterWorkOrderLineKey = selectedLineElement
						.getAttribute("SelectedKey");

				Document ipDoc = XMLUtil
						.getDocument("<NWCGMasterWorkOrderLine MasterWorkOrderLineKey=\""
								+ masterWorkOrderLineKey + "\" />");
				Document opDoc = CommonUtilities.invokeService(env,
						"NWCGGetMasterWorkOrderLineDetailsService", ipDoc);
				Element currentMWOLineElement = opDoc.getDocumentElement();

				Element masterWorkOrderLineElement = masterWorkOrderLineListDoc
						.createElement("NWCGMasterWorkOrderLine");
				rootElement.appendChild(masterWorkOrderLineElement);
				XMLUtil.copyElement(masterWorkOrderLineListDoc,
						currentMWOLineElement, masterWorkOrderLineElement);
			}
		}

		return masterWorkOrderLineListDoc;
	}
}