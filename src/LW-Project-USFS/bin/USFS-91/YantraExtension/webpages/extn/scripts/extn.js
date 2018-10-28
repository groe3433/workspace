/*
 The account code formatting on the incident entry screen
 example FA2402830HT488A (lenght 15)to be formatted as FA-240-249-2830-HT-488A-264B (lenght 28)
*/
//  changing this function as formatting requred was not as expected, if required the previous version can be pulled out from cvs
var specialChars = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?~_"; 
function formatAccountCode(elem,setValue,fundCode,object){
   var val = elem.value;
   //var blmField=document.getElementById("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode");
   if(val==null||val==''){return val};

   //check if formatting has already been done
   //we check if any "-" are present in the value if present we return
   if(val.indexOf('-')!=-1){
    return val;
   }
	// Extract relevant sections
	
   state = val.substring(0,2);//1st-2nd position  = State

   if (state.length != 2)
   {
	   alert("State code is too short");
	   	   elem.focus();
		   return;
   }
   office = val.substring(2,5);//3rd-5th = Office
      if (office.length != 3)
   {
	   alert("Office code is too short");
	   elem.focus();
	   return;
   }
   //fundCode = val.substring(5,8);//6th-8th = Fund code
      if (fundCode.length != 3)
   {
	   alert("Fund code is too short");
	   elem.focus();
	   return;
   }

   activity = val.substring(5,9);//9th-12th= Activity (numeric)  * if activity= 5700, then no project
   if (activity.length != 4)
   {
	   alert("Activity code is too short");
	   elem.focus(); 
	   return;
   }
   programelem = val.substring(9,11);//13th-14th= Program element
      if (programelem.length != 2)
   {
	   alert("Program element code is too short");
	   elem.focus(); 
	   return;
   }
   project = val.substring(11,15);//15th-18th= Project
      if (project.length != 4)
   {
	   project = "    ";
	   //alert("Project code is too short");
	   //elem.focus();
	   //return;
   }
   //object = '264B' ;
   //object = val.substring(14,18);//19th-22nd= Object class
      if (object.length != 4)
   {
	   alert("Object class code too short");
	   elem.focus(); 
	   return;
   }
   formattedval = state+"-"+office+"-"+fundCode+"-"+activity+"-"+programelem+"-"+project+"-"+object;
   //alert(formattedval);
   if(setValue){
	   elem.value = formattedval;
   }
   
   return formattedval;
}

/*
 Generic javascript to update 
*/
function updateAddress(elem,xmlDoc,nodepath,addresstag) {

	nodes=xmlDoc.getElementsByTagName(addresstag);
	//traverse(nodes(0));
	
	//CR 325 - declares the node value to extract attributes below
	var CreateTS = new Date();
	var now = Date.parse(new Date()); // timestamp of the entry
	extn_nodes=xmlDoc.getElementsByTagName("Extn");
	//End CR 325 
	
	if(nodes!=null && nodes.length >0){	
		var personInfo = nodes(0);
		var path=nodepath;
		//populate the address
		document.getElementById(path+"/@AddressLine1").value = personInfo.getAttribute("AddressLine1");
		document.getElementById(path+"/@AddressLine2").value = personInfo.getAttribute("AddressLine2");
		document.getElementById(path+"/@AddressLine3").value = personInfo.getAttribute("AddressLine3");
		document.getElementById(path+"/@AddressLine4").value = personInfo.getAttribute("AddressLine4");
		document.getElementById(path+"/@AddressLine5").value = personInfo.getAttribute("AddressLine5");
		document.getElementById(path+"/@AddressLine6").value = personInfo.getAttribute("AddressLine6");
		document.getElementById(path+"/@City").value = personInfo.getAttribute("City");
		document.getElementById(path+"/@State").value = personInfo.getAttribute("State");
		document.getElementById(path+"/@ZipCode").value = personInfo.getAttribute("ZipCode");
		document.getElementById(path+"/@Company").value = personInfo.getAttribute("Company");		
		document.getElementById(path+"/@DayFaxNo").value = personInfo.getAttribute("DayFaxNo");
		// the country combo
		document.getElementById(path+"/@Country").value = personInfo.getAttribute("Country");
		document.getElementById(path+"/@FirstName").value = personInfo.getAttribute("FirstName");
		document.getElementById(path+"/@LastName").value = personInfo.getAttribute("LastName");
		document.getElementById(path+"/@DayPhone").value = personInfo.getAttribute("DayPhone");
		document.getElementById(path+"/@MobilePhone").value = personInfo.getAttribute("MobilePhone");
		document.getElementById(path+"/@EMailID").value = personInfo.getAttribute("EMailID");

		//CR 325 - finds the Customer Name and Saves in the HttpUrl field in Person Info table
		//       - perform this only for ship to section --> customer name
		if (path == "xml:/NWCGIncidentOrder/YFSPersonInfoShipTo") // coming from Incident
		{
			if (extn_nodes!=null && extn_nodes.length>0)
			{
				var extnInfo = extn_nodes(0);
				//var ExtnName = extnInfo.getAttribute("ExtnCustomerName");
				//alert("Extn Customer Name=["+ ExtnName +"]");
				document.getElementById(path+"/@HttpUrl").value = extnInfo.getAttribute("ExtnCustomerName");
				//using JobTitle field to capture the timestamp for modification
				document.getElementById(path+"/@JobTitle").value = now;
			}
		}
		if (path == "xml:/Order/PersonInfoShipTo") // coming from Issue
		{   // populate the below two fields
			document.getElementById(path+"/@JobTitle").value = personInfo.getAttribute("JobTitle");
			document.getElementById(path+"/@HttpUrl").value = personInfo.getAttribute("HttpUrl");
		}
		//End CR 325 
		
		if (path == "xml:/Order/PersonInfoBillTo") // coming from Issue
		{   // populate the below two fields
			document.getElementById(path+"/@JobTitle").value = personInfo.getAttribute("JobTitle");
			document.getElementById(path+"/@HttpUrl").value = personInfo.getAttribute("HttpUrl");
		}	
			
		var companyAttrib = personInfo.getAttribute("Company");
		if(companyAttrib != null && companyAttrib.length > 0) {
			setRadioOptions(companyAttrib,nodepath);
		}

		return true;
	}else{
		//alert("Customer#:"+elem.value+" does not exist");
		// callers can handle this condition
		return false;
	}
}

function setRadioOptions(value,path)
{
	if(path == null || isUndefined(path))
		path = "" ;

	var radioButtons = document.getElementById(path+"RadioAddress");
	var radioList = radioButtons.getElementsByTagName("input");
	for(index = 0 ; index < radioList.length ; index++)
	{
		var elem = radioList[index];
		if(elem.type == "radio" && elem.value==value)
		{
			elem.checked = true;
		}
	}
}

function validateDeliverExists(){

	var DeliveryMessage = document.getElementById("xml:/GetOperationResultsReq/@DeliveryMessage");
		
	if(DeliveryMessage.value == '' && DeliveryMessage != null){
		alert('There is no message to deliver.  Please enter a Distribution ID first.');
		return false;
	}
}

function validateDistributionIDExists(){

	var DistID = document.getElementById("xml:/GetOperationResultsReq/@DistributionID");

	if(DistID != null && DistID.value == ''){
		alert("Please enter a Distribution ID.");		
		return false;
	}
}

function updateAddressWithoutPath(elem,xmlDoc,addresstag){

	//CR 272 capturing the timestamp of the entry
	var CreateTS = new Date();
	//SimpleDateFormat formatTime = new SimpleDateFormat("YYYY-MM-DDThh:mm:ssTZD");
	var now = Date.parse(new Date());

	//CR 269 - declares the node value to extract attributes below
	extn_nodes=xmlDoc.getElementsByTagName("Extn");
	///End CR 269 
	nodes=xmlDoc.getElementsByTagName(addresstag);
	//traverse(nodes(0));
	if(nodes!=null && nodes.length >0)
	{		
		var personInfo = nodes(0);
		//populate the address
		document.getElementById("AddressLine1").value = personInfo.getAttribute("AddressLine1");
		document.getElementById("AddressLine2").value = personInfo.getAttribute("AddressLine2");
		document.getElementById("AddressLine3").value = personInfo.getAttribute("AddressLine3");
		document.getElementById("AddressLine4").value = personInfo.getAttribute("AddressLine4");
		document.getElementById("AddressLine5").value = personInfo.getAttribute("AddressLine5");
		document.getElementById("AddressLine6").value = personInfo.getAttribute("AddressLine6");
		document.getElementById("City").value = personInfo.getAttribute("City");
		document.getElementById("Company").value = personInfo.getAttribute("Company");
		document.getElementById("State").value = personInfo.getAttribute("State");
		document.getElementById("ZipCode").value = personInfo.getAttribute("ZipCode");
		// the country combo
		document.getElementById("Country").value = personInfo.getAttribute("Country");
		document.getElementById("FirstName").value = personInfo.getAttribute("FirstName");
		document.getElementById("LastName").value = personInfo.getAttribute("LastName");
		document.getElementById("MiddleName").value = personInfo.getAttribute("MiddleName");
		document.getElementById("DayPhone").value = personInfo.getAttribute("DayPhone");
		document.getElementById("MobilePhone").value = personInfo.getAttribute("MobilePhone");
		document.getElementById("EMailID").value = personInfo.getAttribute("EMailID");
		
		//using JobTitle field to capture the timestamp for modification
		document.getElementById("JobTitle").value = now;

		setRadioOptions(personInfo.getAttribute("Company"));
	}else{
		//alert("Customer#:"+elem.value+" does not exist");
		// callers can handle this condition
		return false;
	}
	//CR 269 - finds the Customer Name and Saves in the HttpUrl field in Person Info table
	if (extn_nodes!=null && extn_nodes.length>0)
	{
		var extnInfo = extn_nodes(0);
		var ExtnName = extnInfo.getAttribute("ExtnCustomerName");
		//alert("Extn Customer Name "+ ExtnName);
		document.getElementById("HttpUrl").value = extnInfo.getAttribute("ExtnCustomerName");
	}
	return true;
}
// end of 269

/* Some utility functions */
function isAlien(a) {
   return isObject(a) && typeof a.constructor != 'function';
}
function isArray(a) {
    return isObject(a) && a.constructor == Array;
}
function isBoolean(a) {
    return typeof a == 'boolean';
}
function isEmpty(o) {
    var i, v;
    if (isObject(o)) {
        for (i in o) {
            v = o[i];
            if (isUndefined(v) && isFunction(v)) {
                return false;
            }
        }
    }
    return true;
}
function isFunction(a) {
    return typeof a == 'function';
}
function isNull(a) {
    return typeof a == 'object' && !a;
}
function isNumber(a) {
    return typeof a == 'number' && isFinite(a);
}
function isObject(a) {
    return (a && typeof a == 'object') || isFunction(a);
}
function isString(a) {
    return typeof a == 'string';
}
function isUndefined(a) {
    return typeof a == 'undefined';
} 
function checkStringForNull(str)
{
	if(str == null || str =='null' || str == 'undefined' || str == undefined)
		return '';

	return str ;
}
/* End util function */

/*
 * Incident to Incident transfer
 */
 function updateFromIncidentDetailsWithoutCustDetail(elem,xmlDoc){
		var IncidentName = '' ;
		var IncidentFsAcctCode = '' ;
		var IncidentBlmAcctCode= '' ;
		var IncidentOtherAcctCode = '' ;
	
		var PrimaryCacheId = '' ;
		var incidentType = '' ;
		var overrideCode = '' ;
		nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		if(nodes != null && nodes.length > 0 )
		{	
			var incidentOrder = nodes(0);
			
			IncidentName = incidentOrder.getAttribute("IncidentName");
			IncidentFsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
			IncidentBlmAcctCode =  incidentOrder.getAttribute("IncidentBlmAcctCode");
			IncidentOtherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");
			
			
			incidentType = incidentOrder.getAttribute("IncidentType");
			overrideCode = incidentOrder.getAttribute("OverrideCode");
		}
		document.getElementById("xml:/Order/Extn/@ExtnIncidentName").value = checkStringForNull(IncidentName) ;
		document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode").value = checkStringForNull(IncidentFsAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value = checkStringForNull(IncidentBlmAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode").value = checkStringForNull(IncidentOtherAcctCode);
		
		document.getElementById("xml:/Order/Extn/@ExtnIncidentType").value = checkStringForNull(incidentType) ;
		document.getElementById("xml:/Order/Extn/@ExtnOverrideCode").value = checkStringForNull(overrideCode) ;

		var returnArray = new Object();
		var IncType = document.getElementById("xml:/Order/Extn/@ExtnIncidentType").value;
		returnArray["xml:CommonCode"] = IncType ;
		returnArray["xml:CodeType"] = "INCIDENT_TYPE" ;

		fetchDataWithParams(elem,'getCommonCodeList',updateFromModifyShortDesc,returnArray);

}
// cr 207

function updateFromModifyShortDesc(elem,xmlDoc)
{

	nodes=xmlDoc.getElementsByTagName("CommonCode");
	var CType = nodes(0);
	var CommonCode = CType.getAttribute("CodeShortDescription");

    document.getElementById("xml:CommonCodeList:/CommonCodeList/CommonCode/@CodeShortDescription").value = CommonCode;
		
}
// end of cr 207
 
function updateFromIncidentDetails(elem,xmlDoc){
	var IncidentName = '' ;
	var IncidentFsAcctCode = '' ;
	var IncidentBlmAcctCode= '' ;
	var IncidentOtherAcctCode = '' ;
	var PhoneNo = '' ;
	var PrimaryCacheId = '' ;
	var incidentType = '' ;
	var overrideCode = '' ;
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	if(nodes != null && nodes.length > 0 )
	{	
		var incidentOrder = nodes(0);
	
		
		IncidentName = incidentOrder.getAttribute("IncidentName");
		IncidentFsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
		IncidentBlmAcctCode =  incidentOrder.getAttribute("IncidentBlmAcctCode");
		IncidentOtherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");
		PhoneNo =  incidentOrder.getAttribute("PhoneNo");
		PrimaryCacheId = incidentOrder.getAttribute("PrimaryCacheId");
		incidentType = incidentOrder.getAttribute("IncidentType");
		overrideCode = incidentOrder.getAttribute("OverrideCode");
	}
	document.getElementById("xml:/Order/Extn/@ExtnIncidentName").value = checkStringForNull(IncidentName) ;
	document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode").value = checkStringForNull(IncidentFsAcctCode);
	document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value = checkStringForNull(IncidentBlmAcctCode);
	document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode").value = checkStringForNull(IncidentOtherAcctCode);
	document.getElementById("xml:/Order/Extn/@ExtnPhoneNo").value = checkStringForNull(PhoneNo);
	document.getElementById("xml:/Order/Extn/@ExtnIncidentCacheId").value = checkStringForNull(PrimaryCacheId) ;
	document.getElementById("xml:/Order/Extn/@ExtnIncidentType").value = checkStringForNull(incidentType) ;
	document.getElementById("xml:/Order/Extn/@ExtnOverrideCode").value = checkStringForNull(overrideCode) ;

    var returnArray = new Object();
	var IncType = document.getElementById("xml:/Order/Extn/@ExtnIncidentType").value;
    returnArray["xml:CommonCode"] = IncType ;
    returnArray["xml:CodeType"] = "INCIDENT_TYPE" ;

	fetchDataWithParams(elem,'getCommonCodeList',updateFromShortDesc,returnArray);
}

// cr 207
function updateFromShortDesc(elem,xmlDoc)
{

	nodes=xmlDoc.getElementsByTagName("CommonCode");
	var CType = nodes(0);
	var CommonCode = CType.getAttribute("CodeShortDescription");

    document.getElementById("xml:/Order/Extn/@FromIncidentType").value = CommonCode;
}
// end of cr 207

