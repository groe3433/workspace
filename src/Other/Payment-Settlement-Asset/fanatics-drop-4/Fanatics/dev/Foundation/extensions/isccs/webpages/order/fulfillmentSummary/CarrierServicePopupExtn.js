
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/fulfillmentSummary/CarrierServicePopupExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!extn/utils/CustomUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnCarrierServicePopupExtnUI, _scBaseUtils, _scModelUtils, _scEventUtils, _customUtils, _scScreenUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.CarrierServicePopupExtn", [_extnCarrierServicePopupExtnUI],{
	// custom code here
	handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		if (
		_scBaseUtils.equals(
		mashupRefId, "getCarrierServiceOptionsForOrdering")) {
			_customUtils.formatDescriptionInCombo(this, modelOutput);
			_scBaseUtils.setModel(
			this, "CarrierServiceData", modelOutput, null);
			_scBaseUtils.setModel(
			this, "CarrierServiceSourceData", modelOutput, null);
			var carrierServiceCode = null;
			this.InitialCarrierServiceCode = _scModelUtils.getStringValueFromPath("CarrierServiceList.CarrierServiceCode", modelOutput);
			var carrierServiceModel = null;
			carrierServiceModel = _scModelUtils.getModelObjectFromPath("CarrierServiceList", modelOutput);
			var carrierServiceArgs = null;
			carrierServiceArgs = {};
			_scBaseUtils.setAttributeValue("argumentList.CarrierServiceData", carrierServiceModel, carrierServiceArgs);
			_scEventUtils.fireEventGlobally(
			this, "SetCarrierServiceModelInParent", null, carrierServiceArgs);
		}
	},
	
	populateOverrideShippingModel: function(event, bEvent, ctrl, args) {
		var screenInput = null;
		screenInput = _scScreenUtils.getInitialInputData(this);
		_scScreenUtils.setModel(this, "extn_getCompleteOrderDetails", screenInput, null);
	}
});
});

