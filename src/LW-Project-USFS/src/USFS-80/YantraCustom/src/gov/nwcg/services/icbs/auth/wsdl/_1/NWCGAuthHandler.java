package gov.nwcg.services.icbs.auth.wsdl._1;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

public class NWCGAuthHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext arg0) {
	}

	public boolean handleFault(SOAPMessageContext arg0) {
		return true;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		Document tempDoc = null;
		try {
			tempDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element rootElem = tempDoc.createElement("tempDoc");
			tempDoc.appendChild(rootElem);
			tempDoc.getDocumentElement().setAttribute("Security", "Y");
			tempDoc.getDocumentElement().setAttribute("Messagecontext", "Y");
			tempDoc.getDocumentElement().setAttribute("Password", "Y");
			tempDoc.getDocumentElement().setAttribute("Created", "Y");
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			SOAPMessage message = smc.getMessage();
			message.writeTo(System.out);
			SOAPEnvelope env = message.getSOAPPart().getEnvelope();
			SOAPHeader header = env.getHeader();
			if (header != null) {
				Iterator iter = header.getChildElements();
				while (iter.hasNext()) {
					SOAPElement elem = (SOAPElement) iter.next();
					if (elem.getLocalName().equals("Security")) {
						Iterator usernameTokeIter = elem.getChildElements();
						while (usernameTokeIter.hasNext()) {
							SOAPElement userNameTokenElem = (SOAPElement) usernameTokeIter
									.next();
							Iterator userNameTokenchildIter = userNameTokenElem
									.getChildElements();
							while (userNameTokenchildIter.hasNext()) {
								SOAPElement userNameTokenChildElem = (SOAPElement) userNameTokenchildIter
										.next();
								if (userNameTokenChildElem.getLocalName()
										.equals("Password")) {
									if (userNameTokenChildElem.getValue() == null) {
										tempDoc.getDocumentElement()
												.setAttribute("Password", "N");
									}
								}
								if (userNameTokenChildElem.getLocalName()
										.equals("Created")) {
									String timeStamp = userNameTokenChildElem
											.getValue();
									if (timeStamp != null) {
										XMLGregorianCalendar gregCal = dtf
												.newXMLGregorianCalendar(timeStamp);
										if (!gregCal.isValid()) {
											tempDoc.getDocumentElement()
													.setAttribute("Created",
															"N");
										}
									}
									tempDoc.getDocumentElement().setAttribute(
											"Created", "N");
								}
								if (userNameTokenChildElem.getLocalName()
										.equals("Username")) {
								}
								if (userNameTokenChildElem.getLocalName()
										.equals("Nonce")) {
								}
							}
						}
					} else if (elem.getLocalName().equals("MessageContext")) {
					} else {
						tempDoc.getDocumentElement().setAttribute("Security",
								"N");
						tempDoc.getDocumentElement().setAttribute(
								"Messagecontext", "N");
					}
				}
			}
		} catch (SOAPException se) {
			se.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} catch (DatatypeConfigurationException dtce) {
			dtce.printStackTrace();
		} catch (ParserConfigurationException pcex) {
			pcex.printStackTrace();
		}
		ServletContext ctx = (ServletContext) smc
				.get(SOAPMessageContext.SERVLET_CONTEXT);
		ctx.setAttribute("tempDoc", tempDoc);
		return true;
	}
}