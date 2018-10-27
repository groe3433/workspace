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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//import java.util.Random;
import java.util.TimeZone;

import org.omg.CORBA.StringHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class FANYFSCollectionCreditCardUEImpl 
implements YFSCollectionCreditCardUE, YCDErrorCodes, YCDServiceNames
{

	public static YFCLogCategory cat = YFCLogCategory.instance(FANYFSCollectionCreditCardUEImpl.class);
	  private static YCDFoundationBridge bridge = YCDFoundationBridge.getInstance();
	  
	  public FANYFSCollectionCreditCardUEImpl()
	  {
		cat.verbose("-------------------------------FANYFSCollectionCreditCardUEImpl---------------0----1---------------"); 
	   cat.verbose("Entering FANYFSCollectionCreditCardUEImpl");
	  }
	  
	  public YFSExtnPaymentCollectionOutputStruct collectionCreditCard(YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct)
	    throws YFSUserExitException
	  {
		  
		cat.verbose("-------------------------------FANYFSCollectionCreditCardUEImpl---------------0-------------------"); 
	    cat.beginTimer("FANYFSCollectionCreditCardUEImpl");
	    
	        
	    YFSContext oCtx = (YFSContext)oEnv;
	    
	    YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
	    
	   cat.verbose("-------------------------------FANYFSCollectionCreditCardUEImpl---------------1-------------------");
		
	    try
	    {
	      YFCDocument paymentInputDoc = YCDPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);

	      
	      
	      paymentInputDoc = appendAdditionalInfoTopaymentInputDoc(oEnv,paymentInputDoc);
	      
			cat.verbose("---------------------Input to " +
					"YCD_ExecuteCollectionCreditCard_Proxy_1.0---------1------------"+XMLUtil.getXMLString(paymentInputDoc.getDocument()));
			
	      YFCDocument collectionOutputDoc = executeCreditCardService(oCtx, "YCD_ExecuteCollectionCreditCard_Proxy_1.0", paymentInputDoc);
	      //YFCDocument collectionOutputDoc = executeCreditCardService(oCtx, "PaymentStub", paymentInputDoc);
	      
	     cat.verbose("Collection Output XML = " + collectionOutputDoc.getString());
	      
	      if (collectionOutputDoc != null)
	      {
	        YFCElement root = collectionOutputDoc.getDocumentElement();
	        String chargeType = root.getAttribute("ChargeType");
	        String sOrderHeaderKey = paymentInputDoc.getDocumentElement().getAttribute("OrderHeaderKey");
	        
	        YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, "WAIT", false);
	        
	        String sTranID = bridge.getFulfillmentTransactionId(oCtx, "PAYMENT_EXECUTION", oOrder.getDocument_Type());
	        
	        String sResponseCode = root.getAttribute("ResponseCode");
	        
	        if ((!YFCObject.equals(sResponseCode, "APPROVED")) && (!YFCObject.equals(sResponseCode, "AUTHORIZED")) && (!YFCObject.equals(sResponseCode, "HARD_DECLINED")) && (!YFCObject.equals(sResponseCode, "SOFT_DECLINED")) && (!YFCObject.equals(sResponseCode, "BANK_HOLD")) && (!YFCObject.equals(sResponseCode, "SERVICE_UNAVAILABLE")))
	        {
	          YFCException ex = new YFCException("YCD00001");
	          ex.setAttribute("ResponseCode", sResponseCode);
	          throw ex;
	        }
	        paymentOutputStruct = constructBasicCollectionCreditCardOutput(collectionOutputDoc);
	        
	        
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
				paymentOutputStruct.authCode = collectionOutputDoc.getDocumentElement().getAttribute("AuthCode");

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
		          paymentOutputStruct.holdReason = "PAYMENT_AUTH_HOLD";
		          paymentOutputStruct.holdOrderAndRaiseEvent = true;
		          //paymentOutputStruct.suspendPayment = "B";
		          paymentOutputStruct.tranAmount = 0.0D;
		          paymentOutputStruct.tranReturnFlag = "F";
		          
		          
		          
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
		          
					
					
					
		          
		          
		          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("retryFlag"))) {
		            paymentOutputStruct.retryFlag = collectionOutputDoc.getDocumentElement().getAttribute("retryFlag");
		          } else {
		            paymentOutputStruct.retryFlag = "N";
		          }
		          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment"))) {
		            paymentOutputStruct.suspendPayment = collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment");
		          } else {
		            paymentOutputStruct.suspendPayment = "B";
		          }
		          YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
		          
