


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/return/search/ReturnListScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnReturnListScreenExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.return.search.ReturnListScreenExtnBehaviorController", 
				[_scExtnServerDataController], {
			 screenId : 			'extn.return.search.ReturnListScreenExtn',
		 mashupRefs: [{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_return_getOrderList',
			callSequence : 	'',
			mashupId : 	'extn_return_getOrderList'
		},
		{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_return_getPersonInfoList',
			callSequence : 	'',
			mashupId : 	'extn_return_getPersonInfoList'
		}]

			
			
			
}
);
});

