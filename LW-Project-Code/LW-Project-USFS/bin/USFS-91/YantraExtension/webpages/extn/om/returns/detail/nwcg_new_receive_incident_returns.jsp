<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<%
String ErrorMessage = resolveValue("xml:/Receipt/@ErrorMessage");
YFCElement elem = (YFCElement) request.getAttribute("Receipt");
int iTotalReceiptLine = 0 ;
int iTotalComponents = 0 ;
YFCNodeList nl = null ;
if(elem != null)
{
	nl = elem.getElementsByTagName("ReceiptLine");
	iTotalReceiptLine = nl.getLength();
}
%>

<input type="hidden" name="xml:/Receipt/@SerialList" value="" />
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_returns.js"></script>

<script language="javascript">

function trim (elem) {
	elem.value = elem.value.replace(/(^\s*)|(\s*$)/gi, "");
	return;
}

function checkTagExist(elem)
{
	var crow = elem.parentElement;
	var ilist = crow.getElementsByTagName("input");
	for (i=0;i<ilist.length;i++ )
	{
		if (ilist[i].name.indexOf("@serialID") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@PrimarySerialNo") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@ExpiryDate") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute1") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute2") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute3") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute4") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute5") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@TagAttribute6") != -1)
		{
			return true;
		}
		if (ilist[i].name.indexOf("@ManufacturingDate") != -1)
		{
			return true;
		}
	}
	
	return false;
}

