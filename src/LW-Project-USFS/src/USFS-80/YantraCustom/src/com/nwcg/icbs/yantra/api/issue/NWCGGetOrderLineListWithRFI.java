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

package com.nwcg.icbs.yantra.api.issue;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGGetOrderLineListWithRFI implements YIFCustomApi {

	//Added by Gaurav for RFI update -Start
	Document docChangeOrderDoc = null;
	Element eleOrderLineList = null;
	//Added by Gaurav for RFI update -End
	/**
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document getOrderLineListWithRFI(YFSEnvironment env, Document inDoc)
			throws Exception {

		logger.beginTimer("getOrderLineListWithRFI BEGIN");
		logger.verbose("NWCGGetIncidentDtlsForIssueCreate::getIncidentDetails, "
				+ "Input Document : "
				+ XMLUtil.extractStringFromDocument(inDoc));

		this.myEnvironment = env;	
		
		Document orderLineListOutput = getOrderLineList(inDoc);
		
		//Added by aoadio to identify if an item in the order-line isSerialTracked -Start
		NodeList orderLineItems = orderLineListOutput.getElementsByTagName(NWCGConstants.ITEM);
		if(orderLineItems !=  null){
			for (int i = 0; i < orderLineItems.getLength(); i++) {
				//get the order-item id
				Node orderLineItem = orderLineItems.item(i);
				String strItemId =  ((Element) orderLineItem).getAttribute(NWCGConstants.ITEM_ID); 
				
				//build input document to API getItemList
				Document doc = XMLUtil.createDocument(NWCGConstants.ITEM);
				Element root_ele = doc.getDocumentElement();
				root_ele.setAttribute(NWCGConstants.ITEM_ID, strItemId);
				root_ele.setAttribute(NWCGConstants.ORGANIZATION_CODE, NWCGConstants.ENTERPRISE_CODE);
	
				//build output template to API getItemList
				StringBuffer opTemplateBuff = new StringBuffer("");
				opTemplateBuff.append("<ItemList>");
				opTemplateBuff.append("<Item ItemID=\"\" >");
				opTemplateBuff.append("<InventoryParameters  IsSerialTracked=\"\"/>");
				opTemplateBuff.append("</Item>");
				opTemplateBuff.append("</ItemList>");
				String opTemplate = opTemplateBuff.toString();
				Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
				
				//invoke getItemList API	
				Document opDoc = CommonUtilities.invokeAPI(myEnvironment, opTemplateDoc, 
											NWCGConstants.API_GET_ITEM_LIST, doc);
				
				NodeList invParams =   opDoc.getElementsByTagName("InventoryParameters");
				if(invParams!=null && invParams.getLength() > 0){
					String IsSerialTracked = ((Element) invParams.item(0)).getAttribute("IsSerialTracked");
					if(null != IsSerialTracked){
						((Element) orderLineItem).setAttribute("IsSerialTracked", IsSerialTracked);
					}
				}
				
				
			}
		}
		
		
		//Added by aoadio to identify if an item in the order-line isSerialTracked -End
		
		NodeList orderLineNL = orderLineListOutput
				.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		// Added by Gaurav for RFI update -Start
		String strOrderHeaderKey = ((Element)(orderLineNL.item(0))).getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		docChangeOrderDoc = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element eleChangeOrderDoc = docChangeOrderDoc.getDocumentElement();
		eleChangeOrderDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, strOrderHeaderKey);
		
		eleOrderLineList = docChangeOrderDoc.createElement(NWCGConstants.ORDER_LINES);
		eleChangeOrderDoc.appendChild(eleOrderLineList);

		setExtnQtyRfiOnAllOrderLines(orderLineNL);
		
		updateOrderWithNewRFIQty();
		//Added by Gaurav for RFI update -End
		logger.endTimer("getOrderLineListWithRFI END");
		return orderLineListOutput;
	}

	private void setExtnQtyRfiOnAllOrderLines(NodeList orderLineNL)
			throws Exception {

		if (orderLineNL == null || orderLineNL.getLength() < 1) {
			throw new YFSException("No order lines found");
		}

		String strInventoryStatus  = NWCGConstants.RFI_STATUS;
		String strOrganizationCode = NWCGConstants.ENTERPRISE_CODE;

		double TotalLocationInvQty = 0.00;
		double TotalDemand = 0.00;
		double TotalAvailRFI = 0.00;
		
		for (int i = 0; i < orderLineNL.getLength(); i++) {
			Node curOrderLineNode = orderLineNL.item(i);
			Element curOrderLine = (curOrderLineNode instanceof Element) ? (Element) curOrderLineNode : null;
			if (curOrderLine == null) continue;
			
			Element itemElm = getItemElementForOrderLine(curOrderLine);

			String strItemID = itemElm.getAttribute(NWCGConstants.ITEM_ID);
			String strPC = itemElm.getAttribute(NWCGConstants.PRODUCT_CLASS);
			String strUOM = itemElm.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
			String shipNode = curOrderLine.getAttribute(NWCGConstants.SHIP_NODE);
			//Added by Gaurav for RFI update -Start
			String strOrderLineKey = curOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			//Added by Gaurav for RFI update -End
			Document getNodeInventoryDoc = getNodeInventoryInputDoc(
					shipNode, strInventoryStatus, strItemID);
			Document getATPDoc = getATPInputDoc(shipNode, strItemID, strPC,
					strUOM, strOrganizationCode);

			String nodeInventoryOpTemplate = "<NodeInventory><LocationInventoryList TotalNumberOfRecords=\"\"><LocationInventory "
					+ "LocationId=\"\" PendInQty=\"\" PendOutQty=\"\" Quantity=\"\" ZoneId=\"\"><InventoryItem ItemID=\"\"><Item "
					+ "ItemID=\"\"></Item></InventoryItem></LocationInventory></LocationInventoryList></NodeInventory>";
			
			String getATPOpTemplate = "<InventoryInformation><Item AdvanceNotificationTime=\"\" AvailableToSell=\"\" "
					+ "CalculateProjectedOnhandQty=\"\" Description=\"\" EndDate=\"\" ItemID=\"\" LeadTime=\"\" "
					+ "OrganizationCode=\"\" PeriodicalLength=\"\" ProcessingTime=\"\" ProductClass=\"\" "
					+ "ShipNode=\"\" ShortDescription=\"\" TagControl=\"\"  TimeSensitive=\"\" TrackedEverywhere=\"\" "
					+ "UnitOfMeasure=\"\"><InventoryTotals><Demands TotalDemand=\"\"><Demand DemandType=\"\" "
					+ "OrganizationCode=\"\" Quantity=\"\"/></Demands></InventoryTotals></Item></InventoryInformation>";

			Document nodeInventoryOutput = null;
			Document atpOutput = null;

			try {
				Document getNodeInventoryOpT = XMLUtil.getDocument(nodeInventoryOpTemplate);	
				Document getATPOpT = XMLUtil.getDocument(getATPOpTemplate);

				nodeInventoryOutput = CommonUtilities.invokeAPI(myEnvironment,
						getNodeInventoryOpT, "getNodeInventory",
						getNodeInventoryDoc);
				
				logger.verbose("getATP Input: "+XMLUtil.extractStringFromDocument(getATPDoc));
				atpOutput = CommonUtilities.invokeAPI(myEnvironment, getATPOpT, "getATP", getATPDoc);
				logger.verbose("getATP Output: "+ XMLUtil.extractStringFromDocument(atpOutput));
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			if (nodeInventoryOutput != null) {

				NodeList locationInventoryNL = nodeInventoryOutput.getElementsByTagName("LocationInventory");

				if (locationInventoryNL != null	&& locationInventoryNL.getLength() > 0) {
					Node curLocationInventoryNode = null;
					for (int j = 0; j < locationInventoryNL.getLength(); j++) {
						curLocationInventoryNode = locationInventoryNL.item(j);
						Element curLocationInventoryElm = (curLocationInventoryNode instanceof Element) ? (Element) curLocationInventoryNode
								: null;
						if (curLocationInventoryElm == null) continue;

						String strQty = curLocationInventoryElm.getAttribute(NWCGConstants.QUANTITY);

						double dQty = Double.parseDouble(strQty);
						TotalLocationInvQty = TotalLocationInvQty + dQty;
					}
				}
			}
			
			if (atpOutput != null) {
				NodeList inventoryTotalsNL = atpOutput.getElementsByTagName("InventoryTotals");
				if (inventoryTotalsNL != null && inventoryTotalsNL.getLength() == 1) {
					Node curInventoryTotalNode = inventoryTotalsNL.item(0);
					Element curInventoryTotalElm = (curInventoryTotalNode instanceof Element) ? 
							(Element) curInventoryTotalNode : null;
					if (curInventoryTotalElm == null) throw new YFSException("Output template of getATP is missing InventoryInformation/InventoryTotals");
					
					NodeList inventoryDemandsNL = curInventoryTotalElm.getElementsByTagName("Demand");
					if (inventoryDemandsNL != null && inventoryDemandsNL.getLength() > 0) {
						Node curDemandNode = null;
						for (int k = 0; k < inventoryDemandsNL.getLength(); k++) {
							curDemandNode = inventoryDemandsNL.item(k);
							Element curDemandElm = (curDemandNode instanceof Element) ? (Element) curDemandNode : null;
							if (curDemandElm == null) continue;
		
							String strDemand = curDemandElm.getAttribute(NWCGConstants.QUANTITY);
							double dblDemand = Double.parseDouble(strDemand);
							TotalDemand = TotalDemand + dblDemand;
						}
					}
				}				
			}
			TotalAvailRFI = (TotalLocationInvQty - TotalDemand);

			NodeList extnNL = curOrderLine
					.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
			Node curExtnNode = null;
			if (extnNL != null && extnNL.getLength() > 0) {
				curExtnNode = extnNL.item(0);
				Element curExtnElm = (curExtnNode instanceof Element) ? (Element) curExtnNode : null;
				if (curExtnElm == null) throw new YFSException("Output template of getOrderLineList is missing OrderLine/Extn");
				
				//Added by Gaurav for RFI update -Start
				String strCurrentRFIQty = curExtnElm.getAttribute(NWCGConstants.EXTN_RFI_QTY);
				double dbCurrentRFIQty = Double.valueOf(strCurrentRFIQty);
				if((dbCurrentRFIQty - TotalAvailRFI) != 0)
				{
				
					curExtnElm.setAttribute(NWCGConstants.EXTN_RFI_QTY, Double.toString(TotalAvailRFI));
					Element eleOrderLine = docChangeOrderDoc.createElement(NWCGConstants.ORDER_LINE);
					eleOrderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, strOrderLineKey);
					Element eleExtn = docChangeOrderDoc.createElement(NWCGConstants.EXTN_ELEMENT);
					eleExtn.setAttribute(NWCGConstants.EXTN_RFI_QTY, Double.toString(TotalAvailRFI));
					eleOrderLine.appendChild(eleExtn);
					eleOrderLineList.appendChild(eleOrderLine);
					
				}
				//Added by Gaurav for RFI update -End
			}
			//Reset our inventory quantities per line.
			TotalLocationInvQty = 0.00;
			TotalDemand = 0.00;
			TotalAvailRFI = 0.00;
		}// for this order line item
	}

	private Document getNodeInventoryInputDoc(String shipNode,
			String strInventoryStatus, String strItemID) {
		String getNodeInventoryInputXmlString = "<NodeInventory Node=\""
				+ shipNode + "\"><Inventory InventoryStatus=\""
				+ strInventoryStatus + "\"><InventoryItem ItemID=\""
				+ strItemID + "\"/></Inventory></NodeInventory>";
		Document getNodeInventoryDoc = null;
		try {
			getNodeInventoryDoc = XMLUtil
					.getDocument(getNodeInventoryInputXmlString);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getNodeInventoryDoc;
	}

	private Document getATPInputDoc(String shipNode, String strItemID,
			String strPC, String strUOM, String strOrganizationCode) {
		String getATPXmlString = "<GetATP  ShipNode=\"" + shipNode
				+ "\" ItemID=\"" + strItemID + "\" ProductClass=\"" + strPC
				+ "\" UnitOfMeasure=\"" + strUOM + "\" OrganizationCode=\""
				+ strOrganizationCode + "\" />";
		Document getATPDoc = null;
		try {
			getATPDoc = XMLUtil.getDocument(getATPXmlString);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getATPDoc;
	}

	private Element getItemElementForOrderLine(Element curOrderLine) throws Exception {
		NodeList nl = curOrderLine.getElementsByTagName(NWCGConstants.ITEM);
		
		if (nl == null || nl.getLength() != 1)
			throw new YFSException("Unable to find Item element underneath OrderLine");

		return (Element) nl.item(0);
	}

	private Document getOrderLineList(Document inDoc) throws Exception {
		String opTemplate = "<OrderLineList><OrderLine AwaitingDeliveryRequest=\"\" ConditionVariable1=\"\" ConditionVariable2=\"\" "
				+ "CanAddServiceLines=\"\" ChainedFromOrderHeaderKey=\"\" ChainedFromOrderLineKey=\"\" CurrentWorkOrderKey=\"\" "
				+ "DependentOnLineKey=\"\" DerivedFromOrderHeaderKey=\"\" DerivedFromOrderLineKey=\"\" GiftFlag=\"\" HasChainedLines=\"\" "
				+ "HasDeliveryLines=\"\" HasDerivedChild=\"\" HasServiceLines=\"\" ItemGroupCode=\"\" KitCode=\"\" MaxLineStatusDesc=\"\" MultipleStatusesExist=\"\"	"
				+ "OrderLineKey=\"\" OrderHeaderKey=\"\" ParentOfDependentGroup=\"\" PrimeLineNo=\"\" ReceivingNode=\"\" ReqDeliveryDate=\"\" ReqShipDate=\"\" ShipNode=\"\" Status=\"\" "
				+ "SubLineNo=\"\" MaxLineStatus=\"\"><Item ItemDesc=\"\" ItemID=\"\" ItemShortDesc=\"\" ProductClass=\"\" UnitOfMeasure=\"\"/>"
				+ "<OrderLineTranQuantity OpenQty=\"\"  OriginalOrderedQty=\"\" OrderedQty=\"\" ReceivedQty=\"\" TransactionalUOM=\"\"/>" 
				+ "<Order OrderHeaderKey=\"\" OrderNo=\"\" MaxOrderStatus=\"\" DocumentType=\"\" Status=\"\"/><LineOverallTotals LineTotal=\"\"/><Instructions "
				+ "NumberOfInstructions=\"\"><Instruction InstructionType=\"\" InstructionText=\"\"/></Instructions><KitLines><KitLine ItemID=\"\"/></KitLines>"
				+ "<AllowedModifications/><Extn/><Notes><Note/></Notes></OrderLine></OrderLineList>";
		Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
		Document orderLineListOutput = null;
		try {
			orderLineListOutput = CommonUtilities.invokeService(myEnvironment, NWCGConstants.NWCG_GET_SORTED_ORDER_LINE_LIST_SERVICE, inDoc);
		} catch (Exception e) {
			logger.error(NWCGConstants.NWCG_GET_SORTED_ORDER_LINE_LIST_SERVICE, e);
			e.printStackTrace();
		}
		logger.verbose("getOrderLineList returning:" +XMLUtil.extractStringFromDocument(orderLineListOutput));
		return orderLineListOutput;
	}

	public void setProperties(Properties arg0) throws Exception {
		sdfApiArgs = arg0;
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGGetOrderLineListWithRFI API Properties: "
					+ sdfApiArgs.toString());
	}

	private YFSEnvironment myEnvironment = null;

	private Properties sdfApiArgs = null;

	private static Logger logger = Logger.getLogger(com.nwcg.icbs.yantra.api.issue.NWCGGetOrderLineListWithRFI.class.getName());
	
	//Added by Gaurav for RFI update -start
	private void updateOrderWithNewRFIQty() throws Exception
	{
		NodeList nlOrderLines = eleOrderLineList.getElementsByTagName(NWCGConstants.ORDER_LINE);
		if(nlOrderLines != null && nlOrderLines.getLength() > 0)
		{
			CommonUtilities.invokeAPI(myEnvironment,"changeOrder", docChangeOrderDoc);
		}
	}
	//Added by Gaurav for RFI update -End
	
	
}