// im.js file consists of all the scripts used in Inventory Management module.
    
   	//The following function is used to display different text boxes dynamically, based on selected 'value' in a combobox. 
	function setTextBox(oCombo)  
	{
        //Hide all the input boxes.
        var obj = null;
        for (var i = 0; i < oCombo.options.length; i++) {
            obj = document.all(oCombo.options(i).value);
            setHidden(obj);
			obj.value="";
			obj.errorDescription="";
        }
        //Make the correct one visible.
        document.all(oCombo.value).style.display = "inline";
    }
    
    function setHidden(obj) {
        var cssText = obj.style.cssText;
        if (cssText)
            obj.style.display = "none";
        else
            obj.style.cssText = "display:none";
    }

	//The following function is used by Adjustment Detail inner panel to disable and clear fields based on track/infinite options. 
	function disableAndClearFields(sQtyQryType,sNewQtyText, sAPIQtyText)
	{
        document.all(sNewQtyText).errorDescription='';
        document.all(sNewQtyText).runtimeStyle.cssText='';
        document.all(sNewQtyText).value="";
        document.all(sAPIQtyText).value="";
        document.all(sQtyQryType).disabled=true;
        document.all(sNewQtyText).disabled=true;
    }

	//The following function is used by Adjustment Detail inner panel to enable fields based on track/infinite options. 
    function enableFields(sQtyQryType,sNewQtyText)
	{
        document.all(sQtyQryType).disabled=false;
        document.all(sNewQtyText).disabled=false;
    }

	//The following function is used by Adjustment Detail inner panel. 
	function blankoutTextBoxes(sNewQuantityText, sAPIQuantityText)  
	{
        document.all(sNewQuantityText).value="";
        document.all(sAPIQuantityText).value="";
    }


	//The following function is used by Adjustment Detail inner panel to pass negative quantity values to the adjustInventory api. 
    function updateAPIQty(oText, sAPIQtyText, sQueryTypeCombo)
    {
		onBlurHandler();
        var sOperation=document.all(sQueryTypeCombo).value;
        var oAPIQtyText = document.all(sAPIQtyText);
        if(oText.value != null){
            if (sOperation == "-")
                oAPIQtyText.value= "-" + oText.value;
            else    
                oAPIQtyText.value= oText.value;
        }        
    }
	
	function showDIV(val,expandAlt,collapseAlt,expandgif,collapsegif)
	{
		var tmp=document.all(val);
		var rowElement = tmp.parentElement.parentElement;
		if(tmp.style.display == "none")
		{
			rowElement.style.display="";
			showDIVTable(val,collapseAlt,expandgif);
		}
		else
		{
			rowElement.style.display="none";
			hideDIVTable(val,expandAlt,collapsegif);
		}
	}

	function showATPDiv(val,expandAlt,collapseAlt,expandgif,collapsegif)
	{
		var tmp=document.all(val);
		if(tmp.style.display == "none")
			showDIVTable(val,collapseAlt,expandgif);
		else
			hideDIVTable(val,expandAlt,collapsegif);
	}

	function showDIVTable(val,collapseAlt,expandgif)
	{
		var srcElement;
		srcElement = window.event.srcElement;
		var tmp=document.all(val);
		srcElement.src=expandgif;
		srcElement.alt=collapseAlt;
		tmp.style.display="";
	}
	function hideDIVTable(val,expandAlt,collapsegif)
	{
		var tmp =document.all(val);
		tmp.style.display = "none";
		var srcElement;
		srcElement = window.event.srcElement;
		srcElement.alt=expandAlt;
		srcElement.src=collapsegif;
	}
	
	function setHiddenValue(fieldName,sValue)
	{
		var tmp =document.all(fieldName);	
		tmp.value = sValue;
	}

