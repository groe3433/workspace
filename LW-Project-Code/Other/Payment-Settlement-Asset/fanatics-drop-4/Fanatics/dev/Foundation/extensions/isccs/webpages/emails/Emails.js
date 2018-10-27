scDefine([
		"dojo/text!./templates/Emails.html",	"scbase/loader!extn/emails/EmailsUI",
		"scbase/loader!dojo/_base/declare",	"scbase/loader!dojo/_base/kernel", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",	"scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils","scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!dojo/dom-class", "scbase/loader!dojo/dom"
	],
	function (
		templateText, _extnReviseShipDateUI, _dojodeclare, _dojokernel, _scScreenUtils, _scBaseUtils, _scScreen, _isccsWidgetUtils, _scEventUtils, _scModelUtils, _isccsUIUtils, _isccsBaseTemplateUtils, _isccsModelUtils, _scWidgetUtils, _scEditorUtils, _scGridxUtils, _isccsUIUtils, _scPaginationUtils, _isccsOrderUtils, DDomClass, DDom) {
	return _dojodeclare("extn.emails.Emails", [_extnReviseShipDateUI], {
		handleMashupOutput : function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
				console.log("mashupRefId: "+mashupRefId);
			if (_scBaseUtils.equals(mashupRefId,"extn_getEXTNFanEmail")) {
				var EXTNFanEmailList = null;
				EXTNFanEmailList = _scModelUtils.getModelListFromPath("EXTNFanEmailList.EXTNFanEmail", modelOutput);
				var rowCount = 0;
				rowCount = _scBaseUtils.getAttributeCount(EXTNFanEmailList);
			
				if(!_scBaseUtils.equals(rowCount,0)){
					this.changeEXTNFanEmailcall(mashupInput,modelOutput);
				}else{
					email_type =  _scModelUtils.getStringValueFromPath("EXTNFanEmail.EmailType", mashupInput);
					apiInput = {};
					_scModelUtils.setStringValueAtModelPath("Order.OrderNo", _scModelUtils.getStringValueFromPath("EXTNFanEmail.OrderNo", mashupInput), apiInput);
					_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("EXTNFanEmail.OrderHeaderKey", mashupInput), apiInput);
					_isccsUIUtils.callApi(this, apiInput, "extn_email_getOrderDetails", null);
					//this.createEXTNFanEmailcall(mashupInput, modelOutput);
				}
				_scScreenUtils.clearScreen(this, null);
			}else if(_scBaseUtils.equals(mashupRefId,"extn_email_getOrderDetails")){
				console.log(email_type);
				this.createEXTNFanEmailcall(mashupInput, modelOutput,email_type);
			}else if(_scBaseUtils.equals(mashupRefId,"extn_changeEXTNFanEmail")){
				_scScreenUtils.clearScreen(this, null);
			}
		},
		
		model2xml : function(o){
			if (typeof o == 'object' && o.constructor == Object && len(o) == 1) {
					for (var a in o) {
						return toXML(a, o[a]);
					}
				} else {
			 
				}
				function len(o) {
					var n = 0;
					for (var a in o) {
						n++;
					}
					return n;
				}
				
				function toXML(tag, o) {
					var doc = '<' + tag +' ' ;
					for (var b in o) {
						if(typeof o[b] !== 'object'){
							doc += attr(b, o[b]);
						}
					}
					if (len(o) === 0) {
						doc += '/>';
						return doc;
					} else {
						doc += '>';
					}
					console.log(doc);
					for (var b in o) {
						if(o[b].constructor == Object && typeof o[b] == 'object'){
							doc += toXML(b, o[b]);
						}
					}
					for (var b in o) {
						if (o[b].constructor == Array) {
							for (var i = 0; i < o[b].length; i++) {
								doc += toXML(b, o[b][i]);
							}
						}
					}
					doc += '</' + tag + '>';
					return doc;
				}
				function attr(tag, o){
					if (o.constructor != Object) {
						return tag+"="+"\""+o+"\" ";
					}
				}

		},
		createEXTNFanEmailcall : function(mashupInput,modelOutput,email_type){
			console.log("Entered into createEXTNFanEmailcall");
			
			var targetModel = _scScreenUtils.getTargetModel(this, "extn_TargetReasonCode", null);
			
			var IsOrderConfirmationEmailChecked = _scModelUtils.getStringValueFromPath("IsOrderConfirmationEmailChecked", targetModel);
			var IsShipmentConfirmationEmailChecked = _scModelUtils.getStringValueFromPath("IsShipmentConfirmationEmailChecked", targetModel);
			var EmailEntered = _scModelUtils.getStringValueFromPath("EmailEntered", targetModel);
			
			apiInput = {};
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("Order.OrderNo", mashupInput), apiInput);
            _scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", mashupInput), apiInput);
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrganizationCode", _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", modelOutput), apiInput);
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailStatus", "Initial", apiInput);
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", email_type, apiInput);
			
			var emailModel = {};
			
			
			if(_scBaseUtils.equals(email_type,"Create Order Success")){
				var orderconfirmationEmailID = _scModelUtils.getStringValueFromPath("OrderconfirmationEmailID", targetModel);	
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", orderconfirmationEmailID, apiInput);
				_scModelUtils.setStringValueAtModelPath("Email.EmailID", _scModelUtils.getStringValueFromPath("OrderconfirmationEmailID", targetModel), emailModel);
				_scModelUtils.setStringValueAtModelPath("Email.EventDescription","OrderCreation", emailModel);
				_scModelUtils.setStringValueAtModelPath("Email.EventId","OrderCreate", emailModel);
			}else if(_scBaseUtils.equals(email_type,"Confirm Shipment Success")){
				var shipmentconfirmationEmailID = _scModelUtils.getStringValueFromPath("ShipmentconfirmationEmailID", targetModel);	
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", shipmentconfirmationEmailID, apiInput);
				_scModelUtils.setStringValueAtModelPath("Email.EmailID", _scModelUtils.getStringValueFromPath("OrderconfirmationEmailID", targetModel), emailModel);
				_scModelUtils.setStringValueAtModelPath("Email.EmailID",EmailEntered, emailModel);
				_scModelUtils.setStringValueAtModelPath("Email.EventDescription","ShipConfirmation", emailModel);
				_scModelUtils.setStringValueAtModelPath("Email.EventId","ShipConf", emailModel);
			}
			_scModelUtils.setStringValueAtModelPath("Email.Order",_scModelUtils.getStringValueFromPath("Order", modelOutput), emailModel);
			console.log(emailModel);
			var emailString = this.model2xml(emailModel);
			console.log(emailString);	
			
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.MsgXml", ""+emailString, apiInput);
			
			console.log(apiInput);
			_isccsUIUtils.callApi(this, apiInput, "extn_createEXTNFanEmail", null); 
			
		},
		changeEXTNFanEmailcall : function (mashupInput,modelOutput){
			console.log("Came inside changeEXTNFanEmailcall");
			console.log(mashupInput);
			console.log(modelOutput);
			var EXTNFanEmailList = null;
			EXTNFanEmailList = _scModelUtils.getModelListFromPath("EXTNFanEmailList.EXTNFanEmail", modelOutput);
			var rowCount = 0;
			rowCount = _scBaseUtils.getAttributeCount(EXTNFanEmailList);
			for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
				apiInput = {};
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("EXTNFanEmailList.EXTNFanEmail."+i+".OrderNo", modelOutput), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("EXTNFanEmailList.EXTNFanEmail."+i+".OrderHeaderKey", modelOutput), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailStatus", "Initial", apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailKey", _scModelUtils.getStringValueFromPath("EXTNFanEmailList.EXTNFanEmail."+i+".EmailKey", modelOutput), apiInput);
				var email_Type=_scModelUtils.getStringValueFromPath("EXTNFanEmailList.EXTNFanEmail."+i+".EmailType", modelOutput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", email_Type, apiInput);
				targetModel = _scScreenUtils.getTargetModel(this, "extn_TargetReasonCode", null);
				if(_scBaseUtils.equals(email_Type,"Create Order Success")){
					var orderconfirmationEmailID = _scModelUtils.getStringValueFromPath("OrderconfirmationEmailID", targetModel);	
					_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", orderconfirmationEmailID, apiInput);
				}else if(_scBaseUtils.equals(email_Type,"Confirm Shipment Success")){
					var shipmentconfirmationEmailID = _scModelUtils.getStringValueFromPath("ShipmentconfirmationEmailID", targetModel);	
					_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", shipmentconfirmationEmailID, apiInput);
				}
				console.log(apiInput);
				_isccsUIUtils.callApi(this, apiInput, "extn_changeEXTNFanEmail", null);
			}
			
		},
		handleMashupCompletion : function (
			mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
			_isccsBaseTemplateUtils.handleMashupCompletion(
				mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
		},
        updateEditorHeader: function(
        event, bEvent, ctrl, args) {
            _isccsBaseTemplateUtils.updateTitle(
            this, "Resend Emails", null);
            _scScreenUtils.focusFirstEditableWidget(
            this);
            return true;
        },
		onGridALLSelectDeselect: function(event, bEvent, ctrl, args){
			this.individualSelection = _scBaseUtils.getNewArrayInstance();
			var isChecked = "Y";
			selectedRowsList = _scBaseUtils.getListFromBean("selectedRows", args);
			if (_scBaseUtils.isVoid(selectedRowsList)) {
				selectedRowsList = _scBaseUtils.getListFromBean("deselectedRows", args);
				this.overallSelection = _scBaseUtils.getNewArrayInstance();
			}else{
				rowCount = _scBaseUtils.getAttributeCount(selectedRowsList);
				for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
						selectedRowData = _scModelUtils.getModelFromList(selectedRowsList, i);
						_scModelUtils.addStringValueToModelObject("isChecked", isChecked, selectedRowData);
				} 
				this.overallSelection = selectedRowsList;
			}
		},
		onRowSelect: function(event, bEvent, ctrl, args){
			isChecked = "Y";
			selectedRowObj = _scBaseUtils.getModelValueFromBean("selectedRow", args);
			
			if (_scBaseUtils.isVoid(selectedRowObj)) {
                selectedRowObj = _scBaseUtils.getModelValueFromBean("deselectedRow", args);
                isChecked = "N";
            }
			
			if(!_scBaseUtils.isVoid(selectedRowObj) && _scBaseUtils.isVoid(this.overallSelection)){
				rowCount = _scBaseUtils.getAttributeCount(this.individualSelection);
				sorderHeaderKey = _scBaseUtils.getAttributeValue("OrderHeaderKey", false, selectedRowObj);
				for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
					selectedRowData = _scModelUtils.getModelFromList(this.individualSelection, i);
					orderHeaderKey = _scBaseUtils.getAttributeValue("OrderHeaderKey", false, selectedRowData);
					if (_scBaseUtils.equals(orderHeaderKey, sorderHeaderKey)) {
						_scModelUtils.addStringValueToModelObject("isChecked", isChecked, selectedRowData);
						break;
					}else if(_scBaseUtils.equals(i, rowCount-1)){
						_scModelUtils.addStringValueToModelObject("isChecked", isChecked, selectedRowObj);
						_scBaseUtils.appendToArray(this.individualSelection, selectedRowObj);
					}
				}
				if(_scBaseUtils.equals(rowCount, 0)){
					_scModelUtils.addStringValueToModelObject("isChecked", isChecked, selectedRowObj);
					_scBaseUtils.appendToArray(this.individualSelection, selectedRowObj);
				}
			}else{
				rowCount = _scBaseUtils.getAttributeCount(this.overallSelection);
				this.individualSelection = _scBaseUtils.getNewArrayInstance();
				orderHeaderKey = _scBaseUtils.getAttributeValue("OrderHeaderKey", false, selectedRowObj);
				for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
					selectedRowData = _scModelUtils.getModelFromList(this.overallSelection, i);
					sorderHeaderKey = _scModelUtils.getStringValueFromPath("rowData.OrderHeaderKey", selectedRowData);
					selectedRowObj = _scBaseUtils.getModelValueFromBean("rowData", selectedRowData);
					if (!_scBaseUtils.equals(orderHeaderKey, sorderHeaderKey)) {
						_scModelUtils.addStringValueToModelObject("isChecked", "Y", selectedRowObj);
						_scBaseUtils.appendToArray(this.individualSelection, selectedRowObj);
					}
				}
				this.overallSelection = _scBaseUtils.getNewArrayInstance();
			}
		},
		updateEmails : function(event, bEvent, ctrl, args){
			orderModel = _scScreenUtils.getInitialInputData(this);
			targetModel = _scScreenUtils.getTargetModel(this, "extn_TargetReasonCode", null);
			
			console.log(args);
			console.log(targetModel);
			console.log(this.individualSelection);
			console.log(this.overallSelection);
			selectedRowObj = _scBaseUtils.getModelValueFromBean("Order", targetModel);
			
 			console.log(selectedRowObj);
			if(!_scBaseUtils.isVoid(this.overallSelection)){
				rowCount1 = _scBaseUtils.getAttributeCount(this.overallSelection);
				for (var j = 0;_scBaseUtils.lessThan(j, rowCount);j = _scBaseUtils.incrementNumber(j, 1)) {
					selectedRowData1 = _scModelUtils.getModelFromList(this.overallSelection, j);
					console.log(selectedRowData1);
					isChecked = _scBaseUtils.getAttributeValue("isChecked", false, selectedRowData1);
					if (_scBaseUtils.equals(isChecked, "Y")) {
						sorderHeaderKey = _scModelUtils.getStringValueFromPath("rowData.OrderHeaderKey", selectedRowData1);
						console.log(sorderHeaderKey);
						rowCount = _scBaseUtils.getAttributeCount(selectedRowObj);
						for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
							selectedRowData = _scModelUtils.getModelFromList(selectedRowObj, i);
							console.log(selectedRowData);
							orderHeaderKey = _scModelUtils.getStringValueFromPath("OrderHeaderKey", selectedRowData);
							emailid = _scModelUtils.getStringValueFromPath("CustomElement.EmailID", selectedRowData);
							emailKey = _scModelUtils.getStringValueFromPath("CustomElement.EmailKey", selectedRowData);
							if(_scBaseUtils.equals(sorderHeaderKey, orderHeaderKey)){
								apiInput = {};
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", orderHeaderKey, apiInput);
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailStatus", "Initial", apiInput);	
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", emailid, apiInput);
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailKey", emailKey, apiInput);
								console.log(apiInput);
								_isccsUIUtils.callApi(this, apiInput, "extn_changeEXTNFanEmail", null);
							}
						}
					}
				}
			}else{
				rowCount = _scBaseUtils.getAttributeCount(this.individualSelection);
				for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
					selectedRowData = _scModelUtils.getModelFromList(this.individualSelection, i);
					orderHeaderKey = _scBaseUtils.getAttributeValue("OrderHeaderKey", false, selectedRowData);
					isChecked = _scBaseUtils.getAttributeValue("isChecked", false, selectedRowData);
					console.log(orderHeaderKey);
					console.log(isChecked);
					if (_scBaseUtils.equals(isChecked, "Y")) {
						rowCount1 = _scBaseUtils.getAttributeCount(selectedRowObj);
						for (var j = 0;_scBaseUtils.lessThan(j, rowCount1);j = _scBaseUtils.incrementNumber(j, 1)) {
							selectedRowData1 = _scModelUtils.getModelFromList(selectedRowObj, j);
							console.log(selectedRowData1);
							sorderHeaderKey = _scModelUtils.getStringValueFromPath("OrderHeaderKey", selectedRowData1);
							emailid = _scModelUtils.getStringValueFromPath("CustomElement.EmailID", selectedRowData1);
							emailKey = _scModelUtils.getStringValueFromPath("CustomElement.EmailKey", selectedRowData1);
							if(_scBaseUtils.equals(sorderHeaderKey, orderHeaderKey)){
								console.log(orderHeaderKey+" "+emailid);
								apiInput = {};
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", orderHeaderKey, apiInput);
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailStatus", "Initial", apiInput);	
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailID", emailid, apiInput);
								_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailKey", emailKey, apiInput);
								_isccsUIUtils.callApi(this, apiInput, "extn_changeEXTNFanEmail", null);
							}
						}
					}
				}
			}
			
			
		
			var IsOrderConfirmationEmailChecked = _scModelUtils.getStringValueFromPath("IsOrderConfirmationEmailChecked", targetModel);
			var IsShipmentConfirmationEmailChecked = _scModelUtils.getStringValueFromPath("IsShipmentConfirmationEmailChecked", targetModel);
			var shipmentconfirmationEmailID = _scModelUtils.getStringValueFromPath("ShipmentconfirmationEmailID", targetModel);	
			console.log("ShipmentconfirmationEmailID: "+shipmentconfirmationEmailID);
			var orderconfirmationEmailID = _scModelUtils.getStringValueFromPath("OrderconfirmationEmailID", targetModel);	
			console.log("OrderconfirmationEmailID: "+orderconfirmationEmailID);
			
			if(_scBaseUtils.equals(IsShipmentConfirmationEmailChecked,"Y") && _scBaseUtils.equals(IsOrderConfirmationEmailChecked,"Y")){
				apiInput = {};
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("Order.OrderNo", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", "Confirm Shipment Success", apiInput);
				_isccsUIUtils.callApi(this, apiInput, "extn_getEXTNFanEmail", null);
				apiInput = {};
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("Order.OrderNo", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", "Create Order Success", apiInput);
				_isccsUIUtils.callApi(this, apiInput, "extn_getEXTNFanEmail", null);
			} else if(_scBaseUtils.equals(IsShipmentConfirmationEmailChecked,"Y")){
				apiInput = {};
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("Order.OrderNo", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", "Confirm Shipment Success", apiInput);
				_isccsUIUtils.callApi(this, apiInput, "extn_getEXTNFanEmail", null);
			}else if(_scBaseUtils.equals(IsOrderConfirmationEmailChecked,"Y")){
				apiInput = {};
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderNo", _scModelUtils.getStringValueFromPath("Order.OrderNo", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), apiInput);
				_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.EmailType", "Create Order Success", apiInput);
				_isccsUIUtils.callApi(this, apiInput, "extn_getEXTNFanEmail", null);
			}
			
			
		},
        initializeScreen : function(event, bEvent, ctrl, args) {
			this.individualSelection = _scBaseUtils.getNewArrayInstance();
			this.overallSelection = _scBaseUtils.getNewArrayInstance();
        var options = null;
		var gridID = "OLST_listGrid";
		var orderModel = null;
		var inputData = null;
		orderModel = _scScreenUtils.getInitialInputData(this);
		inputData = _scBaseUtils.getNewModelInstance();
		_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), inputData);
		console.log("OrderHeaderKey: "+ _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel));
		console.log("DisplayStatus: "+ _scModelUtils.getStringValueFromPath("Order.DisplayStatus", orderModel));
		console.log("MaxOrderStatusDesc"+_scModelUtils.getStringValueFromPath("Order.MaxOrderStatusDesc", orderModel));
		console.log("MinOrderStatus"+_scModelUtils.getStringValueFromPath("Order.MinOrderStatus", orderModel));
		extndStatus = _scModelUtils.getStringValueFromPath("Order.Status", orderModel);
			
		var getshipmentFanEmail_output = null;
		getshipmentFanEmail_output = _scScreenUtils.getModel(this, "extn_getCompleteEXTNFanEmail_shipment_output");
		var orderNo = null;
		orderNo=_scModelUtils.getStringValueFromPath("EXTNFanEmail.OrderNo", getshipmentFanEmail_output);
		if(!_scBaseUtils.isVoid(orderNo)){
			console.log("true successful");
		}else{
			console.log("false successful");
			var screenModel =  _scScreenUtils.getModel(this, "chkGiftRecipient1");
			_scWidgetUtils.hideWidget(this, "chkGiftRecipient1");
			
			screenModel =  _scScreenUtils.getModel(this, "txt_customer_email");
			_scWidgetUtils.hideWidget(this, "txt_customer_email");
		}
		
		getChargeTransactionList_output = _scScreenUtils.getModel(this, "extn_getChargeTransactionList_output");
		console.log(getChargeTransactionList_output);
		
		ChargeTransactionDetailLst = _scModelUtils.getModelListFromPath("ChargeTransactionDetails.ChargeTransactionDetail", getChargeTransactionList_output);
		console.log(ChargeTransactionDetailLst);
		
		var rowCount = 0;
		rowCount = _scBaseUtils.getAttributeCount(ChargeTransactionDetailLst);
		apiInput = _scBaseUtils.getNewModelInstance();
		for (var i = 0;_scBaseUtils.lessThan(i, rowCount);i = _scBaseUtils.incrementNumber(i, 1)) {
			var orderHeaderKey = null;
			var ChargeTransactionDetailEle = ChargeTransactionDetailLst[i];
			orderHeaderKey=_scModelUtils.getStringValueFromPath("TransferToOhKey", ChargeTransactionDetailEle);
			console.log(orderHeaderKey);
			
			_scModelUtils.setStringValueAtModelPath("EXTNFanEmail.OrderHeaderKey", orderHeaderKey, apiInput);
			console.log(apiInput);
		}
		
		options = {};
		_scBaseUtils.setAttributeValue("screen", this, options);
		_scBaseUtils.setAttributeValue("mashupRefId", "extn_chargetransaction_getEXTNFanEmail", options);
		console.log(options);
		_isccsUIUtils.startPagination(this, gridID, apiInput, options);
	},
        pagingLoad: function(
        event, bEvent, ctrl, args) {
        },
		isGridRowDisabled: function(
        rowData, screen) {
		}
	});
});
