
scDefine(["dojo/text!./templates/CarrierServicePopupExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _idxCheckBox
			 ,
			    _idxFilteringSelect
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLabel
){
return _dojodeclare("extn.order.fulfillmentSummary.CarrierServicePopupExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
		],
		sourceBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_4'
						,
	  description: "extn_getShippingCharge_output"
						,
	  value: 'extn_getShippingCharge_output'
						
			}
			,
			{
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_5'
						,
	  value: 'extn_getCompleteOrderDetails'
						
			}
			
		]
	}

	
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

,	  description: 'This method will store the screen input in the custom model'



,handler : {
methodName : "populateOverrideShippingModel"

 
}
}
,
{
	  eventId: 'extn_carrierService_onChange'

,	  sequence: '51'




,handler : {
methodName : "carrierServiceOnChange"

 
}
}

]
}

});
});


