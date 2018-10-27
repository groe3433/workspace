package com.fanatics.sterling.stub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanTaxResponse  {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);

	public Document internalTaxResponse(YFSEnvironment yfsEnv, Document inDoc) throws IllegalArgumentException, Exception{

		logger.info("Inside FanTaxResponse");
		logger.info("Input Document: "+XMLUtil.getXMLString(inDoc));
		Document outDoc = null;

		String sCallingClass = XMLUtil.getAttribute(inDoc.getDocumentElement(), "CallingClass");
		if(sCallingClass.contains("FANYFSRecalculateLineTaxUEImpl")){
			double dUnitPrice = Double.parseDouble(XMLUtil.getAttribute(inDoc.getDocumentElement(), "UnitPrice"));
			double dOrderQty = Double.parseDouble(XMLUtil.getAttribute(inDoc.getDocumentElement(), "OrderedQty"));
			double dLineTax = (dUnitPrice*dOrderQty)/10;


			outDoc = XMLUtil.createDocument("LineTaxes");

			NodeList nlYFSExtnTaxBreakup = XPathUtil.getNodeList(inDoc, "//YFSExtnLineTaxCalculationInputStruct/YFSExtnTaxBreakupList/YFSExtnTaxBreakup");
			for(int nlCount=0; nlCount < nlYFSExtnTaxBreakup.getLength(); nlCount++){
				Element eYFSExtnTaxBreakup = (Element) nlYFSExtnTaxBreakup.item(nlCount);
				Element eLineTax = XMLUtil.createChild(outDoc.getDocumentElement(), "LineTax");
				eLineTax.setAttribute("ChargeCategory", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "ChargeCategory"));
				eLineTax.setAttribute("ChargeName", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "ChargeName"));
				eLineTax.setAttribute("Reference1", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "Reference1"));
				eLineTax.setAttribute("Reference2", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "Reference2"));
				eLineTax.setAttribute("TaxableFlag", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxableFlag"));
				eLineTax.setAttribute("TaxName", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxName"));
				eLineTax.setAttribute("Tax", String.valueOf(dLineTax));
				eLineTax.setAttribute("InvoicedTax", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "InvoicedTax"));
				eLineTax.setAttribute("TaxPercentage", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxPercentage"));
				XMLUtil.createChild(eLineTax, "Extn");
			}

		}else if(sCallingClass.contains("FANYFSRecalculateHeaderTaxUEImpl")){

			outDoc = XMLUtil.createDocument("HeaderTaxes");

			NodeList nlYFSExtnTaxBreakup = XPathUtil.getNodeList(inDoc, "//YFSExtnHeaderTaxCalculationInputStruct/YFSExtnTaxBreakupList/YFSExtnTaxBreakup");
			for(int nlCount=0; nlCount < nlYFSExtnTaxBreakup.getLength(); nlCount++){
				Element eYFSExtnTaxBreakup = (Element) nlYFSExtnTaxBreakup.item(nlCount);
				Element eLineTax = XMLUtil.createChild(outDoc.getDocumentElement(), "HeaderTax");
				eLineTax.setAttribute("ChargeCategory", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "ChargeCategory"));
				eLineTax.setAttribute("ChargeName", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "ChargeName"));
				eLineTax.setAttribute("Reference1", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "Reference1"));
				eLineTax.setAttribute("Reference2", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "Reference2"));
				eLineTax.setAttribute("TaxableFlag", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxableFlag"));
				eLineTax.setAttribute("TaxName", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxName"));
				eLineTax.setAttribute("Tax", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "Tax"));
				eLineTax.setAttribute("InvoicedTax", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "InvoicedTax"));
				eLineTax.setAttribute("TaxPercentage", XMLUtil.getAttribute(eYFSExtnTaxBreakup, "TaxPercentage"));
				XMLUtil.createChild(eLineTax, "Extn");
			}
		}

		logger.info("Output Document: "+XMLUtil.getXMLString(outDoc));

		return outDoc;

	}

}
