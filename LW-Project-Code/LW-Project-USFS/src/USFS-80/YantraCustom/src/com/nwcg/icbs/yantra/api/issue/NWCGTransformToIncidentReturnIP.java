package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * This class formats the xml as required by the insertIncidentRecord API
 * Algo:
 * 1. Fetches all the shipment lines
 * 2. Extract the OrderLineKey
 * 3. Extract the UnitPrice from OrderLineKey and add attribute to Shipment Line
 * 4. Add new attributes IncidentNo (OrderNo) and CacheId to the ShipmentLine
 * 5. return back the XML document  
 */
public class NWCGTransformToIncidentReturnIP implements YIFCustomApi
{
/**
			  * Logger Instance.
			  */
			private static Logger log = Logger.getLogger(NWCGInsertIncidentRecord.class.getName());

			private Properties props = null;

			public void setProperties(Properties props) throws Exception {
				this.props = props;
			}//setProperties
			/*
			 * modifies the record
			 */
			public Document modifyRecords(YFSEnvironment env, Document inXML) throws Exception
		    {
				if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP Starts");
				// get the document element
				Element root = inXML.getDocumentElement();
				NodeList shipmentLineList = null ;
				String shipNode = "";
				if(root != null )
				{
					// get the shipment line
					shipmentLineList = root.getElementsByTagName("ShipmentLine");
					// get the ship node
					shipNode = root.getAttribute("ShipNode");
				}
				
				env.setApiTemplate("getOrderLineDetails","NWCGTransformToIncidentReturnIP_getOrderLineDetails");
				if(shipmentLineList != null)
				{
					if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP total Shipment Lines "+shipmentLineList.getLength());
					// navigate through all the lines
					for(int index = 0 ; index < shipmentLineList.getLength() ; index++)
					{
						Element shipmentLine = (Element) shipmentLineList.item(index);
						String strOrderLineKey = shipmentLine.getAttribute("OrderLineKey");
						String strUnitPrice = "";
						String strIncidentNo = "";
						String strIncidentYear = "";
						if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP got the order line key "+strOrderLineKey);
						// get the order line details
						if(strOrderLineKey != null && (!strOrderLineKey.equals("")))
						{
							// if no order line key we wont fetch the cost, rest of the attribute will remain the same
							Document olDetailDoc = XMLUtil.createDocument("OrderLineDetail");
							Element root_olDetail = null ;
							if(olDetailDoc != null)
							{
								root_olDetail = olDetailDoc.getDocumentElement();
								root_olDetail.setAttribute("OrderLineKey",strOrderLineKey);
							}
							
							// get the order line details output
							Document goldDoc = CommonUtilities.invokeAPI(env,"getOrderLineDetails",olDetailDoc);
							//System.out.println("ORDER LINE OUTPUT : "+ XMLUtil.getXMLString(goldDoc));
							
							if(goldDoc != null)
							{
								// extract the unit price and incident number
								strUnitPrice = XPathUtil.getString(goldDoc,"/OrderLine/LinePriceInfo/@UnitPrice");
								strIncidentNo = XPathUtil.getString(goldDoc,"/OrderLine/Order/Extn/@ExtnIncidentNo");
								strIncidentYear = XPathUtil.getString(goldDoc,"/OrderLine/Order/Extn/@ExtnIncidentYear");
								//System.out.println("Incident Year : "+ strIncidentYear);
								if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP getOrderLineDetails o/p "+XMLUtil.getXMLString(goldDoc));
								if(strUnitPrice == null )
									strUnitPrice = "";
							}
						}
						// set those attributes
						shipmentLine.setAttribute("UnitPrice",strUnitPrice);
						shipmentLine.setAttribute("CacheID",shipNode);
						shipmentLine.setAttribute("IncidentNo",strIncidentNo);
						shipmentLine.setAttribute("IncidentYear",strIncidentYear);
						
						if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP shipmentLine o/p "+shipmentLine);
						
					}
				}
				
				env.clearApiTemplate("getOrderLineDetails");
				
				if(log.isVerboseEnabled()) log.verbose("NWCGTransformToIncidentReturnIP Returning "+XMLUtil.getXMLString(inXML));
				return inXML ;
		    }
}
