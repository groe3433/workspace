package com.fanatics.sterling.ue;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.GetAppeasementOffersConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.pca.ycd.japi.ue.YCDGetAppeasementOffersUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class FANYCDGetAppeasementOffersUEimpl implements YCDGetAppeasementOffersUE {

	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	@Override
	public Document getAppeasementOffers(YFSEnvironment env, Document inDoc) throws YFSUserExitException {

		logger.info("FANYCDGetAppeasementOffersUEimpl --> Start");

		logger.verbose("****************getAppeasementOffers************inDoc\n" + SCXmlUtil.getString(inDoc));

		try {

			// Get the Sales Order Header Key
			String sOrderHeaderKey 	= SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
					GetAppeasementOffersConstants.XPATH_ORDER_HEADER_KEY);
			String reasonCode 		= SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
					GetAppeasementOffersConstants.XPATH_REASON_CODE);
			
			String chargeName = "";
			
			logger.info("reasonCode: " + reasonCode);
			if(reasonCode.equals("Shipping Charge")){
				chargeName = "Shipping";
			}
			else if(reasonCode.equals("MRL Fee Waiver")){
				chargeName = "Freight Refund";
			}
			else if(reasonCode.equals("General")){
				chargeName = "Discount";
			}
			else{
				chargeName = "Discount";
			}

			logger.info("****************getAppeasementOffers************sOrderHeaderKey\n" + sOrderHeaderKey);

			// Create the return Doc
			Document docAppeasementOffers 	= SCXmlUtil
					.createDocument(GetAppeasementOffersConstants.EL_APPEASEMENT_OFFERS);
			//Document commonCodeInputDoc 	= SCXmlUtil.createDocument(GetAppeasementOffersConstants.EL_COMMON_CODE);

			//commonCodeInputDoc.getDocumentElement().setAttribute(GetAppeasementOffersConstants.ATT_CODE_TYPE,
			//		GetAppeasementOffersConstants.APPROVED_APPEASEMENTS);

			//logger.info("FANYCDGetAppeasementOffersUEimpl -> Invoking "
			//		+ GetAppeasementOffersConstants.API_COMMON_CODE_LIST + "API");

			//Document outputXML = CommonUtil.invokeAPI(env, GetAppeasementOffersConstants.API_COMMON_CODE_LIST,
			//		commonCodeInputDoc);

			//String headerChargeCategory = SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
					//GetAppeasementOffersConstants.XPATH_H_CHARGE_CATEGORY);
			//String headerChargeCategory = "ApprovedAppeasements";
			//String headerChargeName 	= SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
			//		GetAppeasementOffersConstants.XPATH_H_CHARGE_NAME);

			//Element rootMessageNode 	= outputXML.getDocumentElement();

			Element eleTempAppeasementOffers = docAppeasementOffers.getDocumentElement();

			//if (rootMessageNode != null) {

				//NodeList commonCodes = outputXML.getElementsByTagName(GetAppeasementOffersConstants.EL_COMMON_CODE);
				//if (commonCodes != null && commonCodes.getLength() > 0) {
					//for (int i = 0; i < commonCodes.getLength(); i++) {
						//if (commonCodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

							//Element commonCode 	= (Element) commonCodes.item(i);
							//String chargeName  	= commonCode.getAttribute(GetAppeasementOffersConstants.ATT_CODE_VALUE);
							//String codeType 	= commonCode.getAttribute(GetAppeasementOffersConstants.ATT_CODE_TYPE);

							eleTempAppeasementOffers.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_CATEGORY,
									"Discount");
							eleTempAppeasementOffers.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_NAME,
									chargeName);
							eleTempAppeasementOffers.setAttribute(GetAppeasementOffersConstants.ATT_ORDER_HEADER_KEY,
									sOrderHeaderKey);
							eleTempAppeasementOffers.setAttribute(GetAppeasementOffersConstants.ATT_REASON_CODE,
									reasonCode);

							Element eleTempAppeasementOffer = docAppeasementOffers
									.createElement(GetAppeasementOffersConstants.EL_APPEASEMENT_OFFER);
							Element eleTempOrder			= docAppeasementOffers
									.createElement(GetAppeasementOffersConstants.EL_ORDER);

							eleTempAppeasementOffer.setAttribute(GetAppeasementOffersConstants.ATT_OFFER_TYPE,
									"VARIABLE_AMOUNT_ORDER");
									//GetAppeasementOffersConstants.N);
							eleTempOrder.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_CATEGORY, "Discount");
							eleTempOrder.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_NAME, chargeName);
							
							/*
							Element eleTempAppeasementOffer2 = docAppeasementOffers
									.createElement(GetAppeasementOffersConstants.EL_APPEASEMENT_OFFER);
							Element eleTempOrder2 =  docAppeasementOffers
									.createElement(GetAppeasementOffersConstants.EL_ORDER);
							eleTempAppeasementOffer2.setAttribute(GetAppeasementOffersConstants.ATT_OFFER_TYPE,"FLAT_AMOUNT_ORDER");
							eleTempAppeasementOffer2.setAttribute("OfferAmount","10");
							
							eleTempOrder2.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_CATEGORY, codeType);
							eleTempOrder2.setAttribute(GetAppeasementOffersConstants.ATT_CHARGE_NAME, chargeName);
							*/
							
							eleTempAppeasementOffer.appendChild(eleTempOrder);
							//eleTempAppeasementOffer2.appendChild(eleTempOrder2);
							
							eleTempAppeasementOffers.appendChild(eleTempAppeasementOffer);
							//eleTempAppeasementOffers.appendChild(eleTempAppeasementOffer2);
						//}

					//}
				//}
			//}

			System.out.println("docAppeasementOffers: " + SCXmlUtil.getString(docAppeasementOffers));

			logger.info("FANYCDGetAppeasementOffersUEimpl --> End");

			return docAppeasementOffers;

		} catch (Exception e) {
			logger.error("FANYCDGetAppeasementOffersUEimpl --> ERROR: " + e.getMessage(), e.getCause());
			throw new YFSUserExitException("FANYCDGetAppeasementOffersUEimpl");
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, YFSUserExitException {
		
		//String orderInput=	"<Order AllAddressesVerified=\"N\" AuthorizationExpirationDate=\"2016-07-01T22:11:51-04:00\" BillToID=\"Cust0001\" BuyerOrganizationCode=\"FANATICS_US\" CarrierAccountNo=\"\" CarrierServiceCode=\"2 Day\" ChargeActualFreightFlag=\"N\" CustomerEMailID=\"iarumugam@fanatics.com\" CustomerPONo=\"\" DeliveryCode=\"\" Division=\"\" DocumentType=\"0001\" DraftOrderFlag=\"N\" EnterpriseCode=\"FANATICS_WEB\" EntryType=\"\" FreightTerms=\"\" HoldFlag=\"N\" HoldReasonCode=\"\" MaxOrderStatus=\"9000\" MaxOrderStatusDesc=\"Cancelled\" MinOrderStatus=\"9000\" MinOrderStatusDesc=\"Cancelled\" NotifyAfterShipmentFlag=\"N\" OrderDate=\"2016-03-28T14:15:26-04:00\" OrderHeaderKey=\"20160701221124198847\" OrderName=\"\" OrderNo=\"Y100001506\" OrderType=\"WEB\" OriginalTax=\"0.00\" OriginalTotalAmount=\"66.00\" OtherCharges=\"0.00\" PaymentStatus=\"AUTHORIZED\" PersonalizeCode=\"\" PriorityCode=\"NORMAL\" PriorityNumber=\"0\" Purpose=\"\" SCAC=\"\" ScacAndService=\"\" ScacAndServiceKey=\"\" SearchCriteria1=\"\" SearchCriteria2=\"\" SellerOrganizationCode=\"0951\" ShipToID=\"FANATICS_US\" Status=\"Cancelled\" TaxExemptFlag=\"N\" TaxExemptionCertificate=\"\" TaxJurisdiction=\"\" TaxPayerId=\"\" TermsCode=\"\" TotalAdjustmentAmount=\"0.00\" isHistory=\"N\"><OrderHoldTypes><OrderHoldType HoldType=\"PENDINGREVIEW\" Status=\"1300\"></OrderHoldType></OrderHoldTypes><OrderDates><OrderDate DateTypeId=\"TESTDATE\" CommittedDate=\"\"></OrderDate></OrderDates> </Order>";
		String orderInput = "<AppeasementOffers> <Order DisplayStatus=\"Draft Order Created\" DocumentType=\"0001\" EnterpriseCode=\"FANATICS_WEB\" EnterpriseName=\"Fanatics Web\" OrderHeaderKey=\"20160530180308188527\" OrderNo=\"21\"> <PriceInfo Currency=\"USD\" TotalAmount=\"66.00\"/> <OverallTotals GrandCharges=\"4.00\" GrandDiscount=\"2.00\" GrandTax=\"4.00\" GrandTotal=\"66.00\" HdrCharges=\"4.00\" HdrDiscount=\"0.00\" HdrTax=\"1.00\" HdrTotal=\"5.00\" LineSubTotal=\"60.00\"/> <HeaderCharges> <HeaderCharge ChargeAmount=\"4.00\" ChargeCategory=\"Shipping\" ChargeCategoryDescription=\"Shipping\" ChargeName=\"\" ChargeNameKey=\"\" InvoicedChargeAmount=\"0.00\" Reference=\"\" RemainingChargeAmount=\"4.00\"/> </HeaderCharges> <HeaderTaxes> <HeaderTax ChargeCategory=\"Shipping\" ChargeName=\"\" ChargeNameKey=\"\" InvoicedTax=\"0.00\" Reference_1=\"\" Reference_2=\"\" Reference_3=\"\" RemainingTax=\"1.00\" Tax=\"1.00\" TaxName=\"Shipping Tax\" TaxPercentage=\"0.00\" TaxableFlag=\"N\"/> </HeaderTaxes> <OrderLines> <OrderLine DisplayStatus=\"Created\" ItemGroupCode=\"PROD\" OpenQty=\"3.00\" OrderLineKey=\"20160530180310188528\" OrderedQty=\"3.00\" OtherCharges=\"-2.00\" RemainingQty=\"3.00\" Status=\"Created\" StatusQuantity=\"3.00\"> <Item DisplayUnitOfMeasure=\"Each\" ItemDesc=\"\" ItemID=\"SAN 01\" ItemShortDesc=\"\" ItemWeight=\"0.00\" UOMDisplayFormat=\"formattedQty\" UnitCost=\"20.00\" UnitOfMeasure=\"EACH\"/> <LinePriceInfo ActualPricingQty=\"3.00\" LineTotal=\"61.00\" OrderedPricingQty=\"3.00\" UnitPrice=\"20.00\"/> <LineOverallTotals Charges=\"0.00\" Discount=\"2.00\" ExtendedPrice=\"60.00\" LineTotal=\"61.00\" OptionPrice=\"0.00\" PricingQty=\"3.00\" Tax=\"3.00\" UnitPrice=\"20.00\"/> <LineCharges> <LineCharge ChargeAmount=\"2.00\" ChargeCategory=\"Discount\" ChargeName=\"\" ChargeNameKey=\"DEFAULT_CHARGE_KEY_14\" ChargePerLine=\"2.00\" ChargePerUnit=\"0.00\" InvoicedChargeAmount=\"0.00\" InvoicedChargePerLine=\"0.00\" InvoicedChargePerUnit=\"0.00\" Reference=\"\" RemainingChargeAmount=\"2.00\" RemainingChargePerLine=\"2.00\" RemainingChargePerUnit=\"0.00\"/> </LineCharges> <LineTaxes> <LineTax ChargeCategory=\"Price\" ChargeName=\"\" ChargeNameKey=\"\" InvoicedTax=\"0.00\" Reference_1=\"\" Reference_2=\"\" Reference_3=\"\" RemainingTax=\"3.00\" Tax=\"3.00\" TaxName=\"Line Tax\" TaxPercentage=\"0.00\"/> </LineTaxes> </OrderLine> </OrderLines> <AppeasementReason ReasonCode=\"Damaged Goods\"/> </Order> </AppeasementOffers>";
		Document inDoc = XMLUtil.getDocument(orderInput);
		
		FANYCDGetAppeasementOffersUEimpl appeasement = new FANYCDGetAppeasementOffersUEimpl();
		appeasement.getAppeasementOffers(null, inDoc);

	}

}
