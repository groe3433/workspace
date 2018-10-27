package com.fanatics.sterling.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.FancashSettlementRefundConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FancashSettlementRefund implements YIFCustomApi{

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props;

	public Document validate(YFSEnvironment env, Document inDoc) throws Exception {
		logger.info("Inside Fancash Settlement validation");
		Document outDoc = null;

		String sInvoiceType = XPathUtil.getXpathAttribute(inDoc, FancashSettlementRefundConstants.FANCASH_XPATH_INVOICE_TYPE);
		logger.info("InvoiceType: "+sInvoiceType ); 

		//Check InvoiceType=SHIPMENT and then only go for Fancash Settlement
		if (sInvoiceType.compareTo(FancashSettlementRefundConstants.FANCASH_INVOICE_TYPE_SHIPMENT)==0 || sInvoiceType.compareTo(FancashSettlementRefundConstants.FANCASH_INVOICE_TYPE_RETURN)==0){
			outDoc = fancashSettlementRefund(env, inDoc, sInvoiceType);
		}
		if(outDoc==null){
			outDoc = XMLUtil.createDocument("FanCash");
			XMLUtil.setAttribute(outDoc.getDocumentElement(), "IsFancash", "false");
		}
		return outDoc;
	}

	public Document fancashSettlementRefund (YFSEnvironment env, Document inDoc, String sInvType) throws Exception {

		logger.info("validation successful");
		String sInvoiceType = sInvType;
		Document dFanCashInputDoc = null;

		Double dChargeAmount = 0.00;
		NodeList nlLineDetails = XPathUtil.getXpathNodeList(inDoc, FancashSettlementRefundConstants.FANCASH_XPATH_LINEDETAIL);
		for (int iLineDetailCount = 0; iLineDetailCount <= nlLineDetails.getLength()-1; iLineDetailCount++){
			//Each LineDetail Node
			Node nLineDetail = nlLineDetails.item(iLineDetailCount);
			NodeList nlLineCharges = XPathUtil.getNodeList(nLineDetail, FancashSettlementRefundConstants.FANCASH_XPATH_LINECHARGE);
			for (int iLineChargeCount = 0; iLineChargeCount <= nlLineCharges.getLength()-1; iLineChargeCount++){
				//Each LineCharge element
				Element eLineCharge = (Element) nlLineCharges.item(iLineChargeCount);
				String sChargeName = eLineCharge.getAttribute(FancashSettlementRefundConstants.FANCASH_CHARGE_NAME);
				String sChargeCategory = eLineCharge.getAttribute(FancashSettlementRefundConstants.FANCASH_CHARGE_CATEGORY);
				//Either ChargeName or ChargeCategory should be Fancash
				if(sChargeName.compareTo(FancashSettlementRefundConstants.FANCASH_CHARGE_NAME_VALUE)==0 || sChargeCategory.compareTo(FancashSettlementRefundConstants.FANCASH_CHARGE_NAME_VALUE)==0){
					//Get the chargeAmount and add that
					dChargeAmount = dChargeAmount+Double.valueOf(eLineCharge.getAttribute(FancashSettlementRefundConstants.FANCASH_CHARGE_AMOUNT));
				}
			}
		}
		if(dChargeAmount>0){
			logger.info("Fancash Amount: "+dChargeAmount);

			//Prepare Input document for getOrderList API
			Document dOrderListInputDoc = XMLUtil.createDocument("Order");
			dOrderListInputDoc.getDocumentElement().setAttribute("OrderHeaderKey", XPathUtil.getXpathAttribute(inDoc, FancashSettlementRefundConstants.FANCASH_XPATH_ORDER_HEADER_KEY));
			logger.info("OrderList Input document: "+XMLUtil.getXMLString(dOrderListInputDoc));

			//Prepare Template for getOrderList API
			Document dOrderListTemplateDoc = XMLUtil.getDocument(FancashSettlementRefundConstants.FANCASH_ORDERLIST_TEMPLATE);
			logger.info("OrderList Template Document: "+XMLUtil.getXMLString(dOrderListTemplateDoc));

			//Call getOrderList API
			Document dOrderListOutputDoc = CommonUtil.invokeAPI(env, dOrderListTemplateDoc, "getOrderList", dOrderListInputDoc);
			logger.info("OrderList Output Document: "+XMLUtil.getXMLString(dOrderListOutputDoc));

			//Get Fancash Settlement template from constants
			dFanCashInputDoc = XMLUtil.getDocument(FancashSettlementRefundConstants.FANCASH_SETTLEMENT_INPUT_TEMPLATE);

			// set all attributes in the template for Fancash element
			if(sInvoiceType.compareTo(FancashSettlementRefundConstants.FANCASH_INVOICE_TYPE_SHIPMENT)==0)
				XMLUtil.setAttribute(dFanCashInputDoc.getDocumentElement(), "AmountToDeduct", ""+dChargeAmount);
			else if(sInvoiceType.compareTo(FancashSettlementRefundConstants.FANCASH_INVOICE_TYPE_RETURN)==0)
				XMLUtil.setAttribute(dFanCashInputDoc.getDocumentElement(), "AmountToAdd", ""+dChargeAmount);
			
			XMLUtil.setAttribute(dFanCashInputDoc.getDocumentElement(), "BillToID", XPathUtil.getXpathAttribute(dOrderListOutputDoc, FancashSettlementRefundConstants.FANCASH_XPATH_BILLTOID));
			
			// set all attributes in the template for Order element
			Element eOrder = (Element) dFanCashInputDoc.getElementsByTagName("Order").item(0);
			XMLUtil.setAttribute(eOrder, "OrderNo", XPathUtil.getXpathAttribute(inDoc, FancashSettlementRefundConstants.FANCASH_XPATH_ORDER_NO));
			
			Element eCustomerAttributes = (Element) dFanCashInputDoc.getElementsByTagName("CustomAttributes").item(0);
			XMLUtil.setAttribute(eCustomerAttributes, "CustomerOrderNo", XPathUtil.getXpathAttribute(dOrderListOutputDoc, FancashSettlementRefundConstants.FANCASH_XPATH_CUSTOMER_ORDER_NO));
			
			Element eOrderLines = (Element) dFanCashInputDoc.getElementsByTagName("OrderLines").item(0);
			
			NodeList nlLineDetail = XPathUtil.getXpathNodeList(inDoc, FancashSettlementRefundConstants.FANCASH_XPATH_LINEDETAIL);
			logger.info("nlLineDetail Count: "+nlLineDetail.getLength());
			for(int iLineDetailCount =0 ; iLineDetailCount<nlLineDetail.getLength(); iLineDetailCount++){
				Element eOrderLine = (Element) dFanCashInputDoc.getElementsByTagName("OrderLine").item(0);
				Element eOrderLineItem = (Element) eOrderLine.getElementsByTagName("Item").item(0);
				Element eLineDetail = (Element) nlLineDetail.item(iLineDetailCount);
				Element eOrderLineInv = (Element) eLineDetail.getElementsByTagName("OrderLine").item(0);
				NamedNodeMap nnmAttributes = eOrderLineInv.getAttributes();
				for( int nnmCount =0; nnmCount < nnmAttributes.getLength(); nnmCount++){
					Node attrNode = nnmAttributes.item(nnmCount);
					logger.info(attrNode.getNodeName()+" "+attrNode.getNodeValue());
					eOrderLine.setAttribute(attrNode.getNodeName().toLowerCase(), attrNode.getNodeValue().toString());
				}
				Element eOrderLineInvItem = (Element) eOrderLineInv.getElementsByTagName("Item").item(0);
				
				NamedNodeMap nnmItemAttributes = eOrderLineInvItem.getAttributes();
				for( int nnmCount =0; nnmCount < nnmItemAttributes.getLength(); nnmCount++){
					Node attrNode = nnmItemAttributes.item(nnmCount);
					logger.info(attrNode.getNodeName()+" "+attrNode.getNodeValue());
					eOrderLineItem.setAttribute(attrNode.getNodeName().toLowerCase(), attrNode.getNodeValue().toString());
				}
				eOrderLine.appendChild(eOrderLineItem);
				eOrderLines.appendChild(eOrderLine);
			}
			
			logger.info("Input XML FanCash Settlement"+ XMLUtil.getXMLString(dFanCashInputDoc));

		}
		return dFanCashInputDoc;
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		props = arg0;

	}

}
