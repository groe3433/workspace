package com.nwcg.icbs.yantra.api.issue;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGChkShpmtQtyToOLQty implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGChkShpmtQtyToOLQty.class.getName());
	private String orderHdrKey = "";
	private Hashtable<String, String> htOLKey2ShpmtQty = new Hashtable<String, String>();

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public Document chkShpmtQtyToOLQty(YFSEnvironment env, Document inDoc) throws Exception {
		logger.info("NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty, Entered");
		logger.info("NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty, " +
					"Input XML : " + XMLUtil.extractStringFromDocument(inDoc));
		System.out.println("NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty, " +
						   "Input XML : " + XMLUtil.extractStringFromDocument(inDoc));
		populateOLKey2ShpmtQty(inDoc);
		
		if ((htOLKey2ShpmtQty == null) || (htOLKey2ShpmtQty.size() < 1)){
			logger.info("NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty, " +
					"Not updating OL with shipment quantity as there are no shipment lines");
			return inDoc;
		}
		
		logger.info("No of shipment lines in hashtable : " + htOLKey2ShpmtQty.size());
		Document docOrderDtls = getOrderDetails(env);
		
		verifyAndUpdtOLQuantities(env, docOrderDtls);
		return inDoc;
	}

	/**
	 * This method will populate the hashtable OLKey to shipments actual quantity
	 * @param inDoc
	 */
	private void populateOLKey2ShpmtQty(Document inDoc){
		Element elmConfShpmtDoc = inDoc.getDocumentElement();
		NodeList nlShpmtLine = elmConfShpmtDoc.getElementsByTagName(NWCGConstants.SHIPMENT_LINE_ELEMENT);
		if (nlShpmtLine != null && nlShpmtLine.getLength() > 0){
			System.out.println("NWCGChkShpmtQtyToOLQty::populateOLKey2ShpmtQty, No of shipment lines : " + nlShpmtLine.getLength());
			for (int i=0; i < nlShpmtLine.getLength(); i++){
				Element elmShpmtLine = (Element) nlShpmtLine.item(i);
				htOLKey2ShpmtQty.put(elmShpmtLine.getAttribute(NWCGConstants.ORDER_LINE_KEY),
									 elmShpmtLine.getAttribute(NWCGConstants.ACTUAL_QUANTITY));
				orderHdrKey = elmShpmtLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			}
		}
		else {
			logger.error("NWCGChkShpmtQtyToOLQty::populateOLKey2ShpmtQty, There are no shipment lines for this shipment");
		}
	}
	
	/**
	 * This method will get the order details for a given template
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Document getOrderDetails(YFSEnvironment env) throws Exception{
		Document docOrderDtlsOP = null;
		try {
			Document docOrderDtlsIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHdrKey + "\"/>");
			docOrderDtlsOP = CommonUtilities.invokeAPI(env, "NWCGShipmentQty2OLQty_getOrderDetails", 
													   NWCGConstants.API_GET_ORDER_DETAILS, docOrderDtlsIP);
		}
		catch(ParserConfigurationException pce){
			logger.error("NWCGChkShpmtQtyToOLQty::getOrderDetails, " +
					"ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
			throw pce;
		} catch (SAXException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::getOrderDetails, " +
					"SAXException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::getOrderDetails, " +
					"IOException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			logger.error("NWCGChkShpmtQtyToOLQty::getOrderDetails, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return docOrderDtlsOP;
	}

	/**
	 * This method will get the order line quantities for each line and will check with
	 * shipments actual quantity. If there is no difference, then it will not do anything.
	 * If it is different, then code will update the OrderedQty with shipments actual quantity
	 * and will add the difference of (OrderedQty - Shipments Actual Quantity) to existing
	 * UTF Quantity
	 * @param env
	 * @param docOrderDtls
	 */
	private void verifyAndUpdtOLQuantities(YFSEnvironment env, Document docOrderDtls) throws Exception{
		boolean updtOLs = false;
		try {
			Document docChgOrderIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHdrKey + "\" Action=\"MODIFY\"/>");
			Element elmChgOrderIP = docChgOrderIP.getDocumentElement();
			Element elmChgOLs = docChgOrderIP.createElement(NWCGConstants.ORDER_LINES);
			elmChgOrderIP.appendChild(elmChgOLs);
			
			Element elmOrderDtls = docOrderDtls.getDocumentElement();
			NodeList nlOL = elmOrderDtls.getElementsByTagName(NWCGConstants.ORDER_LINE);
			if (nlOL != null && nlOL.getLength() > 0){
				logger.info("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, No of order lines : " + nlOL.getLength());
				for (int i=0; i < nlOL.getLength(); i++){
					Element elmOL = (Element) nlOL.item(i);
					String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					String orderedQty = elmOL.getAttribute(NWCGConstants.ORDERED_QTY);
					String actualQtyFromShpmtLine = htOLKey2ShpmtQty.get(olKey);
					if (actualQtyFromShpmtLine == null || actualQtyFromShpmtLine.trim().length() < 1){
						continue;
					}
					
					float qtyFromOL = (new Float(orderedQty)).floatValue();
					float qtyFromSL = (new Float(actualQtyFromShpmtLine)).floatValue();
					if (qtyFromOL > qtyFromSL){
						System.out.println("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
								"OL quantity " + orderedQty + " is greater than Shipment Quantity " + 
								actualQtyFromShpmtLine);
						updtOLs = true;
						Element elmChgOL = docChgOrderIP.createElement(NWCGConstants.ORDER_LINE);
						elmChgOLs.appendChild(elmChgOL);
						elmChgOL.setAttribute(NWCGConstants.ORDER_LINE_KEY, olKey);
						elmChgOL.setAttribute(NWCGConstants.ACTION, "MODIFY");
						elmChgOL.setAttribute(NWCGConstants.ORDERED_QTY, actualQtyFromShpmtLine);
						
						String oldUTFQty = ((Element)elmOL.getElementsByTagName(NWCGConstants.EXTN).item(0))
														.getAttribute(NWCGConstants.EXTN_UTF_QTY);
						float utfQty = new Float(oldUTFQty).floatValue();
						String newUTFQty = new Float(utfQty + (qtyFromOL - qtyFromSL)).toString();
						Element elmChgExtnOL = docChgOrderIP.createElement(NWCGConstants.EXTN);
						elmChgOL.appendChild(elmChgExtnOL);
						elmChgExtnOL.setAttribute(NWCGConstants.EXTN_UTF_QTY, newUTFQty);
					}
					else {
						logger.verbose("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, Quantities are same");
					}
				}
			}
			else {
				logger.info("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
						"There are no order lines on this order");
			}
			
			if (updtOLs){
				logger.info("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, Updating order : " + 
						XMLUtil.extractStringFromDocument(docChgOrderIP));
				CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, docChgOrderIP);
			}
			else {
				logger.info("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, OL and Shipment Quantities are in sync");
			}
		} catch (ParserConfigurationException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
					"ParserConfigurationException : " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
					"SAXException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
					"IOException : " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			logger.error("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
					"TransformerException : " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
