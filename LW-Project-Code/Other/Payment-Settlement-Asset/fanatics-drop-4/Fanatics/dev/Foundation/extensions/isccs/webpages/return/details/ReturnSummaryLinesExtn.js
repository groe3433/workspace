
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/return/details/ReturnSummaryLinesExtnUI",  "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnSummaryLinesExtnUI,  _isccsOrderUtils, _isccsUIUtils, _scBaseUtils, _scGridxUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.return.details.ReturnSummaryLinesExtn", [_extnReturnSummaryLinesExtnUI],{
	// custom code here
	getDispostionCode : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig, args){
		console.log("getDispostionCode");
		var model = _scScreenUtils.getModel(this,"extn_getReceiptLineList_output");
		ReceiptLineList = _scModelUtils.getModelListFromPath("ReceiptLineList.ReceiptLine", model);
		resultNumber = _scBaseUtils.getAttributeCount(ReceiptLineList);
		for (var i = 0;_scBaseUtils.lessThan(i, resultNumber);i = _scBaseUtils.incrementNumber(i, 1)) {
			ReceiptLine = ReceiptLineList[i];
			console.log(ReceiptLine);
			var oOrderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", ReceiptLine);
			console.log(oOrderLineKey);
			if(_scBaseUtils.equals(oOrderLineKey,unformattedValue)){
				var dispostionCode = _scModelUtils.getStringValueFromPath("DispositionCode", ReceiptLine);
				return dispostionCode;
			}
		}
		return null;
	}
});
});
