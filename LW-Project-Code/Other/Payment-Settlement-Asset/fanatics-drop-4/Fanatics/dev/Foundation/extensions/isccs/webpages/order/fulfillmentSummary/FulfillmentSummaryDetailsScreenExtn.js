scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/fulfillmentSummary/FulfillmentSummaryDetailsScreenExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!isccs/utils/OrderUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnFulfillmentSummaryDetailsScreenExtnUI, _scBaseUtils, _scScreenUtils, _scModelUtils, _isccsOrderUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtn", [_extnFulfillmentSummaryDetailsScreenExtnUI],{
	// custom code here
	//upadte order with override shipping charge custom attribute
	updateOrder: function(event, bEvent, ctrl, args) {
            if (
            _scScreenUtils.validate(
            this)) {
                var mashupContext = null;
                if (
                _scScreenUtils.isDirty(
                this, null, false)) {
                    if (!(
                    _scBaseUtils.isVoid(
                    args))) {
                        mashupContext = _scBaseUtils.getBeanValueFromBean("mashupContext", args);
                    }
                    var options = null;
                    options = {};
                    options["modified"] = true;
                    options["deleted"] = true;
                    options["allowEmpty"] = true;
                    var addedModifiedtargetModel = null;
                    addedModifiedtargetModel = _scBaseUtils.getTargetModel(
                    this, "getFulfillmentSummaryDetails_input", options);
                    var optimizationType = null;
                    var optimizationTypeOutput = null;
                    optimizationTypeOutput = _scScreenUtils.getModel(
                    this, "optimizationTypeOutput");
                    if (!(
                    _scBaseUtils.isVoid(
                    optimizationTypeOutput))) {
                        optimizationType = _scModelUtils.getStringValueFromPath("Order.OptimizationType", optimizationTypeOutput);
                        _scModelUtils.setStringValueAtModelPath("Order.OptimizationType", optimizationType, addedModifiedtargetModel);
                    }
                    var modelForMFO = null;
                    modelForMFO = _isccsOrderUtils.prepareModel(
                    this, addedModifiedtargetModel, mashupContext);
					 var model = null;
					 model = _scBaseUtils.getModelValueFromBean("model", args);
					 var str = _scModelUtils.getStringValueFromPath("OverrideShippingCharge", model);
					 _scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.OverrideShippingCharge", str, modelForMFO);
                    this.callSaveOrder(
                    modelForMFO, mashupContext);
                } else {
                    _scEventUtils.fireEventToParent(
                    this, "onSaveSuccess", null);
                }
            }
        }
	
});
});

