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

package com.nwcg.icbs.yantra.api.workorder;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGeneratePutawayRequest implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGGeneratePutawayRequest.class);
	
	public void setProperties(Properties props) throws Exception {
	}

	public Document generatePutawayRequest(YFSEnvironment env, Document inDoc)
			throws Exception {

		Element workOrderElement = inDoc.getDocumentElement();
		String nodeKey = workOrderElement.getAttribute("NodeKey");
		String palletId = workOrderElement.getAttribute("PalletId");
		String itemId = workOrderElement.getAttribute("ItemID");
		String productClass = workOrderElement.getAttribute("ProductClass");
		String unitOfMeasure = workOrderElement.getAttribute("Uom");
		String serviceItemId = workOrderElement.getAttribute("ServiceItemID");
		String enterpriseCode = workOrderElement.getAttribute("EnterpriseCode");
		String workOrderKey = workOrderElement.getAttribute("WorkOrderKey");
		String workOrderNo = workOrderElement.getAttribute("WorkOrderNo");
		int quantityCompleted = (int) Double.parseDouble(workOrderElement
				.getAttribute("QuantityCompleted"));

		Element workOrderActivityDetailElement = (Element) workOrderElement
				.getElementsByTagName("WorkOrderActivityDtl").item(0);
		String sourceLocationId = workOrderActivityDetailElement
				.getAttribute("ActivityLocationId");

		if (serviceItemId.equals("KITTING")) {
			// when n kits are created, there are n <SerialList> elements with
			// one <SerialDetail> child element each
			// as opposed to one <SerialList> element with n <SerialDetail>
			// child elements.
			NodeList serialListElementList = workOrderElement
					.getElementsByTagName("SerialList");
			if (serialListElementList != null
					&& serialListElementList.getLength() > 0) {
				// get serial nos already processed for this work order
				Set<String> existingSerials = getExistingSerials(env, nodeKey,
						workOrderNo, itemId);
				if (existingSerials == null) {
					existingSerials = new HashSet<String>();
				}

				// process trackable kit
				for (int i = 0; i < serialListElementList.getLength(); i++) {
					NodeList serialDetailElementList = ((Element) serialListElementList
							.item(i)).getElementsByTagName("SerialDetail");
					if (serialDetailElementList != null
							&& serialDetailElementList.getLength() > 0) {
						// create move request for serial that is not accounted
						// for yet.
						Element serialDetailElement = (Element) serialDetailElementList
								.item(0);
						String requestQty = serialDetailElement
								.getAttribute("QuantityBeingConfirmed");
						String serialNo = serialDetailElement
								.getAttribute("SerialNo");

						if (!existingSerials.contains(serialNo)) {
							existingSerials.add(serialNo);
							createMoveRequest(env, enterpriseCode, nodeKey,
									workOrderKey, workOrderNo, palletId,
									sourceLocationId, itemId, productClass,
									unitOfMeasure, requestQty, serialNo, null);
						}
					}
				}
			} else {
				// process non-trackable kit
				String requestQty = workOrderElement
						.getAttribute("QuantityCompleted");
				createMoveRequest(env, enterpriseCode, nodeKey, workOrderKey,
						workOrderNo, palletId, sourceLocationId, itemId,
						productClass, unitOfMeasure, requestQty, null, null);
			}
		} else if (serviceItemId.equals("DEKITTING")) {
			// check if dekitting trackable or non-trackable kit
			NodeList serialListElementList = workOrderElement
					.getElementsByTagName("SerialList");
			boolean isTrackable = (serialListElementList != null && serialListElementList
					.getLength() > 0) ? true : false;

			NodeList workOrderComponentList = workOrderElement
					.getElementsByTagName("WorkOrderComponent");
			if (workOrderComponentList != null
					&& workOrderComponentList.getLength() > 0) {
				for (int i = 0; i < workOrderComponentList.getLength(); i++) {
					// create move request for each component item
					Element workOrderComponentElement = (Element) workOrderComponentList
							.item(i);
					String componentItemId = workOrderComponentElement
							.getAttribute("ItemID");
					String componentProductClass = workOrderComponentElement
							.getAttribute("ProductClass");
					String componentUOM = workOrderComponentElement
							.getAttribute("Uom");
					String serialNo = workOrderComponentElement
							.getAttribute("SerialNo");
					String componentQuantity = workOrderComponentElement
							.getAttribute("ComponentQuantity");
					int componentQty = (serialNo != null && serialNo.trim()
							.length() > 0) ? 1 : (int) Double
							.parseDouble(componentQuantity);
					String requestQty = (isTrackable) ? String
							.valueOf(componentQty) : String
							.valueOf(quantityCompleted * componentQty);

					// check if it has tag details
					Element workOrderComponentTagElement = null;
					NodeList workOrderComponentTagList = workOrderComponentElement
							.getElementsByTagName("WorkOrderComponentTag");
					if (workOrderComponentTagList != null
							&& workOrderComponentTagList.getLength() > 0) {
						workOrderComponentTagElement = (Element) workOrderComponentTagList
								.item(0);
					}

					createMoveRequest(env, enterpriseCode, nodeKey,
							workOrderKey, workOrderNo, palletId,
							sourceLocationId, componentItemId,
							componentProductClass, componentUOM, requestQty,
							serialNo, workOrderComponentTagElement);
				}
			}
		}

		return inDoc;
	}

	private Document createMoveRequest(YFSEnvironment env,
			String enterpriseCode, String node, String workOrderKey,
			String workOrderNo, String palletId, String sourceLocationId,
			String itemId, String productClass, String unitOfMeasure,
			String requestQty, String serialNo,
			Element workOrderComponentTagElement) throws Exception {
		Document moveRequestInDoc = XMLUtil.createDocument("MoveRequest");
		Element rootElement = moveRequestInDoc.getDocumentElement();
		rootElement.setAttribute("Node", node);
		rootElement.setAttribute("FromActivityGroup", "RECEIPT");
		rootElement.setAttribute("ForActivityCode", "STORAGE");
		// rootElement.setAttribute("Release", "Y");
		rootElement.setAttribute("WorkOrderKey", workOrderKey);

		Element workOrderElement = moveRequestInDoc.createElement("WorkOrder");
		rootElement.appendChild(workOrderElement);

		workOrderElement.setAttribute("EnterpriseCode", enterpriseCode);
		workOrderElement.setAttribute("NodeKey", node);
		workOrderElement.setAttribute("WorkOrderNo", workOrderNo);

		Element moveRequestLinesElement = moveRequestInDoc
				.createElement("MoveRequestLines");
		rootElement.appendChild(moveRequestLinesElement);

		Element moveRequestLineElement = moveRequestInDoc
				.createElement("MoveRequestLine");
		moveRequestLinesElement.appendChild(moveRequestLineElement);

		moveRequestLineElement.setAttribute("EnterpriseCode", enterpriseCode);
		moveRequestLineElement.setAttribute("InventoryStatus", "RFI");
		moveRequestLineElement.setAttribute("ItemId", itemId);
		moveRequestLineElement.setAttribute("SerialNo", serialNo);
		moveRequestLineElement.setAttribute("ProductClass", productClass);
		moveRequestLineElement.setAttribute("UnitOfMeasure", unitOfMeasure);
		moveRequestLineElement.setAttribute("RequestQuantity", requestQty);
		moveRequestLineElement.setAttribute("PalletId", palletId);
		moveRequestLineElement.setAttribute("SourceLocationId",
				sourceLocationId);
		moveRequestLineElement.setAttribute("TargetLocationId", "");

		if (workOrderComponentTagElement != null) {
			Element moveRequestLineTagElement = moveRequestInDoc
					.createElement("MoveRequestLineTag");
			moveRequestLineElement.appendChild(moveRequestLineTagElement);

			moveRequestLineTagElement.setAttribute("LotAttribute1",
					workOrderComponentTagElement.getAttribute("LotAttribute1"));
			moveRequestLineTagElement.setAttribute("LotAttribute2",
					workOrderComponentTagElement.getAttribute("LotAttribute2"));
			moveRequestLineTagElement.setAttribute("LotAttribute3",
					workOrderComponentTagElement.getAttribute("LotAttribute3"));
			moveRequestLineTagElement.setAttribute("LotNumber",
					workOrderComponentTagElement.getAttribute("LotNumber"));
			moveRequestLineTagElement.setAttribute("RevisionNo",
					workOrderComponentTagElement.getAttribute("RevisionNo"));
			moveRequestLineTagElement.setAttribute("LotManufactureDate",
					workOrderComponentTagElement
							.getAttribute("ManufacturingDate"));
		}

		Document moveRequestOutDoc = CommonUtilities.invokeAPI(env,
				"createMoveRequest", moveRequestInDoc);

		Element MoveReqElem = moveRequestOutDoc.getDocumentElement();
		String MoveReqKey = MoveReqElem.getAttribute("MoveRequestKey");
		String MoveReqNo = MoveReqElem.getAttribute("MoveRequestNo");

		Document moveReqReleaseInDoc = XMLUtil.createDocument("MoveRequest");
		Element MoveReqReleaseElem = moveReqReleaseInDoc.getDocumentElement();
		MoveReqReleaseElem.setAttribute("Node", node);
		MoveReqReleaseElem.setAttribute("MoveRequestKey", MoveReqKey);
		MoveReqReleaseElem.setAttribute("MoveRequestNo", MoveReqNo);

		Document moveReqReleaseOutDoc = CommonUtilities.invokeAPI(env,
				"releaseMoveRequest", moveReqReleaseInDoc);

		return moveReqReleaseOutDoc;
	}

	private Set<String> getExistingSerials(YFSEnvironment env, String node,
			String workOrderNo, String itemId) throws Exception {
		Set<String> existingSerials = new HashSet<String>();
		Document opTemplate = XMLUtil
				.getDocument("<MoveRequests><MoveRequest Status=\"\"><MoveRequestLines><MoveRequestLine ItemId=\"\" SerialNo=\"\"/></MoveRequestLines></MoveRequest></MoveRequests>");
		Document ipDoc = XMLUtil.getDocument("<MoveRequest Node=\"" + node
				+ "\" ><WorkOrder WorkOrderNo=\"" + workOrderNo
				+ "\" /></MoveRequest>");
		Document outDoc = CommonUtilities.invokeAPI(env, opTemplate,
				"getMoveRequestList", ipDoc);

		Element moveRequestsElement = outDoc.getDocumentElement();
		NodeList moveRequestLineList = moveRequestsElement
				.getElementsByTagName("MoveRequestLine");
		if (moveRequestLineList != null && moveRequestLineList.getLength() > 0) {
			for (int i = 0; i < moveRequestLineList.getLength(); i++) {
				Element moveRequestLineElement = (Element) moveRequestLineList
						.item(i);
				String currentItemId = moveRequestLineElement
						.getAttribute("ItemId");
				String currentSerialNo = moveRequestLineElement
						.getAttribute("SerialNo");

				if (currentItemId.equals(itemId)) {
					if (currentSerialNo != null && currentSerialNo.length() > 0) {
						existingSerials.add(currentSerialNo);
					}
				}
			}
		}

		return existingSerials;
	}
}