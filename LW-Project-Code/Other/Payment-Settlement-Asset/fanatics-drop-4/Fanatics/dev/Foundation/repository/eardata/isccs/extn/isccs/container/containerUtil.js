/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 , 2015 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/lang",
          "scbase/loader!dojo/_base/connect",
          "scbase/loader!dojo/window",
          "scbase/loader!dojo/fx",
          "scbase/loader!dojo/_base/fx",
          "scbase/loader!dojo/query",
		  "scbase/loader!dojo/aspect",
          "scbase/loader!dojo/dom",
          "scbase/loader!dojo/dom-style",
          "scbase/loader!dojo/dom-class",
          "scbase/loader!dojo/dom-attr",
          "scbase/loader!dojo/dom-geometry",
          "scbase/loader!dijit/registry",
          "scbase/loader!sc/plat/dojo/info/ApplicationInfo",
          "scbase/loader!sc/plat/dojo/utils/BundleUtils",
          "scbase/loader!sc/plat/dojo/utils/ControllerUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/Userprefs",
          "scbase/loader!isccs/container/MenuPopupClass",
          "scbase/loader!isccs",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils",
          "scbase/loader!isccs/utils/BaseTemplateUtils",
          "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!isccs/utils/ItemUtils",
		  "scbase/loader!sc/plat/dojo/utils/WizardUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/events/GlobalEventManager",
		  "scbase/loader!sc/plat/dojo/utils/Util",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/CacheUtils"
          ], 
    function(dLang,
    		dConnect,
    		dWindow,
    		dFx,
    		d_BaseFx,
    		dQuery,
			dAspect,
    		dDom,
    		dDomStyle,
    		dDomClass,
    		dDomAttr,
    		dDomGeometry,
    		dijitRegistry,
    		scApplicationInfo,
    		scBundleUtils,
    		scControllerUtils,
    		scBaseUtils,
			scUserprefs,
    		isccsMenuPopupClass,
    		scIsccs,
    		isccsUIUtils,
    		scEventUtils,
    		isccsBaseTemplateUtils,
    		scEditorUtils,
    		isccsItemUtils,
			scWizardUtils,
			scScreenUtils,
			scWidgetUtils,
			scGlobalEventManager,
			scUtil,
			scModelUtils,
			scCacheUtils){
	
	
	var isccsContainerUtil = dLang.getObject("container.containerUtil", true, scIsccs);
	
	var gvMenuShowing=null;
	var gvHoldClass="";
	var gvSelected=null;
	isccsContainerUtil.showNavMenu = function (pMenu, pMenuItem, e, pPlacement){
		  
		if (pMenu==gvMenuShowing){
			e.cancelBubble=true;
			//if menu is already open then close the menu
			isccsContainerUtil.hideNavMenu();
			return;
		}
		if (gvSelected!==null){
			gvSelected.className=gvHoldClass;
		}
		isccsMenuPopupClass.showMenu(pMenu, e, {focus: this, placement: pPlacement});
		gvHoldClass=pMenuItem.className;
		pMenuItem.className += " lotusHover";
		gvMenuShowing=pMenu;
		gvSelected=pMenuItem;
		e.cancelBubble=true;
	};
	
	isccsContainerUtil.hideNavMenu = function (){
		if (gvMenuShowing===null) {
			return;
		}
		isccsMenuPopupClass.hideMenu(gvMenuShowing);
		gvSelected.className=gvHoldClass;
		gvMenuShowing=null;
		gvSelected=null;
		gvHoldClass="";
	};
/* Menu Item ENDS */


/* Container page methods STARTS */
		isccsContainerUtil.init = function(contextPath) {
			scEventUtils.addGlobalListener("handleError","handleMashupError",isccsBaseTemplateUtils);
			
			scApplicationInfo.setApplicationContext(contextPath);
			isccsContainerUtil.loadDefaultIdentifiers();
//			dijitRegistry.byId("topNavSearch").set("placeHolder",scBundleUtils.getString("search"));			
			var screenContainerInstance = scApplicationInfo.getSCScreenContainerInstance();
			

			if(screenContainerInstance === null) {
				console.log("Got screenContainerInstance as null... Exiting...");
				return;
			}
			
			screenContainerInstance.setEditorClass('isccs.editors.ISCCSEditor');

			scControllerUtils.openScreenInEditor("isccs.home.Home", {}, null, {"editorConfig": {"closable":false,"iconClass":"dijitNoIcon"}}, {}, "isccs.editors.HomeEditor");
			
			isccsContainerUtil.rightToolTabsHandle();
//				dDom.byId("searchScope").value= "order";
			scCacheUtils.setMashupCacheLimit(100);
		};
		
		isccsContainerUtil.handleScratchPad = function (){
			if(dojo.hasClass("right-social-content", "scratch-pad")){
				dojo.removeClass("right-social-content", "scratch-pad");
				dojo.addClass("right-social-content", "scratch-pad-open");
				var textArea = dojo.byId("scratchpadArea");
				textArea.focus();
			}
			else if(dojo.hasClass("right-social-content", "scratch-pad-open")){
				dojo.removeClass("right-social-content", "scratch-pad-open");
				dojo.addClass("right-social-content", "scratch-pad");
			}

		};
		isccsContainerUtil.rightToolTabsHandle = function (){
			isccsContainerUtil.rightToolTab1("one");
			};


			isccsContainerUtil.rightToolTab1 = function(rightToolIdPostfix){				
				var a = dDom.byId("rightTabButton"+rightToolIdPostfix);
				if(a){
				//containers
 				var parentNodeList = a.parentNode,
				toolHover = dQuery('div.right-tab-tool-'+rightToolIdPostfix,parentNodeList)[0]; 
				 
				dDomStyle.set(toolHover,'opacity',0);
				//show/hide
				dConnect.connect(a,'onmouseclick',function() {
  					if (a.classList.contains("righttabbutton-enabled")){
  						isccsContainerUtil.slideClose(toolHover);
 					}else {
 						isccsContainerUtil.slideOpen(toolHover,a);
 					}
				});
				dConnect.connect(a,'onclick',function() {
					if (a.classList.contains("righttabbutton-enabled")){
						isccsContainerUtil.slideClose(toolHover);
 					}else {
 						isccsContainerUtil.slideOpen(toolHover,a);
 					}
				});
 
			  } 

			};
			
			isccsContainerUtil.slideOpen = function (toolHover,a) {
				dDomStyle.set(toolHover,'right','0px');
				dDomStyle.set(toolHover,'display','block');
				dDomStyle.set(toolHover,'position','fixed');
				var parentNodeList = a.parentNode;
				 
				dQuery('a.righttabbutton',parentNodeList).removeClass("righttabbutton-enabled");
				dQuery('a.righttabbutton',parentNodeList).removeClass("roundedCorner");
				dDomClass.addClass(a, "righttabbutton-enabled",true); 
 				 
				var vr = dWindow.getBox();
				var sideTabBox = dDomGeometry.marginBox(a);
				var tp = dDomGeometry.marginBox(a).t;
				if( a.parentNode){
					tp = tp + dDomGeometry.marginBox( a.parentNode).t;
				}

				var slideArgs = {
				  node: toolHover,
				  position:"fixed",
				  top: tp.toString(),
				  left: (vr.w - (dDomGeometry.marginBox(toolHover).w + sideTabBox.w) ).toString(),
				  unit: "px",
				  beforeBegin: function(){
						dDomStyle.set(toolHover, {
							right: "0px",
							position:"fixed",
							top: tp.toString()
						});
					}
				};
		 
				dFx.combine([
					d_BaseFx.fadeIn({ node: toolHover }),
					dFx.slideTo(slideArgs)
				]).play();
				 dDomStyle.set(toolHover,'display','block');
				dDomStyle.set(toolHover,'position','fixed');
			 };

		 isccsContainerUtil.slideClose = function(toolHover) {
			var id = toolHover.id;
			var postfix = id.substring((id.lastIndexOf('-')+1),id.length);
			var a = dQuery("#rightTabButton"+postfix);
			if(a){
				dQuery("#rightTabButton"+postfix).removeClass("righttabbutton-enabled");
				dQuery("#rightTabButton"+postfix).addClass("righttabbutton",true); 
				dQuery("#rightTabButton"+postfix).addClass("roundedCorner",true); 
				
			}
			
			if("block" == dDomStyle.set(toolHover,'display')){
				var vr = dWindow.getBox();
			 var slideArgs = {
				  node: toolHover,
				  top: (dDomGeometry.marginBox(toolHover).t).toString(),
				  left: (vr.w).toString(),
				  unit: "px"
				  
				};
				dFx.combine([
					d_BaseFx.fadeOut({ node: toolHover }),
					dFx.slideTo(slideArgs)
				]).play();
				dDomStyle.set(toolHover,'right','0px');
				 
			} 
		 };

 
		 /*Commenting this out because I can't type 'x' in any field right now
		  * shortcut.add("Alt+Shift+X",function() {
			 dojo.publish("shownotespanel"); 
		 });*/
		
//		dojo.subscribe("shownotespanel", function () { 
//               //	dojo.forEach(dQuery("a.righttabbutton-one"),function(a) {
//				var a = dDom.byId("rightTabButtonone")
//				if(a){ 
//				//containers
//				var parentNodeList = a.parentNode,
//				  toolHover = dQuery('div.right-tab-tool-one',parentNodeList)[0]; 
//				  isccsContainerUtil.slideOpen(toolHover,a) 
//			    } 
//				 
//        }); 
		
		isccsContainerUtil.updateSearchScopes = function(){
			dDomAttr.attr(dDom.byId('customer'),"innerHTML",scBundleUtils.getString("Customer"));
			dDomAttr.attr(dDom.byId('order'),"innerHTML",scBundleUtils.getString("Order"));
			dDomAttr.attr(dDom.byId('item'),"innerHTML",scBundleUtils.getString("Product"));			
		};
		
		isccsContainerUtil.updateSearchPlaceHolder = function (linkName){
			var topNavSearchWidget = dijitRegistry.byId("topNavSearch");
			var searchDefaultPlcHolder = scBundleUtils.getString("Search");
			topNavSearchWidget.set("placeHolder",searchDefaultPlcHolder + ' (' +scBundleUtils.getString(linkName) + ')');
			dDom.byId("searchScope").value= linkName;
			isccsContainerUtil.hideNavMenu();
		};
		
		isccsContainerUtil.showAboutBox = function (){
			var scr = null;
			var HomeEditor = scEditorUtils.getSpecificOpenEditors("isccs.editors.HomeEditor");
			var len = scBaseUtils.getAttributeCount(HomeEditor);
            for (var index = 0;index <len ;index ++ ) {
                var editor = scBaseUtils.getArrayBeanItemByIndex(HomeEditor,index);
              
                var editorInstance = scBaseUtils.getAttributeValue("editorInstance", false, editor);
                 scr = scEditorUtils.getScreenInstance(editorInstance);
            }

			 var popupParams = scBaseUtils.getNewBeanInstance();
             var dialogParams = scBaseUtils.getNewBeanInstance();
			 isccsUIUtils.openSimplePopup("isccs.about.AboutPopup", scScreenUtils.getString(scr,"productName"), scr, popupParams, dialogParams);
	
		};

		
		isccsContainerUtil.showEnterprieSelection = function (itemId){
			
           var currentEditor = scEditorUtils.getCurrentEditor();
           var input = scModelUtils.createNewModelObjectWithRootKey("Organization");
          
           var mashupContext = scControllerUtils.getMashupContext(currentEditor);
		       scBaseUtils.setAttributeValue("Value", itemId, mashupContext);
               isccsUIUtils.callApi(currentEditor, input, "productBrowsing_getOrganizationList",mashupContext);
           
		};
		
		isccsContainerUtil.flushCache = function (){
			
			var currentEditor = scEditorUtils.getCurrentEditor();
	  		isccsUIUtils.removeFromLocalStorage("isccs.screenName");
	        isccsUIUtils.removeFromLocalStorage("isccs.input");
	        isccsUIUtils.removeFromLocalStorage("isccs.editor");
	        var inputModel = null;
	        inputModel = scModelUtils.createNewModelObjectWithRootKey("Dummy");
	        isccsUIUtils.callApi(
	        currentEditor, inputModel, "flushCache", null);       
		};

		

		isccsContainerUtil.closeAboutBox = function (id){
			dijitRegistry.byId('aboutInfo').hide();
		};
		
		isccsContainerUtil.showMaskOrLoading = function(task){
			if(scUtil.isUITaskInProgress()){
				if(!scWidgetUtils.isMaskShown()){
					scWidgetUtils.showMask();
				}
			}
		};
		
		isccsContainerUtil.hideMaskOrLoading = function(task){
			if(!scUtil.isUITaskInProgress()){
				scWidgetUtils.hideMask();
			var wizard = scWidgetUtils.getCurrentRenderedWizard();
			if(wizard){
				scWidgetUtils.hideWizardLoadingPanel(wizard);
			}
		}
		};
		
		
		scGlobalEventManager.addGlobalListener("UITaskStarted", "showMaskOrLoading",isccsContainerUtil);
		scGlobalEventManager.addGlobalListener("UITaskEnded", "hideMaskOrLoading",isccsContainerUtil);
		
		isccsContainerUtil.loadOnlineHelpWindow = function (){
			var url = sc.plat.dojo.info.ApplicationInfo.getActiveHelpURL();
			var win = window.open("https://fanassist4.fanatics.corp/Account/ChangePassword","Product_name","height=500,width=500,left=200,top=200,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes");
			win.focus();
		};

		isccsContainerUtil.loadAboutWindow = function (){
			var url = about.jsp;
			var win = window.open(url,"Product_name","height=500,width=500,left=200,top=200,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes");
			win.focus();
		};
		
		
		isccsContainerUtil.openAdditionalInfoBox = function(){
			dijitRegistry.byId('additionalInfo').show();			
		};
		
		isccsContainerUtil.globalSearch = function(){
			var scope = dDom.byId("searchScope").value;
			var searchValue = dDom.byId("topNavSearch").value;
			if(scope == "Order"){
				if(!isccsContainerUtil.isEmpty(searchValue)){
					var data = {};
					data.Order={};
					data.Order.OrderNo=dLang.trim(searchValue);					
//					scControllerUtils.openScreenInEditor("isccs.order.search.OrderSearchWizard",data,null);
					isccsUIUtils.openWizardInEditor("isccs.order.search.OrderSearchWizard", data,"isccs.editors.OrderSearchEditor",null);
					dijitRegistry.byId("borderContainerCenter").startup();
				}
			}
		};

		isccsContainerUtil.loadDefaultIdentifiers = function() {
			var country = "US";
		//	if(!country){
		//		country = "US";
		//	}
			var addressIdentifiers = {"Identifiers": {"default": country, "Identifier":[]}};
			scBaseUtils.registerIdentifiers("AddressDisplay", addressIdentifiers);
			scBaseUtils.registerIdentifiers("AddressCapture", addressIdentifiers);
			scBaseUtils.registerIdentifiers("AddressFilter", addressIdentifiers);
		};

		isccsContainerUtil.isEmpty = function(value){
			if (!value || dLang.trim(value).length === 0) {
				return true;
			}
			return false;
		};
		
		isccsContainerUtil.showUserPreferences = function (){
			var scr = null;
			var HomeEditor = scEditorUtils.getSpecificOpenEditors("isccs.editors.HomeEditor");
			var len = scBaseUtils.getAttributeCount(HomeEditor);
            for (var index = 0;index <len ;index ++ ) {
                var editor = scBaseUtils.getArrayBeanItemByIndex(HomeEditor,index);
              
                var editorInstance = scBaseUtils.getAttributeValue("editorInstance", false, editor);
                 scr = scEditorUtils.getScreenInstance(editorInstance);
            }

			 var popupParams = scBaseUtils.getNewBeanInstance();
             var dialogParams = scBaseUtils.getNewBeanInstance();
			 isccsUIUtils.openSimplePopup("isccs.userPreferences.UserPreferencePopup", "UserPreference", scr, popupParams, dialogParams);
	
		};



		return isccsContainerUtil;
});
