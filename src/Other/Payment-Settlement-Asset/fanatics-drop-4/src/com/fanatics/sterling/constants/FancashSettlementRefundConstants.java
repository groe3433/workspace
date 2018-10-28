package com.fanatics.sterling.constants;

public class FancashSettlementRefundConstants {

	public static final String FANCASH_INVOICE_TYPE_SHIPMENT = "SHIPMENT";
	public static final String FANCASH_INVOICE_TYPE_RETURN = "RETURN";
	public static final String FANCASH_CHARGE_CATEGORY = "ChargeCategory";
	public static final String FANCASH_CHARGE_NAME = "ChargeName";
	public static final String FANCASH_CHARGE_NAME_VALUE = "Fancash";
	public static final String FANCASH_CHARGE_AMOUNT = "ChargeAmount";
	
	public static final String FANCASH_XPATH_INVOICE_TYPE = "//InvoiceDetail/InvoiceHeader/@InvoiceType";
	public static final String FANCASH_XPATH_LINEDETAIL = "//InvoiceDetail/InvoiceHeader/LineDetails/LineDetail";
	public static final String FANCASH_XPATH_LINECHARGE = "./LineCharges/LineCharge";
	public static final String FANCASH_XPATH_ORDER_HEADER_KEY = "//InvoiceDetail/InvoiceHeader/Order/@OrderHeaderKey";
	public static final String FANCASH_XPATH_ORDER_NO = "//InvoiceDetail/InvoiceHeader/Order/@OrderNo";
	public static final String FANCASH_XPATH_INVOICENO="//InvoiceDetail/InvoiceHeader/@InvoiceNo";
	public static final String FANCASH_XPATH_DATEINVOICED="//InvoiceDetail/InvoiceHeader/@DateInvoiced";
	public static final String FANCASH_XPATH_BILLTOID = "//OrderList/Order/@BillToID";
	public static final String FANCASH_XPATH_CUSTOMER_ORDER_NO = "//OrderList/Order/CustomAttributes/@CustomerOrderNo";
	public static final String FANCASH_XPATH_ORDER_LINE="//InvoiceDetail/InvoiceHeader/LineDetails/LineDetail";
	
	public static final String FANCASH_ORDERLIST_TEMPLATE = "<OrderList><Order OrderNo=\"\" OrderHeaderKey=\"\" BillToID=\"\" ><CustomAttributes CustomerOrderNo=\"\" /></Order></OrderList>";
	public static final String FANCASH_SETTLEMENT_INPUT_TEMPLATE="<FanCash BillToID=\"\" AmountToAdd=\"\" AmountToDeduct=\"\" ><Order OrderNo=\"\" ><CustomAttributes CustomerOrderNo=\"\" /><OrderLines><OrderLine><Item/></OrderLine></OrderLines></Order></FanCash>"; 
	
}
