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

package com.nwcg.icbs.yantra.api.returns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGReceiveIncidentReturns implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGReceiveIncidentReturns.class);
	
	int ReturnLineCount = 0;
	int ShipmentTagSerialCount = 0;
	List serialList = new ArrayList();

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	public Document receiveReturns(YFSEnvironment env, Document inXML) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::receiveReturns @@@@@");
		logger.verbose("@@@@@ inXML :: " + XMLUtil.getXMLString(inXML));
		if (null == env) {
			logger.error("!!!!! YFSEnvironment is null");
			throw new NWCGException("NWCG_RETURN_ENV_IS_NULL");
		}
		if (null == inXML) {
			logger.error("!!!!! Input Document is null");
			throw new NWCGException("NWCG_RETURN_INPUT_DOC_NULL");
		}
		String IncidentNo = inXML.getDocumentElement().getAttribute("IncidentNo");
		String IncidentYear = inXML.getDocumentElement().getAttribute("IncidentYear");
		String IssueNo = inXML.getDocumentElement().getAttribute("IssueNo");
		String Cache = inXML.getDocumentElement().getAttribute("CacheID");
		String ReceivingLoc = inXML.getDocumentElement().getAttribute("LocationId");
		String EnterpriseCode = inXML.getDocumentElement().getAttribute("EnterpriseCode");
		NodeList ReturnLines = inXML.getDocumentElement().getElementsByTagName("ReturnLine");
		ReturnLineCount = ReturnLines.getLength();
		logger.verbose("@@@@@ The inxml has lines :" + ReturnLineCount);
		if (ReturnLineCount > 0) {
			for (int cnt = 0; cnt < ReturnLineCount; cnt++) {
				Element ReturnLineEl = (Element) ReturnLines.item(cnt);
				String Serial = ReturnLineEl.getAttribute("TrackableID");
				logger.verbose("@@@@@ SerialNo is :-" + Serial);
				String ItemID = ReturnLineEl.getAttribute("ItemID");
				String Price = ReturnLineEl.getAttribute("UnitPrice");
				String TotalQtyReturned = ReturnLineEl.getAttribute("TotalQtyReturned");
				String BatchNo = ReturnLineEl.getAttribute("BatchNo");
				String DispositionCode = ReturnLineEl.getAttribute("DispositionCode");
				String LotAttribute1 = ReturnLineEl.getAttribute("LotAttribute1");
				String LotAttribute2 = ReturnLineEl.getAttribute("LotAttribute2");
				String LotAttribute3 = ReturnLineEl.getAttribute("LotAttribute3");
				String LotKeyReference = ReturnLineEl.getAttribute("LotKeyReference");
				String LotNumber = ReturnLineEl.getAttribute("LotNumber");
				String OriginalQuantity = ReturnLineEl.getAttribute("OriginalQuantity");
				String ProductClass = ReturnLineEl.getAttribute("ProductClass");
				logger.verbose("@@@@@ ProductClass:-" + ProductClass);
				String Quantity = ReturnLineEl.getAttribute("Quantity");
				String TagNumber = ReturnLineEl.getAttribute("TagNumber");
				String UnitOfMeasure = ReturnLineEl.getAttribute("UnitOfMeasure");
				double NRFI = Double.parseDouble(ReturnLineEl.getAttribute("QuantityNRFI"));
				logger.verbose("@@@@@ NRFI" + NRFI);
				double RFI = Double.parseDouble(ReturnLineEl.getAttribute("QuantityRFI"));
				logger.verbose("@@@@@ RFI" + RFI);
				double TQR = Double.parseDouble(ReturnLineEl.getAttribute("QuantityReturned"));
				logger.verbose("@@@@@ TQR" + TQR);
				double UnsRet = Double.parseDouble(ReturnLineEl.getAttribute("QuantityUnsRet"));
				logger.verbose("@@@@@ UnsRet" + UnsRet);
				double UnsNWT = Double.parseDouble(ReturnLineEl.getAttribute("QuantityUnsNwtReturn"));
				logger.verbose("@@@@@ UnsNWT" + UnsNWT);
				String TrackableID = ReturnLineEl.getAttribute("TrackableID");
				String IsComponent = ReturnLineEl.getAttribute("IsComponent");
				logger.verbose("@@@@@ IsComponent :-" + IsComponent);
				if (TQR != (RFI + NRFI + UnsRet + UnsNWT)) {
					Object[] params = new Object[] { ItemID, Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT) };
					throw new NWCGException("NWCG_RETURN_TOTAL_ERR", params);
				}
				if (!(Serial.equals(""))) {
					logger.verbose("@@@@@ reached 1");
					Document outDoc = queryTable(env, IncidentNo, IncidentYear, IssueNo, Cache, ItemID, TrackableID);
					logger.verbose("@@@@@ The outdoc is :-" + XMLUtil.getXMLString(outDoc));
					NodeList TableLines = outDoc.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
					int numTableLine = TableLines.getLength();
					logger.verbose("@@@@@ Total Lines :-" + numTableLine);
					char RecComp = 'N';
					String PSerialKey, InvItemKey, PItemID, PSerialNo = "";
					Document PSerialOut;
					if (IsComponent.equals("True")) {
						RecComp = 'Y';
						PSerialKey = getParentSerialKey(env, TrackableID, ItemID);
						PSerialOut = getParentSerialNo(env, PSerialKey);
						Element PSerialElem = (Element) PSerialOut.getDocumentElement().getElementsByTagName("Serial").item(0);
						PSerialNo = PSerialElem.getAttribute("SerialNo");
						InvItemKey = PSerialElem.getAttribute("InventoryItemKey");
						PItemID = getParentItemID(env, InvItemKey, Cache);
						UpdateParentRecord(env, IncidentNo, IncidentYear, IssueNo, Cache, PSerialNo, PItemID);
					}
					if (numTableLine == 0) {
						makeEntry(env, Cache, IncidentNo, IncidentYear, IssueNo, ItemID, TrackableID, Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT), 'Y', RecComp, ProductClass, UnitOfMeasure);
						continue;
					} else {
						Element TableLineEl = (Element) TableLines.item(0);
						String IncidentReturnKey = TableLineEl.getAttribute("IncidentReturnKey");
						updateEntry(env, IncidentReturnKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT), 'N', 'N');
					}
				} else {
					logger.verbose("@@@@@ reached 1.1");
					if (IsComponent.equalsIgnoreCase("false")) {
						logger.verbose("@@@@@ reached 2");
						Document outDoc1 = queryTable(env, IncidentNo, IncidentYear, IssueNo, Cache, ItemID, "");
						NodeList TableLines = outDoc1.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
						int numReceiptLine = TableLines.getLength();
						if (numReceiptLine == 0) {
							// Then just make new entry with overreceiptflag='Y'
							logger.verbose("@@@@@ reached here to call make entry");
							makeEntry(env, Cache, IncidentNo, IncidentYear, IssueNo, ItemID, TrackableID, Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT), 'Y', 'N', ProductClass, UnitOfMeasure);
							continue;
						} else {
							logger.verbose("@@@@@ test1,receiptlines :-" + numReceiptLine);
							for (int cnt1 = 0; cnt1 < numReceiptLine; cnt1++) {
								Element tableLineEl = (Element) TableLines.item(cnt1);
								double QS = Double.parseDouble(tableLineEl.getAttribute("QuantityShipped"));
								logger.verbose("@@@@@ QS:" + QS);
								double QR = Double.parseDouble(tableLineEl.getAttribute("QuantityReturned"));
								logger.verbose("@@@@@ QR:" + QR);
								double QRFI = Double.parseDouble(tableLineEl.getAttribute("QuantityRFI"));
								logger.verbose("@@@@@ QRFI" + QRFI);
								double QNRFI = Double.parseDouble(tableLineEl.getAttribute("QuantityNRFI"));
								logger.verbose("@@@@@ QNRFI" + QNRFI);
								double QUnsRet = Double.parseDouble(tableLineEl.getAttribute("QuantityUnsRet"));
								logger.verbose("@@@@@ QUnsRet" + QUnsRet);
								double QUnsNWT = Double.parseDouble(tableLineEl.getAttribute("QuantityUnsNwtReturn"));
								logger.verbose("@@@@@ QUnsNWT" + QUnsNWT);
								String IncidentKey = tableLineEl.getAttribute("IncidentReturnKey");
								double Adj1 = 0;
								double Diff1 = QS - QR;
								logger.verbose("@@@@@ Diff1 ..test..............1:" + Diff1);
								if (Diff1 > 0) {
									if (RFI > 0) {
										logger.verbose("@@@@@ RFI>0 ................1.1:-" + Diff1 + "RFI:" + RFI);
										Adj1 = Diff1 - RFI;
										if (Adj1 > 0 || Adj1 == 0) {
											logger.verbose("@@@@@ RFI>0 ................2.1:-" + Adj1);
											// This means that this line line in incident table can handle RFI No spillover to next line in the table
											QRFI = QRFI + RFI;
											QR = QR + RFI;
											RFI = 0;
											logger.verbose("@@@@@ QR:" + QR);
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											// This line is added to take care of the scenario wherein The receiptline has more attributes NRFI>0 or UnsRet or UnsNWT>0 and The table record is full ie. QS=QR
											if (Diff1 == 0)
												continue;
										} else {
											logger.verbose("@@@@@ RFI>0 ................3.1:-" + Adj1);
											// this means that there is spillover to process RFI to next row in table
											QRFI = QRFI + Diff1;
											RFI = RFI - Diff1;
											QR = QR + Diff1;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										}
									}
									if (NRFI > 0) {
										logger.verbose("@@@@@ NRFI>0 ................1.2:-" + Diff1);
										Adj1 = Diff1 - NRFI;
										if (Adj1 > 0 || Adj1 == 0) {
											logger.verbose("@@@@@ NRFI>0 ...Adj1>0................2.2:-" + Adj1);
											// This means that this line line in incident table can handle RFI No spillover to next line in the table
											QNRFI = QNRFI + NRFI;
											QR = QR + NRFI;
											NRFI = 0;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										} else {
											logger.verbose("@@@@@ NRFI>0 ...Adj1<0................3.2:-" + Adj1);
											// this means that there is spillover to process RFI to next row in table
											QNRFI = QNRFI + Diff1;
											NRFI = NRFI - Diff1;
											QR = QR + Diff1;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										}
									}
									if (UnsRet > 0) {
										logger.verbose("@@@@@ UnsRet>0 ...................1.3:-" + Diff1);
										Adj1 = Diff1 - UnsRet;
										if (Adj1 > 0 || Adj1 == 0) {
											logger.verbose("@@@@@ UnsRet>0 ...Adj1>0................2.3:-" + Adj1);
											// This means that this line line in incident table can handle RFI No spillover to next line in the table
											QUnsRet = QUnsRet + UnsRet;
											QR = QR + UnsRet;
											UnsRet = 0;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										} else {
											logger.verbose("@@@@@ UnsRet>0 ...Adj1<0................3.3:-" + Adj1);
											// this means that there is spillover to process RFI to next row in table
											QUnsRet = QUnsRet + Diff1;
											UnsRet = UnsRet - Diff1;
											QR = QR + Diff1;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										}
									}
									if (UnsNWT > 0) {
										logger.verbose("@@@@@ UnsNWT>0 ...................1.4:-" + Diff1);
										Adj1 = Diff1 - UnsNWT;
										if (Adj1 > 0 || Adj1 == 0) {
											logger.verbose("@@@@@ UnsNWT>0 ...Adj1>0................2.4:-" + Adj1);
											// This means that this line line in incident table can handle RFI No spillover to next line in the table
											QUnsNWT = QUnsNWT + UnsNWT;
											QR = QR + UnsNWT;
											UnsNWT = 0;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										} else {
											logger.verbose("@@@@@ UnsNWT>0 ...Adj1<0................3.4:-" + Adj1);
											// this means that there is spill over to process RFI to next row in table
											QUnsNWT = QUnsNWT + Diff1;
											UnsNWT = UnsNWT - Diff1;
											QR = QR + Diff1;
											Diff1 = QS - QR;
											updateEntry(env, IncidentKey, Cache, IncidentNo, IncidentYear, ItemID, Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N', 'N');
											if (Diff1 == 0)
												continue;
										}
										// Will come here only if the recietline has been taken care of from one table line and we need not move to next table line to process another receipt
										break;
									}
								} else {
									// Means All items that were shipped have been received Go To next record if available
									logger.verbose("@@@@@ REading next line from the table-");
									continue;
								}
							}
						}
						if (RFI > 0 || NRFI > 0 || UnsRet > 0 || UnsNWT > 0) {
							// Make new entry with overreceiptflag set to 'Y'
							logger.verbose("@@@@@ Making over receipt for normal items");
							double totalOverReceipt = RFI + NRFI + UnsRet + UnsNWT;
							makeEntry(env, Cache, IncidentNo, IncidentYear, IssueNo, ItemID, TrackableID, Double.toString(totalOverReceipt), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT), 'Y', 'N', ProductClass, UnitOfMeasure);
						}
					} else {
						// make new entry and set receivedAsComponent='Y'
						logger.verbose("@@@@@ Receiving components");
						makeEntry(env, Cache, IncidentNo, IncidentYear, IssueNo, ItemID, TrackableID, Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), Double.toString(UnsRet), Double.toString(UnsNWT), 'N', 'Y', ProductClass, UnitOfMeasure);
					}
				}
			}
		} else {
			logger.error("!!!!! No Records to process.... Input xml is empty");
			throw new NWCGException("NWCG_RETURN_INPUT_XML_NO_RECORDS_TO_PROCESS");
		}
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::receiveReturns @@@@@");
		return inXML;
	}
	
	public void updateEntry(YFSEnvironment env, String IncidentReturnKey, String Cache, String IncidentNo, String IncidentYear, String ItemID, String QtyReturned, String QRFI, String QNRFI, String QUnsRet, String QUnsNWT, char OverRcpt, char RcdAsComponent) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::updateEntry @@@@@");
		Document inDoc = XMLUtil.newDocument();
		logger.verbose("@@@@@ Inside update entry");
		logger.verbose("@@@@@ IncidentRetKey:" + IncidentReturnKey);
		logger.verbose("@@@@@ Cache:" + Cache);
		logger.verbose("@@@@@ IncidentNo:" + IncidentNo);
		logger.verbose("@@@@@ QtyReturned:" + QtyReturned);
		logger.verbose("@@@@@ QRFI:" + QRFI);
		logger.verbose("@@@@@ QNRFI:" + QNRFI);
		logger.verbose("@@@@@ QUnsRet:" + QUnsRet);
		logger.verbose("@@@@@ QUnsNWT:" + QUnsNWT);
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("IncidentReturnKey", IncidentReturnKey);
		el_NWCGIncidentReturn.setAttribute("QuantityReturned", QtyReturned);
		el_NWCGIncidentReturn.setAttribute("QuantityRFI", QRFI);
		el_NWCGIncidentReturn.setAttribute("QuantityNRFI", QNRFI);
		el_NWCGIncidentReturn.setAttribute("QuantityUnsRet", QUnsRet);
		el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn", QUnsNWT);
		el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", String.valueOf(RcdAsComponent));
		el_NWCGIncidentReturn.setAttribute("OverReceipt", String.valueOf(OverRcpt));
		logger.verbose("@@@@@ The input xml for updateEntry is:-" + XMLUtil.getXMLString(inDoc));
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::updateEntry @@@@@");
	}

	public void makeEntry(YFSEnvironment env, String Cache, String IncidentNo, String IncidentYear, String IssueNo, String ItemID, String SerialNo, String QtyReturned, String QRFI, String QNRFI, String QUnsRet, String QUnsNWT, char OverRcpt, char RcdAsComponent, String ProductClass, String UnitOfMeasure) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::makeEntry @@@@@");
		logger.verbose("@@@@@ Inside MakeEntry, serialNo:-" + SerialNo);
		Document inDoc = XMLUtil.newDocument();
		Document trackinDoc = XMLUtil.newDocument();
		Document trackoutDoc = XMLUtil.newDocument();
		String lastissueprice = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String date = format.format(new java.util.Date());
		logger.verbose("@@@@@ Date is :-" + date);
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
		el_NWCGIncidentReturn.setAttribute("DateIssued", date);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		el_NWCGIncidentReturn.setAttribute("QuantityReturned", QtyReturned);
		el_NWCGIncidentReturn.setAttribute("QuantityRFI", QRFI);
		el_NWCGIncidentReturn.setAttribute("QuantityNRFI", QNRFI);
		el_NWCGIncidentReturn.setAttribute("QuantityUnsRet", QUnsRet);
		el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn", QUnsNWT);
		el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", String.valueOf(RcdAsComponent));
		el_NWCGIncidentReturn.setAttribute("OverReceipt", String.valueOf(OverRcpt));
		if (SerialNo.length() > 0) {
			// Added for CR-445
			Element el_NWCGTrackableItem = trackinDoc.createElement("NWCGTrackableItem");
			trackinDoc.appendChild(el_NWCGTrackableItem);
			el_NWCGTrackableItem.setAttribute("StatusIncidentNo", IncidentNo);
			el_NWCGTrackableItem.setAttribute("StatusIncidentYear", IncidentYear);
			el_NWCGTrackableItem.setAttribute("ItemID", ItemID);
			el_NWCGTrackableItem.setAttribute("SerialNo", SerialNo);
			el_NWCGTrackableItem.setAttribute("SecondarySerial", CommonUtilities.getSecondarySerial(env, SerialNo, ItemID));
			trackoutDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, trackinDoc);
			Element trackoutelem = null;
			trackoutelem = (Element) trackoutDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
			if (trackoutelem != null) {
				lastissueprice = trackoutelem.getAttribute("LastIssuePrice");
			}
		}
		if (lastissueprice.length() > 0) {
			el_NWCGIncidentReturn.setAttribute("UnitPrice", lastissueprice);
		} else {
			// End CR-445 Changes
			Document rt_ComputePriceForItem = XMLUtil.getDocument();
			Element el_ComputePriceForItem = rt_ComputePriceForItem.createElement("ComputePriceForItem");
			rt_ComputePriceForItem.appendChild(el_ComputePriceForItem);
			el_ComputePriceForItem.setAttribute("Currency", "USD");
			el_ComputePriceForItem.setAttribute("ItemID", ItemID);
			el_ComputePriceForItem.setAttribute("OrganizationCode", "NWCG");
			el_ComputePriceForItem.setAttribute("PriceProgramName", "NWCG_PRICE_PROGRAM");
			el_ComputePriceForItem.setAttribute("ProductClass", ProductClass);
			el_ComputePriceForItem.setAttribute("Quantity", "1");
			el_ComputePriceForItem.setAttribute("Uom", UnitOfMeasure);
			Document rt_ComputePriceForItemOut = XMLUtil.getDocument();
			Element el_ComputePriceForItemOut = rt_ComputePriceForItemOut.createElement("ComputePriceForItem");
			rt_ComputePriceForItemOut.appendChild(el_ComputePriceForItemOut);
			el_ComputePriceForItemOut.setAttribute("UnitPrice", "");
			logger.verbose("@@@@@ The outputtemplate for computePriceForItem is:-" + XMLUtil.getXMLString(rt_ComputePriceForItemOut));
			// Calling the API to get the price
			logger.verbose("@@@@@ The input xml for computePriceForItem is:-" + XMLUtil.getXMLString(rt_ComputePriceForItem));
			Document PriceDocument = CommonUtilities.invokeAPI(env, "computePriceForItem", rt_ComputePriceForItem);
			logger.verbose("@@@@@ The output xml for computePriceForItem is:-" + XMLUtil.getXMLString(PriceDocument));
			Element root_elem = PriceDocument.getDocumentElement();
			if (root_elem != null) {
				// Complete the input xml for updating the record in the Incident table
				el_NWCGIncidentReturn.setAttribute("UnitPrice", root_elem.getAttribute("UnitPrice"));
			}
			logger.verbose("@@@@@ UnitPrice :: " + root_elem.getAttribute("UnitPrice"));
			logger.verbose("@@@@@ The input xml for makeEntry is:-" + XMLUtil.getXMLString(inDoc));
		}
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_INSERT_REC_IN_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::makeEntry @@@@@");
	}

	public Document queryTable(YFSEnvironment env, String IncidentNo, String IncidentYear, String IssueNo, String Cache, String ItemID, String SerialNo) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::queryTable @@@@@");
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		if (SerialNo.length() > 0) {
			el_NWCGIncidentReturn.setAttribute("QuantityReturned", "0");
			el_NWCGIncidentReturn.setAttribute("QuantityRFI", "0");
			el_NWCGIncidentReturn.setAttribute("QuantityNRFI", "0");
			el_NWCGIncidentReturn.setAttribute("QuantityUnsRet", "0");
			el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn", "0");
		}
		// This to make sure over receipt entries and received as components are not selected
		el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", "N");
		el_NWCGIncidentReturn.setAttribute("OverReceipt", "N");
		Element el_OrderBy = inDoc.createElement("OrderBy");
		el_NWCGIncidentReturn.appendChild(el_OrderBy);
		Element el_Attribute = inDoc.createElement("Attribute");
		el_OrderBy.appendChild(el_Attribute);
		el_Attribute.setAttribute("Name", "Createts");
		logger.verbose("@@@@@ Input xml for querying the table is:-" + XMLUtil.getXMLString(inDoc));
		outDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::queryTable @@@@@");
		return outDoc;
	}

	public String getParentSerialKey(YFSEnvironment env, String SerialNo, String strItemID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::getParentSerialKey @@@@@");
		String GSerialKey = "";
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("SerialNo", SerialNo);
		Element el_InvItem = inDoc.createElement("InventoryItem");
		el_PSerial.appendChild(el_InvItem);
		el_InvItem.setAttribute("ItemID", strItemID);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList");
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);
		Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
		GSerialKey = PSerialOut.getAttribute("ParentSerialKey");
		env.clearApiTemplate("getSerialList");
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::getParentSerialKey @@@@@");
		return GSerialKey;
	}

	public Document getParentSerialNo(YFSEnvironment env, String ParentSerialKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::getParentSerialNo @@@@@");
		String PSerialNo = "";
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("GlobalSerialKey", ParentSerialKey);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList");
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);
		env.clearApiTemplate("getSerialList");
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::getParentSerialNo @@@@@");
		return outDoc;
	}

	public String getParentItemID(YFSEnvironment env, String InvItemKey, String NodeKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::getParentItemID @@@@@");
		String PItemID = "";
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_PItem = inDoc.createElement("InventoryItem");
		inDoc.appendChild(el_PItem);
		el_PItem.setAttribute("InventoryItemKey", InvItemKey);
		el_PItem.setAttribute("OrganizationCode", NodeKey);
		outDoc = CommonUtilities.invokeAPI(env, "getInventoryItemList", inDoc);
		Element PItemOut = (Element) outDoc.getDocumentElement().getElementsByTagName("InventoryItem").item(0);
		PItemID = PItemOut.getAttribute("ItemID");
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::getParentItemID @@@@@");
		return PItemID;
	}

	public void UpdateParentRecord(YFSEnvironment env, String IncidentNo, String IncidentYear, String IssueNo, String Cache, String PSerialNo, String PItemID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGReceiveIncidentReturns::UpdateParentRecord @@@@@");
		Document outDoc = queryTable(env, IncidentNo, IncidentYear, IssueNo, Cache, PItemID, PSerialNo);
		NodeList TableLines = outDoc.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
		if (TableLines.getLength() > 0) {
			Element TableLineEl = (Element) TableLines.item(0);
			String IncidentReturnKey = TableLineEl.getAttribute("IncidentReturnKey");
			Document inDoc = XMLUtil.newDocument();
			Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
			inDoc.appendChild(el_NWCGIncidentReturn);
			el_NWCGIncidentReturn.setAttribute("IncidentReturnKey", IncidentReturnKey);
			el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", "Y");
			CommonUtilities.invokeService(env, NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE, inDoc);
		}
		logger.verbose("@@@@@ Exiting NWCGReceiveIncidentReturns::UpdateParentRecord @@@@@");
	}
}