function showInvAuditSearch(sViewID,sItemID,sUOM,sProductClass,sOrgCode,sShipNode)
{   
       var ItemID = document.all(sItemID).value;
       var UOM = document.all(sUOM).value;
       var PC = document.all(sProductClass).value;
       var Org = document.all(sOrgCode).value;
	   var sNode = "";
	   if(document.all(sShipNode))
		   sNode = document.all(sShipNode).value;
		
       var entity="inventoryaudit";
       var sAddnParams = "&xml:/InventoryAudit/@ItemID="+ItemID+"&xml:/InventoryAudit/@UnitOfMeasure="+UOM;
       sAddnParams = sAddnParams + "&xml:/InventoryAudit/@ProductClass="+PC+"&xml:/InventoryAudit/@OrganizationCode="+Org;
	   sAddnParams = sAddnParams + "&xml:/InventoryAudit/@ShipNode="+sNode;
       
       yfcShowListPopupWithParams(sViewID,"",'900', '500','',entity, encodeURI(sAddnParams) );
}

function showFindInvSearch(sViewID,sItemID,sUOM,sProductClass,sOrgCode)
{   
       var ItemID = document.all(sItemID).value;
       var UOM = document.all(sUOM).value;
       var PC = document.all(sProductClass).value;
       var Org = document.all(sOrgCode).value;
       var entity="availability";
       var sAddnParams = "&xml:/Promise/PromiseLines/PromiseLine/@ItemID="+ItemID+"&xml:/Promise/PromiseLines/PromiseLine/@UnitOfMeasure="+UOM;
       sAddnParams = sAddnParams + "&xml:/Promise/PromiseLines/PromiseLine/@ProductClass="+PC+"&xml:/Promise/@OrganizationCode="+Org;
       
       yfcShowSearchPopupWithParams(sViewID, "", '900', '550', '', entity, encodeURI(sAddnParams) );
}

function checkKeyPress(oEvent)
{
    if(oEvent.keyCode == 13)
    {
	yfcCheckTextField(oEvent.srcElement);
        if(validateControlValues())
            yfcChangeDetailView(getCurrentViewId());    
        return false;    
    }
}

function updatedSummedAdjustment(checkboxObject, summedQtyInputName, individualQty, adjustmentCounter, adjustmentKey)
{
    var hiddenAdjustmentKeyName = "xml:/Adjustments/ShipNode/Item/Adjustment_" + adjustmentCounter + "/@AdjustmentKey";
    var summedQtyInputObject = document.all(summedQtyInputName);
    var differenceQty = yfcGetNumber(individualQty);

    if (checkboxObject.checked) {
        // Update summed quantity (+)
        summedQtyInputObject.value = yfcGetNumber(summedQtyInputObject.value) + differenceQty;

        // Create a hidden input for the adjustment key so that it gets posted in the postPendingAdjustment API
        var checkboxParent = checkboxObject.parentNode;
        var hiddenAdjustmentKeyInput = document.createElement("<INPUT type='hidden' name='" + hiddenAdjustmentKeyName + "'/>");
        hiddenAdjustmentKeyInput.value = adjustmentKey;
        checkboxParent.appendChild(hiddenAdjustmentKeyInput);
    }
    else {
        // Update summed quantity (-)
        summedQtyInputObject.value = yfcGetNumber(summedQtyInputObject.value) - differenceQty;

        // Remove the hidden input that was created so that this adjustment key does not get posted
        
        var hiddenAdjustmentKeyObj = document.all(hiddenAdjustmentKeyName);
        var checkboxParent = checkboxObject.parentNode;
        checkboxParent.removeChild(hiddenAdjustmentKeyObj);
    }
}

function updatedAllSummedAdjustments(checkAllObject) {

    // Loop through all check boxes on the adjustment list and fire the onclick event
    var i = 1;
    var checkboxObject = document.all('chkAdjustment_' + i);
    while (checkboxObject) {

        if (checkAllObject.checked != checkboxObject.checked) {
            checkboxObject.checked = checkAllObject.checked;
            checkboxObject.fireEvent("onclick");
        }
        i++;
        checkboxObject = document.all('chkAdjustment_' + i);
    }
}

function setDistributionState(distributionControlName, disableValue) {

    var distributionCombo = document.all(distributionControlName);
    distributionCombo.disabled = disableValue;
    if (disabledValue = 'true') {
        // Blank out the selected distribution rule
        distributionCombo.selectedIndex = -1;
    }
}

