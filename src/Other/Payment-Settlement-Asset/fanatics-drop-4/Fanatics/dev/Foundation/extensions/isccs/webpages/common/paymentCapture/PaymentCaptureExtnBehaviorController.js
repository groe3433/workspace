


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/paymentCapture/PaymentCaptureExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPaymentCaptureExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.paymentCapture.PaymentCaptureExtn'
				,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_getPaymentEncoding'
,
		 mashupRefId : 			'extn_getPaymentEncoding'

	}

	]

}
);
});

