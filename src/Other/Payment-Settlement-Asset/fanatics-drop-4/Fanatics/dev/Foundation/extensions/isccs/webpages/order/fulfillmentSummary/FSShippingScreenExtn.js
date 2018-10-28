scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/fulfillmentSummary/FSShippingScreenExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",	"scbase/loader!sc/plat/dojo/utils/EventUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnFSShippingScreenExtnUI, _scBaseUtils, _scGridxUtils, _scScreenUtils, _scEventUtils, _scModelUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FSShippingScreenExtn", [_extnFSShippingScreenExtnUI],{
	//this method will validate if all the line is selected
	validateLineLevelLOS: function(event, bEvent, ctrl, args) {
		var selectedSourceRecordsList = null;
		selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(this, "OLST_listGrid");
		var lengthOfSourceRecord = null;
		lengthOfSourceRecord = _scBaseUtils.getAttributeCount(selectedSourceRecordsList);
		var totalRecord = _scGridxUtils.getRowCount(this, "OLST_listGrid");
		if (!_scBaseUtils.equals(lengthOfSourceRecord, totalRecord)) {
			var warningString = null;
			warningString = _scScreenUtils.getString(this, "WARNING_selecteAllItem");
			var textObj = null;
			textObj = {};
			var textOK = null;
			textOK = _scScreenUtils.getString(this, "OK");
			textObj["OK"] = textOK;
			_scScreenUtils.showErrorMessageBox(this, warningString, "waringCallback", textObj, null);
			_scEventUtils.stopEvent(bEvent);
			return false;
		}
	},
	
	updateCarrierService: function(newModelData) {
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var selectedTargetRecordsList = null;
            selectedTargetRecordsList = _scGridxUtils.getSelectedTargetRecordsWithRowIndex(
            this, "OLST_listGrid");
            var listOfMergedModel = null;
            listOfMergedModel = this.mergeModel(
            selectedSourceRecordsList, selectedTargetRecordsList);
            var length = null;
            length = _scBaseUtils.getAttributeCount(
            listOfMergedModel);
            for (var z = 0; z < length; z = z + 1) {
                var beanItem = null;
                beanItem = listOfMergedModel[
                z];
                var lineIndex = null;
                lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                var srcRowData = null;
                srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(
                this, "OLST_listGrid", lineIndex);
                var rowJson = null;
                rowJson = _scGridxUtils.getRowJsonFromRowIndex(
                this, "OLST_listGrid", lineIndex);
                var orderLineKey = null;
                orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", rowJson);
                this.handleBundleItems(
                rowJson, newModelData, "CARRIERSERVICE");
                _scGridxUtils.updateRecordToGridUsingUId(
                this, "OLST_listGrid", srcRowData, newModelData, true, true);
            }
            _scGridxUtils.deselectAllRowsInGridUsingUId(
            this, "OLST_listGrid");
            _scGridxUtils.refreshGridxLayout(
            this, "OLST_listGrid");
            var temp = null;
            temp = {};
            _scBaseUtils.setAttributeValue("argumentList.model", newModelData, temp);
            _scEventUtils.fireEventToParent(
            this, "SaveOrder", temp);
    },
	
	formInputForLOS: function(
        orderLines, orderModel, groupData) {
            var apiInput = null;
            apiInput = {};
            var input = null;
            input = {};
            _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), input);
            _scModelUtils.addModelToModelPath("Order.PersonInfoShipTo", _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", orderModel), input);
			_scModelUtils.addModelToModelPath("Order.CustomAttributes", _scModelUtils.getModelObjectFromPath("Order.CustomAttributes", orderModel), input);
            var length = null;
            length = _scBaseUtils.getAttributeCount(
            orderLines);
            var orderLinesArray = null;
            orderLinesArray = [];
            for (
            var i = 0;
            i < length;
            i = i + 1) {
                var orderLine = null;
                orderLine = {};
                var orderLineElem = null;
                orderLineElem = orderLines[
                i];
                _scModelUtils.setStringValueAtModelPath("ItemID", _scModelUtils.getStringValueFromPath("Item.ItemID", orderLineElem), orderLine);
                _scModelUtils.setStringValueAtModelPath("IsParcelShippingAllowed", _scModelUtils.getStringValueFromPath("ItemDetails.PrimaryInformation.IsParcelShippingAllowed", orderLineElem), orderLine);
                _scModelUtils.setStringValueAtModelPath("OrderLineKey", _scModelUtils.getStringValueFromPath("OrderLineKey", orderLineElem), orderLine);
                _scModelUtils.setStringValueAtModelPath("EarliestShipDate", _scModelUtils.getStringValueFromPath("EarliestShipDate", orderLineElem), orderLine);
                if (!(
                _scBaseUtils.isVoid(
                _scModelUtils.getModelObjectFromKey("PersonInfoShipTo", orderLineElem)))) {
                    _scModelUtils.addModelToModelPath("PersonInfoShipTo", _scModelUtils.getModelObjectFromKey("PersonInfoShipTo", orderLineElem), orderLine);
                }
                orderLinesArray.push(
                orderLine);
            }
            _scModelUtils.addListToModelPath("Order.OrderLines.OrderLine", orderLinesArray, input);
            return input;
        },
	
});
});