// the incident details updater
function updateToIncidentDetailsWithoutCustDetail(elem,xmlDoc){
		nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		var IncidentName = '' ;
		var IncidentFsAcctCode = '' ;
		var IncidentBlmAcctCode= '' ;
		var IncidentOtherAcctCode = '' ;

		var incidentType = '' ;
		var overrideCode = '' ;
		if(nodes!=null && nodes.length >0 ){	
			var incidentOrder = nodes(0);
			
			IncidentName = incidentOrder.getAttribute("IncidentName");
			IncidentFsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
			IncidentBlmAcctCode =  incidentOrder.getAttribute("IncidentBlmAcctCode");
			IncidentOtherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");

			incidentType = incidentOrder.getAttribute("IncidentType");
			overrideCode = incidentOrder.getAttribute("OverrideCode");
			try{
				updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","YFSPersonInfoShipTo");
				updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","YFSPersonInfoBillTo");
			}catch(err){
				// we dont care 	
			}
		}
		document.getElementById("xml:/Order/Extn/@ExtnToIncidentName").value = checkStringForNull(IncidentName) ;
		document.getElementById("xml:/Order/Extn/@ExtnToFsAcctCode").value = checkStringForNull(IncidentFsAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToBlmAcctCode").value = checkStringForNull(IncidentBlmAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToOtherAcctCode").value = checkStringForNull(IncidentOtherAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToIncidentType").value = checkStringForNull(incidentType);
		document.getElementById("xml:/Order/Extn/@ExtnToOverrideCode").value = checkStringForNull(overrideCode);
    
		var returnArray = new Object();
		var IncType = document.getElementById("xml:/Order/Extn/@ExtnToIncidentType").value;
		returnArray["xml:CommonCode"] = IncType ;
		returnArray["xml:CodeType"] = "INCIDENT_TYPE" ;

		fetchDataWithParams(elem,'getCommonCodeList',updateToModifyShortDesc,returnArray);
}

// cr 207
function updateToModifyShortDesc(elem,xmlDoc)
{

	nodes=xmlDoc.getElementsByTagName("CommonCode");
	var CType = nodes(0);
	var CommonCode = CType.getAttribute("CodeShortDescription");

    document.getElementById("xml:/CommonCodeList/CommonCode/@CodeShortDescription").value = CommonCode;		
}
// end of cr 207


function updateToIncidentDetails(elem,xmlDoc){
		var documentType = document.getElementById("xml:/Order/@DocumentType");
		var toIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnToIncidentNo");
		var toIncidentYear = document.getElementById("xml:/Order/Extn/@ExtnToIncidentYear");

		nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
		var IncidentName = '' ;
		var IncidentFsAcctCode = '' ;
		var IncidentBlmAcctCode= '' ;
		var IncidentOtherAcctCode = '' ;
		var PhoneNo = '' ;
		var PrimaryCacheId = '' ;
		var incidentType = '' ;
		var overrideCode = '' ;
		if(nodes!=null && nodes.length >0 ){	
			var incidentOrder = nodes(0);
			
			IncidentName = incidentOrder.getAttribute("IncidentName");
			IncidentFsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
			IncidentBlmAcctCode =  incidentOrder.getAttribute("IncidentBlmAcctCode");
			IncidentOtherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");
			PhoneNo =  incidentOrder.getAttribute("PhoneNo");
			PrimaryCacheId = incidentOrder.getAttribute("PrimaryCacheId");
			incidentType = incidentOrder.getAttribute("IncidentType");
			overrideCode = incidentOrder.getAttribute("OverrideCode");
			try{
				updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoShipTo","YFSPersonInfoShipTo");
				updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","YFSPersonInfoBillTo");
			}catch(err){
				// we dont care 	
			}
		}
		else if(nodes.length == 0)
	    {
			if (documentType.value == '0008.ex')
			{
				var answer = confirm('Incident may exist in ROSS would you like to check?');
				if (answer)
				{
					var iparams = "xml:/NWCGIncidentOrder/@IncidentNo=" + toIncidentNo.value;
					iparams += "&xml:/NWCGIncidentOrder/@IncidentYear=" + toIncidentYear.value;
					iparams += "&xml:/NWCGIncidentOrder/@iCreateNewIncident=Y";
					iparams += "&xml:/NWCGIncidentOrder/@IncidentAction=CREATE";
					yfcShowDetailPopupWithParams('NWCGYOMD050',"",1030,650,iparams,'NWCGIncident',"",1);
					nodes = "" ;
					return true;
				} 
				else 
				{
					nodes = "" ;
					toIncidentNo.value = "";
					toIncidentYear.value = "";
					toIncidentNo.focus();
					return false;
				}
			}
			nodes = "" ;
	    }
		
		document.getElementById("xml:/Order/Extn/@ExtnToIncidentName").value = checkStringForNull(IncidentName) ;
		document.getElementById("xml:/Order/Extn/@ExtnToFsAcctCode").value = checkStringForNull(IncidentFsAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToBlmAcctCode").value = checkStringForNull(IncidentBlmAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToOtherAcctCode").value = checkStringForNull(IncidentOtherAcctCode);
		document.getElementById("xml:/Order/Extn/@ExtnToPhoneNo").value = checkStringForNull(PhoneNo);
		document.getElementById("xml:/Order/Extn/@ExtnToIncidentCacheId").value = checkStringForNull(PrimaryCacheId) ;
		document.getElementById("xml:/Order/Extn/@ExtnToIncidentType").value = checkStringForNull(incidentType) ;
		document.getElementById("xml:/Order/Extn/@ExtnToOverrideCode").value = checkStringForNull(overrideCode) ;
    
		var returnArray = new Object();
		var IncType = document.getElementById("xml:/Order/Extn/@ExtnToIncidentType").value;
		returnArray["xml:CommonCode"] = IncType ;
		returnArray["xml:CodeType"] = "INCIDENT_TYPE" ;

		fetchDataWithParams(elem,'getCommonCodeList',updateToShortDesc,returnArray);
}

// cr 207
function updateToShortDesc(elem,xmlDoc)
{

	nodes=xmlDoc.getElementsByTagName("CommonCode");
	var CType = nodes(0);
	var CommonCode = CType.getAttribute("CodeShortDescription");

    document.getElementById("xml:/Order/Extn/@ToIncidentType").value = CommonCode;
		
}
// end of cr 207

function makeIncidentNumberReadOnly(elem,incidentNoID)
{
	var objSelOption = eval(elem.options[elem.selectedIndex]);
	var objIncNo = document.getElementById(incidentNoID);
			
	if(objSelOption && (objSelOption.value == 'ISNULL' || objSelOption.value == 'NOTNULL'))
	{
			objIncNo.value = "" ;
			objIncNo.readOnly = true;
	}
	else
	{
		objIncNo.readOnly = false;
	}
}

function populateCustomerInfo(elem,xmlDoc,strPath)
{
	if(xmlDoc.length >= 1)
	{
		var objExtn = xmlDoc[0];
		var labelList = document.getElementsByTagName("label");
		setCustomerInfoValues(objExtn,labelList,false,strPath);
		inputList = document.getElementsByTagName("input");
		setCustomerInfoValues(objExtn,inputList,true,strPath);
	}// end if length >= 1 
}

function setCustomerInfoValues(setFromXML,setToNodeList,bIsHidden,strPath)
{
	for (index = 0 ; index < setToNodeList.length ; index++)
	{
		var currentElem = setToNodeList[index];
		if(currentElem.id == strPath+'Department' || currentElem.name == strPath+'Department' )
		{
			if(!bIsHidden)
			{
				currentElem.innerText = checkStringForNull(setFromXML.getAttribute("ExtnDepartment"));
				if(currentElem.innerText == "")
					currentElem.innerText = checkStringForNull(setFromXML.getAttribute("Department"));
			}
			else
			{
				currentElem.value = checkStringForNull(setFromXML.getAttribute("ExtnDepartment"));
				if(currentElem.value == "")
					currentElem.value = checkStringForNull(setFromXML.getAttribute("Department"));
			}

		}// end if Department
		else if(currentElem.id == strPath+'Agency' || currentElem.name == strPath+'Agency')
		{
			if(!bIsHidden)
			{
				currentElem.innerText = checkStringForNull(setFromXML.getAttribute("ExtnAgency"));
				if(currentElem.innerText == "")
					currentElem.innerText = checkStringForNull(setFromXML.getAttribute("Agency"));
			}
			else
			{
				currentElem.value = checkStringForNull(setFromXML.getAttribute("ExtnAgency"));
				if(currentElem.value == "")
					currentElem.value = checkStringForNull(setFromXML.getAttribute("Agency"));
			}
		}// end if Agency
		else if(currentElem.id == strPath+'UnitType' || currentElem.name == strPath+'UnitType')
		{
			if(!bIsHidden)
			{
				currentElem.innerText = checkStringForNull(setFromXML.getAttribute("ExtnUnitType"));
				if(currentElem.innerText == "")
					currentElem.innerText = checkStringForNull(setFromXML.getAttribute("UnitType"));
			}
			else
			{
				currentElem.value = checkStringForNull(setFromXML.getAttribute("ExtnUnitType"));
				if(currentElem.value == "")
					currentElem.value = checkStringForNull(setFromXML.getAttribute("UnitType"));
			}
		}// end if @JurisdictionID
		else if(currentElem.id == strPath+'GACC' || currentElem.name == strPath+'GACC')
		{
			if(!bIsHidden)
			{
				currentElem.innerText = checkStringForNull(setFromXML.getAttribute("ExtnGACC"));

				if(currentElem.innerText == "")
					currentElem.innerText = checkStringForNull(setFromXML.getAttribute("GACC"));
			}
			else
			{
				currentElem.value = checkStringForNull(setFromXML.getAttribute("ExtnGACC"));
				if(currentElem.value == "")
					currentElem.value = checkStringForNull(setFromXML.getAttribute("GACC"));
			}
		}// end if @JurisdictionID
		else if(currentElem.id == strPath+'CustomerName' || currentElem.name == strPath+'CustomerName')
		{
			if(!bIsHidden)
			{
				currentElem.innerText = checkStringForNull(setFromXML.getAttribute("ExtnCustomerName"));
				if(currentElem.innerText == "")
					currentElem.innerText = checkStringForNull(setFromXML.getAttribute("CustomerName"));
			}
			else
			{
				currentElem.value = checkStringForNull(setFromXML.getAttribute("ExtnCustomerName"));
				if(currentElem.value == "")
					currentElem.value = checkStringForNull(setFromXML.getAttribute("CustomerName"));
			}
		}// end if @JurisdictionID		
	}// end for 
}


function updateCustShipAddressFromIncident(elem,xmlDoc){
	
	var objCustomer = xmlDoc.getElementsByTagName("Customer");
	
	var objExtn = xmlDoc.getElementsByTagName("Extn");
	//alert(elem.name);
	//alert(elem.value);

	if (!objCustomer[0])
	{
		var answer = confirm('The entered Customer ID is invalid. Do you want to create one?');
		if (answer)
		{
			var iparams = "xml:/Customer/Consumer/BillingPersonInfo/@PersonID=" + elem.value;
			iparams += "&xml:/newCustomer/@iCreateNewCustomer=Y";
			yfcShowDetailPopupWithParams('NWCGYOMD030',"",1030,650,iparams,'NWCGCustomer',"",1);
			// -- comment out -- let below code handles address updates
			//return;
		} else { // answered No here
		        elem.focus();
		        return false;
	        }
		//alert('Please enter a valid Customer ID');
		//elem.focus();
		//return false;
	}

	if(objExtn[0].getAttribute("ExtnActiveFlag") == 'N' )
	{
		alert('Please enter an Active Customer');
		elem.focus();
		return false;
	}

	if(elem.name == "xml:/Order/@BillToID")
	{
		populateCustomerInfo(elem,objExtn,"xml:/Order/Extn/@Extn");
	}
	else
	{
		populateCustomerInfo(elem,objExtn,"xml:/NWCGIncidentOrder/@");
	}
	
	// don't populate the address details if this function is invoked from the order page
	// we dont need that 
	if(elem.name != "xml:/Order/@BillToID")
	{
		 //update billing address
		 var billaddRet = updateAddress(elem,xmlDoc,"xml:/NWCGIncidentOrder/YFSPersonInfoBillTo","BillingPersonInfo");
		 //update shipping address
		 var shipaddRet = updateAddress(elem,xmlDoc,"xml:/NWCGIncidentOrder/YFSPersonInfoShipTo","BillingPersonInfo");

		 if(!billaddRet && !shipaddRet){
			alert("Customer# "+elem.value+ " does not exist");
		 }
	}
}

function updateCustShipAddress(elem,xmlDoc){
	
	var objCustomer = xmlDoc.getElementsByTagName("Customer");
	
	var objExtn = xmlDoc.getElementsByTagName("Extn");
	
	if (!objCustomer[0])
	{
		alert('Please enter a valid Customer ID');
		elem.focus();
		return false;
	}

	if(objExtn[0].getAttribute("ExtnActiveFlag") == 'N' )
	{
		alert('Please enter an Active Customer');
		elem.focus();
		return false;
	}

	if(elem.name == "xml:/Order/@BillToID")
	{
		populateCustomerInfo(elem,objExtn,"xml:/Order/Extn/@Extn");
	}
	else
	{
		populateCustomerInfo(elem,objExtn,"xml:/NWCGIncidentOrder/@");
	}
	
	// don't populate the address details if this function is invoked from the order page
	// we dont need that 
	if(elem.name != "xml:/Order/@BillToID")
	{
		 //update billing address
		 var billaddRet = updateAddress(elem,xmlDoc,"xml:/NWCGIncidentOrder/YFSPersonInfoBillTo","BillingPersonInfo");
		 //update shipping address
		 var shipaddRet = updateAddress(elem,xmlDoc,"xml:/NWCGIncidentOrder/YFSPersonInfoShipTo","BillingPersonInfo");

		 if(!billaddRet && !shipaddRet){
			alert("Customer# "+elem.value+ " does not exist");
		 }
	}
}

function updateCustDetailsForOtherIssue(elem,xmlDoc){
	
	var objCustomer = xmlDoc.getElementsByTagName("Customer");
	var objExtn = xmlDoc.getElementsByTagName("Extn");


	if (!objCustomer[0])
	{
		alert('Please enter a valid customer');
		elem.focus();
		return false;
	}

	if(objExtn[0].getAttribute("ExtnActiveFlag") == 'N' )
	{
		alert('Please enter an Active Customer');
		elem.focus();
		return false;
	}
	populateCustomerInfo(elem,objExtn,"xml:/Order/Extn/@Extn");
	
	// don't populate the address details if this function is invoked from the order page
	// we dont need that 
}

function validateIncident() {

	var elemFSCode = document.getElementById("xml:/NWCGIncidentOrder/@IncidentFsAcctCode");
	var incidentNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo");
    var cacheId = document.getElementById("xml:/NWCGIncidentOrder/@PrimaryCacheId");
    //var incidentState = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNoState");
	var customerId = document.getElementById("xml:/NWCGIncidentOrder/@CustomerId");	
	var isOtherOrder = document.getElementById("xml:/NWCGIncidentOrder/@IsOtherOrder");
	var objOwnerAgency = document.getElementById("xml:/NWCGIncidentOrder/@OwnerAgency");			

	if (isOtherOrder != null && isOtherOrder.value == 'N')
	{
		if (incidentNo != null && incidentNo.value == '')
		{
			alert("Please enter an Incident/Other Order #");
			incidentNo.focus();
			return false;
		}
	}
	// validation to check for an entry for a customer id - validation on actual customer id is an ajax call in another function
	if(customerId != null && customerId != 'undefined')
	{
		if(customerId.value == '' )
		{
			alert('Please enter the Customer ID');
			customerId.focus();
			return false;
		}
	}

	if (cacheId != null && cacheId.value == '')
	{
		alert("Cache ID is Mandatory !!!");
		return false;
	}

	// if fs account code is entered
	if(eval(elemFSCode) && elemFSCode.value != '' )
	{
		// override code should be mandatory, even though the name of the field is OverrideCost it is actually OverrideCode
		var elemOverrideCode = document.getElementById("xml:/NWCGIncidentOrder/@OverrideCode");
		if(eval(elemOverrideCode) && elemOverrideCode.value == '' )
		{
			alert("Override Code is Mandatory if FS Account Code is entered");
			return false;
		}
	} 

	return validateIncidentExtended();
}

function validateTagDetails()
{
	//alert('Validating Tag Details...');
	
	var unitOfMeasure = document.getElementById("xml:/NWCGTrackableItem/@UnitOfMeasure");
	var availStatus = document.getElementById("xml:/NWCGTrackableItem/@SerialStatus");
	
	//alert("UOM: " + unitOfMeasure.value);
	//alert("Status: " + availStatus.value);
	
	if((availStatus != null && unitOfMeasure != null) && (availStatus.value == 'A' && unitOfMeasure.value != 'KT'))
	{
		return true;
	}
	else
	{
		alert('Tag Details for this Item cannot be updated.');
		return false;
	}	
}

function populateIncidentDetailsOnAdjustInventory(elem,xmlDoc)
{
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	var IncidentName = '' ;
	var IncidentFsAcctCode = '' ;
	var IncidentBlmAcctCode= '' ;
	var IncidentOtherAcctCode = '' ;
	var IncidentFsOverrideAcctCode = '';
	var PhoneNo = '' ;
	var Year = '' ;
	var OwnerAgency = '';

	OwnerAgency = document.getElementById("xml:/AdjustLocationInventory/Audit/@OwnerAgency").value;

	if(nodes!=null && nodes.length >0 ){	
		var incidentOrder = nodes(0);
		
		IncidentName = incidentOrder.getAttribute("IncidentName");
		IncidentFsAcctCode = incidentOrder.getAttribute("IncidentFsAcctCode");
		IncidentBlmAcctCode =  incidentOrder.getAttribute("IncidentBlmAcctCode");
		IncidentOtherAcctCode = incidentOrder.getAttribute("IncidentOtherAcctCode");
		IncidentFsOverrideAcctCode = incidentOrder.getAttribute("OverrideCode");
		Year =  incidentOrder.getAttribute("Year");
		
	}
	
	document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference5").value = checkStringForNull(Year);
	document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference4").value = checkStringForNull(IncidentName);
	
	//Added by GN - 02/08/2007
	
	//alert("OwnAgency "+ OwnerAgency);
	if (OwnerAgency == 'BLM')
	{
	  document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference3").value = checkStringForNull(IncidentBlmAcctCode);
	}
	else if (OwnerAgency == 'FS')
	{
	  document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference3").value = checkStringForNull(IncidentFsAcctCode);
	  document.getElementById("xml:/AdjustLocationInventory/Audit/@OverrideCode").value = checkStringForNull(IncidentFsOverrideAcctCode);
    }

}

function setAccCodeAndOverridecode(){
	var tempAccountCode = document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference3");
	var tempOeverrideCode = document.getElementById("xml:/AdjustLocationInventory/Audit/@OverrideCode");
	if(tempOeverrideCode != null && tempOeverrideCode != 'undefined'){
	var referenceCode = tempAccountCode.value+"~"+tempOeverrideCode.value;
	document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference3").value = referenceCode;
	}
	return true;
}

function validateDLTFormat(){
	// First try ALIS - Adjust Location Inventory Module first
	var labelList = document.getElementsByTagName("label");
	var tempRevisionNo = document.getElementById("xml:/AdjustLocationInventory/Source/Inventory/TagDetail/@RevisionNo");
	if(tempRevisionNo != null && tempRevisionNo != 'undefined') // validate only when a value is entered
	{
		var varStr = tempRevisionNo.value;
		if (varStr.length > 0) // validate only when a value is entered
		{
			if (varStr.indexOf('/') != 2 || varStr.length != 10)
			{
				alert("Please enter a valid date in the \"Date Last Tested\" field in \"mm/dd/yyyy\" format");
				return false;
			}
		}
	}
	return true;
}

function validateIncidentExtended()
{
	var incidentNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo");
	var issueType = document.getElementById("xml:/Order/@OrderType");
	var incidentName = document.getElementById("xml:/NWCGIncidentOrder/@IncidentName");
		
	var incidentType = document.getElementById("xml:/NWCGIncidentOrder/@IncidentType");
	var customerId = document.getElementById("xml:/NWCGIncidentOrder/@CustomerId");

	var fsCode = document.getElementById("xml:/NWCGIncidentOrder/@IncidentFsAcctCode");
	var blmCode = document.getElementById("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode");
	var otherCode = document.getElementById("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode");
	var overrideCode = document.getElementById("xml:/NWCGIncidentOrder/@OverrideCode");

	var objOwnerAgency = document.getElementById("xml:/NWCGIncidentOrder/@OwnerAgency");	
	var isOtherOrder = document.getElementById("xml:/NWCGIncidentOrder/@IsOtherOrder");

	//CR 589
	var costCenter = document.getElementById("xml:/NWCGIncidentOrder/@CostCenter");
	var FunctionalArea = document.getElementById("xml:/NWCGIncidentOrder/@FunctionalArea");
	var WBS = document.getElementById("xml:/NWCGIncidentOrder/@WBS");

	if (isOtherOrder != null && isOtherOrder.value == 'N')
	{
		if(incidentNo.value == '' || incidentNo.type == 'text')
		{
			alert('Please enter the Incident #');
			//incidentNo.focus();
			return false;
		}

		if(incidentName != null && incidentName != 'undefined')
		{
			if(incidentName.value == '')
			{
				alert('Please enter the Incident Name');
				incidentName.focus();
				return false;
			}
		}
	}

	if(incidentType != null && incidentType != 'undefined')
	{
		if(incidentType.value == '' )
		{
			alert('Please enter the Incident Type');
			incidentType.focus();
			return false;
		}
	}

	var bCodeEntered = false ;
	if((fsCode != null && fsCode != 'undefined' && fsCode.value != '') )
	{
		bCodeEntered = true ;
	}
	if((otherCode != null && otherCode != 'undefined' && otherCode.value != '') )
	{
		bCodeEntered = true ;
	}
	if((blmCode != null && blmCode != 'undefined' && blmCode.value != '') )
	{
		bCodeEntered = true ;
	}
	if(bCodeEntered == false)
	{
		//alert("objOwnerAgency " + objOwnerAgency.value);
		//alert('At least One Account Code should be entered ');
		//fsCode.focus();
		//return false;
	}

	if(overrideCode.value != "")
	{
		if (fsCode.value == "")
		{
			alert('FS Account code Mandatory with Override Code ');
			fsCode.focus();
			return false;
		}		
	}
	
	if(objOwnerAgency.value == 'BLM') {
		//blmCode
		if(blmCode.value == ''){
			alert("BLM Code is NULL");
			return false;
		}

	}

	if(objOwnerAgency.value == 'FS') {
		//fsCode
		if(fsCode.value == '') {
			alert("FS Code is NULL");
			return false;
		}
	}
	
	if(objOwnerAgency.value == 'OTHER') {
		//otherCode
		if(otherCode.value == ''){
			alert("Other Code is NULL");
			return false;
		}
	}

	if(objOwnerAgency.value == 'BLM') {
		if (costCenter == null || FunctionalArea == null)
		{
			alert("Cost Center or Functional Area is Null !!!");
			return false;
		}
		
		if (costCenter.value == '' || FunctionalArea.value == '') 
		{
			alert("Cost Center or Functional Area is Empty !!!");
			return false;
		}
		
		var strCostCenter = costCenter.value;
		var strLL = strCostCenter.substring(0,2);
		
		if(strCostCenter != '') { 
			for (var i = 0; i < strCostCenter.length; i++)
	{
		if (specialChars.indexOf(strCostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center code.");
			return false;
		}
	}
			if((strLL != "LL") || (strCostCenter.length != 10))
			{
				alert("Enter a ten digit code for Cost Center that begins with LL");
				return false;
			}
		}
		
		if (FunctionalArea == null || FunctionalArea.value == '') {
			alert("Invalid Functional Area");
			return false;
		}
		
		var strFunctionalArea = FunctionalArea.value;
		var strSubStringFA = strFunctionalArea.substring(0,1);
		var strDotFA =strFunctionalArea.substring(9,10);
		if(strFunctionalArea != ''){ 
			for (var i = 0; i < strCostCenter.length; i++)
	{
		if (specialChars.indexOf(strCostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center code.");
			return false;
		}
	}
			if((strSubStringFA != "L") || (strDotFA != ".") || (strFunctionalArea.length != 16))
			{
				alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
				return false;
			}
		}		
		
		// Commenting WBS validation as it is not mandatory field now		
		/*if (WBS == null || WBS.value == '') {
			alert("WBS value is invalid.");
			return false;
		}*/
		
		var strWBS = WBS.value;
		
		
		if(strWBS != '') { 
			var strSubStringWBS = strWBS.substring(0,1);
			for (var i = 0; i < strWBS.length; i++)
			{
				if (specialChars.indexOf(strWBS.charAt(i)) != -1) 
				{
					alert ("Please do not enter special characters for WBS code.");
					return false;
				}
			}
			if((strSubStringWBS != "L") || (strWBS.length != 12)) {
					alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
					return false;
			}
		}						
	}// if objOwnerAgency is BLM
	
	return true;
}

function validateCustomer()
{
	var objCustomerId = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@PersonID");
	var objCustomerName = document.getElementById("xml:/Customer/Extn/@ExtnCustomerName");
	var objCustomerType = document.getElementById("xml:/Customer/Extn/@ExtnCustomerType");

	if(objCustomerType.value == "")
	{
		alert('Please enter the Customer Type');
		objCustomerType.focus();
		return false;
	}
	var objCustomerType = document.getElementById("xml:/Customer/Extn/@ExtnCustomerType");
	//details page will have this value as true
	objSelOption = eval(objCustomerType.options[objCustomerType.selectedIndex]);
	
	if(eval(objCustomerId) && objCustomerId.value == '')
	{
		//alert('customer ID: ' + objCustomerId.value);
		alert('Please enter the Customer ID/Unit ID');
		//objCustomerId.focus();
		return false;
	}

	if(eval(objCustomerName) && objCustomerName.value == '')
	{
		alert('Please enter the Customer Name');
		objCustomerName.focus();
		return false;
	}
	var objCtry = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@Country");
	var objState = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@State");
	var objCity = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@City");
	var objZipCode = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode");
	
	if(objSelOption.value == '01')// NWCG Customer
	{
		// these fields are mandatory for the nwcg customer as per cr # 315
		var objOrgType = document.getElementById("xml:/Customer/Extn/@ExtnUnitType");
		var objGACC = document.getElementById("xml:/Customer/Extn/@ExtnGACC");
		var objCustomerId = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@PersonID");
		/*var objJurType = document.getElementById("xml:/Customer/Extn/@ExtnJurisdictionType");
		var objJurID = document.getElementById("xml:/Customer/Extn/@ExtnJurisdictionID");
		var objJurName = document.getElementById("xml:/Customer/Extn/@ExtnJurisdictionName");
		*/
		var objDept = document.getElementById("xml:/Customer/Extn/@ExtnDepartment");
		var objAgency = document.getElementById("xml:/Customer/Extn/@ExtnAgency");
		
		if(eval(objDept) && objDept.value == '')
		{
			alert('Department is Mandatory for Customer Type NWCG');
			objDept.focus();
			return false ;
		}
		if(eval(objAgency) && objAgency.value == '')
		{
			alert('Agency is Mandatory for Customer Type NWCG');
			objAgency.focus();
			return false ;
		}
		if(eval(objCustomerType) && objCustomerType.value == '')
		{
			alert('Customer Type is Mandatory for Customer Type NWCG');
			objCustomerType.focus();
			return false ;
		}
		if(eval(objOrgType) && objOrgType.value == '')
		{
			alert('Unit Type is Mandatory for Customer Type NWCG');
			objOrgType.focus();
			return false ;
		}
		if(eval(objGACC) && objGACC.value == '')
		{
			alert('GACC is Mandatory for Customer Type NWCG');
			objGACC.focus();
			return false ;
		}
	}
	else if(objSelOption.value == '02')// Other Customer
	{
		// no checks 
	}
	if(eval(objCtry) && objCtry.value == '')
	{
		alert('Please Enter Country');
		objCtry.focus();
		return false ;
	}

/* removed for CR 637	 - ML
	if(eval(objState) && objState.value == '')
	{
		alert('Please enter State');
		objState.focus();
		return false ;
	} */
//End of CR 637

 
	if(eval(objCity) && objCity.value == '')
	{
		alert('Please enter City');
		objCity.focus();
		return false ;
	}
	if(eval(objZipCode) && objZipCode.value == '')
	{
		alert('Please enter Zip Code');
		objZipCode.focus();
		return false ;
	}
	
	enableFields();
	return true;
}

 function enableFields(elem){

   document.getElementById("xml:/Customer/Extn/@ExtnCustomerName").disabled = false;
   document.getElementById("xml:/Customer/Extn/@ExtnUnitType").disabled = false;
   document.getElementById("xml:/Customer/Extn/@ExtnDepartment").disabled = false;
   document.getElementById("xml:/Customer/Extn/@ExtnAgency").disabled = false;
   document.getElementById("xml:/Customer/Extn/@ExtnGACC").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine4").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine5").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine6").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@City").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@State").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@Country").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@FirstName").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@MiddleName").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@LastName").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@DayPhone").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@EveningPhone").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@MobilePhone").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@DayFaxNo").disabled = false;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@EmailID").disabled = false;
   document.getElementById("BusinessRadio").disabled = false;
   document.getElementById("ResidentialRadio").disabled = false;
 }


 function disableFieldsOnInactiveCustomer(elem){

   document.getElementById("xml:/Customer/Extn/@ExtnCustomerName").disabled = "true";
   document.getElementById("xml:/Customer/Extn/@ExtnUnitType").disabled = "true";
   document.getElementById("xml:/Customer/Extn/@ExtnDepartment").disabled = true;
   document.getElementById("xml:/Customer/Extn/@ExtnAgency").disabled = true;
   document.getElementById("xml:/Customer/Extn/@ExtnGACC").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine4").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine5").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine6").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@City").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@State").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@Country").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@FirstName").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@MiddleName").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@LastName").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@DayPhone").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@EveningPhone").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@MobilePhone").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@DayFaxNo").disabled = true;
   document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@EmailID").disabled = true;
   document.getElementById("BusinessRadio").disabled = true;
   document.getElementById("ResidentialRadio").disabled = true; 	 
 }

