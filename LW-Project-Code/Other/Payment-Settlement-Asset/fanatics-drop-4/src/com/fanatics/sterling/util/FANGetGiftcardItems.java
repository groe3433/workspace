package com.fanatics.sterling.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfs.japi.YFSEnvironment;

public class FANGetGiftcardItems {

	public Document getGiftcardItems(YFSEnvironment env, Document inDoc) throws Exception {
		System.out.println("Input Document \n"+XMLUtil.getXMLString(inDoc));

		String sOrderHeaderKey= XPathUtil.getAttribute(inDoc.getDocumentElement(), "OrderHeaderKey");

		Document getChargeTransactionListIn = XMLUtil.createDocument("ChargeTransactionDetail");
		XMLUtil.setAttribute((Element)getChargeTransactionListIn.getDocumentElement(), "OrderHeaderKey", sOrderHeaderKey);
		XMLUtil.setAttribute((Element)getChargeTransactionListIn.getDocumentElement(), "ChargeType", "TRANSFER_OUT");
		String getChargeTransactionListtemp = "<ChargeTransactionDetails><ChargeTransactionDetail TransferToOhKey=\"\"/></ChargeTransactionDetails>";
		Document getChargeTransactionListtempDoc = XMLUtil.getDocument(getChargeTransactionListtemp);

		Document getCompleteOrderLineListOut = CommonUtil.invokeAPI(env, getChargeTransactionListtempDoc, "getChargeTransactionList", getChargeTransactionListIn);

		NodeList nlChargeTransactionDetail = XPathUtil.getNodeList(getCompleteOrderLineListOut, "//ChargeTransactionDetails/ChargeTransactionDetail");

		if(nlChargeTransactionDetail.getLength()!=0){
			Document FanGetListEmailEntryIn = XMLUtil.createDocument("EXTNFanEmail");
			Element eComplexQuery = XMLUtil.createChild(FanGetListEmailEntryIn.getDocumentElement(),"ComplexQuery");
			XMLUtil.setAttribute(eComplexQuery, "Operator", "OR");
			Element eor = XMLUtil.createChild(eComplexQuery,"Or");

			for (int i =0; i<nlChargeTransactionDetail.getLength(); i++){
				Element eChargeTransactionDetail = (Element) nlChargeTransactionDetail.item(i);
				String sTransferToOhKey = XPathUtil.getAttribute(eChargeTransactionDetail, "TransferToOhKey");
				if(!StringUtil.isEmpty(sTransferToOhKey)){
					Element eexp = XMLUtil.createChild(eor,"Exp");
					XMLUtil.setAttribute(eexp, "Name", "OrderHeaderKey");
					XMLUtil.setAttribute(eexp, "QryType", "EQ");
					XMLUtil.setAttribute(eexp, "Value", sTransferToOhKey);
				}
			}

			Document outDoc = CommonUtil.invokeService(env, "FanGetListEmailEntry", FanGetListEmailEntryIn);

			if(outDoc != null){
				Document getCompleteOrderLineListIn = XMLUtil.createDocument("OrderLine");
				eComplexQuery = XMLUtil.createChild(getCompleteOrderLineListIn.getDocumentElement(),"ComplexQuery");
				XMLUtil.setAttribute(eComplexQuery, "Operator", "OR");
				eor = XMLUtil.createChild(eComplexQuery,"Or");

				NodeList nlEXTNFanEmail = outDoc.getElementsByTagName("EXTNFanEmail");
				for(int i=0; i<nlEXTNFanEmail.getLength(); i++){
					Element eEXTNFanEmail = (Element) nlEXTNFanEmail.item(i);
					sOrderHeaderKey = eEXTNFanEmail.getAttribute("OrderHeaderKey");
					Element eexp = XMLUtil.createChild(eor,"Exp");
					XMLUtil.setAttribute(eexp, "Name", "OrderHeaderKey");
					XMLUtil.setAttribute(eexp, "QryType", "EQ");
					XMLUtil.setAttribute(eexp, "Value", sOrderHeaderKey);				
				}

				String template = "<OrderLineList><OrderLine OrderHeaderKey=\"\"><Item CostCurrency=\"\"/><ItemDetails ItemID=\"\"><PrimaryInformation ShortDescription=\"\"/></ItemDetails><LineOverallTotals DisplayUnitPrice=\"\"/></OrderLine></OrderLineList>";
				Document templateDoc = XMLUtil.getDocument(template);

				getCompleteOrderLineListOut = CommonUtil.invokeAPI(env, templateDoc, "getCompleteOrderLineList", getCompleteOrderLineListIn);
				if(getCompleteOrderLineListOut!=null){
					NodeList nlOrderLine = getCompleteOrderLineListOut.getElementsByTagName("OrderLine");
					for(int i =0; i<nlOrderLine.getLength(); i++){
						Element eOrderLine = (Element) nlOrderLine.item(i);
						sOrderHeaderKey = XMLUtil.getAttribute(eOrderLine, "OrderHeaderKey");
						nlEXTNFanEmail = outDoc.getElementsByTagName("EXTNFanEmail");
						for(int j=0; j<nlEXTNFanEmail.getLength(); j++){
							Element eEXTNFanEmail = (Element) nlEXTNFanEmail.item(j);
							String sEXTNFanEmailOrderHeaderKey = eEXTNFanEmail.getAttribute("OrderHeaderKey");
							String semailid = eEXTNFanEmail.getAttribute("EmailID");
							String sEmailKey = eEXTNFanEmail.getAttribute("EmailKey");
							if(sEXTNFanEmailOrderHeaderKey.compareTo(sOrderHeaderKey)==0){
								Element eCustomElement = XMLUtil.createChild(eOrderLine, "CustomElement");
								XMLUtil.setAttribute(eCustomElement, "EmailID", semailid);
								XMLUtil.setAttribute(eCustomElement, "EmailKey", sEmailKey);
							}
						}
					}
				}

			}
		}
		System.out.println("Return Document \n"+XMLUtil.getXMLString(getCompleteOrderLineListOut));
		return getCompleteOrderLineListOut;
	}
}
