scDefine(["scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/reviseShipDate/ReviseShipDate",
		"scbase/loader!sc/plat/dojo/controller/ScreenController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnReviseShipDate,
		_scScreenController) {

	return _dojodeclare("extn.reviseShipDate.ReviseShipDateInitController",[_scScreenController], {
		screenId : 'extn.reviseShipDate.ReviseShipDate',
		mashupRefs : [{
			sourceNamespace : 'extn_getReviseShipDateReasonCode_output',
			mashupRefId : 'extn_getReviseShipDateReasonCode',
			sequence : '',
			mashupId : 'extn_getReviseShipDateReasonCode',
			callSequence : '',
			extnType : 'ADD',
			sourceBindingOptions : ''
		}]		
	});
});