package com.fanatics.sterling.ue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
import com.yantra.yfs.japi.YFSExtnHeaderChargeStruct;
import com.yantra.yfs.japi.YFSExtnHeaderTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateHeaderTaxUE;

public class FANYFSRecalculateHeaderTaxUEImpl implements YFSRecalculateHeaderTaxUE {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);

	@Override
	public YFSExtnTaxCalculationOutStruct recalculateHeaderTax(
			YFSEnvironment env, YFSExtnHeaderTaxCalculationInputStruct headerTaxInputStruct)
					throws YFSUserExitException {

		logger.info("Inside FANYFSRecalculateHeaderTaxUEImpl");

		YFSExtnTaxCalculationOutStruct headerTaxOutputStruct = new YFSExtnTaxCalculationOutStruct();

		if(headerTaxInputStruct.bForInvoice){
			if(headerTaxInputStruct.bLastInvoice){
				logger.info(headerTaxInputStruct);
				headerTaxOutputStruct.colTax = headerTaxInputStruct.colTax;
				headerTaxOutputStruct.tax = headerTaxInputStruct.tax;
				headerTaxOutputStruct.taxPercentage = headerTaxInputStruct.taxPercentage;
			}else{
				logger.info(headerTaxInputStruct);
				
				ArrayList<YFSExtnTaxBreakup> headerTaxBreakUpList = (ArrayList<YFSExtnTaxBreakup>) headerTaxInputStruct.colTax;
				ArrayList<YFSExtnTaxBreakup> headerTaxOutputList = new ArrayList<YFSExtnTaxBreakup>();
				
	            Iterator<YFSExtnTaxBreakup> taxIt = headerTaxBreakUpList.iterator();
	            while (taxIt.hasNext()) {
	                YFSExtnTaxBreakup headerTaxBreakUpIn = taxIt.next();
	                YFSExtnTaxBreakup headerTaxBreakUp = new YFSExtnTaxBreakup();
	               
	                headerTaxBreakUp.chargeCategory = headerTaxBreakUpIn.chargeCategory;
	                headerTaxBreakUp.chargeName = headerTaxBreakUpIn.chargeName;
	                headerTaxBreakUp.invoicedTax = headerTaxBreakUpIn.invoicedTax;
	                headerTaxBreakUp.reference1 = headerTaxBreakUpIn.reference1;
	                headerTaxBreakUp.reference2 = headerTaxBreakUpIn.reference2;
	                headerTaxBreakUp.reference3 = headerTaxBreakUpIn.reference3;
	                headerTaxBreakUp.tax = 0.0;
	                headerTaxBreakUp.taxableFlag = headerTaxBreakUpIn.taxableFlag;
	                headerTaxBreakUp.taxName = headerTaxBreakUpIn.taxName;
	                headerTaxBreakUp.taxPercentage = 0.0;
	                headerTaxOutputList.add(headerTaxBreakUp);
	            }
	            headerTaxOutputStruct.colTax = headerTaxOutputList;
				headerTaxOutputStruct.tax = 0.0;
				headerTaxOutputStruct.taxPercentage = 0.0;
			}
		}else{
			try {
				Document headerTaxInDoc = XMLUtil.createDocument("YFSExtnHeaderTaxCalculationInputStruct");
				Element headerTaxInElem = headerTaxInDoc.getDocumentElement();
				headerTaxInElem.setAttribute("BForInvoice", Boolean.toString(headerTaxInputStruct.bForInvoice));
				headerTaxInElem.setAttribute("BForPacklistPrice", Boolean.toString(headerTaxInputStruct.bForPacklistPrice));
				headerTaxInElem.setAttribute("BLastInvoice", Boolean.toString(headerTaxInputStruct.bLastInvoice));
				headerTaxInElem.setAttribute("DiscountAmount", Double.toString(headerTaxInputStruct.discountAmount));
				headerTaxInElem.setAttribute("DocumentType", headerTaxInputStruct.documentType);
				headerTaxInElem.setAttribute("EnterpriseCode", headerTaxInputStruct.enterpriseCode);
				headerTaxInElem.setAttribute("HeaderHandlingCharges", Double
						.toString(headerTaxInputStruct.headerHandlingCharges));
				headerTaxInElem.setAttribute("HeaderPersonalizeCharges", Double
						.toString(headerTaxInputStruct.headerPersonalizeCharges));
				headerTaxInElem.setAttribute("HeaderShippingCharges", Double
						.toString(headerTaxInputStruct.headerShippingCharges));
				headerTaxInElem.setAttribute("InvoiceKey", headerTaxInputStruct.invoiceKey);
				headerTaxInElem.setAttribute("InvoiceMode", headerTaxInputStruct.invoiceMode);
				headerTaxInElem.setAttribute("OrderHeaderKey", headerTaxInputStruct.orderHeaderKey);
				headerTaxInElem.setAttribute("Purpose", headerTaxInputStruct.purpose);
				headerTaxInElem.setAttribute("ShipToCity", headerTaxInputStruct.shipToCity);
				headerTaxInElem.setAttribute("ShipToCountry", headerTaxInputStruct.shipToCountry);
				headerTaxInElem.setAttribute("ShipToId", headerTaxInputStruct.shipToId);
				headerTaxInElem.setAttribute("ShipToState", headerTaxInputStruct.shipToState);
				headerTaxInElem.setAttribute("ShipToZipCode", headerTaxInputStruct.shipToZipCode);
				headerTaxInElem.setAttribute("SShipNode", headerTaxInputStruct.sShipNode);
				headerTaxInElem.setAttribute("Tax", Double.toString(headerTaxInputStruct.tax));
				headerTaxInElem.setAttribute("TaxExemptFlag", headerTaxInputStruct.taxExemptFlag);
				headerTaxInElem.setAttribute("TaxExemptionCertificate", headerTaxInputStruct.taxExemptionCertificate);
				headerTaxInElem.setAttribute("TaxJurisdiction", headerTaxInputStruct.taxJurisdiction);
				headerTaxInElem.setAttribute("TaxpayerId", headerTaxInputStruct.taxpayerId);
				headerTaxInElem.setAttribute("TaxPercentage", Double.toString(headerTaxInputStruct.taxPercentage));

				Element headerChargeListElem = headerTaxInDoc.createElement("YFSExtnHeaderChargeStructList");
				headerTaxInElem.appendChild(headerChargeListElem);

				ArrayList<YFSExtnHeaderChargeStruct> headerChargeList = (ArrayList<YFSExtnHeaderChargeStruct>) headerTaxInputStruct.colCharge;

				Iterator<YFSExtnHeaderChargeStruct> it = headerChargeList.iterator();
				while (it.hasNext()) {
					YFSExtnHeaderChargeStruct headerChargeStruct = it.next();
					Element headerChargeElem = headerTaxInDoc.createElement("YFSExtnHeaderChargeStruct");
					headerChargeListElem.appendChild(headerChargeElem);
					headerChargeElem.setAttribute("ChargeAmount", Double.toString(headerChargeStruct.chargeAmount));
					headerChargeElem.setAttribute("ChargeCategory", headerChargeStruct.chargeCategory);
					headerChargeElem.setAttribute("ChargeName", headerChargeStruct.chargeName);
					headerChargeElem.setAttribute("InvoicedAmount", Double.toString(headerChargeStruct.invoicedAmount));
					headerChargeElem.setAttribute("Reference", headerChargeStruct.reference);
					Element extnChargeFieldsElem = headerTaxInDoc.createElement("EleExtendedFields");
					headerChargeElem.appendChild(extnChargeFieldsElem);
					Element extnElem = headerChargeStruct.eleExtendedFields.getDocumentElement();
					XMLUtil.copyElementAttributes(extnElem, extnChargeFieldsElem);
				}

				Element headerTaxBreakUpListElem = headerTaxInDoc.createElement("YFSExtnTaxBreakupList");
				headerTaxInElem.appendChild(headerTaxBreakUpListElem);

				ArrayList<YFSExtnTaxBreakup> headerTaxBreakUpList = (ArrayList<YFSExtnTaxBreakup>) headerTaxInputStruct.colTax;

				Iterator<YFSExtnTaxBreakup> taxIt = headerTaxBreakUpList.iterator();
				while (taxIt.hasNext()) {
					YFSExtnTaxBreakup headerTaxBreakUp = taxIt.next();
					Element headerTaxElem = headerTaxInDoc.createElement("YFSExtnTaxBreakup");
					headerTaxBreakUpListElem.appendChild(headerTaxElem);
					headerTaxElem.setAttribute("ChargeCategory", headerTaxBreakUp.chargeCategory);
					headerTaxElem.setAttribute("ChargeName", headerTaxBreakUp.chargeName);
					headerTaxElem.setAttribute("InvoicedTax", Double.toString(headerTaxBreakUp.invoicedTax));
					headerTaxElem.setAttribute("Reference1", headerTaxBreakUp.reference1);
					headerTaxElem.setAttribute("Reference2", headerTaxBreakUp.reference2);
					headerTaxElem.setAttribute("Reference3", headerTaxBreakUp.reference3);
					headerTaxElem.setAttribute("Tax", Double.toString(headerTaxBreakUp.tax));
					headerTaxElem.setAttribute("TaxableFlag", headerTaxBreakUp.taxableFlag);
					headerTaxElem.setAttribute("TaxName", headerTaxBreakUp.taxName);
					headerTaxElem.setAttribute("TaxPercentage", Double.toString(headerTaxBreakUp.taxPercentage));
					Element extnTaxFieldsElem = headerTaxInDoc.createElement("EleExtendedFields");
					headerTaxElem.appendChild(extnTaxFieldsElem);
					Element extnElem = headerTaxBreakUp.eleExtendedFields.getDocumentElement();
					XMLUtil.copyElementAttributes(extnElem, extnTaxFieldsElem);
				}

				logger.info("Input to recalculateHeaderTax: \n"+headerTaxInDoc);
				Document headerTaxCalcOutDoc = recalculateHeaderTax(env, headerTaxInDoc);
				logger.info("Output from recalculateHeaderTax: \n"+headerTaxCalcOutDoc);	            

				Element headerTaxOutElem = headerTaxCalcOutDoc.getDocumentElement();

				NodeList headerTaxNodeList = XPathUtil.getNodeList(headerTaxOutElem, "//HeaderTaxes/HeaderTax");
				int headerTaxesListLength = headerTaxNodeList.getLength();

				if(headerTaxesListLength > 0){

					ArrayList<YFSExtnTaxBreakup> headerTaxOutputList = new ArrayList<YFSExtnTaxBreakup>();
					for(int i=0;i<headerTaxesListLength;i++){
						Element headerTaxElem = (Element) headerTaxNodeList.item(i);
						YFSExtnTaxBreakup headerTaxBreakUp = new YFSExtnTaxBreakup();
						headerTaxBreakUp.chargeCategory = headerTaxElem.getAttribute("ChargeCategory");
						headerTaxBreakUp.chargeName = headerTaxElem.getAttribute("ChargeName");
						headerTaxBreakUp.reference1 = headerTaxElem.getAttribute("Reference1");
						headerTaxBreakUp.reference2 = headerTaxElem.getAttribute("Reference2");
						headerTaxBreakUp.reference3 = headerTaxElem.getAttribute("Reference3");
						headerTaxBreakUp.taxableFlag = headerTaxElem.getAttribute("TaxableFlag");
						headerTaxBreakUp.taxName = headerTaxElem.getAttribute("TaxName");

						if(!StringUtil.isEmpty(headerTaxElem.getAttribute("Tax")))
							headerTaxBreakUp.tax = Double.parseDouble(headerTaxElem.getAttribute("Tax"));
						if(!StringUtil.isEmpty(headerTaxElem.getAttribute("InvoicedTax")))
							headerTaxBreakUp.invoicedTax = Double.parseDouble(headerTaxElem.getAttribute("InvoicedTax"));
						if(!StringUtil.isEmpty(headerTaxElem.getAttribute("TaxPercentage")))
							headerTaxBreakUp.taxPercentage = Double.parseDouble(headerTaxElem.getAttribute("TaxPercentage"));

						if(null != headerTaxElem.getElementsByTagName("Extn").item(0)){
							headerTaxBreakUp.eleExtendedFields = XMLUtil.createDocument(headerTaxElem.getElementsByTagName("Extn").item(0));
						}

						headerTaxOutputList.add(headerTaxBreakUp);
					}

					headerTaxOutputStruct.colTax = headerTaxOutputList;

				}else{
					headerTaxOutputStruct.colTax = headerTaxInputStruct.colTax;
				}
			}catch(Exception e){
				logger.info("Error in FANYFSRecalculateHeaderTaxUEImpl: "+e.getMessage());
			}
		}
		return headerTaxOutputStruct;
	}

	private Document recalculateHeaderTax(YFSEnvironment env, Document inDoc) throws TransformerException, Exception {

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
