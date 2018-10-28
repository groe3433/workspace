


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/details/OrderSummaryLinesExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnOrderSummaryLinesExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.details.OrderSummaryLinesExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.details.OrderSummaryLinesExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'getCompleteOrderLineList_output'
,
		 mashupRefId : 			'getCompleteOrderLineList_Init'
,
		 sequence : 			''
,
		 mashupId : 			'OrderSummaryLines_getCompleteOrderLineList'
,
		 callSequence : 			''
,
		 extnType : 			''
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

