////////////////////////////////////////////////////////////////////////
//Place the javascript functions that need to be invoked through custom actions in this file.
////////////////////////////////////////////////////////////////////////
// steps to implement this framework to invoke AJAX
/*
1.	Make a call to fetchDataFromServer and pass the parameters, 
	the current element (this), the command(must match the name in the server side command file) and the call back function.
2.  Implement the callback function to do result processing 
2.  Refer to the sample implementation below
	a. Callback function having two parameters (element,xml document)
	c. set the current elements disabled property to false
	d. invoke setParams to set the parameters - criterias to fetch the data from server
		ex wrapperAdd.setParams('&'+this.name+'='+this.value)
*/

/*
 Example usage
 <!--  add the event and call the javascript from the event invoker field-->
<input type="text" class="unprotectedinput" id="xml:/Order/@BuyerOrganizationCode" 
onblur="javascript:fetchDataFromServer(this,'getBillingAddress',updateBillToaddress);"/>

 ---------------------------------------------------------------------------
<!-- Include the following script in the JSP -->
<SCRIPT language="Javascript" src="/smcfs/extn/scripts/ajaxHelper.js"/>
 
<!-- this will be in the value JSP order_lines_details.jsp-->

<SCRIPT language="Javascript">
	function updateBillToaddress(elem,xmlDoc){ // the async callback function
		nodes=xmlDoc.getElementsByTagName("BillingPersonInfo");
		if(nodes!=null && nodes.length >0){	
			var personInfo = nodes(0);
			var path="xml:/Order/PersonInfoBillTo";
			//populate the address
			document.getElementById(path+"/@AddressLine1").value = personInfo.getAttribute("AddressLine1");
			document.getElementById(path+"/@AddressLine2").value = personInfo.getAttribute("AddressLine2");
			document.getElementById(path+"/@City").value = personInfo.getAttribute("City");
			document.getElementById(path+"/@State").value = personInfo.getAttribute("State");
			document.getElementById(path+"/@ZipCode").value = personInfo.getAttribute("ZipCode");
			// the country combo
			document.getElementById(path+"/@Country").value = personInfo.getAttribute("Country");
		}
	}

</SCRIPT>
 
 

*/



/*global cache to cache responses the key is the lookup value the value is the response xml*/
//TODO get a better caching mechanism and allow for cache timeouts 

var cache = new Object();
//var strURL ="/smcfs/console/ajaxcontroller.detail"; // need to externalize this or get it from the context
var strURL ="/smcfs/AjaxServlet";

function fetchDataWithParams(elem,command,onSuccessCallback,params,callBackFlag){
 var ajaxWrapper = new AJAXWrapper();
 if(command == null || command.length <1){return;}
 //set the command
 ajaxWrapper.setCommand(command);
 //set the onComplete handler
 ajaxWrapper.onComplete = onSuccessCallback; 
 
 //set the queryString for the element value
 //ajaxWrapper.setQueryString(elem.id+"="+elem.value); 
 ajaxWrapper.setParameters(params);
 // if the value of flag is not passed, assumming the call back function needs to be called
 if(callBackFlag == null || callBackFlag == undefined ||  callBackFlag == 'undefined' || callBackFlag == true)
	ajaxWrapper.setAlwaysCallBack(true);
 else
	ajaxWrapper.setAlwaysCallBack(false);

 return fetchData(elem,ajaxWrapper);
}

/*
* This function bound to javascript event from JSP
*/
function fetchDataFromServer(elem,command,onSuccessCallback){
 var ajaxWrapper = new AJAXWrapper();
 if(command == null || command.length <1){return;}
 //set the command
 ajaxWrapper.setCommand(command);
 //set the onComplete handler
 ajaxWrapper.onComplete = onSuccessCallback; 
 
 //set the queryString for the element value
 if(elem.id != '')
	ajaxWrapper.setQueryString(elem.id+"="+elem.value); 
 else
	ajaxWrapper.setQueryString(elem.name+"="+elem.value); 

 return fetchData(elem,ajaxWrapper);
}
/*
 * If more control is needed then use this function directly passing the AJAXWrapper object
 */
