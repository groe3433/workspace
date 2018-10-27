scDefine([
		"dojo/text!./templates/MRLPopup.html",
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/widgets/Screen"
	],
	function (
		templateText,
		_dojodeclare,
		_dojokernel,
		_scScreenUtils,
		_scBaseUtils,
		_scScreen) {
	return _dojodeclare("extn.mrlPopup.MRLPopupUI", [_scScreen, ], {
		templateString : templateText,
		baseTemplate : {
			url : _dojokernel.moduleUrl("extn.mrlPopup.templates", "MRLPopup.html"),
			shared : true
		},
		uId : "mrlPopup",
		packageName : "extn.mrlPopup",
		className : "MRLPopup",
		showRelatedTask : false,
		isDirtyCheckRequired : true,
		namespaces : {
			targetBindingNamespaces : [{
					value : 'extn_TargetReasonCode',
					description : "This mashup will hold the selected return reason code"
				}
			],
			sourceBindingNamespaces : [{
					value : 'extn_getMRLReasonPopupCodes_output',
					description : "This namespace will hold the return reason code from getCommonCodeList output"
				},
				{
					value: 'extn_MRLPopupModel_output',
					description: "This namespace will hold the selected order lines return reason code"
				}, {
					value: 'screenInput',
					description: "This is set when the screen loads to prepopulated the search criteria."
				}
			]

		},
		events : [],
		subscribers : {
			local : [{
					eventId : 'afterScreenInit',
					sequence : '25', 
					description: 'This method will check if the call is for make changes in return reason code, if yes then hide all the controls in the screen and show new dropdown.',
					handler : {
						methodName : "fetchMRLReasonCode"
					}
				}, {
					eventId : 'extn_cancel_btn_onClick',
					sequence : '25',
					description : 'This method will be responsible for closing the popup',
					handler : {
						methodName : "closePopup",
						description : ""
					}
				}, {
					eventId : 'extn_apply_btn_onClick',
					sequence : '25',
					description : 'This method will invoke change order to save the Return reason.',
					handler : {
						methodName : "updateMRLReasonCode",
						description : ""
					}
				}
			]
		}

	});
});
