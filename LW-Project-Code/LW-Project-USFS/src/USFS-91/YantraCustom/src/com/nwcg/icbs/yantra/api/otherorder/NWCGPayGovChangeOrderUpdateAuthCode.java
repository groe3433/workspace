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

package com.nwcg.icbs.yantra.api.otherorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

public class NWCGPayGovChangeOrderUpdateAuthCode implements YIFCustomApi 
{
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPayGovChangeOrderUpdateAuthCode.class);
	
	private Properties myProperties = null;
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document buildInputXMLChangeOrder(YFSEnvironment env, Document inXML) throws Exception {
		
		/*
			<Order OrderNo="0000029513" OrderHeaderKey="200807071524231264354"
			Action="MODIFY">
			<Extn ExtnAuthorizationCode="123456789"/>
			</Order>
		*/
		String sOrderNo = "";
		String sOrderHeaderKey ="";
		String sAuthCode = "";
		
		logger.verbose("********************************************");
		logger.verbose("*               changeOrder                *");
		logger.verbose("********************************************");

		logger.verbose("Input XML:: "+ XMLUtil.getXMLString(inXML));

		Element rootElement = inXML.getDocumentElement();
		
		sOrderNo = rootElement.getAttribute( NWCGConstants.AGENCY_TRACKING_ID );
		sOrderHeaderKey = rootElement.getAttribute( NWCGConstants.ORDER_HEADER_KEY);
		sAuthCode = rootElement.getAttribute( NWCGConstants.NWCG_AUTHORIZATION_CODE);

		logger.verbose("Input Order No: " + sOrderNo);
		logger.verbose("Input Order Header Key: " + sOrderHeaderKey);
		logger.verbose("Authorization Code: " + sAuthCode);
		
		Document changeOrderInputDoc = XMLUtil.newDocument();
		Element el_Order = changeOrderInputDoc.createElement(NWCGConstants.ORDER_ELM);

		changeOrderInputDoc.appendChild(el_Order);
		el_Order.setAttribute( NWCGConstants.ACTION , NWCGConstants.MODIFY);
		el_Order.setAttribute( NWCGConstants.ORDER_HEADER_KEY , sOrderHeaderKey);
		el_Order.setAttribute( NWCGConstants.ORDER_NO , sOrderNo);
		
		Element el_extn = changeOrderInputDoc.createElement(NWCGConstants.EXTN_ELEMENT);
		el_Order.appendChild(el_extn);
		el_extn.setAttribute( NWCGConstants.EXTN_AUTHORIZATION_CODE , sAuthCode);

		
		logger.verbose("changeOrder API Input XML:: "+ XMLUtil.getXMLString(changeOrderInputDoc));
		
		return changeOrderInputDoc;
		
	}
	
}