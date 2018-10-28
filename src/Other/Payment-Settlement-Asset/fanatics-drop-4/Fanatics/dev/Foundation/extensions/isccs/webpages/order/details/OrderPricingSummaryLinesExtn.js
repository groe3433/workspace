
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderPricingSummaryLinesExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderPricingSummaryLinesExtnUI, _scBaseUtils, _scPaginationUtils, _scScreenUtils, _scModelUtils, _scGridxUtils, _scEventUtils
){ 
	return _dojodeclare("extn.order.details.OrderPricingSummaryLinesExtn", [_extnOrderPricingSummaryLinesExtnUI],{
	// custom code here
	handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineList")) {
                _scPaginationUtils.loadPageFromMashupContext(
                this, "OLST_listGrid", mashupContext, "getCompleteOrderLineList");
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineList")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "getCompleteOrderLineList_output", modelOutput, null);
                }
                var orderLine = null;
                var requiresPagination = "true";
                if (
                _scBaseUtils.equals(
                requiresPagination, "true")) {
                    orderLine = _scModelUtils.getModelListFromPath("Page.Output.OrderLineList.OrderLine", modelOutput);
                } else {
                    orderLine = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput);
                }
                if (!(
                _scBaseUtils.isVoid(
                orderLine))) {
                    _scGridxUtils.selectRowUsingUId(
                    this, "OLST_listGrid", 0);
                }
                this.customOrderListHandler(
                modelOutput);
				 var eventDefinition = null;
				eventDefinition = {};
				_scBaseUtils.setAttributeValue("argumentList.model", orderLine, eventDefinition);
				 _scEventUtils.fireEventToParent(this, "loadPricingSummaryAdj", eventDefinition);
            }
        }
});
});

