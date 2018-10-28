// Jay : we have to override this file for changes related to populating the company
// which has to be a radio button
var myObject = new Object();
myObject = dialogArguments;

var parentwin = myObject.currentwindow;
var src = myObject.currentsource;
var addressip = getAddressIP(src);
var allowedModValue = getAllowedModValue();
var answerSetOptionsBinding = getAnswerSetOptionsBinding();

function window.onload()	{
    if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
        return;
    }

    setAddress(document.containerform.AddressType,"AddressType");
    document.containerform.AddressTypeLabel.value = document.containerform.AddressType.value;
    
    if (document.containerform.AddressTypeLabel.value == '')    {
        var addressTypeRow=document.all("AddressTypeRow");
        addressTypeRow.style.display="none";
    }

    setAddress(document.containerform.Company,"Company");
    setAddress(document.containerform.FirstName,"FirstName");
    setAddress(document.containerform.MiddleName,"MiddleName");
    setAddress(document.containerform.LastName,"LastName");
    setAddress(document.containerform.AddressLine1,"AddressLine1");
    setAddress(document.containerform.AddressLine2,"AddressLine2");
    setAddress(document.containerform.AddressLine3,"AddressLine3");
    setAddress(document.containerform.AddressLine4,"AddressLine4");
    setAddress(document.containerform.AddressLine5,"AddressLine5");
    setAddress(document.containerform.AddressLine6,"AddressLine6");
    setAddress(document.containerform.City,"City");
    setAddress(document.containerform.State,"State");
    setAddress(document.containerform.ZipCode,"ZipCode");
    setCountryAddress(document.containerform.Country,"Country");
    setAddress(document.containerform.DayPhone,"DayPhone");
    setAddress(document.containerform.EveningPhone,"EveningPhone");
    setAddress(document.containerform.MobilePhone,"MobilePhone");
    setAddress(document.containerform.DayFaxNo,"DayFaxNo");
    setAddress(document.containerform.Emailid,"EMailID");
    setAddress(document.containerform.AlternateEmailID,"AlternateEmailID");
    setAddress(document.containerform.Beeper,"Beeper");
    setAddress(document.containerform.Department,"Department");
    setAddress(document.containerform.EveningFaxNo,"EveningFaxNo");
    setAddress(document.containerform.JobTitle,"JobTitle");
    setAddress(document.containerform.OtherPhone,"OtherPhone");
    setAddress(document.containerform.Suffix,"Suffix");
    setAddress(document.containerform.Title,"Title");
    setAddress(document.containerform.ErrorTxt,"ErrorTxt");
	setAddress(document.containerform.HttpUrl,"HttpUrl");
    setAddress(document.containerform.PersonID,"PersonID");
    setAddress(document.containerform.PreferredShipAddress,"PreferredShipAddress");
    setAddress(document.containerform.UseCount,"UseCount");
    setAddress(document.containerform.VerificationStatus,"VerificationStatus");
	
	// code added to check the radio button based on the company
	for (i=0; i < document.forms[0].PseudoCompany.length ; i++) 
	{
		var elemRadio = document.forms[0].PseudoCompany[i];
		if(elemRadio.value == document.containerform.Company.value)
		{
			elemRadio.checked = true ;
		}
		if(allowedModValue == "N")
		{
			elemRadio.disabled = true ;
		}
	}
	
	var elemCustomerId = document.getElementById("xml:/Customer/@CustomerID");
	
	if(allowedModValue == "N")
	{
		elemCustomerId.disabled = true ;

		var LoadButton = document.getElementById("LoadButton");
		if(!isUndefined(LoadButton) && LoadButton != null)
		{
			LoadButton.disabled = true ;
		}
		var LoadImage = document.getElementById("LoadImage");
		if(!isUndefined(LoadImage) && LoadImage != null)
		{
			LoadImage.disabled = true ;
		}
	}
	// end
    
	setDraftOrderFlag(document.containerform.hiddenDraftOrderFlag);

	if (null != answerSetOptionsBinding) {
		setAnswerOptions(document.all("RetainAnswersOption"));
	}
	
    window.doNotChangeNames = true;
}

