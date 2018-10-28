package com.fanatics.sterling.constants;

public class ManageFanCashConstants {

	public static final String FANCASH_XPATH_SELLERORGANIZATIONCODE = "//Order/@SellerOrganizationCode";
	public static final String FANCASH_XPATH_ENTERPRISECODE = "//Order/@EnterpriseCode";
	public static final String FANCASH_XPATH_ORDERNO = "//Order/@OrderNo";
	public static final String FANCASH_XPATH_PAYMENTSTATUS = "//Order/@PaymentStatus";
	
	public static final String FANCASH_XPATH_BILLTOID = "//Order/@BillToID";
	public static final String FANCASH_XPATH_CUSTOMERREWARDSNO = "//OrderList/Order/@CustomerRewardsNo";

	public static final String FANCASH_XPATH_CSRREASONCODE = "//Order/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel='ORDER']/OrderAuditDetails/OrderAuditDetail[@AuditType='Note']/Attributes/Attribute[@Name='NoteText']/@NewValue";

	

	public static final String FANCASH_XPATH_COMMONCODESHORTDESC = "//CommonCodeList/CommonCode/@CodeShortDescription";
	
	public static final String FANCASH_XPATH_CHARGENAME = "Fancash";
	
	
	public static final String FANCASH_SERVICE_FANATICSFANCASHREST = "FanaticsFanCashREST";
	public static final String FANCASH_SERVICE_FANATICSFANCASHREVAUTHREST = "FanaticsFanCashRevAuthREST";


	
	


}
