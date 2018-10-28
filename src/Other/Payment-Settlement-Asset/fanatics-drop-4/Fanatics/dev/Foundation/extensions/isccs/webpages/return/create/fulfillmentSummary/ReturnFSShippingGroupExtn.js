
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/return/create/fulfillmentSummary/ReturnFSShippingGroupExtnUI"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnFSShippingGroupExtnUI
){ 
	return _dojodeclare("extn.return.create.fulfillmentSummary.ReturnFSShippingGroupExtn", [_extnReturnFSShippingGroupExtnUI],{
	// custom code here
	
	toggleButtonsBasedOnData: function(orderLine){
		var isCustomerKeepAllowed = _scModelUtils.getStringValueFromPath("IsCustomerKeepAllowed",orderLine);
        	var IsSchedulePickupAllowed = _scModelUtils.getStringValueFromPath("IsSchedulePickupAllowed",orderLine);
        	var modificationList = _scModelUtils.getModelListFromPath("Modifications.Modification", orderLine);
		var sIsDeliveryMethodChangeAllowed = _isccsUIUtils.getModificationTypeValueFromList(modificationList, "CHANGE_DELIVERY_METHOD");
	}
});
});

