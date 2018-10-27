
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderPricingSummaryExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderPricingSummaryExtnUI, _scBaseUtils, _scScreenUtils, _scModelUtils
){ 
	return _dojodeclare("extn.order.details.OrderPricingSummaryExtn", [_extnOrderPricingSummaryExtnUI],{
	getHeaderTaxes : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var headerTax = 0.00;
		var lstheaderTax = _scModelUtils.getModelListFromPath("Order.HeaderTaxes.HeaderTax", dataValue);
		if (!_scBaseUtils.isVoid(lstheaderTax)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstheaderTax);
			for (var index = 0;	index < length;	index = index + 1) {
				var tax = null;
				tax = lstheaderTax[index];
				headerTax = parseFloat(headerTax) + parseFloat(_scModelUtils.getStringValueFromPath("Tax", tax));
			}
		}	
		return "$"+headerTax.toFixed(2);
	},	
	getHeaderCharges : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var headerCharge = 0.00;
		var lstheaderCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstheaderCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstheaderCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstheaderCharge[index];
				var HeaderChargeCategory = null;
				var HeaderChargeName = null;
				var IsDiscount = null;
				HeaderChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				HeaderChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "N")) {
					headerCharge = parseFloat(headerCharge) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", charge));
				}else if(_scBaseUtils.equals(IsDiscount, "Y") && _scBaseUtils.equals(HeaderChargeCategory,"MRLFee")){
					headerCharge = parseFloat(headerCharge) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", charge));
				}
			}
		}
		
		var document_type = _scModelUtils.getStringValueFromPath("Order.DocumentType", dataValue);
		if(_scBaseUtils.equals(document_type, "0003")) {
			if(headerCharge<0)
				headerCharge = headerCharge*-1;
			return "($"+headerCharge.toFixed(2)+")";
		}else{
			return "$"+headerCharge.toFixed(2);
		}
	},	
	getFancashAmount : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var fancashValue = 0.00;
		var lstLineCharge = _scModelUtils.getModelListFromPath("OrderLine.LineCharges.LineCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstLineCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstLineCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstLineCharge[index];
				var LineChargeCategory = null;
				var LineChargeName = null;
				LineChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				LineChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				if(_scBaseUtils.equals(LineChargeCategory, "Fancash") || _scBaseUtils.equals(LineChargeName, "Fancash")) {
					fancashValue = parseFloat(fancashValue) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", charge));
				}
			}
		}	
		return "($"+fancashValue.toFixed(2)+")";
	},
	getLineCharges : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var lineCharge = 0.00;
		var lstLineCharge = _scModelUtils.getModelListFromPath("OrderLine.LineCharges.LineCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstLineCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstLineCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstLineCharge[index];
				var LineChargeCategory = null;
				var LineChargeName = null;
				var IsDiscount = null;
				LineChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				LineChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "N") && !_scBaseUtils.equals(LineChargeName, "Fancash")) {
					lineCharge = parseFloat(lineCharge) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", charge));
				}
			}
		}	
		return "$"+lineCharge.toFixed(2);
	},
	getLineAppeasements: function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var lineAppeasement= 0.00;
		var lstLineCharge = _scModelUtils.getModelListFromPath("OrderLine.LineCharges.LineCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstLineCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstLineCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstLineCharge[index];
				var LineChargeCategory = null;
				var LineChargeName = null;
				var IsDiscount = null;
				LineChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				LineChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "Y") && _scBaseUtils.equals(LineChargeName, "Appeasements")) {
					lineAppeasement = parseFloat(lineAppeasement) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", charge));
				}
			}
		}	
		return "($"+lineAppeasement.toFixed(2)+")";
	},
	getHeaderAppeasements: function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var headerAppeasement= 0.00;
		var lstheaderCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstheaderCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstheaderCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstheaderCharge[index];
				var HeaderChargeCategory = null;
				var HeaderChargeName = null;
				var IsDiscount = null;
				HeaderChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				HeaderChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "Y") && _scBaseUtils.equals(HeaderChargeName, "Appeasements")) {
					headerAppeasement = parseFloat(headerAppeasement) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", charge));
				}
			}
		}	
		return "($"+headerAppeasement.toFixed(2)+")";
	},
	getLineTaxes: function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var lineTax = 0.00;
		var lstLineTax = _scModelUtils.getModelListFromPath("OrderLine.LineTaxes.LineTax", dataValue);
		if (!_scBaseUtils.isVoid(lstLineTax)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstLineTax);
			for (var index = 0;	index < length;	index = index + 1) {
				var tax = null;
				tax = lstLineTax[index];
				var taxName = null;
				taxName = _scModelUtils.getStringValueFromPath("TaxName", tax);
				lineTax = parseFloat(lineTax) + parseFloat(_scModelUtils.getStringValueFromPath("Tax", tax));
			}
		}	
		return "$"+lineTax.toFixed(2)+"";
	}, 
	getLineDiscounts: function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var lineDiscount = 0.00;
		var lstLineCharge = _scModelUtils.getModelListFromPath("OrderLine.LineCharges.LineCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstLineCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstLineCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstLineCharge[index];
				var LineChargeCategory = null;
				var LineChargeName = null;
				var IsDiscount = null;
				LineChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				LineChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "Y") && !_scBaseUtils.equals(LineChargeName, "Fancash") && !_scBaseUtils.equals(LineChargeName, "Appeasements")) {
					lineDiscount = parseFloat(lineDiscount) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", charge));
				}
			}
		}	
		return "($"+lineDiscount.toFixed(2)+")";
	},
	getHeaderDiscounts: function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var headerDiscount = 0.00;
		var lstheaderCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", dataValue);
		if (!_scBaseUtils.isVoid(lstheaderCharge)) {
			var length = null;
			length = _scBaseUtils.getAttributeCount(lstheaderCharge);
			for (var index = 0;	index < length;	index = index + 1) {
				var charge = null;
				charge = lstheaderCharge[index];
				var HeaderChargeCategory = null;
				var HeaderChargeName = null;
				var IsDiscount = null;
				HeaderChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", charge);
				HeaderChargeName = _scModelUtils.getStringValueFromPath("ChargeName", charge);
				IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", charge);
				if(_scBaseUtils.equals(IsDiscount, "Y") && !_scBaseUtils.equals(HeaderChargeName, "Appeasements") && !_scBaseUtils.equals(HeaderChargeCategory, "MRLFee")) {
					headerDiscount = parseFloat(headerDiscount) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", charge));
				}
			}
		}	
		return "($"+headerDiscount.toFixed(2)+")";
	},
	loadPricingSummaryAdj : function(event, bEvent, ctrl, args) {
		var orderLines = null;
		var orderModel = null;
        orderLines = _scBaseUtils.getModelValueFromBean("model", args);
		orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
		var adjustmentArray = null;
		adjustmentArray = [];
		if (!_scBaseUtils.isVoid(orderLines)) {
			for (var i = 0; i < orderLines.length; i++) {
				var orderLineElem = null;
				orderLineElem = orderLines[i];
				var lineAdjustments = _scModelUtils.getModelListFromPath("LineAdjustments.LineAdjustment", orderLineElem);
				if (!_scBaseUtils.isVoid(lineAdjustments)) {
					for (var j = 0; j < lineAdjustments.length; j++) {
						var lineAdjustment = lineAdjustments[j];
						var totalAdjustment = null;
						var foundMatchingAdjustment = false;
						totalAdjustment = {};
						var description = null;
						description = _scModelUtils.getStringValueFromPath("Description", lineAdjustment);
						for (var k = 0; k < adjustmentArray.length; k++) {
							var matchingAdjustment = adjustmentArray[k];
							if(_scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Description",matchingAdjustment), description)) {
								foundMatchingAdjustment = true;								
								var amount = null;
								var amountToAdd = null;
								amount = _scModelUtils.getNumberValueFromPath("AdjustmentAmount",matchingAdjustment);
								amountToAdd = _scModelUtils.getNumberValueFromPath("AdjustmentAmount", lineAdjustment);
								amount = amount + amountToAdd;
								_scModelUtils.setNumberValueAtModelPath("AdjustmentAmount", amount, matchingAdjustment);
								break;
							}
						}
						if(!foundMatchingAdjustment) {
							_scModelUtils.setStringValueAtModelPath("Description", description, totalAdjustment);
							_scModelUtils.setStringValueAtModelPath("AdjustmentAmount", _scModelUtils.getStringValueFromPath("AdjustmentAmount", lineAdjustment), totalAdjustment);
							adjustmentArray.push(totalAdjustment);
						}
					}
				}
			}
		}
		var headerAdjustments = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", orderModel);
		if (!_scBaseUtils.isVoid(headerAdjustments)) {
			for (var i = 0; i < headerAdjustments.length; i++) {
				var headerAdjustment = null;
				headerAdjustment = headerAdjustments[i];
				var description = null;
				description = _scModelUtils.getStringValueFromPath("ChargeNameDescription", headerAdjustment);
				for (var k = 0; k < adjustmentArray.length; k++) {
					var matchingAdjustment = adjustmentArray[k];
					if(_scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Description",matchingAdjustment), description)) {
						foundMatchingAdjustment = true;								
						var amount = null;
						var amountToAdd = null;
						amount = _scModelUtils.getNumberValueFromPath("AdjustmentAmount",matchingAdjustment);
						amountToAdd = _scModelUtils.getNumberValueFromPath("ChargeAmount", headerAdjustment);
						amount = amount + amountToAdd;
						_scModelUtils.setNumberValueAtModelPath("AdjustmentAmount", amount, matchingAdjustment);
						break;
					}
				}
				if(!foundMatchingAdjustment) {
					_scModelUtils.setStringValueAtModelPath("Description", description, totalAdjustment);
					_scModelUtils.setStringValueAtModelPath("AdjustmentAmount", _scModelUtils.getStringValueFromPath("AdjustmentAmount", headerAdjustment), totalAdjustment);
					adjustmentArray.push(totalAdjustment);
				}
			}
		}
		_scModelUtils.addListToModelPath("Order.TotalAdjustments.TotalAdjustment", adjustmentArray, orderModel);
		_scScreenUtils.setModel(this, "getCompleteOrderDetails_output", orderModel, null);
	}
});
});

