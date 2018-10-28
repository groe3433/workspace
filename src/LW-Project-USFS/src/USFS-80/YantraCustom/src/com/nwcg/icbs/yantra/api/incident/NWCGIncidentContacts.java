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
package com.nwcg.icbs.yantra.api.incident;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author sdas
 * @since May 7, 2008 
 */
public class NWCGIncidentContacts implements YIFCustomApi {
	
	private Properties myProperties;
	private static Logger logger = Logger.getLogger();
	
    /* (non-Javadoc)
     * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
     */
    public void setProperties(Properties arg0) throws Exception {
        this.myProperties = arg0;
    }
    
    public Document getIncidentContactList(YFSEnvironment env, Document doc) throws YFSException {
        logger.beginTimer("getIncidentContactList");
        try {
			logger.verbose("input doc to NWCGIncidentContacts:"+XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}

        Document resultDoc = null;
        Document retDoc	   = null;
        Document outDoc    = null;
        
        String incidentKey = NWCGConstants.EMPTY_STRING;
        
        // Fetch tag name
        String tagName = doc.getDocumentElement().getTagName();
        
        try{
            
            if(tagName.equals(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG)){
            
		        NodeList nwcgIncidentOrderNl = doc.getDocumentElement().getElementsByTagName(NWCGConstants.NWCG_INCIDENT_ORDER);
		        if(nwcgIncidentOrderNl != null){
		            
		            // Creating the final return document
		            retDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG);
			        
		            for(int c=0;c<nwcgIncidentOrderNl.getLength();c++){
			            Element el = (Element)nwcgIncidentOrderNl.item(c);
			            
			            // Retrieving the incident key
			            incidentKey = el.getAttribute(NWCGConstants.INCIDENT_KEY);
			            logger.verbose("inc key:"+incidentKey);
			            
			            outDoc = getIncidentOrderContactInfo(env, incidentKey);
			            
			            Document elemDoc = XMLUtil.getDocumentForElement(el); 
			            
			            // Appending the output of NWCGGetIncidentContactsListService to the input Doc
			            if(outDoc.getChildNodes() != null){
			                resultDoc = elemDoc.getDocumentElement().appendChild(elemDoc.importNode(outDoc.getDocumentElement(),true)).getOwnerDocument();
			            }
			            
			            // appending the result of API call to retDoc
		                retDoc.getDocumentElement().appendChild(retDoc.importNode(resultDoc.getDocumentElement(),true));
		                
			        }
		            logger.verbose("Input XML for NWCGIncidentOrderList:"+XMLUtil.extractStringFromDocument(retDoc));
		        }
		        
            }else{
                
	            incidentKey = doc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_KEY);
                logger.verbose("inc key:"+incidentKey);
                
	            outDoc = getIncidentOrderContactInfo(env, incidentKey);
	            
	            // Appending the output of NWCGGetIncidentContactsListService to the input Doc
	            if(outDoc.getChildNodes() != null) {
	                retDoc = doc.getDocumentElement().appendChild(doc.importNode(outDoc.getDocumentElement(),true)).getOwnerDocument();
	            }	            
	            logger.verbose("final Doc for NWCGIncidentOrder:"+XMLUtil.extractStringFromDocument(retDoc));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.endTimer("getIncidentContactList");
        return retDoc;
    }
    
    public Document createIncidentContact(YFSEnvironment environment, Document document) throws YFSException {
        Document elemDoc = null;
        createIncidentContactMethod(environment, document);
        elemDoc = getNWCGIncidentOrderDoc(document, elemDoc);
        try {
			logger.verbose("elemDoc :" + XMLUtil.extractStringFromDocument(elemDoc));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
        return elemDoc;
    }
    
    /**
     * @param env
     * @param incidentKey
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public Document getIncidentOrderContactInfo(YFSEnvironment env, String incidentKey) throws ParserConfigurationException, SAXException, IOException, Exception {
        // Forming Input XML to get data from NWCG_INCIDENT_ORDER_CON_INFO table
        Document inDoc = XMLUtil.getDocument("<NWCGIncidentOrderConInfo IncidentOrderKey=\""+incidentKey+"\" />");
        Document outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_INCIDENT_CONTACTS_LIST_SERVICE,inDoc);
        return outDoc;
    }

    /**
     * @param document
     * @param elemDoc
     * @return
     */
    private Document getNWCGIncidentOrderDoc(Document document, Document elemDoc) {
        try{
	        XPathWrapper wrapper = new XPathWrapper(document);
	        Element element = (Element)wrapper.getNode("/MergedDocument/InputDocument/NWCGIncidentOrder");
	        elemDoc = XMLUtil.getDocumentForElement(element);
        }catch(Exception e){
            e.printStackTrace();
        }
        return elemDoc;
    }

    /**
     * @param environment
     * @param document
     */
    private void createIncidentContactMethod(YFSEnvironment env, Document document) {
        
        Document inDoc = null;
        Document opDoc = null;
        
        try{
            logger.verbose("inside createIncidentContact method");
	        logger.verbose("Document :"+XMLUtil.extractStringFromDocument(document));
            
	        XPathWrapper xpWrap = new XPathWrapper(document);
	        NodeList nodeList = xpWrap.getNodeList("/MergedDocument/EnvironmentDocument/NWCGIncidentOrder/NWCGIncidentOrderConInfoList/NWCGIncidentOrderConInfo");
	        for(int c=0;c<nodeList.getLength();c++){
	            Element element = (Element)nodeList.item(c);
	            String incidentKey = xpWrap.getAttribute("/MergedDocument/InputDocument/NWCGIncidentOrder/@IncidentKey");
	            
	            inDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_CON_INFO_TAG);
	            inDoc.getDocumentElement().setAttribute("Contact",element.getAttribute("Contact"));
	            inDoc.getDocumentElement().setAttribute("ContactInfo",element.getAttribute("ContactInfo"));
	            inDoc.getDocumentElement().setAttribute("ContactType",element.getAttribute("ContactType"));
	            inDoc.getDocumentElement().setAttribute("IncidentOrderKey",incidentKey);
	            
	            logger.verbose("NWCGIncidentOrderConInfo ipDoc:"+XMLUtil.extractStringFromDocument(inDoc));
	            
	            opDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_INCIDENT_CONTACTS_SERVICE,inDoc);
	            
	            logger.verbose("NWCGIncidentOrderConInfo opDoc:"+XMLUtil.extractStringFromDocument(opDoc));
	        }
	      
        }catch(Exception e){
            logger.verbose("Exception in createIncidentContacts method.Raising alert");
            String primCacheId = (document != null) ? document.getDocumentElement().getAttribute(NWCGConstants.PRIMARY_CACHE_ID) : NWCGConstants.EMPTY_STRING;
            String cacheAdmin = CommonUtilities.getAdminUserForCache(env, primCacheId);
            CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, e.toString(), cacheAdmin, inDoc,e,null);
        }
    }

    /**
     * This method is used to update the incident contacts as part of getIncidentDetails +
     * update incident details request from ROSS.
     * This method will delete the existing incident contacts and create new contacts
     * 
     * based on input xml
     * @param env
     * @param ipDoc
     * @return
     * @throws Exception
     */
    public Document updateIncidentContacts(YFSEnvironment env, Document ipDoc) throws Exception {
    	logger.verbose("Input Doc : " + XMLUtil.getXMLString(ipDoc));
    	Element nwcgIncidentOrderElm = ipDoc.getDocumentElement();
    	// Delete all the existing incident contacts by calling deleteIncidentOrderContactInfo
    	String incidentKey = nwcgIncidentOrderElm.getAttribute(NWCGConstants.INCIDENT_KEY);
    	deleteIncidentOrderContactInfo(env, incidentKey);
    	
    	// Insert new incident contacts from the input document
    	NodeList incidentContactsNL = nwcgIncidentOrderElm.getElementsByTagName(NWCGAAConstants.INCIDENT_ORDER_CON_INFO_TAG);
    	if (incidentContactsNL.getLength() > 0){
    		for (int i=0; i < incidentContactsNL.getLength(); i++){
    			Node incidentContact = incidentContactsNL.item(i);
    			NamedNodeMap incidentContactAttrs = incidentContact.getAttributes();
	            Document incidentContactDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_CON_INFO_TAG);
	            Element incidentContactElm = incidentContactDoc.getDocumentElement();
    			for (int attr=0; attr < incidentContactAttrs.getLength(); attr++){
    				Node attrNameVal = incidentContactAttrs.item(attr);
    				incidentContactElm.setAttribute(attrNameVal.getNodeName(), attrNameVal.getNodeValue());
    			}
    			// We don't know whether incidentOrderKey is part of the xml and if it is part of it
    			// we are not getting the incidentkey value. So, we are resetting it to IncidentOrderKey 
    			// from input xml
    			incidentContactElm.setAttribute("IncidentOrderKey", incidentKey);
    			
                logger.verbose("NWCGIncidentOrderConInfo ipDoc:"+XMLUtil.getXMLString(incidentContactDoc));
                CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_INCIDENT_CONTACTS_SERVICE, incidentContactDoc);
    		}
    	}
    	else {
    		logger.verbose("There are no incident contacts for this incident");
    	}
    	return ipDoc;
    }
    
    
    /**
     * Get the contact info key (s) based on incident key. Call the delete incident contacts
     * service for each contact info key 
     */
    public void deleteIncidentOrderContactInfo(YFSEnvironment env, String incidentKey) throws Exception {
    	Document conInfoListDoc = getIncidentOrderContactInfo(env, incidentKey);
    	if (conInfoListDoc != null){
    		Element conInfoListElm = conInfoListDoc.getDocumentElement();
    		NodeList conInfoListNL = conInfoListElm.getElementsByTagName(NWCGAAConstants.INCIDENT_ORDER_CON_INFO_TAG);
    		if (conInfoListNL != null){
    			for (int i=0; i < conInfoListNL.getLength(); i++){
    				Element conInfoElm = (Element) conInfoListNL.item(i);
    				String conInfoKey = conInfoElm.getAttribute("ContactInfoKey");
    				if (conInfoKey != null && conInfoKey.length() > 0){
    					Document delConInfoDoc = XMLUtil.getDocument("<NWCGIncidentOrderConInfo ContactInfoKey=\"" + conInfoKey + "\" />");
    			    	CommonUtilities.invokeService(env, NWCGConstants.NWCG_DELETE_INCIDENT_CONTACTS_SERVICE, delConInfoDoc);
    				}
    				else {
    					logger.verbose("Contact Info Key is null or empty string");
    				}
    			}
    		}
    		else {
    			logger.verbose("NWCGIncidentConInfo list is empty");
    		}
    	}
    	else {
    		logger.verbose("There are no NWCGIncidentConInfo elements for incident key : " + incidentKey);
    	}
    }  
    
    public static void main(String[] args) throws Exception{
        
        /*try{
            Document d1 = XMLUtil.getDocument("<NWCGIncidentOrderContactInfoList><NWCGIncidentOrderContactInfo ContactInfoKey=\"123\" IncidentOrderKey=\"456\" Contact=\"abc\" /></NWCGIncidentOrderContactInfoList>");
            String s = "<NWCGIncidentOrderList><NWCGIncidentOrder IncidentKey=\"20071205224601696329\" /><NWCGIncidentOrder IncidentKey=\"12345\" /></NWCGIncidentOrderList>";
            Document doc = XMLUtil.getDocument(s);
            NodeList nl = doc.getDocumentElement().getElementsByTagName(NWCGConstants.NWCG_INCIDENT_ORDER);
            for(int c=0;c<nl.getLength();c++){
                Element el = (Element)nl.item(c);
                System.out.println("inc key:"+el.getAttribute(NWCGConstants.INCIDENT_KEY));
                System.out.println("elem string:"+XMLUtil.getElementXMLString(el));
                
                Document elemDoc = XMLUtil.getDocumentForElement(el);
                Document resultDoc = elemDoc.getDocumentElement().appendChild(elemDoc.importNode(d1.getDocumentElement(),true)).getOwnerDocument();
                System.out.println("resultDoc :"+XMLUtil.getXMLString(resultDoc));
                
                Document retDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG);
                retDoc.getDocumentElement().appendChild(retDoc.importNode(resultDoc.getDocumentElement(),true));
                System.out.println("final Doc :"+XMLUtil.getXMLString(retDoc));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }*/
        
        /*File file = new File("E:\\testGetIncidentDoc1.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document xml = db.parse(file);
        
        XPathWrapper wrapper = new XPathWrapper(xml);
        Element element = (Element)wrapper.getNode("/MergedDocument/InputDocument/NWCGIncidentOrder");
        Document elemDoc = XMLUtil.getDocumentForElement(element);
        System.out.println("elemDoc :"+XMLUtil.getXMLString(elemDoc));*/
        
        /*System.out.println("input doc to NWCGIncidentContacts:"+XMLUtil.getXMLString(xml));
        System.out.println("tag name:"+xml.getDocumentElement().getTagName());
        
        Document resultDoc = null;
        Document retDoc	   = null;
        
        try{
	        NodeList nwcgIncidentOrderNl = xml.getDocumentElement().getElementsByTagName(NWCGConstants.NWCG_INCIDENT_ORDER);
	        if(nwcgIncidentOrderNl != null){
	            
	            // Creating the final return document
	            retDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_LIST_TAG);
		        
	            for(int c=0;c<nwcgIncidentOrderNl.getLength();c++){
		            Element el = (Element)nwcgIncidentOrderNl.item(c);
		            
		            // Retrieving the incident key
		            String incidentKey = el.getAttribute(NWCGConstants.INCIDENT_KEY);
		            System.out.println("inc key:"+incidentKey);
	            }
	        }
        }catch(Exception e){
            e.printStackTrace();
        }*/
        
        /*System.out.println("Input XML: " + XMLUtil.getXMLString(xml));
        
        XPathWrapper xpWrap = new XPathWrapper(xml);
        NodeList nodeList = xpWrap.getNodeList("MergedDocument/EnvironmentDocument/NWCGIncidentOrder/NWCGIncidentOrderConInfoList/NWCGIncidentOrderConInfo");
        System.out.println("nllength :"+nodeList.getLength());
        for(int c=0;c<nodeList.getLength();c++){
            Element element = (Element)nodeList.item(c);
            String incidentKey = xpWrap.getAttribute("/MergedDocument/InputDocument/NWCGIncidentOrder/@IncidentKey");
            
            Document inDoc = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_CON_INFO_TAG);
            inDoc.getDocumentElement().setAttribute("Contact",element.getAttribute("Contact"));
            inDoc.getDocumentElement().setAttribute("ContactInfo",element.getAttribute("ContactInfo"));
            inDoc.getDocumentElement().setAttribute("ContactType",element.getAttribute("ContactType"));
            inDoc.getDocumentElement().setAttribute("IncidentOrderKey",incidentKey);
            
            System.out.println("NWCGIncidentOrderConInfo ipDoc:"+XMLUtil.getXMLString(inDoc));
            
            //Document opDoc = CommonUtilities.invokeService(environment,NWCGConstants.NWCG_CREATE_INCIDENT_CONTACTS_SERVICE,inDoc);
            
            //System.out.println("NWCGIncidentOrderConInfo opDoc:"+XMLUtil.getXMLString(opDoc));
        }*/       
    }
}