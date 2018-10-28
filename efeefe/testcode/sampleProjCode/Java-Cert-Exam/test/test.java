package test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class test {
	public static void main(String [] s) throws JAXBException, ParserConfigurationException, SAXException, IOException {
		JAXBContext context = JAXBContext.newInstance("gov.nwcg.services.ross.resource_order._1");
		
		System.out.println("@@@@@ Entering NWCGJAXBContextWrapper::processUnmarshalling (Document)");
		Unmarshaller unmar = context.createUnmarshaller();
		unmar.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
		Object returnObj = null;
		
		String str = "<ro:StatusNFESResourceRequestResp xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
						"<ro:ResponseStatus>" + 
				        "<ReturnCode>0</ReturnCode>" + 
				        "<ResponseMessage>" + 
				            "<Code>STATUS-I-000001</Code>" + 
				            "<Severity>Information</Severity>" + 
				            "<Description>SUCCESS</Description>" + 
				       "</ResponseMessage>" + 
				    "</ro:ResponseStatus>" + 
				    "<ro:RequestKey>" + 
				        "<NaturalResourceRequestKey>" + 
				            "<IncidentKey>" + 
				                "<NaturalIncidentKey>" + 
				                   "<HostID>" + 
				                        "<UnitIDPrefix>AK</UnitIDPrefix>" + 
				                        "<UnitIDSuffix>TAD</UnitIDSuffix>" + 
				                    "</HostID>" + 
				                    "<SequenceNumber>333</SequenceNumber>" + 
				                    "<YearCreated>2015</YearCreated>" + 
				                "</NaturalIncidentKey>" + 
				            "</IncidentKey>" + 
				            "<RequestCode>" + 
				                "<CatalogID>S</CatalogID>" + 
				                "<SequenceNumber>463</SequenceNumber>" + 
				            "</RequestCode>" + 
				        "</NaturalResourceRequestKey>" + 
				    "</ro:RequestKey>" + 
				    "<ro:MessageOriginator>" + 
				        "<SystemOfOrigin>" + 
				            "<SystemType>ICBS</SystemType>" + 
				        "</SystemOfOrigin>" + 
				        "<DispatchUnitID>" + 
				            "<UnitIDPrefix>AK</UnitIDPrefix>" + 
				            "<UnitIDSuffix>AKK</UnitIDSuffix>" + 
				        "</DispatchUnitID>" + 
				    "</ro:MessageOriginator>" + 
				    "<ro:FillDetail>" + 
				        "<CacheIssue>" + 
				            "<CacheIssueNumber>0000759191</CacheIssueNumber>" + 
				            "<CacheIssueCreateDateTime>2015-07-07T19:43:44-05:00</CacheIssueCreateDateTime>" + 
				        "</CacheIssue>" + 
				        "<CatalogItem>" + 
				            "<CatalogType>NWCG</CatalogType>" + 
				            "<CatalogItemName>KIT - CHAINSAW (AKK SPECIFIC)</CatalogItemName>" + 
				        "</CatalogItem>" + 
				        "<FillQuantity>10</FillQuantity>" + 
				        "<UtfQuantity>0</UtfQuantity>" + 
				        "<BackorderQuantity>0</BackorderQuantity>" + 
				        "<ForwardQuantity>0</ForwardQuantity>" + 
				        "<UserDocumentation>Deliver to Aviation Ops to Transport to Tanana.</UserDocumentation>" + 
				        "<TrackableId>8735-AKK-0159-1198</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1225</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1354</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1369</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1413</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1812</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1821</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1830</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1858</TrackableId>" + 
				        "<TrackableId>8735-AKK-0159-1883</TrackableId>" + 
				        "<MobilizationETD>2015-07-07T19:38:05-05:00</MobilizationETD>" + 
				        "<MobilizationETA>2015-07-07T19:38:05-05:00</MobilizationETA>" + 
				    "</ro:FillDetail>" + 
				    "<ro:ShippingAddress>" + 
				        "<Name>Tanana Zone</Name>" + 
				        "<Type>Shipping</Type>" + 
				        "<Line1>1541 Gaffney Road</Line1>" + 
				        "<Line2/>" + 
				        "<City>Fort Wainwright</City>" + 
				        "<State>AK</State>" + 
				        "<ZipCode>99703</ZipCode>" + 
				        "<CountryCode>USA</CountryCode>" + 
				    "</ro:ShippingAddress>" + 
				    "<ro:ShippingContactName>Expanded Dispatch</ro:ShippingContactName>" + 
				    "<ro:ShippingContactPhone>907-356-5812</ro:ShippingContactPhone>" + 
				"</ro:StatusNFESResourceRequestResp>";
		
		Document doc1 = getDocumentNotNamespaceAware(str);
		
		// xmlns:ro=\"http://nwcg.gov/services/ross/resource_order/1.1\"
		doc1.getDocumentElement().setAttribute("xmlns:ro", "http://nwcg.gov/services/ross/resource_order/1.1");
		
		String str1 = getXMLString(doc1);
		System.out.println("@@@@@ str1 :: " + str1);
		returnObj = unmar.unmarshal(new StreamSource(new StringReader(str1)));

		System.out.println("@@@@@ Exiting NWCGJAXBContextWrapper::processUnmarshalling (Document)");
	}
	
	/**
	 * Created for PI 1736. August 25, 2015. 
	 * Called from the following class files: NWCGDeliverOperationResultsAgent. 
	 * 
	 * @param inXML
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getDocumentNotNamespaceAware(String inXML) throws ParserConfigurationException, SAXException, IOException {
		if ((inXML != null)) {
			inXML = inXML.trim();
			if (inXML.length() > 0) {
				if (inXML.startsWith("<")) {
					StringReader strReader = new StringReader(inXML);
					InputSource iSource = new InputSource(strReader);
					Document newDocument = getDocumentNotNamespaceAware(iSource);
					return newDocument;
				}
				FileReader inFileReader = new FileReader(inXML);
				Document retVal = null;
				try {
					InputSource iSource = new InputSource(inFileReader);
					retVal = getDocumentNotNamespaceAware(iSource);
				} finally {
					inFileReader.close();
				}
				return retVal;
			}
		}
		return null;
	}
	
	/**
	 * Created for PI 1736. August 25, 2015. 
	 * Called from the following class files: XMLUtil::getDocumentNotNamespaceAware. 
	 * 
	 * @param inSource
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getDocumentNotNamespaceAware(InputSource inSource) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dbdr = fac.newDocumentBuilder();
		Document parseDocument = dbdr.parse(inSource);
		return parseDocument;
	}
	
	public static String getXMLString(Document document) {
		if (document == null) {
			return "null";
		} else {
			return serialize(document);
		}
	}
	
	public static String serialize(Node node) {
		return serialize(node, "iso-8859-1", true);
	}

	public static String serialize(Node node, String encoding, boolean indenting) {
		OutputFormat outFmt = null;
		StringWriter strWriter = null;
		XMLSerializer xmlSerializer = null;
		String retVal = null;
		try {
			outFmt = new OutputFormat("xml", encoding, indenting);
			outFmt.setOmitXMLDeclaration(true);
			strWriter = new StringWriter();
			xmlSerializer = new XMLSerializer(strWriter, outFmt);
			if (node == null) {
				return "null";
			}
			short ntype = node.getNodeType();
			switch (ntype) {
			case Node.DOCUMENT_FRAGMENT_NODE:
				xmlSerializer.serialize((DocumentFragment) node);
				break;
			case Node.DOCUMENT_NODE:
				xmlSerializer.serialize((Document) node);
				break;
			case Node.ELEMENT_NODE:
				xmlSerializer.serialize((Element) node);
				break;
			default:
				throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
			}
			retVal = strWriter.toString();
		} catch (IOException e) {
			retVal = e.getMessage();
		} finally {
			try {
				strWriter.close();
			} catch (IOException ie) {
			}
		}
		return retVal;
	}
}
