scDefine([
		"dojo/text!./templates/ReviseShipDate.html",	"scbase/loader!extn/reviseShipDate/ReviseShipDateUI",
		"scbase/loader!dojo/_base/declare",	"scbase/loader!dojo/_base/kernel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",	"scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils","scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!isccs/utils/OrderUtils"
	],
	function (
		templateText, _extnReviseShipDateUI, _dojodeclare, _dojokernel, _scScreenUtils, _scBaseUtils, _scScreen, _isccsWidgetUtils, _scEventUtils, _scModelUtils, _isccsUIUtils, _isccsBaseTemplateUtils, _isccsModelUtils, _scWidgetUtils, _scEditorUtils, _scGridxUtils, _isccsUIUtils, _scPaginationUtils, _isccsOrderUtils) {
	return _dojodeclare("extn.reviseShipDate.ReviseShipDate", [_extnReviseShipDateUI], {
		//This method will close the pop up screen on successful updation of return reason
		handleMashupOutput : function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (_scBaseUtils.equals(mashupRefId, "extn_ChangeOrderForReviseShipDate")) {
				this.initializeScreen(this, null, null, null);
				var resetModel = null;
				resetModel = {};
				resetModel.RevisedShipDateReason="";
                _scScreenUtils.setModel(this, "resetModel", resetModel, null);
                _scScreenUtils.clearScreen(this, null);
			}
			if (_scBaseUtils.equals(mashupRefId, "getCompleteOrderLineList")) {
                _scPaginationUtils.loadPageFromMashupContext(
                this, "OLST_listGrid", mashupContext, mashupRefId);
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
            this, "extn_reviseShipDate_tile", null);
            _scScreenUtils.focusFirstEditableWidget(
            this);
            return true;
        },
		//This method will add new instruction
	updateReviseShipDate : function(event, bEvent, ctrl, args){
		if (!(isScreenValid = _scScreenUtils.validate(this))) {
			_isccsBaseTemplateUtils.showMessage(this, "Message_screenHasErrors", "error", null);
			return;
		}
		var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var lengthOfSourceRecord = null;
            lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
            selectedSourceRecordsList);
            if (
            _scBaseUtils.equals(
            lengthOfSourceRecord, 0)) {
                var warningString = null;
                warningString = _scScreenUtils.getString(
                this, "WARNING_selecteOneItem");
                var textObj = null;
                textObj = {};
                var textOK = null;
                textOK = _scScreenUtils.getString(
                this, "OK");
                textObj["OK"] = textOK;
                _scScreenUtils.showErrorMessageBox(
                this, warningString, "waringCallback", textObj, null);
            } else {
				var selectedRecords = null;
				var orderModel = null;
				var targetModel = null;
				targetModel = _scBaseUtils.getTargetModel(this, "extn_TargetReasonCode", null);
                orderModel = _scScreenUtils.getInitialInputData(this);
				selectedRecords = _scGridxUtils.getSelectedSourceRecordsUsingUId(
                this, "OLST_listGrid");
				 var orderLines = null;
                orderLines = _scModelUtils.getModelListFromPath("OrderLines.OrderLine", selectedRecords);
				 var apiInput = null;
                apiInput = this.formInputChangeOrder(orderLines, orderModel, targetModel);
                		_isccsUIUtils.callApi(this, apiInput, "extn_FanManageEmailEntry", null);
				_isccsUIUtils.callApi(this, apiInput, "extn_ChangeOrderForReviseShipDate", null);
			}
		},
        formInputChangeOrder: function(orderLines, orderModel, targetModel) {
            var apiInput = null;
            apiInput = {};
            var input = null;
            input = {};
            _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), input);
            var length = null;
            length = _scBaseUtils.getAttributeCount(orderLines);
            var orderLinesArray = null;
            orderLinesArray = [];
            for (var i = 0; i < length; i = i + 1) {
                var orderLine = null;
                orderLine = {};
                var orderLineElem = null;
                orderLineElem = orderLines[i];
                _scModelUtils.setStringValueAtModelPath("OrderLineKey", _scModelUtils.getStringValueFromPath("OrderLineKey", orderLineElem), orderLine);
				_scModelUtils.setStringValueAtModelPath("CustomAttributes.RevisedShipDateReason", _scModelUtils.getStringValueFromPath("OrderLine.RevisedShipDateReason", targetModel), orderLine);
                _scModelUtils.setStringValueAtModelPath("OrderDates.OrderDate.ExpectedDate", _scModelUtils.getStringValueFromPath("OrderLine.RevisedShipDate", targetModel), orderLine);
                orderLinesArray.push(orderLine);
            }
            _scModelUtils.addListToModelPath("Order.OrderLines.OrderLine", orderLinesArray, input);
            return input;
        },
		initializeScreen : function(event, bEvent, ctrl, args) {
            var options = null;
			var gridID = "OLST_listGrid";
			var orderModel = null;
			var inputData = null;
			orderModel = _scScreenUtils.getInitialInputData(this);
			inputData = _scBaseUtils.getNewModelInstance();
			_scModelUtils.setStringValueAtModelPath("OrderLine.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), inputData);
			options = {};
			_scBaseUtils.setAttributeValue("screen", this, options);
			_scBaseUtils.setAttributeValue("mashupRefId", "extn_getCompleteOrderLineList_reviseShipDate", options);
			_isccsUIUtils.startPagination(this, gridID, inputData, options);
		},
        pagingLoad: function(
        event, bEvent, ctrl, args) {
            var isLargeOrder = null;
            isLargeOrder = _scModelUtils.getStringValueFromPath("IsLargeOrder", _scScreenUtils.getModel(
            this, "extn_getCompleteOrderLineList_reviseShipDate_output"));
            if (
            _scBaseUtils.isVoid(
            isLargeOrder) || _scBaseUtils.equals(
            isLargeOrder, "Y")) {
                _scPaginationUtils.showPaginationBar(
                this, "OLST_listGrid", false);
            } else {
                _scPaginationUtils.hidePaginationBar(
                this, "OLST_listGrid", true);
            }
        },
        isGridRowDisabled: function(
        rowData, screen) {
            var lineMaxStatus = null;
            lineMaxStatus = _scModelUtils.getStringValueFromPath("MaxLineStatus", rowData);
            var extndStatus = null;
            extndStatus = _isccsOrderUtils.getExtendedStatus(
            lineMaxStatus);
            if (
            _scBaseUtils.greaterThan(
            extndStatus, 3200) || _scBaseUtils.equals(
            _scModelUtils.getStringValueFromPath("HasNonCancellableChainedLines", rowData), "Y")) {
                return true;
            } else {
                return false;
            }
        }
	});
});
