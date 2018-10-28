scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/mrlPopup/MRLPopup",
		"scbase/loader!sc/plat/dojo/controller/ServerDataController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnMRLPopup,
		_scServerDataController) {
	return _dojodeclare("extn.mrlPopup.MRLPopupBehaviorController",[_scServerDataController], {
		screenId : 'extn.mrlPopup.MRLPopup',
		 mashupRefs: [{
			sourceNamespace : 	'',
			sequence : 	'',
			sourceBindingOptions : 	'',
			extnType : 	'ADD',
			mashupRefId : 	'extn_ChangeOrderForMRL',
			callSequence : 	'',
			mashupId : 	'extn_ChangeOrderForMRL'
		}]
});
});