//		          if (bAuthStrikeLimitReached)
//		          {
//		        	  YFCElement eventRoot = eventDoc.getDocumentElement();
//		              YFCElement failureElem = eventRoot.getChildElement("CollectionFailureDetails");
//		              failureElem.setAttribute("FailureReasonCode", "YCD_AUTH_RETRY_LIMIT");
//		          }
		          raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
			   	
				cat.verbose("-----------------------------AUTH--------HARD_DECLINED--------2---------------------------------------------------");

			
	        } 
	        else if (YFCObject.equals(sResponseCode, "HARD_DECLINED") && "CHARGE".equalsIgnoreCase(chargeType))
	        {
	        	cat.verbose("-------------------------------------HARD_DECLINED------------------1-------------------");
	        	
	          
	          paymentOutputStruct.authorizationAmount = 0.0D;
	          paymentOutputStruct.holdReason = "PAYMENT_CAPTURE_HOLD";
	          paymentOutputStruct.holdOrderAndRaiseEvent = true;
	          //paymentOutputStruct.suspendPayment = "B";
	          paymentOutputStruct.tranAmount = 0.0D;
	          paymentOutputStruct.tranReturnFlag = "F";
	          
	          
	          
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
	          
				
				
				
	          
	          
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("retryFlag"))) {
	            paymentOutputStruct.retryFlag = collectionOutputDoc.getDocumentElement().getAttribute("retryFlag");
	          } else {
	            paymentOutputStruct.retryFlag = "N";
	          }
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment"))) {
	            paymentOutputStruct.suspendPayment = collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment");
	          } else {
	            paymentOutputStruct.suspendPayment = "B";
	          }
	          YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
	          