function setCountryAddress(obj, str)	{
    obj.name = getAddressName(str);
	obj.value = getAddressValue(str);
    obj.OldValue = getAddressValue(str);

    if (allowedModValue=="N") {
        obj.className = "protectedinput";
		obj.disabled="true";
    }
    else if (allowedModValue=="Y") {
        obj.className = "combobox";
    }
    else {
        obj.className = "comboboxoverride";
    }
}

function setAddress(obj, str)	{
    obj.name = getAddressName(str);
    obj.value = getAddressValue(str);
    obj.OldValue = getAddressValue(str);

    if (allowedModValue=="N") {
        obj.className = "protectedinput";
        obj.contentEditable = "false";
    }
    else if (allowedModValue=="Y") {
        obj.className = "unprotectedinput";
    }
    else {
        obj.className = "unprotectedoverrideinput";
        obj.setAttribute("yfsoverride", "true");
    }
}

function getAddressValue(str)	 {
    var childNodes = addressip.getElementsByTagName("input");
    var addvalue="";
    for (var i=0;i<childNodes.length;i++)	 {
        var oNode = childNodes.item(i);
        if (oNode.type == "hidden")	{
            if (getAddressNodeName(oNode.getAttribute("yName"))==str)	 {
                addvalue=oNode.value;
                i=childNodes.length;
            }
        }
    }
    return addvalue;

}

function getAddressName(str)	 {
    var childNodes = addressip.getElementsByTagName("input");
    var addvalue="";
    for (var i=0;i<childNodes.length;i++)	 {
        var oNode = childNodes.item(i);
        if (oNode.type == "hidden")	{
            if (getAddressNodeName(oNode.getAttribute("yName"))==str)	 {
                addvalue=oNode.getAttribute("yName");
                i=childNodes.length;
            }
        }
    }
    return addvalue;
}

function getAddressNodeName(str) {
	if (null != str) {
		var i = str.lastIndexOf("@");
		return str.substring(i+1,str.length);
	}
}

function getAddressIP(obj)	 {
    var ipfound = 0;
    var obj1=obj;
    while(ipfound == 0)	{
        obj1 = obj1.parentElement;
        if (obj1.getAttribute("addressip") == "true")	
            ipfound=1;
    }			
    return obj1;
}

function getAllowedModValue() {

    var allInputs = addressip.getElementsByTagName("input");
    for (var i=0;i<allInputs.length;i++) {
        var singleInput = allInputs.item(i);
        if (getAddressNodeName(singleInput.getAttribute("yName"))=="AllowedModValue") {
            return (singleInput.value);
        }
    }
}

function getAnswerSetOptionsBinding() {

    var allInputs = addressip.getElementsByTagName("input");
    for (var i = 0; i < allInputs.length; i++) {
        var singleInput = allInputs.item(i);
        if (singleInput.name == "AnswerSetOptionsBinding") {
            return (singleInput.value);
        }
    }
	return (null);
}

function setDraftOrderFlag(draftOrderInput) {

    var parentDraftOrderInput = parentwin.document.all("hiddenDraftOrderFlag");
    if (parentDraftOrderInput != null) {
        draftOrderInput.value = parentDraftOrderInput.value;
    }
}

function setAnswerOptions(answerOptionRadioButtons) {

	if (null != answerOptionRadioButtons) {
		if (answerOptionRadioButtons.length != null) {
			if (allowedModValue=="Y") {
				for (var i = 0; i < answerOptionRadioButtons.length; i++) {
					var singleRadio = answerOptionRadioButtons.item(i);
					singleRadio.name = answerSetOptionsBinding;
				}
				// Now display the options to retain or clear answers in the screen
				var answerOptionsTableObj = document.all("answerOptionsTable");
				if (null != answerOptionsTableObj) {
					answerOptionsTableObj.style.display="";
				}
			}
		}
	}
}