package com.nwcg.icbs.yantra.api.refurb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGReceiveTransferRefurbOrder implements YIFCustomApi {
	public void setProperties(Properties arg0) throws Exception {
	}

	public Document receiveTransferRefurbOrder(YFSEnvironment env,
			Document inDoc) throws Exception {
		Element receiptElement = inDoc.getDocumentElement();
		NodeList receiptLineList = receiptElement
				.getElementsByTagName("ReceiptLine");
		if (receiptLineList != null && receiptLineList.getLength() > 0) {
			Element receiptLineFirstElement = (Element) receiptLineList.item(0);
			String dispositionCode = receiptLineFirstElement
					.getAttribute("DispositionCode");
			if (dispositionCode != null && dispositionCode.equals("NRFI")) {
				createMasterWorkOrder(env, inDoc);
			}
		}

		return inDoc;
	}

	private Document createMasterWorkOrder(YFSEnvironment env, Document inXML)
			throws Exception {
		Document opDoc = CommonUtilities.invokeService(env,
				"NWCGTransformReceipt", inXML);
		Element mwoRootElement = opDoc.getDocumentElement();
		Element masterWorkOrderElement = (Element) mwoRootElement
				.getElementsByTagName("NWCGMasterWorkOrder").item(0);

		updateReceipt(env, masterWorkOrderElement);
		addAccountCodes(env, masterWorkOrderElement);

		Document opMasterWorkOrderDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME,
				XMLUtil.getElementXMLString(masterWorkOrderElement));

		String masterWorkOrderKey = opMasterWorkOrderDoc.getDocumentElement()
				.getAttribute("MasterWorkOrderKey");

		NodeList masterWorkOrderLines = mwoRootElement
				.getElementsByTagName("NWCGMasterWorkOrderLine");
		for (int i = 0; i < masterWorkOrderLines.getLength(); i++) {
			Element masterWorkOrderLineElement = (Element) masterWorkOrderLines
					.item(i);
			masterWorkOrderLineElement.setAttribute("MasterWorkOrderKey",
					masterWorkOrderKey);

			CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME,
							XMLUtil.getElementXMLString(masterWorkOrderLineElement));
		}

		return opDoc;
	}

	private void updateReceipt(YFSEnvironment env, Element mwoElement)
			throws Exception {
		String receiptHeaderKey = mwoElement.getAttribute("ReceiptHeaderKey");
		String incidentNo = mwoElement.getAttribute("IncidentNo");
		String incidentYear = mwoElement.getAttribute("IncidentYear");

		Document ipDoc = XMLUtil.getDocument("<Receipt ReceiptHeaderKey=\""
				+ receiptHeaderKey + "\" ><Extn ExtnIncidentNo=\"" + incidentNo
				+ "\" ExtnIncidentYear=\"" + incidentYear
				+ "\" ExtnIsReturnReceipt=\"Y\" /></Receipt>");
		CommonUtilities.invokeAPI(env, "changeReceipt", ipDoc);
	}

	private void addAccountCodes(YFSEnvironment env, Element mwoElement)
			throws Exception {
		// fetch account code info from the incident
		String node = mwoElement.getAttribute("Node");
		String incidentNo = mwoElement.getAttribute("IncidentNo");
		String incidentYear = mwoElement.getAttribute("IncidentYear");

		Document opOrgTemplate = XMLUtil
				.getDocument("<OrganizationList><Organization OrganizationCode=\"\"><Extn ExtnOwnerAgency=\"\"/></Organization></OrganizationList>");
		Document ipOrgDoc = XMLUtil
				.getDocument("<Organization OrganizationCode=\"" + node
						+ "\" />");
		Document opOrgDoc = CommonUtilities.invokeAPI(env, opOrgTemplate,
				"getOrganizationList", ipOrgDoc);
		String ownerAgency = ((Element) opOrgDoc.getElementsByTagName("Extn")
				.item(0)).getAttribute("ExtnOwnerAgency");

		Document ipIncidentDoc = XMLUtil
				.getDocument("<NWCGIncidentOrder IncidentNo=\"" + incidentNo
						+ "\" Year=\"" + incidentYear + "\" />");
		Document opIncidentDoc = CommonUtilities.invokeService(env,
				"NWCGGetIncidentOrderService", ipIncidentDoc);

		Element incidentOrderElement = opIncidentDoc.getDocumentElement();
		String incidenBLMAccountCode = incidentOrderElement
				.getAttribute("IncidentBlmAcctCode");
		String incidenFSAccountCode = incidentOrderElement
				.getAttribute("IncidentFsAcctCode");
		String incidenOTHERAccountCode = incidentOrderElement
				.getAttribute("IncidentOtherAcctCode");
		String incidenOverrideCode = incidentOrderElement
				.getAttribute("OverrideCode");

		if (ownerAgency.equals("FS")) {
			if (incidenFSAccountCode != null
					&& !incidenFSAccountCode.trim().equals("")) {
				mwoElement.setAttribute("FSAccountCode", incidenFSAccountCode);
				mwoElement.setAttribute("OverrideCode", incidenOverrideCode);
			} else {
				throw new NWCGException(
						"Please update FS Account code and Override code for Incident No. "
								+ incidentNo);
			}
		} else if (ownerAgency.equals("BLM")) {
			if (incidenBLMAccountCode != null
					&& !incidenBLMAccountCode.trim().equals("")) {
				mwoElement
						.setAttribute("BLMAccountCode", incidenBLMAccountCode);
			} else {
				throw new NWCGException(
						"Please update BLM Account code for Incident No. "
								+ incidentNo);
			}
		} else {
			if (incidenOTHERAccountCode != null
					&& !incidenOTHERAccountCode.trim().equals("")) {
				mwoElement.setAttribute("OtherAccountCode",
						incidenOTHERAccountCode);
			} else {
				throw new NWCGException(
						"Please update OTHER Account code for Incident No. "
								+ incidentNo);
			}
		}
	}
}