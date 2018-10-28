scDefine(["scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/emails/Emails",
		"scbase/loader!sc/plat/dojo/controller/ScreenController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnReviseShipDate,
		_scScreenController) {

	return _dojodeclare("extn.emails.EmailsInitController",[_scScreenController], {
		screenId : 'extn.emails.Emails',
		mashupRefs : [{
			sourceNamespace : 'extn_getCompleteEXTNFanEmail_confirmation_output',
			mashupRefId : 'extn_getCompleteEXTNFanEmail_confirmation',
			sequence : '',
			mashupId : 'extn_getCompleteEXTNFanEmail_confirmation',
			callSequence : '',
			extnType : 'ADD',
			sourceBindingOptions : ''
		},{
			sourceNamespace : 'extn_getCompleteEXTNFanEmail_shipment_output',
			mashupRefId : 'extn_getCompleteEXTNFanEmail_shipment',
			sequence : '',
			mashupId : 'extn_getCompleteEXTNFanEmail_shipment',
			callSequence : '',
			extnType : 'ADD',
			sourceBindingOptions : ''
		}]	
	});
});