/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
                     LIMITATION OF LIABILITY
THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
*/

package com.nwcg.icbs.yantra.api.receipt;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Hashtable;
import java.util.Enumeration;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGGetPOLinesToReceive implements YIFCustomApi
{
	
	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger();
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	
	
	public Document getPOLinesToReceive(YFSEnvironment env, Document doc) throws Exception
	{
		logger.verbose("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
				"Input document : " + XMLUtil.extractStringFromDocument(doc));
		//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - Input document : " + XMLUtil.extractStringFromDocument(doc));
		
		String receivingNode 	= doc.getDocumentElement().getAttribute(NWCGConstants.RECEIVING_NODE);
		//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive -  Receiving Node = " + receivingNode);
		
		String documentType 	= doc.getDocumentElement().getAttribute(NWCGConstants.DOCUMENT_TYPE);
		//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive -  Document Type = " + documentType);
		
		String shipmentKey	= doc.getDocumentElement().getAttribute(NWCGConstants.SHIPMENT_KEY);
		//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive -  shipmentKey = " + shipmentKey );
		
		String orderHeaderKey	= doc.getDocumentElement().getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive -  Order Header Key = " + orderHeaderKey + " Shipment Key = " +shipmentKey +" Document Type = " + documentType + " Receiving Node = " + receivingNode);
		
	
		Document docShipmentInput				= null;
		Document docShipmentTemplate			= null;
		Document docShipmentOutput				= null;
		Document docGetOrderDetailsInput		= null;
		Document docGetOrderDetailsTemplate		= null;
		Document docGetOrderDetailsOutput		= null;
		Document docGetLinesToReceiveInput		= null;
		Document docGetLinesToReceiveTemplate	= null;
		Document docGetLinesToReceiveOutput		= null;
		//Update 03-17-11
		Document docGetShipmentLineListInput	= null;
		Document docGetShipmentLineListOutput	= null;
		Document docGetShipmentLineListtemplate = null;
				
		try
		{
			docShipmentInput 	= XMLUtil.createDocument("Shipment");
			docShipmentTemplate = XMLUtil.createDocument("Shipment");
			docGetOrderDetailsInput = XMLUtil.createDocument("Order");
			docGetOrderDetailsTemplate = XMLUtil.createDocument("Order");
			docGetLinesToReceiveInput 		= XMLUtil.createDocument("GetLinesToReceive");
			docGetLinesToReceiveTemplate	= XMLUtil.createDocument("GetLinesToReceive");
			
			Element elemGetShipmentDeails = docShipmentInput.getDocumentElement();
			elemGetShipmentDeails.setAttribute(NWCGConstants.SHIPMENT_KEY, shipmentKey);
			docShipmentTemplate.getDocumentElement().setAttribute(NWCGConstants.SHIPMENT_KEY, NWCGConstants.EMPTY_STRING);
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - getShipmentDetails Input document : " + XMLUtil.extractStringFromDocument(docShipmentInput));
			docShipmentOutput = CommonUtilities.invokeAPI(env,"getShipmentDetails", docShipmentInput);
			
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - getShipmentDetails Output document : " + XMLUtil.extractStringFromDocument(docShipmentOutput));
			
			orderHeaderKey	= docShipmentOutput.getDocumentElement().getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			
			/* ML - UPDATE 03-17-11
			 * It appears that Cache-to-Cache shipments MAY NOT hoave the Order Information Attached to the Shipment record.
			 * We need to add a check if the OrderHeader Key is returned from the getShipmentDetail API call, and if not, call 
			 * getShipmentLineList  and get the 1st line returned info to get the OrderHeaderKey...
			 */
			if(orderHeaderKey.equals(""))
			{
				//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - Shipment Does not have Order Info attached. Attempting to get Order info fromn shipment line.");
				
				docGetShipmentLineListInput = XMLUtil.createDocument("ShipmentLine");
				Element elemGetShipmentLineList = docGetShipmentLineListInput.getDocumentElement();
				elemGetShipmentLineList.setAttribute(NWCGConstants.SHIPMENT_KEY, shipmentKey);
				
				docShipmentOutput = CommonUtilities.invokeAPI(env,"getShipmentLineList", docGetShipmentLineListInput);
				
				NodeList shipmentLineNL = docShipmentOutput.getElementsByTagName("ShipmentLine");
				
				if (shipmentLineNL == null || shipmentLineNL.getLength() < 1) {
					throw new YFSException("No shipment lines found. Unable to get Order for Shipment");
				}
				
				for (int i = 0; i < 1; i++) {
					
					Node curShipmentLineNode = shipmentLineNL.item(i);
					
					
					Element elemSL = (Element) curShipmentLineNode;
					orderHeaderKey = elemSL.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
					//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - Shipment Line Order Header Key = " + orderHeaderKey);
				
				}
			}
			// END UPDATE 03-17-11 
			
			logger.verbose("NWCGgetPOLinesToReceive::getPOLinesToReceive -  OrderHeader Key = " + orderHeaderKey);
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive -  OrderHeader Key = " + orderHeaderKey);
			
			//--Need to Build Template here ---
			
			StringBuffer sbGetOrderDetailsTemplate = new StringBuffer("<Order OrderType=\"\">");
			sbGetOrderDetailsTemplate.append("<OrderLines><OrderLine OrderHeaderKey=\"\" OrderLineKey=\"\" ConditionVariable1=\"\" ConditionVariable2=\"\" PrimeLineNo=\"\" SubLineNo=\"\" ");
			sbGetOrderDetailsTemplate.append("IntentionalBackorder=\"\" ItemGroupCode=\"\" OrderClass=\"\" OriginalOrderedQty=\"\" ShipNode=\"\" isHistory=\"\">");
			sbGetOrderDetailsTemplate.append("<Item ItemID=\"\" ProductClass=\"\"/><OrderLineTranQuantity OrderedQty=\"\" TransactionalUOM=\"\"/>");
			sbGetOrderDetailsTemplate.append("<Extn/></OrderLine></OrderLines></Order>");		
			String sGetOrderDetailsTemplate = sbGetOrderDetailsTemplate.toString();
			
			docGetOrderDetailsTemplate = XMLUtil.getDocument(sGetOrderDetailsTemplate);
			
			Element elemGetOrderDeails = docGetOrderDetailsInput.getDocumentElement();
			elemGetOrderDeails.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive - getOrderDetails Input document : " + XMLUtil.extractStringFromDocument(docGetOrderDetailsInput));
			docGetOrderDetailsOutput = CommonUtilities.invokeAPI(env,docGetOrderDetailsTemplate,"getOrderDetails", docGetOrderDetailsInput);
			
			String orderType = docGetOrderDetailsOutput.getDocumentElement().getAttribute("OrderType");
			System.out.println("NWCGgetPOLinesToReceive:getPOLinesToReceive: orderType = " + orderType);
			
			logger.verbose("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
					"Return from getOrderDetails : " + XMLUtil.extractStringFromDocument(docGetOrderDetailsOutput));
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
			//		"Return from getOrderDetails : " + XMLUtil.extractStringFromDocument(docGetOrderDetailsOutput));
			
			Hashtable<String, String> primeLineNum = new Hashtable();
			
			NodeList orderLineNL = docGetOrderDetailsOutput.getElementsByTagName(NWCGConstants.ORDER_LINE);
			
			if (orderLineNL == null || orderLineNL.getLength() < 1) {
				throw new YFSException("No order lines found");
			}
			
			for (int i = 0; i < orderLineNL.getLength(); i++) {
				
				Node curOrderLineNode = orderLineNL.item(i);
				String LineNum = null;
				
				LineNum = getPrimeLinesWithoutExtnToReceive(curOrderLineNode);
				//System.out.println("LineNum = " + LineNum );
				
				if(LineNum != null){
					Element elemOL = (Element) curOrderLineNode;
					String  orderLine = elemOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					primeLineNum.put(LineNum,orderLine);
				}
			
			}
			
			//System.out.println("ArrayList = " + primeLineNum );
			
			Element elemGetLinesToReceive = docGetLinesToReceiveInput.getDocumentElement();
			elemGetLinesToReceive.setAttribute(NWCGConstants.SHIPMENT_KEY, shipmentKey);
			elemGetLinesToReceive.setAttribute(NWCGConstants.RECEIVING_NODE, receivingNode);
			elemGetLinesToReceive.setAttribute(NWCGConstants.DOCUMENT_TYPE, documentType);
			docGetLinesToReceiveTemplate.getDocumentElement().setAttribute(NWCGConstants.GET_LINES_TO_RECEIVE, NWCGConstants.EMPTY_STRING);
			
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
			//		"Input To getLineToReceive : " + XMLUtil.extractStringFromDocument(docGetLinesToReceiveInput));
			
			StringBuffer sbGetLinesToReceiveTemplate = new StringBuffer("<GetLinesToReceive>");
			sbGetLinesToReceiveTemplate.append("<ReceivableLineList><ReceivableLine AvailableToReceiveQuantity=\"\" EnterpriseCode=\"\" ExpectedQuantity=\"\" ItemID=\"\" KitCode=\"\" ");
			sbGetLinesToReceiveTemplate.append("KitQty=\"\" LineType=\"\" OrderHeaderKey=\"\" OrderLineKey=\"\" OrderNo=\"\" OrderReleaseKey=\"\" ");
			sbGetLinesToReceiveTemplate.append("PrimeLineNo=\"\" ProductClass=\"\" ReceivedQuantity=\"\" ReleaseNo=\"\" RequestedSerialNo=\"\" RequiresDetailAttributes=\"\" ");
			sbGetLinesToReceiveTemplate.append("Segment=\"\" SegmentType=\"\" ShipmentLineKey=\"\" ShipmentLineNo=\"\" SubLineNo=\"\" SuggestedDispositionCode=\"\" ");
			sbGetLinesToReceiveTemplate.append("TotalQuantity=\"\" UnitOfMeasure=\"\" />");
			sbGetLinesToReceiveTemplate.append("<KitLines><ReceivableLine AvailableToReceiveQuantity=\"\" EnterpriseCode=\"\" ItemID=\"\" KitCode=\"\" KitQty=\"\" OrderHeaderKey=\"\" ");
			sbGetLinesToReceiveTemplate.append("OrderLineKey=\"\" OrderNo=\"\" OrderReleaseKey=\"\" PrimeLineNo=\"\" ProductClass=\"\" ReceivedQuantity=\"\" ReleaseNo=\"\" RequestedSerialNo=\"\" ");
			sbGetLinesToReceiveTemplate.append("RequiresDetailAttributes=\"\" ShipmentLineKey=\"\" ShipmentLineNo=\"\" SubLineNo=\"\" SuggestedDispositionCode=\"\" TotalQuantity=\"\" UnitOfMeasure=\"\" /> ");
			sbGetLinesToReceiveTemplate.append("</KitLines></ReceivableLineList></GetLinesToReceive>");		
			
			String sGetLinesToReceiveTemplate = sbGetLinesToReceiveTemplate.toString();
			
			docGetLinesToReceiveTemplate = XMLUtil.getDocument(sGetLinesToReceiveTemplate);
			docGetLinesToReceiveOutput = CommonUtilities.invokeAPI(env,"getLinesToReceive", docGetLinesToReceiveInput);
			
			logger.verbose("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
					"Return from getLineToReceive : " + XMLUtil.extractStringFromDocument(docGetLinesToReceiveOutput));
			//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
			//		"Return from getLineToReceive : " + XMLUtil.extractStringFromDocument(docGetLinesToReceiveOutput));
			
					
			Element elmRoot = docGetLinesToReceiveOutput.getDocumentElement();
			
			Element elmOLs = null;
			NodeList nlRootChilds = elmRoot.getChildNodes();
			
			for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++){
				Node tmpNode = nlRootChilds.item(rootNode);
				if (tmpNode.getNodeName().equals("ReceivableLineList")){
					elmOLs = (Element) tmpNode; 
				}
			}
			
			if(elmOLs != null)
			{
				NodeList nlOrderLine = elmOLs.getElementsByTagName("ReceivableLine");
				int noOfOLs = nlOrderLine.getLength();
				for (int i=noOfOLs; i > 0; i--){
					Element elmOL = (Element) nlOrderLine.item(i-1);
					elmOL.setAttribute("OrderType", orderType);
					String olPrimeNum = elmOL.getAttribute("PrimeLineNo");
					if (primeLineNum.containsKey(olPrimeNum)){
						elmOLs.removeChild(nlOrderLine.item(i-1));
					}
				}
			}
			
			//System.out.println("Std Lines removed");
			//System.out.println("Hashtable = " + primeLineNum.toString());
	
			//Here is where KitLines need to be checked as well....
			
				
			Element elmKitRoot = docGetLinesToReceiveOutput.getDocumentElement();
			
			Element elmKitOLs = null;
			NodeList nlKitRootChilds = elmKitRoot.getChildNodes();
			
			for (int rootNode=0; rootNode < nlKitRootChilds.getLength(); rootNode++){
				Node tmpNode = nlKitRootChilds.item(rootNode);
				//System.out.println("tmpNode Name = " +tmpNode.getNodeName());
				if (tmpNode.getNodeName().equals("KitLines")){
					elmKitOLs = (Element) tmpNode; 
				}
			}
			
			if(elmKitOLs != null)
			{
				NodeList nlKitOrderLine = elmKitOLs.getElementsByTagName("ReceivableLine");
			
				int noOfKitOLs = nlKitOrderLine.getLength();
			
				for (int i=noOfKitOLs; i > 0; i--){
					Element elmKitOL = (Element) nlKitOrderLine.item(i-1);
					elmKitOL.setAttribute("OrderType", orderType);
					String olPrimeNum = elmKitOL.getAttribute("PrimeLineNo");

					if (primeLineNum.contains(olPrimeNum)){
						elmKitOLs.removeChild(nlKitOrderLine.item(i-1));
					}
				}
			}
			 
			
			// Now we need to update the OrderLines ExtnToReceive back to default state of yes, so all lines
			/* show "Y" the next time they are called on to receive....
			
			Document docChangeOrderInput = null;
			Document docChangeOrderOutput = null;
			Enumeration enumHashKeys = primeLineNum.keys();
			StringBuffer sbChangeOrderInput = new StringBuffer();
			
			if(primeLineNum.isEmpty()== false)
			{
				sbChangeOrderInput.append("<Order OrderHeaderKey=\""+ orderHeaderKey +"\" Override=\"Y\"><OrderLines>");
							
				for(int x = 0;x < primeLineNum.size();x++)
				{
					sbChangeOrderInput.append("<OrderLine PrimeLineNo=\"");
					String keyValue = (String) enumHashKeys.nextElement();
					sbChangeOrderInput.append(keyValue+"\" OrderLineKey=\"");
					String olKey = primeLineNum.get(keyValue);
					sbChangeOrderInput.append(olKey+"\"><Extn ExtnToReceive=\"Y\" /></OrderLine>");
				}
				sbChangeOrderInput.append("</OrderLines></Order>");
				
				String sChangeOrderInput = sbChangeOrderInput.toString();
				docChangeOrderInput = XMLUtil.getDocument(sChangeOrderInput);
				
				//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
						"Input for changeOrder : " + XMLUtil.extractStringFromDocument(docChangeOrderInput));
				
				docChangeOrderOutput = CommonUtilities.invokeAPI(env,"changeOrder", docChangeOrderInput);
				
				//System.out.println("NWCGgetPOLinesToReceive::getPOLinesToReceive, " +
						"Output From changeOrder : " + XMLUtil.extractStringFromDocument(docChangeOrderOutput));
				
			}
			*/		
			
		}
		catch(ParserConfigurationException pce){
			pce.printStackTrace();
		}
		//System.out.println("NWCGGetPOLinesToReceive: getPOLinesToReceive: docGetLinesToReceiveOutput: \n" + XMLUtil.getXMLString(docGetLinesToReceiveOutput));
		return docGetLinesToReceiveOutput;
	}
	
	private String getPrimeLinesWithoutExtnToReceive(Node curOrderLineNode)
	throws Exception
	{
		String primeLineNum = null;
		//Get the Extn Element
		Element curOrderLine = (curOrderLineNode instanceof Element) ? (Element) curOrderLineNode : null;
		Element extnElem = getExtnElementForOrderLine(curOrderLine);
		Element orderLineElem = (Element)curOrderLineNode;
		//Get  ExtnToreceive
		String strExtnToReceive = extnElem.getAttribute(NWCGConstants.NWCG_EXTN_TO_RECIEVE);
		//System.out.println("strExtnToReceive = " + strExtnToReceive);
		//If ExtnToreceive == "N", get the PrimeLineNo
		if(strExtnToReceive.equals("N")){
			primeLineNum = orderLineElem.getAttribute(NWCGConstants.PRIME_LINE_NO);
			//System.out.println("primeLineNum = " + primeLineNum);
		}
		
		return primeLineNum;
	}
	private Element getExtnElementForOrderLine(Element curOrderLine) throws Exception {
		NodeList nl = curOrderLine.getElementsByTagName(NWCGConstants.EXTN);
		
		if (nl == null || nl.getLength() != 1)
			throw new YFSException("Unable to find Extn element underneath OrderLine");

		return (Element) nl.item(0);
	}
	
}