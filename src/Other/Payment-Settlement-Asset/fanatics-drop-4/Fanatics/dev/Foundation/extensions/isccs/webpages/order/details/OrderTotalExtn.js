
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderTotalExtnUI", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils" , "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderTotalExtnUI, _scScreenUtils, _scModelUtils, _isccsUIUtils, _scEventUtils, _scBaseUtils
){ 
	return _dojodeclare("extn.order.details.OrderTotalExtn", [_extnOrderTotalExtnUI],{
	
	getHeaderCharges : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		apiInput=_scScreenUtils.getModel(this, "apiInput", null);
		console.log("getHeaderCharges");
		console.log("ShippingCharges"+_scModelUtils.getStringValueFromPath("Order.OverallTotals.ShippingCharges", apiInput));
		return _scModelUtils.getStringValueFromPath("Order.OverallTotals.ShippingCharges", apiInput);
	},
	
	getFancash : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var fancash = 0.0;
		apiInput=_scScreenUtils.getModel(this, "apiInput", null);
		return _scModelUtils.getStringValueFromPath("Order.OverallTotals.Fancash", apiInput);
	},
	getCharges : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var charges = 0.0;
		apiInput=_scScreenUtils.getModel(this, "apiInput", null);
		return _scModelUtils.getStringValueFromPath("Order.OverallTotals.Charges", apiInput);
	},
	getDiscounts : function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var discount = 0.0;
		apiInput=_scScreenUtils.getModel(this, "apiInput", null);
		return _scModelUtils.getStringValueFromPath("Order.OverallTotals.Discounts", apiInput);
	},
	getAppeasements :function(event, bEvent, ctrl, args,dataValue, screen, widget, namespace, modelObject, options){
		var appeasements = 0.0;
		apiInput=_scScreenUtils.getModel(this, "apiInput", null);
		return _scModelUtils.getStringValueFromPath("Order.OverallTotals.Appeasements", apiInput);
	},
	initScreen: function(event, bEvent, ctrl, args,value, screen, widget, namespace, modelObject, options) {
		this.isScreeninitialized = true;
		console.log(args);
		var orderHeaderKey = null;
		var documentType = null;
		orderHeaderKey = _scModelUtils.getStringValueFromPath("screen.scEditorInput.Order.OrderHeaderKey", args);
		documentType = _scModelUtils.getStringValueFromPath("screen.scEditorInput.Order.DocumentType", args);
		if(_scBaseUtils.isVoid(orderHeaderKey)){
			orderHeaderKey = _scModelUtils.getStringValueFromPath("screen._initialInputData.Order.OrderHeaderKey", args);
			documentType = _scModelUtils.getStringValueFromPath("screen._initialInputData.Order.DocumentType", args);
		}
		console.log(orderHeaderKey);
		
		if(!_scBaseUtils.isVoid(orderHeaderKey)){
			apiInput = {};
			_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, apiInput);
			_isccsUIUtils.callApi(this, apiInput, "extn_OrderTotal_getCompleteOrderDetails", null);
		}
	},
	handleMashupOutput : function (mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		console.log("handleMashupOutput");
		console.log(modelOutput);
		var shippingCharges=0.0;
		var charges=0.0;
		var fancash=0.0;
		var discounts = 0.0;
		var appeasements=0.0;
		var lineDiscount = 0.0;
		var headerDiscount = 0.0;
		var tax = 0.0;
		var grandTotal = 0.0;
		
		if(!_scBaseUtils.isVoid(modelOutput)){
			var lstAdjustments = null;
			lstAdjustments = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", modelOutput);
			if (!_scBaseUtils.isVoid(lstAdjustments)) {
				var length = null;
				length = _scBaseUtils.getAttributeCount(lstAdjustments);
				for (var index = 0;	index < length;	index = index + 1) {
					var adjustment = null;
					adjustment = lstAdjustments[index];
					HeaderChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", adjustment);
					HeaderChargeName = _scModelUtils.getStringValueFromPath("ChargeName", adjustment);
					IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", adjustment);
					if(_scBaseUtils.equals(IsDiscount, "Y") && !_scBaseUtils.equals(HeaderChargeName, "Appeasements") && !_scBaseUtils.equals(HeaderChargeCategory, "MRLFee")) {
						headerDiscount = parseFloat(headerDiscount) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", adjustment));
					}
					if(_scBaseUtils.equals(HeaderChargeName, "Appeasements")){
						appeasements = parseFloat(appeasements) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", adjustment));
					}
					if(_scBaseUtils.equals(HeaderChargeCategory, "Shipping")) {
						shippingCharges = parseFloat(shippingCharges) + parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", adjustment));
					}
					if(!_scBaseUtils.equals(HeaderChargeCategory, "Shipping") || _scBaseUtils.equals(HeaderChargeCategory, "MRLFee")){
						charges = parseFloat(charges) - parseFloat(_scModelUtils.getStringValueFromPath("ChargeAmount", adjustment));
					}
				}
			}
		}
				
		if(!_scBaseUtils.isVoid(modelOutput)){
			var lstorderLines = null;
			lstorderLines= _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", modelOutput);
			if(!_scBaseUtils.isVoid(lstorderLines)){
				var lengthol = _scBaseUtils.getAttributeCount(lstorderLines);
				for (var index = 0;	index < lengthol;	index = index + 1) {
					var orderLine = null;
					orderLine = lstorderLines[index];
					var lstAdjustments = null;
					lstAdjustments = _scModelUtils.getModelListFromPath("LineCharges.LineCharge", orderLine);
					if (!_scBaseUtils.isVoid(lstAdjustments)) {
						var length = null;
						length = _scBaseUtils.getAttributeCount(lstAdjustments);
						for (var index1 = 0;	index1 < length;	index1 = index1 + 1) {
							var adjustment = null;
							adjustment = lstAdjustments[index1];
							ChargeCategory = _scModelUtils.getStringValueFromPath("ChargeCategory", adjustment);
							ChargeName = _scModelUtils.getStringValueFromPath("ChargeName", adjustment);
							IsDiscount = _scModelUtils.getStringValueFromPath("IsDiscount", adjustment);
							if(_scBaseUtils.equals(ChargeCategory, "Fancash")) {
								fancash = parseFloat(fancash) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", adjustment));
							}
							if(_scBaseUtils.equals(ChargeCategory, "Appeasements")) {
								appeasements = parseFloat(appeasements) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", adjustment));
							}
							if(_scBaseUtils.equals(IsDiscount, "Y") && !_scBaseUtils.equals(ChargeName, "Fancash") && !_scBaseUtils.equals(ChargeName, "Appeasements")) {
								lineDiscount = parseFloat(lineDiscount) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", adjustment));
							}
							if(_scBaseUtils.equals(IsDiscount, "N")){
								charges = parseFloat(charges) + parseFloat(_scModelUtils.getStringValueFromPath("ChargePerLine", adjustment));
							}
						}
					}
				}
			}
		}
		
		if(!_scBaseUtils.isVoid(modelOutput)){
			tax = _scModelUtils.getNumberValueFromPath("Order.OverallTotals.GrandTax", modelOutput);
			grandTotal = _scModelUtils.getNumberValueFromPath("Order.OverallTotals.GrandTotal", modelOutput);
		}
		
		if(fancash<0)
			fancash = fancash*-1;
		if(shippingCharges<0)
			shippingCharges = shippingCharges*-1;
		discount = lineDiscount+headerDiscount;
		if(discount<0)
			discount = discount*-1;
		if(appeasements<0)
			appeasements = appeasements*-1;
		
		apiInput={};
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.GrandTax", "$"+tax.toFixed(2), apiInput);
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.GrandTotal", "$"+grandTotal.toFixed(2), apiInput);
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.ShippingCharges", "$"+shippingCharges.toFixed(2), apiInput);
		
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.Fancash", "($"+fancash.toFixed(2)+")", apiInput);
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.Discounts", "($"+discount.toFixed(2)+")", apiInput);
		_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.Appeasements", "($"+appeasements.toFixed(2)+")", apiInput);
		
		
		if(charges<0){
			charges = charges*-1;
			_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.Charges", "($"+charges.toFixed(2)+")", apiInput);
		}else
			_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.Charges", "$"+charges.toFixed(2), apiInput);

		_scScreenUtils.setModel(this, "apiInput", apiInput, null);
		_scEventUtils.fireEventToParent(this, "reloadScreen", null);
	},
	loadAdjustments :function(value, screen, widget, namespace, modelObject) {
		var orderPricingSummaryScreen = null;
		orderPricingSummaryScreen = _isccsUIUtils.getParentScreen(screen, true);
		var orderModel = null;
		orderModel = _scScreenUtils.getModel(orderPricingSummaryScreen, "getCompleteOrderDetails_output");
		_scScreenUtils.setModel(this, "getCompleteOrderDetails_output", orderModel, null);
		console.log("loadAdjustments");
		console.log(orderModel);
		var returnValue = null;
		returnValue = {};
		_scModelUtils.addStringValueToModelObject("HeaderChargeFlag", "Y", value);
		if(_scModelUtils.getNumberValueFromPath("ChargeAmount", value) > 0) {
			return null;
		}
		this.generateCommonData(value, returnValue);
		return returnValue;
	},
	generateCommonData: function(value, returnValue) {
		returnValue["repeatingscreenID"] = "isccs.order.details.OrderPricingSummaryAdjustments";
		var innerNamespaceBean = null;
		innerNamespaceBean = {};
		var namespaceDataList = null;
		namespaceDataList = [];
		innerNamespaceBean["dataObj"] = value;
		namespaceDataList.push(
		innerNamespaceBean);
		var namespaceDataBean = null;
		namespaceDataBean = {};
		namespaceDataBean["namespaceData"] = namespaceDataList;
		var constructorData = null;
		constructorData = {};
		constructorData["namespaceData"] = namespaceDataBean;
		returnValue["constructorArguments"] = constructorData;
		return returnValue;
	}
});
});

