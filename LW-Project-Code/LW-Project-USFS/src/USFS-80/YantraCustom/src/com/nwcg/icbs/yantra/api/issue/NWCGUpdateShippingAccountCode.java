package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class updates the shipping account code field 
 * on the shipment details. 
 * 
 * Related to CR 870
 * Issue: When an issue is in a status of 'shipped' and the shipping account code is edited it
 * is not updating the shipping account code field on the shipment details.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
 *  
 * @author Oxford Consulting Group
 * @since March 22, 2013 
 * @version
 */
public class NWCGUpdateShippingAccountCode implements YIFCustomApi {

	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger(NWCGUpdateShippingAccountCode.class.getName());
	
	public void setProperties(Properties props) throws Exception {
		this.myProperties = props;	
		if (logger.isVerboseEnabled()) { logger.verbose("SDF API Properties set"+this.myProperties); }
	}//setProperties

	/*
	 * This method checked the changeOrder Document and if in a status of 'shipped'
	 * updates the shipping account code from the changeOrder inputDoc.
	 */
	public Document updateShipmentAccountCode (YFSEnvironment env, Document inputDoc) throws Exception 
	{
		boolean updateShippment = false;
		Document outDoc = null;
		String sNewValue = "";
		
		if(logger.isVerboseEnabled()){ logger.verbose("Entering the updateShipmentAccountCode method , input document is:" + XMLUtil.getXMLString(inputDoc));}
			
		NodeList attributesList = inputDoc.getElementsByTagName(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE);
		
		if(attributesList != null)
		{
			//loop through Attribute list
			for(int i=0; i<attributesList.getLength(); i++)
			{
				Element attribute =(Element) attributesList.item(i);  
				String Name = (String)(attribute.getAttribute(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE_NAME));
				
				if(Name != null)
				{
					if(Name.equals(NWCGConstants.EXTN_SHIP_ACCT_CODE))
					{
						sNewValue = (String)(attribute.getAttribute(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE_NEWVALUE));
						updateShippment = true;
					}
				}
			}
		}

		if(updateShippment && sNewValue != null)
		{
			Element order = inputDoc.getDocumentElement();
			//Retrieve orderheaderkey from changeOrder Document to call getShipmentList
			String sOrderHeaderKey = order.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			
			// Get all the shipments 
			Document docGetShipmentList = getShipmentListFromOrder(env,sOrderHeaderKey);
			if(logger.isVerboseEnabled()) logger.verbose("updateShippedRecords :: docGetShipmentList "+XMLUtil.getXMLString(docGetShipmentList));
			
			NodeList nlShipment = docGetShipmentList.getElementsByTagName(NWCGConstants.SHIPMENT_ELEMENT);
			
			if( nlShipment.getLength() >= 1 && nlShipment != null )
			{
				if(logger.isVerboseEnabled()) logger.verbose("updateShippedRecords :: nlShipment " + nlShipment.getLength());
				
				//Note: Will only be one Node in the list
				Element elemShipmentTag = (Element) nlShipment.item(0);
				//Retrieve shipmentkey for changeShipment API call
				String sShipmentKey = elemShipmentTag.getAttribute(NWCGConstants.SHIPMENT_KEY);
				if(logger.isVerboseEnabled()) logger.verbose("updateShippedRecords :: strShipmentKey "+ sShipmentKey);

				if(sShipmentKey != null && (!sShipmentKey.equals("")))
				{
					// Create changeShipment document
					Document rt_Shipment = XMLUtil.newDocument();
					Element el_Shipment=rt_Shipment.createElement(NWCGConstants.SHIPMENT_ELEMENT);
					Element el_Extn=rt_Shipment.createElement(NWCGConstants.CUST_EXTN_ELEMENT);
					rt_Shipment.appendChild(el_Shipment);
						 
					el_Shipment.setAttribute( NWCGConstants.SHIPMENT_KEY ,sShipmentKey);
					el_Extn.setAttribute( NWCGConstants.EXTN_SHIP_ACCT_CODE , sNewValue);
					el_Shipment.appendChild(el_Extn);
						 
					// update the shipped record
					if(logger.isVerboseEnabled()) logger.verbose("The input xml for ChangeShipment is:-"+XMLUtil.getXMLString(rt_Shipment));
						 
			        outDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_SHIPMENT ,rt_Shipment);
			             
					if(logger.isVerboseEnabled()) logger.verbose("The output xml for ChangeShipment is:-"+XMLUtil.getXMLString(outDoc));
				}
			}
	
		}

		return inputDoc;
	}
	
	/**
	 * 
	 * Create shipment document and invoke getShipmentList API
	 * 
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws Exception
	 */
	private Document getShipmentListFromOrder(YFSEnvironment env, String strOrderHeaderKey) throws Exception 
	{
		Document rt_Shipment = XMLUtil.getDocument();

		Element el_Shipment=rt_Shipment.createElement(NWCGConstants.DOCUMENT_NODE_SHIPMENT);
		
		rt_Shipment.appendChild(el_Shipment);

		Element el_ShipmentLines=rt_Shipment.createElement(NWCGConstants.SHIPMENT_LINES_ELEMENT);
		el_Shipment.appendChild(el_ShipmentLines);

		Element el_ShipmentLine=rt_Shipment.createElement(NWCGConstants.SHIPMENT_LINE_ELEMENT);
		el_ShipmentLines.appendChild(el_ShipmentLine);
		el_ShipmentLine.setAttribute("OrderHeaderKey",strOrderHeaderKey);
		
		if(logger.isVerboseEnabled()) logger.verbose("getShipmentListFromOrder :: invoking getShipmentList with OrderHeaderKey "+ strOrderHeaderKey);

		return CommonUtilities.invokeAPI(env,"NWCGUpdateShippingAccountCode_getShipmentList","getShipmentList",rt_Shipment);
	}
	
}