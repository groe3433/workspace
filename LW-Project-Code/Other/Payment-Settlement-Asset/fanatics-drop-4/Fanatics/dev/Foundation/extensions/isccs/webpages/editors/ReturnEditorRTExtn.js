scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/editors/ReturnEditorRTExtnUI","scbase/loader!isccs/utils/RelatedTaskUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnEditorRTExtnUI, _isccsRelatedTaskUtils, _scBaseUtils, _isccsUIUtils, _scScreenUtils
){ 
	return _dojodeclare("extn.editors.ReturnEditorRTExtn", [_extnReturnEditorRTExtnUI],{
	//This method will open the wavie mrl fee popup
	openWavieMRLFeePopup : function (event, bEvent, ctrl, args) {
		var taskInput = null;
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
		var popupParams = null;
		popupParams = _scBaseUtils.getNewBeanInstance();
		var dialogParams = null;
		dialogParams = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.addModelValueToBean("screenInput", taskInput, popupParams);
		var popupTitle = _scScreenUtils.getFormattedString(this, "extn_mrl_fee_title", null);
		_isccsUIUtils.openSimplePopup("extn.mrlPopup.MRLPopup", popupTitle, this, popupParams, dialogParams);
	}
});
});

