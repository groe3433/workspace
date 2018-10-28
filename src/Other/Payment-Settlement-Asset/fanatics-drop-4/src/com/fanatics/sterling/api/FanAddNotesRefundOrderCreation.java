package com.fanatics.sterling.api;
	
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.FanSVSConfirmShipmentConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanAddNotesRefundOrderCreation {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	
	public void addNotes(YFSEnvironment env, Document inDoc){
		
		// get the OrderHeaderKey of the refund order
		Element eleRootInDoc = inDoc.getDocumentElement();
		
		String strRefOrderHeaderKey = eleRootInDoc.getAttribute(FANConstants.ATT_ORDER_HEADER_KEY);
		String strOrderNo = eleRootInDoc.getAttribute(FANConstants.ORDER_NO);
		
		/**
		 * Hit an OOB api with template, to get the OrderHerderKey of the corresponding Sales order
		 */
		Document docTempGetOrderList = null;
		Document docIPGetOrderList = null;
		Document docOPGetOrderList = null;
		//Document docIPGetChargeTxnList = null;
		
		try {		
		docIPGetOrderList = XMLUtil.getDocument("<Order OrderHeaderKey='"+strRefOrderHeaderKey+"' />");		
		
		docTempGetOrderList = XMLUtil.getDocument("<OrderList TotalNumberOfRecords=''><Order OrderPurpose=''><ChargeTransactionDetails TotalTransferredIn=''>" +
					                              "<ChargeTransactionDetail TransferFromOhKey=''><TransferFromOrder OrderHeaderKey=''/></ChargeTransactionDetail>" +
					                              "</ChargeTransactionDetails></Order></OrderList>");
		
		docOPGetOrderList = CommonUtil.invokeAPI(env, FANConstants.API_getOrderList, docIPGetOrderList);
		
		logger.verbose("docOPGetOrderList 1 "+ XMLUtil.getXMLString(docOPGetOrderList));
		
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * Add Notes only if its a Refund Order
		 */
		String strOrderPurpose = XMLUtil.getXpathProperty(docOPGetOrderList, "OrderList/Order/@OrderPurpose");
		
		logger.verbose("purpose is "+ strOrderPurpose);
		
		String strOrderHeadKeySales = FANConstants.NO_VALUE;
		String strTransferFromOhKey = FANConstants.NO_VALUE;
				
		if (strOrderPurpose.equals("REFUND")){
		
			logger.verbose("its inside the refund code now ");
			
			NodeList nlChargeTransactionDetail = docOPGetOrderList.getElementsByTagName(FanSVSConfirmShipmentConstants.ELE_CHARGE_TXN_DET);
			int nlOrderHoldTypeLength = nlChargeTransactionDetail.getLength();
			
			for(int i=0;i<nlOrderHoldTypeLength;i++){
				
				logger.verbose("Index here is "+i);
				Element eleChargeTransactionDetail = (Element) nlChargeTransactionDetail.item(i);
				strTransferFromOhKey = eleChargeTransactionDetail.getAttribute(FanSVSConfirmShipmentConstants.ATT_TRANSFER_FROM_ORD_HEAD_KEY);
				logger.verbose("strTransferFromOhKey is "+strTransferFromOhKey);
				
				if (!strTransferFromOhKey.equals(FANConstants.NO_VALUE) && strTransferFromOhKey != null && !strTransferFromOhKey.equals(" ")){
					logger.verbose("SO Order header key found");
					strOrderHeadKeySales = strTransferFromOhKey;
					break;
				}
			}
			
		logger.verbose("strOrderHeadKeySales is "+ strOrderHeadKeySales);
		
		/**
		 * Hit an OOB api with template, to get the OrderHerderKey of the corresponding Return order
		 */
		
		Document docTempGetChargeTxnList = null;
		Document docIPGetChargeTxnList = null;
		Document docOPGetChargeTxnList = null;
		
		try {		
			docIPGetChargeTxnList = 
					XMLUtil.getDocument("<ChargeTransactionDetail OrderHeaderKey='"+strOrderHeadKeySales+"' TransferToOhKey='"+strRefOrderHeaderKey+"' />");	
			
			docTempGetChargeTxnList = XMLUtil.getDocument("<ChargeTransactionDetails><ChargeTransactionDetail TransferToOhKey=''><InvoiceCollectionDetails>" +
					                                  "<InvoiceCollectionDetail OrderHeaderKey=''/></InvoiceCollectionDetails></ChargeTransactionDetail>" +
					                                  "</ChargeTransactionDetails>");
			
			docOPGetChargeTxnList = CommonUtil.invokeAPI(env, docTempGetChargeTxnList, "getChargeTransactionList", docIPGetChargeTxnList);
			
			logger.verbose("docOPGetChargeTxnList 1 "+ XMLUtil.getXMLString(docOPGetChargeTxnList));
			
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		String strOrderHeadKeyReturn = 
		XMLUtil.getXpathProperty(docOPGetChargeTxnList, "ChargeTransactionDetails/ChargeTransactionDetail/InvoiceCollectionDetails/InvoiceCollectionDetail/@OrderHeaderKey");
		
		logger.verbose("strOrderHeadKeyReturn "+strOrderHeadKeyReturn);
		
		Document docIPChangeOrder = null;
		
		/**
		 * Add notes to the Sales Order
		 */
		try {
			docIPChangeOrder = XMLUtil.getDocument("<Order OrderHeaderKey='"+strOrderHeadKeySales+"' />");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		String strNoteText = "Refund Order #" +strOrderNo+ " was created to fulfill the corresponding refund." ;
		
		Element eleNotes = docIPChangeOrder.createElement(FANConstants.ATT_Notes);
		Element eleNote = docIPChangeOrder.createElement(FANConstants.ATT_Note);
		eleNote.setAttribute(FANConstants.ATT_NoteText, strNoteText);
		
		eleNotes.appendChild(eleNote);
		
		Element eleInputChangeOrderRoot = docIPChangeOrder.getDocumentElement();
		eleInputChangeOrderRoot.appendChild(eleNotes);
		
		logger.verbose("docIPChangeOrder 1 "+ XMLUtil.getXMLString(docIPChangeOrder));
		
		// invoke changeOrder API		
		try {
			CommonUtil.invokeAPI(env, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		/**
		 * Add notes to the Return Order
		 */
		
		// set the order header key
		docIPChangeOrder.getDocumentElement().setAttribute(FANConstants.ATT_ORDER_HEADER_KEY, strOrderHeadKeyReturn);
		logger.verbose("docIPChangeOrder 2 "+ XMLUtil.getXMLString(docIPChangeOrder));
		// invoke changeOrder API		
		try {
			CommonUtil.invokeAPI(env, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	}
}
