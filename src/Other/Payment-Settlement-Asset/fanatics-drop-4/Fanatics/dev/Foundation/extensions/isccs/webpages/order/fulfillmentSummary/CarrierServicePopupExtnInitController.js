


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/fulfillmentSummary/CarrierServicePopupExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCarrierServicePopupExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.fulfillmentSummary.CarrierServicePopupExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.fulfillmentSummary.CarrierServicePopupExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_getShippingCharge_output'
,
		 mashupRefId : 			'extn_getShippingCharge'
,
		 sequence : 			''
,
		 mashupId : 			'extn_getShippingCharge'
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