function populateCompSerials(elem,xmlDoc){
	var	SerializedFlag="";
	var currentRow = "";
	var rowID = "";
	var TimeSensitive = "";
	var NumReturns = "";
	var ItemID1 = "";
	var PC = "";
	var UOM = "";
	var NumSecondarySerials = "";
	var CompTableID = "";
	var MainTableID = "";
	
	//---------Added after final demo
		var tagFlag = "";
		var BatchNo = "";
		var ManufName = "";
		var ManufModel = "";
		var RevisionNo = "";
		var LotNumber = "";
		var OwnerUnitId = "";
		var ManufDate = "";

    if (checkTagExist(elem))
    {
		return false;
    }
	//----------End added after final demo

	
    // Getting Parent Item ID
    var PItemID="";
	var PItemRow = elem.parentElement.parentElement.parentElement.parentElement.parentElement;
   	var PItemList = PItemRow.getElementsByTagName("input");

   	if(PItemList[0].name.indexOf('@ItemID') != -1)
	    {
		  PItemID = PItemList[0].value;
		}
     // End Getting Parent Item ID

     // Getting Return Header Details
	 var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	 var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	 var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
	 var CacheId = document.getElementById("xml:/Receipt/@CacheID").value;
	 // End Getting Return Header Details

	var nodes=xmlDoc.getElementsByTagName("Item");
	
	if(nodes!=null && nodes.length > 0 )
	{	
		var ResultNode = nodes(0);
		SerializedFlag = ResultNode.getAttribute("SerializedFlag") ;
		TimeSensitive = ResultNode.getAttribute("TimeSensitive");
		ItemID1 = ResultNode.getAttribute("ItemID");
		UOM = ResultNode.getAttribute("UOM");
		PC = ResultNode.getAttribute("ProductClass");
		NumSecondarySerials = ResultNode.getAttribute("NumSecondarySerials");

		//-------Added after final demo
		tagFlag = ResultNode.getAttribute("TagControlFlag");
		BatchNo = ResultNode.getAttribute("BatchNo");
		ManufName = ResultNode.getAttribute("LotAttribute1");
		ManufModel = ResultNode.getAttribute("LotAttribute3");
		LotNumber = ResultNode.getAttribute("LotNumber");
		RevisionNo = ResultNode.getAttribute("RevisionNo");
		OwnerUnitId = ResultNode.getAttribute("LotAttribute2");
		ManufDate = ResultNode.getAttribute("ManufacturingDate");
		//alert("Manufacturing Date: " + ManufDate);
		
		//-------End adding after final demo

		//alert("tagFlag:-"+tagFlag+" BatchNo:-"+BatchNo+" ManufName:-"+ManufName+"LotNumber"+LotNumber);
	}//End Nodes


	if(SerializedFlag=="Y"){
			//-----get the id value
				currentRow = elem.parentElement.parentElement;
				//alert("Current Row :-"+currentRow.innerHTML);
				var InputList = currentRow.getElementsByTagName("input");
				
				for(cntr=0;cntr<InputList.length;cntr++){
						if(InputList[cntr].name.indexOf('@ItemID') != -1)
						{
							 ItemID = InputList[cntr].value;
							 rowID  = InputList[cntr].id;
						}//end if
						
						if(InputList[cntr].name.indexOf('@QtyReturned') != -1)
						{
							 NumReturns = InputList[cntr].value;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
						
						if(InputList[cntr].name.indexOf('MainRowID') != -1)
						{
							 MainTableID = InputList[cntr].value;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
						
						if(InputList[cntr].name.indexOf('ComponentID') != -1)
						{
							 CompTableID = InputList[cntr].value;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
				}

				//for(cnt1=0;cnt1<InputList.length;cnt1++){
						//alert("testing"+InputList.length+" cnt1:-"+cnt1);
						
						//InputList[cnt1].name:-"+InputList[cnt1].value + "cnt1:-"+cnt1);
						/*if(InputList[cnt1].name.indexOf('@ItemID') != -1)
						{
							 ItemID = InputList[cnt1].value;
							 rowID  = InputList[cnt1].id;
						}//end if
						if(InputList[cnt1].name.indexOf('@QtyReturned') != -1)
						{
							 NumReturns = InputList[cnt1].value;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
						if(InputList[cnt1].name.indexOf('MainTableID') != -1)
						{
							 MainTableID = InputList[cnt1].id;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
						if(InputList[cnt1].name.indexOf('CompTableID') != -1)
						{
							 CompTableID = InputList[cnt1].id;
							 //alert("NumReturns:-"+NumReturns);
						}//end if
						*/
				//}//End For Loop

				//return true;

				//alert("ItemID is :-"+ItemID+" rowID:-"+rowID+" NumReturns:-"+NumReturns+" CompTableID:-"+CompTableID+"MainTableID:-"+MainTableID);
			//-----------------End getting id value

			//---find the right table
			var tableList = currentRow.getElementsByTagName("table");

					for(index = 0 ; index < tableList.length ; index++)
					{
							////alert("test:-"+divList[index].tBodies[0].innerHTML+"rows to add:"+rows);
							var baseString = "CompSerials";
							var compareString = baseString.concat(rowID);
							//alert("compareString-for compSerials:-"+compareString);

							if(tableList[index].id==compareString){
								var tblBody = tableList[index].tBodies[0];

								//var newRow = tblBody.insertRow();
								//var newCell0 = newRow.insertCell(0);
								//var finalString = 'testing comp serial insertion';
								//----------------following is to when to show serial and Expiration
												var finalRow = "";
												var TimesenitiveRow = "";
										for (cntr1=0;cntr1< NumReturns;cntr1++ )
										{
												var finalRow = "";
												var newRow = tblBody.insertRow();
												var newCell0 = newRow.insertCell(0);
												var secSerialRow ='';
												var serialRow = '';
												
												secSerialRow = '<td>Manufacturers Serial :</td><td><input type="text" class="unprotectedinput"  READONLY size=10 maxlength=30 value="" id="'+cntr1+'"    name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@SecondarySerialNo" /></td><td><input type="hidden" name="PC" value="'+PC+'"></td><td><input type="hidden" name="UOM" value="'+UOM+'"></td><td><input type="hidden" name="ITEMID" value="'+ItemID1+'"></td><TD noWrap><SELECT class=combobox name=xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@DispositionCode	  OldValue=""> <OPTION value=RFI selected>RFI</OPTION> <OPTION value=NRFI>NRFI</OPTION> <OPTION value=UnsRet>UnsRet</OPTION><OPTION value=UnsRetNWT>UnsRetNWT</OPTION></SELECT> </TD>';
												// CR 217 & 693 Begin												
												if(NumSecondarySerials=="0"){
													serialRow = '<td>Trackable ID:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text"  READONLY size=30 maxlength=40 id="'+cntr1+'" class="unprotectedinput" value="" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,\'checkSerialNo\',PopupMessage,setParamSecSerial(this));" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@serialID" /><img class="lookupicon" onclick="callTrackableIDLookup(this,\'xml:/Receipt/@SerialList\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@serialID\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@SecondarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute1\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute2\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute3\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute4\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute5\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute6\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@ManufacturingDate\',\'NWCGTrackableIDLookup\',\'&xml:/Serial/@IncidentNo='+IncidentNo+'&xml:/Serial/InventoryItem/@ItemID='+ItemID1+'&xml:/Serial/InventoryItem/@ParentItemID='+PItemID+'&xml:/Serial/@IncidentYear='+IncidentYear+'&xml:/Serial/@IssueNo='+IssueNo+'&xml:/Serial/@CacheID='+CacheId+'&xml:/Serial/@NoOfSecSerials='+NumSecondarySerials+'\')" src="<%=request.getContextPath()%>/console/icons/lookup.gif\" alt="Search for Trackable ID"/></td>&nbsp;&nbsp;<td><input type="hidden" name="PC" value="'+PC+'"></td><td><input type="hidden" name="UOM" value="'+UOM+'"></td><td><input type="hidden" name="ITEMID" value="'+ItemID1+'"></td><TD noWrap><SELECT class=combobox name=xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@DispositionCode  OldValue=""> <OPTION value=RFI selected>RFI</OPTION> <OPTION value=NRFI>NRFI</OPTION> <OPTION value=UnsRet>UnsRet</OPTION><OPTION value=UnsRetNWT>UnsRetNWT</OPTION></SELECT> </TD>';
												}else{
													serialRow = '<td>Trackable ID:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text"   size=30 maxlength=40 id="'+cntr1+'" class="unprotectedinput" value="" onBlur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,\'checkSerialNo\',PopupMessage,setParamSecSerial(this));"	name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@serialID" /></td>  <img class="lookupicon" onclick="callTrackableIDLookup(this,\'xml:/Receipt/@SerialList\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@serialID\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@SecondarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute1\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute2\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute3\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute4\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute5\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute6\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@ManufacturingDate\',\'NWCGTrackableIDLookup\',\'&xml:/Serial/@IncidentNo='+IncidentNo+'&xml:/Serial/InventoryItem/@ItemID='+ItemID1+'&xml:/Serial/InventoryItem/@ParentItemID='+PItemID+'&xml:/Serial/@IncidentYear='+IncidentYear+'&xml:/Serial/@IssueNo='+IssueNo+'&xml:/Serial/@CacheID='+CacheId+'&xml:/Serial/@NoOfSecSerials='+NumSecondarySerials+'\')" src="<%=request.getContextPath()%>/console/icons/lookup.gif\" alt="Search for Trackable ID"/></td>&nbsp;&nbsp;<td><input type="hidden" name="PC" value="'+PC+'"></td><td><input type="hidden" name="UOM" value="'+UOM+'"></td><td><input type="hidden" name="ITEMID" value="'+ItemID1+'"></td>';
												// CR 217 & 693 End	
												}//End NumSecondarySerials=="0"

												finalRow = finalRow + serialRow;

												//----------check if it has secondary serial
												if(NumSecondarySerials!="0"){
													finalRow = finalRow + secSerialRow;
												}//End if NumSecondarySerials!=0
												
												//-------------Added after final demo
												if(tagFlag=="Y"){ //Changed "S" to "Y" - GN 01/24/07
													var Final = '';
													
													var MName ='<td>Manufacturer Name:</td>&nbsp;&nbsp;<td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute1" /></td>';
													
													var  MModel = '<td>Manufacturer Model:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute2" /></td>';
													
													var LotNum = '<td>Trackable ID:</td><td><input type="text" READONLY class="unprotectedinput" size=28 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute3" /></td>';
													
													var RevNum ='<td>Date Last Tested:</td><td><input type="text" class="unprotectedinput" size=10 maxlength=40 value="" onblur="javascript:if(this.value !=\'\' && (this.value.length != 10 || this.value.indexOf(\'/\') != 2)){alert(\'Please enter a valid date in mm/dd/yyyy format\');this.focus();}"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute4" /><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif" /></td>';
													
													var BNo='<td>Batch No:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute5" /></td>';

													var OwnId='<td>Owner Unit ID:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TagAttribute6" /></td>';
													
													// CR 693 BEGIN -ML
													var ManufactureDate='<td>Manufacturing Date:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@ManufacturingDate" /></td>';
													// CR 693 END -ML
													
													
													if((BatchNo=="01") || (BatchNo=="02"))
														Final = Final + BNo;

													if(ManufName=="01")
														Final = Final + MName;

													if(ManufModel=="01")
														Final = Final + MModel + ManufactureDate;

													if((RevisionNo=="01") || (RevisionNo=="02"))
														Final = Final + RevNum;

													if((LotNumber=="01") || (LotNumber=="02"))
														Final = Final + LotNum;

													if((OwnerUnitId=="01") || (OwnerUnitId=="02"))
														Final = Final + OwnId;
													
													finalRow = finalRow + Final;
												}//End if ---tagFlag=="Y"
												//--------------End adding after final demo

												//------------------End check secondary serial
												if(TimeSensitive=="Y"){
															TimesenitiveRow = '<td>Expiration  Date:-</td>&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput onblur=onBlurHandler() size=8 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@ExpiryDate" dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> </TD><td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+MainTableID+'/Components/Component_'+CompTableID+'/SerialInfo/Serial_'+cntr1+'/@TAttribute" /></td>';
															finalRow = finalRow + TimesenitiveRow;
												}//if(TimeSensitive[cnt1]=="Y")

												//alert("reached here");
												newCell0.innerHTML = finalRow;
												//alert("serial row -"+finalRow);
										}//End For Loop
								//-----------------End serial and expiration	
								//newCell0.innerHTML = finalString;
								////alert("NumSecondarySerials:-"+NumSecondarySerials);
							}//End IF
					}//End For Loop
			//------finding the right table
	}//End ---SerializedFlag=="Y"
	return true;
}//---End	getComponentSerials


function addTagRows(elem,xmlDoc){
		
		//---get the index 
		
       if (checkTagExist(elem))
        {
	      return false;
        }
		
		var inputElid ="";
		var tagAttribute="";
		var SerializedFlag = "";
		var NumSecondarySerials ="";
		var ItemID1="";
		var PItemID="";
		var TimeSensitive ="";
		var PC = "";
		var UOM = "";
		
		//Added after final demo
		var BatchNo = "";
		var ManufName = "";
		var ManufModel = "";
		var RevisionNo = "";
		var LotNumber = "";
		var OwnerUnitId = "";
		var ManufDate ="";

		//-----------------for getting the returned QTY
		var RetQty = "";
		////alert("setParam3 reached");
		var currentRow = elem.parentElement.parentElement;
		////alert("teeeesting:"+currentRow.innerHTML);
		
		var InputList = currentRow.getElementsByTagName("input");
		var IncidentNo = document.getElementById("xml:/Receipt/@IncidentNo").value;
	    var IncidentYear = document.getElementById("xml:/Receipt/@IncidentYear").value;
	    var IssueNo = document.getElementById("xml:/Receipt/@IssueNo").value;
		var CacheId = document.getElementById("xml:/Receipt/@CacheID").value;
		
		if(InputList!=null && InputList.length >0 ){
			for (i1 = 0 ; i1 < InputList.length ; i1++)
			{
					if(InputList[i1].name.indexOf('@QtyReturned') != -1)
					{
						 RetQty = parseInt(InputList[i1].value);
						 ////alert("return qty:-"+RetQty);
					}//end if

					if(InputList[i1].name.indexOf('@ItemID') != -1)
					{
						ItemID1 = InputList[i1].value;
						////alert("testing ItemID:"+ItemID1);
					}//end if

					if(InputList[i1].name.indexOf('@UOM') != -1)
					{
						UOM = InputList[i1].value;
						////alert("testing UOM:"+UOM);
					}//end if


					if(InputList[i1].name.indexOf('@ProductClass') != -1)
					{
						PC = InputList[i1].value;
						////alert("testing PC:"+PC);
					}//end if
			}//End For Loop
		}//End IF
		
		//-------------Following is to take care of situation when the tabout at retQTY happens w/o ItemID
		if(ItemID1==null||ItemID1==""||RetQty<=0){
			//alert("This is causing problem");
			return true;
		}//End IF


		////alert("RetQty:-"+RetQty);
		//-----End for getting the returned qty
		//This is to get the index of the element
		var test1 = elem.parentElement;
		////alert("test1:-"+test1.innerHTML);
		
		var inputList = test1.getElementsByTagName("input");
		//Suresh made changes
		//for(index = 0 ; index < 1 ; cnt++)
		//{
			inputElid = inputList[0].id;	
			////alert("testing -inputList.id:-"+inputList[0].id);
		//}//End For Loop
		 

		//Reading frorm the input xml	
		nodes=xmlDoc.getElementsByTagName("Item");
		if(nodes!=null && nodes.length >0 ){	
			var InventoryTagAttributes = nodes(0);
			tagAttribute = InventoryTagAttributes.getAttribute("TagControlFlag");
			SerializedFlag =  InventoryTagAttributes.getAttribute("SerializedFlag");
			NumSecondarySerials = InventoryTagAttributes.getAttribute("NumSecondarySerials");
			var NumberSecSerials = parseInt(NumSecondarySerials);
			TimeSensitive = InventoryTagAttributes.getAttribute("TimeSensitive");
			//Added after final demo
			BatchNo = InventoryTagAttributes.getAttribute("BatchNo");
			ManufName = InventoryTagAttributes.getAttribute("LotAttribute1");
			//alert("ManufName (LotAttribute1) " +ManufName);
			ManufModel = InventoryTagAttributes.getAttribute("LotAttribute3");
			LotNumber = InventoryTagAttributes.getAttribute("LotNumber");
			RevisionNo = InventoryTagAttributes.getAttribute("RevisionNo");
			OwnerUnitId = InventoryTagAttributes.getAttribute("LotAttribute2");
			ManufDate = InventoryTagAttributes.getAttribute("ManufacturingDate");
		//	alert("Manufacturing Date: " + ManufDate);

			//End adding after final demo
		}//End if(nodes>0)
		
		if(tagAttribute=="Y" || SerializedFlag=="Y" || TimeSensitive=="Y"){
			//-----------------------------------
					var tableRow = elem.parentElement.parentElement.parentElement.parentElement;
				
					var divList = tableRow.getElementsByTagName("table");

					//NOTE :- This number to be obtained from the outputXML 
					var rows = 1;

					for(index = 0 ; index < divList.length ; index++)
					{
							//alert("test:- "+ divList[index].tBodies[0].innerHTML +" - rows to add:"+rows);
							//alert("inputElid = " + inputElid);
							//alert("DivList.Id "+divList[index].id);
						
							var baseString = "KitItems";
							var pattern = /\d+/
							
							var divcount = inputElid.match(pattern);
							
							//alert("divcount = "+divcount);
							
							var compareString = baseString.concat(divcount);
							//alert("compareString:-"+compareString);

							if(divList[index].id==compareString)
							{
								var tblBody = divList[index].tBodies[0];

								if(SerializedFlag=="Y")
								{
										//alert("In Serialized Flag If");
										var newRow = tblBody.insertRow();
										var newCell0 = newRow.insertCell(0);
										var finalString = '';
										//alert("NumSecondarySerials:-"+NumSecondarySerials);
										////alert("NumSecondarySerials:-"+NumSecondarySerials)
										if(NumSecondarySerials!="0")
										{
												//alert("secondary serial present");
												var newCell1 = newRow.insertCell(1);
												for(cntr1=0;cntr1<RetQty;cntr1++)
												{
													//Next Line Bulds up Initial Ser # w/ secondaries
													// CR 217 & 693 Begin	
													var rowWITHSec = '<td>Trackable ID:</td>&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text"  size=30 maxLength=40 value="" class="unprotectedinput"  id="'+cntr1+'" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,\'checkSerialNo\',PopupMessage,setParamSecSerial(this));" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@PrimarySerialNo" /><img class="lookupicon" onclick="callTrackableIDLookup(this,\'xml:/Receipt/@SerialList\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@PrimarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@SecondarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute1\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute2\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute3\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute4\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute5\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute6\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ManufacturingDate\',\'NWCGTrackableIDLookup\',\'&xml:/Serial/@IncidentNo='+IncidentNo+'&xml:/Serial/InventoryItem/@ItemID='+ItemID1+'&xml:/Serial/@IncidentYear='+IncidentYear+'&xml:/Serial/@IssueNo='+IssueNo+'&xml:/Serial/@CacheID='+CacheId+'&xml:/Serial/InventoryItem/@ParentItemID='+PItemID+'&xml:/Serial/@NoOfSecSerials='+NumSecondarySerials+'\')" src="<%=request.getContextPath()%>/console/icons/lookup.gif\" alt="Search for Trackable ID"/></td>&nbsp;&nbsp;<td>Manufacturers Serial :</td><td><input type="text" READONLY value="" class="unprotectedinput" size=10 maxlength=20 id="'+cntr1+'"   name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@SecondarySerialNo" /></td><td><input type="hidden" name="PC" value="'+PC+'"></td><td><input type="hidden" name="UOM" value="'+UOM+'"></td><td><input type="hidden" name="ITEMID" value="'+ItemID1+'"></td><TD noWrap><SELECT class=combobox name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@DispositionCode"  OldValue=""> <OPTION value=RFI selected>RFI</OPTION> <OPTION value=NRFI>NRFI</OPTION> <OPTION value=UnsRet>UnsRet</OPTION><OPTION value=UnsRetNWT>UnsRetNWT</OPTION></SELECT> </TD>';
													// CR 217 & 693 End	
													//Adding time sensitive in serial  line
													TimesenitiveRow = '<td>Expiration  Date:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput READONLY onblur=onBlurHandler() size=8 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ExpiryDate" dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> </TD><td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@HAttribute" /></td>';

													//----------added after final demo
													if(tagAttribute=="Y")
													{//GN - Changed "S" to "Y" - 01/24/07
															var MName ='<td>Manufacturer Name:</td>&nbsp;&nbsp;<td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute1" /></td>';
										
															var  MModel = '<td>Manufacturer Model:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute2" /></td>';
										
															var LotNum = '<td>Trackable ID:</td><td><input type="text" READONLY class="unprotectedinput" size=28 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute3" /></td>';
										
															var RevNum ='<td>Date Last Tested:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" onblur="javascript:if(this.value !=\'\' && (this.value.length != 10 || this.value.indexOf(\'/\') != 2)){alert(\'Please enter a valid date in mm/dd/yyyy format\');this.focus();}"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute4" /><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif" /></TD>';
										
															var BNo='<td>Batch No:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute5" /></td>';
                                                            
															var OwnId='<td>Owner Unit ID:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute6" /></td>';
															// CR 693 BEGIN -ML
															var ManufactureDate='<td>Manufacturing Date:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ManufacturingDate" /></td>';
															// CR 693 END -ML
															
															
															 if((BatchNo=="01") || (BatchNo=="02"))
																rowWITHSec = rowWITHSec + BNo;

															 if(ManufName=="01")
																rowWITHSec = rowWITHSec + MName;

															 if(ManufModel=="01")
																 rowWITHSec = rowWITHSec + MModel + ManufactureDate;

															 if((RevisionNo=="01") || (RevisionNo=="02"))
																 rowWITHSec = rowWITHSec + RevNum;

															 if((LotNumber=="01") || (LotNumber=="02"))
																  rowWITHSec = rowWITHSec + LotNum;
															 
															 if((OwnerUnitId=="01") || (OwnerUnitId=="02"))
																  rowWITHSec = rowWITHSec + OwnId;

													}//End if---tagAttribute=="S"
													//-----End adding after final demo

													//alert("TimeSensitive---with sec"+TimeSensitive);
													if(TimeSensitive=="Y")
																rowWITHSec = rowWITHSec+TimesenitiveRow;
													//End adding time sensitive in same row

													if(cntr1 >=1)
													{
														//alert("reached here");
														newRow = tblBody.insertRow();
														newCell0 = newRow.insertCell(0);
														newCell0.innerHTML = rowWITHSec;
													}else
													{
														newCell0.innerHTML = rowWITHSec;
													}//End IF
												}//End For Loop
										}
										else
										{
										
										//	alert("secondary serial not present");
										//------------Adding 
											for(cntr1=0;cntr1<RetQty;cntr1++)
											{
													//Following Line FOR PRIMARY SERIAL WO Secondary
													// CR 217 Begin	
													var RowWOSec ='<td>Trackable ID:</td>&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text"  size=30 maxLength=40 value="" class="unprotectedinput"  id="'+cntr1+'" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,\'checkSerialNo\',PopupMessage,setParamPrimSerial(this));" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@PrimarySerialNo"/><img class="lookupicon" onclick="callTrackableIDLookup(this,\'xml:/Receipt/@SerialList\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@PrimarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@SecondarySerialNo\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute1\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute2\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute3\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute4\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute5\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute6\',\'xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ManufacturingDate\',\'NWCGTrackableIDLookup\',\'&xml:/Serial/@IncidentNo='+IncidentNo+'&xml:/Serial/InventoryItem/@ItemID='+ItemID1+'&xml:/Serial/@IncidentYear='+IncidentYear+'&xml:/Serial/@IssueNo='+IssueNo+'&xml:/Serial/@CacheID='+CacheId+'&xml:/Serial/InventoryItem/@ParentItemID='+PItemID+'&xml:/Serial/@NoOfSecSerials='+NumSecondarySerials+'\')" src="<%=request.getContextPath()%>/console/icons/lookup.gif\" alt="Search for Trackable ID"/></td>&nbsp;&nbsp;<td><input type="hidden" name="PC" value="'+PC+'"></td><td><input type="hidden" name="UOM" value="'+UOM+'"></td><td><input type="hidden" name="ITEMID" value="'+ItemID1+'"></td><TD noWrap><SELECT class=combobox name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@DispositionCode"  OldValue=""> <OPTION value=RFI selected>RFI</OPTION> <OPTION value=NRFI>NRFI</OPTION> <OPTION value=UnsRet>UnsRet</OPTION><OPTION value=UnsRetNWT>UnsRetNWT</OPTION></SELECT> </TD><tr></tr>';
													// CR 217 End	
													//Adding time sensitive in serial  line
													TimesenitiveRow = '<td>Expiration  Date:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput READONLY onblur=onBlurHandler() size=8 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ExpiryDate" dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> </TD><td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TAttribute" /></td>';
													
													//----------added after final demo
													if(tagAttribute=="Y")
													{//GN - Changed "S" to "Y" - 01/24/07
															var MName ='<td>Manufacturer Name:</td>&nbsp;&nbsp;<td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute1" /></td>';
										
															var  MModel = '<td>Manufacturer Model:</td><td><input type="text" READONY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute2" /></td>';
										
															var LotNum = '<td>Trackable ID:</td><td><input type="text" READONLY class="unprotectedinput" size=28 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute3" /></td>';
										
															var RevNum ='<td>Date Last Tested:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" onblur="javascript:if(this.value !=\'\' && (this.value.length != 10 || this.value.indexOf(\'/\') != 2)){alert(\'Please enter a valid date in mm/dd/yyyy format\');this.focus();}"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute4" /><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif" /></td>';
										
															var BNo='<td>Batch No:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute5" /></td>';
                                                            
															var OwnId='<td>Owner Unit ID:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@TagAttribute6" /></td>';
															
															// CR 693 BEGIN -ML
															var ManufactureDate='<td>Manufacturing Date:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+divcount+'/SerialInfoMainItem_'+cntr1+'/@ManufacturingDate" /></td>';
															// CR 693 END -ML
																						
															 if((BatchNo=="01") || (BatchNo=="02"))
																RowWOSec = RowWOSec + BNo;

															 if(ManufName=="01")
																RowWOSec = RowWOSec + MName;

															 if(ManufModel=="01")
																 RowWOSec = RowWOSec + MModel;

															 if((RevisionNo=="01") || (RevisionNo=="02"))
																 RowWOSec = RowWOSec + RevNum;

															 if((LotNumber=="01") || (LotNumber=="02"))
																 RowWOSec = RowWOSec + LotNum;

															 if((OwnerUnitId=="01") || (OwnerUnitId=="02"))
																 RowWOSec = RowWOSec + OwnId;

													}//End if---tagAttribute=="S"
													//-----End adding after final demo
													
													
													//alert("TimeSensitive:-"+TimeSensitive);
													if(TimeSensitive=="Y")
																RowWOSec = RowWOSec+TimesenitiveRow;
													//End adding time sensitive in same row
													
													if(cntr1 >=1)
													{
															//alert("reached here");
															newRow = tblBody.insertRow();
															newCell0 = newRow.insertCell(0);
															newCell0.innerHTML = RowWOSec;
													}
													else
													{
															newCell0.innerHTML = RowWOSec;
													}//End IF
												}//End For Loop
												//newCell0.innerHTML = finalString;
										//---End adding
										}//End If - Secondary serial
								}//End IF - If seriallised

								//Following sectio is commented due to tabout issue with multiple serials
								var TimesenitiveRow = "";
								if(SerializedFlag!="Y" && TimeSensitive=="Y"){
									var newRow = tblBody.insertRow();
									var newCell0 = newRow.insertCell(0);
									var cntr1=0;
									//for(cntr1=0;cntr1<RetQty;cntr1++){
											TimesenitiveRow = '<td>Expiration  Date:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput READONLY onblur=onBlurHandler() size=8 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TimeExpiryMainItem_'+cntr1+'/@ExpiryDate" dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"></TD><td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TimeExpiryMainItem_'+cntr1+'/@TAttribute" /></td>';
											if(cntr1 >=1){
														//alert("reached here");
														newRow = tblBody.insertRow();
														newCell0 = newRow.insertCell(0);
														newCell0.innerHTML = TimesenitiveRow;
													}else{
														newCell0.innerHTML = TimesenitiveRow;
											}// End IF cnt>1
									//}//End For Loop
								}//End IF - SerializedFlag!="Y" && TimeSensitive=="Y"
								
								//Made change after final demo-- adding && condition
								if(SerializedFlag!="Y" && tagAttribute=="Y"){//GN - Changed "S" to "Y" - 01/24/07
										//addRowInnerHTML(elem,divList[index].id);
										//Commented by sureh
										//var tblBody = divList[index].tBodies[0];
										////alert("TABLE == 1 **  "+tblBody.innerHTML);
										////alert("TABLE == >> "+tblBody.innerHTML);
										////alert("inputElid:---"+inputElid);
										var newRow = tblBody.insertRow();
										var newCell0 = newRow.insertCell(0);
										newCell0.innerHTML = '';
										
										var Final = '';
										var MName ='<td>Manufacturer Name:</td>&nbsp;&nbsp;<td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute1" /></td>';
										
										var  MModel = '<td>Manufacturer Model:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute2" /></td>';
										
										var LotNum = '<td>Trackable ID:</td><td><input type="text" READONLY class="unprotectedinput" size=28 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute3" /></td>';
										
										var RevNum ='<td>Date Last Tested:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" onblur="javascript:if(this.value !=\'\' && (this.value.length != 10 || this.value.indexOf(\'/\') != 2)){alert(\'Please enter a valid date in mm/dd/yyyy format\');this.focus();}"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute4" /><IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif" /></td>';
										
										var BNo='<td>Batch No:</td><td><input type="text" READONLY class="unprotectedinput" value="" size=10 maxlength=40 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute5" /></td>';

										var OwnId='<td>Owner Unit ID:</td><td><input type="text" READONLY class="unprotectedinput" value="" size=10 maxlength=40 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@TagAttribute6" /></td>';		
										
										var HiddenField='<td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/TagAttributesMainItem/@HAttribute" /></td>';

										    Final = Final + HiddenField;

										if((BatchNo=="01") || (BatchNo=="02"))
											Final = Final + BNo;

										 if(ManufName=="01")
											Final = Final + MName;

										 if(ManufModel=="01")
											 Final = Final + MModel;

										 if((RevisionNo=="01") || (RevisionNo=="02"))
											 Final = Final + RevNum;

										 if((LotNumber=="01") || (LotNumber=="02"))
											  Final = Final + LotNum;

										 if((OwnerUnitId=="01") || (OwnerUnitId=="02"))
											  Final = Final + OwnId;

										 newCell0.innerHTML = Final ;
								}//End IF - If tagged item											
							}//End IF -Finding right table
					}//Outer For Loop
			//-----------------------------------
		}//if(tagAttribute)
}//End Function addTags

function receiveKitComponents(elem,xmlDoc){
	
	//----------------
		//alert("Inside receive kit as components");
		
		
		var TotalNumberOfComps = "";
		//Declaring Array
		var ItemID = new Array();
		var SerializedFlag = new Array();
		var TagControlFlag = new Array();
		var TimeSensitive = new Array();
		var DefaultProductClass = new Array();
		var ItemDescription = new Array();
		var MaxReturn = new Array();
		var UnitOfMeasure = new Array();

		//Added after final demo
		var BatchNo = new Array();
		var ManufName = new Array();
		var ManufModel = new Array();
		var RevisionNo = new Array();
		var LotNumber = new Array();
		var OwnerUnitId = new Array();
		//End adding after final demo

		var inputElid ="";
		//This is to get the index of the element
		var test1 = elem.parentElement;
		////alert("test1:-"+test1.innerHTML);
		
		var inputList = test1.getElementsByTagName("input");
		//changes made by suresh
		//for(index = 0 ; index < inputList.length ; index++)
		//{
			inputElid = inputList[0].id;	
		////alert("inputElid obtained:-"+inputList[0].id);
		//}//End For Loop
		
		//CR 634 BEGIN-- Need to remove the 'ITEM' from the front of the ID.
		inputElid = inputElid.substr(inputElid.search(/[0-9]/));
		
		////alert("inputElid is now:-"+inputElid);
		
		//CR 634 END
		//Reading from the input xml	
		var nodes=xmlDoc.getElementsByTagName("Item");
		if(nodes!=null && nodes.length >0 ){	
			var ItemAttributes = nodes(0);
			TotalNumberOfComps = ItemAttributes.getAttribute("TotalNumberOfComps");
			////alert("TotalNumberOfComps:-"+TotalNumberOfComps);
		}//End if(nodes>0)

		CompNodes=xmlDoc.getElementsByTagName("Component");
		////alert("No OfComps:-"+CompNodes.length)

		if(CompNodes!=null && CompNodes.length >0 ){	
			for (cnt=0;cnt<CompNodes.length;cnt++ )
			{
				var CompAttributes = CompNodes(cnt);
				////alert("ItemID:-"+CompAttributes.getAttribute("ItemID")+"___ DefaultProductClass:-"+CompAttributes.getAttribute("DefaultProductClass"));
				ItemID.push(CompAttributes.getAttribute("ItemID"));
				SerializedFlag.push(CompAttributes.getAttribute("SerializedFlag"));
				TagControlFlag.push(CompAttributes.getAttribute("TagControlFlag"));
				TimeSensitive.push(CompAttributes.getAttribute("TimeSensitive"));
				DefaultProductClass.push(CompAttributes.getAttribute("DefaultProductClass"));
				ItemDescription.push(CompAttributes.getAttribute("ShortDescription"));
				UnitOfMeasure.push(CompAttributes.getAttribute("UnitOfMeasure"));
				MaxReturn.push(CompAttributes.getAttribute("MaxReturn"));
				
				//Added after final demo
				BatchNo.push(CompAttributes.getAttribute("BatchNo"));
				ManufName.push(CompAttributes.getAttribute("LotAttribute1"));
				ManufModel.push(CompAttributes.getAttribute("LotAttribute3"));
				RevisionNo.push(CompAttributes.getAttribute("RevisionNo"));
				LotNumber.push(CompAttributes.getAttribute("LotNumber"));
				OwnerUnitId.push(CompAttributes.getAttribute("LotAttribute2"));
				//End added after final demo
			
			}//End For Loop
		}//End if(nodes>0)

		//---------------adding
						var tableRow = elem.parentElement.parentElement.parentElement.parentElement;
						var tblList = tableRow.getElementsByTagName("table");
						////alert("tblList.length:-"+tblList.length);

						var baseString = "KitItems"
						var compareString = baseString.concat(inputElid);
						////alert("compareString:-"+compareString);
						
						for(cntr=0;cntr<tblList.length;cntr++){
							////alert("tblList[index].id:-"+tblList[cntr].id);
								if(tblList[cntr].id==compareString){ 
										////alert("table obtained &&&& ItemID.length:-"+ItemID.length);

										var tblBody = tblList[cntr].tBodies[0];
											for (cnt1=0;cnt1<ItemID.length;cnt1++ ){

												////alert("adding row #"+cnt1);	
												var newRow = tblBody.insertRow();
												newRow.setAttribute('id',cnt1);
												//if(SerializedFlag[cnt1]!="Y"){
														var newCell0 = newRow.insertCell(0);
														//newCell0.innerHTML = '<input type="input" value="cell 0 - text box" style="color: blue;" />';

														var finalRow = "";
														var itemDetailRow = '<td><b>ItemId:-</b></td>&nbsp;&nbsp;&nbsp;<td><input type="text"  class="unprotectedinput" id="'+cnt1+'" value="'+ItemID[cnt1]+'" size="10" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@ItemID" /></td>&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" readOnly="yes" value="'+ItemDescription[cnt1]+'"  size=30 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@ShortDescription"/></td>&nbsp;<td>PC:</td><td><input type="text" class="unprotectedinput" readOnly="yes" value="'+DefaultProductClass[cnt1]+'"  size=5 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@ProductClass"/></td>  <td class="tablecolumn" ><td>UOM:</td><input type="text" class="unprotectedinput"  readOnly="yes" value="'+UnitOfMeasure[cnt1]+'" size=3 name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@UOM"/></td> <td>Possible Return:-</td><td><input type="text"  class="unprotectedinput" size="3" readOnly="yes" value="'+MaxReturn[cnt1]+'" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@MaxReturn" /></td><td>Actual Return:-</td><td><input type="text" class="unprotectedinput" size="3"  onblur="fetchDataWithParams(this,\'getComponentSerials\',populateCompSerials,setParamCompSerial(this));"  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@QtyReturned" /></td><td><input type="hidden" name="MainRowID"  value="'+inputElid+'"><td><input type="hidden" name="ComponentID"  value="'+cnt1+'"></td><td>RFI:-</td><td><input type="text" size="3" value="" class="unprotectedinput" onblur="checkRFI(this);" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@RFI"/></td><td>NRFI:-</td><td><input type="text" value="" class="unprotectedinput" onblur="checkNRFI(this);" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@NRFI" size="3"/></td><td>UnsRet:-</td><td><input type="text" class="unprotectedinput" value="" size="3" onblur="checkUnsRet(this);" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@UnsRet"/></td><td>UnsNWT:-</td><td><input type="text" class="unprotectedinput" size="3" value="" onblur="checkUnsRetNWT(this);" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@UnsRetNWT" /></td></tr><tr><td>Line Notes:-</td><td><input type="text" class="unprotectedinput" size="40" maxLength=120 value=""  name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@LineNotes" /></td></tr><tr><td colspan="120" background-color="#EAEAF6" ><table class="table" width="100%" size=3 id="CompSerials'+cnt1+'"><tbody></tbody></table></td></tr>';

														finalRow = itemDetailRow;
														//alert("itemDetailRow:-"+itemDetailRow);
														newCell0.innerHTML = finalRow;

														//alert("final 1"+finalRow);

													/*if(SerializedFlag[cnt1]=="Y"){
														var finalRow = "";
														var TimesenitiveRow = "";
														var newRow = tblBody.insertRow();
														var newCell0 = newRow.insertCell(0);
														var serialRow = '<td>SerialID:-</td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<td><input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/@serialID")%>" /></td>';
														finalRow = finalRow + serialRow;
															if(TimeSensitive[cnt1]=="Y"){
																TimesenitiveRow = '<td>Expiry Date:-</td>&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput onblur=onBlurHandler() size=8 value="" name=xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/SerialInfo_'+cnt1+'/@ExpiryDate dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> </TD>';
																finalRow = finalRow + TimesenitiveRow;
															}//if(TimeSensitive[cnt1]=="Y")
														newCell0.innerHTML = finalRow;
														//alert("serial row -"+finalRow);
													}//End IF ----if(SerializedFlag[cnt1]=="Y")
													*/
													////alert("TimeSensitive[cnt1]=="+TimeSensitive[cnt1]);

													if(TimeSensitive[cnt1]=="Y" && SerializedFlag[cnt1]!="Y"){
														var finalRow="";
														var newRow = tblBody.insertRow();
														var newCell0 = newRow.insertCell(0);
														TimesenitiveRow = '<td>Expiration  Date:-</td>&nbsp;&nbsp;&nbsp;&nbsp;<TD noWrap><INPUT class=dateinput onblur=onBlurHandler() size=8 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/SerialInfo_'+cnt1+'/@ExpiryDate" dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> </TD><td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/SerialInfo_'+cnt1+'/@TAttribute" /></td>';
														finalRow = finalRow + TimesenitiveRow;
														newCell0.innerHTML = finalRow;
														//alert("timesensitive row"+finalRow);
													}//End IF

													//alert("tagAttribute=="+TagControlFlag[cnt1]);

													//=-----added && condition after final demo
													//-Changed "S" to "Y" - 01/27/07 - GN
													if(TagControlFlag[cnt1]=="Y" && SerializedFlag[cnt1]!="Y"){
														var finalRow = "";
														var newRow = tblBody.insertRow();
														var newCell0 = newRow.insertCell(0);
														var Final = '';
														tagRow = ''; 
														 
														  var MName ='<td>Manufacturer Name:</td>&nbsp;&nbsp;<td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute1" /></td>';
														  
														  var MModel='<td>Manufacturer Model:</td><td><input type="text"  READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute2" /></td>';
														  
														  var LotNum ='<td>Trackable ID:</td><td><input type="text" READONLY class="unprotectedinput" size=28 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute3" /></td>';
														  
														  var RevNum='<td>Date Last Tested:</td><td><input type="text" READONLY class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute4" /></td>';
														  
														  var BNo='<td>Batch No:</td><td><input type="text" class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute5" /></td>';

														  var OwnId='<td>Owner Unit ID:</td><td><input type="text" class="unprotectedinput" size=10 maxlength=40 value="" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@TagAttribute6" /></td>';

														  var HiddenField='<td><input type="hidden" class="unprotectedinput" value="Hide" name="xml:/Receipt/ReceiptLines/ReceiptLine_'+inputElid+'/Components/Component_'+cnt1+'/TagAttributes/@HAttribute"/></td>';
														  
														 Final = Final + HiddenField;

                                                            if((BatchNo[cnt1]=="01") || (BatchNo=="02")) 
																Final = Final + BNo;

															if(ManufName[cnt1]=="01")
																Final = Final + MName;

															if(ManufModel[cnt1]=="01")
																 Final = Final + MModel;

															if((RevisionNo[cnt1]=="01") || (RevisionNo[cnt1]=="02"))
																 Final = Final + RevNum;

															if((LotNumber[cnt1]=="01") || (LotNumber[cnt1]=="02"))
																  Final = Final + LotNum;

															if((OwnerUnitId[cnt1]=="01") || (OwnerUnitId[cnt1]=="02"))
																  Final = Final + OwnId;
														
														finalRow = finalRow + Final;
														//-----------------End adding after final demo
														newCell0.innerHTML = finalRow;
														//alert("tag row:-"+finalRow);
												}//End IF - If tagged item		
													////alert("final5"+finalRow);
													//newCell0.innerHTML = finalRow;
												//}//if(SerializedFlag[cnt1]!="Y"){
					
											}//End ForLoop for Item Array
								}//end if
						}//End For Loop of the table element
			//----------------End Adding

}//End receiveKitComponents
// CR 217 Begin	
function updateTrackableIDLookup(elem)
{
        var orderNumber         = document.getElementById("OrderNum");

//Begin CR883 12142012
        //alert("nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::orderNumber="+orderNumber.value);

        // change function call from getElementsByName to getElementById to avoid returning value "undefined"
        //var incidentYear      = document.getElementsByName("xml:/Receipt/@IncidentYear");
        var incidentYear            = document.getElementsByName("xml:/Receipt/@IncidentYear");
        //alert("MK1 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::incidentYear  "+incidentYear.value);
        if(incidentYear.value == undefined)
        {
                var incidentYear        = document.getElementById("xml:/Receipt/@IncidentYear");
                //alert("MK2 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::incidentYear="+incidentYear.value);
        }

        //var issueNo = document.getElementsByName("xml:/Receipt/@IssueNo");
        var issueNo            = document.getElementsByName("xml:/Receipt/@IssueNo");
        //alert("MK1 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::issueNo  "+issueNo.value);
        if(issueNo.value == undefined)
        {
                var issueNo = document.getElementById("xml:/Receipt/@IssueNo");
                //alert("MK2 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::issueNo="+issueNo.value);
        }

        //var node            = document.getElementsByName("xml:/Receipt/@CacheID");
        var node            = document.getElementsByName("xml:/Receipt/@CacheID");
        //alert("MK1 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::node  "+node.value);
        if(node.value == undefined)
        {
                var node            = document.getElementById("xml:/Receipt/@CacheID");
                //alert("MK2 nwcg_new_receive_incident_returns.jsp::updateTrackableIDLookup::node="+node.value);
        }
//End CR883 12142012

        var onClickEvent = elem.nextSibling.onclick;
        if(orderNumber != '' && onClickEvent != null) 	{
				var newfunc = elem.nextSibling.getAttribute("onclick");
				var oldFuncStr = (newfunc.toString()).match(/\S*(?=\s})/);
	
				newfunc = (oldFuncStr.toString()).replace( /[A-Z-0-9]*(?=&xml:\/Serial\/InventoryItem\/@ItemID)/ ,orderNumber.value);				
                // BEGIN change by MK
                // Commented by MK because incidentYear[0].value was null
                //newfunc =  (newfunc.toString()).replace( /[0-9]*(?=&xml:\/Serial\/@IssueNo=)/,incidentYear[0].value);
                newfunc =  (newfunc.toString()).replace( /[0-9]*(?=&xml:\/Serial\/@IssueNo=)/,incidentYear.value);
                // END change by MK				
				
				var newfunction = new Function(newfunc);
				elem.nextSibling.attachEvent("onclick", newfunction);
				elem.nextSibling.setAttribute("onclick",newfunction);
	}	
}
// CR 217 End	
</script>

<!-- Get Header Details-->
<table>
<!-- End Header Details-->
<!-- table starts here -->

<table id="MainTable" width="100%" ID="LinesToReceive">
<!-- ROMEVED <THEAD> FROM HERE AND MOVED DOWN-->
<tfoot>
<!-- START OF TEMPLATE ROW-->
<tr >
<td>
	<!-- Starting table in tfoot-->
	<table class="table">
		<THEAD>
			<TR>
			        <TD class=tablecolumnheader>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ItemID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Description&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;PC&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;UOM&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RFI&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NRFI&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;QtyUns&nbsp;&nbsp;QtyUnsDueToNWT&nbsp;&nbsp;TotalQtyRet&nbsp;&nbsp;&nbsp;RecdKitAsComp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Line Notes</TD>
					<!--<TD class="tablecolumnheader" >ItemID</TD>
					<TD class="tablecolumnheader" >PC</TD>
					<TD class="tablecolumnheader" >UOM</TD>
					<TD class="tablecolumnheader" >RFI</TD>
					<TD class="tablecolumnheader" >NRFI</TD>
					<TD class="tablecolumnheader" >QtyUns</TD>
					<TD class="tablecolumnheader" >QtyUnsDueToNWT</TD>
					<TD class="tablecolumnheader" >TotalQntyRet</TD>-->
			</TR>
		</THEAD>
	</table>
<!-- 2.1 ENHANCEMENT ***************************************************************** -->
<div style="overflow: auto; width: 100%; height: 200px; border: 1px solid #963;">
<!-- 2.1 ENHANCEMENT ***************************************************************** -->
<table  class=table id="ChildTable1" width="100%" counter="0">
			<!-- TR-1 -->
		<tbody  style="overflow: scroll; width: 100%">
		<!--<tr><td colspan="120" >suresh &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; is doing fine</td></tr>-->
		<!--prepopulation -->
		<%if(iTotalReceiptLine > 0 && ErrorMessage.length() > 0) //MAIN IF FOR PROCESSING RECEIPTS
		{ //iTotalReceiptLine > 0 IF
		   String IncidentNo = resolveValue("xml:/Receipt/@IncidentNo");
	       String IncidentYear = resolveValue("xml:/Receipt/@IncidentYear");
	   	   String IssueNo = resolveValue("xml:/Receipt/@IssueNo");
		   String CacheID = resolveValue("xml:/Receipt/@CacheID");
		for(int index=0 ; index < iTotalReceiptLine ; index ++) //MAIN FOR LOOP
		{ //iTotalReceiptLine FOR LOOP
			YFCElement elemReceiptLine = (YFCElement) nl.item(index) ;

		%>
		  <TR id=<%=index%> >
		  <!-- ERROR -->
			<TD>
			<!--<IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=deleteRows(this) alt="Delete Row" src="../console/icons/delete.gif">
			&nbsp;&nbsp;-->
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT class=unprotectedinput size=10 onblur="fetchDataWithParams(this,'getItemCompDetailsForRet',populateItemDetails1,setParam(this));" id="<%="ITEM"+index %>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@ItemID  value="<%=elemReceiptLine.getAttribute("ItemID")%>" />
			<IMG class=lookupicon id="<%="LOOKUP"+index %>" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item','xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value )" alt="Search for Item" src="<%=request.getContextPath()%>/console/icons/lookup.gif"/>&nbsp;&nbsp;
			<INPUT class=unprotectedinput readOnly size=20 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@ShortDescription value="<%=elemReceiptLine.getAttribute("ShortDescription")%>" />&nbsp;&nbsp;&nbsp;
			<INPUT class=unprotectedinput readOnly size=5 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@ProductClass value="<%=elemReceiptLine.getAttribute("ProductClass")%>" />&nbsp;&nbsp;
			<INPUT class=unprotectedinput value="<%=elemReceiptLine.getAttribute("UOM")%>"  readOnly size=5 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@UOM )/>&nbsp;&nbsp;
			<INPUT class=unprotectedinput size=5 id="<%="RFIQTY"+index %>"  onblur="updateLineTotalQty(<%=index%>);" value="<%=elemReceiptLine.getAttribute("RFI")%>"   name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@RFI />&nbsp;&nbsp;
			<INPUT class=unprotectedinput size=5 id="<%="NRFIQTY"+index %>" onblur="updateLineTotalQty(<%=index%>);" value="<%=elemReceiptLine.getAttribute("NRFI")%>"   name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@NRFI />&nbsp;&nbsp;&nbsp;
			<INPUT class=unprotectedinput size=5 id="<%="UNSQTY"+index %>" onblur="updateLineTotalQty(<%=index%>);"  value="<%=elemReceiptLine.getAttribute("UnsRet")%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@UnsRet/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT class=unprotectedinput size=5 id="<%="NWTQTY"+index %>" onblur="updateLineTotalQty(<%=index%>);" value="<%=elemReceiptLine.getAttribute("UnsRetNWT")%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@UnsRetNWT/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT class=protectedinput id="<%="TOTQTY"+index %>" value="<%=elemReceiptLine.getAttribute("QtyReturned")%>"  onblur="fetchDataWithParams(this,'getItemCompDetailsForRet',addTagRows,setParamQty(this));" size=5 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@QtyReturned/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<%  YFCNodeList complist = elemReceiptLine.getElementsByTagName("Component");
	            int compcnt = complist.getLength();   
		   if (compcnt > 0)
            {
            %>
			<INPUT class="" id="<%="RCVDASCOMP" + index%>" onclick="getKitDetails(this, <%=index%>);" type=checkbox value="<%=elemReceiptLine.getAttribute("RecdAsComp")%>" CHECKED name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@RecdAsComp>&nbsp;
            <% }
			else
			{ %>
			<INPUT class="" id="<%="RCVDASCOMP" + index%>" onclick="getKitDetails(this, <%=index%>);" type=checkbox value="<%=elemReceiptLine.getAttribute("RecdAsComp")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@RecdAsComp>&nbsp;
			<% } %>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT class=unprotectedinput size=25 maxLengtgh=120 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/@LineNotes value="<%=elemReceiptLine.getAttribute("LineNotes")%>" >
			</TD>
          
     <%  
	  YFCNodeList nls,nlt,nltag = null ;
	  nls = elemReceiptLine.getElementsByTagName("SerialInfoMainItem");
	  nlt = elemReceiptLine.getElementsByTagName("TimeExpiryMainItem");
	  nltag = elemReceiptLine.getElementsByTagName("TagAttributesMainItem");
	  
	  int iTotalTime = nlt.getLength();
	  int iTotalSerial = nls.getLength();
	  int iTotalTags = nltag.getLength();
	  %>

	  <%
	  if (iTotalTime > 0)
	  {
		  YFCElement elemTime = (YFCElement) nlt.item(0);
		  String Edate="";
		  if (elemTime.getAttribute("ExpiryDate") != null)
		    Edate = elemTime.getAttribute("ExpiryDate");
      %>
       <tr>
	   <td>
	   <TABLE class=table id=KitItems<%=index%> width="100%" cellSpacing=0>
	   <TBODY>
	   <tr>
	   <td>Expiration Date:-
	   <INPUT class=dateinput onblur=onBlurHandler() size=8 value="<%=Edate%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TimeExpiryMainItem/@ExpiryDate dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif">
	   <input type="hidden" class="unprotectedinput" value="Hide" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TimeExpiryMainItem/@TAttribute >
	   </td>
	   </tr>
	   </TBODY> 
	   </TABLE>
	   </TD>
	   </TR>
      <%} //iTotalTime END IF %>

      <%
	  if (iTotalTags > 0)
	  {
		  YFCElement elemTag = (YFCElement) nltag.item(0);
      %>
       <tr>
	   <td>
	   <TABLE class=table id=KitItems<%=index%> width="100%" cellSpacing=0>
	   <TBODY>
	   <tr>
	   <td>
	   <input type="hidden" class="unprotectedinput" value="Hide" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@HAttribute>
	  <% if (elemTag.hasAttribute("TagAttribute1"))
		{
       %>
	     Manufacturer Name:
	     <input type="text" READONLY size=10  maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute1")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute1 >
	  <% } %>

	  
	   <% if (elemTag.hasAttribute("TagAttribute2"))
		{
       %>
	     Manufacturer Model:
	     <input type="text" size=10 maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute2")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute2 >
	  <% } %>

	  <% if (elemTag.hasAttribute("TagAttribute3"))
		{
      %>
	     Trackable ID:
	     <input type="text" READONLY size=28 maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute3")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute3 >
	  <% } %>

	  <% if (elemTag.hasAttribute("TagAttribute4"))
		{
      %>
	     Date Last Tested:
	     <input type="text" size=10 maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute4")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute4 >
	  <% } %>

	  <% if (elemTag.hasAttribute("TagAttribute5"))
		{
      %>
	     Batch No:
	     <input type="text" size=10 maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute5")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute5 >
	  <% } %>

	  <% if (elemTag.hasAttribute("TagAttribute6"))
		{
      %>
	     Owner Unit ID:
	     <input type="text" size=10 maxlength=40 class="unprotectedinput"  value="<%=elemTag.getAttribute("TagAttribute6")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/TagAttributesMainItem/@TagAttribute6 >
	  <% } %>
       </td>
	   </tr>
	   </TBODY> 
	   </TABLE>
	   </TD>
	   </TR>
    <%} //iTotalTags END IF %>


     <%
	  if (iTotalSerial > 0)
	 {
     %>
	 <tr>
	 <td>
	 <TABLE class=table id=KitItems<%=index%>  cellSpacing=0>
	 <TBODY>
     <%	  
	   for(int k=0; k < iTotalSerial; k++) 
	   { //iTotalSerials FOR LOOP
		YFCElement elemSerial = (YFCElement) nls.item(k);
		String ESdate="";
		  if (elemSerial.getAttribute("ExpiryDate") != null)
		    ESdate = elemSerial.getAttribute("ExpiryDate");
	 %>  
      <tr> 
      <td>Trackable ID:
	 <!-- // CR 217 & 693  Begin	 -->
	  <input type="text" READONLY size=30 maxLength=40 class="unprotectedinput"   value="<%=elemSerial.getAttribute("PrimarySerialNo")%>" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,'checkSerialNo',PopupMessage,setParamSecSerial(this));" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@PrimarySerialNo >
	  <img class="lookupicon" onclick="callTrackableIDLookup(this,'xml:/Receipt/@SerialList','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@PrimarySerialNo','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@SecondarySerialNo','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute1','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute2','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute3','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute4','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute5','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute6','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@ManufacturingDate','NWCGTrackableIDLookup','&xml:/Serial/@IncidentNo=<%=IncidentNo%>&xml:/Serial/InventoryItem/@ItemID=<%=elemReceiptLine.getAttribute("ItemID")%>&xml:/Serial/InventoryItem/@ParentItemID=<%=elemReceiptLine.getAttribute("ItemID")%>&xml:/Serial/@IncidentYear=<%=IncidentYear%>&xml:/Serial/@IssueNo=<%=IssueNo%>&xml:/Serial/@CacheID=<%=CacheID%>&xml:/Serial/@NoOfSecSerials=1')" src="<%=request.getContextPath()%>/console/icons/lookup.gif" alt="Search for Trackable ID"/>
		<!-- // CR 217  & 693 End	 -->
	  <% if (elemSerial.hasAttribute("SecondarySerialNo"))
		{
      %>
	     Manufacturers Serial :
	    <input type="text" READONLY size=10 maxLength=20 class="unprotectedinput"  value="<%=elemSerial.getAttribute("SecondarySerialNo")%>"   name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@SecondarySerialNo >
	  <% } %>

	   <select class=combobox name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@DispositionCode  OldValue=""> <option value=RFI selected>RFI</option> <option value=NRFI>NRFI</option> <option value=UnsRet>UnsRet</option><option value=UnsRetNWT>UnsRetNWT</option></select> 

	   <% if (elemSerial.hasAttribute("TagAttribute1"))
		{
       %>
	     Manufacturer Name:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute1")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute1 >
	  <% } %>

	  
	   <% if (elemSerial.hasAttribute("TagAttribute2"))
		{
       %>
	     Manufacturer Model:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute2")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute2 >
	  <% } %>
	  <!-- // CR 217 & 693  Begin	 -->
	  <% if (elemSerial.hasAttribute("ManufacturingDate"))
		{
       %>
	     Manufacturing Date:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("ManufacturingDate")%>"  name='xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@ManufacturingDate' >
	  <% } %>
	  <!-- // CR 217 & 693  End	 -->
	  <% if (elemSerial.hasAttribute("TagAttribute3"))
		{
      %>
	     Trackable ID:
	     <input type="text" READONLY size=28 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute3")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute3 >
	  <% } %>

	  <% if (elemSerial.hasAttribute("TagAttribute4"))
		{
      %>
	     Date Last Tested:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute4")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute4 >
	  <% } %>

	  <% if (elemSerial.hasAttribute("TagAttribute5"))
		{
      %>
	     Batch No:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute5")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute5 >
	  <% } %>

	  <% if (elemSerial.hasAttribute("TagAttribute6"))
		{
      %>
	     Owner Unit ID:
	     <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput"  value="<%=elemSerial.getAttribute("TagAttribute6")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@TagAttribute6 >
	  <% } %>

	  <% if (elemSerial.hasAttribute("ExpiryDate") || elemSerial.hasAttribute("TAttribute"))
		{
      %>

	  Expiration Date:-
	  <input class=dateinput onblur=onBlurHandler() size=8 value="<%=ESdate%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/SerialInfoMainItem_<%=k%>/@ExpiryDate dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> 

	  <% }%>
      </td>
	  </tr>

   	 <%
	   } //iTotalSerials END FOR LOOP
	 %>
	 </TBODY> 
	 </TABLE>
	 </TD>
	 </TR>
    <%} //iTotalSerial END IF %>

	 <%
      YFCNodeList nlc,nlcs,nlctag,nlctime = null;
      nlc = elemReceiptLine.getElementsByTagName("Component");
	  iTotalComponents = nlc.getLength(); 
	  if (iTotalComponents > 0)
	 { //iTotalComponents > 0 IF
     %>
	 <tr>
	 <td>
	 <TABLE class=table id=KitItems<%=index%>  cellSpacing=0>
	 <TBODY>
    <%	  
	   for(int j=0; j < iTotalComponents; j++) 
	   { //iTotalComponents FOR LOOP
		YFCElement elemComponent = (YFCElement) nlc.item(j);  
	%>  
	   <tr>
       <td>
	   <b>ItemId:-</b>
       <input type="text" class="unprotectedinput"  value="<%=elemComponent.getAttribute("ItemID")%>" size=10 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@ItemID >
	   <input type="text" class="unprotectedinput" readOnly="yes" value="<%=elemComponent.getAttribute("ShortDescription")%>"  size=20 name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@ShortDescription>
       PC:
       <input type="text" class="unprotectedinput" readOnly="yes" value="<%=elemComponent.getAttribute("ProductClass")%>"  size="3" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@ProductClass>
       UOM:
       <input type="text" class="unprotectedinput" readOnly="yes" value="<%=elemComponent.getAttribute("UOM")%>" size="5" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@UOM>
       Possible Return:-
       <input type="text" class="unprotectedinput" size="3" readOnly="yes" value="<%=elemComponent.getAttribute("MaxReturn")%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@MaxReturn >
       Actual Return:-
       <input type="text" class="unprotectedinput" size="3" value="<%=elemComponent.getAttribute("QtyReturned")%>" onblur="fetchDataWithParams(this,\'getComponentSerials\',populateCompSerials,setParamCompSerial(this));"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@QtyReturned >
       <input type="hidden" name="MainRowID"  value="<%=index%>">
       <input type="hidden" name="ComponentID"  value="<%=j%>">
       RFI:-
       <input type="text" size="3" class="unprotectedinput" value="<%=elemComponent.getAttribute("RFI")%>" onblur="checkRFI(this);" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@RFI>
       NRFI:-
       <input type="text" class="unprotectedinput" value="<%=elemComponent.getAttribute("NRFI")%>"  onblur="checkNRFI(this);" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@NRFI size="3">
       UnsRet:-
       <input type="text" class="unprotectedinput" value="<%=elemComponent.getAttribute("UnsRet")%>" size="3" onblur="checkUnsRet(this);" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@UnsRet>
       UnsNWT:-
       <input type="text" value="<%=elemComponent.getAttribute("UnsRetNWT")%>"class="unprotectedinput" size="3" onblur="checkUnsRetNWT(this);" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@UnsRetNWT >
	   Line Notes:-
       <input type="text" value="<%=elemComponent.getAttribute("LineNotes")%>"class="unprotectedinput" size="30" onblur="checkUnsRetNWT(this);" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/@LineNotes >
	   </td>
       </tr>
       <%
		nlcs = elemComponent.getElementsByTagName("Serial");
	    nlctag = elemComponent.getElementsByTagName("TagAttributes");
		nlctime = elemComponent.getElementsByTagName("SerialInfo");
	    int iTotalCompSerials = nlcs.getLength(); 
		int iTotalCompTags = nlctag.getLength();
		int iTotalCtime = nlctime.getLength();
	    if (iTotalCompSerials > 0)
		  { 
        %>
		 <tr>
	     <td>
	     <TABLE class=table id=KitItems<%=index%> width="100%" cellSpacing=0>
	     <TBODY>
		 <%	  
	     for(int m=0; m < iTotalCompSerials; m++) 
	     { //iTotalCompSerials FOR LOOP
		  YFCElement elemCompSerial = (YFCElement) nlcs.item(m);
		  String ECSdate="";
		  if (elemCompSerial.getAttribute("ExpiryDate") != null)
		    ECSdate = elemCompSerial.getAttribute("ExpiryDate");
	     %>  
		  <tr> 

	      <% if (elemCompSerial.hasAttribute("SecondarySerialNo"))
	    	{
          %>
		  <td>Trackable ID:
	      &nbsp;&nbsp;&nbsp;&nbsp;
	      <!-- // CR 217 Begin	 -->
	      <input type="text" READONLY size=30 maxLength=40 class="unprotectedinput"  id=<%=m%> value="<%=elemCompSerial.getAttribute("serialID")%>" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,'checkSerialNo',PopupMessage,setParamSecSerial(this));" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@serialID >
	      <!-- // CR 217 End	 -->
	       <!-- // CR 693 Begin	 -->
		  <img class="lookupicon" onclick="callTrackableIDLookup(this,'xml:/Receipt/@SerialList','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@serialID','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@SecondarySerialNo','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute1','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute2','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute3','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute4','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute5','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute6','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@ManufacturingDate','NWCGTrackableIDLookup','&xml:/Serial/@IncidentNo=<%=IncidentNo%>&xml:/Serial/InventoryItem/@ItemID=<%=elemComponent.getAttribute("ItemID")%>&xml:/Serial/InventoryItem/@ParentItemID=<%=elemReceiptLine.getAttribute("ItemID")%>&xml:/Serial/@IncidentYear=<%=IncidentYear%>&xml:/Serial/@IssueNo=<%=IssueNo%>&xml:/Serial/@CacheID=<%=CacheID%>&xml:/Serial/@NoOfSecSerials=1')" src="<%=request.getContextPath()%>/console/icons/lookup.gif" alt="Search for Trackable ID"/>
	      Manufacturers Serial :
	      <input type="text" READONLY size=10  maxLength=20 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("SecondarySerialNo")%>"   name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@SecondarySerialNo >
	      <% } else {%>
          <td>Trackable ID:
	      &nbsp;&nbsp;&nbsp;&nbsp;
	      <!-- // CR 217 Begin	 -->
	      <input type="text" READONLY size=30 maxLength=40 class="unprotectedinput"  id=<%=m%> value="<%=elemCompSerial.getAttribute("serialID")%>" onblur="trim(this); updateTrackableIDLookup(this); fetchDataWithParams(this,'checkSerialNo',PopupMessage,setParamSecSerial(this));" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@serialID >
		  <!-- // CR 217 End	 -->
		  <!-- // CR 693 Begin	 -->
		  <img class="lookupicon" onclick="callTrackableIDLookup(this,'xml:/Receipt/@SerialList','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@serialID','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@SecondarySerialNo','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute1','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute2','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute3','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute4','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute5','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute6','xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@ManufacturingDate','NWCGTrackableIDLookup','&xml:/Serial/@IncidentNo=<%=IncidentNo%>&xml:/Serial/InventoryItem/@ItemID=<%=elemComponent.getAttribute("ItemID")%>&xml:/Serial/InventoryItem/@ParentItemID=<%=elemReceiptLine.getAttribute("ItemID")%>&xml:/Serial/@IncidentYear=<%=IncidentYear%>&xml:/Serial/@IssueNo=<%=IssueNo%>&xml:/Serial/@CacheID=<%=CacheID%>&xml:/Serial/@NoOfSecSerials=1')" src="<%=request.getContextPath()%>/console/icons/lookup.gif" alt="Search for Trackable ID"/>
		  <!-- // CR 693 End	 -->
		  <% } %>

	      <select class=combobox name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@DispositionCode  OldValue=""> <option value="<%=elemCompSerial.getAttribute("DispositionCode")%>">RFI</option> <option value=NRFI>NRFI</option> <option value=UnsRet>UnsRet</option><option value=UnsRetNWT>UnsRetNWT</option></select> 

	      <% if (elemCompSerial.hasAttribute("TagAttribute1"))
	      {
          %>
	      Manufacturer Name:
	      <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute1")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute1 >
	      <% } %>

	  
	      <% if (elemCompSerial.hasAttribute("TagAttribute2"))
	      {
          %>
	      Manufacturer Model:
	      <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute2")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute2 >
	      <% } %>
    	  <!-- CR 693 BEGIN -->
   	      <% if (elemCompSerial.hasAttribute("ManufacturingDate"))
	      {
          %>
	      Manufacturing Date:
	      <input type="text" READONLY size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute2")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@ManufacturingDate >
	      <% } %>
    	  <!-- CR 693 END -->

    	  <% if (elemCompSerial.hasAttribute("TagAttribute3"))
          {
          %>
            
 	     Trackable ID:
	     <input type="text" READONLY size=28 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute3")%>"name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute3 >
         <% } %>

	    <% if (elemCompSerial.hasAttribute("TagAttribute4"))
	 	{
        %>
	     Date Last Tested:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute4")%>"name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute4 >
	    <% } %>

	  <% if (elemCompSerial.hasAttribute("TagAttribute5"))
		{
      %>
	     Batch No:
	    <input type="text" size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute5")%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute5 >
	  <% } %>

	  <% if (elemCompSerial.hasAttribute("TagAttribute6"))
		{
      %>
	     Owner Unit ID:
	    <input type="text" size=10 maxLength=40 class="unprotectedinput" id=<%=m%> value="<%=elemCompSerial.getAttribute("TagAttribute6")%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@TagAttribute6 >
	  <% } %>

	  <% if (elemCompSerial.hasAttribute("ExpiryDate") || elemCompSerial.hasAttribute("TAttribute"))
		{
      %>

	  Expiration Date:-
	  <input class=dateinput onblur=onBlurHandler() size=8 value="<%=ECSdate%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/Serial_<%=m%>/@ExpiryDate dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif"> 

	  <% }%>
      </td>
	  </tr>

	  <% } //TotalSerial FOR LOOP %>
	    </TBODY> 
		</TABLE>
		</TD>
		</TR>
      <% } //TotaSerial END IF %>

	  <%
	  if (iTotalCtime > 0)
	  {
		  YFCElement elemCTime = (YFCElement) nlctime.item(0);
		  String ECdate="";
		  if (elemCTime.getAttribute("ExpiryDate") != null)
		    ECdate = elemCTime.getAttribute("ExpiryDate");
      %>
       <tr>
	   <td>
	   <TABLE class=table id=KitItems<%=index%> width="100%" cellSpacing=0>
	   <TBODY>
	   <tr>
	   <td>Expiration Date:-
	   <INPUT class=dateinput onblur=onBlurHandler() size=8 value="<%=ECdate%>" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/@ExpiryDate dataType="DATE" OldValue=""> <IMG class=lookupicon onclick="invokeCalendar(this);return false" alt=Calendar src="<%=request.getContextPath()%>/console/icons/calendar.gif">
       <input type="hidden" class="unprotectedinput" value="Hide" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/SerialInfo/@TAttribute >
	   </td>
	   </tr>
	   </TBODY> 
	   </TABLE>
	   </TD>
	   </TR>
      <%} //iTotalCTime END IF %>

      <%
	  if (iTotalCompTags > 0)
	  {
		  YFCElement elemCompTag = (YFCElement) nlctag.item(0);
      %>
       <tr>
	   <td>
	   <TABLE class=table id=KitItems<%=index%> width="100%" cellSpacing=0>
	   <TBODY>
	   <tr>
	   <td>
	   <input type="hidden" class="unprotectedinput" value="Hide" name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@HAttribute>
	  <% if (elemCompTag.hasAttribute("TagAttribute1"))
		{
       %>
	     ManufacturerName:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute1")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute1 >
	  <% } %>

	  
	   <% if (elemCompTag.hasAttribute("TagAttribute2"))
		{
       %>
	     ManufacturerModel:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute2")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute2 >
	  <% } %>

	  <% if (elemCompTag.hasAttribute("TagAttribute3"))
		{
      %>
	     Trackable ID:
	     <input type="text" READONLY size=28 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute3")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute3 >
	  <% } %>

	  <% if (elemCompTag.hasAttribute("TagAttribute4"))
		{
      %>
	     Date Last Tested:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute4")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute4 >
	  <% } %>

	  <% if (elemCompTag.hasAttribute("TagAttribute5"))
		{
      %>
	     Batch No:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute5")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute5 >
	  <% } %>

	  <% if (elemCompTag.hasAttribute("TagAttribute6"))
		{
      %>
	     Owner Unit ID:
	     <input type="text" size=10 maxLength=40 class="unprotectedinput"  value="<%=elemCompTag.getAttribute("TagAttribute6")%>"  name=xml:/Receipt/ReceiptLines/ReceiptLine_<%=index%>/Components/Component_<%=j%>/TagAttributes/@TagAttribute6 >
	  <% } %>
       </td>
	   </tr>
	   </TBODY> 
	   </TABLE>
	   </TD>
	   </TR>
    <%} //iTotalTags END IF %>

    <%
     } //iTotalComponents END FOR LOOP
	%>
	</TBODY> 
	</TABLE>
	</TD>
	</TR>
    <%} //iTotalComponents END IF %>
    </TR>
    <%
	   } //iTotalReceiptLine END FOR LOOP
	} //iTotalReceiptLine > 0 END IF
	%>
							<!-- End Pre Population-->
	</tbody>
	</table>

