


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/return/details/ReturnSummaryLinesExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnReturnSummaryLinesExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.return.details.ReturnSummaryLinesExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.return.details.ReturnSummaryLinesExtn'

						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_getReceiptLineList_output'
,
		 mashupRefId : 			'extn_ReturnSummaryLines_getReceiptLineList'
,
		 sequence : 			''
,
		 mashupId : 			'extn_ReturnSummaryLines_getReceiptLineList'
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

