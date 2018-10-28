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

package com.nwcg.icbs.yantra.api.common;

import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API will retrieve the object stored in the environment context under a
 * certain key. The key should be configured in the Configurator as an Argument
 * for the API node.
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGGetDocumentFromEnvAPI implements YIFCustomApi {

	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Properties props;

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGGetDocumentFromEnvAPI.class
			.getName());

	/**
	 * Creates a new instance of GetDocumentFromEnvAPI.
	 */
	public NWCGGetDocumentFromEnvAPI() {
	}

	/**
	 * Stores properties configured in configurator. The property expected by
	 * this API is: bls.helpdesk.getdoc.env.key
	 * 
	 * @param props
	 *            Properties configured in Configurator.
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	/**
	 * Retrieves the object stored in the environment context under the key
	 * specified in the property: bls.helpdesk.getdoc.env.key. The object should
	 * be an instance of either org.w3c.dom.Document or
	 * com.yantra.yfc.dom.YFCDocument.
	 * 
	 * @param env Yantra Environment Context.
	 * @param inDoc Input Document. Not used by this API.
	 * @return Returns the document stored in the environment.
	 */
	public Document getDocumentFromEnv(YFSEnvironment env, Document inDoc) throws Exception {
    	System.out.println("@@@@@ Entering NWCGGetDocumentFromEnvAPI::getDocumentFromEnv @@@@@");
		System.out.println("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
		
		String docid = props.getProperty(NWCGConstants.KEY_GETDOC_ENV_KEY);
		Object obj = CommonUtilities.getContextObject(env, docid);
		Document doc = null;
		if (obj instanceof Document) {
			doc = (Document) obj;
		} else if (obj instanceof YFCDocument) {
			doc = ((YFCDocument) obj).getDocument();
		} else {
			YFCDocument tmpDoc = YFCDocument.parse("<" + NWCGConstants.TAG_INTERNAL_ENVELOPE + "/>");
			YFCNode tnode = tmpDoc.createTextNode(obj.toString());
			tmpDoc.getDocumentElement().appendChild(tnode);
			doc = tmpDoc.getDocument();
		}

		System.out.println("@@@@@ doc : " + XMLUtil.getXMLString(doc));
    	System.out.println("@@@@@ Exiting NWCGGetDocumentFromEnvAPI::getDocumentFromEnv @@@@@");
		return doc;
	}
}