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
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API will store the input document in the environment context under a
 * certain key. The key should be configured in the Configurator as an Argument
 * for the API node.
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGSetDocumentInEnvAPI implements YIFCustomApi {

	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Properties props;

	/**
	 * Creates a new instance of SetDocumentInEnvAPI
	 */
	public NWCGSetDocumentInEnvAPI() {
	}

	/**
	 * Stores properties configured in configurator. The property expected by
	 * this API is: bls.helpdesk.setdoc.env.key
	 * 
	 * @param props
	 *            Properties configured in Configurator.
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	/**
	 * Stores the input document in the environment context under the key
	 * configured in property: bls.helpdesk.setdoc.env.key. A copy of the
	 * document will be created and stored so that changes to the input document
	 * by downstream components do not affect the copy stored in the
	 * environment.
	 * 
	 * @return Returns the document stored in the environment.
	 * @param env
	 *            Yantra Environment Context.
	 * @param inDoc
	 *            Input Document to be stored.
	 * @throws java.lang.Exception
	 *             Thrown if unable to clone the input document.
	 */
	public Document setDocumentInEnv(YFSEnvironment env, Document inDoc)
			throws Exception {

		String docid = props.getProperty(NWCGConstants.KEY_SETDOC_ENV_KEY);

		// We got to make a copy of the doc and then store in env.
		// This is because, we are stoirng the env as given to us now
		// If we store the reference, further modifications of the doc
		// in the SDf would affect the reference in env too.
		Document cloneDoc = CommonUtilities.cloneDocument(inDoc);

		// We'll simply set the value and return the same document as output.
		// If some other object was in the env for the same key,
		// We'll not return that, since the document that has to be saved is the
		// latest and needs to be carried forward to the next component in the
		// SDF.
		// If the obj that is in the env currently is needed, then it needs to
		// be
		// explicitly fetched.
		CommonUtilities.setContextObject(env, docid, cloneDoc);

		return cloneDoc;
	}
}