function copyComboBoxToTextField(elem)
{
	var objPrefixType = document.getElementById("CUSTOMER_PREFIX_DROPDOWN");
	var objSelOptionPrefix = eval(objPrefixType.options[objPrefixType.selectedIndex]);
	var objCustomerId = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@PersonID");
	objCustomerId.value = objSelOptionPrefix.value;
}

function markMandatoryFields(elem)
{
	var objCustomerType = document.getElementById("xml:/Customer/Extn/@ExtnCustomerType");
	var objPrefixType = document.getElementById("CUSTOMER_PREFIX_DROPDOWN");
	var objSelOption = eval(objCustomerType.options[objCustomerType.selectedIndex]);
	var objCustLabel = document.getElementById("CUSTOMER_LABEL");
	var objCustomerId = document.getElementById("xml:/Customer/Consumer/BillingPersonInfo/@PersonID");
	var objSelOptionPrefix = eval(objPrefixType.options[objPrefixType.selectedIndex]);
	var iCreateNewCustomer = document.getElementById("xml:/Customer/@iCreateNewCustomer");

	if(objSelOption.value == '01')
	{
		for (index = 1 ; index <= 5 ; index ++)
		{
			var objOrgType = document.getElementById("VALID_"+index);
			if(objOrgType != null)
				setMandatary(objOrgType);
		}
		
		if(objCustomerId != null)
		{
			hideField("CUSTOMER_PREFIX_DROPDOWN");
			showField("xml:/CUSTOMER_PREFIX_ID");
			objCustLabel.innerHTML = "Customer ID/Unit ID"  + "<font color='orange'>*</font>";
			if(iCreateNewCustomer.value != 'Y') //if it's Y, keep the CustomerID value
			{
				objCustomerId.value="";
			}
			objCustomerId.maxLength = 35 ;
			objCustomerId.size = 25 ;
		}
		return false;

	}
	else // remove the * 
	{
		for (index = 1 ; index <= 5 ; index ++)
		{
			var objOrgType = document.getElementById("VALID_"+index);
			if(objOrgType != null)
				unsetMandatary(objOrgType);
		}
		
		if(objCustomerId != null)
		{
			
			showField("CUSTOMER_PREFIX_DROPDOWN");
			hideField("xml:/CUSTOMER_PREFIX_ID");
			objCustLabel.innerHTML = "Customer/ Unit ID Prefix"  + "<font color='orange'>*</font>";
		
		}
		return false;
	}
}

