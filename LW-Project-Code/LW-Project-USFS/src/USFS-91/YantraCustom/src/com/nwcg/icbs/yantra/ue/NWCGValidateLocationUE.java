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

import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.wms.japi.ue.WMSBeforeCreateCountRequestUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author sgomathinayagam *
 */
public class NWCGValidateLocationUE implements WMSBeforeCreateCountRequestUE {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGValidateLocationUE.class);

	/*
	 * <CountRequest DocumentType="3001" EnterpriseCode="" FinishNoLaterThan=""
	 * LocationId"=STOR1-01010101" Node="CORMK" Priority="3" RequestType=""
	 * RequestingUserId="mdean" StartNoEarlierThan="2009-12-02T02:00:00"/>
	 * 
	 * <CountRequest AisleNumber="" BayNumber="" CancellationReasonCode=""
	 * CaseId="" CountRequestKey="" CountRequestNo="" CreateFlag=""
	 * DocumentType="3001" EnterpriseCode="NWCG" FinishNoLaterThan=""
	 * FromLocation="STOR1-01010201" IgnoreOrdering="Y" ItemClassification1=""
	 * ItemClassification2="" ItemClassification3="" ItemID="" LevelNumber=""
	 * LocationId="" ModifyFlag="" Node="CORMK" PalletId="" PipelineKey=""
	 * Priority="3" ProductClass="" ReasonText="" ReceiptHeaderKey=""
	 * ReceiptNo="" RequestType="PHYSICAL-COUNT" RequestTypeDescription=""
	 * RequestingUserId="mdean" SerialNo=""
	 * StartNoEarlierThan="2009-11-19T02:00:00" Status=""
	 * ToLocation="STOR1-01010302" UnitOfMeasure="" ZoneId=""/>
	 */

	public Document beforeCreateCountRequest(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		if (logger.isVerboseEnabled())
			logger.verbose("Input XML " + XMLUtil.getXMLString(inXML));
		Element root = inXML.getDocumentElement();
		String LocationId = "";
		String Node = "";
		String ZoneId = "";
		String AisleNo = "";
		String ItemID = "";
		String RequestType = "";
		String CountReqNo = "";
		String OriginalCountReqNo = "";
		String OriginalTaskId = "";
		LocationId = root.getAttribute("LocationId");
		Node = root.getAttribute("Node");
		ZoneId = root.getAttribute("ZoneId");
		AisleNo = root.getAttribute("AisleNumber");
		ItemID = root.getAttribute("ItemID");
		RequestType = root.getAttribute("RequestType");
		CountReqNo = root.getAttribute("CountRequestNo");
		OriginalCountReqNo = root.getAttribute("OriginalCountRequestNo");
		OriginalTaskId = root.getAttribute("OriginalTaskId");

		try {
			Document inDoc = XMLUtil.newDocument();
			Document outDoc = XMLUtil.newDocument();
			String FreezeIn = "";
			String FreezeOut = "";
			String NewCountReqNo = "";

			if (RequestType.equals("PHYSICAL-COUNT")) {
				if (OriginalTaskId.length() > 0) {
					Document taskXML = XMLUtil
							.createDocument("DeleteCountResultRecordsForTask");
					taskXML.getDocumentElement().setAttribute("Action",
							"Modify");
					taskXML.getDocumentElement().setAttribute("TaskId",
							OriginalTaskId);
					taskXML.getDocumentElement().setAttribute(
							"VarianceAccepted", "I");

					Document taskoutputXML = CommonUtilities.invokeAPI(env,
							"deleteCountResultRecordsForTask", taskXML);
				}

				if (LocationId.length() > 0) {
					Element Loc = inDoc.createElement("Location");
					inDoc.appendChild(Loc);
					Loc.setAttribute("LocationId", LocationId);
					Loc.setAttribute("Node", Node);
					outDoc = CommonUtilities.invokeAPI(env,
							"getLocationDetails", inDoc);

					Element LocOut = outDoc.getDocumentElement();

					FreezeIn = LocOut.getAttribute("FreezeMoveIn");
					FreezeOut = LocOut.getAttribute("FreezeMoveOut");
					ZoneId = LocOut.getAttribute("ZoneId");
					AisleNo = LocOut.getAttribute("AisleNumber");
				}

				if (FreezeIn.equals("Y") || FreezeOut.equals("Y")) {
					root.setAttribute("RejectRequest", "Y");
					return inXML;
				}

				if (OriginalCountReqNo.length() > 0 && CountReqNo.length() > 0) {
					int indexOfPeriod = OriginalCountReqNo.lastIndexOf(".");
					if (indexOfPeriod == -1)// original count request
					{
						NewCountReqNo = OriginalCountReqNo + ".1";
					} else// subsequent count request
					{
						// Format for subsequent count requests (xxxxx.1,
						// xxxxx.2 etc.)
						String numericPortion = OriginalCountReqNo
								.substring(indexOfPeriod + 1);
						String countRequestNumberBeforePeriod = OriginalCountReqNo
								.substring(0, indexOfPeriod);

						try {
							int integerSuffix = Integer
									.parseInt(numericPortion);
							NewCountReqNo = countRequestNumberBeforePeriod
									+ "." + (integerSuffix + 1);
						} catch (NumberFormatException nfe) {
							throw new NWCGException(
									"NWCGValidateLocationUE: Could not parse Count Request Number.");
						}
					}

					root.setAttribute("CountRequestNo", NewCountReqNo);
					return inXML;
				}

				if (CountReqNo.length() == 0) {
					// creating an input document
					Document inputXML = XMLUtil.createDocument("NextReceiptNo");
					inputXML.getDocumentElement().setAttribute("ReceiptNo", "");
					inputXML.getDocumentElement().setAttribute("SequenceType",
							"COUNTREQUEST");
					inputXML.getDocumentElement().setAttribute("CacheID", Node);

					Document outputXML = CommonUtilities.invokeService(env,
							"NWCGGenerateReceiptNoService", inputXML);

					// catch receipt number
					Element receiptNo = outputXML.getDocumentElement();
					String nextCountNo = receiptNo.getAttribute("ReceiptNo");

					Document doc = XMLUtil.createDocument("CommonCode");
					doc.getDocumentElement().setAttribute("CodeType",
							"NWCG_PC_YEAR");
					Document CDoc = CommonUtilities.invokeAPI(env,
							"getCommonCodeList", doc);

					String nwcg_pc_year = XPathUtil.getString(CDoc,
							"/CommonCodeList/CommonCode/@CodeValue");
					String Count_Seq = nextCountNo.substring(6);

					NewCountReqNo = nwcg_pc_year + "-" + Node;

					if (ZoneId.length() > 0) {
						NewCountReqNo = NewCountReqNo + "-" + ZoneId;
					} else {
						if (ItemID.length() > 0) {
							NewCountReqNo = NewCountReqNo + "-" + ItemID;
						}
					}

					if (AisleNo.length() > 0) {
						NewCountReqNo = NewCountReqNo + "-" + AisleNo;
					} else {
						if (ZoneId.length() > 0)
							NewCountReqNo = NewCountReqNo + "-00";
					}

					if (OriginalCountReqNo.length() > 0) {
						NewCountReqNo = OriginalCountReqNo + "-" + Count_Seq;
						// QC1156 - Limiting the CountRequest Length to 40
						// character
						if (NewCountReqNo.length() > 40) {
							logger.verbose(" @@@@ CountReqNo greater than allowed limit");
							int extraChar = NewCountReqNo.length() - 40;
							StringTokenizer stz = new StringTokenizer(NewCountReqNo, "-");
							String[] array = new String[stz.countTokens()];
							int count = 0;
							String format = "";
							while (stz.hasMoreElements()) {
								String nextAtt = (String) stz.nextElement();
								if ((count >= 2) && (nextAtt.length() >= 3)	&& (extraChar > 0)) {
									extraChar = extraChar - (nextAtt.length() - 3);
									nextAtt = nextAtt.substring(0, 3);
								}
								array[count] = nextAtt;
								if (count != 0) {
									format += "-";
								}
								format += "%s";
								count++;
							}
							final String limitedCountReqNo = String.format(format, array);
							NewCountReqNo = limitedCountReqNo;
							logger.verbose(" @@@@ The NewCountReqNo within limited 40 character count : "+ NewCountReqNo);
						}
					} else {
						NewCountReqNo = NewCountReqNo + "-" + Count_Seq;
					}

					root.setAttribute("CountRequestNo", NewCountReqNo);
				}

			}

		} catch (Exception E) {
			throw new YFSUserExitException(E.toString());
		}
		return inXML;
	}
}