function setSegmentState(segmentTypeControlName, segmentControlName, disableValue) {
    var segmentTypeCombo = document.all(segmentTypeControlName);
	var segmentInput = document.all(segmentControlName);
    segmentTypeCombo.disabled = disableValue;
	segmentInput.disabled = disableValue;
    if (disabledValue = 'true') {
        // Blank out the selected SegmentType
        segmentTypeCombo.selectedIndex = -1;
		segmentInput.value = '';
    }
}

function showDemandList(itemIDValue, UOMValue, productClassValue, orgCodeValue, demandTypeValue, shipNodeValue,fromDate,toDate,disbRuleId,considerAllNodes,demandShipDateQryType) {
	var entity = "NWCdemand";
    var addnParams = "&xml:/DemandDetails/@ItemID=" + itemIDValue + "&xml:/DemandDetails/@UnitOfMeasure=" + UOMValue;
    addnParams = addnParams + "&xml:/DemandDetails/@ProductClass=" + productClassValue + "&xml:/DemandDetails/@OrganizationCode=" + orgCodeValue;
    addnParams = addnParams + "&xml:/DemandDetails/@DemandType=" + demandTypeValue;
    addnParams = addnParams + "&xml:/DemandDetails/@ShipNode=" + shipNodeValue;
	addnParams = addnParams + "&xml:/DemandDetails/@FromDemandShipDate=" + fromDate;
	addnParams = addnParams + "&xml:/DemandDetails/@ToDemandShipDate=" + toDate;
	addnParams = addnParams + "&xml:/DemandDetails/@DistributionRuleId=" + disbRuleId;
	addnParams = addnParams + "&xml:/DemandDetails/@ConsiderAllNodes=" + considerAllNodes;
	addnParams = addnParams + "&xml:/DemandDetails/@DemandShipDateQryType=" + demandShipDateQryType;

    yfcShowListPopupWithParams('', '', '900', '500', '', entity, encodeURI(addnParams) );
}

function goToFindInv(sSearchViewID,sKeyName,showPopup)
{
	if(yfcAllowSingleSelection(sKeyName))
	{
		var entity="availability";
		var iIndex = getSelectedIndex(sKeyName);
		if(iIndex != -1)
		{
			var ItemID = document.all('ItemID_'+iIndex).value;
			var UOM = document.all('UOM_'+iIndex).value;
			var PC = document.all('PC_'+iIndex).value;
			var OrgCode = document.all('OrgCode_'+iIndex).value;
			var sAddnParams = "xml:/Promise/PromiseLines/PromiseLine/@ItemID="+ItemID;
			sAddnParams += "&xml:/Promise/PromiseLines/PromiseLine/@UnitOfMeasure="+UOM;
			sAddnParams += "&xml:/Promise/PromiseLines/PromiseLine/@ProductClass="+PC+"&xml:/Promise/@OrganizationCode="+OrgCode+"&xml:/Promise/@OptimizationType=01";

			if(showPopup == 'Y')
				yfcShowSearchPopupWithParams(sSearchViewID, "", '900', '550', '', entity, encodeURI(sAddnParams) );
			else
				yfcShowSearchWithParams(sSearchViewID,entity, encodeURI(sAddnParams) );
		}
	}
}

function getSelectedIndex(sName)
{
	var eleArray = document.all(sName);

	if (!eleArray)
		return -1;

	for ( var i =0; i < eleArray.length; i++ ) 
	{
        if (eleArray[i].checked) {
            return i+1;
        }
    }
	return 1; // this is the case where only one checkbox was checked.in this case, the eleArray.length variable was being set to UNDEFINED and so had to resort to this fix.CR ID 31641
}

function processSaveRecordsForIndividualAdj(){
    yfcSpecialChangeNames("IndividualAdj", false);
}

function callOrgLookup(currObj,entityname)
{
	var oObj = document.all("xml:/Promise/@OrganizationCode");
	var oDisbRuleObj = document.all("xml:/Promise/@DistributionRuleId");
	var oScheduleRuleObj = document.all("xml:/Promise/@AllocationRuleID");
	callLookup(currObj,entityname);
	oObj.disabled=false;
	oDisbRuleObj.value = "";
	oScheduleRuleObj.value = "";
	yfcPostSearchForm();
}