function hideField(id){ 

	if (document.getElementById)
	{ 
		obj = document.getElementById(id); 
		obj.style.display = "none"; 
	} 
} 

function showField(id)
{ 
	if (document.getElementById(id))
	{ 
		obj = document.getElementById(id); 
		obj.style.display = ""; 
	} 
} 

function setMandatary(obj)
{
	if(eval(obj))
		obj.innerHTML = obj.innerHTML + "<font color='orange'>*</font>" ;
}

function unsetMandatary(obj)
{
	var str = new String(obj.innerHTML) ;
	str = str.toUpperCase();
	if(eval(obj) && str.indexOf("<FONT") != -1 )
		obj.innerHTML = obj.innerHTML.substring(0,str.indexOf("<FONT"));
}

function setRequestDeliverDate(elem)
{
	var rdd = document.getElementById("xml:/Order/@ReqDeliveryDate");
	if(eval(rdd))
	{
		rdd.value = elem.value ;
	}
}
function setDerivedIncident(elem,xmlDoc)
{
	nodes=xmlDoc.getElementsByTagName("NWCGIncidentOrder");

	var lastInc2 = document.getElementById("xml:/NWCGIncidentOrder/@ReplacedIncidentNo2");
	var lastInc2Year = document.getElementById("xml:/NWCGIncidentOrder/@ReplacedIncidentYear2");
	var strReplacedIncidentNo = "" ;
	var strReplacedIncidentYear = "" ;


	if(nodes!=null && nodes.length > 0 )
	{	
		var result = nodes(0);
		strReplacedIncidentNo = result.getAttribute("ReplacedIncidentNo");
		strReplacedIncidentYear = result.getAttribute("ReplacedIncidentYear");
		if(strReplacedIncidentNo == null || strReplacedIncidentNo == 'null' || strReplacedIncidentNo == 'undefined')
			strReplacedIncidentNo = "" ;
		if(strReplacedIncidentYear == null || strReplacedIncidentYear == 'null' || strReplacedIncidentYear == 'undefined')
			strReplacedIncidentYear = "" ;

	}
	
	lastInc2.value = strReplacedIncidentNo; 
	lastInc2Year.value = strReplacedIncidentYear; 
}

function setAdjustLocationParams(elemIncident)
{
	var returnArray = new Object();
	var elemYear = document.getElementById("xml:/AdjustLocationInventory/Audit/@Reference5");
	returnArray[elemYear.name] = elemYear.value;
	returnArray[elemIncident.name] = elemIncident.value;
	return returnArray;
}

function setIncidentParam(elem)
{
	var returnArray = new Object();
	var incidentNo = document.getElementById("xml:/NWCGIncidentOrder/@ReplacedIncidentNo");

	returnArray["xml:/Order/Extn/@ExtnIncidentNo"] = incidentNo.value;
	returnArray["xml:/Order/Extn/@ExtnIncidentYear"] = elem.value;
	return returnArray;
}

// validate override code length if and only if the fs account code is entered
function validateOverrideCodeLength(elem)
{
	var inputList = document.getElementsByTagName("input");
	
	for(index = 0 ; index < inputList.length ; index++)
	{
		var inputElem = inputList[index];
		if(inputElem.name.indexOf('FsAcctCode') != -1)
		{
			if(inputElem.value != "" && elem.value.length != 4)
			{
				alert('Override Code Should be of four Characters (Mandatory if FS Account Code is entered)');
				elem.focus();
				return false;
			}
		}
		
		if(inputElem.name.indexOf('SAOverrideCode') != -1)
		{
			if(inputElem.value != "" && elem.value.length != 4)
			{
				alert('Shipping Account Override Code Should be of two Characters');
				elem.focus();
				return false;
			}
		}
		if(inputElem.name.indexOf('RAOverrideCode') != -1)
		{
			if(inputElem.value != "" && elem.value.length != 4)
			{
				alert('Receiving Cache Account Code Should be of four Characters');
				elem.focus();
				return;
			}
		}
		if(inputElem.name.indexOf('OverrideCode') != -1)
		{
			if(inputElem.value != "" && elem.value.length != 4)
			{
				alert('Shipping Account Override Code Should be of four Characters');
				elem.focus();
				return;				
			}
		}		
	}
}

// validate FSoverride code length if and only if the fs account code is entered
function validateFSOverrideCodeLength(elem)
{
	var inputList = document.getElementsByTagName("input");
	
	for(index = 0 ; index < inputList.length ; index++)
	{
		var inputElem = inputList[index];
		if(inputElem.name.indexOf('xml:/AdjustLocationInventory/Audit/@OverrideCode') != -1)
		{
			if(inputElem.value != "" && elem.value.length != 4)
			{
				alert('Override Code Should be of four Characters (Mandatory if Account Code is entered)');
				elem.focus();
			}
		}
	}
}

function callIncidentLookup(sIncidentNo,sIncidentYear,entityname,extraParams)
{
	var oIncidentNo = document.all(sIncidentNo);
	var oIncidentYear = document.all(sIncidentYear);
	if(extraParams == null) {
		showIncidentLookupPopup(oIncidentNo, oIncidentYear,entityname);
	}
	else{
		showIncidentLookupPopup(oIncidentNo, oIncidentYear, entityname, extraParams);
	}
}

//------------------------------------------------------------------------------------------------------
//Function Type: Private
//------------------------------------------------------------------------------------------------------
function showIncidentLookupPopup(IncidentNoInput,IncidentYearInput,entityname, extraParams)
{
	var oObj = new Object();
	oObj.field1 = IncidentNoInput;
	oObj.field2 = IncidentYearInput;
	if(extraParams == null) {
		yfcShowSearchPopup('','NWCGIncidentLookup',900,550,oObj,entityname);
	}
	else{
		yfcShowSearchPopupWithParams('','NWCGIncidentLookup',900,550,oObj,entityname,extraParams);
	}
}

function setIncidentLookupValue(sIncidentNo,sIncidentYear)
 {
  var Obj = window.dialogArguments
  if(Obj != null)
  {
   Obj.field1.value = sIncidentNo;
   Obj.field2.value = sIncidentYear;
  }
  window.close();
 }

function setOtherLookupValue(sIncidentNo)
 {
  var Obj = window.dialogArguments

	
  if(Obj != null)
  {
   Obj.field1.value = sIncidentNo;

  }
  window.close();
 } 

function setPercentValue(elem,percent,nextamt,nextpercent,totamt,runtotal)
{
   var amt = elem.value;
   var total,runtotalval;

   setCurrentTotal();

   total = parseFloat(totamt);
   amt = parseFloat(elem.value);

   if(isNaN(amt))
	{
	  amt = 0;
	  elem.value = 0.00;
	}

   if (amt == 0)
   {
	   alert("Amount Entered is 0 !!!");
	   return false;
   }

   if (amt > total)
   {
	   alert("Amount Greater Than Total Amount !!!");
	   return false;
   }

   runtotalval = document.getElementById(runtotal).value;
   var percent1=(amt/total)*100.00;
   percent1=Math.round(percent1*100.00)/100.00;
   document.getElementById(percent).value=percent1+'%';
   var nextamtval=document.getElementById(nextamt).value;

   if(isNaN(nextamtval))
	{
	  nextamtval = 0.00;
	}

   var diff=total-runtotalval;
   document.getElementById(runtotal).value=diff;

   if (diff > 0)
   {
	 document.getElementById(nextamt).value=parseFloat(diff)+parseFloat(nextamtval);
	 var percent2=((parseFloat(diff)+parseFloat(nextamtval))/total)*100.00;
	 percent2=Math.round(percent2*100.00)/100.00;
	 document.getElementById(nextpercent).value=percent2+'%';
   }

    setCurrentTotal();
    return true;
}


function checkOverrideCode(overridecode)
{
	var overrideval = document.getElementById(overridecode).value;
	var overridelength = document.getElementById(overridecode).value.length;

	if (overridelength != 4)
	{
		alert("FS Override Code is Mandatory and should have minimum FOUR Characters !!!");
		return false;
	} 
	 return true;
}

function checkAccountCode(accountcode)
{
	var acctcodeval = document.getElementById(accountcode).value;
	var acctcodelength = document.getElementById(accountcode).value.length;

	if (acctcodelength == 0)
	{
		alert("Account Code is Mandatory for Each Split Amount !!!");
		return false;
	} 
	 return true;
}

function checkAmt()
{
	var amt1 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount1").value;
	var amt2 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount2").value;
	var amt3 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount3").value;
	var amt4 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount4").value;
	var amt5 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount5").value;
	var gtot = document.getElementById("xml:/Extn/@GrandTotal").value;
	
	var tot_amt = 0;

	var amt1val = parseFloat(amt1);
	var amt2val = parseFloat(amt2);
	var amt3val = parseFloat(amt3);
	var amt4val = parseFloat(amt4);
	var amt5val = parseFloat(amt5);

    if(isNaN(amt1val))
	{
	  amt1val = 0;
	}

    if(isNaN(amt2val))
	{
	  amt2val = 0;
	}

	if(isNaN(amt3val))
	{
	  amt3val = 0;
	}

	if(isNaN(amt4val))
	{
	  amt4val = 0;
	}

	if(isNaN(amt5val))
	{
	  amt5val = 0;
	}

    tot_amt = amt1val+amt2val+amt3val+amt4val+amt5val;

    if (gtot != tot_amt)
	{
		alert("The Sum of Split Amounts is not equal to Issue Total Amount!!!");
		return false;
    }  

   if (!checkAccountCodes())
    {	       
    	return false; 
    }
 

   if (!checkOverrideCodes())
   {	       
	return false; 
   }
    enterModificationReason('YMRD001','xml:/Order/@ModificationReasonCode','xml:/Order/@ModificationReasonText','xml:/Order/@Override');

	return true;
}

function checkAccountCodes()
{
      var amt1 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount1").value;
	  var amt2 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount2").value;
	  var amt3 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount3").value;
	  var amt4 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount4").value;
	  var amt5 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount5").value;
	  var OwnerAgency = document.getElementById("xml:/Extn/@OwnerAgency").value;

	  if (amt1 > 0)
	  {
		 if (!checkAccountCode('xml:/Order/Extn/@ExtnAcctCode1'))
		 {
			 return false;
		 }
	  }

	  if (amt2 > 0)
	  {
	  	if(OwnerAgency == 'BLM'){
			 if (!checkCostCenter2splitcodes())
			 {
				 return false;
			 }
			 if (!checkFA2splitcodes())
			 {
				 return false;
			 }
			 if (!checkWBS2splitcodes())
			 {
				 return false;
			 }
		 }
		 if (!checkAccountCode('xml:/Order/Extn/@ExtnAcctCode2'))
		 {
			 return false;
		 }
	  }

	  if (amt3 > 0)
	  {
		 if(OwnerAgency == 'BLM'){
			 if (!checkCostCenter3splitcodes())
			 {
				 return false;
			 }
			 if (!checkFA3splitcodes())
			 {
				 return false;
			 }
			 if (!checkWBS3splitcodes())
			 {
				 return false;
			 }
		 }
		 if (!checkAccountCode('xml:/Order/Extn/@ExtnAcctCode3'))
		 {
			 return false;
		 }
	  }

	  if (amt4 > 0)
	  {
		 if(OwnerAgency == 'BLM'){
			 if (!checkCostCenter4splitcodes())
			 {
				 return false;
			 }
			 if (!checkFA4splitcodes())
			 {
				 return false;
			 }
			 if (!checkWBS4splitcodes())
			 {
				 return false;
			 }
		 }
		 if (!checkAccountCode('xml:/Order/Extn/@ExtnAcctCode4'))
		 {
			 return false;
		 }
	  }

	  if (amt5 > 0)
	  {
		 if(OwnerAgency == 'BLM'){
			 if (!checkCostCenter5splitcodes())
			 {
				 return false;
			 }
			 if (!checkFA5splitcodes())
			 {
				 return false;
			 }
			 if (!checkWBS5splitcodes())
			 {
				 return false;
			 }
		 }
		 if (!checkAccountCode('xml:/Order/Extn/@ExtnAcctCode5'))
		 {
			 return false;
		 }
	  }     
	 return true;
}

