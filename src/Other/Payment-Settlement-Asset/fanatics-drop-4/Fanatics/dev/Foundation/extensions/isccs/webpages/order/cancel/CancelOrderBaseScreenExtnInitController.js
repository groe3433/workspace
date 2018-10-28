


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/cancel/CancelOrderBaseScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCancelOrderBaseScreenExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.cancel.CancelOrderBaseScreenExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.cancel.CancelOrderBaseScreenExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'getCompleteOrderDetails_output'
,
		 mashupRefId : 			'getCompleteOrderDetails'
,
		 sequence : 			''
,
		 mashupId : 			'cancelOrder_getCompleteOrderDetails'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}
,
	 		{
		 sourceNamespace : 			'extn_getFanCashReasonCode_output'
,
		 mashupRefId : 			'extn_getFanCashReasonCode'
,
		 sequence : 			''
,
		 mashupId : 			'extn_getFanCashReasonCode'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

