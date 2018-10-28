package com.fanatics.sterling.stub;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;

public class FANGetShipCharge {
	

	public Document invokeGetShipChargeREST (Document inDoc) throws ParserConfigurationException, SAXException, IOException,Exception{
		

		Element inDocele = inDoc.getDocumentElement();
		String OHK = XPathUtil.getXpathAttribute(inDocele, "//Order/@OrderHeaderKey");
		String OrderNo = XPathUtil.getXpathAttribute(inDocele, "//Order/@OrderNo");
		String EarliestShipDate = XPathUtil.getXpathAttribute(inDocele, "//Order/OrderLines/OrderLine[@PrimeLineNo='1']/@EarliestShipDate");
		String OrderLineKey = XPathUtil.getXpathAttribute(inDocele, "//Order/OrderLines/OrderLine[@PrimeLineNo='1']/@OrderLineKey");
		String ItemID = XPathUtil.getXpathAttribute(inDocele, "//Order/OrderLines/OrderLine[@PrimeLineNo='1']/Item/@ItemID");


		
		String responseString ="<Order DocumentType=\"0001\" EnterpriseCode=\"FANATICS_WEB\" OrderHeaderKey=\""+OHK+"\" OrderNo=\""+OrderNo+"\">"
				+ "<PersonInfoShipTo/>"
				
				+ "<OrderLines>"
				+ "<OrderLine EarliestShipDate=\""+EarliestShipDate+"\" OrderLineKey=\""+OrderLineKey+"\" PrimeLineNo=\"1\" SubLineNo=\"1\">"
				+ "<PersonInfoShipTo/>"
				+ "<Item ItemID=\""+ItemID+"\" ProductClass=\"\" UnitOfMeasure=\"EACH\"/>"
				+ "<CarrierServiceList>"
				+ "<CarrierService CarrierServiceCode=\"GRND1\" CarrierServiceDesc=\"Ground\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-10\" DeliveryStartDate=\"2016-08-06\" Price=\"5.99\"/>"
				+ "<CarrierService CarrierServiceCode=\"GRND2\" CarrierServiceDesc=\"7 - 14 Business Days\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-17\" DeliveryStartDate=\"2016-08-10\" Price=\"3.99\"/>"
				+ "<CarrierService CarrierServiceCode=\"GRND4\" CarrierServiceDesc=\"Up to 4 Weeks\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-31\" DeliveryStartDate=\"2016-08-10\" Price=\"1.99\"/>"
				+ "<CarrierService CarrierServiceCode=\"3DAY\" CarrierServiceDesc=\"3 Business Days\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-06\" DeliveryStartDate=\"2016-08-06\" Price=\"15.99\"/>"
				+ "<CarrierService CarrierServiceCode=\"1DAY\" CarrierServiceDesc=\"Next Business Days by End of Day\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-04\" DeliveryStartDate=\"2016-08-04\" Price=\"18.99\"/>"
				+ "<CarrierService CarrierServiceCode=\"2DAY\" CarrierServiceDesc=\"2 Business Days\" CarrierType=\"PARCEL\" Currency=\"\" DeliveryEndDate=\"2016-08-05\" DeliveryStartDate=\"2016-08-05\" Price=\"25.99\"/>"
				+ "</CarrierServiceList>"
				+ "</OrderLine>"
				+ "</OrderLines>"
				+ "</Order>";
		Document responseXml = XMLUtil.getDocument(responseString);
		Element respEle = responseXml.getDocumentElement();
/*
		Element PersonInfoShipTo = (Element)XPathUtil.getXpathNode(inDoc, "/Order/PersonInfoShipTo");	
		Element PersonInfoShipToOrderline = (Element)XPathUtil.getXpathNode(responseXml, "/Order/OrderLines/OrderLine");	

		PersonInfoShipToOrderline.appendChild(PersonInfoShipTo);
		respEle.appendChild(PersonInfoShipTo);
*/
		return responseXml;
}
}
