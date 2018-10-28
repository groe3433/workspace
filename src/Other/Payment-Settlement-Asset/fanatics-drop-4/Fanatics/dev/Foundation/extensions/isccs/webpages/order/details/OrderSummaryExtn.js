scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/details/OrderSummaryExtnUI","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/EditorUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!dojo/date/locale","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!extn/utils/CustomUtils","scbase/loader!isccs/utils/UIUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOrderSummaryExtnUI, _scScreenUtils, _scEditorUtils, _scModelUtils, _dLocale, _scBaseUtils, _scWidgetUtils, _customUtils, _isccsUIUtils
){ 
	return _dojodeclare("extn.order.details.OrderSummaryExtn", [_extnOrderSummaryExtnUI],{
	// This method will display the customer info on Customer panel
	getCustomerInfo : function (event, bEvent, ctrl, args) {
		var editor = _scEditorUtils.getCurrentEditor();
		var businessCardScreen = _scScreenUtils.getChildScreen(editor, "pnlCustomerContext");
		var custInfoModel = _scScreenUtils.getModel(businessCardScreen, "CustomerInfo");

		var custSince = _scModelUtils.getStringValueFromPath("Customer.ExtnDataElement.CustomerSince", custInfoModel);
		var fancashBal = _scModelUtils.getStringValueFromPath("Customer.ExtnDataElement.FanCashBalance", custInfoModel);
		var accountBal = _scModelUtils.getStringValueFromPath("Customer.ExtnDataElement.AccountBalance", custInfoModel);
		var custStatus = _scModelUtils.getStringValueFromPath("Customer.ExtnDataElement.CustomerStatus", custInfoModel);
		
		var customerInput = null;
		customerInput = _scModelUtils.createNewModelObjectWithRootKey("Customer");
		_scModelUtils.setStringValueAtModelPath("Customer.ExtnDataElement.CustomerSince", custSince, customerInput);
		_scModelUtils.setStringValueAtModelPath("Customer.ExtnDataElement.FanCashBalance", fancashBal, customerInput);
		_scModelUtils.setStringValueAtModelPath("Customer.ExtnDataElement.AccountBalance", accountBal, customerInput);
		_scModelUtils.setStringValueAtModelPath("Customer.ExtnDataElement.CustomerStatus", custStatus, customerInput);

		_scScreenUtils.setModel(this, "extn_customerdetails_output", customerInput, null);
	},
	getOrderChannel : function(event, bEvent, ctrl, args){
		console.log("Came into the function");
		orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
		console.log(orderModel);
		var entryType = _scModelUtils.getStringValueFromPath("Order.EntryType", orderModel);
		var enteredBy = _scModelUtils.getStringValueFromPath("Order.EnteredBy", orderModel);
		console.log(entryType);
		console.log(enteredBy);
		var orderChannel = null;
		if(!_scBaseUtils.isVoid(entryType) && entryType.toLowerCase() == "web")
		{
			if(!_scBaseUtils.isVoid(enteredBy) && enteredBy.toLowerCase() !="system")
				orderChannel = entryType+"("+enteredBy+")";
			else
				orderChannel = entryType;
		}
		else
			orderChannel = entryType;
		return orderChannel;
	},
	
	//This method will show formated orderdate
	getFormatOrderDate: function (event, bEvent, ctrl, args) {
		var orderModel = null;
		var orderDate = null;
		orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
		orderDate =  _scModelUtils.getDateValueFromPath("Order.OrderDate", orderModel);
		if (!_scBaseUtils.isVoid(orderDate)) {
			var val = _customUtils.getFormatedDate(orderDate);
			return val;
		}
	},
	
	//This method will form Customer Full Name
	getCustomerName : function (event, bEvent, ctrl, args) {
		var orderModel = null;
		var customerFirstName = null;
		var customerLastName = null;
		var customerFullName = null;
		orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
		customerFirstName = _scModelUtils.getStringValueFromPath("Order.CustomerFirstName", orderModel);
		customerLastName = _scModelUtils.getStringValueFromPath("Order.CustomerLastName", orderModel);
		customerFullName = customerFirstName + " " + customerLastName;
		return customerFullName;
	},

	//This method will show actual date
	getActualDate: function (event, bEvent, ctrl, args) {
	   var orderModel = null;
	   var orderDateType = null;
	   var fraudStatus = null;
	   var lstOrderDate = null;
	   var fraudUserId = null;
	   orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
	   if (!_scBaseUtils.isVoid(orderModel)) {
		   fraudStatus = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.FraudStatus", orderModel);
			if (!_scBaseUtils.isVoid(fraudStatus)) {
				if(_scBaseUtils.equals(fraudStatus, "0")) {
					lstOrderDate = _scModelUtils.getModelListFromPath("Order.OrderDates.OrderDate", orderModel);
		            if (!_scBaseUtils.isVoid(lstOrderDate)) {
			            for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(lstOrderDate)); i = _scBaseUtils.incrementNumber(i, 1)) {
				            var orderDate = null;
				            orderDate = _scModelUtils.getModelFromList(lstOrderDate, i);
				            if (!_scBaseUtils.isVoid(orderDate)) {
					            var srcDateTypeId = null;
					            srcDateTypeId = _scModelUtils.getStringValueFromPath("DateTypeId", orderDate);
					            if(_scBaseUtils.equals("FRAUD_APPROVAL_DATE", srcDateTypeId)) {
						            matchingDate = _scModelUtils.getDateValueFromPath("ActualDate", orderDate);
						            if (!_scBaseUtils.isVoid(matchingDate)) {
							           var val = _customUtils.getFormatedDate(matchingDate);
		                               return val;
						            }
					            }
				                
			                }
		                }
	                }
			    }
	        }
	   }	   
	},	
	
	//This method will return fraudUserdId
 	getFraudUserId: function (event, bEvent, ctrl, args) {
	   var orderModel = null;
	   var fraudStatus = null;
	   var fraudUserId = null;
	   orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
	   if (!_scBaseUtils.isVoid(orderModel)) {
		   fraudStatus = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.FraudStatus", orderModel);
			if (!_scBaseUtils.isVoid(fraudStatus)) {
				if(_scBaseUtils.equals(fraudStatus, "0")) {
					fraudUserId = _scModelUtils.getStringValueFromPath("Order.CustomAttributes.FraudStatusUserID", orderModel);
					console.log("fraudUserId: ", fraudUserId);

					if(!_scBaseUtils.isVoid(fraudUserId)){
						return fraudUserId;
					}
				}
            }
	   }
	},  
	
	//This method will show the hold type as tool tip
	getHoldTypeForToolTip : function (event, bEvent, ctrl, args) {
		var orderModel = null;
		var lstOrderHold = null;
		var orderHold = null;
		var holdTitle = null;
		orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
		lstOrderHold = _scModelUtils.getModelObjectFromPath("Order.OrderHoldTypes.OrderHoldType", orderModel);
		if (!_scBaseUtils.isVoid(lstOrderHold)) {
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(lstOrderHold)); i = _scBaseUtils.incrementNumber(i, 1)) {
				orderHold = _scModelUtils.getModelFromList(lstOrderHold, i);
				if (!_scBaseUtils.isVoid(orderHold)) {
					var holdType = null;
					holdType = _scModelUtils.getStringValueFromPath("HoldType", orderHold);
					if(!_scBaseUtils.isVoid(holdType)) {
						if(_scBaseUtils.isVoid(holdTitle)) {
							holdTitle = holdType;
						} else {
							holdTitle = holdTitle + ", "+ holdType;
						}
					}
				}
			}
		}
		if(!_scBaseUtils.isVoid(holdTitle)) {
			_scWidgetUtils.setTitle(this, "imgOnHold", holdTitle);
		}
	},
	
	openTotalAmount: function() {
            var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
            var cleanOrderModel = null;
            cleanOrderModel = _scScreenUtils.getModel(
            this, "popupInput_output");
            var popupParams = null;
            popupParams = _scBaseUtils.getNewBeanInstance();
            var EnterpriseCode = null;
            EnterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", cleanOrderModel);
            _scModelUtils.setStringValueAtModelPath("Order.CallingOrganizationCode", EnterpriseCode, cleanOrderModel);
            _scBaseUtils.addModelValueToBean("screenInput", cleanOrderModel, popupParams);
            var options = null;
            options = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.addStringValueToBean("style", "width:90%;", options);
            _scBaseUtils.addStringValueToBean("closeCallBackHandler", "refreshNamespaces", options);
            var screenConstructorParams = null;
            screenConstructorParams = _scBaseUtils.getNewModelInstance();
            _scModelUtils.addStringValueToModelObject("screenMode", "ReadOnlyMode", screenConstructorParams);
            _scBaseUtils.addModelValueToBean("screenConstructorParams", screenConstructorParams, popupParams);
			var popupTitle = _scScreenUtils.getFormattedString(this, "extn_Order_Total_Summary", null);
            _isccsUIUtils.openSimplePopup("isccs.order.details.OrderPricingSummary", popupTitle, this, popupParams, options);
        }
	
});
});

