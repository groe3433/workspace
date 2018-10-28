scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/reviseShipDate/ReviseShipDate",
		"scbase/loader!sc/plat/dojo/controller/ServerDataController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnReviseShipDate,
		_scServerDataController) {
	return _dojodeclare("extn.reviseShipDate.ReviseShipDateBehaviorController",[_scServerDataController], {
		screenId : 'extn.reviseShipDate.ReviseShipDate',
		 mashupRefs: [{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_ChangeOrderForReviseShipDate',
			callSequence : 	'',
			mashupId : 	'extn_ChangeOrderForReviseShipDate'
		},{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_getCompleteOrderLineList_reviseShipDate',
			callSequence : 	'',
			mashupId : 	'extn_getCompleteOrderLineList_reviseShipDate'
		},{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_FanManageEmailEntry',
			callSequence : 	'',
			mashupId : 	'extn_FanManageEmailEntry'
		}]
});
});