function prepareSelectedAvailabilityOption(optionNo) {    

	var containerForm= document.all("containerform");
	var oDiv = document.getElementById("divOrderLines");
	oDiv.innerHTML = "";
    
	var i = 1;
    while (document.all("ShipNode_" + optionNo + "_" + i)) {
			var itemID = document.all("ItemID_" + optionNo + "_" + i);
			var uom = document.all("UnitOfMeasure_" + optionNo + "_" + i);
			var pc = document.all("ProductClass_" + optionNo + "_" + i);
            var shipNode = document.all("ShipNode_" + optionNo + "_" + i);
            var shipDate = document.all("ShipDate_" + optionNo + "_" + i);
            var deliveryDate = document.all("DeliveryDate_" + optionNo + "_" + i);
            var quantity = document.all("quantity_" + optionNo + "_" + i);
            
            
            var hiddenShipNode = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/@ShipNode' value='" + shipNode.value + "'/>");
            oDiv.insertBefore(hiddenShipNode);
            var hiddenShipDate = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/@ReqShipDate' value='" + shipDate.value + "'/>");
            oDiv.insertBefore(hiddenShipDate);
            var hiddenDeliveryDate = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/@ReqDeliveryDate' value='" + deliveryDate.value + "'/>");
            oDiv.insertBefore(hiddenDeliveryDate);
            var hiddenQuantity = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/@OrderedQty' value='" + quantity.value + "'/>");
            oDiv.insertBefore(hiddenQuantity);
			var hiddenItemID = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/Item/@ItemID' value='" + itemID.value + "'/>");
            oDiv.insertBefore(hiddenItemID);
			var hiddenUOM = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/Item/@UnitOfMeasure' value='" + uom.value + "'/>");
            oDiv.insertBefore(hiddenUOM);
			var hiddenPC = document.createElement("<INPUT type='hidden' id='selectedLine' name='xml:/Order/OrderLines/OrderLine_" + i + "/Item/@ProductClass' value='" + pc.value + "'/>");
            oDiv.insertBefore(hiddenPC);
            
            i++;
    }
}

function showCreateOrder(sViewID)
{
	var existingHiddenInputs = document.all("selectedLine"); 
    if (existingHiddenInputs == null) 
	{
		var sNo = document.all("DefaultOptionNo").value;
		prepareSelectedAvailabilityOption(sNo);
	}

	var myObject = new Object();
	var sKey = document.all("DummyEntityKey");
	var params = "xml:/Order/@EnterpriseCode=" + document.all("OrgCode").value;
	var oDiv = document.getElementById("divOrderLines");
    myObject.currentWindow = window;
	myObject.OrderLineSet = oDiv;
    yfcShowDetailPopupWithParams(sViewID, "", "700", "350",params,'availability',sKey.value,myObject);

}

function callCreateOrder()
{
	insertOrderLines();
	window.doNotCheck=true;
	return true;
}

function insertOrderLines()
{
	var myObject = new Object();
	myObject = window.dialogArguments;
	var parentWin = myObject.currentWindow;
	var oDiv = document.getElementById("divOrderLines");
	var containerForm= document.all("containerform");
	oDiv.innerHTML = myObject.OrderLineSet.innerHTML;
}

function checkForSelection()
{
	var i = 1;
    var checkboxObject = document.all('chkAdjustment_' + i);
    while (checkboxObject) 
	{
        if (checkboxObject.checked) 
			return true;
		i++;
        checkboxObject = document.all('chkAdjustment_' + i);
    }
	alert(YFCMSG002);
	document.body.style.cursor='auto';
	return false;
}

function RefreshAllRegions(allRegions,bodyElem){
    var tmpBodyElem = document.all("bodyElem");
    var inputs = tmpBodyElem.getElementsByTagName("INPUT");
    if(allRegions) {
        for ( var i = 0; i < inputs.length ; i++ ) {
            var input = inputs.item(i);
            input.disabled=true;
        }
    } else {
        for ( var i = 0; i < inputs.length ; i++ ) {
            var input = inputs.item(i);
            input.disabled=false;
        }
    }
    
}