function checkOverrideCodes()
{
	var OwnerAgency = document.getElementById("xml:/Extn/@OwnerAgency").value;
	if (OwnerAgency == 'FS')
	{
      var amt1 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount1").value;
	  var amt2 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount2").value;
	  var amt3 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount3").value;
	  var amt4 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount4").value;
	  var amt5 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount5").value;

	  if (amt1 > 0)
	  {
		 if (!checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode1'))
		 {
			 return false;
		 }
	  }

	  if (amt2 > 0)
	  {
		 if (!checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode2'))
		 {
			 return false;
		 }
	  }

	  if (amt3 > 0)
	  {
		 if (!checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode3'))
		 {
			 return false;
		 }
	  }

	  if (amt4 > 0)
	  {
		 if (!checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode4'))
		 {
			 alert("Failure");
			 return false;
		 }
	  }

	  if (amt5 > 0)
	  {
		 if (!checkOverrideCode('xml:/Order/Extn/@ExtnOverrideCode5'))
		 {
			 return false;
		 }
	  }
     
    }
	return true;
}

function setCurrentTotal()
{
	var amt1 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount1").value;
	var amt2 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount2").value;
	var amt3 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount3").value;
	var amt4 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount4").value;
	var amt5 = document.getElementById("xml:/Order/Extn/@ExtnSplitAmount5").value;
	var gtot = document.getElementById("xml:/Extn/@GrandTotal").value;
	var currenttotalval = document.getElementById("xml:/Extn/@CurrentTotal").value;
	
	var tot_amt = 0;

	var amt1val = parseFloat(amt1);
	var amt2val = parseFloat(amt2);
	var amt3val = parseFloat(amt3);
	var amt4val = parseFloat(amt4);
	var amt5val = parseFloat(amt5);

    if(isNaN(amt1val))
	{
	  amt1val = 0;
	}

    if(isNaN(amt2val))
	{
	  amt2val = 0;
	}

	if(isNaN(amt3val))
	{
	  amt3val = 0;
	}

	if(isNaN(amt4val))
	{
	  amt4val = 0;
	}

	if(isNaN(amt5val))
	{
	  amt5val = 0;
	}

    tot_amt = amt1val+amt2val+amt3val+amt4val+amt5val;
	tot_amt=Math.round(tot_amt*100.00)/100.00;
	document.getElementById("xml:/Extn/@CurrentTotal").value = '$ '+tot_amt;
    document.getElementById("xml:/Extn/@RunTotal").value = tot_amt;

	return true;
 }

 function set_extncodes()
 {
   var DocType = document.getElementById("xml:/Order/@DocumentType").value;
   if (DocType != '0001')
   {
    var OwnerAgency = document.getElementById("xml:/Extn/@OwnerAgency").value;
    if(OwnerAgency == 'BLM')
     {
 	  var BLMCode = document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value;
	  document.getElementById("xml:/Order/Extn/@ExtnAcctCode1").value = BLMCode;
	 }
     else if(OwnerAgency == 'FS')
     {
 	  var FSCode = document.getElementById("xml:/Order/Extn/@ExtnFsAcctCode").value;
	  document.getElementById("xml:/Order/Extn/@ExtnAcctCode1").value = FSCode;
	  var FSOverrideCode = document.getElementById("xml:/Order/Extn/@ExtnOverrideCode").value;
	  document.getElementById("xml:/Order/Extn/@ExtnOverrideCode1").value = FSOverrideCode;
	 }
	 else
	 {
	   var OTHCode = document.getElementById("xml:/Order/Extn/@ExtnOtherAcctCode").value;
	   document.getElementById("xml:/Order/Extn/@ExtnAcctCode1").value = OTHCode;
	 }
   }
   return true;
 }

 function checkBillingExtractFields()
{
	var PrimaryEnterpriseKey =  document.getElementById("xml:/NWCGBillingTransaction/@PrimaryEnterpriseKey").value;
	var ToDate = document.getElementById("xml:/NWCGBillingTransaction/@ToTransDate").value;
	var FileName = document.getElementById("xml:/NWCGBillingTransaction/@BillingTransFileName").value;
	var OwnerAgency = document.getElementById("xml:/NWCGBillingTransaction/@OwnerAgency").value;
	var DocumentNo =  document.getElementById("xml:/NWCGBillingTransaction/@DocumentNo").value;
	var FiscalYear =  document.getElementById("xml:/NWCGBillingTransaction/@FiscalYear").value;
	var OffsetAcctCode =  document.getElementById("xml:/NWCGBillingTransaction/@OffsetAcctCode").value;

	if (ToDate.length == 0)
	{
      alert("To Date is Mandatory for the Extract !!!");
	  return false;
	}

	if (PrimaryEnterpriseKey.length == 0)
	{
      alert("PrimaryEnterpriseKey is not setup correctly for this Organization !!!");
	  return false;
	}
	
	if (FileName.length == 0)
	{
      alert("Billing Transaction Extract File Name is not setup correctly for this Organization !!!");
	  return false;
	}

	if (OwnerAgency.length == 0)
	{
      alert("OwnerAgency is not setup correctly for this Organization !!!");
	  return false;
	}

	if (DocumentNo.length == 0)
	{
      alert("DocumentNo is not setup correctly for this Organization !!!");
	  return false;
	}

	if (FiscalYear.length == 0)
	{
      alert("FiscalYear is not setup correctly for this Organization !!!");
	  return false;
	}

	if (OffsetAcctCode.length < 28)
	{
      alert("Final OffSet Account Code is not setup correctly for this Organization !!!");
	  return false;
	}
	//CR1051 - Stores Extract - time out problem
	alert("Thank you for submitting the Billing Extract request. We will process the file over the next 2 hours !");
	 return true;
}

function callTrackableIDLookup(elem,sSerialList,
sSerialID,sSecSerialID,sTagAttribute1,sTagAttribute2,sTagAttribute3,sTagAttribute4,sTagAttribute5,sTagAttribute6,sManufacturingDate, entityname,extraParams)
{
	var oSerialList = document.all(sSerialList);
	var oSerialID = document.all(sSerialID);
	var oSecSerialID = document.all(sSecSerialID);
	var oTagAttribute1 = document.all(sTagAttribute1);
	var oTagAttribute2 = document.all(sTagAttribute2);
	var oTagAttribute3 = document.all(sTagAttribute3);
	var oTagAttribute4 = document.all(sTagAttribute4);
	var oTagAttribute5 = document.all(sTagAttribute5);
	var oTagAttribute6 = document.all(sTagAttribute6);
	//CR 693 BEGIN - ML
	var oManufacturingDate = document.all(sManufacturingDate);
	//CR 693 END - ML
	var currentRow = elem.parentElement.parentElement.parentElement;
	//alert("displaying current row:-"+currentRow.innerHTML);
	var InputList = currentRow.getElementsByTagName("input");

    if(InputList[0].name.indexOf('@serialID') != -1)
		{
			 CSerialNo = InputList[0].value;
		}//end if
		if(InputList[0].name.indexOf('@PrimarySerialNo') != -1)
		{
			 CSerialNo = InputList[0].value;
		}//end if

       //alert("CSerial "+CSerialNo);

	var CSerialList = document.getElementById("xml:/Receipt/@SerialList").value;
	//alert("CSerial List "+CSerialList);

       extraParams=extraParams+'&xml:/Serial/@CurrentSerialNo='+CSerialNo+'&xml:/Serial/@CurrentSerialList='+CSerialList;
	//alert("ExtraParams "+ extraParams);

	var oObj = new Object();
	oObj.field1 = oSerialList;
	oObj.field2 = oSerialID;
	oObj.field3 = oSecSerialID;
	oObj.field4 = oTagAttribute1;
	oObj.field5 = oTagAttribute2;
    oObj.field6 = oTagAttribute3;
	oObj.field7 = oTagAttribute4;
	oObj.field8 = oTagAttribute5;
	oObj.field9 = oTagAttribute6;
	oObj.field10 = oManufacturingDate

    yfcShowListPopupWithParams('','NWCGTrackableIDLookup',900,550,oObj,entityname,extraParams);
}

function setTrackableIDLookupValue(sSerialList,
sSerialID,sSecSerialID,sTagAttribute1,sTagAttribute2,sTagAttribute3,sTagAttribute4,sTagAttribute5,sTagAttribute6,sManufacturingDate)
 {
  var Obj = window.dialogArguments
  if(Obj != null)
  {
   Obj.field1.value = sSerialID + ',' + sSerialList;
      
   Obj.field2.value = sSerialID;

   if (Obj.field3 != null)
   {
	   Obj.field3.value = sSecSerialID;
   }

   if (Obj.field4 != null)
   {
	   Obj.field4.value = sTagAttribute1;
   }

   if (Obj.field5 != null)
   {
	   Obj.field5.value = sTagAttribute2;
   }

   if (Obj.field6 != null)
   {
	   Obj.field6.value = sTagAttribute3;
   }
      
   if (Obj.field7 != null)
   {
	   Obj.field7.value = sTagAttribute4;
   }

   if (Obj.field8 != null)
   {
	   Obj.field8.value = sTagAttribute5;
   }

   if (Obj.field9 != null)
   {
	   Obj.field9.value = sTagAttribute6;
   }
    if (Obj.field10 != null)
   {
	   Obj.field10.value = sManufacturingDate;
   }
  }
  window.close();
 }

 function setRange(inputEl,selStart,selEnd) 
{  
  if (inputEl.setSelectionRange) 
  {   inputEl.focus();   
      inputEl.setSelectionRange(selStart,selEnd); 
  } 
  else if (inputEl.createTextRange) 
  {   var range = inputEl.createTextRange();   
      range.collapse(true);   
	  range.moveEnd('character', selEnd);   
	  range.moveStart('character', selStart);   
	  range.select(); 
   } 
}

function maxLength(field,maxChars)
 {
       if(field.value.length >= maxChars) {
          event.returnValue=false;
		  alert("Max Characters is "+maxChars);
          return false;
       }
 }
 
 function SetToDate(){
	var ToDate = document.getElementById("xml:/NWCGBillingTransaction/@ToTransDate").value;
	if(ToDate == null || ToDate == 'undefined' || ToDate.length == 0){
		alert('To Date is Mandatory: Please Enter To Date');
	}
	return true;
 }	  

function maxLengthPaste(field,maxChars)
{
	event.returnValue=false;
	if((field.value.length +  window.clipboardData.getData("Text").length) > maxChars) {
	  alert("Pasting More than " +maxChars + " Characters");
	  return false;
	}
	event.returnValue=true;
 }

function checkCustomerName (elem, xmlDoc)
{
	nodes=xmlDoc.getElementsByTagName("Customer");

	if (nodes.length != 0)
	{
		alert("This customer name already exists.");
		elem.focus();
	}
 }
	 
function updateCustDetailsForIssueEntry(elem,xmlDoc){
	
	var objCustomer = xmlDoc.getElementsByTagName("Customer");
	var objExtn = xmlDoc.getElementsByTagName("Extn");

	//alert(elem.name);
	//alert(elem.value);

	if (!objCustomer[0])
	{
		var answer = confirm('The entered Customer ID is invalid. Do you want to create one?');
		if (answer)
		{
			var iparams = "xml:/Customer/Consumer/BillingPersonInfo/@PersonID=" + elem.value;
			iparams += "&xml:/newCustomer/@iCreateNewCustomer=Y";
			yfcShowDetailPopupWithParams('NWCGYOMD030',"",1030,650,iparams,'NWCGCustomer',"",1);
			// populate the customer details here
			populateCustomerInfo(elem,objExtn,"xml:/NWCGIncidentOrder/@");
			nodes = "" ;
			return;
		} else { // answered No here
			nodes = "" ;
			return;
		}
	}

	if(objExtn[0].getAttribute("ExtnActiveFlag") == 'N' )
	{
		alert('Please enter an Active Customer');
		elem.focus();
		return false;
	}
	
	if(elem.name == "xml:/Order/@BillToID")
	{
		populateCustomerInfo(elem,objExtn,"xml:/Order/Extn/@Extn");
		updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","BillingPersonInfo");
	}
	else
	{
		populateCustomerInfo(elem,objExtn,"xml:/NWCGIncidentOrder/@");
	}
}

function updateCustDetailsForCreditPayment(elem,xmlDoc){

	var objBillingInfo = xmlDoc.getElementsByTagName("BillingPersonInfo");
	var objExtn = xmlDoc.getElementsByTagName("Extn");
	var sExtnPath = "xml:/Customer/Extn/@";
	var sCustPath = "xml:/Customer/Consumer/BillingPersonInfo/@";
	
	if (!objBillingInfo[0])
	{
		alert('Customer ID not found!');
		return;
	}
	
	var objExtnV = objExtn[0];
	var objCustV = objBillingInfo[0];
	
	var sCompany = objCustV.getAttribute("Company");
	
	document.getElementById("radioBIndividual").checked = false;
	document.getElementById("radioBOrganization").checked = false;
		
	if( sCompany == null || sCompany == '' )
	{
		sCompany = 'Individual';
		
	}
	else
	{
		sCompany = 'Organization';
	}
	
	document.getElementById("transactionType").value = sCompany;
	
	var inputList = document.getElementsByTagName("input");

	for (index = 0 ; index < inputList .length ; index++)
	{
		var currentElem = inputList[index];

		if(currentElem.id == "billCustomerName")
		{
			currentElem.innerText = checkStringForNull(objExtnV.getAttribute("ExtnCustomerName"));
			currentElem.value = checkStringForNull(objExtnV.getAttribute("ExtnCustomerName"));	
		}
		
		if(currentElem.id == "billFirstName")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("FirstName"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("FirstName"));	
		}
		if(currentElem.id == "billLastName")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("LastName"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("LastName"));	
		}
		if(currentElem.id == "billAddress1")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("AddressLine1"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("AddressLine1"));	
		}
		if(currentElem.id == "billAddress2")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("AddressLine2"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("AddressLine2"));	
		}
		if(currentElem.id == "billAddress3")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("AddressLine3"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("AddressLine3"));	
		}
		if(currentElem.id == "billCity")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("City"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("City"));	
		}
		if(currentElem.id == "billState")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("State"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("State"));	
		}
		if(currentElem.id == "billZipCode")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("ZipCode"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("ZipCode"));	
		}
		if(currentElem.id == "billCountry")
		{
			currentElem.innerText = checkStringForNull(objCustV.getAttribute("Country"));
			currentElem.value = checkStringForNull(objCustV.getAttribute("Country"));	
		}
	}

}


function showItemPrintPopUp(sViewID, entity, sScreenType,key){	
    var myObject = new Object();
	var eKey=key;
	myObject.currentWindow = window;	
	if("Detail"==sScreenType){
		yfcShowDetailPopup(sViewID,"",450, 400," ",entity,document.all("PrintEntityKey").value);
	}else{
		if(!key){
			eKey="EntityKey";
		}
		if(yfcAllowSingleSelection(eKey)){
			var eleArray = document.forms["containerform"].elements;
			var sPrintEntityKey;

			for ( var i = 0; i < eleArray.length; i++ ) {
				if ( eleArray[i].name == eKey ) {
					if (eleArray[i].checked) {
						var sPrintEntityKey = eleArray[i].getAttribute("PrintEntityKey");		
					}
				}
			}
			yfcShowDetailPopup(sViewID,"",450, 400," ",entity,sPrintEntityKey);
		}else{
			return false;
		}
	}
	var retVal = myObject["EMReturnValue"];	
	var returnValue = myObject["OKClicked"];
	if ( "YES" == returnValue ) 
	{
		window.document.documentElement.setAttribute("OKClicked", "false");	
		return (retVal);
	}else
	{
		window.document.documentElement.setAttribute("OKClicked", "false");
		return (false);
	}	
}

// Supplier Name 2.1 enchancement  - KS -
function updateSupplierName(elem,xmlDoc){
	
	var objOrg = xmlDoc.getElementsByTagName("Organization");
	
	if (!objOrg[0])
	{
		alert('Please enter an valid Supplier ID');

		elem.focus();
		return false;
	}
	
	var name = objOrg[0].getAttribute("OrganizationName");
	document.all("xml:/NWCGSupplierItem/@OrganizationName").value = name;

}
// END Supplier Name 2.1 enchancement  - KS -

//	Shipping Account Code formatting  2.1 enchancement  - KS -
function formatShippingAccountCode(elem,setValue,ShipFundCode,object){
   var val = elem.value;
	  // alert("value for shipping code: " + val);
   
   //var blmField=document.getElementById("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode");
   if(val==null||val==''){return val};

   //check if formatting has already been done
   //we check if any "-" are present in the value if present we return
   if(val.indexOf('-')!=-1){
    return val;
   }
	// Extract relevant sections
	
   state = val.substring(0,2);//1st-2nd position  = State

   if (state.length != 2)
   {
	   alert("State code is too short");
	   	   elem.focus();
		   return;
   }
   office = val.substring(2,5);//3rd-5th = Office
      if (office.length != 3)
   {
	   alert("Office code is too short");
	   elem.focus();
	   return;
   }
   //ShipFundCode = val.substring(5,8);//6th-8th = Fund code
      if (ShipFundCode.length != 3)
   {
	   alert("Fund code is too short");
	   elem.focus();
	   return;
   }

   activity = val.substring(5,9);//9th-12th= Activity (numeric)  * if activity= 5700, then no project
   if (activity.length != 4)
   {
	   alert("Activity code is too short");
	   elem.focus(); 
	   return;
   }
   programelem = val.substring(9,11);//13th-14th= Program element
      if (programelem.length != 2)
   {
	   alert("Program element code is too short");
	   elem.focus(); 
	   return;
   }
   project = val.substring(11,15);//15th-18th= Project
      if (project.length != 4)
   {
	   project = "    ";
	   //alert("Project code is too short");
	   //elem.focus();
	   //return;
   }
   //object = '264B' ;
   //object = val.substring(14,18);//19th-22nd= Object class
      if (object.length != 4)
   {
	   alert("Object class code too short");
	   elem.focus(); 
	   return;
   }
   formattedCode = state+"-"+office+"-"+ShipFundCode+"-"+activity+"-"+programelem+"-"+project+"-"+object;
   
   //alert("formmatted date: " +formattedCode);
   
   if(setValue){
	   elem.value = formattedCode;
   }
   
   return formattedCode;
}
//	END Shipping Account Code formatting  2.1 enchancement  - KS -

