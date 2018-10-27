/*
 * MergeDocumentInEnvWithInputAPI.java
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */


package com.nwcg.icbs.yantra.api.common;

import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API will merge the input document with the document stored in the
 * environment context under a certain key. The key should be configured in
 * Configurator as an Argument for the API node.
 * @author ssankar
 */
public class NWCGMergeDocumentInEnvWithInputAPI implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGMergeDocumentInEnvWithInputAPI.class);
    
    /**
     * Instance to store the properties configured for the condition in Configurator.
     */
    private Properties props;
    
    /** Creates a new instance of MergeDocumentInEnvWithInputAPI */
    public NWCGMergeDocumentInEnvWithInputAPI() {
    }
    
    /**
     * Stores properties configured in configurator. The property expected by this API
     * is: bls.helpdesk.mergedoc.env.key
     * @param props Properties configured in Configurator.
     */
    public void setProperties(Properties props) {
        this.props = props;
    }

    /**
     * Retrieves the document stored in the environment context under the key
     * configured in property: bls.helpdesk.mergedoc.env.key. Merges this with
     * the Input Document and returns the merged document. Since org.w3c.dom.Document
     * follows the Composition pattern and does not allow an element to belong to
     * more than one Document, a clone of the input and environment documents
     * would be created prior to merging. The merging thus leaves both the input
     * document and the document in the environment intact.The structure of the
     * merged document would be:
     * <MergedDocument>
     *     <InputDocument>
     *         ...INPUT DOCUMENT...
     *     </InputDocument>
     *     <EnvironmentDocument>
     *         ...DOCUMENT RETRIEVED FROM THE ENVIRONMENT...
     *     </EnvironmentDocument>
     * </MergedDocument>
     * @return Returns the document stored in the environment merged with the
     * input document.
     * @param env Yantra Environment Context.
     * @param inDoc Input Document to be merged with the document in the environment.
     * @throws java.lang.Exception Thrown if unable to clone the input document
     * or the document retrieved from the environment.
     */    
    public Document mergeDocumentInEnvWithInputAPI(YFSEnvironment env, Document inDoc) throws Exception {
    	logger.verbose("Entering MergeDocumentInEnvWithInputAPI.mergeDocumentInEnvWithInputAPI()");
        
        YFCDocument yfcInDoc = YFCDocument.getDocumentFor(inDoc);
        
        if (logger.isVerboseEnabled())
        	logger.verbose("Got Document: " + yfcInDoc.getString());
        
        YFCDocument mergeDoc = YFCDocument.createDocument(NWCGConstants.TAG_MERGE_ROOT_DOC);
        YFCElement rootElement = mergeDoc.getDocumentElement();
        YFCElement wrapperElement = rootElement.createChild(NWCGConstants.TAG_MERGE_INPUT_DOC);
        
        //Insert input document as first child.
        //Now, to import, we have to remove the element from the original Document
        //and then insert into new parent. Since we dont want to disturb the original
        //document, lets operate on its clone.
        YFCDocument cloneInDoc = CommonUtilities.cloneDocument(yfcInDoc);
        YFCElement yfce = cloneInDoc.getDocumentElement();
        cloneInDoc.removeChild(yfce);
        wrapperElement.importNode(yfce);

        //Get document in env.
        String docid = props.getProperty(NWCGConstants.KEY_MERGEDOC_ENV_KEY);
        if (docid != null)
            addDocumentInEnv(env, rootElement, docid);
        
        int i = 1;
        while ((docid = props.getProperty(NWCGConstants.KEY_MERGEDOC_ENV_KEY + "." + i++)) != null) {
            addDocumentInEnv(env, rootElement, docid);
        }

        if (logger.isVerboseEnabled())
        	logger.verbose("Returning Merged Document: " + mergeDoc.getString());

        logger.verbose("Exiting MergeDocumentInEnvWithInputAPI.mergeDocumentInEnvWithInputAPI()");
        return mergeDoc.getDocument();
    }
    
    
    private void addDocumentInEnv(YFSEnvironment env, YFCElement rootElement, String docid)
    throws Exception {
    	if (logger.isVerboseEnabled())
    		logger.verbose("Looking in Env for key [" + docid + "]");
        
        Object obj = CommonUtilities.getContextObject(env, docid);

        YFCDocument envDoc = null;
        if (obj instanceof Document) {
            envDoc = YFCDocument.getDocumentFor((Document) obj);
        } else if (obj instanceof YFCDocument) {
            envDoc = (YFCDocument) obj;
        }

        if (logger.isVerboseEnabled()) {
        	if (envDoc != null) {
        		logger.verbose("Document Found in Env for Key [" + docid + "] Document [" + envDoc.getString() + "]");
        	} else {
        		logger.verbose("No document found in Env for Key [" + docid + "] Values [" + obj + "]");
        	}
        }

        //Create wrapper document.
        
        //Insert document obtained form the env as second child of the wrapper inDoc.
        YFCElement wrapperElement = rootElement.createChild(NWCGConstants.TAG_MERGE_ENVIRONMENT_DOC);        
        if (envDoc != null) {
            //Lets operate on env's clone.
            YFCDocument cloneInDoc = CommonUtilities.cloneDocument(envDoc);
            YFCElement yfce = cloneInDoc.getDocumentElement();
            cloneInDoc.removeChild(yfce);
            wrapperElement.importNode(yfce);
        }
                
    }
    
    /**
     * This method will get the incident key from the output of extended api changeIncidentOrder (which is the
     * input document (ipDoc) to this method). Set this incident key to the document stored in env. IncidentKey 
     * placed in this document is used to retrieve the data for ROSSAccountCodes and ContactInfo. This is used
     * as part of NWCGChangeIncidentOrderService
     * @param env
     * @param ipDoc
     * @return
     * @throws Exception
     */
    public Document updateIncidentKeyInEnvDocument(YFSEnvironment env, Document ipDoc) throws Exception {
    	if (logger.isVerboseEnabled())
    		logger.verbose("Input document : " + XMLUtil.getXMLString(ipDoc));
    	
    	logger.verbose("Env Key : " + NWCGConstants.KEY_SETDOC_ENV_KEY);
    	logger.verbose("Properties : " + props.toString());
		String docid = props.getProperty(NWCGConstants.KEY_SETDOC_ENV_KEY);
		if (logger.isVerboseEnabled())
			logger.verbose("Looking in Env for key [" + docid + "]");

		Object obj = CommonUtilities.getContextObject(env, docid);
		logger.verbose("Obtained the object");

		Document doc = null;
		if (obj instanceof Document) {
			doc = (Document) obj;
		} else if (obj instanceof YFCDocument) {
			doc = ((YFCDocument) obj).getDocument();
		} else {
			YFCDocument tmpDoc = YFCDocument.parse("<"
					+ NWCGConstants.TAG_INTERNAL_ENVELOPE + "/>");
			YFCNode tnode = tmpDoc.createTextNode(obj.toString());
			tmpDoc.getDocumentElement().appendChild(tnode);
			doc = tmpDoc.getDocument();
		}

		if (logger.isVerboseEnabled()) {
			if (doc != null) {
				logger.verbose("Document Found in Env for Key [" + docid
						+ "] Document [" + XMLUtil.getXMLString(doc) + "]");
			} else {
				logger.verbose("No document found in Env for Key [" + docid
						+ "] Values [" + obj + "]");
			}
		}

    	// Retrieve incidentKey
    	String incidentKey = ipDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_KEY);
    	if (incidentKey != null){
    		logger.verbose("Incident Key : " + incidentKey);
        	// Set incidentKey in the env document (input of NWCGChangeIncidentOrderService)
        	doc.getDocumentElement().setAttribute(NWCGConstants.INCIDENT_KEY, incidentKey);
    	}
    	
    	// Return the env document with the incidentkey
    	return doc;
    }
    
}
