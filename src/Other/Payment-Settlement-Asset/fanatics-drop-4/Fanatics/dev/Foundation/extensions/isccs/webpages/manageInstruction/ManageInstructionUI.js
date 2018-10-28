scDefine([
		"dojo/text!./templates/ManageInstruction.html",
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
	return _dojodeclare("extn.manageInstruction.ManageInstructionUI", [_scScreen, ], {
		templateString : templateText,
		baseTemplate : {
			url : _dojokernel.moduleUrl("extn.manageInstruction.templates", "ManageInstruction.html"),
			shared : true
		},
		uId : "manageInstruction",
		packageName : "extn.manageInstruction",
		className : "ManageInstruction",
		showRelatedTask : false,
		selectedRow: 0,
		isDirtyCheckRequired : true,
		namespaces : {
			targetBindingNamespaces : [{
					value : 'extn_ChangeOrderForManageInstruction',
					description : "This mashup will hold the input to add/modify the instruction"
				}, {
					value : 'extn_ChangeOrderForManageInstructionUpd',
					description : "This mashup will hold the input to add/modify the instruction"
				}
			],
			sourceBindingNamespaces : [{
					value : 'extn_getCompleteOrderDetails_output',
					description : "This namespace will hold the order details"
				}, {
					value: 'extn_ReturnReasonPopupModel_output',
					description: "This namespace will hold the selected order lines return reason code"
				}, {
					value: 'selectedInstruction',
					description: "This is set when the instruction is selected."
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
					eventId: 'grdInstructions_Link_ScHandleLinkClicked',
					sequence: '25',
					description: 'Causes the link in a grid to open the double click event',
					handler: {
						methodName: "OLST_cellClick"
					}
				}, {
					eventId: 'updateInstruction_onClick',
					sequence: '30',
					description: '',
					listeningControlUId: 'updateInstruction',
					handler: {
						methodName: "addUpdateInstruction",
						description: "This method will add the new instruction"
					}
				}, {
					eventId: 'addInstruction_onClick',
					sequence: '30',
					description: '',
					listeningControlUId: 'addInstruction',
					handler: {
						methodName: "addUpdateInstruction",
						description: "This method will add the new instruction"
					}
				}, {
					eventId: 'grdInstructions_ScRowSelect',
					sequence: '25',
					description: 'Listens for Row Change',
					handler: {
						methodName: "onSingleRowSelect",
						description: "Handles the single Click from List"
					}
				}
			]
		}

	});
});
