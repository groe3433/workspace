scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/emails/Emails",
		"scbase/loader!sc/plat/dojo/controller/ServerDataController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnReviseShipDate,
		_scServerDataController) {
	return _dojodeclare("extn.emails.EmailsBehaviorController",[_scServerDataController], {
		screenId : 'extn.emails.Emails',
		 mashupRefs: [{
			sourceNamespace : 	'extn_changeEXTNFanEmail_output',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_changeEXTNFanEmail',
			callSequence : 	'',
			mashupId : 	'extn_changeEXTNFanEmail'
		},{
			sourceNamespace : 	'extn_createEXTNFanEmail_output',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_createEXTNFanEmail',
			callSequence : 	'',
			mashupId : 	'extn_createEXTNFanEmail'
		},{
			sourceNamespace : 	'extn_getEXTNFanEmail_output',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_getEXTNFanEmail',
			callSequence : 	'',
			mashupId : 	'extn_getEXTNFanEmail'
		},{
			sourceNamespace : 	'extn_chargetransaction_getEXTNFanEmail_output',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_chargetransaction_getEXTNFanEmail',
			callSequence : 	'',
			mashupId : 	'extn_chargetransaction_getEXTNFanEmail'
		},{
			sourceNamespace : 	'getOrderDetails_output',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_email_getOrderDetails',
			callSequence : 	'',
			mashupId : 	'extn_email_getOrderDetails'
		}]
});
});
