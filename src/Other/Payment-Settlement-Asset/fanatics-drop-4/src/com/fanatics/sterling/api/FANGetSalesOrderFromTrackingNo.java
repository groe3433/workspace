package com.fanatics.sterling.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.ibm.sterling.afc.restdocgen.DocValidator.SysoutCallback;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FANGetSalesOrderFromTrackingNo implements YIFCustomApi {

	private static YFCLogCategory log = YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props = null;
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	public Document getSalesOrderNo(YFSEnvironment env, Document inDoc) throws Exception{
		

		
		Element inputEle = inDoc.getDocumentElement();
		String trackingNo = inputEle.getAttribute("TrackingNo");
		String extnReturnTrackingNo = inputEle.getAttribute("ExtnReturnTrackingNo");
		String customerFirstName = inputEle.getAttribute("CustomerFirstName");
		String customerLastName = inputEle.getAttribute("CustomerLastName");
		String customerEMailID = inputEle.getAttribute("CustomerEMailID");
		String customerPhoneNo = inputEle.getAttribute("CustomerPhoneNo");
		String orderNo = inputEle.getAttribute("OrderNo");
		String documentType = inputEle.getAttribute("DocumentType");
		String enterpriseCode = inputEle.getAttribute("EnterpriseCode");

		
		if(trackingNo != null){
			if(!trackingNo.equalsIgnoreCase("")){
				
				Document containerDoc = XMLUtil.createDocument("Container");
				Element containerEle = containerDoc.getDocumentElement();
				
				containerEle.setAttribute("TrackingNo", trackingNo);
				
				log.verbose("--------------FANGetSalesOrderFromTrackingNo------10---"
						+XMLUtil.getXMLString(containerDoc));
			
			Document outDoc = CommonUtil.invokeAPI
					(env,"global/template/api/getShipmentContainerList_ip.xml","getShipmentContainerList", containerDoc);
			
			log.verbose("--------------FANGetSalesOrderFromTrackingNo------11---"
					+XMLUtil.getXMLString(outDoc));
			
			
			Document containersDoc = outDoc;
			Element containerEle2 = (Element)containersDoc.getElementsByTagName("Container").item(0);
			
			String shipmentKey = containerEle2.getAttribute("ShipmentKey");
			
			Document shipmentDoc = XMLUtil.createDocument("Shipment");
			Element shipmentEle = shipmentDoc.getDocumentElement();
			shipmentEle.setAttribute("ShipmentKey", shipmentKey);
			
			log.verbose("--------------FANGetSalesOrderFromTrackingNo------12---"
					+XMLUtil.getXMLString(shipmentDoc));
		
		Document outDoc2 = CommonUtil.invokeAPI
				(env,"getShipmentDetails", shipmentDoc);
		
		log.verbose("--------------FANGetSalesOrderFromTrackingNo------13---"
				+XMLUtil.getXMLString(outDoc2));
			
		Element  shipmentEle2 =  (Element)XPathUtil.getXpathNode(outDoc2, "/Shipment/ShipmentLines/ShipmentLine");
		orderNo = shipmentEle2.getAttribute("OrderNo");
		
				
			}
			
			
		}
			
			if(extnReturnTrackingNo != null){
				if(!extnReturnTrackingNo.equalsIgnoreCase("")){
					
					Document containerDoc = XMLUtil.createDocument("Container");
					Element containerEle = containerDoc.getDocumentElement();
					Element extn = containerDoc.createElement("Extn");
					extn.setAttribute("ExtnReturnTrackingNo", extnReturnTrackingNo);
					containerEle.appendChild(extn);
					
					log.verbose("--------------FANGetSalesOrderFromTrackingNo------14---"
							+XMLUtil.getXMLString(containerDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"global/template/api/getShipmentContainerList_ip.xml","getShipmentContainerList", containerDoc);
				
				log.verbose("--------------FANGetSalesOrderFromTrackingNo------15---"
						+XMLUtil.getXMLString(outDoc));
				
				
				Document containersDoc = outDoc;
				Element containerEle2 = (Element)containersDoc.getElementsByTagName("Container").item(0);
				
				String shipmentKey = containerEle2.getAttribute("ShipmentKey");
				
				Document shipmentDoc = XMLUtil.createDocument("Shipment");
				Element shipmentEle = shipmentDoc.getDocumentElement();
				shipmentEle.setAttribute("ShipmentKey", shipmentKey);
				
				log.verbose("--------------FANGetSalesOrderFromTrackingNo------16---"
						+XMLUtil.getXMLString(shipmentDoc));
			
			Document outDoc2 = CommonUtil.invokeAPI
					(env,"getShipmentDetails", shipmentDoc);
			
			log.verbose("--------------FANGetSalesOrderFromTrackingNo------17---"
					+XMLUtil.getXMLString(outDoc2));
				
			Element  shipmentEle2 =  (Element)XPathUtil.getXpathNode(outDoc2, "/Shipment/ShipmentLines/ShipmentLine");
			orderNo = shipmentEle2.getAttribute("OrderNo");
			
					
				}
			
			}
			
			
			
		
			
			
			
			if(documentType.equalsIgnoreCase("0001")){
				
		
			Document orderDoc = XMLUtil.createDocument("Order");
			Element orderEle = orderDoc.getDocumentElement();
			orderEle.setAttribute("OrderNo", orderNo);
			orderEle.setAttribute("CustomerFirstName", customerFirstName);
			orderEle.setAttribute("CustomerLastName", customerLastName);
			orderEle.setAttribute("CustomerEMailID", customerEMailID);
			orderEle.setAttribute("CustomerPhoneNo", customerPhoneNo);
			orderEle.setAttribute("DocumentType", "0001");
			orderEle.setAttribute("EnterpriseCode", enterpriseCode);
			

			log.verbose("--------------FANGetSalesOrderFromTrackingNo------1--1-"
						+XMLUtil.getXMLString(orderDoc));
			
			Document outDoc3 = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
			
			log.verbose("--------------FANGetSalesOrderFromTrackingNo------1--2-"
					+XMLUtil.getXMLString(outDoc3));
			
			NodeList orderList = outDoc3.getElementsByTagName("Order");
			List<String> salesOrderList = new ArrayList<String>();
			
			for(int i=0; i<orderList.getLength(); i++){
				
				Element orderDocEle = (Element)orderList.item(i);
				log.verbose("--------------FANGetSalesOrderFromTrackingNo------1--3-"
						+XMLUtil.getElementXMLString(orderDocEle));
				String salesOrderNo = orderDocEle.getAttribute("OrderNo");
				salesOrderList.add(salesOrderNo);
				orderDocEle.getParentNode().removeChild(orderDocEle);
				i--;
			}
			
			log.verbose("---------salesOrderList.size()-----------"+salesOrderList.size());
//			<Order EnterpriseCode="FANATICS_WEB" DocumentType="0001" OrderHeaderKey="20160611025539128419" OrderNo="F200017">
//			<OrderLines>
//			<OrderLine PrimeLineNo="1">
//			</OrderLine>
//			</OrderLines>
//			</Order>
			
			if(salesOrderList.size()>0){
				
				for(int p=0; p<salesOrderList.size(); p++){
					
					String orderNo2 = salesOrderList.get(p);
					
					Document orderDoc3 = XMLUtil.createDocument("Order");
					Element orderEle3 = orderDoc3.getDocumentElement();
					orderEle3.setAttribute("OrderNo", orderNo2);
					orderEle3.setAttribute("DocumentType", "0001");
					orderEle3.setAttribute("EnterpriseCode", enterpriseCode);
					
					Element orderLines = orderDoc3.createElement("OrderLines");
					Element orderLine = orderDoc3.createElement("OrderLine");
					orderLines.appendChild(orderLine);
					orderEle3.appendChild(orderLines);
					
					
					log.verbose("--------------FANGetSalesOrderFromTrackingNo------18---"
							+XMLUtil.getXMLString(orderDoc3));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"global/template/api/getCompleteOrderDetails_returnly.xml","getCompleteOrderDetails", orderDoc3);
				
				log.verbose("--------------FANGetSalesOrderFromTrackingNo------19---"
						+XMLUtil.getXMLString(outDoc));
//				NodeList orderLineList = outDoc3.getElementsByTagName("OrderLine");
				
				NodeList returnOrderLinesList = outDoc.getElementsByTagName("ReturnOrderLines");		
				
				
				for(int k=0; k<returnOrderLinesList.getLength(); k++){
					
					Element returnOrderLines = (Element)returnOrderLinesList.item(k);
					
					Element salesOrderLine = (Element)returnOrderLines.getParentNode();
					 
					String salesPrimeLineNo = salesOrderLine.getAttribute("PrimeLineNo");
					
					log.verbose("--------------FANGetSalesOrderFromTrackingNo------19--1-"
							+XMLUtil.getElementXMLString(returnOrderLines));
					

					NodeList returnLineList = returnOrderLines.getElementsByTagName("OrderLine");
					
					List<String> retOrderHeaderKeyList = new ArrayList<String>();
					
					for(int i=0; i<returnLineList.getLength(); i++){
						
						Element orderLine2 = (Element)returnLineList.item(i);
						String retOrderHeaderKey = orderLine2.getAttribute("OrderHeaderKey");
						retOrderHeaderKeyList.add(retOrderHeaderKey);
						orderLine2.getParentNode().removeChild(orderLine2);
						i--;
					}
					
					
						
					for(int i=0; i<retOrderHeaderKeyList.size(); i++){
						
						log.verbose("------------i--------------"+i+"------------"+retOrderHeaderKeyList.size());
//						Element orderLine2 = (Element)returnLineList.item(i);
//						String retOrderHeaderKey = orderLine2.getAttribute("OrderHeaderKey");
						
						log.verbose("--------------FANGetSalesOrderFromTrackingNo------19--2-"+retOrderHeaderKeyList.get(i));
						
						Document orderDoc2 = XMLUtil.createDocument("Order"); 
						Element orderEle2 = orderDoc2.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", retOrderHeaderKeyList.get(i));
						
						
						log.verbose("--------------FANGetSalesOrderFromTrackingNo------20---"
									+XMLUtil.getXMLString(orderDoc2));
						
						Document outDoc2 = CommonUtil.invokeAPI
								(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc2);
						
						log.verbose("--------------FANGetSalesOrderFromTrackingNo------21---"
								+XMLUtil.getXMLString(outDoc2));
						
						Element retOrderEle = (Element)outDoc2.getElementsByTagName("Order").item(0);
						
						Element retOrderEleClone = (Element) outDoc.importNode(retOrderEle
								.cloneNode(true), true);
						returnOrderLines.appendChild(retOrderEleClone);


						
						log.verbose("--------------FANGetSalesOrderFromTrackingNo------21--1-"
								+XMLUtil.getElementXMLString(returnOrderLines));
						
						log.verbose("-----"+salesPrimeLineNo+"---------FANGetSalesOrderFromTrackingNo------21-2--"
								+XMLUtil.getXMLString(outDoc));
						
						
						log.verbose("--------------FANGetSalesOrderFromTrackingNo------21-5--"
								+XMLUtil.getXMLString(outDoc));
						

					}

					
					}
				
				Element salesOrderEle = outDoc.getDocumentElement();
				
				Element salesOrderEleClone2 = (Element) outDoc3.importNode(salesOrderEle
						.cloneNode(true), true);
				outDoc3.getDocumentElement().appendChild(salesOrderEleClone2);
				
				inDoc = outDoc3;
				}
				
							
			}
			

	}	
		
		log.verbose("--------------FANGetSalesOrderFromTrackingNo------22---"
				+XMLUtil.getXMLString(inDoc));
			
			return inDoc;
		
	}

}
