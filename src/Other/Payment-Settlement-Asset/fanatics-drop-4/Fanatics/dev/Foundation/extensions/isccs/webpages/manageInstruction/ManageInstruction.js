scDefine([
		"dojo/text!./templates/ManageInstruction.html",	"scbase/loader!extn/manageInstruction/ManageInstructionUI",
		"scbase/loader!dojo/_base/declare",	"scbase/loader!dojo/_base/kernel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",	"scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils","scbase/loader!isccs/utils/UIUtils"
	],
	function (
		templateText, _extnManageInstructionUI, _dojodeclare, _dojokernel, _scScreenUtils, _scBaseUtils, _scScreen, _isccsWidgetUtils, _scEventUtils, _scModelUtils, _isccsUIUtils, _isccsBaseTemplateUtils, _isccsModelUtils, _scWidgetUtils, _scEditorUtils, _scGridxUtils, _isccsUIUtils) {
	return _dojodeclare("extn.manageInstruction.ManageInstruction", [_extnManageInstructionUI], {
		//This method will close the pop up screen on successful updation of return reason
		handleMashupOutput : function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (_scBaseUtils.equals(mashupRefId, "extn_ChangeOrderForManageInstruction")) {
				_scScreenUtils.setModel(this, "extn_getCompleteOrderDetails_output", modelOutput, null);
				_scWidgetUtils.disableWidget(this, "updateInstruction", false);
				var resetModel = null;
				resetModel = _scBaseUtils.getNewModelInstance();
				_scModelUtils.setStringValueAtModelPath("InstructionType", _scScreenUtils.getString(this, "extn_freeze"), resetModel);
				_scModelUtils.setStringValueAtModelPath("InstructionText", "", resetModel);
				_scModelUtils.setStringValueAtModelPath("InstructionDetailKey", "", resetModel);
				_scScreenUtils.setModel(this, "resetModel", resetModel, null);
				_scScreenUtils.setModel(this, "selectedInstruction", resetModel, null);
			}
		},
		//This method is used to validate the outcome of the mashup invocation
		handleMashupCompletion : function (
			mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
			_isccsBaseTemplateUtils.handleMashupCompletion(
				mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
		},
        updateEditorHeader: function(
        event, bEvent, ctrl, args) {
            _isccsBaseTemplateUtils.updateTitle(
            this, "extn_manageInstaruction_tile", null);
            _scScreenUtils.focusFirstEditableWidget(
            this);
            return true;
        },
        OLL_handleCancel: function(event, bEvent, ctrl, args) {
			var popupParams = null;
			popupParams = {};
			_scBaseUtils.setAttributeValue("HideRelatedChkBox", true, popupParams);
			_scScreenUtils.setModel(this, "HideRelatedChkBox", popupParams, null);
			_scBaseUtils.setAttributeValue("url", null, popupParams);
			var title = null;
			title = _scScreenUtils.getString(this, "Confirm");
			popupParams["title"] = title;
			_scBaseUtils.setAttributeValue("isModal", true, popupParams);
			popupParams["Screen"] = null;
			popupParams["screenId"] = "isccs.order.delete.DeleteConfirmationPopup";
			popupParams["cancelData"] = args;
			var dialogParams = null;
			dialogParams = {};
			dialogParams["closeCallBackHandler"] = "deleteConfirmationPopup";
			dialogParams["class"] = "idxConfirmDialog";
			_isccsUIUtils.openSimplePopup("isccs.order.delete.DeleteConfirmationPopup", title, this, popupParams, dialogParams);
		},
        deleteConfirmationPopup: function(event, bEvent, ctrl, args) {
            var argument = null;
            var deleteRelatedItemFlag = null;
            if (_scBaseUtils.equals(event, "APPLY")) {
				var currentRowIndexValue = null;
				var oldModelData = null;
				var instructionKey = null;
				var orderModelData = null;
				var orderHeaderKey = null;
				currentRowIndexValue = _scBaseUtils.getAttributeValue("cancelData.uniqueRowId", false, ctrl);
				this.selectedRow = currentRowIndexValue;
				oldModelData = _scGridxUtils.getItemFromRowIndexUsingUId(this, "grdInstructions", this.selectedRow);
				instructionKey = _scModelUtils.getStringValueFromPath("InstructionDetailKey", oldModelData);
				orderModelData = _scScreenUtils.getModel(this, "extn_getCompleteOrderDetails_output");
				orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModelData);
				var changeOrder = null;
				changeOrder = _scBaseUtils.getNewModelInstance();
				if (!_scBaseUtils.isVoid(instructionKey) && instructionKey.length > 0 && !_scBaseUtils.isVoid(orderHeaderKey)) {
					_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, changeOrder);
					_scModelUtils.setStringValueAtModelPath("Order.Instructions.Instruction.Action", "REMOVE", changeOrder);
					_scModelUtils.setStringValueAtModelPath("Order.Instructions.Instruction.InstructionDetailKey", instructionKey[0], changeOrder);
					_isccsUIUtils.callApi(this, changeOrder, "extn_ChangeOrderForManageInstruction", null);
				}
            }
        },
		OLST_cellClick : function(event, bEvent, ctrl, args){
			var cellJson = _scBaseUtils.getAttributeValue("cellJson",false,args);
			//setting additional data  for link image fields
			var cellJsonData = _scBaseUtils.getAttributeValue("cellJsonData",false,args);
			var itemData = _scBaseUtils.getAttributeValue("item",false,args);
			if (!_scBaseUtils.isVoid(itemData) && _scBaseUtils.isVoid(cellJsonData))
				_scBaseUtils.setAttributeValue("cellJsonData", itemData, args);
			var uniqueRowId = _scBaseUtils.getAttributeValue("uniqueRowId",false,args);
			var rowIndex = _scBaseUtils.getAttributeValue("rowIndex",false,args);
			if (!_scBaseUtils.isVoid(rowIndex) && _scBaseUtils.isVoid(uniqueRowId))
				_scBaseUtils.setAttributeValue("uniqueRowId", rowIndex, args);
			//
			if (!(_scBaseUtils.isVoid(cellJson))){
				if (_scBaseUtils.equals("DeleteInstruction", _scBaseUtils.getAttributeValue("colField", false, cellJson))) {
					this.OLL_handleCancel(event, bEvent, ctrl, args);
				}
			}
		}, 
		//This method will add new instruction
		addUpdateInstruction : function(event, bEvent, ctrl, args){
			var orderModelData = null;
			var orderHeaderKey = null;
			var instructionText = null;
			var targetModel = null;
			orderModelData = _scScreenUtils.getModel(this, "extn_getCompleteOrderDetails_output");
			var widget = "";
			widget = _scEventUtils.getOriginatingControlUId(bEvent);
			if(_scBaseUtils.equals("updateInstruction", widget)) {
				targetModel = _scScreenUtils.getTargetModel(this, "extn_ChangeOrderForManageInstructionUpd", null);
			} else {
				targetModel = _scScreenUtils.getTargetModel(this, "extn_ChangeOrderForManageInstruction", null);
			}
			
			orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModelData);
			instructionText = _scModelUtils.getStringValueFromPath("Order.Instructions.Instruction.InstructionText", targetModel);
			if (_scBaseUtils.isVoid(instructionText)) {
				return;
			}
			var changeOrder = null;
			changeOrder = _scBaseUtils.getNewModelInstance();
			if (!_scBaseUtils.isVoid(orderHeaderKey)) {
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, targetModel);
				_isccsUIUtils.callApi(this, targetModel, "extn_ChangeOrderForManageInstruction", null);
			}
		},
		onSingleRowSelect: function(event, bEvent, ctrl, args) {
            var model = null;
            var selectRowData = null;
            var rowIndex = 0;
            model = {};
            rowIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", args);
            this.selectedRow = rowIndex;
            selectRowData = _scBaseUtils.getAttributeValue("selectedRow", false, args);
            if (!(_scBaseUtils.isVoid(selectRowData))) {
				_scScreenUtils.setModel(this, "selectedInstruction", selectRowData, null);
				 _scWidgetUtils.enableWidget(this, "updateInstruction");
			}
		}
	});
});
