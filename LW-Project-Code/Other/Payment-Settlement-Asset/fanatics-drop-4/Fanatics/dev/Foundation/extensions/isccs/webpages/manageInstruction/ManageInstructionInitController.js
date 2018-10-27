scDefine(["scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/manageInstruction/ManageInstruction",
		"scbase/loader!sc/plat/dojo/controller/ScreenController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnManageInstruction,
		_scScreenController) {

	return _dojodeclare("extn.manageInstruction.ManageInstructionInitController",[_scScreenController], {
		screenId : 'extn.manageInstruction.ManageInstruction',
		mashupRefs : [{
			sourceNamespace : 'extn_getCompleteOrderDetails_output',
			mashupRefId : 'extn_getCompleteOrderDetails_manageInstruction',
			sequence : '',
			mashupId : 'extn_getCompleteOrderDetails_manageInstruction',
			callSequence : '',
			extnType : 'ADD',
			sourceBindingOptions : ''
		}]		
	});
});