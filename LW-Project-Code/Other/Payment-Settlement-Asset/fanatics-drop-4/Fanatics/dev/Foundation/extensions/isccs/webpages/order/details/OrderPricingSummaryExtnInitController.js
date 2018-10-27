


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/details/OrderPricingSummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnOrderPricingSummaryExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.details.OrderPricingSummaryExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.details.OrderPricingSummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getCompleteOrderDetails'
,
		 sequence : 			'5'
,
		 mashupId : 			'pricingSummary_getCompleteOrderDetails'
,
		 callSequence : 			'1'
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