function goToSearch(sSearchViewID,sEntity,bPopup) {
    
    if(bPopup == 'Y')
        yfcShowSearchPopup(sSearchViewID,"",'900', '500','',sEntity);
    else
        yfcShowSearch(sSearchViewID,sEntity,"");

}

function prepareAddToResourceGroup() {
    getKeyFromParent('xml:/ResourceGroup/@ResourceGroupKey','xml:/ResourceGroup/@ResourceGroupKey');
    getKeyFromParent('xml:/ResourceGroup/@ShipNode','xml:/ResourceGroup/@ShipNode');
    yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'regionKey', 'xml:/ResourceGroup/RegionsServiced/RegionServiced/Region', null); 

    return (true);
}

function showResourceGroupCapacity(sViewID, resCapacityEntityKey) {   
       var capacityKey = document.all(resCapacityEntityKey).value;
       var entity="resourcecapacity";
       yfcShowDetailPopupWithParams(sViewID,"",'900', '500',"", entity, capacityKey, "");
}

function highlightColumns(counter,dayId,weight) {
    regionId="Region_"+counter;
    var region = document.all(regionId);
    var weekDayHeader = document.all(dayId);
    region.style.fontWeight=weight;
    weekDayHeader.style.fontWeight=weight;
}

function checkIfResourcePoolValidForAction(checkBoxName, attributeValue, msgCodeForFailure) {

	var allCheckBoxesArray = document.all(checkBoxName);

	if (!allCheckBoxesArray)
		return (true);

	if (allCheckBoxesArray.length != null) {
		for (var i =0; i < allCheckBoxesArray.length; i++) {

			if ((allCheckBoxesArray[i].checked) && (allCheckBoxesArray[i].resourceLevel != attributeValue)) {
				alert (msgCodeForFailure);
				return (false);
			}
		}
	}
	else {
		// If there was only one checkbox in the document, just look at the checked state of the object
		if ((allCheckBoxesArray.checked) && (allCheckBoxesArray.resourceLevel != attributeValue)) {
			alert (msgCodeForFailure);
			return (false);
		}
	}
	return (true);
}

function checkForMultipleSelection(checkBoxName, attributeValue, msgCodeForFailure){
	if(yfcAllowSingleSelection(checkBoxName)){
		return checkIfResourcePoolValidForAction(checkBoxName, attributeValue, msgCodeForFailure)
	}
}
//this function is called when override reason popup is invoked
function overrideReasonPopup(viewID,width, height){
	var myObject = new Object();
	yfcShowDetailPopup(viewID,"",width,height,myObject,'resourcecapacity');
	if (myObject.OKClicked == "true") {
		var overrideReasonCtrl=null;
		var ctrl=null;
		var bindingPrefix="";
		var tableObject = document.getElementById("CAPACITYTABLE"); 
		var tdNodes=tableObject.getElementsByTagName("TD");
		for(var k=0;k<tdNodes.length;k++){
			var tdNode=tdNodes.item(k);
			if(tdNode.id!="CAPACITYTD") continue;
			var ctrls=tdNode.childNodes;
			for(var l=0;l<ctrls.length;l++){
				ctrl=ctrls.item(l);
				if(ctrl.id=="CAPACITY") {
					if(ctrl.getAttribute("oldValue")!=ctrl.getAttribute("value")){
						//alert("capacity modified. oldvalue=" + ctrl.getAttribute("oldValue") +",value=" +ctrl.getAttribute("value"));
						bindingPrefix=ctrl.getAttribute("bindingPrefix");
						overrideReasonCtrl=document.all[bindingPrefix+"OverrideReason"];
						overrideReasonCtrl.setAttribute("value",myObject.OverrideReason);
						overrideReasonCtrl.setAttribute("oldValue","");
					}
				}
			}

		}
        return (true);
    }
    else {
        return (false);
    }

}

