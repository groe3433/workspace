package com.nwcg.icbs.yantra.api.refurb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGTransferRefurbOrder implements YIFCustomApi 
{
	public void setProperties(Properties arg0) throws Exception 
	{}

	public Document transferRefurbOrder(YFSEnvironment env,Document inDoc) throws Exception
	{		
		System.out.println("NWCGTransferRefurbOrder: transferRefurbOrder: inDoc\n" + XMLUtil.getXMLString(inDoc));
		Document createOrderInputDoc = CommonUtilities.invokeService(env, "NWCGTransformRefurbOrder", inDoc);
		System.out.println("NWCGTransferRefurbOrder: transferRefurbOrder: createOrderInputDoc\n" + XMLUtil.getXMLString(createOrderInputDoc));

		addAdditionalElements(env, createOrderInputDoc, inDoc);

		Document opTemplate = XMLUtil.getDocument("<Order OrderHeaderKey=\"\" OrderNo=\"\" />");

		System.out.println("NWCGTransferRefurbOrder:transferRefurbOrder:createOrderInputDoc\n" + XMLUtil.getXMLString(createOrderInputDoc));
		Document opDoc = CommonUtilities.invokeAPI(env, opTemplate, "createOrder", createOrderInputDoc);
		System.out.println("NWCGTransferRefurbOrder:transferRefurbOrder:opDoc\n" + XMLUtil.getXMLString(opDoc));

		updateOriginalRefurbLines(env, inDoc);

		return opDoc;
	}

	private void addAdditionalElements(YFSEnvironment env, Document createOrderInputDoc, Document inDoc) throws Exception
	{
		Element createOrderRootElement = createOrderInputDoc.getDocumentElement();
		String destinationCache = createOrderRootElement.getAttribute("ReceivingNode");

		Element mwoListElement = inDoc.getDocumentElement();
		String masterWorkOrderKey = mwoListElement.getAttribute("MasterWorkOrderKey");
		Document masterWorkOrderDetailsDoc = getMasterWorkOrderDetails(env, masterWorkOrderKey);
		Element masterWorkOrderElement = masterWorkOrderDetailsDoc.getDocumentElement();

		String enterprise = masterWorkOrderElement.getAttribute("Enterprise");
		String shipNode = masterWorkOrderElement.getAttribute("Node");
		String masterWorkOrderNo = masterWorkOrderElement.getAttribute("MasterWorkOrderNo");

		createOrderRootElement.setAttribute("EnterpriseCode", enterprise);
		createOrderRootElement.setAttribute("ShipNode", shipNode);

		String incidentNo = masterWorkOrderElement.getAttribute("IncidentNo");
		String incidentYear = masterWorkOrderElement.getAttribute("IncidentYear");
		String incidentName = masterWorkOrderElement.getAttribute("IncidentName");
		String incidentType = masterWorkOrderElement.getAttribute("IncidentType");

		Element extnElement = createOrderInputDoc.createElement("Extn");
		createOrderRootElement.appendChild(extnElement);
		extnElement.setAttribute("ExtnIncidentNo", incidentNo);
		extnElement.setAttribute("ExtnIncidentYear", incidentYear);
		extnElement.setAttribute("ExtnIncidentName", incidentName);
		extnElement.setAttribute("ExtnIncidentType", incidentType);
		extnElement.setAttribute("ExtnRefurbWO", masterWorkOrderNo);		

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar currentDate = Calendar.getInstance();

		Element orderDatesElement = createOrderInputDoc.createElement("OrderDates");
		Element orderDateElement = createOrderInputDoc.createElement("OrderDate");
		orderDatesElement.appendChild(orderDateElement);
		createOrderRootElement.appendChild(orderDatesElement);
		orderDateElement.setAttribute("ExpectedDate", dateFormat.format(currentDate.getTime()));		

		Element personInfoShipToElement = createOrderInputDoc.createElement("PersonInfoShipTo");
		Element personInfoBillToElement = createOrderInputDoc.createElement("PersonInfoBillTo");
		createOrderRootElement.appendChild(personInfoShipToElement);
		createOrderRootElement.appendChild(personInfoBillToElement);

		Document orgListTemplate = XMLUtil.getDocument("<OrganizationList><Organization OrganizationCode=\"\"><BillingPersonInfo/></Organization></OrganizationList>");
		Document ipOrgListDoc = XMLUtil.getDocument("<Organization OrganizationCode=\"" + destinationCache + "\" />");
		Document opOrgListDoc = CommonUtilities.invokeAPI(env, orgListTemplate, "getOrganizationList", ipOrgListDoc);

		Element billingPersonInfoElement = (Element)opOrgListDoc.getElementsByTagName("BillingPersonInfo").item(0);
		if(billingPersonInfoElement != null)
		{
			copyAddress(billingPersonInfoElement, personInfoShipToElement);
			copyAddress(billingPersonInfoElement, personInfoBillToElement);
		}

		personInfoShipToElement = removeExtraAttributesFromPersonInfoElement(personInfoShipToElement);			
		personInfoBillToElement = removeExtraAttributesFromPersonInfoElement(personInfoBillToElement);			

		addAcountCodes(env, extnElement, shipNode, destinationCache);
	}

	private void addAcountCodes(YFSEnvironment env, Element orderExtnElement, String shipNode, String destinationNode) throws Exception
	{
		//replicated the functionality from nwcg_new_cache_transfer.js --> updateAccountCodes()
		Document orgListTemplate = XMLUtil.getDocument("<OrganizationList><Organization OrganizationCode=\"\"><Extn ExtnOwnerAgency=\"\"/></Organization></OrganizationList>");
		Document ipOrgListDoc = XMLUtil.getDocument("<Organization OrganizationCode=\"" + shipNode + "\" />");
		Document opOrgListDoc = CommonUtilities.invokeAPI(env, orgListTemplate, "getOrganizationList", ipOrgListDoc);
		String shipOwnerAgency = ((Element)opOrgListDoc.getDocumentElement().getElementsByTagName("Extn").item(0)).getAttribute("ExtnOwnerAgency");

		Document ipDoc = XMLUtil.getDocument("<Organization OrganizationCode=\"" + destinationNode + "\" />");   
		Document opDoc = CommonUtilities.invokeService(env, "NWCGUpdateAccountCodes", ipDoc);
		Element extnElement = (Element)opDoc.getDocumentElement().getElementsByTagName("Extn").item(0);

		if(extnElement != null)
		{
			String receivingAccountCode = extnElement.getAttribute("ExtnRecvAcctCode");
			String shippingAccountCode = extnElement.getAttribute("ExtnShipAcctCode");
			String receivingOverrideCode = extnElement.getAttribute("ExtnRAOverrideCode");
			String shippingOverrideCode = extnElement.getAttribute("ExtnSAOverrideCode");
			String extnFSTransferShipAcctCode = extnElement.getAttribute("ExtnFSTransferShipAcctCode");
			String extnFSTransferSAOverrideCode = extnElement.getAttribute("ExtnFSTransferSAOverrideCode");
			String extnBLMTransferShipAcctCode = extnElement.getAttribute("ExtnBLMTransferShipAcctCode");
			String extnOtherTransferShipAcctCode = extnElement.getAttribute("ExtnOtherTransferShipAcctCode");
			String extnFSTransferOrderAcctCode = extnElement.getAttribute("ExtnFSTransferOrderAcctCode");
			String extnFSTransferOrderOverrideCode = extnElement.getAttribute("ExtnFSTransferOrderOverrideCode");
			String extnBLMTransferOrderAcctCode = extnElement.getAttribute("ExtnBLMTransferOrderAcctCode");
			String extnOtherTransferOrderAcctCode = extnElement.getAttribute("ExtnOtherTransferOrderAcctCode");
			String receivingOwnerAgency = extnElement.getAttribute("ExtnOwnerAgency");
			String incidentFSAcctCode = extnElement.getAttribute("IncidentFSAcctCode");
			String incidentOverrideCode = extnElement.getAttribute("IncidentOverrideCode");
			String incidentBLMAcctCode = extnElement.getAttribute("IncidentBlmAcctCode");
			String incidentOtherAcctCode = extnElement.getAttribute("IncidentOtherAcctCode");

			String sAccountCode = "";
			String sOverrideCode = "";
			String fsOrderAccountCode = "";
			String blmOrderAccountCode = "";
			String otherOrderAccountCode = "";
			String fsOrderOverrideCode = "";

			if (receivingOwnerAgency.equals(shipOwnerAgency))
			{
				if (shipOwnerAgency.equals("BLM"))
				{
					sAccountCode = (incidentBLMAcctCode != null && !incidentBLMAcctCode.trim().equals(""))? incidentBLMAcctCode : shippingAccountCode;
					blmOrderAccountCode = (incidentBLMAcctCode != null && !incidentBLMAcctCode.trim().equals(""))? incidentBLMAcctCode : receivingAccountCode;
				}
				else if (shipOwnerAgency.equals("FS"))
				{
					sAccountCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentFSAcctCode : shippingAccountCode;
					sOverrideCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentOverrideCode : shippingOverrideCode;
					fsOrderAccountCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentFSAcctCode : receivingAccountCode;
					fsOrderOverrideCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentOverrideCode : receivingOverrideCode;
				}
				else 
				{
					sAccountCode = (incidentOtherAcctCode != null && !incidentOtherAcctCode.trim().equals(""))? incidentOtherAcctCode : shippingAccountCode;
					otherOrderAccountCode = (incidentOtherAcctCode != null && !incidentOtherAcctCode.trim().equals(""))? incidentOtherAcctCode : receivingAccountCode;
				}
			}
			else
			{
				if (shipOwnerAgency.equals("BLM"))
				{
					sAccountCode = (incidentBLMAcctCode != null && !incidentBLMAcctCode.trim().equals(""))? incidentBLMAcctCode : extnBLMTransferShipAcctCode;
					blmOrderAccountCode = (incidentBLMAcctCode != null && !incidentBLMAcctCode.trim().equals(""))? incidentBLMAcctCode : extnBLMTransferOrderAcctCode;
				}
				else if (shipOwnerAgency.equals("FS"))
				{
					sAccountCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentFSAcctCode : extnFSTransferShipAcctCode;
					sOverrideCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentOverrideCode : extnFSTransferSAOverrideCode;
					fsOrderAccountCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentFSAcctCode : extnFSTransferOrderAcctCode;
					fsOrderOverrideCode = (incidentFSAcctCode != null && !incidentFSAcctCode.trim().equals(""))? incidentOverrideCode : extnFSTransferOrderOverrideCode;
				}
				else 
				{
					sAccountCode = (incidentOtherAcctCode != null && !incidentOtherAcctCode.trim().equals(""))? incidentOtherAcctCode : extnOtherTransferShipAcctCode;
					otherOrderAccountCode = (incidentOtherAcctCode != null && !incidentOtherAcctCode.trim().equals(""))? incidentOtherAcctCode : extnOtherTransferOrderAcctCode;
				}
			}

			if(receivingAccountCode != null && !receivingAccountCode.trim().equals("")) 
				orderExtnElement.setAttribute("ExtnRecvAcctCode", receivingAccountCode);
			if(receivingOverrideCode != null && !receivingOverrideCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnRAOverrideCode", receivingOverrideCode);
			if(sAccountCode != null && !sAccountCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnShipAcctCode", sAccountCode);
			if(sOverrideCode != null && !sOverrideCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnSAOverrideCode", sOverrideCode);
			if(blmOrderAccountCode != null && !blmOrderAccountCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnBlmAcctCode", blmOrderAccountCode);
			if(otherOrderAccountCode != null && !otherOrderAccountCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnOtherAcctCode", otherOrderAccountCode);
			if(fsOrderAccountCode != null && !fsOrderAccountCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnFsAcctCode", fsOrderAccountCode);
			if(fsOrderOverrideCode != null && !fsOrderOverrideCode.trim().equals(""))
				orderExtnElement.setAttribute("ExtnOverrideCode", fsOrderOverrideCode);
		}
	}

	private void updateOriginalRefurbLines(YFSEnvironment env, Document inDoc) throws Exception 
	{
		Element masterWorkOrderLineListElement = inDoc.getDocumentElement();

		NodeList masterWorkOrderLineList = masterWorkOrderLineListElement.getElementsByTagName("NWCGMasterWorkOrderLine");
		if(masterWorkOrderLineList != null && masterWorkOrderLineList.getLength() > 0)
		{
			for(int i = 0; i < masterWorkOrderLineList.getLength(); i++)
			{
				Element masterWorkOrderLineElement = (Element)masterWorkOrderLineList.item(i);
				String masterWorkOrderLineKey = masterWorkOrderLineElement.getAttribute("MasterWorkOrderLineKey");
				
				String actualQty = masterWorkOrderLineElement.getAttribute("ActualQuantity");
				String oldTransferQty = masterWorkOrderLineElement.getAttribute("TransferredQuantity");
				String newTransferQty = masterWorkOrderLineElement.getAttribute("TransferQuantity");
				String refurbQty = masterWorkOrderLineElement.getAttribute("RefurbishedQuantity");

				actualQty = ((actualQty == null) || (actualQty != null && actualQty.trim().equals("")))? actualQty = "0.0" : actualQty;
				oldTransferQty = ((oldTransferQty == null) || (oldTransferQty != null && oldTransferQty.trim().equals("")))? oldTransferQty = "0.0" : oldTransferQty;
				newTransferQty = ((newTransferQty == null) || (newTransferQty != null && newTransferQty.trim().equals("")))? newTransferQty = "0.0" : newTransferQty;
				refurbQty = ((refurbQty == null) || (refurbQty != null && refurbQty.trim().equals("")))? refurbQty = "0.0" : refurbQty;
				
				double dActualQty = Double.parseDouble(actualQty);
				double dRefurbQty = Double.parseDouble(refurbQty);
				double dOldTransferQty = Double.parseDouble(oldTransferQty);
				double dNewTransferQty = Double.parseDouble(newTransferQty);
				double totalTransferQty = dOldTransferQty + dNewTransferQty;
				double dRemainingQty = dActualQty - dRefurbQty - totalTransferQty;
				String status = (dRemainingQty > 0)? NWCGConstants.NWCG_REFURB_MWOL_INTERMEDTIATE_STATUS : (totalTransferQty == dActualQty)? NWCGConstants.NWCG_REFURB_MWOL_TRANSFERRED_STATUS : NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS;

				Document ipDoc = XMLUtil.getDocument("<NWCGMasterWorkOrderLine MasterWorkOrderLineKey=\"" + masterWorkOrderLineKey + "\" Status=\"" + status + "\" TransferQty=\"" + totalTransferQty + "\"/>");   
				System.out.println("NWCGTransferRefurbOrder:updateOriginalRefurbLines:ipDoc\n" + XMLUtil.getXMLString(ipDoc));
				Document opDoc = CommonUtilities.invokeService(env, "NWCGChangeMasterWorkOrderLineService", ipDoc);
				System.out.println("NWCGTransferRefurbOrder:updateOriginalRefurbLines:opDoc\n" + XMLUtil.getXMLString(opDoc));
			}
		}
	}

	private Document getMasterWorkOrderDetails(YFSEnvironment env, String masterWorkOrderKey) throws Exception
	{
		Document ipDoc = XMLUtil.createDocument("NWCGMasterWorkOrderDetail");
		ipDoc.getDocumentElement().setAttribute("MasterWorkOrderKey", masterWorkOrderKey);
		Document opDoc = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderDetailsService", ipDoc);
		System.out.println("NWCGTransferRefurbOrder:getMasterWorkOrderDetails:opDoc\n" + XMLUtil.getXMLString(opDoc));
		return opDoc;
	}	

	private void copyAddress(Element from, Element to) 
	{
		NamedNodeMap attrMap = from.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) 
		{
			Attr attr = (Attr) attrMap.item(i);
			String name = attr.getName();
			String value = attr.getValue();
			to.setAttribute(name, value);
		}
	}

	private Element removeExtraAttributesFromPersonInfoElement(Element aPersonInfoElm) 
	{
		aPersonInfoElm.removeAttribute("Createprogid");		
		aPersonInfoElm.removeAttribute("Createts");
		aPersonInfoElm.removeAttribute("Createuserid");		
		aPersonInfoElm.removeAttribute("Modifyprogid");
		aPersonInfoElm.removeAttribute("Modifyts");
		aPersonInfoElm.removeAttribute("Modifyuserid");		
		aPersonInfoElm.removeAttribute("UseCount");
		aPersonInfoElm.removeAttribute("VerificationStatus");
		aPersonInfoElm.removeAttribute("isHistory");
		aPersonInfoElm.removeAttribute("Lockid");		
		return aPersonInfoElm;
	}
}