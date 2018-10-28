
scDefine(["dojo/text!./templates/OrderEditorRTExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Link"]
 , function(			 
			    templateText
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
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLink
){
return _dojodeclare("extn.editors.OrderEditorRTExtnUI",
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
	  eventId: 'extn_manage_instruction_onClick'

,	  sequence: '51'

,	  description: 'This method will open the manage instruction popup'



,handler : {
methodName : "openManageInstructionPopup"

 
}
}
,
{
	  eventId: 'extn_waive_mrl_onClick'

,	  sequence: '51'

,	  description: 'This method will open the wavie mrl fee popup'



,handler : {
methodName : "openWavieMRLFeePopup"

 
}
}
,
{
	  eventId: 'extn_revise_ship_date_onClick'

,	  sequence: '51'

,	  description: 'This method open Revise Ship Date in new wizard'



,handler : {
methodName : "openReviseShipDate"

 
}
},
{
	  eventId: 'extn_ResendEmail_onClick'

,	  sequence: '51'




,handler : {
methodName : "openEmailScreen"

 
 
}
}

]
}

});
});