<!-- Cannot Add More Lines in Error Mode -->

 <%if (ErrorMessage.length() == 0 )
 {
 %>
	<!--Adding provision to add plus sign for adding new rows -->
	<TR>
		<TD align=left>

				<TABLE class=tablefooter style="BORDER-RIGHT: 0px; BORDER-TOP: 0px; BORDER-LEFT: 0px; BORDER-BOTTOM: 0px" cellSpacing=0>
			<TBODY>
			<TR>
			<TD>
			<IMG class=icon style="WIDTH: 12px; HEIGHT: 12px" onclick=addRows(this) alt="Add/Copy Row" src="<%=request.getContextPath()%>/console/icons/add.gif">
			<input type="hidden" name="rowCounter" value="1"/>
			</TD>
			<td>Add a Return Line</td>
			</TR>
			</TBODY>
			</TABLE>
			</TD>
		</TR>
  <% } %>
	<!-- End Adding new rows-->
	<!-- End OF TEMPLATE ROW-->
</td>
</tr>
</tfoot>
</table>
<!-- 2.1 ENHANCEMENT - END DIV ***************************************************************** -->
</div>
<!-- 2.1 ENHANCEMENT - END DIV ***************************************************************** -->
<!-- table ENDDS starts here -->
<!-- 	 -->
<!--         -->
<!-- <table> -->