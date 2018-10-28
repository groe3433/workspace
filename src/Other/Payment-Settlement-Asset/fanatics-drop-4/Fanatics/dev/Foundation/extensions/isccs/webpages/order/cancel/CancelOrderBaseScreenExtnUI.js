
scDefine(["dojo/text!./templates/CancelOrderBaseScreenExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CurrencyTextBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/RadioButtonSet","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder","scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxCurrencyTextBox
			 ,
			    _idxFilteringSelect
			 ,
			    _idxRadioButtonSet
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scRadioSetDataBinder
			 ,
			    _scAdvancedTableLayout
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.cancel.CancelOrderBaseScreenExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						,
	  description: "This model will hold the fancash and fancash reason code."
						,
	  value: 'extn_fancashModel'
						
			}
			
		],
		sourceBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_8'
						,
	  description: "This namespace will hold the cancellation reason code"
						,
	  value: 'extn_getFanCashReasonCode_output'
						
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

]
}

});
});


