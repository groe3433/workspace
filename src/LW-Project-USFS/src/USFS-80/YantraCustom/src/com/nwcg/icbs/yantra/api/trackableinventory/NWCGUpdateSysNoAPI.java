/* &copy; Copyright 2010, Sterling Commerce, Inc. All rights reserved.*/
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Class invoked by the ICBS UI console for handling system 
 * number assignment to all selected order lines 
 *  
 * @author ViKumar, drodriguez
 * @since USFS ICBS-ROSS BR1, in 2-7-3 USFS CVS branch
 * @version 1.2
 */
public class NWCGUpdateSysNoAPI implements YIFCustomApi {

	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger(NWCGUpdateSysNoAPI.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;	
		if (logger.isVerboseEnabled()) { logger.verbose("SDF API Properties set"+this.myProperties); }
	}

	/**
	 * Applies the <code>Order/@ExtnSystemNo</code> attribute passed in from
	 * the <code>ISUorderline/ISUYOMD406</code> System Number Entry entity
	 * resource to all of the <code>OrderLines/OrderLine/Extn</code> elements.
	 * 
	 * <br/><br/>Sample input to this method:
	 * <br/>
	 * <pre>
	 * <code>&lt;?xml version="1.0" encoding="UTF-8"?></code>
	 * <code>&lt;Order IgnoreOrdering="Y" OrderHeaderKey="2009082815021910708426"</code>
	 * <code>  ExtnSystemNumber="123"></code>
	 * <code>&lt;OrderLines></code>
	 * <code>&lt;OrderLine OrderLineKey="2009082815071910708922" YFC_NODE_NUMBER="1"/></code>
	 * <code>&lt;OrderLine OrderLineKey="2009082815071910708923" YFC_NODE_NUMBER="2"/></code>
	 * <code>&lt;/OrderLines></code>
	 * <code>&lt;/Order></code>
	 * </pre> 
	 * @since USFS ICBS-ROSS BR2 Increment 5
	 * @param env Standard YFSEnvironment object
	 * @param inputDoc Document passed by the ISUYOMD406 Save Action
	 * @return <code>org.w3c.dom.Document</code> output XML of the changeOrder API
	 * @see <a
	 *      href="http://download.oracle.com/javase/1.5.0/docs/api/org/w3c/dom/Document.html">org.w3c.dom.Document</a>
	 * @throws Exception
	 */
	public Document process (YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("NWCGUpdateSysNoAPI::process() input document: "+ XMLUtil.extractStringFromDocument(inputDoc));
		
		Element docElm = inputDoc.getDocumentElement();
		String extnSystemNo = docElm.getAttribute(NWCGConstants.SYSTEM_NO_ELEM);
		docElm.removeAttribute(NWCGConstants.SYSTEM_NO_ELEM);
		
		if (StringUtil.isEmpty(extnSystemNo)) {
			logger.debug("Order/@ExtnSystemNo wasn't found in input, exiting.");
			return inputDoc;
		}
		
		NodeList nl = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i =0; i < nl.getLength(); i++) {
			Node curNode = nl.item(i);
			Element curOLElem = (curNode instanceof Element) ? (Element) curNode : null;
			if (curOLElem == null) continue;			

			String newNoteText = NWCGConstants.EMPTY_STRING;
			StringBuffer sb = new StringBuffer("SYS NO: ");
			sb.append(extnSystemNo);	
			newNoteText = sb.toString();
			Element curNewExtnElm = inputDoc.createElement(NWCGConstants.EXTN_ELEMENT);
			curNewExtnElm.setAttribute(NWCGConstants.SYSTEM_NO_ELEM, extnSystemNo);
			curOLElem.appendChild(curNewExtnElm);	
			
			Element curNewNotesElm = inputDoc.createElement(NWCGConstants.NOTES_ELM);
			Element curNewNoteElm = inputDoc.createElement(NWCGConstants.NOTE_ELM);
			curNewNoteElm.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, newNoteText);

