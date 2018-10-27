package com.fanatics.sterling.ue;


import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.pca.ycd.ue.utils.YCDPaymentUEXMLManager;
import com.yantra.shared.dbclasses.YFS_Charge_TransactionDBHome;
import com.yantra.shared.dbi.YFS_Charge_Transaction;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.dbi.YFS_Payment;
import com.yantra.shared.dbi.YFS_Payment_Type;
import com.yantra.shared.ycd.YCDErrorCodes;
import com.yantra.shared.ycd.YCDServiceNames;
import com.yantra.shared.ycp.YFSContext;
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
import com.yantra.yfs.japi.ue.YFSCollectionStoredValueCardUE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FANYFSCollectionSVCUEImpl
  implements YFSCollectionStoredValueCardUE, YCDErrorCodes, YCDServiceNames
{
  public static YFCLogCategory cat = YFCLogCategory.instance(FANYFSCollectionSVCUEImpl .class);
  private static YCDFoundationBridge bridge = YCDFoundationBridge.getInstance();
  
  public FANYFSCollectionSVCUEImpl()
  {
	cat.verbose("--------------FANYFSCollectionSVCUEImpl------0-1--");
    cat.verbose("Entering FANYFSCollectionSVCUEImpl");
  }
  
  public YFSExtnPaymentCollectionOutputStruct collectionStoredValueCard(YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct)
    throws YFSUserExitException
  {
	  cat.verbose("--------------FANYFSCollectionSVCUEImpl------0--2-");
	  
    cat.beginTimer("FANYFSCollectionSVCUEImpl");
    cat.verbose("");
    YFSContext oCtx = (YFSContext)oEnv;
    
    YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
    try
    {
      YFCDocument paymentInputDoc = YCDPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
      
      paymentInputDoc = appendAdditionalInfoTopaymentInputDoc(oEnv,paymentInputDoc);
      
      cat.verbose("--------------FANYFSCollectionSVCUEImpl------1---"
				+XMLUtil.getXMLString(paymentInputDoc.getDocument()));
      
      String sOrderHeaderKey = paymentInputDoc.getDocumentElement().getAttribute("OrderHeaderKey");
      
      YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, "WAIT", false);
      
      String sTranID = bridge.getFulfillmentTransactionId(oCtx, "PAYMENT_EXECUTION", oOrder.getDocument_Type());
      
      YFCDocument collectionOutputDoc = executeSVCService(oCtx, "YCD_ExecuteCollectionSVC_Proxy_1.0", paymentInputDoc);
      
      cat.verbose("Collection Output XML is " + collectionOutputDoc.getString());
      
      if (collectionOutputDoc != null)
      {
        YFCElement root = collectionOutputDoc.getDocumentElement();
        
        String sResponseCode = root.getAttribute("ResponseCode");
        String chargeType = root.getAttribute("ChargeType");
       
        if ((!YFCObject.equals(sResponseCode, "APPROVED")) && (!YFCObject.equals(sResponseCode, "AUTHORIZED")) && (!YFCObject.equals(sResponseCode, "HARD_DECLINED")) && (!YFCObject.equals(sResponseCode, "SOFT_DECLINED")) && (!YFCObject.equals(sResponseCode, "BANK_HOLD")) && (!YFCObject.equals(sResponseCode, "SERVICE_UNAVAILABLE")))
        {
          YFCException ex = new YFCException("YCD00001");
          ex.setAttribute("ResponseCode", sResponseCode);
          throw ex;
        }
        //paymentOutputStruct = constructBasicCollectionSVCOutput(collectionOutputDoc);
        
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
            paymentOutputStruct.retryFlag = "N";
            paymentOutputStruct.suspendPayment = "B";
            paymentOutputStruct.holdReason = "PAYMENT_AUTH_HOLD";
            paymentOutputStruct.holdOrderAndRaiseEvent = true;
            
            YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
            raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
		   	
			cat.verbose("-----------------------------AUTH--------HARD_DECLINED--------2---------------------------------------------------");

		
        } 
        else if(YFCObject.equals(sResponseCode, "APPROVED") && "CHARGE".equalsIgnoreCase(chargeType))
        {
          cat.verbose("----------------------------APPROVED-----------------------------------");
          
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
        
        if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("AuthorizationExpirationDate"))) {
        	paymentOutputStruct.authorizationExpirationDate = collectionOutputDoc.getDocumentElement().getAttribute("AuthorizationExpirationDate");
          } 
      
      if (!YFCObject.isVoid(collectionOutputDoc.getDocumentElement().getAttribute("AuthTime"))) {
    	  paymentOutputStruct.authTime = collectionOutputDoc.getDocumentElement().getAttribute("AuthTime");
          } 
      

          paymentOutputStruct.retryFlag = "N";
          paymentOutputStruct.suspendPayment = "N";
          
          cat.verbose("----------------------------APPROVED----------------------------2-------");
        }
        else if (YFCObject.equals(sResponseCode, "HARD_DECLINED") && "CHARGE".equalsIgnoreCase(chargeType))
        {
        	cat.verbose("----------------------------DECLINED-----------------------------------");
          paymentOutputStruct.authorizationAmount = 0.0D;
          paymentOutputStruct.retryFlag = "N";
          paymentOutputStruct.suspendPayment = "B";
          paymentOutputStruct.holdReason = "PAYMENT_CAPTURE_HOLD";
          paymentOutputStruct.holdOrderAndRaiseEvent = true;
          
          YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
          raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
          
          cat.verbose("----------------------------DECLINED-----------2------------------------");
        }
        else if (YFCObject.equals(sResponseCode, "SERVICE_UNAVAILABLE") && "CHARGE".equalsIgnoreCase(chargeType))
        {
        	cat.verbose("----------------------------SERVICE_UNAVAILABLE-----------------------------------");
          paymentOutputStruct.authorizationAmount = 0.0D;
          paymentOutputStruct.retryFlag = "Y";
          paymentOutputStruct.suspendPayment = "N";
          
          
          modifyRetryInterval(paymentOutputStruct, oEnv);
	   	
          YFCDocument eventDoc = prepareEventDoc(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, sResponseCode, sTranID);
          raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, sTranID);
          
          cat.verbose("----------------------------SERVICE_UNAVAILABLE-------------2---------------------");
          
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
      cat.endTimer("YCDCollectionSVCImpl");
      cat.verbose("Exiting YCDCollectionSVCImpl");
    }
    return paymentOutputStruct;
  }
  
  
  private void modifyRetryInterval(
			YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFSEnvironment env) throws Exception{
			// TODO Auto-generated method stub
		  
		Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
		Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
		ordercommonCodeEle.setAttribute("CodeValue", "RetryInterval");

		
		cat.verbose("--------------FANYFSCollectionSVCUEImpl------12--"
					+XMLUtil.getXMLString(commonCodeDoc));
		
		Document outDoc = CommonUtil.invokeAPI
				(env,"getCommonCodeList", commonCodeDoc);
		
		cat.verbose("--------------FANYFSCollectionSVCUEImpl------13---"
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
  

private void raiseEvent(YFSContext oCtx, YFS_Order_Header oOrder, YFSExtnPaymentCollectionInputStruct paymentInputStruct, YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFCDocument eventDoc, String sTranID)
  {
    boolean bEventReqd = bridge.isRaiseEventRequired(oCtx, sTranID, "COLLECTION_FAILED");
    if (bEventReqd)
    {
      YFCDataBuf sKeyData = oOrder.getOrderHeaderBuf(oCtx);
      
      bridge.raiseEvent(oCtx, sKeyData, eventDoc, sTranID, "COLLECTION_FAILED");
    }
  }
  
  private YFCDocument getTemplate(YFSContext oCtx, YFS_Order_Header oOrder, String sTranID)
  {
    Map bufParams = new HashMap();
    bufParams.put("EnterpriseCode", oOrder.getEnterprise_Key());
    bufParams.put("DocumentType", oOrder.getDocument_Type());
    
    YFCDocument templateDoc = bridge.getEventTemplate(oCtx, sTranID, "COLLECTION_FAILED", oOrder.getEnterprise_Key(), oOrder.getDocument_Type());
    
    return templateDoc;
  }
  
  private YFCDocument prepareEventDoc(YFSContext oCtx, YFS_Order_Header oOrder, YFSExtnPaymentCollectionInputStruct paymentInputStruct, YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, String sResponseCode, String sTranID)
  {
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
      failureDetails.setAttribute("DisplayPaymentReference1", paymentOutputStruct.DisplayPaymentReference1);
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
      YFS_Charge_Transaction oChgTran = YFS_Charge_TransactionDBHome.getInstance().selectWithPK(oCtx, paymentInputStruct.chargeTransactionKey);
      
      
      if (oChgTran != null)
      {
        YFS_Payment_Type oType = oChgTran.getPayment().getPaymentType();
        if (oType != null) {
          failureDetails.setAttribute("PaymentTypeGroup", oType.getPayment_Type_Group());
        }
      }
      
      setAlertAttributes(root, paymentInputStruct, paymentOutputStruct);
      
    }
    return eventDoc;
  }
  
  private void setAlertAttributes(YFCElement root, YFSExtnPaymentCollectionInputStruct paymentInputStruct, YFSExtnPaymentCollectionOutputStruct paymentOutputStruct)
  {
    root.setAttribute("DocumentType", paymentInputStruct.documentType);
    root.setAttribute("EnterpriseCode", paymentInputStruct.enterpriseCode);
    root.setAttribute("OrderNo", paymentInputStruct.orderNo);
    root.setAttribute("OrderHeaderKey", paymentInputStruct.orderHeaderKey);
    root.setAttribute("PaymentReference1", paymentInputStruct.paymentReference1);
    root.setAttribute("PaymentReference2", paymentInputStruct.paymentReference2);
    root.setAttribute("PaymentReference3", paymentInputStruct.paymentReference3);
    root.setAttribute("PaymentType", paymentInputStruct.paymentType);
    root.setDoubleAttribute("RequestAmount", paymentInputStruct.requestAmount);
    root.setAttribute("SvcNo", paymentInputStruct.svcNo);
    root.setAttribute("DisplaySvcNo", paymentOutputStruct.DisplaySvcNo);
  }
  
  public YFCDocument executeSVCService(YFSContext oCtx, String sServiceName, YFCDocument serviceInputDoc)
    throws Exception
  {
	  
      cat.verbose("--------------FANYFSCollectionSVCUEImpl---executeSVCService---1---"
				+XMLUtil.getXMLString(serviceInputDoc.getDocument()));
      
    YFCDocument serviceOutputDoc = bridge.executeService(oCtx, sServiceName, serviceInputDoc);
    
    cat.verbose("--------------FANYFSCollectionSVCUEImpl---executeSVCService---2---"
				+XMLUtil.getXMLString(serviceOutputDoc.getDocument()));
    
    return serviceOutputDoc;
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

			
			cat.verbose("--------------FANYFSCollectionSVCUEImpl------10---"
						+XMLUtil.getXMLString(orderDoc));
			
			Document outDoc = CommonUtil.invokeAPI
					(env,"template/api/getOrderList_payment.xml","getOrderList", orderDoc);
			
			cat.verbose("--------------FANYFSCollectionSVCUEImpl------11---"
					+XMLUtil.getXMLString(outDoc));
			
			Element outEle = (Element)outDoc.getDocumentElement().getElementsByTagName("Order").item(0);
			
			//paymentInputEle.setAttribute("BillToId", outEle.getAttribute("BillToID"));
			paymentInputEle.setAttribute("PartnerSiteId", outEle.getAttribute("SellerOrganizationCode"));
			
			
					} catch (Exception e) {
			
							e.printStackTrace();
					}
		
		
		return paymentInputDoc;
	
	}
  
//  public YFSExtnPaymentCollectionOutputStruct constructBasicCollectionSVCOutput(YFCDocument paymentOutputDoc)
//  {
//    YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
//    try
//    {
//      YCDPaymentUEXMLManager.populatePaymentOutputStructFromXML(paymentOutputStruct, paymentOutputDoc);
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//    return paymentOutputStruct;
//  }
}
