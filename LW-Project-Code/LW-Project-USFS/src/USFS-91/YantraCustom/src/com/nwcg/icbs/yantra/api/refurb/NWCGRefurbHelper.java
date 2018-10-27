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

package com.nwcg.icbs.yantra.api.refurb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class contains all the helper methods for refurbishment work order
 * process
 * 
 * @author jvishwakarma
 * 
 */
public class NWCGRefurbHelper {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbHelper.class);

	protected static Document getChangeLocationInventoryAttributesIP(
			YFSEnvironment env, Element elemWO,
			String strInventoryCurrentStatus, String strLocationId,
			boolean bIsComponent, String strDestinationInvStatus, String strNode)
			throws DOMException, Exception {
		// Element elemWO = inXML.getDocumentElement();
		NodeList nlNWCGMasterWorkOrderLine = null;
		Element elemNWCGMasterWorkOrderLine = null;

		if (bIsComponent) {
			nlNWCGMasterWorkOrderLine = elemWO
					.getElementsByTagName("WorkOrderComponent");
		} else {
			nlNWCGMasterWorkOrderLine = elemWO
					.getElementsByTagName("NWCGMasterWorkOrderLine");
		}

		if (nlNWCGMasterWorkOrderLine != null
				&& nlNWCGMasterWorkOrderLine.getLength() > 0) {
			elemNWCGMasterWorkOrderLine = (Element) nlNWCGMasterWorkOrderLine
					.item(0);
		} else // should not come here as all the items will have
				// NWCGMasterWorkOrderLine
		{
			elemNWCGMasterWorkOrderLine = elemWO.getOwnerDocument()
					.createElement("NWCGMasterWorkOrderLine");
		}

		Document rt_ChangeLocationInventoryAttributes = XMLUtil.getDocument();

		Element el_ChangeLocationInventoryAttributes = rt_ChangeLocationInventoryAttributes
				.createElement("ChangeLocationInventoryAttributes");
		rt_ChangeLocationInventoryAttributes
				.appendChild(el_ChangeLocationInventoryAttributes);
		el_ChangeLocationInventoryAttributes.setAttribute("EnterpriseCode",
				NWCGConstants.ENTERPRISE_CODE);
		el_ChangeLocationInventoryAttributes.setAttribute("Node", strNode);

		Element el_Source = rt_ChangeLocationInventoryAttributes
				.createElement("Source");
		el_ChangeLocationInventoryAttributes.appendChild(el_Source);
		el_Source.setAttribute("LocationId", strLocationId);

		Element el_FromInventory = rt_ChangeLocationInventoryAttributes
				.createElement("FromInventory");
		el_Source.appendChild(el_FromInventory);
		el_FromInventory.setAttribute("InventoryStatus",
				strInventoryCurrentStatus);

		if (bIsComponent)
			el_FromInventory.setAttribute("Quantity",
					elemWO.getAttribute("ComponentQuantity"));
		else
			el_FromInventory.setAttribute("Quantity",
					elemWO.getAttribute("QuantityRequested"));

		el_FromInventory.setAttribute("ShipByDate", CommonUtilities
				.changeDateFormat(StringUtil.nonNull(elemWO
						.getAttribute("ShipByDate"))));

		Element el_InventoryItem = rt_ChangeLocationInventoryAttributes
				.createElement("InventoryItem");
		el_FromInventory.appendChild(el_InventoryItem);
		el_InventoryItem.setAttribute("ItemID", elemWO.getAttribute("ItemID"));
		el_InventoryItem.setAttribute("ProductClass",
				elemWO.getAttribute("ProductClass"));
		el_InventoryItem.setAttribute("UnitOfMeasure",
				elemWO.getAttribute("Uom"));

		Element el_TagDetail = rt_ChangeLocationInventoryAttributes
				.createElement("TagDetail");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::&&&&&&&&&&&&&&&&&&&&&");
		if (logger.isVerboseEnabled())
			logger.verbose(XMLUtil
					.getElementXMLString(elemNWCGMasterWorkOrderLine));
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::&&&&&&&&&&&&&&&&&&&&&");
		boolean bAttach = false;
		if (bIsComponent) {
			/*
			 * String strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("BatchNo")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("BatchNo",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute1")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("LotAttribute1",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute2")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("LotAttribute2",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute3")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("LotAttribute3",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotNumber")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("LotNumber",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("RevisionNo")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("RevisionNo",strTemp);
			 * 
			 * //CR 502 starts here
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("OldRevisionNo")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("OldRevisionNo",strTemp);
			 * 
			 * 
			 * //CR 502 ends here
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotKeyReference")); bAttach = notEmptyString(strTemp,bAttach);
			 * el_TagDetail.setAttribute("LotKeyReference",strTemp);
			 */
		} else {
			String strTemp = "";
			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("BatchNo"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("BatchNo", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("ManufacturerName"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("LotAttribute1", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("OwnerUnitID"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("LotAttribute2", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("ManufacturerModel"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("LotAttribute3", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("LotNo"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("LotNumber", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("RevisionNo"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("RevisionNo", strTemp);

			// CR 502 starts here

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("OldRevisionNo"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("OldRevisionNo", strTemp);

			// CR 502 ends here

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("LotKeyReference"));
			bAttach = notEmptyString(strTemp, bAttach);
			el_TagDetail.setAttribute("LotKeyReference", strTemp);
		}

		if (bAttach) {
			el_FromInventory.appendChild(el_TagDetail);
		}

		Element el_SerialList = rt_ChangeLocationInventoryAttributes
				.createElement("SerialList");

		Element el_SerialDetail = rt_ChangeLocationInventoryAttributes
				.createElement("SerialDetail");
		el_SerialList.appendChild(el_SerialDetail);

		String strTemp = "";
		if (bIsComponent) {
			strTemp = StringUtil.nonNull(elemWO.getAttribute("OldSerialNo"));
			// el_SerialDetail.setAttribute("SecondarySerial1",StringUtil.nonNull(elemWO.getAttribute("OldSecondarySerialNo0")));
		} else {
			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("PrimarySerialNo"));
			/*
			 * el_SerialDetail.setAttribute("SecondarySerial1",StringUtil.nonNull
			 * (
			 * elemNWCGMasterWorkOrderLine.getAttribute("SecondarySerialNo1")));
			 * el_SerialDetail
			 * .setAttribute("SecondarySerial2",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo2")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial3",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo3")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial4",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo4")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial5",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo5")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial6",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo6")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial7",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo7")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial8",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo8")));
			 * el_SerialDetail.setAttribute
			 * ("SecondarySerial9",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo9")));
			 */
		}

		el_SerialDetail.setAttribute("SerialNo", strTemp);

		if (!strTemp.equals("")) {
			el_FromInventory.appendChild(el_SerialList);
		}

		Element el_ToInventory = rt_ChangeLocationInventoryAttributes
				.createElement("ToInventory");
		el_Source.appendChild(el_ToInventory);
		if (strDestinationInvStatus == null) {
			el_ToInventory.setAttribute("InventoryStatus",
					elemNWCGMasterWorkOrderLine
							.getAttribute("DestinationInventoryStatus"));
		} else {
			el_ToInventory.setAttribute("InventoryStatus",
					strDestinationInvStatus);
		}

		if (bIsComponent)
			el_ToInventory.setAttribute("Quantity",
					elemWO.getAttribute("ComponentQuantity"));
		else
			el_ToInventory.setAttribute("Quantity",
					elemWO.getAttribute("QuantityRequested"));

		el_ToInventory.setAttribute("ShipByDate", CommonUtilities
				.changeDateFormat(StringUtil.nonNull(elemWO
						.getAttribute("ShipByDate"))));

		Element el_InventoryItem_1 = rt_ChangeLocationInventoryAttributes
				.createElement("InventoryItem");
		el_ToInventory.appendChild(el_InventoryItem_1);
		el_InventoryItem_1
				.setAttribute("ItemID", elemWO.getAttribute("ItemID"));
		el_InventoryItem_1.setAttribute("ProductClass",
				elemWO.getAttribute("ProductClass"));
		el_InventoryItem_1.setAttribute("UnitOfMeasure",
				elemWO.getAttribute("Uom"));

		Element el_TagDetail_2 = rt_ChangeLocationInventoryAttributes
				.createElement("TagDetail");

		bAttach = false;
		if (bIsComponent) {
			/*
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("BatchNo")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("BatchNo",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute1")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("LotAttribute1",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute2")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("LotAttribute2",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotAttribute3")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("LotAttribute3",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotNumber")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("LotNumber",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("RevisionNo")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("RevisionNo",strTemp);
			 * 
			 * strTemp =
			 * StringUtil.nonNull(elemNWCGMasterWorkOrderLine.getAttribute
			 * ("LotKeyReference")); notEmptyString(strTemp,bAttach);
			 * el_TagDetail_2.setAttribute("LotKeyReference",strTemp);
			 */
		} else {
			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("BatchNo"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("BatchNo", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("ManufacturerName"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("LotAttribute1", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("OwnerUnitID"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("LotAttribute2", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("ManufacturerModel"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("LotAttribute3", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("LotNo"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("LotNumber", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("RevisionNo"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("RevisionNo", strTemp);

			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("LotKeyReference"));
			notEmptyString(strTemp, bAttach);
			el_TagDetail_2.setAttribute("LotKeyReference", strTemp);
		}

		if (bAttach) {
			el_ToInventory.appendChild(el_TagDetail_2);
		}

		Element el_SerialList_3 = rt_ChangeLocationInventoryAttributes
				.createElement("SerialList");

		Element el_SerialDetail_4 = rt_ChangeLocationInventoryAttributes
				.createElement("SerialDetail");
		el_SerialList_3.appendChild(el_SerialDetail_4);

		if (bIsComponent) {
			strTemp = StringUtil.nonNull(elemWO.getAttribute("OldSerialNo"));
			// el_SerialDetail_4.setAttribute("SecondarySerial1",StringUtil.nonNull(elemWO.getAttribute("OldSecondarySerialNo0")));
		} else {
			strTemp = StringUtil.nonNull(elemNWCGMasterWorkOrderLine
					.getAttribute("PrimarySerialNo"));
			/*
			 * el_SerialDetail_4.setAttribute("SecondarySerial1",StringUtil.nonNull
			 * (
			 * elemNWCGMasterWorkOrderLine.getAttribute("SecondarySerialNo1")));
			 * el_SerialDetail_4
			 * .setAttribute("SecondarySerial2",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo2")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial3",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo3")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial4",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo4")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial5",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo5")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial6",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo6")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial7",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo7")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial8",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo8")));
			 * el_SerialDetail_4.setAttribute
			 * ("SecondarySerial9",StringUtil.nonNull
			 * (elemNWCGMasterWorkOrderLine
			 * .getAttribute("SecondarySerialNo9")));
			 */
		}
		el_SerialDetail_4.setAttribute("SerialNo", strTemp);

		if (!strTemp.equals("")) {
			el_ToInventory.appendChild(el_SerialList_3);
		}

		Element el_Audit = rt_ChangeLocationInventoryAttributes
				.createElement("Audit");
		el_ChangeLocationInventoryAttributes.appendChild(el_Audit);
		el_Audit.setAttribute("ReasonCode",
				NWCGConstants.RFB_ADJUSTMENT_REASON_CODE);
		el_Audit.setAttribute("ReasonText",
				"CHANGE LOCATION INVENTORY ATTRIBUTE ADJUSTMENT");
		return rt_ChangeLocationInventoryAttributes;
	}

	/*
	 * this method takes two params, first is the actual string to be tested and
	 * second is the verified flag. if current element has already got an
	 * attribute value the second param will be true and thsi method will return
	 * true blindly this variable is used to capture if any attribute is attaced
	 * to the current element
	 */
	private static boolean notEmptyString(String strTemp, boolean bVerified) {
		if (bVerified)
			return true;

		if (!strTemp.equals("")) {
			return true;
		}
		return false;
	}

	// if add key is true, system will add key else the element
	/*
	 * if value passed == 0 - All items == 1 - Replaced Items == 2 - Missing
	 * Items == 3 - Missing Items which are serially tracked == 4 - Missing
	 * Items not Serially Tracked == 5 - Items which are Replaced or Missing But
	 * Serially Tracked
	 */
	// return (HashMap)
	// NWCGRefurbHelper.getComponentsList(documentElement,false,"OldSerialNo",null,"0");
	// return
	// (ArrayList)NWCGRefurbHelper.getComponentsList(elemConfrmWO,true,"SerialNo",set,"");
	protected static Object getComponentsList(Element elemUserInputWO,
			boolean bReturnArray, String strSerialNoAttributeName,
			Collection arrayForComparission, String strIdentifier) {
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getComponentsList params "
					+ elemUserInputWO);
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getComponentsList params bReturnArray "
					+ bReturnArray);
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getComponentsList params strIdentifier  "
					+ strIdentifier);
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getComponentsList params strSerialNoAttributeName "
					+ strSerialNoAttributeName);
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getComponentsList params arrayForComparission "
					+ arrayForComparission);

		Object alMissingItems = new ArrayList();

		if (!bReturnArray) {
			alMissingItems = new HashMap();
		}

		NodeList nlComponents = elemUserInputWO
				.getElementsByTagName("WorkOrderComponent");
		if (nlComponents != null && nlComponents.getLength() > 0) {
			for (int index = 0; index < nlComponents.getLength(); index++) {
				Element elemWorkOrderComponent = (Element) nlComponents
						.item(index);

				NodeList nlWorkOrderComponentTag = elemWorkOrderComponent
						.getElementsByTagName("WorkOrderComponentTag");
				Element elemWorkOrderComponentTag = null;
				if (nlWorkOrderComponentTag != null
						&& nlWorkOrderComponentTag.getLength() > 0) {
					elemWorkOrderComponentTag = (Element) nlWorkOrderComponentTag
							.item(0);
				} else {
					// just to avoid null pointer check
					elemWorkOrderComponentTag = elemUserInputWO
							.getOwnerDocument().createElement(
									"WorkOrderComponentTag");
				}

				String strKey = elemWorkOrderComponent.getAttribute("ItemID")
						.trim()
						+ ","
						+ StringUtil
								.nonNull(
										elemWorkOrderComponent
												.getAttribute(strSerialNoAttributeName))
								.trim()
						+ ","
						+ StringUtil.nonNull(
								elemWorkOrderComponentTag
										.getAttribute("BatchNo")).trim();
				// + ","+
				// StringUtil.nonNull(elemWorkOrderComponentTag.getAttribute("LotNumber")).trim();

				if (arrayForComparission != null
						&& arrayForComparission.size() > 0) {
					if (arrayForComparission.contains(strKey)) {
						// mark the current element for removal
						// later we will directly push all the catalog items in
						// the work order for creation
						((ArrayList) alMissingItems)
								.add(elemWorkOrderComponent);
					}
				} else {

					/*
					 * if value passed == 0 - All items
					 */
					// return all elements
					if (strIdentifier.equals("0")) {
						if (bReturnArray)
							((ArrayList) alMissingItems).add(strKey);
						else
							((HashMap) alMissingItems).put(strKey,
									elemWorkOrderComponent);
					} else // return only missing components
					{// i.e. user hvnt checked the checked box Replace Component
						String strReplaceComponent = StringUtil
								.nonNull(elemWorkOrderComponent
										.getAttribute("ReplaceComponent"));
						String strSerialNo = StringUtil.nonNull(StringUtil
								.nonNull(elemWorkOrderComponent
										.getAttribute("SerialNo")));
						strSerialNo = strSerialNo.trim();
						if (logger.isVerboseEnabled())
							logger.verbose("NWCGRefurbHelper::strReplaceComponent = "
									+ strReplaceComponent);
						if (logger.isVerboseEnabled())
							logger.verbose("NWCGRefurbHelper::strSerialNo = "
									+ strSerialNo);
						/*
						 * if value passed == 1 - Replaced Items
						 */
						if (strIdentifier.equals("1")) {

							if (strReplaceComponent.equals("Y")) {
								if (bReturnArray)
									((ArrayList) alMissingItems).add(strKey);
								else
									((HashMap) alMissingItems).put(strKey,
											elemWorkOrderComponent);

							}// end if

						}
						/*
						 * if value passed == 2 - Missing Items
						 */
						else if (strIdentifier.equals("2")) {
							if (strReplaceComponent.equals("N")) {

								if (bReturnArray)
									((ArrayList) alMissingItems).add(strKey);
								else
									((HashMap) alMissingItems).put(strKey,
											elemWorkOrderComponent);

							}// end if
						}
						/*
						 * if value passed == 3 - Missing Items which are
						 * serially tracked
						 */
						else if (strIdentifier.equals("3")) {
							if (strReplaceComponent.equals("NA")
									&& (!strSerialNo.equals(""))) {

								if (bReturnArray)
									((ArrayList) alMissingItems).add(strKey);
								else
									((HashMap) alMissingItems).put(strKey,
											elemWorkOrderComponent);

							}// end if
						}
						/*
						 * if value passed == 4 - Missing Items not Serially
						 * Tracked
						 */
						else if (strIdentifier.equals("4")) {
							if (strReplaceComponent.equals("N")
									&& strSerialNo.equals("")) {

								if (bReturnArray)
									((ArrayList) alMissingItems).add(strKey);
								else
									((HashMap) alMissingItems).put(strKey,
											elemWorkOrderComponent);

							}// end if
						}
						/*
						 * == 5 - Items which are Replaced or Missing But
						 * Serially Tracked
						 */
						else if (strIdentifier.equals("5")) {
							if ((strReplaceComponent.equals("NA") && (!strSerialNo
									.equals("")))
									|| strReplaceComponent.equals("Y")) {

								if (bReturnArray)
									((ArrayList) alMissingItems).add(strKey);
								else
									((HashMap) alMissingItems).put(strKey,
											elemWorkOrderComponent);

							}// end if
						}
					}
				}
			}// end for
		}// end if
		return alMissingItems;
	}

	/*
	 * this is a little complicated method which does the following 1. gets all
	 * the catalog and component items entered by the user - it gets this list
	 * based on the old serial 2. delete all of the entries from current input
	 * xml - basically remove the old serials from the kit 3. removes not
	 * catalog items from the input xml - since we are reusing the old work
	 * order - that may have some of the items which are note part of the
	 * component, so we need to remove those items before we create the work
	 * order 4. Append all of the user input data into the xml - this will
	 * basically over ride the old serials if any 5. return xml
	 */

	protected static Document getCreateRefurbishmentKittingWorkOrderForCatalogAndComponentItemsXML(
			YFSEnvironment env, Document docConfrmWO, Document inXML)
			throws Exception {
		HashMap hmCatalogPlusComponents = new HashMap();
		NodeList nlWOComponents = inXML.getDocumentElement()
				.getElementsByTagName("WorkOrderComponent");

		if (nlWOComponents != null && nlWOComponents.getLength() > 0) {
			hmCatalogPlusComponents = getCatalogPlusComponentKeyAndElement(inXML
					.getDocumentElement());
		}
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::List of All Components enterd by user :: "
					+ hmCatalogPlusComponents);

		Element elemConfrmWO = docConfrmWO.getDocumentElement();

		// now remove the components which user has already entered
		ArrayList alToBeRemoved = getArrayOfElementsWhichUserWantsToReplaceOrMarkAsLost(
				elemConfrmWO, hmCatalogPlusComponents.keySet());

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::list to remove the items :: "
					+ alToBeRemoved);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::Document Befroe list to remove the items :: "
					+ XMLUtil.getXMLString(elemConfrmWO.getOwnerDocument()));
		deleteComponentElementFromArrayList(alToBeRemoved);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::Document After list to remove the items :: "
					+ XMLUtil.getXMLString(elemConfrmWO.getOwnerDocument()));
		removeNonCatalogItemsFromWorkOrder(env, elemConfrmWO,
				elemConfrmWO.getAttribute("ItemID"),
				elemConfrmWO.getAttribute("Uom"),
				elemConfrmWO.getAttribute("ProductClass"));

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::Document After removing non catalog items :: "
					+ XMLUtil.getXMLString(elemConfrmWO.getOwnerDocument()));
		// push all the items enterd by user into the create work order input
		// xml
		NodeList nlWorkOrderComponents = elemConfrmWO
				.getElementsByTagName("WorkOrderComponents");
		if (nlWorkOrderComponents != null
				&& nlWorkOrderComponents.getLength() > 0) {
			appendComponentOrCatalogFromAnotherNode(
					(Element) nlWorkOrderComponents.item(0),
					hmCatalogPlusComponents);
		}

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::Document After adding all catalog and component items :: "
					+ XMLUtil.getXMLString(elemConfrmWO.getOwnerDocument()));
		elemConfrmWO.setAttribute("ServiceItemGroupCode", "KIT");
		elemConfrmWO.setAttribute("ServiceItemID", "REFURB-KITTING");

		Element elemWorkOrderActivities = docConfrmWO
				.createElement("WorkOrderActivities");
		elemConfrmWO.appendChild(elemWorkOrderActivities);

		Element elemWorkOrderActivity = docConfrmWO
				.createElement("WorkOrderActivity");
		elemWorkOrderActivities.appendChild(elemWorkOrderActivity);

		elemWorkOrderActivity.setAttribute("ActivityCode", "REFURBISHMENT");
		elemWorkOrderActivity.setAttribute("ActivitySeqNo", "1");

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::returning :: docConfrmWO :: "
					+ XMLUtil.getXMLString(docConfrmWO));
		return docConfrmWO;
	}

	private static ArrayList getArrayOfElementsWhichUserWantsToReplaceOrMarkAsLost(
			Element elemConfrmWO, Set set) {

		return (ArrayList) NWCGRefurbHelper.getComponentsList(elemConfrmWO,
				true, "SerialNo", set, "");
	}

	private static void appendComponentOrCatalogFromAnotherNode(
			Element elemWorkOrderComponents, HashMap hmCatalogPlusComponents) {
		if (hmCatalogPlusComponents.values() != null) {
			Iterator itr = hmCatalogPlusComponents.values().iterator();
			while (itr.hasNext()) {
				Element elemComponentOrCatalogItem = (Element) itr.next();
				Element parsedElement = (Element) elemWorkOrderComponents
						.getOwnerDocument().importNode(
								elemComponentOrCatalogItem, true);
				elemWorkOrderComponents.appendChild(parsedElement);

			}
		}

	}

	private static HashMap getCatalogPlusComponentKeyAndElement(
			Element documentElement) {
		return (HashMap) NWCGRefurbHelper.getComponentsList(documentElement,
				false, "OldSerialNo", null, "0");
	}

	private static void removeNonCatalogItemsFromWorkOrder(YFSEnvironment env,
			Element elemWorkOrderComponents, String strItemID, String strUom,
			String strPC) throws Exception {
		Document docItem = XMLUtil.createDocument("Item");
		Element elemItem = docItem.getDocumentElement();
		elemItem.setAttribute("OrganizationCode", NWCGConstants.ENTERPRISE_CODE);
		elemItem.setAttribute("ItemID", strItemID);
		elemItem.setAttribute("UnitOfMeasure", strUom);
		elemItem.setAttribute("ProductClass", strPC);

		Document docGetItemList = CommonUtilities.invokeAPI(env,
				"NWCGProcessRefurbishmentOrder_getItemDetails",
				"getItemDetails", docItem);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::docGetItemList "
					+ XMLUtil.getXMLString(docGetItemList));

		Element elemGetItemList = docGetItemList.getDocumentElement();
		NodeList nlComponent = elemGetItemList
				.getElementsByTagName("Component");
		ArrayList alComponentItemID = new ArrayList();
		if (nlComponent != null && nlComponent.getLength() > 0) {
			for (int index = 0; index < nlComponent.getLength(); index++) {
				Element elemComponent = (Element) nlComponent.item(index);
				String strComponentItemID = elemComponent
						.getAttribute("ComponentItemID");
				alComponentItemID.add(strComponentItemID);
			}
		}

		NodeList nlWorkOrderComponents = elemWorkOrderComponents
				.getElementsByTagName("WorkOrderComponent");
		ArrayList alItemNotInComponentList = new ArrayList();
		if (nlWorkOrderComponents != null
				&& nlWorkOrderComponents.getLength() > 0) {
			for (int index = 0; index < nlWorkOrderComponents.getLength(); index++) {
				Element elemComponent = (Element) nlWorkOrderComponents
						.item(index);
				String strWOItemId = elemComponent.getAttribute("ItemID");
				if (!alComponentItemID.contains(strWOItemId)) {
					if (logger.isVerboseEnabled())
						logger.verbose("NWCGRefurbHelper::removeNonCatalogItemsFromWorkOrder:: NOT A COMPONENT BUT a part of Work Order removing item "
								+ strWOItemId);
					// if(logger.isVerboseEnabled())
					// logger.verbose("NWCGRefurbHelper::removeNonCatalogItemsFromWorkOrder: "
					// +
					// XMLUtil.getXMLString(elemWorkOrderComponents.getOwnerDocument()));
					// this in not a catalog item - delete it
					alItemNotInComponentList.add(elemComponent);
				}
			}
		}

		deleteComponentElementFromArrayList(alItemNotInComponentList);

	}

	protected static void deleteComponentElementFromArrayList(
			ArrayList alComponentToBeDeleted) {
		Iterator itr = alComponentToBeDeleted.iterator();
		while (itr.hasNext()) {
			Element elem = (Element) itr.next();
			elem.getParentNode().removeChild(elem);
		}

	}

	protected static void removeMissingComponentItemsNotSeriallyTrackedfromDekittingWorkOrder(
			Document docCreateWO, Element elemUserInputWO) {
		// unchecking the check box means the component is missing
		// get all component list enterd by the user which has ReplaceComponent
		// set to N or blank
		ArrayList alMissingComponents = getMissingComponentsWhichAreNotSeriallyTrackedKeyList(elemUserInputWO);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::removeMissingComponentItemsNotSeriallyTrackedfromDekittingWorkOrder "
					+ alMissingComponents);

		// now get list of all the Component in the current document which has
		// missing components
		ArrayList alComponentToBeDeleted = getElementsToBeDeletedFromParam(
				docCreateWO.getDocumentElement(), alMissingComponents);

		deleteComponentElementFromArrayList(alComponentToBeDeleted);

	}

	protected static Document prepareConfirmWorderOrderActivityComponentXML(
			Document confrmWODoc, Element elemWOCreated,
			Document docNWCGgetWorkOrder) throws Exception {
		NodeList nlWorkOrderActivityDtl = confrmWODoc
				.getElementsByTagName("WorkOrderActivityDtl");

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::Input Document to prepareConfirmWorderOrderActivityComponentXML:------------\n");
		if (logger.isVerboseEnabled())
			logger.verbose(XMLUtil.getXMLString(confrmWODoc));
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::----------------------------------------------------------------------------");

		// we need to append the WorkOrderComponents from the output of the
		// create work order xml to the confirm work order activity input xml

		if (nlWorkOrderActivityDtl != null
				&& nlWorkOrderActivityDtl.getLength() > 0) {
			// only one WorkOrderActivityDtl element
			Element elemWorkOrderActivityDtl = (Element) nlWorkOrderActivityDtl
					.item(0);

			if (logger.isVerboseEnabled())
				logger.verbose("NWCGRefurbHelper::Element elemWorkOrderActivityDtl is :------------\n");
			if (logger.isVerboseEnabled())
				logger.verbose(XMLUtil
						.getElementXMLString(elemWorkOrderActivityDtl));
			if (logger.isVerboseEnabled())
				logger.verbose("NWCGRefurbHelper::--------------------------------------------------");

			NodeList nlWOComponents = elemWOCreated
					.getElementsByTagName("WorkOrderComponents");

			if (nlWOComponents != null && nlWOComponents.getLength() > 0) {
				Element elemWorkOrderComponents = (Element) confrmWODoc
						.importNode(nlWOComponents.item(0), true);

				elemWorkOrderActivityDtl.appendChild(elemWorkOrderComponents);

				NodeList nlWorkOrderComponents = elemWorkOrderComponents
						.getElementsByTagName("WorkOrderComponent");

				if (nlWorkOrderComponents != null
						&& nlWorkOrderComponents.getLength() > 0) {
					for (int index = 0; index < nlWorkOrderComponents
							.getLength(); index++) {
						Element elemWOComponent = (Element) nlWorkOrderComponents
								.item(index);
						String strItemID = elemWOComponent
								.getAttribute("ItemID");

						String strSerialNo = StringUtil.nonNull(elemWOComponent
								.getAttribute("SerialNo"));
						strSerialNo = strSerialNo.trim();
						Element elemWOComponentTag = (Element) XPathUtil
								.getNode(docNWCGgetWorkOrder,
										"/WorkOrder/WorkOrderComponents/WorkOrderComponent[@ItemID='"
												+ strItemID
												+ "' and @SerialNo='"
												+ strSerialNo
												+ "']/WorkOrderComponentTag");
						if (logger.isVerboseEnabled())
							logger.verbose("NWCGRefurbHelper:: elemWOComponentTag ==> "
									+ elemWOComponentTag);
						if (elemWOComponentTag != null) {
							if (logger.isVerboseEnabled())
								logger.verbose("NWCGRefurbHelper:: elemWOComponentTag ==> "
										+ XMLUtil
												.getElementXMLString(elemWOComponentTag));
							elemWOComponent.getOwnerDocument().importNode(
									elemWOComponentTag, true);
						}

						if (logger.isVerboseEnabled())
							logger.verbose("NWCGRefurbHelper::The WO Component is:------------------");
						if (logger.isVerboseEnabled())
							logger.verbose(XMLUtil
									.getElementXMLString(elemWOComponent));
						if (logger.isVerboseEnabled())
							logger.verbose("NWCGRefurbHelper::--------------------------------------");
						NodeList nlItem = elemWOComponent
								.getElementsByTagName("Item");
						if (nlItem != null && nlItem.getLength() > 0) {
							elemWOComponent.removeChild(nlItem.item(0));
						}
						// confirm work order activity understand Quantity
						// attribute not ComponentQuantity attribute
						// for the serial numbers this should be always 1
						if (logger.isVerboseEnabled())
							logger.verbose("prepareConfirmWorderOrderActivityComponentXML :: strSerialNo --"
									+ strSerialNo + "--");
						if (logger.isVerboseEnabled())
							logger.verbose("prepareConfirmWorderOrderActivityComponentXML :: ComponentQuantity --"
									+ elemWOComponent
											.getAttribute("ComponentQuantity")
									+ "--");
						if (strSerialNo.equals(""))
							elemWOComponent
									.setAttribute(
											"Quantity",
											StringUtil.nonNull(elemWOComponent
													.getAttribute("ComponentQuantity")));
						else
							elemWOComponent.setAttribute("Quantity", "1");
					}
				}

			}
		}
		return confrmWODoc;
	}

	private static ArrayList getMissingComponentsWhichAreNotSeriallyTrackedKeyList(
			Element elemUserInputWO) {
		return (ArrayList) NWCGRefurbHelper.getComponentsList(elemUserInputWO,
				true, "OldSerialNo", null, "4");
	}

	private static ArrayList getElementsToBeDeletedFromParam(
			Element documentElement, ArrayList alMissingComponents) {

		// doesnt matter the last param wont come into the picture
		return (ArrayList) NWCGRefurbHelper.getComponentsList(documentElement,
				true, "SerialNo", alMissingComponents, "");
	}

	/*
	 * get the virtual location i.e. Loc#2 from the Loc# 1, this is configured
	 * in the common code
	 */
	protected static String deriveTargetLocationFromSourceLocation(
			YFSEnvironment env, String strSourceLocationID) throws Exception {
		Document doc = XMLUtil.createDocument("CommonCode");
		doc.getDocumentElement().setAttribute("CodeType", "NWCG_RFBVTRLOC");
		doc.getDocumentElement().setAttribute("CodeValue", strSourceLocationID);
		Document outDoc = CommonUtilities.invokeAPI(env, "getCommonCodeList",
				doc);

		String returnVal = XPathUtil.getString(outDoc,
				"/CommonCodeList/CommonCode/@CodeShortDescription");

		return returnVal;
	}

	/*
	 * get the RRP Returns Flag for the Node, This is configured in the common
	 * code
	 */
	public static String get_RRP_Flag(YFSEnvironment env, String cacheID)
			throws Exception {
		Document doc = XMLUtil.createDocument("CommonCode");
		doc.getDocumentElement().setAttribute("CodeType", "NWCG_RRP_RETURN");
		doc.getDocumentElement().setAttribute("CodeValue", cacheID);
		Document outDoc = CommonUtilities.invokeAPI(env, "getCommonCodeList",
				doc);

		String returnVal = XPathUtil.getString(outDoc,
				"/CommonCodeList/CommonCode/@CodeShortDescription");
		if (returnVal.length() == 0) {
			returnVal = "NO";
		}
		return returnVal;
	}

	protected static Document getRefurbishmentDekittingWorkOrderOrderXML(
			YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine,
			String strItemID, String strUOM, String strPC,
			String strRequestQty, String strNode) throws Exception {
		Document docWorkOrder = XMLUtil.createDocument("WorkOrder");
		Element elemWorkOrder = docWorkOrder.getDocumentElement();

		Document docWorkOrderForOutput = XMLUtil.createDocument("WorkOrder");
		Element elemWorkOrderForOutput = docWorkOrderForOutput
				.getDocumentElement();

		elemWorkOrderForOutput.setAttribute("SerialNo",
				elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo"));
		elemWorkOrderForOutput.setAttribute("NewNodeKey", strNode);

		elemWorkOrder.setAttribute("SerialNo",
				elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo"));
		elemWorkOrder.setAttribute("ItemID", strItemID);
		elemWorkOrder.setAttribute("ProductClass", strPC);
		elemWorkOrder.setAttribute("Uom", strUOM);
		elemWorkOrder.setAttribute("NodeKey", strNode);

		// elemWorkOrder.setAttribute("QuantityRequested",strRequestQty);

		Element elemWorkOrderTag = docWorkOrder.createElement("WorkOrderTag");
		boolean bAttributeExists = false;

		String strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("BatchNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("BatchNo", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerName");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute1", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("OwnerUnitID");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute2", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerModel");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute3", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("LotKeyReference");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotKeyReference", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("LotNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotNumber", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("BatchNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("BatchNo", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturingDate");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("ManufacturingDate", strTemp);
		}

		if (bAttributeExists)
			elemWorkOrder.appendChild(elemWorkOrderTag);

		// As per discussion with Dave : 06/26 - the user may not carry the same
		// values for them
		// do not consider ship by date and revision number for getting the work
		// order but mark them while creating the dekitting WO
		// if(logger.isVerboseEnabled())
		// logger.verbose("NWCGRefurbHelper::I made it to just before getLatestWorkOrder");
		// if(logger.isVerboseEnabled())
		// logger.verbose("NWCGRefurbHelper::*************************************");

		// Document docWO = getLatestWorkOrder(env,docWorkOrder);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::*************************************");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::docWorkOrderForOutput is: \n"
					+ XMLUtil.getXMLString(docWorkOrderForOutput));
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::*************************************");

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::\n");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::\n");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::About to invoke NWCGgetWorkOrder\n");

		Document docWO = CommonUtilities.invokeService(env,
				"NWCGgetWorkOrderListService", docWorkOrderForOutput);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::*************************************");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::docWO is: \n"
					+ XMLUtil.getXMLString(docWO));
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::*************************************");

		Element elemWO = docWO.getDocumentElement();

		NodeList nlWorkOrderTag = docWorkOrder
				.getElementsByTagName("WorkOrderTag");

		if (nlWorkOrderTag != null && nlWorkOrderTag.getLength() > 0) {
			elemWorkOrderTag = (Element) nlWorkOrderTag.item(0);

			strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			if (strTemp != null && (!strTemp.equals(""))) {
				elemWorkOrderTag.setAttribute("RevisionNo", strTemp);
			}
		} else {
			elemWorkOrderTag = docWO.createElement("WorkOrderTag");

			strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			if (strTemp != null && (!strTemp.equals(""))) {
				elemWO.appendChild(elemWorkOrderTag);
				elemWorkOrderTag.setAttribute("RevisionNo", strTemp);
			}

		}
		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("ShipByDate");
		if (strTemp != null && (!strTemp.equals(""))) {
			elemWO.setAttribute("ShipByDate", CommonUtilities
					.changeDateFormat(StringUtil.nonNull(strTemp)));
		}

		elemWO.setAttribute("QuantityRequested", strRequestQty);
		elemWO.setAttribute("ServiceItemGroupCode", "DKIT");
		elemWO.setAttribute("ServiceItemID", "REFURB-DEKITTING");

		Element elemWorkOrderActivities = docWO
				.createElement("WorkOrderActivities");
		elemWO.appendChild(elemWorkOrderActivities);

		Element elemWorkOrderActivity = docWO
				.createElement("WorkOrderActivity");
		elemWorkOrderActivities.appendChild(elemWorkOrderActivity);

		elemWorkOrderActivity.setAttribute("ActivityCode", "REFURBISHMENT");
		elemWorkOrderActivity.setAttribute("ActivitySeqNo", "1");

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getRefurbishmentWorkOrderOrderXML :: "
					+ XMLUtil.getXMLString(docWO));
		return docWO;
	}

	/*
	 * purely mapping method
	 */
	protected static Document prepareConfirmWorkderOrderActivityXML(
			Document docNWCGgetWorkOrder, Element elemNWCGMasterWorkOrderLine,
			Document createWODoc, String strActivityCode) throws Exception {

		Element rtdocNWCGgetWorkOrder = null;
		Element elemConfirmWOTag = null;
		Document rt_WorkOrder = XMLUtil.getDocument();
		Element elemWOCreated = createWODoc.getDocumentElement();
		if (docNWCGgetWorkOrder != null) {

			rtdocNWCGgetWorkOrder = docNWCGgetWorkOrder.getDocumentElement();
			// nldocNWCGgetWorkOrderSerialDetail =
			// rtdocNWCGgetWorkOrder.getElementsByTagName("SerialDetail");
			NodeList nldocNWCGgetWorkOrderTagAttributes = rtdocNWCGgetWorkOrder
					.getElementsByTagName("WorkOrderTag");

			if (nldocNWCGgetWorkOrderTagAttributes != null
					&& nldocNWCGgetWorkOrderTagAttributes.getLength() > 0) {
				Element elemWOTag = (Element) nldocNWCGgetWorkOrderTagAttributes
						.item(0);
				elemConfirmWOTag = (Element) rt_WorkOrder.importNode(elemWOTag,
						true);
			}
		}

		Element el_WorkOrder = rt_WorkOrder.createElement("WorkOrder");
		rt_WorkOrder.appendChild(el_WorkOrder);
		el_WorkOrder.setAttribute("EnterpriseCode",
				NWCGConstants.ENTERPRISE_CODE);
		el_WorkOrder.setAttribute("NodeKey",
				elemWOCreated.getAttribute("NodeKey"));
		el_WorkOrder.setAttribute("IgnoreOrdering", "Y");
		el_WorkOrder.setAttribute("WorkOrderKey",
				elemWOCreated.getAttribute("WorkOrderKey"));
		el_WorkOrder.setAttribute("WorkOrderNo",
				elemWOCreated.getAttribute("WorkOrderNo"));
		el_WorkOrder.setAttribute("ShipByDate", CommonUtilities
				.changeDateFormat(StringUtil.nonNull(elemWOCreated
						.getAttribute("ShipByDate"))));

		Element el_WorkOrderActivityDtl = rt_WorkOrder
				.createElement("WorkOrderActivityDtl");
		el_WorkOrder.appendChild(el_WorkOrderActivityDtl);
		el_WorkOrderActivityDtl.setAttribute("ActivityCode", strActivityCode);
		el_WorkOrderActivityDtl.setAttribute("ActivityLocationId",
				elemNWCGMasterWorkOrderLine.getAttribute("LocationID"));
		el_WorkOrderActivityDtl.setAttribute("EndTimeStamp", "");
		el_WorkOrderActivityDtl.setAttribute("QuantityBeingConfirmed",
				elemWOCreated.getAttribute("QuantityRequested"));
		el_WorkOrderActivityDtl.setAttribute("SerialNo", StringUtil
				.nonNull(elemNWCGMasterWorkOrderLine
						.getAttribute("PrimarySerialNo")));
		el_WorkOrderActivityDtl.setAttribute("StartTimeStamp", "");

		if (elemConfirmWOTag != null) {
			el_WorkOrderActivityDtl.appendChild(elemConfirmWOTag);
		}

		Element elemWOSerail = (Element) XPathUtil.getNode(docNWCGgetWorkOrder,
				"WorkOrder/SerialDetail");
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::elemWOSerail ==> " + elemWOSerail);
		if (elemWOSerail != null) {
			if (logger.isVerboseEnabled())
				logger.verbose(XMLUtil.getElementXMLString(elemWOSerail));
			el_WorkOrderActivityDtl.appendChild(rt_WorkOrder.importNode(
					elemWOSerail, true));
		}
		// jay : this will append serialdetail and tag always at the root level,
		// we need to append the serial in the respective locations
		//
		// Element el_WorkOrderTag=rt_WorkOrder.createElement("WorkOrderTag");
		//
		// if (nldocNWCGgetWorkOrderTagAttributes != null &&
		// nldocNWCGgetWorkOrderTagAttributes.getLength() > 0){
		//
		// Element ele = (Element) nldocNWCGgetWorkOrderTagAttributes.item(0);
		// el_WorkOrderActivityDtl.appendChild(el_WorkOrderTag);
		// el_WorkOrderTag.setAttribute("BatchNo",StringUtil.nonNull(ele.getAttribute("BatchNo")));
		// el_WorkOrderTag.setAttribute("LotAttribute1",StringUtil.nonNull(ele.getAttribute("LotAttribute1")));
		// el_WorkOrderTag.setAttribute("LotAttribute2",StringUtil.nonNull(ele.getAttribute("LotAttribute2")));
		// el_WorkOrderTag.setAttribute("LotAttribute3",StringUtil.nonNull(ele.getAttribute("LotAttribute3")));
		// el_WorkOrderTag.setAttribute("LotNumber",StringUtil.nonNull(ele.getAttribute("LotNumber")));
		// el_WorkOrderTag.setAttribute("RevisionNo",StringUtil.nonNull(ele.getAttribute("RevisionNo")));
		// el_WorkOrderTag.setAttribute("LotKeyReference",StringUtil.nonNull(ele.getAttribute("LotKeyReference")));
		//
		// }

		// Element el_SerialDetail=rt_WorkOrder.createElement("SerialDetail");

		// if (nldocNWCGgetWorkOrderSerialDetail != null &&
		// nldocNWCGgetWorkOrderSerialDetail.getLength() > 0){
		//
		// Element ele = (Element)nldocNWCGgetWorkOrderSerialDetail.item(0);
		// el_WorkOrderActivityDtl.appendChild(el_SerialDetail);
		//
		// el_SerialDetail.setAttribute("SecondarySerial1",StringUtil.nonNull(ele.getAttribute("SecondarySerial1")));
		// el_SerialDetail.setAttribute("SecondarySerial2",StringUtil.nonNull(ele.getAttribute("SecondarySerial2")));
		// el_SerialDetail.setAttribute("SecondarySerial3",StringUtil.nonNull(ele.getAttribute("SecondarySerial3")));
		// el_SerialDetail.setAttribute("SecondarySerial4",StringUtil.nonNull(ele.getAttribute("SecondarySerial4")));
		// el_SerialDetail.setAttribute("SecondarySerial5",StringUtil.nonNull(ele.getAttribute("SecondarySerial5")));
		// el_SerialDetail.setAttribute("SecondarySerial6",StringUtil.nonNull(ele.getAttribute("SecondarySerial6")));
		// el_SerialDetail.setAttribute("SecondarySerial7",StringUtil.nonNull(ele.getAttribute("SecondarySerial7")));
		// el_SerialDetail.setAttribute("SecondarySerial8",StringUtil.nonNull(ele.getAttribute("SecondarySerial8")));
		// el_SerialDetail.setAttribute("SecondarySerial9",StringUtil.nonNull(ele.getAttribute("SecondarySerial9")));
		// el_SerialDetail.setAttribute("SerialNo",StringUtil.nonNull(ele.getAttribute("PrimarySerialNo")));
		//
		//
		// }
		//
		//
		//
		//
		return NWCGRefurbHelper.prepareConfirmWorderOrderActivityComponentXML(
				rt_WorkOrder, elemWOCreated, docNWCGgetWorkOrder);
	}

	/*
	 * for the given move request we need to find the move task
	 */
	protected static String getTaskKeyFromMoveRequestKey(YFSEnvironment env,
			String strMoveRequestKey) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getTaskKeyFromMoveRequestKey :: "
					+ strMoveRequestKey);
		Document rt_Task = XMLUtil.getDocument();

		Element el_Task = rt_Task.createElement("Task");
		rt_Task.appendChild(el_Task);

		// get the non summary task
		el_Task.setAttribute("IsSummaryTask", "N");

		Element el_TaskReferences = rt_Task.createElement("TaskReferences");
		el_Task.appendChild(el_TaskReferences);
		el_TaskReferences.setAttribute("MoveRequestKey", strMoveRequestKey);

		Document doc = CommonUtilities.invokeAPI(env, "getTaskList", rt_Task);
		if (doc != null) {
			if (logger.isVerboseEnabled())
				logger.verbose("NWCGRefurbHelper::getTaskList:: output "
						+ XMLUtil.getXMLString(doc));
			Element elem = doc.getDocumentElement();
			if (elem != null) {
				NodeList nl = elem.getElementsByTagName("Task");
				if (nl != null && nl.getLength() > 0) {
					for (int index = 0; index < nl.getLength(); index++) {
						Element elemTask = (Element) nl.item(index);
						String strParentTaskId = elemTask
								.getAttribute("ParentTaskId");
						if (strParentTaskId != null
								&& (!strParentTaskId.equals(""))) {
							return elemTask.getAttribute("TaskKey");
						}

					}
				}
			}
		}
		// task has got some exceptions - may be some config issue
		throw new NWCGException(
				"Task Does not exist - check the move request with key "
						+ strMoveRequestKey);
	}

	protected static Document getCompleteTaskXML(YFSEnvironment env,
			Element elemnlNWCGMasterWorkOrderLine, String strMoveRequestKey,
			String strItemID, String strPC, String strUOM, String strNode,
			String strSourceLocationID, String strUserID,
			String strDestinationStatus) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::getCompleteTaskXML :: ");

		String strTaskKey = NWCGRefurbHelper.getTaskKeyFromMoveRequestKey(env,
				strMoveRequestKey);

		Document rt_Task = XMLUtil.getDocument();

		Element el_Task = rt_Task.createElement("Task");
		rt_Task.appendChild(el_Task);
		el_Task.setAttribute("AssignedToUserId", strUserID);
		el_Task.setAttribute("EnterpriseKey", NWCGConstants.ENTERPRISE_CODE);
		el_Task.setAttribute("IgnoreOrdering", "Y");
		// el_Task.setAttribute("OrganizationCode",strNode);
		el_Task.setAttribute("TaskKey", strTaskKey);
		el_Task.setAttribute("HoldReasonCode", "");
		el_Task.setAttribute("ReasonText", "REFURB MOVE TASK ADJUSTMENT");
		el_Task.setAttribute("ReasonCode",
				NWCGConstants.RFB_ADJUSTMENT_REASON_CODE);
		el_Task.setAttribute("SourceLocationId", strSourceLocationID);
		el_Task.setAttribute("TargetLocationId", NWCGRefurbHelper
				.deriveTargetLocationFromSourceLocation(env,
						strSourceLocationID));

		Element el_Inventory = rt_Task.createElement("Inventory");
		el_Task.appendChild(el_Inventory);
		el_Inventory.setAttribute("InventoryStatus", strDestinationStatus);
		el_Inventory.setAttribute("ItemId", strItemID);
		el_Inventory.setAttribute("Node", strNode);
		// el_Inventory.setAttribute("Quantity","");
		el_Inventory.setAttribute("UnitOfMeasure", strUOM);

		Element el_TagAttributes = rt_Task.createElement("TagAttributes");

		// make sure we attach any attribute if and only if the attribute value
		// exists or the element has got any attribute failing which
		// the wont process the move task
		String strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("BatchNo"));
		boolean bValueSet = false;

		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("BatchNo", strValue);
		}
		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerName"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("LotAttribute1", strValue);
		}

		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("OwnerUnitID"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("LotAttribute2", strValue);
		}

		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerModel"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("LotAttribute3", strValue);
		}
		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("LotKeyReference"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("LotKeyReference", strValue);
		}
		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("LotNo"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("LotNumber", strValue);
		}

		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturingDate"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("ManufacturingDate", strValue);
		}

		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("RevisionNo"));
		if (!strValue.equals("")) {
			bValueSet = true;
			el_TagAttributes.setAttribute("RevisionNo", strValue);
		}
		if (bValueSet) {
			// append the element if and only if any one of the attribute is set
			el_Inventory.appendChild(el_TagAttributes);
		}

		Element el_SerialNumberDetails = rt_Task
				.createElement("SerialNumberDetails");

		strValue = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine
				.getAttribute("PrimarySerialNo"));

		if (!strValue.equals("")) {
			Element el_SerialNumberDetail = rt_Task
					.createElement("SerialNumberDetail");
			el_SerialNumberDetails.appendChild(el_SerialNumberDetail);
			el_Inventory.appendChild(el_SerialNumberDetails);
			el_SerialNumberDetail.setAttribute("SerialNo", strValue);
		}
		return rt_Task;

	}

	public static Element removeChildElementsFromXML(Element elem,
			String strChildElementName) {
		NodeList nlWOC = elem.getElementsByTagName(strChildElementName);

		ArrayList al = new ArrayList();
		if (nlWOC != null && nlWOC.getLength() > 0) {
			for (int index = 0; index < nlWOC.getLength(); index++) {
				Element elemWOC = (Element) nlWOC.item(index);
				al.add(elemWOC);
			}// end for
		}// end if

		Iterator itr = al.iterator();
		while (itr.hasNext()) {
			Element elemTemp = (Element) itr.next();
			elemTemp.getParentNode().removeChild(elemTemp);
		}

		return elem;
	}

	public static void appendChildElements(Element elem, ArrayList al) {
		Iterator itr = al.iterator();
		while (itr.hasNext()) {
			Element elemTemp = (Element) itr.next();

			elem.appendChild(elem.getOwnerDocument().importNode(elemTemp, true));
		}
	}

	public static void replaceChildElementsFromArray(Element elem,
			ArrayList al, String strChildElementName) {
		removeChildElementsFromXML(elem, strChildElementName);
		appendChildElements(elem, al);
	}

	public static HashMap prepareMapOfComponentItemAndComponentKey(
			Document confrmWODoc) throws Exception {
		HashMap hm = new HashMap();

		Element elemWOCs = (Element) XPathUtil.getNode(confrmWODoc,
				"WorkOrder/WorkOrderActivityDtl/WorkOrderComponents");

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGRefurbHelper::prepareMapOfComponentItemAndComponentKey "
					+ XMLUtil.getElementXMLString(elemWOCs));

		NodeList nl = elemWOCs.getElementsByTagName("WorkOrderComponent");

		if (nl != null && nl.getLength() > 0) {
			for (int index = 0; index < nl.getLength(); index++) {
				Element elem = (Element) nl.item(index);
				hm.put("" + elem.getAttribute("ItemID")
						+ elem.getAttribute("Uom"),
						elem.getAttribute("WorkOrderComponentKey") + "");
			}
		}

		return hm;
	}

	public static void adjustLocationInventory(YFSEnvironment env,
			String strNode, String strLocationID, String strInventoryStatus,
			Element elem, String strQty, String strShipByDate,
			String strItemID, String strPC, String strUOM) throws Exception {
		Document rt_AdjustLocationInventory = XMLUtil.getDocument();

		Element el_AdjustLocationInventory = rt_AdjustLocationInventory
				.createElement("AdjustLocationInventory");
		rt_AdjustLocationInventory.appendChild(el_AdjustLocationInventory);
		el_AdjustLocationInventory.setAttribute("EnterpriseCode",
				NWCGConstants.ENTERPRISE_CODE);
		el_AdjustLocationInventory.setAttribute("IgnoreOrdering", "Y");
		el_AdjustLocationInventory.setAttribute("Node", strNode);

		Element el_Source = rt_AdjustLocationInventory.createElement("Source");
		el_AdjustLocationInventory.appendChild(el_Source);
		el_Source.setAttribute("LocationId", strLocationID);

		Element el_Inventory = rt_AdjustLocationInventory
				.createElement("Inventory");
		el_Source.appendChild(el_Inventory);

		el_Inventory.setAttribute("InventoryStatus", strInventoryStatus);
		el_Inventory.setAttribute("QtyQryType", "-");
		el_Inventory.setAttribute("Quantity", strQty);
		el_Inventory.setAttribute("ShipByDate", CommonUtilities
				.changeDateFormat(StringUtil.nonNull(strShipByDate)));

		Element el_InventoryItem = rt_AdjustLocationInventory
				.createElement("InventoryItem");
		el_Inventory.appendChild(el_InventoryItem);
		el_InventoryItem.setAttribute("ItemID", strItemID);
		el_InventoryItem.setAttribute("ProductClass", strPC);
		el_InventoryItem.setAttribute("UnitOfMeasure", strUOM);

		Element el_Audit = rt_AdjustLocationInventory.createElement("Audit");
		el_AdjustLocationInventory.appendChild(el_Audit);
		el_Audit.setAttribute("ReasonCode",
				NWCGConstants.RFB_ADJUSTMENT_REASON_CODE);
		el_Audit.setAttribute("ReasonText",
				"ADJUST LOCATION INVENTORY FOR REFURBISHMENT");

		Element el_SerialList = rt_AdjustLocationInventory
				.createElement("SerialList");

		Element el_SerialDetail = rt_AdjustLocationInventory
				.createElement("SerialDetail");
		el_SerialList.appendChild(el_SerialDetail);
		el_SerialDetail.setAttribute("Quantity", strQty);

		String str = StringUtil.nonNull(elem.getAttribute("SerialNo"));

		if (str.equals(""))
			str = StringUtil.nonNull(elem.getAttribute("PrimarySerialNo"));

		if (!str.equals("")) {
			el_Inventory.appendChild(el_SerialList);

			el_SerialDetail.setAttribute("SerialNo", str);

			el_SerialDetail.setAttribute("SecondarySerial1",
					elem.getAttribute("SecondarySerialNo1"));
			el_SerialDetail.setAttribute("SecondarySerial2",
					elem.getAttribute("SecondarySerialNo2"));
			el_SerialDetail.setAttribute("SecondarySerial3",
					elem.getAttribute("SecondarySerialNo3"));
			el_SerialDetail.setAttribute("SecondarySerial4",
					elem.getAttribute("SecondarySerialNo4"));
			el_SerialDetail.setAttribute("SecondarySerial5",
					elem.getAttribute("SecondarySerialNo5"));
			el_SerialDetail.setAttribute("SecondarySerial6",
					elem.getAttribute("SecondarySerialNo6"));
			el_SerialDetail.setAttribute("SecondarySerial7",
					elem.getAttribute("SecondarySerialNo7"));
			el_SerialDetail.setAttribute("SecondarySerial8",
					elem.getAttribute("SecondarySerialNo8"));
			el_SerialDetail.setAttribute("SecondarySerial9",
					elem.getAttribute("SecondarySerialNo9"));
		}

		Element el_TagDetail = rt_AdjustLocationInventory
				.createElement("TagDetail");
		el_Inventory.appendChild(el_TagDetail);

		NodeList nlWorkOrderComponentTag = elem
				.getElementsByTagName("WorkOrderComponentTag");
		Element elemWorkOrderComponentTag = null;
		if (nlWorkOrderComponentTag != null
				&& nlWorkOrderComponentTag.getLength() > 0) {
			elemWorkOrderComponentTag = (Element) nlWorkOrderComponentTag
					.item(0);
		} else {
			elemWorkOrderComponentTag = rt_AdjustLocationInventory
					.createElement("WorkOrderComponentTag");
		}

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("LotAttribute1"));

		if (str.equals(""))
			el_TagDetail.setAttribute("LotAttribute1",
					StringUtil.nonNull(elem.getAttribute("ManufacturerName")));
		else
			el_TagDetail.setAttribute("LotAttribute1", str);

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("LotAttribute2"));
		if (str.equals(""))
			el_TagDetail.setAttribute("LotAttribute2",
					StringUtil.nonNull(elem.getAttribute("OwnerUnitID")));
		else
			el_TagDetail.setAttribute("LotAttribute2", str);

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("LotAttribute3"));
		if (str.equals(""))
			el_TagDetail.setAttribute("LotAttribute3",
					StringUtil.nonNull(elem.getAttribute("ManufacturerModel")));
		else
			el_TagDetail.setAttribute("LotAttribute3", str);

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("LotNumber"));
		if (str.equals(""))
			el_TagDetail.setAttribute("LotNumber",
					StringUtil.nonNull(elem.getAttribute("LotNo")));
		else
			el_TagDetail.setAttribute("LotNumber", str);

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("RevisionNo"));
		if (str.equals(""))
			el_TagDetail.setAttribute("RevisionNo",
					StringUtil.nonNull(elem.getAttribute("RevisionNo")));
		else
			el_TagDetail.setAttribute("RevisionNo", str);

		str = StringUtil.nonNull(elemWorkOrderComponentTag
				.getAttribute("LotKeyReference"));
		if (str.equals(""))
			el_TagDetail.setAttribute("LotKeyReference",
					StringUtil.nonNull(elem.getAttribute("LotKeyReference")));
		else
			el_TagDetail.setAttribute("LotKeyReference", str);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGProcessRefurbishmentOrders::el_AdjustLocationInventory :: "
					+ XMLUtil.getXMLString(el_AdjustLocationInventory
							.getOwnerDocument()));

		CommonUtilities.invokeAPI(env, "adjustLocationInventory",
				rt_AdjustLocationInventory);

	}

	public static void adjustInventoryFromMap(YFSEnvironment env,
			String strNode, String strLocation, Map map) throws Exception {
		if (map != null && map.entrySet() != null) {
			Iterator itrHmKey = map.entrySet().iterator();
			while (itrHmKey.hasNext()) {

				Map.Entry ele = (Map.Entry) itrHmKey.next();
				Element elem = (Element) ele.getValue();

				adjustLocationInventory(env, strNode, strLocation,
						NWCGConstants.NWCG_NRFI_DISPOSITION_CODE, elem,
						elem.getAttribute("ComponentQuantity"),
						elem.getAttribute("ShipByDate"),
						elem.getAttribute("ItemID"),
						elem.getAttribute("ProductClass"),
						elem.getAttribute("Uom"));

			}
		}
	}

	protected static Document getRefurbishmentWorkOrderOrderXML(
			YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine,
			String strItemID, String strUOM, String strPC,
			String strRequestQty, String strNode) throws Exception {
		Document docWorkOrder = XMLUtil.createDocument("WorkOrder");
		Element elemWorkOrder = docWorkOrder.getDocumentElement();

		Document docWorkOrderForOutput = XMLUtil.createDocument("WorkOrder");
		Element elemWorkOrderForOutput = docWorkOrderForOutput
				.getDocumentElement();

		elemWorkOrderForOutput.setAttribute("SerialNo",
				elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo"));
		elemWorkOrderForOutput.setAttribute("NewNodeKey", strNode);

		elemWorkOrder.setAttribute("SerialNo",
				elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo"));
		elemWorkOrder.setAttribute("ItemID", strItemID);
		elemWorkOrder.setAttribute("ProductClass", strPC);
		elemWorkOrder.setAttribute("Uom", strUOM);
		elemWorkOrder.setAttribute("NodeKey", strNode);

		Element elemWorkOrderTag = docWorkOrder.createElement("WorkOrderTag");
		boolean bAttributeExists = false;

		String strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("BatchNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("BatchNo", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerName");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute1", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("OwnerUnitID");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute2", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturerModel");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotAttribute3", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("LotKeyReference");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotKeyReference", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("LotNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("LotNumber", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("BatchNo");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("BatchNo", strTemp);
		}

		strTemp = elemnlNWCGMasterWorkOrderLine
				.getAttribute("ManufacturingDate");
		if (strTemp != null && (!strTemp.equals(""))) {
			bAttributeExists = true;
			elemWorkOrderTag.setAttribute("ManufacturingDate", strTemp);
		}

		if (bAttributeExists)
			elemWorkOrder.appendChild(elemWorkOrderTag);

		Document docWO = CommonUtilities.invokeService(env,
				"NWCGgetWorkOrderListService", docWorkOrderForOutput);
		Element elemWO = docWO.getDocumentElement();

		NodeList nlWorkOrderTag = docWorkOrder
				.getElementsByTagName("WorkOrderTag");

		if (nlWorkOrderTag != null && nlWorkOrderTag.getLength() > 0) {
			elemWorkOrderTag = (Element) nlWorkOrderTag.item(0);

			strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			if (strTemp != null && (!strTemp.equals(""))) {
				elemWorkOrderTag.setAttribute("RevisionNo", strTemp);
			}
		} else {
			elemWorkOrderTag = docWO.createElement("WorkOrderTag");

			strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			if (strTemp != null && (!strTemp.equals(""))) {
				elemWO.appendChild(elemWorkOrderTag);
				elemWorkOrderTag.setAttribute("RevisionNo", strTemp);
			}

		}
		strTemp = elemnlNWCGMasterWorkOrderLine.getAttribute("ShipByDate");
		if (strTemp != null && (!strTemp.equals(""))) {
			elemWO.setAttribute("ShipByDate", CommonUtilities
					.changeDateFormat(StringUtil.nonNull(strTemp)));
		}
		return docWO;
	}

}