			curNewNotesElm.appendChild(curNewNoteElm);
			curOLElem.appendChild(curNewNotesElm);
		}		
		logger.verbose("Input doc to changeOrder: "+XMLUtil.extractStringFromDocument(inputDoc));
		Document changeOrderOP = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, inputDoc);
		logger.verbose("Output of changeOrder: "+XMLUtil.extractStringFromDocument(changeOrderOP));
		
		return changeOrderOP;
	}
	
	/**
	 * Sample input to this file is 
	 * <Order IgnoreOrdering="Y" OrderHeaderKey="2009082815021910708426"
	 *   SystemNumber="123" strNoOfOrderLines="3">
	 *   <OrderLines>
	 *       <OrderLine OrderLineKey_0="2009082815071910708922"
	 *           OrderLineKey_1="2009082815071910708923"
	 *           OrderLineKey_2="2009082815071910708924" strNot_0="1"
	 *         strNot_1="2" strNot_2="3"/>
	 *  </OrderLines>
	 * </Order>
	 * it uses SystemNumber to update Order Line table and NWCG Trackable Item Table
	 * 
	 * @since USFS ICBS-ROSS BR1, in 2-7-3 USFS CVS branch
	 * @deprecated
	 */
	@Deprecated
	public Document consumeMessage(YFSEnvironment env,Document doc) throws Exception
	{
		System.out.println("doc ::" + XMLUtil.extractStringFromDocument(doc));
		
		Element rootElem = doc.getDocumentElement();
		
		//prepare input for change order and call change order api 
		
		Document changeOrderInDoc = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element orderElem = changeOrderInDoc.getDocumentElement();
		String strOrderHeaderKey = rootElem.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		String strSystemNumber = rootElem.getAttribute("SystemNumber");
		
		orderElem.setAttribute(NWCGConstants.ORDER_HEADER_KEY, strOrderHeaderKey );
		orderElem.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
		orderElem.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		
		Element orderLinesElem = changeOrderInDoc.createElement(NWCGConstants.ORDER_LINES);
		orderElem.appendChild(orderLinesElem);
		
		System.out.println("changeOrderInDoc before adding orderlines ::" + XMLUtil.extractStringFromDocument(changeOrderInDoc));
		
		String strNoOfOrderLines = rootElem.getAttribute("strNoOfOrderLines");
		System.out.println("total number of orderlines " + strNoOfOrderLines);
		
		int intNoOfOrderLines = Integer.parseInt(strNoOfOrderLines);
		
		Element olsrcElem = (Element) rootElem.getElementsByTagName(NWCGConstants.ORDER_LINE).item(0);
		
		System.out.println("olsrcElem :: " + XMLUtil.extractStringFromNode(olsrcElem));
		
		for(int i = 0; i < intNoOfOrderLines ; i++) {
			Element orderLineElement = changeOrderInDoc.createElement(NWCGConstants.ORDER_LINE);
			Element extnElement = changeOrderInDoc.createElement(NWCGConstants.EXTN_ELEMENT);
			extnElement.setAttribute("ExtnSystemNo", strSystemNumber);
			orderLineElement.appendChild(extnElement);
			
			String strOrderLineKey = olsrcElem.getAttribute("OrderLineKey_"+i);
			String strNot = olsrcElem.getAttribute("strNot_"+i);
			System.out.println("OrderLineKey_"+i  + strOrderLineKey);
			orderLineElement.setAttribute(NWCGConstants.ORDER_LINE_KEY, strOrderLineKey);
			
			Element notesElem = changeOrderInDoc.createElement("Notes");
			orderLineElement.appendChild(notesElem);
			Element noteElem = changeOrderInDoc.createElement("Note");
			strNot = "( SYS#:"+strSystemNumber+" ), "+strNot;
			noteElem.setAttribute("NoteText", strNot);
			notesElem.appendChild(noteElem);
			orderLinesElem.appendChild(orderLineElement);
		}	
		
		System.out.println("changeOrderInDoc after adding orderlines ::" + XMLUtil.extractStringFromDocument(changeOrderInDoc));		
        CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInDoc);		
		
		return doc;
	}
}