// CR 352 - helper function to convert input string to Upper case
function makeUppercase(lStr)
{
	//alert("lConvert=["+lStr.value+"]");
	if(lStr != '') {
		var tStr = lStr.value;
		lStr.value = tStr.toUpperCase();
	}
	return lStr;
}

//cr 417
function validatePO()
{
	var objReceivingCache = document.getElementById("xml:/Order/@ReceivingNode");
	//alert(objReceivingCache.value);
	
	if(objReceivingCache.value == "")
	{
		alert("Please enter the Receiving Cache");
		objReceivingCache.focus();
		return false;
	}
	return true;
	
}

//for CR 443 ks
function checkIssuedQuantity(issueQty)
{
	var strIssueQty 	= issueQty.value;
	var currentRow 		= issueQty.parentNode.parentNode;
	var InputList 	= currentRow.getElementsByTagName("Input");
	var strReqQty = '';
	var strAvailRFIQty = '';
    var strMaxLineStatusDesc = ' ';
	
	for(index = 0 ; index < InputList.length ; index++)
	{
		var elem = InputList[index];
		if(elem.name.indexOf('@ExtnQtyRfi') != -1)
		{
			strAvailRFIQty = elem.value;			
		}
		else if (elem.name.indexOf('@ExtnOrigReqQty') != -1) {
			strReqQty = elem.value;
		}
	if(elem.name.indexOf('@MaxLineStatusDesc') != -1)
		{
			strMaxLineStatusDesc = elem.value;
		}		
		//if (strAvailRFIQty != '' && strReqQty != null)
			//break;
	}
		
if((strAvailRFIQty*1) < (strIssueQty*1)) //condition to make sure Issued Qty does not exceed Available RFI
	{
              //alert ('Status Check');
          if ((strMaxLineStatusDesc == "Shipped") || (strMaxLineStatusDesc == "Partially Shipped"))
            {
             //alert ('Status : ' + strMaxLineStatusDesc);
              return true;
            }
            else
            {
		alert('Issue Qty can not exceed Available RFI Qty');
		issueQty.focus();
		return false;
            }
	}

	//Commented By Gaurav
	/*if (strReqQty*1 > 0) {
		if((strReqQty*1) < (strIssueQty*1) )
		{
			alert('Issue Qty can not exceed Requested Qty');
			issueQty.focus();
			return false;
		}
	}*/
	return true;
}

function openAlertLink(linkPrefix, keyElementName, keyAttributeName, keyAttributeValue) 
{
    var entityKeyToPass = encodeURIComponent("<" + keyElementName + " " + keyAttributeName + "=\"" + keyAttributeValue + "\"/>");
    var urlToLink = "/smcfs/console/" + linkPrefix + "?EntityKey=" + entityKeyToPass;
    window.location = urlToLink;
}	
//for FBMS validation

function callOrderLookup(sIncidentNo,sIncidentYear,sDocumentType,entityname,extraParams)
{
	var oIncidentNo = document.all(sIncidentNo);
	var oIncidentYear = document.all(sIncidentYear);
	
	//if(sDocumentType != null)
	//(
		var extraParams='xml:/Order/@DocumentType='+sDocumentType;
	//}
	
	if(extraParams == null) {
		showOrderLookupPopup(oIncidentNo, oIncidentYear, entityname);
	}
	else{
		showOrderLookupPopup(oIncidentNo, oIncidentYear, entityname, extraParams);
	}
	
}
	
function showOrderLookupPopup(IncidentNoInput,IncidentYearInput, entityname, extraParams)
{
	var oObj = new Object();
	oObj.field1 = IncidentNoInput;
	oObj.field2 = IncidentYearInput;
	if(extraParams == null) {
		yfcShowSearchPopup('','NWCGIncidentLookup',900,550,oObj,entityname);
	}
	else{
		yfcShowSearchPopupWithParams('','NWCGIncidentLookup',900,550,oObj,entityname,extraParams);
	}
}
	
//for FBMS validation
function checkCostCenter(elem)
{
	var strCostCenter = elem.value;
	var objCostCenter = document.getElementById("xml:/NWCGIncidentOrder/@CostCenter");
	var objOwnerAgency = document.getElementById("xml:/NWCGIncidentOrder/@OwnerAgency");
	var objFunctionalArea = document.getElementById("xml:/NWCGIncidentOrder/@FunctionalArea").value;
	var objWBS = document.getElementById("xml:/NWCGIncidentOrder/@WBS").value;

	//alert(" its here in checkCostCenter");
	//alert(objOwnerAgency.value);

	if(strCostCenter == ""){

		if(objOwnerAgency.value != 'BLM'){
		return true;
		}

	}
	for (var i = 0; i < strCostCenter.length; i++)
	{
		if (specialChars.indexOf(strCostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center code.");
			return false;
		}
	}
	
	var strLL = strCostCenter.substring(0,2);
	if((strLL != "LL") || (strCostCenter.length != 10))
	{
		alert("Enter a ten digit code for Cost Center that begins with LL");
		return false;
	}

	var formattedBLMCode =  "";
	//formattedBLMCode = strCostCenter+"."+objWBS+"."+objFunctionalArea;
	formattedBLMCode = strCostCenter+"."+objFunctionalArea+"."+objWBS;
	document.all("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode").value = formattedBLMCode;	
}
function checkWBS(elem)
{
	var strWBS = elem.value;
	var objFunctionalArea = document.getElementById("xml:/NWCGIncidentOrder/@FunctionalArea").value;
	var objWBS = document.getElementById("xml:/NWCGIncidentOrder/@WBS").value;
	var objCostCenter = document.getElementById("xml:/NWCGIncidentOrder/@CostCenter").value;
	var objWBSelement = document.getElementById("xml:/NWCGIncidentOrder/@WBS");
	var strSubStringWBS = strWBS.substring(0,1);
	var formattedBLMCode =  "";

if(strWBS == ""){

		//if(objOwnerAgency.value != 'BLM'){
			formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+objWBS;
	document.all("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode").value = formattedBLMCode;
		return true;
		//}

	}
	for (var i = 0; i < strWBS.length; i++)
	{
		if (specialChars.indexOf(strWBS.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for WBS code.");
			return false;
		}
	}
	
	if((strSubStringWBS != "L") || (strWBS.length != 12))
	{
		alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
		return false;
	}
	
	
	//formattedBLMCode = objCostCenter+"."+objWBS+"."+objFunctionalArea;
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+objWBS;
	document.all("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode").value = formattedBLMCode;
	
}

function checkFunctionalArea(elem)
{
	var objFunctionalArea = document.getElementById("xml:/NWCGIncidentOrder/@FunctionalArea");
	var strFunctionalArea = elem.value;
	var strSubStringFA = strFunctionalArea.substring(0,1);
	var strDotFA =strFunctionalArea.substring(9,10);
	var objOwnerAgency = document.getElementById("xml:/NWCGIncidentOrder/@OwnerAgency");
	var objWBS = document.getElementById("xml:/NWCGIncidentOrder/@WBS").value;
	var objCostCenter = document.getElementById("xml:/NWCGIncidentOrder/@CostCenter").value;

	if(strFunctionalArea == ""){

		if(objOwnerAgency.value != 'BLM'){
		return true;
		}
	}	
	for (var i = 0; i < strFunctionalArea.length; i++)
	{
		if (specialChars.indexOf(strFunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && strFunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area code.");
				return false;
			}
		}
	}
	if((strSubStringFA != "L") || (strDotFA != ".") || (strFunctionalArea.length != 16))
	{
		alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
		return false;
	}

	var formattedBLMCode =  "";
	//formattedBLMCode = objCostCenter+"."+objWBS+"."+strFunctionalArea;
	formattedBLMCode = objCostCenter+"."+strFunctionalArea+"."+objWBS;
	document.all("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode").value = formattedBLMCode;	
}

function checkCostCenter2splitcodes()
{
	var CostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter2").value;
	var strCostCenterLength = CostCenter.substring(0,2);
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter2");
		
	for (var i = 0; i < CostCenter.length; i++)
	{
		if (specialChars.indexOf(CostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center2 code.");
			//objCostCenter.focus();
			return false;
		}
	}
	if((strCostCenterLength != "LL") || (CostCenter.length < 10))
	{
		alert('Enter a ten digit value for Cost Center2. Must begin with LL');
		//objCostCenter.focus();
		return false;
	}
	return true;
	
}

function checkCostCenter3splitcodes()
{
	var CostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter3").value;
	var strCostCenterLength = CostCenter.substring(0,2);
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter3");
		
	for (var i = 0; i < CostCenter.length; i++)
	{
		if (specialChars.indexOf(CostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center3 code.");
			//objCostCenter.focus();
			return false;
		}
	}
	if((strCostCenterLength != "LL") || (CostCenter.length < 10))
	{
		alert('Enter a ten digit value for Cost Center3. Must begin with LL');
		//objCostCenter.focus();
		return false;
	}
	return true;
}

function checkCostCenter4splitcodes()
{
	var CostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter4").value;
	var strCostCenterLength = CostCenter.substring(0,2);
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter4");
		
	for (var i = 0; i < CostCenter.length; i++)
	{
		if (specialChars.indexOf(CostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center4 code.");
			//objCostCenter.focus();
			return false;
		}
	}
	if((strCostCenterLength != "LL") || (CostCenter.length < 10))
	{
		alert('Enter a ten digit value for Cost Center4. Must begin with LL');
		//objCostCenter.focus();
		return false;
	}
	return true;
}

function checkCostCenter5splitcodes()
{
	var CostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter5").value;
	var strCostCenterLength = CostCenter.substring(0,2);
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter5");
		
	for (var i = 0; i < CostCenter.length; i++)
	{
		if (specialChars.indexOf(CostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center5 code.");
			//objCostCenter.focus();
			return false;
		}
	}
	if((strCostCenterLength != "LL") || (CostCenter.length < 10))
	{
		alert('Enter a ten digit value for Cost Center5. Must begin with LL');
		//objCostCenter.focus();
		return false;
	}
	return true;
}

function checkFA2splitcodes()
{
	var FunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA2").value;
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter2").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA2");
	var strFunctionalAreaLength = FunctionalArea.substring(0,1);
	var strFAdotPosition = FunctionalArea.substring(9,10);
	for (var i = 0; i < FunctionalArea.length; i++)
	{
		if (specialChars.indexOf(FunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && FunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area2 code.");
				//objFunctionalArea.focus();
				return false;
			}
		}
	}
	if((strFunctionalAreaLength != "L") || (strFAdotPosition != ".") || (FunctionalArea.length < 16))
	{
		alert("Enter a sixteen digit value for Functional Area2 that begins with 'L' and has a '.' in the tenth position.");
		//objFunctionalArea.focus();
		return false;
	}
	return true;
}

function checkFA3splitcodes()
{
	var FunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA3").value;
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter3").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA3");
	var strFunctionalAreaLength = FunctionalArea.substring(0,1);
	var strFAdotPosition = FunctionalArea.substring(9,10);
	for (var i = 0; i < FunctionalArea.length; i++)
	{
		if (specialChars.indexOf(FunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && FunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area3 code.");
				//objFunctionalArea.focus();
				return false;
			}
		}
	}	
	if((strFunctionalAreaLength != "L") || (strFAdotPosition != ".") || (FunctionalArea.length < 16))
	{
		alert("Enter a sixteen digit value for Functional Area3 that begins with 'L' and has a '.' in the tenth position.");
		//objFunctionalArea.focus();
		return false;
	}
	return true;
}

function checkFA4splitcodes()
{
	var FunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA4").value;
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter4").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA4");
	var strFunctionalAreaLength = FunctionalArea.substring(0,1);
	var strFAdotPosition = FunctionalArea.substring(9,10);
	for (var i = 0; i < FunctionalArea.length; i++)
	{
		if (specialChars.indexOf(FunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && FunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area4 code.");
				//objFunctionalArea.focus();
				return false;
			}
		}
	}	
	if((strFunctionalAreaLength != "L") || (strFAdotPosition != ".") || (FunctionalArea.length < 16))
	{
		alert("Enter a sixteen digit value for Functional Area4 that begins with 'L' and has a '.' in the tenth position.");
		//objFunctionalArea.focus();
		return false;
	}
	return true;
}

function checkFA5splitcodes()
{
	var FunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA5").value;
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter5").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA5");
	var strFunctionalAreaLength = FunctionalArea.substring(0,1);
	var strFAdotPosition = FunctionalArea.substring(9,10);
	for (var i = 0; i < FunctionalArea.length; i++)
	{
		if (specialChars.indexOf(FunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && FunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area5 code.");
				//objFunctionalArea.focus();
				return false;
			}
		}
	}	
	if((strFunctionalAreaLength != "L") || (strFAdotPosition != ".") || (FunctionalArea.length < 16))
	{
		alert("Enter a sixteen digit value for Functional Area5 that begins with 'L' and has a '.' in the tenth position.");
		//objFunctionalArea.focus();
		return false;
	}
	return true;
}

function checkWBS2splitcodes()
{
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter2").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA2").value;
	
	var objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS2");
	var WBS = document.getElementById("xml:/Order/Extn/@ExtnWBS2").value;
	if(WBS != "")
	{
		for (var i = 0; i < WBS.length; i++)
		{
			if (specialChars.indexOf(WBS.charAt(i)) != -1) 
			{
				alert ("Please do not enter special characters for WBS2 code.");
				//objWBS.focus();
				return false;
			}
		}

		var strWBSLength = WBS.substring(0,1);
		
		if((strWBSLength != "L") || (WBS.length < 12))
		{
			alert("Enter a twelve digit value for WBS2 that begins with 'L'.");
			//objWBS.focus();
			return false;
		}
		
	}
	var formattedBLMCode =  "";
	//CR 731 - ML BEGIN
	//formattedBLMCode = objCostCenter+"."+WBS+"."+objFunctionalArea;
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+WBS;
	//alert("BLM code: " + formattedBLMCode);
	//CR 731 - ML END
	document.all("xml:/Order/Extn/@ExtnAcctCode2").value = formattedBLMCode;	
	return true;
}

function checkWBS3splitcodes()
{
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter3").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA3").value;
	var objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS3");
	var WBS = document.getElementById("xml:/Order/Extn/@ExtnWBS3").value;
	if(WBS != "")
	{
		for (var i = 0; i < WBS.length; i++)
		{
			if (specialChars.indexOf(WBS.charAt(i)) != -1) 
			{
				alert ("Please do not enter special characters for WBS3 code.");
				//objWBS.focus();
				return false;
			}
		}
		var strWBSLength = WBS.substring(0,1);
		
		if((strWBSLength != "L") || (WBS.length < 12))
		{
			alert("Enter a twelve digit value for WBS3 that begins with 'L'.");
			//objWBS.focus();
			return false;
		}
		
	}
	var formattedBLMCode =  "";
	//CR 731 - ML BEGIN
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+WBS;
	//alert("BLM code: " + formattedBLMCode);
	//CR 731 - ML END
	document.all("xml:/Order/Extn/@ExtnAcctCode3").value = formattedBLMCode;
	return true;
}

