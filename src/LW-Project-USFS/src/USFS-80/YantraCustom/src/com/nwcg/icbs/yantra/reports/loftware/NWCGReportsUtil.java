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

package com.nwcg.icbs.yantra.reports.loftware;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReportsUtil {

	private static Logger log = Logger.getLogger(NWCGReportsUtil.class
			.getName());

	public Document generatePrintHeader(String documentId, String path,
			String orgCode, String printerId, String enterpriseCode)
			throws Exception {
		log.verbose("NWCGReportsUtil::generatePrintHeader, Entered");
		Document printHdr = XMLUtil.getDocument();
		Element printDocsElm = printHdr.createElement("PrintDocuments");
		printDocsElm.setAttribute("FlushToPrinter", "Y");
		printHdr.appendChild(printDocsElm);

		Element printDocElm = printHdr.createElement("PrintDocument");
		printDocElm.setAttribute("BeforeChildrenPrintDocumentId", documentId);
		printDocElm.setAttribute("DataElementPath", path);
		printDocsElm.appendChild(printDocElm);

		Element printerPrefElm = printHdr.createElement("PrinterPreference");
		printerPrefElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		printerPrefElm.setAttribute("PrinterId", printerId);
		printDocElm.appendChild(printerPrefElm);

		Element labelPrefElm = printHdr.createElement("LabelPreference");
		labelPrefElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR,
				enterpriseCode);
		printDocElm.appendChild(labelPrefElm);

		if (log.isVerboseEnabled()) {
			log.verbose("NWCGReportsUtil::generatePrintHeader, Header XML : "
					+ XMLUtil.getXMLString(printHdr));
		}
		log.verbose("NWCGReportsUtil::generatePrintHeader, Returning");
		return printHdr;
	}

	public String getWeightInKg(String weightInLbs) {
		double wtInKg = 0;
		if (!weightInLbs.equalsIgnoreCase("0")
				|| !weightInLbs.equalsIgnoreCase("0.0")
				|| !weightInLbs.equalsIgnoreCase("0.00")) {
			double wt = Double.parseDouble(weightInLbs);
			wtInKg = wt / 2.2;
		}
		return new Double(wtInKg).toString();
	}

	public String getWeightInLbs(String weightInKg) {
		double wt = Double.parseDouble(weightInKg);
		double wtInLbs = wt * 2.2;
		return new Double(wtInLbs).toString();
	}

	/**
	 * 1 Cubic Meter = 35.3 Cubic Feet Source:
	 * http://www.metric-conversions.org/volume/cubic-feet-to-cubic-meters.htm
	 * 
	 * @param volInCubicMeters
	 * @return
	 */
	public String getVolInCubicFeet(String volInCubicMeters) {
		double vol = Double.parseDouble(volInCubicMeters);
		double volInCubicFt = vol * (35.3);
		return new Double(volInCubicFt).toString();
	}

	public String getVolInCubicFeetFromInch(String volInCubicInches) {
		double vol = Double.parseDouble(volInCubicInches);
		double volInCubicFt = vol * (0.0005);
		return new Double(volInCubicFt).toString();
	}

	/**
	 * 1 Cubic Meter = 35.3 Cubic Feet Source:
	 * http://www.metric-conversions.org/volume/cubic-feet-to-cubic-meters.htm
	 * 
	 * @param volInCubicFeet
	 * @return
	 */
	public String getVolInCubicMeters(String volInCubicFeet) {
		double volInCubicMeters = 0.0;
		if (volInCubicFeet != null
				&& ((!volInCubicFeet.equals("0"))
						|| (!volInCubicFeet.equals("0.0")) || (!volInCubicFeet
						.equals("0.00")))) {
			double vol = Double.parseDouble(volInCubicFeet);
			volInCubicMeters = vol / (35.3);
		}
		return new Double(volInCubicMeters).toString();
	}

	public String dateToString(java.util.Date date, String format)
			throws Exception {
		if (date == null)
			return "";
		if (format == null)
			return "";

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateString = sdf.format(date);
		return dateString;
	}

	public String convertTimeZone(String DateStr, String LocaleStr)
			throws Exception {
		// System.out.println("In ConvertTimeZone 1");
		String newDateStr = "";
		if (DateStr != null && DateStr.length() > 0) {

			SimpleDateFormat sdf1 = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ");
			SimpleDateFormat sdf2 = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ");

			if (LocaleStr.equals("en_US_EST")) {
				sdf1.setTimeZone(TimeZone.getTimeZone("EST"));
			} else if (LocaleStr.equals("en_US_CST")) {
				sdf1.setTimeZone(TimeZone.getTimeZone("CST"));
			} else if (LocaleStr.equals("en_US_MST")) {
				sdf1.setTimeZone(TimeZone.getTimeZone("MST"));
			} else if (LocaleStr.equals("en_US_PST")) {
				sdf1.setTimeZone(TimeZone.getTimeZone("PST"));
			} else if (LocaleStr.equals("en_US_AST")) {
				sdf1.setTimeZone(TimeZone.getTimeZone("AST"));
			} else {
				sdf1.setTimeZone(TimeZone.getTimeZone("CST"));
			}
			// System.out.println("In ConvertTimeZone 2");
			Date tdate = sdf2.parse(DateStr);
			newDateStr = sdf1.format(tdate);
		}
		return newDateStr;
	}

	/**
	 * Input XML <GetPrinter PrintDocumentId="NWCG_KIT_SKU_LABEL">
	 * <PrinterPreference OrganizationCode="CORMK"/> </GetPrinter>
	 * 
	 * @param env
	 * @param documentId
	 * @param orgCode
	 * @return
	 * @throws Exception
	 */
	public String getPrinterId(YFSEnvironment env, String documentId,
			String orgCode, String workStationId) throws Exception {
		log.verbose("NWCGReportsUtil::getPrinterId, Entered");
		Document getPrinterInputDoc = XMLUtil.getDocument();
		Element getPrinterElm = getPrinterInputDoc.createElement("GetPrinter");
		getPrinterElm.setAttribute(NWCGConstants.PRINT_DOCUMENT_ID, documentId);
		getPrinterInputDoc.appendChild(getPrinterElm);

		Element printerPrefElm = getPrinterInputDoc
				.createElement("PrinterPreference");
		printerPrefElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		if (workStationId.length() > 0) {
			printerPrefElm.setAttribute("WorkStationId", workStationId);
		}
		getPrinterElm.appendChild(printerPrefElm);

		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsUtil::getPrinterId, Input for getPrinterId : "
							+ XMLUtil.getXMLString(getPrinterInputDoc));
		}

		/*
		 * Out XML Format <?xml version="1.0" encoding="UTF-8" ?> <Printer
		 * PrintDocumentId="NWCG_KIT_SKU_LABEL" PrinterId="PDFCreator"
		 * PrinterType="Windows"> <PrinterParams> <Attributes> <Attribute
		 * Name="DropDirectory" Value="/opt/apps/projects/USFS/LoftwareDrop" />
		 * <Attribute Name="PrinterAlias" Value="PDFCreator" /> <Attribute
		 * Name="PrinterServerPort" Value="2723" /> <Attribute
		 * Name="PrintServerHostName" Value="10.10.30.215" /> <Attribute
		 * Name="PrintServerHostName" Value="" /> <Attribute
		 * Name="PrintServerPort" Value="2723" /> </Attributes> </PrinterParams>
		 * </Printer>
		 */
		Document printerDtlsDoc = XMLUtil.getDocument();
		log
				.verbose("NWCGReportsUtil::getPrinterId, Making a call to get the printer id");
		printerDtlsDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_PRINTER, getPrinterInputDoc);
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsUtil::getPrinterId, Output of getPrinter API : "
							+ XMLUtil.getXMLString(printerDtlsDoc));
		}

		Element outputRootElm = printerDtlsDoc.getDocumentElement();
		String printerId = outputRootElm.getAttribute(NWCGConstants.PRINTER_ID);
		log.verbose("NWCGReportsUtil::getPrinterId, Returning");
		return printerId;
	}

	public String getEquipmentId(YFSEnvironment env, String orgCode,
			String locationId) throws Exception {

		// Created by GN - 01/24/08
		log.verbose("NWCGReportsUtil::getEquipmentId, Entered");
		String EquipmentId = "";
		Document getEquipmentInputDoc = XMLUtil.getDocument();
		Element getEquipmentElm = getEquipmentInputDoc
				.createElement("Equipment");
		getEquipmentElm.setAttribute("Node", orgCode);
		getEquipmentInputDoc.appendChild(getEquipmentElm);
		Element getEquipmentDtlsElm = getEquipmentInputDoc
				.createElement("EquipmentDetails");
		getEquipmentElm.appendChild(getEquipmentDtlsElm);
		Element getEquipmentDtlElm = getEquipmentInputDoc
				.createElement("EquipmentDetail");
		getEquipmentDtlElm.setAttribute("LocationId", locationId);
		getEquipmentDtlsElm.appendChild(getEquipmentDtlElm);

		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsUtil::getEquipmentId, Input for getEquipmentId : "
							+ XMLUtil.getXMLString(getEquipmentInputDoc));
		}

		/*
		 * <?xml version="1.0" encoding="UTF-8" ?> <Equipments> <Equipment
		 * EquipmentId="VAS-KIT" EquipmentKey="20061020151417547243"
		 * Node="CORMK"> <EquipmentDevices> <EquipmentDevice
		 * DeviceKey="20071213135512685293"
		 * EquipmentDeviceKey="20080123181049854334"
		 * EquipmentKey="20061020151417547243" /> </EquipmentDevices>
		 * <EquipmentDetails> <EquipmentDetail
		 * EquipmentDetailKey="20061020151417547244" EquipmentId="VAS-KIT"
		 * EquipmentType="VAS Station" LocationId="V1-000001"
		 * LocationLogicalName="VAS" LocationSizeCode="INFINITE" Node="CORMK">
		 * <Location AisleNumber="0" BayNumber="0" FreezeMoveIn="N"
		 * FreezeMoveOut="N" InStagingLocationId="" LevelNumber="0"
		 * LocationBarCode="V1-000001" LocationId="V1-000001"
		 * LocationKey="20060926112551509007" LocationSizeCode="INFINITE"
		 * LocationType="REGULAR" MoveInSeqNo="0" MoveOutSeqNo="0" Node="CORMK"
		 * OutStagingLocationId="" StorageCode="" VelocityCode="C"
		 * ZoneId="VAS-ZONE" /> </EquipmentDetail> </EquipmentDetails>
		 * <EquipmentType Description="VAS Station" EquipmentType="VAS Station"
		 * EquipmentTypeKey="20060926112543505653" Node="CORMK" /> </Equipment>
		 * </Equipments>
		 */

		Document EquipmentDtlsDoc = XMLUtil.getDocument();
		log
				.verbose("NWCGReportsUtil::getEquipmentId, Making a call to get the Equipment id");
		EquipmentDtlsDoc = CommonUtilities.invokeAPI(env, "getEquipmentList",
				getEquipmentInputDoc);
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsUtil::getEquipmentId, Output of getEquipment API : "
							+ XMLUtil.getXMLString(EquipmentDtlsDoc));
		}

		Element EquipElm = (Element) EquipmentDtlsDoc.getDocumentElement()
				.getElementsByTagName("Equipment").item(0);
		EquipmentId = EquipElm.getAttribute("EquipmentId");
		log.verbose("NWCGReportsUtil::getEquipmentId, Returning");
		return EquipmentId;
	}

	/*
	 * Method to Remove Commas in Unit Weight and Volume Added on 01/15/2010 -
	 * GN
	 */
	public String TrimCommas(String Str1) {
		String[] Ctfields = Str1.split(",");
		int scnt = Ctfields.length;
		String newStr1 = "";
		for (int i = 0; i < scnt; i++) {
			newStr1 = newStr1 + Ctfields[i];
		}
		return newStr1;
	}
}