function overrideReasonPopupForCapacity(viewID,width, height){
	var myObject = new Object();
	yfcShowDetailPopup(viewID,"",width,height,myObject,'resourcecapacity');
	if (myObject.OKClicked == "true") {
		var overrideReasonCtrl=null;
		var ctrl=null;
		var bindingPrefix="";
		var tableObject = document.getElementById("CAPACITYTABLE"); 
		var tdNodes=tableObject.getElementsByTagName("TD");
		for(var k=0;k<tdNodes.length;k++){
			var tdNode=tdNodes.item(k);
			if(tdNode.id!="CAPACITYTD") continue;
			var ctrls=tdNode.childNodes;
			for(var l=0;l<ctrls.length;l++){
				ctrl=ctrls.item(l);
				if(ctrl.id=="CAPACITY") {
					if(ctrl.getAttribute("oldValue")!=ctrl.getAttribute("value")){
						//alert("capacity modified. oldvalue=" + ctrl.getAttribute("oldValue") +",value=" +ctrl.getAttribute("value"));
						bindingPrefix=ctrl.getAttribute("bindingPrefix");
						overrideReasonCtrl=document.all[bindingPrefix+"OverrideReason"];
						overrideReasonCtrl.setAttribute("value",myObject.OverrideReason);
						overrideReasonCtrl.setAttribute("oldValue","");
					}
				}
			}

		}
		okClicked();
        return (true);
    }
    else {
        return;
    }

}

function parentExpandClose(slotRowid,expandgif,collapsegif,expandAlt,collapseAlt){
	var allrows = document.body.getElementsByTagName("tr");
	if(document.getElementById("uomRow_" + slotRowid + "_1_1").style.display == "none")
	{
		var srcElement;
        srcElement = window.event.srcElement;
        srcElement.src=expandgif;
        srcElement.alt=collapseAlt;
	document.getElementById("uomRow_" + slotRowid + "_1").src = collapsegif;

		for(i=0;i<allrows.length;i++){
			var rowID=allrows[i].id;
			if(rowID!=null){
			if(rowID.substr(7,(slotRowid.length)) == slotRowid){	
				for(a=i;a<allrows.length;a++){
					var newID=allrows[a].id;
					if(newID!=null){
					if((newID.substr(7,(slotRowid.length+1)) == slotRowid + "_")&& (newID.length == slotRowid.length+11)){
						
						document.getElementById(newID).style.display = "block";
						var eleArray = document.getElementById(newID).getElementsByTagName("IMG");						
						 for (var j = 0; j < eleArray.length; j++) {
								eleArray.item(j).src = collapsegif;
								}
						
							}
						}
					}break
				}
			}
		}
	
	}
else{
	var srcElement;
        srcElement = window.event.srcElement;
        srcElement.alt=expandAlt;
        srcElement.src=collapsegif;
		for(i=0;i<allrows.length;i++){
	
			var rowID=allrows[i].id;
			if(rowID!=null){
			if(rowID.substr(7,(slotRowid.length)) == slotRowid){
	
				for(a=i;a<allrows.length;a++){
					var newID=allrows[a].id;
					if(newID!=null){
					if((newID.substr(7,(slotRowid.length+1)) == slotRowid + "_")&& (newID.length >= slotRowid.length+11)
						|| (newID.substr(12,(slotRowid.length+1)) == slotRowid + "_")&& (newID.length >= slotRowid.length+16)
						||(newID.substr(13,(slotRowid.length+1)) == slotRowid + "_")&& (newID.length >= slotRowid.length+17)
							){
						
						document.getElementById(newID).style.display = "none";
						var eleArrayAlt = document.getElementById(newID).getElementsByTagName("IMG");						
						 for (var j = 0; j < eleArrayAlt.length; j++) {
								eleArrayAlt.item(j).alt = expandAlt; 
								}
							}
						}
					}break
				}
			}
		}	
	}
}


