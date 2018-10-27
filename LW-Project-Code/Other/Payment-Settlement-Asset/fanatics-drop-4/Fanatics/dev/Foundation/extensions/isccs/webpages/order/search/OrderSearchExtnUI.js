
scDefine(["dojo/text!./templates/OrderSearchExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/RadioButtonSet","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxRadioButtonSet
			 ,
			    _idxTextBox
			 ,
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scRadioSetDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.search.OrderSearchExtnUI",
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
	  eventId: 'extn_txt_cusordno_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_firstName_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_lastName_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_addrsLine1_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_addrsLine2_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_postalCode_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_emailId_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}
,
{
	  eventId: 'extn_txtTelephone_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
 
}
}

]
}

});
});


