
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderPricingSummaryAdjustmentsExtnUI","scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderPricingSummaryAdjustmentsExtnUI, _scModelUtils, _isccsUIUtils, _scScreenUtils, _scBaseUtils
){ 
	return _dojodeclare("extn.order.details.OrderPricingSummaryAdjustmentsExtn", [_extnOrderPricingSummaryAdjustmentsExtnUI],{
	applyCouponDesc : function(dataValue, screen, widget, namespace, modelObject, options) {
		if(_scBaseUtils.isVoid(dataValue)) {
			dataValue = _scModelUtils.getStringValueFromPath("Adjustments.TaxName", modelObject);
			if(_scBaseUtils.isVoid(dataValue)) {
				dataValue = _scModelUtils.getStringValueFromPath("Adjustments.ChargeCategory", modelObject);
			}
		}
		var chargeName = null;
		chargeCategory = _scModelUtils.getStringValueFromPath("Adjustments.ChargeCategory", modelObject);
		var orderPricingSummaryScreen = null;
		orderPricingSummaryScreen = _isccsUIUtils.getParentScreen(screen, true);
		var orderModel = null;
		orderModel = _scScreenUtils.getModel(orderPricingSummaryScreen, "getCompleteOrderDetails_output");
		var chargeCategory = null;
		var chargeName = null;
		chargeCategory = _scModelUtils.getStringValueFromPath("Adjustments.ChargeCategory", modelObject);
		chargeName = _scModelUtils.getStringValueFromPath("Adjustments.ChargeName", modelObject);
		if(!_scBaseUtils.isVoid(chargeCategory) && !_scBaseUtils.isVoid(chargeName)) {
			var lstAwards = null;
			lstAwards = _scModelUtils.getModelListFromPath("Order.Awards.Award", orderModel);
			if (!_scBaseUtils.isVoid(lstAwards)) {
				var length = null;
				length = _scBaseUtils.getAttributeCount(lstAwards);
				for (var index = 0;	index < length;	index = index + 1) {
					var award = null;
					award = lstAwards[index];
					var awardChargeCategory = null;
					var awardChargeName = null;
					awardChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", award);
					awardChargeName = _scModelUtils.getStringValueFromPath("ChargeName", award);
					if(_scBaseUtils.equals(chargeCategory, awardChargeCategory) && _scBaseUtils.equals(chargeName, awardChargeName)) {
						var couponDesc = _scModelUtils.getStringValueFromPath("Description", award);
						dataValue = dataValue + " - " + couponDesc;
						return dataValue;
					}
				}
			}	
		}
		return dataValue;
	}
});
});

