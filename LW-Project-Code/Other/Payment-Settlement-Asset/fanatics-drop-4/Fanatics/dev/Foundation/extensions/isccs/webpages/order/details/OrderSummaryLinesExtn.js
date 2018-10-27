scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderSummaryLinesExtnUI","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!extn/utils/CustomUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderSummaryLinesExtnUI, _scModelUtils, _scBaseUtils, _customUtils
){ 
	return _dojodeclare("extn.order.details.OrderSummaryLinesExtn", [_extnOrderSummaryLinesExtnUI],{
	// custom code here
	
	//This method returns the shipnode from shipment
	getShipNodeFromShipment : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
		
		var shipmentLine = null;
		shipmentLine = _scModelUtils.getStringValueFromPath("ShipmentLines.ShipmentLine", gridRowJSON);
		if (!_scBaseUtils.isVoid(shipmentLine)) {
			var str = null;
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(shipmentLine)); i = _scBaseUtils.incrementNumber(i, 1)) {
				var shipment  = null;
				shipment= _scModelUtils.getModelFromList(shipmentLine, i);
				if (!_scBaseUtils.isVoid(shipment)){					
										
					var shipNode = null;
					shipNode = _scModelUtils.getStringValueFromPath("Shipment.0.ShipNode", shipment);
					if (!_scBaseUtils.isVoid(shipNode)){
					  if (_scBaseUtils.isVoid(str)){
					  str = shipNode;
					  }
					  else{
					  str += shipNode;
					  }
					  str += "\n";
					}					  
				}
			}
			
			return str;
		}
	},
	
	//This method returns the actualshipmentdate from shipment
	getActualShipDateFromShipment : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
		
		var shipmentLine = null;
		shipmentLine = _scModelUtils.getStringValueFromPath("ShipmentLines.ShipmentLine", gridRowJSON);
		if (!_scBaseUtils.isVoid(shipmentLine)) {
			var str = null;
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(shipmentLine)); i = _scBaseUtils.incrementNumber(i, 1)) {
				var shipment  = null;
				shipment= _scModelUtils.getModelFromList(shipmentLine, i);
				if (!_scBaseUtils.isVoid(shipment)){
					var shipDate = null;
					shipDate = _scModelUtils.getDateValueFromPath("Shipment.0.ActualShipmentDate", shipment);
					if (!_scBaseUtils.isVoid(shipDate)){
					  var val = _customUtils.getFormatedDate(shipDate);
					  if (_scBaseUtils.isVoid(str)){
					  str = val;
					  }
					  else{
					  str += val;
					  }
					  str += "\n";
					}
				}
			}
			return str;
		}
	},
	
	//This method will display revised ship date and reason
	getRevisedShipDateAndReason : function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
		var lstOrderDate = null;
		lstOrderDate = _scModelUtils.getModelListFromPath("OrderDates.OrderDate", gridRowJSON);
		if (!_scBaseUtils.isVoid(lstOrderDate)) {
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(lstOrderDate)); i = _scBaseUtils.incrementNumber(i, 1)) {
				var orderDate = null;
				orderDate = _scModelUtils.getModelFromList(lstOrderDate, i);
				if (!_scBaseUtils.isVoid(orderDate)) {
					var srcDateTypeId = null;
					srcDateTypeId = _scModelUtils.getStringValueFromPath("DateTypeId", orderDate);
					if(_scBaseUtils.equals("REVISED_SHIP_DATE", srcDateTypeId)) {
						matchingDate = _scModelUtils.getDateValueFromPath("ExpectedDate", orderDate);
						if (!_scBaseUtils.isVoid(matchingDate)) {
							var str = null;
							var str = _customUtils.getFormatedDate(matchingDate);
							
							var reasonCode = _scModelUtils.getStringValueFromPath("CustomAttributes.RevisedShipDateReason", gridRowJSON);
							if (!_scBaseUtils.isVoid(reasonCode)){
								str += "\n";
								str += reasonCode;
							}
							return str;
							
						}
					}
				}
			}
		}
	}	
	
});
});

