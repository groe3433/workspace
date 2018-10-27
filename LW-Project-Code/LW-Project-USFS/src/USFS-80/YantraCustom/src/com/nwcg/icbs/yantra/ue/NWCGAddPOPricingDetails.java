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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;


/**
 * This user exit is implimented to get the supplire price from NWCG_SUPPLIER_ITEM 
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date May 16, 2013
 */
public class NWCGAddPOPricingDetails implements YFSOrderRepricingUE {

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGAddPOPricingDetails.class.getName());

	/**
	 * this method will return the xml having item unit price populated from
	 * NWCG_SUPPLIER_ITEM
	 * 
	 * (orderRepricing.xml)
	 * <?xml version="1.0" encoding="UTF-8"?>
		<Order DocumentType="" EnterpriseCode="" OrderHeaderKey="" OrderNo="" SellerOrganizationCode="">
    		<OrderReleases>
        		<OrderRelease OrderReleaseKey="" ReleaseNo=""/>
    		</OrderReleases>
    		<OrderLines>
        		<OrderLine OrderLineKey="" PrimeLineNo="" SubLineNo="">
            		<Item CostCurrency="" CountryOfOrigin="" CreditWOReceipt=""
                		CustomerItem="" CustomerItemDesc="" ECCNNo=""
                		HarmonizedCode="" ISBN="" IsReturnable="" ItemDesc=""
                		ItemID="" ItemShortDesc="" ItemWeight=""
                		ItemWeightUOM="" ManufacturerItem=""
                		ManufacturerItemDesc="" ManufacturerName="" NMFCClass=""
                		NMFCCode="" NMFCDescription="" ProductClass=""
                		ProductLine="" ReturnWindow="" ScheduleBCode=""
                		SupplierItem="" SupplierItemDesc="" TaxProductCode=""
                		UPCCode="" UnitCost="" UnitOfMeasure=""/>
        		</OrderLine>
    		</OrderLines>
    		<OverallTotals GrandCharges="" GrandDiscount="" GrandTax=""
        		GrandTotal="" HdrCharges="" HdrDiscount="" HdrTax="" HdrTotal="" LineSubTotal=""/>
		</Order>
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws YFSUserExitException
	 */
	public Document orderReprice(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		System.out.println("@@@@@ Entering NWCGAddPOPricingDetails::orderReprice @@@@@");
		System.out.println("@@@@@ inDoc: " + XMLUtil.getXMLString(inDoc));

		Element order = inDoc.getDocumentElement();
		
		// SellerOrganizationCode = WA0025
		String strSellerOrgCode = order.getAttribute("SellerOrganizationCode");
		
		// get all the order lines
		Element orderLines = (Element) XMLUtil.getChildNodeByName(order, "OrderLines");
		NodeList nlOrderLine = orderLines.getElementsByTagName("OrderLine");

		for (int index = 0; index < nlOrderLine.getLength(); index++) {
			Element elemOrderLine = (Element) nlOrderLine.item(index);
			// if element is not null
			if (elemOrderLine != null) {
				// get the flag HasRepricingQuantityChanged - the value to this flag is set to Y if its a new row
				String strHasRepricingQuantityChanged = StringUtil.nonNull(elemOrderLine.getAttribute("HasRepricingQuantityChanged"));
				if (strHasRepricingQuantityChanged.equals("N")) {
					// continue with the next record if the pricing is already done for this
					continue;
				}
				
				// get the LinePriceInfo
				NodeList nlLinePriceInfo = elemOrderLine.getElementsByTagName("LinePriceInfo");
				Element elemLinePriceInfo = null;
				System.out.println("@@@@@ nlLinePriceInfo: " + nlLinePriceInfo.getLength());

				if (nlLinePriceInfo != null) {
					System.out.println("@@@@@ NWCGAddPOPricingDetails:: ***** Got the LinePriceInfo setting the unit price");

					elemLinePriceInfo = (Element) nlLinePriceInfo.item(0);
					// if the tag doesnt exists, create a new one and append to the OrderLine
					if (elemLinePriceInfo == null) {
						// create an element
						elemLinePriceInfo = inDoc.createElement("LinePriceInfo");
						// append it to OrderLine
						elemOrderLine.appendChild(elemLinePriceInfo);
					}
				}

				Element item = (Element) XMLUtil.getChildNodeByName(elemOrderLine, "Item");
				String strItemID = "", strUOM = "", strPC = "";
				if (item != null) {
					strItemID = item.getAttribute("ItemID");
					strUOM = item.getAttribute("UnitOfMeasure");
					strPC = item.getAttribute("ProductClass");
				}
				
				// query the nwcg supplier item for the item
				try {
					Document getNWCGSupplierItemListInput = XMLUtil.createDocument("NWCGSupplierItem");
					Element root = getNWCGSupplierItemListInput.getDocumentElement();
					root.setAttribute("ItemID", strItemID);
					root.setAttribute("ProductClass", strPC);
					root.setAttribute("UnitOfMeasure", strUOM);
					root.setAttribute("SupplierID", strSellerOrgCode);

					Document getNWCGSupplierItemListOutput = null;
					try {
						System.out.println("@@@@@ getNWCGSupplierItemListInput: " + XMLUtil.getXMLString(getNWCGSupplierItemListInput));
						getNWCGSupplierItemListOutput = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_SUPPLIER_ITEM_LIST, getNWCGSupplierItemListInput);
						System.out.println("@@@@@ getNWCGSupplierItemListOutput: " + XMLUtil.getXMLString(getNWCGSupplierItemListOutput));
					} catch (Exception e) {
						// idk why the system was unable to get the resource nwcg.icbs.getsupplieritemlist.service so just hardcoding the value.
						System.out.println("@@@@@ getNWCGSupplierItemListInput: " + XMLUtil.getXMLString(getNWCGSupplierItemListInput));
						getNWCGSupplierItemListOutput = CommonUtilities.invokeService(env, "NWCGGetSupplierItemList", getNWCGSupplierItemListInput);
						System.out.println("@@@@@ getNWCGSupplierItemListOutput: " + XMLUtil.getXMLString(getNWCGSupplierItemListOutput));
					}

					System.out.println("@@@@@ NWCGConstants.NWCG_GET_SUPPLIER_ITEM_LIST: " + XMLUtil.getXMLString(getNWCGSupplierItemListOutput));

					Element supplierItemList = getNWCGSupplierItemListOutput.getDocumentElement();

					// get the NWCGSupplierItem entry - should return only one record
					NodeList nlSupplierItemList = supplierItemList.getElementsByTagName("NWCGSupplierItem");
					Element supplierItem = null;
					String unitCost = "";
					// if record exists
					System.out.println("@@@@@ nlSupplierItemList: " + nlSupplierItemList.getLength());
					if (nlSupplierItemList != null && nlSupplierItemList.getLength() > 0) {
						// get the record
						supplierItem = (Element) nlSupplierItemList.item(0);
					}

					if (supplierItem != null) {
						System.out.println("@@@@@ Supplier item is not NULL ");
						
						// extract the unit cost
						unitCost = supplierItem.getAttribute("UnitCost");
					}
					// set the unit cost from the supplier item if its available
					if (!StringUtil.isEmpty(unitCost)) {
						System.out.println("@@@@@ Setting unitCost....");
						elemLinePriceInfo.setAttribute("UnitPrice", unitCost);
					}
					System.out.println("@@@@@ unitCost: " + unitCost);
				} catch (Exception e) {
					// wrap the exception and send it across
					e.printStackTrace();
					YFSUserExitException ye = new YFSUserExitException(e.getMessage());
					ye.setStackTrace(e.getStackTrace());
					throw ye;
				}
			}
		}
		System.out.println("@@@@@ Exiting NWCGAddPOPricingDetails::orderReprice @@@@@");
		return inDoc;
	}
}