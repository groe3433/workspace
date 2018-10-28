package com.nwcg.icbs.yantra.api.adjlocninv;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGAdjLocnInvReference implements YIFCustomApi {

	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	public Document adjLocnInvReference(YFSEnvironment env, Document inputXml) throws Exception {
		System.out.println("@@@@@ Entered com.nwcg.icbs.yantra.api.adjlocninv.NWCGAdjLocnInvReference::adjLocnInvReference @@@@@");
		System.out.println("@@@@@ inputXml :: " + XMLUtil.getXMLString(inputXml));
		
		if (env == null) {
			System.out.println("!!!!! YFSEnvironment is null... ");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inputXml == null) {
			System.out.println("!!!!! Input Document is null... ");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}

		Document docRoot = XMLUtil.createDocument("AdjustLocationInventory");
		Element rootElem = docRoot.getDocumentElement();
		long seqNo = ((YCPContext) env).getNextDBSeqNo(NWCGConstants.SEQ_YFS_ORDER_NO);
		StringBuffer sb = new StringBuffer("A");
		sb.append((new Long(seqNo)).toString());
		rootElem.setAttribute("DocumentNumber", sb.toString());

		System.out.println("@@@@@ docRoot :: " + XMLUtil.getXMLString(docRoot));
		System.out.println("@@@@@ Exiting com.nwcg.icbs.yantra.api.adjlocninv.NWCGAdjLocnInvReference::adjLocnInvReference @@@@@");
		return docRoot;
	}
}