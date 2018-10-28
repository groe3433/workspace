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

package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * This is a Consumer class, basically based on the attribute TrackableInventoryAction
 * it Request the factory to for an appropriate instance and then invokes insertOrUpdateTrackableRecord
 * method for processing the message
 * 
 * @author Oxford
 */
public class NWCGTrackableInventoryMessageConsumer implements YIFCustomApi {

	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
	}

	public Document consumeMessage(YFSEnvironment env, Document doc) throws Exception {
		Element root_elem = doc.getDocumentElement();
		NWCGITrackableRecordMutator obj = null;
		if (root_elem != null) {
			String strAction = StringUtil.nonNull(root_elem.getAttribute("TrackableInventoryAction"));
			if (strAction.equals("")) {
				return doc;
			}
			obj = NWCGTrackableInventoryFactory.getMessageProcesser(strAction);
		}
		
		Document outXML = obj.insertOrUpdateTrackableRecord(env, doc);
		System.out.println("@@@@@ outXML: " + XMLUtil.getXMLString(outXML));
		if(outXML == null) {
			System.out.println("!!!!! outXML is NULL...");
		}
		return outXML;
	}
}