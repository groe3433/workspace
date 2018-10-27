scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/editors/OrderEditorRTExtnUI", "scbase/loader!isccs/utils/RelatedTaskUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderEditorRTExtnUI, _isccsRelatedTaskUtils, _scBaseUtils, _isccsUIUtils, _scScreenUtils
){ 
	return _dojodeclare("extn.editors.OrderEditorRTExtn", [_extnOrderEditorRTExtnUI],{
	//This method will open the manage instruction screen
	openManageInstructionPopup : function (event, bEvent, ctrl, args) {
		var taskInput = null;
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
		var popupParams = null;
		popupParams = _scBaseUtils.getNewBeanInstance();
		var dialogParams = null;
		dialogParams = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.addModelValueToBean("screenInput", taskInput, popupParams);
		_isccsUIUtils.openWizardInEditor("extn.manageInstruction.wizards.manageInstruction.ManageInstructionWizard", taskInput, "isccs.editors.OrderEditor", this);
	},
	//This method will open the wavie mrl fee popup
	openWavieMRLFeePopup : function (event, bEvent, ctrl, args) {
		var taskInput = null;
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
		var popupParams = null;
		popupParams = _scBaseUtils.getNewBeanInstance();
		var dialogParams = null;
		dialogParams = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.addModelValueToBean("screenInput", taskInput, popupParams);
		var popupTitle = _scScreenUtils.getFormattedString(this, "extn_mrl_fee_title", null);
		_isccsUIUtils.openSimplePopup("extn.mrlPopup.MRLPopup", popupTitle, this, popupParams, dialogParams);
	},
	//This method open Revise Ship Date in new wizard
	openReviseShipDate : function (event, bEvent, ctrl, args) {
		var taskInput = null;
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
		var popupParams = null;
		popupParams = _scBaseUtils.getNewBeanInstance();
		var dialogParams = null;
		dialogParams = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.addModelValueToBean("screenInput", taskInput, popupParams);
		_isccsUIUtils.openWizardInEditor("extn.reviseShipDate.wizards.reviseShipDate.ReviseShipDateWizard", taskInput, "isccs.editors.OrderEditor", this);
	},
	openEmailScreen : function (event, bEvent, ctrl, args) {
		var taskInput = null;
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
		var popupParams = null;
		popupParams = _scBaseUtils.getNewBeanInstance();
		var dialogParams = null;
		dialogParams = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.addModelValueToBean("screenInput", taskInput, popupParams);
		_isccsUIUtils.openWizardInEditor("extn.emails.wizards.emails.EmailsWizard", taskInput, "isccs.editors.OrderEditor", this);
	}
});
});

