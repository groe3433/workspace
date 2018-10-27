package com.nwcg.icbs.yantra.wms.barcodetranslate;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author sgunda
 *
 * Goal of this class is to adjust the location inventory if the serial scan is for
 * refurbishment of component.
 * Note : Used debug while printing the logs instead of verbose. In all the earlier
 * bar code translators, we have debug instead of verbose, so following that pattern.
 * Also, this logs would be printed in Java console rather than in the logs, so that
 * might need to do something with debug rather than verbose.
 */

public class NWCGBarCodeTranslator implements YIFCustomApi {
	private Properties _properties;

	private static Logger log = Logger.getLogger();
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}
	
	
	/**
	 * This method gets the details regarding the item and adjusts the location inventory if we are dealing
	 * with refurbing an item. Also, it returns the data in the translateBarCode output format.
	 * Item is being concluded as refurb if the status of the item is NRFI-RFB.
	 * Input XML Format :
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <BarCode BarCodeData="812" BarCodeType="SerialScan" IgnoreOrdering="Y">
	 * 		<ContextualInfo EnterpriseCode="NWCG" OrganizationCode="RMK"/>
	 * 		<ShipmentContextualInfo ShipNode="RMK" ShipmentKey="" ShipmentNo=""/>
	 * 		<ItemContextualInfo InventoryUOM="EA" ItemID="Comp-1"/>
	 *    <LocationContextualInfo LocationId="RADIO-01" StationId=""/>
	 * </BarCode>
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document translateBarCode(YFSEnvironment env, Document inputDoc) 
	throws Exception {
		log.debug("NWCGBarCodeTranslator::translateBarCode, Entered");
		if (log.isVerboseEnabled()){
			log.verbose("NWCGBarCodeTranslator::translateBarCode, Input XML : " + XMLUtil.getXMLString(inputDoc));
		}

		Element rootElm = inputDoc.getDocumentElement();
		NamedNodeMap itemAttrs = rootElm.getElementsByTagName("ItemContextualInfo").item(0).getAttributes();
		String itemId = itemAttrs.getNamedItem("ItemID").getNodeValue();
		String itemUom = itemAttrs.getNamedItem("InventoryUOM").getNodeValue();
		log.debug("NWCGBarCodeTranslator::translateBarCode, Unit of Measure : " + itemUom);
		String prodClass = "Supply";
		String serialNo = rootElm.getAttribute("BarCodeData");
		String barCodeType = rootElm.getAttribute("BarCodeType");
		String locId = rootElm.getElementsByTagName("LocationContextualInfo").item(0).getAttributes().getNamedItem("LocationId").getNodeValue();
		NamedNodeMap contextualAttrs = rootElm.getElementsByTagName("ContextualInfo").item(0).getAttributes();
		String enterpriseCode = contextualAttrs.getNamedItem("EnterpriseCode").getNodeValue();
		String nodeKey = contextualAttrs.getNamedItem("OrganizationCode").getNodeValue();
		log.debug("NWCGBarCodeTranslator::translateBarCode, Item ID : " + itemId + ", Product Class : " + prodClass); 
		log.debug("NWCGBarCodeTranslator::translateBarCode, Loc ID : " + locId + ", Enterprise Code : " + enterpriseCode);
		
		Document serialListOPDoc = getSerialItemDetails(env, serialNo, itemId, prodClass, itemUom);
		/*
		 * Out XML format is
		 * <?xml version="1.0" encoding="UTF-8" ?> 
		 * <SerialList>
		 *   <Serial AtNode="Y" FifoNo="0" GlobalSerialKey="20060815150715461906" InventoryItemKey="20060806141213431639" InventoryStatus="NRFI-RFB" 
		 *   LocationId="RADIO-01" PalletId="" ParentSerialKey="" ReceiptHeaderKey="" SecondarySerial1="0" SecondarySerial2="0" SecondarySerial3="0" 
		 *   SecondarySerial4="0" SecondarySerial5="0" SecondarySerial6="0" SecondarySerial7="0" SecondarySerial8="0" SecondarySerial9="0" 
		 *   Segment="" SegmentType="" SerialNo="812" ShipByDate="2500-01-01" ShipNode="RMK" TagNumber="" /> 
		 * </SerialList>			 
		 */
		Element serialRootNode = serialListOPDoc.getDocumentElement();
		NodeList serialList = serialRootNode.getElementsByTagName("Serial");
		String invStatus = "";
		String shipByDate = "";
		if (serialList != null && serialList.getLength() > 0){
			NamedNodeMap serialAttrs = serialList.item(0).getAttributes();
			invStatus = serialAttrs.getNamedItem("InventoryStatus").getNodeValue();
			shipByDate = serialAttrs.getNamedItem("ShipByDate").getNodeValue();
		}
		
		log.debug("NWCGBarCodeTranslator::translateBarCode, Inventory Status : " + invStatus);
		
		if ((itemUom.equalsIgnoreCase("EA") || itemUom.equalsIgnoreCase("EACH")) &&
				(invStatus.equalsIgnoreCase(NWCGConstants.NRFI_RFB_STATUS))){
			log.debug("NWCGBarCodeTranslator::translateBarCode, calling adjust location inv. method");
			Document adjLocnDoc = getAdjustLocnInvXml(enterpriseCode, nodeKey, locId, itemId, itemUom, serialNo, prodClass, invStatus);
			CommonUtilities.invokeAPI(env, NWCGConstants.API_ADJUST_LOCN_INV, adjLocnDoc);
			log.debug("NWCGBarCodeTranslator::translateBarCode, returning from adjustLocationInventory API");
		}
		
		// Build the output. This output should be the format of the translateBarCode output.
		Document barCodeOP = buildTranslateBarCodeOutput(
															serialNo, barCodeType, enterpriseCode, nodeKey, itemUom, itemId, 
															prodClass, invStatus, locId, shipByDate);

		log.debug("NWCGBarCodeTranslator::translateBarCode, Returning");
		return barCodeOP;
	}
	
	
	/**
	 * This method gets the node key based on the serial number. This node key is used
	 * in the adjustLocationInventory API call.
	 * @param serialNo
	 * @param itemId
	 * @param prodClass
	 * @param itemUom
	 * @return
	 */
	private Document getSerialItemDetails(YFSEnvironment env, String serialNo, String itemId, String prodClass, String itemUom)
	throws Exception {
		log.debug("NWCGBarCodeTranslator::getSerialItemStatus, Entered");
		/* Input XML required by getSerialList
		 * <Serial SerialNo="10099" >  
		 *   <InventoryItem ItemID="RTEST" ProductClass="Supply" UnitOfMeasure="KT" /> 
		 * </Serial>
		 */
		Document inputSerialListDoc = XMLUtil.getDocument();
		Element serialElm = inputSerialListDoc.createElement("Serial");
		serialElm.setAttribute("SerialNo", serialNo);
		
		Element invItemElm = inputSerialListDoc.createElement("InventoryItem");
		invItemElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		invItemElm.setAttribute(NWCGConstants.PRODUCT_CLASS, prodClass);
		invItemElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, itemUom);
		serialElm.appendChild(invItemElm);
		inputSerialListDoc.appendChild(serialElm);
		
		if (log.isVerboseEnabled()){
			log.verbose("NWCGBarCodeTranslator::getSerialItemStatus, Input XML for getSerialList : " + XMLUtil.getXMLString(inputSerialListDoc));
		}
		Document serialListOPDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_SERIAL_LIST, inputSerialListDoc);
		
		if (serialListOPDoc != null){
			/*
			 * Out XML format is
			 * <?xml version="1.0" encoding="UTF-8" ?> 
			 * <SerialList>
			 *   <Serial AtNode="Y" FifoNo="0" GlobalSerialKey="20060815150715461906" InventoryItemKey="20060806141213431639" InventoryStatus="NRFI-RFB" 
			 *   LocationId="RADIO-01" PalletId="" ParentSerialKey="" ReceiptHeaderKey="" SecondarySerial1="0" SecondarySerial2="0" SecondarySerial3="0" 
			 *   SecondarySerial4="0" SecondarySerial5="0" SecondarySerial6="0" SecondarySerial7="0" SecondarySerial8="0" SecondarySerial9="0" 
			 *   Segment="" SegmentType="" SerialNo="812" ShipByDate="2500-01-01" ShipNode="RMK" TagNumber="" /> 
			 * </SerialList>			 
			 */
			if (log.isVerboseEnabled()){
				log.verbose("NWCGBarCodeTranslator::getSerialItemStatus, Output XML of getSerialList : " + XMLUtil.getXMLString(serialListOPDoc));
			}
			log.debug("NWCGBarCodeTranslator::getSerialItemStatus, Output returned properly, returning");
		}
		else {
			log.debug("NWCGBarCodeTranslator::getSerialItemStatus, Output returned NULL object");
		}

		return serialListOPDoc;
	}
	
	
	/**
	 * This method builds the xml for adjustLocationInventory
	 * 
	 * Input XML :
	 * 
	 * @param enterpriseCode
	 * @param nodeKey
	 * @param locId
	 * @param itemUom
	 * @param itemId
	 * @param prodClass
	 * @return
	 * @throws Exception
	 */
	private Document getAdjustLocnInvXml(String enterpriseCode, String nodeKey, String locId, 
																			 String itemId, String itemUom, String serialNo,
																			 String prodClass, String invStatus)
	throws Exception {
		log.debug("NWCGBarCodeTranslator::getAdjustLocnInvXml, Entered");
		/*
		 * <?xml version="1.0" encoding="UTF-8" ?> 
		 * <AdjustLocationInventory EnterpriseCode="Required" Node="Required">
		 * 	<Source LocationId="">
		 * 		<Inventory InventoryStatus="">
		 * 			<InventoryItem ItemID="" ProductClass="" UnitOfMeasure="" /> 
		 * 			<SerialList>
		 * 				<SerialDetail Quantity="" SerialNo="" /> 
		 * 			</SerialList>
		 * 		</Inventory>
		 * 	</Source>
		 * 	<Audit DocumentType="" ReasonCode=""/> 
		 * </AdjustLocationInventory>
		 */
		Document inputDoc = XMLUtil.getDocument();
		
		Element adjLocnInvElm = inputDoc.createElement("AdjustLocationInventory");		
		adjLocnInvElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, enterpriseCode);
		adjLocnInvElm.setAttribute(NWCGConstants.NODE, nodeKey);
		
		inputDoc.appendChild(adjLocnInvElm);
		
		Element sourceElm = inputDoc.createElement("Source");
		sourceElm.setAttribute(NWCGConstants.LOCATION_ID, locId);
		
		Element inventoryElm = inputDoc.createElement("Inventory");
		inventoryElm.setAttribute(NWCGConstants.INVENTORY_STATUS, invStatus);
		
		Element invItemElm = inputDoc.createElement("InventoryItem");
		invItemElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		invItemElm.setAttribute(NWCGConstants.PRODUCT_CLASS, prodClass);
		invItemElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, itemUom);
		
		Element serialListElm = inputDoc.createElement("SerialList");
		Element serialDtlElm = inputDoc.createElement("SerialDetail");
		serialDtlElm.setAttribute(NWCGConstants.QUANTITY, "-1");
		serialDtlElm.setAttribute(NWCGConstants.SERIAL_NO, serialNo);
		
		serialListElm.appendChild(serialDtlElm);
		inventoryElm.appendChild(serialListElm);
		inventoryElm.appendChild(invItemElm);
		sourceElm.appendChild(inventoryElm);
		adjLocnInvElm.appendChild(sourceElm);
		
		Element auditElm = inputDoc.createElement("Audit");
		auditElm.setAttribute(NWCGConstants.DOCUMENT_TYPE, "7001");
		auditElm.setAttribute(NWCGConstants.REASON_CODE, NWCGConstants.RFB_ADJUSTMENT_REASON_CODE);
		adjLocnInvElm.appendChild(auditElm);
		
		if (log.isVerboseEnabled()){
			log.verbose("NWCGBarCodeTranslator::getAdjustLocnInvXml, Input XML : " + XMLUtil.getXMLString(inputDoc));
		}
		
		log.debug("NWCGBarCodeTranslator::getAdjustLocnInvXml, Output returned properly, returning");
		return inputDoc;
	}
	
	
	/**
	 * This method builds the translate barCode output. Whenever we extend the bar code, we need
	 * to return the output in translateBarCode output format. So, this is the method.
	 * @param serialNo
	 * @param barCodeType
	 * @param enterpriseCode
	 * @param orgCode
	 * @param itemUom
	 * @param itemId
	 * @param prodClass
	 * @param invStatus
	 * @param actLocId
	 * @param shipByDate
	 * @return
	 * @throws Exception
	 */
	private Document buildTranslateBarCodeOutput(String serialNo, String barCodeType, String enterpriseCode, String orgCode,
																							 String itemUom, String itemId, String prodClass, String invStatus, 
																							 String actLocId, String shipByDate)
	throws Exception{
		log.debug("NWCGBarCodeTranslator::buildTranslateBarCodeOutput, Entered");
		Document transBarCodeOP = XMLUtil.getDocument();
		/*
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <BarCode BarCodeData="812" BarCodeType="SerialScan">
		 * 		<Translations BarCodeTranslationSource="SerialTranslator" TotalNumberOfRecords="1">
		 *			<Translation>
		 *				<ContextualInfo EnterpriseCode="NWCG" InventoryOrganizationCode="NWCG" OrganizationCode="NWCG"/>
		 *        <ItemContextualInfo InventoryUOM="EA" ItemID="Comp-1" ProductClass="Supply" Quantity="1">
		 *        		<Inventory CountryOfOrigin="" FifoNo="0" InventoryStatus="NRFI-RFB" Segment="" SegmentType="" ShipByDate="2500-01-01">
		 *        			<SerialDetail SecondarySerial1="0" SecondarySerial2="0" SecondarySerial3="0" SecondarySerial4="0" SecondarySerial5="0"
		 *        					SecondarySerial6="0" SecondarySerial7="0" SecondarySerial8="0" SecondarySerial9="0" SerialNo="812"/>
		 *        		</Inventory>
		 *        </ItemContextualInfo>
		 *        <LocationContextualInfo LocationId="RADIO-01"/>
		 *			</Translation>
		 *		</Translations>
		 * </BarCode>
		 */
		Element barCodeElm = transBarCodeOP.createElement("BarCode");
		barCodeElm.setAttribute(NWCGConstants.BAR_CODE_DATA, serialNo);
		barCodeElm.setAttribute(NWCGConstants.BAR_CODE_TYPE, barCodeType);
		
		transBarCodeOP.appendChild(barCodeElm);
		
		Element translationsElm = transBarCodeOP.createElement("Translations");
		translationsElm.setAttribute(NWCGConstants.BAR_CODE_TRANSLATION_SOURCE, "SerialTranslator");
		translationsElm.setAttribute(NWCGConstants.TOTAL_NUMBER_OF_RECORDS, "1");		
		barCodeElm.appendChild(translationsElm);
		
		Element transElm = transBarCodeOP.createElement("Translation");
		translationsElm.appendChild(transElm);
		
		Element contextElm = transBarCodeOP.createElement("ContextualInfo");
		contextElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, enterpriseCode);
		contextElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		transElm.appendChild(contextElm);
		
		Element itemContextInfoElm = transBarCodeOP.createElement("ItemContextualInfo");
		itemContextInfoElm.setAttribute(NWCGConstants.INVENTORY_UOM, itemUom);
		itemContextInfoElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		itemContextInfoElm.setAttribute(NWCGConstants.PRODUCT_CLASS, prodClass);
		itemContextInfoElm.setAttribute(NWCGConstants.QUANTITY, "1");
		transElm.appendChild(itemContextInfoElm);
		
		Element invElm = transBarCodeOP.createElement("Inventory");
		if (invStatus.length() > 1){
			invElm.setAttribute(NWCGConstants.INVENTORY_STATUS, invStatus);
		}
		if (shipByDate.length() > 1){
			invElm.setAttribute(NWCGConstants.SHIP_BY_DATE, shipByDate);
		}
		itemContextInfoElm.appendChild(invElm);
		
		Element serialDtlElm = transBarCodeOP.createElement("SerialDetail");
		serialDtlElm.setAttribute(NWCGConstants.SERIAL_NO, serialNo);
		invElm.appendChild(serialDtlElm);
		
		Element locContextualInfoElm = transBarCodeOP.createElement("LocationContextualInfo");
		locContextualInfoElm.setAttribute(NWCGConstants.LOCATION_ID, actLocId);
		transElm.appendChild(locContextualInfoElm);

		if (log.isVerboseEnabled()){
			log.verbose("NWCGBarCodeTranslator::buildTranslateBarCodeOutput, XML : " + XMLUtil.getXMLString(transBarCodeOP));
		}
		log.debug("NWCGBarCodeTranslator::buildTranslateBarCodeOutput, Entered");
		return transBarCodeOP;
	}	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
