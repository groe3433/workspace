package com.fanatics.sterling.ue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.stub.FanTaxResponse;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.StringUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineChargeStruct;
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;

public class FANYFSRecalculateLineTaxUEImpl implements YFSRecalculateLineTaxUE {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);

	@Override
	public YFSExtnTaxCalculationOutStruct recalculateLineTax(
			YFSEnvironment env, YFSExtnLineTaxCalculationInputStruct lineTaxInputStruct)
					throws YFSUserExitException {

		YFSExtnTaxCalculationOutStruct lineTaxOutputStruct = new YFSExtnTaxCalculationOutStruct();

		logger.info("Inside FANYFSRecalculateLineTaxUEImpl");
		if(lineTaxInputStruct.bForInvoice){
			if(lineTaxInputStruct.bLastInvoiceForOrderLine){
				lineTaxOutputStruct.colTax = lineTaxInputStruct.colTax;
				lineTaxOutputStruct.tax = lineTaxInputStruct.tax;
				lineTaxOutputStruct.taxPercentage = lineTaxInputStruct.taxPercentage;
			}else{
				ArrayList<YFSExtnTaxBreakup> lineTaxBreakUpList = (ArrayList<YFSExtnTaxBreakup>) lineTaxInputStruct.colTax;
				ArrayList<YFSExtnTaxBreakup> lineTaxOutputList = new ArrayList<YFSExtnTaxBreakup>();

				Iterator<YFSExtnTaxBreakup> taxIt = lineTaxBreakUpList.iterator();
				while (taxIt.hasNext()) {
					YFSExtnTaxBreakup lineTaxBreakUpIn = taxIt.next();
					YFSExtnTaxBreakup lineTaxBreakUp = new YFSExtnTaxBreakup();

					lineTaxBreakUp.chargeCategory = lineTaxBreakUpIn.chargeCategory;
					lineTaxBreakUp.chargeName = lineTaxBreakUpIn.chargeName;
					lineTaxBreakUp.invoicedTax = lineTaxBreakUpIn.invoicedTax;
					lineTaxBreakUp.reference1 = lineTaxBreakUpIn.reference1;
					lineTaxBreakUp.reference2 = lineTaxBreakUpIn.reference2;
					lineTaxBreakUp.reference3 = lineTaxBreakUpIn.reference3;
					lineTaxBreakUp.tax = 0.0;
					lineTaxBreakUp.taxableFlag = lineTaxBreakUpIn.taxableFlag;
					lineTaxBreakUp.taxName = lineTaxBreakUpIn.taxName;
					lineTaxBreakUp.taxPercentage = 0.0;
					lineTaxOutputList.add(lineTaxBreakUp);
				}
				lineTaxOutputStruct.colTax = lineTaxOutputList;
				lineTaxOutputStruct.tax = 0.0;
				lineTaxOutputStruct.taxPercentage = 0.0;
			}
		}else{
			try {
				Document lineTaxCalcInDoc = XMLUtil.createDocument("YFSExtnLineTaxCalculationInputStruct");
				Element lineTaxCalcInElem = lineTaxCalcInDoc.getDocumentElement();
				lineTaxCalcInElem.setAttribute("BForInvoice", Boolean.toString(lineTaxInputStruct.bForInvoice));
				lineTaxCalcInElem.setAttribute("BForPacklistPrice", Boolean.toString(lineTaxInputStruct.bForPacklistPrice));
				lineTaxCalcInElem.setAttribute("BForPreSettlement", Boolean.toString(lineTaxInputStruct.bForPreSettlement));
				lineTaxCalcInElem.setAttribute("BLastInvoiceForOrderLine", Boolean
						.toString(lineTaxInputStruct.bLastInvoiceForOrderLine));
				lineTaxCalcInElem.setAttribute("CurrentQty", Double.toString(lineTaxInputStruct.currentQty));
				lineTaxCalcInElem.setAttribute("DocumentType", lineTaxInputStruct.documentType);
				lineTaxCalcInElem.setAttribute("EnterpriseCode", lineTaxInputStruct.enterpriseCode);
				lineTaxCalcInElem.setAttribute("InvoicedQty", Double.toString(lineTaxInputStruct.invoicedQty));
				lineTaxCalcInElem.setAttribute("InvoiceKey", lineTaxInputStruct.invoiceKey);
				lineTaxCalcInElem.setAttribute("InvoiceMode", lineTaxInputStruct.invoiceMode);
				lineTaxCalcInElem.setAttribute("ItemId", lineTaxInputStruct.itemId);
				lineTaxCalcInElem.setAttribute("LineQty", Double.toString(lineTaxInputStruct.lineQty));
				lineTaxCalcInElem.setAttribute("OrderedQty", Double.toString(lineTaxInputStruct.orderedQty));
				lineTaxCalcInElem.setAttribute("OrderHeaderKey", lineTaxInputStruct.orderHeaderKey);
				lineTaxCalcInElem.setAttribute("OrderLineKey", lineTaxInputStruct.orderLineKey);
				lineTaxCalcInElem.setAttribute("Purpose", lineTaxInputStruct.purpose);
				lineTaxCalcInElem.setAttribute("ShipToCity", lineTaxInputStruct.shipToCity);
				lineTaxCalcInElem.setAttribute("ShipToCountry", lineTaxInputStruct.shipToCountry);
				lineTaxCalcInElem.setAttribute("ShipToId", lineTaxInputStruct.shipToId);
				lineTaxCalcInElem.setAttribute("ShipToState", lineTaxInputStruct.shipToState);
				lineTaxCalcInElem.setAttribute("ShipToZipCode", lineTaxInputStruct.shipToZipCode);
				lineTaxCalcInElem.setAttribute("SShipNode", lineTaxInputStruct.sShipNode);
				lineTaxCalcInElem.setAttribute("Tax", Double.toString(lineTaxInputStruct.tax));
				lineTaxCalcInElem.setAttribute("TaxExemptFlag", lineTaxInputStruct.taxExemptFlag);
				lineTaxCalcInElem.setAttribute("TaxExemptionCertificate", lineTaxInputStruct.taxExemptionCertificate);
				lineTaxCalcInElem.setAttribute("TaxJurisdiction", lineTaxInputStruct.taxJurisdiction);
				lineTaxCalcInElem.setAttribute("TaxpayerId", lineTaxInputStruct.taxpayerId);
				lineTaxCalcInElem.setAttribute("TaxPercentage", Double.toString(lineTaxInputStruct.taxPercentage));
				lineTaxCalcInElem.setAttribute("TotalHandlingCharges", Double
						.toString(lineTaxInputStruct.totalHandlingCharges));
				lineTaxCalcInElem.setAttribute("Totaloptionprice", Double.toString(lineTaxInputStruct.totaloptionprice));
				lineTaxCalcInElem.setAttribute("TotalPersonalizeCharges", Double
						.toString(lineTaxInputStruct.totalPersonalizeCharges));
				lineTaxCalcInElem.setAttribute("TotalShippingCharges", Double
						.toString(lineTaxInputStruct.totalShippingCharges));
				lineTaxCalcInElem.setAttribute("UnitPrice", Double.toString(lineTaxInputStruct.unitPrice));

				Element lineChargeListElem = lineTaxCalcInDoc.createElement("YFSExtnLineChargeStructList");
				lineTaxCalcInElem.appendChild(lineChargeListElem);

				ArrayList<YFSExtnLineChargeStruct> lineChargeList = (ArrayList<YFSExtnLineChargeStruct>) lineTaxInputStruct.colCharge;

				Iterator<YFSExtnLineChargeStruct> it = lineChargeList.iterator();
				while (it.hasNext()) {
					YFSExtnLineChargeStruct lineChargeStruct = it.next();
					Element lineChargeElem = lineTaxCalcInDoc.createElement("YFSExtnLineChargeStruct");
					lineChargeListElem.appendChild(lineChargeElem);
					lineChargeElem.setAttribute("ChargeAmount", Double.toString(lineChargeStruct.chargeAmount));
					lineChargeElem.setAttribute("ChargeCategory", lineChargeStruct.chargeCategory);
					lineChargeElem.setAttribute("ChargeName", lineChargeStruct.chargeName);
					lineChargeElem.setAttribute("ChargePerLine", Double.toString(lineChargeStruct.chargePerLine));
					lineChargeElem.setAttribute("ChargePerUnit", Double.toString(lineChargeStruct.chargePerUnit));
					lineChargeElem.setAttribute("InvoicedExtended", Double.toString(lineChargeStruct.invoicedExtended));
					lineChargeElem.setAttribute("InvoicedPerLine", Double.toString(lineChargeStruct.invoicedPerLine));
					lineChargeElem.setAttribute("Reference", lineChargeStruct.reference);
					Element extnChargeFieldsElem = lineTaxCalcInDoc.createElement("EleExtendedFields");
					lineChargeElem.appendChild(extnChargeFieldsElem);
					Element extnElem = lineChargeStruct.eleExtendedFields.getDocumentElement();
					XMLUtil.copyElementAttributes(extnElem, extnChargeFieldsElem);
				}

				Element lineTaxBreakUpListElem = lineTaxCalcInDoc.createElement("YFSExtnTaxBreakupList");
				lineTaxCalcInElem.appendChild(lineTaxBreakUpListElem);

				ArrayList<YFSExtnTaxBreakup> lineTaxBreakUpList = (ArrayList<YFSExtnTaxBreakup>) lineTaxInputStruct.colTax;

				Iterator<YFSExtnTaxBreakup> taxIt = lineTaxBreakUpList.iterator();
				while (taxIt.hasNext()) {
					YFSExtnTaxBreakup lineTaxBreakUp = taxIt.next();
					Element lineTaxElem = lineTaxCalcInDoc.createElement("YFSExtnTaxBreakup");
					lineTaxBreakUpListElem.appendChild(lineTaxElem);
					lineTaxElem.setAttribute("ChargeCategory", lineTaxBreakUp.chargeCategory);
					lineTaxElem.setAttribute("ChargeName", lineTaxBreakUp.chargeName);
					lineTaxElem.setAttribute("InvoicedTax", Double.toString(lineTaxBreakUp.invoicedTax));
					lineTaxElem.setAttribute("Reference1", lineTaxBreakUp.reference1);
					lineTaxElem.setAttribute("Reference2", lineTaxBreakUp.reference2);
					lineTaxElem.setAttribute("Reference3", lineTaxBreakUp.reference3);
					lineTaxElem.setAttribute("Tax", Double.toString(lineTaxBreakUp.tax));
					lineTaxElem.setAttribute("TaxableFlag", lineTaxBreakUp.taxableFlag);
					lineTaxElem.setAttribute("TaxName", lineTaxBreakUp.taxName);
					lineTaxElem.setAttribute("TaxPercentage", Double.toString(lineTaxBreakUp.taxPercentage));
					Element extnTaxFieldsElem = lineTaxCalcInDoc.createElement("EleExtendedFields");
					lineTaxElem.appendChild(extnTaxFieldsElem);
					Element extnElem = lineTaxBreakUp.eleExtendedFields.getDocumentElement();
					XMLUtil.copyElementAttributes(extnElem, extnTaxFieldsElem);
				}

				logger.info("Input to recalculateLineTax: \n"+lineTaxCalcInDoc);

				Document lineTaxCalcOutDoc = recalculateLineTax(env, lineTaxCalcInDoc);

				logger.info("Output from recalculateLineTax: \n"+lineTaxCalcOutDoc);

				Element lineTaxOutElem = lineTaxCalcOutDoc.getDocumentElement();

				NodeList lineTaxNodeList = XPathUtil.getNodeList(lineTaxOutElem, "//LineTaxes/LineTax");
				int lineTaxesListLength = lineTaxNodeList.getLength();

				if(lineTaxesListLength > 0){

					ArrayList<YFSExtnTaxBreakup> lineTaxOutputList = new ArrayList<YFSExtnTaxBreakup>();
					for(int i=0;i<lineTaxesListLength;i++){
						Element lineTaxElem = (Element) lineTaxNodeList.item(i);
						YFSExtnTaxBreakup lineTaxBreakUp = new YFSExtnTaxBreakup();
						lineTaxBreakUp.chargeCategory = lineTaxElem.getAttribute("ChargeCategory");
						lineTaxBreakUp.chargeName = lineTaxElem.getAttribute("ChargeName");
						lineTaxBreakUp.reference1 = lineTaxElem.getAttribute("Reference1");
						lineTaxBreakUp.reference2 = lineTaxElem.getAttribute("Reference2");
						lineTaxBreakUp.reference3 = lineTaxElem.getAttribute("Reference3");
						lineTaxBreakUp.taxableFlag = lineTaxElem.getAttribute("TaxableFlag");
						lineTaxBreakUp.taxName = lineTaxElem.getAttribute("TaxName");

						if(!StringUtil.isEmpty(lineTaxElem.getAttribute("Tax")))
							lineTaxBreakUp.tax = Double.parseDouble(lineTaxElem.getAttribute("Tax"));
						if(!StringUtil.isEmpty(lineTaxElem.getAttribute("InvoicedTax")))
							lineTaxBreakUp.invoicedTax = Double.parseDouble(lineTaxElem.getAttribute("InvoicedTax"));
						if(!StringUtil.isEmpty(lineTaxElem.getAttribute("TaxPercentage")))
							lineTaxBreakUp.taxPercentage = Double.parseDouble(lineTaxElem.getAttribute("TaxPercentage"));

						if(null != lineTaxElem.getElementsByTagName("Extn").item(0)){
							lineTaxBreakUp.eleExtendedFields = XMLUtil.createDocument(lineTaxElem.getElementsByTagName("Extn").item(0));
						}

						lineTaxOutputList.add(lineTaxBreakUp);
					}

					lineTaxOutputStruct.colTax = lineTaxOutputList;

				}else{
					lineTaxOutputStruct.colTax = lineTaxInputStruct.colTax;
				}

			}catch(Exception e){
				logger.info("Error in FANYFSRecalculateLineTaxUEImpl : "+e.getMessage());
			}
		}
		return lineTaxOutputStruct;
	}

	public Document recalculateLineTax(YFSEnvironment env,Document inDoc) throws Exception {

		Document restOutputDoc = null;

		String path = YFSSystem.getProperty("fan.rest.tax.path");

		if(!XMLUtil.isVoid(path)){
			RESTClient rClient = new RESTClient();

			//Get REST connection details
			String authType = YFSSystem.getProperty("fan.rest.authtype"); 
			String baseUrl = YFSSystem.getProperty("fan.rest.base.url"); 
			String username = YFSSystem.getProperty("fan.rest.user"); 
			String password = YFSSystem.getProperty("fan.rest.pwd"); 

			rClient.setAuthType(authType);
			rClient.setBaseUrl(baseUrl);
			rClient.setUsername(username);
			rClient.setPassword(password);
			String restOutputStr = "";

			try{
				restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(inDoc));
			}catch(Exception e){
				logger.error("Exception making REST call to fraud: " + e.getMessage() , e.getCause());
			}

			//Convert string to document
			try {
				restOutputDoc = XMLUtil.getDocument(restOutputStr);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
			}
		}else {
			FanTaxResponse fanTaxResponse = new FanTaxResponse();
			Element docElement = inDoc.getDocumentElement();
			docElement.setAttribute("CallingClass", this.getClass().getName());
			restOutputDoc = fanTaxResponse.internalTaxResponse(env,inDoc);
		} 

		return restOutputDoc;
	}

}