function fetchData(elem,ajaxWrapper){
	var key = elem.value;
	//disable the element
	//elem.disabled = true;
	//Made changes in the IF condition to take care of checkBox-Suresh
	// checking for text box explictly... as incase of text box the message was sent to
	// server even if nothing is entered
	if(elem.type == 'text' && key.length == 0)
	{
		// sending a blank document as we need a call back function to clean up all the other 
		// values otherwise the values will be there by default
		// calling the call back function if and only if the flag is set
		// by default this flag is false
		if(ajaxWrapper.getAlwaysCallBack())
		{
			ajaxWrapper.onComplete(elem,new ActiveXObject("Microsoft.XMLDOM"));
		}
		return ;
	}
	else if(key.length==0 && (eval(elem.checked) && elem.checked == false)){
		//elem.disabled = false;
		return; // the value is null so return 
	}
	var cacheKey = ajaxWrapper.getQueryString() ;
	//alert(cacheKey);
	
	if(cache[cacheKey] != null && ajaxWrapper.getUseCache()){
		//alert("Returning from Cache"); 
		//update values from the cache
		ajaxWrapper.onComplete(elem,cache[cacheKey]);
		//elem.disabled = false;
	}
	else
	{
		//value not found in cache so go get it from server
		var xmlHttpReq ;
		// Mozilla/Safari
		if (window.XMLHttpRequest) {
			xmlHttpReq = new XMLHttpRequest();
		}
		// IE
		else if (window.ActiveXObject) {
			xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlHttpReq.open('POST', strURL, true);
		xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		
		
		xmlHttpReq.onreadystatechange = function() {
				if (xmlHttpReq==null) { return; }
				if (xmlHttpReq.readyState==1) { 
					ajaxWrapper.beforeLoading(elem,xmlHttpReq.responseXML);
					ajaxWrapper.onLoading(elem,xmlHttpReq.responseXML); 
				}
				if (xmlHttpReq.readyState==2) { ajaxWrapper.onLoaded(elem,xmlHttpReq.responseXML);}
				if (xmlHttpReq.readyState==3) { ajaxWrapper.onInteractive(elem,xmlHttpReq.responseXML);}
				if (xmlHttpReq.readyState==4) { 
					if(cache[cacheKey] == null){
						cache[cacheKey] = xmlHttpReq.responseXML;
					}
					ajaxWrapper.beforeComplete(elem,xmlHttpReq.responseXML); 
					ajaxWrapper.onComplete(elem,xmlHttpReq.responseXML); 
					//elem.disabled = false;
				}
		}
		//alert(ajaxWrapper.getQueryString());
		//post the request
		//alert(ajaxWrapper.getQueryString());
		xmlHttpReq.send(ajaxWrapper.getQueryString());
	}
}

/*
 * The Wrapper class representing the key elements for the AJAX calls 
 */
function AJAXWrapper()
{
	this.command = '';
	this.setCommand = function(command){ this.command = command; };
	this.getCommand = function(){ return this.command;};

	this.alwaysCallBack = false;
	this.getAlwaysCallBack = function(){return this.alwaysCallBack;};
	this.setAlwaysCallBack = function(flag){this.alwaysCallBack = flag;};
	

	this.useCache=true;
	this.getUseCache = function(){return this.useCache;};
	this.setUseCache = function(flag){this.useCache = flag;};
	// the request parameters array
	this.parameters = new Object();
	this.resetParameter = function(){this.parameters = new Object();};
	this.setParameters = function(params){
		
		this.parameters = params;
			for(key in params)
			{
				//alert(key + ' ' + params[key]);
				if(key=='USECACHE' && params[key]=='false')
				{
					this.setUseCache(false);
				}
			}

		};
	this.setParameter = function(key,value){this.parameters[key] = value;};
	// the queryString
	this.queryString = '';
	
	this.setQueryString = function(queryString){ this.queryString = queryString;};
	this.getQueryString = function(){
		// build the query string with the command
		if (this.queryString.length>0) { 
			this.queryString += "&"; 
		}
		// set the command in the request
		if(this.command == '' || this.command.length <1){return '';}
		this.queryString += "c=" + encodeURIComponent(this.command);
		for(var i in this.parameters){
				this.queryString += "&" + encodeURIComponent(i) + "=" + encodeURIComponent(this.parameters[i]);
			}
		return this.queryString;
		};
	// the callback bindings
	// default implementation can provide for user interaction during server calls.
	this.beforeLoading = function(elem,xmlDoc){
		elem.disabled = true;
		elem.style.cursor='progress';
		var cell = elem.parentNode;
		var imgNodeList = cell.getElementsByTagName("IMG");
		var imgNode;
		if(imgNodeList!=null){
			var found = false;
			for(var i=0;i<imgNodeList.length;i++){
				imgNode = imgNodeList[i];
				if(imgNode.id =='searchimg'){//if we are looking at the right image
					found = true;
					imgNode.style.display = ''; //enable the image display				
					break;
				}
			}
			if(!found){//create the image
			imgNode = document.createElement("IMG");
			imgNode.setAttribute("id","searchimg");
			imgNode.setAttribute("src","/smcfs/extn/icons/searching.gif");
			imgNode.setAttribute("alt","Fetching data from server..");
			cell.appendChild(imgNode);
			}
		}
		
	};
	this.onLoading = function(elem,xmlDoc){};
	this.onLoaded = function(elem,xmlDoc){};
	this.onInteractive = function(elem,xmlDoc){};
	this.beforeComplete = function(elem,xmlDoc){
		elem.style.cursor='auto';
		elem.disabled = false;
		var cell = elem.parentNode;
		var imgNodeList = cell.getElementsByTagName("IMG");
		if(imgNodeList!=null){
			for(var i=0;i<imgNodeList.length;i++){
				if(imgNodeList[i].id =='searchimg'){//if we are looking at the right image
					imgNodeList[i].style.display = 'none'; //hide the image display				
					break;
				}
			}
		}
	};
	this.onComplete = function(elem,xmlDoc){};
}