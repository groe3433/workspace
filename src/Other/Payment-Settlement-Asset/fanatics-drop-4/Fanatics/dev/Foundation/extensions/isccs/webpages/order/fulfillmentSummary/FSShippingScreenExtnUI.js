
scDefine(["dojo/text!./templates/FSShippingScreenExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!gridx/Grid","scbase/loader!isccs/utils/OrderLineUtils","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/GridxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _gridxGrid
			 ,
			    _isccsOrderLineUtils
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scGridxDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.fulfillmentSummary.FSShippingScreenExtnUI",
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
	  eventId: 'changeLOS_onClick'

,	  sequence: '19'

,	  description: 'this method will validate if all the line is selected'



,handler : {
methodName : "validateLineLevelLOS"

 
}
}

]
}

});
});


