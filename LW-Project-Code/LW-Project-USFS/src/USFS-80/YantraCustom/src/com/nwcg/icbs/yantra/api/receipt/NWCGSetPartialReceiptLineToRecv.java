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

package com.nwcg.icbs.yantra.api.receipt;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Hashtable;
import java.util.Enumeration;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGSetPartialReceiptLineToRecv implements YIFCustomApi
{
	
	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger();
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	
	public Document setPartialReceiptLineToRecv(YFSEnvironment env, Document doc) throws Exception
	{
		logger.verbose("NWCGSetPartialReceiptLineToRecv::setPartialReceiptLineToRecv, " +
				"Input document : " + XMLUtil.extractStringFromDocument(doc));
		//System.out.println("NWCGSetPartialReceiptLineToRecv::setPartialReceiptLineToRecv - Input document : " + XMLUtil.extractStringFromDocument(doc));
		
		// Need to Check the OrderLine.OpenQty = 0. If so, then we need to set the OrderLine ExtnToReceive
		
		Document docChangeOrderInput		= null;
		StringBuffer sbChangeOrderInput = new StringBuffer();
		
		NodeList orderLineNL = doc.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		if (orderLineNL == null || orderLineNL.getLength() < 1) 
			throw new YFSException("No order lines found");
		
		for(int i = 0; i < orderLineNL.getLength(); i++)
		{
			Node curOrderLineNode = orderLineNL.item(i);
			
			Element curOrderLine = (curOrderLineNode instanceof Element) ? (Element) curOrderLineNode : null;
			
			if(curOrderLine == null)
				throw new YFSException("Element cannot be established");
			
			String strOpenQty = curOrderLine.getAttribute(NWCGConstants.OPEN_QTY);
			
			
			if(i == 0)
			{
				String strOrderHeaderKey = curOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
				sbChangeOrderInput.append("<Order OrderHeaderKey=\""+ strOrderHeaderKey +"\" Override=\"Y\"><OrderLines>");
			}
			
			
			//if(Float.parseFloat(strOpenQty)== 0)
			//{
					//Since this line has an OpenQty of 0, we need to get the OrderHeaderKey and OrderLineKey
					//and build the stringBuffer to build up changeOrder input document
				
					sbChangeOrderInput.append("<OrderLine PrimeLineNo=\"");
					String strPrimeLineNo = curOrderLine.getAttribute(NWCGConstants.PRIME_LINE_NO);
					sbChangeOrderInput.append(strPrimeLineNo+"\" OrderLineKey=\"");
					String strOrderLineKey = curOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					sbChangeOrderInput.append(strOrderLineKey+"\"><Extn ExtnToReceive=\"N\" /></OrderLine>");
			//}
			
			}
		
		sbChangeOrderInput.append("</OrderLines></Order>");
		String sChangeOrderInput = sbChangeOrderInput.toString();
		docChangeOrderInput = XMLUtil.getDocument(sChangeOrderInput);
		
		//Call ChangeOrderAPI to Set ExtnToRecv 
		Document docChangeOrderOutput = CommonUtilities.invokeAPI(env,"changeOrder", docChangeOrderInput);
		//System.out.println("NWCGSetPartialReceiptLineToRecv::setPartialReceiptLineToRecv, " + "Input To changeOrder : " + XMLUtil.extractStringFromDocument(docChangeOrderInput));
		
		return doc;
	}
	
}