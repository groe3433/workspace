package com.fanatics.sterling.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.OrderNotesConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanReturnReceipt implements YIFCustomApi {


	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props = null;

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub/

	}

	public Document orderReceipt (YFSEnvironment env, Document inDoc) throws Exception{


		Document receiveOrderDoc = inDoc;
		Document receiveOrderDoc2 = XMLUtil.createDocument("CopyReceipt");
		Element receiptCloneEle = inDoc.getDocumentElement();
		Element receiptCloneEle2 = (Element) receiveOrderDoc2.importNode(receiptCloneEle.cloneNode(true), true);
		receiveOrderDoc2.getDocumentElement().appendChild(receiptCloneEle2);	
		Element receiptLinesEle = (Element)inDoc.getElementsByTagName("ReceiptLines").item(0);
		NodeList receiptLineList = receiveOrderDoc.getElementsByTagName("ReceiptLine");
		Element retshipmentEle = (Element)receiveOrderDoc.getElementsByTagName("Shipment").item(0);
		String enterpriseCode = retshipmentEle.getAttribute("EnterpriseCode");
		String retOrderNo = retshipmentEle.getAttribute("OrderNo");
		String receivingNode = retshipmentEle.getAttribute("ReceivingNode");
		String salesOrderNo = "";

		Document orderDoc4 = XMLUtil.createDocument("Order");
		Element orderEle4 = orderDoc4.getDocumentElement();
		orderEle4.setAttribute("OrderNo", retOrderNo);
		orderEle4.setAttribute("DocumentType", "0003");
		orderEle4.setAttribute("EnterpriseCode", enterpriseCode);

		logger.verbose("--------------FanReturnReceipt------1---"
				+XMLUtil.getXMLString(orderDoc4));

		Document outDoc4 = CommonUtil.invokeAPI
				(env,"global/template/api/getOrderList_reshipLine.xml","getOrderList", orderDoc4);

		logger.verbose("--------------FanReturnReceipt--------2-"
				+XMLUtil.getXMLString(outDoc4));

		NodeList retOrderList = outDoc4.getElementsByTagName("Order");

		// checking if return order exists, if not new return order will be created
		if(retOrderList.getLength() == 0){
			logger.verbose("----------------------------------2--------2--------------------");

			Document orderDoc = XMLUtil.createDocument("Order");
			Element orderEle = orderDoc.getDocumentElement();
			orderEle.setAttribute("DraftOrderFlag", "Y");
			orderEle.setAttribute("DocumentType", "0003");
			orderEle.setAttribute("EnterpriseCode", "FANATICS_WEB");
			orderEle.setAttribute("SellerOrganizationCode", enterpriseCode);
			orderEle.setAttribute("OrderNo", retOrderNo);
			orderEle.setAttribute("DraftOrderFlag", "Y");
			orderEle.setAttribute("ShipNode", receivingNode);

			Element orderLines = orderDoc.createElement("OrderLines");

			// taking each receipt line at a time
			for(int i=0; i<receiptLineList.getLength(); i++){

				Element receiptLine = (Element)receiptLineList.item(i);
				String itemId = receiptLine.getAttribute("ItemID");
				String qty = receiptLine.getAttribute("Quantity");
				String dispositionCode = receiptLine.getAttribute("DispositionCode");

				Element derivedFromOrder = (Element)receiptLine.getElementsByTagName("DerivedFromOrder").item(0);
				salesOrderNo = derivedFromOrder.getAttribute("OrderNo");

				Element retOrderLine = (Element)receiptLine.getElementsByTagName("OrderLine").item(0);
				String returnReason = retOrderLine.getAttribute("ReturnReason");			

				Document orderDoc2 = XMLUtil.createDocument("Order");
				Element orderEle2 = orderDoc2.getDocumentElement();
				orderEle2.setAttribute("OrderNo", salesOrderNo);
				orderEle2.setAttribute("DocumentType", "0001");
				orderEle2.setAttribute("EnterpriseCode", enterpriseCode);

				logger.verbose("--------------FanReturnReceipt------3-"
						+XMLUtil.getXMLString(orderDoc2));

				Document outDoc3 = CommonUtil.invokeAPI
						(env,"global/template/api/getOrderList_reshipLine.xml","getOrderList", orderDoc2);

				logger.verbose("--------------FanReturnReceipt------4-"
						+XMLUtil.getXMLString(outDoc3));

				Float totalOrderedQty = 0.0f;
				Float totalReturnedQty = 0.0f;
				Float ZCRReturnableQty = 0.0f;
				Float WMSReceiptQty = Float.parseFloat(qty);

				List refundableLineQtyList = new ArrayList();
				List nonRefundableLineQtyList = new ArrayList();

				// For each receipt line recognizing the sales order lines with same item
				NodeList orderLineList = outDoc3.getElementsByTagName("OrderLine");

				for(int a=0; a<orderLineList.getLength(); a++){
					logger.verbose("----------------------------------5-----------------------------");
					Element orderLine3 = (Element)orderLineList.item(a);
					String salesPrimeLineNo = orderLine3.getAttribute("PrimeLineNo");

					Element itemDetails = (Element)orderLine3.getElementsByTagName("ItemDetails").item(0);
					String salesitemId = itemDetails.getAttribute("ItemID");

					if(salesitemId.equalsIgnoreCase(itemId)){
						logger.verbose("----------------------------------6-----------------------------");
						String returnableQty = "";
						Float returnableQtyFlt = 0.0f;
						String orderedQty = orderLine3.getAttribute("OrderedQty");

						NodeList orderStatusList = orderLine3.getElementsByTagName("OrderStatus");

						for(int j=0; j<orderStatusList.getLength(); j++){

							Element orderStatusEle = (Element)orderStatusList.item(j);
							if(orderStatusEle.getAttribute("Status").equalsIgnoreCase("3700")){
								returnableQty = orderStatusEle.getAttribute("StatusQty");
								returnableQtyFlt = Float.parseFloat(returnableQty);
							}
						}

						String reshipParentLineKey = orderLine3.getAttribute("ReshipParentLineKey");

						// segregating the refundable lines and the non-refundable ones and keeping them in different ArrayList
						if(returnableQtyFlt.floatValue() > 0.0f){

							if(reshipParentLineKey != null){

								if(!reshipParentLineKey.equalsIgnoreCase("")){
									nonRefundableLineQtyList.add(salesPrimeLineNo + "#" + returnableQty);
									ZCRReturnableQty = ZCRReturnableQty.floatValue() + Float.parseFloat(returnableQty);
									logger.verbose("----------------------------------7-----------------------------");

								}else{
									refundableLineQtyList.add(salesPrimeLineNo + "#" + returnableQty);
									logger.verbose("----------------------------------8-----------------------------");
								}

							}else{

								refundableLineQtyList.add(salesPrimeLineNo + "#" + returnableQty);
								logger.verbose("----------------------------------9-----------------------------");

							}
						}
					}
				}

				for(int a=0; a<nonRefundableLineQtyList.size(); a++){
					logger.verbose("--------------------------nonRefundableLineQtyList------------10-----------"+nonRefundableLineQtyList.get(a));
				}

				for(int a=0; a<refundableLineQtyList.size(); a++){
					logger.verbose("--------------------------refundableLineQtyList-------------11----------"+refundableLineQtyList.get(a));
				}

				// Return orders are getting created with line totals having zero or non-zero amount based on disposition code
				if(dispositionCode.equalsIgnoreCase("CARRIER_RETURNED") || dispositionCode.equalsIgnoreCase("MISSING_GOT_RETURNED") 
						|| dispositionCode.equalsIgnoreCase("LOST_GOT_RETURNED") || dispositionCode.equalsIgnoreCase("LOST_IN_TRANSIT")){

					for(int a=0; a<nonRefundableLineQtyList.size(); a++){

						String nonRefundableLineQty = (String)nonRefundableLineQtyList.get(a);

						String pLNo = nonRefundableLineQty.split("#")[0];
						String nonRefundableQty = nonRefundableLineQty.split("#")[1];
						Float qtyFlt = Float.parseFloat(nonRefundableQty);

						Element orderLine2 = orderDoc.createElement("OrderLine");
						orderLine2.setAttribute("OrderedQty", nonRefundableQty);
						orderLine2.setAttribute("ReturnReason", returnReason);
						orderLine2.setAttribute("ShipNode", receivingNode);

						Element derivedFrom2 = orderDoc.createElement("DerivedFrom");
						derivedFrom2.setAttribute("OrderNo", salesOrderNo);
						derivedFrom2.setAttribute("EnterpriseCode", "FANATICS_WEB");
						derivedFrom2.setAttribute("DocumentType", "0001");
						derivedFrom2.setAttribute("PrimeLineNo", pLNo);
						derivedFrom2.setAttribute("SubLineNo", "1");

						if(WMSReceiptQty.floatValue() > 0.0f){

							if(WMSReceiptQty.floatValue() <= qtyFlt.floatValue()){

								orderLine2.setAttribute("OrderedQty", String.valueOf(WMSReceiptQty.intValue()));

								WMSReceiptQty = 0.0f;

							}else{
								orderLine2.setAttribute("OrderedQty", nonRefundableQty);
								WMSReceiptQty = WMSReceiptQty.floatValue() - qtyFlt.floatValue();
							}
							orderLine2.appendChild(derivedFrom2);
							orderLines.appendChild(orderLine2);
						}
					}

					if(WMSReceiptQty.floatValue() > 0.0f){

						String pLNo2 = "";

						for(int a=0; a<orderLineList.getLength(); a++){
							logger.verbose("----------------------------------11--1---------------------------");
							Element orderLine3 = (Element)orderLineList.item(a);
							String salesPrimeLineNo = orderLine3.getAttribute("PrimeLineNo");
							String reshipParentLineKey = orderLine3.getAttribute("ReshipParentLineKey");

							Element itemDetails = (Element)orderLine3.getElementsByTagName("ItemDetails").item(0);
							String salesitemId = itemDetails.getAttribute("ItemID");

							if(salesitemId.equalsIgnoreCase(itemId)){

								if(reshipParentLineKey != null){

									if(reshipParentLineKey.equalsIgnoreCase("")){
										pLNo2 = orderLine3.getAttribute("PrimeLineNo");
									}
								}else{
									pLNo2 = orderLine3.getAttribute("PrimeLineNo");
								}
							}
						}

						Document orderDoc3 = XMLUtil.createDocument("Order");
						Element orderEle3 = orderDoc3.getDocumentElement();
						orderEle3.setAttribute("OrderNo", salesOrderNo);
						orderEle3.setAttribute("DocumentType", "0001");
						orderEle3.setAttribute("EnterpriseCode", enterpriseCode);
						orderEle3.setAttribute("Override", "Y");

						Element orderLinesEle3 = orderDoc3.createElement("OrderLines");
						Element orderLineEle3 = orderDoc3.createElement("OrderLine");
						orderLineEle3.setAttribute("PrimeLineNo", pLNo2);
						orderLineEle3.setAttribute("SubLineNo", "1");
						orderLineEle3.setAttribute("QuantityToReship", WMSReceiptQty.toString());

						orderLinesEle3.appendChild(orderLineEle3);
						orderDoc3.getDocumentElement().appendChild(orderLinesEle3);

						logger.verbose("--------------FanReturnReceipt------reshipOrderLines--ip--13--"
								+XMLUtil.getXMLString(orderDoc3));

						Document outDoc31 = CommonUtil.invokeAPI
								(env,"global/template/api/reshipOrderLines_op.xml","reshipOrderLines", orderDoc3);

						logger.verbose("--------------FanReturnReceipt----reshipOrderLines--op--14--"
								+XMLUtil.getXMLString(outDoc31));

						orderEle3.setAttribute("Action", "MODIFY");
						Element orderHoldTypesEle = (Element)XPathUtil.getXpathNode(outDoc31, "/Order/OrderHoldTypes");
						Element orderHoldTypesEleClone = (Element) orderDoc3.importNode(orderHoldTypesEle.cloneNode(true), true);
						orderDoc3.getDocumentElement().appendChild(orderHoldTypesEleClone);	

						NodeList holdTypeList = orderDoc3.getElementsByTagName("OrderHoldType");

						for(int p=0; p<holdTypeList.getLength(); p++){

							Element orderHoldType = (Element)holdTypeList.item(p);
							String holdType = orderHoldType.getAttribute("HoldType");

							if(holdType != null){
								if(holdType.equalsIgnoreCase("PENDINGEVALUATION")){
									orderHoldType.setAttribute("Status", "1300");
								}
							}
						}

						String pLNo5 = "";
						NodeList salesOrderlineList = outDoc31.getElementsByTagName("OrderLine");	

						for(int q=0; q<salesOrderlineList.getLength(); q++){

							Element orderLine = (Element)salesOrderlineList.item(q);

							String pLNo4 = orderLine.getAttribute("PrimeLineNo");
							String reshipParentLineKey = orderLine.getAttribute("ReshipParentLineKey");

							NodeList orderStatusList = orderLine.getElementsByTagName("OrderStatus");
							Element itemDetailsEle = (Element)orderLine.getElementsByTagName("ItemDetails").item(0);
							String tempItemID = itemDetailsEle.getAttribute("ItemID");

							for(int r=0; r<orderStatusList.getLength(); r++){

								Element orderStatus = (Element)orderStatusList.item(r);

								String status = orderStatus.getAttribute("Status");
								String statusQty = orderStatus.getAttribute("StatusQty");

								Float statusQtyFlt = Float.parseFloat(statusQty);

								if((statusQtyFlt.floatValue() > 0.0f) && (status.equalsIgnoreCase("1100"))){

									if(reshipParentLineKey != null){

										if((!reshipParentLineKey.equalsIgnoreCase("")) && (itemId.equalsIgnoreCase(tempItemID))	){
											logger.verbose("------------------------------Inside condition---------------------------------------");
											orderLineEle3.setAttribute("PrimeLineNo", pLNo4);
											orderLineEle3.setAttribute("SubLineNo", "1");
											orderLineEle3.setAttribute("ShipNode", receivingNode);
											orderLineEle3.removeAttribute("QuantityToReship");
											pLNo5 = pLNo4;

										}
									}
								}
							}
						}

						orderLinesEle3.appendChild(orderLineEle3);
						orderDoc3.getDocumentElement().appendChild(orderLinesEle3);

						logger.verbose("--------------FanReturnReceipt------changeOrder--ip----11--2--"
								+XMLUtil.getXMLString(orderDoc3));

						Document outDoc32 = CommonUtil.invokeAPI
								(env,"changeOrder", orderDoc3);

						logger.verbose("--------------FanReturnReceipt------changeOrder--op----11--3--"
								+XMLUtil.getXMLString(outDoc32));

						// Scheduling the reship lines	
						Document outDoc33 = CommonUtil.invokeAPI
								(env,"scheduleOrder", outDoc32);

						logger.verbose("--------------FanReturnReceipt------scheduleOrder--done---11--4-");

						Document orderDoc5 = XMLUtil.createDocument("ReleaseOrder");
						Element orderEle5 = orderDoc5.getDocumentElement();
						orderEle5.setAttribute("OrderNo", salesOrderNo);
						orderEle5.setAttribute("DocumentType", "0001");
						orderEle5.setAttribute("EnterpriseCode", enterpriseCode);
						orderEle5.setAttribute("CheckInventory", "N");
						orderEle5.setAttribute("IgnoreReleaseDate", "Y");
						orderEle5.setAttribute("IgnoreTransactionDependencies", "Y");
						orderEle5.setAttribute("Override", "Y");

						logger.verbose("--------------FanReturnReceipt------releaseOrder--ip----11--5---"
								+XMLUtil.getXMLString(orderDoc5));

						Document outDoc51 = CommonUtil.invokeAPI
								(env,"releaseOrder", orderDoc5);

						logger.verbose("--------------FanReturnReceipt------releaseOrder--done--11--6--");

						// getting the release number list, not releasing the order once again
						Document OrderRelease = XMLUtil.createDocument("OrderRelease");
						Element OrderReleaseEle = OrderRelease.getDocumentElement();
						OrderReleaseEle.setAttribute("ShipNode", receivingNode);
						Element Order = OrderRelease.createElement("Order");
						Order.setAttribute("DocumentType", "0001");
						Order.setAttribute("EnterpriseCode", enterpriseCode);
						Order.setAttribute("OrderNo", salesOrderNo);
						OrderReleaseEle.appendChild(Order);

						logger.verbose("--------------FanReturnReceipt------getOrderReleaseList--ip--11--7--"
								+XMLUtil.getXMLString(OrderRelease));

						Document getOrderReleaseListOp = CommonUtil.invokeAPI
								(env,"getOrderReleaseList", OrderRelease);

						logger.verbose("--------------FanReturnReceipt------getOrderReleaseList--done--11--8--"+XMLUtil.getXMLString(getOrderReleaseListOp));

						Element getOrderReleaseListEle = getOrderReleaseListOp.getDocumentElement();
						NodeList OrderReleaseList = getOrderReleaseListEle.getElementsByTagName("OrderRelease");
						int releaseNoMax = 0;
						int releaseNoMin = 0;

						for(int s=0; s<OrderReleaseList.getLength(); s++)
						{
							Element OrderReleaseEle2 = (Element) OrderReleaseList.item(s);
							String ReleaseNo = OrderReleaseEle2.getAttribute("ReleaseNo");

							int ReleaseNoInt = Integer.parseInt(ReleaseNo);

							if(s==0)
							{
								releaseNoMin = ReleaseNoInt;
							}
							if(ReleaseNoInt > releaseNoMax)
							{
								releaseNoMax = ReleaseNoInt;
							}
							if(ReleaseNoInt < releaseNoMin)
							{
								releaseNoMin = ReleaseNoInt;
							}
						}

						String releaseNoMaxStr = String.valueOf(releaseNoMax);
						String releaseNoMinStr = String.valueOf(releaseNoMin);

						Document shipmentDoc = XMLUtil.createDocument("Shipment");
						Element shipmentEle = shipmentDoc.getDocumentElement();
						shipmentEle.setAttribute("EnterpriseCode", enterpriseCode);

						Element shipmentLinesEle = shipmentDoc.createElement("ShipmentLines");
						Element shipmentLineEle = shipmentDoc.createElement("ShipmentLine");

						shipmentLineEle.setAttribute("DocumentType", "0001");
						shipmentLineEle.setAttribute("OrderNo", salesOrderNo);
						shipmentLineEle.setAttribute("PrimeLineNo", pLNo5);
						shipmentLineEle.setAttribute("SubLineNo", "1");
						shipmentLineEle.setAttribute("UnitOfMeasure", "EACH");
						shipmentLineEle.setAttribute("Quantity", WMSReceiptQty.toString());
						shipmentLineEle.setAttribute("ReleaseNo", releaseNoMaxStr);

						shipmentLinesEle.appendChild(shipmentLineEle);
						shipmentEle.appendChild(shipmentLinesEle);

						logger.verbose("--------------FanReturnReceipt------confirmShipment--ip--11--9--"
								+XMLUtil.getXMLString(shipmentDoc));

						Document confirmShipmentDoc = CommonUtil.invokeAPI
								(env,"confirmShipment", shipmentDoc);

						logger.verbose("--------------FanReturnReceipt------confirmShipment--op--11--10--"+XMLUtil.getXMLString(confirmShipmentDoc));

						Element orderLine2 = orderDoc.createElement("OrderLine");
						orderLine2.setAttribute("OrderedQty", String.valueOf(WMSReceiptQty.intValue()));
						orderLine2.setAttribute("ReturnReason", returnReason);
						orderLine2.setAttribute("ShipNode", receivingNode);

						Element derivedFrom2 = orderDoc.createElement("DerivedFrom");
						derivedFrom2.setAttribute("OrderNo", salesOrderNo);
						derivedFrom2.setAttribute("EnterpriseCode", "FANATICS_WEB");
						derivedFrom2.setAttribute("DocumentType", "0001");
						derivedFrom2.setAttribute("PrimeLineNo", pLNo5);
						derivedFrom2.setAttribute("SubLineNo", "1");

						WMSReceiptQty = 0.0f;

						orderLine2.appendChild(derivedFrom2);
						orderLines.appendChild(orderLine2);

					}
					logger.verbose("----------------CARRIER_RETURNED---------12------11--11--"+XMLUtil.getElementXMLString(orderLines));
				}else{

					// first return lines will be created against ZCR lines by default, no refunds will be generated
					for(int a=0; a<nonRefundableLineQtyList.size(); a++){

						String nonRefundableLineQty = (String)nonRefundableLineQtyList.get(a);
						String pLNo = nonRefundableLineQty.split("#")[0];
						String nonRefundableQty = nonRefundableLineQty.split("#")[1];
						Float qtyFlt = Float.parseFloat(nonRefundableQty);

						Element orderLine2 = orderDoc.createElement("OrderLine");
						orderLine2.setAttribute("OrderedQty", nonRefundableQty);
						orderLine2.setAttribute("ReturnReason", returnReason);
						orderLine2.setAttribute("ShipNode", receivingNode);

						Element derivedFrom2 = orderDoc.createElement("DerivedFrom");
						derivedFrom2.setAttribute("OrderNo", salesOrderNo);
						derivedFrom2.setAttribute("EnterpriseCode", "FANATICS_WEB");
						derivedFrom2.setAttribute("DocumentType", "0001");
						derivedFrom2.setAttribute("PrimeLineNo", pLNo);
						derivedFrom2.setAttribute("SubLineNo", "1");

						if(WMSReceiptQty.floatValue() > 0.0f){

							if(WMSReceiptQty.floatValue() <= qtyFlt.floatValue()){
								orderLine2.setAttribute("OrderedQty", String.valueOf(WMSReceiptQty.intValue()));
								logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty------17-----"+WMSReceiptQty.intValue());
								WMSReceiptQty = 0.0f;
							}else{
								logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty------18-----"+WMSReceiptQty.intValue());
								orderLine2.setAttribute("OrderedQty", nonRefundableQty);
								WMSReceiptQty = WMSReceiptQty.floatValue() - qtyFlt.floatValue();
								logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty------19-----"+WMSReceiptQty.intValue());
							}
							orderLine2.appendChild(derivedFrom2);
							orderLines.appendChild(orderLine2);
						}
					}

					// Return lines will be created against regular lines,refunds will be generated for these
					if(WMSReceiptQty.floatValue() > 0.0f){

						for(int a=0; a<refundableLineQtyList.size(); a++){

							String refundableLineQty = (String)refundableLineQtyList.get(a);
							String pLNo = refundableLineQty.split("#")[0];
							String refundableQty = refundableLineQty.split("#")[1];
							Float qtyFlt = Float.parseFloat(refundableQty);

							Element orderLine2 = orderDoc.createElement("OrderLine");
							orderLine2.setAttribute("OrderedQty", refundableQty);
							orderLine2.setAttribute("ReturnReason", returnReason);
							orderLine2.setAttribute("ShipNode", receivingNode);

							Element derivedFrom2 = orderDoc.createElement("DerivedFrom");
							derivedFrom2.setAttribute("OrderNo", salesOrderNo);
							derivedFrom2.setAttribute("EnterpriseCode", "FANATICS_WEB");
							derivedFrom2.setAttribute("DocumentType", "0001");
							derivedFrom2.setAttribute("PrimeLineNo", pLNo);
							derivedFrom2.setAttribute("SubLineNo", "1");

							if(WMSReceiptQty.floatValue() > 0.0f){

								if(WMSReceiptQty.floatValue() <= qtyFlt.floatValue()){
									orderLine2.setAttribute("OrderedQty", String.valueOf(WMSReceiptQty.intValue()));
									logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty-----20------"+WMSReceiptQty.intValue());
									WMSReceiptQty = 0.0f;
								}else{
									logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty-----21------"+WMSReceiptQty.intValue());
									orderLine2.setAttribute("OrderedQty", refundableQty);
									WMSReceiptQty = WMSReceiptQty.floatValue() - qtyFlt.floatValue();
									logger.verbose(qtyFlt.floatValue()+"------------WMSReceiptQty------22-----"+WMSReceiptQty.intValue());
								}
								orderLine2.appendChild(derivedFrom2);
								orderLines.appendChild(orderLine2);
							}
						}
					}
					logger.verbose("----------------ELSE---------23--------"+XMLUtil.getElementXMLString(orderLines));
				}
				orderDoc.getDocumentElement().appendChild(orderLines);

				logger.verbose("--------------FanReturnReceipt------orderDoc--24---"
						+XMLUtil.getXMLString(orderDoc));
			}

			Element customAttributesEle = (Element)XPathUtil.getXpathNode(receiveOrderDoc, "/Receipt/CustomAttributes");

			if(customAttributesEle != null){

				Element customAttributesEleClone = (Element) orderDoc.importNode(customAttributesEle.cloneNode(true), true);
				orderDoc.getDocumentElement().appendChild(customAttributesEleClone);	
			}

			logger.verbose("--------------FanReturnReceipt------FanCreateReturnSync ip---25--"
					+XMLUtil.getXMLString(orderDoc));

			Document createReturnOrderOpDoc = CommonUtil.invokeService(env, "FanCreateReturnSync", orderDoc);

			logger.verbose("--------------FanReturnReceipt------FanCreateReturnSync op---26--"
					+XMLUtil.getXMLString(createReturnOrderOpDoc));

			CommonUtil.invokeAPI(env, "scheduleOrder", createReturnOrderOpDoc);

			logger.verbose("--------------FanReturnReceipt------scheduleReturnDoc--27--");
		}

		// Code for new return order creation and scheduling ends here

		// The receiving part of returns start from here
		Element receiptLinesEle2 = receiveOrderDoc.createElement("ReceiptLines");

		for(int i=0; i<receiptLineList.getLength(); i++){

			Element receiptLine = (Element)receiptLineList.item(i);
			String qty = receiptLine.getAttribute("Quantity");
			String receiptItemId = receiptLine.getAttribute("ItemID");
			String dispositionCode = receiptLine.getAttribute("DispositionCode");

			logger.verbose("--------------FanReturnReceipt------receiveOrder ip--28-"
					+XMLUtil.getXMLString(inDoc));

			Document orderDoc2 = XMLUtil.createDocument("Order");
			Element orderEle2 = orderDoc2.getDocumentElement();
			orderEle2.setAttribute("OrderNo", retOrderNo);
			orderEle2.setAttribute("DocumentType", "0003");
			orderEle2.setAttribute("EnterpriseCode", enterpriseCode);

			logger.verbose("--------------FanReturnReceipt------29-"
					+XMLUtil.getXMLString(orderDoc2));

			Document outDoc3 = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderList_reshipLine.xml","getOrderList", orderDoc2);

			logger.verbose("--------------FanReturnReceipt------30--"
					+XMLUtil.getXMLString(outDoc3));

			Float totalOrderedQty = 0.0f;
			Float totalReturnedQty = 0.0f;
			Float ZCRReturnableQty = 0.0f;
			Float WMSReceiptQty = Float.parseFloat(qty);

			List refundableLineQtyList = new ArrayList();
			List nonRefundableLineQtyList = new ArrayList();

			NodeList retOrderLineList = outDoc3.getElementsByTagName("OrderLine");

			for(int a=0; a<retOrderLineList.getLength(); a++){
				logger.verbose("----------------------------------31-----------------------------");
				Element orderLine3 = (Element)retOrderLineList.item(a);
				String retPrimeLineNo = orderLine3.getAttribute("PrimeLineNo");
				String derivedFromOrderLineKey = orderLine3.getAttribute("DerivedFromOrderLineKey");
				String retOrderLineQty = orderLine3.getAttribute("OrderedQty");
				Float retOrderLineQtyFlt = Float.parseFloat(retOrderLineQty);

				String receivableQty = "";
				Float receivableQtyFlt = 0.0f;

				NodeList orderStatusList = orderLine3.getElementsByTagName("OrderStatus");

				for(int j=0; j<orderStatusList.getLength(); j++){

					Element orderStatusEle = (Element)orderStatusList.item(j);
					if(orderStatusEle.getAttribute("Status").equalsIgnoreCase("3200")){
						receivableQty = orderStatusEle.getAttribute("StatusQty");
						receivableQtyFlt = Float.parseFloat(receivableQty);
					}
				}

				Element itemDetails = (Element)orderLine3.getElementsByTagName("ItemDetails").item(0);
				String retItemId = itemDetails.getAttribute("ItemID");

				if(retItemId.equalsIgnoreCase(receiptItemId)){
					logger.verbose("----------------------------------32-----------------------------");
					String orderedQty = orderLine3.getAttribute("OrderedQty");

					Document orderDoc13 = XMLUtil.createDocument("OrderLineDetail");
					Element orderEle13 = orderDoc13.getDocumentElement();
					orderEle13.setAttribute("OrderLineKey", derivedFromOrderLineKey);

					// calling sales order line
					logger.verbose("--------------FanReturnReceipt------32---1--"
							+XMLUtil.getXMLString(orderDoc13));

					Document outDoc14 = CommonUtil.invokeAPI
							(env,"global/template/api/getOrderLineDetails_reshipLine.xml","getOrderLineDetails", orderDoc13);

					logger.verbose("--------------FanReturnReceipt-----32---2--"
							+XMLUtil.getXMLString(outDoc14));

					String reshipParentLineKey = outDoc14.getDocumentElement().getAttribute("ReshipParentLineKey");

					logger.verbose("----------------------------------32-----3------------------------"+receivableQty);

					if(receivableQtyFlt.floatValue() > 0.0f){

						if(reshipParentLineKey != null){

							if(!reshipParentLineKey.equalsIgnoreCase("")){
								nonRefundableLineQtyList.add(retPrimeLineNo + "#" + receivableQty);
								logger.verbose("----------------------------------33-----------------------------"+receivableQty);

							}else{
								refundableLineQtyList.add(retPrimeLineNo + "#" + receivableQty);
								logger.verbose("----------------------------------34-----------------------------"+receivableQty);
							}

						}else{
							refundableLineQtyList.add(retPrimeLineNo + "#" + receivableQty);
							logger.verbose("----------------------------------35-----------------------------"+receivableQty);
						}
					}
				}
			}

			for(int a=0; a<nonRefundableLineQtyList.size(); a++){
				logger.verbose("--------------------------nonRefundableLineQtyList---------36--------------"+nonRefundableLineQtyList.get(a));
			}

			for(int a=0; a<refundableLineQtyList.size(); a++){
				logger.verbose("--------------------------refundableLineQtyList------------37-----------"+refundableLineQtyList.get(a));
			}

			Document orderDoc11 = XMLUtil.createDocument("Order");
			Element orderEle11 = orderDoc11.getDocumentElement();
			orderEle11.setAttribute("OrderNo", retOrderNo);
			orderEle11.setAttribute("DocumentType", "0003");
			orderEle11.setAttribute("EnterpriseCode", enterpriseCode);

			logger.verbose("--------------FanReturnReceipt------38-"
					+XMLUtil.getXMLString(orderDoc11));

			Document outDoc12 = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderList_reshipLine.xml","getOrderList", orderDoc11);

			logger.verbose("--------------FanReturnReceipt------39-"
					+XMLUtil.getXMLString(outDoc12));

			// receipts are getting consumed based on disposition code
			if(dispositionCode.equalsIgnoreCase("CARRIER_RETURNED") || dispositionCode.equalsIgnoreCase("MISSING_GOT_RETURNED") 
					|| dispositionCode.equalsIgnoreCase("LOST_GOT_RETURNED") || dispositionCode.equalsIgnoreCase("LOST_IN_TRANSIT")){

				for(int a=0; a<nonRefundableLineQtyList.size(); a++){

					String nonRefundableLineQty = (String)nonRefundableLineQtyList.get(a);
					String pLNo = nonRefundableLineQty.split("#")[0];
					String nonRefundableQty = nonRefundableLineQty.split("#")[1];
					Float nonRefundableQtyFlt = Float.parseFloat(nonRefundableQty);

					Element receiptLine2 = receiveOrderDoc.createElement("ReceiptLine");
					receiptLine2.setAttribute("DispositionCode", dispositionCode);
					receiptLine2.setAttribute("ItemID", receiptItemId);
					receiptLine2.setAttribute("UnitOfMeasure", "EACH");
					receiptLine2.setAttribute("PrimeLineNo", pLNo);
					receiptLine2.setAttribute("SubLineNo", "1");

					if(WMSReceiptQty.floatValue() > 0.0f){

						if(WMSReceiptQty.floatValue() <= nonRefundableQtyFlt.floatValue()){

							receiptLine2.setAttribute("Quantity", String.valueOf(WMSReceiptQty.intValue()));
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty-----42------"+WMSReceiptQty.intValue());
							WMSReceiptQty = 0.0f;

						}else{
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty------43-----"+WMSReceiptQty.intValue());
							receiptLine2.setAttribute("Quantity", nonRefundableQty);
							WMSReceiptQty = WMSReceiptQty.floatValue() - nonRefundableQtyFlt.floatValue();
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty-------44----"+WMSReceiptQty.intValue());
						}
						receiptLinesEle2.appendChild(receiptLine2);
					}
				}

				logger.verbose("----------------CARRIER_RETURNED----------46-------"+XMLUtil.getElementXMLString(receiptLinesEle2));

			}else{

				for(int a=0; a<nonRefundableLineQtyList.size(); a++){

					String nonRefundableLineQty = (String)nonRefundableLineQtyList.get(a);
					String pLNo = nonRefundableLineQty.split("#")[0];
					String nonRefundableQty = nonRefundableLineQty.split("#")[1];
					Float nonRefundableQtyFlt = Float.parseFloat(nonRefundableQty);

					Element receiptLine2 = receiveOrderDoc.createElement("ReceiptLine");
					receiptLine2.setAttribute("DispositionCode", dispositionCode);
					receiptLine2.setAttribute("ItemID", receiptItemId);
					receiptLine2.setAttribute("UnitOfMeasure", "EACH");
					receiptLine2.setAttribute("PrimeLineNo", pLNo);
					receiptLine2.setAttribute("SubLineNo", "1");

					if(WMSReceiptQty.floatValue() > 0.0f){

						if(WMSReceiptQty.floatValue() <= nonRefundableQtyFlt.floatValue()){

							receiptLine2.setAttribute("Quantity", String.valueOf(WMSReceiptQty.intValue()));
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty-----51------"+WMSReceiptQty.intValue());
							WMSReceiptQty = 0.0f;

						}else{
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty------52-----"+WMSReceiptQty.intValue());
							receiptLine2.setAttribute("Quantity", nonRefundableQty);
							WMSReceiptQty = WMSReceiptQty.floatValue() - nonRefundableQtyFlt.floatValue();
							logger.verbose(nonRefundableQtyFlt.floatValue()+"------------WMSReceiptQty-------53----"+WMSReceiptQty.intValue());
						}
						receiptLinesEle2.appendChild(receiptLine2);
					}
				}

				logger.verbose("----------------CARRIER_RETURNED----------54-------"+XMLUtil.getElementXMLString(receiptLinesEle2));

				if(WMSReceiptQty.floatValue() > 0.0f){

					for(int a=0; a<refundableLineQtyList.size(); a++){

						String refundableLineQty = (String)refundableLineQtyList.get(a);
						String pLNo = refundableLineQty.split("#")[0];
						String refundableQty = refundableLineQty.split("#")[1];
						Float refundableQtyFlt = Float.parseFloat(refundableQty);

						Element receiptLine2 = receiveOrderDoc.createElement("ReceiptLine");
						receiptLine2.setAttribute("DispositionCode", dispositionCode);
						receiptLine2.setAttribute("ItemID", receiptItemId);
						receiptLine2.setAttribute("UnitOfMeasure", "EACH");
						receiptLine2.setAttribute("PrimeLineNo", pLNo);
						receiptLine2.setAttribute("SubLineNo", "1");

						if(WMSReceiptQty.floatValue() > 0.0f){

							if(WMSReceiptQty.floatValue() <= refundableQtyFlt.floatValue()){

								receiptLine2.setAttribute("Quantity", String.valueOf(WMSReceiptQty.intValue()));
								logger.verbose(refundableQtyFlt.floatValue()+"------------WMSReceiptQty-----55------"+WMSReceiptQty.intValue());
								WMSReceiptQty = 0.0f;

							}else{
								logger.verbose(refundableQtyFlt.floatValue()+"------------WMSReceiptQty------56-----"+WMSReceiptQty.intValue());
								receiptLine2.setAttribute("Quantity", refundableQty);
								WMSReceiptQty = WMSReceiptQty.floatValue() - refundableQtyFlt.floatValue();
								logger.verbose(refundableQtyFlt.floatValue()+"------------WMSReceiptQty-------57----"+WMSReceiptQty.intValue());
							}
							receiptLinesEle2.appendChild(receiptLine2);
						}
					}
				}
			}
		}

		receiveOrderDoc.getDocumentElement().removeChild(receiptLinesEle);
		// adding the receipt lines
		receiveOrderDoc.getDocumentElement().appendChild(receiptLinesEle2);

		logger.verbose("--------------FanReturnReceipt------receiveOrder ip--67---"
				+XMLUtil.getXMLString(receiveOrderDoc));

		logger.verbose("--------------FanReturnReceipt------receiveOrder ip--67--1-"
				+XMLUtil.getXMLString(receiveOrderDoc2));

		// injecting the IsItemSentToCharity and IsOutsideOfReturnPolicy  from the main original Return Receipt doc
		NodeList receiptLineList5 = receiveOrderDoc.getElementsByTagName("ReceiptLine");

		for(int i=0; i<receiptLineList5.getLength(); i++){

			Element receiptLine5 = (Element)receiptLineList5.item(i);
			String itemID5 = receiptLine5.getAttribute("ItemID");
			NodeList receiptLineList6 = receiveOrderDoc2.getElementsByTagName("ReceiptLine");

			for(int k=0; k<receiptLineList6.getLength(); k++){

				Element receiptLine6 = (Element)receiptLineList6.item(k); 
				String itemID6 = receiptLine6.getAttribute("ItemID");

				if(itemID5.equalsIgnoreCase(itemID6)){

					Element customAttributesEle = (Element)receiptLine6.getElementsByTagName("CustomAttributes").item(0);

					if(customAttributesEle != null){

						String IsOutsideOfReturnPolicy = customAttributesEle.getAttribute("IsOutsideOfReturnPolicy");
						String IsItemSentToCharity = customAttributesEle.getAttribute("IsItemSentToCharity");

						if(IsOutsideOfReturnPolicy != null){
							if(IsOutsideOfReturnPolicy.equalsIgnoreCase("Y")){
								receiptLine5.setAttribute("IsOutsideOfReturnPolicy", "Y");
								logger.verbose("------------------------------1-----------------------");
							}
						}

						if(IsItemSentToCharity != null){
							if(IsItemSentToCharity.equalsIgnoreCase("Y")){
								receiptLine5.setAttribute("IsItemSentToCharity", "Y");
								logger.verbose("------------------------------2-----------------------");
							}
						}
					}
				}
			}
		}

		logger.verbose("--------------FanReturnReceipt------receiveOrder ip--67--2---"
				+XMLUtil.getXMLString(receiveOrderDoc));

		// Refund is decided based on Disposition Code
		refundIssuedBasedOnDispositionCode(env, receiveOrderDoc, retOrderNo, enterpriseCode);

		// OrderLine node is removed from receipt lines if present
		NodeList orderLineList = receiveOrderDoc.getElementsByTagName("OrderLine");
		for(int i=0; i<orderLineList.getLength(); i++){

			Element tempEle = (Element)orderLineList.item(0);
			tempEle.getParentNode().removeChild(tempEle);
			i--;
		}

		// refund is denied if falling outside return policy
		checkIfReturnPolicyOutside(env, receiveOrderDoc);

		// waive MRL Fee if present in return receipt
		waiveMRLFeeIfRequired(env, receiveOrderDoc);

		Element  shipmentEle =  (Element)XPathUtil.getXpathNode(receiveOrderDoc, "/Receipt/Shipment");
		String orderNo = shipmentEle.getAttribute("OrderNo");

		logger.verbose("--------------FanReturnReceipt------receiveOrder ip--68---"
				+XMLUtil.getXMLString(receiveOrderDoc));

		Document outDoc = CommonUtil.invokeAPI
				(env,"receiveOrder", receiveOrderDoc);

		logger.verbose("--------------FanReturnReceipt------receiveOrder 2---69--"
				+XMLUtil.getXMLString(outDoc));

		Element receiptEle = outDoc.getDocumentElement();
		String receiptHeaderKey = receiptEle.getAttribute("ReceiptHeaderKey");

		Document receiptDoc = XMLUtil.createDocument("Receipt");
		Element receiptEle2 = receiptDoc.getDocumentElement();
		receiptEle2.setAttribute("ReceiptHeaderKey", receiptHeaderKey);
		receiptEle2.setAttribute("DocumentType", "0003");

		logger.verbose("--------------FanReturnReceipt------closeReceipt ip---70--"
				+XMLUtil.getXMLString(receiptDoc));

		Document outDoc2 = CommonUtil.invokeAPI
				(env,"closeReceipt", receiptDoc);
		Document orderDoc3 = XMLUtil.createDocument("Order");
		Element orderEle3 = orderDoc3.getDocumentElement();

		orderEle3.setAttribute("DocumentType", "0003");
		orderEle3.setAttribute("EnterpriseCode", "FANATICS_WEB");
		orderEle3.setAttribute("OrderNo", orderNo);
		orderEle3.setAttribute("TransactionId", "CREATE_ORDER_INVOICE.0003");

		logger.verbose("--------------FanReturnReceipt------createOrderInvoice ip---71--"
				+XMLUtil.getXMLString(orderDoc3));

		Document outDoc3 = CommonUtil.invokeAPI
				(env,"createOrderInvoice", orderDoc3);

		logger.verbose("--------------FanReturnReceipt------createOrderInvoice op---72--"
				+XMLUtil.getXMLString(outDoc3));

		return receiveOrderDoc;
	}

	// Refund Issued or not is based on the Disposition Code logic
	private void refundIssuedBasedOnDispositionCode(YFSEnvironment env,
			Document inDoc,String retOrderNo, String enterpriseCode) throws Exception {

		boolean isRefundable = true;

		Document orderDoc5 = XMLUtil.createDocument("Order");
		Element orderEle5 = orderDoc5.getDocumentElement();
		orderEle5.setAttribute("OrderNo", retOrderNo);
		orderEle5.setAttribute("DocumentType", "0003");
		orderEle5.setAttribute("EnterpriseCode", enterpriseCode);
		orderEle5.setAttribute("Action", "MODIFY");
		orderEle5.setAttribute("Override", "Y");
		Element orderLinesEle = orderDoc5.createElement("OrderLines");
		NodeList receiptLineList2 = inDoc.getElementsByTagName("ReceiptLine");

		for(int k=0; k<receiptLineList2.getLength(); k++){

			Element receiptLine = (Element)receiptLineList2.item(k);

			logger.verbose("-------FanReturnReceipt----receiptLineList2---ip--73---"
					+XMLUtil.getElementXMLString(receiptLine));

			String dispositionCode = receiptLine.getAttribute("DispositionCode");

			if(dispositionCode != null){

				if(!dispositionCode.trim().equalsIgnoreCase("")){

					String primeLineNo = receiptLine.getAttribute("PrimeLineNo");

					// getting custom common code for disposition code
					Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
					Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
					ordercommonCodeEle.setAttribute("CodeType", "DispositionCode");
					ordercommonCodeEle.setAttribute("CodeValue", dispositionCode);

					logger.verbose("-------FanReturnReceipt----getCommonCodeList---ip---74--"
							+XMLUtil.getXMLString(commonCodeDoc));

					Document commonCodeDocOp = CommonUtil.invokeAPI
							(env,"getCommonCodeList", commonCodeDoc);

					logger.verbose("-------FanReturnReceipt----getCommonCodeList---op---75--"
							+XMLUtil.getXMLString(commonCodeDocOp));

					Element outEle3 = (Element)commonCodeDocOp.getDocumentElement().getElementsByTagName("CommonCode").item(0);
					String codeShortDescription = outEle3.getAttribute("CodeShortDescription");

					if(codeShortDescription.equalsIgnoreCase("N")){

						isRefundable = false;

						Element orderLine5 = orderDoc5.createElement("OrderLine"); 
						orderLine5.setAttribute("PrimeLineNo", primeLineNo);
						orderLine5.setAttribute("SubLineNo", "1"); 
						Element linePriceInfo  = orderDoc5.createElement("LinePriceInfo"); 
						linePriceInfo.setAttribute("IsLinePriceForInformationOnly", "Y");
						orderLine5.appendChild(linePriceInfo);
						orderLinesEle.appendChild(orderLine5);
					}
				}
			}
		}

		if(isRefundable == false){

			// calling changeOrder to set the line prices to zero
			orderDoc5.getDocumentElement().appendChild(orderLinesEle);

			logger.verbose("-------FanReturnReceipt----changeOrder---ip---78--"
					+XMLUtil.getXMLString(orderDoc5));

			Document changeOrderOp = CommonUtil.invokeAPI
					(env,"changeOrder", orderDoc5);

			logger.verbose("-------FanReturnReceipt----changeOrder---op--79--"
					+XMLUtil.getXMLString(changeOrderOp));
		}
	}

	// setting MRL Fee zero if it is waived
	private Document waiveMRLFeeIfRequired(YFSEnvironment env,
			Document inputDoc) throws Exception {

		logger.verbose("--------------FanReturnReceipt------waiveMRLFeeIfRequired entry----"
				+XMLUtil.getXMLString(inputDoc));

		Element receiptEle = inputDoc.getDocumentElement();
		Element shipmentEle = (Element)inputDoc.getElementsByTagName("Shipment").item(0);

		String orderNo = shipmentEle.getAttribute("OrderNo");
		String documentType = shipmentEle.getAttribute("DocumentType");
		String enterpriseCode = shipmentEle.getAttribute("EnterpriseCode");

		Element customAttributes = (Element)receiptEle.getElementsByTagName("CustomAttributes").item(0);

		Document orderDoc2 = XMLUtil.createDocument("Order");
		Element eleOrder = orderDoc2.getDocumentElement();

		if(customAttributes != null){

			String isMRLFeeWaived = customAttributes.getAttribute("IsMRLFeeWaived");

			if(isMRLFeeWaived != null){

				if(isMRLFeeWaived.equalsIgnoreCase("Y")){

					eleOrder.setAttribute("Override", "Y");
					eleOrder.setAttribute("Action", "MODIFY");

					String MRLFeeWaiverReason = customAttributes.getAttribute("MRLFeeWaiverReason");

					Document orderDoc = XMLUtil.createDocument("Order");
					Element orderEle2 = orderDoc.getDocumentElement();
					orderEle2.setAttribute("OrderNo", orderNo);
					orderEle2.setAttribute("EnterpriseCode", enterpriseCode);
					orderEle2.setAttribute("DocumentType", documentType);

					logger.verbose("--------------FanReturnReceipt------getOrderList--ip--82--"
							+XMLUtil.getXMLString(orderDoc));

					Document outDoc = CommonUtil.invokeAPI
							(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);

					logger.verbose("--------------FanReturnReceipt------getOrderList--op--83--"
							+XMLUtil.getXMLString(outDoc));

					NodeList headerChargeList = outDoc.getDocumentElement().getElementsByTagName("HeaderCharge");

					Element orderEle3 = (Element)outDoc.getDocumentElement().getElementsByTagName("Order").item(0);
					String orderHeaderKey = orderEle3.getAttribute("OrderHeaderKey");

					eleOrder.setAttribute("OrderHeaderKey", orderHeaderKey);

					if(headerChargeList.getLength() > 0){

						for(int i=0; i<headerChargeList.getLength(); i++){

							Element headerCharge = (Element)headerChargeList.item(i);
							String chargeCategory = headerCharge.getAttribute("ChargeCategory");

							if(chargeCategory.equalsIgnoreCase("MRLFee")){

								Element headerChargesEle = orderDoc2.createElement("HeaderCharges");
								Element headerChargeEle = orderDoc2.createElement("HeaderCharge");

								headerChargeEle.setAttribute("ChargeCategory", "MRLFee");
								headerChargeEle.setAttribute("ChargeName", "MRLFee");
								headerChargeEle.setAttribute("ChargeAmount", "0.0");
								headerChargeEle.setAttribute("RemainingChargeAmount", "0.0");

								headerChargesEle.appendChild(headerChargeEle);
								orderDoc2.getDocumentElement().appendChild(headerChargesEle);

								Element notes = orderDoc2.createElement("Notes");
								Element note = orderDoc2.createElement("Note");

								note.setAttribute("NoteText", "CSR waived MRL Fee, Reason : " + MRLFeeWaiverReason);
								note.setAttribute("Tranid", "changeOrder");
								note.setAttribute("VisibleToAll", "Y");

								notes.appendChild(note);
								orderDoc2.getDocumentElement().appendChild(notes);

							}
						}
					}

					// calling changeOrder to set the line prices to zero
					logger.verbose("-------FanReturnReceipt----changeOrder---ip---84--"
							+XMLUtil.getXMLString(orderDoc2));

					Document changeOrderOp = CommonUtil.invokeAPI
							(env,"changeOrder", orderDoc2);

					logger.verbose("-------FanReturnReceipt----changeOrder---op--85--"
							+XMLUtil.getXMLString(changeOrderOp));

				}
			}
		}

		logger.verbose("--------------FanReturnReceipt------waiveMRLFeeIfRequired exit----"
				+XMLUtil.getXMLString(inputDoc));

		return inputDoc;
	}

	// refund is denied if Return falls Out of Return Policy
	private Document checkIfReturnPolicyOutside(YFSEnvironment env,
			Document inputDoc) throws Exception {

		logger.verbose("--------------FanReturnReceipt------checkIfReturnPolicyOutside entry----"
				+XMLUtil.getXMLString(inputDoc));

		Element receiptEle = inputDoc.getDocumentElement();
		Element shipmentEle = (Element)inputDoc.getElementsByTagName("Shipment").item(0);

		String orderNo = shipmentEle.getAttribute("OrderNo");
		String documentType = shipmentEle.getAttribute("DocumentType");
		String enterpriseCode = shipmentEle.getAttribute("EnterpriseCode");

		NodeList receiptLineList = inputDoc.getElementsByTagName("ReceiptLine");

		Document orderDoc2 = XMLUtil.createDocument("Order");
		Element eleOrder = orderDoc2.getDocumentElement();
		Element orderLinesEle = orderDoc2.createElement("OrderLines");
		Element headerChargesEle = orderDoc2.createElement("HeaderCharges");
		Element headerTaxesEle = orderDoc2.createElement("HeaderTaxes");
		Element notes = orderDoc2.createElement("Notes");

		eleOrder.setAttribute("Override", "Y");
		eleOrder.setAttribute("Action", "MODIFY");

		Document orderDoc = XMLUtil.createDocument("Order");
		Element orderEle2 = orderDoc.getDocumentElement();
		orderEle2.setAttribute("OrderNo", orderNo);
		orderEle2.setAttribute("EnterpriseCode", enterpriseCode);
		orderEle2.setAttribute("DocumentType", documentType);

		logger.verbose("--------------FanReturnReceipt------getOrderList--ip--86--"
				+XMLUtil.getXMLString(orderDoc));

		Document outDoc = CommonUtil.invokeAPI
				(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);

		logger.verbose("--------------FanReturnReceipt------getOrderList--op--87--"
				+XMLUtil.getXMLString(outDoc));



		Element orderEle3 = (Element)outDoc.getDocumentElement().getElementsByTagName("Order").item(0);
		String orderHeaderKey = orderEle3.getAttribute("OrderHeaderKey");

		eleOrder.setAttribute("OrderHeaderKey", orderHeaderKey);

		NodeList orderLineList = outDoc.getDocumentElement().getElementsByTagName("OrderLine");

		if(orderLineList.getLength() > 0){

			for(int i=0; i<orderLineList.getLength(); i++){

				Element orderLine = (Element)orderLineList.item(i);
				String primeLineNo = orderLine.getAttribute("PrimeLineNo");

				Element itemDetailsEle = (Element)orderLine.getElementsByTagName("ItemDetails").item(0);
				String itemID = itemDetailsEle.getAttribute("ItemID");
				Element primaryInfo = (Element) itemDetailsEle.getElementsByTagName("PrimaryInformation").item(0);
				String itemShortDesc = primaryInfo.getAttribute("ShortDescription");

				boolean isOutsideOfReturnPolicyFlag = false;
				boolean isItemSentToCharityFlag = false;

				for(int k=0; k<receiptLineList.getLength(); k++){

					Element ReceiptLine = (Element)receiptLineList.item(k);
					String retPrimeLineNo = ReceiptLine.getAttribute("PrimeLineNo");
					String isOutsideOfReturnPolicy = ReceiptLine.getAttribute("IsOutsideOfReturnPolicy");
					String isItemSentToCharity = ReceiptLine.getAttribute("IsItemSentToCharity");

					if(isOutsideOfReturnPolicy != null){
						if((isOutsideOfReturnPolicy.equalsIgnoreCase("Y")) && (primeLineNo.equalsIgnoreCase(retPrimeLineNo))){
							isOutsideOfReturnPolicyFlag = true;
						}
					}

					if(isItemSentToCharity != null){
						if((isItemSentToCharity.equalsIgnoreCase("Y")) && (primeLineNo.equalsIgnoreCase(retPrimeLineNo))){

							isItemSentToCharityFlag = true;
						}
					}
				}

				if((isOutsideOfReturnPolicyFlag == true) || (isItemSentToCharityFlag == true)){

					Element orderLine2 = orderDoc2.createElement("OrderLine"); 
					orderLine2.setAttribute("PrimeLineNo", primeLineNo);
					orderLine2.setAttribute("SubLineNo", "1"); 

					Element linePriceInfo  = orderDoc2.createElement("LinePriceInfo"); 
					linePriceInfo.setAttribute("IsLinePriceForInformationOnly", "Y");

					orderLine2.appendChild(linePriceInfo);

					orderLinesEle.appendChild(orderLine2);

					if(isOutsideOfReturnPolicyFlag == true){

						Element note = orderDoc2.createElement("Note");

						note.setAttribute("NoteText", "This "+itemID+" was returned outside of return policy and sent to Charity, Refund not generated ");
						note.setAttribute("Tranid", "changeOrder");
						note.setAttribute("VisibleToAll", "Y");
						notes.appendChild(note);
					}

					if(isItemSentToCharityFlag == true){

						Element note = orderDoc2.createElement("Note");

						ArrayList<Object[]> alTimeStamp = FANDBUtil.getDBResult(env, OrderNotesConstants.SQL_GET_DBTIME, 1);
						Object[] resultRow 				= alTimeStamp.get(0);
						String strCurrentDBDateTime 	= (String) resultRow[0];
						System.out.println(strCurrentDBDateTime);

						note.setAttribute("NoteText", "PID "+itemID+" "+itemShortDesc+" returned in Commonwealth, damaged item sent to charity, "+strCurrentDBDateTime);
						note.setAttribute("Tranid", "changeOrder");
						note.setAttribute("VisibleToAll", "Y");
						notes.appendChild(note);
					}

				}
				if(isOutsideOfReturnPolicyFlag == false){
					Exception e = new Exception();
					e.fillInStackTrace();
				}

			}
		}

		orderDoc2.getDocumentElement().appendChild(notes);
		orderDoc2.getDocumentElement().appendChild(orderLinesEle);

		// calling changeOrder to set the line prices to zero
		logger.verbose("-------FanReturnReceipt----changeOrder---ip---88--"
				+XMLUtil.getXMLString(orderDoc2));

		Document changeOrderOp = CommonUtil.invokeAPI
				(env,"changeOrder", orderDoc2);

		logger.verbose("-------FanReturnReceipt----changeOrder---op--89--"
				+XMLUtil.getXMLString(changeOrderOp));



		logger.verbose("--------------FanReturnReceipt------checkIfReturnPolicyOutside exit----"
				+XMLUtil.getXMLString(inputDoc));

		return inputDoc;
	}
} 
