package com.fanatics.sterling.constants;

public interface FanaticsFraudCheckConstants {


		public static final String HOLDTYPE_fanatics_PENDINGEVALUATION = "PENDINGEVALUATION";
		public static final String HOLDTYPE_fanatics_PENDINGEREVIEW = "PENDINGREVIEW";
		public static final String SERVICE_fanatics_FanaticsFraudCheck = "FanaticsFraudCheck";
		
		public static final String FRAUD_PROPS_fanatics_BASE_URL = "FraudEngine_Base.url";
		public static final String FRAUD_PROPS_fanatics_USERNAME = "FraudEngine_User";
		public static final String FRAUD_PROPS_fanatics_PASSWORD = "FraudEngine_Pwd";
		public static final String FRAUD_PROPS_fanatics_PATH = "/smcfs/restapi/executeFlow/FanaticsFraudEngine";
		public static final String ATT_fanatics_Sender = "FraudResponseSender";
		public static final String ATT_fanatics_FanChangeOrder = "FanSendChangeOrder";
		public static final String ATT_fanatics_FanaticsBuyersRemorse = "FanaticsBuyersRemorse";
		public static final String ATT_fanatics_CustomAttributes = "CustomAttributes";
		public static final String ATT_fanatics_FanCash = "FanCash";
		public static final String ATT_fanatics_FraudStatusTS = "FraudStatusTS";
		public static final String ATT_fanatics_FraudStatusUserID = "FraudStatusUserID";
		public static final String ATT_fanatics_FraudStatus = "FraudStatus";
		public static final String ATT_fanatics_OrderDates = "OrderDates";
		public static final String ATT_fanatics_OrderDate = "OrderDate";
		public static final String ATT_fanatics_DateTypeId = "DateTypeId";
		public static final String ATT_fanatics_ActualDate = "ActualDate";

		
		public static final String ATT_fanatics_OrderHoldTypes = "OrderHoldTypes";
		public static final String ATT_fanatics_OrderHoldType = "OrderHoldType";
		public static final String ATT_fanatics_HoldType = "HoldType";		
		
		public static final String CONSTANT_fanatics_Accertify = "ACCERTIFY";
		public static final String CONSTANT_fanatics_BUYERS_REMORSE = "BUYERS_REMORSE";
		public static final String CONSTANT_fanatics_BUYERSREMORSE = "BUYERSREMORSE";
		public static final String CONSTANT_fanatics_FRAUD_APPROVAL_DATE = "FRAUD_APPROVAL_DATE";
		public static final String CONSTANT_fanatics_FRAUD_HOLDS_COMMON_CODE = "FanFraudHolds";

		public static final String ATT_fanatics_FraudToken = "FraudToken";
		public static final String ATT_fanatics_FraudResponseCode = "FraudResponseCode";
		public static final String SERVICE_fanatics_FanaticsIntFraudResponse = "FanaticsIntFraudResponse";
		public static final String SERVICE_fanatics_FanaticsCancelOrder = "FanaticsCancelOrder";
		public static final String SERVICE_fanatics_FanaticsPostEmailTrigger = "FanaticsPostEmailTrigger";
		public static final String SERVICE_fanatics_FanaticsFraudCheckREST = "FanaticsFraudCheckREST";
		public static final String SERVICE_fanatics_FanSendFraudResponse = "FanSendFraudResponse";
		
		
		public static final String XPATH_fanatics_FraudTokenOMS = "Order/CustomAttributes/@FraudToken";
		public static final String XPATH_fanatics_FraudTokenFraudEngine = "Order/@FraudToken";
		public static final String XPATH_fanatics_FraudDocumentType = "Order/@DocumentType";
		public static final String XPATH_fanatics_FraudEnterpriseCode = "Order/@EnterpriseCode";
		public static final String XPATH_fanatics_FraudOrderNo = "Order/@OrderNo";
		public static final String XPATH_fanatics_FraudOrderDate = "Order/@OrderDate";
		public static final String XPATH_fanatics_FraudSellerOrg = "Order/@SellerOrganizationCode";
		public static final String XPATH_fanatics_FraudResponseCode = "Order/@FraudResponseCode";
		public static final String XPATH_fanatics_FraudResponseSender = "Order/@FraudResponseSender";
		public static final String XPATH_fanatics_FraudCusOrderNo = "Order/CustomAttributes/@CustomerOrderNo";
		public static final String XPATH_fanatics_FraudOrderHeaderKey = "Order/@OrderHeaderKey";
		
		

		
		public static final String  SQL_fanatics_GetDBTime= "select to_char(systimestamp, 'IYYY-MM-DD HH24:MI:SS') as d from dual";
		
}
