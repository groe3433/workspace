scDefine([
	"scbase/loader!dojo/_base/lang",
	"scbase/loader!isccs",        
	"scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/OrderLineUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!isccs/utils/ContextUtils", "scbase/loader!sc/plat/dojo/info/ApplicationInfo"],
     function(dLang, isccs, _isccsOrderUtils, _isccsOrderLineUtils, _scBaseUtils, _isccsContextUtils, scApplicationInfo){
	var orderCustomUtils = dLang.getObject("extn.utils.orderCustomUtils", true, isccs);
				
				orderCustomUtils.getProductHTML = function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
					var str = '<div>';
					str += '<div class="productImageDesc">';
					if(_isccsContextUtils.getDisplayItemImageRule()==="Y"){
						var imgsrc = "";
						var imageLocation = _isccsOrderLineUtils.getOrderLineItemImageLocation(colConfig.bindingData,gridRowJSON,gridReference,gridRowRecord);
						var imageID = _isccsOrderLineUtils.getOrderLineItemImageId(colConfig.bindingData,gridRowJSON,gridReference,gridRowRecord);
						if(_scBaseUtils.isVoid(imageLocation) || _scBaseUtils.isVoid(imageID)){
							imgsrc = _isccsOrderUtils.getNoItemImage(colConfig.bindingData,gridRowJSON,gridReference,gridRowRecord);
						}else{
							if(imageID){
								if(scApplicationInfo.getShouldEncodeImageId()) {
									imageID = encodeURIComponent(imageID);
								}
							}
							imgsrc = encodeURI(imageLocation) + "/" + imageID;
						}
						
						str += '<img src="'+ imgsrc +'" class="itemImage">';
					}
					
					var desc = _isccsOrderUtils.getModifiedExtendedDisplayDesc(colConfig.bindingData,gridRowJSON,gridReference,gridRowRecord);
					str += '<span>'+ desc +' </span>';
					str += '</div>';
					
					var imageTags = "";
					var columnData = ['IsRelatedItem','GiftFlag','StopDeliveryRequestDetails.TotalNumberOfRecords','IsOpenBox',
					                  'HasReturnLines','ReshippedQty','IsPriceMatched','LinePriceInfo.IsPriceLocked','HasNotes','IsBundleParent',
					                  'IsBundleComponent','IsPickupAllowed','IsShippingAllowed'];
					
					for (var i = 0; i < columnData.length; i++) {
						imageTags += _isccsOrderLineUtils.getImageTag(colConfig.bindingData,gridRowJSON,gridReference,gridRowRecord,columnData[i]);
					}
					
					if(!_scBaseUtils.isVoid(imageTags)){
						str += '<div class="productIndicatorIcons">';
						str += imageTags;
						str += '</div>';
					}
					
					str += '</div>';
					return str;
				};
				
		return orderCustomUtils;
				
	});			
				