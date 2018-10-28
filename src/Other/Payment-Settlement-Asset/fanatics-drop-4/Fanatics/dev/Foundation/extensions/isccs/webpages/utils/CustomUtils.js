scDefine([
		"scbase/loader!dojo/_base/lang",
		"scbase/loader!isccs",
	"scbase/loader!dojo/date/locale",
	"scbase/loader!sc/plat/dojo/utils/BundleUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",  "scbase/loader!isccs/utils/OrderUtils"
	],
	function (dLang, isccs, _dLocale, _scBundleUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _isccsOrderUtils) {
	var CustomUtils = dLang.getObject("extn.utils.CustomUtils", true, isccs);
	
	CustomUtils.getFormatedDate = function (date) {
		var fmto = "EEEE, MMMM d, yyyy h:m:s a";
    	var val = _dLocale.format(date, {selector:'date', datePattern:fmto});
		return val;
	};
	
	CustomUtils.getHTMLForAddItemsLineNo = function (gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig) {
		var str = '<div>';
		str += '<span>'+ value +' </span>';
		str += '</div>';
		return str;
	};
	
	CustomUtils.formatDescriptionInCombo = function(screen, modelOutput){
		if(!_scBaseUtils.isVoid(modelOutput)) {
			var orderCurrency = null;
			orderCurrency = _scScreenUtils.getModel(screen.ownerScreen.ownerScreen, "OrderTotalModel");
			var carrierServiceList = _scModelUtils.getModelListFromPath("CarrierServiceList.CarrierService", modelOutput);;
			var commonCodeModel = null;
			commonCodeModel = _scScreenUtils.getModel(screen, "extn_getShippingCharge_output");
			for (var j = 0; _scBaseUtils.lessThan(j, _scBaseUtils.getAttributeCount(carrierServiceList)); j = _scBaseUtils.incrementNumber(j, 1)) {
				var carrierService = null;
				carrierService = _scModelUtils.getModelFromList(carrierServiceList, j);
				if (!_scBaseUtils.isVoid(carrierService)) {
					var carrierServiceCode = null;
					var carrierServiceDesc = null;
					carrierServiceCode = _scModelUtils.getStringValueFromPath("CarrierServiceCode", carrierService);
					carrierServiceDesc = _scModelUtils.getStringValueFromPath("CarrierServiceDesc", carrierService);
					var commonCodeList = null;
					commonCodeList = _scModelUtils.getModelListFromPath("CommonCodeList.CommonCode", commonCodeModel);
					if (!_scBaseUtils.isVoid(commonCodeList)) {
						for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(commonCodeList)); i = _scBaseUtils.incrementNumber(i, 1)) {
							var commonCode = null;
							var codeValue = null;
							var codeShortDesc = null;
							commonCode = _scModelUtils.getModelFromList(commonCodeList, i);
							codeValue = _scModelUtils.getStringValueFromPath("CodeValue", commonCode);
							if(!_scBaseUtils.isVoid(codeValue) && _scBaseUtils.equals(codeValue, carrierServiceCode)) {
								codeShortDesc = _scModelUtils.getStringValueFromPath("CodeShortDescription", commonCode);
								var inputArray = _scBaseUtils.getNewArrayInstance();
								_scBaseUtils.appendToArray(inputArray,carrierServiceDesc);
								codeShortDesc = _isccsOrderUtils.getFormattedCurrency(orderCurrency, codeShortDesc);
								_scBaseUtils.appendToArray(inputArray,codeShortDesc);
								descField = _scScreenUtils.getFormattedString(screen,"FormattedDescriptionPrice",inputArray);
								_scBaseUtils.setAttributeValue("CarrierServiceDesc", descField, carrierService);
								continue;
							}
						}
					}
				}
			}
		}
	};
	
	CustomUtils.getDateFromOrderDate = function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue, modifiedValue,gridRowRecord,colConfig){
		var matchingDate = null;
		var dateTypeId = null;
		var dateAttribute = null;
		var date = null;
		if(_scBaseUtils.equals(colConfig.field, "extn_order_date")) {
			date = _scModelUtils.getDateValueFromPath("OrderDate", gridRowJSON);
			return this.getFormatedDate(date);
		}	
		
		if(_scBaseUtils.equals("FRAUD_APPROVAL_DATE",colConfig.bindingData.sFieldBinding.path)) {
			var fraudStatus = null;
			fraudStatus = _scModelUtils.getStringValueFromPath("CustomAttributes.FraudStatus", gridRowJSON);
			if (!_scBaseUtils.isVoid(fraudStatus)) {
				if(_scBaseUtils.equals(fraudStatus, "0")) {
					dateTypeId = "FRAUD_APPROVAL_DATE";
			        dateAttribute = "ActualDate";
				}
			} 
		} else if(_scBaseUtils.equals("PROMISED_SHIP_DATE",colConfig.bindingData.sFieldBinding.path)) {
			dateTypeId = "PROMISED_SHIP_DATE";
			dateAttribute = "CommittedDate";
		}
		else if(_scBaseUtils.equals("REVISED_SHIP_DATE",colConfig.bindingData.sFieldBinding.path)) {
			dateTypeId = "REVISED_SHIP_DATE";
			dateAttribute = "ExpectedDate";	
			var RevisedShipDateReason = _scModelUtils.getStringValueFromPath("CustomAttributes.RevisedShipDateReason", gridRowJSON);
		}
		
		var lstOrderDate = null;
		lstOrderDate = _scModelUtils.getModelListFromPath("OrderDates.OrderDate", gridRowJSON);
		if (!_scBaseUtils.isVoid(lstOrderDate)) {
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(lstOrderDate)); i = _scBaseUtils.incrementNumber(i, 1)) {
				var orderDate = null;
				orderDate = _scModelUtils.getModelFromList(lstOrderDate, i);
				if (!_scBaseUtils.isVoid(orderDate)) {
					var srcDateTypeId = null;
					srcDateTypeId = _scModelUtils.getStringValueFromPath("DateTypeId", orderDate);
					if(_scBaseUtils.equals(dateTypeId, srcDateTypeId)) {
						matchingDate = _scModelUtils.getDateValueFromPath(dateAttribute, orderDate);
						if (!_scBaseUtils.isVoid(matchingDate)) {
							if(!_scBaseUtils.equals(dateTypeId,"REVISED_SHIP_DATE")){
								return this.getFormatedDate(matchingDate);
							}else{
								return this.getFormatedDate(matchingDate)+"\n( "+RevisedShipDateReason+" )";
							}
						}
					}
				}
			}
		}
		
	}
	
	
	return CustomUtils;
});
