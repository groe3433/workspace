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

package com.nwcg.icbs.yantra.api.otherorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.math.BigDecimal;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

public class NWCGPayGovAutoCharge implements YIFCustomApi 
{
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPayGovAutoCharge.class);
	
	private Properties myProperties = null;
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document checkShippingCost(YFSEnvironment env,Document inXML) throws Exception 
	{	
		logger.verbose("********************************************");
		logger.verbose("            Checking Shipping Cost          ");
		logger.verbose("********************************************");
		logger.verbose("INPUT XML:: "+ XMLUtil.getXMLString(inXML));
		
		/*
		 * <?xml version="1.0" encoding="UTF-8"?>
			<ShipmentList>
				<Shipment TotalActualCharge="" ShipmentKey="">
					<ShipmentLines TotalNumberOfRecords="">
						<ShipmentLine OrderNo="" ShipmentLineNo=""/>
					</ShipmentLines>
				</Shipment>
			</ShipmentList>
		*/
				
		Document docChangeOrderStatus = null;
		
		if(inXML != null)
		{
			
			String sShippingCost = "";
			
			Element RootElement = inXML.getDocumentElement();
			
			NodeList nlShipments = RootElement.getElementsByTagName( NWCGConstants.DOCUMENT_NODE_SHIPMENT );
			
			if (nlShipments != null && nlShipments.getLength() > 0 ){
				
				Element elemShipment = (Element) nlShipments.item(0);
				sShippingCost = elemShipment.getAttribute( NWCGConstants.TOTAL_ACTUAL_CHARGE_ATTR );
			}
			
			if(	!sShippingCost.equals("") && sShippingCost != null ){
				
				float fShippingCost = Float.parseFloat(sShippingCost);  
				
				if(fShippingCost > 0)
				{
					YFSException ne = new YFSException();
					ne.setErrorCode("Order Status Change Failed");
					ne.setErrorDescription("Cannot mark order as Charged, contains a shipping cost greater than zero");
					throw ne;
				}
				else
				{
					docChangeOrderStatus = buildInputXMLChangeOrderStatus(inXML);
				}
			}

			
		}
			
		return docChangeOrderStatus;
	}
	
	public Document buildInputXMLChangeOrderStatus(Document inXML) throws Exception {
		
		String sOrderNo = "";
		
		logger.verbose("********************************************");
		logger.verbose("             changeOrderStatus              ");
		logger.verbose("********************************************");
		
		Element rootElement = inXML.getDocumentElement();
		
		NodeList nlShipmentLines = rootElement.getElementsByTagName( NWCGConstants.SHIPMENT_LINE_ELEMENT );
		
		if (nlShipmentLines != null && nlShipmentLines.getLength() > 0 ){
			
			Element elemShipmentLine = (Element) nlShipmentLines.item(0);
			sOrderNo = elemShipmentLine.getAttribute( NWCGConstants.ORDER_NO );
		}
		

		Document OrderStatusChangeInputDoc = XMLUtil.newDocument();
		Element el_OrderStatusChange = OrderStatusChangeInputDoc.createElement( NWCGConstants.ORDER_STATUS_CHANGE_ELEMENT );

		OrderStatusChangeInputDoc.appendChild(el_OrderStatusChange);
		el_OrderStatusChange.setAttribute( NWCGConstants.ENTERPRISE_CODE_STR , NWCGConstants.NWCG_ENTERPRISE_CODE);
		el_OrderStatusChange.setAttribute( NWCGConstants.ORDER_NO , sOrderNo);
		el_OrderStatusChange.setAttribute( NWCGConstants.TRANSACTION_ID_ATTR , NWCGConstants.NWCG_CHANGE_PAY_STATUS);
		el_OrderStatusChange.setAttribute( NWCGConstants.DOCUMENT_TYPE , NWCGConstants.DOCUMENT_TYPE_OTHERISSUE);
		el_OrderStatusChange.setAttribute( NWCGConstants.BASE_STATUS_CHANGE_ATTR , NWCGConstants.NWCG_SHIPPED_CHARGED_STATUS);
		
		/*
		<OrderStatusChange TransactionId="CHANGE_PAY_STATUS.0007.ex.ex" OrderNo="0000695418" 
		 EnterpriseCode="NWCG" DocumentType="0007.ex" BaseDropStatus="3700.200">
		</OrderStatusChange>
		*/
		
		logger.verbose("changeOrderStatus API Input XML:: "+ XMLUtil.getXMLString(OrderStatusChangeInputDoc));
		
		return OrderStatusChangeInputDoc;
		
	}
	
	
}