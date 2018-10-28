scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/return/create/fulfillmentSummary/ReturnFSDetailsExtnUI"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnFSDetailsExtnUI
){ 
	return _dojodeclare("extn.return.create.fulfillmentSummary.ReturnFSDetailsExtn", [_extnReturnFSDetailsExtnUI],{
	showOrHideCustomerKeepGroup: function(modelOutput){
    		var customerKeepGroup = _scModelUtils.getModelObjectFromPath("Order.CustomerKeepGroup",modelOutput);
    		if(customerKeepGroup != null){
    			var totalLinesForCustomerKeep = customerKeepGroup.TotalLineList;
    			if(totalLinesForCustomerKeep != null && totalLinesForCustomerKeep > 0){
    				_scWidgetUtils.hideWidget(this,"pnlCustomerKeepLinesContainer");
    			}else{
	    			_scWidgetUtils.hideWidget(this,"pnlCustomerKeepLinesContainer");
	    		}
    		}
    		else{
    			_scWidgetUtils.hideWidget(this,"pnlCustomerKeepLinesContainer");
    		}
    	}
});
});

