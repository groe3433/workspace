
scDefine(["dojo/text!./templates/PaymentCaptureExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'afterScreenInit'

,	  sequence: '51'

,	  description: 'This method will validate if the user have the permission to use paypal payment method'



,handler : {
methodName : "validateUserForPayPal"

 
 
}
}
,
{
	  eventId: 'extn_popup_btnNext_onClick'

,	  sequence: '51'




,handler : {
methodName : "onTokenize"

 
 
}
}

]
}

});
});