function checkWBS4splitcodes()
{
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter4").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA4").value;
	var objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS4");
	var WBS = document.getElementById("xml:/Order/Extn/@ExtnWBS4").value;
	if(WBS != "")
	{
		for (var i = 0; i < WBS.length; i++)
		{
			if (specialChars.indexOf(WBS.charAt(i)) != -1) 
			{
				alert ("Please do not enter special characters for WBS4 code.");
				//objWBS.focus();
				return false;
			}
		}
		var strWBSLength = WBS.substring(0,1);
		
		if((strWBSLength != "L") || (WBS.length < 12))
		{
			alert("Enter a twelve digit value for WBS4 that begins with 'L'.");
			//objWBS.focus();
			return false;
		}
		
	}
	var formattedBLMCode =  "";
	//CR 731 - ML BEGIN
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+WBS;
	//alert("BLM code: " + formattedBLMCode);
	//CR 731 - ML END
	document.all("xml:/Order/Extn/@ExtnAcctCode4").value = formattedBLMCode;
	return true;
}

function checkWBS5splitcodes()
{
	var objCostCenter = document.getElementById("xml:/Order/Extn/@ExtnCostCenter5").value;
	var objFunctionalArea = document.getElementById("xml:/Order/Extn/@ExtnFA5").value;
	var objWBS = document.getElementById("xml:/Order/Extn/@ExtnWBS5");
	var WBS = document.getElementById("xml:/Order/Extn/@ExtnWBS5").value;
	if(WBS != "")
	{
		for (var i = 0; i < WBS.length; i++)
		{
			if (specialChars.indexOf(WBS.charAt(i)) != -1) 
			{
				alert ("Please do not enter special characters for WBS5 code.");
				return false;
			}
		}
		var strWBSLength = WBS.substring(0,1);
		
		if((strWBSLength != "L") || (WBS.length < 12))
		{
			alert("Enter a twelve digit value for WBS5 that begins with 'L'.");
			return false;
		}
		
	}
	var formattedBLMCode =  "";
	//CR 731 - ML BEGIN
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+WBS;
	//alert("BLM code: " + formattedBLMCode);
	//CR 731 - ML END
	document.all("xml:/Order/Extn/@ExtnAcctCode5").value = formattedBLMCode;
	return true;
}

function checkExtractCostCenter(elem)
{
	var strCostCenter = elem.value;
	var objCostCenter = document.getElementById("xml:/NWCGBillingTransExtract/@CostCenter");
	
	var strLL = strCostCenter.substring(0,2);
	if((strLL != "LL") || (strCostCenter.length != 10))
	{
		alert("Enter a ten digit code for Cost Center that begins with LL");
		objCostCenter.focus();
	}
	
	return false;
}

function checkExtractWBS(elem)
{
	var strWBS = elem.value;
	var objFunctionalArea = document.getElementById("xml:/NWCGBillingTransExtract/@FunctionalArea").value;
	var objWBS = document.getElementById("xml:/NWCGBillingTransExtract/@WBS").value;
	var objCostCenter = document.getElementById("xml:/NWCGBillingTransExtract/@CostCenter").value;
	var objWBSelement = document.getElementById("xml:/NWCGBillingTransExtract/@WBS");
	//condition added by Gaurav for CR 617
	var strSubStringWBS = strWBS.substring(0,1);
	if(strWBS != "")
	{
		if((strSubStringWBS != "L") || (strWBS.length != 12))
		{
			alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
			objWBSelement.focus();
		}
	}
	var formattedBLMCode =  "";
	formattedBLMCode = objCostCenter+"."+objWBS+"."+objFunctionalArea;
	//alert("BLM code: " + formattedBLMCode);
	//condition added by Gaurav for CR 617
	var objIncidentBlmAcctCode = document.all("xml:/NWCGBillingTransExtract/@IncidentBlmAcctCode");
	if(objIncidentBlmAcctCode != null)
	{
		objIncidentBlmAcctCode.value = formattedBLMCode;
	}
	//return false;
}

function checkExtractFunctionalArea(elem)
{
	var objFunctionalArea = document.getElementById("xml:/NWCGBillingTransExtract/@FunctionalArea");
	var strFunctionalArea = elem.value;
	var strSubStringFA = strFunctionalArea.substring(0,1);
	var strDotFA =strFunctionalArea.substring(9,10);
	
	
	if((strSubStringFA != "L") || (strDotFA != ".") || (strFunctionalArea.length != 16))
	{
		alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
		objFunctionalArea.focus();
	}

	return false;	
}

function checkExtnCostCenter()
{
	//alert("enters checkExtnCostCenter");
	//var strCostCenter 			= document.getElementById("xml:/Order/Extn/@ExtnCostCenter").value;
	var strCostCenter 			= document.getElementById("xml:/Order/Extn/@ExtnCostCenter");
	if(strCostCenter != null) strCostCenter = strCostCenter.value;
	else strCostCenter.value = '';
	//var objFunctionalArea 		= document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea").value;
	var objFunctionalArea 			= document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea");
	if(objFunctionalArea != null) objFunctionalArea = objFunctionalArea.value;
	else objFunctionalArea.value = '';
	//var objWBS			 		= document.getElementById("xml:/Order/Extn/@ExtnWBS").value;
	var objWBS			 		= document.getElementById("xml:/Order/Extn/@ExtnWBS");
	if(objWBS != null) objWBS = objWBS.value;
	else objWBS.value = '';

	//var objIncidentBlmAcctCode 	= document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value;
	var objIncidentBlmAcctCode 	= document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode");
	if(objIncidentBlmAcctCode != null) objIncidentBlmAcctCode = objIncidentBlmAcctCode.value;
	else objIncidentBlmAcctCode.value = '';



	var obj = document.getElementById("xml:/Order/@OwnerAgency") ;

	if(obj == null) alert("obj is null");

	//alert(" obj owner agency :: " + obj.value);
	var formattedBLMCode =  "";

	if(strCostCenter == ""){

		formattedBLMCode = strCostCenter+"."+objFunctionalArea+"."+objWBS;
		document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;

		if(obj.value != 'BLM'){
		return true;
		}

	}
	for (var i = 0; i < strCostCenter.length; i++)
	{
		if (specialChars.indexOf(strCostCenter.charAt(i)) != -1) 
		{
			alert ("Please do not enter special characters for Cost Center code.");
			return false;
		}
	}	
	var strLL = strCostCenter.substring(0,2);
	if((strLL != "LL") || (strCostCenter.length != 10))
	{
		alert("Enter a ten digit code for Cost Center that begins with LL");
		return false;
	}
	//if(objIncidentBlmAcctCode != "")
	{
		
		//formattedBLMCode = strCostCenter+"."+objWBS+"."+objFunctionalArea;
		formattedBLMCode = strCostCenter+"."+objFunctionalArea+"."+objWBS;
		document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;
		if(obj.value == 'BLM')
		document.all("xml:/Order/Extn/@ExtnShipAcctCode").value = formattedBLMCode;
	}	
}
function checkExtnFunctionalArea()
{
	var objFunctionalArea 			= document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea");
	var strFunctionalArea 			= objFunctionalArea.value;
	var strSubStringFA 				= strFunctionalArea.substring(0,1);
	var strDotFA 					= strFunctionalArea.substring(9,10);
	
	var objCostCenter				= document.getElementById("xml:/Order/Extn/@ExtnCostCenter").value;
	var objWBS			 			= document.getElementById("xml:/Order/Extn/@ExtnWBS").value;
	var objIncidentBlmAcctCode 		= document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value;
	var obj = document.getElementById("xml:/Order/@OwnerAgency") ;
	var formattedBLMCode =  "";

	if(strFunctionalArea == ""){
		formattedBLMCode = objCostCenter+"."+strFunctionalArea+"."+objWBS;
		document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;

		if(obj.value != 'BLM'){
			return true;
		}
	}
	for (var i = 0; i < strFunctionalArea.length; i++)
	{
		if (specialChars.indexOf(strFunctionalArea.charAt(i)) != -1) 
		{	
			if(!(i==9 && strFunctionalArea.charAt(i) == "." ))
			{
				alert ("Please do not enter special characters for Functional Area code.");
				return false;
			}
		}
	}	
	if((strSubStringFA != "L") || (strDotFA != ".") || (strFunctionalArea.length != 16))
	{
		alert("Enter a sixteen digit value for Functional Area that begins with 'L' and must have a '.' in the tenth position");
		return false;
	}
	//if(objIncidentBlmAcctCode != "")
	{
		
		//formattedBLMCode = objCostCenter+"."+objWBS+"."+strFunctionalArea;
		formattedBLMCode = objCostCenter+"."+strFunctionalArea+"."+objWBS;
		document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;
		//objIncidentBlmAcctCode.value = formattedBLMCode;
		//alert("checkExtnFunctionalArea " + formattedBLMCode);
		if(obj.value == 'BLM')
		document.all("xml:/Order/Extn/@ExtnShipAcctCode").value = formattedBLMCode;
	}	
}

function checkOtherAcctCode(elem){
	alert("inside checkOtherAcctCode");
	return false;
}

function checkExtnWBS()
{
	//var strWBS 					= elem.value;
	var objFunctionalArea 		= document.getElementById("xml:/Order/Extn/@ExtnFunctionalArea").value;
	var objWBS 					= document.getElementById("xml:/Order/Extn/@ExtnWBS").value;
	var objCostCenter 			= document.getElementById("xml:/Order/Extn/@ExtnCostCenter").value;
	var objWBSelement 			= document.getElementById("xml:/NWCGIncidentOrder/@WBS");
	var strSubStringWBS 		= objWBS.substring(0,1);
	var objIncidentBlmAcctCode 		= document.getElementById("xml:/Order/Extn/@ExtnBlmAcctCode").value;
	var formattedBLMCode =  "";
	var obj = document.getElementById("xml:/Order/@OwnerAgency") ;

	if(objWBS != "")
	{
		for (var i = 0; i < objWBS.length; i++)
		{
			if (specialChars.indexOf(objWBS.charAt(i)) != -1) 
			{
				alert ("Please do not enter special characters for WBS code.");
				return false;
			}
		}
		if((strSubStringWBS != "L") || (objWBS.length != 12))
		{
			alert("Enter a twelve digit value for Work Breakdown Structure that begins with 'L'");
			//objWBSelement.focus();
			return false;
		}		
		
	}
	formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+objWBS;
	//alert("formattedBLMCode :: " + formattedBLMCode);
	document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;
	if(obj.value == 'BLM')
	document.all("xml:/Order/Extn/@ExtnShipAcctCode").value = formattedBLMCode;
	return true;
	/*if(objIncidentBlmAcctCode != "")
	{
		//var formattedBLMCode =  "";
		//formattedBLMCode = objCostCenter+"."+objWBS+"."+objFunctionalArea;
		formattedBLMCode = objCostCenter+"."+objFunctionalArea+"."+objWBS;
		alert("formattedBLMCode :: " + formattedBLMCode);
		document.all("xml:/Order/Extn/@ExtnBlmAcctCode").value = formattedBLMCode;
		if(obj.value == 'BLM')
		document.all("xml:/Order/Extn/@ExtnShipAcctCode").value = formattedBLMCode;
		return true;
	}*/
	
}

function confirm_shipMethodChange()	{
	var smcConfirm = confirm("Are you sure you want to change the shipping method for this issue?");
	if (smcConfirm == true) {
		var extnNavInfo = document.getElementById("xml:/Order/Extn/@ExtnNavInfo");
		if (extnNavInfo != null) {
			document.documentElement.setAttribute("SelectedShippingType", extnNavInfo.value);
		}		
		invokeSave();
	} else {
		yfcChangeDetailView(getCurrentViewId());
	}
}

function callOrderLookup(sIncidentNo,sIncidentYear,sDocumentType,entityname,extraParams)
{
	var oIncidentNo = document.all(sIncidentNo);
	var oIncidentYear = document.all(sIncidentYear);
	
	//if(sDocumentType != null)
	//(
		var extraParams='xml:/Order/@DocumentType='+sDocumentType;
	//}
	
	if(extraParams == null) {
		showOrderLookupPopup(oIncidentNo, oIncidentYear, entityname);
	}
	else{
		showOrderLookupPopup(oIncidentNo, oIncidentYear, entityname, extraParams);
	}		
}
	
function showOrderLookupPopup(IncidentNoInput,IncidentYearInput, entityname, extraParams)
{
	var oObj = new Object();
	oObj.field1 = IncidentNoInput;
	oObj.field2 = IncidentYearInput;
	if(extraParams == null) {
		yfcShowSearchPopup('','NWCGIncidentLookup',900,550,oObj,entityname);
	}
	else{
		yfcShowSearchPopupWithParams('','NWCGIncidentLookup',900,550,oObj,entityname,extraParams);
	}
}

function returnFocusToItemId() 
{
	var itemIdElm = document.getElementById('itemid');
	itemIdElm.focus();
	itemIdElm.select();
}

function addTimeStamp(elem,fieldId,id)
{
	//CR 708 - ML BEGIN
	//function to add TS to end of date field
	var fieldValue = document.getElementById(fieldId).value;
	//alert("The field Value = "+ fieldValue);
	if(fieldValue != "")
	{
		document.getElementById(id).value = fieldValue + " 00:00:00";
		//var test = document.getElementById(id).value;
		//alert("The form value = " + test);
 	}
 }
 
function checkCancelCriteria()
{
	var serialStatus = document.getElementById("xml:/NWCGTrackableItem/@SerialStatus");
//	alert("serialStatus: " + serialStatus.value);	
	if(serialStatus != null && serialStatus.value == 'I')
	{
		return true;
	}
	else
	{
		alert('Item cannot be cancelled. Status must be Issue.');
		return false;
	}	
}

function showCancellationPopup(cancelReasonViewID, cancelReasonCodeBinding) 
{
//	alert('cancelReasonViewID: ' + cancelReasonViewID );
//	alert('cancelReasonCodeBinding: ' + cancelReasonCodeBinding );	
	if(checkCancelCriteria())
	{
		var myCancelObject = new Object();
		myCancelObject.currentWindow = window;
		myCancelObject.cancelReasonCodeInput = document.all(cancelReasonCodeBinding);
		myCancelObject.OKClicked = false;
		yfcShowDetailPopup(cancelReasonViewID, "", "400", "300", myCancelObject);

		if(myCancelObject.OKClicked == true && myCancelObject.cancelReasonCodeInput.value != '') return true;
		else return false;
	}	
}

function checkEstimatedDates()
{
	//alert('Checking Dates...');
	
	var eDepartDate = document.getElementById("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCDATE");
	var eDepartTime = document.getElementById("xml:/Shipment/Extn/@ExtnEstimatedDepartDate_YFCTIME");
	var eArrivalDate = document.getElementById("xml:/Shipment/Extn/@ExtnEstimatedArrivalDate_YFCDATE");
	var eArrivalTime = document.getElementById("xml:/Shipment/Extn/@ExtnEstimatedArrivalDate_YFCTIME");
	
	var eDDstr = eDepartDate.value;
	var eDTstr = eDepartTime.value;
	var eADstr = eArrivalDate.value;
	var eATstr = eArrivalTime.value;	
	
	var regex = /:/g;

	var eDD = eDDstr.substring(6) + eDDstr.substring(0,2) + eDDstr.substring(3,5) + eDTstr.replace(regex,"");
	var eAD = eADstr.substring(6) + eADstr.substring(0,2) + eADstr.substring(3,5) + eATstr.replace(regex,"");
		
	//alert("eDD: " + eDD + "  eAD: " + eAD);
		
	if(parseInt(eAD) >= parseInt(eDD))
	{
		setOverrideFlag('xml:/Shipment/@OverrideModificationRules');
		return true;
	}
	else
	{
		alert('Mobilization ETA must be on or after the Mobilization ETD.');
		return false;
	}	
}

