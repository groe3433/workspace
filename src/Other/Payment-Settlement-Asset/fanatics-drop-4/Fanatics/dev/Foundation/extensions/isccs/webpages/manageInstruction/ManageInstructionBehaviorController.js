scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/manageInstruction/ManageInstruction",
		"scbase/loader!sc/plat/dojo/controller/ServerDataController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnManageInstruction,
		_scServerDataController) {
	return _dojodeclare("extn.manageInstruction.ManageInstructionBehaviorController",[_scServerDataController], {
		screenId : 'extn.manageInstruction.ManageInstruction',
		 mashupRefs: [{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_ChangeOrderForManageInstruction',
			callSequence : 	'',
			mashupId : 	'extn_ChangeOrderForManageInstruction'
		}]
});
});