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

package com.nwcg.icbs.yantra.api.adjlocninv;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;

public class NWCGAdjLocnInvReference implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGAdjLocnInvReference.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	public Document adjLocnInvReference(YFSEnvironment env, Document inputXml) throws Exception {
		logger.verbose("@@@@@ Entered com.nwcg.icbs.yantra.api.adjlocninv.NWCGAdjLocnInvReference::adjLocnInvReference @@@@@");
		logger.verbose("@@@@@ inputXml :: " + XMLUtil.getXMLString(inputXml));
		
		if (env == null) {
			logger.error("!!!!! YFSEnvironment is null... ");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inputXml == null) {
			logger.error("!!!!! Input Document is null... ");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}

		Document docRoot = XMLUtil.createDocument("AdjustLocationInventory");
		Element rootElem = docRoot.getDocumentElement();
		long seqNo = ((YCPContext) env).getNextDBSeqNo(NWCGConstants.SEQ_YFS_ORDER_NO);
		StringBuffer sb = new StringBuffer("A");
		sb.append((new Long(seqNo)).toString());
		rootElem.setAttribute("DocumentNumber", sb.toString());

		logger.verbose("@@@@@ docRoot :: " + XMLUtil.getXMLString(docRoot));
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.adjlocninv.NWCGAdjLocnInvReference::adjLocnInvReference @@@@@");
		return docRoot;
	}
}