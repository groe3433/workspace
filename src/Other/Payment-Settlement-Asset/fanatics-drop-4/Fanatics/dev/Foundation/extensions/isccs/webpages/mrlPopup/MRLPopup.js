scDefine([
		"dojo/text!./templates/MRLPopup.html",	"scbase/loader!extn/mrlPopup/MRLPopupUI",
		"scbase/loader!dojo/_base/declare",	"scbase/loader!dojo/_base/kernel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",	"scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"
	],
	function (
		templateText, _extnMRLPopupUI, _dojodeclare, _dojokernel, _scScreenUtils, _scBaseUtils, _scScreen, _isccsWidgetUtils, _scEventUtils, _scModelUtils, _isccsUIUtils, _isccsBaseTemplateUtils, _isccsModelUtils, _scWidgetUtils, _scEditorUtils) {
	return _dojodeclare("extn.mrlPopup.MRLPopup", [_extnMRLPopupUI], {
		//This method will check if the call is for make changes in return reason code, if yes then hide all the controls in the screen and show new dropdown.
		fetchMRLReasonCode : function (event, bEvent, ctrl, args) {
			var originatingScrForOrder = null;
			var originatingScrUidForOrder = null;
			originatingScrForOrder = _scBaseUtils.getValueFromPath("ownerScreen.ownerScreen", this);
			originatingScrUidForOrder = _scScreenUtils.getScreenUId(originatingScrForOrder);
			if (_scBaseUtils.equals(originatingScrUidForOrder, "OrderEditor")) {
				var mrlReasonModel = null;
				var screenModel = null;
				mrlReasonModel = _scBaseUtils.getNewModelInstance();
				screenModel = _scScreenUtils.getInitialInputData(this);
				if (!_scBaseUtils.isVoid(screenModel)) {
					var selectedMRLReason = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.MRLFeeWaiverReason", screenModel);
					var isMRLFee = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.IsMRLFeeWaived", screenModel);
					if (!_scBaseUtils.isVoid(selectedMRLReason) && !_scBaseUtils.isVoid(isMRLFee)) {
						_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.MRLFeeWaiverReason", selectedMRLReason, mrlReasonModel);
						_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.IsMRLFeeWaived", isMRLFee, mrlReasonModel);
						_scScreenUtils.setModel(this, "extn_MRLReasonModel_output", mrlReasonModel, null);
					}
				}
			}
		},
		//This method will be responsible for closing the popup
		closePopup : function (event, bEvent, ctrl, args) {
			_scWidgetUtils.closePopup(this, "CLOSE", false);
		},
		//This method will invoke change order to save the Return reason.
		updateMRLReasonCode : function (event, bEvent, ctrl, args) {
			var targetModel = null;
			targetModel = _scScreenUtils.getTargetModel(this, "extn_TargetReasonCode", null);
			var isMRLFeeWaived = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.IsMRLFeeWaived", targetModel);
			var mrlFeeWaiverReason = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.MRLFeeWaiverReason", targetModel);
			if (_scBaseUtils.isVoid(isMRLFeeWaived) || _scBaseUtils.isVoid(mrlFeeWaiverReason)) {
				_isccsBaseTemplateUtils.showMessage(this, "extn_mrl_error", "error", null);
				return;
			}
			var screenModel = null;
			var changeOrderModel = null;
			changeOrderModel = _scBaseUtils.getNewModelInstance();
			screenModel = _scScreenUtils.getInitialInputData(this);
			if (!_scBaseUtils.isVoid(screenModel)) {
				var orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", screenModel);				
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, targetModel);
				_isccsUIUtils.callApi(this, targetModel, "extn_ChangeOrderForMRL", null);
			}
		},
		//This method will close the pop up screen on successful updation of return reason
		handleMashupOutput : function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (_scBaseUtils.equals(mashupRefId, "extn_ChangeOrderForMRL")) {
				this.handleChangeOrder(modelOutput);
			}
		},
		//This method gets called after success if changeOrder api call and it closed the popup
		handleChangeOrder : function (modelOutput) {
			if (_scScreenUtils.validate(this)) {
				var taskInput = null;
				var currentEditor = _scEditorUtils.getCurrentEditor();
				if (!_scBaseUtils.isVoid(currentEditor)) {
					taskInput = _scScreenUtils.getInitialInputData(currentEditor);
					if (!_scBaseUtils.isVoid(taskInput)) {
						var targetModel = null;
						targetModel = _scScreenUtils.getTargetModel(this, "extn_TargetReasonCode", null);
						var isMRLFee = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.IsMRLFeeWaived", targetModel);
						var mrlReason = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.MRLFeeWaiverReason", targetModel);
						_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.MRLFeeWaiverReason", mrlReason , taskInput);
						_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.IsMRLFeeWaived", isMRLFee , taskInput);
						var args = null;
						args = _scBaseUtils.getNewBeanInstance();
						var modelToAdd = null;
						modelToAdd = _scBaseUtils.getNewBeanInstance();
						_scBaseUtils.addModelValueToBean("model", taskInput, modelToAdd);
						_scBaseUtils.addBeanValueToBean("argumentList", modelToAdd, args);
						_scEventUtils.fireEventToParent(this, "setEditorInput", args);
						parentScreen = _isccsUIUtils.getParentScreen(this, false);
						parentScreen = _isccsUIUtils.getParentScreen(parentScreen, false);
						screenInput = _scScreenUtils.getInitialInputData(parentScreen);
						_isccsOrderUtils.openOrder(parentScreen, screenInput);
						_scWidgetUtils.closePopup(this, "APPLY", false);
					}
				}
			}
		},
		//This method is used to validate the outcome of the mashup invocation
		handleMashupCompletion : function (
			mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
			_isccsBaseTemplateUtils.handleMashupCompletion(
				mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
		}

	});
});