//	          if (bAuthStrikeLimitReached)
//	          {
//	        	  YFCElement eventRoot = eventDoc.getDocumentElement();
//	              YFCElement failureElem = eventRoot.getChildElement("CollectionFailureDetails");
//	              failureElem.setAttribute("FailureReasonCode", "YCD_AUTH_RETRY_LIMIT");
//	          }
	          raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
	          
	         cat.verbose("-------------------------------------HARD_DECLINED------------------2-------------------");
	        }
	        else if (YFCObject.equals(sResponseCode, "SERVICE_UNAVAILABLE") && "CHARGE".equalsIgnoreCase(chargeType))
	        {
	        	cat.verbose("-------------------------------------SERVICE_UNAVAILABLE------------------1-------------------");
				paymentOutputStruct.authorizationAmount = 0.0D;
				
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("retryFlag"))) {
	            paymentOutputStruct.retryFlag = collectionOutputDoc.getDocumentElement().getAttribute("retryFlag");
	          } else {
	            paymentOutputStruct.retryFlag = "Y";
	          }
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment"))) {
	            paymentOutputStruct.suspendPayment = collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment");
	          } else {
	            paymentOutputStruct.suspendPayment = "N";
	          }
	          
	          // modifying CollectionDate and setting it to a future time
	          modifyRetryInterval(paymentOutputStruct, oEnv);
	   	   
	          
	          YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
	          raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
	          
	          String tranReturnMessage = root.getAttribute("TranReturnMessage");
				if (!YFCObject.isNull(tranReturnMessage) && !YFCObject.isVoid(tranReturnMessage)) {
					if (tranReturnMessage.length() > 255) {
						paymentOutputStruct.tranReturnMessage = tranReturnMessage
								.substring(0, 255);
					} else {
						paymentOutputStruct.tranReturnMessage = tranReturnMessage;
					}
				}
	          
	          
	         cat.verbose("---------------------------------SERVICE_UNAVAILABLE-------2-----------------------------");
	          
	        }
	        else if (YFCObject.equals(sResponseCode, "APPROVED") && "CHARGE".equalsIgnoreCase(chargeType))
	        {
	        	cat.verbose("-------------------------------------APPROVED------------------1-------------------");
	        	
	          

	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("retryFlag"))) {
	            paymentOutputStruct.retryFlag = collectionOutputDoc.getDocumentElement().getAttribute("retryFlag");
	          } else {
	            paymentOutputStruct.retryFlag = "N";
	          }
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment"))) {
	            paymentOutputStruct.suspendPayment = collectionOutputDoc.getDocumentElement().getAttribute("suspendPayment");
	          } else {
	            paymentOutputStruct.suspendPayment = "N";
	          }
	          
	          
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("AuthorizationAmount"))) {
		            paymentOutputStruct.authorizationAmount = collectionOutputDoc.getDocumentElement().getDoubleAttribute("AuthorizationAmount");
		          } else {
		            paymentOutputStruct.authorizationAmount = 0.0f;
		          }
	          
	          if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("TranAmount"))) {
	        	  paymentOutputStruct.tranAmount = Double.parseDouble(collectionOutputDoc.getDocumentElement().getAttribute("TranAmount"));
		          } else {
		            paymentOutputStruct.tranAmount = 0.0f;
		          }
	          
	          
	          
				cat.verbose("-------------------------------------APPROVED------------------2-------------------");
				
	        }
	        
	        
	        
	        
	        
	      }
	    }
	    catch (YFCException e)
	    {
	      throw e;
	    }
	    catch (Exception e)
	    {
	      YFSUserExitException ex = new YFSUserExitException(e.getMessage());
	      throw ex;
	    }
	    finally
	    {
	      cat.endTimer("FANYFSCollectionCreditCardUEImpl");
	     cat.verbose("Exiting FANYFSCollectionCreditCardUEImpl");
	    }
	    return paymentOutputStruct;
	  }
	  

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
	  
	  private YFCDocument executeCreditCardService(YFSContext oCtx, String sServiceName, YFCDocument serviceInputDoc)
	    throws Exception
	  {
		 cat.verbose("-------------------------------------executeCreditCardService------------------1-------------------");
	    YFCDocument serviceOutputDoc = bridge.executeService(oCtx, sServiceName, serviceInputDoc);
	   cat.verbose("-------------------------------------executeCreditCardService------------------2-------------------");
	    return serviceOutputDoc;
	  }
	  

	  
	  private YFSExtnPaymentCollectionOutputStruct constructBasicCollectionCreditCardOutput(YFCDocument paymentOutputDoc)
	  {
		 cat.verbose("-------------------------------------constructBasicCollectionCreditCardOutput------------------1------------------");
	    YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
	    try
	    {
	      //YCDPaymentUEXMLManager.populatePaymentOutputStructFromXML(paymentOutputStruct, paymentOutputDoc);
	      
//			cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------2----"
//			+XMLUtil.getXMLString(paymentInputDoc.getDocument()));
			

	      paymentOutputStruct.authCode="";
	      paymentOutputStruct.authorizationAmount=0.0f;
	      paymentOutputStruct.authorizationExpirationDate="";
	      paymentOutputStruct.authTime="";
	      paymentOutputStruct.authorizationId="";
	      paymentOutputStruct.authReturnCode="";
	      paymentOutputStruct.authReturnMessage="";
	      paymentOutputStruct.tranReturnCode="";
	      //paymentOutputStruct.collectionDate="";
	      paymentOutputStruct.tranAmount=0.0f;
	      paymentOutputStruct.tranReturnMessage="";
	      paymentOutputStruct.PaymentReference1="";
	      paymentOutputStruct.internalReturnCode="";
	      paymentOutputStruct.internalReturnMessage="";

	     
	      
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	   cat.verbose("-------------------------------------constructBasicCollectionCreditCardOutput------------------2------------------");
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
					
					String authorizationId = paymentInputEle.getAttribute("AuthorizationId");
					
					if(authorizationId.trim().equalsIgnoreCase("")){
						
						Random rand = new Random();
						int  n = rand.nextInt(9999) + 1000;
						authorizationId = paymentInputEle.getAttribute("OrderHeaderKey") + n;
						paymentInputEle.setAttribute("AuthorizationId", authorizationId);
					}
					
					Document orderDoc = XMLUtil.createDocument("Order");
					Element orderEle = orderDoc.getDocumentElement();
					orderEle.setAttribute("OrderHeaderKey", paymentInputEle.getAttribute("OrderHeaderKey"));

					
					cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------10---"
								+XMLUtil.getXMLString(orderDoc));
					
					Document outDoc = CommonUtil.invokeAPI
							(env,"template/api/getOrderList_payment.xml","getOrderList", orderDoc);
					
					cat.verbose("--------------FANYFSCollectionCreditCardUEImpl------11---"
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
			  
}
