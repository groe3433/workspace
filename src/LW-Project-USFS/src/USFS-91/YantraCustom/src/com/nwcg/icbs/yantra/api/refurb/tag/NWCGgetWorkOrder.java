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

package com.nwcg.icbs.yantra.api.refurb.tag;

import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGgetWorkOrder implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGgetWorkOrder.class);

	public Document getWorkOrder(YFSEnvironment env, Document inDoc)
			throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("Now in getWorkOrder...");
		if (logger.isVerboseEnabled())
			logger.verbose("Input doc to NWCGgetWorkOrder is...\n*************************************\n"
					+ XMLUtil.getXMLString(inDoc));
		// inDoc = makeInputDoc("000340");
		try {
			Element rootelem = inDoc.getDocumentElement();
			String NewNode = rootelem.getAttribute("NewNodeKey");
			rootelem.setAttribute("ParentSerialNo",
					rootelem.getAttribute("SerialNo"));
			// Added this to make sure we get the latest record
			Element el_OrderBy = inDoc.createElement("OrderBy");
			rootelem.appendChild(el_OrderBy);

			Element el_Attribute = inDoc.createElement("Attribute");
			el_OrderBy.appendChild(el_Attribute);
			el_Attribute.setAttribute("Name", "Modifyts");
			el_Attribute.setAttribute("Desc", "Y");

			Document docLatestWOKey = getLatestWOKey(inDoc, env);
			Document docFormatted = getWorkOrderComponents(docLatestWOKey,
					NewNode, env);
			return docFormatted;
		} catch (Exception e) {
			throw new NWCGException("NWCG_WORK_ORDER_TAG_SERIAL_KIT_LIST_NULL");
		}

	}

	public Document getLatestWOKey(Document inXML, YFSEnvironment env)
			throws Exception {

		/*
		 * This method will return a document that is in the following format:
		 * 
		 * <WorkOrder WorkOrderKey="" />
		 * 
		 * Where the WorkOrderKey is the key from the most recent work order
		 * returned from the NWCGGetWorkOrderSerialKitListService when passed a
		 * serial number in the inXML.
		 * 
		 * If there is no matching WorkOrder found when searching by serial
		 * number, an error is thrown.
		 */
		Element serialelem = inXML.getDocumentElement();
		String ParentSerial = serialelem.getAttribute("SerialNo");
		Document doc = CommonUtilities.invokeService(env,
				"NWCGGetWorkOrderSerialKitListService", inXML);
		Element returnElement = null;

		if (doc == null) {
			throw new NWCGException("NWCG_WORK_ORDER_TAG_SERIAL_KIT_LIST_NULL");
			// throw an error here
		} else {
			// if(logger.isVerboseEnabled())
			// logger.verbose("Printing the xml returned from NWCGGetWorkOrderSerialKitList...");
			// if(logger.isVerboseEnabled())
			// logger.verbose(XMLUtil.getXMLString(doc));
			Element ele = doc.getDocumentElement();
			NodeList nlNWCGWorkOrderSerialKit = ele
					.getElementsByTagName("NWCGWorkOrderSerialKit");
			if (nlNWCGWorkOrderSerialKit != null
					|| nlNWCGWorkOrderSerialKit.getLength() >= 1) {
				/*
				 * Commented by GN - 08/09/10. Added Order By Clause
				 * SimpleDateFormat sdf = new
				 * SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ssZ"); Date
				 * dLatestModifyts = null ; for(int index = 0 ; index <
				 * nlNWCGWorkOrderSerialKit.getLength() ; index ++) { Element
				 * elemWO = (Element) nlNWCGWorkOrderSerialKit.item(index);
				 * String str1 = elemWO.getAttribute("Modifyts"); str1 =
				 * str1.substring(0,str1.lastIndexOf(":"))+
				 * str1.substring(str1.lastIndexOf(":")+1,str1.length());
				 * if(index == 0) { dLatestModifyts = sdf.parse(str1);
				 * returnElement = elemWO; } else { Date dModifyts =
				 * sdf.parse(str1); if(dModifyts.after(dLatestModifyts)) {
				 * dLatestModifyts = dModifyts ; returnElement = elemWO; }
				 * }//else }//for //if(logger.isVerboseEnabled())
				 * logger.verbose("The WorkOrderKey: " +
				 * returnElement.getAttribute("WorkOrderKey"));
				 */
				Element elemWO = (Element) nlNWCGWorkOrderSerialKit.item(0);
				returnElement = elemWO;
			}
		}// else

		Document docWorkOrder = XMLUtil.createDocument("WorkOrder");
		docWorkOrder.createAttribute("WorkOrderKey");
		Element ele = docWorkOrder.getDocumentElement();
		ele.setAttribute("WorkOrderKey",
				returnElement.getAttribute("WorkOrderKey"));
		ele.setAttribute("ParentSerialNo", ParentSerial);
		return docWorkOrder;
	}// end checkForMatchingSerial

	public Document getWorkOrderComponents(Document inDoc, String NewNode,
			YFSEnvironment env) throws Exception {

		/*
		 * Build a Document that comprises all the work order elements and work
		 * order components in the following format:
		 * 
		 * 
		 * <WorkOrder DocumentType="7001" EnterpriseCode="NWCG" ItemID="000960"
		 * NodeKey="CORMK" ProductClass="Supply" SellerOrganizationCode=""
		 * SerialNo="" Uom="KT"> <WorkOrderTag BatchNo=”” LotAttribute1=””
		 * LotAttribute2=”” LotAttribute3=”” LotKeyReference=”” LotNumber=””
		 * BatchNo=”” ManufacturingDate=””/> <SerialDetail SerialNo=””
		 * SecondarySerial1=”” SecondarySerial2=”” SecondarySerial3=””
		 * SecondarySerial4=”” SecondarySerial5=”” SecondarySerial6=””/>
		 * <WorkOrderComponents> <WorkOrderComponent ComponentQuantity="1.00"
		 * ItemID="000070" ProductClass="Supply" SerialNo=""
		 * ShipByDate="2500-01-01" Uom="EA" > <WorkOrderComponentTag BatchNo=""
		 * LotAttribute1="" LotAttribute2="" LotAttribute3="" LotKeyReference=""
		 * LotNumber="" ManufacturingDate="" RevisionNo=""/> <SerialDetail
		 * SerialNo=”” SecondarySerial1=”” SecondarySerial2=””
		 * SecondarySerial3=”” SecondarySerial4=”” SecondarySerial5=””
		 * SecondarySerial6=””/> <WorkOrderComponent/> </WorkOrderComponents>
		 * </WorkOrder>
		 */
		Document outXML = XMLUtil.createDocument("WorkOrder");
		Element eleOutXMLWorkOrder = outXML.getDocumentElement();
		// Element eleOutXMLWorkOrder = outXML.createElement("WorkOrder");
		// eleOutXMLRoot.appendChild(eleOutXMLWorkOrder);

		Document docNWCGWorkOrderSerialKitList = CommonUtilities.invokeService(
				env, "NWCGGetWorkOrderSerialKitListService", inDoc);
		// if(logger.isVerboseEnabled())
		// logger.verbose("Output from getWorkOrderComponents is: " +
		// XMLUtil.getXMLString(docNWCGWorkOrderSerialKitList));

		Element root = docNWCGWorkOrderSerialKitList.getDocumentElement();
		NodeList nlNWCGWorkOrderSerialKit = root
				.getElementsByTagName("NWCGWorkOrderSerialKit");
		if (nlNWCGWorkOrderSerialKit != null
				|| nlNWCGWorkOrderSerialKit.getLength() >= 1) {

			int index = 0;
			Element ele = null;
			for (index = 0; index < nlNWCGWorkOrderSerialKit.getLength(); index++) {
				ele = (Element) nlNWCGWorkOrderSerialKit.item(index);
				if (logger.isVerboseEnabled())
					logger.verbose("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				if (logger.isVerboseEnabled())
					logger.verbose(XMLUtil.getElementXMLString(ele));
				if (logger.isVerboseEnabled())
					logger.verbose("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				if (ele.getAttribute("WorkOrderComponentKey") == null
						|| ele.getAttribute("WorkOrderComponentKey").equals("")) {
					if (logger.isVerboseEnabled())
						logger.verbose("*****Work Order Component Key is null or blank ********");
					if (logger.isVerboseEnabled())
						logger.verbose("WorkOrderComponentKey for the component with a serial number is: "
								+ ele.getAttribute("WorkOrderComponentKey"));
					if (logger.isVerboseEnabled())
						logger.verbose("ItemID for the component w a serial number is: "
								+ ele.getAttribute("ItemID"));
					if (logger.isVerboseEnabled())
						logger.verbose("******END BLANK WO COMPONENT KEY**********");
					break;
				}
			}// for
			eleOutXMLWorkOrder.setAttribute("DocumentType",
					ele.getAttribute("DocumentType"));
			eleOutXMLWorkOrder.setAttribute("EnterpriseCode",
					ele.getAttribute("EnterpriseCode"));
			eleOutXMLWorkOrder.setAttribute("ItemID",
					ele.getAttribute("ItemID"));
			eleOutXMLWorkOrder.setAttribute("NodeKey", NewNode);
			eleOutXMLWorkOrder.setAttribute("ProductClass",
					ele.getAttribute("ProductClass"));
			eleOutXMLWorkOrder.setAttribute("SellerOrganizationCode",
					ele.getAttribute("SellerOrganizationCode"));
			eleOutXMLWorkOrder.setAttribute("SerialNo",
					ele.getAttribute("SerialNo"));
			eleOutXMLWorkOrder.setAttribute("Uom",
					ele.getAttribute("UnitOfMeasure"));

			Element eleWorkOrderTag = outXML.createElement("WorkOrderTag");
			boolean bValueSet = false;
			String strTemp = StringUtil.nonNull(ele.getAttribute("BatchNo"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("BatchNo",
						ele.getAttribute("BatchNo"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("LotAttribute1"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("LotAttribute1",
						ele.getAttribute("LotAttribute1"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("RevisionNo"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("RevisionNo",
						ele.getAttribute("RevisionNo"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("LotAttribute2"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("LotAttribute2",
						ele.getAttribute("LotAttribute2"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("LotAttribute3"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("LotAttribute3",
						ele.getAttribute("LotAttribute3"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("LotKeyReference"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("LotKeyReference",
						ele.getAttribute("LotKeyReference"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("LotNumber"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("LotNumber",
						ele.getAttribute("LotNumber"));
				bValueSet = true;
			}

			strTemp = StringUtil.nonNull(ele.getAttribute("ManufacturingDate"));
			if (!strTemp.equals("")) {
				eleWorkOrderTag.setAttribute("ManufacturingDate",
						ele.getAttribute("ManufacturingDate"));
				bValueSet = true;
			}

			if (bValueSet) {

				eleOutXMLWorkOrder.appendChild(eleWorkOrderTag);
				bValueSet = false;
			}

			Element eleWOSerialDetail = outXML.createElement("SerialDetail");

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo1"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial1",
						ele.getAttribute("SecondarySerialNo1"));
				bValueSet = true;
			}

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo2"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial2",
						ele.getAttribute("SecondarySerialNo2"));
				bValueSet = true;
			}

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo3"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial3",
						ele.getAttribute("SecondarySerialNo3"));
				bValueSet = true;
			}

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo4"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial4",
						ele.getAttribute("SecondarySerialNo4"));
				bValueSet = true;
			}

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo5"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial5",
						ele.getAttribute("SecondarySerialNo5"));
				bValueSet = true;
			}

			strTemp = StringUtil
					.nonNull(ele.getAttribute("SecondarySerialNo6"));
			if (!strTemp.equals("")) {
				eleWOSerialDetail.setAttribute("SecondarySerial6",
						ele.getAttribute("SecondarySerialNo6"));
				bValueSet = true;
			}

			if (bValueSet) {

				eleOutXMLWorkOrder.appendChild(eleWOSerialDetail);
				bValueSet = false;
			}
			Element eleWOComponents = outXML
					.createElement("WorkOrderComponents");
			eleOutXMLWorkOrder.appendChild(eleWOComponents);

			for (int y = 0; y < nlNWCGWorkOrderSerialKit.getLength(); y++) {
				ele = (Element) nlNWCGWorkOrderSerialKit.item(y);
				String strWorkOrderComponentKey = ele
						.getAttribute("WorkOrderComponentKey");
				if (!(strWorkOrderComponentKey.equals(""))) {
					// if(logger.isVerboseEnabled())
					// logger.verbose("WorkOrderComponentKey being added: " +
					// ele.getAttribute("WorkOrderComponentKey"));
					// if(logger.isVerboseEnabled())
					// logger.verbose("ItemID for the component being added: " +
					// ele.getAttribute("ItemID"));
					Element eleWOComponent = outXML
							.createElement("WorkOrderComponent");
					String strItemID = "";
					String strComponentSerialNumber = "";
					strTemp = StringUtil.nonNull(ele.getAttribute("Quantity"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("ComponentQuantity",
								ele.getAttribute("Quantity"));
						bValueSet = true;
					}
					strTemp = StringUtil.nonNull(ele.getAttribute("ItemID"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("ItemID",
								ele.getAttribute("ItemID"));
						strItemID = ele.getAttribute("ItemID");
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("ProductClass"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("ProductClass",
								ele.getAttribute("ProductClass"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele.getAttribute("SerialNo"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("SerialNo",
								ele.getAttribute("SerialNo"));
						strComponentSerialNumber = ele.getAttribute("SerialNo");
						bValueSet = true;
					}

					strTemp = StringUtil
							.nonNull(ele.getAttribute("ShipByDate"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("ShipByDate",
								ele.getAttribute("ShipByDate"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("UnitOfMeasure"));
					if (!strTemp.equals("")) {
						eleWOComponent.setAttribute("Uom",
								ele.getAttribute("UnitOfMeasure"));
						bValueSet = true;
					}

					if (bValueSet) {
						eleWOComponents.appendChild(eleWOComponent);
						bValueSet = false;
					}
					Element eleWorkOrderComponentTag = outXML
							.createElement("WorkOrderComponentTag");
					if (!strComponentSerialNumber.equals("")) {
						if (logger.isVerboseEnabled())
							logger.verbose("In !strComponentSerialNumber.equals('')");
						Document docInputItemlList = XMLUtil
								.createDocument("Item");
						Element eleInputItemlList = docInputItemlList
								.getDocumentElement();
						eleInputItemlList.setAttribute("ItemID", strItemID);

						Document docInputSerialList = XMLUtil
								.createDocument("Serial");
						Element eleRootSerial = docInputSerialList
								.getDocumentElement();
						eleRootSerial.setAttribute("SerialNo",
								strComponentSerialNumber);

						Element el_InvItem = docInputSerialList
								.createElement("InventoryItem");
						eleRootSerial.appendChild(el_InvItem);
						el_InvItem.setAttribute("ItemID", strItemID);

						Document docSerialListOut = CommonUtilities.invokeAPI(
								env, "getSerialList", docInputSerialList);
						Element eleSerialListRoot = docSerialListOut
								.getDocumentElement();
						NodeList nlSerial = eleSerialListRoot
								.getElementsByTagName("Serial");

						String strTagNumber = "";
						if (nlSerial != null && nlSerial.getLength() > 0) {
							Element eleSerial = (Element) nlSerial.item(0);
							strTagNumber = eleSerial.getAttribute("TagNumber");
							// NodeList nlTagDetail =
							// eleSerial.getElementsByTagName("TagDetail");
							// Element eleTagDetail =
							// (Element)nlTagDetail.item(0);
						}
						Document docItemTemplate = XMLUtil
								.createDocument("ItemList");
						Element eleRootItem = docItemTemplate
								.getDocumentElement();
						Element eleItem = docItemTemplate.createElement("Item");
						eleRootItem.appendChild(eleItem);

						Element eleInventoryTagAttributes = docItemTemplate
								.createElement("InventoryTagAttributes");
						eleItem.appendChild(eleInventoryTagAttributes);

						eleItem.setAttribute("BatchNo", "");
						eleItem.setAttribute("ItemKey", "");
						eleItem.setAttribute("ItemTagKey", "");
						eleItem.setAttribute("LotAttribute1", "");
						eleItem.setAttribute("LotAttribute2", "");
						eleItem.setAttribute("LotAttribute3", "");
						eleItem.setAttribute("LotKeyReference", "");
						eleItem.setAttribute("LotNumber", "");
						eleItem.setAttribute("ManufacturingDate", "");
						eleItem.setAttribute("RevisionNo", "");

						if (logger.isVerboseEnabled())
							logger.verbose("docItemTemplate is: "
									+ XMLUtil.getXMLString(docItemTemplate));

						Document docItemListOut = CommonUtilities.invokeAPI(
								env, docItemTemplate, "getItemList",
								docInputItemlList);

						if (logger.isVerboseEnabled())
							logger.verbose("Output from getSerialList is: \n"
									+ XMLUtil.getXMLString(docItemListOut));

						Element eleRootdocItemListOut = docItemListOut
								.getDocumentElement();
						NodeList nleleRootdocItemListOut = eleRootdocItemListOut
								.getElementsByTagName("InventoryTagAttributes");
						String strLotNo = "";
						String strBatchNo = "";
						String strRevisionNo = "";

						if (nleleRootdocItemListOut != null
								&& nleleRootdocItemListOut.getLength() > 0) {
							Element eleTagAttributes = (Element) nleleRootdocItemListOut
									.item(0);
							strLotNo = eleTagAttributes
									.getAttribute("LotNumber");
							strBatchNo = eleTagAttributes
									.getAttribute("BatchNo");
							strRevisionNo = eleTagAttributes
									.getAttribute("RevisionNo");
						}

						StringTokenizer stringTokenizer = new StringTokenizer(
								strTagNumber);

						String strRetrievedLotNo = "";
						String strRetrievedBatchNo = "";
						String strRetrievedRevisionNo = "";

						if (strLotNo.equals("02")) {
							strRetrievedLotNo = stringTokenizer.nextToken();
						}

						if (strBatchNo.equals("02")) {
							strRetrievedBatchNo = stringTokenizer.nextToken();
						}

						if (strRevisionNo.equals("02")) {
							strRetrievedRevisionNo = stringTokenizer
									.nextToken();
						}

						if (logger.isVerboseEnabled())
							logger.verbose("strRetrievedLotNo is: "
									+ strRetrievedLotNo);
						if (logger.isVerboseEnabled())
							logger.verbose("strRetrievedBatchNo is: "
									+ strRetrievedBatchNo);
						if (logger.isVerboseEnabled())
							logger.verbose("strRetrievedRevisionNo is: "
									+ strRetrievedRevisionNo);
						strTemp = strRetrievedBatchNo;
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("BatchNo",
									strRetrievedBatchNo);
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute1"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute1",
									ele.getAttribute("LotAttribute1"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute2"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute2",
									ele.getAttribute("LotAttribute2"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute3"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute3",
									ele.getAttribute("LotAttribute3"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotKeyReference"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotKeyReference",
									ele.getAttribute("LotKeyReference"));
							bValueSet = true;
						}

						strTemp = strRetrievedLotNo;
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("LotNumber",
									strRetrievedLotNo);
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotManufactureDate"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"ManufacturingDate",
									ele.getAttribute("LotManufactureDate"));
							bValueSet = true;
						}

						strTemp = strRetrievedRevisionNo;
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("RevisionNo",
									strRetrievedRevisionNo);
							bValueSet = true;
						}
					} else {

						strTemp = StringUtil.nonNull(ele
								.getAttribute("BatchNo"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("BatchNo",
									ele.getAttribute("BatchNo"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute1"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute1",
									ele.getAttribute("LotAttribute1"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute2"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute2",
									ele.getAttribute("LotAttribute2"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotAttribute3"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotAttribute3",
									ele.getAttribute("LotAttribute3"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotKeyReference"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"LotKeyReference",
									ele.getAttribute("LotKeyReference"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotNumber"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("LotNumber",
									ele.getAttribute("LotNumber"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("LotManufactureDate"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute(
									"ManufacturingDate",
									ele.getAttribute("LotManufactureDate"));
							bValueSet = true;
						}

						strTemp = StringUtil.nonNull(ele
								.getAttribute("RevisionNo"));
						if (!strTemp.equals("")) {
							eleWorkOrderComponentTag.setAttribute("RevisionNo",
									ele.getAttribute("RevisionNo"));
							bValueSet = true;
						}
					}// end else
					if (bValueSet) {

						eleWOComponent.appendChild(eleWorkOrderComponentTag);
						bValueSet = false;
					}

					Element eleSerialDetail = outXML
							.createElement("SerialDetail");

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo1"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial1",
								ele.getAttribute("SecondarySerialNo1"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo2"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial2",
								ele.getAttribute("SecondarySerialNo2"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo3"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial3",
								ele.getAttribute("SecondarySerialNo3"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo4"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial4",
								ele.getAttribute("SecondarySerialNo4"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo5"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial5",
								ele.getAttribute("SecondarySerialNo5"));
						bValueSet = true;
					}

					strTemp = StringUtil.nonNull(ele
							.getAttribute("SecondarySerialNo6"));
					if (!strTemp.equals("")) {
						eleSerialDetail.setAttribute("SecondarySerial6",
								ele.getAttribute("SecondarySerialNo6"));
						bValueSet = true;
					}

					if (bValueSet) {
						eleWOComponent.appendChild(eleSerialDetail);
						bValueSet = false;
					}
				}
			}// for

			if (logger.isVerboseEnabled())
				logger.verbose("output from NWCGgetWorkOrder...\n"
						+ XMLUtil.getXMLString(outXML));
		}// end if
		return outXML;
	}

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}// end class