function copyDays() {
	var days = new Array(7);
	days[0] = "";
	days[1] = "";
	days[2] = "";
	days[3] = "";
	days[4] = "";
	days[5] = "";
	days[6] = "";
	
	var allrows = document.body.getElementsByTagName("input");	
	NoOfSlots=0
	for(i=0;i<allrows.length;i++){
			var rowID=allrows[i].id;				
			if(rowID!=null){				
			if(rowID.substr(0,8) == "Standard"){
				if(rowID.substr(10,1) != "_"){
					if(parseInt(rowID.substr(9,2)) > NoOfSlots){
					NoOfSlots = parseInt(rowID.substr(9,2))
							}
						}		
				if(rowID.substr(10,1) == "_"){
					if(rowID.substr(9,1) > NoOfSlots){
					NoOfSlots = rowID.substr(9,1)
							}
						}	
					}
				}
			}
			
	NoOfUOMs=0
	for(i=0;i<allrows.length;i++){
			var rowID=allrows[i].id;			
			if(rowID!=null){				
			if(rowID.substr(0,8) == "Standard"){
				if(NoOfSlots < 10){
					if(rowID.substr(11,1) > NoOfUOMs){
					NoOfUOMs = rowID.substr(11,1)
							}
						}
				if(NoOfSlots >= 10){
					if(rowID.substr(12,1) > NoOfUOMs){
					NoOfUOMs = rowID.substr(12,1)
							}
						}
					}
				}
			}
	
	for(var i = 1; i < 8; i++){		
		if(document.getElementById("Check_" + i).checked){
			days[i] = document.getElementById("Check_" + i).value;
				}
	}

	for(var j = 1; j <= NoOfSlots; j++){
			for(var a = 1; a <= NoOfUOMs; a++){
				if(document.getElementById("Standard_" + j + "_" + a).value != "0.00" && document.getElementById("Standard_" + j + "_" + a).value != ""){
						for(var i = 0; i < days.length; i++){
							if(days[i] != ""){
								if(document.getElementById(days[i] + "_Standard_" + j + "_" + a) != null)
									document.getElementById(days[i] + "_Standard_" + j + "_" + a).value = document.getElementById("Standard_" + j + "_" + a).value;
								}
						}
				}
		document.getElementById("Standard_"+j + "_" + a).value = "";

		if(document.getElementById("Supplemental_"+j + "_" + a).value != "0.00" && document.getElementById("Supplemental_"+j + "_" + a).value != ""){
			for(var i = 0; i < days.length; i++){
				if(days[i] != ""){
					if(document.getElementById(days[i] + "_Supplemental_" + j+ "_" + a) != null)
						document.getElementById(days[i] + "_Supplemental_" + j+ "_" + a).value = document.getElementById("Supplemental_"+j + "_" + a).value;
					}
				}
			}
		document.getElementById("Supplemental_"+j + "_" + a).value = "";
		}
	}
}

function copyCapacity() {
	var days = new Array(7);
	days[0] = "";
	days[1] = "";
	days[2] = "";
	days[3] = "";
	days[4] = "";
	days[5] = "";
	days[6] = "";
	var allrows = document.body.getElementsByTagName("input");	
	NoOfSlots=0
	for(i=0;i<allrows.length;i++){
			var rowID=allrows[i].id;				
			if(rowID!=null){				
			if(rowID.substr(0,8) == "Standard"){
				if(rowID.substr(9,1) > NoOfSlots){
					NoOfSlots = rowID.substr(9,1)
						}
					}
				}
			}
	NoOfUOMs=1;
	for(i=0;i<allrows.length;i++){
			var rowID=allrows[i].id;			
			if(rowID!=null){				
			if(rowID.substr(0,8) == "Standard"){				
				if(rowID.substr(11,1) > NoOfUOMs){
					NoOfUOMs = rowID.substr(11,1)
						}
					}
				}
			}
	for(var i = 1; i < 8; i++){		
		if(document.getElementById("Check_" + i).checked){
			days[i] = document.getElementById("Check_" + i).value;
				}
	}
	for(var j = 1; j <= NoOfSlots; j++){
		if(document.getElementById("Standard_" + j).value != "0.00" && document.getElementById("Standard_" + j ).value != ""){
			for(var i = 0; i < days.length; i++){
				if(days[i] != ""){
					document.getElementById(days[i] + "_Standard_" + j ).value = document.getElementById("Standard_" + j ).value;
				}
			}
		}
		document.getElementById("Standard_"+j ).value = "";
	}
}
