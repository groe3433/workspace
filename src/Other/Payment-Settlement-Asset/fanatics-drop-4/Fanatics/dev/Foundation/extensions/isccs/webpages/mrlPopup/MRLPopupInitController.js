scDefine(["scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!dojo/text",
		"scbase/loader!extn/mrlPopup/MRLPopup",
		"scbase/loader!sc/plat/dojo/controller/ScreenController"
	],
	function (
		_dojodeclare,
		_dojokernel,
		_dojotext,
		_extnMRLPopup,
		_scScreenController) {

	return _dojodeclare("extn.mrlPopup.MRLPopupInitController",[_scScreenController], {
		screenId : 'extn.mrlPopup.MRLPopup',
		mashupRefs : [{
			sourceNamespace : 'extn_getMRLReasonCodes_output',
			mashupRefId : 'extn_getMRLReasonCodes',
			sequence : '',
			mashupId : 'extn_getMRLReasonCodes',
			callSequence : '',
			extnType : 'ADD',
			sourceBindingOptions : ''
		}]		
	});
});