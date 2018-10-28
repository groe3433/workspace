package com.nwcg.icbs.yantra.condition.issue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/*
 * <Order >
    <Extn />
    <OrderLines>
        <OrderLine ReqShipDate="2007-02-12T00:00:00" YFC_NODE_NUMBER="1">
            <Extn ExtnBackOrderFlag="N" ExtnBackorderedQty=""
                ExtnFwdQty="" ExtnOrigReqQty="1" ExtnQtyRfi="0"
                ExtnRequestNo="S-111" ExtnUTFQty=""/>
            <OrderLineTranQuantity OrderedQty="1.0" TransactionalUOM="EA"/>
            <Item ItemID="001100" ProductClass="Supply"/>
        </OrderLine>
        <OrderLine ReqShipDate="2007-02-12T00:00:00" YFC_NODE_NUMBER="2">
            <Extn ExtnBackOrderFlag="" ExtnBackorderedQty=""
                ExtnFwdQty="" ExtnOrigReqQty="" ExtnQtyRfi=""
                ExtnRequestNo="S-" ExtnUTFQty=""/>
        </OrderLine>
    </OrderLines>
</Order>
 */
public class NWCGDiscardBlankLines implements YIFCustomApi {
	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger(NWCGDiscardBlankLines.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}
	
	public Document discardBlankLines(YFSEnvironment env,Document inXML) throws Exception
	{
		//System.out.println("INCOMING XML:: "+ XMLUtil.getXMLString(inXML));
		if(logger.isVerboseEnabled()) logger.verbose("NWCGDiscardBlankLines:: inXML "+XMLUtil.getXMLString(inXML));
		// get the elemenet
		Element rootElem = inXML.getDocumentElement();
		String strDocumentType = rootElem.getAttribute(NWCGConstants.DOCUMENT_TYPE);
		NodeList nlOrderLines = rootElem.getElementsByTagName(NWCGConstants.ORDER_LINES);
		// get the orderlines this is help us to remove the orderline elements 
		if(nlOrderLines != null && nlOrderLines.getLength() >= 1)
		{
			Element elemOrderLines = (Element) nlOrderLines.item(0);
			// this will carry all the nodes to be remvoed
			ArrayList removeList = new ArrayList(20);
			
			NodeList nlOrderLine= elemOrderLines.getElementsByTagName(NWCGConstants.ORDER_LINE);
			
			if(nlOrderLine != null)
			{
				int iTotal = nlOrderLine.getLength() ;
				if(logger.isVerboseEnabled()) logger.verbose("iTotal ==> "+iTotal);
				// for all orderline(s)
				for(int index = 0 ; index < iTotal ; index++)
				{
					Element elemOrderLine = (Element) nlOrderLine.item(index);
					if(logger.isVerboseEnabled()) logger.verbose("elemOrderLine => "+elemOrderLine);
					NodeList nlItem = elemOrderLine.getElementsByTagName(NWCGConstants.ITEM);
					String strOrderLineKey = StringUtil.nonNull(elemOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
					// if the item node exists
					if(nlItem != null)
					{
						if(nlItem.getLength() >= 1)
						{
							Element elemItem = (Element) nlItem.item(0);
							String strItemID = elemItem.getAttribute(NWCGConstants.ITEM_ID);
							// if item id is null add the reference
							if(strItemID == null || strItemID.equals(NWCGConstants.EMPTY_STRING))
							{
								if(strOrderLineKey.equals(NWCGConstants.EMPTY_STRING))
								{
									removeList.add(elemOrderLine);
								}
							} else {
								// if it's PO doc, update orderedQty from originalOrderedQty before passing to changeOrder
								if (strDocumentType.equals("0005"))
								{
									NodeList nlOrderLineTQ = elemOrderLine.getElementsByTagName(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
									if((nlOrderLineTQ != null) && (nlOrderLineTQ.getLength() >= 1))
									{
										Element elemOrderLineTQ = (Element) nlOrderLineTQ.item(0);
										String strOriginalOrderedQty = elemOrderLineTQ.getAttribute(NWCGConstants.ORIGINAL_ORDERED_QTY);
										elemOrderLineTQ.setAttribute(NWCGConstants.ORDERED_QTY,strOriginalOrderedQty);
									}
								}
							}
						}
					}
					// add the element to the remove list even if the item
					// tag is missing
					if(nlItem == null || nlItem.getLength() == 0)
					{
						if(strOrderLineKey.equals(NWCGConstants.EMPTY_STRING))
							removeList.add(elemOrderLine);
					}
				}
			}
			//remove order lines
			Iterator itr = removeList.iterator();
			while(itr.hasNext())
			{
				Element elem = (Element)itr.next();
				elemOrderLines.removeChild(elem);
				
			}
		}
		NodeList orderLines = rootElem.getElementsByTagName(NWCGConstants.ORDER_LINES);
		Element elemOrderLine = null;
		Element orderLineMsg = inXML.getDocumentElement();
		
		String RequestedQty	 			= "";
		String BackorderedQty 			= "";
		String FwdOrderedQty		 	= "";
		String UTFQty		 			= "";
		String RequestNo	 			= "";
		String OrderedQty		 		= "";
		
		Element  ollDSTElm = inXML.createElement("OrderLineQtyMsg");
		ArrayList al = new ArrayList(); 
		
		if(orderLines != null && orderLines.getLength() >= 1)
		{
			Element elemOrderLines = (Element) orderLines.item(0);
								
			NodeList nodeOrderLineQty = elemOrderLines.getElementsByTagName(NWCGConstants.ORDER_LINE);
			
			for(int n=0; n<nodeOrderLineQty.getLength(); n++)
			{
				
					elemOrderLine	 				= (Element) nodeOrderLineQty.item(n);
					Element ExtnElem 				= (Element)elemOrderLine.getElementsByTagName(NWCGConstants.EXTN_ELEMENT).item(0);
					Element OrderLineTranQtyElem 	= (Element)elemOrderLine.getElementsByTagName(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM).item(0);
					if(ExtnElem != null)
					{
						RequestedQty	 			= ExtnElem.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
						BackorderedQty 				= ExtnElem.getAttribute(NWCGConstants.EXTN_BACKORDER_QTY);
						FwdOrderedQty		 		= ExtnElem.getAttribute(NWCGConstants.EXTN_FWD_QTY);
						UTFQty		 				= ExtnElem.getAttribute(NWCGConstants.EXTN_UTF_QTY);
						RequestNo	 				= ExtnElem.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
					}
					if(OrderLineTranQtyElem != null)
					{
						OrderedQty		 			= OrderLineTranQtyElem.getAttribute(NWCGConstants.ORDERED_QTY);
					}
					double dblTotalQty	 			= 0; 
					double dblRequestedQty 			= 0;
					double dblBackorderedQty 		= 0;
					double dblFwdOrderedQty 		= 0;
					double dblUTFQty		 		= 0;
					double dblOrderedQty	 		= 0;
					
					if(!RequestedQty.equals(NWCGConstants.EMPTY_STRING) && RequestedQty != null){
						dblRequestedQty					= Double.parseDouble(RequestedQty);
					} else {
						dblRequestedQty=0;
					}
					if(!BackorderedQty.equals(NWCGConstants.EMPTY_STRING) && BackorderedQty != null){
						dblBackorderedQty					= Double.parseDouble(BackorderedQty);
						if(dblBackorderedQty > 0)
						ExtnElem.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, "Y");
					} else {
						dblBackorderedQty=0;
					}
					if(!FwdOrderedQty.equals(NWCGConstants.EMPTY_STRING) && FwdOrderedQty != null){
						dblFwdOrderedQty					= Double.parseDouble(FwdOrderedQty);
						if(dblFwdOrderedQty > 0)
						ExtnElem.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, "Y");
					} else {
						dblFwdOrderedQty=0;
					}
					if(!UTFQty.equals(NWCGConstants.EMPTY_STRING) && UTFQty != null){
						dblUTFQty					= Double.parseDouble(UTFQty);
					} else {
						dblUTFQty=0;
					}
					if(!OrderedQty.equals(NWCGConstants.EMPTY_STRING) && OrderedQty != null){
						dblOrderedQty					= Double.parseDouble(OrderedQty);
					} else {
						dblOrderedQty=0;
					}
					
					dblTotalQty = (dblBackorderedQty + dblFwdOrderedQty + dblUTFQty + dblOrderedQty);	

					if(dblTotalQty != dblRequestedQty)
					{
						//System.out.println("####################THIS IS WHERE THE ERROR NEEDS TO BE DISPLAYED FOR ORDER LINES ");
						al.add(RequestNo);
						if(dblRequestedQty > dblTotalQty)
						{
							YFSException e = new YFSException("TOTAL QUANTITY IS LESS THAN REQUESTED QUANTITY");
							throw e;
						}
					} 			
			}//end of for loop
		}
		
		if(al.size()>0)
		{
			if(logger.isVerboseEnabled()) logger.verbose("Contents of al: " + al);
			ollDSTElm.setAttribute("Msg","Y");
		} else {
			ollDSTElm.setAttribute("Msg","N");
		}
		//ollDSTElm.setAttribute("Msg","Y");
		orderLineMsg.appendChild(ollDSTElm);
		
		//System.out.println("returning XML:: "+ XMLUtil.getXMLString(inXML));
		if(logger.isVerboseEnabled()) logger.verbose("returning:: "+ XMLUtil.getXMLString(inXML));
		return inXML;
	}	
}