function updateManualROSSOrderInfo(elem, xmlDoc)
{
	//alert("In the Callback function!");
	//alert("updateManualROSSOrderInfo XML ==> " + xmlDoc.xml);
	
	var OrderHeaderKey ='';
	var OrderType ='';
	var IncidentNo ='';
	var IncidentYr ='';
	var sysOfOrigin = '';
	
	
	nodes=xmlDoc.getElementsByTagName("Order");
	if(nodes != null && nodes.length > 0 )
	{
		var order = nodes(0);
		OrderHeaderKey = order.getAttribute("OrderHeaderKey");
		OrderType = order.getAttribute("OrderType");
	}
	
	ExtnNodes=xmlDoc.getElementsByTagName("Extn");
	if(ExtnNodes != null && ExtnNodes.length > 0 )
	{
		var extn = ExtnNodes(0);
		IncidentNo  = extn.getAttribute("ExtnIncidentNo");
		IncidentYr  = extn.getAttribute("ExtnIncidentYear");
		sysOfOrigin = extn.getAttribute("ExtnSystemOfOrigin");

	}
	
	
	
	
	document.getElementById("xml:/Order/@OrderHeaderKey").value = checkStringForNull(OrderHeaderKey) ;
	document.getElementById("xml:/Order/Extn/@ExtnIncidentNo").value = checkStringForNull(IncidentNo) ;
	document.getElementById("xml:/Order/Extn/@ExtnIncidentYear").value = checkStringForNull(IncidentYr) ;
	document.getElementById("xml:/Order/Extn/@ExtnSystemOfOrigin").value = checkStringForNull(sysOfOrigin) ;
	document.getElementById("xml:/RossInfo/@OrderType").value = checkStringForNull(OrderType) ;
	//alert("OHK ==> "+ document.getElementById("xml:/Order/@OrderHeaderKey").value);
	yfcChangeDetailView('ManualUpdate');
	
	
	
	
}
function validateMsgType()
{
	//alert('MSG ==> ' + document.getElementById('xml:/RossInfo/@MsgType').value);
		//alert('OrderNo ==> ' + document.getElementById('xml:/RossInfo/@OrderNo').value);
	
	if((document.getElementById('xml:/RossInfo/@MsgType').value == 'BlankSpace')||(document.getElementById('xml:/RossInfo/@OrderNo').value == ''))
	{
		alert('Please enter the Message Type and Order Number');
		document.getElementById('lookupicon').focus();
		return false;
	}	
	
	return true;
}


//Begin CR833 
//overloading above function 
function returnFocusToItemId(ipcounter) 
{
        var itemidElem = document.getElementById("itemid"+ipcounter);
        itemidElem.focus();
	itemidElem.select();
}
//End CR833

//Begin CR 1161  
//Checks if transaction is extracted or not set to reviewed before trying to unset review flag
function checkToUnsetReviewFlag() {
	var eleArray = document.forms["containerform"].elements;
	var IsReviewed;
	var IsExtracted;
	var TransNo;
	var ItemIds = "";
	var itemsNotReviewed = "";
	var foundChk = false;
	for (var i = 0; i < eleArray.length; i++) {
		if (eleArray[i].name == 'EntityKey') {
			if (eleArray[i].checked) {
				var entityKey = eleArray[i].value;
				IsReviewed = eleArray[i].reviewed;
				IsExtracted = eleArray[i].extracted;

				if(IsReviewed == "N"){
					itemsNotReviewed += eleArray[i].transNo + " ";
					eleArray[i].checked = false;
					foundChk = true;
				} else if(IsExtracted == "Y") {
					ItemIds += eleArray[i].transNo + " ";
					eleArray[i].checked = false;
					foundChk = true;
				}
			}
		}
	}
	if(foundChk) {
		var error = "";
		if(itemsNotReviewed != "") {
			error += "Billing Transaction Review status for Transaction No: " + itemsNotReviewed + " cannot be unset because they are not in reviewed status.\n";
		}
		if (ItemIds != "") {
			error += "Billing Transaction Review status for Transaction No: " + ItemIds + " cannot be unset because extracted flag is set.";
		}
		alert(error);
		return false;
	}
	return true;
}
// End CR 1161

// Pay.gov
function checkPaymentDetails(elem)
{
	var accountNumber = document.getElementById("xml:PCSale/@account_number");
	var expirationDate = document.getElementById("xml:PCSale/@credit_card_expiration_date");
	var action = document.getElementById("cbAction");
	var businessName = document.getElementById("billCustomerName");
	var firstName = document.getElementById("billFirstName");
	var lastName = document.getElementById("billLastName");
	var billAddress = document.getElementById("billAddress1");
	var billCity = document.getElementById("billCity");
	var billState = document.getElementById("billState");
	var billZipCode = document.getElementById("billZipCode");
	var billCountry = document.getElementById("billCountry");
	var transType =  document.getElementById("transactionType");
	var iFirstName =  document.getElementById("iFirstName");
	var iLastName =  document.getElementById("iLastName");
	
	var shippingCost = document.getElementById("totalShippingCost");
	var totalCost = document.getElementById("TransactionAmount");
	
	if(totalCost.value <= 0)
	{
		alert('Transaction amount must be greater than zero');
		shippingCost.focus();
		return false;
	}
	
	if(accountNumber != null && accountNumber != 'undefined')
	{
		if(accountNumber.value == '' )
		{
			alert('Account Number not populated');
			accountNumber.focus();
			return false;
		}
		else
		{
			if( !valid_credit_card(accountNumber.value) )
			{
				alert('Credit Card Number Invalid: ' + accountNumber.value);
				accountNumber.focus();
				return false;
			}
		}

	}
	else
	{
		alert('Account Number null or undefined');
		return false;
	}
	
	if(expirationDate != null && expirationDate != 'undefined')
	{
		if(expirationDate.value == '' )
		{
			alert('Expiration Date not populated');
			expirationDate.focus();
			return false;
		}
		else
		{
			if( !checkExp(expirationDate.value) )
			{
				expirationDate.focus();
				return false
			}
		}

	}
	else
	{
		alert('Expiration Date null or undefined');
		expirationDate.focus();
		return false;
	}
	
	if( action != null && action != 'undefined')
	{
		if(action.value == '' ||  action.value == ' ')
		{
			alert('Action not populated');
			action.focus();
			return false;
		}

	}
	else
	{
		alert('Action null or undefined');
		return false;
	}
	
	
	if( businessName != null && businessName != 'undefined')
	{
		if(businessName.value == '' ||  businessName.value == ' ')
		{
			alert('Business Name not populated');
			businessName.focus();
			return false;
		}

	}
	else
	{
		alert('Business Name null or undefined');
		return false;
	}
	
	
	if( firstName != null && firstName != 'undefined')
	{
		if(firstName.value == '' ||  firstName.value == ' ')
		{
			alert('First Name not populated');
			firstName.focus();
			return false;
		}

	}
	else
	{
		alert('First Name null or undefined');
		return false;
	}
	
	if( lastName != null && lastName != 'undefined')
	{
		if(lastName.value == '' ||  lastName.value == ' ')
		{
			alert('Last Name not populated');
			lastName.focus();
			return false;
		}

	}
	else
	{
		alert('Last Name null or undefined');
		return false;
	}
	
	if( billAddress != null && billAddress != 'undefined')
	{
		if(billAddress.value == '' ||  billAddress.value == ' ')
		{
			alert('Address 1 not populated');
			billAddress.focus();
			return false;
		}

	}
	else
	{
		alert('Address 1 null or undefined');
		return false;
	}
	
	if( billCity != null && billCity != 'undefined')
	{
		if(billCity.value == '' ||  billCity.value == ' ')
		{
			alert('City not populated');
			billCity.focus();
			return false;
		}

	}
	else
	{
		alert('City null or undefined');
		return false;
	}
	
	if( billState != null && billState != 'undefined')
	{
		if(billState.value == '' ||  billState.value == ' ')
		{
			alert('State not populated');
			billState.focus();
			return false;
		}

	}
	else
	{
		alert('State null or undefined');
		return false;
	}
	
	if( billZipCode != null && billZipCode != 'undefined')
	{
		if(billZipCode.value == '' ||  billZipCode.value == ' ')
		{
			alert('Zip Code not populated');
			billZipCode.focus();
			return false;
		}

	}
	else
	{
		alert('Zip Code null or undefined');
		return false;
	}
	
	if( billCountry != null && billCountry != 'undefined')
	{
		if(billCountry.value == '' ||  billCountry.value == ' ')
		{
			alert('Country not populated');
			billCountry.focus();
			return false;
		}

	}
	else
	{
		alert('Country null or undefined');
		return false;
	}
	
	if( document.getElementById("radioBIndividual").checked != true & 
			document.getElementById("radioBOrganization").checked != true )
	{
		alert('Transaction Type not selected, please select either Individual or Organization!');	
		return false;
	}
	
			if( document.getElementById("radioBIndividual").checked == true)
			{
				
				if( iFirstName != null && iFirstName != 'undefined')
				{
					if(iFirstName.value == '' ||  iFirstName.value == ' ')
					{
						alert('First Name not populated');
						iFirstName.focus();
						return false;
					}

				}
				else
				{
					alert('First Name null or undefined');
					return false;
				}
				
				if( iLastName != null && iLastName != 'undefined')
				{
					if(iLastName.value == '' ||  iLastName.value == ' ')
					{
						alert('Last Name not populated');
						iLastName.focus();
						return false;
					}

				}
				else
				{
					alert('Last Name null or undefined');
					return false;
				}
			}
			
			
	
	return true;
}

function valid_credit_card(value) {
// accept only digits, dashes or spaces
    if (/[^0-9-\s]+/.test(value)) return false;

// The Luhn Algorithm.
    var nCheck = 0, nDigit = 0, bEven = false;
    value = value.replace(/\D/g, "");

    for (var n = value.length - 1; n >= 0; n--) {
        var cDigit = value.charAt(n),
            nDigit = parseInt(cDigit, 10);

        if (bEven) {
            if ((nDigit *= 2) > 9) nDigit -= 9;
        }

        nCheck += nDigit;
        bEven = !bEven;
    }

    return (nCheck % 10) == 0;
}

function checkExp(value){

 	var match = value.match(/(\d{2})-(\d{4})/);

    if (!match){
        alert('Invalid expiration date, format must match: MM-YYYY')
        return false;
    }
    
    var now=new Date();
	
    if( match[2] < now.getFullYear() )
    {
    	alert('Credit Card Year Expired');
    	return false;
    }

    if ( ( parseInt(match[1], 10) <= now.getMonth()+1 ) && ( match[2] <= now.getFullYear() ) )
    {
       alert('Credit Card Month Expired');
       return false;
    } 
    
    return true;
}

function checkOrderIsCharged(viewId, chkName)
{

	var inputList = document.getElementsByTagName("input");
	var count = 0;
	
	if(inputList != null) {
		for(i=0;i < inputList.length;i++) {
			var elem = inputList[i];
			if(elem.type == 'checkbox' && elem.checked == true) 
			{	
				++count;
			}
		}
	}
	
	if(count > 1)
	{
		alert('Cannot select more than one record!');
		return false;
	}
	
	var status;
	
	var eleArray = document.forms["containerform"].elements;

	for (var i = 0; i < eleArray.length; i++) 
	{
		if (eleArray[i].name == chkName)
		{
			if (eleArray[i].checked) 
			{
				var entityKey = eleArray[i].value;
				status = eleArray[i].Status;
				
				if(status == 'ShippedAndCharged')
				{
					return true;
				}
				
			}
		}
	}

	alert('Order in wrong status, must be ShippedAndCharged');
	return false;
}

function checkOrderIsNotCharged(viewId, chkName)
{
	var inputList = document.getElementsByTagName("input");
	var count = 0;
	
	if(inputList != null) {
		for(i=0;i < inputList.length;i++) {
			var elem = inputList[i];
			if(elem.type == 'checkbox' && elem.checked == true) 
			{	
				++count;
			}
		}
	}
	
	if(count > 1)
	{
		alert('Cannot select more than one record!');
		return false;
	}
	
	var status;
	
	var eleArray = document.forms["containerform"].elements;

	for (var i = 0; i < eleArray.length; i++) 
	{
		if (eleArray[i].name == chkName)
		{
			if (eleArray[i].checked) 
			{
				var entityKey = eleArray[i].value;
				status = eleArray[i].Status;
				
				if(status == 'ShippedAndNotCharged')
				{
					return true;
				}
				
			}
		}
	}

	alert('Order in wrong status, must be ShippedAndNotCharged');
	return false;
}

function markOrderAsCharged(viewId, chkName) {
	var inputList = document.getElementsByTagName("input");
	var count = 0;	
	if(inputList != null) {
		for(i=0;i < inputList.length;i++) {
			var elem = inputList[i];
			if(elem.type == 'checkbox' && elem.checked == true) {	
				++count;
			}
		}
	}
	if(count > 1) {
		alert('Cannot select more than one record!');
		return false;
	}
	var status;
	var totalIssuedAmount;
	var eleArray = document.forms["containerform"].elements;
	for (var i = 0; i < eleArray.length; i++) {
		if (eleArray[i].name == chkName) {
			if (eleArray[i].checked) {
				var entityKey = eleArray[i].value;
				status = eleArray[i].Status;
				totalIssuedAmount = parseFloat(eleArray[i].totalAmount.replace(/,/g, ''));
				if(status != 'ShippedAndNotCharged') {
					alert('Order cannot be marked as Charged, not in correct status!');
					return false;
				}
			}
		}
	}
	if(totalIssuedAmount > 0) {
		alert('Cannot mark this order as Charged, total amount greater than 0!');
		return false;
	}
	var smcConfirm = confirm("Are you sure you want to mark this order as Charged?");
	if (smcConfirm == true) {
		return true;
	} else {
		return false;
	}
}

// CR 1333 - BEGIN - May 20, 2015
function validateShippingInstructions() {
	var sShippingMethodIndex = document.getElementById("xml:/Order/Extn/@ExtnNavInfo").selectedIndex;
	if(sShippingMethodIndex == 2) {
		var sShippingDirections = document.getElementById("xml:/Order/Extn/@ExtnShippingInstructions").value.replace(/(^\s*)|(\s*$)/gi, "");
		if(sShippingDirections == "") {
			alert("You have selected 'Shipping Instructions' as the Shipping Method, please enter 'Information' in the Shipping Instructions Sub-Screen!!");
			return false;			
		}
		var sShippingCity = document.getElementById("xml:/Order/Extn/@ExtnShipInstrCity").value.replace(/(^\s*)|(\s*$)/gi, "");
		if(sShippingCity == "") {
			alert("You have selected 'Shipping Instructions' as the Shipping Method, please enter a 'City' in the Shipping Instructions Sub-Screen!!");
			return false;			
		}
		var sStateSelectionIndex = document.getElementById("xml:/Order/Extn/@ExtnShipInstrState").selectedIndex;
		if(sStateSelectionIndex == 0) {
			alert("You have selected 'Shipping Instructions' as the Shipping Method, please select a 'State' in the Shipping Instructions Sub-Screen!!");
			return false;	
		}
	}
	return true;
}
//CR 1333 - END - May 20, 2015

//Defect # 1660 - Start
function changeCache()
{
	var cache = document.getElementById("xml:/User/@OrganizationKey").value;	
	if(cache == "")
	{ 
		alert("Cache cannot be blank. Please select a Cache !");
		return false;
	} 
	else if(cache != null && cache != 'NWCG')
	{
		alert("The User Cache is updated to "+cache+". Please logout and login again for the changes to take affect !");
		return true;
	}	
}

function changeCacheToNWCG()
{
	var cache = document.getElementById("xml:/User/@OrganizationKey").value;	
	if(!(cache == "") && cache != null && cache != 'NWCG')
	{ 
		alert("Please Unselect the Cache. The Organization will be updated to 'NWCG' !!!");
		return false;
	} 
	else
	{
	 alert("The User Cache is updated to 'NWCG'. Please logout and login again for the changes to take affect !");
	 return true;
	}	
}
//Defect # 1660 - End