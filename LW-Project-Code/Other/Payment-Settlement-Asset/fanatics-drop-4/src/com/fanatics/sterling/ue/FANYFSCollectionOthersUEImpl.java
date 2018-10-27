package com.fanatics.sterling.ue;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.pca.ycd.ue.utils.YCDPaymentUEXMLManager;
import com.yantra.shared.dbclasses.YFS_Charge_TransactionDBHome;
import com.yantra.shared.dbi.YFS_Charge_Transaction;
import com.yantra.shared.dbi.YFS_Order_Header;
//import com.yantra.shared.dbi.YFS_Payment;
import com.yantra.shared.dbi.YFS_Payment_Type;
import com.yantra.shared.ycd.YCDErrorCodes;
import com.yantra.shared.ycd.YCDServiceNames;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDataBuf;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;
import com.yantra.yfs.japi.ue.YFSCollectionOthersUE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.omg.CORBA.StringHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FANYFSCollectionOthersUEImpl implements YFSCollectionOthersUE,
YCDErrorCodes, YCDServiceNames {
	

	private static YFCLogCategory cat = YFCLogCategory
			.instance(FANYFSCollectionOthersUEImpl.class.getName());
	private static YCDFoundationBridge bridge = YCDFoundationBridge.getInstance();

	public FANYFSCollectionOthersUEImpl() {
		cat.verbose("Entering FANYFSCollectionOthersUEImpl");
	}

	public YFSExtnPaymentCollectionOutputStruct collectionOthers(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct paymentInput)
			throws YFSUserExitException {
		
		cat.beginTimer("FANYFSCollectionOthersUEImpl");
		
		
		YFSExtnPaymentCollectionOutputStruct paymentOutputInfo = new YFSExtnPaymentCollectionOutputStruct();
		try {
			YFCDocument paymentInputDoc = YCDPaymentUEXMLManager
					.convertPaymentInputStructToXML(paymentInput);
			
			paymentInputDoc = appendAdditionalInfoTopaymentInputDoc(env,paymentInputDoc);
			
			if (YFCObject.equals(paymentInput.paymentType, "ACCOUNT_BALANCE")) {
				
				YFCDocument collectionOutputDoc = executeCollectionAccountBalanceService(env,
						paymentInputDoc);
				
				paymentOutputInfo = constructCollectionOutput(env,
						collectionOutputDoc, paymentInputDoc,paymentInput);
				
			} else if (YFCObject.equals(paymentInput.paymentType, "PAYPAL")) {

				String chargeType = paymentInputDoc.getDocumentElement()
						.getAttribute("ChargeType");

				cat.verbose("-------------------------------1------------------------------");
				// check for blank authorizationID when we dont have actual
				// authorization.
				YFCElement paymentXML = paymentInputDoc.getDocumentElement();

				if ("CHARGE".equalsIgnoreCase(chargeType)) {
					String authorizationID = paymentXML
							.getAttribute("AuthorizationId");
					if ("".equalsIgnoreCase(authorizationID)) {
						authorizationID = "Dummy";
						paymentXML.setAttribute("AuthorizationId",
								authorizationID);
					}
				}

				//paymentInputDoc = appendAdditionalInfoTopaymentInputDoc(env,paymentInputDoc);
				cat.verbose("-------------------------------2------------------------------");
				// Execute the colelction service for PAYPAL
				YFCDocument collectionOutputDoc = executePaypalService(env,
						paymentInputDoc);

				// Process output.

					paymentOutputInfo = constructCollectionOutput(env,
							collectionOutputDoc, paymentInputDoc,paymentInput);
				
				
			} else {
				paymentOutputInfo = constructFailedOutputStruct(paymentInput,
						"");
			}
		} catch (Exception ex) {
			// YFCDocument collectionOutputDoc;
			cat
					.error(
							"Exception in the collectionOthers method of YCDCollectionOtherUEImpl ",
							ex);
			paymentOutputInfo = constructFailedOutputStruct(paymentInput, ex
					.getMessage());

			return paymentOutputInfo;
		} finally {
			cat.endTimer("YCDCollectionOtherUEImpl");
			cat.verbose("Exiting YCDCollectionOtherUEImpl");
		}
		
		return paymentOutputInfo;
		
	}

	public YFCDocument executePaypalService(YFSEnvironment env,
			YFCDocument paymentInputDoc) throws Exception {
		
		cat.verbose("---------------------Input to " +
				"YCD_ExecuteCollectionOTHERS_Proxy_1.0---------1------------"+XMLUtil.getXMLString(paymentInputDoc.getDocument()));
		
		
		YFCDocument collectionOutputDoc = YCDFoundationBridge.getInstance()
				.executeService((YFSContext) env,
						"YCD_ExecuteCollectionOTHERS_Proxy_1.0",
						paymentInputDoc);
		
		cat.verbose("---------------------Input to YCD_ExecuteCollectionOTHERS_Proxy_1.0---------2------------"+XMLUtil.getXMLString(collectionOutputDoc.getDocument()));
		

		if (cat.isDebugEnabled()) {
			cat.verbose("Collection Output XML = "
					+ collectionOutputDoc.getString());
		}

		return collectionOutputDoc;
	}

	public YFCDocument executeCollectionAccountBalanceService(YFSEnvironment env,
			YFCDocument paymentInputDoc) throws Exception {
		
		cat.verbose("---------------------Input to YCD_ExecuteCollectionOTHERS_Proxy_1.0---------3------------"+XMLUtil.getXMLString(paymentInputDoc.getDocument()));
		
		YFCDocument collectionOutputDoc = YCDFoundationBridge
				.getInstance()
				.executeService((YFSContext) env,
						"YCD_ExecuteCollectionOTHERS_Proxy_1.0", paymentInputDoc);
		
		cat.verbose("---------------------Input to YCD_ExecuteCollectionOTHERS_Proxy_1.0---------4------------"+XMLUtil.getXMLString(collectionOutputDoc.getDocument()));
		
		if (cat.isDebugEnabled()) {
			cat.verbose("Collection Output XML = "
					+ collectionOutputDoc.getString());
		}

		return collectionOutputDoc;
	}



	public YFSExtnPaymentCollectionOutputStruct constructCollectionOutput(YFSEnvironment env,
			YFCDocument paymentOutput, YFCDocument paymentInputDoc, YFSExtnPaymentCollectionInputStruct paymentInputStruct) throws Exception {
		
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();

		YFCElement root = paymentOutput.getDocumentElement();
		YFCElement paymentInputXML = paymentInputDoc.getDocumentElement();
		
	    YFSContext ctx = (YFSContext)env;

	        
	        String sOrderHeaderKey = paymentInputXML.getAttribute("OrderHeaderKey");
	        
	        YFS_Order_Header oOrder = bridge.getOrderHeader(ctx, sOrderHeaderKey, null, null, null, "WAIT", false);
	        
	        String sTranID = bridge.getFulfillmentTransactionId(ctx, "PAYMENT_EXECUTION", oOrder.getDocument_Type());
	        
	        String sResponseCode = root.getAttribute("ResponseCode");
	        String chargeType = root.getAttribute("ChargeType");

		String responseCode = root.getAttribute("ResponseCode");
		
		if ((!YFCObject.equals(sResponseCode, "APPROVED")) && (!YFCObject.equals(sResponseCode, "AUTHORIZED")) && (!YFCObject.equals(sResponseCode, "HARD_DECLINED")) && (!YFCObject.equals(sResponseCode, "SOFT_DECLINED")) && (!YFCObject.equals(sResponseCode, "BANK_HOLD")) && (!YFCObject.equals(sResponseCode, "SERVICE_UNAVAILABLE")))
        {
          YFCException ex = new YFCException("YCD00001");
          ex.setAttribute("ResponseCode", sResponseCode);
          throw ex;
        }

		
        if ("AUTHORIZATION".equalsIgnoreCase(chargeType) && YFCObject.equals(sResponseCode, "AUTHORIZED")) {
			
			
        	cat.verbose("--------------------------------AUTH-------------AUTHORIZED---1---------------------------------------------------");
			
			paymentOutputStruct.tranType = paymentInputDoc.getDocumentElement().getAttribute("ChargeType");
			paymentOutputStruct.authorizationId = paymentInputDoc.getDocumentElement().getAttribute("AuthorizationId");
			paymentOutputStruct.authorizationAmount = paymentInputDoc.getDocumentElement().getDoubleAttribute("RequestAmount");
			paymentOutputStruct.authReturnCode = "1";
			paymentOutputStruct.authReturnFlag = "Y";
			paymentOutputStruct.tranAmount = paymentInputDoc.getDocumentElement().getDoubleAttribute("RequestAmount");
			paymentOutputStruct.internalReturnCode = "1";
			paymentOutputStruct.internalReturnMessage = "AUTHORIZED";
			paymentOutputStruct.authCode = paymentOutput.getDocumentElement().getAttribute("AuthCode");

			paymentOutputStruct.executionDate = YTimestamp
					.newMutableTimestamp();

			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		   	Date date = new Date(System.currentTimeMillis()+ 43200*60*1000);
		   	String testDateString2 = dateFormat.format(date);
		   	DateFormat df2 = new SimpleDateFormat("YYYYMMDD");
		   	Date d2 = df2.parse(testDateString2);
		   	paymentOutputStruct.authorizationExpirationDate = "20161230000000";
		   	
		   	
			cat.verbose("-----------------------------------AUTH----------AUTHORIZED---2---------------------------------------------------");

		}
        else if("AUTHORIZATION".equalsIgnoreCase(chargeType) && YFCObject.equals(sResponseCode, "HARD_DECLINED")){

        	cat.verbose("----------------------------------AUTH-----------HARD_DECLINED---1---------------------------------------------------");
			
			paymentOutputStruct.authorizationAmount = 0.0D;
			paymentOutputStruct.retryFlag = "N";
			paymentOutputStruct.suspendPayment = "B";
			paymentOutputStruct.holdReason = "PAYMENT_AUTH_HOLD";
	        paymentOutputStruct.holdOrderAndRaiseEvent = true;

			paymentOutputStruct.tranType = paymentInputXML
					.getAttribute("ChargeType");
			paymentOutputStruct.tranAmount = 0.0D;

			String reasonCode = root.getAttribute("ReasonCode");
			if (!YFCObject.isNull(reasonCode) && !YFCObject.isVoid(reasonCode)) {
				if (reasonCode.length() > 20) {
					paymentOutputStruct.authReturnCode = reasonCode.substring(
							0, 20);
				} else {
					paymentOutputStruct.authReturnCode = reasonCode;
				}
			}

			String reasonDesc = root.getAttribute("ReasonDesc");
			if (!YFCObject.isNull(reasonDesc) && !YFCObject.isVoid(reasonDesc)) {
				if (reasonDesc.length() > 255) {
					paymentOutputStruct.authReturnMessage = reasonDesc
							.substring(0, 255);
				} else {
					paymentOutputStruct.authReturnMessage = reasonDesc;
				}
			}


			String tranReturnMessage = root.getAttribute("TranReturnMessage");
			if (!YFCObject.isNull(tranReturnMessage) && !YFCObject.isVoid(tranReturnMessage)) {
				if (tranReturnMessage.length() > 255) {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage
							.substring(0, 255);
				} else {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage;
				}
			} 

	          YFCDocument eventDoc = prepareEventDoc(ctx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
	          raiseEvent(ctx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
			
		   	
			cat.verbose("-----------------------------AUTH--------HARD_DECLINED--------2---------------------------------------------------");

		
        } 
        else if ("APPROVED".equalsIgnoreCase(responseCode) && "CHARGE".equalsIgnoreCase(chargeType)) {
			
			cat.verbose("---------------------------------------------APPROVED---1---------------------------------------------------");
			
	          if (!YFCObject.isVoid(root.getAttribute("AuthorizationAmount"))) {
		            paymentOutputStruct.authorizationAmount = root.getDoubleAttribute("AuthorizationAmount");
		          } else {
		            paymentOutputStruct.authorizationAmount = 0.0f;
		          }
	        
	        if (!YFCObject.isVoid(root.getAttribute("TranAmount"))) {
	      	  paymentOutputStruct.tranAmount = Double.parseDouble(root.getAttribute("TranAmount"));
		          } else {
		            paymentOutputStruct.tranAmount = 0.0f;
		          }
	        
	        if (!YFCObject.isVoid(root.getAttribute("AuthorizationExpirationDate"))) {
	        	paymentOutputStruct.authorizationExpirationDate = root.getAttribute("AuthorizationExpirationDate");
	          } 
	      
	      if (!YFCObject.isVoid(root.getAttribute("AuthTime"))) {
	    	  paymentOutputStruct.authTime = root.getAttribute("AuthTime");
	          } 
	      
	      if (!YFCObject.isVoid(paymentInputXML.getAttribute("ChargeType"))) {
	    	  paymentOutputStruct.tranType = paymentInputXML.getAttribute("ChargeType");
	          } 

	      if (!YFCObject.isVoid(paymentInputXML.getAttribute("AuthorizationId"))) {
	    	  paymentOutputStruct.authorizationId = paymentInputXML.getAttribute("AuthorizationId");
	          }
	      
			paymentOutputStruct.authReturnMessage = "Approved.";
			paymentOutputStruct.authReturnCode = "1";
			paymentOutputStruct.authReturnFlag = "Y";
			paymentOutputStruct.internalReturnCode = "1";
			paymentOutputStruct.internalReturnMessage = "APPROVED";

			paymentOutputStruct.executionDate = YTimestamp
					.newMutableTimestamp();

			
			cat.verbose("---------------------------------------------APPROVED---2---------------------------------------------------");

		} else if ("HARD_DECLINED".equalsIgnoreCase(responseCode) && "CHARGE".equalsIgnoreCase(chargeType)) {
			
			cat.verbose("---------------------------------------------HARD_DECLINED---1---------------------------------------------------");

			paymentOutputStruct.authorizationAmount = 0.0D;
			paymentOutputStruct.retryFlag = "N";
			paymentOutputStruct.suspendPayment = "B";
			paymentOutputStruct.holdReason = "PAYMENT_CAPTURE_HOLD";
	        paymentOutputStruct.holdOrderAndRaiseEvent = true;

			paymentOutputStruct.tranType = paymentInputXML
					.getAttribute("ChargeType");
			paymentOutputStruct.tranAmount = 0.0D;

			String reasonCode = root.getAttribute("ReasonCode");
			if (!YFCObject.isNull(reasonCode) && !YFCObject.isVoid(reasonCode)) {
				if (reasonCode.length() > 20) {
					paymentOutputStruct.authReturnCode = reasonCode.substring(
							0, 20);
				} else {
					paymentOutputStruct.authReturnCode = reasonCode;
				}
			}

			String reasonDesc = root.getAttribute("ReasonDesc");
			if (!YFCObject.isNull(reasonDesc) && !YFCObject.isVoid(reasonDesc)) {
				if (reasonDesc.length() > 255) {
					paymentOutputStruct.authReturnMessage = reasonDesc
							.substring(0, 255);
				} else {
					paymentOutputStruct.authReturnMessage = reasonDesc;
				}
			}


			String tranReturnMessage = root.getAttribute("TranReturnMessage");
			if (!YFCObject.isNull(tranReturnMessage) && !YFCObject.isVoid(tranReturnMessage)) {
				if (tranReturnMessage.length() > 255) {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage
							.substring(0, 255);
				} else {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage;
				}
			} 

	          YFCDocument eventDoc = prepareEventDoc(ctx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
	          raiseEvent(ctx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
			
			cat.verbose("---------------------------------------------HARD_DECLINED---2---------------------------------------------------");
			
		}

		else if ("SERVICE_UNAVAILABLE".equalsIgnoreCase(responseCode) && "CHARGE".equalsIgnoreCase(chargeType)) {

			cat.verbose("---------------------------------------------SERVICE_UNAVAILABLE---1---------------------------------------------------");
			
				paymentOutputStruct.authorizationAmount = 0.0D;
				paymentOutputStruct.retryFlag = "Y";
				paymentOutputStruct.suspendPayment = "N";
			
				modifyRetryInterval(paymentOutputStruct, env);
			
			String tranReturnMessage = root.getAttribute("TranReturnMessage");
			if (!YFCObject.isNull(tranReturnMessage) && !YFCObject.isVoid(tranReturnMessage)) {
				if (tranReturnMessage.length() > 255) {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage
							.substring(0, 255);
				} else {
					paymentOutputStruct.tranReturnMessage = tranReturnMessage;
				}
			}
			
	          YFCDocument eventDoc = prepareEventDoc(ctx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
	          raiseEvent(ctx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
			
			cat.verbose("---------------------------------------------SERVICE_UNAVAILABLE---2---------------------------------------------------");
			
		} 
		
		

		return paymentOutputStruct;
	}

	
	  //append additional data to input xml
		private YFCDocument appendAdditionalInfoTopaymentInputDoc(
				YFSEnvironment env, YFCDocument paymentInputDoc) {
			

			// TODO Auto-generated method stub
			//CustomerPONo
			
			try {
				
				YFCElement paymentInputEle = paymentInputDoc.getDocumentElement();
				paymentInputEle.setAttribute("Region", "US");
				paymentInputEle.setAttribute("CurrencyCode", "USD");
				
				
				Document orderDoc = XMLUtil.createDocument("Order");
				Element orderEle = orderDoc.getDocumentElement();
				orderEle.setAttribute("OrderHeaderKey", paymentInputEle.getAttribute("OrderHeaderKey"));

				
				cat.verbose("--------------FANYFSCollectionOthersUEImpl------6---"
							+XMLUtil.getXMLString(orderDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"template/api/getOrderList_payment.xml","getOrderList", orderDoc);
				
				cat.verbose("--------------FANYFSCollectionOthersUEImpl------7---"
						+XMLUtil.getXMLString(outDoc));
				
				Element outEle = (Element)outDoc.getDocumentElement().getElementsByTagName("Order").item(0);
				
				//paymentInputEle.setAttribute("BillToId", outEle.getAttribute("BillToID"));
				paymentInputEle.setAttribute("PartnerSiteId", outEle.getAttribute("SellerOrganizationCode"));
				
				// logic to get the LastCapture attribute for Paypal
				if("PAYPAL".equalsIgnoreCase(paymentInputEle.getAttribute("PaymentType"))){
					
					paymentInputEle.setAttribute("LastCapture", "N");
					
				}
				
						} catch (Exception e) {
				
								e.printStackTrace();
						}
			
			
			return paymentInputDoc;
		
		}
	

//		// create alert for hard decline
//		  private void createAlert(YFSEnvironment env, String orderHeaderKey, String orderNo) throws Exception{
//
//				
//				Document inboxDoc = XMLUtil.createDocument("Inbox");
//				Element inboxEle = inboxDoc.getDocumentElement();
//				
//				inboxEle.setAttribute("ActiveFlag", "Y");
//				inboxEle.setAttribute("ApiName", "executeCollection");
//				inboxEle.setAttribute("OrderHeaderKey", orderHeaderKey);
//				inboxEle.setAttribute("OrderNo", orderNo);
//				inboxEle.setAttribute("QueueId", "YCD_PAYMENT_SERVICE_UNAVAIL");
//				inboxEle.setAttribute("ExceptionType", "PaymentExecutionException");
//				
//				Element consolidationTemplate = inboxDoc.createElement("ConsolidationTemplate");
//				Element inboxTem = inboxDoc.createElement("Inbox");
//				inboxTem.setAttribute("ExceptionType", "");
//				inboxTem.setAttribute("OrderHeaderKey", "");
//				
//				consolidationTemplate.appendChild(inboxTem);
//				inboxEle.appendChild(consolidationTemplate);
//				
//				
//				cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------10---"
//							+XMLUtil.getXMLString(inboxDoc));
//				
//				Document outDoc = CommonUtil.invokeAPI
//						(env,"createException", inboxDoc);
//				
//				cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------11---"
//						+XMLUtil.getXMLString(outDoc));
//				
//			
//		}
		  
		  
			private void raiseEvent(YFSContext oCtx, YFS_Order_Header oOrder, YFSExtnPaymentCollectionInputStruct paymentInputStruct, YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFCDocument eventDoc, String sTranID)
			  {
				cat.verbose("-------------------------------------raiseEvent------------------1-------------------");
				
			    boolean bEventReqd = bridge.isRaiseEventRequired(oCtx, sTranID, "COLLECTION_FAILED");
			    if (bEventReqd)
			    {
			      YFCDataBuf sKeyData = oOrder.getOrderHeaderBuf(oCtx);
			      
			      bridge.raiseEvent(oCtx, sKeyData, eventDoc, sTranID, "COLLECTION_FAILED");
			    }
			    cat.verbose("-------------------------------------raiseEvent------------------2-------------------");
			  }
		  
		  
		  private YFCDocument getTemplate(YFSContext oCtx, YFS_Order_Header oOrder, String sTranID)
		  {
			  cat.verbose("-------------------------------------getTemplate------------------1-------------------");
		    Map bufParams = new HashMap();
		    bufParams.put("EnterpriseCode", oOrder.getEnterprise_Key());
		    bufParams.put("DocumentType", oOrder.getDocument_Type());
		    
		    YFCDocument templateDoc = bridge.getEventTemplate(oCtx, sTranID, "COLLECTION_FAILED", oOrder.getEnterprise_Key(), oOrder.getDocument_Type());
		    
		    cat.verbose("-------------------------------------getTemplate------------------2-------------------");
		    
		    return templateDoc;
		  }
		  
		  
		  
		  private YFCDocument prepareEventDoc(YFSContext oCtx, YFS_Order_Header oOrder, YFSExtnPaymentCollectionInputStruct paymentInputStruct, YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, String sResponseCode, String sTranID)
		  {
			  cat.verbose("-------------------------------------prepareEventDoc------------------1-------------------");
		    YFCDocument templateDoc = getTemplate(oCtx, oOrder, sTranID);
		    YFCDocument eventDoc = bridge.getOrderDetails(oCtx, oOrder, templateDoc);
		    
		    YFCElement root = eventDoc.getDocumentElement();
		    if (root != null)
		    {
		      YFCElement failureDetails = root.createChild("CollectionFailureDetails");
		      failureDetails.setAttribute("FailureReasonCode", sResponseCode);
		      failureDetails.setAttribute("AuthReturnCode", paymentOutputStruct.authReturnCode);
		      failureDetails.setAttribute("AuthReturnFlag", paymentOutputStruct.authReturnFlag);
		      failureDetails.setAttribute("AuthReturnMessage", paymentOutputStruct.authReturnMessage);
		      failureDetails.setAttribute("ChargeType", paymentOutputStruct.tranType);
		      failureDetails.setAttribute("CreditCardExpirationDate", paymentInputStruct.creditCardExpirationDate);
		      failureDetails.setAttribute("CreditCardName", paymentInputStruct.creditCardName);
		      failureDetails.setAttribute("CreditCardNo", paymentInputStruct.creditCardNo);
		      failureDetails.setAttribute("CreditCardType", paymentInputStruct.creditCardType);
		      failureDetails.setAttribute("DisplaySvcNo", paymentOutputStruct.DisplaySvcNo);
		      failureDetails.setAttribute("HoldOrderAndRaiseEvent", paymentOutputStruct.holdOrderAndRaiseEvent);
		      failureDetails.setAttribute("HoldReason", paymentOutputStruct.holdReason);
		      failureDetails.setAttribute("InternalReturnCode", paymentOutputStruct.internalReturnCode);
		      failureDetails.setAttribute("InternalReturnFlag", paymentOutputStruct.internalReturnFlag);
		      failureDetails.setAttribute("InternalReturnMessage", paymentOutputStruct.internalReturnMessage);
		      failureDetails.setAttribute("PaymentReference1", paymentOutputStruct.PaymentReference1);
		      failureDetails.setAttribute("PaymentReference2", paymentOutputStruct.PaymentReference2);
		      failureDetails.setAttribute("PaymentReference3", paymentOutputStruct.PaymentReference3);
		      failureDetails.setAttribute("PaymentType", paymentInputStruct.paymentType);
		      failureDetails.setAttribute("RequestAmount", paymentInputStruct.requestAmount);
		      failureDetails.setAttribute("SvcNo", paymentInputStruct.svcNo);
		      failureDetails.setAttribute("TranAmount", paymentOutputStruct.tranAmount);
		      failureDetails.setAttribute("TranRequestTime", paymentOutputStruct.tranRequestTime);
		      failureDetails.setAttribute("TranReturnCode", paymentOutputStruct.tranReturnCode);
		      failureDetails.setAttribute("TranReturnFlag", paymentOutputStruct.tranReturnFlag);
		      failureDetails.setAttribute("TranReturnMessage", paymentOutputStruct.tranReturnMessage);
		      failureDetails.setAttribute("TranType", paymentOutputStruct.tranType);
		      failureDetails.setAttribute("AuthTime", paymentOutputStruct.authTime);
		      failureDetails.setAttribute("AuthAVS", paymentOutputStruct.authAVS);
		      failureDetails.setAttribute("CVVAuthCode", paymentOutputStruct.sCVVAuthCode);
		      
		      YFS_Charge_Transaction oChgTran = YFS_Charge_TransactionDBHome.getInstance().selectWithPK(oCtx, paymentInputStruct.chargeTransactionKey);
		      if (oChgTran != null)
		      {
		        YFS_Payment_Type oType = oChgTran.getPayment().getPaymentType();
		        if (oType != null) {
		          failureDetails.setAttribute("PaymentTypeGroup", oType.getPayment_Type_Group());
		        }
		        failureDetails.setAttribute("DisplayCreditCardNo", "************" + oChgTran.getPayment().getDisplay_Credit_Card_No());
		      }
		    }
		    cat.verbose("-------------------------------------prepareEventDoc------------------2-------------------");
		    return eventDoc;
		  }
		  
		  
		  
		  
		  private void modifyRetryInterval(
					YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFSEnvironment env) throws Exception{
					// TODO Auto-generated method stub
				  
				Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
				Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
				ordercommonCodeEle.setAttribute("CodeValue", "RetryInterval");

				
				cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------12--"
							+XMLUtil.getXMLString(commonCodeDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"getCommonCodeList", commonCodeDoc);
				
				cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------13---"
						+XMLUtil.getXMLString(outDoc));
				
				Element outEle = (Element)outDoc.getDocumentElement().getElementsByTagName("CommonCode").item(0);
				
				String codeShortDescription = outEle.getAttribute("CodeShortDescription");
			      
				int retryInterval = Integer.parseInt(codeShortDescription);
			      
				cat.verbose("-------------------------retryInterval-------------------------"+retryInterval);
				
				    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
				   	Date date = new Date(System.currentTimeMillis()+ retryInterval*60*1000);
				   	String testDateString2 = dateFormat.format(date);
				   	DateFormat df2 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
				   	Date d2 = df2.parse(testDateString2);
				   	cat.verbose("----Date in MM-dd-yyyy HH:mm:ss format is: "+df2.format(d2));
				   	
				   	paymentOutputStruct.collectionDate = d2;
				
			}
		  
	
	public YFSExtnPaymentCollectionOutputStruct constructFailedOutputStruct(
			YFSExtnPaymentCollectionInputStruct paymentInput, String sHoldReason) {
		YFSExtnPaymentCollectionOutputStruct paymentOutputInfo = new YFSExtnPaymentCollectionOutputStruct();

		paymentOutputInfo.asynchRequestProcess = false;
		paymentOutputInfo.authReturnMessage = sHoldReason;
		paymentOutputInfo.holdOrderAndRaiseEvent = false;
		paymentOutputInfo.holdReason = sHoldReason;
		paymentOutputInfo.retryFlag = "N";
		paymentOutputInfo.suspendPayment = "B";
		paymentOutputInfo.tranType = paymentInput.chargeType;
		paymentOutputInfo.authorizationAmount = 0.0D;
		paymentOutputInfo.tranAmount = 0.0D;
		paymentOutputInfo.tranReturnMessage = "Invalid payment Info or critical data absent";

		return paymentOutputInfo;
	}
	
}
