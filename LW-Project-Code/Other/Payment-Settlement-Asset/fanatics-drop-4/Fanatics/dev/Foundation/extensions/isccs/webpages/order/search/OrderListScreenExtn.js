scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/order/search/OrderListScreenExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/SearchUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"],
	function (
		_dojodeclare, _extnOrderListScreenExtnUI, _scBaseUtils,_scWidgetUtils, _isccsUIUtils, _isccsSearchUtils, _scModelUtils, _scScreenUtils) {
	return _dojodeclare("extn.order.search.OrderListScreenExtn", [_extnOrderListScreenExtnUI], {
        onPagingload: function(
        event, bEvent, ctrl, args) {
            var modelOutput = null;
            modelOutput = _scBaseUtils.getValueFromPath("result.Page.Output", args);
			var totalRecords = _scModelUtils.getStringValueFromPath("OrderList.TotalNumberOfRecords", modelOutput);
			var newTitle = null;
			newTitle = "Search Results - " + "Total # of Records: " + totalRecords;
			var tilePanel = this.ownerScreen.getWidgetByUId('SST_SearchResult');
			_scWidgetUtils.setTitle(this.ownerScreen, "SST_SearchResult", newTitle);
            _scScreenUtils.setModel(
            this, "getOrderList_output", modelOutput, null);
            if (
            _isccsUIUtils.isFunction(
            this, "LST_handleSingleRecord")) {
                this.LST_handleSingleRecord();
            }
            if (!(
            _scBaseUtils.isEmptyArray(
            _scModelUtils.getModelListFromPath("OrderList.Order", modelOutput)))) {
                _isccsSearchUtils.updateParentScreen(
                this, "collapseSearchResults", modelOutput);
            }
            if (
            _isccsUIUtils.isFunction(
            this, "handleScreenSpecificAction")) {
                this.handleScreenSpecificAction(
                null, modelOutput, null, null);
            }
        }
	});
});

