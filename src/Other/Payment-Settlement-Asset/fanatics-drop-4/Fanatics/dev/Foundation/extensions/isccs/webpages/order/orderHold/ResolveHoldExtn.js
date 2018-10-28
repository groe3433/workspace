scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/orderHold/ResolveHoldExtnUI", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils",
		"scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnResolveHoldExtnUI, _isccsUIUtils, _scScreenUtils, _scModelUtils, _scGridxUtils, _scBaseUtils, _scPaginationUtils, _scControllerUtils, _scWidgetUtils, _scEventUtils, _isccsBaseTemplateUtils, _scEditorUtils
){ 
	return _dojodeclare("extn.order.orderHold.ResolveHoldExtn", [_extnResolveHoldExtnUI],{
	// custom code here
	
	updateEditorHeader: function(event, bEvent, ctrl, args) {
		_isccsBaseTemplateUtils.updateTitle(_isccsUIUtils.getWizardForScreen(this), "Wizard_ResolveHolds", "Review Holds");
		_scScreenUtils.focusFirstEditableWidget(this);
		return true;
	},
	save: function(event, bEvent, ctrl, args) {
		var getSelectedRecordeModel = null;
		var orderHeaderKey = null;
		var holdResolutionModel = null;
		var holdStatus = null;
		holdResolutionModel = _scScreenUtils.getModel(
		this, "getOrderHoldResolutionList_output");
		orderHeaderKey = _scModelUtils.getStringValueFromPath("Page.OrderHeaderKey", holdResolutionModel, true);
		getSelectedRecordeModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(
		this, "activeHolds");
		var widget = null;
		widget = _scEventUtils.getOriginatingControlUId(bEvent);
		if(_scBaseUtils.equals(widget, "update_holds")) {
			holdStatus = "1200";
		} else {
			holdStatus = "1300";
		}
		var listOfOrderHolds = null;
        listOfOrderHolds = _scModelUtils.getModelListFromPath("Order.OrderHoldTypes.OrderHoldType", getSelectedRecordeModel);
        var length = 0;
        length = _scBaseUtils.getAttributeCount(listOfOrderHolds);
        var orderHold = null;
        for (var counter = 0; counter < length; counter = counter + 1) {
            orderHold = listOfOrderHolds[counter];
			_scBaseUtils.setAttributeValue("Status", holdStatus, orderHold);
		}
		_scBaseUtils.setAttributeValue("Order.OrderHeaderKey", orderHeaderKey, getSelectedRecordeModel);
		var mashupContext = null;
		var inputModelList = null;
		var mashupRefIdList = null;
		var pageSize = null;
		var pageToShow = 1;
		inputModelList = [];
		mashupRefIdList = [];
		var action = null;
		action = _scBaseUtils.getAttributeValue("Action", false, args);
		//mashupRefIdList.push("extn_resolveHolds_changeOrder");
		mashupRefIdList.push("getOrderHoldResolutionList");
		mashupRefIdList.push("getResolvedHolds");
		_scBaseUtils.removeBlankAttributes(getSelectedRecordeModel);
		//inputModelList.push(getSelectedRecordeModel);
		mashupInputActiveHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
		this, "activeHolds");
		pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
		this, "activeHolds");
		pageToShow = this.getPageToShow(
		pageToShow);
		mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
		this, "activeHolds", pageSize, pageToShow, "getOrderHoldResolutionList", mashupContext, null);
		inputModelList.push(
		mashupInputActiveHolds);
		mashupInputResolvedHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
		this, "resolvedHolds");
		pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
		this, "resolvedHolds");
		mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
		this, "resolvedHolds", pageSize, pageToShow, "getResolvedHolds", mashupContext, null);
		inputModelList.push(
		mashupInputResolvedHolds);
		if (
		_scBaseUtils.equals(
		action, "CONFIRM")) {
			_scControllerUtils.setMashupContextIdenitier(
			mashupContext, "CONFIRM");
		} else {
			_scControllerUtils.setMashupContextIdenitier(
			mashupContext, "SAVE_CLICKED");
		}
		_isccsUIUtils.callApi(this, getSelectedRecordeModel, "extn_resolveHolds_changeOrder", null);
		_isccsUIUtils.callApis(this, inputModelList, mashupRefIdList, mashupContext, null);
	},
	onSelectOrDeSelect: function(
	event, bEvent, ctrl, args) {
		if (
		_scGridxUtils.hasSelectedRecords(
		this, "activeHolds")) {
			_scWidgetUtils.enableWidget(
			this, "update_holds", false);
			_scWidgetUtils.enableWidget(
			this, "extn_approvebtn", false);
		} else {
			_scWidgetUtils.disableWidget(
			this, "update_holds", false);
			 _scWidgetUtils.disableWidget(
			this, "extn_approvebtn", false);
		}
	},
	initializeScreen: function(
	event, bEvent, ctrl, args) {
		this.handleGetOrderHoldResolution();
		var holdResolutionModel = null;
		var resolvedHoldsModel = null;
		holdResolutionModel = _scScreenUtils.getModel(
		this, "getOrderHoldResolutionList_output");
		resolvedHoldsModel = _scScreenUtils.getModel(
		this, "getResolvedHolds_output");
		this.determineIfLineHoldsArePresent(
		holdResolutionModel, resolvedHoldsModel);
		this.handleOrderLevelModification(
		holdResolutionModel);
		_scWidgetUtils.disableWidget(
		this, "update_holds", false);
		_scWidgetUtils.disableWidget(
		this, "extn_approvebtn", false);
	},
	handleMashupOutput: function(
	mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		if (
		_scBaseUtils.equals(
		mashupRefId, "extn_resolveHolds_changeOrder")) {
			_scEventUtils.fireEventToParent(
			this, "onSaveSuccess", null);
			_scWidgetUtils.disableWidget(
			this, "update_holds", false);
			_scWidgetUtils.disableWidget(
			this, "extn_approvebtn", false);
		}
		if (
		_scBaseUtils.equals(
		mashupRefId, "getOrderHoldResolutionList")) {
			var mashupIdentifier = null;
			mashupIdentifier = _scControllerUtils.getMashupContextIdenitier(
			mashupContext);
			if (
			_scBaseUtils.equals(
			mashupIdentifier, "SAVE_CLICKED")) {
				_scPaginationUtils.loadPageFromMashupContext(
				this, "activeHolds", mashupContext, mashupRefId);
				_isccsBaseTemplateUtils.showMessage(
				this, "HoldsResolvedSuccess", "success", null);
			} else {
				_scPaginationUtils.loadPageFromMashupContext(
				this, "activeHolds", mashupContext, mashupRefId);
			}
			if (
			_scBaseUtils.getAttributeCount(
			_scModelUtils.getModelListFromPath("Page.Output.OrderHoldTypes.OrderHoldType", modelOutput)) < 1) {
				_scWidgetUtils.disableWidget(
				this, "update_holds", false);
				_scWidgetUtils.disableWidget(
				this, "extn_approvebtn", false);
			}
			if (
			_scBaseUtils.equals(
			this.showPageReloadMessage, "Y")) {
				this.showPageReloadMessage = "N";
				var inputArray = null;
				var pageNum = null;
				pageNum = _scBaseUtils.getValueFromPath("Page.PageNumber", modelOutput);
				inputArray = [];
				inputArray.push(
				pageNum);
				var msg = null;
				msg = _scScreenUtils.getFormattedString(
				this, "Same_Page_Reloaded", inputArray);
				this.showGridMessage(
				msg, "information", null);
			} else {
				this.hideGridMessage();
			}
		}
		if (
		_scBaseUtils.equals(
		mashupRefId, "getResolvedHolds")) {
			var mashupIdentifier = null;
			mashupIdentifier = _scControllerUtils.getMashupContextIdenitier(
			mashupContext);
			if (
			_scBaseUtils.equals(
			mashupIdentifier, "SAVE_CLICKED")) {
				_scPaginationUtils.loadPageFromMashupContext(
				this, "resolvedHolds", mashupContext, mashupRefId);
			} else {
				_scPaginationUtils.loadPageFromMashupContext(
				this, "resolvedHolds", mashupContext, mashupRefId);
			}
			this.determineIfLineHoldsArePresent(
			null, modelOutput);
		}
	}, 
	//This method returns the customer order number
	getCustomerOrderNumber : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
		var customerOrderNo = null;
		 var initialInputData = null;
		initialInputData = _scScreenUtils.getInitialInputData(_scEditorUtils.getCurrentEditor());
		customerOrderNo = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.CustomerOrderNo", initialInputData);
		return customerOrderNo;
	},
	//This method gets the holdstatus description
	getHoldStatus : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
		var status = null;
		status = _scModelUtils.getStringValueFromPath("Status", gridRowJSON);
		if (!_scBaseUtils.isVoid(status)) {
			var str = null;
			if(_scBaseUtils.equals("1100", status)) {
				str = "Created";
				return str;
			}
			if(_scBaseUtils.equals("1200", status)) {
				str = "Rejected";
				return str;
			}
			if(_scBaseUtils.equals("1300", status)) {
				str = "Approved";
				return str;
			}
		}
	}

});
});

