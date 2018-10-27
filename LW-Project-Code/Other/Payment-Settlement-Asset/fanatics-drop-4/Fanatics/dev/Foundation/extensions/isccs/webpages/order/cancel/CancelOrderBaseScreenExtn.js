
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/cancel/CancelOrderBaseScreenExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils",  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils",  "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!isccs/utils/BaseTemplateUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnCancelOrderBaseScreenExtnUI, _scBaseUtils, _scScreenUtils, _scModelUtils, _isccsUIUtils, _scWizardUtils, _isccsOrderUtils, _scEditorUtils, _scEventUtils, _scWidgetUtils, _scGridxUtils, _isccsBaseTemplateUtils
){ 
	return _dojodeclare("extn.order.cancel.CancelOrderBaseScreenExtn", [_extnCancelOrderBaseScreenExtnUI],{
	// custom code here
	handleConfirm: function(res, mashupContext) {
		if (
		_scBaseUtils.equals(
		res, "Ok")) {
			var cancelOrder = null;
			cancelOrder = _scScreenUtils.getModel(this, "cancelOrderReasonCode_input");
			// This is added for cancel Exchange Flow
			sScreenType = _scWizardUtils.getCurrentPageScreenType(_isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor()));
			if (sScreenType == "CancelExchange") {
				initialInputData = _scScreenUtils.getInitialInputData(this);
				var exchangeOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.ExchangeOrders.ExchangeOrder.0.OrderHeaderKey", initialInputData);
				_scBaseUtils.setAttributeValue("Order.OrderHeaderKey", exchangeOrderHeaderKey, cancelOrder);
			}
			var orderCurrency = null;
			orderCurrency = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			if (!_scBaseUtils.isVoid(orderCurrency)) {
				var fanCashmodel = null;
				var noteText = null;
				var userEnteredAmt = null;
				var fanCashReason = null;
				fanCashmodel = _scScreenUtils.getTargetModel(this, "extn_fancashModel", null);
				userEnteredAmt = _scModelUtils.getNumberValueFromPath("Order.FanCash", fanCashmodel);				
				userEnteredAmt = _isccsOrderUtils.getFormattedCurrency(orderCurrency, userEnteredAmt);
				fanCashReason = _scModelUtils.getStringValueFromPath("Order.FanCashReason", fanCashmodel);
				getOrderNotesInput = _scScreenUtils.getTargetModel(this, "getOrderNoteInput", null);
				cancelReason = _scModelUtils.getStringValueFromPath("Order.ReasonCode", getOrderNotesInput);
				var array = [];
				array[0] = userEnteredAmt;
				array[1] = fanCashReason;
				array[2] = cancelReason;
				noteText = _scScreenUtils.getFormattedString(this, "extn_addNotesForFancash", array);
				_scBaseUtils.setAttributeValue("Order.Notes.Note.NoteText", noteText, cancelOrder);
			}
			//End of cancel exchange flow
			_isccsUIUtils.callApi(this, cancelOrder, "cancelOrderMashupRef", mashupContext);
		}
	},
	initializeLayout: function(
	event, bEvent, ctrl, args) {
		var isLinesModificationAllowed = false;
		isLinesModificationAllowed = _scBaseUtils.getBooleanValueFromBean("isLinesModificationAllowed", args, false);
		var getCompleteOrderDetails_output = null;
		getCompleteOrderDetails_output = _scScreenUtils.getModel(
		this, "getCompleteOrderDetails_output");
		this.statusOfOrder = _scModelUtils.getStringValueFromPath("Order.Status", getCompleteOrderDetails_output);
		var modificationsList = null;
		modificationsList = _scModelUtils.getModelListFromPath("Order.Modifications.Modification", getCompleteOrderDetails_output);
		var modificationObj = null;
		modificationObj = modificationsList[
		0];
		var modificationAllowed = null;
		modificationAllowed = _scModelUtils.getStringValueFromKey("ModificationAllowed", modificationObj);
		if (
		_scBaseUtils.equals(
		this.action, "NEXT")) {
			_isccsBaseTemplateUtils.hideMessage(
			this);
		}
		if (
		_scBaseUtils.equals(
		modificationAllowed, "N")) {
			this.canOrderBeModified = false;
		}
		var hiddenCancelQtyColn = true;
		if (
		_scBaseUtils.equals(
		isLinesModificationAllowed, false)) {
			_scWidgetUtils.showWidget(
			this, "lblnoCancellation", false, null);
			_scWidgetUtils.hideWidget(
			this, "cmbReasoncode", false);
			_scWidgetUtils.hideWidget(
			this, "extn_fancashtxtbox", false);
			_scWidgetUtils.hideWidget(
			this, "extn_fancashreason", false);
			_scWidgetUtils.hideWidget(
			this, "cancelType", false);
			_scWidgetUtils.setWidgetNonMandatory(this,"cmbReasoncode");
			_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashtxtbox");
			_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashreason");
			if (
			_scBaseUtils.equals(
			this.isAnyLineCancelledInCancelFlow, 0)) {
				/* This is done to handle a scenario in Cancel Return flow. 
				Scenario : CancelReturn (cancel one line) >>Next>> Cancel Exchange (Cancel all lines) >>Next>> Payment >>Previous>> Cancel Exchange >>Previous>> 
				Cancel Return >>Next>> Cancel Exchange (The next button is disabled and CSR can't see payment)
				*/
				 sScreenType = _scWizardUtils.getCurrentPageScreenType(_isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor()));
				if (!(sScreenType == "CancelExchange")) {
					_scEventUtils.fireEventToParent(this, "disableNextButton", null);
				}
			}
		} else {
			_scEventUtils.fireEventToParent(
			this, "enableNextButton", null);
			_scWidgetUtils.hideWidget(
			this, "lblnoCancellation", false);
			_scWidgetUtils.showWidget(
			this, "cmbReasoncode", false, null);
			_scWidgetUtils.showWidget(
			this, "extn_fancashtxtbox", false, null);
			_scWidgetUtils.showWidget(
			this, "extn_fancashreason", false, null);
			_scWidgetUtils.showWidget(
			this, "cancelType", false, null);
			_scWidgetUtils.setWidgetMandatory(this,"cmbReasoncode");
			_scWidgetUtils.setWidgetMandatory(this,"extn_fancashtxtbox");
			_scWidgetUtils.setWidgetMandatory(this,"extn_fancashreason");
			var defaultCancelType = "01";
			var cancelRadioModel = null;
			cancelRadioModel = {};
			if (
			_scBaseUtils.equals(
			modificationAllowed, "N")) {
				_scWidgetUtils.disableOptionsInListWidget(
				this, "cancelType", "01");
				defaultCancelType = "02";
				hiddenCancelQtyColn = false;
				_scModelUtils.setStringValueAtModelPath("Order.CancelType", defaultCancelType, cancelRadioModel);
				 sScreenType = _scWizardUtils.getCurrentPageScreenType(_isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor()));
				if (sScreenType == "CancelExchange") {
					_scModelUtils.setStringValueAtModelPath("Order.CancelType", "00", cancelRadioModel);
					_scWidgetUtils.setWidgetNonMandatory(this,"cmbReasoncode");
					_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashtxtbox");
					_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashreason");
				}
				var setModelOptns = null;
				setModelOptns = {};
				_scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptns);
				_scScreenUtils.setModel(
				this, "getCancelProp_output", cancelRadioModel, setModelOptns);
			} else {
				if (
				_scBaseUtils.equals(
				this.isCancelTypeChanged, "F")) {
					_scModelUtils.setStringValueAtModelPath("Order.CancelType", defaultCancelType, cancelRadioModel);
					if (
					_scBaseUtils.equals(
					defaultCancelType, "02")) {
						hiddenCancelQtyColn = false;
					}
					var setModelOptns = null;
					setModelOptns = {};
					_scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptns);
					sScreenType = _scWizardUtils.getCurrentPageScreenType(_isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor()));
					if (sScreenType == "CancelExchange") {
						_scModelUtils.setStringValueAtModelPath("Order.CancelType", "00", cancelRadioModel);
						_scWidgetUtils.setWidgetNonMandatory(this,"cmbReasoncode");
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashtxtbox");
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashreason");
					}
					_scScreenUtils.setModel(
					this, "getCancelProp_output", cancelRadioModel, setModelOptns);
				} else {
					var cancelPropModel = null;
					cancelPropModel = _scScreenUtils.getTargetModel(
					this, "getCancelProp_input", null);
					var cancelType = null;
					cancelType = _scModelUtils.getStringValueFromPath("Order.CancelType", cancelPropModel);
					if (
					_scBaseUtils.equals(
					cancelType, "02")) {
						hiddenCancelQtyColn = false;
					}
					_scModelUtils.setStringValueAtModelPath("Order.CancelType", cancelType, cancelRadioModel);
					sScreenType = _scWizardUtils.getCurrentPageScreenType(_isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor()));
					if (sScreenType == "CancelExchange") {
						_scModelUtils.setStringValueAtModelPath("Order.CancelType", "00", cancelRadioModel);
						_scWidgetUtils.setWidgetNonMandatory(this,"cmbReasoncode");
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashtxtbox");
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_fancashreason");
					}
					var setModelOptns = null;
					setModelOptns = {};
					_scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptns);
					_scScreenUtils.setModel(
					this, "getCancelProp_output", cancelRadioModel, setModelOptns);
				}
			}
		}
		if (
		_scBaseUtils.equals(
		hiddenCancelQtyColn, true)) {
			var eventDefn = null;
			var blankModel = null;
			eventDefn = {};
			blankModel = {};
			eventDefn["argumentList"] = blankModel;
			_scBaseUtils.setAttributeValue("argumentList.showCancelQtyColumn", false, eventDefn);
			_scEventUtils.fireEventToChild(
			this, "cancelOrderListScreen", "showGridWithHiddenCancelQty", eventDefn);
		}
	},
	cancelType_changed: function(
	event, bEvent, widgetId, args) {
		this.isCancelTypeChanged = "T";
		cancelOrderListScreenObj = _scScreenUtils.getChildScreen(
			this, "cancelOrderListScreen");
		var eventDefn = null;
		var blankModel = null;
		eventDefn = {};
		blankModel = {};
		eventDefn["argumentList"] = blankModel;
		var cancelPropModel = null;
		cancelPropModel = _scScreenUtils.getTargetModel(
		this, "getCancelProp_input", null);
		var cancelType = null;
		cancelType = _scModelUtils.getStringValueFromPath("Order.CancelType", cancelPropModel);
		if (
		_scBaseUtils.equals(
		cancelType, "01") ||_scBaseUtils.equals(
		cancelType, "00") ) {
			if (!(
			_scBaseUtils.isVoid(
			cancelOrderListScreenObj))) {
				_scGridxUtils.deselectAllRowsInGridUsingUId(
				cancelOrderListScreenObj, "OLST_listGrid");
			}
			_scWidgetUtils.disableWidget(
			this, "update_order", false);
			_scBaseUtils.setAttributeValue("argumentList.showCancelQtyColumn", false, eventDefn);
		} else{
			_scBaseUtils.setAttributeValue("argumentList.showCancelQtyColumn", true, eventDefn);
			_scWidgetUtils.disableWidget(
			this, "update_order", false);
		} 
		
		if (!_scBaseUtils.equals(cancelType, "00")) {
			_scWidgetUtils.setWidgetMandatory(this, "cmbReasoncode");
			_scWidgetUtils.setWidgetMandatory(this, "extn_fancashtxtbox");
			_scWidgetUtils.setWidgetMandatory(this, "extn_fancashreason");
		}
		else {
			_scWidgetUtils.setWidgetNonMandatory(this, "cmbReasoncode");
			_scWidgetUtils.setWidgetNonMandatory(this, "extn_fancashtxtbox");
			_scWidgetUtils.setWidgetNonMandatory(this, "extn_fancashreason");
		}
		_scEventUtils.fireEventToChild(
		this, "cancelOrderListScreen", "showGridWithHiddenCancelQty", eventDefn);
	}
});
});

