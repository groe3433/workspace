package com.fanatics.sterling.constants;

import com.fanatics.sterling.util.CommonUtil;

public interface FanaticsOrderRepricingConstants {


		
		public static final String SERVICE_fanatics_FanaticsOrderRepricing = "FanaticsOrderRepricingRestService";
		
		
		public static final String XPATH_ORDER_LINE = "Order/OrderLines/OrderLine";
		public static final String XPATH_fanatics_OrderRepricingDeliverymethod = "Order/OrderLines/OrderLine/@DeliveryMethod";
		public static final String XPATH_fanatics_OrderRepricingSubLineNo = "Order/OrderLines/OrderLine/@SubLineNo";
		public static final String XPATH_fanatics_OrderRepricingPrimeLineNo = "Order/OrderLines/OrderLine/@PrimeLineNo";
		public static final String XPATH_fanatics_OrderRepricingOriginalOrderedQty = "Order/OrderLines/OrderLine/@OriginalOrderedQty";
		public static final String XPATH_fanatics_OrderRepricingOrderedQty = "Order/OrderLines/OrderLine/@OrderedQty";
		public static final String XPATH_fanatics_OrderRepricingOtherCharges = "Order/OrderLines/OrderLine/@OtherCharges";
		
		
		public static final String XPATH_fanatics_OrderRepricingITEMID="Order/OrderLines/OrderLine/Item/@ItemID";
		public static final String XPATH_fanatics_OrderRepricingUnitOfMeasure="Order/OrderLines/OrderLine/Item/@UnitOfMeasure";
		public static final String XPATH_fanatics_OrderRepricingUnitCost="Order/OrderLines/OrderLine/Item/@UnitCost";
		public static final String XPATH_fanatics_OrderRepricingItemShortDesc="Order/OrderLines/OrderLine/Item/@ItemShortDesc";
		public static final String XPATH_fanatics_OrderRepricingCostCurrency="Order/OrderLines/OrderLine/Item/@CostCurrency";
		
		
		
		public static final String XPATH_fanatics_OrderRepricingDocumentType = "Order/@DocumentType";
		public static final String XPATH_fanatics_OrderRepricingEnterpriseCode = "Order/@EnterpriseCode";
		public static final String XPATH_fanatics_OrderRepricingOrderNo = "Order/@OrderNo";
		public static final String XPATH_fanatics_OrderRepricingOrderDate = "Order/@OrderDate";
		public static final String XPATH_fanatics_OrderRepricingSellerOrg = "Order/@SellerOrganizationCode";
		public static final String XPATH_fanatics_OrderRepricingResponseCode = "Order/@FraudResponseCode";
		public static final String XPATH_fanatics_OrderRepricingResponseSender = "Order/@FraudResponseSender";
		public static final String XPATH_fanatics_OrderRepricingCusOrderNo = "Order/CustomAttributes/@CustomerOrderNo";
		
		public static final String XPATH_fanatics_OrderRepricingLinePriceInfoUnitPrice="Order/OrderLines/OrderLine/LinePriceInfo/@UnitPrice";
		public static final String XPATH_fanatics_OrderRepricingLinePriceInfoAdditionalLinePriceTotal="Order/OrderLines/OrderLine/LinePriceInfo/@AdditionalLinePriceTotal";
		
				
		//public static final String  SQL_fanatics_GetDBTime= "select to_char(systimestamp, 'IYYY-MM-DD HH24:MI:SS') as d from dual";
		
}
