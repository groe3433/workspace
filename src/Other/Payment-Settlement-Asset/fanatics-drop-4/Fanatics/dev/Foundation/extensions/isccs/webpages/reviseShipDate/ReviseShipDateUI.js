scDefine([
		"dojo/text!./templates/ReviseShipDate.html",
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
	return _dojodeclare("extn.reviseShipDate.ReviseShipDateUI", [_scScreen, ], {
		templateString : templateText,
		baseTemplate : {
			url : _dojokernel.moduleUrl("extn.reviseShipDate.templates", "ReviseShipDate.html"),
			shared : true
		},
		uId : "reviseShipDate",
		packageName : "extn.reviseShipDate",
		className : "ReviseShipDate",
		showRelatedTask : false,
		selectedRow: 0,
		isDirtyCheckRequired : true,
		namespaces : {
			targetBindingNamespaces : [{
					value : 'extn_ChangeOrderForReviseShipDate',
					description : "This mashup will hold the input to add/modify the instruction"
				}, {
					value : 'extn_TargetReasonCode',
					description : "This mashup will hold the input to update order line"
				}
			],
			sourceBindingNamespaces : [{
					value : 'extn_getCompleteOrderLineList_output',
					description : "This namespace will hold the order line details"
				}, {
					value: 'extn_getReviseShipDateReasonCode_output',
					description: "This namespace will hold the revise ship date reason code"
				}, {
					value: 'resetModel',
					description: "Holds the data which should be used to reset the model."
				}
			]

		},
		events : [],
		subscribers : {
			local : [{
					eventId: 'afterScreenLoad',
					sequence: '25',
					handler: {
						methodName: "updateEditorHeader"
					}
				}, {
					eventId: 'reloadScreen',
					sequence: '28',
					handler: {
						methodName: "updateEditorHeader"
					}
				}, {
					eventId: 'afterScreenInit',
					sequence: '30',
					description: '',
					handler: {
						methodName: "initializeScreen",
						description: "This method gets invoked in screen init"
					}
				}, {
					eventId: 'updateReviseShipDate_onClick',
					sequence: '30',
					description: '',
					listeningControlUId: 'updateReviseShipDate',
					handler: {
						methodName: "updateReviseShipDate",
						description: "This method will update the revise shipdate and reason to the selected order line"
					}
				}, {
					eventId: 'OLST_listGrid_pagingload',
					sequence: '30',
					handler: {
						methodName: "pagingLoad"
					}
				}
			]
		}

	});
});
