
scDefine(["dojo/text!./templates/ReturnListScreenExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!gridx/Grid", "scbase/loader!gridx/modules/ColumnWidth", "scbase/loader!gridx/modules/HLayout", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/CustomerUtils", "scbase/loader!isccs/utils/OrderLineUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/ReturnUtils", "scbase/loader!isccs/utils/SearchUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/GridxDataBinder", "scbase/loader!sc/plat/dojo/plugins/gridx/Pagination", "scbase/loader!sc/plat/dojo/plugins/gridx/PaginationBar", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/Screen"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _gridxGrid, _gridxColumnWidth, _gridxHLayout, _idxContentPane, _isccsBaseTemplateUtils, _isccsCustomerUtils, _isccsOrderLineUtils, _isccsOrderUtils, _isccsReturnUtils, _isccsSearchUtils, _isccsUIUtils, _scplat, _scGridxDataBinder, _scPagination, _scPaginationBar, _scBaseUtils, _scEventUtils, _scGridxUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _scScreen) {
return _dojodeclare("extn.return.search.ReturnListScreenExtnUI",
				[], {
			templateString: templateText
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

]
},
	handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (_scBaseUtils.equals(mashupRefId, "getReturnList")) {
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "getOrderList_output", modelOutput, null);
                }
                this.LST_handleSingleRecord();
                if (!(_scBaseUtils.isEmptyArray(_scModelUtils.getModelListFromPath("OrderList.Order", modelOutput)))) {
                    _scGridxUtils.selectRowUsingUId(this, "listReturnGrid", 0);
                    _isccsSearchUtils.updateParentScreen(this, "collapseSearchResults", modelOutput);
                }
            }else if(_scBaseUtils.equals(mashupRefId, "extn_return_getOrderList")) {
				var args = _scScreenUtils.getModel(this, "args");
				inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
				console.log(_scModelUtils.getModelListFromPath("OrderList", modelOutput));
				var orderList = _scModelUtils.getModelListFromPath("OrderList.Order", modelOutput);
				var orderElement = orderList[0];
				var orderNo = _scModelUtils.getStringValueFromPath("OrderNo", orderElement);
				var orderHeaderkKey = _scModelUtils.getStringValueFromPath("OrderHeaderKey", orderElement);
				_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.CustomerOrderNo", "", inputData);
				_scBaseUtils.removeBlankAttributes(inputData);
				_scModelUtils.setStringValueAtModelPath("Order.OrderLine.DerivedFrom.OrderNo", orderNo, inputData);
				_scModelUtils.setStringValueAtModelPath("Order.OrderLine.DerivedFrom.OrderHeaderKey", orderHeaderkKey, inputData);
				var gridID = "listReturnGrid";
				var options = null;
				options = _scBaseUtils.getNewBeanInstance();
				_scBaseUtils.setAttributeValue("screen", this, options);
				_scBaseUtils.setAttributeValue("mashupRefId", "getReturnList", options);
				_scScreenUtils.setModel(this, "LST_listAPIInput", inputData, null);
				_isccsUIUtils.startPagination(this, gridID, inputData, options);
			}else if(_scBaseUtils.equals(mashupRefId, "extn_return_getPersonInfoList")){
				console.log("Came into the last mashup call handleMashupOutput");
				console.log(modelOutput);
				var infoList = _scModelUtils.getModelListFromPath("PersonInfoList.PersonInfo", modelOutput);
				var args = _scScreenUtils.getModel(this, "args");
				inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
				_scModelUtils.setStringValueAtModelPath("Order.CustomerFirstName", "", inputData);
				_scBaseUtils.removeBlankAttributes(inputData);
				var length = _scBaseUtils.getAttributeCount(infoList);	

				model = _scBaseUtils.getNewArrayInstance();
				for (var i = 0; i < length; i = i + 1){
					var infoElement = infoList[i];
					var personInfoKey = _scModelUtils.getStringValueFromPath("PersonInfoKey", infoElement);
					var expmodel = _scBaseUtils.getNewModelInstance();
					_scModelUtils.setStringValueAtModelPath("Name", "BillToKey", expmodel);
					_scModelUtils.setStringValueAtModelPath("QryType", "EQ", expmodel);
					_scModelUtils.setStringValueAtModelPath("Value", personInfoKey, expmodel);
					_scBaseUtils.appendToArray(model, expmodel);
				}
				
				var modelObject = _scBaseUtils.getNewModelInstance();
				_scModelUtils.setStringValueAtModelPath("Or.Exp", model, modelObject);

				ordermodel = _scBaseUtils.getNewModelInstance();
				_scModelUtils.setStringValueAtModelPath("Order.ComplexQuery.Or.Exp", model, ordermodel);
				_scModelUtils.setStringValueAtModelPath("Order.ComplexQuery.Operator", "AND", ordermodel);
				console.log(ordermodel);
				
				var gridID = "listReturnGrid";
				var options = null;
				options = _scBaseUtils.getNewBeanInstance();
				_scBaseUtils.setAttributeValue("screen", this, options);
				_scBaseUtils.setAttributeValue("mashupRefId", "getReturnList", options);
				console.log(inputData);
				_isccsUIUtils.startPagination(this, gridID, ordermodel, options);
				
				
			}
        },
	LST_executeApi: function(event, bEvent, ctrl, args) {
            var scr = null;
            var inputData = null;
            scr = _scEventUtils.getScreenFromEventArguments(args);
			console.log(scr);
            inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
            _scEventUtils.fireEventInsideScreen(this, "addExtraHandlers", null, args);
            _scScreenUtils.setModel(this, "LST_listAPIInput", inputData, null);
			_scScreenUtils.setModel(this, "args", args, null);
            var gridID = "listReturnGrid";
            var hasGridPagination = false;
            hasGridPagination = _isccsUIUtils.hasPaginationBinding(scr, gridID);
            if (!(_scBaseUtils.equals(true, hasGridPagination))) {
                _isccsUIUtils.callApi(scr, inputData, "getReturnList", null);
            } else {
				var customerOrderNo = _scModelUtils.getModelListFromPath("Order.CustomAttributes.CustomerOrderNo", inputData);
				var firstName = _scModelUtils.getModelListFromPath("Order.CustomerFirstName", inputData);
				var lastName = _scModelUtils.getModelListFromPath("Order.CustomerLastName", inputData);
				console.log(firstName);
				if(_scBaseUtils.isVoid(customerOrderNo)&& _scBaseUtils.isVoid(firstName) && _scBaseUtils.isVoid(lastName)){
					var options = null;
					options = _scBaseUtils.getNewBeanInstance();
					_scBaseUtils.setAttributeValue("screen", this, options);
					_scBaseUtils.setAttributeValue("mashupRefId", "getReturnList", options);
					_isccsUIUtils.startPagination(this, gridID, inputData, options);
				}else if(!_scBaseUtils.isVoid(customerOrderNo)&& _scBaseUtils.isVoid(firstName) && _scBaseUtils.isVoid(lastName)){
					var apiInput = {};
					_scModelUtils.setStringValueAtModelPath("Order.DocumentType", "0001", apiInput);
					_scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", _scModelUtils.getModelListFromPath("Order.EnterpriseCode", inputData), apiInput);
					_scModelUtils.setStringValueAtModelPath("Order.CustomAttributes.CustomerOrderNo", _scModelUtils.getModelListFromPath("Order.CustomAttributes.CustomerOrderNo", inputData), apiInput);
					_isccsUIUtils.callApi(scr, apiInput, "extn_return_getOrderList", null);
				}else if(!_scBaseUtils.isVoid(firstName) || !_scBaseUtils.isVoid(lastName)){
					console.log("Came into firstName or lastName");
					console.log(firstName);
					console.log(lastName);
					var apiInput = {};
					if(!_scBaseUtils.isVoid(firstName))
						_scModelUtils.setStringValueAtModelPath("PersonInfo.FirstName", firstName, apiInput);
					if(!_scBaseUtils.isVoid(lastName))
						_scModelUtils.setStringValueAtModelPath("PersonInfo.LastName", lastName, apiInput);
					console.log(apiInput);
					_isccsUIUtils.callApi(scr, apiInput, "extn_return_getPersonInfoList", null);
				}
            }
        }

});
});


