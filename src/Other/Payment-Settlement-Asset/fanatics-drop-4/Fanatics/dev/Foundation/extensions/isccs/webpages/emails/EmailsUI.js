scDefine([
		"dojo/text!./templates/Emails.html",
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!dojo/_base/kernel",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/widgets/Screen", 
		"scbase/loader!isccs/utils/OrderLineUtils", 
		"scbase/loader!isccs/utils/OrderUtils"
	],
	function (
		templateText,
		_dojodeclare,
		_dojokernel,
		_scScreenUtils,
		_scBaseUtils,
		_scScreen, 
		_isccsOrderLineUtils,
		_isccsOrderUtils) {
	return _dojodeclare("extn.emails.EmailsUI", [_scScreen, ], {
		templateString : templateText,
		baseTemplate : {
			url : _dojokernel.moduleUrl("extn.emails.templates", "Emails.html"),
			shared : true
		},
		uId : "emails",
		packageName : "extn.emails",
		className : "Emails",
		showRelatedTask : false,
		selectedRow: 0,
		isDirtyCheckRequired : true,
		namespaces : {
			targetBindingNamespaces : [{
					value : 'extn_TargetReasonCode',
					description : "This mashup will hold the input to update order line"
				}
			],
			sourceBindingNamespaces : [{
					value: 'extn_getReviseShipDateReasonCode_output',
					description: "This namespace will hold the revise ship date reason code"
				},{
					value: 'extn_getCompleteEXTNFanEmail_confirmation_output',
					description: "This namespace will hold confirmation email ids"
				},{
					value: 'extn_getCompleteEXTNFanEmail_shipment_output',
					description: "This namespace will hold shipment confirmation email ids"
				},{
					value: 'extn_getChargeTransactionList_output',
					description: "This namespace will hold Charge Transaction details"
				},{
					value: 'extn_chargetransaction_getEXTNFanEmail_output',
					description: "This namespace will hold Charge Transaction details"
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
					eventId: 'updateEmails_onClick',
					sequence: '30',
					description: '',
					listeningControlUId: 'updateEmails',
					handler: {
						methodName: "updateEmails",
						description: "This method will update the revise shipdate and reason to the selected order line"
					}
				}, {
                eventId: 'OLST_listGrid_ScRowSelect',
                sequence: '30',
					handler: {
						methodName: "onRowSelect"
					}
				}, {
                eventId: 'OLST_listGrid_ScHeaderSelect',
                sequence: '30',
					handler: {
						methodName: "onGridALLSelectDeselect"
					}
				}, {
                eventId: 'OLST_listGrid_ScRowDeselect',
                sequence: '30',
					handler: {
						methodName: "onRowSelect"
					}
				}, {
                eventId: 'OLST_listGrid_ScHeaderDeselect',
                sequence: '30',
					handler: {
						methodName: "onGridALLSelectDeselect"
					}
				}
			]
		}

	});
});
